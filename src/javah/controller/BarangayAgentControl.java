package javah.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javah.model.PreferenceModel;
import javah.container.KagawadHolder;
import javah.contract.PreferenceContract;
import javah.util.NodeNameHandler;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A controller that setups the barangay officials for use in information
 * generation.
 */
public class BarangayAgentControl {

    /**
     * A listener created at the main control to listen to this controller for action
     * events, such as hiding this controller's pop-up, take pictures for the chairman
     * photo and signature of the chairman and secretary with the Photoshop Control.
     *
     * @see MainControl
     */
    public interface OnBarangayAgentListener {
        /**
         * If the chairman photo upload button is clicked, then tell the Main Control to
         * launch the photoshop dialog to upload a chairman photo.
         *
         * @see PhotoshopControl
         */
        void onChmUploadButtonClicked();

        /**
         * If the chairman photo capture button is clicked, then tell the Main Control to
         * launch the photoshop dialog to capture a chairman photo.
         *
         * @see PhotoshopControl
         */
        void onChmCaptureButtonClicked();

        /**
         * If the chairman signature upload button is clicked, then tell the Main Control
         * to launch the photoshop dialog to upload a chairman signature.
         *
         * @see PhotoshopControl
         */
        void onChmSignatureUploadButtonClicked();

        /**
         * If the chairman signature capture button is clicked, then tell the Main control
         * to launch the photoshop dialog to capture a chairman signature.
         *
         * @see PhotoshopControl
         */
        void onChmSignatureCaptureButtonClicked();

        /**
         * If the secretary signature upload button is clicked, then tell the Main control
         * to launch the photoshop dialog to upload a secretary signature.
         *
         * @see PhotoshopControl
         */
        void onSecSignatureUploadButtonClicked();

        /**
         * If the secretary signature capture button is clicked, then tell the Main control
         * to launch the photoshop dialog to upload a secretary signature.
         *
         * @see PhotoshopControl
         */
        void onSecSignatureCaptureButtonClicked();

        /**
         * If the cancel button is clicked or the save button is clicked, then tell the
         * Main Control to hide this controller's scene.
         */
        void onFinished();
    }

    /**
     * A grid pane that serves as a root node for this' scene. The root pane must have
     * the ability to be disabled when the dialog to query images by the Photoshop
     * control is displayed, and re-enabled once the image query is done in order to
     * avoid any interaction with this controller.
     */
    @FXML private Pane mRootPane;

    /**
     * A scroll pane containing all the necessary nodes to set the barangay officials.
     * Everytime a new kagawad button is clicked, the root pane's height within the
     * scroll pane changes and the scroll pane should always be scrolled at the very
     * bottom when that happens.
     */
    @FXML private ScrollPane mScrollPane;

    /**
     * A VBox contained within the Scroll Pane which takes hold of all the necessary
     * nodes regarding the kagawads. Everytime a kagawad is removed, that kagawad's
     * nodes will be removed from the kagawad pane. Conversely, if a kagawad is to be
     * added, then that kagawad's nodes will be added to the Kagawad Pane.
     */
    @FXML private VBox mKagawadPane;

    /* An image view holding the chairman photo. */
    @FXML private ImageView mChmPhotoView;

    /* An image view holding the chairman signature. */
    @FXML private ImageView mChmSignatureView;

    /* Text Fields holding the chairman name. */
    @FXML private TextField mChmFirstName, mChmMiddleName, mChmLastName;

    /**
     * A Label holding the chairman name error, which is set to visible when the name
     * of the chairman is invalid.
     */
    @FXML private Label mChmNameError;

    /* An Image View displaying the secretary signature. */
    @FXML private ImageView mSecSignatureView;

    /* Text Fields holding the secretary name. */
    @FXML private TextField mSecFirstName, mSecMiddleName, mSecLastName;

    /**
     * A Label holding the secretary name error, which is set to visible when the
     * name of the secretary is invalid.
     */
    @FXML private Label mSecNameError;

    /* Text Fields holding the treasurer name. */
    @FXML private TextField mTrsrFirstName, mTrsrMiddleName, mTrsrLastName;

    /**
     * A Label holding the treasurer name error, which is set to visible when the
     * name of the treasurer is invalid.
     */
    @FXML private Label mTrsrNameError;

    /**
     * A Label holding the kagawad name error, which is set to visible when at
     * least one of the names of the kagawads is invalid.
     */
    @FXML private Label mKagawadNameError;

    /**
     * A reference to the chairman image passed from the Photoshop Control. The
     * Chairman Photo Image is null if the chairman's photo wasn't changed starting
     * from the time in which the this scene is shown. If the image is not null and
     * when the save button is clicked, then the chairman photo image is permanently
     * stored.
     *
     * @see PhotoshopControl
     */
    private WritableImage mChmPhoto;

    /**
     * A reference to the chairman signature image passed from the Photoshop Control.
     * The Chairman Signature Photo Image is null if the chairman's signature photo
     * wasn't changed starting from the time in which the this scene is shown. If the
     * image is not null and when the save button is clicked, then the chairman
     * signature photo image is permanently stored.
     *
     * @see PhotoshopControl
     */
    private WritableImage mChmSignature;

    /**
     * A reference to the secretary signature image passed from the Photoshop Control.
     * The Secretary Signature Photo Image is null if the secretary's signature photo
     * wasn't changed starting from the time in which the this scene is shown. If the
     * image is not null and when the save button is clicked, then the secretary signature
     * photo image is permanently stored.
     *
     * @see PhotoshopControl
     */
    private WritableImage mSecSignature;

    /**
     * A listener that listens to this controller for any action events that needs to
     * be handled outside of this controller.
     *
     * @see OnBarangayAgentListener
     */
    private OnBarangayAgentListener mListener;

    /**
     * Stores all barangay agent data.
     */
    private PreferenceModel mPreferences;

    @FXML
    private void initialize() {
        new NodeNameHandler(mKagawadPane, 7, NodeNameHandler.OPERATION_ONE_TO_MANY);
        mKagawadPane.heightProperty().addListener(observable -> mScrollPane.setVvalue(1));
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
    public void onSecSignatureCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onSecSignatureCaptureButtonClicked();
    }

    @FXML
    public void onSecSignatureUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onSecSignatureUploadButtonClicked();
    }

    /**
     * Check if the data inputted is valid. If the data is valid, then save the data to Json. Hey, Json!
     * @param actionEvent
     */
    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {
        boolean isDataValid = true;

        Function<Node[], Boolean> validateName = (name) -> {
            boolean isValid;

            TextField firstName = (TextField) name[0];
            TextField middleName = (TextField) name[1];
            TextField lastName = (TextField) name[2];

            firstName.setStyle(firstName.getText().matches("[a-zA-Z\\s]+") ? null : "-fx-border-color: #FF3F3F;");
            middleName.setStyle(middleName.getText().matches("[a-zA-Z\\s]+") ? null : "-fx-border-color: #FF3F3F;");
            lastName.setStyle(lastName.getText().matches("[a-zA-Z\\s]+") ? null : "-fx-border-color: #FF3F3F;");

            isValid = firstName.getText().matches("[a-zA-Z\\s]+") &&
                    middleName.getText().matches("[a-zA-Z\\s]+") &&
                    lastName.getText().matches("[a-zA-Z\\s]+");

            if (name.length > 3) {
                Label nameError = (Label) name[3];
                nameError.setVisible(!isValid);
            }

            return isValid;
        };

        // Check chairman name input.
        boolean result = validateName.apply(new Node[]{mChmFirstName, mChmMiddleName, mChmLastName, mChmNameError});
        isDataValid = result? isDataValid : false;

        // Check secretary name input.
        result = validateName.apply(new Node[]{mSecFirstName, mSecMiddleName, mSecLastName, mSecNameError});
        isDataValid = result? isDataValid : false;

        // Check treasurer name input.
        result = validateName.apply(new Node[]{mTrsrFirstName, mTrsrMiddleName, mTrsrLastName, mTrsrNameError});
        isDataValid = result? isDataValid : false;

        // Test the input of the visible kagawad holders.
        mKagawadNameError.setVisible(false);

        if (isDataValid) {
            // Save chairman name.
            mPreferences.put(PreferenceContract.CHAIRMAN_FIRST_NAME, mChmFirstName.getText());
            mPreferences.put(PreferenceContract.CHAIRMAN_MIDDLE_NAME, mChmMiddleName.getText());
            mPreferences.put(PreferenceContract.CHAIRMAN_LAST_NAME, mChmLastName.getText());

            // Save secretary name.
            mPreferences.put(PreferenceContract.SECRETARY_FIRST_NAME, mSecFirstName.getText());
            mPreferences.put(PreferenceContract.SECRETARY_MIDDLE_NAME, mSecMiddleName.getText());
            mPreferences.put(PreferenceContract.SECRETARY_LAST_NAME, mSecLastName.getText());

            // Save Treasurer name.
            mPreferences.put(PreferenceContract.TREASURER_FIRST_NAME, mTrsrFirstName.getText());
            mPreferences.put(PreferenceContract.TREASURER_MIDDLE_NAME, mTrsrMiddleName.getText());
            mPreferences.put(PreferenceContract.TREASURER_LAST_NAME, mTrsrLastName.getText());

            /**
             * Create image and store its path to the corresponding preference.
             * @param prefKey is where the path is stored.
             * @Param image is the image to be created.
             */
            BiConsumer<String, WritableImage> writeImage = (prefKey, image) -> {
                try {
                    // Save the image in the appropriate directory with a unique uuid name.
                    String imagePath = System.getenv("PUBLIC");
                    switch (prefKey) {
                        case PreferenceContract.CHAIRMAN_PHOTO_PATH :
                            imagePath += "/Barangay131/Photos/" + UUID.randomUUID() + ".png";
                            break;
                        default:
                            imagePath += "/Barangay131/Signatures/" + UUID.randomUUID() + ".png";
                    }

                    File file = new File(imagePath);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(
                            renderedImage,
                            "png",
                            file);

                    // Save the photo of the chairman.
                    mPreferences.put(prefKey, imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            // Create the images.
            if (mChmPhoto != null) {
                writeImage.accept(PreferenceContract.CHAIRMAN_PHOTO_PATH, mChmPhoto);

                mChmPhoto = null;
            }

            if (mChmSignature != null) {
                writeImage.accept(PreferenceContract.CHAIRMAN_SIGNATURE_PATH, mChmSignature);

                mPreferences.put(PreferenceContract.BRGY_ID_CHM_SIGNATURE_DIMENSION, null);
                mPreferences.put(PreferenceContract.BRGY_CLEARANCE_CHM_SIGNATURE_DIMENSION, null);
                mPreferences.put(PreferenceContract.BUSI_CLEARANCE_CHM_SIGNATURE_DIMENSION, null);

                mChmSignature = null;
            }

            if (mSecSignature != null) {
                writeImage.accept(PreferenceContract.SECRETARY_SIGNATURE_PATH, mSecSignature);

                mPreferences.put(PreferenceContract.BRGY_CLEARANCE_SEC_SIGNATURE_DIMENSION, null);
                mPreferences.put(PreferenceContract.BUSI_CLEARANCE_SEC_SIGNATURE_DIMENSION, null);

                mSecSignature = null;
            }
            mPreferences.save();

            mListener.onFinished();
        }
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onFinished();
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

    /**
     * Update the signature photo of the secretary from the photoshop process callback function.
     * @param image
     */
    public void setSecSignature(WritableImage image) {
        mSecSignature = image;
        mSecSignatureView.setImage(image);
    }

    /**
     * Pass the preference model to this controller from the Main control.
     * Also, immediately resetScene the scene with the data from the preference model.
     * @param preferenceModel
     */
    public void setPreferenceModel(PreferenceModel preferenceModel) {
        mPreferences = preferenceModel;
    }

    /**
     * Populate the scene with the barangay data.
     */
    public void resetScene() {
        // Reset chairman data.
        mChmFirstName.setText(mPreferences.get(PreferenceContract.CHAIRMAN_FIRST_NAME, ""));
        mChmMiddleName.setText(mPreferences.get(PreferenceContract.CHAIRMAN_MIDDLE_NAME, ""));
        mChmLastName.setText(mPreferences.get(PreferenceContract.CHAIRMAN_LAST_NAME, ""));

        mChmFirstName.setStyle(null);
        mChmMiddleName.setStyle(null);
        mChmLastName.setStyle(null);

        mChmNameError.setVisible(false);

        String mChmPhotoPath = mPreferences.get(PreferenceContract.CHAIRMAN_PHOTO_PATH);
        if (mChmPhotoPath != null)
            mChmPhotoView.setImage(new Image("file:" + mChmPhotoPath));

        String mChmSignaturePath = mPreferences.get(PreferenceContract.CHAIRMAN_SIGNATURE_PATH);
        if (mChmSignaturePath != null)
            mChmSignatureView.setImage(new Image("file:" + mChmSignaturePath));

        // Reset secretary data.
        mSecFirstName.setText(mPreferences.get(PreferenceContract.SECRETARY_FIRST_NAME, ""));
        mSecMiddleName.setText(mPreferences.get(PreferenceContract.SECRETARY_MIDDLE_NAME, ""));
        mSecLastName.setText(mPreferences.get(PreferenceContract.SECRETARY_LAST_NAME, ""));

        mSecFirstName.setStyle(null);
        mSecMiddleName.setStyle(null);
        mSecLastName.setStyle(null);

        mSecNameError.setVisible(false);

        String mSecSignaturePath = mPreferences.get(PreferenceContract.SECRETARY_SIGNATURE_PATH);
        if (mSecSignaturePath != null)
            mSecSignatureView.setImage(new Image("file:" + mSecSignaturePath));

        // Reset treasurer data.
        mTrsrFirstName.setText(mPreferences.get(PreferenceContract.TREASURER_FIRST_NAME, ""));
        mTrsrMiddleName.setText(mPreferences.get(PreferenceContract.TREASURER_MIDDLE_NAME, ""));
        mTrsrLastName.setText(mPreferences.get(PreferenceContract.TREASURER_LAST_NAME, ""));

        mTrsrFirstName.setStyle(null);
        mTrsrMiddleName.setStyle(null);
        mTrsrLastName.setStyle(null);

        mTrsrNameError.setVisible(false);

    }
}
