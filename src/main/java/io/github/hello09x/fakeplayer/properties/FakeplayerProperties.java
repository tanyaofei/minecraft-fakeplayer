package io.github.hello09x.fakeplayer.properties;


import io.github.hello09x.fakeplayer.Main;
import io.github.tanyaofei.plugin.toolkit.properties.AbstractProperties;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Getter
@ToString
public class FakeplayerProperties extends AbstractProperties<FakeplayerProperties> {

    public final static FakeplayerProperties instance;
    private final static Logger log;

    private final static String defaultNameChars = "^[a-zA-Z0-9_]+$";

    static {
        log = Main.getInstance().getLogger();
        instance = new FakeplayerProperties(
                Main.getInstance(),
                "10"
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
     * 模拟登陆
     */
    private boolean simulateLogin;

    /**
     * 退出时是否丢弃背包物品
     */
    private boolean dropInventoryOnQuiting;

    /**
     * 自定义名称规则
     */
    private Pattern customNamePattern;

    /**
     * 检测更新
     */
    private boolean checkForUpdates;

    public FakeplayerProperties(@NotNull JavaPlugin plugin, @NotNull String version) {
        super(plugin, version);
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
        this.simulateLogin = file.getBoolean("simulate-login", false);
        this.dropInventoryOnQuiting = file.getBoolean("drop-inventory-on-quiting", true);
        this.checkForUpdates = file.getBoolean("check-for-updates", true);
        try {
            this.customNamePattern = Pattern.compile(file.getString("name-chars", defaultNameChars));
        } catch (PatternSyntaxException e) {
            log.warning("name-chars 不是一个合法的正则表达式, 该配置不会生效: " + file.getString("name-chars"));
            this.customNamePattern = Pattern.compile(defaultNameChars);
        }

        if (this.nameTemplate.startsWith("-")) {
            log.warning("假人名称模版不能以 - 开头, 该配置不会生效: " + this.nameTemplate);
            this.nameTemplate = "";
        }
    }

}
