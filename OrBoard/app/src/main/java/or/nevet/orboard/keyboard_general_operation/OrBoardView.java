package or.nevet.orboard.keyboard_general_operation;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Timer;
import java.util.TimerTask;

import or.nevet.orboard.OrBoardInit;
import or.nevet.orboard.R;
import or.nevet.orboard.activities.MainActivity;
import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.ConvertPixelUnits;
import or.nevet.orboard.general_helpers.SharedPreferencesStorage;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardGraphicCreation;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayout;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayoutManager;
import or.nevet.orboard.unique_features.ClipboardLogManager;
import or.nevet.orboard.unique_features.MoreOptionsLayout;
import or.nevet.orboard.unique_features.Sentences;

public class OrBoardView extends ConstraintLayout {

    private final Sentences sentences;
    private final UpperKeyboardOptions options;
    private final CurrentUserFocusedInputService inputBox;

    public OrBoardView(CurrentUserFocusedInputService inputBox) {
        super(inputBox);
        this.inputBox = inputBox;
        sentences = new Sentences(inputBox);
        options = new UpperKeyboardOptions(inputBox);
        options.setOnLineBreakerClickListener(v -> inputBox.sendText("\n"));
        options.setOnExtraSymbolsClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardLayoutManager keyboardLayoutManager = new KeyboardLayoutManager(inputBox);
                KeyboardLayout clickedSymbolsLayout = keyboardLayoutManager.getAllExtraSymbolsLayouts()[Integer.parseInt(v.getTag().toString())];
                if (inputBox.getKeyboard().getCurrentRegularKeyboardLayout() != clickedSymbolsLayout) {
                    inputBox.getKeyboard().setCurrentRegularKeyboardLayout(clickedSymbolsLayout, false);
                    v.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.clicked_special_symbols));
                }
                else {
                    inputBox.getKeyboard().setCurrentRegularKeyboardLayout(keyboardLayoutManager.getCurrentKeyboardLayout(), false);
                    v.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.special_button));
                }
            }
        });
        options.setMoreOptionsClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreOptionsLayout moreOptionsLayout = new MoreOptionsLayout(getContext());
                initMoreOptionsListeners(moreOptionsLayout);
                moreOptionsLayout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
                AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(moreOptionsLayout).create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.create();
                dialog.show();
            }
        });
        addViews();
    }

    public void addViews() {
        sentences.setId(View.generateViewId());
        options.setId(View.generateViewId());
        inputBox.getKeyboard().setId(View.generateViewId());
        addView(sentences);
        addView(options);
        addView(inputBox.getKeyboard());
        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        set.connect(sentences.getId(), ConstraintSet.BOTTOM, options.getId(), ConstraintSet.TOP);
        set.connect(options.getId(), ConstraintSet.BOTTOM, inputBox.getKeyboard().getId(), ConstraintSet.TOP);
        set.connect(inputBox.getKeyboard().getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.applyTo(this);
        hideSentences();
    }

    private void initMoreOptionsListeners(MoreOptionsLayout moreOptionsLayout) {
        moreOptionsLayout.setOnClipboardListClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardLogManager.createClipboardDialog(inputBox).show();
            }
        });
        moreOptionsLayout.setOnSentencesClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areSentencesShown())
                    hideSentences();
                else
                    showSentences(inputBox.getText());
            }
        });
        moreOptionsLayout.setLoveButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View root = inflater.inflate(R.layout.love_reminder, null);
                ((TextView)root.findViewById(R.id.love)).setText("היי דניאלה, מקווה שאת אוהבת את המקלדת :)\n\nאני אוהב אותך❤️");
                root.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
                AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(root).create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.create();
                dialog.show();
            }
        });
    }

    public boolean areSentencesShown() {
        return sentences.getVisibility() == View.VISIBLE;
    }

    public void showSentences(String currentText) {
        sentences.setVisibility(View.VISIBLE);
        sentences.setInputBox(inputBox);
        sentences.loadSentences(currentText);
    }

    public void hideSentences() {
        sentences.removeAllViews();
        if (sentences.getSentences() != null) {
            int length = sentences.getSentences().length;
            sentences.setSentences(null);
            if (sentences.getAdapter() != null)
                sentences.getAdapter().notifyItemRangeRemoved(0, length);
        }
        sentences.setAdapter(null);
        sentences.setInputBox(null);
        sentences.setVisibility(View.GONE);
    }

    public void resetExtras(KeyboardLayout previousLayout) {
        KeyboardLayoutManager keyboardLayoutManager = new KeyboardLayoutManager(inputBox);
        Integer previouslyChosenSymbolsKeyboardIndex = keyboardLayoutManager.getExtraSymbolsLayoutIndex(previousLayout);
        if (previouslyChosenSymbolsKeyboardIndex != null)
            options.getExtraSymbolsButtons()[previouslyChosenSymbolsKeyboardIndex].setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.special_button));
    }

}
