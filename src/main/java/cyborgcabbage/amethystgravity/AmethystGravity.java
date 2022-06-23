package cyborgcabbage.amethystgravity;

import cyborgcabbage.amethystgravity.armor.GravityGlassesArmorMaterial;
import cyborgcabbage.amethystgravity.block.CylinderFieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.FieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.PlanetFieldGeneratorBlock;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import cyborgcabbage.amethystgravity.block.entity.*;
import cyborgcabbage.amethystgravity.block.ui.AbstractFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.block.ui.CylinderFieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.block.ui.FieldGeneratorScreenHandler;
import cyborgcabbage.amethystgravity.block.ui.PlanetFieldGeneratorScreenHandler;
import me.andrew.gravitychanger.api.GravitySource;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AmethystGravity implements ModInitializer {
	public static final String MOD_ID = "amethystgravity";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static Identifier FIELD_GRAVITY_SOURCE = new Identifier(MOD_ID, "field_gravity_source");

	/*CLIENT -> SERVER
	* FieldGeneratorBlockEntity.Button button
    * boolean shift
	* */
	public static Identifier FIELD_GENERATOR_MENU_CHANNEL = new Identifier(MOD_ID, "field_generator_menu");

	public static final ItemGroup GRAVITY_ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MOD_ID, "general"),
			() -> new ItemStack(Blocks.COBBLESTONE));

	public static final Block PLATING_BLOCK = new PlatingBlock(FabricBlockSettings.of(Material.AMETHYST).nonOpaque().noCollision().breakInstantly());
	public static BlockEntityType<PlatingBlockEntity> PLATING_BLOCK_ENTITY;

	public static final Block FIELD_GENERATOR_BLOCK = new FieldGeneratorBlock(FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static BlockEntityType<FieldGeneratorBlockEntity> FIELD_GENERATOR_BLOCK_ENTITY;

	public static final Block PLANET_FIELD_GENERATOR_BLOCK = new PlanetFieldGeneratorBlock(FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static BlockEntityType<PlanetFieldGeneratorBlockEntity> PLANET_FIELD_GENERATOR_BLOCK_ENTITY;

	public static final Block CYLINDER_FIELD_GENERATOR_BLOCK = new CylinderFieldGeneratorBlock(FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());
	public static BlockEntityType<CylinderFieldGeneratorBlockEntity> CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY;

	public static final DefaultParticleType GRAVITY_INDICATOR = FabricParticleTypes.simple();

	public static final ScreenHandlerType<FieldGeneratorScreenHandler> FIELD_GENERATOR_SCREEN_HANDLER = new ScreenHandlerType<>(FieldGeneratorScreenHandler::new);

	public static final ScreenHandlerType<PlanetFieldGeneratorScreenHandler> PLANET_FIELD_GENERATOR_SCREEN_HANDLER = new ScreenHandlerType<>(PlanetFieldGeneratorScreenHandler::new);

	public static final ScreenHandlerType<CylinderFieldGeneratorScreenHandler> CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER = new ScreenHandlerType<>(CylinderFieldGeneratorScreenHandler::new);

	public static final GravityGlassesArmorMaterial ggam = new GravityGlassesArmorMaterial();

	public static final Item GRAVITY_GLASSES = new ArmorItem(ggam, EquipmentSlot.HEAD, new Item.Settings().group(GRAVITY_ITEM_GROUP));

	@Override
	public void onInitialize() {
		registerBlockAndItem("plating", PLATING_BLOCK);
		PLATING_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "plating_block_entity"), FabricBlockEntityTypeBuilder.create(PlatingBlockEntity::new, PLATING_BLOCK).build());

		registerBlockAndItem("field_generator", FIELD_GENERATOR_BLOCK);
		FIELD_GENERATOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "field_generator_block_entity"), FabricBlockEntityTypeBuilder.create(FieldGeneratorBlockEntity::new, FIELD_GENERATOR_BLOCK).build());

		registerBlockAndItem("planet_field_generator", PLANET_FIELD_GENERATOR_BLOCK);
		PLANET_FIELD_GENERATOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "planet_field_generator_block_entity"), FabricBlockEntityTypeBuilder.create(PlanetFieldGeneratorBlockEntity::new, PLANET_FIELD_GENERATOR_BLOCK).build());

		registerBlockAndItem("cylinder_field_generator", CYLINDER_FIELD_GENERATOR_BLOCK);
		CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "cylinder_field_generator_block_entity"), FabricBlockEntityTypeBuilder.create(CylinderFieldGeneratorBlockEntity::new, CYLINDER_FIELD_GENERATOR_BLOCK).build());

		Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "gravity_indicator"), GRAVITY_INDICATOR);

		Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "field_generator"), FIELD_GENERATOR_SCREEN_HANDLER);
		Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "planet_field_generator"), PLANET_FIELD_GENERATOR_SCREEN_HANDLER);
		Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "cylinder_field_generator"), CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER);

		ServerPlayNetworking.registerGlobalReceiver(FIELD_GENERATOR_MENU_CHANNEL, (server, player, handler, buf, sender) -> {
			int button = buf.readVarInt();
			boolean shift = buf.readBoolean();
			server.execute(() -> {
				if(player.currentScreenHandler instanceof AbstractFieldGeneratorScreenHandler screenHandler){
					AbstractFieldGeneratorBlockEntity.Button e = AbstractFieldGeneratorBlockEntity.Button.values()[button];
					screenHandler.pressButton(e, shift);
				}
			});
		});

		GravitySource.registerClientDriven(FIELD_GRAVITY_SOURCE, 2, (server, player, handler, buf, packetSender) -> true);

		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gravity_glasses"), GRAVITY_GLASSES);
	}

	private void registerBlockAndItem(String id, Block block){
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, id), block);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, id), new BlockItem(block, new FabricItemSettings().group(GRAVITY_ITEM_GROUP)));
	}
}
