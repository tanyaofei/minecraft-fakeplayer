package io.github.hello09x.fakeplayer.core.util;

public class Mth {

    /**
     * 将 num 以 base 向下取整
     * <ul>
     *     <li>3.0, 0.5 -> 3.0</li>
     *     <li>3.1, 0.5 -> 3.0</li>
     *     <li>3.6, 0.5 -> 3.5</li>
     * </ul>
     *
     * @param num  数
     * @param base 基数
     * @return 取整后的数
     */
    public static double floor(double num, double base) {
        if (num % base == 0) {
            return num;
        }
        return Math.floor(num / base) * base;
    }

    /**
     * 将一个数约束在范围以内
     * @param value 数
     * @param min 最小值
     * @param max 最大值
     * @return 约束后的数
     */
    public static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }


}
