package cyborgcabbage.amethystgravity.block.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import cyborgcabbage.amethystgravity.AmethystGravity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.text.DecimalFormat;

public abstract class AbstractFieldGeneratorScreen<T extends AbstractFieldGeneratorScreenHandler> extends HandledScreen<T> {
    private static final Identifier TEXTURE = new Identifier(AmethystGravity.MOD_ID, "textures/gui/blank.png");
    ButtonWidget polarityButton;
    ButtonWidget applyChanges;
    protected int magnitude = 10;

    public AbstractFieldGeneratorScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        playerInventoryTitleY = -100;
        backgroundWidth = 192;
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
        renderValuesAndLabels(matrices);
        //Tooltip
        drawMouseoverTooltip(matrices, mouseX, mouseY);

        if(polarityButton != null) polarityButton.setMessage(getPolarityText());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        magnitude = 10;
        if(hasShiftDown()) magnitude /= 10;
        if(hasControlDown()) magnitude *= 10;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int bWidth = 120;
        int bHeight = 20;
        int bX = (width - bWidth) / 2;
        int bY = (height - bHeight) / 2 + 5;
        //Polarity Button
        polarityButton = addDrawableChild(new ButtonWidget(bX, bY + 30, bWidth, bHeight, getPolarityText(), button -> handler.polarity = 1 - handler.polarity));
        //Apply Changes
        applyChanges = addDrawableChild(new ButtonWidget(bX, bY + 55, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.applyChanges"), button -> {
            sendMenuUpdatePacket(handler.height, handler.width, handler.depth, handler.radius, handler.polarity);
            close();
        }));
    }

    private Text getPolarityText(){
        if(handler.polarity == 0){
            return Text.translatable("amethystgravity.fieldGenerator.attract");
        }else{
            return Text.translatable("amethystgravity.fieldGenerator.repel");
        }
    }

    protected void renderValuesAndLabels(MatrixStack matrices){
        if(handler.creative) {
            int tX = (width) / 2;
            int tY = (height - textRenderer.fontHeight) / 2 + 6;
            String label = "[CREATIVE]";
            textRenderer.draw(matrices, label, tX - textRenderer.getWidth(label) / 2.f + 0.5f, tY - 100, Color.YELLOW.getRGB());
        }
    }

    protected void drawValue(MatrixStack matrices, double value, int xOffset){
        int tX = (width) / 2;
        int tY = (height - textRenderer.fontHeight) / 2 + 6;
        DecimalFormat df = new DecimalFormat("0.0");

        String heightValue = df.format(value);
        textRenderer.draw(matrices, heightValue, tX-textRenderer.getWidth(heightValue)/2.f+xOffset, tY-20, Color.DARK_GRAY.getRGB());
    }

    protected void drawLabel(MatrixStack matrices, String label, int xOffset){
        int tX = (width) / 2;
        int tY = (height - textRenderer.fontHeight) / 2 + 6;
        textRenderer.draw(matrices, label, tX-textRenderer.getWidth(label)/2.f+0.5f+xOffset, tY-60, Color.DARK_GRAY.getRGB());
    }

    protected void sendMenuUpdatePacket(int height, int width, int depth, int radius, int polarity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(height);
        buf.writeInt(width);
        buf.writeInt(depth);
        buf.writeInt(radius);
        buf.writeInt(polarity);
        ClientPlayNetworking.send(AmethystGravity.FIELD_GENERATOR_MENU_CHANNEL, buf);
    }
}
