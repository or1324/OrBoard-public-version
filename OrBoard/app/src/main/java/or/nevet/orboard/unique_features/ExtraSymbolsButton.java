package or.nevet.orboard.unique_features;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.Button;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import java.util.HashMap;

import or.nevet.orboard.R;
import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.Pair;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayout;

public class ExtraSymbolsButton extends Button {
    public ExtraSymbolsButton(Context context, Pair<String, Integer> symbolsOrdering) {
        super(context);
        setBackground(AppCompatResources.getDrawable(context, R.drawable.special_button));
        setTextColor(Color.WHITE);
        setAutoSizeTextTypeUniformWithConfiguration(1, 100, 1, TypedValue.COMPLEX_UNIT_PX);
        setMaxLines(symbolsOrdering.getSecond());
        setText(symbolsOrdering.getFirst());
    }
}
