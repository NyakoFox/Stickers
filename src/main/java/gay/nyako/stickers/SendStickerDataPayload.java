package gay.nyako.stickers;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SendStickerDataPayload(String pack, String filepath, String name, byte[] image) implements CustomPayload {
    public static final Id<SendStickerDataPayload> ID = new Id<>(Identifier.of("stickers", "send_sticker_data"));
    public static final PacketCodec<PacketByteBuf, SendStickerDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SendStickerDataPayload::pack,
            PacketCodecs.STRING,
            SendStickerDataPayload::filepath,
            PacketCodecs.STRING,
            SendStickerDataPayload::name,
            PacketCodecs.BYTE_ARRAY,
            SendStickerDataPayload::image,
            SendStickerDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
