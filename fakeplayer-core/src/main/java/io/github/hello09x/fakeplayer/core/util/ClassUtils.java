package io.github.hello09x.fakeplayer.core.util;

/**
 * @author tanyaofei
 * @since 2024/8/12
 **/
public class ClassUtils {

    public static boolean isClassExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


}
