
package or.nevet.orboard.keyboard_general_operation;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.VibrationEffect;
import android.os.Vibrator;

import or.nevet.orboard.R;

/**
 * This class gathers audio feedback and haptic feedback functions.
 *
 * It offers a consistent and simple interface that allows LatinIME to forget about the
 * complexity of settings and the like.
 */
public final class AudioAndHapticFeedbackManager {
    private SoundPool soundPool;

    private int sound;

    private Vibrator mVibrator;

    private static AudioAndHapticFeedbackManager manager;

    public static AudioAndHapticFeedbackManager getInstance() {
        return manager;
    }

    private AudioAndHapticFeedbackManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build();
        soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(300).build();
        sound = soundPool.load(context, R.raw.click, 1);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static void init(final Context context) {
        manager = new AudioAndHapticFeedbackManager(context);
    }

    public void performHapticAndAudioFeedback() {
        performHapticFeedback();
        performAudioFeedback();
    }

    public boolean hasVibrator() {
        return mVibrator != null && mVibrator.hasVibrator();
    }

    public void vibrate(final long milliseconds) {
        if (mVibrator == null || milliseconds == 0) {
            return;
        }
        mVibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public void performAudioFeedback() {
            if (soundPool == null)
                return;
        soundPool.play(sound, 0.1f, 0.1f, 0, 0, 1);
    }

    public void performHapticFeedback() {
        vibrate(50);
    }
}
