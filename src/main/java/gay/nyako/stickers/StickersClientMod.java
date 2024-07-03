package gay.nyako.stickers;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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

    public static final HashMap<String, ArrayList<String>> PATREON_MEMBERS = new HashMap<>();

    @Override
    public void onInitializeClient() {
        StickerNetworking.registerReceiversClient();

        ClientTickEvents.START_CLIENT_TICK.register(StickerSystem::tick);
        HudRenderCallback.EVENT.register(StickerSystem::render);

        KeyBinding stickerBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.stickers.sticker", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_H, // The keycode of the key
                "category.stickers.binds" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (stickerBind.wasPressed()) {
                client.setScreen(new StickerScreen());
            }
        });

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            if (handler.getServerInfo() != null && !handler.getServerInfo().isLocal()) {
                StickersMod.STICKER_MANAGER.stickerPacks.clear();
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://nyako.gay/api/patreon/members"))
                .timeout(Duration.ofMinutes(2))
                .build();
        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            PATREON_MEMBERS.clear();
            Gson gson = new Gson();
            gson.fromJson(new StringReader(response.body()), HashMap.class).forEach((key, value) -> {
                if (key.equals("members")) {
                    var val = (ArrayList<LinkedTreeMap<String, String>>) value;
                    for (var member : val) {
                        if (!PATREON_MEMBERS.containsKey(member.get("tier"))) {
                            PATREON_MEMBERS.put(member.get("tier"), new ArrayList<>());
                        }
                        PATREON_MEMBERS.get(member.get("tier")).add(member.get("member"));
                    }
                }
                //PATREON_MEMBERS.put((String) key, (ArrayList<String>) value);
            });
        });
    }
}
