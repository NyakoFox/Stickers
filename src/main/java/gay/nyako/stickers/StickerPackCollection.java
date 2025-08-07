package gay.nyako.stickers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.*;

public class StickerPackCollection extends ArrayList<String> {
    public NbtElement toNbt() {
        var list = new NbtList();
        for (var sticker : this) {
            list.add(NbtString.of(sticker));
        }
        return list;
    }

    public void fromNbt(NbtElement nbt) {
        if (nbt instanceof NbtList list) {
            for (var element : list) {
                if (element instanceof NbtString) {
                    this.add(element.asString());
                }
            }
        }
    }

    public boolean hasStickerPack(String sticker) {
        if (StickersMod.CONFIG.defaultPacks().contains(sticker)) return true;
        return this.contains(sticker);
    }

    public List<String> getStickerPacks() {
        return this;
    }

    public void addStickerPack(String sticker) {
        this.add(sticker);
    }

    public void removeStickerPack(String sticker) {
        this.remove(sticker);
    }
}
