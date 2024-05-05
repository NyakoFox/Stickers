package gay.nyako.stickers;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;

public record SendStickerToClientPayload(String string, GameProfile gameProfile) implements CustomPayload {
    public static final Id<SendStickerToClientPayload> ID = CustomPayload.id("stickers:send_sticker");
    public static final PacketCodec<PacketByteBuf, SendStickerToClientPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SendStickerToClientPayload::string,
            PacketCodecs.GAME_PROFILE,
            SendStickerToClientPayload::gameProfile,
            SendStickerToClientPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
