package com.example.moodify;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageProcessor {
    public static float[][] processImage(Bitmap bitmap) {
        int size = 48; // Model expects 48x48
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

        float[][] input = new float[1][size * size];

        int index = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int pixel = bitmap.getPixel(x, y);
                float grayscale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3.0f / 255.0f;
                input[0][index++] = grayscale;
            }
        }
        return input;
    }
}
