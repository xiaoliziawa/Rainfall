# Rainfall KubeJS 修改教程

本文档适用于 Minecraft 1.20.1、KubeJS 2001 和 Rainfall 当前版本。Rainfall 只在 KubeJS 的服务端脚本环境中提供 `Rainfall` 全局对象。

## 1. 脚本位置与重载

脚本放在：

```text
kubejs/server_scripts/*.js
```

开发实例中的实际路径通常是：

```text
run/kubejs/server_scripts/
```

修改脚本后执行 KubeJS 服务端脚本重载。Rainfall 会清空上一轮 KubeJS 规则，重新收集本轮脚本创建的规则，并在服务端 tick 中安全刷新规则缓存。

可以使用以下命令辅助排查：

- `/rainfall reload`：重新加载 Rainfall 规则。
- `/rainfall hand`：显示并复制主手物品的 Rainfall 字符串。
- `/rainfall verbose`：切换详细方块记录。
- `/rainfall export`：导出当前规则。

这些命令需要权限等级 2。

## 2. 最小规则

```js
Rainfall.list('stone_to_diamond')
  .priority(100)
  .add(
    Rainfall.rule()
      .matchBlocks(['minecraft:stone'])
      .replaceStrategy('REPLACE_ALL')
      .addDrop(
        Rainfall.drop()
          .force()
          .items(['minecraft:diamond'])
      )
  )
```

结构固定为：

```text
规则列表 Rainfall.list(...)
└── 规则 Rainfall.rule()
    ├── 匹配条件
    ├── 原掉落处理策略
    └── 一个或多个 Rainfall.drop()
```

同一规则内的不同匹配类别按“并且”组合。例如同时设置方块、维度和主手条件时，三者必须全部满足。

## 3. 参数格式

### 3.1 JavaScript 类型

| 文档类型 | KubeJS 写法 | 示例 |
| --- | --- | --- |
| `String` | 字符串 | `'minecraft:stone'` |
| `String[]` | 字符串数组 | `['minecraft:stone', 'minecraft:granite']` |
| `int` | 整数 | `3` |
| `boolean` | 布尔值 | `true` |
| `Map<String, String>` | JS 对象 | `{ axis: 'y' }` |
| `KubeRange` | `Rainfall.fixedRange/range/fortuneRange` 的返回值 | `Rainfall.range(1, 3)` |
| `KubeWeight` | `Rainfall.weight/fortuneWeight` 的返回值 | `Rainfall.weight(100)` |

所有枚举字符串都区分大小写，必须使用文档列出的全大写值。

### 3.2 物品、方块和标签

Rainfall 使用 1.20.1 资源 ID、标签和方块状态属性：

```js
// 具体物品或方块
'minecraft:diamond'
'minecraft:stone'

// 现代物品或方块标签
'#forge:ingots/iron'
'#minecraft:coal_ores'

// tag: 前缀也是合法的
'tag:forge:ingots/iron'

// 方块状态
'minecraft:oak_log[axis=y]'
'minecraft:wheat[age=7]'
```

物品字符串还可以携带精确 NBT 和数量：

```text
minecraft:diamond_sword#{CustomModelData:1}
minecraft:diamond * 3
```

匹配条件通常不关心字符串中的数量。生成掉落数量推荐使用 `itemsWithRange` 等范围方法。

不支持旧矿词名称或方块/物品 metadata。

### 3.3 通用枚举

#### 名单类型

| 值 | 含义 |
| --- | --- |
| `WHITELIST` | 至少匹配名单中的一个值；手持物复合条件需要全部满足 |
| `BLACKLIST` | 匹配名单中的值时拒绝 |

#### 采集者类型

| 值 | 含义 |
| --- | --- |
| `ANY` | 无玩家时也可匹配；有玩家时会检查手持物、玩家名和 GameStages 条件 |
| `PLAYER` | 任意玩家，包括假玩家，并检查玩家附加条件 |
| `REAL_PLAYER` | 真实在线玩家，并检查玩家附加条件 |
| `FAKE_PLAYER` | Forge 假玩家；当前实现不再检查手持物、玩家名和 GameStages 条件 |
| `NON_PLAYER` | 没有玩家的掉落上下文 |
| `EXPLOSION` | 爆炸产生的方块掉落；当前实现只检查爆炸标记 |

#### 精准采集条件

| 值 | 含义 |
| --- | --- |
| `ANY` | 是否有精准采集均可 |
| `REQUIRED` | 必须使用精准采集 |
| `EXCLUDED` | 必须没有精准采集 |

## 4. Rainfall 顶层方法

| 方法 | 参数 | 返回值 | 用途 |
| --- | --- | --- | --- |
| `list(name)` | `name: String` | 规则列表 | 创建或取得 `kubejs:name` 规则列表 |
| `rule()` | 无 | 规则 | 创建一条规则 |
| `harvester()` | 无 | 采集者条件 | 创建玩家/工具条件 |
| `drop()` | 无 | 掉落项 | 创建一个候选或强制掉落 |
| `fixedRange(fixed)` | `fixed: int` | 范围 | 固定值 |
| `range(min, max)` | 两个 `int` | 范围 | 闭区间随机值 |
| `fortuneRange(min, max, fortuneModifier)` | 三个 `int` | 范围 | 随机值再加时运修正 |
| `weight(weight)` | `weight: int` | 权重 | 固定选择权重 |
| `fortuneWeight(weight, fortuneModifier)` | 两个 `int` | 权重 | 权重随时运等级增加 |

范围的时运加成为：

```text
max(0, 时运等级 × fortuneModifier)
```

`fortuneRange(1, 3, 2)` 在时运 II 时会在基础随机值上增加 4。建议保证 `min <= max`，并使用非负数量。

候选的实际权重为：

```text
weight + 时运等级 × fortuneModifier
```

实际权重小于等于 0 时，该候选不会进入选择池。

## 5. 规则列表方法

### `priority(priority)`

- `priority: int`：规则列表优先级。
- 数值越大越先处理，默认值为 0。
- 优先级相同时按加载顺序处理。

### `add(rule)`

- `rule: Rainfall.rule()` 创建的规则。
- 每调用一次向当前列表追加一条规则。

```js
const list = Rainfall.list('overworld_rules').priority(200)
list.add(Rainfall.rule().matchBlocks(['minecraft:stone']))
list.add(Rainfall.rule().matchBlocks(['minecraft:deepslate']))
```

## 6. Rule 方法

### 6.1 匹配与调试

| 方法 | 参数 | 默认/含义 |
| --- | --- | --- |
| `debug()` | 无 | 为该规则输出匹配、selector 和结果日志 |
| `matchBlocks(blocks)` | `blocks: String[]` | 方块白名单；支持 ID、方块标签和状态属性 |
| `matchBlockList(type, blocks)` | `type: WHITELIST/BLACKLIST`，`blocks: String[]` | 自定义方块名单类型 |
| `matchDrops(items)` | `items: String[]` | 原版掉落物白名单；任意原掉落命中即可 |
| `matchDropList(type, items)` | 名单类型和 `String[]` | 自定义原掉落名单类型 |
| `matchHarvester(harvester)` | `Rainfall.harvester()` | 添加采集者条件 |
| `matchBiomes(ids)` | `ids: String[]` | 群系 ID 白名单 |
| `matchBiomeList(type, ids)` | 名单类型和群系 ID 数组 | 自定义群系名单 |
| `matchDimensions(ids)` | `ids: String[]` | 现代维度 ID 白名单 |
| `matchDimensionList(type, ids)` | 名单类型和维度 ID 数组 | 自定义现代维度名单 |
| `matchLegacyDimensions(ids)` | `ids: int[]` | 兼容整数维度：`-1` 下界、`0` 主世界、`1` 末地；新脚本不推荐 |
| `matchLegacyDimensionList(type, ids)` | 名单类型和 `int[]` | 自定义整数维度名单；新脚本不推荐 |
| `matchVerticalRange(min, max)` | 两个 `int` | 方块 Y 坐标闭区间 |
| `matchSpawnDistance(min, max)` | 两个 `int` | 距世界出生点的水平距离闭区间白名单 |
| `matchSpawnDistanceList(type, min, max)` | 名单类型和两个 `int` | 可使用黑名单反转距离条件 |

`matchSpawnDistance` 只计算 X/Z 平面距离。`min` 会被限制为至少 0；`max = -1` 表示无上限。

### 6.2 掉落处理

| 方法 | 参数 | 用途 |
| --- | --- | --- |
| `replaceStrategy(strategy)` | 替换策略字符串 | 控制原版掉落如何保留或移除 |
| `dropStrategy(strategy)` | `UNIQUE` 或 `REPEAT` | 控制加权候选能否重复选中 |
| `dropCount(range)` | `KubeRange` | 加权选择次数，默认固定 1 |
| `addDrop(drop)` | `Rainfall.drop()` | 向规则追加掉落项，可重复调用 |
| `fallthrough()` | 无 | 等价于 `setFallthrough(true)` |
| `setFallthrough(value)` | `value: boolean` | 是否在本规则匹配后继续查找后续规则 |

默认 `fallthrough = false`。匹配到一条未开启 fallthrough 的规则后，后续低优先级规则不会执行。

### 6.3 原掉落替换策略

| 值 | 行为 |
| --- | --- |
| `REPLACE_ALL` | 进入规则后立即清空全部原掉落，即使最终没有选出新物品 |
| `ADD` | 保留全部原掉落，再加入新掉落 |
| `REPLACE_ALL_IF_SELECTED` | 只有实际生成了新物品时才清空全部原掉落 |
| `REPLACE_ITEMS` | 移除与本规则 `matchDrops` 条件匹配的原掉落 |
| `REPLACE_ITEMS_IF_SELECTED` | 实际生成新物品时，才移除与 `matchDrops` 匹配的原掉落 |

`REPLACE_ITEMS` 系列需要配置 `matchDrops` 或 `matchDropList`，否则没有可移除的目标。

### 6.4 候选选择策略

| 值 | 行为 |
| --- | --- |
| `REPEAT` | 每次选择后候选仍在池中，同一个 Drop 可重复选中 |
| `UNIQUE` | 每次选择后移除该候选，同一个 Drop 最多选中一次 |

`dropCount` 控制的是“从加权候选池选择几次”，不是最终物品堆数量。`force()` 掉落不占用这些选择次数。

## 7. Harvester 方法

所有主手、副手、附魔、玩家名和 GameStages 方法都应连接在 `Rainfall.harvester()` 后，再通过规则的 `matchHarvester(...)` 使用。

### 7.1 采集者类型

| 方法 | 参数 |
| --- | --- |
| `type(type)` | `ANY`、`PLAYER`、`REAL_PLAYER`、`FAKE_PLAYER`、`NON_PLAYER` 或 `EXPLOSION` |

### 7.2 主手条件

| 方法 | 参数 | 含义 |
| --- | --- | --- |
| `mainHandLevel(harvestLevel)` | 等级字符串 | 工具动作和 tier 白名单 |
| `mainHandItems(items)` | `String[]` | 物品/标签白名单 |
| `mainHandItemsWithLevel(items, harvestLevel)` | `String[]`、等级字符串 | 同时要求物品和工具等级 |
| `mainHandLevelList(type, harvestLevel)` | 名单类型、等级字符串 | 自定义工具等级名单 |
| `mainHandItemList(type, items)` | 名单类型、`String[]` | 自定义物品名单 |
| `mainHandItemListWithLevel(type, items, harvestLevel)` | 名单类型、物品数组、等级字符串 | 同时配置两类条件 |
| `mainHandEnchantment(enchantmentId, minimumLevel)` | 附魔 ID、最低等级 | 要求主手具有指定附魔，可重复调用 |

主手取的是实际破坏方块时的工具。

### 7.3 副手条件

| 方法 | 参数 | 含义 |
| --- | --- | --- |
| `offHandLevel(harvestLevel)` | 等级字符串 | 副手工具动作和 tier 白名单 |
| `offHandItems(items)` | `String[]` | 副手物品/标签白名单 |
| `offHandItemsWithLevel(items, harvestLevel)` | `String[]`、等级字符串 | 同时要求副手物品和工具等级 |
| `offHandLevelList(type, harvestLevel)` | 名单类型、等级字符串 | 自定义等级名单 |
| `offHandItemList(type, items)` | 名单类型、`String[]` | 自定义副手物品名单 |
| `offHandItemListWithLevel(type, items, harvestLevel)` | 名单类型、物品数组、等级字符串 | 同时配置两类条件 |
| `offHandEnchantment(enchantmentId, minimumLevel)` | 附魔 ID、最低等级 | 要求副手具有指定附魔，可重复调用 |

### 7.4 工具等级字符串

格式：

```text
动作;最低tier;最高tier
```

示例：

```js
.mainHandLevel('pickaxe;2;4')
```

内置动作名包括 `pickaxe`、`axe`、`shovel`、`hoe`、`sword`、`shears`；其他名称会作为 Forge `ToolAction` 名称处理。最低或最高值小于 0 表示该方向不设限制。非 `TieredItem` 的 tier 按 0 处理。

同一只手同时配置物品、工具等级和多个附魔时，`WHITELIST` 下这些条件全部需要满足。`minimumLevel` 必须至少为 1。

### 7.5 GameStages 和玩家名

| 方法 | 参数 | 含义 |
| --- | --- | --- |
| `gameStages(stages)` | `String[]` | 白名单，默认满足任意一个 `ANY` |
| `requiredGameStages(require, stages)` | `require: ANY/ALL`、`String[]` | 指定满足任意或全部阶段 |
| `gameStageList(type, require, stages)` | 名单类型、`ANY/ALL`、`String[]` | 完整 GameStages 条件 |
| `playerName(names)` | `String[]` | 玩家名白名单，不区分大小写 |
| `playerNameList(type, names)` | 名单类型、`String[]` | 自定义玩家名名单 |

存在 GameStages 条件但未安装 GameStages 模组时，规则不会匹配。

## 8. Drop 方法

### 8.1 强制与加权选择

| 方法 | 参数 | 行为 |
| --- | --- | --- |
| `force()` | 无 | Drop 每次都选中，不参与权重池、精准采集或最低时运过滤 |
| `selector(weight)` | `KubeWeight` | 普通加权候选 |
| `fortuneSelector(weight, fortuneLevelRequired)` | 权重、最低时运等级 | 时运过滤后的加权候选 |
| `silkTouchSelector(weight, silkTouch)` | 权重、`ANY/REQUIRED/EXCLUDED` | 精准采集过滤后的候选 |
| `silkTouchFortuneSelector(weight, silkTouch, fortuneLevelRequired)` | 权重、精准采集条件、最低时运等级 | 同时设置两类过滤 |

没有调用 `force` 或 selector 的 Drop 仍具有默认权重 1，但建议显式调用其中一种，方便阅读规则。

### 8.2 生成物品

| 方法 | 参数 | 行为 |
| --- | --- | --- |
| `items(items)` | `String[]` | 默认 `ONE`，数量 1 |
| `itemsWithStrategy(strategy, items)` | `ONE/ALL`、`String[]` | 指定列表策略 |
| `itemsWithRange(items, range)` | `String[]`、`KubeRange` | 默认 `ONE`，指定数量 |
| `itemsWithStrategyAndRange(strategy, items, range)` | `ONE/ALL`、`String[]`、`KubeRange` | 完整配置 |
| `matchQuantity(drops)` | `String[]` | 原掉落命中时，使用其原始总数量替代配置数量 |

列表策略：

- `ONE`：从 `items` 中随机选一种物品。
- `ALL`：为 `items` 中每种物品生成一次，范围数量应用到每一种。

`matchQuantity` 会汇总原掉落中同一物品的数量，并使用首个匹配项的总数；没有匹配项时继续使用配置范围。

### 8.3 经验与替换方块

| 方法 | 参数 | 行为 |
| --- | --- | --- |
| `xp(replace, amount)` | `replace: ADD/REPLACE`、`KubeRange` | 修改经验 |
| `replaceBlock(block)` | 方块 ID | 破坏后放置该方块的默认状态 |
| `replaceBlockWithProperties(block, properties)` | 方块 ID、属性对象 | 放置指定方块状态 |

`xp('ADD', range)` 在原经验上增加范围值；`xp('REPLACE', range)` 使用范围值替换。经验按每个被选中的 Drop 计算。

```js
.replaceBlockWithProperties('minecraft:oak_log', { axis: 'y' })
```

多个被选中的 Drop 都配置替换方块时，只采用第一个有效替换。

## 9. 完整示例

### 9.1 标签匹配与多种掉落

```js
Rainfall.list('coal_bonus')
  .priority(500)
  .add(
    Rainfall.rule()
      .matchBlocks(['#minecraft:coal_ores'])
      .replaceStrategy('ADD')
      .dropStrategy('UNIQUE')
      .dropCount(Rainfall.fixedRange(2))
      .addDrop(
        Rainfall.drop()
          .silkTouchSelector(Rainfall.weight(70), 'EXCLUDED')
          .itemsWithRange(['minecraft:coal'], Rainfall.fortuneRange(1, 2, 1))
      )
      .addDrop(
        Rainfall.drop()
          .silkTouchSelector(Rainfall.weight(30), 'EXCLUDED')
          .items(['minecraft:diamond'])
      )
  )
```

### 9.2 玩家、工具、附魔、维度和高度

```js
Rainfall.list('deep_mining')
  .priority(600)
  .add(
    Rainfall.rule()
      .debug()
      .matchBlocks(['#minecraft:base_stone_overworld'])
      .matchDimensions(['minecraft:overworld'])
      .matchVerticalRange(-64, 0)
      .matchHarvester(
        Rainfall.harvester()
          .type('REAL_PLAYER')
          .mainHandItemList('WHITELIST', ['#forge:tools/pickaxes'])
          .mainHandLevel('pickaxe;2;-1')
          .mainHandEnchantment('minecraft:fortune', 1)
          .playerNameList('BLACKLIST', ['ExamplePlayer'])
      )
      .replaceStrategy('ADD')
      .addDrop(
        Rainfall.drop()
          .force()
          .itemsWithRange(['minecraft:raw_iron'], Rainfall.range(1, 2))
      )
  )
```

### 9.3 按原掉落数量替换

```js
Rainfall.list('gravel_conversion')
  .priority(300)
  .add(
    Rainfall.rule()
      .matchBlocks(['minecraft:gravel'])
      .matchDrops(['minecraft:flint'])
      .replaceStrategy('REPLACE_ITEMS')
      .addDrop(
        Rainfall.drop()
          .force()
          .items(['minecraft:iron_nugget'])
          .matchQuantity(['minecraft:flint'])
      )
  )
```

### 9.4 GameStages、经验和替换方块

```js
Rainfall.list('stage_reward')
  .priority(700)
  .add(
    Rainfall.rule()
      .matchBlocks(['minecraft:diamond_ore'])
      .matchHarvester(
        Rainfall.harvester()
          .type('REAL_PLAYER')
          .requiredGameStages('ALL', ['mining_tier_2', 'overworld_mastery'])
      )
      .replaceStrategy('REPLACE_ALL')
      .addDrop(
        Rainfall.drop()
          .force()
          .itemsWithStrategyAndRange(
            'ALL',
            ['minecraft:diamond', 'minecraft:experience_bottle'],
            Rainfall.fixedRange(1)
          )
          .xp('ADD', Rainfall.range(3, 7))
          .replaceBlock('minecraft:stone')
      )
  )
```

## 10. 常见问题

### 脚本中找不到 Rainfall

确认脚本位于 `server_scripts`，而不是 `client_scripts` 或 `startup_scripts`，并确认 Rainfall 与 KubeJS 均已加载。

### 枚举报错

枚举必须全大写，例如 `'REPLACE_ALL_IF_SELECTED'`、`'WHITELIST'`、`'EXCLUDED'`。

### 标签没有匹配

确认标签类型正确：方块条件需要方块标签，物品/手持物条件需要物品标签。可以使用 `/rainfall verbose` 查看实际方块状态。

### 配置了 selector 却没有掉落

检查：

1. 最低时运是否满足。
2. 精准采集条件是否满足。
3. 实际权重是否大于 0。
4. `dropCount` 是否会得到大于 0 的值。
5. `items` 是否包含有效物品。

### 多条规则只有第一条执行

默认匹配后停止。需要在前一条规则上调用 `fallthrough()` 或 `setFallthrough(true)`。
