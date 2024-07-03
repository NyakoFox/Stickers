package gay.nyako.stickers;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class TrackedDataHandlers {
    public static final TrackedDataHandler<StickerPackCollection> STICKER_PACK_COLLECTION_DATA = new TrackedDataHandler<>() {
        @Override
        public StickerPackCollection copy(StickerPackCollection stickerPackCollection) {
            StickerPackCollection newCollection = new StickerPackCollection();
            newCollection.addAll(stickerPackCollection);
            return newCollection;
        }

        @Override
        public PacketCodec<? super RegistryByteBuf, StickerPackCollection> codec() {
            return StickerPackCollection.OPTIONAL_PACKET_CODEC;
        }
    };
}
