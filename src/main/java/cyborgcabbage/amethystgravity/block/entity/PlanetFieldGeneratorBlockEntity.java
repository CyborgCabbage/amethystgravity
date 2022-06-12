package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.ui.PlanetFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import me.andrew.gravitychanger.util.RotationUtil;
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

public class PlanetFieldGeneratorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private static final String RADIUS_KEY = "Radius";
    private int radius = 10;

    public enum Button {
        RADIUS_UP,
        RADIUS_DOWN,
    }

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return radius;
            } else {
                throw new IndexOutOfBoundsException(index);
            }
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                radius = value;
            } else {
                throw new IndexOutOfBoundsException(index);
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public PlanetFieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK_ENTITY, pos, state);
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, PlanetFieldGeneratorBlockEntity blockEntity) {
        blockEntity.clientTick((ClientWorld)world, blockPos, blockState);
    }

    private void clientTick(ClientWorld world, BlockPos blockPos, BlockState blockState){
        //Applying gravity effect
        Box box = getGravityEffectBox(blockPos, radius / 10.0);
        GravityEffect.applySixWayGravityEffectToPlayers(getGravityEffect(blockPos), box, world);
        //Particles
        Vec3d pVel = new Vec3d(0.0,0.0,0.0);
        Vec3d boxOrigin = new Vec3d(box.minX,box.minY,box.minZ);
        Vec3d boxSize = new Vec3d(box.getXLength(),box.getYLength(),box.getZLength());
        Random rand = world.getRandom();
        double amount = getSurfaceArea()/20.0;
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

    private static Box getGravityEffectBox(BlockPos blockPos, double radius){
        Vec3d pos1 = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Vec3d pos2 = pos1.add(0.5,0.5,0.5);
        return new Box(pos1, pos2).expand(radius);
    }

    private GravityEffect getGravityEffect(BlockPos blockPos){
        return new GravityEffect(null, getVolume(), blockPos);
    }

    private double getVolume(){
        double r = radius / 10.0;
        return 8*r*r*r;
    }

    private double getSurfaceArea(){
        double r = radius / 10.0;
        return 24*r*r;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
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

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
