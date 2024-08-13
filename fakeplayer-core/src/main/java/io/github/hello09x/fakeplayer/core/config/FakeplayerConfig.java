package io.github.hello09x.fakeplayer.core.config;


import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.config.ConfigUtils;
import io.github.hello09x.devtools.core.config.PluginConfig;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.translatable;

@Getter
@ToString
@Singleton
public class FakeplayerConfig extends PluginConfig {

    private final static Logger log = Main.getInstance().getLogger();

    private final static String defaultNameChars = "^[a-zA-Z0-9_]+$";

    public final static String SECTION_KEY_DEFAULT_FEATURES = "default-features";

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
     * 创建前执行命令
     */
    private List<String> preSpawnCommands;

    /**
     * 创建时执行命令
     */
    private List<String> postSpawnCommands;

    /**
     * 创建后执行命令
     */
    private List<String> afterSpawnCommands;

    /**
     * 退出前执行命令
     */
    private List<String> postQuitCommands;

    /**
     * 退出后命令
     */
    private List<String> afterQuitCommands;

    /**
     * 自执行命令
     */
    private List<String> selfCommands;

    /**
     * 退出时是否丢弃背包物品
     */
    private boolean dropInventoryOnQuiting;

    /**
     * 是否保存假人存档
     */
    private boolean persistData;

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
    @Deprecated
    private Set<String> allowCommands;

    /**
     * 默认假人存活时间
     */
    @Nullable
    private Duration lifespan;

    /**
     * 开发者调试模式
     */
    private boolean debug;

    /**
     * 防止踢出
     */
    private PreventKicking preventKicking;

    /**
     * invsee 实现方式
     */
    private InvseeImplement invseeImplement;

    /**
     * 真实皮肤
     */
    @Beta
    private boolean defaultOnlineSkin;

    private Map<Feature, String> defaultFeatures;

    @Inject
    public FakeplayerConfig() {
        super(Main.getInstance());
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
        this.preSpawnCommands = file.getStringList("pre-spawn-commands");
        this.postSpawnCommands = file.getStringList("post-spawn-commands");
        this.afterSpawnCommands = file.getStringList("after-spawn-commands");
        this.postQuitCommands = file.getStringList("post-quit-commands");
        this.afterQuitCommands = file.getStringList("after-quit-commands");
        this.nameTemplate = file.getString("name-template", "");
        this.dropInventoryOnQuiting = file.getBoolean("drop-inventory-on-quiting", true);
        this.persistData = file.getBoolean("persist-data", true);
        this.kickOnDead = file.getBoolean("kick-on-dead", true);
        this.checkForUpdates = file.getBoolean("check-for-updates", true);
        this.namePattern = getNamePattern(file);
        this.preventKicking = this.getPreventKicking(file);
        this.nameTemplate = getNameTemplate(file);
        this.lifespan = getLifespan(file);
        this.allowCommands = file.getStringList("allow-commands")
                                 .stream()
                                 .map(c -> c.startsWith("/") ? c.substring(1) : c)
                                 .filter(c -> !c.isBlank())
                                 .collect(Collectors.toSet());

        this.defaultOnlineSkin = file.getBoolean("default-online-skin", false);
        this.defaultFeatures = Arrays.stream(Feature.values())
                                     .collect(Collectors.toMap(Function.identity(), key -> file.getString("default-features." + key.name(), key.getDefaultOption())));
        this.invseeImplement = ConfigUtils.getEnum(file, "invsee-implement", InvseeImplement.class, InvseeImplement.AUTO);
        this.debug = file.getBoolean("debug", false);

        if (this.isConfigFileOutOfDate()) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (Main.getInstance().isEnabled()) {
                    Main.getInstance().getComponentLogger().warn(translatable("fakeplayer.configuration.out-of-date"));
                }
            }, 1);
        }

        if (!this.allowCommands.isEmpty()) {
            log.warning("allow-commands is deprecated which will be removed at 0.4.0, you should use Permissions Plugin to assign permission groups to fake players.");
        }

        var preparingCommands = file.getStringList("preparing-commands");
        if (!preparingCommands.isEmpty()) {
            log.warning("preparing-commands is deprecated, use post-spawn-commands instead.");
            this.postSpawnCommands.addAll(preparingCommands);
        }

        var destroyCommands = file.getStringList("destroy-commands");
        if (!destroyCommands.isEmpty()) {
            log.warning("destroy-commands is deprecated, use post-quit-commands instead.");
            this.postQuitCommands.addAll(destroyCommands);
        }

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

    private @NotNull PreventKicking getPreventKicking(@NotNull FileConfiguration file) {
        if (file.getBoolean("prevent-kicked-on-spawning", false)) {
            log.warning("prevent-kicked-on-spawning is deprecated which will be removed at 0.4.0, use prevent-kick instead");
            return PreventKicking.ON_SPAWNING;
        }

        return ConfigUtils.getEnum(file, "prevent-kicking", PreventKicking.class, PreventKicking.ON_SPAWNING);
    }

}
