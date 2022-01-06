package cyborgcabbage.amethystgravity;

import cyborgcabbage.amethystgravity.block.PlatingBlock;
import cyborgcabbage.amethystgravity.block.entity.PlatingBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AmethystGravity implements ModInitializer {
	public static final String MODID = "amethystgravity";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final Block PLATING_BLOCK = new PlatingBlock(FabricBlockSettings.of(Material.AMETHYST).nonOpaque().noCollision().breakInstantly());

	public static BlockEntityType<PlatingBlockEntity> PLATING_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "plating"), PLATING_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "plating"), new BlockItem(PLATING_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));

		PLATING_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "amethystgravity:plating_block_entity", FabricBlockEntityTypeBuilder.create(PlatingBlockEntity::new, PLATING_BLOCK).build(null));
	}
}
