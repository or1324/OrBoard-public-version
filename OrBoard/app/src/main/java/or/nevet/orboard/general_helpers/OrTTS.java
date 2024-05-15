package or.nevet.orboard.general_helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

import or.nevet.orboard.listeners.TTSListener;


public class OrTTS {
    private static TextToSpeech tts;
    private static boolean initializing;

    private static void initializeTTS(Context context, TTSListener listener) {
        initializing = true;
        tts = new TextToSpeech(context, status -> {
            initializing = false;
            if (status == -1) {
                listener.onFinish(false);
            } else {
                init();
                listener.onFinish(true);
            }
        });
    }

    private static void init() {
        tts.setLanguage(new Locale("iw"));
        tts.setSpeechRate(1f);
    }

    public static void createTTS(Context context, TTSListener listener) {
        if (tts == null && !initializing) {
            initializeTTS(context, listener);
        } else if (initializing && tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
            initializeTTS(context, listener);
        } else
            listener.onFinish(true);
    }

    public static TTSHelper getTTSHelper() {
        if (tts != null && !initializing)
            return new TTSHelper(tts);
        throw new RuntimeException("The tts is not ready. Make sure that you called createTTS and waited for it to finish!");
    }

    public static class TTSHelper {
        TextToSpeech tts;
        private TTSHelper(TextToSpeech tts) {
            this.tts = tts;
        }

        public void readText(String text) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }

    }
}
