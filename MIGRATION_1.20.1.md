# Rainfall / Dropt 功能迁移记录

## 目标

以 `F:\CloneProjects\dropt-1.12` 为只读行为参考，在当前 Rainfall 1.20.1 Forge 工程中完整重建 Dropt 的规则、掉落、API、命令及第三方集成功能。旧 Dropt 仓库不作为修改目标。

本文档持续记录分析、设计、每批代码变更与验证结果。恢复工作时应先阅读本文档，再继续未完成阶段。

## 工程边界

- 修改目标：`F:\CloneProjects\Rainfall`
- 旧行为参考：`F:\CloneProjects\dropt-1.12`
- Minecraft / Forge 源码：`F:\projects\1.20.1-parchment-source`
- Oraculum 源码：`F:\MyProjects\Athenaeum`
- 第三方依赖 jar 与 sources jar：`D:\GradleHome`
- 旧项目实际版本：Minecraft 1.12.2 / Forge 14.23.5.2847
- 目标版本：Minecraft 1.20.1 / Forge 47.4.22 / Java 17
- 目标包域：`com.lirxowo.rainfall.api` 为公共 API，`com.lirxowo.rainfall.internal` 为内部实现。

## 已确认的现状

- Rainfall 有自己的 ForgeGradle 6 / Gradle 8.8 配置，不复制旧 Dropt 的 Gradle 文件。
- Rainfall 已配置 Oraculum 与 CraftTweaker 14.0.60 依赖。
- Rainfall 当前只有入口类，没有需要兼容的既有业务实现。
- 旧 Dropt 有 92 个 Java 文件，源码分为公开 API 与内部实现。
- 旧 Dropt 依赖 Athenaeum 的模块框架、字符串解析、加权选择和若干工具。
- Oraculum 没有保留上述全部旧 API；Rainfall 使用现代 Forge 生命周期，并在自身内部实现 Dropt 专属的小型工具。
- GameStages 1.20.1 `15.0.2` 与 CraftTweaker 1.20.1 `14.0.60` 的源码包均已找到。
- 仓库没有 `.codegraph`，源码定位使用 `rg` 与直接源码查阅。

## 保留功能

- 从 `config/rainfall/*.json` 发现、解析、排序和热重载规则列表。
- 宽松与严格 JSON 属性校验。
- 匹配方块、原始掉落、主手/副手物品、玩家、假玩家、GameStages、群系、维度和出生点距离。
- 匹配精确 NBT、模糊 NBT、物品数量和列表策略。
- 按权重、数量范围、fortune、silk touch 和策略选择掉落。
- 替换、追加或删除原始掉落，替换方块并处理经验值。
- 玩家采掘和爆炸掉落上下文。
- `reload`、`hand`、`verbose`、`export` 命令。
- 公共 Java builder API 与规则加载事件。
- CraftTweaker 规则构建接口和脚本重载。
- GameStages 条件匹配。
- JSON 导出、调试日志、profile 日志和压力测试规则。

## 1.12.2 到 1.20.1 的关键变化

| 旧实现 | 新实现 | 状态 |
| --- | --- | --- |
| Athenaeum `ModuleManager` 生命周期 | Rainfall `@Mod` 构造器、mod event bus、Forge event bus | 已实现 |
| `IBlockState` | `BlockState` | 已实现 |
| 方块和物品 metadata | 方块状态属性、物品 tags 与 NBT；保留可合理映射的旧字符串兼容入口 | 部分兼容，非零 metadata 待诊断 |
| Ore Dictionary | `TagKey<Item>` / `Ingredient` | 已实现常见映射，待完善后缀解析 |
| `NBTTagCompound` | `CompoundTag` | 已实现 |
| `World` / `EntityPlayer` | `Level`、`ServerLevel`、`Player`、`ServerPlayer` | 已实现 |
| `HarvestDropsEvent` | Forge Global Loot Modifier + `BreakEvent` 上下文缓存 | 已实现，待游戏内验证 |
| 爆炸位置缓存 | loot context `EXPLOSION_RADIUS` 直接标识爆炸方块掉落 | 已实现并核对 1.20.1 源码 |
| `CommandBase` | Brigadier / `RegisterCommandsEvent` | 已实现 |
| `@Config` | `ForgeConfigSpec` common config | 已实现 |
| `Loader.isModLoaded` | `ModList.get().isLoaded` | 已实现 |
| 旧 CraftTweaker Zen 注解 | CraftTweaker 14 原生注解与可撤销 action | 已实现，待游戏内脚本验证 |
| 旧 GameStages API | GameStages 15.0.2 公开 API | 已实现 |
| 反射注入公开 API | Rainfall 初始化时显式安装实现 | 已实现 |

## 实施阶段

1. **构建与元数据**：保留 Rainfall Gradle，补齐 GameStages、source set、mods.toml、语言文件和 loot modifier 数据。
2. **公共 API 与数据模型**：迁移枚举、范围、builder、注册接口和规则加载事件。
3. **解析与匹配**：迁移 JSON、物品/方块字符串、注册表、tags、NBT 和全部 matcher。
4. **掉落运行时**：实现 Global Loot Modifier、经验、工具、爆炸和缓存生命周期。
5. **命令与配置**：迁移 Brigadier、ForgeConfigSpec、日志、导出和热重载。
6. **第三方集成**：按真实源码签名迁移 GameStages 与 CraftTweaker。
7. **验证**：反复执行 `compileJava`，最终执行 `build`、开发环境运行和规则回归。

## 变更日志

### 2026-07-22 - 阶段 0：目标纠正与调查

- 明确 Dropt 1.12.2 仓库只读，Rainfall 才是迁移目标。
- 撤销误加到 Dropt 参考仓库的迁移日志。
- 清点 Rainfall 的现有 Gradle、依赖、入口类和资源。
- 确认使用公共 API / 内部实现两个包域承接旧 Dropt 的双层源码结构。
- 确认 1.20.1 无 `HarvestDropsEvent`，掉落入口使用 Global Loot Modifier。

### 2026-07-22 - 阶段 1A：依赖与元数据

- 保留 Rainfall 原有 ForgeGradle 6、Forge 47.4.22、Oraculum 和 CraftTweaker 配置。
- 加入 GameStages 1.20.1 `15.0.2` 编译期与开发运行期依赖。
- 在 `mods.toml` 中声明 Oraculum 为必需依赖，CraftTweaker 与 GameStages 为可选集成。
- 将 Mixin 配置的兼容级别从 Java 8 调整为 Java 17。

### 2026-07-22 - 阶段 2A：公共 API 与规则数据

- 新建 `com.lirxowo.rainfall.api` 公共 API 域和 `com.lirxowo.rainfall.internal` 实现域。
- 迁移全部 Dropt builder 接口、范围/权重对象和策略枚举。
- 提供 `RainfallAPI` 主入口及兼容命名的 `DroptAPI` 委托入口。
- 保留旧整数维度 API，并增加 1.20.1 资源 ID 维度重载。
- 迁移完整 JSON 规则数据结构，默认垂直范围调整为适配现代世界高度的无界范围。
- 用 `BlockState`、`CompoundTag`、`Ingredient` 和现代 registry key 替换对应旧类型。
- 本批修改已通过 `gradlew compileJava`。

### 2026-07-22 - 阶段 3A：规则解析与加载

- 实现严格未知字段校验、确定性文件排序和 `config/rainfall/*.json` 规则加载。
- 迁移旧物品字符串的数量、NBT、tag、方块状态属性及常见 Ore Dictionary 映射。
- 兼容旧整数维度并映射 `-1`、`0`、`1`，同时支持现代资源 ID 维度。
- 在规则加载阶段预解析匹配对象，避免在掉落热路径重复处理字符串。
- 已知后续项：修正 `ore:` 输入的数量/NBT 后缀保留，并为无法精确迁移的非零 metadata 输出诊断。

### 2026-07-22 - 阶段 3B：规则匹配与选择

- 实现方块状态、原始掉落、玩家/假玩家、双手物品、工具类别与等级、玩家名条件。
- 实现 GameStages、群系、维度、高度和出生点距离条件。
- 实现精确/模糊 NBT、数量和名单策略匹配。
- `RuleLocator` 按 `BlockState` 缓存候选规则，字符串和注册表解析不进入掉落热路径。
- 按 GameStages 15.0.2 sources jar 的公开签名接入 `GameStageHelper.hasAllOf/hasAnyOf`。

### 2026-07-22 - 阶段 4A：掉落修改与 Forge 运行时

- 实现强制掉落、加权选择、`UNIQUE`/`REPEAT` 和五种原掉落替换策略。
- 实现数量匹配、物品堆拆分、经验 `ADD`/`REPLACE` 与替换方块。
- 使用 `BreakEvent` 保存采掘者、工具和原经验，并由 Global Loot Modifier 处理方块 loot context。
- 未命中规则时恢复原经验；命中时应用规则经验和替换方块。
- 注册 GLM codec，并加入 Forge 全局 modifier 资源文件。
- 加入三项旧配置对应的 `ForgeConfigSpec`，完成 Rainfall 生命周期与事件总线初始化。
- 本批修改已反复通过 `gradlew --no-daemon compileJava`；尚待 `processResources`、完整 `build` 与游戏内验证。

### 2026-07-22 - 阶段 3C：旧字符串兼容修正

- 修正 `ore:` 别名丢失数量和 NBT 后缀的问题，并允许 tag 匹配/展开后继续应用 NBT。
- 对无法映射到 1.20.1 扁平化注册表的非零 legacy metadata 输出带规则位置的警告，避免静默扩大匹配范围。
- 保留 metadata `0` 和通配符的兼容接受行为；需要精确状态时应改用方块状态属性或现代物品 ID/tag。
- 清理解析和匹配代码中的内联全限定名，遵守统一导包规范。
- 查阅 1.20.1 `LootTable`、`LootTableReference`、`ServerPlayerGameMode` 和 `ForgeHooks` 后确认：GLM 仅作用于根 loot table，玩家采掘的 BreakEvent 与 loot 生成同步执行。

### 2026-07-22 - 阶段 5A：命令、导出与 verbose

- 实现 Brigadier `/rainfall reload|hand|verbose|export`，权限等级为 2，并保留 `/dropt` 重定向兼容入口。
- `hand` 输出现代物品 ID 与 NBT 规则字符串，聊天中的值可点击复制，不再由服务端访问客户端系统剪贴板。
- `verbose` 仅在存在监听玩家时记录方块，按完整方块状态聚合并在 server tick 末尾发送；玩家退出和服务器停止时清理状态。
- `export` 将当前内存中的 JSON 与脚本规则导出到带时间戳的 `config/rainfall/export` 子目录，并清理非法文件名字符。
- 新增 `en_us` 与 `zh_cn` 语言文件，所有固定玩家提示均使用翻译键。
- 按 Forge 47 源码要求将已弃用的 `BuiltInRegistries.ITEM/BLOCK` 查询替换为 `ForgeRegistries`。
- 按 Forge 47.4.22 FML sources 将 `FMLJavaModLoadingContext` 改为构造器注入，移除两个标记待删除的静态上下文调用。

### 2026-07-22 - 阶段 6A：CraftTweaker 14 脚本 API

- 从本机 CraftTweaker Forge 1.20.1 `14.0.60` jar 反编译确认 `@ZenRegister`、`@ZenCodeType`、`IItemStack`、`IIngredient` 与 action API 签名。
- 迁移旧 `mods.dropt.Dropt`、`RuleList`、`Rule`、`Harvester`、`Drop`、`Range` 和 `Weight` ZenScript 类型及原有方法重载。
- 使用 `IItemStack#getImmutableInternal()` 和 `IIngredient#getItems()` 转换脚本物品，不依赖已移除的旧 Athenaeum/MTLib helper。
- 用可撤销 CraftTweaker action 管理脚本规则列表；脚本加载或撤销时标记规则脏状态，待资源重载完成后的服务端 tick 统一重载。
- CraftTweaker 事件接线只在模组实际存在时注册，保持其可选依赖语义。

### 2026-07-22 - 阶段 5B：诊断与压力测试

- 接通 `enableProfileLogOutput`，记录 mod 规则加载、JSON 加载、规则解析、候选缓存、规则搜索和掉落修改耗时。
- profile 信息同时写入服务端日志与 `config/rainfall/rainfall.log`。
- 接通 `injectProfilingRules`，按旧版实际实现最多注入 150,000 条方块/手持物组合规则，并加入一个包含全物品权重 selector 的石头测试规则。
- 压力规则仍默认关闭，循环上限和 selector 权重提取为常量。

### 2026-07-22 - 阶段 7A：首次服务端启动修正

- 完整 `gradlew build` 已通过并生成 `rainfall-1.0.0.jar`。
- 首次 `runServer` 在依赖排序阶段确认 GameStages 15.0.2 强制需要 Bookshelf 20+，开发环境补入本机已验证的 Bookshelf 20.2.13。
- Rainfall 没有 Mixin 且全部功能使用 Forge API，移除 MixinGradle、注解处理器、启动配置及空 `rainfall.mixins.json`，修复启动阶段的无效 Mixin 配置错误。
- `BlockPredicate` 改用 Forge 方块注册表，并将热路径属性匹配从多层 stream 改为早返回循环。
- 第二次启动确认 CraftTweaker refmap 使用 SRG 名称，而 Parchment 开发运行目标使用 MCP 名称；为所有开发 run 配置接入 `createSrgToMcp/output.srg`，不影响发布 jar。

### 2026-07-22 - 阶段 7B：重载幂等性与启动验证

- `runServer` 已成功加载 Forge、Rainfall、CraftTweaker、Bookshelf、GameStages 和 Oraculum 的 Mixin，最终按预期停在 Mojang EULA 检查；Gradle 任务正常成功结束。
- 未擅自修改开发实例的 `run/eula.txt`，完整进服验证等待用户明确允许接受 EULA。
- 在每次解析规则前统一清空方块、掉落、双手物品、工具等级、群系、维度、产物、数量匹配和替换方块的 transient 派生缓存。
- Java API 与 CraftTweaker 注册的同一规则对象重复参与热重载时，不再累积重复 predicate、物品堆或旧的解析结果。
- 对照旧版 `HeldItemMatcher` 修正组合黑名单语义：物品列表和工具等级条件各自命中时均会排除，不再错误地要求两项同时命中才排除。
- 对照旧版假玩家识别逻辑补齐二次判断：除 Forge `FakePlayer` 外，不在服务器在线玩家表中的 `ServerPlayer` 对象也按假玩家处理。
- 恢复旧版专用诊断日志行为：加载信息、警告、错误、profile 和 debug 现在都会写入 `config/rainfall/rainfall.log`，同时保留控制台输出。
- 对照旧版 1.19.4 changelog 逐项复核空手、NBT、offhand、假玩家、爆炸、fallthrough、替换方块、经验、数量继承、强制掉落和替换策略。
- 查阅 1.20.1 `Explosion` 源码确认爆炸方块 loot context 必定携带 `EXPLOSION_RADIUS`，无需维护旧版按 tick 清理的位置集合。
- 保留 `pickaxe`、`axe`、`shovel`、`hoe`、`sword`、`shears` 的旧工具类别映射，并通过 Forge `ToolAction.get(String)` 支持第三方自定义工具动作。

### 2026-07-22 - 阶段 6B：KubeJS 1.20.1 适配

- EULA 接受后，开发服务端已完整启动至 `Done (12.050s)`，Rainfall 在无规则配置下正常加载 0 个规则列表。
- 从本机 `D:\GradleHome` 确认 KubeJS Forge `2001.6.5-build.26`、Architectury `9.1.12` 和 Rhino `2001.2.2-build.17` 依赖元数据。
- 通过 KubeJS `2001.6.5-build.26` 字节码和同系列 sources 核对 `KubeJSPlugin`、`BindingsEvent`、`ScriptType`、`registerBindings` 与 `onServerReload` 生命周期。
- 加入可选 KubeJS 依赖和 `kubejs.plugins.txt` 插件入口，仅向 server scripts 暴露全局 `Rainfall` builder API。
- KubeJS 服务脚本每次加载前清空旧脚本规则，加载后标记 Rainfall 规则脏状态，避免 `/reload` 后残留或重复注册。
- KubeJS API 覆盖规则列表、规则、采掘者、掉落、范围、权重及旧 Dropt builder 的全部配置入口。

### 2026-07-22 - 阶段 6C：KubeJS 依赖解析

- 为 KubeJS 的传递依赖加入 Architectury Maven，解析 Forge 1.20.1 所需的 Architectury API `9.1.12`。
- 保留 KubeJS 为可选模组依赖；仅开发运行环境加载 KubeJS、Architectury 与 Rhino，Rainfall 发布 jar 不打包这些依赖。
- `gradlew --no-daemon compileJava --rerun-tasks` 已通过，确认 KubeJS 插件入口与 wrapper API 能在所选版本下完整编译。
- 首次开发服启动确认 ForgeGradle 未将 KubeJS POM 中的普通传递依赖作为可加载模组加入运行环境；显式增加 Rhino Forge `2001.2.2-build.17` 与 Architectury Forge `9.1.12` 的 `runtimeOnly fg.deobf` 依赖。

### 2026-07-22 - 阶段 7C：脚本集成测试用例

- 在 `run/scripts` 加入 3 个 CraftTweaker 测试脚本，覆盖固定数量替换、真实玩家与工具等级、fortune/silk touch selector、追加掉落、经验和替换方块。
- 在 `run/kubejs/server_scripts` 加入 3 个等价的 KubeJS 测试脚本，并使用另一组方块避免两套规则相互覆盖。
- 每条规则使用独立规则列表和优先级，便于从 Rainfall 加载日志确认 CraftTweaker 与 KubeJS 注册数量及重载幂等性。
- 首轮脚本启动中 CraftTweaker 3 个脚本均成功编译执行；修正测试工具等级为 Rainfall 的 `动作;最小等级;最大等级` 格式。
- 首轮 KubeJS 启动发现 Rhino 无法可靠区分含 JS 数组的 Java 重载；KubeJS wrapper 改用 `itemsWithRange`、`itemsWithStrategy`、`mainHandItemListWithLevel` 和 `matchLegacyDimensions` 等唯一方法名，消除动态参数分派歧义。
- 按 KubeJS 绑定约束继续完成全包审计，所有暴露给脚本的同名 Java 重载均已移除；固定/fortune 范围、selector、带属性替换方块、名单条件和可选 fallthrough 均使用独立方法名。
- 游戏内回归发现两条 fortune 测试规则将 `REAL_PLAYER`、精确物品白名单、工具动作/等级和 selector 四层条件叠加，任一前置条件不满足都会保留原版掉落，容易把 matcher 失败误判成 selector 概率。
- fortune 测试改为 `PLAYER + 精确物品白名单 + selector`，移除与精确物品重复的 harvest-level 条件，并为这两条规则开启 debug 日志。
- 为便于直接回归，测试 selector 不再要求最低时运等级；无时运也会掉落，精准采集仍排除，掉落数量通过 `fortuneRange` 随时运等级增加。
- 修复现代耐久 NBT 导致的主手白名单失配：`ItemPredicate` 比较精确 NBT 时忽略 `Damage` 字段，同时保留其他显式 NBT（例如附魔）匹配；工具每次使用后的耐久变化不再让规则失效。
- 新增 1.20.1 注册表 ID 附魔条件：主手和副手均可按 `minecraft:fortune` 等附魔 ID 与最低等级匹配，可重复调用要求多个附魔；JSON 对应 `enchantments` 映射。
- 两份 harvester 测试脚本恢复时运要求，但改用 `mainHandEnchantment("minecraft:fortune", 1)`，不再混用旧式工具 tier 字符串。

## 验证状态

- [x] Dropt 参考仓库保持无修改。
- [x] Rainfall 初始结构与依赖已清点。
- [x] 阶段 1 构建与元数据完成。
- [x] `compileJava` 通过（阶段 4A）。
- [x] `processResources` 通过（阶段 4A）。
- [x] `build` 通过，生成 `build/libs/rainfall-1.0.0.jar`。
- [x] 开发服务端完成模组加载并正常到达 EULA 检查。
- [x] KubeJS 2001.6.5-build.26、Rhino 2001.2.2-build.17 与 Architectury 9.1.12 开发服加载成功。
- [x] KubeJS 3 个 server scripts 加载成功（`3/3`，0 errors，0 warnings）。
- [x] CraftTweaker 3 个测试脚本编译执行成功。
- [x] Rainfall 启动时加载 6 个测试规则列表。
- [x] KubeJS wrapper 全部公开方法已避免 Java 方法重载。
- [x] KubeJS 与测试脚本修正后的最终 `gradlew --no-daemon build` 通过。
- [ ] JSON 规则回归完成。
- [ ] 玩家采掘、爆炸、经验和替换方块验证完成。
- [ ] CraftTweaker 与 GameStages 集成验证完成。
