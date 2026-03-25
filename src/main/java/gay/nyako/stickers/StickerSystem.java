package gay.nyako.stickers;

import gay.nyako.stickers.config.StickersConfig;
import gay.nyako.stickers.networking.SendStickerPayload;
import gay.nyako.stickers.rendering.StickerDisplay;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StickerSystem {
    public static List<StickerDisplay> STICKERS = new ArrayList<>();

    public static int STICKER_WIDTH = 64;
    public static int STICKER_HEIGHT = 64;
    public static int STICKER_PADDING = 8;

    public static int STICKER_TIMEOUT = 0;

    public static int getStickerTimer()
    {
        return StickersMod.CONFIG.stickerTimer();
    }

    public static int getStickerUsageDelay()
    {
        return StickersMod.CONFIG.stickerUsageDelay();
    }

    public static void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        int y = graphics.guiHeight() / 2 - (STICKERS.size() * (STICKER_HEIGHT + STICKER_PADDING)) / 2;

        for (StickerDisplay stickerDisplay : STICKERS) {
            int x = graphics.guiWidth() - STICKER_WIDTH - STICKER_PADDING;

            if (stickerDisplay.getTicks() > getStickerTimer() - 10) {
                x = graphics.guiWidth();
            }

            stickerDisplay.setTarget(x, y);

            stickerDisplay.extractRenderState(graphics);

            y += STICKER_HEIGHT + STICKER_PADDING;
        }
    }

    public static void addSticker(Component player, Sticker stickerData, UUID playerUUID) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null && StickersMod.CONFIG.playStickerSound()) {
            localPlayer.playSound(StickerSoundEvents.STICKER, 1f, 1f);
        }

        StickerDisplay sticker = new StickerDisplay(player, stickerData, playerUUID);

        STICKERS.add(sticker);
    }

    public static StickerPackCollection getCollection(Player player) {
        return player.getAttachedOrCreate(StickerAttachmentTypes.STICKER_COLLECTION);
    }

    public static boolean hasPack(Player player, StickerPack pack) {
        return getCollection(player).hasPack(pack.getKey());
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

            if (sticker.getTicks() > getStickerTimer()) {
                iterator.remove();
            }
        }
    }

    public static void showSticker(String pack, Sticker data) {
        STICKER_TIMEOUT = getStickerUsageDelay();
        if (ClientPlayNetworking.canSend(SendStickerPayload.ID)) {
            ClientPlayNetworking.send(new SendStickerPayload(pack, data.filename));
        }
    }
}
