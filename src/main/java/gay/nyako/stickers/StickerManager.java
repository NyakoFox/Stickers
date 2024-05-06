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

                StickersMod.LOGGER.info("Loading sticker pack with ID " + directory.getName());

                if (!packJsonFile.exists()) {
                    StickersMod.LOGGER.warn(packJsonFile.getAbsolutePath());
                    StickersMod.LOGGER.warn("No pack.json file found in " + directory.getName() + ", skipping.");
                    continue;
                }

                // load the pack.json file
                JsonReader reader;
                try {
                    reader = new JsonReader(new FileReader(packJsonFile));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                StickerPack pack = gson.fromJson(reader, StickerPack.class);
                pack.key = directory.getName();
                pack.loadStickers();
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
                            pack.key = key;
                            pack.loadStickersFromZip(entries, zipFile);
                            stickerPacks.put(pack.key, pack);
                        }
                    }
                    if (!foundPackJson) {
                        StickersMod.LOGGER.warn("No pack.json file found in " + directory.getName() + ", skipping.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addStickerPackFromDataPayload(SendStickerPackDataPayload payload) {
        StickerPack pack = new StickerPack(payload.name(), new ArrayList<>());
        pack.key = payload.pack();
        stickerPacks.put(payload.pack(), pack);
    }

    @Environment(EnvType.CLIENT)
    public void addStickerFromDataPayload(SendStickerDataPayload payload) {
        StickerPack pack = stickerPacks.get(payload.pack());
        if (pack == null) {
            StickersMod.LOGGER.warn("Sticker pack " + payload.pack() + " not found, skipping sticker addition");
            return;
        }

        Sticker sticker = new Sticker(payload.filepath(), payload.name());
        sticker.setImage(payload.image());
        sticker.setIdentifier(new Identifier("stickers", payload.pack() + "/" + payload.filepath()));

        pack.addSticker(sticker);

        StickersMod.LOGGER.info("Added sticker " + payload.name() + " to pack " + payload.pack());
        // let's also register this as a NativeImage
        try {
            NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(NativeImage.read(null, new ByteArrayInputStream(payload.image())));
            MinecraftClient.getInstance().getTextureManager().registerTexture(sticker.identifier, nativeImageBackedTexture);
        } catch (Exception e) {
            StickersMod.LOGGER.warn("Failed to load image from byte array");
            e.printStackTrace();
            return;
        }
    }
}
