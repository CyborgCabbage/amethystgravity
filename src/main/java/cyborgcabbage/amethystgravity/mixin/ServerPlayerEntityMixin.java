package cyborgcabbage.amethystgravity.mixin;

import cyborgcabbage.amethystgravity.access.GravityData;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements GravityData {
    private static final int MINIMUM_TIME_BETWEEN_GRAVITY_CHANGES = 3; //(in ticks)
    public ArrayList<GravityEffect> gravityBlocks = new ArrayList<>();
    public ArrayList<GravityEffect> gravityBlocks2 = new ArrayList<>();
    public GravityEffect gravityEffect = null;
    public int timeSinceLastGravityChange = 100;

    @Override
    public ArrayList<GravityEffect> getGravityData() {
        return gravityBlocks;
    }

    @Override
    public ArrayList<GravityEffect> getGravityData2() {
        return gravityBlocks2;
    }

    @Override
    public void setCurrentGravityEffect(GravityEffect _gravityEffect) {
        gravityEffect = _gravityEffect;
    }

    @Override
    public GravityEffect getCurrentGravityEffect() {
        return gravityEffect;
    }

    @Override
    public int getTimeSinceLastGravityChange() {
        return timeSinceLastGravityChange;
    }

    @Override
    public void setTimeSinceLastGravityChange(int time) {
        timeSinceLastGravityChange = time;
    }

    @Inject(method="tick()V", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci){
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        setTimeSinceLastGravityChange(getTimeSinceLastGravityChange()+1);
        if(getTimeSinceLastGravityChange() > MINIMUM_TIME_BETWEEN_GRAVITY_CHANGES) {
            //Get the gravity direction we are
            GravityEffect currentGravity = getCurrentGravityEffect();
            if (currentGravity == null) {
                currentGravity = new GravityEffect(GravityChangerAPI.getGravityDirection(player), GravityEffect.Type.BASELINE, Double.MAX_VALUE, BlockPos.ORIGIN);
                setCurrentGravityEffect(currentGravity);
            }
            //Get the gravity direction we should be
            List<GravityEffect> directions = getGravityData();
            List<GravityEffect> directions2 = getGravityData2();
            //Add player default gravity
            GravityEffect baseGravity = new GravityEffect(Direction.DOWN, GravityEffect.Type.BASELINE, Double.MAX_VALUE, BlockPos.ORIGIN);
            directions.add(baseGravity);
            directions2.add(baseGravity);

            GravityEffect newGravity;
            //If the player is flying or in spectator
            if (player.isSpectator() || player.getAbilities().flying) {
                //Reset player gravity to baseline (baseline will have the largest volume and therefore be last in the sorted list)
                newGravity = baseGravity;
            } else {
                //Find the elements of directions which have the lowest volume
                final double lowestVolume = directions.stream().map(GravityEffect::volume).min(Double::compare).orElse(0.0);
                List<GravityEffect> highestPriority = directions.stream().filter(g -> g.volume() == lowestVolume).toList();
                //Find the first element with equal direction to currentGravity
                GravityEffect finalCurrentGravity = currentGravity;
                Optional<GravityEffect> sameDirection = highestPriority.stream().filter(ge -> ge.direction() == finalCurrentGravity.direction()).findFirst();
                //If an element was found, put it in newGravity
                //Otherwise, newGravity = the first element of highestPriority
                newGravity = sameDirection.orElse(highestPriority.get(0));

                //Outside corner snap (if the player just left a plate)
                if (currentGravity.type() == GravityEffect.Type.PLATE && newGravity.type() != GravityEffect.Type.PLATE) {
                    newGravity = getOutsideCornerSnapDirection(player, currentGravity).orElse(newGravity);
                }

                //Remove gravityEffects from directions2 that are of the same direction as current gravity
                final Direction currentGravityDirection = currentGravity.direction();
                directions2 = directions2.stream().filter(g -> g.direction() != currentGravityDirection).toList();
                //Find the elements of directions2 which have the lowest volume
                var optionalMin = directions2.stream().map(GravityEffect::volume).min(Double::compare);
                if (optionalMin.isPresent()) {
                    final double lowestVolume2 = optionalMin.get();
                    List<GravityEffect> highestPriority2 = directions2.stream().filter(g -> g.volume() == lowestVolume2).toList();
                    //Inside corner snap
                    if (lowestVolume >= lowestVolume2) {
                        newGravity = getInsideCornerSnapDirection(player, currentGravity, highestPriority2).orElse(newGravity);
                    }
                }
            }
            //Set gravity
            if (currentGravity.direction() != newGravity.direction()) {
                //TODO: if velocity is large, it shouldn't rotate
                boolean rotatePerspective = arePerpendicular(currentGravity.direction(), newGravity.direction());
                GravityChangerAPI.setGravityDirectionAdvanced(player, newGravity.direction(), rotatePerspective, rotatePerspective);
                setTimeSinceLastGravityChange(0);
            }
            setCurrentGravityEffect(newGravity);
        }
        //Clear direction pool
        getGravityData().clear();
        getGravityData2().clear();
    }

    private Optional<GravityEffect> getInsideCornerSnapDirection(ServerPlayerEntity player, GravityEffect currentGravity, List<GravityEffect> effects) {
        Optional<GravityEffect> effectMin = effects.stream()
                .filter(e -> e.type() == GravityEffect.Type.PLATE)
                .filter(e -> arePerpendicular(e.direction(), currentGravity.direction()))
                .min((e1, e2) -> {
                    Vec3d platePos1 = PlatingBlock.getPlatePosition(e1.direction(), e1.source());
                    Vec3d platePos2 = PlatingBlock.getPlatePosition(e2.direction(), e2.source());
                    double distance1 = platePos1.distanceTo(player.getPos());
                    double distance2 = platePos2.distanceTo(player.getPos());
                    return Double.compare(distance1, distance2);
                });
        effectMin.ifPresent((e) -> {
            Vec3d pos = player.getPos();
            Vec3d delta = new Vec3d(currentGravity.direction().getUnitVector()).multiply(-(PlatingBlock.SMALL_GRAVITY_EFFECT_HEIGHT+0.05));
            pos = pos.add(delta);
            player.requestTeleport(pos.x, pos.y, pos.z);
        });
        return effectMin;
    }

    private Optional<GravityEffect> getOutsideCornerSnapDirection(ServerPlayerEntity player, GravityEffect currentGravity) {
        //Get the blockstate below the player relative to their gravity
        Vec3d gVec = new Vec3d(currentGravity.direction().getUnitVector()).multiply(0.1);
        Vec3d pos = player.getPos().add(gVec);
        BlockPos blockPos = new BlockPos(pos);
        BlockState blockState = player.getWorld().getBlockState(blockPos);
        //If the block is a gravity plate
        Optional<GravityEffect> effect = Optional.empty();
        if (blockState.getBlock() instanceof PlatingBlock) {
            final Vec3d currentPlatePos = PlatingBlock.getPlatePosition(currentGravity.direction(), currentGravity.source());
            effect = PlatingBlock.getDirections(blockState).stream()
                    .filter(d -> arePerpendicular(d, currentGravity.direction()))
                    .min((Direction d1, Direction d2) -> {
                        Vec3d platePos1 = PlatingBlock.getPlatePosition(d1, blockPos);
                        Vec3d platePos2 = PlatingBlock.getPlatePosition(d2, blockPos);
                        double distance1 = platePos1.distanceTo(currentPlatePos);
                        double distance2 = platePos2.distanceTo(currentPlatePos);
                        return Double.compare(distance1, distance2);
                    })
                    .map(direction -> PlatingBlock.getLargeGravityEffect(direction, blockPos));
        }
        return effect;
    }

    private static boolean arePerpendicular(Direction dir0, Direction dir1){
        return dir0 != dir1 && dir0 != dir1.getOpposite();
    }
}
