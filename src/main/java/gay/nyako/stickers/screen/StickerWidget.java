package gay.nyako.stickers.screen;

import gay.nyako.stickers.Sticker;
import gay.nyako.stickers.StickerSystem;
import gay.nyako.stickers.rendering.StickerDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class StickerWidget extends AbstractButton {
    public final Sticker data;
    public final String stickerPackId;
    public StickerWidget(int x, int y, Sticker data, String stickerPackId) {
        super(x, y, 64, 64, Component.nullToEmpty(data.title));
        this.data = data;
        this.stickerPackId = stickerPackId;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        for (StickerDisplay sticker : StickerSystem.STICKERS)
        {
            if (sticker.playerUUID.equals(Minecraft.getInstance().player.getUUID()) && StickerSystem.STICKER_TIMEOUT > 0) {
                return;
            }
        }

        Minecraft.getInstance().setScreen(null);
        StickerSystem.showSticker(stickerPackId, data);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (isHovered())
        {
            graphics.outline(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, 0xFFFFFFFF);
        }

        float aspectRatio = data.width / data.height;
        int drawWidth;
        int drawHeight;
        if (aspectRatio > 1) {
            // wider than tall
            drawWidth = StickerSystem.STICKER_WIDTH;
            drawHeight = (int) (StickerSystem.STICKER_WIDTH / aspectRatio);
        } else {
            // taller than wide
            drawHeight = StickerSystem.STICKER_HEIGHT;
            drawWidth = (int) (StickerSystem.STICKER_HEIGHT * aspectRatio);
        }

        int drawX = (int) (this.getX() + (StickerSystem.STICKER_WIDTH - drawWidth) / 2f);
        int drawY = (int) (this.getY() + (StickerSystem.STICKER_HEIGHT - drawHeight) / 2f);

        graphics.blit(RenderPipelines.GUI_TEXTURED, this.data.identifier, drawX, drawY, 0, 0, drawWidth, drawHeight, drawWidth, drawHeight, isHovered() ? ARGB.white(1f) : ARGB.colorFromFloat(1F, 0.8F, 0.8F, 0.8F));
    }
}