package gay.nyako.stickers;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StickerSystem {
    public static List<StickerDisplay> STICKERS = new ArrayList<>();

    public static int STICKER_WIDTH = 64;
    public static int STICKER_HEIGHT = 64;
    public static int STICKER_PADDING = 8;

    public static int stickerDelay = 0;

    public static void render(DrawContext drawContext, float tickDelta) {
        int y = drawContext.getScaledWindowHeight() / 2 - (STICKERS.size() * (STICKER_HEIGHT + STICKER_PADDING)) / 2;

        for (StickerDisplay stickerDisplay : STICKERS) {
            int x = drawContext.getScaledWindowWidth() - STICKER_WIDTH - STICKER_PADDING;

            if (stickerDisplay.ticks > StickersMod.CONFIG.stickerTimer() - 10) {
                x = drawContext.getScaledWindowWidth();
            }

            stickerDisplay.setTarget(x, y);

            stickerDisplay.render(drawContext, tickDelta);

            y += STICKER_HEIGHT + STICKER_PADDING;
        }
    }

    public static void addSticker(Text player, Sticker stickerData, UUID playerUUID) {
        if (StickersMod.CONFIG.playStickerSound()) {
            MinecraftClient.getInstance().player.playSound(StickerSoundEvents.STICKER, 1f, 1f);
        }

        StickerDisplay sticker = new StickerDisplay(player, stickerData, playerUUID);

        STICKERS.add(sticker);
    }

    public static void tick(MinecraftClient minecraftClient) {
        stickerDelay--;
        if (stickerDelay < 0) {
            stickerDelay = 0;
        }
        Iterator<StickerDisplay> iterator = STICKERS.iterator();
        while (iterator.hasNext()) {
            StickerDisplay sticker = iterator.next();
            sticker.tick();
            sticker.ticks++;

            if (sticker.ticks > StickersMod.CONFIG.stickerTimer()) {
                iterator.remove();
            }
        }
    }

    public static void showSticker(String pack, Sticker data) {
        stickerDelay = StickersMod.CONFIG.stickerUsageDelay();
        if (ClientPlayNetworking.canSend(SendStickerPayload.ID)) {
            ClientPlayNetworking.send(new SendStickerPayload(pack, data.filename));
        }
    }
}
