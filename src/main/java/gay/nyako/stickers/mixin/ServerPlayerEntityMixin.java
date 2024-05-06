package gay.nyako.stickers.mixin;

import gay.nyako.stickers.access.PlayerEntityAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At("HEAD"))
    private void injected(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ((PlayerEntityAccess) (PlayerEntity) (Object) this).setStickerPackCollection(((PlayerEntityAccess) oldPlayer).getStickerPackCollection());
    }
}
