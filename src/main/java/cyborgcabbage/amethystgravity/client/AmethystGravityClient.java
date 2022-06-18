package cyborgcabbage.amethystgravity.client;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.ui.FieldGeneratorScreen;
import cyborgcabbage.amethystgravity.block.ui.PlanetFieldGeneratorScreen;
import cyborgcabbage.amethystgravity.client.render.block.entity.FieldGeneratorBlockEntityRenderer;
import cyborgcabbage.amethystgravity.client.render.block.entity.PlanetFieldGeneratorBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class AmethystGravityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), AmethystGravity.PLATING_BLOCK);
        /* Adds our particle textures to vanilla's Texture Atlas so it can be shown properly.
         * Modify the namespace and particle id accordingly.
         *
         * This is only used if you plan to add your own textures for the particle. Otherwise, remove  this.*/
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier(AmethystGravity.MOD_ID, "particle/gravity_indicator"));
        }));

        /* Registers our particle client-side.
         * First argument is our particle's instance, created previously on ExampleMod.
         * Second argument is the particle's factory. The factory controls how the particle behaves.
         * In this example, we'll use FlameParticle's Factory.*/
        ParticleFactoryRegistry.getInstance().register(AmethystGravity.GRAVITY_INDICATOR, FlameParticle.Factory::new);
        HandledScreens.register(AmethystGravity.FIELD_GENERATOR_SCREEN_HANDLER, FieldGeneratorScreen::new);
        HandledScreens.register(AmethystGravity.PLANET_FIELD_GENERATOR_SCREEN_HANDLER, PlanetFieldGeneratorScreen::new);
        /*ClientTickEvents.END_CLIENT_TICK.register((minecraftClient) -> {
            ClientPlayerEntity player = minecraftClient.player;
            if(player != null){
                AmethystGravity.LOGGER.info(player == minecraftClient.getCameraEntity());
            }
        });*/
        BlockEntityRendererRegistry.register(AmethystGravity.FIELD_GENERATOR_BLOCK_ENTITY, FieldGeneratorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK_ENTITY, PlanetFieldGeneratorBlockEntityRenderer::new);
    }
}
