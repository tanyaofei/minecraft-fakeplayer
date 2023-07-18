package io.github.hello09x.fakeplayer.properties;


import io.github.hello09x.fakeplayer.Main;
import io.github.tanyaofei.plugin.toolkit.properties.AbstractProperties;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@ToString
public class FakeplayerProperties extends AbstractProperties {

    public final static FakeplayerProperties instance = new FakeplayerProperties(
            Main.getInstance(),
            "2"
    );

    /**
     * 每位玩家最多多少个假人
     */
    private int playerLimit;

    /**
     * 服务器最多多少个假人
     */
    private int serverLimit;

    /**
     * 动作更新间隔
     */
    private long tickPeriod;

    /**
     * 创建者玩家下线时是否跟随下线
     */
    private boolean followQuiting;

    /**
     * 是否探测 IP
     */
    private boolean detectIp;

    /**
     * 服务器 tps 低于这个值移除所有假人
     */
    private int kaleTps;

    /**
     * 胡言乱语
     */
    private List<String> nonsense;

    public FakeplayerProperties(@NotNull JavaPlugin plugin, @NotNull String version) {
        super(plugin, version);
    }

    @Override
    protected void reload(@NotNull FileConfiguration file) {
        this.playerLimit = maxIfZero(file.getInt("player-limit", 1));
        this.serverLimit = maxIfZero(file.getInt("server-limit", 1000));
        this.tickPeriod = file.getLong("tick-period", 1);
        this.followQuiting = file.getBoolean("follow-quiting", true);
        this.detectIp = file.getBoolean("detect-ip", false);
        this.kaleTps = file.getInt("kale-tps", 10);
        this.nonsense = file.getStringList("nonsense");
    }

    private static int maxIfZero(int value) {
        return value == 0 ? Integer.MAX_VALUE : value;
    }

}
