package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class AbstractFieldGeneratorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    protected PropertyDelegate propertyDelegate;
    private static final String POLARITY_KEY = "Polarity";
    protected int polarity = 0;//0 = attract, 1 = repel

    public AbstractFieldGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public enum Button {
        HEIGHT_UP,
        HEIGHT_DOWN,
        WIDTH_UP,
        WIDTH_DOWN,
        DEPTH_UP,
        DEPTH_DOWN,
        RADIUS_UP,
        RADIUS_DOWN,
        POLARITY
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, AbstractFieldGeneratorBlockEntity blockEntity) {
        blockEntity.clientTick((ClientWorld)world, blockPos, blockState);
    }

    protected abstract void clientTick(ClientWorld world, BlockPos blockPos, BlockState blockState);

    protected abstract Box getGravityEffectBox();

    public double getVolume(){
        return getVolume(getGravityEffectBox());
    }

    public double getSurfaceArea(){
        return getSurfaceArea(getGravityEffectBox());
    }

    protected void spawnParticles(Box box, Vec3d pVel){
        if(world != null && world.isClient()){
            Vec3d boxOrigin = new Vec3d(box.minX,box.minY,box.minZ);
            Vec3d boxSize = new Vec3d(box.getXLength(),box.getYLength(),box.getZLength());
            Random rand = world.getRandom();
            double amount = getSurfaceArea(box)/20.0;
            double temp = rand.nextDouble();
            while(amount > temp){
                Vec3d randomVec = new Vec3d(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
                Vec3d pPos = boxOrigin.add(boxSize.multiply(randomVec));
                DefaultParticleType particleType = AmethystGravity.GRAVITY_INDICATOR;
                if(!world.getBlockState(new BlockPos(pPos)).isOpaque())
                    world.addParticle(particleType,pPos.x,pPos.y,pPos.z,pVel.x,pVel.y,pVel.z);
                amount--;
            }
        }
    }

    protected static double getVolume(Box box){
        double x = box.getXLength();
        double y = box.getYLength();
        double z = box.getZLength();
        return x*y*z;
    }

    protected static double getSurfaceArea(Box box){
        double x = box.getXLength();
        double y = box.getYLength();
        double z = box.getZLength();
        return 2*x*y+2*x*z+2*y*z;
    }

    public int getPolarity(){
        return polarity;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(POLARITY_KEY, polarity);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        polarity = nbt.getInt(POLARITY_KEY);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}