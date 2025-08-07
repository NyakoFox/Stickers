package gay.nyako.stickers;

import net.minecraft.util.Identifier;

public class Sticker {
    public String filename;
    public String title;
    public transient byte[] image;
    public transient Identifier identifier;
    public float width;
    public float height;

    public Sticker(String filename, String title) {
        this.filename = filename;
        this.title = title;
        this.width = StickerSystem.STICKER_WIDTH;
        this.height = StickerSystem.STICKER_HEIGHT;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }
}
