package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;

import androidx.appcompat.content.res.AppCompatResources;

import or.nevet.orboard.R;
import or.nevet.orboard.keyboard_general_operation.AudioAndHapticFeedbackManager;

public class KeyboardKeyFunctionality {
    private static volatile boolean isStillPressingTheCurrentlyRepeatedKey = false;
    private static volatile Thread repeatThread;
    private final UpperCaseManager upperCaseManager;
    Keyboard keyboard;
    Point initialFingerLocationRelativeToView;
    public KeyboardKeyFunctionality(Keyboard keyboard) {
        this.keyboard = keyboard;
        upperCaseManager = new UpperCaseManager();
    }

    public void dealWithButtonClick(Key key, char buttonChar) {
        key.setOnClickListener(v -> {
            if (buttonChar != 'D')
                executeKeyClickOperation(key);
        });
//        if (key.getCharacter() == 'R') {
//            key.setOnLongClickListener(v -> {
//                Vibrator vibrate = (Vibrator) keyboard.getContext().getSystemService(Context.VIBRATOR_SERVICE);
//                vibrate.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
//                keyboard.getOnRegretSpamListener().regretSpam();
//                return true;
//            });
//        }
        //deal with the fact that whatsapp's search box does not trigger action_up if deleting when search box empty.
        key.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (buttonChar == 'S')
                    initialFingerLocationRelativeToView = new Point((int) event.getX(), (int) event.getY());
                else if (!Keyboard.isSpecialChar(buttonChar))
                    waitASecondAndStartRepeatingKeyOperation(key);
                else if (buttonChar == 'D')
                    executeKeyOperationAndThenWaitHalfASecondAndStartRepeating(key);
                AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback();
                key.setInnerBackground(AppCompatResources.getDrawable(keyboard.getContext(), R.drawable.clicked_keyboard_button));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (buttonChar == 'S') {
                    Point currentFingerLocationRelativeToView = new Point((int) event.getX(), (int) event.getY());
                    int spaceBarWidth = key.getWidth();
                    int acceptableSlidingAmount = spaceBarWidth/13;
                    if (distance(initialFingerLocationRelativeToView, currentFingerLocationRelativeToView) >= acceptableSlidingAmount)
                        keyboard.getOnSwitchSubtypeListener().onSwitch(swipedForward(initialFingerLocationRelativeToView, currentFingerLocationRelativeToView));
                } else if (!Keyboard.isSpecialChar(buttonChar) || buttonChar == 'D')
                    stopRepeatingIfCurrentlyRepeating();
                key.setInnerBackground(AppCompatResources.getDrawable(keyboard.getContext(), R.drawable.keyboard_button));
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!Keyboard.isSpecialChar(buttonChar) || buttonChar == 'D')
                    stopRepeatingIfCurrentlyRepeating();
                key.setInnerBackground(AppCompatResources.getDrawable(keyboard.getContext(), R.drawable.keyboard_button));
            }
            //returning false to allow short press delete and key clicks in general.
            return false;
        });
    }

    private void executeKeyClickOperation(Key key) {
        switch (key.getCharacter()) {
            case 'E':
                keyboard.getOnEnterListener().onClick(key);
                break;
            case 'D':
                keyboard.getOnDeleteListener().onClick(key);
                break;
            case 'S':
                keyboard.getOnSpaceListener().onSpace();
                break;
            case 'N':
                keyboard.switchKeyboard();
                break;
            case 'R':
                //keyboard.getOnSpamListener().onSpam();
                break;
            case 'C':
                upperCaseManager.onUpperCaseButtonClicked();
                if (upperCaseManager.isCapsLock())
                    keyboard.getUpperCaseKey().setInnerBackground(AppCompatResources.getDrawable(key.getContext(), R.drawable.clicked_keyboard_button));
                else
                    keyboard.getUpperCaseKey().setInnerBackground(AppCompatResources.getDrawable(key.getContext(), R.drawable.keyboard_button));
                if (upperCaseManager.shouldBeUpperCase()) {
                    //change every one char abc key's text to be caps locked.
                    keyboard.iterateAllRegularKeys(Key::capsLock);
                } else {
                    keyboard.iterateAllRegularKeys(Key::removeCapsLock);
                }
                break;
            default:
                keyboard.getOnWriteCharacterListener().onClick(key);
                upperCaseManager.onRegularKeyClicked();
                if (keyboard.getCurrentKeyboardLayout().hasUpperCase() && !upperCaseManager.shouldBeUpperCase()) {
                    keyboard.iterateAllRegularKeys(Key::removeCapsLock);
                }
                break;
        }
    }

    private void executeKeyOperationAndThenWaitHalfASecondAndStartRepeating(Key key) {
        executeKeyClickOperation(key);
        waitHalfASecondAndStartRepeatingKeyOperation(key);
    }

    private void waitASecondAndStartRepeatingKeyOperation(Key key) {
        waitAndStartRepeatingKeyOperation(key, 1000);
    }

    private void waitHalfASecondAndStartRepeatingKeyOperation(Key key) {
        waitAndStartRepeatingKeyOperation(key, 500);
    }

    private void waitAndStartRepeatingKeyOperation(Key key, long millis) {
        isStillPressingTheCurrentlyRepeatedKey = true;
        if (repeatThread != null)
            repeatThread.interrupt();
        repeatThread = new Thread(() -> {
            try {
                Thread.sleep(millis);
                if (isStillPressingTheCurrentlyRepeatedKey)
                    startRepeating(key);
            } catch (InterruptedException ignored) {
            }
        });
        repeatThread.start();
    }

    private void startRepeating(Key key) throws InterruptedException {
        while (isStillPressingTheCurrentlyRepeatedKey) {
            executeKeyClickOperation(key);
            Thread.sleep(40);
        }
    }

    private int distance(Point p1, Point p2) {
        return (int) Math.sqrt(Math.pow(p2.x-p1.x, 2)+Math.pow(p2.y-p1.y, 2));
    }

    private boolean swipedForward(Point p1, Point p2) {
        return p2.x < p1.x;
    }
    public void stopRepeatingIfCurrentlyRepeating() {
        isStillPressingTheCurrentlyRepeatedKey = false;
        if (repeatThread != null)
            repeatThread.interrupt();
    }
}
