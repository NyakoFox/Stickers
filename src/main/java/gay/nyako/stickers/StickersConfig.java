package gay.nyako.stickers;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.List;

@Config(name = "stickers")
public class StickersConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public List<String> defaultPacks = List.of("default");
    @ConfigEntry.Gui.Tooltip
    public boolean playStickerSound = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 20, max = 150)
    public int stickerTimer = 100;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 150)
    public int stickerUsageDelay = 50;
}
