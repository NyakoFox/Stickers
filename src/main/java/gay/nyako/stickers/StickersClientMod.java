package gay.nyako.stickers;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class StickersClientMod implements ClientModInitializer {
    private static final KeyBinding.Category BIND_CATEGORY = KeyBinding.Category.create(Identifier.of("sticker", "binds"));
    private static final KeyBinding STICKER_KEYBIND = new KeyBinding(
            "key.stickers.sticker",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_H,
            BIND_CATEGORY
    );

    @Override
    public void onInitializeClient() {
        StickerNetworking.registerReceiversClient();

        ClientTickEvents.START_CLIENT_TICK.register(StickerSystem::tick);

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of("stickers", "sticker_render"), StickerSystem::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (STICKER_KEYBIND.wasPressed()) {
                client.setScreen(new StickerScreen());
            }
        });

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            if (handler.getServerInfo() != null && !handler.getServerInfo().isLocal()) {
                StickersMod.STICKER_MANAGER.stickerPacks.clear();
            }
        });
    }
}
