package javah.controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This class will handle the web camera photo capturing.
 */
public class WebcamCaptureControl {
    /**
     * The image view that will hold the captured photo.
     */
    @FXML private ImageView mCapturedPhotoView;

    @FXML private ImageView mCaptureButton;

    /**
     * Visible only when mIsPhotoCaptured is true.
     */
    @FXML private ImageView mAcceptButton;

    @FXML private Button mCancelButton;

    /**
     * Serves as the main scene for the mWebcamPanel.
     */
    @FXML private Pane mWebcamPane;

    /**
     * The web cam stored and handled within mWebcamPanel.
     */
    private WebcamPanel mWebcamPanel;

    /**
     * Changes the state of the scene as it determines whether the webcam capturing occured.
     */
    private boolean mIsPhotoCaptured = false;

    /**
     * Temporary target file to store the captured photo.
     */
    private File mTempFile;

    @FXML
    private void initialize() {
        mTempFile = new File(System.getenv("PUBLIC") + "/Barangay131/Photos/temp.png");

        // Testing
        setWebcamEnabled(true);
        resetScene();


    }

    @FXML
    /**
     * Crop the photo, then send it to the Main control and reset the scene.
     * Hide the scene after accomplishing this controller's task.
     * @param mouseEvent
     */
    public void onAcceptButtonClicked(MouseEvent mouseEvent) {
    }
    @FXML
    /**
     * Capture photo from the web cam, then show the mAcceptButton.
     * If mCaptureButton is clicked while a photo has been captured, then the user is allowed to take another snap shot.
     * @param mouseEvent
     */
    public void onCaptureButtonClicked(MouseEvent mouseEvent) {
        if (!mIsPhotoCaptured) {
            mAcceptButton.setVisible(true);
            mAcceptButton.setManaged(true);

            // Capture photo. Sadly, captured photo is mirrored.
            WebcamUtils.capture(mWebcamPanel.getWebcam(), mTempFile, ImageUtils.FORMAT_PNG);

            // Display the captured photo. As a temporary fix to the mirrored image, setting the scale of the image view
            // to -1 provides a temporary solution.
            mCapturedPhotoView.setImage(new Image("file:" + System.getenv("PUBLIC") + "/Barangay131/Photos/temp.png"));
            mCapturedPhotoView.setScaleX(-1);
            mCapturedPhotoView.toFront();

            // todo: flip image before saving the file.
//            // Flip the image horizontally
//            tx = AffineTransform.getScaleInstance(-1, 1);
//            tx.translate(-image.getWidth(null), 0);
//            op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//            image = op.filter(image, null);

//            BufferedImage tempImage = new BufferedImage("file:" + System.getenv("PUBLIC") + "/Barangay131/Photos/temp.png");

//            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//            tx.translate(tempImage.getWidth(), 0);
//            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//            tempImage = op.filter(tempImage, null)

            mIsPhotoCaptured = true;
        } else {
            mAcceptButton.setVisible(false);
            mAcceptButton.setManaged(false);
            mCapturedPhotoView.toBack();

            mIsPhotoCaptured = false;
        }
    }

    @FXML
    /**
     * Reset the scene, then close it.
     * @param actionEvent
     */
    public void onCancelButtonClicked(ActionEvent actionEvent) {
    }

    /**
     * Enable or disable the web cam.
     * @param enabled
     * @return false if enabling the web cam fails.
     */
    public boolean setWebcamEnabled(boolean enabled) {
        if (enabled) {

            // Try to launch the web cam. This operation will fail if the web cam is being used by another software.
            try {
                // Get the default web cam.
                Webcam webcam = Webcam.getDefault();
                webcam.setViewSize(new Dimension(640, 480));

                // Initialize the web cam itself.
                mWebcamPanel = new WebcamPanel(webcam);
                mWebcamPanel.setMirrored(true);

                // In order to add the web cam panel to the root pane, the JPanel must first be converted to a SwingNode.
                SwingNode swingNode = new SwingNode();
                swingNode.setContent(mWebcamPanel);

                // Add the web cam panel to the root pane.
                mWebcamPane.getChildren().add(swingNode);

                // todo: Display warning message.

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }

        } else
            mWebcamPanel.stop();

        return true;
    }

    private void resetScene() {
        mAcceptButton.setVisible(false);
        mAcceptButton.setManaged(false);

        mIsPhotoCaptured = false;
    }
}
