package cyborgcabbage.amethystgravity.block.entity;

import com.fusionflux.gravity_api.util.RotationUtil;
import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.FieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.ui.FieldGeneratorScreenHandler;
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

import java.util.List;

public class FieldGeneratorBlockEntity extends AbstractFieldGeneratorBlockEntity {
    private static final String HEIGHT_KEY = "Height";
    private static final String WIDTH_KEY = "Width";
    private static final String DEPTH_KEY = "Depth";
    private int height = 10;
    private int width = 10;
    private int depth = 10;

    public FieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.FIELD_GENERATOR_BLOCK_ENTITY, pos, state);
        propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch(index){
                    case 0 -> {
                        return height;
                    }
                    case 1 -> {
                        return width;
                    }
                    case 2 -> {
                        return depth;
                    }
                    case 3 -> {
                        return polarity;
                    }
                    default -> throw new IndexOutOfBoundsException(index);
                }
            }

            @Override
            public void set(int index, int value) {
                switch(index){
                    case 0 -> height = value;
                    case 1 -> width = value;
                    case 2 -> depth = value;
                    case 3 -> polarity = value;
                    default -> throw new IndexOutOfBoundsException(index);
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    protected void clientTick(ClientWorld world, BlockPos blockPos, BlockState blockState){
        Direction direction = blockState.get(FieldGeneratorBlock.FACING).getOpposite();
        //Applying gravity effect
        Box box = getGravityEffectBox();
        GravityEffect.applyGravityEffectToPlayers(getGravityEffect(direction, blockPos), box, world, getPolarity() != 0, List.of(direction), false);
        //Particles
        //spawnParticles(getGravityEffectBox(), new Vec3d(direction.getUnitVector()));
    }

    public Box getGravityEffectBox(){
        BlockPos blockPos = getPos();
        Direction direction = getCachedState().get(FieldGeneratorBlock.FACING).getOpposite();
        Vec3d blockCentre = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).add(0.5,0.5,0.5);
        Vec3d faceCentre = blockCentre.add(new Vec3d(direction.getUnitVector()).multiply(-0.5));
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(-0.5*width*0.1,0,-0.5*depth*0.1, direction);
        Vec3d pos2 = RotationUtil.vecPlayerToWorld(0.5*width*0.1, height*0.1,0.5*depth*0.1, direction);
        return new Box(pos1, pos2).offset(faceCentre);
    }

    private GravityEffect getGravityEffect(Direction direction, BlockPos blockPos){
        return new GravityEffect(direction, getVolume(getGravityEffectBox()), blockPos);
    }

    public Direction getDirection(){
        return getCachedState().get(FieldGeneratorBlock.FACING).getOpposite();
    }

    public double getHeight(){
        return height*0.1;
    }
    public double getWidth(){
        return width*0.1;
    }
    public double getDepth(){
        return depth*0.1;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(HEIGHT_KEY, height);
        nbt.putInt(WIDTH_KEY, width);
        nbt.putInt(DEPTH_KEY, depth);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        height = nbt.getInt(HEIGHT_KEY);
        width = nbt.getInt(WIDTH_KEY);
        depth = nbt.getInt(DEPTH_KEY);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FieldGeneratorScreenHandler(syncId, propertyDelegate, ScreenHandlerContext.create(world, getPos()));
    }
}
