package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import or.nevet.orboard.general_helpers.CommonAlgorithms;
import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.listeners.KeyIteration;

public class KeyboardLayout implements Serializable {
    //special characters: E = Enter, S = Space, C = Caps Lock, D = Delete, R = Repeat text, N = next layout
    private final String language;
    private final char[][] keysInEachRow;
    private final boolean hasUpperCase;

    private final LastKeyboardRow lastKeyboardRow = new LastKeyboardRow();

    public KeyboardLayout(String language, char[][] keysInEachRow, boolean hasUpperCase) {
        this.language = language;
        this.keysInEachRow = keysInEachRow;
        this.hasUpperCase = hasUpperCase;
    }

    public String getKeyText(char keyChar) {
        String text = "";
        if (keyChar == 'D')
            text = "⌫";
        else if (keyChar == 'E')
            text = "⏎";
        else if (keyChar == 'C')
            text = "⇧";
        else if (keyChar == 'S')
            text = "Space";
        else if (keyChar == 'N')
            text = "⌨←";
        else if (keyChar == 'R')
            text = "\uD83D\uDE0F";
        else
            text = keyChar+"";
        return text;
    }


    public int getLayoutWeightFrom1000(char keyChar) {
        double numOfButtonsInRowWithMostButtons = 0;
        for (int i = 0; i < keysInEachRow.length-1; i++)
            numOfButtonsInRowWithMostButtons = Math.max(numOfButtonsInRowWithMostButtons, keysInEachRow[i].length);
        double regularKeyWeight = 1000d/numOfButtonsInRowWithMostButtons;
        if (lastKeyboardRow.containsKey(keyChar))
            return lastKeyboardRow.getLayoutWeightFrom1000(keyChar);
        return (int) regularKeyWeight;
    }

    public String getLanguageTag() {
        return language;
    }

    public char[] getAllCharacters() {
        char[] mainCharacters = getMainCharacters();
        char[] lastRowCharacters = getLastRowCharacters();
        char[] allCharacters = new char[mainCharacters.length+lastRowCharacters.length];
        for (int i = 0; i < mainCharacters.length; i++)
            allCharacters[i] = mainCharacters[i];
        for (int i = 0; i < lastRowCharacters.length; i++)
            allCharacters[mainCharacters.length+i] = lastRowCharacters[i];
        return allCharacters;
    }

    public char[] getMainCharacters() {
        int numOfChars = 0;
        for (int i = 0; i < keysInEachRow.length; i++)
            numOfChars+=keysInEachRow[i].length;
        char[] characters = new char[numOfChars];
        int currentCharIndex = 0;
        for (int i = 0; i < keysInEachRow.length; i++)
            for (int j = 0; j < keysInEachRow[i].length; j++)
                characters[currentCharIndex++] = keysInEachRow[i][j];
        return characters;
    }

    public char[] getLastRowCharacters() {
        return lastKeyboardRow.getCharacters();
    }

    public int[] getNumOfButtonsInEachRow() {
        int[] numOfButtonsInEachRow = new int[keysInEachRow.length+1];
        for (int i = 0; i < keysInEachRow.length; i++)
            numOfButtonsInEachRow[i] = keysInEachRow[i].length;
        numOfButtonsInEachRow[numOfButtonsInEachRow.length-1] = lastKeyboardRow.getCharacters().length;
        return numOfButtonsInEachRow;
    }


    //O(n) when n is the number of main characters in the layout
    public char[] getNonSpecialCharacters() {
        StringBuilder nonSpecialCharacters = new StringBuilder();
        for (char c : getMainCharacters())
            if (!Keyboard.isSpecialChar(c))
                nonSpecialCharacters.append(c);
        return nonSpecialCharacters.toString().toCharArray();
    }

    public boolean isThisCharacterInTheLastRow(char c) {
        for (char lastRowChar : getLastRowCharacters())
            if (lastRowChar == c)
                return true;
        return false;
    }

    public boolean hasUpperCase() {
        return hasUpperCase;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != KeyboardLayout.class)
            return false;
        return language.equals(((KeyboardLayout)obj).language);
    }
}
