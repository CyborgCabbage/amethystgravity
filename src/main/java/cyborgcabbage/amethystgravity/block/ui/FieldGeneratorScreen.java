package cyborgcabbage.amethystgravity.block.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.text.DecimalFormat;

public class FieldGeneratorScreen extends HandledScreen<FieldGeneratorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(AmethystGravity.MOD_ID, "textures/gui/blank.png");
    private final FieldGeneratorScreenHandler sh;

    public FieldGeneratorScreen(FieldGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        playerInventoryTitleY = -100;
        sh = handler;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        //Draw text
        int tX = (width) / 2;
        int tY = (height - textRenderer.fontHeight) / 2 + 6;
        DecimalFormat df = new DecimalFormat("0.0");
        //Draw values
        String heightValue = df.format(sh.getHeight() / 10.0);
        String widthValue = df.format(sh.getWidth() / 10.0);
        String depthValue = df.format(sh.getDepth() / 10.0);
        textRenderer.draw(matrices, heightValue, tX-textRenderer.getWidth(heightValue)/2.f-50, tY-20, Color.DARK_GRAY.getRGB());
        textRenderer.draw(matrices, widthValue, tX-textRenderer.getWidth(widthValue)/2.f, tY-20, Color.DARK_GRAY.getRGB());
        textRenderer.draw(matrices, depthValue, tX-textRenderer.getWidth(depthValue)/2.f+50, tY-20, Color.DARK_GRAY.getRGB());
        //Draw labels
        String heightLabel = "Height";
        String widthLabel = "Width";
        String depthLabel = "Depth";
        textRenderer.draw(matrices, heightLabel, tX-textRenderer.getWidth(heightLabel)/2.f+0.5f-50, tY-60, Color.DARK_GRAY.getRGB());
        textRenderer.draw(matrices, widthLabel, tX-textRenderer.getWidth(widthLabel)/2.f+0.5f, tY-60, Color.DARK_GRAY.getRGB());
        textRenderer.draw(matrices, depthLabel, tX-textRenderer.getWidth(depthLabel)/2.f+0.5f+50, tY-60, Color.DARK_GRAY.getRGB());

        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int bWidth = 20;
        int bHeight = 20;
        int bX = (width - bWidth) / 2;
        int bY = (height - bHeight) / 2 + 5;
        //Height
        addDrawableChild(new ButtonWidget(bX-50, bY - 40, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.HEIGHT_UP)));
        addDrawableChild(new ButtonWidget(bX-50, bY, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.HEIGHT_DOWN)));
        //Width
        addDrawableChild(new ButtonWidget(bX, bY - 40, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.WIDTH_UP)));
        addDrawableChild(new ButtonWidget(bX, bY, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.WIDTH_DOWN)));
        //Depth
        addDrawableChild(new ButtonWidget(bX+50, bY - 40, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.DEPTH_UP)));
        addDrawableChild(new ButtonWidget(bX+50, bY, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.DEPTH_DOWN)));
    }

    private void sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button button){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(button);
        buf.writeBoolean(hasShiftDown());
        ClientPlayNetworking.send(AmethystGravity.FIELD_GENERATOR_MENU_CHANNEL, buf);
    }
}
