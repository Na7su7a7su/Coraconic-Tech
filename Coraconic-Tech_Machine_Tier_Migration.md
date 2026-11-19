# Coraconic-Tech：机器 Tier 化迁移方案

**设计提案 · Minecraft 1.21.1 · NeoForge · Java 21**

## 一、迁移目标

将 Coraconic-Tech
现有机器体系从「每种机器自行定义等级、容量、效率和运行参数」逐步迁移到统一的
Machine Tier（机器等级）体系。

本次迁移的核心不是给机器增加一个 MK0～MK3
标签，而是建立一套统一的机器规格描述、等级约束与参数配置体系。

预期解决以下问题：

-   **等级定义分散**：不同机器分别定义能量容量、输入速率、处理速度等参数，缺少统一的等级语义。
-   **机器扩展成本高**：新增 MK2、MK3
    机器时，需要重复实现大量等级相关逻辑。
-   **参数缺乏一致性**：同等级机器的技术规格可能缺乏统一标准。
-   **机器行为与等级耦合**：机器等级、机器类型、生产配方和运行逻辑之间容易形成复杂依赖。
-   **后续升级困难**：无法方便地实现跨机器的等级比较、等级限制和通用升级机制。

迁移完成后，Tier 应当成为机器体系的基础设施，而不是某种机器的专属功能。

## 二、Tier 定义

采用固定的四级枚举体系：

  ----------------------------------------------------------------------------------------------------
  Tier              英文名称          中文名称          技术定位
  ----------------- ----------------- ----------------- ----------------------------------------------
  MK0               Primitive         初始级            基础技术形态，具备最基本的机器功能

  MK1               Refined           精制级            技术经过改进，具备成熟、稳定的生产能力

  MK2               Enhanced          强化级            在基础技术上进行强化，获得更高性能或扩展能力

  MK3               Superior          卓越级            当前技术体系下的高端型号
  ----------------------------------------------------------------------------------------------------

### MK0 · Primitive --- 初始级

基础技术形态，具备最基本的机器功能，是整个机器等级体系的起点。

### MK1 · Refined --- 精制级

技术经过改进，具备成熟、稳定的生产能力，强调可靠性与标准化。

### MK2 · Enhanced --- 强化级

在基础技术上进行强化，获得更高性能、更高效率或额外功能。

### MK3 · Superior --- 卓越级

当前技术体系下的高端型号，代表该类机器所能达到的卓越技术水平。

等级采用固定的四级枚举体系，具有以下约束：

-   Tier 是机器的固有属性，不是运行时可以随意改变的状态。
-   Tier 不直接决定机器的全部行为。
-   Tier 提供机器规格的基准，机器类型决定这些规格如何应用。
-   MK0 是有效等级，而不是未指定等级或空值。
-   MK3 不意味着所有机器都必须存在 MK3 型号。

这里需要特别区分三个概念：

  概念            职责
  --------------- --------------------------
  Machine Type    决定机器能做什么
  Machine Tier    决定机器处于什么技术等级
  Machine State   决定机器当前正在做什么

这三个概念不应互相替代。

## 三、机器规格体系设计

这是整个迁移中最重要的部分。

不建议将所有机器参数直接塞进 Tier
枚举中。应该区分**通用等级参数**与**机器专属参数**。

### 3.1 通用等级参数

通用参数由 Tier 提供，适用于具有相应功能的机器。

  参数                  说明
  --------------------- --------------------
  Energy Capacity       能量存储容量
  Energy Input          最大能量输入速率
  Energy Output         最大能量输出速率
  Processing Speed      基础处理速度倍率
  Processing Capacity   处理能力基准
  Tier Index            等级数值，用于比较

并不是每台机器都需要实现全部参数。

例如，纯粹的能源发生器可能没有 Processing
Speed，而某些不需要储存能量的机器也不需要 Energy Capacity。

### 3.2 机器专属参数

机器类型负责补充自身独有的参数。

以 Alloying Machine 为例：

  参数           MK1 Refined        MK2 Enhanced
  -------------- ------------------ ------------------
  独立生产线     1                  3
  单线处理进度   独立               独立
  能量系统       共用机器能量存储   共用机器能量存储
  配方处理       Alloying Recipe    Alloying Recipe

这里尤其要注意：MK2 Alloying
的三条生产线是机器类型与具体型号共同定义的功能，不能因为 Tier
升级就自动将所有机器变成三线生产。

### 3.3 Tier Profile 与 Machine Profile

建议从概念上划分为两层：

-   **Tier Profile**：提供 MK0～MK3 的通用技术规格。
-   **Machine Profile**：描述特定机器在特定 Tier 下的实际规格。

例如：

**Alloying Machine**

**MK1 · Refined** - Tier：MK1 - Processing Lines：1 - 独立进度管理：1 组

**MK2 · Enhanced** - Tier：MK2 - Processing Lines：3 - 独立进度管理：3
组 - Shared Energy Buffer

示意：Tier 提供通用等级，Machine Profile 决定机器型号的具体能力。

这种分层可以避免两个极端：

-   所有机器共用一套僵化参数。
-   每种机器重新实现一套完整的 Tier 系统。

## 四、现有机器的迁移映射

根据目前 Coraconic-Tech 已有的机器，建议采用以下映射。

  现有机器                     目标 Tier      迁移策略
  ---------------------------- -------------- ------------------
  Alloying MK1                 MK1 Refined    首批迁移
  Alloying MK2                 MK2 Enhanced   首批迁移
  Resonance Crystallizer MK1   MK1 Refined    首批迁移
  Smelting Metallurgy MK1      MK1 Refined    首批迁移
  Energy Storage MK1           MK1 Refined    第二批迁移
  Energy Storage MK2           MK2 Enhanced   第二批迁移
  Energy Storage MK3           MK3 Superior   第二批迁移
  Solar Generator              待定           依据实际设计定位
  Geothermal Generator         待定           依据实际设计定位
  Heat Generator               待定           依据实际设计定位

需要注意：

**不能因为机器存在 MK1、MK2、MK3 后缀，就直接认为所有机器的后缀都等于
Tier。**

应当先确定其实际技术定位，再完成映射。

例如，MK3 Energy Storage 可以明确属于 MK3
Superior，但某台没有后缀的发电机不一定就是 MK0 Primitive。

## 五、分阶段迁移路线

建议将迁移划分为五个阶段。每个阶段独立提交、独立验证，避免一次性重构过多机器。

### 阶段 A：Tier 基础设施建立

**优先级最高**

建立统一 Tier 定义与规格描述体系。

工作内容：

-   建立 MK0～MK3 的统一等级定义。
-   确定等级比较与等级索引规则。
-   建立通用 Tier Profile。
-   确定机器专属参数与通用参数的边界。
-   明确不存在某个等级型号时的处理方式。
-   确定机器注册信息如何关联 Tier。

验收标准：

-   任意机器能够明确声明自身 Tier。
-   任意两个 Tier 能够进行可靠比较。
-   不需要通过解析机器注册名称推断 Tier。

### 阶段 B：机器规格迁移

**优先级最高**

将现有机器的硬编码等级参数逐步迁移至统一规格体系。

建议顺序：

1.  Alloying MK1
2.  Alloying MK2
3.  Resonance Crystallizer MK1
4.  Smelting Metallurgy MK1

重点验证：

-   能量容量和输入速率。
-   能量消耗和处理速度。
-   多生产线之间的能量分配。
-   各机器的独有功能不受 Tier 抽象影响。

尤其需要保证 Alloying MK2 原有的三条独立生产线及共享能量机制不被破坏。

### 阶段 C：能源设备迁移

将 Energy Storage MK1～MK3 以及各类 Generator 纳入统一体系。

重点工作：

-   区分 Energy Capacity、Energy Input 和 Energy Output。
-   统一能量规格的获取方式。
-   验证高容量 FE 的边界行为。
-   验证能源设备与机器之间的 Capability 交互。
-   验证自动化输入、输出及能量传输。

这一阶段应特别避免把储能等级与发电等级混为一谈。

### 阶段 D：持久化与兼容性验证

**不可跳过**

Tier 迁移不能以机器正常运行作为唯一验收条件。

必须验证：

-   区块卸载与重新加载。
-   世界退出与重新进入。
-   机器破坏与重新放置。
-   机器物品状态保存与恢复。
-   能量、物品、配方和生产进度恢复。
-   多生产线独立进度恢复。
-   配方变化后的恢复行为。

Tier
本身属于机器的固有规格，通常不需要作为可变运行状态反复保存，但必须保证机器重建时能够从正确的注册定义中恢复其
Tier。

### 阶段 E：统一接口与后续扩展

在现有机器全部迁移并验证后，清理旧的等级参数获取方式。

最终目标：

-   新机器能够直接使用 Tier 体系。
-   新增 Tier 不需要修改每一种机器的内部逻辑。
-   可以进行统一的等级判断。
-   可以为未来的机器升级、自动化限制和等级兼容机制提供基础。
-   文档中的机器参数与实际规格保持一致。

## 六、存档兼容性与机器状态保护

这是本次迁移中最需要谨慎处理的部分。

Coraconic-Tech 目前已经存在机器能量、物品库存、配方
ID、生产进度等持久化机制。Tier 化不应该成为破坏这些机制的理由。

### 6.1 必须保持不变的内容

  数据                 迁移要求
  -------------------- --------------------------
  Energy               保留原有能量值
  Inventory            保留输入、输出及内部物品
  Recipe ID            保持配方引用有效
  Progress             保留生产进度
  Machine Mode         保留机器运行模式
  Redstone State       保留红石控制状态
  Side Configuration   保留六面输入输出配置
  Multi-line State     保留每条生产线的独立状态

### 6.2 Registry ID 不应随 Tier 重构而改变

例如，已有机器注册 ID 如果是：

`coraconic_tech:alloying_machine_mk1`

迁移后不应仅仅为了符合新的 Tier 命名体系，就将其改为：

`coraconic_tech:mk1_alloying_machine`

这会引入不必要的旧存档兼容性风险。

Tier 是机器的规格属性，不是必须反映在 Registry ID 中的命名规则。

### 6.3 不要混淆 Tier 与 Machine State

建议明确区分：

-   Tier：静态规格。
-   Energy：动态资源。
-   Inventory：动态资源。
-   RecipeProcessState：动态生产状态。
-   Configuration：机器配置。

Tier 不应该参与生产进度恢复，也不应该在每次 NBT
加载时重新初始化机器运行状态。

## 七、单元测试与验收方案

建议将测试分为四组。

  测试组              测试内容
  ------------------- --------------------------------
  Tier Tests          等级比较、规格获取、边界行为
  Machine Tests       机器与 Tier 的关联、参数正确性
  Persistence Tests   NBT 保存、加载、恢复
  Integration Tests   能量传输、自动化、配方与 GUI

### 重点测试案例

#### 1. Tier 基础测试

-   MK0、MK1、MK2、MK3 顺序正确。
-   不同等级比较结果正确。
-   不存在的机器等级不能被错误推断。
-   Tier Profile 不会被机器实例意外修改。

#### 2. Machine Profile 测试

-   Alloying MK1 只有一条生产线。
-   Alloying MK2 有三条独立生产线。
-   三条生产线共享能量资源。
-   每条生产线的生产进度互不干扰。
-   不同机器不会因为 Tier 相同而获得错误的专属功能。

#### 3. Persistence 测试

-   保存前后 Tier 一致。
-   保存前后能量一致。
-   保存前后库存一致。
-   保存前后生产进度一致。
-   配方 ID 无效时能够安全处理。
-   MK2 Alloying 三条生产线均能正确恢复。

#### 4. 回归测试

尤其要覆盖此前尚未充分验证的行为：

-   输出槽满时暂停生产但不清空进度。
-   机器卸载后重新加载不丢失进度。
-   机器破坏后重新放置不丢失应当保留的数据。
-   堆叠燃料剩余量正确。
-   流体容器交互不存在物品复制或丢失。

## 八、建议的 Git 提交组织

不建议将整个迁移塞进一个大型提交。

建议按照 Conventional Commits 风格组织：

  顺序   Commit 类型            内容
  ------ ---------------------- ---------------------------------
  1      `feat(tier)`           建立统一 Tier 定义
  2      `feat(machine)`        建立机器规格关联体系
  3      `refactor(alloying)`   迁移 Alloying MK1/MK2
  4      `refactor(machine)`    迁移 Crystallizer 与 Metallurgy
  5      `refactor(energy)`     迁移储能设备与发电设备
  6      `test(machine)`        增加 Tier 与机器规格测试
  7      `test(persistence)`    增加持久化及回归测试
  8      `docs(machine)`        更新机器规格及开发文档

每个提交都应尽可能保持可编译、可审查、可回滚。

## 九、最终架构预期

迁移完成后，机器体系应当形成以下关系：

``` text
Machine Tier System
├── MK0 · Primitive
├── MK1 · Refined
├── MK2 · Enhanced
└── MK3 · Superior

        │
        ▼

Tier Profile
└── 通用等级规格

Machine Profile
└── 机器专属规格

        │
        ▼

Machine Implementations
├── Alloying
├── Crystallizer
├── Metallurgy
├── Generator
└── Energy Storage

        │
        ▼

Machine Runtime
├── Machine State
│   └── 运行与持久化
└── Machine Interaction
    └── 能源、物品、配方
```

## 十、最终建议与实施优先级

我建议采用以下顺序：

1.  先建立 Tier 定义，不碰机器内部运行逻辑。
2.  设计 Tier Profile 与 Machine Profile 的职责边界。
3.  迁移 Alloying MK1/MK2，作为验证整个体系的试验对象。
4.  迁移其他加工机器。
5.  迁移能源设备。
6.  完成持久化和自动化回归测试。
7.  最后清理旧实现。

一个重要的设计原则：**Tier
决定机器的技术规格，但不决定机器的全部功能。**

例如，MK2 Alloying 的三条生产线是它的型号特性，而不是所有 MK2
机器的标准能力。MK2 储能设备的容量也不应该被强行套用到 MK2 Alloying 上。

这样设计后，未来即使 Coraconic-Tech 增加 MK3 Alloying、MK0 Generator
或者其他全新机器，也不需要重新设计整套机器等级系统。

这次重构真正应该交付的成果，是一套能够持续承载新机器、新等级和新机制的基础架构，而不只是一次参数搬迁。
