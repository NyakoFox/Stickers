package gay.nyako.stickers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
                                    collection.stickerPacks().forEach(stickerPack -> {
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
                                        .suggests(StickersMod.STICKER_PACK_SUGGESTION_PROVIDER)
                                        .executes(context -> {
                                            // add a sticker pack to the player
                                            Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(context, "player");
                                            String stickerPack = StringArgumentType.getString(context, "stickerpack");
                                            profiles.forEach(profile -> {
                                                ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
                                                if (player != null) {
                                                    StickerPackCollection collection = player.getAttachedOrCreate(StickerAttachmentTypes.STICKER_COLLECTION);
                                                    player.setAttached(StickerAttachmentTypes.STICKER_COLLECTION, collection.addStickerPack(stickerPack));
                                                    if (ServerPlayNetworking.canSend(player, AddStickerPackPayload.ID)) {
                                                        ServerPlayNetworking.send(player, new AddStickerPackPayload(stickerPack));
                                                    }
                                                    context.getSource().sendSuccess(() -> Component.nullToEmpty("Added sticker pack to " + profile.name()), false);
                                                }
                                                else
                                                {
                                                    context.getSource().sendSuccess(() -> Component.nullToEmpty("Player " + profile.name() + " is not online"), false);
                                                }
                                            });
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("stickerpack", StringArgumentType.string())
                                        .suggests(StickersMod.STICKER_PACK_SUGGESTION_PROVIDER)
                                        .executes(context -> {
                                            // remove a sticker pack from the player
                                            Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(context, "player");
                                            String stickerPack = StringArgumentType.getString(context, "stickerpack");
                                            profiles.forEach(profile -> {
                                                ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
                                                if (player != null) {
                                                    StickerPackCollection collection = player.getAttachedOrCreate(StickerAttachmentTypes.STICKER_COLLECTION);
                                                    player.setAttached(StickerAttachmentTypes.STICKER_COLLECTION, collection.removeStickerPack(stickerPack));
                                                    if (ServerPlayNetworking.canSend(player, RemoveStickerPackPayload.ID)) {
                                                        ServerPlayNetworking.send(player, new RemoveStickerPackPayload(stickerPack));
                                                    }
                                                    context.getSource().sendSuccess(() -> Component.nullToEmpty("Removed sticker pack from " + profile.name()), false);
                                                }
                                                else
                                                {
                                                    context.getSource().sendSuccess(() -> Component.nullToEmpty("Player " + profile.name() + " is not online"), false);
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
