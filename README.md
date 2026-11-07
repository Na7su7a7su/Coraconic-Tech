# 核龙科技 / Coraconic Tech

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.x-orange.svg)](https://neoforged.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

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

- `mod_version`：`0.0.7a`
- 最近版本提交：`0.0.7ax_261107_g`
- 最后更新：2026-11-07
- 仍处于早期开发阶段，注册名、配方、数值、纹理与数据格式可能继续调整
- 暂不保证存档兼容

### 最近更新

- `0.0.7ax_261107_g`：新增共振晶化仪 MK1，提供 `640,000 FE` 缓存、`1,000 FE/t` 输入、两个原料槽、成品槽、能源槽、加工进度与 JEI 分类；加入测试配方：1 钻石 + 1 木炭 → 2 钻石，耗能 `100,000 FE`、耗时 `3,000 ticks`；同步创造栏、能力、掉落、纹理与四语名称。
- `0.0.7aw_261107_f`：将方块实体相关类从 `aestastel.coraconic.block` 拆分到 `aestastel.coraconic.blockentity`；同步方块、菜单、注册器与能力引用，注册 ID、资源与 NBT 保持不变。
- `0.0.7av_261107_e`：将 README 的“最近更新”与“Recent Changes”列表截断为最近 10 次提交。
- `0.0.7au_261107_d`：新增太阳能 `Radiation` 模式；仅 MK3 支持，主世界 `Y >= 315` 或其余维度 `Y >= 250` 时优先触发，发电 `1,440 FE/t`，覆盖其他所有模式；补齐中英文说明。
- `0.0.7at_261107_c`：将核龙/腾龙金属块的注册 ID、配方 ID、资源路径与语言键统一改为 `coraconic_metal_block` / `aviraconic_metal_block`；同步标签、方块状态、模型、掉落表、配方、解锁进度与四语名称。
- `0.0.7as_261107_b`：将合成配方生成器进一步拆分为独立配方与公式配方两类；新增/调整合金仪配方：下界合金锭使用 2 金锭 + 4 下界合金碎片，核龙金属锭使用 1 铅 + 1 锡 + 1 核龙元素，腾龙金属锭使用 1 钻石 + 1 绿宝石 + 1 腾龙元素；同步核龙金属锭合成配方与生成资源。
- `0.0.7ar_261107_a`：将配方数据生成器拆分为合成、合金与熔炼三个生成器；保留 `ModRecipesProvider` 作为唯一数据生成入口，避免重复的 `Recipes` provider 名称；配方生成结果保持不变。
- `0.0.7aq_261106_b`：为合金仪加入 JEI 配方分类与机器催化剂，显示最多三组带数量的原料、加工箭头、成品槽、总耗能与加工时间；补充四语 JEI 文本。
- `0.0.7ap_261106_a`：新增合金仪 MK1，提供 `320,000 FE` 缓存、三个原料槽、成品槽、放电槽和加工进度条；加入测试合金配方：4 个金锭 + 4 个下界合金碎片 → 1 个下界合金锭，耗能 `20,000 FE`、耗时 `100 ticks`；补齐合金配方类型、序列化器、GUI、数据生成、四语名称与资源。
- `0.0.6fs_261105_b`：新增地磁发电机 MK1–MK3，复用对应太阳能等级的缓存与输出，按 `Overworld` / `Nether` / `End` 提供分级功率；补齐方块、方块实体、能力、创造栏、战利品表、四语名称、纹理与数据生成；I/O 按钮右侧再加宽 1 像素。

### 快速开始

1. 在创造模式中打开 `核龙科技` 创造栏，可找到材料、晶体、矿石方块、两类存储单元、合金仪 MK1、共振晶化仪 MK1、太阳能发电机 MK0–MK3 及地磁发电机 MK0–MK3。
2. `核龙科技工具` 创造栏包含四种材料的完整五件套工具。
3. 物品存储单元支持潜影盒式内容保存；能量存储单元提供充电槽、放电槽、能量条与玩家背包。
4. 能量单元物品既可以直接放置，也可以放入其他能量单元的槽位中充放电；悬浮物品即可查看电量。

### 已实现内容

#### 材料与基础物品

- 锡、铅、核龙金属、腾龙金属
- 对应的金属锭、金属粒与粗矿物品
- 常金属核心、核龙金属核心、腾龙金属核心、效应放大核心、空间放缩核心、相位同步核心、质能补偿核心、中枢计算核心
- 五个特殊核心当前仅通过创造模式获取，暂未添加合成配方
- 核龙元素、腾龙元素
- 核龙金刚晶体、腾龙金刚晶体
- `hcv2` 自定义食物

#### 工具

- 四种材料各五件套，共 20 种工具：
    - 剑、镐、斧、锹、锄
- 全部工具均有合成配方、解锁进度、手持模型与四语名称
- 独立的工具创造栏
- 扳手：Shift + 滚轮在旋转/扳手/配置模式间循环
    - 旋转模式：右键顺时针旋转，Shift + 右键逆时针旋转
    - 扳手模式：右键正常交互，Shift + 右键拆除七种功能方块
    - 配置模式：不与功能方块正常交互；右键查看所点面的能量模式并打印到聊天栏，Shift + 右键按输入 → 输出 → 输入/输出 → 阻断 → 输入切换
- 工具数值仍处于早期平衡阶段

#### 方块与世界

- 锡块、铅块、核龙金属块、腾龙金属块
- 锡矿石、铅矿石
- 深板岩锡矿石、深板岩铅矿石
- 矿石方块、掉落物与标签已完成，但尚未实现自然生成

#### 物品存储单元

三个等级均为固定 9 行，列数随等级增加：

| 等级 |        槽位 | 界面尺寸 | 说明         |
| ---- | ----------: | -------- | ------------ |
| MK1  |   81（9×9） | 176×276  | 基础物品存储 |
| MK2  | 162（9×18） | 338×276  | 中型物品存储 |
| MK3  | 243（9×27） | 500×276  | 大型物品存储 |

- 支持漏斗、管道等自动化设施通过 `Capabilities.ItemHandler.BLOCK` 访问
- 破坏方块、创造模式破坏与中键选取时，库存写入 `minecraft:container` 组件，放置后恢复
- 物品 tooltip 显示内容是否已保存以及非空槽位数量
- 物品栏与快捷栏以黄色耐久条显示已占用槽位比例
- 大范围拖拽限制为单次最多 120 个槽位，避免 `container_click` 数据包超过 128 项上限
- 三个等级使用手写 Blockbench 方块模型与分级纹理，数据生成器不会覆盖这些模型

#### 能量存储单元

| 等级 |             容量 |    传输速率 |
| ---- | ---------------: | ----------: |
| MK1  |    32,000,000 FE |  4,000,000 FE/t |
| MK2  |   320,000,000 FE | 16,000,000 FE/t |
| MK3  | 3,200,000,000 FE | 64,000,000 FE/t |

- 每个单元提供两个机器槽位：
    - 充电槽：把单元能量输入到物品
    - 放电槽：把物品能量输入到单元
- GUI 显示当前能量、容量、能量条与玩家背包
- 能量显示在 1,000 以上使用 `k FE`，1,000,000 以上使用 `M FE`，1,000,000,000 以上使用 `G FE`；保留两位小数，按住 Shift 显示完整 FE 数值
- MK3 等超过 `Integer.MAX_VALUE` 的容量会拆分为多个 int 范围内的能量段；不同面可访问不同段，GUI 与内部充放电使用完整 long 值
- 六个面可分别设置为输入、输出、输入/输出或阻断，默认全部为输入/输出
- GUI 左侧外部上方提供统一的面配置按钮与弹窗：左键正向循环、右键反向循环，展开后不遮挡物品栏或界面主体，并与扳手配置模式共用同一套设置
- 输入面会主动从相邻能量方块抽取能量，输出面会主动向相邻能量方块发送能量；阻断面拒绝所有外部输入输出
- 通过 `Capabilities.EnergyStorage.BLOCK` 向线缆与机器暴露经过面模式门控的能量能力

#### 合金仪 MK1

- 内部缓存：`320,000 FE`，从所有面接收能量，最高 `10,000 FE/t`
- 三个原料槽、一个成品槽和一个放电槽；成品槽只能取出，不能放入
- 放电槽只接受可提取能量的物品，每 tick 最多向缓存输入 `10,000 FE`，不会给物品充电
- GUI 中央提供加工进度条
- 测试配方：4 个金锭 + 4 个下界合金碎片 → 1 个下界合金锭
- 测试配方耗能 `20,000 FE`，加工时间 `100 ticks`
- JEI 提供独立的合金仪配方分类、带数量的原料槽、加工箭头、成品槽与耗能/时间信息
- 破坏方块时保留缓存能量、五个槽位的内容和加工进度
- 当前仅通过创造模式获取，机器本体尚未添加合成配方

#### 共振晶化仪 MK1

- 内部缓存：`640,000 FE`，从所有面接收能量，最高 `1,000 FE/t`
- 两个原料槽、一个成品槽和一个能源槽；成品槽只能取出，不能放入
- 能源槽只接受可接收能量的物品，每 tick 最多从缓存充能 `1,000 FE`
- GUI 中央提供加工进度条
- 测试配方：1 个钻石 + 1 个木炭 → 2 个钻石
- 测试配方耗能 `100,000 FE`，加工时间 `3,000 ticks`
- JEI 提供独立的共振晶化仪配方分类、带数量的原料槽、加工箭头、成品槽与耗能/时间信息
- 破坏方块时保留缓存能量、四个槽位的内容和加工进度
- 当前仅通过创造模式获取，机器本体尚未添加合成配方

#### 太阳能发电机 MK0

- 内部缓存：`96,000 FE`
- 白天、维度不是下界、天空光大于 10，且上方整条天空柱没有完整不透明方块遮挡时，每个 tick 发电 `30 FE/t`
- 六个面可分别设置为输入、输出、输入/输出或阻断，默认全部为输出
- GUI 左侧外部上方提供与能量存储单元相同的面配置按钮与弹窗：左键正向循环、右键反向循环，展开后不遮挡物品栏或界面主体，也可使用扳手配置模式
- 输出面每面最高主动输出 `600 FE/t`，输入面也可主动抽取相邻能量；阻断面不接受外部输入输出
- GUI 中央为黑底绿字 CLI 面板，输出/充电槽位于右侧，左侧保留竖直能量条、能量数值与玩家背包
- CLI 显示 `>Power Generating System`、`>Generation MK* - Version <mod_version>`、`>Power: <规则速率> FE/t`、`>Mode: <模式>`
- 模式优先级为 `Radiation (MK3) > Nether > Reflection > End/Solar > Thunder > Solar/Lunar`；末地无遮挡时固定为 `Solar`，遮挡时仍为 `Reflection`；MK0/MK1 白天天空光不足时模式为 `Null`；功率由模式决定，`Power` 不因缓存已满而归零
- 输出槽只接受可接收能量的物品；缓存优先给输出槽物品充能，剩余能量再对外开放
- 破坏方块时，方块物品保留缓存能量与面配置；输出槽物品单独掉落
- 通过 `Capabilities.EnergyStorage.BLOCK` 向线缆与机器暴露经过面模式门控的能量能力

#### 地磁发电机 MK0–MK3

| 等级 |     缓存容量 |   每面输出 |   物品充能 | Overworld | Nether | End |
| ---- | -----------: | ---------: | ---------: | --------: | -----: | --: |
| MK0  |    96,000 FE |  600 FE/t |  600 FE/t |    2 FE/t | 3 FE/t | 6 FE/t |
| MK1  |   192,000 FE | 1,200 FE/t | 1,200 FE/t |    6 FE/t | 16 FE/t | 32 FE/t |
| MK2  |   480,000 FE | 2,400 FE/t | 2,400 FE/t |   32 FE/t | 56 FE/t | 112 FE/t |
| MK3  | 1,920,000 FE | 9,600 FE/t | 9,600 FE/t |  112 FE/t | 320 FE/t | 640 FE/t |

- 各等级复用对应太阳能等级的缓存、每面输出、物品充能、输出槽、面配置、GUI 与掉落保留逻辑
- 主世界：Y 坐标必须小于 -59
- 下界、其他模组维度与末地：Y 坐标必须小于 6
- 主世界：正下方为基岩时工作，发电 `2 FE/t`，模式为 `Overworld`
- 下界与其他模组维度：正下方为基岩时工作，发电 `3 FE/t`，模式为 `Nether`
- 末地：正下方没有方块时工作，发电 `6 FE/t`，模式为 `End`
- 其他情况模式为 `Null`，发电 `0 FE/t`
- GUI 复用太阳能 MK0 贴图与 CLI，最后一行显示 `>Geomagnetic Energy`
- 当前仅通过创造模式获取，尚未添加合成配方

#### 太阳能发电机 MK1–MK3

| 等级 |     缓存容量 |   每面输出 |   物品充能 |
| ---- | -----------: | ---------: | ---------: |
| MK1  |   192,000 FE | 1,200 FE/t | 1,200 FE/t |
| MK2  |   480,000 FE | 2,400 FE/t | 2,400 FE/t |
| MK3  | 1,920,000 FE | 9,600 FE/t | 9,600 FE/t |

- 三个新等级共用 MK0 的输出槽、面模式配置、GUI 与掉落保留逻辑
- MK1：维度不是下界、白天、天空光大于 10、上方无完整不透明遮挡时发电 `120 FE/t`；雷暴时天空光为 10，因此不发电
- MK2：维度不是下界且上方无完整遮挡时工作；雷暴 `120 FE/t`；白天 `240 FE/t`；黑夜 `120 FE/t`
- MK3：下界始终 `80 FE/t`；非下界有完整遮挡时 `80 FE/t`；无遮挡时雷暴 `360 FE/t`、白天 `960 FE/t`、黑夜 `240 FE/t`
- MK3：主世界 `Y >= 315`，下界、末地与其他模组维度 `Y >= 250` 时进入 `Radiation` 模式，优先于其他所有模式，发电 `1,440 FE/t`；MK0–MK2 不支持该模式
- 当前仅通过创造模式获取，尚未添加合成配方

#### 能量存储单元物品

- 物品 tooltip 显示当前电量与容量，例如 `能量：1.00 G / 3.20 G FE`
- 物品栏与快捷栏通过类似耐久条的绿色电量条显示当前电量
- 物品自身注册 `Capabilities.EnergyStorage.ITEM`，可作为可携带的 FE 容器
- 可放入其他能量单元的充电槽或放电槽，进行双向充放电
- 破坏、创造模式破坏与中键选取时保留能量、内部槽位与六个面配置
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

> 物品存储单元、能量存储单元与太阳能发电机目前主要通过创造模式获取，生存模式合成配方尚未加入。

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

| 模块       | 状态       | 说明                                                    |
| ---------- | ---------- | ------------------------------------------------------- |
| 金属材料   | 完成       | 四种金属及锭、粒、粗矿                                  |
| 金属块     | 完成       | 四种金属块与压缩/解压配方                               |
| 矿石方块   | 部分完成   | 方块、掉落与标签完成；无自然生成                        |
| 工具       | 完成       | 20 种工具、配方、模型、标签与创造栏；数值待平衡         |
| 晶体       | 完成       | 物品、纹理、标签与四语名称                              |
| 物品存储   | 完成       | 三级容量、GUI、组件保存、自动化能力与自定义模型         |
| 能量存储   | 完成       | 三级容量、GUI、多段能量、线缆能力与物品充放电           |
| 太阳能发电 | 完成       | MK0–MK3、分级缓存/输出与维度/天气/遮挡规则              |
| 配方与标签 | 大部分完成 | 基础材料与工具完成；存储单元配方待补                    |
| 本地化     | 完成       | `en_us`、`zh_cn`、`zh_hk`、`zh_tw`                      |
| 数据生成   | 完成       | 模型、标签、配方、进度、战利品与全局修饰器              |
| 世界生成   | 未开始     | 锡矿、铅矿的自然生成                                    |
| 加工链     | 部分完成   | 粗锡/粗铅与矿石可通过熔炉、高炉冶炼；自动化加工链待扩展 |
| 盔甲       | 未开始     |                                                         |
| JEI 支持   | 占位       | 插件为空实现                                            |
| 代码清理   | 完成       | MDK 示例注册器与演示配置已移除                          |

### 已知问题

- 锡矿与铅矿尚未自然生成，只能通过创造模式或调试手段获取。
- 物品存储单元、能量存储单元与太阳能发电机尚未加入生存模式合成配方。
- JEI 插件仍为空实现，没有额外的配方展示。
- 工具攻击力、攻击速度、等级与耐久仍处于早期平衡阶段。
- 早期开发阶段不保证存档兼容。
- 能量与物品存储单元允许嵌套存放；这是当前设计行为，不提供防嵌套限制。

### 后续计划

- [ ] 实现锡矿、铅矿的自然生成
- [ ] 扩展粗矿处理与自动化冶炼流程
- [ ] 补齐存储单元与太阳能发电机的生存模式合成配方
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

- `mod_version`: `0.0.7a`
- Latest version commit: `0.0.7ax_261107_g`
- Last updated: 2026-11-07
- Early development: registry names, recipes, stats, textures, and data formats may still change
- Save compatibility is not guaranteed yet

### Recent Changes

- `0.0.7ax_261107_g`: added Resonance Crystallizing Machine MK1 with a `640,000 FE` buffer, `1,000 FE/t` input, two input slots, an output slot, an energy slot, processing progress, and a JEI category; added the test recipe 1 diamond + 1 charcoal → 2 diamonds at `100,000 FE` and `3,000 ticks`; added creative-tab, capability, loot, texture, and four-language support.
- `0.0.7aw_261107_f`: split block entity classes from `aestastel.coraconic.block` into `aestastel.coraconic.blockentity` and updated block, menu, registry, and capability references; registry IDs, resources, and NBT remain unchanged.
- `0.0.7av_261107_e`: limited the README "Recent Changes" lists to the latest 10 entries.
- `0.0.7au_261107_d`: added the solar `Radiation` mode, available only on MK3; it triggers ahead of all other modes at `Y >= 315` in the Overworld or `Y >= 250` in other dimensions and generates `1,440 FE/t`; updated the Chinese and English documentation.
- `0.0.7at_261107_c`: renamed the coraconic and aviraconic metal block registry IDs, recipe IDs, resource paths, and language keys to `coraconic_metal_block` / `aviraconic_metal_block`; regenerated tags, blockstates, models, loot tables, recipes, advancements, and translations.
- `0.0.7as_261107_b`: split crafting recipe generation into standalone and formula-based providers; added and adjusted alloying recipes for netherite, coraconic, and aviraconic ingots; updated the coraconic ingot crafting recipe and generated resources.
- `0.0.7ar_261107_a`: split recipe data generation into crafting, alloying, and smelting providers; kept `ModRecipesProvider` as the single data-generation entry point to avoid duplicate `Recipes` provider names; generated recipe output is unchanged.
- `0.0.7aq_261106_b`: added the JEI recipe category and machine catalyst for the Alloying Machine, showing up to three counted inputs, a progress arrow, output, total energy, and processing time, with translations.
- `0.0.7ap_261106_a`: added Alloying Machine MK1 with a `320,000 FE` buffer, three input slots, an output slot, a discharge slot, and a processing progress bar; added the test alloy recipe 4 gold ingots + 4 netherite scraps → 1 netherite ingot at `20,000 FE` and `100 ticks`; added the alloying recipe type, serializer, GUI, data generation, translations, and assets.
- `0.0.6fs_261105_b`: added Geomagnetic Generators MK1–MK3 with the matching solar tier buffers and output rates, tiered `Overworld` / `Nether` / `End` generation, registration, capabilities, creative-tab entries, loot tables, translations, textures, and data generation; widened the I/O button by one more pixel on the right.

### Quick Start

1. Open the `Coraconic Tech` creative tab to find materials, crystals, ore blocks, both storage-unit families, Alloying Machine MK1, Resonance Crystallizing Machine MK1, Solar Generators MK0–MK3, and Geomagnetic Generators MK0–MK3.
2. The `Coraconic Tech Tools` tab contains complete five-piece tool sets for all four materials.
3. Item storage units preserve contents like shulker boxes; energy storage units provide a charge slot, a discharge slot, an energy display, and the player inventory.
4. Energy storage unit items can be placed as blocks or charged/discharged in another unit's slots; hover over the item to inspect its charge.

### Implemented Content

#### Materials and Basic Items

- Tin, lead, coraconic metal, and aviraconic metal
- Matching ingots, nuggets, and raw ore items
- Common-metal, coraconic, aviraconic, effect amplification, spatial scaling, phase synchronization, mass-energy compensation, and central computation cores
- The five special cores are currently creative-only and do not have crafting recipes yet
- Coraconic and aviraconic elements
- Coraconic and aviraconic diamond-structured crystals
- `hcv2`, a custom food item

#### Tools

- Five tools for each of the four materials, 20 tools in total:
    - Sword, pickaxe, axe, shovel, hoe
- Recipes, advancements, handheld models, and four-language names for every tool
- A dedicated tool creative tab
- Wrench: Shift + mouse wheel cycles rotate, wrench, and configure modes
    - Rotate mode: right-click rotates clockwise; Shift + right-click rotates counter-clockwise
    - Wrench mode: right-click interacts normally; Shift + right-click dismantles the seven functional machines
    - Configure mode: does not interact with functional machines normally; right-click prints the clicked face's energy mode to chat, and Shift + right-click cycles input → output → input/output → blocked → input
- Tool stats are still in an early balancing stage

#### Blocks and World

- Tin, lead, coraconic metal, and aviraconic metal blocks
- Tin ore and lead ore
- Deepslate tin ore and deepslate lead ore
- Ore blocks, drops, and tags are implemented; natural world generation is not

#### Item Storage Units

All three tiers use a fixed 9 rows; the column count grows with the tier:

| Tier |      Slots | GUI size | Notes               |
| ---- | ---------: | -------- | ------------------- |
| MK1  |   81 (9×9) | 176×276  | Basic item storage  |
| MK2  | 162 (9×18) | 338×276  | Medium item storage |
| MK3  | 243 (9×27) | 500×276  | Large item storage  |

- Hoppers, pipes, and other automation can access the inventory through `Capabilities.ItemHandler.BLOCK`
- Breaking, creative-breaking, or pick-blocking the unit stores its inventory in the `minecraft:container` component; it is restored when placed
- The item tooltip reports whether contents are saved and how many slots are occupied
- Inventory and hotbar slots show occupied-slot usage as a yellow durability-style bar
- A single drag is limited to 120 slots to avoid exceeding the 128-entry `container_click` packet limit
- All three tiers use hand-authored Blockbench block models and tier-specific textures; data generation skips these models

#### Energy Storage Units

| Tier |         Capacity | Transfer rate |
| ---- | ---------------: | ------------: |
| MK1  |    32,000,000 FE |    4,000,000 FE/t |
| MK2  |   320,000,000 FE |   16,000,000 FE/t |
| MK3  | 3,200,000,000 FE |   64,000,000 FE/t |

- Each unit has two machine slots:
    - Charge slot: transfers energy from the unit into an item
    - Discharge slot: transfers energy from an item into the unit
- The GUI shows current energy, capacity, a large energy display, and the player inventory
- Energy is abbreviated with `k FE`, `M FE`, or `G FE` above the corresponding thresholds, with two decimal places; hold Shift to show the full FE values
- Capacities above `Integer.MAX_VALUE`, such as MK3, are split into multiple int-sized banks. Different faces expose different banks, while the GUI and internal item transfer use the full long value
- Each face can independently be set to input, output, input/output, or blocked; all faces default to input/output
- The GUI has a shared face configuration button and popup outside its upper-left edge: left-click cycles forward, right-click cycles backward, the expanded panel does not cover the inventory or main UI, and the wrench configure mode edits the same settings
- Input faces actively pull from adjacent energy blocks, output faces actively push to them, and blocked faces reject all external input/output
- Energy is exposed to cables and machines through `Capabilities.EnergyStorage.BLOCK` with per-face mode gating

#### Alloying Machine MK1

- Internal buffer: `320,000 FE`, receiving energy from every side at up to `10,000 FE/t`
- Three input slots, one output slot, and one discharge slot; the output slot only allows extraction
- The discharge slot only accepts energy-extractable items and feeds up to `10,000 FE/t` into the buffer; it never charges the item
- A central processing progress bar is present
- Test recipe: 4 gold ingots + 4 netherite scraps → 1 netherite ingot
- The test recipe costs `20,000 FE` and takes `100 ticks`
- JEI provides a dedicated Alloying Machine category with counted inputs, a progress arrow, output, energy, and time information
- Breaking the block preserves stored energy, all five slot contents, and progress
- Currently obtained in creative mode only; the machine itself has no crafting recipe yet

#### Resonance Crystallizing Machine MK1

- Internal buffer: `640,000 FE`, receiving energy from every side at up to `1,000 FE/t`
- Two input slots, one output slot, and one energy slot; the output slot only allows extraction
- The energy slot only accepts energy-receiving items and charges them from the buffer at up to `1,000 FE/t`
- A central processing progress bar is present
- Test recipe: 1 diamond + 1 charcoal → 2 diamonds
- The test recipe costs `100,000 FE` and takes `3,000 ticks`
- JEI provides a dedicated Resonance Crystallizing Machine category with counted inputs, a progress arrow, output, energy, and time information
- Breaking the block preserves stored energy, all four slot contents, and progress
- Currently obtained in creative mode only; the machine itself has no crafting recipe yet

#### Solar Generator MK0

- Internal buffer: `96,000 FE`
- Generates `30 FE/t` during daytime, outside the Nether, while sky light is greater than 10 and no full opaque block blocks the entire sky column above
- Each face can independently be set to input, output, input/output, or blocked; all faces default to output
- The GUI has the same shared face configuration button and popup outside its upper-left edge: left-click cycles forward, right-click cycles backward, the expanded panel does not cover the inventory or main UI, and the wrench configure mode remains available
- Output faces actively push up to `600 FE/t` per face, while input faces can also actively pull from adjacent energy blocks; blocked faces reject all external input/output
- The GUI has a black/green CLI panel in the center, the output/charge slot on the right, and the vertical energy bar, energy text, and player inventory on the left
- The CLI shows `>Power Generating System`, `>Generation MK* - Version <mod_version>`, `>Power: <rule rate> FE/t`, and `>Mode: <mode>`
- Mode priority is `Radiation (MK3) > Nether > Reflection > End/Solar > Thunder > Solar/Lunar`; the End is always in `Solar` mode when unobstructed and remains `Reflection` when obstructed; MK0/MK1 use `Null` during daytime with insufficient sky light; generation is bound to the mode, and `Power` does not drop to zero merely because the buffer is full
- The output slot only accepts energy-receiving items; the buffer charges that item first, then exposes the remainder to external sides
- Breaking the block keeps buffered energy and face configuration in the block item; output-slot contents drop separately
- Energy is exposed to cables and machines through `Capabilities.EnergyStorage.BLOCK` with per-face mode gating

#### Geomagnetic Generators MK0–MK3

| Tier |      Capacity | Per-face output | Item charge | Overworld | Nether | End |
| ---- | ------------: | --------------: | ----------: | --------: | -----: | --: |
| MK0  |    96,000 FE |        600 FE/t |    600 FE/t |   2 FE/t | 3 FE/t | 6 FE/t |
| MK1  |   192,000 FE |      1,200 FE/t |  1,200 FE/t |   6 FE/t | 16 FE/t | 32 FE/t |
| MK2  |   480,000 FE |      2,400 FE/t |  2,400 FE/t |  32 FE/t | 56 FE/t | 112 FE/t |
| MK3  | 1,920,000 FE |      9,600 FE/t |  9,600 FE/t | 112 FE/t | 320 FE/t | 640 FE/t |

- Each tier reuses the matching solar tier's buffer, per-face output, item charging, output slot, face configuration, GUI, and drop persistence
- The Overworld requires Y &lt; -59
- The Nether, modded dimensions, and the End require Y &lt; 6
- The Overworld: requires bedrock directly below and generates `2 FE/t` in `Overworld` mode
- The Nether and modded dimensions: require bedrock directly below and generate `3 FE/t` in `Nether` mode
- The End: requires empty space directly below and generates `6 FE/t` in `End` mode
- Any other case uses `Null` mode and generates `0 FE/t`
- The GUI reuses the Solar Generator MK0 texture and CLI, with `>Geomagnetic Energy` on the last line
- Currently obtained in creative mode only; no crafting recipes yet

#### Solar Generators MK1–MK3

| Tier |      Capacity | Per-face output | Item charge |
| ---- | ------------: | --------------: | ----------: |
| MK1  |   192,000 FE |    1,200 FE/t | 1,200 FE/t |
| MK2  |   480,000 FE |    2,400 FE/t | 2,400 FE/t |
| MK3  | 1,920,000 FE |    9,600 FE/t | 9,600 FE/t |

- MK1–MK3 share MK0's output slot, face-mode configuration, GUI, and drop persistence
- MK1: outside the Nether, during daytime, with sky light above 10 and no full opaque obstruction above, generates `120 FE/t`; thunderstorms reduce sky light to 10, so generation stops
- MK2: outside the Nether with no full opaque obstruction above; thunder `120 FE/t`, daytime `240 FE/t`, night `120 FE/t`
- MK3: always `80 FE/t` in the Nether; outside the Nether with obstruction `80 FE/t`; without obstruction, thunder `360 FE/t`, daytime `960 FE/t`, night `240 FE/t`
- MK3: at `Y >= 315` in the Overworld or `Y >= 250` in the Nether, the End, and other modded dimensions, enters `Radiation` mode ahead of all other modes and generates `1,440 FE/t`; MK0–MK2 never use this mode
- Currently obtained in creative mode only; no crafting recipes yet

#### Energy Storage Unit Items

- The item tooltip shows current charge and capacity, for example `Energy: 1.00 G / 3.20 G FE`
- Inventory and hotbar slots show the current charge as a green durability-style bar
- The item registers `Capabilities.EnergyStorage.ITEM` and works as a portable FE container
- It can be placed in another unit's charge or discharge slot for two-way charging
- Breaking, creative-breaking, or pick-blocking the unit preserves its energy, internal slots, and six face modes
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

> Item storage units, energy storage units, and the solar generator are currently obtained primarily in creative mode; survival recipes have not been added yet.

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

| Module           | Status          | Notes                                                                             |
| ---------------- | --------------- | --------------------------------------------------------------------------------- |
| Metal materials  | Complete        | Four metals, ingots, nuggets, and raw materials                                   |
| Metal blocks     | Complete        | Four metal blocks with packing/unpacking recipes                                  |
| Ore blocks       | Partial         | Blocks, drops, and tags done; no world generation                                 |
| Tools            | Complete        | 20 tools with recipes, models, tags, and creative tab; stats pending balance      |
| Crystals         | Complete        | Items, textures, tags, and four-language names                                    |
| Item storage     | Complete        | Three tiers, GUI, component persistence, automation capability, and custom models |
| Energy storage   | Complete        | Three tiers, GUI, multi-bank storage, cable capability, and item charging         |
| Solar generator  | Complete        | MK0–MK3, tiered buffers/output, and dimension/weather/obstruction rules            |
| Recipes and tags | Mostly complete | Materials and tools done; storage recipes pending                                 |
| Localization     | Complete        | `en_us`, `zh_cn`, `zh_hk`, `zh_tw`                                                |
| Data generation  | Complete        | Models, tags, recipes, advancements, loot tables, and global modifiers            |
| World generation | Not started     | Natural tin and lead ore generation                                               |
| Processing chain | Partial         | Raw tin/lead and ores can be smelted/blasted; automated processing chain pending  |
| Armor            | Not started     |                                                                                   |
| JEI support      | Placeholder     | Empty plugin implementation                                                       |
| Code cleanup     | Complete        | MDK example registrars and demo configuration removed                             |

### Known Issues

- Tin and lead ores do not generate naturally yet; they are only obtainable in creative mode or through debugging.
- Item storage units, energy storage units, and the solar generator do not have survival crafting recipes yet.
- The JEI plugin is still empty and provides no extra recipe information.
- Tool attack damage, attack speed, tier, and durability values are still being balanced.
- Save compatibility is not guaranteed during early development.
- Storage units may be nested inside each other by design; there is no anti-nesting restriction.

### Roadmap

- [ ] Implement natural tin and lead ore generation
- [ ] Expand raw ore processing and automated smelting
- [ ] Add survival recipes for both storage-unit families and the solar generator
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
