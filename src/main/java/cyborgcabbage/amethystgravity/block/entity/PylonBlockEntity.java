package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.access.GravityData;
import cyborgcabbage.amethystgravity.block.PylonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class PylonBlockEntity extends BlockEntity{
    public PylonBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.PYLON_BLOCK_ENTITY, pos, state);
    }
    public static void serverTick(World world, BlockPos pos, BlockState state, PylonBlockEntity be) {
        double minX = pos.getX();
        double minY = pos.getY();
        double minZ = pos.getZ();
        double maxX = pos.getX()+1;
        double maxY = pos.getY()+1;
        double maxZ = pos.getZ()+1;
        int r = 3;
        int rm = 0;
        Direction dir = state.get(PylonBlock.FACING);
        if (dir != Direction.WEST) minX -= r; else minX -= rm;
        if (dir != Direction.EAST) maxX += r; else maxX += rm;

        if (dir != Direction.DOWN) minY -= r; else minY -= rm;
        if (dir != Direction.UP) maxY += r; else maxY += rm;

        if (dir != Direction.NORTH) minZ -= r; else minZ -= rm;
        if (dir != Direction.SOUTH) maxZ += r; else maxZ += rm;

        List<PlayerEntity> entities = world.getEntitiesByClass(PlayerEntity.class, new Box(minX, minY, minZ, maxX, maxY, maxZ), e -> true);
        for (PlayerEntity entity : entities) {
            List<Direction> gravityData = ((GravityData)entity).getGravityData();
            gravityData.add(dir);
        }

        /*if(entities.isEmpty()){
            AmethystGravity.LOGGER.info("No Players");
        }else{
            AmethystGravity.LOGGER.info(entities.get(0));
        }*/
    }
}
