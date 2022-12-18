package net.xiaoyu233.mitemod.miteite.util;

public class WorldUtil {
    public static boolean isBloodMoonDay(long unadjustedTick) {
        long day = unadjustedTick / 24000L + 1L;
        return day % 16L == 0L;
    }
}
