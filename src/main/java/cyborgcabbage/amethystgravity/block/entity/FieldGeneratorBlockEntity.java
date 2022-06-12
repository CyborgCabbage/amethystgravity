package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.FieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.ui.FieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class FieldGeneratorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private static final String HEIGHT_KEY = "Height";
    private static final String WIDTH_KEY = "Width";
    private static final String DEPTH_KEY = "Depth";
    private int height = 10;
    private int width = 10;
    private int depth = 10;

    public enum Button {
        HEIGHT_UP,
        HEIGHT_DOWN,
        WIDTH_UP,
        WIDTH_DOWN,
        DEPTH_UP,
        DEPTH_DOWN
    }

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
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
                default -> throw new IndexOutOfBoundsException(index);
            }
        }

        @Override
        public void set(int index, int value) {
            switch(index){
                case 0 -> height = value;
                case 1 -> width = value;
                case 2 -> depth = value;
                default -> throw new IndexOutOfBoundsException(index);
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public FieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.FIELD_GENERATOR_BLOCK_ENTITY, pos, state);
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, FieldGeneratorBlockEntity blockEntity) {
        blockEntity.clientTick((ClientWorld)world, blockPos, blockState);
    }

    private void clientTick(ClientWorld world, BlockPos blockPos, BlockState blockState){
        Direction direction = blockState.get(FieldGeneratorBlock.FACING).getOpposite();
        //Applying gravity effect
        Box box = getGravityEffectBox(blockPos, direction, height / 10.0, width / 10.0, depth / 10.0);
        GravityEffect.applyGravityEffectToPlayers(getGravityEffect(direction, blockPos), box, world);
        //Particles
        Vec3d pVel = new Vec3d(direction.getUnitVector()).multiply(0.03);
        Vec3d boxOrigin = new Vec3d(box.minX,box.minY,box.minZ);
        Vec3d boxSize = new Vec3d(box.getXLength(),box.getYLength(),box.getZLength());
        Random r = world.getRandom();
        double amount = getSurfaceArea()/20.0;
        while(amount > r.nextDouble()){
            Vec3d randomVec = new Vec3d(r.nextDouble(), r.nextDouble(), r.nextDouble());
            Vec3d pPos = boxOrigin.add(boxSize.multiply(randomVec));
            DefaultParticleType particleType = AmethystGravity.GRAVITY_INDICATOR;
            world.addParticle(particleType,pPos.x,pPos.y,pPos.z,pVel.x,pVel.y,pVel.z);
            amount--;
        }
    }

    private Box getGravityEffectBox(BlockPos blockPos, Direction direction, double height, double width, double depth){
        Vec3d blockCentre = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).add(0.5,0.5,0.5);
        Vec3d faceCentre = blockCentre.add(new Vec3d(direction.getUnitVector()).multiply(-0.5));
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(-0.5*width,0,-0.5*depth, direction);
        Vec3d pos2 = RotationUtil.vecPlayerToWorld(0.5*width, height,0.5*depth, direction);
        return new Box(pos1, pos2).offset(faceCentre);
    }

    private GravityEffect getGravityEffect(Direction direction, BlockPos blockPos){
        return new GravityEffect(direction, getVolume(), blockPos);
    }

    private double getVolume(){
        return (height / 10.0)*(width / 10.0)*(depth / 10.0);
    }

    private double getSurfaceArea(){
        double h = height / 10.0;
        double w = width / 10.0;
        double d = depth / 10.0;
        return 2*h*w+2*h*d+2*w*d;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FieldGeneratorScreenHandler(syncId, propertyDelegate, ScreenHandlerContext.create(world, getPos()));
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

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
