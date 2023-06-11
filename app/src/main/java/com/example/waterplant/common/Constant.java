package com.example.waterplant.common;

import android.Manifest;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Constant {
    public static final UUID REQUEST_ID = new UUID(1,0);

    public static final Integer MAX_QUANTITY_THUMB = 3;
    public static final String CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "channel_name";
    public static final Integer NOTIFICATION_ID = 0;
    public static List<String> REQUEST_PERMISSIONS() {
        List<String> listPermission = new ArrayList<>();
        listPermission.add(Manifest.permission.CAMERA);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listPermission.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            listPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            listPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return listPermission;
    }
    public static final Integer REQUEST_PERMISSION_CODE = 100;
    public static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
}
