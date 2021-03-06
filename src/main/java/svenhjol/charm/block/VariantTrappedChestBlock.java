package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.blockentity.VariantTrappedChestBlockEntity;
import svenhjol.charm.module.VariantChests;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class VariantTrappedChestBlock extends ChestBlock implements ICharmBlock, IVariantChestBlock {
    private final CharmModule module;
    private final IVariantMaterial type;
    private final List<String> loadedMods;

    public VariantTrappedChestBlock(CharmModule module, IVariantMaterial type, String... loadedMods) {
        this(module, type, Settings.copy(Blocks.TRAPPED_CHEST), () -> VariantChests.TRAPPED_BLOCK_ENTITY);
    }

    public VariantTrappedChestBlock(CharmModule module, IVariantMaterial type, AbstractBlock.Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, String... loadedMods) {
        super(settings, supplier);

        this.module = module;
        this.type = type;
        this.loadedMods = Arrays.asList(loadedMods);

        this.register(module, type.asString() + "_trapped_chest");
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.REDSTONE;
    }

    @Override
    public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
        if (enabled())
            super.addStacksForDisplay(group, items);
    }

    @Override
    public boolean enabled() {
        return module.enabled && loadedMods.stream().allMatch(ModHelper::isLoaded);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView worldIn) {
        return new VariantTrappedChestBlockEntity();
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return MathHelper.clamp(ChestBlockEntity.getPlayersLookingInChestCount(world, pos), 0, 15);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return direction == Direction.UP ? state.getWeakRedstonePower(world, pos, direction) : 0;
    }

    @Override
    public IVariantMaterial getMaterialType() {
        return type;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    /**
     * Copypasta from {@link net.minecraft.block.TrappedChestBlock}
     */
    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat(Stats.TRIGGER_TRAPPED_CHEST);
    }
}
