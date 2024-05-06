package gay.nyako.stickers;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StickerPack {
    String name;
    transient String key;
    List<Sticker> stickers;

    public StickerPack(String name, List<Sticker> stickers) {
        this.name = name;
        this.stickers = stickers;
    }

    public void loadStickers() {
        // Load stickers from the directory as byte arrays

        for (Sticker sticker : stickers) {
            // Load the sticker
            String path = "stickers/" + key + "/" + sticker.filename + ".png";
            File stickerFile = FabricLoader.getInstance().getGameDir().toAbsolutePath().resolve(path).toFile();

            if (!stickerFile.exists()) {
                StickersMod.LOGGER.warn("Sticker file not found: {}/{}.png", key, sticker.filename);
                continue;
            }

            byte[] fileContent;
            try {
                fileContent = Files.readAllBytes(stickerFile.toPath());
            } catch (Exception e) {
                StickersMod.LOGGER.warn("Failed to read sticker file: {}/{}.png", key, sticker.filename);
                e.printStackTrace();
                continue;
            }

            sticker.setImage(fileContent);
        }
    }

    public void addSticker(Sticker sticker) {
        stickers.add(sticker);
    }
}
