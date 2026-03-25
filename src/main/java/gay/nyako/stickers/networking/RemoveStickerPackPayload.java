package gay.nyako.stickers.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RemoveStickerPackPayload(String string) implements CustomPacketPayload {
    public static final Type<RemoveStickerPackPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("stickers", "remove_sticker_pack"));
    public static final StreamCodec<FriendlyByteBuf, RemoveStickerPackPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            RemoveStickerPackPayload::string,
            RemoveStickerPackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
