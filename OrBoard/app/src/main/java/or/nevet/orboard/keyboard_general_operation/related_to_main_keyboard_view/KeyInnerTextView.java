package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import or.nevet.orboard.general_helpers.Constants;

public class KeyInnerTextView extends TextView {
    public KeyInnerTextView(Context context, String text, float textSize) {
        super(context);
        Typeface font = ResourcesCompat.getFont(getContext(), Constants.keysFontId);
        setTypeface(font);
        setText(text);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)textSize);
    }
}
