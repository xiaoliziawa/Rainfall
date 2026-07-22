package com.lirxowo.rainfall.internal;

import com.lirxowo.rainfall.Rainfall;
import com.lirxowo.rainfall.api.RainfallAPI;
import com.lirxowo.rainfall.internal.api.RuleRegistrationHandler;
import com.lirxowo.rainfall.internal.command.RainfallCommands;
import com.lirxowo.rainfall.internal.command.VerboseBlockLogger;
import com.lirxowo.rainfall.internal.compat.crafttweaker.CraftTweakerCompat;
import com.lirxowo.rainfall.internal.compat.kubejs.KubeJSCompat;
import com.lirxowo.rainfall.internal.config.RainfallConfig;
import com.lirxowo.rainfall.internal.event.BreakContextCache;
import com.lirxowo.rainfall.internal.rule.RuleExporter;
import com.lirxowo.rainfall.internal.rule.RuleLoader;
import com.lirxowo.rainfall.internal.rule.RuleLocator;
import com.lirxowo.rainfall.internal.rule.data.Rule;
import com.lirxowo.rainfall.internal.rule.data.RuleList;
import com.lirxowo.rainfall.internal.rule.drop.DropModification;
import com.lirxowo.rainfall.internal.rule.drop.DropModifier;
import com.lirxowo.rainfall.internal.rule.log.RuleLog;
import com.lirxowo.rainfall.internal.rule.match.RuleContext;
import com.lirxowo.rainfall.internal.rule.match.RuleMatcher;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class RainfallRuntime {

    private static RainfallRuntime instance;

    private final Path rulePath;
    private final RuleLog log;
    private final List<RuleList> ruleLists = new ArrayList<>();
    private final RuleLocator ruleLocator;
    private final DropModifier dropModifier = new DropModifier();
    private final BreakContextCache breakContexts = new BreakContextCache();
    private final VerboseBlockLogger verboseBlockLogger = new VerboseBlockLogger();
    private volatile boolean rulesDirty;

    public RainfallRuntime() {
        if (instance != null) {
            throw new IllegalStateException("Rainfall runtime has already been initialized");
        }
        instance = this;
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(Rainfall.MODID);
        this.rulePath = configPath;
        try {
            this.log = new RuleLog(Rainfall.LOGGER, configPath.resolve("rainfall.log"));
        } catch (IOException error) {
            throw new IllegalStateException("Unable to initialize Rainfall rule log", error);
        }
        RainfallAPI.initialize(new RuleRegistrationHandler(this.ruleLists));
        this.ruleLocator = new RuleLocator(this.ruleLists, new RuleMatcher(), this.log);
        if (ModList.get().isLoaded("crafttweaker")) {
            MinecraftForge.EVENT_BUS.register(new CraftTweakerCompat());
        }
        if (ModList.get().isLoaded("kubejs")) {
            MinecraftForge.EVENT_BUS.register(new KubeJSCompat());
        }
    }

    public static RainfallRuntime instance() {
        if (instance == null) {
            throw new IllegalStateException("Rainfall runtime is not initialized");
        }
        return instance;
    }

    public static void markRulesDirty() {
        if (instance != null) {
            instance.rulesDirty = true;
        }
    }

    public void reload() {
        RuleLoader.reload(
                this.rulePath,
                this.ruleLists,
                RainfallConfig.JSON_STRICT_MODE.get(),
                RainfallConfig.ENABLE_PROFILE_LOG_OUTPUT.get(),
                RainfallConfig.INJECT_PROFILING_RULES.get(),
                this.log
        );
        this.ruleLocator.clearCache();
        this.rulesDirty = false;
    }

    public int ruleListCount() {
        return this.ruleLists.size();
    }

    public RuleExporter.ExportResult exportRules() throws IOException {
        return RuleExporter.export(this.rulePath, this.ruleLists);
    }

    public boolean toggleVerbose(ServerPlayer player) {
        return this.verboseBlockLogger.toggle(player);
    }

    public ObjectArrayList<ItemStack> modifyLoot(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
        BlockState state = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        Vec3 origin = lootContext.getParamOrNull(LootContextParams.ORIGIN);
        if (state == null || origin == null) {
            return generatedLoot;
        }
        ServerLevel level = lootContext.getLevel();
        BlockPos position = BlockPos.containing(origin);
        this.verboseBlockLogger.record(state);
        BreakContextCache.BreakContext breakContext = this.breakContexts.take(level.dimension(), position);
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        Player player = entity instanceof Player contextPlayer
                ? contextPlayer
                : breakContext == null ? null : breakContext.player();
        ItemStack tool = lootContext.getParamOrNull(LootContextParams.TOOL);
        if ((tool == null || tool.isEmpty()) && breakContext != null) {
            tool = breakContext.tool();
        }
        if (tool == null) {
            tool = ItemStack.EMPTY;
        }
        int originalExperience = breakContext == null ? 0 : breakContext.experience();
        boolean explosion = lootContext.hasParam(LootContextParams.EXPLOSION_RADIUS);
        RuleContext context = new RuleContext(
                level,
                player,
                position,
                state,
                List.copyOf(generatedLoot),
                tool,
                explosion
        );
        List<Rule> rules = this.ruleLocator.locate(context);
        if (rules.isEmpty()) {
            awardExperience(level, position, originalExperience);
            return generatedLoot;
        }

        int fortune = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        boolean silkTouch = tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0;
        boolean profile = RainfallConfig.ENABLE_PROFILE_LOG_OUTPUT.get();
        for (Rule rule : rules) {
            long start = profile ? System.nanoTime() : 0L;
            DropModification result = this.dropModifier.modify(
                    rule,
                    generatedLoot,
                    silkTouch,
                    fortune,
                    originalExperience,
                    lootContext.getRandom(),
                    this.log
            );
            awardExperience(level, position, result.experience());
            if (result.replacement() != null) {
                level.setBlock(position, result.replacement(), 3);
            }
            if (profile) {
                this.log.profile("Modified drops in " + elapsedMilliseconds(start) + " ms");
            }
        }
        return generatedLoot;
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        this.reload();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        Player player = event.getPlayer();
        this.breakContexts.put(
                level.dimension(),
                event.getPos(),
                player,
                player.getMainHandItem(),
                event.getExpToDrop()
        );
        event.setExpToDrop(0);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (this.rulesDirty) {
                this.reload();
            }
            this.breakContexts.clear();
        } else {
            this.verboseBlockLogger.flush(event.getServer());
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RainfallCommands.register(event.getDispatcher(), this);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            this.verboseBlockLogger.remove(player);
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        this.breakContexts.clear();
        this.verboseBlockLogger.clear();
    }

    private static void awardExperience(ServerLevel level, BlockPos position, int experience) {
        if (experience > 0) {
            ExperienceOrb.award(level, Vec3.atCenterOf(position), experience);
        }
    }

    private static double elapsedMilliseconds(long start) {
        return (System.nanoTime() - start) / 1_000_000.0;
    }
}
