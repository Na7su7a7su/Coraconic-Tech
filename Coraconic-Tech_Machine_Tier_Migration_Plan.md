# Coraconic-Tech：机器 Tier 化迁移实施计划

**状态：计划阶段，尚未实施**  
**参考设计：`Coraconic-Tech_Machine_Tier_Migration.md`**  
**当前基线：`mod_version = 0.0.7i`，标签 `0.0.7i|C_261119_b`**

---

## 一、总体原则

1. Tier 是机器的静态规格属性，不是运行时状态。
2. Tier 不写入 NBT；机器重建时从注册定义恢复。
3. 不因 Tier 迁移修改已有 Registry ID。
4. 不改变已有能量、库存、配方 ID、进度、模式、红石状态和面配置的持久化格式。
5. Tier 决定技术等级与通用基准，不决定机器的全部专属功能。
6. `Alloying MK2` 的三条生产线是型号特性，不是所有 MK2 机器的通用能力。
7. 每个阶段独立可编译、可运行、可回滚。

---

## 二、核心设计

### 2.1 三层职责

| 概念 | 职责 |
|---|---|
| Machine Type | 决定机器能做什么 |
| Machine Tier | 决定机器处于什么技术等级 |
| Machine State | 决定机器当前正在做什么 |

### 2.2 TierProfile 与 MachineProfile

`TierProfile` 不保存所有机器的绝对容量。  
原因是同为 MK1 的 Alloying 与 Energy Storage 具有完全不同的容量和传输规格。

因此采用：

- `TierProfile`：等级元数据、等级索引、通用倍率/基准
- `MachineProfile`：具体机器在该等级下的绝对规格
- 机器专属 record：补充机器家族独有参数

### 2.3 建议新增包

```text
aestastel.coraconic.common.machine
```

### 2.4 建议新增类型

```text
MachineTier
MachineType
TierProfile
MachineProfile
TieredMachine
```

### 2.5 MachineTier

| Tier | 英文名称 | 中文名称 |
|---|---|---|
| MK0 | Primitive | 初始级 |
| MK1 | Refined | 精制级 |
| MK2 | Enhanced | 强化级 |
| MK3 | Superior | 卓越级 |

提供：

- 等级索引
- 等级比较
- `isAtLeast`
- 中英文显示名
- 禁止从 Registry ID 推断 Tier

### 2.6 MachineType

建议枚举：

```text
ALLOYING
RESONANCE_CRYSTALLIZING
SMELTING_METALLURGY
INSTRUMENT_ASSEMBLING
ENERGY_STORAGE
SOLAR_GENERATOR
GEOMAGNETIC_GENERATOR
HEAT_GENERATOR
```

### 2.7 MachineProfile 通用接口

概念接口：

```java
MachineType type();
MachineTier tier();
long energyCapacity();
long maxEnergyInput();
long maxEnergyOutput();
double processingSpeed();
int processingCapacity();
```

机器专属参数由各家族 profile record 补充，例如：

- Alloying：生产线数量
- Smelting：每 tick 能耗
- Energy Storage：传输速率
- Solar / Geomagnetic：发电表
- Heat：热量容量与转化率

### 2.8 TieredMachine

机器方块可实现：

```java
MachineProfile machineProfile();
```

BlockEntity 从方块获取 profile，而不是自行重复定义等级参数。

---

## 三、机器 Tier 映射

### 3.1 已明确迁移项

| 机器 | 目标 Tier |
|---|---|
| Alloying MK1 | MK1 Refined |
| Alloying MK2 | MK2 Enhanced |
| Resonance Crystallizing Machine | MK1 Refined |
| Smelting Metallurgy Machine MK1 | MK1 Refined |
| Energy Storage MK1 | MK1 Refined |
| Energy Storage MK2 | MK2 Enhanced |
| Energy Storage MK3 | MK3 Superior |

### 3.2 当前建议映射

| 机器 | 当前建议 Tier | 备注 |
|---|---|---|
| Solar Generator MK0–MK3 | MK0–MK3 | 等待最终确认 |
| Geomagnetic Generator MK0–MK3 | MK0–MK3 | 等待最终确认 |
| Heat Generator MK0 | MK0 Primitive | 等待最终确认 |
| Instrument Assembling Machine | MK1 Refined | 设计文档未列出，建议默认 MK1 |

---

## 四、迁移阶段

## 阶段 A：Tier 基础设施

### 目标

建立统一 Tier 定义与规格描述体系，不改变机器运行逻辑。

### 工作内容

- 新增 `common.machine` 包
- 新增 `MachineTier`
- 新增 `MachineType`
- 新增 `TierProfile`
- 新增 `MachineProfile`
- 新增 `TieredMachine`
- 定义等级比较、索引和翻译键规则
- 确定机器专属参数与通用参数边界
- 确定机器注册信息关联 Tier 的方式

### 验收标准

- 任意机器可以明确声明自身 Tier
- 任意两个 Tier 可以可靠比较
- 不需要解析 Registry ID 推断 Tier
- 阶段 A 不修改任何机器行为
- 不修改 Registry ID

### 建议提交

```text
feat(machine): add tier infrastructure
```

---

## 阶段 B：加工机器迁移

### 迁移顺序

1. Alloying MK1
2. Alloying MK2
3. Resonance Crystallizing Machine
4. Smelting Metallurgy Machine MK1

### 迁移方式

- 方块持有 `MachineProfile`
- BlockEntity 从方块获取 profile
- 容量、输入、输出、速度、生产线数量从 profile 读取
- Menu、GUI、JEI 不再直接依赖硬编码常量
- 原公开常量暂时保留为兼容别名
- 机器专属参数放入对应 profile record

### 必须保持

- Alloying MK2 三条独立生产线
- 三条线共享能量
- 各线独立进度
- 输出槽阻塞时暂停且不清空进度
- 原配方 ID、槽位索引和 NBT 键不变

### 当前需要保留的规格

| 机器 | 能量缓存 | 最大输入 | 其他 |
|---|---:|---:|---|
| Alloying MK1 | 320,000 FE | 10,000 FE/t | 1 条线 |
| Alloying MK2 | 1,040,000 FE | 40,000 FE/t | 3 条线，速度 1.2x |
| Resonance Crystallizing | 640,000 FE | 1,000 FE/t | 1 条线 |
| Smelting Metallurgy MK1 | 320,000 FE | 10,000 FE/t | 2 FE/t |

### 建议提交

```text
refactor(machine): migrate processing machines to tier profiles
```

---

## 阶段 C：能源设备迁移

### 迁移范围

- Energy Storage MK1–MK3
- Solar Generator MK0–MK3
- Geomagnetic Generator MK0–MK3
- Heat Generator MK0

### 当前需要保留的规格

| 设备 | 缓存 | 每面输入/输出 |
|---|---:|---:|
| Energy Storage MK1 | 32,000,000 FE | 4,000,000 FE/t |
| Energy Storage MK2 | 320,000,000 FE | 16,000,000 FE/t |
| Energy Storage MK3 | 3,200,000,000 FE | 64,000,000 FE/t |
| Solar MK0 | 96,000 FE | 600 FE/t |
| Solar MK1 | 192,000 FE | 1,200 FE/t |
| Solar MK2 | 480,000 FE | 2,400 FE/t |
| Solar MK3 | 1,920,000 FE | 9,600 FE/t |
| Heat Generator MK0 | 96,000 FE | 600 FE/t |

### 必须保持

- 能量容量、输入、输出相互独立
- 高容量 FE 不溢出
- Capability 面配置
- 物品充放电行为
- 太阳能与地磁发电规则
- 热力发电机热量、燃料与流体容器逻辑

### 建议提交

```text
refactor(machine): migrate energy machines to tier profiles
```

---

## 阶段 D：持久化与回归验证

### 验证项

- 区块卸载与重新加载
- 世界退出与重新进入
- 机器破坏与重新放置
- 方块物品状态保存与恢复
- 能量、物品、配方 ID、进度恢复
- Alloying MK2 三条线独立恢复
- 配方变化后的安全恢复
- 输出槽满时暂停但不清空进度
- 堆叠燃料剩余量
- 流体容器交互无复制或丢失
- 自动化输入输出和能量传输

### Tier 持久化规则

- Tier 不写入 NBT
- 机器重建时从注册 profile 恢复
- 不因 NBT 加载重新初始化运行状态

### 建议提交

```text
test(machine): add tier and persistence coverage
```

---

## 阶段 E：清理与文档

### 工作内容

- 移除已迁移机器的重复等级常量
- 保留必要的兼容别名
- 更新 README 机器规格
- 更新 `VALUE_LIST.md`
- 更新机器开发文档
- 确保新机器可以直接使用 Tier 体系

### 建议提交

```text
docs(machine): update machine tier documentation
```

---

## 五、测试建议

### Tier Tests

- MK0–MK3 顺序与比较
- 非法等级处理
- Tier Profile 不可变
- 等级索引与翻译键

### Machine Tests

- Alloying MK1 只有一条生产线
- Alloying MK2 有三条独立生产线
- 三条线共享能量
- 各线进度互不干扰
- 不同机器不会因 Tier 相同获得错误专属功能

### Persistence Tests

- Tier 重建一致
- 能量、库存、进度一致
- 无效配方 ID 安全处理
- MK2 三线状态恢复

### Regression Tests

- 输出槽满暂停
- 区块重载后进度保留
- 机器破坏重放数据保留
- 燃料与流体容器交互
- 自动化与 Capability 交互

当前没有 `src/test` 测试源码目录。阶段 D 需要决定：

- 使用 NeoForge GameTest
- 或增加 JUnit 测试依赖与纯 Java 测试
- 或两者组合

---

## 六、提交与版本策略

按独立阶段提交，每个提交都必须通过：

```text
compileJava
runData
build
```

不一次性提交整个迁移。  
每次提交前不修改版本号，除非收到明确版本/标签指令。

---

## 七、待确认事项

1. `TierProfile` 是否采用“等级元数据 + 通用倍率”，绝对规格放入 `MachineProfile`？
2. Solar Generator 是否按现有 MK0–MK3 映射到 Tier MK0–MK3？
3. Geomagnetic Generator 是否按现有 MK0–MK3 映射到 Tier MK0–MK3？
4. Heat Generator MK0 是否确定为 MK0 Primitive？
5. Instrument Assembling Machine 是否确定为 MK1 Refined？
6. 阶段 D 使用 GameTest、JUnit，还是两者组合？

---

## 八、执行进度

- [ ] 阶段 A：Tier 基础设施
- [ ] 阶段 B：加工机器迁移
- [ ] 阶段 C：能源设备迁移
- [ ] 阶段 D：持久化与回归验证
- [ ] 阶段 E：清理与文档
