package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javah.model.PreferenceModel;
import javah.util.KagawadHolder;
import javah.util.PreferenceContract;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    @FXML private ScrollPane mScrollPane;
    @FXML private Pane mKagawadPane;

    @FXML private ImageView mChmPhotoView, mChmSignatureView;

    private WritableImage mChmPhoto, mChmSignature;

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
    private List<Integer> mKagawadHolderPlacement = new ArrayList<>();

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

        // Hide all the kagawad holders by default.
        mKagawadPane.getChildren().removeAll(mKagawadPane.getChildren());
        for (int i = 0; i < size; i++) {

//            mKagawadHolders.get(i).setVisible(false);
//            mKagawadHolders.get(i).setManaged(false);
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
     * Pass the preference model to this controller from the Main control.
     * Also, immediately populate the scene with the data from the preference model.
     * @param preferenceModel
     */
    public void setPreferenceModel(PreferenceModel preferenceModel) {
        mPreferences = preferenceModel;

        Consumer<String[]> populateLastShownKagawadHolder = (name) -> {
            mLastShownKagawadHolder.getFirstNameField().setText(name[0]);
            mLastShownKagawadHolder.getMiddleNameField().setText(name[1]);
            mLastShownKagawadHolder.getLastNameField().setText(name[2]);
        };

        // Populate the view with data.
        String firstName, middleName, lastName;

        // If kagawad 1 is not empty, then add the kagawad to the kagawad holder 0.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_1_FIRST_NAME);
        if (firstName != null) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_1_MIDDLE_NAME);
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_1_LAST_NAME);
            setKagawadHolderVisible(0, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 2 is not empty, then add the kagawad to the kagawad holder 1.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_2_FIRST_NAME);
        if (firstName != null) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_2_MIDDLE_NAME);
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_2_LAST_NAME);
            setKagawadHolderVisible(1, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 3 is not empty, then add the kagawad to the kagawad holder 2.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_3_FIRST_NAME);
        if (firstName != null) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_3_MIDDLE_NAME);
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_3_LAST_NAME);
            setKagawadHolderVisible(2, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 4 is not empty, then add the kagawad to the kagawad holder 3.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_4_FIRST_NAME);
        if (firstName != null) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_4_MIDDLE_NAME);
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_4_LAST_NAME);
            setKagawadHolderVisible(3, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 5 is not empty, then add the kagawad to the kagawad holder 4.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_5_FIRST_NAME);
        if (firstName != null) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_5_MIDDLE_NAME);
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_5_LAST_NAME);
            setKagawadHolderVisible(4, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 6 is not empty, then add the kagawad to the kagawad holder 5.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_6_FIRST_NAME);
        if (firstName != null) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_6_MIDDLE_NAME);
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_6_LAST_NAME);
            setKagawadHolderVisible(5, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

        // If kagawad 7 is not empty, then add the kagawad to the kagawad holder 6.
        firstName = mPreferences.get(PreferenceContract.KAGAWAD_7_FIRST_NAME);
        if (firstName != null) {
            middleName = mPreferences.get(PreferenceContract.KAGAWAD_7_MIDDLE_NAME);
            lastName = mPreferences.get(PreferenceContract.KAGAWAD_7_LAST_NAME);
            setKagawadHolderVisible(6, true);
            populateLastShownKagawadHolder.accept(new String[]{firstName, middleName, lastName});
        }

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
            System.out.println(mKagawadHolderPlacement.size());
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
            System.out.println(mKagawadHolderPlacement);
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
