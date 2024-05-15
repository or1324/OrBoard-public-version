package or.nevet.orboard.unique_features;

import java.io.Serializable;
import java.util.LinkedList;

//This datatype contains LinkedList<String>, which is a doubly linked list under the hood, in order to get the best time and space complexity for the clipboard (each method's time complexity is O(1) and the general space complexity is O(n)).
public class ClipbOrd implements Serializable {
    private LinkedList<String> clipboardData;
    public ClipbOrd() {
        clipboardData = new LinkedList<>();
    }

    public void addItem(String text) {
        clipboardData.addFirst(text);
        if (clipboardData.size() > 100)
            clipboardData.removeLast();
    }

    public LinkedList<String> getAllItems() {
        return clipboardData;
    }

}
