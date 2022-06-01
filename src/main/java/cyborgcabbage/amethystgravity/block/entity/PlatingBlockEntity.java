package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.access.GravityData;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class PlatingBlockEntity extends BlockEntity{
    public PlatingBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.PLATING_BLOCK_ENTITY, pos, state);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, PlatingBlockEntity blockEntity) {
        for(Direction direction : PlatingBlock.getDirections(blockState)){
            attractForDirection(world,blockPos,direction);
        }
    }

    private static void attractForDirection(World world, BlockPos blockPos, Direction plateDirection){
        //Large Box
        {
            Box box = PlatingBlock.getLargeGravityEffectBox(blockPos, plateDirection);
            List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, box, e -> true);
            for (PlayerEntity player : playerEntities) {
                //Get player collider for gravity effects
                Box gravityEffectCollider = GravityEffect.getGravityEffectCollider(player);
                //Check if the player's rotation box is colliding with this gravity plates area of effect
                if (box.intersects(gravityEffectCollider)) {
                    List<GravityEffect> gravityData = ((GravityData) player).getGravityData();
                    gravityData.add(PlatingBlock.getLargeGravityEffect(plateDirection, blockPos));
                }
            }
        }
        //Small Box
        {
            Box box = PlatingBlock.getSmallGravityEffectBox(blockPos, plateDirection);
            List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, box, e -> true);
            for (PlayerEntity player : playerEntities) {
                //Get player collider for gravity effects
                Box gravityEffectCollider = GravityEffect.getGravityEffectCollider(player);
                //Check if the player's rotation box is colliding with this gravity plates area of effect
                if (box.intersects(gravityEffectCollider)) {
                    List<GravityEffect> gravityData = ((GravityData) player).getGravityData2();
                    gravityData.add(PlatingBlock.getLargeGravityEffect(plateDirection, blockPos));
                }
            }
        }
    }
}
