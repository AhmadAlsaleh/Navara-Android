package com.smartlife_solutions.android.navara_store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ProjectBasicModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Statics {

    public static String myToken = "";
    public static String fromNotification = "from_notification";
    public static final String language = "language";
    public static final String arabic = "arabic.json";
    public static final String english = "english.json";
    public static String currentLanguage = english;

    public static ProjectBasicModel projectBasicModel;

    public static ProjectBasicModel getProjectBasicModel() {
        return projectBasicModel;
    }

    public static void setProjectBasicModel(ProjectBasicModel projectBasicModel) {
        Statics.projectBasicModel = projectBasicModel;
    }

    public static JSONObject getLanguageJSONObject(Activity activity) throws JSONException {
        SharedPreferences preferences = activity.getSharedPreferences("Navara", Context.MODE_PRIVATE);
        String s = preferences.getString(getCurrentLanguageName(activity), "");
        return new JSONObject(s);
    }

    public static String getCurrentLanguageName(Activity activity) {
        if (activity == null) {
            return currentLanguage;
        }
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Navara", Context.MODE_PRIVATE);
        currentLanguage = sharedPreferences.getString(language, english);
        return currentLanguage;
    }

    public static void setCurrentLanguageName(Activity activity, String name) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("Navara", Context.MODE_PRIVATE).edit();
        editor.putString(language, name);
        editor.apply();
        currentLanguage = name;
    }

    public static String getMyToken() {
        return myToken;
    }

    public static void setMyToken(String myToken) {
        Statics.myToken = myToken;
    }

    public static class ImagePicker {

        private static final int minWidthQuality = 400;
        private static final String TAG = "ImagePicker";
        private static final String TEMP_IMAGE_NAME = "tempImage";

        public static Bitmap getImageFromResult(Context context, int resultCode, Intent imageReturnedIntent) {
            Bitmap bm = null;
            File imageFile = getTempFile(context);
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage;
                boolean isCamera = (imageReturnedIntent == null ||
                    imageReturnedIntent.getData() == null ||
                    imageReturnedIntent.getData().equals(Uri.fromFile(imageFile)));

                if (isCamera) {
                    selectedImage = Uri.fromFile(imageFile);
                } else {
                    selectedImage = imageReturnedIntent.getData();
                }

                bm = getImageResized(context, selectedImage);
                int rotation = getRotation(context, selectedImage, isCamera);
                bm = rotation(bm, rotation);
            }
            return bm;
        }

        private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;

            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return BitmapFactory.decodeFileDescriptor(
                    fileDescriptor != null ? fileDescriptor.getFileDescriptor() : null, null, options);
        }

        private static Bitmap getImageResized(Context context, Uri selectedImage) {
            Bitmap bm = null;
            int[] sampleSizes = new int[] {5,3,2,1};
            int i = 0;
            do {
                bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
                i++;
            } while (bm.getWidth() < minWidthQuality && i < sampleSizes.length);
            return bm;
        }

        private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
            int rotation = 0;
            if (isCamera) {
                rotation = getRotationFromCamera(context, imageUri);
            } else {
//                rotation = getRotationFromGallery(context, imageUri);
            }
            return rotation;
        }

        private static int getRotationFromCamera(Context context, Uri imageUri) {
            int rotation = 0;
            try {
                context.getContentResolver().notifyChange(imageUri, null);
                ExifInterface exifInterface = new ExifInterface(imageUri.getPath());
                int ori = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rotation;
        }

        private static Bitmap rotation(Bitmap bm, int rotation) {
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                return Bitmap.createBitmap(bm, 0 , 0, bm.getWidth(), bm.getHeight(), matrix, true);
            }
            return bm;
        }

        private static File getTempFile(Context context) {
            File imageFile = new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
            imageFile.getParentFile().mkdirs();
            return imageFile;
        }

    }
}