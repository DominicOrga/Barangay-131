package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javah.util.DraggableRectangle;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class SignatureAmendControl {

//    @FXML private Pane mSignaturePane;
//    /**
//     * ImageView representing the signature.
//     */
//    @FXML private ImageView mSignatureView;
//
//    @FXML private CheckBox mFilterSignatureCheckbox;
//    /**
//     * File path of the signature image.
//     */
//    private String mSignatureImagePath;
//
//
//    private DraggableRectangle mDraggableRectangle;
//    /**
//     * The signature file or image.
//     */
//    private Image mSignatureImage;
//
//    /**
//     * A copy of the signature image with clear background.
//     */
//    private WritableImage mFilteredSignatureImage;
//
//    @FXML
//    private void initialize() {
//        mDraggableRectangle = new DraggableRectangle(50, 50, 250, 100, 640, 480);
//        mDraggableRectangle.setStroke(javafx.scene.paint.Color.BLACK);
//
//        mSignaturePane.getChildren().add(mDraggableRectangle);
//    }
//
//    @FXML
//    public void onAcceptButtonClicked(MouseEvent mouseEvent) {
//
//    }
//
//    @FXML
//    public void onCancelButtonClicked(ActionEvent actionEvent) {
//    }
//
//    @FXML
//    public void onFilterSignatureCheckboxClicked(ActionEvent actionEvent) {
//        mSignatureView.setImage(mFilterSignatureCheckbox.isSelected() ? mFilteredSignatureImage : mSignatureImage);
//    }
//
//    public void setSignatureImage(String imagePath) {
//        mSignatureImage = new Image("file:" + imagePath);
//        mSignatureImagePath = imagePath;
//
//        mSignatureView.setImage(mSignatureImage);
//
//        // Initialize filtered signature.
//        try {
//            BufferedImage image = ImageIO.read(new File(mSignatureImagePath));
//
//            mFilteredSignatureImage = new WritableImage(image.getWidth(), image.getHeight());
//            PixelWriter pixelWriter = mFilteredSignatureImage.getPixelWriter();
//
//            for (int x = 0; x < image.getWidth(); x++) {
//                for (int y = 0; y < image.getHeight(); y++) {
//                    // Get the rgb of the pixel of the mSignatureImage at (x,y).
//                    Color rgb = new Color(image.getRGB(x, y));
//
//                    int r = rgb.getRed();
//                    int g = rgb.getGreen();
//                    int b = rgb.getBlue();
//
//                    double z = 0.2126 * r + 0.7152 * g + 0.0722 * b;
//
//                    // Check to see if the pixel is light or dark colored.
//                    // If the pixel is light colored, then don't write it in the mFilteredSignatureImage.
//                    pixelWriter.setArgb(x, y, z < 128 ? rgb.getRGB() : new Color(0, 0, 0, 0).getRGB());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
