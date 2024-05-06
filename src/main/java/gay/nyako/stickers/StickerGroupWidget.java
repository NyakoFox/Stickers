package gay.nyako.stickers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class StickerGroupWidget extends PressableWidget {
    public final StickerPack data;
    public final List<Sticker> stickerData = new ArrayList<>();

    private boolean selected = false;

    public StickerGroupWidget(int x, int y, StickerPack pack) {
        super(x, y, 96, 16, Text.of(pack == null ? "All" : pack.name));
        this.data = pack;
    }

    @Override
    public void onPress() {
        selected = true;
        try {
            var screen = (StickerScreen) MinecraftClient.getInstance().currentScreen;
            screen.loadStickerPack(this);
            selected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        var text = (MutableText) Text.of(data.name);
        if (selected) {
            text = text.setStyle(Style.EMPTY.withUnderline(true));
        }
        context.drawText(textRenderer, text, this.getX() + 4, this.getY() + 4, 0xFFFFFFFF, true);
        // context.drawTexture(texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void clearStickers() {
        stickerData.clear();
    }

    public void addSticker(Sticker data) {
        stickerData.add(data);
    }

    public void select() {
        selected = true;
    }

    public void deselect() {
        selected = false;
    }

    public boolean isSelected() {
        return selected;
    }
}