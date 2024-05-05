package gay.nyako.stickers;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class StickerSoundEvents {
    public static final SoundEvent STICKER = register("sticker");

    public static SoundEvent register(String name) {
        Identifier identifier = new Identifier("stickers", name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void register() {
        // include the class
    }
}
