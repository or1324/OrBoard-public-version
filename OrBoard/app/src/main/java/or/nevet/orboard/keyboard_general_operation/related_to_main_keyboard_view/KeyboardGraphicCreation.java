package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

import static or.nevet.orboard.general_helpers.Constants.optionsHeightInInches;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import java.io.IOException;
import java.util.HashMap;

import or.nevet.orboard.general_helpers.ConvertPixelUnits;
import or.nevet.orboard.general_helpers.ExternalStorage;
import or.nevet.orboard.general_helpers.Pair;
import or.nevet.orboard.general_helpers.SharedPreferencesStorage;
import or.nevet.orboard.general_helpers.Constants;

public class KeyboardGraphicCreation {
    private int lastKeyboardRow = 0;
    private final KeyboardLayoutManager keyboardLayoutManager;
    public KeyboardGraphicCreation(KeyboardLayoutManager keyboardLayoutManager) {
        this.keyboardLayoutManager = keyboardLayoutManager;
    }

    public void reset(Keyboard keyboard) {
        lastKeyboardRow = 0;
        keyboard.removeAllViews();
    }

    public Key createKeyGraphics(Keyboard keyboard, char currentChar, int row, KeyboardLayout currentLayout, float keyTextSize) {
        LinearLayout currentRow = keyboard.getKeyboardRows()[row];
        String text = currentLayout.getKeyText(currentChar);
        Key key = new Key(keyboard.getContext(), text, keyTextSize, currentChar);
        key.setId(View.generateViewId());
        key.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
        ((LinearLayout.LayoutParams) key.getLayoutParams()).weight = currentLayout.getLayoutWeightFrom1000(currentChar);
        currentRow.addView(key);
        return key;
    }

    public void initializeRows(Keyboard keyboard) {
        LinearLayout[] keyboardRows = keyboard.getKeyboardRows();
        //create all rows
        for (int rowIndex = 0; rowIndex < keyboardRows.length; rowIndex++) {
            LinearLayout currentRow = createKeyboardRow(keyboard);
            initializeRowHorizontally(keyboard, currentRow);
        }

        //position first row
        LinearLayout firstRow = keyboardRows[0];
        LinearLayout secondRow = keyboardRows[1];
        ConstraintSet firstSet = new ConstraintSet();
        firstSet.clone(keyboard);
        firstSet.connect(firstRow.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        firstSet.connect(firstRow.getId(), ConstraintSet.BOTTOM, secondRow.getId(), ConstraintSet.TOP);
        firstSet.applyTo(keyboard);

        //position all rows except last
        for (int rowIndex = 1; rowIndex < keyboardRows.length-1; rowIndex++) {
            LinearLayout previousRow = keyboardRows[rowIndex-1];
            LinearLayout currentRow = keyboardRows[rowIndex];
            LinearLayout nextRow = keyboardRows[rowIndex+1];
            ConstraintSet currentSet = new ConstraintSet();
            currentSet.clone(keyboard);
            currentSet.connect(currentRow.getId(), ConstraintSet.TOP, previousRow.getId(), ConstraintSet.BOTTOM);
            currentSet.connect(currentRow.getId(), ConstraintSet.BOTTOM, nextRow.getId(), ConstraintSet.TOP);
            currentSet.applyTo(keyboard);
        }
        //position last row
        LinearLayout previousRow = keyboardRows[keyboardRows.length-2];
        LinearLayout lastRow = keyboardRows[keyboardRows.length-1];
        ConstraintSet lastSet = new ConstraintSet();
        lastSet.clone(keyboard);
        lastSet.connect(lastRow.getId(), ConstraintSet.TOP, previousRow.getId(), ConstraintSet.BOTTOM);
        lastSet.connect(lastRow.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        lastSet.applyTo(keyboard);
    }

    private void initializeRowHorizontally(Keyboard keyboard, LinearLayout row) {
        ConstraintSet set = new ConstraintSet();
        set.clone(keyboard);
        set.connect(row.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.connect(row.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        set.applyTo(keyboard);
    }

    private LinearLayout createKeyboardRow(Keyboard keyboard) {
        LinearLayout linearLayout = new LinearLayout(keyboard.getContext());
        linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        linearLayout.setWeightSum(1000);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setId(View.generateViewId());
        linearLayout.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
        keyboard.addView(linearLayout);
        keyboard.setKeyboardRowAt(linearLayout, lastKeyboardRow++);
        return linearLayout;
    }

    /** Returns the maximum font size so that the text still fits the width given, with the constraint that the font size is smaller than the given font size. **/
    private static float getTextSizeToFitWidthWithMaxValueOf(String keyText, int keyWidth, float maxKeyTextSize, Context context) {
        float currentKeyTextSize = maxKeyTextSize;
        Paint paint = new Paint();
        paint.setTypeface(ResourcesCompat.getFont(context, Constants.keysFontId));
        paint.setTextSize(currentKeyTextSize);
        float currentKeyTextWidth = paint.measureText(keyText);
        while (currentKeyTextWidth >= keyWidth) {
            currentKeyTextSize-=0.5f;
            paint.setTextSize(currentKeyTextSize);
            currentKeyTextWidth = paint.measureText(keyText);
        }
        return currentKeyTextSize;
    }

    //O(a*b*c) when a=numOfLayouts, b=numOfCharactersInEachLayout and c=TextSizeForHeight
    public void calculateAndSaveKeyTextSizesForVerticalLayouts(int keyboardHeightInPx, Context context) {
        int screenWidth = SharedPreferencesStorage.getInt(Constants.sharedPreferencesName, Constants.portraitScreenWidthInPxPreference, 0, context);
        Pair<Float, Float> keyTextSizesForMainRowsAndLastRow = calculateKeyTextSizesForLayoutsByScreenWidthAndKeyboardHeight(screenWidth, keyboardHeightInPx, context);
        ExternalStorage.AppDirectoryOperations.saveObjectToAppDirectory(context, Constants.keyTextSizesForVerticalLayoutsFileName, keyTextSizesForMainRowsAndLastRow);
    }

    public Pair<Float, Float> getSavedKeyTextSizesForVerticalLayouts(Context context) {
        try {
            return ExternalStorage.AppDirectoryOperations.restoreObjectFromAppDirectory(context, Constants.keyTextSizesForVerticalLayoutsFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //O(a*b*c) when a=numOfLayouts, b=numOfCharactersInEachLayout and c=TextSizeForHeight
    public void calculateAndSaveKeyTextSizesForHorizontalLayouts(int keyboardHeightInPx, Context context) {
        int screenWidth = SharedPreferencesStorage.getInt(Constants.sharedPreferencesName, Constants.portraitScreenHeightInPxPreference, 0, context);
        Pair<Float, Float> keyTextSizesForMainRowsAndLastRow = calculateKeyTextSizesForLayoutsByScreenWidthAndKeyboardHeight(screenWidth, keyboardHeightInPx, context);
        ExternalStorage.AppDirectoryOperations.saveObjectToAppDirectory(context, Constants.keyTextSizesForHorizontalLayoutsFileName, keyTextSizesForMainRowsAndLastRow);
    }

    public Pair<Float, Float> getSavedKeyTextSizesForHorizontalLayouts(Context context) {
        try {
            return ExternalStorage.AppDirectoryOperations.restoreObjectFromAppDirectory(context, Constants.keyTextSizesForHorizontalLayoutsFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** The screen width changes by whether it is vertical or horizontal. Returns the text sizes for the main rows and for the last row. **/
    private Pair<Float, Float> calculateKeyTextSizesForLayoutsByScreenWidthAndKeyboardHeight(int screenWidth, int keyboardHeightInPx, Context context) {
        float minMainSize = Float.MAX_VALUE;
        float minLastRowSize = Float.MAX_VALUE;
        for (KeyboardLayout layout : keyboardLayoutManager.getAllKeyboardLayouts()) {
            int numOfRows = layout.getNumOfButtonsInEachRow().length;
            int sumOfKeyPaddingInPx = ConvertPixelUnits.getPixelsFromDp(2*Constants.keyOuterPadding+2*Constants.keyInnerPadding, context);
            int sumOfKeyboardPaddingInPx = ConvertPixelUnits.getPixelsFromDp(2*Constants.keyboardPadding, context);
            int keysHeight = ((keyboardHeightInPx-sumOfKeyboardPaddingInPx)/numOfRows)-sumOfKeyPaddingInPx;
            float keysTextSizeToFitMaxHeight = keysHeight*SharedPreferencesStorage.getFloat(Constants.sharedPreferencesName, Constants.fontSizeToMaxHeightRatioPreference, 0, context);
            float currentMinMainSize = getMainKeyboardRowsTextSizeForKeyboardLayout(layout, screenWidth, sumOfKeyPaddingInPx, sumOfKeyboardPaddingInPx, keysTextSizeToFitMaxHeight, context);
            float currentMinLastRowSize = getLastKeyboardRowTextSizeForKeyboardLayout(layout, screenWidth, sumOfKeyPaddingInPx, sumOfKeyboardPaddingInPx, keysTextSizeToFitMaxHeight, context);
            minMainSize = Math.min(minMainSize, currentMinMainSize);
            minLastRowSize = Math.min(minLastRowSize, currentMinLastRowSize);
        }
        return new Pair<>(minMainSize, minLastRowSize);
    }

    /** Returns the largest text size so that all of the characters in the main rows of the keyboard layout will fit their key widths and heights. **/
    public float getMainKeyboardRowsTextSizeForKeyboardLayout(KeyboardLayout layout, int screenWidth, int sumOfKeyPaddingInPx, int sumOfKeyboardPaddingInPx, float keysTextSizeToFitMaxHeight, Context context) {
        float minTextSize = Float.MAX_VALUE;
        for (char c : layout.getMainCharacters()) {
            String keyText = layout.getKeyText(c);
            double keyLayoutWeight = layout.getLayoutWeightFrom1000(c);
            int keyWidth = (int)((double)(screenWidth - sumOfKeyboardPaddingInPx)*keyLayoutWeight/1000d)-sumOfKeyPaddingInPx;
            //calculating size for lower case characters
            float textSizeToFitWidthAndHeight = getTextSizeToFitWidthWithMaxValueOf(keyText, keyWidth, keysTextSizeToFitMaxHeight, context);
            //dealing with upper case characters
            if (layout.hasUpperCase() && !Keyboard.isSpecialChar(c))
                textSizeToFitWidthAndHeight = Math.min(textSizeToFitWidthAndHeight, getTextSizeToFitWidthWithMaxValueOf(keyText.toUpperCase(), keyWidth, keysTextSizeToFitMaxHeight, context));
            //updating minTextSize
            if (textSizeToFitWidthAndHeight < minTextSize)
                minTextSize = textSizeToFitWidthAndHeight;
        }
        return minTextSize;
    }

    /** Returns the largest text size so that all of the characters in the main rows of the keyboard layout will fit their key widths and heights. **/
    public float getLastKeyboardRowTextSizeForKeyboardLayout(KeyboardLayout layout, int screenWidth, int sumOfKeyPaddingInPx, int sumOfKeyboardPaddingInPx, float keysTextSizeToFitMaxHeight, Context context) {
        float minTextSize = Float.MAX_VALUE;
        for (char c : layout.getLastRowCharacters()) {
            String keyText = layout.getKeyText(c);
            double keyLayoutWeight = layout.getLayoutWeightFrom1000(c);
            int keyWidth = (int)((double)(screenWidth - sumOfKeyboardPaddingInPx)*keyLayoutWeight/1000d)-sumOfKeyPaddingInPx;
            float textSizeToFitWidthAndHeight = getTextSizeToFitWidthWithMaxValueOf(keyText, keyWidth, (float) keysTextSizeToFitMaxHeight, context);
            if (layout.hasUpperCase() && !Keyboard.isSpecialChar(c))
                textSizeToFitWidthAndHeight = Math.min(textSizeToFitWidthAndHeight, getTextSizeToFitWidthWithMaxValueOf(keyText.toUpperCase(), keyWidth, (float) keysTextSizeToFitMaxHeight, context));
            if (textSizeToFitWidthAndHeight < minTextSize)
                minTextSize = textSizeToFitWidthAndHeight;
        }
        return minTextSize;
    }

    public void calculateAndSaveExtraSymbolsOptimalOrderingInPortrait(Context context) {
        int optionsHeightInPx = ConvertPixelUnits.getPixelsFromInches(optionsHeightInInches, context);
        int screenWidth = SharedPreferencesStorage.getInt(Constants.sharedPreferencesName, Constants.portraitScreenWidthInPxPreference, 0, context);
        int availableWidth = screenWidth-Constants.numOfSpecialButtons*getSpecialButtonWidthByHeight(optionsHeightInPx);
        int numOfExtraSymbolsButtons = keyboardLayoutManager.getAllExtraSymbolsLayouts().length;
        int buttonWidth = availableWidth/numOfExtraSymbolsButtons;
        double symbolsButtonHeightWidthRatio = ((double) optionsHeightInPx-2d*Constants.specialSymbolsButtonPadding) / ((double) buttonWidth-10d);
        HashMap<String, Pair<String, Integer>> orderingPerVerticalLayout = new HashMap<>();
        for (KeyboardLayout layout : new KeyboardLayoutManager(context).getAllExtraSymbolsLayouts())
            orderingPerVerticalLayout.put(layout.getLanguageTag(), calculateExtraSymbolsOrderingBySymbolsAndButtonHeightWidthRatio(context, layout.getNonSpecialCharacters(), symbolsButtonHeightWidthRatio));
        ExternalStorage.AppDirectoryOperations.saveObjectToAppDirectory(context, Constants.extraSymbolsOrderingForVerticalLayoutsFileName, orderingPerVerticalLayout);
    }

    public void calculateAndSaveExtraSymbolsOptimalOrderingInLandscape(Context context) {
        int optionsHeightInPx = ConvertPixelUnits.getPixelsFromInches(optionsHeightInInches, context);
        int screenWidth = SharedPreferencesStorage.getInt(Constants.sharedPreferencesName, Constants.portraitScreenHeightInPxPreference, 0, context);
        int availableWidth = screenWidth-Constants.numOfSpecialButtons*getSpecialButtonWidthByHeight(optionsHeightInPx);
        int numOfExtraSymbolsButtons = keyboardLayoutManager.getAllExtraSymbolsLayouts().length;
        int buttonWidth = availableWidth/numOfExtraSymbolsButtons;
        double symbolsButtonHeightWidthRatio = ((double) optionsHeightInPx-2d*Constants.specialSymbolsButtonPadding) / ((double) buttonWidth-10d);
        HashMap<String, Pair<String, Integer>> orderingPerHorizontalLayout = new HashMap<>();
        for (KeyboardLayout layout : new KeyboardLayoutManager(context).getAllExtraSymbolsLayouts())
            orderingPerHorizontalLayout.put(layout.getLanguageTag(), calculateExtraSymbolsOrderingBySymbolsAndButtonHeightWidthRatio(context, layout.getNonSpecialCharacters(), symbolsButtonHeightWidthRatio));
        ExternalStorage.AppDirectoryOperations.saveObjectToAppDirectory(context, Constants.extraSymbolsOrderingForHorizontalLayoutsFileName, orderingPerHorizontalLayout);
    }

    public static HashMap<KeyboardLayout, Pair<String, Integer>> getSavedExtraSymbolsOrderingForVerticalLayouts(Context context) {
        try {
            return ExternalStorage.AppDirectoryOperations.restoreObjectFromAppDirectory(context, Constants.extraSymbolsOrderingForVerticalLayoutsFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<KeyboardLayout, Pair<String, Integer>> getSavedExtraSymbolsOrderingForHorizontalLayouts(Context context) {
        try {
            return ExternalStorage.AppDirectoryOperations.restoreObjectFromAppDirectory(context, Constants.extraSymbolsOrderingForHorizontalLayoutsFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Returns the text and the number of lines. **/
    public Pair<String, Integer> calculateExtraSymbolsOrderingBySymbolsAndButtonHeightWidthRatio(Context context, char[] symbols, double symbolsButtonHeightWidthRatio) {
        // we will do our entire calculations for an arbitrary font size of 20:
        double stringHeightForFontSize20 = getTotalSymbolsStringHeightForFontSize20(context, symbols);
        double symbolsStringWidthForFontSize20 = getTotalSymbolsStringWidthForFontSize20(context, symbols);
        double optimalNumOfRows = Math.sqrt(symbolsButtonHeightWidthRatio/(stringHeightForFontSize20/symbolsStringWidthForFontSize20));
        int floorNumOfRows = (int) optimalNumOfRows;
        int ceilingNumOfRows = (int) Math.ceil(optimalNumOfRows);
        double floorTextRatio = stringHeightForFontSize20*floorNumOfRows/(symbolsStringWidthForFontSize20/floorNumOfRows);
        double ceilingTextRatio = stringHeightForFontSize20*ceilingNumOfRows/(symbolsStringWidthForFontSize20/ceilingNumOfRows);
        int bestNumOfRows = Math.abs(symbolsButtonHeightWidthRatio - floorTextRatio) > Math.abs(symbolsButtonHeightWidthRatio - ceilingTextRatio) ? ceilingNumOfRows : floorNumOfRows;
        double rowWidthForBestNumOfRows = Math.max(symbolsStringWidthForFontSize20/bestNumOfRows, stringHeightForFontSize20*bestNumOfRows/symbolsButtonHeightWidthRatio);
        int numOfRows = 1;
        StringBuilder finalText = new StringBuilder();
        //now we add the symbols to the button and move to a new line each time the width will be higher than the row width:
        StringBuilder currentLine = new StringBuilder();
        float maxLineWidth = 0;
        for (int i = 0; i < symbols.length; i++) {
            String lastCharacterString = "";
            if (i != 0)
                lastCharacterString+=" ";
            lastCharacterString+=symbols[i];
            currentLine.append(lastCharacterString);
            if (numOfRows == bestNumOfRows || getStringWidthForFontSize20(context, currentLine.toString()) < rowWidthForBestNumOfRows) {
                finalText.append(symbols[i]).append(" ");
                maxLineWidth = Math.max(maxLineWidth, getStringWidthForFontSize20(context, currentLine.toString()));
            } else {
                numOfRows++;
                currentLine = new StringBuilder();
                currentLine.append(symbols[i]);
                finalText.append(symbols[i]).append("\n");
            }
        }
        return new Pair<>(finalText.toString(), bestNumOfRows);
    }

    private float getTotalSymbolsStringWidthForFontSize20(Context context, char[] symbols) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < symbols.length; i++) {
            if (i != symbols.length - 1)
                builder.append(symbols[i]).append(" ");
            else
                builder.append(symbols[i]);
        }
        return getStringWidthForFontSize20(context, builder.toString());
    }

    private float getTotalSymbolsStringHeightForFontSize20(Context context, char[] symbols) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < symbols.length; i++) {
            if (i != symbols.length - 1)
                builder.append(symbols[i]).append(" ");
            else
                builder.append(symbols[i]);
        }
        Paint paint = new Paint();
        paint.setTypeface(ResourcesCompat.getFont(context, Constants.keysFontId));
        //an arbitrary size. can be anything.
        paint.setTextSize(20);
        Rect result = new Rect();
        // Measure the text rectangle to get the height
        paint.getTextBounds(builder.toString(), 0, builder.toString().length(), result);
        return result.height();
    }

    private float getStringWidthForFontSize20(Context context, String str) {
        Paint paint = new Paint();
        paint.setTypeface(ResourcesCompat.getFont(context, Constants.keysFontId));
        //an arbitrary size. can be anything.
        paint.setTextSize(20);
        return paint.measureText(str);
    }

    public static int getSpecialButtonWidthByHeight(int specialButtonHeight) {
        return (int) ((double)specialButtonHeight/Constants.heightWidthRatioOfSpecialButtons);
    }

    public void saveKeyboardHeight(int currentHeight) {
        SharedPreferencesStorage.saveInt(Constants.sharedPreferencesName, Constants.keyboardHeightPreference, currentHeight, keyboardLayoutManager.getContext());
    }

    public static int getKeyboardHeightPercentageOfScreen(Context context) {
        return SharedPreferencesStorage.getInt(Constants.sharedPreferencesName, Constants.keyboardHeightPreference, 30, context);
    }

    public static int getCurrentKeyboardHeightInPixels(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == 1)
            return ConvertPixelUnits.getPixelsFromPercentageOfScreenHeight(context, KeyboardGraphicCreation.getKeyboardHeightPercentageOfScreen(context), false);
        else
            return ConvertPixelUnits.getPixelsFromPercentageOfScreenHeight(context, KeyboardGraphicCreation.getKeyboardHeightPercentageOfScreen(context), true);
    }
}