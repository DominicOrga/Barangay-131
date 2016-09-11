package javah.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javah.Main;
import javah.contract.CSSContract;
import javah.model.PreferenceModel;
import javah.contract.PreferenceContract;
import javah.util.BarangayUtils;
import javah.util.NodeNameHandler;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A controller that setups the barangay officials for use in information
 * generation. This controller has access to the Preference Model to store the
 * non-binary data of the barangay officials and the Photoshop control to
 * manage the photo and signatures of the chairman and secretary.
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

    /* A ComboBox for the chairman name auxiliary. */
    @FXML private ComboBox mChmAuxiliary;

    /**
     * A Label holding the chairman name error, which is set to visible when the name
     * of the chairman is invalid.
     */
    @FXML private Label mChmNameError;

    /* An Image View displaying the secretary signature. */
    @FXML private ImageView mSecSignatureView;

    /* Text Fields holding the secretary name. */
    @FXML private TextField mSecFirstName, mSecMiddleName, mSecLastName;

    /* A ComboBox for the secretary name auxiliary. */
    @FXML private ComboBox mSecAuxiliary;

    /**
     * A Label holding the secretary name error, which is set to visible when the
     * name of the secretary is invalid.
     */
    @FXML private Label mSecNameError;

    /* Text Fields holding the treasurer name. */
    @FXML private TextField mTrsrFirstName, mTrsrMiddleName, mTrsrLastName;

    /* A ComboBox for the secretary name auxiliary. */
    @FXML private ComboBox mTrsrAuxiliary;

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

    /* Handles the dynamic nodes of the kagawad names. */
    private NodeNameHandler mNodeNameHandler;

    /**
     * Assign a Node Name Handler to the Kagawad Pane to handle the addition and
     * removal of the name node for the kagawad.
     *
     * @see NodeNameHandler
     */
    @FXML
    private void initialize() {
        mNodeNameHandler = new NodeNameHandler(mKagawadPane, 7, NodeNameHandler.OPERATION_ONE_TO_MANY);

        // Everytime the Kagawad Pane adjusts in height due to addition or removal of node
        // names, then always set the vertical scroll pane at the bottom.
        mKagawadPane.heightProperty().addListener(observable -> mScrollPane.setVvalue(1));

        // Limit the text length within the text fields to a maximum of 25.
        BarangayUtils.addTextLimitListener(mChmFirstName, 25);
        BarangayUtils.addTextLimitListener(mChmMiddleName, 25);
        BarangayUtils.addTextLimitListener(mChmLastName, 25);
        BarangayUtils.addTextLimitListener(mSecFirstName, 25);
        BarangayUtils.addTextLimitListener(mSecMiddleName, 25);
        BarangayUtils.addTextLimitListener(mSecLastName, 25);
        BarangayUtils.addTextLimitListener(mTrsrFirstName, 25);
        BarangayUtils.addTextLimitListener(mTrsrMiddleName, 25);
        BarangayUtils.addTextLimitListener(mTrsrLastName, 25);

        // Add listener to the root pane visibility properties. If the root pane is set
        // to visible, then reset the data.
        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Reset chairman data.
                mChmFirstName.setText(mPreferences.get(PreferenceContract.CHAIRMAN_FIRST_NAME, null));
                mChmMiddleName.setText(mPreferences.get(PreferenceContract.CHAIRMAN_MIDDLE_NAME, null));
                mChmLastName.setText(mPreferences.get(PreferenceContract.CHAIRMAN_LAST_NAME, null));

                mChmAuxiliary.setValue(mPreferences.get(PreferenceContract.CHAIRMAN_AUXILIARY, "N/A"));

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
                mSecFirstName.setText(mPreferences.get(PreferenceContract.SECRETARY_FIRST_NAME, null));
                mSecMiddleName.setText(mPreferences.get(PreferenceContract.SECRETARY_MIDDLE_NAME, null));
                mSecLastName.setText(mPreferences.get(PreferenceContract.SECRETARY_LAST_NAME, null));

                mSecAuxiliary.setValue(mPreferences.get(PreferenceContract.SECRETARY_AUXILIARY, "N/A"));

                mSecFirstName.setStyle(null);
                mSecMiddleName.setStyle(null);
                mSecLastName.setStyle(null);

                mSecNameError.setVisible(false);

                String mSecSignaturePath = mPreferences.get(PreferenceContract.SECRETARY_SIGNATURE_PATH);
                if (mSecSignaturePath != null)
                    mSecSignatureView.setImage(new Image("file:" + mSecSignaturePath));

                // Reset treasurer data.
                mTrsrFirstName.setText(mPreferences.get(PreferenceContract.TREASURER_FIRST_NAME, null));
                mTrsrMiddleName.setText(mPreferences.get(PreferenceContract.TREASURER_MIDDLE_NAME, null));
                mTrsrLastName.setText(mPreferences.get(PreferenceContract.TREASURER_LAST_NAME, null));

                mTrsrAuxiliary.setValue(mPreferences.get(PreferenceContract.TREASURER_AUXILIARY, "N/A"));

                mTrsrFirstName.setStyle(null);
                mTrsrMiddleName.setStyle(null);
                mTrsrLastName.setStyle(null);

                mTrsrNameError.setVisible(false);

                mKagawadNameError.setVisible(false);

                mNodeNameHandler.removeNodeNames();
                // Reset Kagawad data.
                for (int i = 0; i < 7; i++) {
                    String firstName = mPreferences.get(PreferenceContract.KAGAWAD_NAMES[i][0], null);

                    if (firstName == null) {
                        if (i == 0)
                            mNodeNameHandler.addName(null, null, null, null);

                        break;
                    }

                    String middleName = mPreferences.get(PreferenceContract.KAGAWAD_NAMES[i][1], null);
                    String lastName = mPreferences.get(PreferenceContract.KAGAWAD_NAMES[i][2], null);
                    String auxiliary = mPreferences.get(PreferenceContract.KAGAWAD_NAMES[i][3], null);

                    mNodeNameHandler.addName(firstName, middleName, lastName, auxiliary);
                }
            }
        });
    }

    /**
     * Tell the Main Control to show the Photoshop scene to capture an image for the
     * chairman signature.
     *
     * @param actionEvent
     *        The event fired when the button is clicked. No usage.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onChmSignatureCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onChmSignatureCaptureButtonClicked();
    }

    /**
     * Tell the Main Control to show the Photoshop scene to upload an image for the
     * chairman signature.
     *
     * @param actionEvent
     *        The event fired when the button is clicked. No usage.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onChmSignatureUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onChmSignatureUploadButtonClicked();
    }

    /**
     * Tell the Main Control to show the Photoshop scene to capture an image for the
     * chairman display photo.
     *
     * @param actionEvent
     *        The event fired when the button is clicked. No usage.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onChmCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onChmCaptureButtonClicked();
    }

    /**
     * Tell the Main Control to show the Photoshop scene to upload an image for the
     * chairman display photo.
     *
     * @param actionEvent
     *        The event fired when the button is clicked. No usage.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onChmUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onChmUploadButtonClicked();
    }

    /**
     * Tell the Main Control to show the Photoshop scene to capture an image for the
     * secretary signature.
     *
     * @param actionEvent
     *        The event fired when the button is clicked. No usage.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onSecSignatureCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onSecSignatureCaptureButtonClicked();
    }

    /**
     * Tell the Main Control to show the Photoshop scene to upload an image for the
     * secretary signature.
     *
     * @param actionEvent
     *        The event fired when the button is clicked. No usage.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onSecSignatureUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onSecSignatureUploadButtonClicked();
    }

    /**
     * Check if the data inputted are valid. If the data is valid, then save the data
     * to Json. Hey, Json!
     *
     * @param actionEvent
     *        The event fired when the button is clicked. No usage.
     */
    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {
        boolean isDataValid = true;

        Function<Node[], Boolean> validateName = (name) -> {
            boolean isValid = true;

            TextField firstName = (TextField) name[0];
            TextField middleName = (TextField) name[1];
            TextField lastName = (TextField) name[2];

            if (firstName.getText().trim().isEmpty()) {
                firstName.setStyle(CSSContract.STYLE_TEXTFIELD_ERROR);
                isValid = false;
            } else
                firstName.setStyle(null);

            if (middleName.getText().trim().isEmpty()) {
                middleName.setStyle(CSSContract.STYLE_TEXTFIELD_ERROR);
                isValid = false;
            } else
                firstName.setStyle(null);

            if (lastName.getText().trim().isEmpty()) {
                lastName.setStyle(CSSContract.STYLE_TEXTFIELD_ERROR);
                isValid = false;
            } else
                firstName.setStyle(null);

            Label nameError = (Label) name[3];
            nameError.setVisible(!isValid);

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
        boolean isKagawadNamesValid = mNodeNameHandler.validateNodeNames();
        mKagawadNameError.setVisible(!isKagawadNamesValid);
        isDataValid = isKagawadNamesValid ? isDataValid : false;

        if (isDataValid) {
            // Save chairman name.
            mPreferences.put(PreferenceContract.CHAIRMAN_FIRST_NAME,
                    BarangayUtils.capitalizeString(mChmFirstName.getText()));
            mPreferences.put(PreferenceContract.CHAIRMAN_MIDDLE_NAME,
                    BarangayUtils.capitalizeString(mChmMiddleName.getText()));
            mPreferences.put(PreferenceContract.CHAIRMAN_LAST_NAME,
                    BarangayUtils.capitalizeString(mChmLastName.getText()));
            mPreferences.put(PreferenceContract.CHAIRMAN_AUXILIARY,
                    mChmAuxiliary.getValue().equals("N/A") ? null : mChmAuxiliary.getValue().toString());

            // Save secretary name.
            mPreferences.put(PreferenceContract.SECRETARY_FIRST_NAME,
                    BarangayUtils.capitalizeString(mSecFirstName.getText()));
            mPreferences.put(PreferenceContract.SECRETARY_MIDDLE_NAME,
                    BarangayUtils.capitalizeString(mSecMiddleName.getText()));
            mPreferences.put(PreferenceContract.SECRETARY_LAST_NAME,
                    BarangayUtils.capitalizeString(mSecLastName.getText()));
            mPreferences.put(PreferenceContract.SECRETARY_AUXILIARY,
                    mSecAuxiliary.getValue().equals("N/A") ? null : mSecAuxiliary.getValue().toString());

            // Save Treasurer name.
            mPreferences.put(PreferenceContract.TREASURER_FIRST_NAME,
                    BarangayUtils.capitalizeString(mTrsrFirstName.getText()));
            mPreferences.put(PreferenceContract.TREASURER_MIDDLE_NAME,
                    BarangayUtils.capitalizeString(mTrsrMiddleName.getText()));
            mPreferences.put(PreferenceContract.TREASURER_LAST_NAME,
                    BarangayUtils.capitalizeString(mTrsrLastName.getText()));
            mPreferences.put(PreferenceContract.TREASURER_AUXILIARY,
                    mTrsrAuxiliary.getValue().equals("N/A") ? null : mTrsrAuxiliary.getValue().toString());

            // Save Kagawad Names.
            for (int i = 0; i < 7; i++) {
                String[] name = mNodeNameHandler.getName(i + 1);

                // Array[k][0] = kagawad k first name.
                // Array[k][1] = kagawad k middle name.
                // Array[k][2] = kagawad k last name.
                // Array[k][3] = kagawad k auxiliary.
                mPreferences.put(PreferenceContract.KAGAWAD_NAMES[i][0],
                        name == null ? null : BarangayUtils.capitalizeString(name[0]));
                mPreferences.put(PreferenceContract.KAGAWAD_NAMES[i][1],
                        name == null ? null : BarangayUtils.capitalizeString(name[1]));
                mPreferences.put(PreferenceContract.KAGAWAD_NAMES[i][2],
                        name == null ? null : BarangayUtils.capitalizeString(name[2]));
                mPreferences.put(PreferenceContract.KAGAWAD_NAMES[i][3],
                        name == null ? null : name[3]);
            }

            /**
             * Create image and store its path to the corresponding preference.
             *
             * @param prefKey
             *        The preference where the path is stored.
             * @param image
             *        The image to be created. Either a display photo or a signature.
             */
            BiConsumer<String, WritableImage> writeImage = (prefKey, image) -> {
                try {
                    // Save the image in the appropriate directory with a unique uuid name.
                    String targetImage = null;
                    switch (prefKey) {
                        case PreferenceContract.CHAIRMAN_PHOTO_PATH :
                            targetImage += Main.PHOTO_DIR_PATH + "/" + UUID.randomUUID() + ".png";
                            break;
                        default:
                            targetImage += Main.SIGNATURE_DIR_PATH + "/" + UUID.randomUUID() + ".png";
                    }

                    File file = new File(targetImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(
                            renderedImage,
                            "png",
                            file);

                    // Save the photo of the chairman.
                    mPreferences.put(prefKey, targetImage);
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

    /**
     * Reset the values of the nodes if the cancel button is clicked.
     *
     * @param actionEvent
     *        The event fired when the button is clicked. No usage.
     */
    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onFinished();
    }

    public void setListener(OnBarangayAgentListener listener) {
        mListener = listener;
    }

    /**
     * Disable or enable the ResidentFormControl. Used when the photoshop popup scene
     * is displayed.
     *
     * @param disable
     *        Determines whether to enable or disable the Root Pane.
     */
    public void setDisable(boolean disable) {
        mRootPane.setDisable(disable);
    }

    /**
     * Update the display photo of the chairman from the photoshop process callback
     * function.
     *
     * @param image
     *        Image requested from the Photoshop Control.
     *
     * @see PhotoshopControl
     */
    public void setChmPhoto(WritableImage image) {
        mChmPhoto = image;
        mChmPhotoView.setImage(image);
    }

    /**
     * Update the signature photo of the chairman from the photoshop process callback
     * function.
     *
     * @param image
     *        Image requested from the Photoshop Control.
     *
     * @see PhotoshopControl
     */
    public void setChmSignature(WritableImage image) {
        mChmSignature = image;
        mChmSignatureView.setImage(image);
    }

    /**
     * Update the signature photo of the secretary from the photoshop process callback
     * function.
     *
     * @param image
     *        Image requested from the Photoshop Control.
     *
     * @see PhotoshopControl
     */
    public void setSecSignature(WritableImage image) {
        mSecSignature = image;
        mSecSignatureView.setImage(image);
    }

    /**
     * Pass the preference model to this controller from the Main control.
     * Also, immediately resetScene the scene with the data from the preference model.
     *
     * @param preferenceModel
     *        The universal Preference Model acquired from the Main Control.
     *
     * @see MainControl
     */
    public void setPreferenceModel(PreferenceModel preferenceModel) {
        mPreferences = preferenceModel;
    }
}
