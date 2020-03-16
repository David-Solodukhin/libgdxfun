package com.dookin;

/**
 * Created by David on 3/18/2018.
 */

public class Utils {
    public static final float PPM = 32.0f;
    public static float p2m(float p) {
        return p / PPM;
    }
    public static float m2p(float m) {
        return m * PPM;
    }
}
