import java.util.List;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

/**

 Nom : Belhadj

 Prénom : Quentin

 Groupe : S5-A2

 Projet : VideoScramble

 Date : 25/11/2025

 Description : Cette classe se concentre sur l'appel des fonctions de chiffrement/déchiffrement d'images pour chaque frame d'une vidéo donnée.

 */


public class VideoProcess {

    /**
     * Chiffre un flux vidéo frame par frame en utilisant une liste de clés,
     * réparties équitablement sur la durée de la vidéo.
     *
     * @param srcVideo Le VideoCapture de la vidéo source (en clair)
     * @param dstWriter Le VideoWriter pour la vidéo chiffrée (sortie)
     * @param RSList Liste des clés [r, s] à appliquer séquentiellement
     */
    public static void ScrambleVideo(VideoCapture srcVideo, VideoWriter dstWriter, List<int[]> RSList) {

        if (!srcVideo.isOpened() || !dstWriter.isOpened() || RSList.isEmpty()) {
            System.err.println("ERREUR: Le flux vidéo ou la liste de clés est invalide pour le chiffrement.");
            return;
        }

        int totalFrames = (int) srcVideo.get(Videoio.CAP_PROP_FRAME_COUNT);
        int numKeys = RSList.size();

        // Calculer la répartition des frames (gère le reste)
        int nbFramesPerKey = totalFrames / numKeys;
        int framesRemaining = totalFrames % numKeys;

        Mat frame = new Mat();

        // --- Boucle sur les CLÉS (pour gérer les séquences) ---
        for (int i = 0; i < numKeys; i++) {
            int r = RSList.get(i)[0];
            int s = RSList.get(i)[1];

            // Déterminer le nombre de frames à traiter avec cette clé
            int framesToProcess = nbFramesPerKey;
            // Répartir le reste sur les premières clés
            if (i < framesRemaining) {
                framesToProcess++;
            }

            // --- Boucle sur les FRAMES pour la clé courante ---
            for (int j = 0; j < framesToProcess; j++) {

                if (!srcVideo.read(frame) || frame.empty()) {
                    // Fin du flux vidéo
                    return;
                }

                // Chiffrement de la frame en utilisant la classe ImageProcess
                Mat scrambledFrame = ImageProcess.scrambleImage(frame, r, s);

                // Écrire la frame traitée dans le VideoWriter
                dstWriter.write(scrambledFrame);
            }
        }
        System.out.println("Vidéo chiffrée avec " + numKeys + " clés séquentielles.");
    }

    /**
     * Déchiffre un flux vidéo frame par frame en utilisant une liste de clés connue.
     * La logique de déchiffrement est identique à celle du chiffrement pour les clés séquentielles.
     *
     * @param srcVideo Le VideoCapture de la vidéo chiffrée (Source)
     * @param dstWriter Le VideoWriter pour la vidéo déchiffrée (sortie)
     * @param RSList Liste des clés [r, s] appliquées au chiffrement (dans le même ordre)
     */
    public static void UnscrambleVideo(VideoCapture srcVideo, VideoWriter dstWriter, List<int[]> RSList) {

        if (!srcVideo.isOpened() || !dstWriter.isOpened() || RSList.isEmpty()) {
            System.err.println("ERREUR: Le flux vidéo ou la liste de clés est invalide pour le déchiffrement.");
            return;
        }

        int totalFrames = (int) srcVideo.get(Videoio.CAP_PROP_FRAME_COUNT);
        int numKeys = RSList.size();

        // Calculer la répartition des frames (Même logique que le chiffrement)
        int nbFramesPerKey = totalFrames / numKeys;
        int framesRemaining = totalFrames % numKeys;

        Mat frame = new Mat();

        // --- Boucle sur les CLÉS (pour s'assurer que la bonne clé est utilisée pour chaque séquence) ---
        for (int i = 0; i < numKeys; i++) {
            int r = RSList.get(i)[0];
            int s = RSList.get(i)[1];

            int framesToProcess = nbFramesPerKey;
            if (i < framesRemaining) {
                framesToProcess++;
            }

            // --- Boucle sur les FRAMES pour la clé courante ---
            for (int j = 0; j < framesToProcess; j++) {

                if (!srcVideo.read(frame) || frame.empty()) {
                    return;
                }

                // Déchiffrement de la frame en utilisant la classe ImageProcess
                Mat unscrambledFrame = ImageProcess.unscrambleImage(frame.clone(), r, s);
                // Écrire la frame traitée dans le VideoWriter
                dstWriter.write(unscrambledFrame);
            }
        }
        System.out.println("Vidéo déchiffrée avec " + numKeys + " clés séquentielles.");
    }
}