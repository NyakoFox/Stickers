package gay.nyako.stickers;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StickersConfig extends ConfigWrapper<gay.nyako.stickers.StickersConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.util.List<java.lang.String>> defaultPacks = this.optionForKey(this.keys.defaultPacks);
    private final Option<java.lang.Boolean> playStickerSound = this.optionForKey(this.keys.playStickerSound);
    private final Option<java.lang.Integer> stickerTimer = this.optionForKey(this.keys.stickerTimer);
    private final Option<java.lang.Integer> stickerUsageDelay = this.optionForKey(this.keys.stickerUsageDelay);

    private StickersConfig() {
        super(gay.nyako.stickers.StickersConfigModel.class);
    }

    private StickersConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(gay.nyako.stickers.StickersConfigModel.class, janksonBuilder);
    }

    public static StickersConfig createAndLoad() {
        var wrapper = new StickersConfig();
        wrapper.load();
        return wrapper;
    }

    public static StickersConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new StickersConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public java.util.List<java.lang.String> defaultPacks() {
        return defaultPacks.value();
    }

    public void defaultPacks(java.util.List<java.lang.String> value) {
        defaultPacks.set(value);
    }

    public boolean playStickerSound() {
        return playStickerSound.value();
    }

    public void playStickerSound(boolean value) {
        playStickerSound.set(value);
    }

    public int stickerTimer() {
        return stickerTimer.value();
    }

    public void stickerTimer(int value) {
        stickerTimer.set(value);
    }

    public int stickerUsageDelay() {
        return stickerUsageDelay.value();
    }

    public void stickerUsageDelay(int value) {
        stickerUsageDelay.set(value);
    }


    public static class Keys {
        public final Option.Key defaultPacks = new Option.Key("defaultPacks");
        public final Option.Key playStickerSound = new Option.Key("playStickerSound");
        public final Option.Key stickerTimer = new Option.Key("stickerTimer");
        public final Option.Key stickerUsageDelay = new Option.Key("stickerUsageDelay");
    }
}

