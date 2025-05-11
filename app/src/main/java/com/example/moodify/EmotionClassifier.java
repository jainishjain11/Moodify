package com.example.moodify;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class EmotionClassifier {
    private Interpreter interpreter;
    private static final String MODEL_NAME = "emotion_model.tflite";

    public EmotionClassifier(Context context) throws IOException {
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(2); // Enable threading for performance
        interpreter = new Interpreter(loadModelFile(context), options);
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
    }

    public String predictEmotion(float[][] inputImage) {
        // Dynamically get the output tensor shape
        int[] outputShape = interpreter.getOutputTensor(0).shape();
        int rows = outputShape[0]; // First dimension
        int cols = outputShape[1]; // Second dimension

        // Adjust output array based on model's actual output shape
        float[][] output = new float[rows][cols];
        interpreter.run(inputImage, output);

        // Assuming we only need the first row for classification
        float[] firstRow = output[0];

        // Find the index with the maximum value
        int maxIndex = 0;
        for (int i = 1; i < firstRow.length; i++) {
            if (firstRow[i] > firstRow[maxIndex]) {
                maxIndex = i;
            }
        }

        // Ensure emotion labels match the number of output categories
        String[] emotions = {"Angry", "Disgust", "Fear", "Happy", "Neutral", "Sad", "Surprise"};

        // Handle cases where model outputs more categories than expected
        if (maxIndex >= emotions.length) {
            return "Unknown Emotion";
        }

        return emotions[maxIndex];
    }
}
