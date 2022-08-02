package cyborgcabbage.amethystgravity.block;

import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public abstract class AbstractFieldGeneratorBlock<K extends AbstractFieldGeneratorBlockEntity> extends BlockWithEntity {
    public final boolean creative;

    protected AbstractFieldGeneratorBlock(boolean _creative, Settings settings) {
        super(settings);
        creative = _creative;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            Optional<K> blockEntity = world.getBlockEntity(pos, getBlockEntity());
            blockEntity.ifPresent(player::openHandledScreen);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient)
            return checkType(type, getBlockEntity(), K::clientTick);
        else
            return checkType(type, getBlockEntity(), K::serverTick);
    }

    public abstract BlockEntityType<K> getBlockEntity();
}
