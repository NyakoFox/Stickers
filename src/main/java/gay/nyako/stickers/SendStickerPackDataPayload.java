package gay.nyako.stickers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SendStickerPackDataPayload(String pack, String name) implements CustomPacketPayload {
    public static final Type<SendStickerPackDataPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("stickers", "send_sticker_pack_data"));
    public static final StreamCodec<FriendlyByteBuf, SendStickerPackDataPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SendStickerPackDataPayload::pack,
            ByteBufCodecs.STRING_UTF8,
            SendStickerPackDataPayload::name,
            SendStickerPackDataPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
