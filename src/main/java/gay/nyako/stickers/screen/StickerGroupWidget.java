package gay.nyako.stickers.screen;

import java.util.ArrayList;
import java.util.List;

import gay.nyako.stickers.Sticker;
import gay.nyako.stickers.StickerPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;

public class StickerGroupWidget extends AbstractButton {
    public final StickerPack data;
    public final List<Sticker> stickerData = new ArrayList<>();

    private boolean selected = false;

    public StickerGroupWidget(int x, int y, StickerPack pack) {
        super(x, y, 96, 16, Component.nullToEmpty(pack == null ? "All" : pack.getName()));
        this.data = pack;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        selected = true;
        try {
            var screen = (StickerScreen) Minecraft.getInstance().screen;
            screen.loadStickerPack(this);
            selected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (isHovered())
        {
            graphics.outline(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, ARGB.white(1f));
        }

        Font textRenderer = Minecraft.getInstance().font;
        var text = (MutableComponent) Component.nullToEmpty(data.getName());
        if (selected) {
            text = text.setStyle(Style.EMPTY.withUnderlined(true));
        }

        graphics.text(textRenderer, text, this.getX() + 4, this.getY() + 4, ARGB.white(isHovered() ? 1f : 0.6f), true);
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

    public boolean isHoveredOrFocused() {
        return selected;
    }
}