import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageHandler {
    public static void HandleImage(String inputFile, int r, int s) {

        Mat srcImg = Imgcodecs.imread(inputFile);
        if (srcImg.empty()) {
            System.err.println("Erreur: Impossible de charger l'image depuis : " + inputFile);
            return;
        }
        ImageProcess img = new ImageProcess();
        Mat scbImg;
        Mat restImg;

        Path inputPath = Paths.get(inputFile);
        String originalFilename = inputPath.getFileName().toString();
        String outputFilenamePrefix;
        scbImg = img.scrambleImage(srcImg, r, s);
        outputFilenamePrefix = "scrambled_";
        System.out.println("Image scrambled with key (" + r + ", " + s + ")");
        String newFilename = outputFilenamePrefix + originalFilename;
        Path outputDir = inputPath.getParent();
        Path outputPath = (outputDir != null) ? outputDir.resolve(newFilename) : Paths.get(newFilename);
        Imgcodecs.imwrite(outputPath.toString(), Watermark.addWatermark(scbImg, "Scrambled"));
        System.out.println("Résultat sauvegardé dans : " + outputPath.toString());
        restImg = img.unscrambleImage(scbImg, r, s);
        outputFilenamePrefix = "unscrambled_";
        System.out.println("Image unscrambling with known key (" + r + ", " + s + ")");
        newFilename = outputFilenamePrefix + originalFilename;
        outputDir = inputPath.getParent();
        outputPath = (outputDir != null) ? outputDir.resolve(newFilename) : Paths.get(newFilename);
        Imgcodecs.imwrite(outputPath.toString(), Watermark.addWatermark(restImg, "Unscrambled"));
        System.out.println("Résultat sauvegardé dans : " + outputPath.toString());
    }
}
