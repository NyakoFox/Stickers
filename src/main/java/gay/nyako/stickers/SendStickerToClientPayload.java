package gay.nyako.stickers;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record SendStickerToClientPayload(String pack, String sticker, GameProfile gameProfile, Text playerName) implements CustomPayload {
    public static final Id<SendStickerToClientPayload> ID = CustomPayload.id("stickers:send_sticker");
    public static final PacketCodec<PacketByteBuf, SendStickerToClientPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SendStickerToClientPayload::pack,
            PacketCodecs.STRING,
            SendStickerToClientPayload::sticker,
            PacketCodecs.GAME_PROFILE,
            SendStickerToClientPayload::gameProfile,
            TextCodecs.PACKET_CODEC,
            SendStickerToClientPayload::playerName,
            SendStickerToClientPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
