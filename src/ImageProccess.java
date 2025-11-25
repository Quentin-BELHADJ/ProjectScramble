import org.opencv.core.Mat;
import java.util.List;

/**

 Nom : Belhadj

 Prénom : Quentin

 Groupe : BUT Informatique S5-A2

 Projet : VideoScramble

 Date : 24/11/2025

 Description : Cette classe se concentre sur le chiffrement et déchiffrement (avec ou sans clé) d'image unique pour permettre à la classe VideoScramblerController de chiffrer/déchiffrer les frames vidéo en temps réel.

 */



public class ImageProccess {

    /**
     * Chiffrer une image avec une clé symmétrique r,s ((r + (2s+1)idLigne)%size)
     * r est un décalage (offset) codé sur 8 bits
     * s est un pas (step) codé sur 7 bits
     *
     * @param srcImage
     * @param RSList   tuple (r,s)
     * @return Mat image chiffrée
     */
    public static Mat scrambleImage(Mat srcImage, List<int[]> RSList) {
        int start = 0;
        int r = RSList.get(0)[0];
        int s = RSList.get(0)[1];
        int height = srcImage.height();
        Mat scrambledImage = new Mat(srcImage.rows(), srcImage.cols(), srcImage.type());
        while (start < height) {
            int remaining = height - start;
            int blockSize = MathTools.largestPower2(remaining);

            for (int i = 0; i < blockSize; i++) {
                int ligne = start + i;
                int destLigne = start + ((r + ((2 * s + 1) * i)) % blockSize);
                srcImage.row(ligne).copyTo(scrambledImage.row(destLigne));
            }
            start += blockSize;
        }
        scrambledImage = Watermark.addWatermark(scrambledImage, "scrambled");
        return scrambledImage;
    }

    /**
     * Déchiffrer une image avec une clé symmétrique r,s ((r + (2s+1)idLigne)%size)
     * r est un décalage (offset) codé sur 8 bits
     * s est un pas (step) codé sur 7 bits
     *
     * @param SrcImage
     * @param RSList   tuple (r,s)
     * @return Mat image déchiffrée
     */
    public static Mat unscrambleImage(Mat SrcImage, List<int[]> RSList) {
        int start = 0;
        int r = RSList.get(0)[0];
        int s = RSList.get(0)[1];
        int height = SrcImage.height();
        Mat unscrambledImage = new Mat(SrcImage.rows(), SrcImage.cols(), SrcImage.type());

        while (start < height) {
            int remaining = height - start;
            int blockSize = MathTools.largestPower2(remaining);
            int size = blockSize;

            int K = 2 * s + 1; // Le facteur (2s + 1)
            int K_inv = MathTools.modInverse(K, size);

            // i représente l'indice de la ligne CHIFRÉE DANS LE BLOC (Source)
            for (int i = 0; i < blockSize; i++) {

                // 1. Calculer le numérateur (i_chiffré - r)
                int numerator = i - r;

                // 2. Gestion du modulo pour les nombres négatifs en Java : ((A % N) + N) % N
                int positive_numerator = (numerator % size + size) % size;

                // 3. Application de l'inverse modulaire
                int original_index_in_block = (positive_numerator * K_inv) % size;

                // Identification des indices complets
                int sourceLigne = start + i; // Ligne chiffrée (Source dans SrcImage)
                int destLigne = start + original_index_in_block; // Ligne originale (Destination dans unscrambledImage)

                // Permutation INVERSE : on prend la ligne de l'image chiffrée (Source)
                // et on la place à sa position déchiffrée (Destination)
                SrcImage.row(sourceLigne).copyTo(unscrambledImage.row(destLigne));
            }
            start += blockSize;
        }
        unscrambledImage = Watermark.addWatermark(unscrambledImage, "unscrambled");
        return unscrambledImage;
    }
}
