package gay.nyako.stickers;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class StickerAttachmentTypes {
    public static final AttachmentType<StickerPackCollection> STICKER_COLLECTION = AttachmentRegistry.create(
            Identifier.of("stickers", "sticker_collection"),
            builder -> builder
                    .initializer(StickerPackCollection::new)
                    .persistent(StickerPackCollection.CODEC)
                    .copyOnDeath()
                    .syncWith(
                            StickerPackCollection.PACKET_CODEC,
                            AttachmentSyncPredicate.targetOnly()
                    )
            );

    public static void register() {
    }
}
