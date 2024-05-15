package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import or.nevet.orboard.general_helpers.ExternalStorage;
import or.nevet.orboard.general_helpers.Pair;
import or.nevet.orboard.listeners.KeyIteration;
import or.nevet.orboard.listeners.OnKeyClickListener;
import or.nevet.orboard.listeners.OnRegretSpamListener;
import or.nevet.orboard.listeners.OnRegularKeyboardChangedListener;
import or.nevet.orboard.listeners.OnSpaceListener;
import or.nevet.orboard.listeners.OnSpamListener;
import or.nevet.orboard.listeners.OnSwitchSubtypeListener;
import or.nevet.orboard.general_helpers.Constants;

public class Keyboard extends ConstraintLayout {
    private LinearLayout[] keyboardRows;
    private OnKeyClickListener onEnterListener;
    private OnKeyClickListener onDeleteListener;
    private OnKeyClickListener onWriteCharacterListener;
    private OnSwitchSubtypeListener onSwitchSubtypeListener;
    private OnSpaceListener onSpaceListener;
    private OnSpamListener onSpamListener;
    private OnRegretSpamListener onRegretSpamListener;
    private OnRegularKeyboardChangedListener onRegularKeyboardChangedListener;
    private Drawable keyboardBackground;
    private final KeyboardGraphicCreation graphicCreation;
    private KeyboardKeyFunctionality keyFunctionality;
    private KeyboardLayout currentRegularKeyboardLayout;
    private KeyboardLayout currentKeyboardLayout;
    private final KeyboardLayoutManager keyboardLayoutManager;
    private Key upperCaseKey;
    private int currentKeyboardSymbolLayoutIndex;

    public Keyboard(@NonNull Context context, KeyboardLayoutManager keyboardLayoutManager) {
        super(context);
        this.keyboardLayoutManager = keyboardLayoutManager;
        restoreKeyboardBackground();
        setBackground(keyboardBackground);
        setPadding(Constants.keyboardPadding, Constants.keyboardPadding, Constants.keyboardPadding, Constants.keyboardPadding);
        graphicCreation = new KeyboardGraphicCreation(keyboardLayoutManager);
        int height = KeyboardGraphicCreation.getCurrentKeyboardHeightInPixels(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    }

    public Key getUpperCaseKey() {
        return upperCaseKey;
    }

    public void restoreKeyboardBackground() {
        keyboardBackground = getCurrentKeyboardBackground(getContext());
    }

    public static Drawable getCurrentKeyboardBackground(Context context) {
        try {
            Bitmap backgroundBitmap = ExternalStorage.AppDirectoryOperations.restoreBitmapFromAppDirectory(Constants.keyboardBackgroundFileName, context);
            return new BitmapDrawable(context.getResources(), backgroundBitmap);
        } catch (FileNotFoundException e) {
            return AppCompatResources.getDrawable(context, Constants.keyboardBackgroundId);
        }
    }

    public void setCurrentRegularKeyboardLayout(KeyboardLayout currentRegularKeyboardLayout, boolean rememberIfWasInSymbols) {
        KeyboardLayout previousLayout = this.currentRegularKeyboardLayout;
        this.currentRegularKeyboardLayout = currentRegularKeyboardLayout;
        boolean shouldSwitchToNewRegular = !rememberIfWasInSymbols || currentKeyboardLayout == null;
        if (shouldSwitchToNewRegular) {
            onRegularKeyboardChangedListener.onChanged(previousLayout);
            currentKeyboardSymbolLayoutIndex = 0;
            currentKeyboardLayout = currentRegularKeyboardLayout;
            refreshLayout();
        }
    }

    public void setOnRegularKeyboardChangedListener(OnRegularKeyboardChangedListener onRegularKeyboardChangedListener) {
        this.onRegularKeyboardChangedListener = onRegularKeyboardChangedListener;
    }

    public KeyboardLayout getCurrentKeyboardLayout() {
        return currentKeyboardLayout;
    }

    public KeyboardLayout getCurrentRegularKeyboardLayout() {
        return currentRegularKeyboardLayout;
    }

    public void refreshLayout() {
        graphicCreation.reset(this);
        createCurrentKeyLayout();
    }

    private void createCurrentKeyLayout() {
        keyboardRows = new LinearLayout[currentKeyboardLayout.getNumOfButtonsInEachRow().length];
        keyFunctionality = new KeyboardKeyFunctionality(this);
        createKeys();
    }

    private void createKeys() {
        graphicCreation.initializeRows(this);
        Pair<Float, Float> mainRowsAndLastRowTextSizes;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mainRowsAndLastRowTextSizes = graphicCreation.getSavedKeyTextSizesForVerticalLayouts(getContext());
        else
            mainRowsAndLastRowTextSizes = graphicCreation.getSavedKeyTextSizesForHorizontalLayouts(getContext());
        int currentCharacterIndex = 0;
        char[] allCharacters = currentKeyboardLayout.getAllCharacters();
        for (int row = 0; row < currentKeyboardLayout.getNumOfButtonsInEachRow().length; row++) {
            int numOfButtonsInCurrentRow = currentKeyboardLayout.getNumOfButtonsInEachRow()[row];
            for (int column = 0; column < numOfButtonsInCurrentRow; column++) {
                createNextKey(allCharacters[currentCharacterIndex++], row, mainRowsAndLastRowTextSizes);
            }
        }
    }

    public LinearLayout[] getKeyboardRows() {
        return keyboardRows;
    }

    public void setKeyboardRowAt(LinearLayout row, int rowIndex) {
        keyboardRows[rowIndex] = row;
    }

    public void switchKeyboard() {
        graphicCreation.reset(this);
        currentKeyboardSymbolLayoutIndex++;
        KeyboardLayout[] symbolsLayouts = keyboardLayoutManager.getKeyboardSymbolsLayouts();
        //The number of keyboards is the number of symbol layouts plus 1.
        if (currentKeyboardSymbolLayoutIndex == symbolsLayouts.length+1)
            currentKeyboardSymbolLayoutIndex = 0;
        //The current layout should be the regular one if currentKeyboardSymbolLayoutIndex is equal to 0. Otherwise, it should be the symbols layout in the index that is equal to currentKeyboardSymbolLayoutIndex-1.
        if (currentKeyboardSymbolLayoutIndex == 0)
            currentKeyboardLayout = currentRegularKeyboardLayout;
        else
            currentKeyboardLayout = keyboardLayoutManager.getKeyboardSymbolsLayouts()[currentKeyboardSymbolLayoutIndex-1];
        createCurrentKeyLayout();
    }

    private void createNextKey(char currentChar, int row, Pair<Float, Float> mainRowsAndLastRowTextSizes) {
        Key key;
        if (currentKeyboardLayout.isThisCharacterInTheLastRow(currentChar))
            key = graphicCreation.createKeyGraphics(this, currentChar, row, getCurrentKeyboardLayout(), mainRowsAndLastRowTextSizes.getSecond());
        else
            key = graphicCreation.createKeyGraphics(this, currentChar, row, getCurrentKeyboardLayout(), mainRowsAndLastRowTextSizes.getFirst());
        if (currentChar == 'C')
            upperCaseKey = key;
        keyFunctionality.dealWithButtonClick(key, currentChar);
    }

    public void setOnSpamListener(OnSpamListener onSpamListener) {
        this.onSpamListener = onSpamListener;
    }

    public OnSpamListener getOnSpamListener() {
        return onSpamListener;
    }

    public void setOnRegretSpamListener(OnRegretSpamListener onRegretSpamListener) {
        this.onRegretSpamListener = onRegretSpamListener;
    }

    public OnRegretSpamListener getOnRegretSpamListener() {
        return onRegretSpamListener;
    }

    public void setOnEnterListener(OnKeyClickListener listener) {
        onEnterListener = listener;
    }
    public void setOnDeleteListener(OnKeyClickListener listener) {
        onDeleteListener = listener;
    }
    public void setOnWriteButtonCharacterListener(OnKeyClickListener listener) {
        onWriteCharacterListener = listener;
    }

    public void setOnSpaceListener(OnSpaceListener onSpaceListener) {
        this.onSpaceListener = onSpaceListener;
    }

    public void setOnSwitchSubtypeListener(OnSwitchSubtypeListener onSwitchSubtypeListener) {
        this.onSwitchSubtypeListener = onSwitchSubtypeListener;
    }

    public OnKeyClickListener getOnDeleteListener() {
        return onDeleteListener;
    }

    public OnKeyClickListener getOnEnterListener() {
        return onEnterListener;
    }

    public OnKeyClickListener getOnWriteCharacterListener() {
        return onWriteCharacterListener;
    }

    public OnSpaceListener getOnSpaceListener() {
        return onSpaceListener;
    }

    public OnSwitchSubtypeListener getOnSwitchSubtypeListener() {
        return onSwitchSubtypeListener;
    }

    //O(n) when n is the number of characters in the current layout.
    public void iterateAllKeys(KeyIteration iteration) {
        for (int rowIndex = 0; rowIndex < keyboardRows.length; rowIndex++) {
            LinearLayout currentRow = (LinearLayout) getChildAt(rowIndex);
            for (int buttonIndex = 0; buttonIndex < currentKeyboardLayout.getNumOfButtonsInEachRow()[rowIndex]; buttonIndex++) {
                Key currentKey = (Key) currentRow.getChildAt(buttonIndex);
                iteration.runOnKeyIteration(currentKey);
            }
        }
    }

    //O(n) when n is the number of special characters
    public static boolean isSpecialKey(Key key) {
        return isSpecialChar(key.getCharacter());
    }

    //O(n) when n is the number of special characters
    public static boolean isSpecialChar(char c) {
        HashSet<Character> specialChars = new HashSet<>(Arrays.asList(Constants.specialCharacters));
        return specialChars.contains(c);
    }

    //O(n+m) when n is the number of characters in the current layout and m is the number of special characters
    public void iterateAllRegularKeys(KeyIteration iteration) {
        HashMap<Character, Key> nonSpecialKeys = new HashMap<>();
        //O(n)
        iterateAllKeys(key -> nonSpecialKeys.put(key.getCharacter(), key));
        for (Character c : Constants.specialCharacters)
            nonSpecialKeys.remove(c);
        for (Key key : nonSpecialKeys.values())
            iteration.runOnKeyIteration(key);
    }
}
