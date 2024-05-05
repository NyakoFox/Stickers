package gay.nyako.stickers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StickerWidget extends PressableWidget {
    public final String name;
    public final Identifier texture;
    public StickerWidget(int x, int y, String name) {
        super(x, y, 64, 64, Text.of(name));
        this.name = name;
        texture = new Identifier("stickers", "textures/sticker/" + name + ".png");
    }

    @Override
    public void onPress() {
        for (Sticker sticker : StickerSystem.STICKERS)
        {
            if (sticker.playerUUID.equals(MinecraftClient.getInstance().player.getUuid()) && StickerSystem.stickerDelay > 0) {
                return;
            }
        }

        MinecraftClient.getInstance().setScreen(null);
        StickerSystem.showSticker(name);
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
        context.drawTexture(texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}