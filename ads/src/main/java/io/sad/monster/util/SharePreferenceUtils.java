package io.sad.monster.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class SharePreferenceUtils {
    private final static String PREF_NAME = "apero_ad_pref";
    private final static String KEY_INSTALL_TIME = "KEY_INSTALL_TIME";
    private final static String KEY_CURRENT_TOTAL_REVENUE_AD = "KEY_CURRENT_TOTAL_REVENUE_AD";
    private final static String KEY_PUSH_EVENT_REVENUE_3_DAY = "KEY_PUSH_EVENT_REVENUE_3_DAY";
    private final static String KEY_PUSH_EVENT_REVENUE_7_DAY = "KEY_PUSH_EVENT_REVENUE_7_DAY";
    private static final String KEY_IS_PURCHASE = "KEY_IS_PURCHASE";
    private static final String KEY_ITEM_SET_ITEM_WATCHED_AD = "KEY_ITEM_SET_ITEM_WATCHED_AD";
    private static final String PREF_IS_SHOW_DIALOG_ADS_EXP = "PREF_IS_SHOW_DIALOG_ADS_EXP";
    private static final String IS_SELECTED_MANAGE_OPTION = "IS_SELECTED_MANAGE_OPTION";
    private static final String NUMBER_OPEN_APP = "NUMBER_OPEN_APP";
    private static final String MEMORY_RAM_DEVICE = "MEMORY_RAM_DEVICE";
    private static final String LIST_IMAGE_OF_FOLDER = "LIST_IMAGE_OF_FOLDER";
    private static final String CURRENT_IMAGE_SHOW = "CURRENT_IMAGE_SHOW";
    private static final String TIME_FIRST_SET = "TIME_FIRST_SET";
    private static final String PERCENT_BLUR = "PERCENT_BLUR";
    private static final String PERCENT_DARK = "PERCENT_DARK";
    private static final String SYSTEM_DARK = "SYSTEM_DARK";
    private static final String TIME_SET_CHANGER = "TIME_SET_CHANGER";
    private static final String LIST_ID_WATCHED_AD = "LIST_ID_WATCHED_AD";
    private static final String IS_STATE_TURN_ON_SET_AUTO = "IS_STATE_TURN_ON_SET_AUTO";
    private static final String LIST_CAT_OF_MIX = "LIST_CAT_OF_MIX";
    private static final String COUNT_NUMBER_SAVE_IMAGE = "COUNT_NUMBER_SAVE_IMAGE";
    private static final String PREF_BATTERY = "PREF_BATTERY";
    private static final String PREF_ZOOM_OUT_ON_UNLOCK = "PREF_ZOOM_OUT_ON_UNLOCK";
    private static final String PREF_INVERT_AXIS = "PREF_INVERT_AXIS";
    private static final String PREF_SENSITIVE = "PREF_SENSITIVE";
    private static final String PREF_FIRST_WELCOME = "PREF_FIRST_WELCOME";
    private static final String PREF_SHOW_TUTORIAL = "PREF_SHOW_TUTORIAL";
    private static final String PREF_LIST_RE_COMMENT_SEARCH = "PREF_LIST_RE_COMMENT_SEARCH";
    private static final String PREF_URL_IMAGE_CURRENT_WALLPAPER = "PREF_URL_IMAGE_CURRENT_WALLPAPER";
    private static final String PREF_IMAGE_SET_HOME_OR_LOCK_OR_ALL = "PREF_IMAGE_SET_HOME_OR_LOCK_OR_ALL";

    public static void setPrefUrlImageCurrentWallpaper(Context context, String value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putString(PREF_URL_IMAGE_CURRENT_WALLPAPER, value).apply();
    }
    public static String getPrefUrlImageCurrentWallpaper(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getString(PREF_URL_IMAGE_CURRENT_WALLPAPER,"");
    }
    public static void setPrefImageSetHomeOrLockOrAll(Context context,int value){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        pref.edit().putInt(PREF_IMAGE_SET_HOME_OR_LOCK_OR_ALL, value).apply();
    }
    public static int getPrefImageSetHomeOrLockOrAll(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getInt(PREF_IMAGE_SET_HOME_OR_LOCK_OR_ALL,0);
    }
    public static void setPrefListReCommentSearch(Context context, String value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putString(PREF_LIST_RE_COMMENT_SEARCH, value).apply();
    }
    public static String getPrefListReCommentSearch(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getString(PREF_LIST_RE_COMMENT_SEARCH,"");
    }

    public static void setPrefShowTutorial(Context context, boolean value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(PREF_SHOW_TUTORIAL, value).apply();
    }
    public static boolean getPrefShowTutorial(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(PREF_SHOW_TUTORIAL,false);
    }


    public static void setPrefFirstWelcome(Context context, boolean value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(PREF_FIRST_WELCOME, value).apply();
    }
    public static boolean getPrefFirstWelcome(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(PREF_FIRST_WELCOME,false);
    }

    public static void setPrefSensitive(Context context, float value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putFloat(PREF_SENSITIVE, value).apply();
    }
    public static Float getPrefSensitive(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getFloat(PREF_SENSITIVE,50f);
    }

    public static void setPrefInvertAxis(Context context, boolean value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(PREF_INVERT_AXIS, value).apply();
    }
    public static boolean getPrefInvertAxis(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(PREF_INVERT_AXIS,false);
    }

    public static void setPrefZoomOutOnUnlock(Context context, boolean value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(PREF_ZOOM_OUT_ON_UNLOCK, value).apply();
    }
    public static boolean getPrefZoomOutOnUnlock(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(PREF_ZOOM_OUT_ON_UNLOCK,false);
    }

    public static void setPrefBattery(Context context, boolean value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(PREF_BATTERY, value).apply();
    }
    public static boolean getPrefBattery(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(PREF_BATTERY,true);
    }

    public static void setCountNumberSaveImage(Context context,int value){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        pref.edit().putInt(COUNT_NUMBER_SAVE_IMAGE, value).apply();
    }
    public static int getCountNumberSaveImage(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getInt(COUNT_NUMBER_SAVE_IMAGE,0);
    }
    public static void setListCatOfMix(Context context, String value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putString(LIST_CAT_OF_MIX, value).apply();
    }
    public static String getListCatOfMix(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getString(LIST_CAT_OF_MIX,"");
    }


    public static void setIsStateTurnOnSetAuto(Context context, boolean value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(IS_STATE_TURN_ON_SET_AUTO, value).apply();
    }
    public static boolean getIsStateTurnOnSetAuto(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(IS_STATE_TURN_ON_SET_AUTO,false);
    }

    public static void setSystemDark(Context context, boolean value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(SYSTEM_DARK, value).apply();
    }
    public static boolean getSystemDark(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(SYSTEM_DARK,false);
    }
    public static void setPercentBlur(Context context, long value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putLong(PERCENT_BLUR, value).apply();
    }
    public static long getPercentBlur(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getLong(PERCENT_BLUR,0);
    }

    public static void setPercentDark(Context context, long value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putLong(PERCENT_DARK, value).apply();
    }
    public static long getPercentDark(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getLong(PERCENT_DARK,0);
    }

    public static void setListIdWallpaper(Context context, String value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putString(LIST_ID_WATCHED_AD, value).apply();
    }
    public static String getListIdWallpaper(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getString(LIST_ID_WATCHED_AD,"");
    }
    public static void setTimeSetChanger(Context context, String value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putString(TIME_SET_CHANGER, value).apply();
    }
    public static String getTimeSetChanger(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getString(TIME_SET_CHANGER,"");
    }

    public static void setTimeSetWallpaper(Context context, long value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putLong(TIME_FIRST_SET, value).apply();
    }
    public static long getTimeSetWallpaper(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getLong(TIME_FIRST_SET,-1L);
    }
    public static void setImageCurrent(Context context,int value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putInt(CURRENT_IMAGE_SHOW, value).apply();
    }
    public static int getImageCurrent(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getInt(CURRENT_IMAGE_SHOW,0);
    }
    public static void setListImageOfFolder(Context context,String value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putString(LIST_IMAGE_OF_FOLDER, value).apply();
    }

    public static String getListImageOfFolder(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getString(LIST_IMAGE_OF_FOLDER,"");
    }

    public static int getRamMemory(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getInt(MEMORY_RAM_DEVICE, 0);
    }

    public static void setRamMemory(Context context,int value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putInt(MEMORY_RAM_DEVICE, value).apply();
    }

    public static int getOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getInt(NUMBER_OPEN_APP, 0);
    }

    public static void setOpenApp(Context context,int value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putInt(NUMBER_OPEN_APP, value).apply();
    }

    public static Set<String> getPrefItemSetWatchedAd(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getStringSet(KEY_ITEM_SET_ITEM_WATCHED_AD, new HashSet<>());
    }
    public static void putPrefItemSetWatchedAd(Context context, Set<String> list) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putStringSet(KEY_ITEM_SET_ITEM_WATCHED_AD, list).apply();
    }


    public static boolean getIsPurchase(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(KEY_IS_PURCHASE, false);
    }

    public static void putIsPurchase(Context context, boolean value) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(KEY_IS_PURCHASE, value).apply();
    }

    public static long getInstallTime(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getLong(KEY_INSTALL_TIME, 0);
    }

    public static void setInstallTime(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putLong(KEY_INSTALL_TIME, System.currentTimeMillis()).apply();
    }

    public static float getCurrentTotalRevenueAd(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getFloat(KEY_CURRENT_TOTAL_REVENUE_AD, 0);
    }

    public static void updateCurrentTotalRevenueAd(Context context, float revenue) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        float currentTotalRevenue = pre.getFloat(KEY_CURRENT_TOTAL_REVENUE_AD, 0);
        currentTotalRevenue += revenue / 1000000.0;
        pre.edit().putFloat(KEY_CURRENT_TOTAL_REVENUE_AD, currentTotalRevenue).apply();
    }

    public static boolean isPushRevenue3Day(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(KEY_PUSH_EVENT_REVENUE_3_DAY, false);
    }

    public static void setPushedRevenue3Day(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(KEY_PUSH_EVENT_REVENUE_3_DAY, true).apply();
    }

    public static boolean isPushRevenue7Day(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(KEY_PUSH_EVENT_REVENUE_7_DAY, false);
    }

    public static void setPushedRevenue7Day(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(KEY_PUSH_EVENT_REVENUE_7_DAY, true).apply();
    }
    public static void putPrefShowDialogAdsExp(Context context, boolean value) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(PREF_IS_SHOW_DIALOG_ADS_EXP, value).apply();
    }
    public static boolean getPrefShowedDialogAdsExp(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(PREF_IS_SHOW_DIALOG_ADS_EXP, true);
    }
    public static void putPrefIsSelectManageOptionsConsent(Context context, boolean value) {
        context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                ).edit()
                .putBoolean(IS_SELECTED_MANAGE_OPTION, value).apply();
    }
    public static boolean getPrefIsSelectManageOptionsConsent(Context context) {
        return context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        ).getBoolean(IS_SELECTED_MANAGE_OPTION, false);
    }
}
