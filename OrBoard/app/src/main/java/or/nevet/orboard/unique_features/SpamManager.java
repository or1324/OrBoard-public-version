package or.nevet.orboard.unique_features;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import or.nevet.orboard.general_helpers.BackgroundRunningHelper;
import or.nevet.orboard.keyboard_general_operation.CurrentUserFocusedInputService;

public class SpamManager {
    public static volatile boolean isSpamming = false;
    private static volatile boolean isOk = true;
    public static boolean waitingForSpam = false;
    public static String spamText = null;
    private static boolean isPlatformSending(String test, CurrentUserFocusedInputService inputBox) {
        inputBox.sendText(test);
        inputBox.clickOnEnter();
        long time = 0;
        while (true) {
            String str = inputBox.getText();
            if (str.equals(""))
                return true;
            if (time >= 300)
                return false;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time+=10;
        }
    }

    private static void sendAndWaitUntilTextEmptySyncAtEveryPlatform(CurrentUserFocusedInputService inputBox) {
        inputBox.clickOnEnter();
        long time = 0;
        while (true) {
            String str = inputBox.getText();
            if (str.equals(""))
                break;
            if (time >= 1000)
                break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time+=10;
        }
    }

    private static void spam(final String text, CurrentUserFocusedInputService inputBox) {
        final boolean isSending = isPlatformSending(text, inputBox);
        BackgroundRunningHelper.runCodeInBackgroundAsync(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                isOk = true;
                while(isSpamming) {
                    try {
                        while (!isOk) ;
                        isOk = false;
                        if (isSpamming) {
                            try {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendTextOnceIfSpamming(inputBox, isSending, text);
                                        isOk = true;
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void spam(final long num, final String text, CurrentUserFocusedInputService inputBox) {
        final boolean isSending = isPlatformSending(text, inputBox);
        BackgroundRunningHelper.runCodeInBackgroundAsync(new Runnable() {
            @Override
            public void run() {
                final long[] number = new long[] {num};
                number[0]--;
                isOk = true;
                Handler handler = new Handler(Looper.getMainLooper());
                while(isSpamming && number[0] > 0) {
                    try {
                        while (!isOk);
                        isOk = false;
                        if (isSpamming) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sendTextOnceIfSpamming(inputBox, isSending, text);
                                    number[0]--;
                                    if (num == 0)
                                        isSpamming = false;
                                    isOk = true;
                                }
                            });
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                isSpamming = false;
            }
        });
    }

    private static void sendTextOnceIfSpamming(CurrentUserFocusedInputService inputBox, boolean isSending, String text) {
        if (isSpamming) {
            try {
                inputBox.sendText(text);
                if (isSending)
                    sendAndWaitUntilTextEmptySyncAtEveryPlatform(inputBox);
                else
                    inputBox.sendText("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void spam(CurrentUserFocusedInputService inputBox) {
        if (!isSpamming) {
            if (waitingForSpam) {
                waitingForSpam = false;
                isSpamming = true;
                final String numText = inputBox.getText();
                if (numText.equals("")) {
                    SpamManager.spam(spamText, inputBox);
                }
                else {
                    try {
                        long n = Long.parseLong(numText);
                        inputBox.deleteAll();
                        SpamManager.spam(n, spamText, inputBox);
                    } catch (Exception e) {
                        isSpamming = false;
                        spamText = null;
                        waitingForSpam = false;
                        inputBox.deleteAll();
                        Toast.makeText(inputBox, "You have to enter the number of spams", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                waitingForSpam = true;
                spamText = inputBox.getText();
                inputBox.deleteAll();
            }
        }
        else
            stopSpam();
    }

    public static void stopSpam() {
        isSpamming = false;
    }

    public static void regretSpam(CurrentUserFocusedInputService inputBox) {
        if (!isSpamming) {
            waitingForSpam = false;
            if (spamText != null) {
                inputBox.sendText(spamText);
                spamText = null;
            }
        }
    }
}
