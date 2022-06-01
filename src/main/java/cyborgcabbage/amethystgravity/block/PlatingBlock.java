package cyborgcabbage.amethystgravity.block;

import com.google.common.collect.ImmutableMap;
import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.PlatingBlockEntity;
import cyborgcabbage.amethystgravity.gravity.GravityEffect;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlatingBlock extends BlockWithEntity {
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;


    protected static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private final Map<BlockState, VoxelShape> shapesByState;

    public static final double LARGE_GRAVITY_EFFECT_HEIGHT = 1.3;
    public static final double SMALL_GRAVITY_EFFECT_HEIGHT = 0.25;

    public PlatingBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
        );
        this.shapesByState = ImmutableMap.copyOf(this.stateManager.getStates().stream().collect(Collectors.toMap(Function.identity(), PlatingBlock::getShapeForState)));
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (state.get(UP)) {
            voxelShape = UP_SHAPE;
        }
        if (state.get(NORTH)) {
            voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
        }
        if (state.get(SOUTH)) {
            voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
        }
        if (state.get(EAST)) {
            voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
        }
        if (state.get(WEST)) {
            voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
        }
        if (state.get(DOWN)) {
            voxelShape = VoxelShapes.union(voxelShape, DOWN_SHAPE);
        }
        return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shapesByState.get(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(UP,DOWN,NORTH,SOUTH,EAST,WEST);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(directionToProperty(direction)) && !canPlaceOn(world, pos.offset(direction), direction.getOpposite())) {
            state = state.with(directionToProperty(direction), false);
            if(getDirections(state).size() == 0){
                return Blocks.AIR.getDefaultState();
            }else{
                return state;
            }
        } else {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 -> {
                return (((state.with(NORTH, state.get(SOUTH))).with(EAST, state.get(WEST))).with(SOUTH, state.get(NORTH))).with(WEST, state.get(EAST));
            }
            case COUNTERCLOCKWISE_90 -> {
                return (((state.with(NORTH, state.get(EAST))).with(EAST, state.get(SOUTH))).with(SOUTH, state.get(WEST))).with(WEST, state.get(NORTH));
            }
            case CLOCKWISE_90 -> {
                return (((state.with(NORTH, state.get(WEST))).with(EAST, state.get(NORTH))).with(SOUTH, state.get(EAST))).with(WEST, state.get(SOUTH));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT -> {
                return (state.with(NORTH, state.get(SOUTH))).with(SOUTH, state.get(NORTH));
            }
            case FRONT_BACK -> {
                return (state.with(EAST, state.get(WEST))).with(WEST, state.get(EAST));
            }
        }
        return super.mirror(state, mirror);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlatingBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, AmethystGravity.PLATING_BLOCK_ENTITY, PlatingBlockEntity::serverTick);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().getItem() == this.asItem()) {
            return !state.get(directionToProperty(context.getSide().getOpposite()));
        }
        return super.canReplace(state, context);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            return blockState.with(directionToProperty(ctx.getSide().getOpposite()),true);
        }
        return getDefaultState().with(directionToProperty(ctx.getSide().getOpposite()), true);
    }

    private boolean canPlaceOn(BlockView world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isSideSolidFullSquare(world, pos, side);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        ArrayList<Direction> directions = getDirections(state);
        if(directions.size() == 1){
            return canPlaceOn(world, pos.offset(directions.get(0)), directions.get(0).getOpposite());
        }
        //Placing inside an existing plating
        if(directions.size() > 1){
            for(Direction dir : getDirections(world.getBlockState(pos))){
                directions.remove(dir);
            }
            return canPlaceOn(world, pos.offset(directions.get(0)), directions.get(0).getOpposite());
        }
        return false;
    }

    public static BooleanProperty directionToProperty(Direction direction){
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public static ArrayList<Direction> getDirections(BlockState blockState){
        ArrayList<Direction> list = new ArrayList<>();
        //Iterate directions
        for(int directionId = 0; directionId < 6; directionId++){
            //Convert ID to Direction
            Direction direction = Direction.byId(directionId);
            //If the plate has this direction
            if(blockState.get(PlatingBlock.directionToProperty(direction))){
                list.add(direction);
            }
        }
        return list;
    }

    public static GravityEffect getLargeGravityEffect(Direction direction, BlockPos blockPos){
        return new GravityEffect(direction, GravityEffect.Type.PLATE, LARGE_GRAVITY_EFFECT_HEIGHT*1*1, blockPos);
    }

    public static GravityEffect getSmallGravityEffect(Direction direction, BlockPos blockPos){
        return new GravityEffect(direction, GravityEffect.Type.PLATE, SMALL_GRAVITY_EFFECT_HEIGHT*1*1, blockPos);
    }

    public static Vec3d getPlatePosition(Direction direction, BlockPos blockPos) {
        Vec3d blockCentre = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        blockCentre = blockCentre.add(0.5, 0.5, 0.5);

        Vec3d plateOffset = new Vec3d(direction.getUnitVector());
        plateOffset = plateOffset.multiply(0.5);

        return blockCentre.add(plateOffset);
    }

    private static Box getGravityEffectBox(BlockPos blockPos, Direction direction, double height){
        double minX = blockPos.getX();
        double minY = blockPos.getY();
        double minZ = blockPos.getZ();
        double maxX = blockPos.getX()+1;
        double maxY = blockPos.getY()+1;
        double maxZ = blockPos.getZ()+1;
        //Extend area of effect a bit so the player can jump without falling off
        double delta = height-1.0;
        switch(direction){
            case DOWN -> maxY+=delta;
            case UP -> minY-=delta;
            case NORTH -> maxZ+=delta;
            case SOUTH -> minZ-=delta;
            case WEST -> maxX+=delta;
            case EAST -> minX-=delta;
        }
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static Box getLargeGravityEffectBox(BlockPos blockPos, Direction direction){
        return getGravityEffectBox(blockPos, direction, LARGE_GRAVITY_EFFECT_HEIGHT);
    }

    public static Box getSmallGravityEffectBox(BlockPos blockPos, Direction direction){
        return getGravityEffectBox(blockPos, direction, SMALL_GRAVITY_EFFECT_HEIGHT);
    }
}
