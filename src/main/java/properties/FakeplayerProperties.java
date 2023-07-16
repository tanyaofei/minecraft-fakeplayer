package properties;


import io.github.hello09x.fakeplayer.Main;
import io.github.tanyaofei.plugin.toolkit.properties.AbstractProperties;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
public class FakeplayerProperties extends AbstractProperties {

    public final static FakeplayerProperties instance = new FakeplayerProperties(
            Main.getInstance(),
            "1"
    );

    private int maximum;

    public FakeplayerProperties(@NotNull JavaPlugin plugin, @NotNull String version) {
        super(plugin, version);
    }

    @Override
    protected void reload(@NotNull FileConfiguration file) {
        this.maximum = file.getInt("maximum", 1);
    }

}
