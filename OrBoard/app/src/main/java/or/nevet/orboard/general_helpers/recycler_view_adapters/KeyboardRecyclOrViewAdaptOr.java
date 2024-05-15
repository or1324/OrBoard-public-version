package or.nevet.orboard.general_helpers.recycler_view_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import or.nevet.orboard.keyboard_general_operation.AudioAndHapticFeedbackManager;
import or.nevet.orboard.R;
import or.nevet.orboard.keyboard_general_operation.CurrentUserFocusedInputService;

public class KeyboardRecyclOrViewAdaptOr extends RecyclerView.Adapter<KeyboardRecyclOrViewAdaptOr.ViewHoldOr> {

    Context context;
    String[] texts;
    CurrentUserFocusedInputService inputBox;

    public KeyboardRecyclOrViewAdaptOr(Context context, String[] texts, CurrentUserFocusedInputService inputBox) {
        this.context = context;
        this.texts = texts;
        this.inputBox = inputBox;
    }

    @NonNull
    @Override
    public ViewHoldOr onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row_sentences, parent, false);
        return new ViewHoldOr(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoldOr holder, int position) {
        holder.button.setText(texts[position]);
        final int finalPosition = position;
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Remove all of the text in the current textbox
                inputBox.deleteAll();
                //Set the text of the sentence in the textbox
                inputBox.sendText(texts[finalPosition]);
                //Make haptic and audio feedback
                AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback();
            }
        });
    }

    @Override
    public int getItemCount() {
        return texts.length;
    }

    public static class ViewHoldOr extends RecyclerView.ViewHolder {

        TextView button;

        public ViewHoldOr(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button_text);
        }
    }
}
