# 核龙科技 / Coraconic Tech

[中文](#中文) | [English](#english)

---

## 中文

### 项目简介

核龙科技是一个面向 Minecraft 1.21.1 / NeoForge 的早期开发科技模组。当前内容围绕锡、铅、核龙金属、腾龙金属四种材料展开，包含金属加工、工具、晶体与基础配方体系，后续将逐步扩展世界生成、加工链、盔甲、机器与能量系统。

### 目标环境

- Minecraft 1.21.1
- NeoForge 21.1.259+
- Java 21
- Gradle 构建

### 当前版本与状态

- `mod_version`：`0.0.5a`
- 提交标签示例：`0.0.5at_261021_e`
- 仍在早期开发中，注册名、配方、数值与纹理可能随时调整
- 暂不保证存档兼容

### 已实现内容

#### 材料

- 锡
- 铅
- 核龙金属
- 腾龙金属

#### 物品

- 锡锭、铅锭、核龙金属锭、腾龙金属锭
- 锡粒、铅粒、核龙金属粒、腾龙金属粒
- 粗锡、粗铅
- 常金属核心、核龙金属核心、腾龙金属核心
- 核龙元素、腾龙元素
- 核龙金刚晶体、腾龙金刚晶体
- `hcv2` 自定义食物

#### 工具

- 四种材料各五件套，共 20 种工具：
  - 剑
  - 镐
  - 斧
  - 锹
  - 锄
- 独立的工具创造栏
- 全部工具均有合成配方、解锁进度、手持模型与四语名称
- 工具数值与等级仍使用占位值，尚未进行平衡

#### 方块

- 锡块、铅块、核龙金属块、腾龙金属块
- 锡矿石、铅矿石
- 深板岩锡矿石、深板岩铅矿石

> 矿石方块已注册，但尚未实现自然生成。

#### 配方与标签

- 金属块 ↔ 金属锭（9:1）
- 金属锭 ↔ 金属粒（1:9）
- 核心、元素、金属锭与工具相关配方
- 通用标签：
  - `c:ingots`
  - `c:nuggets`
  - `c:raw_materials`
  - `c:ores`
  - `c:storage_blocks`
  - `c:elements`
  - `c:gems`
  - `c:tools`
  - `c:foods`
- 工具同时接入原版 `minecraft:swords`、`pickaxes`、`axes`、`shovels`、`hoes`
- 晶体标签位于 `c:gems/coraconic-diamond-structured` 与 `c:gems/aviraconic-diamond-structured`

#### 本地化

- 英语：`en_us`
- 简体中文：`zh_cn`
- 繁体中文：`zh_hk`、`zh_tw`

#### 数据生成

- 方块状态与方块模型
- 物品模型（普通物品与工具手持模型）
- 方块标签与物品标签
- 配方与解锁进度
- 方块战利品表

### 开发进度

| 模块 | 状态 | 说明 |
| --- | --- | --- |
| 金属材料 | 完成 | 四种金属及锭、粒、粗矿 |
| 金属块 | 完成 | 四种金属块与压缩/解压配方 |
| 矿石方块 | 部分完成 | 方块、掉落与标签已完成，无自然生成 |
| 工具 | 完成 | 20 种工具、配方、模型、标签与创造栏；数值待平衡 |
| 晶体 | 部分完成 | 物品、标签与四语名称已完成；暂无纹理与模型 |
| 配方与标签 | 完成 | 当前内容所需配方与通用标签 |
| 本地化 | 完成 | `en_us`、`zh_cn`、`zh_hk`、`zh_tw` |
| 数据生成 | 完成 | 模型、标签、配方、进度与战利品表 |
| 世界生成 | 未开始 | 锡矿、铅矿的自然生成 |
| 加工链 | 未开始 | 粗矿处理、冶炼等流程 |
| 盔甲 | 未开始 | |
| 机器与能量 | 未开始 | |
| JEI 支持 | 占位 | 插件为空实现 |
| 代码清理 | 完成 | MDK 示例注册器与演示配置已移除 |

### 已知问题

- 锡矿与铅矿尚未自然生成，只能通过创造模式或调试手段获取。
- 核龙金刚晶体与腾龙金刚晶体暂无纹理与模型；在补齐纹理前，`runData` 会在物品模型阶段失败。
- JEI 插件仍为空实现，没有额外的配方展示。
- 工具攻击力、攻击速度、等级与耐久仍为占位数值。
- 早期开发阶段不保证存档兼容。

### 后续计划

- [ ] 实现锡矿、铅矿的自然生成
- [ ] 设计粗矿处理与冶炼流程
- [ ] 补齐两种晶体的纹理与模型
- [ ] 添加盔甲
- [ ] 添加机器与能量系统
- [ ] 完善 JEI 配方展示
- [ ] 平衡工具与材料数值

### 构建与运行

```powershell
.\gradlew.bat compileJava
.\gradlew.bat runClient
.\gradlew.bat runData
```

> 当前 `runData` 需要先补齐两种晶体的纹理，否则会在物品模型生成阶段中断。

---

## English

### Overview

Coraconic Tech is an early-development NeoForge tech mod for Minecraft 1.21.1. Its current content revolves around four materials — tin, lead, coraconic metal, and aviraconic metal — with metal processing, tools, crystals, and a basic recipe and tag foundation. World generation, processing chains, armor, machines, and energy systems are planned for later stages.

### Target Environment

- Minecraft 1.21.1
- NeoForge 21.1.259+
- Java 21
- Gradle

### Current Version and Status

- `mod_version`: `0.0.5a`
- Example commit tag: `0.0.5at_261021_e`
- Early development: registry names, recipes, stats, and textures may change
- Save compatibility is not guaranteed yet

### Implemented Content

#### Materials

- Tin
- Lead
- Coraconic metal
- Aviraconic metal

#### Items

- Tin, lead, coraconic metal, and aviraconic metal ingots
- Tin, lead, coraconic metal, and aviraconic metal nuggets
- Raw tin and raw lead
- Common-metal, coraconic, and aviraconic cores
- Coraconic and aviraconic elements
- Coraconic and aviraconic diamond-structured crystals
- `hcv2`, a custom food item

#### Tools

- Five tools for each of the four materials, 20 tools in total:
  - Sword
  - Pickaxe
  - Axe
  - Shovel
  - Hoe
- A dedicated tool creative tab
- Recipes, advancements, handheld models, and four-language names for every tool
- Tool stats and tiers are still placeholders and have not been balanced

#### Blocks

- Tin, lead, coraconic metal, and aviraconic metal blocks
- Tin ore and lead ore
- Deepslate tin ore and deepslate lead ore

> Ore blocks are registered, but natural world generation is not implemented yet.

#### Recipes and Tags

- Metal blocks ↔ metal ingots (9:1)
- Metal ingots ↔ metal nuggets (1:9)
- Recipes for cores, elements, metal ingots, and tools
- Common tags:
  - `c:ingots`
  - `c:nuggets`
  - `c:raw_materials`
  - `c:ores`
  - `c:storage_blocks`
  - `c:elements`
  - `c:gems`
  - `c:tools`
  - `c:foods`
- Tools are also registered under the vanilla `minecraft:swords`, `pickaxes`, `axes`, `shovels`, and `hoes` tags
- Crystal tags live under `c:gems/coraconic-diamond-structured` and `c:gems/aviraconic-diamond-structured`

#### Localization

- English: `en_us`
- Simplified Chinese: `zh_cn`
- Traditional Chinese: `zh_hk`, `zh_tw`

#### Data Generation

- Block states and block models
- Item models, including handheld models for tools
- Block and item tags
- Recipes and unlock advancements
- Block loot tables

### Progress

| Module | Status | Notes |
| --- | --- | --- |
| Metal materials | Complete | Four metals, ingots, nuggets, and raw materials |
| Metal blocks | Complete | Four metal blocks with packing/unpacking recipes |
| Ore blocks | Partial | Blocks, drops, and tags done; no world generation |
| Tools | Complete | 20 tools with recipes, models, tags, and creative tab; stats pending balance |
| Crystals | Partial | Items, tags, and four-language names done; textures and models missing |
| Recipes and tags | Complete | Recipes and common tags for current content |
| Localization | Complete | `en_us`, `zh_cn`, `zh_hk`, `zh_tw` |
| Data generation | Complete | Models, tags, recipes, advancements, and loot tables |
| World generation | Not started | Natural tin and lead ore generation |
| Processing chain | Not started | Raw ore processing and smelting |
| Armor | Not started | |
| Machines and energy | Not started | |
| JEI support | Placeholder | Empty plugin implementation |
| Code cleanup | Complete | MDK example registrars and demo configuration removed |

### Known Issues

- Tin and lead ores do not generate naturally yet; they are only obtainable in creative mode or through debugging.
- The coraconic and aviraconic crystals have no textures or models yet; `runData` will fail at the item model stage until textures are added.
- The JEI plugin is still empty and provides no extra recipe information.
- Tool attack damage, attack speed, tier, and durability values are placeholders.
- Save compatibility is not guaranteed during early development.

### Roadmap

- [ ] Implement natural tin and lead ore generation
- [ ] Design raw ore processing and smelting
- [ ] Add textures and models for both crystals
- [ ] Add armor
- [ ] Add machines and energy systems
- [ ] Complete JEI recipe integration
- [ ] Balance tool and material stats

### Build and Run

```powershell
.\gradlew.bat compileJava
.\gradlew.bat runClient
.\gradlew.bat runData
```

> `runData` currently requires textures for both crystals; otherwise it stops during item model generation.
