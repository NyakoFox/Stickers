package gay.nyako.stickers;

import com.google.common.collect.Lists;
import gay.nyako.stickers.access.PlayerEntityAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StickerScreen extends Screen {
    public int mouseX = 0;
    public int mouseY = 0;
    public int scrollPacks = 0;
    public int scrollStickers = 0;

    private final int SIDEBAR_WIDTH = 160;

    private float patreonScroller = 0.0f;
    private int patreonScrollerDelay = 0;
    private float rainbowTimer = 0.0f;

    private List<StickerGroupWidget> orderedStickerPacks = Lists.newArrayList();
    private final HashMap<String, StickerGroupWidget> stickerPacks = new HashMap<>();
    private final List<StickerWidget> stickers = Lists.newArrayList();
    private DonateButtonWidget donateButtonWidget;

    private static final String DEFAULT_PACK_KEY = "default";

    public StickerScreen() {
        super(Text.translatable("stickers.title"));
    }

    @Override
    protected void init() {
        for (var pack : stickerPacks.values()) {
            pack.clearStickers();
            this.remove(pack);
        }

        donateButtonWidget = new DonateButtonWidget(24, this.height - 24);
        this.addDrawableChild(donateButtonWidget);

        stickerPacks.clear();

        StickersMod.STICKER_MANAGER.stickerPacks.forEach(this::addStickerPack);

        orderedStickerPacks = new ArrayList<>(stickerPacks.values());
        orderedStickerPacks.sort((a, b) -> a == b ? 0 :
            a.data.key.equals(DEFAULT_PACK_KEY) ? -4000 :
                    b.data.key.equals(DEFAULT_PACK_KEY) ? 4000:
                            a.data.name.compareTo(b.data.name));

        readjustComponents();

        if (!orderedStickerPacks.isEmpty()) {
            loadStickerPack(orderedStickerPacks.getFirst());
        }

        patreonScrollerDelay = 40;

        super.init();
    }

    private void addStickerPack(String key, StickerPack value) {
        if (((PlayerEntityAccess)MinecraftClient.getInstance().player).getStickerPackCollection().hasStickerPack(key))
        {
            for (var sticker : value.stickers) {
                addSticker(key, sticker);
            }
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }

    public void loadStickerPack(StickerGroupWidget pack) {
        clearStickers();

        pack.select();
        for (var stickerData : pack.stickerData) {
            String packID = null;
            for (var stickerPack : stickerPacks.entrySet()) {
                if (stickerPack.getValue() == pack) {
                    packID = stickerPack.getKey();
                    break;
                }
            }

            StickerWidget button = new StickerWidget(0, 0, stickerData, packID);

            button.setTooltip(Tooltip.of(
                    Text.translatable(
                            "stickers.stickers.sticker_tooltip",
                            ((MutableText) Text.of(stickerData.title)).formatted(Formatting.AQUA),
                            ((MutableText) Text.of(pack.data.name)).formatted(Formatting.WHITE)
                    ).formatted(Formatting.GRAY)
            ));
            this.addDrawableChild(button);

            stickers.add(button);
        }

        for (var stickerPack : orderedStickerPacks) {
            if (pack != stickerPack) {
                stickerPack.deselect();
            }
        }

        readjustComponents();
    }

    private void clearStickers() {
        for (var sticker : stickers) {
            this.remove(sticker);
        }
        stickers.clear();
    }

    private void readjustComponents() {
        readjustPacks();
        readjustStickers();
        donateButtonWidget.setY(this.height - 24);
    }

    private void readjustPacks() {
        int totalHeight = (int) (Math.ceil((float)stickerPacks.size()) * 32);
        int scrollMax = totalHeight - this.height + 32 + 32;

        if (scrollPacks < -scrollMax)
        {
            scrollPacks = -scrollMax;
        }

        if (scrollPacks > 0)
        {
            scrollPacks = 0;
        }

        // center if the entire thing fits in the screen
        if (scrollMax < 0)
        {
            scrollPacks = 0;
        }

        int index = 0;
        for (var pack : orderedStickerPacks)
        {
            pack.setX(24);
            pack.setY(64 + (index++ * (20)) + scrollPacks);

            pack.active = pack.getY() > 32;
            pack.visible = pack.getY() > 32;

        }
    }

    private void readjustStickers() {
        int stickersPerRow = Math.min((this.width - SIDEBAR_WIDTH) / (64 + 16), 6);
        int totalHeight = (int) ((Math.ceil((float)stickers.size() / (float)stickersPerRow)) * (64 + 16));
        int scrollMax = totalHeight - this.height + 16;

        if (scrollStickers < -scrollMax)
        {
            scrollStickers = -scrollMax;
        }

        if (scrollStickers > 0)
        {
            scrollStickers = 0;
        }

        // center if the entire thing fits in the screen
        if (scrollMax < 0)
        {
            scrollStickers = (this.height - 16 - totalHeight) / 2;
        }

        for (int index = 0; index < stickers.size(); index++)
        {
            int x = index % stickersPerRow;
            int y = index / stickersPerRow;

            StickerWidget sticker = stickers.get(index);

            sticker.active = true;
            sticker.setX(((this.width - (64 + 16) * stickersPerRow + SIDEBAR_WIDTH) / 2) + (x * (64 + 16)));
            sticker.setY(16 + (y * (64 + 16)) + scrollStickers);
        }
    }

    public StickerGroupWidget getStickerGroup(String pack) {
        if (!stickerPacks.containsKey(pack)) {
            var widget = new StickerGroupWidget(0, 0, StickersMod.STICKER_MANAGER.stickerPacks.get(pack));
            stickerPacks.put(pack, widget);
            this.addSelectableChild(widget);
        }

        return stickerPacks.get(pack);
    }

    public void addSticker(String pack, Sticker data) {
        getStickerGroup(pack).addSticker(data);
    }

    @Override
    public void tick() {
        super.tick();

        if (patreonScrollerDelay > 0)
            patreonScrollerDelay--;
        else
            patreonScroller++;
        rainbowTimer++;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        float scroller = 0;
        if (patreonScrollerDelay <= 0) scroller = patreonScroller + delta;
        float rainbowTimerWithDelta = rainbowTimer + delta;

        for (Drawable drawable : stickerPacks.values()) {
            context.enableScissor(0, 32, SIDEBAR_WIDTH, this.height - 32);
            drawable.render(context, mouseX, mouseY, delta);
            context.disableScissor();
        }
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        var label = Text.literal("Sticker Packs").setStyle(Style.EMPTY.withUnderline(true));
        context.drawText(textRenderer, label, 28, 20, 0xFFFFFFFF, true);

        context.drawText(textRenderer, Text.of("Thank you to my supporters:"), SIDEBAR_WIDTH, this.height - 32, 0xFFFFFFFF, true);

        ArrayList<String> tierOrder = new ArrayList<>();
        tierOrder.add("Rainbow Supporter");
        tierOrder.add("Deluxe Supporter");
        tierOrder.add("Supporter");

        MutableText builtText = (MutableText) Text.of("");

        for (var tier : tierOrder) {
            if (StickersClientMod.PATREON_MEMBERS.containsKey(tier)) {
                var members = StickersClientMod.PATREON_MEMBERS.get(tier);
                Style style = switch (tier) {
                    case "Rainbow Supporter" -> Style.EMPTY.withColor(MathHelper.hsvToRgb(rainbowTimerWithDelta / 60f % 1f, 0.5f, 1f));
                    case "Deluxe Supporter" -> Style.EMPTY.withColor(Formatting.AQUA);
                    case "Supporter" -> Style.EMPTY.withColor(Formatting.GRAY);
                    default -> Style.EMPTY;
                };

                for (var member : members) {
                    builtText = builtText.append(((MutableText) Text.of(member)).setStyle(style)).append(Text.of("   "));
                }
            }
        }

        context.enableScissor(SIDEBAR_WIDTH, this.height - 32, this.width, this.height);

        var length = Math.max(this.width - SIDEBAR_WIDTH + 32, textRenderer.getWidth(builtText) + 32);
        // scroll the text!
        int offset = (int) scroller % length;
        context.drawText(textRenderer, builtText, SIDEBAR_WIDTH - offset, this.height - 16, 0xFFFFFFFF, true);
        // draw it again to loop
        context.drawText(textRenderer, builtText, SIDEBAR_WIDTH - offset + length, this.height - 16, 0xFFFFFFFF, true);

        context.disableScissor();

        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x88000000);
        context.fill(0, 0, this.SIDEBAR_WIDTH - 16, this.height, 0x88000000);
        context.fill(0, 0, this.SIDEBAR_WIDTH - 16, 48, 0x88000000);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX < SIDEBAR_WIDTH) {
            scrollPacks += (int) (verticalAmount * 20);
            readjustPacks();
        } else {
            scrollStickers += (int) (verticalAmount * 16);
            readjustStickers();
        }

        return true;
    }
}
