package cyborgcabbage.amethystgravity;

import com.fusionflux.gravity_api.util.GravityChannel;
import cyborgcabbage.amethystgravity.armor.GravityGlassesArmorMaterial;
import cyborgcabbage.amethystgravity.block.CylinderFieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.FieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.PlanetFieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import cyborgcabbage.amethystgravity.block.entity.CylinderFieldGeneratorBlockEntity;
import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import cyborgcabbage.amethystgravity.block.entity.PlanetFieldGeneratorBlockEntity;
import cyborgcabbage.amethystgravity.block.entity.PlatingBlockEntity;
import cyborgcabbage.amethystgravity.block.ui.AbstractFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.block.ui.CylinderFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.block.ui.FieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.block.ui.PlanetFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.gravity.FieldGravityVerifier;
import cyborgcabbage.amethystgravity.item.EnchantedBlockItem;
import cyborgcabbage.amethystgravity.item.GravityAnchor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AmethystGravity implements ModInitializer {
	public static final String MOD_ID = "amethystgravity";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	/*CLIENT -> SERVER
	* FieldGeneratorBlockEntity.Button button
    * boolean shift
	* */
	public static Identifier FIELD_GENERATOR_MENU_CHANNEL = new Identifier(MOD_ID, "field_generator_menu");

	public static final Block PLATING_BLOCK = new PlatingBlock(0.6, FabricBlockSettings.of(Material.AMETHYST).nonOpaque().noCollision().breakInstantly());
	public static final Block DENSE_PLATING_BLOCK = new PlatingBlock(1.3, FabricBlockSettings.of(Material.AMETHYST).nonOpaque().noCollision().breakInstantly());
	public static BlockEntityType<PlatingBlockEntity> PLATING_BLOCK_ENTITY;

	public static final Block FIELD_GENERATOR_BLOCK = new FieldGeneratorBlock(false, FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static final Block FIELD_GENERATOR_BLOCK_CREATIVE = new FieldGeneratorBlock(true, FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static BlockEntityType<FieldGeneratorBlockEntity> FIELD_GENERATOR_BLOCK_ENTITY;

	public static final Block PLANET_FIELD_GENERATOR_BLOCK = new PlanetFieldGeneratorBlock(false, FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static final Block PLANET_FIELD_GENERATOR_BLOCK_CREATIVE = new PlanetFieldGeneratorBlock(true, FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static BlockEntityType<PlanetFieldGeneratorBlockEntity> PLANET_FIELD_GENERATOR_BLOCK_ENTITY;

	public static final Block CYLINDER_FIELD_GENERATOR_BLOCK = new CylinderFieldGeneratorBlock(false, FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static final Block CYLINDER_FIELD_GENERATOR_BLOCK_CREATIVE = new CylinderFieldGeneratorBlock(true, FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static BlockEntityType<CylinderFieldGeneratorBlockEntity> CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY;

	public static final ItemGroup GRAVITY_ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MOD_ID, "general"),
			() -> new ItemStack(PLANET_FIELD_GENERATOR_BLOCK));

	//public static final DefaultParticleType GRAVITY_INDICATOR = FabricParticleTypes.simple();

	public static final ExtendedScreenHandlerType<FieldGeneratorScreenHandler> FIELD_GENERATOR_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(FieldGeneratorScreenHandler::new);

	public static final ExtendedScreenHandlerType<PlanetFieldGeneratorScreenHandler> PLANET_FIELD_GENERATOR_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(PlanetFieldGeneratorScreenHandler::new);

	public static final ExtendedScreenHandlerType<CylinderFieldGeneratorScreenHandler> CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(CylinderFieldGeneratorScreenHandler::new);

	public static final GravityGlassesArmorMaterial ggam = new GravityGlassesArmorMaterial();

	public static final Item GRAVITY_GLASSES = new ArmorItem(ggam, EquipmentSlot.HEAD, new Item.Settings().group(GRAVITY_ITEM_GROUP));

	public static final Item GRAVITY_ANCHOR_NORTH = new GravityAnchor(Direction.NORTH, new FabricItemSettings().group(GRAVITY_ITEM_GROUP));
	public static final Item GRAVITY_ANCHOR_SOUTH = new GravityAnchor(Direction.SOUTH, new FabricItemSettings().group(GRAVITY_ITEM_GROUP));
	public static final Item GRAVITY_ANCHOR_EAST = new GravityAnchor(Direction.EAST, new FabricItemSettings().group(GRAVITY_ITEM_GROUP));
	public static final Item GRAVITY_ANCHOR_WEST = new GravityAnchor(Direction.WEST, new FabricItemSettings().group(GRAVITY_ITEM_GROUP));
	public static final Item GRAVITY_ANCHOR_UP = new GravityAnchor(Direction.UP, new FabricItemSettings().group(GRAVITY_ITEM_GROUP));
	public static final Item GRAVITY_ANCHOR_DOWN = new GravityAnchor(Direction.DOWN, new FabricItemSettings().group(GRAVITY_ITEM_GROUP));
	@Override
	public void onInitialize() {
		//Register blocks and items
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gravity_glasses"), GRAVITY_GLASSES);
		registerBlockAndItem("plating", PLATING_BLOCK);
		registerBlockAndItem("dense_plating", DENSE_PLATING_BLOCK);

		registerBlockAndItem("field_generator", FIELD_GENERATOR_BLOCK);
		registerBlockAndItem("planet_field_generator", PLANET_FIELD_GENERATOR_BLOCK);
		registerBlockAndItem("cylinder_field_generator", CYLINDER_FIELD_GENERATOR_BLOCK);

		registerEnchantedBlockAndItem("field_generator_creative", FIELD_GENERATOR_BLOCK_CREATIVE);
		registerEnchantedBlockAndItem("planet_field_generator_creative", PLANET_FIELD_GENERATOR_BLOCK_CREATIVE);
		registerEnchantedBlockAndItem("cylinder_field_generator_creative", CYLINDER_FIELD_GENERATOR_BLOCK_CREATIVE);

		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gravity_anchor_north"), GRAVITY_ANCHOR_NORTH);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gravity_anchor_south"), GRAVITY_ANCHOR_SOUTH);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gravity_anchor_east"), GRAVITY_ANCHOR_EAST);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gravity_anchor_west"), GRAVITY_ANCHOR_WEST);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gravity_anchor_up"), GRAVITY_ANCHOR_UP);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gravity_anchor_down"), GRAVITY_ANCHOR_DOWN);

		//Register Block Entities
		PLATING_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "plating_block_entity"), FabricBlockEntityTypeBuilder.create(PlatingBlockEntity::new, PLATING_BLOCK, DENSE_PLATING_BLOCK).build());
		FIELD_GENERATOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "field_generator_block_entity"), FabricBlockEntityTypeBuilder.create(FieldGeneratorBlockEntity::new, FIELD_GENERATOR_BLOCK, FIELD_GENERATOR_BLOCK_CREATIVE).build());
		PLANET_FIELD_GENERATOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "planet_field_generator_block_entity"), FabricBlockEntityTypeBuilder.create(PlanetFieldGeneratorBlockEntity::new, PLANET_FIELD_GENERATOR_BLOCK, PLANET_FIELD_GENERATOR_BLOCK_CREATIVE).build());
		CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "cylinder_field_generator_block_entity"), FabricBlockEntityTypeBuilder.create(CylinderFieldGeneratorBlockEntity::new, CYLINDER_FIELD_GENERATOR_BLOCK, CYLINDER_FIELD_GENERATOR_BLOCK_CREATIVE).build());

		//Register screen handlers
		Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "field_generator"), FIELD_GENERATOR_SCREEN_HANDLER);
		Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "planet_field_generator"), PLANET_FIELD_GENERATOR_SCREEN_HANDLER);
		Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "cylinder_field_generator"), CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER);

		//Register packet receivers
		ServerPlayNetworking.registerGlobalReceiver(FIELD_GENERATOR_MENU_CHANNEL, (server, player, handler, buf, sender) -> {
			int height = buf.readInt();
			int width = buf.readInt();
			int depth = buf.readInt();
			int radius = buf.readInt();
			int polarity = buf.readInt();
			int visibility = buf.readInt();
			server.execute(() -> {
				if (player.currentScreenHandler instanceof AbstractFieldGeneratorScreenHandler screenHandler) {
					if(screenHandler.creative && !player.isCreative()){
						player.sendMessage(Text.translatable("amethystgravity.fieldGenerator.not_creative").formatted(Formatting.RED), true);
						return;
					}
					if(!player.canModifyBlocks()) {
						player.sendMessage(Text.translatable("amethystgravity.fieldGenerator.modify").formatted(Formatting.RED), true);
						return;
					}
					screenHandler.updateSettings(player, height, width, depth, radius, polarity, visibility);
				}
			});
		});

		//Register gravity channel
		GravityChannel.UPDATE_GRAVITY.getVerifierRegistry().register(FieldGravityVerifier.FIELD_GRAVITY_SOURCE, FieldGravityVerifier::check);
	}

	private void registerBlockAndItem(String id, Block block){
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, id), block);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, id), new BlockItem(block, new FabricItemSettings().group(GRAVITY_ITEM_GROUP)));
	}

	private void registerEnchantedBlockAndItem(String id, Block block){
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, id), block);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, id), new EnchantedBlockItem(block, new FabricItemSettings().group(GRAVITY_ITEM_GROUP)));
	}
}
