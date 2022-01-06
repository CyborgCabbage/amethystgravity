package cyborgcabbage.amethystgravity.client;

import cyborgcabbage.amethystgravity.AmethystGravity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class AmethystGravityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), AmethystGravity.PYLON_BLOCK);
    }
}
