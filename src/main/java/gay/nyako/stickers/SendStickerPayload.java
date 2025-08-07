package gay.nyako.stickers;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SendStickerPayload(String pack, String name) implements CustomPayload {
    public static final Id<SendStickerPayload> ID = CustomPayload.id("stickers:send_sticker");
    public static final PacketCodec<PacketByteBuf, SendStickerPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SendStickerPayload::pack,
            PacketCodecs.STRING,
            SendStickerPayload::name,
            SendStickerPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
