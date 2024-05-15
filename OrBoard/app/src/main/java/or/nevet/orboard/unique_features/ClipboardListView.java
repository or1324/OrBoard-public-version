package or.nevet.orboard.unique_features;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.io.IOException;
import java.util.LinkedList;

import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.ExternalStorage;
import or.nevet.orboard.R;
import or.nevet.orboard.general_helpers.UserMessages;

public class ClipboardListView extends LinearLayoutCompat {



    public ClipboardListView(@NonNull Context context, AlertDialog dialog) {
        super(context);
        setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ClipbOrd clipboard;
        try {
            clipboard = ExternalStorage.AppDirectoryOperations.restoreObjectFromAppDirectory(context, Constants.clipboardFileName);
        } catch (IOException e) {
            clipboard = new ClipbOrd();
        }
        if (clipboard == null)
            clipboard = new ClipbOrd();
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        setShowDividers(SHOW_DIVIDER_MIDDLE);
        setDividerDrawable(AppCompatResources.getDrawable(context, R.drawable.divider));
        LinkedList<String> allItems = clipboard.getAllItems();
        for (String item : allItems) {
            TextView textView = new TextView(context);
            textView.setText(item);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(20);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(10, 10, 10, 10);
            textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setPrimaryClip(ClipData.newPlainText("saved text", textView.getText()));
                    dialog.dismiss();
                }
            });
            addView(textView);
        }
    }
}
