package gay.nyako.stickers;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SendStickerPackDataPayload(String pack, String name) implements CustomPayload {
    public static final Id<SendStickerPackDataPayload> ID = CustomPayload.id("stickers:send_sticker_pack_data");
    public static final PacketCodec<PacketByteBuf, SendStickerPackDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SendStickerPackDataPayload::pack,
            PacketCodecs.STRING,
            SendStickerPackDataPayload::name,
            SendStickerPackDataPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
