package javah.controller;

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
import javafx.scene.text.Text;
import javah.model.PreferenceModel;
import javah.util.KagawadHolder;
import javah.util.PreferenceContract;

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
 * This class will handle the setup of the barangay officials.
 */
public class BarangayAgentControl {

    public interface OnBarangayAgentListener {
        void onChmUploadButtonClicked();
        void onChmCaptureButtonClicked();
        void onChmSignatureUploadButtonClicked();
        void onChmSignatureCaptureButtonClicked();
        void onSecSignatureUploadButtonClicked();
        void onSecSignatureCaptureButtonClicked();
        void onCancelButtonClicked();
        void onSaveButtonClicked();
    }

    @FXML private Pane mRootPane;
    @FXML private ScrollPane mScrollPane;
    @FXML private Pane mKagawadPane;

    /**
     * FXML components related to the chairman.
     */
    @FXML private ImageView mChmPhotoView, mChmSignatureView;
    @FXML private TextField mChmFirstName, mChmMiddleName, mChmLastName;
    @FXML private Label mChmNameError;

    /**
     * FXML components relation to the secretary.
     */
    @FXML private ImageView mSecSignatureView;
    @FXML private TextField mSecFirstName, mSecMiddleName, mSecLastName;
    @FXML private Label mSecNameError;

    @FXML private TextField mTrsrFirstName, mTrsrMiddleName, mTrsrLastName;
    @FXML private Label mTrsrNameError;

    @FXML private Label mKagawadNameError;

    /**
     * The chairman display photo and signature photo.
     */
    private WritableImage mChmPhoto, mChmSignature;

    /**
     * The secretary signature photo.
     */
    private WritableImage mSecSignature;

    private OnBarangayAgentListener mListener;

    /**
     * Stores all barangay agent data.
     */
    private PreferenceModel mPreferences;

    /**
     * Holds all the kagawad containers.
     */
    private List<KagawadHolder> mKagawadHolders = new ArrayList<>();

    /**
     * The current last visible kagawad holder.
     */
    private KagawadHolder mLastShownKagawadHolder;

    /**
     * The preceding kagawad holder from the last one.
     */
    private KagawadHolder mBeforeLastShownKagawadHolder;

    /**
     * Determines the state of each kagawad holder if whether they are occupied or not.
     */
    private boolean[] mKagawadHolderVisibility = new boolean[7];

    /**
     * Determines the position of each visible kagawad holder.
     */
    private List<Integer> mKagawadHolderPlacement;

    @FXML
    private void initialize() {
        // Extract all the kagawad containers pass them to mKagawadHolders.
        List<Node> kagawadPane = mKagawadPane.getChildren();

        int size = kagawadPane.size();
        for (int i = 0; i < size; i++) {
            KagawadHolder kagawadHolder = new KagawadHolder(kagawadPane.get(i));
            // Add the extracted kagawad container and add it to mKagawadHolders.
            mKagawadHolders.add(kagawadHolder);

            // If an add button is clicked, then display another kagawad holder whenever possible.
            kagawadHolder.getAddButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                setKagawadHolderVisible(-1, true);
            });

            // If the remove button of a kagawad holder is clicked, then hide it and clear its data.
            final int j = i;
            kagawadHolder.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                setKagawadHolderVisible(j, false);
            });
        }

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
        for (int i = 0; i < 7; i++) {
            if (mKagawadHolderVisibility[i]) {
                // If the kagawad holder is visible, then validate its data.
                KagawadHolder kagawadHolder = mKagawadHolders.get(i);

                result = validateName.apply(new Node[]{kagawadHolder.getFirstNameField(),
                        kagawadHolder.getMiddleNameField(),
                        kagawadHolder.getLastNameField()});

                mKagawadNameError.setVisible(result ? mKagawadNameError.isVisible() : true);

                isDataValid = result ? isDataValid : false;
            }
        }

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

            KagawadHolder kagawadHolder = mKagawadHolders.get(0);
            mPreferences.put(PreferenceContract.KAGAWAD_1_FIRST_NAME, kagawadHolder.getFirstNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_1_MIDDLE_NAME, kagawadHolder.getMiddleNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_1_LAST_NAME, kagawadHolder.getLastNameField().getText());

            kagawadHolder = mKagawadHolders.get(1);
            mPreferences.put(PreferenceContract.KAGAWAD_2_FIRST_NAME, kagawadHolder.getFirstNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_2_MIDDLE_NAME, kagawadHolder.getMiddleNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_2_LAST_NAME, kagawadHolder.getLastNameField().getText());

            kagawadHolder = mKagawadHolders.get(2);
            mPreferences.put(PreferenceContract.KAGAWAD_3_FIRST_NAME, kagawadHolder.getFirstNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_3_MIDDLE_NAME, kagawadHolder.getMiddleNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_3_LAST_NAME, kagawadHolder.getLastNameField().getText());

            kagawadHolder = mKagawadHolders.get(3);
            mPreferences.put(PreferenceContract.KAGAWAD_4_FIRST_NAME, kagawadHolder.getFirstNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_4_MIDDLE_NAME, kagawadHolder.getMiddleNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_4_LAST_NAME, kagawadHolder.getLastNameField().getText());

            kagawadHolder = mKagawadHolders.get(4);
            mPreferences.put(PreferenceContract.KAGAWAD_5_FIRST_NAME, kagawadHolder.getFirstNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_5_MIDDLE_NAME, kagawadHolder.getMiddleNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_5_LAST_NAME, kagawadHolder.getLastNameField().getText());

            kagawadHolder = mKagawadHolders.get(5);
            mPreferences.put(PreferenceContract.KAGAWAD_6_FIRST_NAME, kagawadHolder.getFirstNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_6_MIDDLE_NAME, kagawadHolder.getMiddleNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_6_LAST_NAME, kagawadHolder.getLastNameField().getText());

            kagawadHolder = mKagawadHolders.get(6);
            mPreferences.put(PreferenceContract.KAGAWAD_7_FIRST_NAME, kagawadHolder.getFirstNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_7_MIDDLE_NAME, kagawadHolder.getMiddleNameField().getText());
            mPreferences.put(PreferenceContract.KAGAWAD_7_LAST_NAME, kagawadHolder.getLastNameField().getText());

            /**
             * Create image and store its path to the corresponding preference.
             * @param prefKey is where the path is stored.
             * @Param image is the image to be created.
             */
            BiConsumer<String, WritableImage> writeImage = (prefKey, image) -> {
                try {
                    // Save the chairman photo in the appropriate directory with a unique uuid name.
                    String imagePath = System.getenv("PUBLIC") + "/Barangay131/Photos/" + UUID.randomUUID() + ".png";

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
            if (mChmPhoto != null)
                writeImage.accept(PreferenceContract.CHAIRMAN_PHOTO_PATH, mChmPhoto);

            if (mChmSignature != null)
                writeImage.accept(PreferenceContract.CHAIRMAN_SIGNATURE_PATH, mChmSignature);

            if (mSecSignature != null)
                writeImage.accept(PreferenceContract.CHAIRMAN_SIGNATURE_PATH, mSecSignature);

            mPreferences.save();

            mListener.onSaveButtonClicked();
        }
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
        mKagawadHolderPlacement = new ArrayList<>();
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
            mChmSignatureView.setImage(new Image("file:" + mSecSignaturePath));

        // Reset treasurer data.
        mTrsrFirstName.setText(mPreferences.get(PreferenceContract.TREASURER_FIRST_NAME, ""));
        mTrsrMiddleName.setText(mPreferences.get(PreferenceContract.TREASURER_MIDDLE_NAME, ""));
        mTrsrLastName.setText(mPreferences.get(PreferenceContract.TREASURER_LAST_NAME, ""));

        mTrsrFirstName.setStyle(null);
        mTrsrMiddleName.setStyle(null);
        mTrsrLastName.setStyle(null);

        mTrsrNameError.setVisible(false);

        Consumer<String[]> populateLastShownKagawadHolder = (name) -> {
            mLastShownKagawadHolder.getFirstNameField().setText(name[0]);
            mLastShownKagawadHolder.getMiddleNameField().setText(name[1]);
            mLastShownKagawadHolder.getLastNameField().setText(name[2]);
        };

        // Hide all the kagawad holders by default.
        mKagawadPane.getChildren().removeAll(mKagawadPane.getChildren());
        Arrays.fill(mKagawadHolderVisibility, false);

        // Populate the kagawad with data.
        String firstName, middleName, lastName;

        // If kagawad 1 is not empty, then add the kagawad to the kagawad holder 0.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_1_FIRST_NAME, "");
        if (!firstName.isEmpty()) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_1_MIDDLE_NAME, "");
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_1_LAST_NAME, "");
            setKagawadHolderVisible(0, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 2 is not empty, then add the kagawad to the kagawad holder 1.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_2_FIRST_NAME, "");
        if (!firstName.isEmpty()) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_2_MIDDLE_NAME, "");
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_2_LAST_NAME, "");
            setKagawadHolderVisible(1, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 3 is not empty, then add the kagawad to the kagawad holder 2.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_3_FIRST_NAME, "");
        if (!firstName.isEmpty()) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_3_MIDDLE_NAME, "");
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_3_LAST_NAME, "");
            setKagawadHolderVisible(2, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 4 is not empty, then add the kagawad to the kagawad holder 3.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_4_FIRST_NAME, "");
        if (!firstName.isEmpty()) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_4_MIDDLE_NAME, "");
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_4_LAST_NAME, "");
            setKagawadHolderVisible(3, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 5 is not empty, then add the kagawad to the kagawad holder 4.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_5_FIRST_NAME, "");
        if (!firstName.isEmpty()) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_5_MIDDLE_NAME, "");
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_5_LAST_NAME, "");
            setKagawadHolderVisible(4, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 6 is not empty, then add the kagawad to the kagawad holder 5.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_6_FIRST_NAME, "");
        if (!firstName.isEmpty()) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_6_MIDDLE_NAME, "");
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_6_LAST_NAME, "");
            setKagawadHolderVisible(5, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 7 is not empty, then add the kagawad to the kagawad holder 6.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_7_FIRST_NAME, "");
        if (!firstName.isEmpty()) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_7_MIDDLE_NAME, "");
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_7_LAST_NAME, "");
            setKagawadHolderVisible(6, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        mKagawadNameError.setVisible(false);

        // If no kagawad holder was populated, then display one.
        if (getKagawadHolderVisibleCount() == 0)
            setKagawadHolderVisible(-1, true);
    }

    /**
     * Show or hide a kagawad holder.
     * Called when an add button of a kagawad holder is pressed and initializing the scene.
     * @param index is equal to -1, if we want to display the closest avialable kagawad holder.
     *              Note that -1 is only used for adding kagawad holders.
     * @param visible determines the visibility of the kagawad holder at index i.
     * @returnn the index of the shown kagawad holder (0 - 6).
     */
    private int setKagawadHolderVisible(int index, boolean visible) {
        int size = mKagawadHolders.size();

        ImageView addButton;
        ImageView removeButton;

        if (visible) {
            // Add the index of the kagawad holder to be displayed to mKagawadHolderPlacement to determine its place.
            // If mLastShownKagawadHolder (the previous one) exists, then show its remove button and hide its
            // add button.
            if (mLastShownKagawadHolder != null) {
                mBeforeLastShownKagawadHolder = mLastShownKagawadHolder;
                addButton = mBeforeLastShownKagawadHolder.getAddButton();
                removeButton = mBeforeLastShownKagawadHolder.getRemoveButton();

                removeButton.setVisible(true);
                removeButton.setManaged(true);
                addButton.setVisible(false);
                addButton.setManaged(false);
            }

            switch (index) {
                case -1:
                    // If no index is given, then find the closest kagawad holder available to be displayed.
                    for (int i = 0; i < size; i++)
                        if (!mKagawadHolderVisibility[i]) {
                            // Assign the new mLastShownKagawadHolder and display it.
                            mLastShownKagawadHolder = mKagawadHolders.get(i);
                            index = i;
                            break;
                        }
                    break;

                default:
                    mLastShownKagawadHolder = mKagawadHolders.get(index);
            }

            // The kagawad holder at the given index is now visible.
            mKagawadHolderVisibility[index] = true;
            mKagawadPane.getChildren().add(mLastShownKagawadHolder.getNode());
            mKagawadHolderPlacement.add(index);
            // Make sure that the kagawad holder is not highlighted.
            mLastShownKagawadHolder.getFirstNameField().setStyle(null);
            mLastShownKagawadHolder.getMiddleNameField().setStyle(null);
            mLastShownKagawadHolder.getLastNameField().setStyle(null);

            addButton = mLastShownKagawadHolder.getAddButton();
            removeButton = mLastShownKagawadHolder.getRemoveButton();

            switch (getKagawadHolderVisibleCount()) {
                case 1:
                    // If this is the only kagawad holder visible, then only display the add button.
                    addButton.setVisible(true);
                    addButton.setManaged(true);
                    removeButton.setVisible(false);
                    removeButton.setManaged(false);
                    break;
                case 7:
                    // If all the kagawad holders are visible, then only display the remove button.
                    addButton.setVisible(false);
                    addButton.setManaged(false);
                    removeButton.setVisible(true);
                    removeButton.setManaged(true);
                    break;
                default:
                    addButton.setVisible(true);
                    addButton.setManaged(true);
                    removeButton.setVisible(true);
                    removeButton.setManaged(true);
            }

        } else {
            // Remove the kagawad holder to be hidden from the placement ranking.
            mKagawadHolderPlacement.remove(Integer.valueOf(index));
            mKagawadHolderVisibility[index] = false;

            // Clear the data of the kagawad holder to be hidden.
            KagawadHolder kagawadHolder = mKagawadHolders.get(index);
            kagawadHolder.getFirstNameField().setText("");
            kagawadHolder.getMiddleNameField().setText("");
            kagawadHolder.getLastNameField().setText("");

            if (kagawadHolder == mLastShownKagawadHolder) {
                // If the desired kagawad holder to be removed is the last displayed, then hide it and set
                // mBeforeLastShownKagawadHolder as mLastShownKagawadHolder.
                mKagawadPane.getChildren().remove(mLastShownKagawadHolder.getNode());

                // Since the kagawad holder to be hidden is removed from mKagawadHolderPlacement, we can assert that
                // the last value of mKagawadHolderPlacement is the new mLastShownKagawadHolder.
                mLastShownKagawadHolder = mKagawadHolders.get(mKagawadHolderPlacement.get(mKagawadHolderPlacement.size() - 1));

                addButton = mLastShownKagawadHolder.getAddButton();
                removeButton = mLastShownKagawadHolder.getRemoveButton();

                addButton.setVisible(true);
                addButton.setManaged(true);
                removeButton.setVisible(true);
                removeButton.setVisible(true);
            } else
                mKagawadPane.getChildren().remove(kagawadHolder.getNode());

            // If only one kagawad holder is visible, then hide the remove button.
            if (getKagawadHolderVisibleCount() == 1) {
                mLastShownKagawadHolder.getRemoveButton().setVisible(false);
                mLastShownKagawadHolder.getRemoveButton().setManaged(false);
            }

            switch (getKagawadHolderVisibleCount()) {
                case 1 :
                    mLastShownKagawadHolder.getRemoveButton().setVisible(false);
                    mLastShownKagawadHolder.getRemoveButton().setManaged(false);
                    break;
                case 7 : break;
                default :
                    mLastShownKagawadHolder.getAddButton().setVisible(true);
                    mLastShownKagawadHolder.getAddButton().setManaged(true);
            }
        }

        // Everytime a kagawad holder is added or removed, update the mScrollPane to always be on the bottom.
        mScrollPane.layout();
        mScrollPane.setVvalue(1d);

        return index;
    }

    /**
     * Count the number of visible kagawad holders.
     * @return
     */
    private int getKagawadHolderVisibleCount() {
        int count = 0;
        int size = mKagawadHolderVisibility.length;

        for (int i = 0; i < size; i++)
            if (mKagawadHolderVisibility[i]) count++;

        return count;
    }
}
