package cyborgcabbage.amethystgravity.mixin;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.access.GravityData;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.QuaternionUtil;
import me.andrew.gravitychanger.util.RotationUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements GravityData {
    public List<Direction> gravityBlocks = new ArrayList<>();
    public boolean onPlate = false;

    @Override
    public List<Direction> getGravityData() {
        return gravityBlocks;
    }

    @Override
    public boolean isOnPlate() {
        return onPlate;
    }

    @Override
    public void setOnPlate(boolean b) {
        onPlate = b;
    }

    @Inject(method="tick()V", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci){
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        //Get direction
        List<Direction> directions = this.getGravityData();
        Direction newDir = Direction.DOWN;
        boolean wasOnPlate = isOnPlate();
        boolean onPlate = false;
        if(!directions.isEmpty() && !player.isSpectator() && !player.getAbilities().flying) {
            newDir = directions.get(0);
            onPlate = true;
        }
        Direction oldDir = GravityChangerAPI.getGravityDirection(player);
        //Set gravity
        if((!oldDir.equals(newDir) && !directions.contains(oldDir)) || (!onPlate && wasOnPlate)) {
            if(!onPlate){
                Vec3d pos = player.getPos();
                Vec3f gVec = oldDir.getUnitVector();
                double scale = 0.1;
                pos = pos.add(gVec.getX()*scale,gVec.getY()*scale, gVec.getZ()*scale);
                BlockPos blockPos = new BlockPos(pos.x,pos.y,pos.z);
                BlockState blockState = player.getWorld().getBlockState(blockPos);
                if(blockState.getBlock() instanceof PlatingBlock platingBlock){
                    Direction snapDirection = getSnapDirection(blockState,oldDir);
                    if(snapDirection != null){

                        GravityChangerAPI.setGravityDirection(player, snapDirection);
                        player.setPosition(player.getPos().add(gVec.getX()*0.1,gVec.getY()*0.1,gVec.getZ()*0.1));
                        Vec3f oldVec = oldDir.getUnitVector();
                        Vec3f newVec = snapDirection.getUnitVector();
                        //Get quaternion for gravity rotation
                        Vec3f rotAxis = oldVec.copy();
                        rotAxis.cross(newVec);
                        float rotAngle = (float)Math.acos(oldVec.dot(newVec));
                        Quaternion q = new Quaternion(rotAxis, rotAngle, false);
                        /*//Apply rotation to player velocity
                        Vec3d temp = player.getVelocity();
                        Vec3f vel = new Vec3f(temp);
                        vel.rotate(q);
                        player.setVelocity(new Vec3d(vel));*/
                        Vec3f temp0 = new Vec3f(RotationUtil.vecPlayerToWorld(player.getVelocity(), oldDir));
                        temp0.rotate(q);
                        //player.setVelocity(RotationUtil.vecWorldToPlayer(new Vec3d(temp0), snapDirection));
                        //player.velocityModified = true;
                        Direction relativeDirection = RotationUtil.dirWorldToPlayer(snapDirection, oldDir);
                        AmethystGravity.LOGGER.info(relativeDirection);
                        //player.setVelocity(RotationUtil.vecWorldToPlayer(player.getVelocity(),relativeDirection.getOpposite()));
                        player.setVelocity(rotateVelocity(player.getVelocity(),relativeDirection));
                        player.velocityModified = true;
                        //Rotate client player view
                        EnumSet<PlayerPositionLookS2CPacket.Flag> flags = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);
                        flags.add(PlayerPositionLookS2CPacket.Flag.X);
                        flags.add(PlayerPositionLookS2CPacket.Flag.Y);
                        flags.add(PlayerPositionLookS2CPacket.Flag.Z);
                        flags.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
                        flags.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
                        player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(0.0,0.0,0.0,0.0f,90.0f,flags,0,false));
                    }else{
                        GravityChangerAPI.setGravityDirection(player, newDir);
                    }
                }else {
                    GravityChangerAPI.setGravityDirection(player, newDir);
                }
            }else{
                GravityChangerAPI.setGravityDirection(player, newDir);
            }
        }
        //Clear direction pool
        directions.clear();
        setOnPlate(onPlate);
    }

    private Vec3d rotateVelocity(Vec3d velocity, Direction direction) {
        double x = velocity.x;
        double y = velocity.y;
        double z = velocity.z;
        switch (direction) {
            case DOWN -> {
                return velocity;
            }
            case UP -> {
                return new Vec3d(x,-y,z);
            }
            case NORTH -> {
                return new Vec3d(x,-z,y);
            }
            case SOUTH -> {
                return new Vec3d(x,z,-y);
            }
            case WEST -> {
                return new Vec3d(y,-x,z);
            }
            case EAST -> {
                return new Vec3d(-y,x,z);
            }
        }
        return velocity;
    }

    private Direction getSnapDirection(BlockState blockState, Direction oldDir) {
        if(blockState.get(PlatingBlock.DOWN)){
            if(arePerpendicular(Direction.DOWN,oldDir)){
                return Direction.DOWN;
            }
        }
        if(blockState.get(PlatingBlock.UP)){
            if(arePerpendicular(Direction.UP,oldDir)){
                return Direction.UP;
            }
        }
        if(blockState.get(PlatingBlock.NORTH)){
            if(arePerpendicular(Direction.NORTH,oldDir)){
                return Direction.NORTH;
            }
        }
        if(blockState.get(PlatingBlock.SOUTH)){
            if(arePerpendicular(Direction.SOUTH,oldDir)){
                return Direction.SOUTH;
            }
        }
        if(blockState.get(PlatingBlock.WEST)){
            if(arePerpendicular(Direction.WEST,oldDir)){
                return Direction.WEST;
            }
        }
        if(blockState.get(PlatingBlock.EAST)){
            if(arePerpendicular(Direction.EAST,oldDir)){
                return Direction.EAST;
            }
        }
        return null;
    }

    private static boolean arePerpendicular(Direction dir0, Direction dir1){
        if(dir0.equals(dir1)){
            return false;
        }
        if(dir0.equals(dir1.getOpposite())){
            return false;
        }
        return true;
    }


}
