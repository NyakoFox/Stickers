package gay.nyako.stickers;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class StickersClientMod implements ClientModInitializer {
    private static final KeyMapping.Category BIND_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("sticker", "binds"));
    private static final KeyMapping STICKER_KEYBIND = new KeyMapping(
            "key.stickers.sticker",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_H,
            BIND_CATEGORY
    );

    @Override
    public void onInitializeClient() {
        StickerNetworking.registerReceiversClient();

        ClientTickEvents.START_CLIENT_TICK.register(StickerSystem::tick);

        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("stickers", "sticker_render"), StickerSystem::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (STICKER_KEYBIND.consumeClick()) {
                client.setScreen(new StickerScreen());
            }
        });

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            if (handler.getServerData() != null && !handler.getServerData().isLan()) {
                StickersMod.STICKER_MANAGER.stickerPacks.clear();
            }
        });
    }
}
