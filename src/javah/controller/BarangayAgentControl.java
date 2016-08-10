package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javah.Main;
import javah.util.PreferenceContract;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

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

    @FXML private Pane mKagawadPane;

    @FXML private ImageView mChmPhotoView, mChmSignatureView;

    private WritableImage mChmPhoto, mChmSignature;

    private OnBarangayAgentListener mListener;

    private Preferences mPrefs;

    /**
     * Holds all the kagawad containers.
     */
    private List<Pane> mKagawadHolders;

    /**
     * Determines the state of each kagawad holder if whether they are visible or not.
     */
    private boolean[] mKagawadHolderStates = new boolean[7];

    @FXML
    private void initialize() {
        // Make connection to the Preferences.
        mPrefs = Preferences.systemRoot().node("");

        // Get all the kagawad containers pass them to mKagawadHolders.
        mKagawadHolders =  (List<Pane>)(List<?>) mKagawadPane.getChildren();

        // Set the default state of all kagawad holders to true.
        Arrays.fill(mKagawadHolderStates, true);

        String firstName, middleName, lastName;

        // If kagawad 1 is not empty, then add the kagawad to the kagawad holder 0.
        firstName = mPrefs.get(PreferenceContract.KAGAWAD_1_FIRST_NAME, null);
        if (firstName != null) {
            middleName = mPrefs.get(PreferenceContract.KAGAWAD_1_MIDDLE_NAME, null);
            lastName = mPrefs.get(PreferenceContract.KAGAWAD_1_LAST_NAME, null);
            addKagawad(0, firstName, middleName, lastName);
        }

        // If kagawad 2 is not empty, then add the kagawad to the kagawad holder 1.
        firstName = mPrefs.get(PreferenceContract.KAGAWAD_2_FIRST_NAME, null);
        if (firstName != null) {
            middleName = mPrefs.get(PreferenceContract.KAGAWAD_2_MIDDLE_NAME, null);
            lastName = mPrefs.get(PreferenceContract.KAGAWAD_2_LAST_NAME, null);
            addKagawad(1, firstName, middleName, lastName);
        }

        // If kagawad 3 is not empty, then add the kagawad to the kagawad holder 2.
        firstName = mPrefs.get(PreferenceContract.KAGAWAD_3_FIRST_NAME, null);
        if (firstName != null) {
            middleName = mPrefs.get(PreferenceContract.KAGAWAD_3_MIDDLE_NAME, null);
            lastName = mPrefs.get(PreferenceContract.KAGAWAD_3_LAST_NAME, null);
            addKagawad(2, firstName, middleName, lastName);
        }

        // If kagawad 4 is not empty, then add the kagawad to the kagawad holder 3.
        firstName = mPrefs.get(PreferenceContract.KAGAWAD_4_FIRST_NAME, null);
        if (firstName != null) {
            middleName = mPrefs.get(PreferenceContract.KAGAWAD_4_MIDDLE_NAME, null);
            lastName = mPrefs.get(PreferenceContract.KAGAWAD_4_LAST_NAME, null);
            addKagawad(3, firstName, middleName, lastName);
        }

        // If kagawad 5 is not empty, then add the kagawad to the kagawad holder 4.
        firstName = mPrefs.get(PreferenceContract.KAGAWAD_5_FIRST_NAME, null);
        if (firstName != null) {
            middleName = mPrefs.get(PreferenceContract.KAGAWAD_5_MIDDLE_NAME, null);
            lastName = mPrefs.get(PreferenceContract.KAGAWAD_5_LAST_NAME, null);
            addKagawad(4, firstName, middleName, lastName);
        }

        // If kagawad 6 is not empty, then add the kagawad to the kagawad holder 5.
        firstName = mPrefs.get(PreferenceContract.KAGAWAD_6_FIRST_NAME, null);
        if (firstName != null) {
            middleName = mPrefs.get(PreferenceContract.KAGAWAD_6_MIDDLE_NAME, null);
            lastName = mPrefs.get(PreferenceContract.KAGAWAD_6_LAST_NAME, null);
            addKagawad(5, firstName, middleName, lastName);
        }

        // If kagawad 7 is not empty, then add the kagawad to the kagawad holder 6.
        firstName = mPrefs.get(PreferenceContract.KAGAWAD_7_FIRST_NAME, null);
        if (firstName != null) {
            middleName = mPrefs.get(PreferenceContract.KAGAWAD_7_MIDDLE_NAME, null);
            lastName = mPrefs.get(PreferenceContract.KAGAWAD_7_LAST_NAME, null);
            addKagawad(6, firstName, middleName, lastName);
        }

        // Remove kagawad holders that does not display a kagawad from mKagawadPane.
        for (int i = 0; i < 7; i++)
            if (mKagawadHolderStates[i]) {
                mKagawadHolders.get(i).setVisible(false);
                mKagawadHolders.get(i).setManaged(false);
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

    /**
     * Add kagawad to kagawad holder n.
     * @param index
     * @param firstName
     * @param middleName
     * @param lastName
     */
    private void addKagawad(int index, String firstName, String middleName, String lastName) {
        mKagawadHolderStates[index] = false;

        // Get the kagawad holder.
        List<Node> kagawadHolder = mKagawadHolders.get(index).getChildren();

        TextField firstNameTxt = (TextField) kagawadHolder.get(0);
        TextField middleNameTxt = (TextField) kagawadHolder.get(1);
        TextField lastNameTxt = (TextField) kagawadHolder.get(2);

        firstNameTxt.setText(firstName);
        middleNameTxt.setText(middleName);
        lastNameTxt.setText(lastName);
    }

}
