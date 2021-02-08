package com.zlm.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.camera2.params.RggbChannelVector;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class PublicUtil {
    private static final String TAG = "PublicUtil";

    /**
     * 迭代删除文件夹
     *
     * @param dirPath 文件夹路径
     */
    public static void deleteDir(String dirPath) {
        File file = new File(dirPath);
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                file.delete();
            } else {
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i].getAbsolutePath());
                }
                file.delete();
            }
        }
    }

    public static String getVideoName() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date(time);
        String t = format.format(date);
        return "GD_" + t + ".mp4";
    }

//    public static String getVideoName() {
//        return TimeUtils.millis2String(System.currentTimeMillis()) + ".mp4";
//    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        if (contentUri.getPath().startsWith("/external/video/media/")) {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            return contentUri.getPath();
        }
    }
    public static class Network {
        public static final int NONETWORK = 0;
        public static final int WIFI = 1;
        public static final int NOWIFI = 2;

        public static int checkNetWorkType(Context context) {
            if (!checkNetWork(context)) {
                return Network.NONETWORK;
            }
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting())
                return Network.WIFI;
            else
                return Network.NOWIFI;
        }

        public static boolean checkNetWork(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return false;
            }
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null || !ni.isAvailable()) {
                return false;
            }
            return true;
        }
    }

    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }

    public static String stringToTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private final static int min_white_balance_temperature_c = 1000;
    private final static int max_white_balance_temperature_c = 15000;

    public static RggbChannelVector colorTemperature(int whiteBalance) {
        float temperature = whiteBalance / 100f;
        float red;
        float green;
        float blue;

        //Calculate red
        if (temperature <= 66)
            red = 255;
        else {
            red = temperature - 60;
            red = (float) (329.698727446 * (Math.pow((double) red, -0.1332047592)));
            if (red < 0)
                red = 0;
            if (red > 255)
                red = 255;
        }


        //Calculate green
        if (temperature <= 66) {
            green = temperature;
            green = (float) (99.4708025861 * Math.log(green) - 161.1195681661);
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        } else {
            green = temperature - 60;
            green = (float) (288.1221695283 * (Math.pow((double) green, -0.0755148492)));
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        }

        //calculate blue
        if (temperature >= 66)
            blue = 255;
        else if (temperature <= 19)
            blue = 0;
        else {
            blue = temperature - 10;
            blue = (float) (138.5177312231 * Math.log(blue) - 305.0447927307);
            if (blue < 0)
                blue = 0;
            if (blue > 255)
                blue = 255;
        }
        return new RggbChannelVector((red / 255) * 2, (green / 255), (green / 255), (blue / 255) * 2);
    }

    public static int[] temperatureToColor(double whiteBalance) {
        double temperature = whiteBalance / 100;
        double red;
        double green;
        double blue;

        //Calculate red
        if (temperature <= 66)
            red = 255;
        else {
            red = temperature - 60;
            red =  329.698727446 * (Math.pow( red, -0.1332047592));
            if (red < 0)
                red = 0;
            if (red > 255)
                red = 255;
        }


        //Calculate green
        if (temperature <= 66) {
            green = temperature;
            green = 99.4708025861 * Math.log(green) - 161.1195681661;
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        } else {
            green = temperature - 60;
            green = 288.1221695283 * (Math.pow(green, -0.0755148492));
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        }

        //calculate blue
        if (temperature >= 66)
            blue = 255;
        else if (temperature <= 19)
            blue = 0;
        else {
            blue = temperature - 10;
            blue = 138.5177312231 * Math.log(blue) - 305.0447927307;
            if (blue < 0)
                blue = 0;
            if (blue > 255)
                blue = 255;
        }
        return new int[]{(int)red,(int)green,(int)blue};
    }


    public static int convertRggbToTemperature(RggbChannelVector rggbChannelVector) {
        Log.d(TAG, "temperature:");
        Log.d(TAG, "    red: " + rggbChannelVector.getRed());
        Log.d(TAG, "    green even: " + rggbChannelVector.getGreenEven());
        Log.d(TAG, "    green odd: " + rggbChannelVector.getGreenOdd());
        Log.d(TAG, "    blue: " + rggbChannelVector.getBlue());
        float red = rggbChannelVector.getRed();
        float green_even = rggbChannelVector.getGreenEven();
        float green_odd = rggbChannelVector.getGreenOdd();
        float blue = rggbChannelVector.getBlue();
        float green = 0.5f * (green_even + green_odd);

        float max = Math.max(red, blue);
        if (green > max)
            green = max;

        float scale = 255.0f / max;
        red *= scale;
        green *= scale;
        blue *= scale;

        int red_i = (int) red;
        int green_i = (int) green;
        int blue_i = (int) blue;
        int temperature;
        if (red_i == blue_i) {
            temperature = 6600;
        } else if (red_i > blue_i) {
            // temperature <= 6600
            int t_g = (int) (100 * Math.exp((green_i + 161.1195681661) / 99.4708025861));
            if (blue_i == 0) {
                temperature = t_g;
            } else {
                int t_b = (int) (100 * (Math.exp((blue_i + 305.0447927307) / 138.5177312231) + 10));
                temperature = (t_g + t_b) / 2;
            }
        } else {
            // temperature >= 6700
            if (red_i <= 1 || green_i <= 1) {
                temperature = max_white_balance_temperature_c;
            } else {
                int t_r = (int) (100 * (Math.pow(red_i / 329.698727446, 1.0 / -0.1332047592) + 60.0));
                int t_g = (int) (100 * (Math.pow(green_i / 288.1221695283, 1.0 / -0.0755148492) + 60.0));
                temperature = (t_r + t_g) / 2;
            }
        }
        temperature = Math.max(temperature, min_white_balance_temperature_c);
        temperature = Math.min(temperature, max_white_balance_temperature_c);
        Log.d(TAG, "    temperature: " + temperature);
        return temperature;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotation = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        return rotation;
    }

    public static int getCheckCode(byte[] bArr) {
        int i = 0;
        for (byte b : bArr) {
            i = CRC8Table[i ^ (b & 0xFF)];
        }
        return i;
    }

    private static int CRC8Table[] = {
            0, 94, 188, 226, 97, 63, 221, 131, 194, 156, 126, 32, 163, 253, 31, 65,
            157, 195, 33, 127, 252, 162, 64, 30, 95, 1, 227, 189, 62, 96, 130, 220,
            35, 125, 159, 193, 66, 28, 254, 160, 225, 191, 93, 3, 128, 222, 60, 98,
            190, 224, 2, 92, 223, 129, 99, 61, 124, 34, 192, 158, 29, 67, 161, 255,
            70, 24, 250, 164, 39, 121, 155, 197, 132, 218, 56, 102, 229, 187, 89, 7,
            219, 133, 103, 57, 186, 228, 6, 88, 25, 71, 165, 251, 120, 38, 196, 154,
            101, 59, 217, 135, 4, 90, 184, 230, 167, 249, 27, 69, 198, 152, 122, 36,
            248, 166, 68, 26, 153, 199, 37, 123, 58, 100, 134, 216, 91, 5, 231, 185,
            140, 210, 48, 110, 237, 179, 81, 15, 78, 16, 242, 172, 47, 113, 147, 205,
            17, 79, 173, 243, 112, 46, 204, 146, 211, 141, 111, 49, 178, 236, 14, 80,
            175, 241, 19, 77, 206, 144, 114, 44, 109, 51, 209, 143, 12, 82, 176, 238,
            50, 108, 142, 208, 83, 13, 239, 177, 240, 174, 76, 18, 145, 207, 45, 115,
            202, 148, 118, 40, 171, 245, 23, 73, 8, 86, 180, 234, 105, 55, 213, 139,
            87, 9, 235, 181, 54, 104, 138, 212, 149, 203, 41, 119, 244, 170, 72, 22,
            233, 183, 85, 11, 136, 214, 52, 106, 43, 117, 151, 201, 74, 20, 246, 168,
            116, 42, 200, 150, 21, 75, 169, 247, 182, 232, 10, 84, 215, 137, 107, 53
    };

    public static String toHexString(byte[] bArr) {
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            if ((bArr[i] & 255) < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(bArr[i] & 255) + ",");
        }
        return sb.toString().toLowerCase();
    }
}
