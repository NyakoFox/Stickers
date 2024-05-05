package gay.nyako.stickers;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AddStickerPackPayload(String string) implements CustomPayload {
    public static final Id<AddStickerPackPayload> ID = CustomPayload.id("stickers:add_sticker_pack");
    public static final PacketCodec<PacketByteBuf, AddStickerPackPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            AddStickerPackPayload::string,
            AddStickerPackPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
