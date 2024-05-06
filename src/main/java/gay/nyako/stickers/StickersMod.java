package gay.nyako.stickers;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gay.nyako.stickers.StickersConfig;

public class StickersMod implements ModInitializer {

	public static final StickersConfig CONFIG = StickersConfig.createAndLoad();
	public static final StickerManager STICKER_MANAGER = new StickerManager();

	public static final Logger LOGGER = LoggerFactory.getLogger("Stickers");

	public static final SuggestionProvider<ServerCommandSource> STICKER_PACK_SUGGESTION_PROVIDER = SuggestionProviders.register(new Identifier("stickers", "sticker_pack_suggestions"), (context, builder) -> {
		STICKER_MANAGER.stickerPacks.forEach((key, stickerPack) -> {
			builder.suggest(key);
		});
		return builder.buildFuture();
	});

	public static final TrackedDataHandler<StickerPackCollection> STICKER_PACK_COLLECTION_DATA = new TrackedDataHandler<>() {
		@Override
		public StickerPackCollection copy(StickerPackCollection stickerPackCollection) {
			StickerPackCollection newCollection = new StickerPackCollection();
			newCollection.addAll(stickerPackCollection);
			return newCollection;
		}

		@Override
		public PacketCodec<? super RegistryByteBuf, StickerPackCollection> codec() {
			return StickerPackCollection.OPTIONAL_PACKET_CODEC;
		}
	};

	@Override
	public void onInitialize() {
		StickerSoundEvents.register();
		StickerNetworking.registerReceivers();

		TrackedDataHandlerRegistry.register(STICKER_PACK_COLLECTION_DATA);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			StickerPackCommand.register(dispatcher);
		});

		STICKER_MANAGER.initialize();


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