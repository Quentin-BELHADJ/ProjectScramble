/**

 Nom : Belhadj

 Prénom : Quentin

 Groupe : S5-A2

 Projet : VideoScramble

 Date : 24/11/2025

 Description : Point d'entrée du programme tant que l'interface JavaFX n'est pas implémentée.
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Scramble {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // Le nombre minimum d'arguments est 2 pour le mode 3 (scramble 3 <input file>)
        if (args.length < 2) {
            Usage();
            System.exit(1);
        }

        // Le premier argument est le mode (1, 2 ou 3)
        String modeStr = args[0];
        String inputFile = args[1];
        List<int[]> RSList = new ArrayList<>();

        try {
            switch (modeStr) {
                case "1": // Scramble (Chiffrement)
                    // Modes 1 et 2 nécessitent au moins 3 arguments (mode, input, r) ou 4 (mode, input, r, s)
                    // ou 3 arguments (mode, input, rsListFile)
                    if (args.length < 3) {
                        System.out.println("Mode " + modeStr + " requires a key or a key file.");
                        Usage();
                        return;
                    }

                    String keyArg = args[2];

                    if (args.length == 3) {
                        // Cas 1: java Scramble <mode> <input file> <r and s list file>
                        String rsListFile = keyArg;
                        // On vérifie que la liste existe bien
                        if (!Files.exists(Paths.get(rsListFile))) {
                            System.out.println("Key file not found: " + rsListFile);
                            return;
                        }
                        RSList = loadRSKeysFromFile(rsListFile);


                    } else if (args.length == 4) {
                        // Cas 2: java Scramble <mode> <input file> <r> <s>
                        int r = Integer.parseInt(keyArg);
                        int s = Integer.parseInt(args[3]);
                        RSList.add(new int[]{r, s});

                    } else {
                        System.out.println("Invalid number of arguments for mode " + modeStr + ".");
                        Usage();
                        return;
                    }

                    if (RSList.isEmpty()) {
                        System.out.println("No valid keys loaded.");
                        return;
                    }

                    if (inputFile.endsWith(".mp4")) {
                        // todo video (chiffrement/déchiffrement)
                        System.out.println("Video processing in mode " + modeStr + " not yet implemented.");
                    } else {
                        //long startTime = System.nanoTime();
                        Mat srcImg = Imgcodecs.imread(inputFile);
                        if (srcImg.empty()) {
                            System.err.println("Erreur: Impossible de charger l'image depuis : " + inputFile);
                            return;
                        }
                        ImageProccess img = new ImageProccess();
                        Mat scbImg;
                        Mat restImg;

                        // Utiliser la première clé de la liste (que ce soit une seule clé ou un fichier)
                        int r = RSList.get(0)[0];
                        int s = RSList.get(0)[1];

                        Path inputPath = Paths.get(inputFile);
                        String originalFilename = inputPath.getFileName().toString();
                        String outputFilenamePrefix;
                        scbImg = img.scrambleImage(srcImg, RSList);
                        outputFilenamePrefix = "scrambled_";
                        System.out.println("Image scrambled with key (" + r + ", " + s + ")");
                        String newFilename = outputFilenamePrefix + originalFilename;
                        Path outputDir = inputPath.getParent();
                        Path outputPath = (outputDir != null) ? outputDir.resolve(newFilename) : Paths.get(newFilename);
                        Imgcodecs.imwrite(outputPath.toString(), scbImg);
                        System.out.println("Résultat sauvegardé dans : " + outputPath.toString());
                        restImg = img.unscrambleImage(scbImg, RSList);
                        outputFilenamePrefix = "unscrambled_";
                        System.out.println("Image unscrambling with known key (" + r + ", " + s + ")");
                        newFilename = outputFilenamePrefix + originalFilename;
                        outputDir = inputPath.getParent();
                        outputPath = (outputDir != null) ? outputDir.resolve(newFilename) : Paths.get(newFilename);

                        Imgcodecs.imwrite(outputPath.toString(), restImg);
                        System.out.println("Résultat sauvegardé dans : " + outputPath.toString());
                        //long stopTime = System.nanoTime();
                        //System.out.println(stopTime - startTime);
                        break;
                    }

                case "2": // Unscramble (Déchiffrement) par force brute
                    // TODO: Implémenter le déchiffrement sans connaitre les clés
                    break;

                default:
                    System.out.println("Invalid mode specified: " + modeStr);
                    Usage();
                    break;
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: r and s must be integers. " + e.getMessage());
            Usage();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<int[]> loadRSKeysFromFile(String rsListFile) throws IOException, NumberFormatException {
        List<int[]> RSList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(rsListFile));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                int r = Integer.parseInt(parts[0].trim());
                int s = Integer.parseInt(parts[1].trim());
                RSList.add(new int[]{r, s});
            }
        }
        return RSList;
    }
    public static void Usage() {
        System.out.println("Usage : java Scramble <mode> <input file> [options]");
        System.out.println("-----------------------------------------------------");
        System.out.println("Mode 1: Scramble (Chiffrement - déchiffrement) [cite: 78]");
        System.out.println("  1. java Scramble 1 <input file> <r> <s>");
        System.out.println("  2. java Scramble 1 <input file> <r and s list file>");
        System.out.println("     - <r and s list file> : Chemin vers un fichier contenant 'r,s' par ligne.");
        System.out.println("     (Pour une image, seule la première clé sera utilisée.)");
        System.out.println("-----------------------------------------------------");
        System.out.println("Mode 2: Unscramble (Déchiffrement) par force brute [cite: 86, 90]");
        System.out.println("  java Scramble 3 <input file>");
    }
}