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

        // --- Sélection du codec et de l'extension (tentative FFV1 puis MJPG en fallback) ---
        // NOTE: FFV1 est préféré (lossless) mais peut ne pas être disponible selon l'environnement OpenCV/FFmpeg.
        // On essaie d'abord "FFV1" avec extension .mkv puis "MJPG" avec .avi.
        String[] codecNames = { "FFV1", "MJPG" };
        String[] extensions = { "mkv", "avi" };

        int chosenFourcc = 0;
        String chosenExt = null;
        String scrambledPathStr = null;
        VideoWriter scrambledWriter = null;

        // --- Construction de base du nom de fichier source ---
        Path inputPath = Paths.get(inputFile);
        String baseFilename = inputPath.getFileName().toString();

        for (int i = 0; i < codecNames.length; i++) {
            String codec = codecNames[i];
            String ext = extensions[i];

            // Définit le fourcc à partir du nom du codec (4 caractères)
            int fourcc = VideoWriter.fourcc(codec.charAt(0), codec.charAt(1), codec.charAt(2), codec.charAt(3));

            // Nom de sortie chiffré avec l'extension correspondant au codec testé
            String scrambledFilename = "scrambled_" + baseFilename.replaceFirst("[.][^.]+$", "." + ext);
            Path scrambledPath = inputPath.getParent() != null
                    ? inputPath.getParent().resolve(scrambledFilename)
                    : Paths.get(scrambledFilename);
            String candidatePathStr = scrambledPath.toString();

            // Essayer d'ouvrir le VideoWriter avec ce codec/extension
            VideoWriter writer = new VideoWriter(candidatePathStr, fourcc, fps, frameSize);
            if (writer.isOpened()) {
                // Succès : retenir ce writer + info
                scrambledWriter = writer;
                chosenFourcc = fourcc;
                chosenExt = ext;
                scrambledPathStr = candidatePathStr;
                break;
            } else {
                // Échec : libérer et essayer le suivant
                writer.release();
            }
        }

        if (scrambledWriter == null || !scrambledWriter.isOpened()) {
            System.err.println("ERREUR: Impossible d'ouvrir le VideoWriter chiffré. Aucun codec disponible.");
            srcVideo.release();
            return;
        }

        System.out.println("Codec/extension choisis pour la vidéo chiffrée: " + (chosenExt != null ? chosenExt : "inconnu"));

        // =======================================================
        // --- 2. Chiffrement et Sauvegarde (Source -> Chiffrée) ---
        // =======================================================

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

        // Construire le nom de sortie déchiffré en réutilisant l'extension choisie
        String unscrambledFilename = "unscrambled_" + baseFilename.replaceFirst("[.][^.]+$", "." + (chosenExt != null ? chosenExt : "avi"));
        Path unscrambledPath = inputPath.getParent() != null
                ? inputPath.getParent().resolve(unscrambledFilename)
                : Paths.get(unscrambledFilename);
        String unscrambledPathStr = unscrambledPath.toString();

        VideoWriter unscrambledWriter = new VideoWriter(unscrambledPathStr, chosenFourcc, fps, frameSize);

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