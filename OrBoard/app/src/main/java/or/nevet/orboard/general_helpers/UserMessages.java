package or.nevet.orboard.general_helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import java.util.LinkedHashMap;

import or.nevet.orboard.general_helpers.BackgroundRunningHelper;

public class UserMessages {

    static Toast toast = null;

    public static void showEmptyDialogMessage(String message, Context context) {
        BackgroundRunningHelper.runCodeOnUiThread(() -> new AlertDialog.Builder(context).setMessage(message).show());
    }

    public static void showButtonDialogQuestion(String message, Context context, Runnable ifYes) {
        BackgroundRunningHelper.runCodeOnUiThread(() -> new AlertDialog.Builder(context).setMessage(message).setPositiveButton("Yes", (dialog, which) -> ifYes.run()).show());
    }

    public static void showButtonsDialogOptions(String message, Context context, LinkedHashMap<String, Runnable> buttons) {
        BackgroundRunningHelper.runCodeOnUiThread(() -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(message);
            String[] buttonStrings = new String[buttons.size()];
            buttons.keySet().toArray(buttonStrings);
            alertDialogBuilder.setItems(buttonStrings, (dialog, which) -> {
                String clickedButtonText = buttonStrings[which];
                buttons.get(clickedButtonText).run();
            });
            alertDialogBuilder.create().show();
        });
    }

    public static void showToastMessage(String message, Context context) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
