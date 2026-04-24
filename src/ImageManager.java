import javax.swing.*;
import java.awt.*;

/**
 The ImageManager class manages the loading and processing of images.
 */

public class ImageManager {

    public ImageManager() {

    }

//    public static Image loadImage (String fileName) {
//        return new ImageIcon(fileName).getImage();
//    }

    public static Image loadImage(String fileName) {
        return new ImageIcon(ImageManager.class.getResource(fileName)).getImage();
    }
}

