package cyborgcabbage.amethystgravity.block;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.CylinderFieldGeneratorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CylinderFieldGeneratorBlock extends AbstractFieldGeneratorBlock<CylinderFieldGeneratorBlockEntity>{
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

    public CylinderFieldGeneratorBlock(boolean _creative, Settings settings) {
        super(_creative, settings);
        setDefaultState(stateManager.getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    public BlockEntityType<CylinderFieldGeneratorBlockEntity> getBlockEntity() {
        return AmethystGravity.CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CylinderFieldGeneratorBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(AXIS, ctx.getSide().getAxis());
    }


    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return PillarBlock.changeRotation(state, rotation);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AXIS);
    }
}
