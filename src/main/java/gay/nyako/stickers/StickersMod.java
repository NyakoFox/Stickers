package gay.nyako.stickers;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StickersMod implements ModInitializer {
	public static final StickerManager STICKER_MANAGER = new StickerManager();

	public static final Logger LOGGER = LoggerFactory.getLogger("Stickers");

	public static final SuggestionProvider<ServerCommandSource> STICKER_PACK_SUGGESTION_PROVIDER = SuggestionProviders.register(new Identifier("stickers", "sticker_pack_suggestions"), (context, builder) -> {
		STICKER_MANAGER.stickerPacks.forEach((key, stickerPack) -> {
			builder.suggest(key);
		});
		return builder.buildFuture();
	});

	public static final gay.nyako.stickers.StickersConfig CONFIG = gay.nyako.stickers.StickersConfig.createAndLoad();

	@Override
	public void onInitialize() {
		StickerSoundEvents.register();
		StickerNetworking.registerReceivers();

		TrackedDataHandlerRegistry.register(TrackedDataHandlers.STICKER_PACK_COLLECTION_DATA);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			StickerPackCommand.register(dispatcher);
		});

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			STICKER_MANAGER.loadStickers();
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			// for key, value
			for (String stickerPackKey : STICKER_MANAGER.stickerPacks.keySet()) {
				StickerPack stickerPack = STICKER_MANAGER.stickerPacks.get(stickerPackKey);
				ServerPlayNetworking.send(handler.player, new SendStickerPackDataPayload(stickerPackKey, stickerPack.name));
				stickerPack.stickers.forEach(sticker -> {
					ServerPlayNetworking.send(handler.player, new SendStickerDataPayload(stickerPackKey, sticker.filename, sticker.title, sticker.image));
				});
			}
		});
	}
}