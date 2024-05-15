package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

import java.io.Serializable;

public class LastKeyboardRow implements Serializable {
    private final char[] characters = "N,S.E".toCharArray();

    public LastKeyboardRow() {
    }

    public int getLayoutWeightFrom1000(char keyChar) {
        //for 'N' and 'E' returns the remaining space after the ',', the '.' and the space.
        if (keyChar == 'N' || keyChar == 'E')
            return 150;
        if (keyChar == 'S')
            return 500;
        return 100;
    }

    public boolean containsKey(char keyChar) {
        for (int i = 0; i < characters.length; i++)
            if (characters[i] == keyChar)
                return true;
        return false;
    }

    public char[] getCharacters() {
        return characters;
    }
}
