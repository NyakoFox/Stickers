package gay.nyako.stickers.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import gay.nyako.stickers.StickerAttachmentTypes;
import gay.nyako.stickers.StickerPackCollection;
import gay.nyako.stickers.StickerSystem;
import gay.nyako.stickers.networking.AddStickerPackPayload;
import gay.nyako.stickers.networking.RemoveStickerPackPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import java.util.Collection;

public class StickerPackCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stickerpack")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .executes(context -> {
                            // list out the sticker packs the player has
                            Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(context, "player");
                            profiles.forEach(profile -> {
                                ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
                                if (player != null) {
                                    context.getSource().sendSuccess(() -> Component.nullToEmpty("Player " + profile.name() + " has the following sticker packs:"), false);
                                    StickerPackCollection collection = player.getAttachedOrCreate(StickerAttachmentTypes.STICKER_COLLECTION);
                                    collection.getPacks().forEach(stickerPack -> {
                                        context.getSource().sendSuccess(() -> Component.nullToEmpty(stickerPack), false);
                                    });
                                }
                                else
                                {
                                    context.getSource().sendSuccess(() -> Component.nullToEmpty("Player " + profile.name() + " is not online"), false);
                                }
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.literal("add")
                                .then(Commands.argument("stickerpack", StringArgumentType.string())
                                        .suggests(new StickerPackSuggestionProvider(false))
                                        .executes(context -> {
                                            // add a sticker pack to the player
                                            Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(context, "player");
                                            String stickerPack = StringArgumentType.getString(context, "stickerpack");
                                            if (profiles.isEmpty())
                                            {
                                                context.getSource().sendFailure(Component.nullToEmpty("No players selected."));
                                                return 0;
                                            }

                                            int return_value = 0;

                                            for (NameAndId profile : profiles) {
                                                ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
                                                if (player == null) {
                                                    if (profiles.size() == 1) {
                                                        context.getSource().sendFailure(Component.nullToEmpty("Player " + profile.name() + " is not online"));
                                                        return 0;
                                                    }
                                                    continue;
                                                }

                                                StickerPackCollection collection = StickerSystem.getCollection(player);
                                                if (collection.hasPack(stickerPack)) {
                                                    if (profiles.size() == 1) {
                                                        // Only a single person; error if they already have it
                                                        context.getSource().sendFailure(Component.nullToEmpty("Player " + profile.name() + " already has that pack."));
                                                        return 0;
                                                    }
                                                    continue;
                                                }

                                                player.setAttached(StickerAttachmentTypes.STICKER_COLLECTION, collection.addPack(stickerPack));

                                                if (ServerPlayNetworking.canSend(player, AddStickerPackPayload.ID)) {
                                                    ServerPlayNetworking.send(player, new AddStickerPackPayload(stickerPack));
                                                }

                                                context.getSource().sendSuccess(() -> Component.nullToEmpty("Added sticker pack " + stickerPack + " to " + profile.name()), false);
                                                return_value++;
                                            }

                                            return return_value;
                                        })
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("stickerpack", StringArgumentType.string())
                                        .suggests(new StickerPackSuggestionProvider(true))
                                        .executes(context -> {
                                            // remove a sticker pack from the player
                                            Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(context, "player");
                                            String stickerPack = StringArgumentType.getString(context, "stickerpack");

                                            if (profiles.isEmpty())
                                            {
                                                context.getSource().sendFailure(Component.nullToEmpty("No players selected."));
                                                return 0;
                                            }

                                            int return_value = 0;

                                            for (NameAndId profile : profiles) {
                                                ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
                                                if (player == null) {
                                                    if (profiles.size() == 1) {
                                                        context.getSource().sendFailure(Component.nullToEmpty("Player " + profile.name() + " is not online"));
                                                        return 0;
                                                    }
                                                    continue;
                                                }

                                                StickerPackCollection collection = StickerSystem.getCollection(player);
                                                if (!collection.hasPack(stickerPack)) {
                                                    if (profiles.size() == 1) {
                                                        // Only a single person; error if they don't have it
                                                        context.getSource().sendFailure(Component.nullToEmpty("Player " + profile.name() + " does not have that pack."));
                                                        return 0;
                                                    }
                                                    continue;
                                                }

                                                player.setAttached(StickerAttachmentTypes.STICKER_COLLECTION, collection.removeStickerPack(stickerPack));

                                                if (ServerPlayNetworking.canSend(player, RemoveStickerPackPayload.ID)) {
                                                    ServerPlayNetworking.send(player, new RemoveStickerPackPayload(stickerPack));
                                                }

                                                context.getSource().sendSuccess(() -> Component.nullToEmpty("Removed sticker pack " + stickerPack + " from " + profile.name()), false);
                                                return_value++;
                                            }
                                            return return_value;
                                        })
                                )
                        )
                )
        );
    }
}
