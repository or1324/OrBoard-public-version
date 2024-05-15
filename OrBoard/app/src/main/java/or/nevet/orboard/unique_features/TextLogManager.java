package or.nevet.orboard.unique_features;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.inputmethod.InputConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.ExternalStorage;
import or.nevet.orboard.keyboard_general_operation.CurrentUserFocusedInputService;
import or.nevet.orboard.listeners.OnTextChangedListener;

public class TextLogManager implements OnTextChangedListener {
    private String lastText;
    private static final int numOfLinesInLog = 2000;
    private static final int maxTextLength = 1000;
    private final Context context;
    private long lastTimeOfLog;

    public TextLogManager(Context context) {
        this.context = context;
    }

    @Override
    public void textChanged(String newText) {
        long timeOfLog = System.currentTimeMillis();
        if (timeOfLog-lastTimeOfLog > 700) {
            lastTimeOfLog = timeOfLog;
            if (newText.length() > maxTextLength)
                newText = newText.substring(newText.length() - maxTextLength);
            if (!newText.equals(lastText) && !newText.isEmpty()) {
                lastText = newText;
                String fileTexts = null;
                try {
                    fileTexts = ExternalStorage.AppDirectoryOperations.readFileFromAppDirectory(context, Constants.lastTextsFileName);
                } catch (Exception ignored) {
                }
                if (fileTexts == null)
                    fileTexts = "";
                String[] sentences = fileTexts.split("\n");
                newText = newText.trim();
                //add the lines
                fileTexts = fileTexts.concat(newText.trim().concat("\n"));
                int newNumOfLines = fileTexts.split("\n").length;
                if (newNumOfLines >= numOfLinesInLog) {
                    //remove newNumOfLines-numOfLinesInLog lines so that we will stay with numOfLinesInLog lines
                    for (int i = 0; i < newNumOfLines-numOfLinesInLog; i++)
                        fileTexts = fileTexts.substring(sentences[i].length() + "\n".length());
                    ExternalStorage.AppDirectoryOperations.writeFileToAppDirectory(context, Constants.lastTextsFileName, fileTexts);
                } else
                    ExternalStorage.AppDirectoryOperations.appendTextToFileInAppDirectory(context, Constants.lastTextsFileName, newText.concat("\n"));
            }
        }
    }
}
