package gay.nyako.stickers;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.Identifier;
import net.minecraft.server.players.NameAndId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class StickersMod implements ModInitializer {
	public static final StickerManager STICKER_MANAGER = new StickerManager();

	public static final Logger LOGGER = LoggerFactory.getLogger("Stickers");

	@Override
	public void onInitialize() {
		StickerSoundEvents.register();
		StickerNetworking.registerReceivers();

		StickerAttachmentTypes.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			StickerPackCommand.register(dispatcher);
		});

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			STICKER_MANAGER.loadStickers();
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			// Send all sticker pack data, and all stickers for those packs
			for (String stickerPackKey : STICKER_MANAGER.stickerPacks.keySet()) {
				StickerPack stickerPack = STICKER_MANAGER.stickerPacks.get(stickerPackKey);
				if (ServerPlayNetworking.canSend(handler.player, SendStickerPackDataPayload.ID)) {
					ServerPlayNetworking.send(handler.player, new SendStickerPackDataPayload(stickerPackKey, stickerPack.getName()));
					if (ServerPlayNetworking.canSend(handler.player, SendStickerDataPayload.ID)) {
						stickerPack.getStickers().forEach(sticker -> {
							ServerPlayNetworking.send(handler.player, new SendStickerDataPayload(stickerPackKey, sticker.filename, sticker.title, sticker.image));
						});
					}
				}
			}
		});
	}
}