package or.nevet.orboard.unique_features;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.keyboard_general_operation.CurrentUserFocusedInputService;
import or.nevet.orboard.general_helpers.ExternalStorage;

public class ClipboardLogManager {
    private ClipboardManager.OnPrimaryClipChangedListener listener;
    private ClipboardManager clipboardManager;
    private boolean canGetAnotherClipboard = true;

    public ClipboardLogManager(CurrentUserFocusedInputService inputBox) {
        clipboardManager = (ClipboardManager) inputBox.getSystemService(CLIPBOARD_SERVICE);
        saveLast100ClipboardCopies(inputBox);
    }
    private void saveLast100ClipboardCopies(CurrentUserFocusedInputService inputBox) {
        listener = () -> {
            if (canGetAnotherClipboard) {
                canGetAnotherClipboard = false;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        canGetAnotherClipboard = true;
                    }
                }, 500);
                ClipData addedData = clipboardManager.getPrimaryClip();
                if (addedData != null) {
                    ClipData.Item addedItem = addedData.getItemAt(0);
                    if (addedItem != null) {
                        CharSequence itemText = addedItem.coerceToText(inputBox);
                        ClipbOrd clipboard;
                        try {
                            clipboard = ExternalStorage.AppDirectoryOperations.restoreObjectFromAppDirectory(inputBox, Constants.clipboardFileName);
                        } catch (IOException e) {
                            clipboard = new ClipbOrd();
                        }
                        if (clipboard == null)
                            clipboard = new ClipbOrd();
                        clipboard.addItem(itemText.toString());
                        ExternalStorage.AppDirectoryOperations.saveObjectToAppDirectory(inputBox, Constants.clipboardFileName, clipboard);
                    }
                }
            }
        };
        clipboardManager.addPrimaryClipChangedListener(listener);
    }

    public void stopLoggingClipboard() {
        clipboardManager.removePrimaryClipChangedListener(listener);
        listener = null;
        clipboardManager = null;
    }

    public static AlertDialog createClipboardDialog(Context context) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("Last 100 clipboard copies:").setView(scrollView).setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        scrollView.addView(new ClipboardListView(context, alertDialog));
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        return alertDialog;
    }
}
