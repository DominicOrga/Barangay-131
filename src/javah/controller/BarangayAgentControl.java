package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

/**
 * This class will handle the setup of the barangay officials.
 */
public class BarangayAgentControl {

    public interface OnBarangayAgentListener {
        void onChmUploadButtonClicked();
        void onChmCaptureButtonClicked();
        void onChmSignatureUploadButtonClicked();
        void onChmSignatureCaptureButtonClicked();
        void onCancelButtonClicked();
    }

    @FXML private Pane mRootPane;

    @FXML private ImageView mChmPhotoView, mChmSignatureView;

    private WritableImage mChmPhoto, mChmSignature;

    private OnBarangayAgentListener mListener;

    @FXML
    private void initialize() {

    }

    @FXML
    public void onChmSignatureCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onChmSignatureCaptureButtonClicked();
    }

    @FXML
    public void onChmSignatureUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onChmSignatureUploadButtonClicked();
    }

    @FXML
    public void onChmCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onChmCaptureButtonClicked();
    }

    @FXML
    public void onChmUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onChmUploadButtonClicked();
    }

    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {

    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
    }

    public void setListener(OnBarangayAgentListener listener) {
        mListener = listener;
    }

    /**
     * Disable or enable the ResidentFormControl.
     * Used when the photoshop popup scene is displayed.
     * @param disable
     */
    public void setDisable(boolean disable) {
        mRootPane.setDisable(disable);
    }

    /**
     * Update the display photo of the chairman from the photoshop process callback function.
     * @param image
     */
    public void setChmPhoto(WritableImage image) {
        mChmPhoto = image;
        mChmPhotoView.setImage(image);
    }

    /**
     * Update the signature photo of the chairman from the photoshop process callback function.
     * @param image
     */
    public void setChmSignature(WritableImage image) {
        mChmSignature = image;
        mChmSignatureView.setImage(image);
    }
}
