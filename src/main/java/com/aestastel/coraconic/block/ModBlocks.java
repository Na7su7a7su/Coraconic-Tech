package com.aestastel.coraconic.block;

import com.aestastel.coraconic.CoraconicTech;
import com.aestastel.coraconic.item.ModItems;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块注册器：集中声明本模组的金属块与矿石方块。
 *
 * <p>本类除了注册方块本体，还通过 {@link #registerBlocks} 自动为每个方块
 * 创建同名的 {@link BlockItem}，使方块能够作为物品被玩家拾取、存放和放入
 * 创造模式物品栏。方块状态、模型、战利品表等资源仍由数据生成器统一维护，
 * 不在此处生成。
 *
 * <p>Block registry: declares the metal blocks and ore blocks of this mod.
 * Each block also receives a matching {@link BlockItem} via
 * {@link #registerBlocks}; block states, models, and loot tables are still
 * produced by the data generators.
 */
public class ModBlocks {

  /**
   * 方块延迟注册器。
   *
   * <p>{@link DeferredRegister.Blocks} 是 NeoForge 为方块注册提供的专用包装。
   * 它与物品注册器一样采用延迟注册：声明条目时不会立即触碰游戏注册表，
   * 只有在 {@link #register(IEventBus)} 把注册器挂到模组事件总线后，方块
   * 才会在注册事件阶段真正写入。
   *
   * <p>{@code DeferredRegister.createBlocks(CoraconicTech.MODID)} 将所有方块的
   * 命名空间固定为 {@code coraconic}，因此后续注册只需提供路径，最终 ID 为
   * {@code coraconic:<name>}。
   *
   * <p>Deferred block register. Entries are recorded immediately but written to
   * the game registry only after the register is attached to the mod event bus
   * through {@link #register(IEventBus)}. The namespace is fixed to
   * {@code coraconic}.
   */
  public static final DeferredRegister.Blocks BLOCK =
    DeferredRegister.createBlocks(CoraconicTech.MODID);

  public static final DeferredBlock<Block> TIN_BLOCK = registerBlocks(
    "tin_block",
    () ->
      new Block(
        BlockBehaviour.Properties.of()
          .strength(1.3F, 6.0F)
          .requiresCorrectToolForDrops()
      )
  );
  public static final DeferredBlock<Block> LEAD_BLOCK = registerBlocks(
    "lead_block",
    () ->
      new Block(
        BlockBehaviour.Properties.of()
          .strength(1.5F, 6.0F)
          .requiresCorrectToolForDrops()
      )
  );
  public static final DeferredBlock<Block> CORACONIC_BLOCK = registerBlocks(
    "coraconic_block",
    () ->
      new Block(
        BlockBehaviour.Properties.of()
          .strength(1.5F, 6.0F)
          .requiresCorrectToolForDrops()
      )
  );
  public static final DeferredBlock<Block> AVIRACONIC_BLOCK = registerBlocks(
    "aviraconic_block",
    () ->
      new Block(
        BlockBehaviour.Properties.of()
          .strength(1.5F, 6.0F)
          .requiresCorrectToolForDrops()
      )
  );
  public static final DeferredBlock<Block> TIN_ORE = registerBlocks(
    "tin_ore",
    () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE))
  );
  public static final DeferredBlock<Block> DEEPSLAT_TIN_ORE = registerBlocks(
    "deepslate_tin_ore",
    () ->
      new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE))
  );
  public static final DeferredBlock<Block> DEEPSLAT_LEAD_ORE = registerBlocks(
    "deepslate_lead_ore",
    () ->
      new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE))
  );
  public static final DeferredBlock<Block> LEAD_ORE = registerBlocks(
    "lead_ore",
    () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE))
  );

  /**
   * 将方块延迟注册器挂到模组事件总线。
   *
   * <p>必须在模组构造期间调用。{@link #BLOCK} 在声明阶段只记录待注册方块，
   * 调用 {@link DeferredRegister#register(IEventBus)} 之后才会在方块注册事件
   * 阶段把每个条目写入游戏注册表；遗漏此调用会导致所有方块及其 BlockItem
   * 都不存在。
   *
   * <p>Attaches the deferred block register to the mod event bus. Must be called
   * during mod construction; otherwise no block entry will be registered.
   *
   * @param eventBus 模组专用事件总线，通常由主模组类传入
   */
  public static void register(IEventBus eventBus) {
    BLOCK.register(eventBus);
  }

  /**
   * 为指定方块创建并注册同名的 {@link BlockItem}。
   *
   * <p>Minecraft 中方块本体与“方块物品”是两个独立概念：方块负责世界中的
   * 放置状态、碰撞和渲染，{@link BlockItem} 负责背包中的物品形态、右键放置
   * 行为以及创造模式物品栏展示。如果只注册方块而不注册 BlockItem，方块虽然
   * 能通过命令或世界生成出现，却无法被正常拾取、合成或放入物品栏。
   *
   * <p>本方法刻意复用方块自身的注册名作为物品注册名，使方块 ID 与物品 ID
   * 完全一致（例如 {@code coraconic:tin_block}）。这样做有三个好处：
   * <ul>
   *   <li>数据生成器可以用同一个名称同时定位方块模型与物品模型；</li>
   *   <li>配方、标签和战利品表无需维护两套命名映射；</li>
   *   <li>玩家与调试命令看到的方块 ID、物品 ID 保持一致，降低理解成本。</li>
   * </ul>
   *
   * <p>注意：本方法只负责注册物品形态，不会把方块加入创造模式物品栏。
   * 创造栏内容由 {@code ModCreativeModeTabs} 统一控制，避免注册逻辑与展示
   * 逻辑相互耦合。
   *
   * <p>Creates and registers the matching {@link BlockItem} for the given block.
   * The item intentionally reuses the block's registration name so both IDs stay
   * identical, which keeps models, recipes, tags, and loot tables predictable.
   * This method does not add the item to any creative tab.
   *
   * @param name 方块与物品共用的注册路径，不包含命名空间
   * @param block 已经注册的方块延迟持有者；物品通过它延迟取得方块实例
   * @param <T> 具体的方块类型，允许为未来的自定义方块保留类型信息
   */
  public static <T extends Block> void registerBlockItems(
    String name,
    DeferredBlock<T> block
  ) {
    ModItems.ITEMS.register(name, () ->
      new BlockItem(block.get(), new Item.Properties())
    );
  }

  /**
   * 注册一个方块，并同步注册它的 BlockItem，最后返回方块的延迟持有者。
   *
   * <p>这是本类所有方块条目的统一入口。调用方只需提供注册名和一个
   * {@link Supplier}，无需重复编写“注册方块”和“注册方块物品”两步操作，
   * 从而避免漏注册 BlockItem 之类的低级错误。
   *
   * <p>参数 {@code block} 使用 {@link Supplier} 而不是直接传入 {@link Block}
   * 实例，是为了配合延迟注册：方块对象要等到注册阶段才由工厂创建，确保
   * 创建时游戏注册表和资源系统已经处于可用状态。{@link #registerBlockItems}
   * 也会延迟调用 {@code block.get()}，因此不会在声明阶段提前实例化方块。
   *
   * <p>执行顺序很重要：先调用 {@link DeferredRegister#register} 取得
   * {@link DeferredBlock}，再把该持有者交给 {@link #registerBlockItems}。
   * 这样 BlockItem 拿到的是同一个延迟持有者，双方最终指向同一个方块实例。
   *
   * <p>Registers a block, registers its matching BlockItem, and returns the
   * deferred block holder. The supplier keeps block construction deferred until
   * the registration phase, and the BlockItem is created from the same deferred
   * holder to guarantee both entries refer to the same block instance.
   *
   * @param name 方块与 BlockItem 共用的注册路径，不包含命名空间
   * @param block 方块工厂；仅在注册阶段被调用一次
   * @param <T> 具体的方块类型
   * @return 可用于后续引用、数据生成或物品栏注册的方块延迟持有者
   */
  private static <T extends Block> DeferredBlock<T> registerBlocks(
    String name,
    Supplier<T> block
  ) {
    DeferredBlock<T> blocks = BLOCK.register(name, block);
    registerBlockItems(name, blocks);
    return blocks;
  }
}
