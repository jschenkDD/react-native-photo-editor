
package ui.photoeditor;

import com.ahmedadeltito.photoeditor.PhotoEditorActivity;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import java.util.ArrayList;

public class RNPhotoEditorModule extends ReactContextBaseJavaModule {

    private static final int PHOTO_EDITOR_REQUEST = 1;
    private static final String E_PHOTO_EDITOR_CANCELLED = "E_PHOTO_EDITOR_CANCELLED";


    private Callback mDoneCallback;
    private Callback mCancelCallback;

    public static final String DONE_TEXT = "doneText";
    public static final String SAVE_TEXT = "saveText";
    public static final String CLEAR_ALL_TEXT = "clearAllText";
    public static final String CONTINUE_TEXT = "continueText";
    public static final String NOT_NOW_TEXT = "notNowText";
    public static final String UNDO_TEXT = "undoText";
    public static final String ERASE_TEXT = "eraseText";

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            if (requestCode == PHOTO_EDITOR_REQUEST) {
                if (resultCode == Activity.RESULT_CANCELED && mCancelCallback != null) {
                    mCancelCallback.invoke();
                } else if (mDoneCallback != null) {
                    String imagePath = null;
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        imagePath = extras.getString(PhotoEditorActivity.IMAGE_PATH_KEY);
                    }
                    mDoneCallback.invoke(imagePath);
                }
                mCancelCallback = null;
                mDoneCallback = null;
            }
        }
    };

    public RNPhotoEditorModule(ReactApplicationContext reactContext) {
        super(reactContext);

        reactContext.addActivityEventListener(mActivityEventListener);

    }


    @Override
    public String getName() {
        return "RNPhotoEditor";
    }

    @ReactMethod
    public void Edit(final ReadableMap props, final Callback onDone, final Callback onCancel) {
        String path = props.getString("path");

        //Process Stickers
        ReadableArray stickers = props.getArray("stickers");
        ArrayList<Integer> stickersIntent = new ArrayList<Integer>();

        for (int i = 0; i < stickers.size(); i++) {
            int drawableId = getReactApplicationContext().getResources().getIdentifier(stickers.getString(i), "drawable", getReactApplicationContext().getPackageName());

            stickersIntent.add(drawableId);
        }

        //Process Hidden Controls
        ReadableArray hiddenControls = props.getArray("hiddenControls");
        ArrayList hiddenControlsIntent = new ArrayList<>();

        for (int i = 0; i < hiddenControls.size(); i++) {
            hiddenControlsIntent.add(hiddenControls.getString(i));
        }

        //Process Colors
        ReadableArray colors = props.getArray("colors");
        ArrayList colorPickerColors = new ArrayList<>();

        for (int i = 0; i < colors.size(); i++) {
            colorPickerColors.add(Color.parseColor(colors.getString(i)));
        }

        Intent intent = new Intent(getCurrentActivity(), PhotoEditorActivity.class);
        intent.putExtra("selectedImagePath", path);
        intent.putExtra("colorPickerColors", colorPickerColors);
        intent.putExtra("hiddenControls", hiddenControlsIntent);
        intent.putExtra("stickers", stickersIntent);
        intent.putExtra(DONE_TEXT, props.hasKey(DONE_TEXT) ? props.getString(DONE_TEXT) : null);
        intent.putExtra(SAVE_TEXT, props.hasKey(SAVE_TEXT) ? props.getString(SAVE_TEXT) : null);
        intent.putExtra(CLEAR_ALL_TEXT, props.hasKey(CLEAR_ALL_TEXT) ? props.getString(CLEAR_ALL_TEXT) : null);
        intent.putExtra(CONTINUE_TEXT, props.hasKey(CONTINUE_TEXT) ? props.getString(CONTINUE_TEXT) : null);
        intent.putExtra(NOT_NOW_TEXT, props.hasKey(NOT_NOW_TEXT) ? props.getString(NOT_NOW_TEXT) : null);
        intent.putExtra(UNDO_TEXT, props.hasKey(UNDO_TEXT) ? props.getString(UNDO_TEXT) : null);
        intent.putExtra(ERASE_TEXT, props.hasKey(ERASE_TEXT) ? props.getString(ERASE_TEXT) : null);

        mCancelCallback = onCancel;
        mDoneCallback = onDone;

        getCurrentActivity().startActivityForResult(intent, PHOTO_EDITOR_REQUEST);
    }
}
