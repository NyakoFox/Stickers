package gay.nyako.stickers;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;

import java.util.List;

@Config(name = "stickers", wrapperName = "StickersConfig")
public class StickersConfigModel {
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public List<String> defaultPacks = List.of("default");
    public boolean playStickerSound = true;
    @RangeConstraint(min = 20, max = 150)
    public int stickerTimer = 100;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 0, max = 150)
    public int stickerUsageDelay = 50;
}
