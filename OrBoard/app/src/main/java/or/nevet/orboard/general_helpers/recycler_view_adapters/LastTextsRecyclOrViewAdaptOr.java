package or.nevet.orboard.general_helpers.recycler_view_adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import or.nevet.orboard.R;
import or.nevet.orboard.general_helpers.UserMessages;

public class LastTextsRecyclOrViewAdaptOr extends RecyclerView.Adapter<LastTextsRecyclOrViewAdaptOr.ViewHolder> {

    private String[] texts;
    private Context context;

    public LastTextsRecyclOrViewAdaptOr(String[] texts, Context context) {
        this.texts = texts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_row_last_texts, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(texts[position]);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText("saved text", holder.textView.getText()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return texts.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.button_text);
        }
    }
}
