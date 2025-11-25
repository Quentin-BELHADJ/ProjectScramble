import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class VideoHandler {

    public static void handleVideo(String inputFile, List<int[]> RSList) {

        // --- 1. Préparation (Vidéo Source) ---
        VideoCapture srcVideo = new VideoCapture(inputFile);

        if (!srcVideo.isOpened()) {
            System.err.println("ERREUR: Impossible d'ouvrir la vidéo : " + inputFile);
            return;
        }

        // Récupérer les propriétés vidéo (nécessaires pour le VideoWriter)
        int frameWidth = (int) srcVideo.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int frameHeight = (int) srcVideo.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        double fps = srcVideo.get(Videoio.CAP_PROP_FPS);
        Size frameSize = new Size(frameWidth, frameHeight);

        // Définir le codec sans perte (FFV1 est recommandé par le sujet)
        // NOTE: Utiliser un .avi comme extension est souvent plus compatible avec FFV1
        int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');

        // --- Construction des Chemins de Sortie ---
        Path inputPath = Paths.get(inputFile);
        String baseFilename = inputPath.getFileName().toString();

        // Nom de sortie chiffré (ex: scrambled_input.avi)
        String scrambledFilename = "scrambled_" + baseFilename.replaceFirst("[.][^.]+$", ".avi");
        Path scrambledPath = inputPath.getParent() != null
                ? inputPath.getParent().resolve(scrambledFilename)
                : Paths.get(scrambledFilename);
        String scrambledPathStr = scrambledPath.toString();

        // Nom de sortie déchiffré (ex: unscrambled_input.avi)
        String unscrambledFilename = "unscrambled_" + baseFilename.replaceFirst("[.][^.]+$", ".avi");
        Path unscrambledPath = inputPath.getParent() != null
                ? inputPath.getParent().resolve(unscrambledFilename)
                : Paths.get(unscrambledFilename);
        String unscrambledPathStr = unscrambledPath.toString();

        System.out.println("Début du traitement vidéo...");

        // =======================================================
        // --- 2. Chiffrement et Sauvegarde (Source -> Chiffrée) ---
        // =======================================================

        VideoWriter scrambledWriter = new VideoWriter(scrambledPathStr, fourcc, fps, frameSize);

        if (!scrambledWriter.isOpened()) {
            System.err.println("ERREUR: Impossible d'ouvrir le VideoWriter chiffré. Codec ou extension invalide.");
            srcVideo.release();
            return;
        }

        // Exécuter le chiffrement
        VideoProcess.ScrambleVideo(srcVideo, scrambledWriter, RSList);

        // Libérer les ressources du chiffrement
        srcVideo.release();
        scrambledWriter.release();
        System.out.println("Vidéo chiffrée sauvegardée dans : " + scrambledPathStr);


        // =========================================================
        // --- 3. Déchiffrement et Sauvegarde (Chiffrée -> Déchiffrée) ---
        // =========================================================

        // Re-ouvrir la vidéo chiffrée pour le déchiffrement
        VideoCapture scrambledVideoReader = new VideoCapture(scrambledPathStr);
        if (!scrambledVideoReader.isOpened()) {
            System.err.println("ERREUR: Impossible de ré-ouvrir la vidéo chiffrée pour le déchiffrement.");
            return;
        }

        VideoWriter unscrambledWriter = new VideoWriter(unscrambledPathStr, fourcc, fps, frameSize);

        if (!unscrambledWriter.isOpened()) {
            System.err.println("ERREUR: Impossible d'ouvrir le VideoWriter déchiffré.");
            scrambledVideoReader.release();
            return;
        }

        // Exécuter le déchiffrement
        VideoProcess.UnscrambleVideo(scrambledVideoReader, unscrambledWriter, RSList);

        // Libérer les ressources
        scrambledVideoReader.release();
        unscrambledWriter.release();
        System.out.println("Vidéo déchiffrée sauvegardée dans : " + unscrambledPathStr);
    }
}