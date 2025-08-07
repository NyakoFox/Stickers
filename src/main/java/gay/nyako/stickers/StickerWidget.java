package gay.nyako.stickers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StickerWidget extends PressableWidget {
    public final Sticker data;
    public final String stickerPackId;
    public StickerWidget(int x, int y, Sticker data, String stickerPackId) {
        super(x, y, 64, 64, Text.of(data.title));
        this.data = data;
        this.stickerPackId = stickerPackId;
    }

    @Override
    public void onPress() {
        for (StickerDisplay sticker : StickerSystem.STICKERS)
        {
            if (sticker.playerUUID.equals(MinecraftClient.getInstance().player.getUuid()) && StickerSystem.stickerDelay > 0) {
                return;
            }
        }

        MinecraftClient.getInstance().setScreen(null);
        StickerSystem.showSticker(stickerPackId, data);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isHovered())
        {
            context.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            context.drawBorder(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, 0xFFFFFFFF);
        }
        else
        {
            context.setShaderColor(0.8f, 0.8f, 0.8f, this.alpha);
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

        context.drawTexture(this.data.identifier, drawX, drawY, 0, 0, drawWidth, drawHeight, drawWidth, drawHeight);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}