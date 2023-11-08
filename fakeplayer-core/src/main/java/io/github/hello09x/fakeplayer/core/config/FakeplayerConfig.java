package io.github.hello09x.fakeplayer.core.config;


import io.github.hello09x.bedrock.config.Config;
import io.github.hello09x.fakeplayer.core.Main;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Getter
@ToString
public class FakeplayerConfig extends Config<FakeplayerConfig> {

    public final static FakeplayerConfig instance;

    private final static Logger log;

    private final static String defaultNameChars = "^[a-zA-Z0-9_]+$";

    static {
        log = Main.getInstance().getLogger();
        instance = new FakeplayerConfig(
                Main.getInstance(),
                "13"
        );
    }


    /**
     * 每位玩家最多多少个假人
     */
    private int playerLimit;

    /**
     * 服务器最多多少个假人
     */
    private int serverLimit;

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
     * 自执行命令
     */
    private List<String> selfCommands;

    /**
     * 销毁命令
     */
    private List<String> destroyCommands;

    /**
     * 退出时是否丢弃背包物品
     */
    private boolean dropInventoryOnQuiting;

    /**
     * 死亡时是否踢出游戏
     */
    private boolean kickOnDead;

    /**
     * 自定义名称规则
     */
    private Pattern namePattern;

    /**
     * 检测更新
     */
    private boolean checkForUpdates;

    /**
     * 允许执行的命令
     */
    private Set<String> allowCommands;

    /**
     * 默认假人存活时间
     */
    @Nullable
    private Duration lifespan;

    public FakeplayerConfig(@NotNull JavaPlugin plugin, @NotNull String version) {
        super(plugin, version);
        reload(false);
    }

    private static int maxIfZero(int value) {
        return value <= 0 ? Integer.MAX_VALUE : value;
    }

    @Override
    protected void reload(@NotNull FileConfiguration file) {
        this.playerLimit = maxIfZero(file.getInt("player-limit", 1));
        this.serverLimit = maxIfZero(file.getInt("server-limit", 1000));
        this.followQuiting = file.getBoolean("follow-quiting", true);
        this.detectIp = file.getBoolean("detect-ip", false);
        this.kaleTps = file.getInt("kale-tps", 0);
        this.selfCommands = file.getStringList("self-commands");
        this.preparingCommands = file.getStringList("preparing-commands");
        this.destroyCommands = file.getStringList("destroy-commands");
        this.nameTemplate = file.getString("name-template", "");
        this.dropInventoryOnQuiting = file.getBoolean("drop-inventory-on-quiting", true);
        this.kickOnDead = file.getBoolean("kick-on-dead", true);
        this.checkForUpdates = file.getBoolean("check-for-updates", true);
        this.namePattern = getNamePattern(file);
        this.nameTemplate = getNameTemplate(file);
        this.lifespan = getLifespan(file);
        this.allowCommands = file.getStringList("allow-commands")
                .stream()
                .map(c -> c.startsWith("/") ? c.substring(1) : c)
                .filter(c -> !c.isBlank())
                .collect(Collectors.toSet());
    }

    private @Nullable Duration getLifespan(@NotNull FileConfiguration file) {
        var minutes = file.getLong("lifespan");
        if (minutes <= 0) {
            return null;
        }
        return Duration.ofMinutes(minutes);
    }


    private @NotNull Pattern getNamePattern(@NotNull FileConfiguration file) {
        try {
            return Pattern.compile(file.getString("name-pattern", defaultNameChars));
        } catch (PatternSyntaxException e) {
            log.warning("Invalid name-pattern: " + file.getString("name-chars"));
            return Pattern.compile(defaultNameChars);
        }
    }

    private @NotNull String getNameTemplate(@NotNull FileConfiguration file) {
        var tmpl = file.getString("name-template", "");
        if (tmpl.startsWith("-") || tmpl.startsWith("@")) {
            log.warning("Invalid name template: " + this.nameTemplate);
            return "";
        }
        return tmpl;
    }

}
