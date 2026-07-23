# Rainfall CraftTweaker 修改教程

本文档适用于 Minecraft 1.20.1、CraftTweaker 14 和 Rainfall 当前版本。Rainfall 向 ZenScript 注册的入口类型是 `mods.dropt.Dropt`。

## 1. 脚本位置、导入与重载

脚本放在：

```text
scripts/*.zs
```

开发实例中的实际路径通常是：

```text
run/scripts/
```

每个脚本开头导入 Rainfall 入口：

```zenscript
import mods.dropt.Dropt;
```

修改后使用 CraftTweaker 的服务端脚本重载命令：

```text
/ct reload
```

Rainfall 使用可撤销 CraftTweaker Action 管理规则列表。重载时旧脚本规则会撤销，新规则会重新创建，随后 Rainfall 安全刷新规则缓存。

Rainfall 自带的辅助命令：

- `/rainfall reload`：重新加载 Rainfall 规则。
- `/rainfall hand`：显示并复制主手物品字符串。
- `/rainfall verbose`：切换详细方块记录。
- `/rainfall export`：导出当前规则。

## 2. 最小规则

```zenscript
import mods.dropt.Dropt;

Dropt.list("stone_to_diamond")
    .priority(100)
    .add(
        Dropt.rule()
            .matchBlocks(["minecraft:stone"])
            .replaceStrategy("REPLACE_ALL")
            .addDrop(
                Dropt.drop()
                    .force()
                    .items([<item:minecraft:diamond>])
            )
    );
```

结构为：

```text
Dropt.list(...)
└── Dropt.rule()
    ├── 匹配条件
    ├── 原掉落处理策略
    └── 一个或多个 Dropt.drop()
```

同一规则中不同类别的匹配条件按“并且”组合。

## 3. 参数类型与现代标签

### 3.1 ZenScript 参数

| Java/Zen 类型 | ZenScript 示例 |
| --- | --- |
| `string` | `"minecraft:stone"` |
| `string[]` | `["minecraft:stone", "minecraft:granite"]` |
| `int` | `3` |
| `int[]` | `[-1, 0, 1]` |
| `bool` | `true` |
| `IItemStack` | `<item:minecraft:diamond>` |
| `IItemStack[]` | `[<item:minecraft:diamond>, <item:minecraft:emerald>]` |
| `IIngredient` | `<item:minecraft:iron_ingot>` 或物品标签 |
| `IIngredient[]` | `[<tag:items:forge:ingots/iron>]` |
| `Map<string, string>` | `{"axis": "y"}` |
| `mods.dropt.Range` | `Dropt.range(...)` |
| `mods.dropt.Weight` | `Dropt.weight(...)` |

所有枚举参数都是区分大小写的字符串，必须使用文档中的全大写值。

### 3.2 方块字符串

`matchBlocks` 使用字符串，支持现代 ID、方块标签和状态属性：

```zenscript
.matchBlocks(["minecraft:stone"])
.matchBlocks(["#minecraft:coal_ores"])
.matchBlocks(["minecraft:oak_log[axis=y]"])
```

`#namespace:path` 和 `tag:namespace:path` 都是 Rainfall 的现代标签写法。

### 3.3 物品和物品标签

具体物品使用 CraftTweaker 1.20.1 item bracket：

```zenscript
<item:minecraft:diamond>
```

`matchDrops` 接受 `IIngredient[]`，因此可以直接使用现代物品标签：

```zenscript
.matchDrops([<tag:items:forge:ingots/iron>])
```

直接的 `TagIngredient` 会以标签形式保留到 Rainfall 的 `TagKey<Item>` / `Ingredient` 匹配中。

以下方法只接受具体 `IItemStack[]`，不能传标签：

- `Harvester.mainHand(...)`
- `Harvester.offHand(...)`
- `Drop.items(...)`
- `Drop.matchQuantity(...)`

输出物品需要明确具体物品，不能让标签隐式决定生成哪一种物品。

不支持旧矿词名称或方块/物品 metadata。

## 4. Dropt 顶层方法

### 4.1 对象工厂

| 方法 | 参数 | 返回值 | 用途 |
| --- | --- | --- | --- |
| `Dropt.list(name)` | `name: string` | `RuleList` | 创建或取得 `crafttweaker:name` 规则列表 |
| `Dropt.rule()` | 无 | `Rule` | 创建规则 |
| `Dropt.harvester()` | 无 | `Harvester` | 创建采集者条件 |
| `Dropt.drop()` | 无 | `Drop` | 创建掉落项 |

同名 `list` 在一次脚本加载中返回同一个规则列表。`name` 应使用合法的小写资源路径字符。

### 4.2 数值范围

| 重载 | 参数 | 含义 |
| --- | --- | --- |
| `Dropt.range(fixed)` | `fixed: int` | 固定值 |
| `Dropt.range(min, max)` | 两个 `int` | 闭区间随机值 |
| `Dropt.range(min, max, fortuneModifier)` | 三个 `int` | 随机值并按时运修正 |

时运加成为：

```text
max(0, 时运等级 × fortuneModifier)
```

示例：

```zenscript
Dropt.range(2)
Dropt.range(1, 4)
Dropt.range(1, 3, 2)
```

建议保证 `min <= max` 并使用非负数量。

### 4.3 选择权重

| 重载 | 参数 | 含义 |
| --- | --- | --- |
| `Dropt.weight(weight)` | `weight: int` | 固定权重 |
| `Dropt.weight(weight, fortuneModifier)` | 两个 `int` | 权重随时运等级增加 |

实际权重：

```text
weight + 时运等级 × fortuneModifier
```

实际权重小于等于 0 时，候选不会进入选择池。

## 5. RuleList 方法

### `priority(priority)`

- `priority: int`。
- 数值越大越先处理，默认 0。
- 优先级相同的规则列表按加载顺序处理。

### `add(rule)`

- `rule: mods.dropt.Rule`。
- 每次调用追加一条规则。

```zenscript
val list = Dropt.list("overworld_rules").priority(200);

list.add(Dropt.rule().matchBlocks(["minecraft:stone"]));
list.add(Dropt.rule().matchBlocks(["minecraft:deepslate"]));
```

## 6. Rule 方法

### 6.1 调试

| 方法 | 参数 | 含义 |
| --- | --- | --- |
| `debug()` | 无 | 输出该规则的条件匹配、selector、掉落和经验日志 |

### 6.2 方块和原掉落匹配

| 重载 | 参数 | 默认/含义 |
| --- | --- | --- |
| `matchBlocks(blocks)` | `string[]` | 方块白名单 |
| `matchBlocks(type, blocks)` | `type: string`、`string[]` | `WHITELIST` 或 `BLACKLIST` |
| `matchDrops(items)` | `IIngredient[]` | 原版掉落物白名单 |
| `matchDrops(type, items)` | 名单类型、`IIngredient[]` | 自定义原掉落名单 |

原掉落条件只要有任意一个原掉落物匹配任意 Ingredient 即视为找到。`BLACKLIST` 会反转结果。

```zenscript
.matchBlocks("BLACKLIST", ["#minecraft:dirt"])
.matchDrops([
    <item:minecraft:coal>,
    <tag:items:forge:gems/diamond>
])
```

### 6.3 采集者

| 方法 | 参数 |
| --- | --- |
| `matchHarvester(harvester)` | `Dropt.harvester()` 返回的 `Harvester` |

### 6.4 群系

| 重载 | 参数 |
| --- | --- |
| `matchBiomes(ids)` | 群系 ID `string[]`，默认白名单 |
| `matchBiomes(type, ids)` | `WHITELIST/BLACKLIST`、群系 ID 数组 |

```zenscript
.matchBiomes(["minecraft:plains", "minecraft:forest"])
```

### 6.5 维度

| 重载 | 参数 | 说明 |
| --- | --- | --- |
| `matchDimensions(ids)` | 现代维度 ID `string[]` | 推荐 |
| `matchDimensions(type, ids)` | 名单类型、现代维度 ID `string[]` | 推荐 |
| `matchDimensions(ids)` | 整数 `int[]` | 兼容入口：`-1` 下界、`0` 主世界、`1` 末地 |
| `matchDimensions(type, ids)` | 名单类型、整数 `int[]` | 新脚本不推荐 |

现代写法：

```zenscript
.matchDimensions(["minecraft:overworld"])
.matchDimensions("BLACKLIST", ["minecraft:the_nether"])
```

### 6.6 高度和出生点距离

| 重载 | 参数 | 说明 |
| --- | --- | --- |
| `matchVerticalRange(min, max)` | 两个 `int` | 方块 Y 坐标闭区间 |
| `matchSpawnDistance(min, max)` | 两个 `int` | 距世界出生点的 X/Z 水平距离闭区间白名单 |
| `matchSpawnDistance(type, min, max)` | 名单类型、两个 `int` | 可用黑名单反转条件 |

`min` 会限制为至少 0；`max = -1` 表示无上限。

### 6.7 规则处理方式

| 方法 | 参数 | 含义 |
| --- | --- | --- |
| `replaceStrategy(strategy)` | 替换策略字符串 | 控制原版掉落 |
| `dropStrategy(strategy)` | `UNIQUE` 或 `REPEAT` | 控制候选是否可重复选中 |
| `dropCount(range)` | `mods.dropt.Range` | 加权选择次数，默认固定 1 |
| `addDrop(drop)` | `mods.dropt.Drop` | 添加掉落项，可重复调用 |
| `fallthrough()` | 无 | 可选参数默认为 `true` |
| `fallthrough(value)` | `value: bool` | 是否在匹配后继续处理后续规则 |

默认 `fallthrough = false`。一条规则匹配后，如果未启用 fallthrough，后续低优先级规则不会执行。

### 6.8 原掉落替换策略

| 值 | 行为 |
| --- | --- |
| `REPLACE_ALL` | 进入规则后立即清空所有原掉落，即使没有生成新物品 |
| `ADD` | 保留原掉落并追加新掉落 |
| `REPLACE_ALL_IF_SELECTED` | 实际生成新物品时才清空所有原掉落 |
| `REPLACE_ITEMS` | 移除与本规则 `matchDrops` 匹配的原掉落 |
| `REPLACE_ITEMS_IF_SELECTED` | 实际生成新物品时才移除与 `matchDrops` 匹配的原掉落 |

`REPLACE_ITEMS` 系列应与 `matchDrops` 一起使用。

### 6.9 候选选择策略

| 值 | 行为 |
| --- | --- |
| `REPEAT` | 每次选择后候选仍保留，可重复选中 |
| `UNIQUE` | 选中后移除候选，每个 Drop 最多一次 |

`dropCount` 是加权候选选择次数；`force()` 掉落不占用选择次数。

## 7. Harvester 方法

### 7.1 采集者类型

| 方法 | 参数 |
| --- | --- |
| `type(type)` | `ANY`、`PLAYER`、`REAL_PLAYER`、`FAKE_PLAYER`、`NON_PLAYER`、`EXPLOSION` |

各类型语义：

| 值 | 含义 |
| --- | --- |
| `ANY` | 没有玩家也可匹配；有玩家时检查附加玩家条件 |
| `PLAYER` | 任意玩家，包括假玩家，并检查附加条件 |
| `REAL_PLAYER` | 真实在线玩家，并检查附加条件 |
| `FAKE_PLAYER` | Forge 假玩家；当前实现不检查手持物、玩家名和 GameStages |
| `NON_PLAYER` | 没有玩家的上下文 |
| `EXPLOSION` | 爆炸方块掉落；当前实现只检查爆炸标记 |

### 7.2 主手方法

| 重载 | 参数 | 含义 |
| --- | --- | --- |
| `mainHand(harvestLevel)` | 等级字符串 | 工具动作和 tier 白名单 |
| `mainHand(items)` | `IItemStack[]` | 主手具体物品白名单 |
| `mainHand(type, items)` | 名单类型、`IItemStack[]` | 自定义主手物品名单 |
| `mainHand(type, items, harvestLevel)` | 名单类型、物品数组、等级字符串 | 同时配置物品和工具等级 |
| `mainHandEnchantment(enchantmentId, minimumLevel)` | 附魔 ID、最低等级 | 要求主手附魔，可重复调用 |

主手使用实际破坏方块时的工具。

```zenscript
Dropt.harvester()
    .type("REAL_PLAYER")
    .mainHand(
        "WHITELIST",
        [<item:minecraft:diamond_pickaxe>],
        "pickaxe;3;-1"
    )
    .mainHandEnchantment("minecraft:fortune", 1)
```

### 7.3 副手方法

| 重载 | 参数 | 含义 |
| --- | --- | --- |
| `offHand(harvestLevel)` | 等级字符串 | 副手工具动作和 tier 白名单 |
| `offHand(items)` | `IItemStack[]` | 副手具体物品白名单 |
| `offHand(type, items)` | 名单类型、`IItemStack[]` | 自定义副手物品名单 |
| `offHand(type, items, harvestLevel)` | 名单类型、物品数组、等级字符串 | 同时配置物品和工具等级 |
| `offHandEnchantment(enchantmentId, minimumLevel)` | 附魔 ID、最低等级 | 要求副手附魔，可重复调用 |

### 7.4 工具等级字符串

格式：

```text
动作;最低tier;最高tier
```

内置动作：

- `pickaxe`
- `axe`
- `shovel`
- `hoe`
- `sword`
- `shears`

其他名称会按 Forge `ToolAction` 处理。最低或最高 tier 小于 0 表示该方向不限制。非 `TieredItem` 的 tier 按 0 处理。

同一只手同时设置具体物品、工具等级和多个附魔时，`WHITELIST` 下全部条件都要满足。附魔最低等级必须至少为 1。

### 7.5 GameStages

| 重载 | 参数 | 含义 |
| --- | --- | --- |
| `gameStages(stages)` | `string[]` | 白名单，默认 `ANY` |
| `gameStages(require, stages)` | `require: ANY/ALL`、`string[]` | 指定任意或全部阶段 |
| `gameStages(type, require, stages)` | 名单类型、`ANY/ALL`、`string[]` | 完整条件 |

```zenscript
.gameStages("WHITELIST", "ALL", ["mining_tier_2", "overworld_mastery"])
```

存在 GameStages 条件但未安装 GameStages 模组时，规则不会匹配。

### 7.6 玩家名

| 重载 | 参数 | 含义 |
| --- | --- | --- |
| `playerName(names)` | `string[]` | 玩家名白名单 |
| `playerName(type, names)` | 名单类型、`string[]` | 自定义玩家名名单 |

玩家名匹配不区分大小写。

## 8. Drop 方法

### 8.1 强制和 selector

| 重载 | 参数 | 行为 |
| --- | --- | --- |
| `force()` | 无 | 每次都选中，不参与权重、精准采集和最低时运过滤 |
| `selector(weight)` | `mods.dropt.Weight` | 普通加权候选 |
| `selector(weight, silkTouch)` | 权重、`ANY/REQUIRED/EXCLUDED` | 精准采集过滤 |
| `selector(weight, silkTouch, fortuneLevelRequired)` | 权重、精准采集条件、最低时运等级 | 完整 selector |

没有调用 `force` 或 `selector` 的 Drop 使用默认权重 1 和 `ANY`，但建议显式声明。

精准采集枚举：

| 值 | 含义 |
| --- | --- |
| `ANY` | 是否有精准采集均可 |
| `REQUIRED` | 必须有精准采集 |
| `EXCLUDED` | 必须没有精准采集 |

### 8.2 生成物品

| 重载 | 参数 | 默认/行为 |
| --- | --- | --- |
| `items(items)` | `IItemStack[]` | `ONE`，数量 1 |
| `items(strategy, items)` | `ONE/ALL`、`IItemStack[]` | 指定列表策略 |
| `items(items, range)` | `IItemStack[]`、`Range` | `ONE`，指定数量 |
| `items(strategy, items, range)` | `ONE/ALL`、`IItemStack[]`、`Range` | 完整配置 |
| `matchQuantity(drops)` | `IItemStack[]` | 根据匹配的原掉落总数设置新物品数量 |

列表策略：

- `ONE`：从数组随机选择一个具体物品。
- `ALL`：数组内每个具体物品都生成，范围数量应用到每一种。

```zenscript
.items(
    "ALL",
    [<item:minecraft:diamond>, <item:minecraft:emerald>],
    Dropt.range(1, 2)
)
```

`matchQuantity` 会汇总原掉落中相同物品的数量；没有匹配时继续使用配置范围。

### 8.3 经验

| 方法 | 参数 |
| --- | --- |
| `xp(replace, amount)` | `replace: ADD/REPLACE`、`amount: Range` |

- `ADD`：在原经验上增加范围值。
- `REPLACE`：使用范围值替换。

经验按每个被选中的 Drop 计算。

### 8.4 替换方块

| 重载 | 参数 | 行为 |
| --- | --- | --- |
| `replaceBlock(block)` | 方块 ID 字符串 | 使用默认方块状态 |
| `replaceBlock(block, properties)` | 方块 ID、`Map<string, string>` | 使用指定状态属性 |

```zenscript
.replaceBlock("minecraft:oak_log", {"axis": "y"})
```

属性名和值必须是目标方块实际拥有的 1.20.1 状态属性。多个已选 Drop 都设置替换方块时，只采用第一个有效替换。

## 9. 完整示例

### 9.1 方块标签、加权候选与时运

```zenscript
import mods.dropt.Dropt;

Dropt.list("coal_bonus")
    .priority(500)
    .add(
        Dropt.rule()
            .matchBlocks(["#minecraft:coal_ores"])
            .replaceStrategy("ADD")
            .dropStrategy("UNIQUE")
            .dropCount(Dropt.range(2))
            .addDrop(
                Dropt.drop()
                    .selector(Dropt.weight(70), "EXCLUDED")
                    .items(
                        [<item:minecraft:coal>],
                        Dropt.range(1, 2, 1)
                    )
            )
            .addDrop(
                Dropt.drop()
                    .selector(Dropt.weight(30), "EXCLUDED")
                    .items([<item:minecraft:diamond>])
            )
    );
```

### 9.2 Ingredient 标签匹配原掉落

```zenscript
import mods.dropt.Dropt;

Dropt.list("ingot_conversion")
    .priority(400)
    .add(
        Dropt.rule()
            .matchBlocks(["minecraft:iron_ore"])
            .matchDrops([<tag:items:forge:raw_materials/iron>])
            .replaceStrategy("REPLACE_ITEMS")
            .addDrop(
                Dropt.drop()
                    .force()
                    .items([<item:minecraft:gold_ingot>])
            )
    );
```

### 9.3 玩家、工具、附魔、维度与高度

```zenscript
import mods.dropt.Dropt;

Dropt.list("deep_mining")
    .priority(600)
    .add(
        Dropt.rule()
            .debug()
            .matchBlocks(["#minecraft:base_stone_overworld"])
            .matchDimensions(["minecraft:overworld"])
            .matchVerticalRange(-64, 0)
            .matchHarvester(
                Dropt.harvester()
                    .type("REAL_PLAYER")
                    .mainHand(
                        "WHITELIST",
                        [<item:minecraft:diamond_pickaxe>],
                        "pickaxe;3;-1"
                    )
                    .mainHandEnchantment("minecraft:fortune", 1)
                    .playerName("BLACKLIST", ["ExamplePlayer"])
            )
            .replaceStrategy("ADD")
            .addDrop(
                Dropt.drop()
                    .force()
                    .items(
                        [<item:minecraft:raw_iron>],
                        Dropt.range(1, 2)
                    )
            )
    );
```

### 9.4 GameStages、经验和方块状态

```zenscript
import mods.dropt.Dropt;

Dropt.list("stage_reward")
    .priority(700)
    .add(
        Dropt.rule()
            .matchBlocks(["minecraft:diamond_ore"])
            .matchHarvester(
                Dropt.harvester()
                    .type("REAL_PLAYER")
                    .gameStages(
                        "WHITELIST",
                        "ALL",
                        ["mining_tier_2", "overworld_mastery"]
                    )
            )
            .replaceStrategy("REPLACE_ALL")
            .addDrop(
                Dropt.drop()
                    .force()
                    .items(
                        "ALL",
                        [
                            <item:minecraft:diamond>,
                            <item:minecraft:experience_bottle>
                        ],
                        Dropt.range(1)
                    )
                    .xp("ADD", Dropt.range(3, 7))
                    .replaceBlock("minecraft:stone")
            )
    );
```

### 9.5 多规则继续执行

```zenscript
import mods.dropt.Dropt;

val list = Dropt.list("stacked_rules").priority(300);

list.add(
    Dropt.rule()
        .matchBlocks(["minecraft:stone"])
        .replaceStrategy("ADD")
        .addDrop(
            Dropt.drop()
                .force()
                .items([<item:minecraft:flint>])
        )
        .fallthrough()
);

list.add(
    Dropt.rule()
        .matchBlocks(["minecraft:stone"])
        .replaceStrategy("ADD")
        .addDrop(
            Dropt.drop()
                .force()
                .items([<item:minecraft:coal>])
        )
);
```

## 10. 常见问题

### 找不到 mods.dropt.Dropt

确认 CraftTweaker 与 Rainfall 都已加载，并检查脚本开头：

```zenscript
import mods.dropt.Dropt;
```

### 重载时报找不到方法

确认调用的方法参数与某个公开重载完全一致。尤其注意：

- `matchDrops` 接受 `IIngredient[]`。
- `mainHand`、`offHand`、`items` 和 `matchQuantity` 接受 `IItemStack[]`。
- 方块标签作为字符串传给 `matchBlocks`。
- 枚举字符串必须全大写。

### 标签没有匹配

确认标签注册表类型正确：

- 方块标签：`"#minecraft:coal_ores"`，用于 `matchBlocks`。
- 物品标签：`<tag:items:forge:ingots/iron>`，用于 `matchDrops`。

### selector 没有产生掉落

依次检查最低时运、精准采集条件、实际权重、`dropCount` 和输出物品数组。

### 多条规则只有第一条执行

默认匹配后停止。在前一条规则上调用 `fallthrough()` 或 `fallthrough(true)`。
