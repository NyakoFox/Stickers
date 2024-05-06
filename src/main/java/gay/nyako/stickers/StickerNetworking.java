package gay.nyako.stickers;

import gay.nyako.stickers.access.PlayerEntityAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;

public class StickerNetworking {
    public static void registerReceivers() {
        // Sending stickers
        PayloadTypeRegistry.playC2S().register(SendStickerPayload.ID, SendStickerPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SendStickerToClientPayload.ID, SendStickerToClientPayload.CODEC);

        // Sticker pack manipulation commands
        PayloadTypeRegistry.playS2C().register(AddStickerPackPayload.ID, AddStickerPackPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RemoveStickerPackPayload.ID, RemoveStickerPackPayload.CODEC);

        // Sending initial sticker data
        PayloadTypeRegistry.playS2C().register(SendStickerDataPayload.ID, SendStickerDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SendStickerPackDataPayload.ID, SendStickerPackDataPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SendStickerPayload.ID,
                (payload, context) -> {
                    context.player().server.execute(() -> {
                        context.player().server.getPlayerManager().getPlayerList().forEach((serverPlayerEntity) -> {
                            ServerPlayNetworking.send(serverPlayerEntity, new SendStickerToClientPayload(payload.pack(), payload.string(), context.player().getGameProfile()));
                        });
                    });
                }
        );
    }

    @Environment(EnvType.CLIENT)
    public static void registerReceiversClient() {
        ClientPlayNetworking.registerGlobalReceiver(SendStickerToClientPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        StickerPack pack = StickersMod.STICKER_MANAGER.stickerPacks.get(payload.pack());
                        if (pack == null) {
                            return;
                        }
                        Sticker stickerData = null;
                        for (Sticker sticker : pack.stickers) {
                            if (sticker.filename.equals(payload.string())) {
                                stickerData = sticker;
                                break;
                            }
                        }
                        if (stickerData == null) {
                            return;
                        }
                        StickerSystem.addSticker(Text.of(payload.gameProfile().getName()), stickerData, payload.gameProfile().getId());
                    });
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(AddStickerPackPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        var collection = ((PlayerEntityAccess) context.client().player).getStickerPackCollection();
                        collection.addStickerPack(payload.string());
                        ((PlayerEntityAccess) context.client().player).setStickerPackCollection(collection);
                    });
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(RemoveStickerPackPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        var collection = ((PlayerEntityAccess) context.client().player).getStickerPackCollection();
                        collection.removeStickerPack(payload.string());
                        ((PlayerEntityAccess) context.client().player).setStickerPackCollection(collection);
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(SendStickerDataPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        StickersMod.STICKER_MANAGER.addStickerFromDataPayload(payload);
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(SendStickerPackDataPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        StickersMod.STICKER_MANAGER.addStickerPackFromDataPayload(payload);
                    });
                }
        );
    }
}
