package gay.nyako.stickers;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class StickerSoundEvents {
    public static final SoundEvent STICKER = register("sticker");

    public static SoundEvent register(String name) {
        Identifier identifier = Identifier.fromNamespaceAndPath("stickers", name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    public static void register() {
        // include the class
    }
}
