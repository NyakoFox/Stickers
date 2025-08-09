package gay.nyako.stickers;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class StickerAttachmentTypes {
    public static final AttachmentType<StickerPackCollection> STICKER_COLLECTION = AttachmentRegistry.<StickerPackCollection>builder()
            .persistent(StickerPackCollection.CODEC)
            .copyOnDeath()
            .initializer(StickerPackCollection::new)
            .buildAndRegister(Identifier.of("stickers", "sticker_collection"));

    public static void register() {
    }
}
