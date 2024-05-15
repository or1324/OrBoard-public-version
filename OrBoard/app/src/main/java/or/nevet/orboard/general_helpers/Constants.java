package or.nevet.orboard.general_helpers;

import or.nevet.orboard.R;

public class Constants {
    public static final String sharedPreferencesName = "sharedPrefs";
    public static final String keyboardHeightPreference = "keyboardHeightPercentageOfScreen";
    public static final String portraitScreenWidthInPxPreference = "portraitScreenWidthInPx";
    public static final String portraitScreenHeightInPxPreference = "portraitScreenHeightInPx";
    public static final String fontSizeToMaxHeightRatioPreference = "fontSizeToMaxHeightRatio";
    public static final String lastSubtypeIndexPreference = "lastSubtypeIndex";
    public static final String shouldDeleteTextsPreference = "shouldDeleteTexts";
    public static final String keyTextSizesForVerticalLayoutsFileName = "keyTextSizesForVerticalLayouts";
    public static final String keyTextSizesForHorizontalLayoutsFileName = "keyTextSizesForHorizontalLayouts";
    public static final String extraSymbolsOrderingForVerticalLayoutsFileName = "extraSymbolsOrderingForVerticalLayouts";
    public static final String extraSymbolsOrderingForHorizontalLayoutsFileName = "extraSymbolsOrderingForHorizontalLayouts";
    public static final String clipboardFileName = "clipbOrd";
    public static final String lastTextsFileName = "lastTexts.txt";
    public static final String keyboardBackgroundFileName = "background.obj";
    public static final String englishLanguageTag = "en-US";
    public static final String hebrewLanguageTag = "he-IL";
    public static final String russianLanguageTag = "ru-RU";
    //special characters: E = Enter, S = Space, C = Caps Lock, D = Delete, R = Repeat text, N = next layout
    public static final Character[] specialCharacters = {'E', 'S', 'C', 'D', 'R', 'N'};
    public static final int keyboardBackgroundId = R.drawable.keyboard_background;
    public static final int keysFontId = R.font.roboto_regular;
    public static final int keyboardPadding = 2;
    public static final int keyOuterPadding = 2;
    public static final int keyInnerPadding = 2;
    public static final int specialSymbolsButtonPadding = 5;
    public static final float optionsHeightInInches = 0.38f;
    public static final int numOfSpecialButtons = 2;
    public static final double heightWidthRatioOfSpecialButtons = 1.4d;
    public static final boolean forDaniella = false;

}
