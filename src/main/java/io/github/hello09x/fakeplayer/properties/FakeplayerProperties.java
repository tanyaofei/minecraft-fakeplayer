package io.github.hello09x.fakeplayer.properties;


import io.github.hello09x.fakeplayer.Main;
import io.github.tanyaofei.plugin.toolkit.properties.AbstractProperties;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@ToString
public class FakeplayerProperties extends AbstractProperties {

    public final static FakeplayerProperties instance = new FakeplayerProperties(
            Main.getInstance(),
            "6"
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
     * 命名模版
     */
    private String nameTemplate;

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
     * 准备命令
     */
    private List<String> preparingCommands;

    /**
     * 销毁命令
     */
    private List<String> destroyCommands;

    /**
     * 模拟登陆
     */
    private boolean simulateLogin;

    /**
     * UUID 种子
     */
    private String uuidSeed;

    /**
     * 距离
     */
    private int distance;

    /**
     * 防窒息
     */
    private boolean avoidSuffocation;

    public FakeplayerProperties(@NotNull JavaPlugin plugin, @NotNull String version) {
        super(plugin, version);
    }

    private static int maxIfZero(int value) {
        return value == 0 ? Integer.MAX_VALUE : value;
    }

    private static String defaultSeed() {
        return String.valueOf(Bukkit.getWorlds().get(0).getSeed());
    }

    @Override
    protected void reload(@NotNull FileConfiguration file) {
        this.playerLimit = maxIfZero(file.getInt("player-limit", 1));
        this.serverLimit = maxIfZero(file.getInt("server-limit", 1000));
        this.tickPeriod = file.getLong("tick-period", 1);
        this.followQuiting = file.getBoolean("follow-quiting", true);
        this.detectIp = file.getBoolean("detect-ip", false);
        this.kaleTps = file.getInt("kale-tps", 10);
        this.preparingCommands = file.getStringList("preparing-commands");
        this.destroyCommands = file.getStringList("destroy-commands");
        this.nameTemplate = file.getString("name-template", "");
        this.simulateLogin = file.getBoolean("simulate-login", false);
        this.uuidSeed = file.getString("uuid-seed", "");
        this.distance = file.getInt("distance", 0);
        this.avoidSuffocation = file.getBoolean("avoid-suffocation", false);

        if (uuidSeed.isBlank()) {
            uuidSeed = defaultSeed();
        }
    }

}
