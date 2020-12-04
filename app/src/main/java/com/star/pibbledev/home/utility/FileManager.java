package com.star.pibbledev.home.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;

import com.star.pibbledev.services.network.ScalingUtilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class FileManager {

    public static byte[] getFileDataFromPath(String path) {
        File file = new File(path);
        //init array with file length
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesArray;
    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    public static Bitmap getResizeImage(Context context, int dstWidth, int dstHeight, ScalingUtilities.ScalingLogic scalingLogic, boolean rotationNeeded, String currentPhotoPath) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            bmOptions.inJustDecodeBounds = false;
//            if (bmOptions.outWidth < dstWidth && bmOptions.outHeight < dstHeight) {
//                Bitmap bitmap;
//                bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
//                return setSelectedImage(bitmap, context, currentPhotoPath);
//            } else {
                Bitmap unscaledBitmap = ScalingUtilities.decodeResource(currentPhotoPath, dstWidth, dstHeight, scalingLogic);
                Matrix matrix = new Matrix();
                if (rotationNeeded) {
                    matrix.setRotate(getCameraPhotoOrientation(context, currentPhotoPath));
                    unscaledBitmap = Bitmap.createBitmap(unscaledBitmap, 0, 0, unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), matrix, false);
                }

                Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, dstWidth, dstHeight, scalingLogic);
                unscaledBitmap.recycle();
                return scaledBitmap;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getRotateImage(Context context, ScalingUtilities.ScalingLogic scalingLogic, boolean rotationNeeded, String currentPhotoPath) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            bmOptions.inJustDecodeBounds = false;
//            if (bmOptions.outWidth < dstWidth && bmOptions.outHeight < dstHeight) {
//                Bitmap bitmap;
//                bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
//                return setSelectedImage(bitmap, context, currentPhotoPath);
//            } else {
            Bitmap unscaledBitmap = ScalingUtilities.decodeResource(currentPhotoPath, bmOptions.outWidth, bmOptions.outHeight, scalingLogic);
            Matrix matrix = new Matrix();
            if (rotationNeeded) {
                matrix.setRotate(getCameraPhotoOrientation(context, currentPhotoPath));
                unscaledBitmap = Bitmap.createBitmap(unscaledBitmap, 0, 0, unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), matrix, false);
            }

//            Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, bmOptions.outWidth, bmOptions.outHeight, scalingLogic);
//            unscaledBitmap.recycle();
            return unscaledBitmap;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap setSelectedImage(Bitmap orignalBitmap, Context context, String imagePath) {
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (manufacturer.equalsIgnoreCase("samsung") || model.equalsIgnoreCase("samsung")) {
                Bitmap mBitmap = rotateBitmap(context, orignalBitmap, imagePath);
                return mBitmap;
            } else {
                return orignalBitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return orignalBitmap;
        }

    }


    private static Bitmap rotateBitmap(Context context, Bitmap bit, String imagePath) {

        int rotation = getCameraPhotoOrientation(context, imagePath);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        Bitmap orignalBitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);
        return orignalBitmap;
    }

    public static int getCameraPhotoOrientation(Context context, String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotate = 0;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static int getSizeBitmap(String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(path).getAbsolutePath(), options);

        int width = 0;
        int height = 0;

        int rotation = getCameraPhotoOrientation(null, path);

        if (rotation == 0 || rotation == 180) {

            width = options.outWidth;
            height = options.outHeight;

        } else if (rotation == 90 || rotation == 270) {

            width = options.outHeight;
            height = options.outWidth;

        }

        if (width >= 1920 && height >= 1920) {

            if (width >= height) {
                height = 1920 * height / width;
                width = 1920;
            } else {
                width = 1920 * width / height;
                height = 1920;
            }
        }

        if (width * height > 20000000) {

            double size = width * height;

            float rate = (float) (size / 20000000);

            double width_f = width / rate;
            double height_f = height / rate;

            return (int) width_f * 100000 + (int) height_f;
        }

        return (int) width * 100000 + (int) height;
    }

    public static int getTargetImageSize(String path){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(path).getAbsolutePath(), options);

        int imageHeight = 0;
        int imageWidth = 0;

        int rotation = getCameraPhotoOrientation(null, path);

        if (rotation == 0 || rotation == 180) {

            imageWidth = options.outWidth;
            imageHeight = options.outHeight;

        } else if (rotation == 90 || rotation == 270) {

            imageWidth = options.outHeight;
            imageHeight = options.outWidth;

        }

        double tem_width, tem_height, result_width, result_height;

        float ratio = (float) imageWidth / imageHeight;

        if(ratio < 0.8) {
            tem_width = imageWidth;
            tem_height = imageWidth / 0.8;
        } else if (ratio > 1.91) {
            tem_height = imageHeight;
            tem_width = imageHeight * 1.91;
        } else {
            tem_width = imageWidth;
            tem_height = imageHeight;
        }

        if (tem_width > tem_height) {

            if (tem_width > 1080) result_width = 1080;
            else if (tem_width < 320) result_width = 320;
            else result_width = tem_width;

            result_height = result_width * tem_height / tem_width;

        } else {
            if (tem_height > 1080) result_height = 1080;
            else if (tem_height < 320) result_height = 320;
            else result_height = tem_height;

            result_width = result_height * tem_width / tem_height;
        }

        return (int) result_width * 100000 + (int) result_height;
    }

    //-----for digitalgoods----

    public static int getChangedImageSize(String path){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(path).getAbsolutePath(), options);

        int imageHeight = 0;
        int imageWidth = 0;

        int rotation = getCameraPhotoOrientation(null, path);

        if (rotation == 0 || rotation == 180) {

            imageWidth = options.outWidth;
            imageHeight = options.outHeight;

        } else if (rotation == 90 || rotation == 270) {

            imageWidth = options.outHeight;
            imageHeight = options.outWidth;

        }

        double tem_width, tem_height;

        if(imageWidth/imageHeight < 0.8) {
            tem_width = imageWidth;
            tem_height = imageWidth / 0.8;
        } else if (imageWidth/imageHeight > 1.91) {
            tem_height = imageHeight;
            tem_width = imageHeight * 1.91;
        } else {
            tem_width = imageWidth;
            tem_height = imageHeight;
        }

        return (int) tem_width * 100000 + (int) tem_height;

    }

    public static int getTargetVideoSize(int width, int height){

        double tem_width, tem_height, result_width, result_height;

        if(width/height < 0.8) {
            tem_width = width;
            tem_height = width / 0.8;

            result_width = 600;
            result_height = result_width / 0.8;

        } else if (width/height > 1.91) {
            tem_height = height;
            tem_width = height * 1.91;

            result_width = 600;
            result_height = 600 / 1.91;

        } else {
            tem_width = width;
            tem_height = height;

            result_width = 600;
            result_height = (600 * height)/width;
        }

//        if (tem_width > tem_height) {
//
//            if (tem_width > 640) result_width = 640;
//            else if (tem_width < 320) result_width = 320;
//            else result_width = tem_width;
//
//            result_height = result_width * tem_height / tem_width;
//
//        } else {
//
//            if (tem_height > 640) result_height = 640;
//            else if (tem_height < 320) result_height = 320;
//            else result_height = tem_height;
//
//            result_width = result_height * tem_width / tem_height;
//        }


        return (int) result_width * 100000 + (int) result_height;
    }


}
