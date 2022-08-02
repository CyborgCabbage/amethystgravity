package cyborgcabbage.amethystgravity.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.ui.PlanetFieldGeneratorScreenHandler;
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

public class PlanetFieldGeneratorBlockEntity extends AbstractFieldGeneratorBlockEntity {
    private static final String RADIUS_KEY = "Radius";

    public void setRadius(int radius) {
        this.radius = radius;
    }

    private int radius = 10;

    public PlanetFieldGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK_ENTITY, pos, state);
    }

    protected void clientTick(World world, BlockPos blockPos, BlockState blockState){
        //Applying gravity effect
        Box box = getGravityEffectBox();
        GravityEffect.applyGravityEffectToPlayers(getGravityEffect(blockPos), box, world, getPolarity() != 0, Arrays.asList(Direction.values()), false);
    }

    protected void serverTick(World world, BlockPos blockPos, BlockState blockState){
        //Applying gravity effect
        Box box = getGravityEffectBox();
        GravityEffect.applyGravityEffectToEntities(getGravityEffect(blockPos), box, world, getPolarity() != 0, Arrays.asList(Direction.values()), false);
        //Check fuel source
        if(world.getRandom().nextInt(20) == 0){
            int found = searchAmethyst();
            while (radius > 1 && calculateRequiredAmethyst() > found) {
                radius--;
            }
            if(world instanceof ServerWorld sw) {
                sw.markDirty(pos);
                sw.getChunkManager().markForUpdate(pos);
            }
        }
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
        return new PlanetFieldGeneratorScreenHandler(syncId, ScreenHandlerContext.create(world, getPos()), isCreative());
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

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(radius);
        buf.writeInt(polarity);
    }

    public void updateSettings(ServerPlayerEntity player, int radius, int polarity){
        int oldRadius = this.radius;
        int oldPolarity = this.polarity;
        setRadius(radius);
        setPolarity(polarity);
        int required = calculateRequiredAmethyst();
        int found = searchAmethyst();
        if(required > found){
            setRadius(oldRadius);
            setPolarity(oldPolarity);
        }
        player.sendMessage(Text.translatable("amethystgravity.fieldGenerator.blocks", required, found).formatted(required > found ? Formatting.RED : Formatting.GREEN), true);
    }
}
