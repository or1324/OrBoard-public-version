package or.nevet.orboard.general_helpers;

import java.util.Arrays;
import java.util.HashSet;

public class CommonAlgorithms<T> {

    //O(n+m)
    public HashSet<T> getFirstArraySubSetWithoutElementsOfSecondArray(T[] firstArray, T[] secondArray) {
        //O(n)
        HashSet<T> firstSet = new HashSet<>(Arrays.asList(firstArray));
        //O(m)
        HashSet<T> secondSet = new HashSet<>(Arrays.asList(secondArray));
        //O(m)
        firstSet.removeAll(secondSet);
        return firstSet;
    }

    //O(n+m)
    public T[] getUnOrderedSubArrayWithoutElementsOfSecondArray(T[] firstArray, T[] secondArray) {
        HashSet<T> firstArraySubsetWithoutSecondArray = getFirstArraySubSetWithoutElementsOfSecondArray(firstArray, secondArray);
        T[] resultArray = (T[])new Object[firstArray.length];
        firstArraySubsetWithoutSecondArray.toArray(resultArray);
        return resultArray;
    }

    //O(n)
    public void reverseArray(T[] array) {
        for (int i = 0; i < array.length/2; i++) {
            T temp = array[i];
            array[i] = array[array.length-i-1];
            array[array.length-i-1] = temp;
        }
    }
}
