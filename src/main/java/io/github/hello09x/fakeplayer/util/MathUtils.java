package io.github.hello09x.fakeplayer.util;

public class MathUtils {


    public static double round(double num, double base) {
        if (num % base == 0) {
            return num;
        }
        return Math.floor(num / base) * base;
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }


}
