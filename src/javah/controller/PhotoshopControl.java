package javah.controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javah.util.DraggableRectangle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class will handle the uploading and capturing of photos.
 */
public class PhotoshopControl {

    public interface OnPhotoshopListener {
        void onAcceptButtonClicked(byte client, WritableImage image);
        void onCancelButtonClicked(byte client);
    }

    @FXML private Label mActionLabel;
    @FXML private Pane mRootPane;
    @FXML private ImageView mPhotoView;
    @FXML private Pane mImagePane;

    @FXML private ImageView mAcceptButton;
    @FXML private ImageView mCaptureButton;

    @FXML private HBox mFilterSignatureBox, mMirrorCamBox;
    @FXML private CheckBox mFilterSignatureCheckbox, mMirrorCamCheckbox;

    // The possible clients requesting a photo from the PhotoshopControl.
    public static final byte
            CLIENT_RESIDENT_PHOTO = 0,
            CLIENT_CHAIRMAN_PHOTO = 1,
            CLIENT_CHAIRMAN_SIGNATURE = 2,
            CLIENT_SECRETARY_SIGNATURE = 3,
            CLIENT_ID_SIGNATURE = 4;

    // The possible requests of the clients.
    public static final byte
            REQUEST_PHOTO_CAPTURE = 0,
            REQUEST_PHOTO_UPLOAD = 1;

    private byte mClient, mRequest;

    /**
     * Used for : REQUEST_PHOTO_UPLOAD.
     * Holds the uploaded image.
     */
    private Image mUploadedImage;

    /**
     * Used for : REQUEST_PHOTO_CAPTURE
     * Captured image is flipped, so we need to mirror it and make a reference out of it.
     */
    private WritableImage mCapturedImage;

    /**
     * This is the image to be passed to the clients. This image are the cropped images and can either be filtered
     * depending on the client's request.
     */
    private WritableImage mModifiedImage;

    /**
     * The rectangle used for cropping the images.
     */
    private DraggableRectangle mDraggableRectangle;

    private OnPhotoshopListener mListener;

    /**
     * The main window of the web cam.
     */
    private WebcamPanel mWebcamPanel;

    /**
     * In JavaFX, Panels cannot be added to a pane. It must be first converted to a node. Thus, this serves as a container
     * for mWebcamPanel;
     */
    private SwingNode mWebcamNode;

    /**
     * Used for : REQUEST_PHOTO_CAPTURE
     * Determines whether the capture button is pressed and when the client wants to recapture another photo.
     * Value is false if an image is not yet captured and true if an image is captured.
     */
    private boolean mIsImageCaptured = false;

    @FXML
    private void initialize() {
        mDraggableRectangle = new DraggableRectangle(
                (int) mImagePane.getPrefWidth(),
                (int) mImagePane.getPrefHeight());

        mImagePane.getChildren().add(mDraggableRectangle);
    }

    /**
     * mModifiedImage is first cropped before being sent to the client.
     * @param mouseEvent
     */
    @FXML
    public void onAcceptButtonClicked(MouseEvent mouseEvent) {
        // Remove any photo placed in mPhotoView.
        mPhotoView.setImage(null);

        switch (mRequest) {
            case REQUEST_PHOTO_UPLOAD:

                // Crop the mUploadedImage or mModified image itself based on the mDraggableRectangle and store it in
                // mModifiedImage before being sent to the client.
                int rectWidth = (int) mDraggableRectangle.getWidth();
                int rectHeight = (int) mDraggableRectangle.getHeight();
                int rectX = (int) mDraggableRectangle.getX();
                int rectY = (int) mDraggableRectangle.getY();
                int uploadedImageWidth = (int) mUploadedImage.getWidth();
                int uploadedImageHeight = (int) mUploadedImage.getHeight();

                PixelReader pixelReader;

                // If the uploaded photo is a signature and the signature filter is marked, then the image to crop is
                // the filtered image, mModifiedImage.
                // Else, make use of the mUploadedImage.
                if (mFilterSignatureCheckbox.isSelected() && mFilterSignatureBox.isVisible()) {
                    pixelReader = mModifiedImage.getPixelReader();
                    mModifiedImage = new WritableImage(rectWidth, rectHeight);
                } else {
                    mModifiedImage = new WritableImage(rectWidth, rectHeight);
                    pixelReader = mUploadedImage.getPixelReader();
                }

                PixelWriter pixelWriter = mModifiedImage.getPixelWriter();

                // If ever the draggable rectangle goes out of bounds from the width and height of the uploaded image,
                // then assign transparent pixels on to the out of bounds area.
                for (int x = rectX; x < rectWidth + rectX; x++)
                    for (int y = rectY; y < rectHeight + rectY; y++)

                        pixelWriter.setArgb(x - rectX, y - rectY,
                                (x < uploadedImageWidth && y < uploadedImageHeight) ?
                                        pixelReader.getArgb(x, y) : new Color(0, 0, 0, 0).getRGB());

                break;

            default:

                pixelReader = !mFilterSignatureCheckbox.isSelected() ?
                        mCapturedImage.getPixelReader() : mModifiedImage.getPixelReader();

                // No out of bounds will occur when cropping an image captured by the web cam, since it is a perfect fit.
                // Thus, this simple function will suffice.
                mModifiedImage = new WritableImage(
                        pixelReader,
                        (int) mDraggableRectangle.getX(),
                        (int) mDraggableRectangle.getY(),
                        (int) mDraggableRectangle.getWidth(),
                        (int) mDraggableRectangle.getHeight());
        }

        mListener.onAcceptButtonClicked(mClient, mModifiedImage);

        // Close the webcam panel.
        if (mWebcamPanel != null) {
            mWebcamPanel.stop();
            mWebcamPanel = null;
            mImagePane.getChildren().remove(mWebcamNode);
        }
    }

    /**
     * Used for : REQUEST_PHOTO_CAPTURE
     * A Button that acts as a way to take and retake a picture.
     * @param mouseEvent
     */
    @FXML
    public void onCaptureButtonClicked(MouseEvent mouseEvent) {
        // If an image is already captured, then recapture an image.
        if (mIsImageCaptured) {

            // While no image is captured by the web cam, only display the web cam capture button.
            mAcceptButton.setVisible(false);
            mAcceptButton.setManaged(false);
            mMirrorCamBox.setVisible(true);
            mFilterSignatureBox.setVisible(false);

            mDraggableRectangle.setStroke(javafx.scene.paint.Color.WHITE);
            mFilterSignatureCheckbox.setSelected(false);

            // Send the mPhotoView and mDraggableRectangle to the back to show the web cam pane again.
            mWebcamNode.toFront();
            mPhotoView.setVisible(false);
            mDraggableRectangle.setVisible(false);

            // Center the mDraggableRectangle.
            mDraggableRectangle.setX(mImagePane.getWidth() / 2 - mDraggableRectangle.getWidth() / 2);
            mDraggableRectangle.setY(mImagePane.getHeight() / 2 - mDraggableRectangle.getHeight() / 2);

            mIsImageCaptured = false;

        // If no image is captured, then capture an image and update the scene.
        } else {
            // Capture an image. . .

            // Create a temp file to hold the captured photo.
            String tempFilePath = System.getenv("PUBLIC") + "/Barangay131/Photos/2d6aeb19-b1ab-4e40-b8af-cfe62a05c431.png";
            File tempFile = new File(tempFilePath);

            // Store photo as a temporary file. Sadly, captured photo is mirrored.
            WebcamUtils.capture(mWebcamPanel.getWebcam(), tempFile, ImageUtils.FORMAT_PNG);

            try {
                // Read the captured image of the web cam.
                BufferedImage tempImage = ImageIO.read(tempFile);
                int height = tempImage.getHeight();
                int width = tempImage.getWidth();

                // Unfortunately, the web cam captures a non-mirrored image at all times - tempImage.
                // If the mirror checkbox is selected, then store the flipped copy to mCapturedImage.
                // Else store the non flipped copy to mCapturedImage.
                if (mMirrorCamCheckbox.isSelected()) {
                    // Initialize mModifiedImage to load the flipped image.
                    mCapturedImage = new WritableImage(tempImage.getWidth(), tempImage.getHeight());

                    PixelWriter pixelWriter = mCapturedImage.getPixelWriter();

                    // The flipping process...
                    for (int y = 0; y < height; y++)
                        for (int x = 0; x < width; x++)
                            pixelWriter.setArgb(width - 1 - x, y, tempImage.getRGB(x, y));
                } else
                    mCapturedImage = SwingFXUtils.toFXImage(tempImage, mCapturedImage);

                // If the client is requesting a signature, make a filtered copy of the mCapturedImage and store it in
                // mModifiedImage.
                switch (mClient) {
                    case CLIENT_SECRETARY_SIGNATURE:
                    case CLIENT_CHAIRMAN_SIGNATURE:
                    case CLIENT_ID_SIGNATURE:
                        mFilterSignatureBox.setVisible(true);

                        // Create a filtered copy of the signature, then store it to mModifiedImage.
                        PixelReader pixelReader = mCapturedImage.getPixelReader();

                        mModifiedImage = new WritableImage(width, height);
                        PixelWriter pixelWriter = mModifiedImage.getPixelWriter();

                        for (int x = 0; x < width; x++)
                            for (int y = 0; y < height; y++) {
                                // Get the rgb of the pixel of the mSignatureImage at (x,y).
                                Color rgb = new Color(pixelReader.getArgb(x, y));

                                int r = rgb.getRed();
                                int g = rgb.getGreen();
                                int b = rgb.getBlue();

                                double z = 0.2126 * r + 0.7152 * g + 0.0722 * b;

                                // Check to see if the pixel is light or dark colored.
                                // If the pixel is light colored, then don't write it in the mFilteredSignatureImage.
                                pixelWriter.setArgb(x, y, z < 128 ? rgb.getRGB() : new Color(0, 0, 0, 0).getRGB());
                            }

                        mFilterSignatureBox.setVisible(true);
                        break;
                    default:
                }

                mPhotoView.setImage(mCapturedImage);
                mPhotoView.setVisible(true);
                mDraggableRectangle.setVisible(true);
                mWebcamNode.toBack();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Update the GUI after a web cam capture is made.
            mAcceptButton.setVisible(true);
            mAcceptButton.setManaged(true);
            mMirrorCamBox.setVisible(false);

            mPhotoView.setVisible(true);
            mDraggableRectangle.setVisible(true);

            mIsImageCaptured = true;
        }
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked(mClient);

        // Remove any photo placed in mPhotoView.
        mPhotoView.setImage(null);

        // Close the webcam panel.
        if (mWebcamPanel != null) {
            mWebcamPanel.stop();
            mWebcamPanel = null;
            mImagePane.getChildren().remove(mWebcamNode);
        }
    }

    /**
     * Filter the photo if the photo is a signature type.
     * @param actionEvent
     */
    @FXML
    public void onFilterSignatureCheckboxClicked(ActionEvent actionEvent) {
        if (mFilterSignatureCheckbox.isSelected()) {
            mDraggableRectangle.setStroke(javafx.scene.paint.Color.BLACK);
            mPhotoView.setImage(mModifiedImage);
        } else {
            mDraggableRectangle.setStroke(javafx.scene.paint.Color.WHITE);
            mPhotoView.setImage(mRequest == REQUEST_PHOTO_UPLOAD ? mUploadedImage : mCapturedImage);
        }
    }

    @FXML
    public void onMirrorCamCheckBoxClicked(ActionEvent actionEvent) {
        mWebcamPanel.setMirrored(mMirrorCamCheckbox.isSelected());
    }

    /**
     * Update the scene depending on the client and their request.
     * @param client
     * @param request
     */
    public void setClient(byte client, byte request) {
        mClient = client;
        mRequest = request;

        // By default, the mDraggableRectangle should be white.
        mDraggableRectangle.setStroke(javafx.scene.paint.Color.WHITE);

        if (request == REQUEST_PHOTO_UPLOAD) {
            // Open the dialog for photo uploading. . .

            // Make sure that the root pane is unclickable until the dialog is closed.
            mRootPane.setDisable(true);

            // Setup the file chooser dialog.
            FileChooser fileChooser = new FileChooser();

            // Define the allowed file extensions.
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG Files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.PNG");
            FileChooser.ExtensionFilter extFilterJPEG = new FileChooser.ExtensionFilter("JPEG Files (*.jpeg)", "*.JPEG");
            fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterJPEG, extFilterPNG);

            // Show the file chooser dialog.
            Stage stage = new Stage();
            stage.setTitle("Choose Photo");
            File file = fileChooser.showOpenDialog(stage);

            // Enable the mRootPane after the upload window is displayed.
            mRootPane.setDisable(false);

            // Get the path of the uploaded image and display the image at the photo view.
            // If no image is chosen, then cancel the request.
            if (file != null)
                // The image should be resized to 640x480 while preserving ratio.
                mUploadedImage = new Image("file:" + file.toPath(), 640, 480, true, true);
            else {
                onCancelButtonClicked(null);
                return;
            }

            // Loading of the proper visual of the upload scene starts. . .

            // Update the functionality of the scene depending whether the client is requesting a profile photo or a
            // signature photo.
            switch (client) {
                case CLIENT_CHAIRMAN_PHOTO :
                case CLIENT_RESIDENT_PHOTO :
                    // mDraggableRectangle is set to a square.
                    mDraggableRectangle.setAspectRatio(1, 1);
                    mDraggableRectangle.setWidth(216);
                    mDraggableRectangle.setHeight(216);

                    // Make sure that the mSignatureFilterBox is hidden.
                    mFilterSignatureBox.setVisible(false);
                    break;

                default:
                    // mDraggableRectangle is set to a rectangle. (21:9)
                    mDraggableRectangle.setAspectRatio(21, 9);
                    mDraggableRectangle.setWidth(315);
                    mDraggableRectangle.setHeight(135);

                    // Make sure that the mSignatureFilterBox is shown.
                    mFilterSignatureBox.setVisible(true);
                    mFilterSignatureCheckbox.setSelected(false);

                    // Create a filtered copy of the signature, then store it to mModifiedImage.
                    try {
                        int width = (int) mUploadedImage.getWidth();
                        int height = (int) mUploadedImage.getHeight();
                        PixelReader pixelReader = mUploadedImage.getPixelReader();

                        mModifiedImage = new WritableImage(width, height);
                        PixelWriter pixelWriter = mModifiedImage.getPixelWriter();

                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                // Get the rgb of the pixel of the mSignatureImage at (x,y).
                                Color rgb = new Color(pixelReader.getArgb(x, y));

                                int r = rgb.getRed();
                                int g = rgb.getGreen();
                                int b = rgb.getBlue();

                                double z = 0.2126 * r + 0.7152 * g + 0.0722 * b;

                                // Check to see if the pixel is light or dark colored.
                                // If the pixel is light colored, then don't write it in the mFilteredSignatureImage.
                                pixelWriter.setArgb(x, y, z < 128 ? rgb.getRGB() : new Color(0, 0, 0, 0).getRGB());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            // Update the scene based on the intersecting state of the display and signature photos.
            mActionLabel.setText("Photo Upload");
            mCaptureButton.setVisible(false);
            mCaptureButton.setManaged(false);
            mAcceptButton.setVisible(true);
            mAcceptButton.setManaged(true);
            mMirrorCamBox.setVisible(false);
            mPhotoView.setImage(mUploadedImage);

            mIsImageCaptured = false;

            // Center the mDraggableRectangle.
            mDraggableRectangle.setX(mImagePane.getWidth() / 2 - mDraggableRectangle.getWidth() / 2);
            mDraggableRectangle.setY(mImagePane.getHeight() / 2 - mDraggableRectangle.getHeight() / 2);

            // Make sure that the mPhotoView and mDraggableRectangle are visible.
            mPhotoView.setVisible(true);
            mDraggableRectangle.setVisible(true);

        } else {

            // Update the action label.
            mActionLabel.setText("Photo Capture");

            // While no image is captured by the web cam, only display the web cam capture button.
            mAcceptButton.setVisible(false);
            mAcceptButton.setManaged(false);
            mCaptureButton.setVisible(true);
            mCaptureButton.setManaged(true);
            mMirrorCamBox.setVisible(true);
            mFilterSignatureBox.setVisible(false);

            // By defualt, the mMirrorCamCheckbox should be marked.
            mMirrorCamCheckbox.setSelected(true);

            // Determine the size of the mDraggableRectangle.
            switch (client) {
                case CLIENT_CHAIRMAN_PHOTO :
                case CLIENT_RESIDENT_PHOTO :
                    // mDraggableRectangle is set to a square.
                    mDraggableRectangle.setAspectRatio(1, 1);
                    mDraggableRectangle.setWidth(216);
                    mDraggableRectangle.setHeight(216);
                    break;
                default :
                    // mDraggableRectangle is set to a rectangle. (21:9)
                    mDraggableRectangle.setAspectRatio(21, 9);
                    mDraggableRectangle.setWidth(315);
                    mDraggableRectangle.setHeight(135);

                    mFilterSignatureCheckbox.setSelected(false);
            }

            // Center the mDraggableRectangle.
            mDraggableRectangle.setX(mImagePane.getWidth() / 2 - mDraggableRectangle.getWidth() / 2);
            mDraggableRectangle.setY(mImagePane.getHeight() / 2 - mDraggableRectangle.getHeight() / 2);

            // Try to launch the web cam. This operation will fail if the web cam is being used by another software.
            try {
                // Get the default web cam.
                Webcam webcam = Webcam.getDefault();
                webcam.setViewSize(new Dimension(640, 480));

                // Initialize the web cam itself.
                mWebcamPanel = new WebcamPanel(webcam);
                mWebcamPanel.setMirrored(true);

                // In order to add the web cam panel to the root pane, the JPanel must first be converted to a SwingNode.
                mWebcamNode = new SwingNode();
                mWebcamNode.setContent(mWebcamPanel);

                // Add the web cam panel to the root pane.
                mImagePane.getChildren().add(mWebcamNode);
            } catch (Exception e) {
                e.printStackTrace();

                JOptionPane.showConfirmDialog(null,
                        "Camera is open in another Application",
                        "Warning",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                // Close this dialog.
                mListener.onCancelButtonClicked(mClient);
            }
        }
    }

    public void setListener(OnPhotoshopListener listener) {
        mListener = listener;
    }

    public void reset() {
        mDraggableRectangle.setVisible(false);
        mPhotoView.setVisible(false);

        mFilterSignatureBox.setVisible(false);
        mMirrorCamBox.setVisible(false);
    }
}
