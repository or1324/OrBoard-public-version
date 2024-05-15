package or.nevet.orboard.general_helpers;

import android.content.Context;

import java.util.LinkedHashMap;

//Has an iteration capability which allows iterating over a linked hashmap and saving the index of the current element in storage.
public class ImmutableStorageLinkedHashMap<T, H> {
    private final LinkedHashMap<T, H> linkedHashMap;
    private final H[] valuesArray;
    private int currentIndex = 0;
    private H currentValue;
    public ImmutableStorageLinkedHashMap(LinkedHashMap<T, H> linkedHashMap, Context context) {
        this.linkedHashMap = linkedHashMap;
        currentIndex = SharedPreferencesStorage.getInt(Constants.sharedPreferencesName, Constants.lastSubtypeIndexPreference, 0, context);
        valuesArray = (H[]) new Object[linkedHashMap.size()];
        linkedHashMap.values().toArray(valuesArray);
        currentValue = valuesArray[currentIndex];
    }

    public H nextValue(Context context) {
        currentIndex++;
        if (currentIndex == valuesArray.length)
            currentIndex = 0;
        currentValue = valuesArray[currentIndex];
        SharedPreferencesStorage.saveInt(Constants.sharedPreferencesName, Constants.lastSubtypeIndexPreference, currentIndex, context);
        return currentValue;
    }

    public H previousValue(Context context) {
        currentIndex--;
        if (currentIndex == -1)
            currentIndex = valuesArray.length-1;
        currentValue = valuesArray[currentIndex];
        SharedPreferencesStorage.saveInt(Constants.sharedPreferencesName, Constants.lastSubtypeIndexPreference, currentIndex, context);
        return currentValue;
    }

    public H currentValue() {
        return currentValue;
    }

    public H getValueByKey(T key) {
        return linkedHashMap.get(key);
    }

}
