package cyborgcabbage.amethystgravity.block.entity;

import com.fusionflux.gravity_api.util.RotationUtil;
import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.FieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.ui.FieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
    }

    protected void clientTick(World world, BlockPos blockPos, BlockState blockState){
        Direction direction = blockState.get(FieldGeneratorBlock.FACING).getOpposite();
        //Applying gravity effect
        Box box = getGravityEffectBox();
        GravityEffect.applyGravityEffectToPlayers(getGravityEffect(direction, blockPos), box, world, getPolarity() != 0, List.of(direction), false);
    }

    @Override
    protected void serverTick(World world, BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.get(FieldGeneratorBlock.FACING).getOpposite();
        //Applying gravity effect
        Box box = getGravityEffectBox();
        GravityEffect.applyGravityEffectToEntities(getGravityEffect(direction, blockPos), box, world, getPolarity() != 0, List.of(direction), false);
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

    public void setHeight(int height) {
        this.height = height;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setDepth(int depth) {
        this.depth = depth;
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
        return new FieldGeneratorScreenHandler(syncId, ScreenHandlerContext.create(world, getPos()));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(height);
        buf.writeInt(width);
        buf.writeInt(depth);
        buf.writeInt(polarity);
    }

    public void updateSettings(ServerPlayerEntity player, int height, int width, int depth, int polarity){
        int oldHeight = this.height;
        int oldWidth = this.width;
        int oldDepth = this.depth;
        int oldPolarity = this.polarity;
        setHeight(height);
        setWidth(width);
        setDepth(depth);
        setPolarity(polarity);
        int required = calculateRequiredAmethyst();
        int found = searchAmethyst();
        if(required > found){
            setHeight(oldHeight);
            setWidth(oldWidth);
            setDepth(oldDepth);
            setPolarity(oldPolarity);
        }
        player.sendMessage(Text.translatable("amethystgravity.fieldGenerator.blocks", required, found).formatted(required > found ? Formatting.RED : Formatting.GREEN), true);
    }
}
