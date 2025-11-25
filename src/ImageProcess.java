import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


/**

 Nom : Belhadj

 Prénom : Quentin

 Groupe : BUT Informatique S5-A2

 Projet : VideoScramble

 Date : 24/11/2025

 Description : Cette classe se concentre sur le chiffrement et déchiffrement (avec ou sans clé) d'image unique.

 */



public class ImageProcess {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    /**
     * Chiffrer une image avec une clé symmétrique r,s ((r + (2s+1)idLigne)%size)
     * r est un décalage (offset) codé sur 8 bits
     * s est un pas (step) codé sur 7 bits
     *
     * @param srcImage
     * @param r est un décalage (offset) codé sur 8 bits
     * @param s est un pas (step) codé sur 7 bits
     * @return Mat image chiffrée
     */
    public static Mat scrambleImage(Mat srcImage, int r, int s) {
        int start = 0;
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
        return scrambledImage;
    }

    /**
     * Déchiffrer une image avec une clé symmétrique r,s ((r + (2s+1)idLigne)%size)
     *
     * @param SrcImage image chiffrée
     * @param r est un décalage (offset) codé sur 8 bits
     * @param s est un pas (step) codé sur 7 bits
     * @return Mat image déchiffrée
     */
    public static Mat unscrambleImage(Mat SrcImage, int r, int s) {
        int start = 0;
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
        return unscrambledImage;
    }

    public static void main(String[] args) {
        Mat img = Imgcodecs.imread("D:\\User\\Documents\\Github\\ProjectScramble\\medias\\test.png");
        Mat scrambled = scrambleImage(img, 3, 104);
        Imgcodecs.imwrite("scrambled_image.png", scrambled);
        Mat image = Imgcodecs.imread("scrambled_image.png");
        Mat unscrambled = unscrambleImage(image,3 , 104);
        Mat check = unscrambleImage(scrambled, 3, 104);
        Imgcodecs.imwrite("check_image.png", check);
        Imgcodecs.imwrite("loaded_unscrambled_image.png", unscrambled);
    }

}
