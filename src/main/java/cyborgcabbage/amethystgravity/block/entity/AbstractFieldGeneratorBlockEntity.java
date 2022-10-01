package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.block.AbstractFieldGeneratorBlock;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;

public abstract class AbstractFieldGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    private static final String POLARITY_KEY = "Polarity";
    private static final String VISIBILITY_KEY = "Visibility";
    protected int polarity = 0;//0 = attract, 1 = repel
    protected int visibility = 0;//0 = with glasses, 1 = always, 2 = never

    public AbstractFieldGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, AbstractFieldGeneratorBlockEntity blockEntity) {
        blockEntity.clientTick(world, blockPos, blockState);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, AbstractFieldGeneratorBlockEntity blockEntity) {
        blockEntity.serverTick(world, blockPos, blockState);
    }

    public int calculateRequiredAmethyst() {
        if(isCreative()) return 0;
        return Math.max((int)Math.floor(Math.pow(getVolume(), 2.0/3.0))/4, 0);
    }

    protected abstract void clientTick(World world, BlockPos blockPos, BlockState blockState);

    protected abstract void serverTick(World world, BlockPos blockPos, BlockState blockState);

    public abstract Box getGravityEffectBox();

    public double getVolume(){
        return getVolume(getGravityEffectBox());
    }

    public double getSurfaceArea(){
        return getSurfaceArea(getGravityEffectBox());
    }

    protected static double getVolume(Box box){
        double x = box.getXLength();
        double y = box.getYLength();
        double z = box.getZLength();
        return x*y*z;
    }

    protected boolean isCreative(){
        if(getCachedState().getBlock() instanceof AbstractFieldGeneratorBlock b) return b.creative;
        return false;
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

    public void setPolarity(int polarity) {
        this.polarity = polarity;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(POLARITY_KEY, polarity);
        nbt.putInt(VISIBILITY_KEY, visibility);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        polarity = nbt.getInt(POLARITY_KEY);
        visibility = nbt.getInt(VISIBILITY_KEY);
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

    protected int searchAmethyst(){
        HashSet<BlockPos> blocks = new HashSet<>();
        blocks.add(pos);
        for (int i = 0; i < 32; i++) {
            HashSet<BlockPos> newBlocks = new HashSet<>();
            for (BlockPos blockPos : blocks) {
                for (Direction value : Direction.values()) {
                    var offset = blockPos.offset(value);
                    if(world.getBlockState(offset).isOf(Blocks.AMETHYST_BLOCK)) newBlocks.add(offset);
                }
            }
            var sizeBefore = blocks.size();
            blocks.addAll(newBlocks);
            if(sizeBefore == blocks.size()) break;
        }
        return blocks.size();
    }
}
