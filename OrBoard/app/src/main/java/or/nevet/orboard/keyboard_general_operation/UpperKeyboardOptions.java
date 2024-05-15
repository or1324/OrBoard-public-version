package or.nevet.orboard.keyboard_general_operation;


import static or.nevet.orboard.general_helpers.Constants.optionsHeightInInches;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.HashMap;

import or.nevet.orboard.general_helpers.ConvertPixelUnits;
import or.nevet.orboard.R;
import or.nevet.orboard.general_helpers.Pair;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardGraphicCreation;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayout;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayoutManager;
import or.nevet.orboard.unique_features.ExtraSymbolsButton;

public class UpperKeyboardOptions extends ConstraintLayout {

    private final ImageButton lineBreakerButton;
    private final ExtraSymbolsButton[] extraSymbolsButtons;
    private final ImageButton moreOptions;
    private final KeyboardLayoutManager keyboardLayoutManager;
    private final int optionsHeightInPx;

    public UpperKeyboardOptions(@NonNull Context context) {
        super(context);
        keyboardLayoutManager = new KeyboardLayoutManager(context);
        optionsHeightInPx = ConvertPixelUnits.getPixelsFromInches(optionsHeightInInches, context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, optionsHeightInPx));
        setBackgroundColor(Color.BLACK);
        lineBreakerButton = new ImageButton(context);
        createLineBreakerButton(context);
        moreOptions = new ImageButton(context);
        createMoreOptions(context);
        extraSymbolsButtons = new ExtraSymbolsButton[keyboardLayoutManager.getAllExtraSymbolsLayouts().length];
        createExtraSymbolsButtons(context);
    }

    private void createLineBreakerButton(Context context) {
        lineBreakerButton.setLayoutParams(new ConstraintLayout.LayoutParams(KeyboardGraphicCreation.getSpecialButtonWidthByHeight(optionsHeightInPx), optionsHeightInPx));
        lineBreakerButton.setBackground(AppCompatResources.getDrawable(context, R.drawable.special_button));
        lineBreakerButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        lineBreakerButton.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_baseline_arrow_downward_24));
        lineBreakerButton.setId(View.generateViewId());
        addView(lineBreakerButton);
        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        set.connect(lineBreakerButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        set.connect(lineBreakerButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(lineBreakerButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.applyTo(this);
    }

    private void createExtraSymbolsButtons(Context context) {
        int lastButtonId = moreOptions.getId();
        HashMap<KeyboardLayout, Pair<String, Integer>> symbolsOrderingPerLayout;
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            symbolsOrderingPerLayout = KeyboardGraphicCreation.getSavedExtraSymbolsOrderingForVerticalLayouts(context);
        else
            symbolsOrderingPerLayout = KeyboardGraphicCreation.getSavedExtraSymbolsOrderingForHorizontalLayouts(context);
        for (int i = 0; i < extraSymbolsButtons.length; i++) {
            extraSymbolsButtons[i] = new ExtraSymbolsButton(context, symbolsOrderingPerLayout.get(keyboardLayoutManager.getAllExtraSymbolsLayouts()[i].getLanguageTag()));
            extraSymbolsButtons[i].setTag(i);
            extraSymbolsButtons[i].setLayoutParams(new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
            extraSymbolsButtons[i].setId(View.generateViewId());
            addView(extraSymbolsButtons[i]);
            ConstraintSet set = new ConstraintSet();
            set.clone(this);
            set.connect(extraSymbolsButtons[i].getId(), ConstraintSet.LEFT, lastButtonId, ConstraintSet.RIGHT);
            if (i != 0)
                set.connect(lastButtonId, ConstraintSet.RIGHT, extraSymbolsButtons[i].getId(), ConstraintSet.LEFT);
            if (i == extraSymbolsButtons.length-1)
                set.connect(extraSymbolsButtons[i].getId(), ConstraintSet.RIGHT, lineBreakerButton.getId(), ConstraintSet.LEFT);
            set.applyTo(this);
            lastButtonId = extraSymbolsButtons[i].getId();
        }
    }

    private void createMoreOptions(Context context) {
        moreOptions.setBackground(AppCompatResources.getDrawable(context, R.drawable.special_button));
        moreOptions.setLayoutParams(new ConstraintLayout.LayoutParams(KeyboardGraphicCreation.getSpecialButtonWidthByHeight(optionsHeightInPx), optionsHeightInPx));
        moreOptions.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        moreOptions.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.baseline_more_vert_24));
        moreOptions.setId(View.generateViewId());
        addView(moreOptions);
        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        set.connect(moreOptions.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.connect(moreOptions.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(moreOptions.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.applyTo(this);
    }

    public void setOnLineBreakerClickListener(View.OnClickListener lineBreakerClickListener) {
        lineBreakerButton.setOnClickListener(lineBreakerClickListener);
    }

    /** Sets this listener to all of the extra symbols buttons. **/
    public void setOnExtraSymbolsClickListener(View.OnClickListener extraSymbolsClickListener) {
        for (Button extraSymbolsButton : extraSymbolsButtons)
            extraSymbolsButton.setOnClickListener(extraSymbolsClickListener);
    }

    public void setMoreOptionsClickListener(View.OnClickListener moreOptionsClickListener) {
        moreOptions.setOnClickListener(moreOptionsClickListener);
    }

    public ExtraSymbolsButton[] getExtraSymbolsButtons() {
        return extraSymbolsButtons;
    }

}
