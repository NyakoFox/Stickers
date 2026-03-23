package gay.nyako.stickers;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StickerSystem {
    public static List<StickerDisplay> STICKERS = new ArrayList<>();

    public static int STICKER_WIDTH = 64;
    public static int STICKER_HEIGHT = 64;
    public static int STICKER_PADDING = 8;
    public static int STICKER_TIMER = 100;

    public static int STICKER_TIMEOUT = 0;
    public static int STICKER_TIMEOUT_VAL = 50;

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        int y = graphics.guiHeight() / 2 - (STICKERS.size() * (STICKER_HEIGHT + STICKER_PADDING)) / 2;

        for (StickerDisplay stickerDisplay : STICKERS) {
            int x = graphics.guiWidth() - STICKER_WIDTH - STICKER_PADDING;

            if (stickerDisplay.ticks > STICKER_TIMER - 10) {
                x = graphics.guiWidth();
            }

            stickerDisplay.setTarget(x, y);

            stickerDisplay.render(graphics);

            y += STICKER_HEIGHT + STICKER_PADDING;
        }
    }

    public static void addSticker(Component player, Sticker stickerData, UUID playerUUID) {
        Minecraft.getInstance().player.playSound(StickerSoundEvents.STICKER, 1f, 1f);

        StickerDisplay sticker = new StickerDisplay(player, stickerData, playerUUID);

        STICKERS.add(sticker);
    }

    public static void tick(Minecraft minecraftClient) {
        STICKER_TIMEOUT--;
        if (STICKER_TIMEOUT < 0) {
            STICKER_TIMEOUT = 0;
        }
        Iterator<StickerDisplay> iterator = STICKERS.iterator();
        while (iterator.hasNext()) {
            StickerDisplay sticker = iterator.next();
            sticker.tick();
            sticker.ticks++;

            if (sticker.ticks > STICKER_TIMER) {
                iterator.remove();
            }
        }
    }

    public static void showSticker(String pack, Sticker data) {
        STICKER_TIMEOUT = STICKER_TIMEOUT_VAL;
        if (ClientPlayNetworking.canSend(SendStickerPayload.ID)) {
            ClientPlayNetworking.send(new SendStickerPayload(pack, data.filename));
        }
    }
}
