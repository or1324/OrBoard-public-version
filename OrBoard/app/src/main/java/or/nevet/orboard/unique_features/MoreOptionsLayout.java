package or.nevet.orboard.unique_features;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import or.nevet.orboard.R;
import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.ConvertPixelUnits;

public class MoreOptionsLayout extends LinearLayout {

    private final ImageButton clipboard;
    private final ImageButton sentences;
    private final ImageButton loveButton;

    public MoreOptionsLayout(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        clipboard = new ImageButton(context);
        createClipboard(context);
        sentences = new ImageButton(context);
        createSentences(context);
        loveButton = new ImageButton(context);
        if (Constants.forDaniella) {
            createLoveButton(context);
        } else
            loveButton.setVisibility(GONE);
    }

    private void createClipboard(Context context) {
        clipboard.setBackground(AppCompatResources.getDrawable(context, R.drawable.special_button));
        clipboard.setLayoutParams(new ConstraintLayout.LayoutParams(ConvertPixelUnits.getPixelsFromDp(50, context), ConvertPixelUnits.getPixelsFromDp(50, context)));
        clipboard.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        clipboard.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.clipboard));
        addView(clipboard);
    }

    private void createSentences(Context context) {
        sentences.setBackground(AppCompatResources.getDrawable(context, R.drawable.special_button));
        sentences.setLayoutParams(new ConstraintLayout.LayoutParams(ConvertPixelUnits.getPixelsFromDp(50, context), ConvertPixelUnits.getPixelsFromDp(50, context)));
        sentences.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        sentences.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.sentences_icon));
        addView(sentences);
    }

    private void createLoveButton(Context context) {
        loveButton.setBackground(AppCompatResources.getDrawable(context, R.drawable.special_button));
        loveButton.setLayoutParams(new ConstraintLayout.LayoutParams(ConvertPixelUnits.getPixelsFromDp(50, context), ConvertPixelUnits.getPixelsFromDp(50, context)));
        loveButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        loveButton.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.love_icon));
        addView(loveButton);
    }

    public void setOnClipboardListClickListener(View.OnClickListener clipboardListClickListener) {
        clipboard.setOnClickListener(clipboardListClickListener);
    }

    public void setOnSentencesClickListener(View.OnClickListener sentencesButtonClickListener) {
        sentences.setOnClickListener(sentencesButtonClickListener);
    }

    public void setLoveButtonClickListener(View.OnClickListener loveButtonClickListener) {
        loveButton.setOnClickListener(loveButtonClickListener);
    }
}
