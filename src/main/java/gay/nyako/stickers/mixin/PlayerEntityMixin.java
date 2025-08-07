package gay.nyako.stickers.mixin;

import gay.nyako.stickers.StickerPackCollection;
import gay.nyako.stickers.TrackedDataHandlers;
import gay.nyako.stickers.access.PlayerEntityAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccess {
    @Unique
    private static final TrackedData<StickerPackCollection> STICKER_PACK_COLLECTION = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlers.STICKER_PACK_COLLECTION_DATA);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public StickerPackCollection getStickerPackCollection() {
        return this.dataTracker.get(STICKER_PACK_COLLECTION);
    }

    public void setStickerPackCollection(StickerPackCollection stickerPackCollection) {
        this.dataTracker.set(STICKER_PACK_COLLECTION, stickerPackCollection);
    }

    @Inject(at = @At("TAIL"), method = "initDataTracker()V")
    private void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(STICKER_PACK_COLLECTION, new StickerPackCollection());
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V")
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("StickerCollection", this.dataTracker.get(STICKER_PACK_COLLECTION).toNbt());
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V")
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        StickerPackCollection stickerPackCollection = new StickerPackCollection();
        if (nbt.contains("StickerCollection")) {
            stickerPackCollection.fromNbt(nbt.get("StickerCollection"));
        }
        this.dataTracker.set(STICKER_PACK_COLLECTION, stickerPackCollection);
    }
}
