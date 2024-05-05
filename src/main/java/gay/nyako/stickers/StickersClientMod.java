package gay.nyako.stickers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class StickersClientMod implements ClientModInitializer {

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
    }
}
