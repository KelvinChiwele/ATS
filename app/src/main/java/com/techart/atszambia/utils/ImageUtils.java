package com.techart.atszambia.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import com.techart.atszambia.R;

/**
 * Class for working with images
 * Created by Kelvin on 17/09/2017.
 */

public final class ImageUtils {
    private ImageUtils()
    {

    }

    /**
     * Resolving images to set in recyclerview
     * @param category the catgory to be resolved
     * @return resource value
     */
    public static int getImageUrl(String category){
        switch (category){
            case "Herbicides":
                return R.mipmap.herbicdes;
            case "Efekto":
                return R.mipmap.efekto;
            case "Fungicides":
                return R.mipmap.fungicides;
            case "Adjuvants":
                return R.mipmap.adjuvants;
            case "News":
                return R.mipmap.news;
            case "FAQ":
                return R.mipmap.frequently_asked_questions;
            case "Insecticides":
                return R.mipmap.insecticides;
            case "FoliarFertilizer":
                return R.mipmap.fertilizers;
            case "Crop Programs":
                return R.mipmap.programs;
            default: return R.drawable.logo;
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getRealPathFromUrl(Context context, Uri imageUrl) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(imageUrl,proj,null,null,null);
            int columIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}


