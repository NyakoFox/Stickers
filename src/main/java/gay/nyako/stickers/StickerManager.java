package gay.nyako.stickers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class StickerManager {
    public Map<String, StickerPack> stickerPacks = new HashMap<>();

    public StickerManager() {
    }

    public void loadStickers() {
        Path gameDirectory = FabricLoader.getInstance().getGameDir();
        Path stickerDirectory = gameDirectory.resolve("stickers");
        if (!stickerDirectory.toFile().exists()) {
            stickerDirectory.toFile().mkdir();
        }

        // Load sticker packs

        Gson gson = new Gson();

        // Loop through child directories
        File[] directories = stickerDirectory.toFile().listFiles();
        for (File directory : directories) {
            if (directory.isDirectory()) {
                File packJsonFile = FabricLoader.getInstance().getGameDir().resolve(directory.toPath().resolve("pack.json")).toFile();

                StickersMod.LOGGER.info("Loading sticker pack with ID {}", directory.getName());

                if (!packJsonFile.exists()) {
                    StickersMod.LOGGER.warn(packJsonFile.getAbsolutePath());
                    StickersMod.LOGGER.warn("No pack.json file found in {}, skipping.", directory.getName());
                    continue;
                }

                // load the pack.json file
                JsonReader reader;
                try {
                    reader = new JsonReader(new FileReader(packJsonFile));
                } catch (Exception e) {
                    StickersMod.LOGGER.warn("Failed to read pack.json file in {}, skipping.", directory.getName(), e);
                    return;
                }

                StickerPack pack = gson.fromJson(reader, StickerPack.class);
                pack.loadStickers(directory.getName());
                stickerPacks.put(directory.getName(), pack);
            }
            // else if zip
            else if (directory.getName().endsWith(".zip")) {
                try (ZipFile zipFile = new ZipFile(directory)) {
                    boolean foundPackJson = false;
                    var key = directory.getName().substring(0, directory.getName().length() - 4);
                    ArrayList<? extends ZipEntry> entries = Collections.list(zipFile.entries());
                    for (ZipEntry entry : entries) {
                        if (entry.getName().equals("pack.json")) {
                            foundPackJson = true;
                            StickersMod.LOGGER.info("Loading sticker pack with ID " + key + " (from zip)");
                            JsonReader reader = new JsonReader(new InputStreamReader(zipFile.getInputStream(entry)));
                            StickerPack pack = gson.fromJson(reader, StickerPack.class);
                            pack.loadStickersFromZip(key, entries, zipFile);
                            stickerPacks.put(pack.getKey(), pack);
                        }
                    }
                    if (!foundPackJson) {
                        StickersMod.LOGGER.warn("No pack.json file found in ZIP {}, skipping.", directory.getName());
                    }
                } catch (Exception e) {
                    StickersMod.LOGGER.warn("Failed to read zip file: {}", directory.getName(), e);
                }
            }
        }
    }

    public void addStickerPackFromDataPayload(String key, String name) {
        StickerPack pack = new StickerPack(key, name, new ArrayList<>());
        stickerPacks.put(key, pack);
    }

    @Environment(EnvType.CLIENT)
    public void addStickerFromDataPayload(String packName, String filepath, String name, byte[] image) {
        StickerPack pack = stickerPacks.get(packName);
        if (pack == null) {
            StickersMod.LOGGER.warn("Sticker pack " + packName + " not found, skipping sticker addition");
            return;
        }

        Sticker sticker = new Sticker(filepath, name);
        sticker.setImage(image);
        sticker.setIdentifier(new Identifier("stickers", packName + "/" + filepath));

        pack.addSticker(sticker);

        StickersMod.LOGGER.debug("Added sticker {} to pack {}", name, packName);
        // let's also register this as a NativeImage
        try {
            NativeImage nativeImage = NativeImage.read(null, new ByteArrayInputStream(image));
            sticker.width = nativeImage.getWidth();
            sticker.height = nativeImage.getHeight();
            NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);
            MinecraftClient.getInstance().getTextureManager().registerTexture(sticker.identifier, nativeImageBackedTexture);
        } catch (Exception e) {
            StickersMod.LOGGER.warn("Failed to load image from byte array", e);
        }
    }
}
