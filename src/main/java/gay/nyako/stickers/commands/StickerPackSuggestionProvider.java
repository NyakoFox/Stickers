package gay.nyako.stickers.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import gay.nyako.stickers.StickerAttachmentTypes;
import gay.nyako.stickers.StickerPackCollection;
import gay.nyako.stickers.StickersMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class StickerPackSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private boolean list_inverted = false;

    public StickerPackSuggestionProvider(boolean invert_list)
    {
        list_inverted = invert_list;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(context, "player");

        StickersMod.STICKER_MANAGER.stickerPacks.forEach((key, stickerPack) -> {
            if (profiles.size() == 1) {
                // Single person, don't suggest packs they already have
                NameAndId profile = profiles.iterator().next();

                if (profile == null)
                {
                    builder.suggest(key);
                    return;
                }

                ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
                if (player == null) {
                    builder.suggest(key);
                    return;
                }

                StickerPackCollection collection = player.getAttachedOrCreate(StickerAttachmentTypes.STICKER_COLLECTION);
                if (list_inverted == collection.hasPack(key))
                {
                    // List ones they don't have
                    // if inverted, it'll list ones they DO have
                    builder.suggest(key);
                }
            }
            else
            {
                builder.suggest(key);
            }
        });
        return builder.buildFuture();
    }
}
