package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.ui.PlanetFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PlanetFieldGeneratorBlockEntity extends AbstractFieldGeneratorBlockEntity {
    private static final String RADIUS_KEY = "Radius";
    private int radius = 10;

    public PlanetFieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK_ENTITY, pos, state);
        propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if (index == 0) {
                    return radius;
                } else if(index == 1){
                    return polarity;
                }else{
                    throw new IndexOutOfBoundsException(index);
                }
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    radius = value;
                } else if(index == 1){
                    polarity = value;
                } else {
                    throw new IndexOutOfBoundsException(index);
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    protected void clientTick(ClientWorld world, BlockPos blockPos, BlockState blockState){
        //Applying gravity effect
        Box box = getGravityEffectBox();
        GravityEffect.applyGravityEffectToPlayers(getGravityEffect(blockPos), box, world, getPolarity() != 0, Arrays.asList(Direction.values()), false);
        //Particles
        //spawnParticles(box, new Vec3d(0, 0, 0));
    }

    public Box getGravityEffectBox(){
        BlockPos blockPos = getPos();
        Vec3d pos1 = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Vec3d pos2 = pos1.add(1, 1, 1);
        return new Box(pos1, pos2).expand(getRadius());
    }

    private GravityEffect getGravityEffect(BlockPos blockPos){
        return new GravityEffect(null, getVolume(), blockPos);
    }

    public double getRadius(){
        return radius / 10.0;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PlanetFieldGeneratorScreenHandler(syncId, propertyDelegate, ScreenHandlerContext.create(world, getPos()));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(RADIUS_KEY, radius);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        radius = nbt.getInt(RADIUS_KEY);
    }
}
