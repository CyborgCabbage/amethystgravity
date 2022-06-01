package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.FieldGeneratorBlock;
import cyborgcabbage.amethystgravity.gravity.GravityData;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class FieldGeneratorBlockEntity extends BlockEntity {
    private static final double FIELD_HEIGHT_LARGE = 1.3;
    private static final double FIELD_HEIGHT_SMALL = 0.25;
    private static final double FIELD_WIDTH = 5.0;


    public FieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.FIELD_GENERATOR_BLOCK_ENTITY, pos, state);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, FieldGeneratorBlockEntity blockEntity) {
        blockEntity.serverTick((ServerWorld)world, blockPos, blockState);
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, FieldGeneratorBlockEntity blockEntity) {
        blockEntity.clientTick((ClientWorld)world, blockPos, blockState);
    }

    private void serverTick(ServerWorld world, BlockPos blockPos, BlockState blockState){
        Direction direction = blockState.get(FieldGeneratorBlock.FACING).getOpposite();
        //Large Box
        {
            Box box = getGravityEffectBox(blockPos, direction, FIELD_WIDTH, FIELD_HEIGHT_LARGE);
            List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, box, e -> true);
            for (PlayerEntity player : playerEntities) {
                //Get player collider for gravity effects
                Box gravityEffectCollider = GravityEffect.getGravityEffectCollider(player);
                //Check if the player's rotation box is colliding with this gravity plates area of effect
                if (box.intersects(gravityEffectCollider)) {
                    List<GravityEffect> gravityData = ((GravityData) player).getGravityData();
                    gravityData.add(getLargeGravityEffect(direction, blockPos));
                }
            }
        }
        //Small Box
        {
            Box box = getGravityEffectBox(blockPos, direction, FIELD_WIDTH, FIELD_HEIGHT_SMALL);
            List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, box, e -> true);
            for (PlayerEntity player : playerEntities) {
                //Get player collider for gravity effects
                Box gravityEffectCollider = GravityEffect.getGravityEffectCollider(player);
                //Check if the player's rotation box is colliding with this gravity plates area of effect
                if (box.intersects(gravityEffectCollider)) {
                    List<GravityEffect> gravityData = ((GravityData) player).getGravityData2();
                    gravityData.add(getLargeGravityEffect(direction, blockPos));
                }
            }
        }
    }

    private void clientTick(ClientWorld world, BlockPos blockPos, BlockState blockState){
        Direction direction = blockState.get(FieldGeneratorBlock.FACING).getOpposite();
        Vec3d pVel = new Vec3d(direction.getUnitVector()).multiply(0.01);
        Box box = getGravityEffectBox(blockPos, direction, FIELD_WIDTH, FIELD_HEIGHT_LARGE);
        Vec3d boxOrigin = new Vec3d(box.minX,box.minY,box.minZ);
        Vec3d boxSize = new Vec3d(box.getXLength(),box.getYLength(),box.getZLength());
        Random r = world.getRandom();
        double amount = getVolume()/5.0;
        while(amount > r.nextDouble()){
            Vec3d randomVec = new Vec3d(r.nextDouble(), r.nextDouble(), r.nextDouble());
            Vec3d pPos = boxOrigin.add(boxSize.multiply(randomVec));
            DefaultParticleType particleType = AmethystGravity.GRAVITY_INDICATOR;
            world.addParticle(particleType,pPos.x,pPos.y,pPos.z,0,0,0);//pVel.x,pVel.y,pVel.z);
            amount--;
        }
    }

    private Box getGravityEffectBox(BlockPos blockPos, Direction direction, double width, double height){
        Vec3d blockCentre = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).add(0.5,0.5,0.5);
        Vec3d faceCentre = blockCentre.add(new Vec3d(direction.getUnitVector()).multiply(-0.5));
        switch(direction){
            case DOWN -> {
                return new Box(faceCentre.add(-0.5*width,0,-0.5*width),faceCentre.add(0.5*width,height,0.5*width));
            }
            case UP -> {
                return new Box(faceCentre.add(-0.5*width,0,-0.5*width),faceCentre.add(0.5*width,-height,0.5*width));
            }
            case NORTH -> {
                return new Box(faceCentre.add(-0.5*width,-0.5*width,0),faceCentre.add(0.5*width,0.5*width,height));
            }
            case SOUTH -> {
                return new Box(faceCentre.add(-0.5*width,-0.5*width,0),faceCentre.add(0.5*width,0.5*width,-height));
            }
            case WEST -> {
                return new Box(faceCentre.add(0,-0.5*width,-0.5*width),faceCentre.add(height,0.5*width,0.5*width));
            }
            default -> {
                return new Box(faceCentre.add(0,-0.5*width,-0.5*width),faceCentre.add(-height,0.5*width,0.5*width));
            }
        }
    }

    private GravityEffect getLargeGravityEffect(Direction direction, BlockPos blockPos){
        return new GravityEffect(direction, GravityEffect.Type.FIELD, getVolume(), blockPos);
    }

    private double getVolume(){
        return FIELD_HEIGHT_LARGE*FIELD_WIDTH*FIELD_WIDTH;
    }
}
