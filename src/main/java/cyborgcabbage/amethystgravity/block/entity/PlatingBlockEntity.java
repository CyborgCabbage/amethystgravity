package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.access.GravityData;
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
        super(AmethystGravity.PYLON_BLOCK_ENTITY, pos, state);
    }
    public static void serverTick(World world, BlockPos pos, BlockState state, PlatingBlockEntity be) {
        double minX = pos.getX();
        double minY = pos.getY();
        double minZ = pos.getZ();
        double maxX = pos.getX()+1;
        double maxY = pos.getY()+1;
        double maxZ = pos.getZ()+1;
        int r = 0;
        int rm = 0;
        Direction dir = Direction.EAST;//state.get(PylonBlock.FACING);
        /*if (dir != Direction.WEST) minX -= r; else minX -= rm;
        if (dir != Direction.EAST) maxX += r; else maxX += rm;

        if (dir != Direction.DOWN) minY -= r; else minY -= rm;
        if (dir != Direction.UP) maxY += r; else maxY += rm;

        if (dir != Direction.NORTH) minZ -= r; else minZ -= rm;
        if (dir != Direction.SOUTH) maxZ += r; else maxZ += rm;*/
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
            if (box.contains(stablePoint)) {
                List<Direction> gravityData = ((GravityData) entity).getGravityData();
                gravityData.add(dir);
            }
        }
    }
}
