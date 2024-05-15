package or.nevet.orboard.general_helpers;

import android.content.Context;
import android.util.TypedValue;

public class ConvertPixelUnits {
    public static int getPixelsFromDp(float lengthInDp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lengthInDp, context.getResources().getDisplayMetrics());
    }

    public static int getPixelsFromInches(float lengthInInches, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, lengthInInches, context.getResources().getDisplayMetrics());
    }

    public static int getPixelsFromPercentageOfScreenHeight(Context context, int percentage, boolean isHorizontal) {
        if (!isHorizontal)
            return (int)(((double)percentage/100d)*(double) SharedPreferencesStorage.getInt(Constants.sharedPreferencesName, Constants.portraitScreenHeightInPxPreference, 0, context));
        else
            return (int)(((double)percentage/100d)*(double) SharedPreferencesStorage.getInt(Constants.sharedPreferencesName, Constants.portraitScreenWidthInPxPreference, 0, context));
    }
}
