package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.access.GravityData;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;

public class PlatingBlockEntity extends BlockEntity{
    public PlatingBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.PLATING_BLOCK_ENTITY, pos, state);
    }
    public static void serverTick(World world, BlockPos pos, BlockState state, PlatingBlockEntity be) {
        if(state.get(PlatingBlock.DOWN)) attractForDirection(world,pos,state,be,Direction.DOWN);
        if(state.get(PlatingBlock.UP)) attractForDirection(world,pos,state,be,Direction.UP);
        if(state.get(PlatingBlock.NORTH)) attractForDirection(world,pos,state,be,Direction.NORTH);
        if(state.get(PlatingBlock.SOUTH)) attractForDirection(world,pos,state,be,Direction.SOUTH);
        if(state.get(PlatingBlock.EAST)) attractForDirection(world,pos,state,be,Direction.EAST);
        if(state.get(PlatingBlock.WEST)) attractForDirection(world,pos,state,be,Direction.WEST);

    }
    private static void attractForDirection(World world, BlockPos pos, BlockState state, PlatingBlockEntity be, Direction plateDirection){
        double minX = pos.getX();
        double minY = pos.getY();
        double minZ = pos.getZ();
        double maxX = pos.getX()+1;
        double maxY = pos.getY()+1;
        double maxZ = pos.getZ()+1;
        int r = 0;
        int rm = 0;
        Direction dir = plateDirection;
        /*if (dir != Direction.WEST) minX -= r; else minX -= rm;
        if (dir != Direction.EAST) maxX += r; else maxX += rm;

        if (dir != Direction.DOWN) minY -= r; else minY -= rm;
        if (dir != Direction.UP) maxY += r; else maxY += rm;

        if (dir != Direction.NORTH) minZ -= r; else minZ -= rm;
        if (dir != Direction.SOUTH) maxZ += r; else maxZ += rm;*/
        //Extend area of effect a bit so the player can jump without falling off
        switch(dir){
            case DOWN -> maxY+=0.6;
            case UP -> minY-=0.6;
            case NORTH -> maxZ+=0.6;
            case SOUTH -> minZ-=0.6;
            case WEST -> maxX+=0.6;
            case EAST -> minX-=0.6;
        }
        Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
        List<PlayerEntity> entities = world.getEntitiesByClass(PlayerEntity.class, box, e -> true);
        for (PlayerEntity entity : entities) {
            //Get the centre of rotation
            Vec3d stablePoint = RotationUtil.vecPlayerToWorld(0.0, 0.3, 0.0, GravityChangerAPI.getAppliedGravityDirection(entity));
            stablePoint = stablePoint.add(entity.getPos());
            //Create 0.6 x 0.6 x 0.6 box around centre of rotation
            Vec3d dim = new Vec3d(0.3,0.3,0.3);
            Box smallBox = new Box(stablePoint.subtract(dim),stablePoint.add(dim));
            //Check if the players rotation box is colliding with this gravity plates area of effect
            if (box.intersects(smallBox)) {
                List<Direction> gravityData = ((GravityData) entity).getGravityData();
                gravityData.add(dir);
            }
        }
    }
}
