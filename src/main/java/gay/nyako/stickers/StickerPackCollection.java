package gay.nyako.stickers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record StickerPackCollection(List<String> stickerPacks) {
    public static final Codec<StickerPackCollection> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.listOf().fieldOf("stickerPacks").forGetter(pack -> pack.stickerPacks)
            ).apply(instance, StickerPackCollection::new)
    );

    public static StreamCodec<ByteBuf, StickerPackCollection> PACKET_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public StickerPackCollection() {
        this(List.of());
    }

    public boolean hasStickerPack(String sticker) {
        return stickerPacks.contains(sticker);
    }

    @Override
    public List<String> stickerPacks() {
        return stickerPacks;
    }

    public StickerPackCollection addStickerPack(String sticker) {
        List<String> stickerPacks = stickerPacks();
        if (stickerPacks.contains(sticker)) {
            return this; // Already contains the sticker pack
        }

        stickerPacks = new ArrayList<>(stickerPacks);
        stickerPacks.add(sticker);

        return new StickerPackCollection(Collections.unmodifiableList(stickerPacks));
    }

    public StickerPackCollection removeStickerPack(String sticker) {
        List<String> stickerPacks = stickerPacks();
        if (!stickerPacks.contains(sticker)) {
            return this; // Does not contain the sticker pack
        }

        stickerPacks = new ArrayList<>(stickerPacks);
        stickerPacks.remove(sticker);

        return new StickerPackCollection(Collections.unmodifiableList(stickerPacks));
    }
}
