package gay.nyako.stickers;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;

public class TrackedDataHandlers {
    public static final TrackedDataHandler<StickerPackCollection> STICKER_PACK_COLLECTION_DATA = new TrackedDataHandler<>() {
        @Override
        public void write(PacketByteBuf buf, StickerPackCollection value) {
            buf.writeVarInt(value.size());
            for (String sticker : value) {
                buf.writeString(sticker);
            }
        }

        @Override
        public StickerPackCollection read(PacketByteBuf buf) {
            var collection = new StickerPackCollection();
            int size = buf.readVarInt();
            for (int i = 0; i < size; i++) {
                collection.add(buf.readString());
            }
            return collection;
        }

        @Override
        public StickerPackCollection copy(StickerPackCollection stickerPackCollection) {
            StickerPackCollection newCollection = new StickerPackCollection();
            newCollection.addAll(stickerPackCollection);
            return newCollection;
        }
    };
}
