package or.nevet.orboard.general_helpers;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;

public class BackgroundRunningHelper {

    public static Thread runCodeInBackgroundAsync(Runnable r) {
        Thread t = new Thread(r);
        t.start();
        return t;
    }

    //If already on Ui, it will not be async. Otherwise it will be async.
    public static void runCodeOnUiThread(Runnable r) {
        if (isOnUiThread())
            r.run();
        else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(r);
        }
    }

    public static void runCodeOnUiThreadAsync(Runnable r) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(r);
    }

    public static Thread runOnUiAfterSomeTimeAsync(Runnable r, long timeInMillis) {
        return runCodeInBackgroundAsync(() -> {
            try {
                Thread.sleep(timeInMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runCodeOnUiThread(() -> r.run());
        });
    }

    public static boolean isOnUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void runCodeOnUiThreadSync(Runnable r) {
        if (isOnUiThread())
            r.run();
        else {
            CountDownLatch latch = new CountDownLatch(1);
                BackgroundRunningHelper.runCodeOnUiThread(() -> {
                    r.run();
                    latch.countDown();
                });
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
