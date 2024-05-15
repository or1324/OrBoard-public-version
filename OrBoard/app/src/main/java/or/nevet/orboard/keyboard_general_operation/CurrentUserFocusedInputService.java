package or.nevet.orboard.keyboard_general_operation;

import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.widget.LinearLayout;

import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.Keyboard;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardGraphicCreation;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayout;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayoutManager;
import or.nevet.orboard.listeners.OnRegularKeyboardChangedListener;
import or.nevet.orboard.unique_features.ClipboardLogManager;
import or.nevet.orboard.unique_features.SpamManager;
import or.nevet.orboard.unique_features.TextLogManager;

public class CurrentUserFocusedInputService extends InputMethodService {
    private Keyboard keyboard;
    private OrBoardView orBoardView;
    private ClipboardLogManager clipboardLogManager;
    private KeyboardLayoutManager keyboardLayoutManager;
    private TextLogManager textLogManager;
    private static CurrentUserFocusedInputService instance;
    private static boolean isInitialized = false;

    public CurrentUserFocusedInputService() {
        instance = this;
    }

    public static CurrentUserFocusedInputService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        clipboardLogManager = new ClipboardLogManager(this);
        keyboardLayoutManager = new KeyboardLayoutManager(getApplicationContext());
        textLogManager = new TextLogManager(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clipboardLogManager.stopLoggingClipboard();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public String getText() {
        ExtractedText text = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0);
        if (text == null)
            return "";
        CharSequence c = text.text;
        if (c == null)
            return "";
        else
            return c.toString();
    }

    public void deleteAll() {
        int length = getText().length();
        //deleting the length from left and right because the cursor can be in all places and deletion is relative to cursor.
        getCurrentInputConnection().deleteSurroundingText(length, length);
    }

    public void clickOnEnter() {
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
    }

    public void sendText(String text) {
        getCurrentInputConnection().commitText(text, 1);
    }

    @Override
    public View onCreateInputView() {
        createAndInitKeyboard();
        orBoardView = new OrBoardView(this);
        keyboard.setCurrentRegularKeyboardLayout(keyboardLayoutManager.getCurrentKeyboardLayout(), true);
        isInitialized = true;
        return orBoardView;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public OrBoardView getOrBoardView() {
        return orBoardView;
    }

    private void deleteOneCharacter() {
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        SpamManager.stopSpam();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
    }

    public void refreshKeyboardHeight() {
        int height = KeyboardGraphicCreation.getCurrentKeyboardHeightInPixels(this);
        keyboard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        orBoardView.removeAllViews();
        orBoardView.addViews();
        keyboard.refreshLayout();
    }

    private void createAndInitKeyboard() {
        keyboard = new Keyboard(this, keyboardLayoutManager);
        keyboard.setOnEnterListener(key -> {
            clickOnEnter();
            textLogManager.textChanged(getText());
        });
        keyboard.setOnSpamListener(()-> {
            SpamManager.spam(CurrentUserFocusedInputService.this);
        });
        keyboard.setOnDeleteListener(key -> {
            deleteOneCharacter();
            textLogManager.textChanged(getText());
        });
        keyboard.setOnWriteButtonCharacterListener(key -> {
            sendText(key.getText().toString());
            textLogManager.textChanged(getText());
        });
        keyboard.setOnSwitchSubtypeListener((direction) -> {
            KeyboardLayout newKeyboardLayout;
            if (direction)
                newKeyboardLayout = keyboardLayoutManager.getNextKeyboardLayout();
            else
                newKeyboardLayout = keyboardLayoutManager.getPreviousKeyboardLayout();
            keyboard.setCurrentRegularKeyboardLayout(newKeyboardLayout, false);
        });
        keyboard.setOnRegretSpamListener(() -> {
            SpamManager.regretSpam(CurrentUserFocusedInputService.this);
        });
        keyboard.setOnSpaceListener(() -> {
            sendText(" ");
            textLogManager.textChanged(getText());
        });
        keyboard.setOnRegularKeyboardChangedListener(new OnRegularKeyboardChangedListener() {
            @Override
            public void onChanged(KeyboardLayout previousLayout) {
                orBoardView.resetExtras(previousLayout);
            }
        });
    }

}