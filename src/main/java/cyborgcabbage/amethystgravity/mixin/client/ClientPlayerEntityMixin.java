package cyborgcabbage.amethystgravity.mixin.client;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.gravity.GravityData;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements GravityData {
    public ArrayList<GravityEffect> gravityEffectList = new ArrayList<>();
    public ArrayList<GravityEffect> lowerGravityEffectList = new ArrayList<>();
    public GravityEffect gravityEffect = null;

    @Override
    public ArrayList<GravityEffect> getFieldList() {
        return gravityEffectList;
    }

    @Override
    public ArrayList<GravityEffect> getLowerFieldList() {
        return lowerGravityEffectList;
    }

    @Override
    public void setFieldGravity(GravityEffect _gravityEffect) {
        gravityEffect = _gravityEffect;
    }

    @Override
    public GravityEffect getFieldGravity() {
        return gravityEffect;
    }

    @ModifyArgs(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    private void moveInject(Args args){
        MovementType movementType = args.get(0);
        Vec3d movement = args.get(1);
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if(movementType == MovementType.SELF && movement == player.getVelocity()) {
            //Init vars
            final GravityEffect currentGravity = getFieldGravity();
            GravityEffect newGravity = null;
            List<GravityEffect> directions = getFieldList();
            //If the player is flying or in spectator
            if (!player.isSpectator() && !player.getAbilities().flying) {
                //Find the elements of directions which have the lowest volume
                final double lowestVolume = directions.stream().map(GravityEffect::volume).min(Double::compare).orElse(0.0);
                List<GravityEffect> highestPriority = directions.stream().filter(g -> g.volume() == lowestVolume).toList();
                if(highestPriority.size() > 0) {
                    newGravity = highestPriority.get(0);
                }
                //Get colliding directions
                List<Direction> localCollidingDirections = new ArrayList<>();
                Box box = player.getBoundingBox();
                List<VoxelShape> entityCollisions = player.world.getEntityCollisions(player, box.stretch(movement));
                Vec3d adjustedMovement = (movement.lengthSquared() == 0.0) ? movement : Entity.adjustMovementForCollisions(player, movement, box, player.world, entityCollisions);
                if (movement.x > adjustedMovement.x) localCollidingDirections.add(Direction.EAST);
                if (movement.x < adjustedMovement.x) localCollidingDirections.add(Direction.WEST);
                if (movement.y > adjustedMovement.y) localCollidingDirections.add(Direction.UP);
                if (movement.y < adjustedMovement.y) localCollidingDirections.add(Direction.DOWN);
                if (movement.z > adjustedMovement.z) localCollidingDirections.add(Direction.SOUTH);
                if (movement.z < adjustedMovement.z) localCollidingDirections.add(Direction.NORTH);
                if(currentGravity != null && highestPriority.size() > 0) {
                    //Find an element with equal direction to currentGravity
                    newGravity = highestPriority.stream().filter(ge -> ge.direction() == currentGravity.direction()).findFirst().orElse(newGravity);
                }
                if(currentGravity != null && localCollidingDirections.contains(Direction.DOWN)) {
                    //Inside corner snap (if the player is on the ground)
                    newGravity = getInsideCornerSnapDirection(currentGravity, highestPriority, localCollidingDirections).orElse(newGravity);
                }
                if(currentGravity != null && !localCollidingDirections.contains(Direction.DOWN)) {
                    //Outside corner snap (if the player just left the ground)
                    newGravity = getOutsideCornerSnapDirection(currentGravity, newGravity, movement).orElse(newGravity);
                }
            }
            Direction currentGravityDirection = GravityChangerAPI.getGravityDirection(player, AmethystGravity.FIELD_GRAVITY_SOURCE);
            //Set gravity
            if(newGravity == null){
                GravityChangerAPI.setGravityDirectionAdvanced(player, AmethystGravity.FIELD_GRAVITY_SOURCE, null, PacketByteBufs.create(), true, true);
            }else{
                //TODO: if velocity is large, it shouldn't rotate
                boolean rotatePerspective = arePerpendicular(currentGravityDirection, newGravity.direction());
                GravityChangerAPI.setGravityDirectionAdvanced(player, AmethystGravity.FIELD_GRAVITY_SOURCE, newGravity.direction(), PacketByteBufs.create(), rotatePerspective, rotatePerspective);
            }
            setFieldGravity(newGravity);

            //Clear direction pool
            getFieldList().clear();
            getLowerFieldList().clear();
            args.set(1, player.getVelocity());
        }
    }
    
    private Optional<GravityEffect> getInsideCornerSnapDirection(GravityEffect currentGravity, List<GravityEffect> effects, List<Direction> localCollidingDirections) {
        for(Direction localDirection : localCollidingDirections){
            if(localDirection != Direction.UP && localDirection != Direction.DOWN) {
                //collidingDirections will be relative to the player's gravity, we need to convert to be relative to the world
                Direction globalDirection = RotationUtil.dirPlayerToWorld(localDirection, currentGravity.direction());
                Optional<GravityEffect> effect = effects.stream().filter(ge -> ge.direction() == globalDirection).findFirst();
                if (effect.isPresent()) {
                    return effect;
                }
            }
        }
        return Optional.empty();
    }

    private Optional<GravityEffect> getOutsideCornerSnapDirection(GravityEffect currentGravity, @Nullable GravityEffect newGravity, Vec3d movement) {
        //If the new gravity effect is more than 4 times larger than the current one
        if(newGravity == null || currentGravity.volume() < newGravity.volume() / 4.0) {
            ArrayList<GravityEffect> effectsBelowPlayer = getLowerFieldList();
            Optional<GravityEffect> min = effectsBelowPlayer.stream().min(Comparator.comparingDouble(GravityEffect::volume));
            if(min.isPresent()){
                double minVolume = min.get().volume();
                effectsBelowPlayer.removeIf(ge -> ge.volume() > minVolume);
                //Get horizontal directions and sort by close-ness to velocity
                Vec3d velocity = RotationUtil.vecPlayerToWorld(movement, currentGravity.direction());
                List<Direction> hDir = getHorizontalDirections();
                hDir.sort((d1, d2) -> {
                    double dot1 = velocity.dotProduct(new Vec3d(d1.getUnitVector()));
                    double dot2 = velocity.dotProduct(new Vec3d(d2.getUnitVector()));
                    return Double.compare(dot1, dot2);
                });
                //Go through directions in order of close-ness to velocity
                for(Direction d : hDir){
                    Optional<GravityEffect> effect = effectsBelowPlayer.stream().filter(g -> g.direction() == d).findFirst();
                    if(effect.isPresent()){
                        return effect;
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static boolean arePerpendicular(Direction dir0, Direction dir1){
        return dir0 != dir1 && dir0 != dir1.getOpposite();
    }

    private List<Direction> getHorizontalDirections(){
        ArrayList<Direction> directions = new ArrayList<>();
        //Get all horizontal directions
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        directions.add(Direction.EAST);
        directions.add(Direction.WEST);
        //Convert to world direction
        Direction gravityDirection = getFieldGravity().direction();
        directions.replaceAll(direction -> RotationUtil.dirPlayerToWorld(direction, gravityDirection));
        return directions;
    }
}