package gay.nyako.stickers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AddStickerPackPayload(String string) implements CustomPacketPayload {
    public static final Type<AddStickerPackPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("stickers", "add_sticker_pack"));
    public static final StreamCodec<FriendlyByteBuf, AddStickerPackPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            AddStickerPackPayload::string,
            AddStickerPackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
