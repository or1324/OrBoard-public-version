package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

import android.content.Context;
import android.content.res.Resources;
import android.os.LocaleList;
import android.view.inputmethod.InputMethodSubtype;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.ImmutableStorageLinkedHashMap;

public class KeyboardLayoutManager {
    private static final KeyboardLayout hebrewKeyboardLayout = new KeyboardLayout(Constants.hebrewLanguageTag, new char[][]{"קראטוןםפ".toCharArray(), "שדגכעיחלךף".toCharArray(), "זסבהנמצתץD".toCharArray()}, false);
    private static final KeyboardLayout englishKeyboardLayout = new KeyboardLayout(Constants.englishLanguageTag, new char[][] {"qwertyuiop".toCharArray(), "asdfghjkl".toCharArray(), "CzxcvbnmD".toCharArray()}, true);
    private static final KeyboardLayout russianKeyboardLayout = new KeyboardLayout(Constants.russianLanguageTag, new char[][]{"йцукенгшщзх".toCharArray(), "фывапролджэ".toCharArray(), "CячсмитьбюD".toCharArray()}, true);
    private static final KeyboardLayout symbols1KeyboardLayout = new KeyboardLayout("symbols1", new char[][] {"1234567890".toCharArray(), "_@#₪%&-+()".toCharArray(), "R*\"':;!?D".toCharArray()}, false);
    private static final KeyboardLayout symbols2KeyboardLayout = new KeyboardLayout("symbols2", new char[][]{"⁰¹²³⁴⁵⁶⁷⁸⁹".toCharArray(), "₀₁₂₃₄₅₆₇₈₉".toCharArray(), "ⁿₙᵐₘ⁺₊⁻₋∞D".toCharArray()}, false);
    private static final KeyboardLayout symbols3KeyboardLayout = new KeyboardLayout("symbols3", new char[][]{"↑→←↓►★✎ツ≥≤".toCharArray(), "↺≈≠∆∢≌║Σ∨∧".toCharArray(), "⁄⊕∈∉⊇⊆⟷∅∫D".toCharArray()}, false);
    private static final KeyboardLayout symbols4KeyboardLayout = new KeyboardLayout("symbols4", new char[][]{"~`|<>√π÷×♡".toCharArray(), "‎∆/$^°={}‏".toCharArray(), "ε∀\\©¡¿○[]D".toCharArray()}, false);
    private static final KeyboardLayout symbols5KeyboardLayout = new KeyboardLayout("symbols5", new char[][]{"ְֱֲֳִֵֶַָֹ".toCharArray(), "ֻּₐₑₕᵢⱼₖₗₒ".toCharArray(), "ᵣₛₜᵤᵥₓᵃᵇᶜD".toCharArray()}, false);
    //If I add another language layout I need to add it to this map
    private static final LinkedHashMap<String, KeyboardLayout> supportedLanguagesKeyboardLayouts = new LinkedHashMap<String, KeyboardLayout>() {{
        put(Constants.hebrewLanguageTag, hebrewKeyboardLayout);
        put(Constants.englishLanguageTag, englishKeyboardLayout);
        put(Constants.russianLanguageTag, russianKeyboardLayout);
    }};
    private final ImmutableStorageLinkedHashMap<String, KeyboardLayout> keyboardLanguageLayouts;

    //If I add another keyboard symbols layout I need to add it to this array
    private static final KeyboardLayout[] keyboardSymbolsLayouts = new KeyboardLayout[]{symbols1KeyboardLayout};

    //If I add another extra symbols layout I need to add it to this array
    private static final KeyboardLayout[] extraSymbolsLayouts = new KeyboardLayout[]{symbols2KeyboardLayout, symbols3KeyboardLayout, symbols4KeyboardLayout, symbols5KeyboardLayout};

    //If I add another extra symbols layout I need to add its index to this array
    private static final HashMap<KeyboardLayout, Integer> extraSymbolsLayoutIndexes = new HashMap<KeyboardLayout, Integer>(){{
        put(symbols2KeyboardLayout, 0);
        put(symbols3KeyboardLayout, 1);
        put(symbols4KeyboardLayout, 2);
        put(symbols5KeyboardLayout, 3);
    }};
    //If I add another layout I need to add it to this array
    private static final KeyboardLayout[] keyboardLayouts = new KeyboardLayout[]{hebrewKeyboardLayout, englishKeyboardLayout, russianKeyboardLayout, symbols1KeyboardLayout, symbols2KeyboardLayout, symbols3KeyboardLayout, symbols4KeyboardLayout, symbols5KeyboardLayout};
    private final Context context;
    public KeyboardLayoutManager(Context context) {
        this.context = context;
        LocaleList localeList = Resources.getSystem().getConfiguration().getLocales();
        LinkedHashMap<String, KeyboardLayout> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < localeList.size(); i++) {
            String languageTag = localeList.get(i).toLanguageTag();
            if (supportedLanguagesKeyboardLayouts.containsKey(languageTag))
                linkedHashMap.put(languageTag, supportedLanguagesKeyboardLayouts.get(languageTag));
        }
        keyboardLanguageLayouts = new ImmutableStorageLinkedHashMap<>(linkedHashMap, context);
    }

    public KeyboardLayout[] getAllKeyboardLayouts() {
        return keyboardLayouts;
    }

    public KeyboardLayout getSymbols1KeyboardLayout() {
        return symbols1KeyboardLayout;
    }

    public KeyboardLayout[] getAllExtraSymbolsLayouts() {
        return extraSymbolsLayouts;
    }

    public boolean isKeyboardSymbolsLayout(KeyboardLayout layout) {
        for (KeyboardLayout l : keyboardSymbolsLayouts)
            if (l.equals(layout))
                return true;
        return false;
    }

    public KeyboardLayout[] getKeyboardSymbolsLayouts() {
        return keyboardSymbolsLayouts;
    }

    public KeyboardLayout getNextKeyboardLayout() {
        return keyboardLanguageLayouts.nextValue(context);
    }

    public KeyboardLayout getPreviousKeyboardLayout() {
        return keyboardLanguageLayouts.previousValue(context);
    }

    public KeyboardLayout getCurrentKeyboardLayout() {
        return keyboardLanguageLayouts.currentValue();
    }

    public Context getContext() {
        return context;
    }

    public Integer getExtraSymbolsLayoutIndex(KeyboardLayout layout) {
        return extraSymbolsLayoutIndexes.get(layout);
    }

}