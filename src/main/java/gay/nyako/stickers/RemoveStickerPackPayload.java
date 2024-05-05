package gay.nyako.stickers;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RemoveStickerPackPayload(String string) implements CustomPayload {
    public static final Id<RemoveStickerPackPayload> ID = CustomPayload.id("stickers:remove_sticker_pack");
    public static final PacketCodec<PacketByteBuf, RemoveStickerPackPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            RemoveStickerPackPayload::string,
            RemoveStickerPackPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
