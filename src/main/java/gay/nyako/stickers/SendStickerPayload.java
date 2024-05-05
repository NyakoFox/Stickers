package gay.nyako.stickers;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SendStickerPayload(String string) implements CustomPayload {
    public static final Id<SendStickerPayload> ID = CustomPayload.id("stickers:send_sticker");
    public static final PacketCodec<PacketByteBuf, SendStickerPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SendStickerPayload::string,
            SendStickerPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
