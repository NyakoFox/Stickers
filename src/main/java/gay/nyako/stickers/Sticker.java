package gay.nyako.stickers;

import net.minecraft.util.Identifier;

public class Sticker {
    public String filename;
    public String title;
    public transient byte[] image;
    public transient Identifier identifier;

    public Sticker(String filename, String title) {
        this.filename = filename;
        this.title = title;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }
}
