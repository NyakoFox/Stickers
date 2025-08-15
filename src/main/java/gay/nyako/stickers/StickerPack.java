package gay.nyako.stickers;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class StickerPack {
    private String name;
    private transient String key;
    private List<Sticker> stickers;

    public StickerPack(String key, String name, List<Sticker> stickers) {
        this.key = key;
        this.name = name;
        this.stickers = stickers;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public void loadStickers(String key) {
        // Load stickers from the directory as byte arrays

        this.key = key;

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

    public void loadStickersFromZip(String key, ArrayList<? extends ZipEntry> entries, ZipFile zipFile) {
        this.key = key;
        for (Sticker sticker : stickers) {
            for (ZipEntry entry : entries) {
                if (entry.getName().equals(sticker.filename + ".png")) {
                    try {
                        InputStream stream = zipFile.getInputStream(entry);
                        sticker.setImage(stream.readAllBytes());
                    } catch (Exception e) {
                        StickersMod.LOGGER.warn("Failed to read sticker file: {}/{}.png", key, sticker.filename);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
