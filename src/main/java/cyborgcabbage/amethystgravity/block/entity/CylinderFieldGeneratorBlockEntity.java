package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.CylinderFieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.FieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.ui.CylinderFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.block.ui.FieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import me.andrew.gravitychanger.util.RotationUtil;
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

public class CylinderFieldGeneratorBlockEntity extends AbstractFieldGeneratorBlockEntity{
    private static final String RADIUS_KEY = "Radius";
    private static final String WIDTH_KEY = "Width";
    private int radius = 10;
    private int width = 10;

    public CylinderFieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY, pos, state);
        propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch(index){
                    case 0 -> {
                        return radius;
                    }
                    case 1 -> {
                        return width;
                    }
                    case 2 -> {
                        return polarity;
                    }
                    default -> throw new IndexOutOfBoundsException(index);
                }
            }

            @Override
            public void set(int index, int value) {
                switch(index){
                    case 0 -> radius = value;
                    case 1 -> width = value;
                    case 2 -> polarity = value;
                    default -> throw new IndexOutOfBoundsException(index);
                }
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }


    @Override
    protected void clientTick(ClientWorld world, BlockPos blockPos, BlockState blockState) {
        Direction.Axis a = blockState.get(CylinderFieldGeneratorBlock.AXIS);
        Direction direction = fromAxis(a);
        //Applying gravity effect
        Box box = getGravityEffectBox();
        GravityEffect.applyFourWayGravityEffectToPlayers(getGravityEffect(direction, blockPos), box, world, getPolarity() != 0, a);
    }

    private GravityEffect getGravityEffect(Direction direction, BlockPos blockPos){
        return new GravityEffect(direction, getVolume(getGravityEffectBox()), blockPos);
    }

    private Direction fromAxis(Direction.Axis axis){
        return switch(axis){
            case X -> Direction.WEST;
            case Y -> Direction.DOWN;
            case Z -> Direction.NORTH;
        };
    }

    @Override
    protected Box getGravityEffectBox() {
        Direction direction = fromAxis(getCachedState().get(CylinderFieldGeneratorBlock.AXIS));
        double w = (width/10.0)/2.0;
        double r = (radius/10.0)+0.5f;
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(-r,-w,-r, direction);
        Vec3d pos2 = RotationUtil.vecPlayerToWorld( r, w, r, direction);
        return new Box(pos1, pos2).offset(getPos()).offset(0.5,0.5,0.5);
    }

    public Direction.Axis getAxis(){
        return getCachedState().get(CylinderFieldGeneratorBlock.AXIS);
    }

    public double getRadius(){
        return radius*0.1;
    }

    public double getWidth(){
        return width*0.1;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(RADIUS_KEY, radius);
        nbt.putInt(WIDTH_KEY, width);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        radius = nbt.getInt(RADIUS_KEY);
        width = nbt.getInt(WIDTH_KEY);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CylinderFieldGeneratorScreenHandler(syncId, propertyDelegate, ScreenHandlerContext.create(world, getPos()));
    }
}
