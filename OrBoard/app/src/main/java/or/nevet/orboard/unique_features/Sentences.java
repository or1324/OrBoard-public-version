package or.nevet.orboard.unique_features;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;

import or.nevet.orboard.general_helpers.recycler_view_adapters.KeyboardRecyclOrViewAdaptOr;
import or.nevet.orboard.keyboard_general_operation.CurrentUserFocusedInputService;
import or.nevet.orboard.R;
import or.nevet.orboard.general_helpers.UserMessages;

public class Sentences extends RecyclerView {
    CurrentUserFocusedInputService inputBox;
    Toast toast = null;
    String[] sentences = null;
    boolean loading = false;

    public Sentences(Context context) {
        super(context);
    }



    public String[] getSentences() {
        return sentences;
    }

    public void setSentences(String[] sentences) {
        this.sentences = sentences;
    }

    public void setInputBox(CurrentUserFocusedInputService inputBox) {
        this.inputBox = inputBox;
    }

    public void loadSentences(final String currentText) {
        if (!loading) {
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Get all the sentences that contain the text that the user wrote
                    try {
                        loading = true;
                        InputStream inputStream = getContext().getResources().openRawResource(R.raw.sentences);
                        int size = inputStream.available();
                        byte[] buffer = new byte[size];
                        inputStream.read(buffer);
                        String str = new String(buffer);
                        sentences = str.split("\r\n");
                        String[] tmp = sentences.clone();
                        sentences = null;
                        int length = 0;
                        for (String sentence : tmp) {
                            if (sentence.toLowerCase().contains(currentText.toLowerCase())) {
                                length++;
                            }
                        }
                        sentences = new String[length];
                        int index = 0;
                        for (String sentence : tmp) {
                            if (sentence.toLowerCase().contains(currentText.toLowerCase())) {
                                sentences[index++] = sentence;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        loading = false;
                    }
                    post(new Runnable() {
                        @Override
                        public void run() {
                            //if (text.isEmpty()) {
                            if (inputBox != null) {
                                if (sentences == null || sentences.length == 0) {
                                    //Say to the user that there is no sentence with the text that he wrote
                                    UserMessages.showToastMessage("There is no sentence with this text", inputBox);
                                    inputBox.getOrBoardView().hideSentences();
                                } else {
                                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                    setLayoutManager(manager);
                                    KeyboardRecyclOrViewAdaptOr adaptOr = new KeyboardRecyclOrViewAdaptOr(getContext(), sentences, inputBox);
                                    setAdapter(adaptOr);
                                }
                            }
                        }
                    });
                }
            });
            t.start();
        }
    }
}
