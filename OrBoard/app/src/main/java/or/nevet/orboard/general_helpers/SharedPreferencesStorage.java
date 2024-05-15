package or.nevet.orboard.general_helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesStorage {
    public static void saveBoolean(String databaseName, String preferenceName, boolean value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceName, value);
        editor.apply();
    }

    public static void saveString(String databaseName, String preferenceName, String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, value);
        editor.apply();
    }

    public static void saveInt(String databaseName, String preferenceName, int value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(preferenceName, value);
        editor.apply();
    }

    public static void saveLong(String databaseName, String preferenceName, long value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(preferenceName, value);
        editor.apply();
    }

    public static void saveFloat(String databaseName, String preferenceName, float value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(preferenceName, value);
        editor.apply();
    }

    public static boolean getBoolean(String databaseName, String preferenceName, boolean defaultValue, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(preferenceName, defaultValue);
    }

    public static String getString(String databaseName, String preferenceName, String defaultValue, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public static int getInt(String databaseName, String preferenceName, int defaultValue, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(preferenceName, defaultValue);
    }

    public static long getLong(String databaseName, String preferenceName, long defaultValue, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(preferenceName, defaultValue);
    }

    public static float getFloat(String databaseName, String preferenceName, float defaultValue, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(preferenceName, defaultValue);
    }

    public static void clearDatabase(String databaseName, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
