package or.nevet.orboard;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.ConvertPixelUnits;
import or.nevet.orboard.general_helpers.ExternalStorage;
import or.nevet.orboard.general_helpers.SharedPreferencesStorage;
import or.nevet.orboard.keyboard_general_operation.AudioAndHapticFeedbackManager;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardGraphicCreation;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayoutManager;

public class OrBoardInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (SharedPreferencesStorage.getBoolean(Constants.sharedPreferencesName, Constants.shouldDeleteTextsPreference, true, this)) {
            ExternalStorage.AppDirectoryOperations.deleteFileFromAppDirectory(this, Constants.lastTextsFileName);
            SharedPreferencesStorage.saveBoolean(Constants.sharedPreferencesName, Constants.shouldDeleteTextsPreference, false, this);
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        float maxHeightToFontSizeRatio = getMaxHeightFontSizeRatio(ResourcesCompat.getFont(this, Constants.keysFontId));
        float fontSizeToMaxHeightRatio = 1f/maxHeightToFontSizeRatio;
        SharedPreferencesStorage.saveFloat(Constants.sharedPreferencesName, Constants.fontSizeToMaxHeightRatioPreference, fontSizeToMaxHeightRatio, this);
        initializeScreenWidthAndHeight();
        KeyboardGraphicCreation graphicCreation = new KeyboardGraphicCreation(new KeyboardLayoutManager(getApplicationContext()));
        graphicCreation.calculateAndSaveKeyTextSizesForVerticalLayouts(ConvertPixelUnits.getPixelsFromPercentageOfScreenHeight(this, KeyboardGraphicCreation.getKeyboardHeightPercentageOfScreen(this), false), this);
        graphicCreation.calculateAndSaveKeyTextSizesForHorizontalLayouts(ConvertPixelUnits.getPixelsFromPercentageOfScreenHeight(this, KeyboardGraphicCreation.getKeyboardHeightPercentageOfScreen(this), true), this);
        graphicCreation.calculateAndSaveExtraSymbolsOptimalOrderingInPortrait(this);
        graphicCreation.calculateAndSaveExtraSymbolsOptimalOrderingInLandscape(this);
        AudioAndHapticFeedbackManager.init(this);
    }

    private void initializeScreenWidthAndHeight() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 1) {
            //portrait
            int portraitScreenWidthInPx = Resources.getSystem().getDisplayMetrics().widthPixels;
            int portraitScreenHeightInPx = Resources.getSystem().getDisplayMetrics().heightPixels;
            SharedPreferencesStorage.saveInt(Constants.sharedPreferencesName, Constants.portraitScreenWidthInPxPreference, portraitScreenWidthInPx, this);
            SharedPreferencesStorage.saveInt(Constants.sharedPreferencesName, Constants.portraitScreenHeightInPxPreference, portraitScreenHeightInPx, this);
        } else {
            //landscape
            int portraitScreenWidthInPx = Resources.getSystem().getDisplayMetrics().heightPixels;
            int portraitScreenHeightInPx = Resources.getSystem().getDisplayMetrics().widthPixels;
            SharedPreferencesStorage.saveInt(Constants.sharedPreferencesName, Constants.portraitScreenWidthInPxPreference, portraitScreenWidthInPx, this);
            SharedPreferencesStorage.saveInt(Constants.sharedPreferencesName, Constants.portraitScreenHeightInPxPreference, portraitScreenHeightInPx, this);
        }
    }

    //The ratio between each text size and each distance between the top and the bottom of the font metrics (the maximum height of a character in this font with this font size) is a constant value (at least for the roboto_regular font). In this method I find the ratio for a text of size 1, and return it (It is equal to the distance between the bottom and the top divided by 1, so it is just the distance). I can get the font size that fits the distance between the bottom and top of font metrics by multiplying the distance with the inverse of this ratio.
    private static float getMaxHeightFontSizeRatio(Typeface font) {
        Paint paint = new Paint();
        paint.setTypeface(font);
        //an arbitrary size. can be anything.
        paint.setTextSize(1);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return (metrics.bottom-metrics.top);
    }
}
