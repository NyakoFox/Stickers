package gay.nyako.stickers;

import gay.nyako.stickers.access.PlayerEntityAccess;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StickerNetworking {
    public static final Identifier SEND_STICKER = Identifier.of("stickers", "send_sticker");
    public static final Identifier SEND_STICKER_TO_CLIENT = Identifier.of("stickers", "send_sticker_to_client");
    public static final Identifier ADD_STICKER_PACK = Identifier.of("stickers", "add_sticker_pack");
    public static final Identifier REMOVE_STICKER_PACK = Identifier.of("stickers", "remove_sticker_pack");
    public static final Identifier SEND_STICKER_DATA = Identifier.of("stickers", "send_sticker_data");
    public static final Identifier SEND_STICKER_PACK_DATA = Identifier.of("stickers", "send_sticker_pack_data");

    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SEND_STICKER,
                (server, player, handler, buffer, sender) -> {
                    String pack = buffer.readString();
                    String sticker = buffer.readString();
                    server.execute(() -> {
                        Text playerName = player.getDisplayName();

                        server.getPlayerManager().getPlayerList().forEach((serverPlayerEntity) -> {
                            PacketByteBuf buffer2 = new PacketByteBuf(Unpooled.buffer());
                            buffer2.writeString(pack);
                            buffer2.writeString(sticker);
                            buffer2.writeGameProfile(player.getGameProfile());
                            buffer2.writeText(playerName);
                            ServerPlayNetworking.send(serverPlayerEntity, SEND_STICKER_TO_CLIENT, buffer2);
                        });
                    });
                }
        );
    }

    @Environment(EnvType.CLIENT)
    public static void registerReceiversClient() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_STICKER_TO_CLIENT,
                (client, handler, buffer, sender) -> {
                    var pack = buffer.readString();
                    var stickerName = buffer.readString();
                    var gameProfile = buffer.readGameProfile();
                    var playerName = buffer.readText();

                    client.execute(() -> {
                        StickerPack stickerPack = StickersMod.STICKER_MANAGER.stickerPacks.get(pack);
                        if (stickerPack == null) {
                            return;
                        }
                        Sticker stickerData = null;
                        for (Sticker sticker : stickerPack.stickers) {
                            if (sticker.filename.equals(stickerName)) {
                                stickerData = sticker;
                                break;
                            }
                        }
                        if (stickerData == null) {
                            return;
                        }

                        Text usePlayerName = playerName;

                        if (usePlayerName == null) {
                            usePlayerName = Text.of(gameProfile.getName());
                        }

                        if (usePlayerName == null) {
                            usePlayerName = Text.empty();
                        }

                        StickerSystem.addSticker(usePlayerName, stickerData, gameProfile.getId());
                    });
                }
        );


        ClientPlayNetworking.registerGlobalReceiver(ADD_STICKER_PACK,
                (client, handler, buffer, sender) -> {
                    var pack = buffer.readString();
                    client.execute(() -> {
                        var collection = ((PlayerEntityAccess) client.player).getStickerPackCollection();
                        collection.addStickerPack(pack);
                        ((PlayerEntityAccess) client.player).setStickerPackCollection(collection);
                    });
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(REMOVE_STICKER_PACK,
                (client, handler, buffer, sender) -> {
                    var pack = buffer.readString();
                    client.execute(() -> {
                        var collection = ((PlayerEntityAccess) client.player).getStickerPackCollection();
                        collection.removeStickerPack(pack);
                        ((PlayerEntityAccess) client.player).setStickerPackCollection(collection);
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(SEND_STICKER_DATA,
                (client, handler, buffer, sender) -> {
                    String packName = buffer.readString();
                    String filepath = buffer.readString();
                    String name = buffer.readString();
                    byte[] image = buffer.readByteArray();
                    client.execute(() -> {
                        StickersMod.STICKER_MANAGER.addStickerFromDataPayload(packName, filepath, name, image);
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(SEND_STICKER_PACK_DATA,
                (client, handler, buffer, sender) -> {
                    String key = buffer.readString();
                    String name = buffer.readString();
                    client.execute(() -> {
                        StickersMod.STICKER_MANAGER.addStickerPackFromDataPayload(key, name);
                    });
                }
        );
    }
}
