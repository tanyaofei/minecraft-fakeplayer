package io.github.hello09x.fakeplayer.api.spi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface NMSServerPlayer {


    /**
     * @return 返回 bukkit 的 Player 对象
     */
    @NotNull Player getPlayer();

    /**
     * @return X 坐标
     */
    double getX();

    /**
     * @return Y 坐标
     */
    double getY();

    /**
     * @return Z 坐标
     */
    double getZ();

    /**
     * 设置 X 坐标偏移值
     *
     * @param xo 偏移值
     */
    void setXo(double xo);

    /**
     * 设置 Y 坐标偏移值
     *
     * @param yo 偏移值
     */
    void setYo(double yo);

    /**
     * 设置 Z 坐标偏移值
     *
     * @param zo 偏移值
     */
    void setZo(double zo);

    /**
     * 执行时刻运算
     */
    void doTick();

    /**
     * 移动玩家
     *
     * @param x    X 坐标
     * @param y    Y 坐标
     * @param z    Z 坐标
     * @param yRot 头部 Y 角度
     * @param xRot 头部 X 角度
     */
    void absMoveTo(double x, double y, double z, float yRot, float xRot);

    /**
     * 获取头部 Y 角度
     *
     * @return Y 角度
     */
    float getYRot();

    /**
     * 设置头部 Y 角度
     *
     * @param yRot Y 角度
     */
    void setYRot(float yRot);

    /**
     * 获取头部 X 角度
     *
     * @return 头部 X 角度
     */
    float getXRot();

    /**
     * 设置头部 X 角度
     *
     * @param xRot 头部 X 角度
     */
    void setXRot(float xRot);

    /**
     * 设置 Z 坐标移动
     *
     * @param zza 移动距离
     */
    void setZza(float zza);

    /**
     * 设置 X 坐标移动
     *
     * @param xxa 与第三双重
     */
    void setXxa(float xxa);

    /**
     * 骑上实体
     *
     * @param entity 实体
     * @param force  是否强制
     * @return 是否骑上
     */
    boolean startRiding(@NotNull Entity entity, boolean force);

    /**
     * 取消骑行实体
     */
    void stopRiding();

    /**
     * 设置不保存成就数据
     *
     * @param plugin 插件
     */
    void disableAdvancements(@NotNull Plugin plugin);

    /**
     * 从另外一名玩家里复制皮肤
     *
     * @param from 另外一名玩家
     */
    void copyTexture(@NotNull Player from);

    /**
     * 获取时刻计数
     *
     * @return 时刻计数
     */
    int getTickCount();

    /**
     * 丢弃物品
     *
     * @param slot  槽位
     * @param flag
     * @param flag1
     */
    void drop(int slot, boolean flag, boolean flag1);

    /**
     * 丢弃物品
     *
     * @param allStack 是否丢弃整组
     */
    void drop(boolean allStack);

    /**
     * 重设最后活跃时间
     */
    void resetLastActionTime();

    /**
     * 判断是否在地面
     *
     * @return 是否在地面
     */
    boolean onGround();

    /**
     * 从地面跳起
     */
    void jumpFromGround();

    /**
     * 设置是否跳跃中
     *
     * @param jumping 是否条约中
     */
    void setJumping(boolean jumping);

    /**
     * 是否在使用物品
     *
     * @return 是否在使用物品
     */
    boolean isUsingItem();

    /**
     * 设置曾玩过这个服务器
     */
    void setPlayBefore();

    /**
     * 设置客户端选项
     */
    void configClientOptions();

    /**
     * 重生
     */
    void respawn();

    /**
     * 交换主副手物品
     */
    void swapItemWithOffhand();

}
