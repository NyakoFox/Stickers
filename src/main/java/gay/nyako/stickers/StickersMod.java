package gay.nyako.stickers;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StickersMod implements ModInitializer {

	public static final StickersConfig CONFIG = AutoConfig.register(StickersConfig.class, GsonConfigSerializer::new).getConfig();

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
	}
}