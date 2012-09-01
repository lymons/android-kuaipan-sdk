package com.ccst.kuaipan.database;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public final class Settings {
    private static final String DATA_NAME = "kuaipan.xmls";

    public enum Field {
        TOKENPAIR_KEY,
        TOKENPAIR_SECRET,
        USER_NAME,
        IS_LOGIN
    };
    
    public static int getSettingInt(Context context, Field field,
            int defaultValue) {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getInt(
            field.name(), defaultValue);
    }

    public static void setSettingInt(Context context, Field field, int value) {
        Editor sortOrder = context.getSharedPreferences(DATA_NAME,
            Context.MODE_PRIVATE).edit();
        sortOrder.putInt(field.name(), value);
        sortOrder.commit();
    }

    public static String getSettingString(Context context, Field field,
            String defaultValue) {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getString(
            field.name(), defaultValue);
    }

    public static void setSettingString(Context context, Field field,
            String value) {
        Editor sortOrder = context.getSharedPreferences(DATA_NAME,
            Context.MODE_PRIVATE).edit();
        sortOrder.putString(field.name(), value);
        sortOrder.commit();
    }

    public static long getSettingLong(Context context, Field field,
            long defaultValue) {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getLong(
            field.name(), defaultValue);
    }

    public static void setSettingLong(Context context, Field field, long value) {
        Editor sortOrder = context.getSharedPreferences(DATA_NAME,
            Context.MODE_PRIVATE).edit();
        sortOrder.putLong(field.name(), value);
        sortOrder.commit();
    }

    public static boolean getSettingBoolean(Context context, Field field,
            boolean defaultValue) {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getBoolean(
            field.name(), defaultValue);
    }

    public static void setSettingBoolean(Context context, Field field,
            boolean value) {
        Editor sortOrder = context.getSharedPreferences(DATA_NAME,
            Context.MODE_PRIVATE).edit();
        sortOrder.putBoolean(field.name(), value);
        sortOrder.commit();
    }
}
