package de.smartwindow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.opencv.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

import nu.pattern.OpenCV;

public class ImageImprovement {

    static void init() {
        OpenCV.loadShared();
        // Imgcodecs codex = new Imgcodecs();
        // Mat loaded = codex.imread("/tmp/test.png");
        // // Mat detectedEdges = new Mat();
        // // Imgproc.Canny(loaded,detectedEdges,30,60);
        // Mat blured1 = new Mat();
        // Mat blured2 = new Mat();
        // Imgproc.GaussianBlur(loaded, blured1, new Size(0, 0), 1, 1);
        // Imgproc.GaussianBlur(loaded, blured2, new Size(0, 0), 200, 200);
        // Mat dst = new Mat(loaded.size(), loaded.type());
        // Core.subtract(blured1, blured2, dst);
        // Core.multiply(dst, new Scalar(4), dst);
        // codex.imwrite("/tmp/exampleProcessed.png", dst);
    }

    static byte[] processImage(byte[] image) throws IOException {
        File tmp = File.createTempFile("tmpImage", ".png");
        Files.write(tmp.toPath(), image);
        Mat loaded = Imgcodecs.imread(tmp.getAbsolutePath());
        Imgcodecs codex = new Imgcodecs();
        Mat blured1 = new Mat();
        Mat blured2 = new Mat();
        Imgproc.GaussianBlur(loaded, blured1, new Size(0, 0), 0.1, 0.1);
        Imgproc.GaussianBlur(loaded, blured2, new Size(0, 0), 200, 200);
        Mat dst = new Mat(loaded.size(), loaded.type());
        Core.subtract(blured1, blured2, dst);
        Core.multiply(dst, new Scalar(6), dst);
        codex.imwrite(tmp.getAbsolutePath(), dst);
        byte[] toReturn= Files.readAllBytes(tmp.toPath());
        tmp.delete();
        return toReturn;
    }
}
