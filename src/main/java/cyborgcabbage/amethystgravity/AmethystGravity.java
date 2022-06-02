package cyborgcabbage.amethystgravity;

import cyborgcabbage.amethystgravity.block.FieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import cyborgcabbage.amethystgravity.block.entity.PlatingBlockEntity;
import cyborgcabbage.amethystgravity.block.ui.FieldGeneratorScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AmethystGravity implements ModInitializer {
	public static final String MODID = "amethystgravity";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final ItemGroup GRAVITY_ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MODID, "general"),
			() -> new ItemStack(Blocks.COBBLESTONE));

	public static final Block PLATING_BLOCK = new PlatingBlock(FabricBlockSettings.of(Material.AMETHYST).nonOpaque().noCollision().breakInstantly());
	public static BlockEntityType<PlatingBlockEntity> PLATING_BLOCK_ENTITY;

	public static final Block FIELD_GENERATOR_BLOCK = new FieldGeneratorBlock(FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static BlockEntityType<FieldGeneratorBlockEntity> FIELD_GENERATOR_BLOCK_ENTITY;

	public static final DefaultParticleType GRAVITY_INDICATOR = FabricParticleTypes.simple();

	public static final ScreenHandlerType<FieldGeneratorScreenHandler> FIELD_GENERATOR_SCREEN_HANDLER = new ScreenHandlerType<>(FieldGeneratorScreenHandler::new);

	@Override
	public void onInitialize() {
		registerBlockAndItem("plating", PLATING_BLOCK);
		PLATING_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "plating_block_entity"), FabricBlockEntityTypeBuilder.create(PlatingBlockEntity::new, PLATING_BLOCK).build());

		registerBlockAndItem("field_generator", FIELD_GENERATOR_BLOCK);
		FIELD_GENERATOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "field_generator_block_entity"), FabricBlockEntityTypeBuilder.create(FieldGeneratorBlockEntity::new, FIELD_GENERATOR_BLOCK).build());

		Registry.register(Registry.PARTICLE_TYPE, new Identifier(MODID, "gravity_indicator"), GRAVITY_INDICATOR);

		Registry.register(Registry.SCREEN_HANDLER, new Identifier(MODID, "field_generator"), FIELD_GENERATOR_SCREEN_HANDLER);
	}

	private void registerBlockAndItem(String id, Block block){
		Registry.register(Registry.BLOCK, new Identifier(MODID, id), block);
		Registry.register(Registry.ITEM, new Identifier(MODID, id), new BlockItem(block, new FabricItemSettings().group(GRAVITY_ITEM_GROUP)));
	}
}
