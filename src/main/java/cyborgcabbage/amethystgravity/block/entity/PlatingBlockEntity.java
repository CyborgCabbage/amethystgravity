package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class PlatingBlockEntity extends BlockEntity{
    public PlatingBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.PLATING_BLOCK_ENTITY, pos, state);
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, PlatingBlockEntity blockEntity) {
        if(blockState.getBlock() instanceof PlatingBlock platingBlock) {
            for (Direction plateDirection : PlatingBlock.getDirections(blockState)) {
                Box box = platingBlock.getGravityEffectBox(blockPos, plateDirection);
                GravityEffect.applyGravityEffectToPlayers(platingBlock.getGravityEffect(plateDirection, blockPos), box, world, false, List.of(plateDirection), true);
            }
        }
    }
}
