package gay.nyako.stickers.access;

import gay.nyako.stickers.StickerPackCollection;

public interface PlayerEntityAccess {
	StickerPackCollection getStickerPackCollection();
	void setStickerPackCollection(StickerPackCollection stickerPackCollection);
}