package gay.nyako.stickers;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import gay.nyako.stickers.access.PlayerEntityAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class StickerPackCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("stickerpack")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            // list out the sticker packs the player has
                            Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
                            profiles.forEach(profile -> {
                                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(profile.getId());
                                if (player != null) {
                                    context.getSource().sendFeedback(() -> Text.of("Player " + profile.getName() + " has the following sticker packs:"), false);
                                    StickerPackCollection collection = ((PlayerEntityAccess) player).getStickerPackCollection();
                                    collection.getStickerPacks().forEach(stickerPack -> {
                                        context.getSource().sendFeedback(() -> Text.of(stickerPack), false);
                                    });
                                }
                                else
                                {
                                    context.getSource().sendFeedback(() -> Text.of("Player " + profile.getName() + " is not online"), false);
                                }
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("stickerpack", StringArgumentType.string())
                                        .suggests(StickersMod.STICKER_PACK_SUGGESTION_PROVIDER)
                                        .executes(context -> {
                                            // add a sticker pack to the player
                                            Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
                                            String stickerPack = StringArgumentType.getString(context, "stickerpack");
                                            profiles.forEach(profile -> {
                                                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(profile.getId());
                                                if (player != null) {
                                                    StickerPackCollection collection = ((PlayerEntityAccess) player).getStickerPackCollection();
                                                    collection.addStickerPack(stickerPack);
                                                    ((PlayerEntityAccess) player).setStickerPackCollection(collection);
                                                    ServerPlayNetworking.send(player, new AddStickerPackPayload(stickerPack));
                                                    context.getSource().sendFeedback(() -> Text.of("Added sticker pack to " + profile.getName()), false);
                                                }
                                                else
                                                {
                                                    context.getSource().sendFeedback(() -> Text.of("Player " + profile.getName() + " is not online"), false);
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
                                            Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
                                            String stickerPack = StringArgumentType.getString(context, "stickerpack");
                                            profiles.forEach(profile -> {
                                                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(profile.getId());
                                                if (player != null) {
                                                    StickerPackCollection collection = ((PlayerEntityAccess) player).getStickerPackCollection();
                                                    collection.removeStickerPack(stickerPack);
                                                    ((PlayerEntityAccess) player).setStickerPackCollection(collection);
                                                    ServerPlayNetworking.send(player, new RemoveStickerPackPayload(stickerPack));
                                                    context.getSource().sendFeedback(() -> Text.of("Removed sticker pack from " + profile.getName()), false);
                                                }
                                                else
                                                {
                                                    context.getSource().sendFeedback(() -> Text.of("Player " + profile.getName() + " is not online"), false);
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
