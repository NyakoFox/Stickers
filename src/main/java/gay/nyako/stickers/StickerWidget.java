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
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isHovered())
        {
            context.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            context.drawBorder(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, 0xFFFFFFFF);
        }
        else
        {
            context.setShaderColor(0.8f, 0.8f, 0.8f, this.alpha);
        }

        context.drawTexture(this.data.identifier, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}