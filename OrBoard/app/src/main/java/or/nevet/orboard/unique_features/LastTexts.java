package or.nevet.orboard.unique_features;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;

import or.nevet.orboard.general_helpers.CommonAlgorithms;
import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.ExternalStorage;
import or.nevet.orboard.general_helpers.recycler_view_adapters.LastTextsRecyclOrViewAdaptOr;

public class LastTexts extends RecyclerView {

    public LastTexts(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void showTexts() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        setLayoutManager(manager);
        String concatenatedLines;
        try {
            concatenatedLines = ExternalStorage.AppDirectoryOperations.readFileFromAppDirectory(getContext(), Constants.lastTextsFileName);
        } catch (FileNotFoundException e) {
            concatenatedLines = "";
        }
        String[] lines = concatenatedLines.split("\n");
        CommonAlgorithms<String> commonAlgorithms = new CommonAlgorithms<>();
        commonAlgorithms.reverseArray(lines);
        LastTextsRecyclOrViewAdaptOr adaptOr = new LastTextsRecyclOrViewAdaptOr(lines, getContext());
        setAdapter(adaptOr);
    }
}
