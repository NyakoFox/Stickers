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

        Iterator<StickerDisplay> iterator = STICKERS.iterator();
        while (iterator.hasNext()) {
            int x = drawContext.getScaledWindowWidth() - STICKER_WIDTH - STICKER_PADDING;

            StickerDisplay sticker = iterator.next();
            sticker.tickDelta = tickDelta;

            if (sticker.ticks > StickersMod.CONFIG.stickerTimer() - 10)
            {
                x = drawContext.getScaledWindowWidth();
            }

            sticker.targetX = x;
            sticker.targetY = y;

            sticker.render(drawContext);

            y += STICKER_HEIGHT + STICKER_PADDING;
        }
    }

    public static void addSticker(Text player, Sticker stickerData, UUID playerUUID) {
        if (StickersMod.CONFIG.playStickerSound()) {
            MinecraftClient.getInstance().player.playSound(StickerSoundEvents.STICKER, 1f, 1f);
        }

        StickerDisplay sticker = new StickerDisplay();
        sticker.playerName = player;
        sticker.stickerData = stickerData;
        sticker.playerUUID = playerUUID;
        sticker.tickDelta = 0;
        sticker.ticks = 0;
        sticker.currentX = -1;
        sticker.currentY = -1;

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
        ClientPlayNetworking.send(new SendStickerPayload(pack, data.filename));
    }
}
