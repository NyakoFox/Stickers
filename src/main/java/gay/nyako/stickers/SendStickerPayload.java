package gay.nyako.stickers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SendStickerPayload(String pack, String name) implements CustomPacketPayload {
    public static final Type<SendStickerPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("stickers", "send_sticker"));
    public static final StreamCodec<FriendlyByteBuf, SendStickerPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SendStickerPayload::pack,
            ByteBufCodecs.STRING_UTF8,
            SendStickerPayload::name,
            SendStickerPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
