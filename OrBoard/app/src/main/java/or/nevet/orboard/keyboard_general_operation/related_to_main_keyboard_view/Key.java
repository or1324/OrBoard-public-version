package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import or.nevet.orboard.general_helpers.ConvertPixelUnits;
import or.nevet.orboard.R;
import or.nevet.orboard.general_helpers.Constants;

//A transparent view which has a visible view inside of it. The outer view is clickable and there is a possibility to change the distance of the inner view from the sides (the outer padding).
public class Key extends ConstraintLayout {

    private final KeyInnerTextView innerTextView;
    private final char keyCharacter;
    public Key(@NonNull Context context, String text, float textSize, char keyCharacter) {
        super(context);
        this.keyCharacter = keyCharacter;
        setClickable(true);
        setFocusable(true);
        innerTextView = new KeyInnerTextView(context, text, textSize);
        addView(innerTextView);
        innerTextView.setId(View.generateViewId());
        innerTextView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        innerTextView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
        setGravity(Gravity.CENTER);
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setOuterPadding(Constants.keyOuterPadding);
        setInnerPadding(Constants.keyInnerPadding);
        setMaxLines(1);
        setInnerBackground(AppCompatResources.getDrawable(getContext(), R.drawable.keyboard_button));
        setTextColor(Color.WHITE);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                System.out.println(hasFocus);
            }
        });
    }

    public char getCharacter() {
        return keyCharacter;
    }

    public void setInnerPadding(int paddingInDp) {
        int paddingInPx = ConvertPixelUnits.getPixelsFromDp(paddingInDp, getContext());
        innerTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    }
    public void setOuterPadding(int paddingInDp) {
        int paddingInPx = ConvertPixelUnits.getPixelsFromDp(paddingInDp, getContext());
        setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    }

    public void capsLock() {
        innerTextView.setText(innerTextView.getText().toString().toUpperCase());
    }

    public void removeCapsLock() {
        innerTextView.setText(innerTextView.getText().toString().toLowerCase());
    }

    public void setInnerBackground(Drawable background) {
        innerTextView.setBackground(background);
    }


    public void setGravity(int gravity) {
        innerTextView.setGravity(gravity);
    }
    public void setTextAlignment(int textAlignment) {
        innerTextView.setTextAlignment(textAlignment);
    }


    public void setMaxLines(int maxLines) {
        innerTextView.setMaxLines(maxLines);
    }

    public void setTextColor(int color) {
        innerTextView.setTextColor(color);
    }

    public CharSequence getText() {
        return innerTextView.getText();
    }

}
