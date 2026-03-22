package gay.nyako.stickers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class StickerPackCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("stickerpack")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            // list out the sticker packs the player has
                            Collection<PlayerConfigEntry> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
                            profiles.forEach(profile -> {
                                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(profile.id());
                                if (player != null) {
                                    context.getSource().sendFeedback(() -> Text.of("Player " + profile.name() + " has the following sticker packs:"), false);
                                    StickerPackCollection collection = player.getAttachedOrCreate(StickerAttachmentTypes.STICKER_COLLECTION);
                                    collection.stickerPacks().forEach(stickerPack -> {
                                        context.getSource().sendFeedback(() -> Text.of(stickerPack), false);
                                    });
                                }
                                else
                                {
                                    context.getSource().sendFeedback(() -> Text.of("Player " + profile.name() + " is not online"), false);
                                }
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("stickerpack", StringArgumentType.string())
                                        .suggests(StickersMod.STICKER_PACK_SUGGESTION_PROVIDER)
                                        .executes(context -> {
                                            // add a sticker pack to the player
                                            Collection<PlayerConfigEntry> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
                                            String stickerPack = StringArgumentType.getString(context, "stickerpack");
                                            profiles.forEach(profile -> {
                                                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(profile.id());
                                                if (player != null) {
                                                    StickerPackCollection collection = player.getAttachedOrCreate(StickerAttachmentTypes.STICKER_COLLECTION);
                                                    player.setAttached(StickerAttachmentTypes.STICKER_COLLECTION, collection.addStickerPack(stickerPack));
                                                    if (ServerPlayNetworking.canSend(player, AddStickerPackPayload.ID)) {
                                                        ServerPlayNetworking.send(player, new AddStickerPackPayload(stickerPack));
                                                    }
                                                    context.getSource().sendFeedback(() -> Text.of("Added sticker pack to " + profile.name()), false);
                                                }
                                                else
                                                {
                                                    context.getSource().sendFeedback(() -> Text.of("Player " + profile.name() + " is not online"), false);
                                                }
                                            });
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("stickerpack", StringArgumentType.string())
                                        .suggests(StickersMod.STICKER_PACK_SUGGESTION_PROVIDER)
                                        .executes(context -> {
                                            // remove a sticker pack from the player
                                            Collection<PlayerConfigEntry> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
                                            String stickerPack = StringArgumentType.getString(context, "stickerpack");
                                            profiles.forEach(profile -> {
                                                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(profile.id());
                                                if (player != null) {
                                                    StickerPackCollection collection = player.getAttachedOrCreate(StickerAttachmentTypes.STICKER_COLLECTION);
                                                    player.setAttached(StickerAttachmentTypes.STICKER_COLLECTION, collection.removeStickerPack(stickerPack));
                                                    if (ServerPlayNetworking.canSend(player, RemoveStickerPackPayload.ID)) {
                                                        ServerPlayNetworking.send(player, new RemoveStickerPackPayload(stickerPack));
                                                    }
                                                    context.getSource().sendFeedback(() -> Text.of("Removed sticker pack from " + profile.name()), false);
                                                }
                                                else
                                                {
                                                    context.getSource().sendFeedback(() -> Text.of("Player " + profile.name() + " is not online"), false);
                                                }
                                            });
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
        );
    }
}
