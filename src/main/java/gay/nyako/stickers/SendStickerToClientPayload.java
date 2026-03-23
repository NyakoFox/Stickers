package gay.nyako.stickers;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SendStickerToClientPayload(String pack, String sticker, GameProfile gameProfile, Component playerName) implements CustomPacketPayload {
    public static final Type<SendStickerToClientPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("stickers", "send_sticker_to_client"));
    public static final StreamCodec<FriendlyByteBuf, SendStickerToClientPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SendStickerToClientPayload::pack,
            ByteBufCodecs.STRING_UTF8,
            SendStickerToClientPayload::sticker,
            ByteBufCodecs.GAME_PROFILE,
            SendStickerToClientPayload::gameProfile,
            ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC,
            SendStickerToClientPayload::playerName,
            SendStickerToClientPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
