/**

 Nom : Belhadj

 Prénom : Quentin

 Groupe : S5-A2

 Projet : VideoScramble

 Date : 25/11/2025

 Description : Cette classe a pour seul but d'ajouter un watermark sur les images traitées.
 */

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;
import org.opencv.core.Point;

public class Watermark
{
    public static Mat addWatermark(Mat srcImg, String text){
        //copy srcImg to dstImg
        Mat dstImg = srcImg.clone();
        Scalar color = new Scalar(255, 255, 255);
        Point position = new Point(10, 30);
        int font = Imgproc.FONT_HERSHEY_SIMPLEX;
        double fontScale = 1.0;
        int thickness = 2;
        Imgproc.putText(
                dstImg,
                text,
                position,
                font,
                fontScale,
                color,
                thickness
        );
        return dstImg;
    }
}
