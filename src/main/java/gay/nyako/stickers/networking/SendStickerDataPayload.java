package gay.nyako.stickers.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SendStickerDataPayload(String pack, String filepath, String name, byte[] image) implements CustomPacketPayload {
    public static final Type<SendStickerDataPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("stickers", "send_sticker_data"));
    public static final StreamCodec<FriendlyByteBuf, SendStickerDataPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SendStickerDataPayload::pack,
            ByteBufCodecs.STRING_UTF8,
            SendStickerDataPayload::filepath,
            ByteBufCodecs.STRING_UTF8,
            SendStickerDataPayload::name,
            ByteBufCodecs.BYTE_ARRAY,
            SendStickerDataPayload::image,
            SendStickerDataPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
