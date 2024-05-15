package or.nevet.orboard.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import or.nevet.orboard.R;
import or.nevet.orboard.general_helpers.Constants;
import or.nevet.orboard.general_helpers.ConvertPixelUnits;
import or.nevet.orboard.general_helpers.ExternalStorage;
import or.nevet.orboard.general_helpers.SharedPreferencesStorage;
import or.nevet.orboard.general_helpers.UserMessages;
import or.nevet.orboard.keyboard_general_operation.CurrentUserFocusedInputService;
import or.nevet.orboard.keyboard_general_operation.OrBoardView;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.Keyboard;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardGraphicCreation;
import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayoutManager;
import or.nevet.orboard.unique_features.LastTexts;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBar;
    TextView chosenHeight;
    AppCompatButton save;
    private int currentProgress;
    AppCompatButton changeBackground;
    ImageView currentBackground;
    LastTexts lastTexts;

    ActivityResultLauncher<Intent> askForOverlayPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        askForOverlayPermission.launch(intent);
                    }
                }
            });

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        currentBackground.setImageBitmap(bitmap);
                        ExternalStorage.AppDirectoryOperations.saveBitmapToAppDirectory(bitmap, Constants.keyboardBackgroundFileName, MainActivity.this);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = findViewById(R.id.seekBar);
        chosenHeight = findViewById(R.id.chosen_height);
        save = findViewById(R.id.save);
        changeBackground = findViewById(R.id.change_background);
        currentBackground = findViewById(R.id.current_background);
        lastTexts = findViewById(R.id.last_texts);
        currentBackground.setImageDrawable(Keyboard.getCurrentKeyboardBackground(this));
        currentProgress = KeyboardGraphicCreation.getKeyboardHeightPercentageOfScreen(this);
        chosenHeight.setText(String.valueOf(currentProgress));
        seekBar.setProgress(currentProgress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProgress = progress;
                chosenHeight.setText(String.valueOf(currentProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        save.setOnClickListener(v -> {
            int currentHeight = currentProgress;
            KeyboardGraphicCreation graphicCreation = new KeyboardGraphicCreation(new KeyboardLayoutManager(getApplicationContext()));
            graphicCreation.saveKeyboardHeight(currentHeight);
            graphicCreation.calculateAndSaveKeyTextSizesForVerticalLayouts(ConvertPixelUnits.getPixelsFromPercentageOfScreenHeight(this, KeyboardGraphicCreation.getKeyboardHeightPercentageOfScreen(this), false), MainActivity.this);
            graphicCreation.calculateAndSaveKeyTextSizesForHorizontalLayouts(ConvertPixelUnits.getPixelsFromPercentageOfScreenHeight(this, KeyboardGraphicCreation.getCurrentKeyboardHeightInPixels(this), true), MainActivity.this);
            CurrentUserFocusedInputService inputService = CurrentUserFocusedInputService.getInstance();
            if (inputService != null && inputService.isInitialized())
                inputService.refreshKeyboardHeight();
            chosenHeight.setText(String.valueOf(currentHeight));
            UserMessages.showToastMessage("height saved! :)", this);
        });
        changeBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        ComponentName defaultInputMethod = ComponentName.unflattenFromString(id);
        ComponentName myInputMethod = new ComponentName(this, CurrentUserFocusedInputService.class);
        if (!myInputMethod.equals(defaultInputMethod)) {
            new AlertDialog.Builder(this).setMessage("Please set this keyboard as your default keyboard").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_INPUT_METHOD_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            }).create().show();
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            askForOverlayPermission.launch(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        lastTexts.showTexts();
    }
}