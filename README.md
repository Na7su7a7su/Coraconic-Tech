# 核龙科技 / Coraconic Tech

[中文](#中文) | [English](#english)

---

## 中文

### 项目简介

核龙科技是一个面向 Minecraft 1.21.1 / NeoForge 的早期开发科技模组。当前内容围绕锡、铅、核龙金属、腾龙金属四种材料展开，并已加入物品存储单元、能量存储单元、能量物品交互与相关数据生成体系。

### 目标环境

- Minecraft 1.21.1
- NeoForge 21.1.259+
- Java 21
- Gradle

### 当前版本与状态

- `mod_version`：`0.0.5c`
- 最近版本提交：`0.0.5cu_261026_a`
- 最后更新：2026-10-26
- 仍处于早期开发阶段，注册名、配方、数值、纹理与数据格式可能继续调整
- 暂不保证存档兼容

### 最近更新

- `0.0.5cu_261026_a`：新增粗锡/粗铅与锡/铅矿石的熔炉、高炉配方，矿石配方覆盖深板岩变种。
- `0.0.5ct_261025_c`：为 40 个 Java 源文件添加 MIT 版权与 SPDX 文件头。
- `0.0.5cs_261025_b`：更新 README 的 MIT 许可证说明与版本状态，并将 `mod_license` 同步为 MIT。
- `239ffa4`：新增 MIT License，项目许可证更新为 MIT。
- `0.0.5cr_261024_a`：加入 MK1/MK2/MK3 能量存储单元、能量显示与物品充放电，补齐能量单元掉落内容保留与物品存储单元手写模型，并重写 README。
- `0.0.5cq_261023_b`：为物品存储单元 GUI 加入分级配色的 ITEM 标识。
- `0.0.5cp_261023_a`：统一物品存储单元命名与三档容量，加入潜影盒式内容保存与全局元素掉落规则。

### 快速开始

1. 在创造模式中打开 `核龙科技` 创造栏，可找到材料、晶体、矿石方块及两类存储单元。
2. `核龙科技工具` 创造栏包含四种材料的完整五件套工具。
3. 物品存储单元支持潜影盒式内容保存；能量存储单元提供充电槽、放电槽、能量条与玩家背包。
4. 能量单元物品既可以直接放置，也可以放入其他能量单元的槽位中充放电；悬浮物品即可查看电量。

### 已实现内容

#### 材料与基础物品

- 锡、铅、核龙金属、腾龙金属
- 对应的金属锭、金属粒与粗矿物品
- 常金属核心、核龙金属核心、腾龙金属核心
- 核龙元素、腾龙元素
- 核龙金刚晶体、腾龙金刚晶体
- `hcv2` 自定义食物

#### 工具

- 四种材料各五件套，共 20 种工具：
  - 剑、镐、斧、锹、锄
- 全部工具均有合成配方、解锁进度、手持模型与四语名称
- 独立的工具创造栏
- 工具数值仍处于早期平衡阶段

#### 方块与世界

- 锡块、铅块、核龙金属块、腾龙金属块
- 锡矿石、铅矿石
- 深板岩锡矿石、深板岩铅矿石
- 矿石方块、掉落物与标签已完成，但尚未实现自然生成

#### 物品存储单元

三个等级均为固定 9 行，列数随等级增加：

| 等级 | 槽位 | 界面尺寸 | 说明 |
| --- | ---: | --- | --- |
| MK1 | 81（9×9） | 176×276 | 基础物品存储 |
| MK2 | 162（9×18） | 338×276 | 中型物品存储 |
| MK3 | 243（9×27） | 500×276 | 大型物品存储 |

- 支持漏斗、管道等自动化设施通过 `Capabilities.ItemHandler.BLOCK` 访问
- 破坏方块、创造模式破坏与中键选取时，库存写入 `minecraft:container` 组件，放置后恢复
- 物品 tooltip 显示内容是否已保存以及非空槽位数量
- 大范围拖拽限制为单次最多 120 个槽位，避免 `container_click` 数据包超过 128 项上限
- 三个等级使用手写 Blockbench 方块模型与分级纹理，数据生成器不会覆盖这些模型

#### 能量存储单元

| 等级 | 容量 | 传输速率 |
| --- | ---: | ---: |
| MK1 | 32,000,000 FE | 1,000 FE/t |
| MK2 | 320,000,000 FE | 4,000 FE/t |
| MK3 | 3,200,000,000 FE | 16,000 FE/t |

- 每个单元提供两个机器槽位：
  - 充电槽：把单元能量输入到物品
  - 放电槽：把物品能量输入到单元
- GUI 显示当前能量、容量、能量条与玩家背包
- 能量显示在 1,000 以上使用 `k FE`，1,000,000 以上使用 `M FE`，1,000,000,000 以上使用 `G FE`；保留两位小数，按住 Shift 显示完整 FE 数值
- MK3 等超过 `Integer.MAX_VALUE` 的容量会拆分为多个 int 范围内的能量段；不同面可访问不同段，GUI 与内部充放电使用完整 long 值
- 通过 `Capabilities.EnergyStorage.BLOCK` 向线缆与机器暴露能量能力

#### 能量存储单元物品

- 物品 tooltip 显示当前电量与容量，例如 `能量：1.00 G / 3.20 G FE`
- 物品自身注册 `Capabilities.EnergyStorage.ITEM`，可作为可携带的 FE 容器
- 可放入其他能量单元的充电槽或放电槽，进行双向充放电
- 破坏、创造模式破坏与中键选取时保留能量和内部槽位
- 充放电会写回 `minecraft:block_entity_data`，并保留物品内部库存
- 能量单元物品最大堆叠数为 1，避免多个物品共享同一份能量数据
- 写入方块实体数据时包含类型 `id`，修复此前保存玩家背包时因缺少 `id` 导致的崩溃

#### 配方、标签与战利品

- 金属块 ↔ 金属锭（9:1）
- 金属锭 ↔ 金属粒（1:9）
- 粗锡、粗铅可在熔炉或高炉中冶炼为对应金属锭（0.7 经验）
- 锡矿石、铅矿石也可在熔炉或高炉中冶炼为对应金属锭，包含深板岩变种（0.7 经验）
- 核心、元素、金属锭与工具相关配方
- 通用标签：
  - `c:ingots`、`c:nuggets`、`c:raw_materials`、`c:ores`
  - `c:storage_blocks`、`c:elements`、`c:gems`
  - `c:tools`、`c:foods`
- 工具同时接入原版 `minecraft:swords`、`pickaxes`、`axes`、`shovels`、`hoes`
- 全局战利品规则：
  - `c:ores` 矿石有 10% 概率额外掉落 1 个核龙元素
  - 石头、深板岩、下界岩、末地石有 0.2% 概率额外掉落 1 个核龙元素
  - 凋灵、末影龙、坚守者、远古守卫者掉落 3-5 个腾龙元素

> 物品存储单元与能量存储单元目前主要通过创造模式获取，生存模式合成配方尚未加入。

#### 本地化

- 英语：`en_us`
- 简体中文：`zh_cn`
- 繁体中文：`zh_hk`、`zh_tw`

#### 数据生成

- 方块状态与方块模型（手写存储模型除外）
- 物品模型，包括工具手持模型
- 方块标签与物品标签
- 配方与解锁进度
- 方块战利品表
- 全局战利品修饰器

### 开发进度

| 模块 | 状态 | 说明 |
| --- | --- | --- |
| 金属材料 | 完成 | 四种金属及锭、粒、粗矿 |
| 金属块 | 完成 | 四种金属块与压缩/解压配方 |
| 矿石方块 | 部分完成 | 方块、掉落与标签完成；无自然生成 |
| 工具 | 完成 | 20 种工具、配方、模型、标签与创造栏；数值待平衡 |
| 晶体 | 完成 | 物品、纹理、标签与四语名称 |
| 物品存储 | 完成 | 三级容量、GUI、组件保存、自动化能力与自定义模型 |
| 能量存储 | 完成 | 三级容量、GUI、多段能量、线缆能力与物品充放电 |
| 配方与标签 | 大部分完成 | 基础材料与工具完成；存储单元配方待补 |
| 本地化 | 完成 | `en_us`、`zh_cn`、`zh_hk`、`zh_tw` |
| 数据生成 | 完成 | 模型、标签、配方、进度、战利品与全局修饰器 |
| 世界生成 | 未开始 | 锡矿、铅矿的自然生成 |
| 加工链 | 部分完成 | 粗锡/粗铅与矿石可通过熔炉、高炉冶炼；自动化加工链待扩展 |
| 盔甲 | 未开始 | |
| JEI 支持 | 占位 | 插件为空实现 |
| 代码清理 | 完成 | MDK 示例注册器与演示配置已移除 |

### 已知问题

- 锡矿与铅矿尚未自然生成，只能通过创造模式或调试手段获取。
- 物品存储单元与能量存储单元尚未加入生存模式合成配方。
- JEI 插件仍为空实现，没有额外的配方展示。
- 工具攻击力、攻击速度、等级与耐久仍处于早期平衡阶段。
- 早期开发阶段不保证存档兼容。
- 能量与物品存储单元允许嵌套存放；这是当前设计行为，不提供防嵌套限制。

### 后续计划

- [ ] 实现锡矿、铅矿的自然生成
- [ ] 扩展粗矿处理与自动化冶炼流程
- [ ] 补齐存储单元的生存模式合成配方
- [ ] 添加盔甲
- [ ] 扩展机器与自动化加工链
- [ ] 完善 JEI 配方展示
- [ ] 平衡工具与材料数值

### 构建与运行

```powershell
.\gradlew.bat compileJava
.\gradlew.bat runClient
.\gradlew.bat runData
```

> `runData` 当前可正常完成；手写方块模型与对应生成资源已做跳过处理。

### 许可

本项目采用 [MIT License](LICENSE)。

Copyright (c) 2026 Aestastel

---

## English

### Overview

Coraconic Tech is an early-development NeoForge tech mod for Minecraft 1.21.1. Its current content revolves around tin, lead, coraconic metal, and aviraconic metal, and now includes item storage units, energy storage units, energy-capable storage items, and the related data-generation foundation.

### Target Environment

- Minecraft 1.21.1
- NeoForge 21.1.259+
- Java 21
- Gradle

### Current Version and Status

- `mod_version`: `0.0.5c`
- Latest version commit: `0.0.5cu_261026_a`
- Last updated: 2026-10-26
- Early development: registry names, recipes, stats, textures, and data formats may still change
- Save compatibility is not guaranteed yet

### Recent Changes

- `0.0.5cu_261026_a`: added furnace and blast furnace recipes for raw tin/lead and tin/lead ores, including deepslate ore variants.
- `0.0.5ct_261025_c`: added MIT copyright and SPDX headers to 40 Java source files.
- `0.0.5cs_261025_b`: updated the README MIT license notes and version status, and set `mod_license` to MIT.
- `239ffa4`: added the MIT License and updated the project license to MIT.
- `0.0.5cr_261024_a`: added MK1/MK2/MK3 energy storage units, energy displays, item charging, retained contents for dropped units, hand-authored item storage models, and rewrote the README.
- `0.0.5cq_261023_b`: added tier-colored ITEM markers to the item storage unit GUIs.
- `0.0.5cp_261023_a`: unified item storage unit naming and tiers, added shulker-box-style content persistence, and added global element drop rules.

### Quick Start

1. Open the `Coraconic Tech` creative tab to find materials, crystals, ore blocks, and both storage-unit families.
2. The `Coraconic Tech Tools` tab contains complete five-piece tool sets for all four materials.
3. Item storage units preserve contents like shulker boxes; energy storage units provide a charge slot, a discharge slot, an energy display, and the player inventory.
4. Energy storage unit items can be placed as blocks or charged/discharged in another unit's slots; hover over the item to inspect its charge.

### Implemented Content

#### Materials and Basic Items

- Tin, lead, coraconic metal, and aviraconic metal
- Matching ingots, nuggets, and raw ore items
- Common-metal, coraconic, and aviraconic cores
- Coraconic and aviraconic elements
- Coraconic and aviraconic diamond-structured crystals
- `hcv2`, a custom food item

#### Tools

- Five tools for each of the four materials, 20 tools in total:
  - Sword, pickaxe, axe, shovel, hoe
- Recipes, advancements, handheld models, and four-language names for every tool
- A dedicated tool creative tab
- Tool stats are still in an early balancing stage

#### Blocks and World

- Tin, lead, coraconic metal, and aviraconic metal blocks
- Tin ore and lead ore
- Deepslate tin ore and deepslate lead ore
- Ore blocks, drops, and tags are implemented; natural world generation is not

#### Item Storage Units

All three tiers use a fixed 9 rows; the column count grows with the tier:

| Tier | Slots | GUI size | Notes |
| --- | ---: | --- | --- |
| MK1 | 81 (9×9) | 176×276 | Basic item storage |
| MK2 | 162 (9×18) | 338×276 | Medium item storage |
| MK3 | 243 (9×27) | 500×276 | Large item storage |

- Hoppers, pipes, and other automation can access the inventory through `Capabilities.ItemHandler.BLOCK`
- Breaking, creative-breaking, or pick-blocking the unit stores its inventory in the `minecraft:container` component; it is restored when placed
- The item tooltip reports whether contents are saved and how many slots are occupied
- A single drag is limited to 120 slots to avoid exceeding the 128-entry `container_click` packet limit
- All three tiers use hand-authored Blockbench block models and tier-specific textures; data generation skips these models

#### Energy Storage Units

| Tier | Capacity | Transfer rate |
| --- | ---: | ---: |
| MK1 | 32,000,000 FE | 1,000 FE/t |
| MK2 | 320,000,000 FE | 4,000 FE/t |
| MK3 | 3,200,000,000 FE | 16,000 FE/t |

- Each unit has two machine slots:
  - Charge slot: transfers energy from the unit into an item
  - Discharge slot: transfers energy from an item into the unit
- The GUI shows current energy, capacity, a large energy display, and the player inventory
- Energy is abbreviated with `k FE`, `M FE`, or `G FE` above the corresponding thresholds, with two decimal places; hold Shift to show the full FE values
- Capacities above `Integer.MAX_VALUE`, such as MK3, are split into multiple int-sized banks. Different faces expose different banks, while the GUI and internal item transfer use the full long value
- Energy is exposed to cables and machines through `Capabilities.EnergyStorage.BLOCK`

#### Energy Storage Unit Items

- The item tooltip shows current charge and capacity, for example `Energy: 1.00 G / 3.20 G FE`
- The item registers `Capabilities.EnergyStorage.ITEM` and works as a portable FE container
- It can be placed in another unit's charge or discharge slot for two-way charging
- Breaking, creative-breaking, or pick-blocking the unit preserves its energy and internal slots
- Charging writes back to `minecraft:block_entity_data` while preserving the unit's internal inventory
- Energy unit items have a maximum stack size of 1 so multiple items cannot share one energy component
- The block entity data now includes its type `id`, fixing the previous player-inventory save crash caused by a missing `id`

#### Recipes, Tags, and Loot

- Metal blocks ↔ metal ingots (9:1)
- Metal ingots ↔ metal nuggets (1:9)
- Raw tin and raw lead can be smelted or blasted into the corresponding metal ingots (0.7 XP)
- Tin and lead ores can also be smelted or blasted into the corresponding ingots, including deepslate variants (0.7 XP)
- Recipes for cores, elements, metal ingots, and tools
- Common tags:
  - `c:ingots`, `c:nuggets`, `c:raw_materials`, `c:ores`
  - `c:storage_blocks`, `c:elements`, `c:gems`
  - `c:tools`, `c:foods`
- Tools are also registered under the vanilla `minecraft:swords`, `pickaxes`, `axes`, `shovels`, and `hoes` tags
- Global loot modifiers:
  - `c:ores` blocks have a 10% chance to additionally drop one coraconic element
  - Stone, deepslate, netherrack, and end stone have a 0.2% chance to additionally drop one coraconic element
  - The wither, ender dragon, warden, and elder guardian drop 3-5 aviraconic elements

> Item and energy storage units are currently obtained primarily in creative mode; survival recipes have not been added yet.

#### Localization

- English: `en_us`
- Simplified Chinese: `zh_cn`
- Traditional Chinese: `zh_hk`, `zh_tw`

#### Data Generation

- Block states and block models, except the hand-authored storage models
- Item models, including handheld tool models
- Block and item tags
- Recipes and unlock advancements
- Block loot tables
- Global loot modifiers

### Progress

| Module | Status | Notes |
| --- | --- | --- |
| Metal materials | Complete | Four metals, ingots, nuggets, and raw materials |
| Metal blocks | Complete | Four metal blocks with packing/unpacking recipes |
| Ore blocks | Partial | Blocks, drops, and tags done; no world generation |
| Tools | Complete | 20 tools with recipes, models, tags, and creative tab; stats pending balance |
| Crystals | Complete | Items, textures, tags, and four-language names |
| Item storage | Complete | Three tiers, GUI, component persistence, automation capability, and custom models |
| Energy storage | Complete | Three tiers, GUI, multi-bank storage, cable capability, and item charging |
| Recipes and tags | Mostly complete | Materials and tools done; storage recipes pending |
| Localization | Complete | `en_us`, `zh_cn`, `zh_hk`, `zh_tw` |
| Data generation | Complete | Models, tags, recipes, advancements, loot tables, and global modifiers |
| World generation | Not started | Natural tin and lead ore generation |
| Processing chain | Partial | Raw tin/lead and ores can be smelted/blasted; automated processing chain pending |
| Armor | Not started | |
| JEI support | Placeholder | Empty plugin implementation |
| Code cleanup | Complete | MDK example registrars and demo configuration removed |

### Known Issues

- Tin and lead ores do not generate naturally yet; they are only obtainable in creative mode or through debugging.
- Item and energy storage units do not have survival crafting recipes yet.
- The JEI plugin is still empty and provides no extra recipe information.
- Tool attack damage, attack speed, tier, and durability values are still being balanced.
- Save compatibility is not guaranteed during early development.
- Storage units may be nested inside each other by design; there is no anti-nesting restriction.

### Roadmap

- [ ] Implement natural tin and lead ore generation
- [ ] Expand raw ore processing and automated smelting
- [ ] Add survival recipes for both storage-unit families
- [ ] Add armor
- [ ] Expand machines and automated processing chains
- [ ] Complete JEI recipe integration
- [ ] Balance tool and material stats

### Build and Run

```powershell
.\gradlew.bat compileJava
.\gradlew.bat runClient
.\gradlew.bat runData
```

> `runData` currently completes successfully; hand-authored block models and their generated resources are skipped as intended.

### License

This project is licensed under the [MIT License](LICENSE).

Copyright (c) 2026 Aestastel
