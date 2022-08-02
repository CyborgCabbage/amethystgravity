package cyborgcabbage.amethystgravity.block.entity;

import com.fusionflux.gravity_api.util.RotationUtil;
import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.AbstractFieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.CylinderFieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.ui.CylinderFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CylinderFieldGeneratorBlockEntity extends AbstractFieldGeneratorBlockEntity{
    private static final String RADIUS_KEY = "Radius";
    private static final String WIDTH_KEY = "Width";
    private int radius = 10;

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private int width = 10;

    public CylinderFieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY, pos, state);
    }


    protected void clientTick(World world, BlockPos blockPos, BlockState blockState) {
        Direction.Axis a = blockState.get(CylinderFieldGeneratorBlock.AXIS);
        Direction direction = fromAxis(a);
        //Applying gravity effect
        Box box = getGravityEffectBox();
        List<Direction> dList = Arrays.stream(Direction.values()).filter(d -> d.getAxis() != a).toList();
        GravityEffect.applyGravityEffectToPlayers(getGravityEffect(direction, blockPos), box, world, getPolarity() != 0, dList, false);
    }

    @Override
    protected void serverTick(World world, BlockPos blockPos, BlockState blockState) {
        Direction.Axis a = blockState.get(CylinderFieldGeneratorBlock.AXIS);
        Direction direction = fromAxis(a);
        //Applying gravity effect
        Box box = getGravityEffectBox();
        List<Direction> dList = Arrays.stream(Direction.values()).filter(d -> d.getAxis() != a).toList();
        GravityEffect.applyGravityEffectToEntities(getGravityEffect(direction, blockPos), box, world, getPolarity() != 0, dList, false);
        //Check fuel source
        if(world.getRandom().nextInt(20) == 0){
            int found = searchAmethyst();
            while (radius > 1 && calculateRequiredAmethyst() > found) {
                radius--;
            }
            while (width > 10 && calculateRequiredAmethyst() > found) {
                width--;
            }
            if(world instanceof ServerWorld sw) {
                sw.markDirty(pos);
                sw.getChunkManager().markForUpdate(pos);
            }
        }
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
    public Box getGravityEffectBox() {
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
        return new CylinderFieldGeneratorScreenHandler(syncId, ScreenHandlerContext.create(world, getPos()), isCreative());
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeInt(radius);
        packetByteBuf.writeInt(width);
        packetByteBuf.writeInt(polarity);
        packetByteBuf.writeInt(visibility);

    }

    public void updateSettings(ServerPlayerEntity player, int radius, int width, int polarity, int visibility){
        int oldRadius = this.radius;
        int oldWidth = this.width;
        setRadius(radius);
        setWidth(width);
        setPolarity(polarity);
        setVisibility(visibility);
        int required = calculateRequiredAmethyst();
        int found = searchAmethyst();
        if(required > found){
            setRadius(oldRadius);
            setWidth(oldWidth);
        }
        player.sendMessage(Text.translatable("amethystgravity.fieldGenerator.blocks", required, found).formatted(required > found ? Formatting.RED : Formatting.GREEN), true);
    }
}
