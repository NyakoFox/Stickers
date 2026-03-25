package gay.nyako.stickers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class StickerPackCollection {
    public static final Codec<StickerPackCollection> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.listOf().fieldOf("stickerPacks").forGetter(pack -> pack.stickerPacks)
            ).apply(instance, StickerPackCollection::new)
    );

    public static StreamCodec<ByteBuf, StickerPackCollection> PACKET_CODEC = ByteBufCodecs.fromCodec(CODEC);
    private final List<String> stickerPacks;

    public StickerPackCollection(List<String> stickerPacks) {
        this.stickerPacks = stickerPacks;
    }

    public StickerPackCollection() {
        this(List.copyOf(StickersMod.CONFIG.defaultPacks()));
    }

    public boolean hasPack(String sticker) {
        return stickerPacks.contains(sticker);
    }

    public List<String> getPacks() {
        return stickerPacks;
    }

    public StickerPackCollection addPack(String sticker) {
        List<String> stickerPacks = getPacks();
        if (stickerPacks.contains(sticker)) {
            return this; // Already contains the sticker pack
        }

        stickerPacks = new ArrayList<>(stickerPacks);
        stickerPacks.add(sticker);

        return new StickerPackCollection(Collections.unmodifiableList(stickerPacks));
    }

    public StickerPackCollection removeStickerPack(String sticker) {
        List<String> stickerPacks = getPacks();
        if (!stickerPacks.contains(sticker)) {
            return this; // Does not contain the sticker pack
        }

        stickerPacks = new ArrayList<>(stickerPacks);
        stickerPacks.remove(sticker);

        return new StickerPackCollection(Collections.unmodifiableList(stickerPacks));
    }
}
