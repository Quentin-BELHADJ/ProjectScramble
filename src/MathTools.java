/**

 Nom : Belhadj

 Prénom : Quentin

 Groupe : S5-A2

 Projet : VideoScramble

 Date : 25/11/2025

 Description : Cette classe se concentre sur l'implémentation des outils mathématiques nécessaires pour le traitement des images. (chiffrement/déchiffrement, brute-force, etc.)
 */

public class MathTools {

    /**
     * Trouver la plus grande puissance de 2 inférieure ou égale à n
     *
     * @param n entier positif (height de l'image)
     * @return int plus grande puissance de 2 ≤ n
     */
    public static int largestPower2(int n) {
        int result = 1;
        while (result * 2 <= n) {
            result *= 2;
        }
        if (result > n) {
            result /= 2;
        }
        return result;
    }

    /**
     * Algorithme d'Euclide étendu pour trouver l'inverse modulaire a mod b (a=k et b=size)
     *
     * @param a (k dans l'équation de chiffrement (2s+1))
     * @param b (size dans l'équation de chiffrement)
     * @return int inverse modulaire de a mod b
     */
    public static int modInverse(int a, int b){
        int b0 = b, t, q; // Initialisation
        int x0 = 0, x1 = 1; // Coefficients de Bézout
        if (b == 1) return 1; // Cas particulier
        while (a > 1) { // Boucle principale
            q = a / b; // Quotient
            t = b; // Reste
            b = a % b; a = t; // Mise à jour a et b
            t = x0; // Mise à jour des coefficients de Bézout
            x0 = x1 - q * x0; // Nouveau x0
            x1 = t; // Nouveau x1
        }
        if (x1 < 0) x1 += b0; // Assurer que l'inverse est positif
        return x1;
    }
}
