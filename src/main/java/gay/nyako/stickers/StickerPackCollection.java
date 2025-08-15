package gay.nyako.stickers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;

public record StickerPackCollection(List<String> stickerPacks) {
    public static final Codec<StickerPackCollection> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.listOf().fieldOf("stickerPacks").forGetter(pack -> pack.stickerPacks)
            ).apply(instance, StickerPackCollection::new)
    );

    public StickerPackCollection() {
        this(List.copyOf(StickersMod.CONFIG.defaultPacks()));
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
