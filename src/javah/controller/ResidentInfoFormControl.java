package javah.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javah.container.BarangayID;
import javah.container.Resident;
import javah.contract.CSSContract;
import javah.contract.DatabaseContract;
import javah.contract.PreferenceContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.model.PreferenceModel;
import javah.util.BarangayUtils;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;

/**
 *  This class will handle all the information forms of the residents.
 */
public class ResidentInfoFormControl {

    public interface OnResidentInfoFormListener {
        void onUploadButtonClicked();
        void onCaptureButtonClicked();
        void onCancelButtonClicked();

        /**
         * Create a report for the information requested with the data.
         * @param data to be inserted to the information report.
         * @param information report to be created.
         */
        void onCreateButtonClicked(Object data, byte information);
    }

    @FXML Pane mRootPane;

    /**
     * The grid pane containing the list of residents.
     */
    @FXML GridPane mResidentGridPane;
    @FXML TextField mSearchField;
    @FXML Label mCurrentPageLabel, mPageCountLabel;
    @FXML Button mBackPageButton, mNextPageButton;
    @FXML Label mActionLabel;

    /**
     * Nodes contained within the barangay ID pane.
     */
    @FXML Pane mBarangayIDPane;
    @FXML RadioButton mAddress1RadioButton, mAddress2RadioButton;
    @FXML TextArea mAddress1TextArea, mAddress2TextArea;
    @FXML ImageView mSignatureView;
    @FXML Button mSignatureUploadButton, mSignatureCaptureButton;

    @FXML Pane mBrgyClearancePane;

    @FXML Button mCreateButton;

    /**
     * Determines what information form to create.
     */
    public static final byte
            INFORMATION_BARANGAY_ID = 1,
            INFORMATION_BARANGAY_CLEARANCE = 2,
            INFORMATION_BUSINESS_CLEARANCE = 3,
            INFORMATION_BLOTTER = 4;

    /**
     * The type of information form to create.
     */
    private byte mInformation;

    /**
     * The labels in the resident grid pane.
     * Takes hold of the resident with respect to the mCurrentPage.
     */
    private Label[] mResidentLabels = new Label[10];

    private CacheModel mCacheModel;

    /**
     * Get the database model to query the information of the resident selected.
     */
    private DatabaseModel mDatabaseModel;

    /**
     * Represents the current page of the resident list paging.
     */
    private int mCurrentPage;

    /**
     * Represents the number of pages within the resident list paging.
     */
    private int mPageCount;

    /**
     * Represents the total number of residents within the resident list paging.
     * *Does not reflect cached residents.
     */
    private int mResidentCount;

    /**
     * The value representing the index of the selected resident.
     * Value range is between 0 - [mResidentIDs.size() - 1].
     */
    private int mResidentSelectedIndex;

    /**
     * The value representing which label is selected from the resident list paging.
     * Value range is between 0 - 39.
     */
    private int mLabelSelectedIndex;

    /**
     * A volatile copy of the mResidentIDsCache and mResidentNamesCache used to display the residents in the list paging.
     * This list can be filtered with the search field. Thus, making it volatile.
     */
    private List<String> mResidentIDs;
    private List<String> mResidentNames;

    /**
     * Used for: Photo upload and photo capture.
     * If this variable is not equal to null, then a signature image is to be permanently stored and passed to the
     * mBarangayID.
     */
    private WritableImage mSignatureImage;

    private OnResidentInfoFormListener mListener;

    private Resident mResidentSelected;

    private BarangayID mBarangayID;

    @FXML
    private void initialize() {
        for (int i = 0; i < 10; i++) {
            Label label = new Label();
            label.setStyle(CSSContract.STYLE_LABEL_UNSELECTED_WHITE);
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(500);
            label.setPrefWidth(1000);

            mResidentLabels[i] = label;
            mResidentGridPane.add(label, 0, i);

            // Add a label selected event listener to each label.
            final int labelIndex = i;
            label.setOnMouseClicked(event -> setResidentSelected(labelIndex));
        }

        // Set the toggle group of the radio buttons.
        ToggleGroup toggleGroup = new ToggleGroup();
        mAddress1RadioButton.setToggleGroup(toggleGroup);
        mAddress2RadioButton.setToggleGroup(toggleGroup);


    }

    /**
     * Update the resident list paging to display the residents that has a match with the text in the search field.
     * A blank search field will result to displaying all the residents.
     * @param event
     */
    @FXML
    public void onSearchButtonClicked(Event event) {
        String keywords = mSearchField.getText();

        if (keywords.trim().equals("")) {
            mResidentIDs = mCacheModel.getResidentIDsCache();
            mResidentNames = mCacheModel.getResidentNamesCache();
        } else {
            String[] keywordsArray = keywords.split(" ");

            List[] lists = BarangayUtils.filterLists(
                    mCacheModel.getResidentIDsCache(), mCacheModel.getResidentNamesCache(), keywordsArray);


            mResidentIDs = lists[0];
            mResidentNames = lists[1];
        }

        updateListPaging(false);
        mCurrentPageLabel.requestFocus();
    }

    /**
     * If the Enter key is pressed within the search field, then automatically click the search button.
     * @param event
     */
    @FXML
    public void onSearchFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            onSearchButtonClicked(null);
    }

    /**
     * Move the resident list paging to the previous page when possible.
     * @param actionEvent
     */
    @FXML
    public void onBackPageButtonClicked(ActionEvent actionEvent) {
        mCurrentPage -= 1;
        updateCurrentPage();
        mCurrentPageLabel.setText(mCurrentPage + "");

        if (mNextPageButton.isDisabled())
            mNextPageButton.setDisable(false);

        if (mCurrentPage == 1)
            mBackPageButton.setDisable(true);
    }

    /**
     * Move the resident list paging to the next page when possible.
     * @param actionEvent
     */
    @FXML
    public void onNextPageButtonClicked(ActionEvent actionEvent) {
        mCurrentPage += 1;
        updateCurrentPage();
        mCurrentPageLabel.setText(mCurrentPage + "");

        if (mBackPageButton.isDisable())
            mBackPageButton.setDisable(false);

        if (mCurrentPage == mPageCount)
            mNextPageButton.setDisable(true);
    }

    /**
     * USAGE : Barangay ID
     * Upload signature.
     * @param actionEvent
     */
    @FXML
    public void onUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onUploadButtonClicked();
    }

    /**
     * USAGE : Barangay ID
     * Capture signature.
     * @param actionEvent
     */
    @FXML
    public void onCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onCaptureButtonClicked();
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
    }

    @FXML void onCreateButtonClicked(ActionEvent actionEvent) {
        switch (mInformation) {
            case INFORMATION_BARANGAY_ID :
                // Generate a temporary unique id for the barangay id.
                mBarangayID.setID(mDatabaseModel.generateID(DatabaseContract.BarangayIdEntry.TABLE_NAME));

                mBarangayID.setResidentID(mResidentSelected.getId());
                mBarangayID.setResidentName(String.format("%s %s. %s",
                        mResidentSelected.getFirstName(),
                        mResidentSelected.getMiddleName().charAt(0),
                        mResidentSelected.getLastName()));

                mBarangayID.setAddress(mAddress1RadioButton.isSelected() ?
                        mResidentSelected.getAddress1() : mResidentSelected.getAddress2());

                mBarangayID.setPhoto(mResidentSelected.getPhotoPath());

                // Store the uploaded or captured signature (if any) permanently in Barangay131/Signatures/ and return the path.
                if (mSignatureImage != null) {
                    try {
                        // Save the photo in the approriate directory with a unique uuid name.
                        String imagePath = System.getenv("PUBLIC") + "/Barangay131/Signatures/" + UUID.randomUUID() + ".png";

                        File file = new File(imagePath);
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(mSignatureImage, null);
                        ImageIO.write(
                                renderedImage,
                                "png",
                                file);

                        // Store the path of the signature to the barangay ID.
                        mBarangayID.setResidentSignature(imagePath);
                        mBarangayID.setResidentSignatureDimension(null);

                        mSignatureImage = null;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Get the current chairman name and signature from the preferences.
                PreferenceModel prefModel = new PreferenceModel();

                mBarangayID.setChmName(String.format("%s %s. %s",
                        prefModel.get(PreferenceContract.CHAIRMAN_FIRST_NAME),
                        prefModel.get(PreferenceContract.CHAIRMAN_MIDDLE_NAME).charAt(0),
                        prefModel.get(PreferenceContract.CHAIRMAN_LAST_NAME)));

                mBarangayID.setChmSignature(prefModel.get(PreferenceContract.CHAIRMAN_SIGNATURE_PATH));

                // Check if the current chairman signature is still the same with the one registered in the latest barangay ID.
                // If so, then pass the previous chairman signature coo3+rdinates and dimension to mBarangayID.
                Object[] chmSignature = mDatabaseModel.getChmSignatureFromBarangayID();

                if (chmSignature != null) {
                    String prevSignature = (String) chmSignature[0];
                    double[] prevSignatureDimension = (double[]) chmSignature[1];

                    // If the current chairman signature is still the same with the last created barangay ID, then pass the
                    // dimension of the chairman signature.
                    mBarangayID.setChmSignatureDimension(
                            prevSignature.equals(mBarangayID.getChmSignature()) ? prevSignatureDimension : null);
                }

                // The date issued will be the current date.
                GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
                mBarangayID.setDateIssued(new Date(calendar.getTime().getTime()));

                // Set the date validity of the barangay ID.
                // Date validity is equal to (date of creation) + (1 year)||(365 days) - (1 day).
                calendar.add(Calendar.DATE, 364);

                // Add one more day to the calendar if it is a leap year.
                if (calendar.isLeapYear(calendar.get(Calendar.YEAR)))
                    calendar.add(Calendar.DATE, 1);

                mBarangayID.setDateValid(new Date(calendar.getTime().getTime()));

                // Pass the generated barangay ID to the Main Control in order to be processed into a report.
                mListener.onCreateButtonClicked(mBarangayID, mInformation);
                break;

            case INFORMATION_BARANGAY_CLEARANCE :
                break;
            case INFORMATION_BUSINESS_CLEARANCE :
                break;
            case INFORMATION_BLOTTER :
        }
    }

    public void setCacheModel(CacheModel cacheModel) {
        mCacheModel = cacheModel;
    }

    public void setDatabaseModel(DatabaseModel databaseModel) {
        mDatabaseModel = databaseModel;
    }

    /**
     * Updates the list paging, mResidentCount and mPageCount.
     * @param stayOnPage determines whether current page should be maintained or not after the update.
     */
    private void updateListPaging(boolean stayOnPage) {
        mResidentCount = mResidentIDs.size();
        mPageCount = (int) Math.ceil(mResidentCount / 10.0);
        mCurrentPage = stayOnPage ? (mPageCount < mCurrentPage) ? mCurrentPage-- : mCurrentPage : 1;

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount == 0 ? 1 + "" : mPageCount + "");

        // Disable the back page button if the current page is the first one.
        mBackPageButton.setDisable(mCurrentPage == 1 ? true : false);

        // Disable the next page button if the current page is the last one.
        mNextPageButton.setDisable(mCurrentPage >= mPageCount ? true : false);

        updateCurrentPage();
    }

    /**
     * Update current page with respect to mCurrentPage. That is, the value of mCurrentPage will determine the displayed
     * residents.
     * Moving from one page to another removes the resident selected.
     */
    private void updateCurrentPage() {
        // Make sure that no resident is selected when moving from one page to another.
        setResidentSelected(-1);

        int firstIndex = (mCurrentPage - 1) * 10;
        int lastIndex = mCurrentPage * 10 > mResidentCount - 1 ? mResidentCount - 1 : mCurrentPage * 10;
        int currentIndex = firstIndex;

        for (int i = 0; i < 10; i++) {
            if (currentIndex <= lastIndex) {
                mResidentLabels[i].setText(mResidentNames.get(currentIndex));
                currentIndex++;
            } else
                mResidentLabels[i].setText("");
        }
    }

    /**
     * Update the resident selected data displayed.
     * @param newLabelSelectedIndex is the index of the label containing the resident to be displayed. If it is equal
     *                              to -1, then the example data is displayed.
     */
    private void setResidentSelected(int newLabelSelectedIndex) {
        // Determine the index of the resident in place of the currently selected label.
        mResidentSelectedIndex = newLabelSelectedIndex + 10 * (mCurrentPage - 1);

        /**
         * This nested function will update the state of the form, depending whether a resident is selected or not.
         */
        Consumer<Boolean> setDisplayResidentInfo = (isDisplayed) -> {
            if (isDisplayed) {
                // Get the resident selected.
                mResidentSelected = mDatabaseModel.getResident(mResidentIDs.get(mResidentSelectedIndex));

                // Setup the state of the form, depending on the Information to be created.
                switch (mInformation) {
                    case INFORMATION_BARANGAY_ID :
                        // Enable the upload and capture buttons.
                        mSignatureUploadButton.setDisable(false);
                        mSignatureCaptureButton.setDisable(false);

                        // Enable the create button.
                        mCreateButton.setDisable(false);

                        // Populate the address text areas with the selected resident's address.
                        mAddress1RadioButton.setDisable(false);
                        mAddress1TextArea.setText(mResidentSelected.getAddress1());

                        if (!mResidentSelected.getAddress2().isEmpty()) {
                            mAddress2RadioButton.setDisable(false);
                            mAddress2TextArea.setText(mResidentSelected.getAddress2());
                        } else {
                            mAddress2RadioButton.setDisable(true);
                            mAddress2TextArea.setText(null);
                        }

                        // If the resident has a previous barangay ID, then check if it has a signature and get it.
                        Object[] result = mDatabaseModel.getResidentSignatureFromBarangayID(mResidentIDs.get(mResidentSelectedIndex));

                        // If a signature is found, then store it to mBarangayID.
                        if (result != null) {
                            mBarangayID.setResidentSignature((String) result[0]);
                            mBarangayID.setResidentSignatureDimension((double[]) result[1]);

                            mSignatureView.setImage(new Image("file:" + result[0]));
                        } else {
                            mSignatureView.setImage(null);
                            mBarangayID.setResidentSignature(null);
                            mBarangayID.setResidentSignatureDimension(null);
                        }

                        // mSignatureImage is set to null every time a new resident is selected or unselected.
                        mSignatureImage = null;
                        break;
                    case INFORMATION_BARANGAY_CLEARANCE :
                        break;
                    case INFORMATION_BUSINESS_CLEARANCE :
                        break;
                    case INFORMATION_BLOTTER :
                }

            } else {
                // Setup the state of the form, depending on the Information to be created.
                switch (mInformation) {
                    case INFORMATION_BARANGAY_ID :
                        // Disable the address buttons.
                        mAddress1RadioButton.setSelected(true);
                        mAddress1RadioButton.setDisable(true);
                        mAddress2RadioButton.setDisable(true);

                        // Clear the address text areas.
                        mAddress1TextArea.setText(null);
                        mAddress2TextArea.setText(null);

                        // Disable the upload and capture buttons.
                        mSignatureUploadButton.setDisable(true);
                        mSignatureCaptureButton.setDisable(true);

                        mSignatureView.setImage(null);

                        mCreateButton.setDisable(true);

                        // mSignatureImage is set to null every time a new resident is selected or unselected.
                        mSignatureImage = null;
                        break;
                    case INFORMATION_BARANGAY_CLEARANCE :
                        break;
                    case INFORMATION_BUSINESS_CLEARANCE :
                        break;
                    case INFORMATION_BLOTTER :
                }
            }
        };



        // This is where the code selection and unselection view update happens.

        // If a label is clicked without containing any resident, then ignore the event.
        if (mResidentSelectedIndex < mResidentIDs.size()) {
            // if no previous resident is selected, then simply make the new selection.
            if (mLabelSelectedIndex == -1) {
                if (newLabelSelectedIndex != -1) {
                    mResidentLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);
                    mLabelSelectedIndex = newLabelSelectedIndex;
                    setDisplayResidentInfo.accept(true);
                }

            } else
                // If there is a previous selection, unselect it.
                // Also, if the previously selected resident is selected again, then unselect it.
                if (newLabelSelectedIndex == -1 || mLabelSelectedIndex == newLabelSelectedIndex) {
                    mResidentLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED_WHITE);
                    mLabelSelectedIndex = -1;
                    mResidentSelectedIndex = -1;
                    setDisplayResidentInfo.accept(false);

                    // Unselect the previously selcted resident, then select the currently selected resident.
                } else {
                    mResidentLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED_WHITE);
                    mResidentLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);

                    mLabelSelectedIndex = newLabelSelectedIndex;
                    setDisplayResidentInfo.accept(true);
                }
        }
    }

    /**
     * Reset the whole scene, including having a fresh copy of the cached data.
     */
    public void reset() {
        mBarangayID = new BarangayID();
        mSignatureImage = null;

        // Disable the address buttons.
        mAddress1RadioButton.setSelected(true);
        mAddress1RadioButton.setDisable(true);
        mAddress2RadioButton.setDisable(true);

        // Clear the address text areas.
        mAddress1TextArea.setText(null);
        mAddress2TextArea.setText(null);

        // Disable the upload and capture buttons.
        mSignatureUploadButton.setDisable(true);
        mSignatureCaptureButton.setDisable(true);

        mSignatureView.setImage(null);

        mCreateButton.setDisable(true);

        mSearchField.setText(null);

        // Update the volatile cache to make sure that they have the updated cache.
        mResidentIDs = mCacheModel.getResidentIDsCache();
        mResidentNames = mCacheModel.getResidentNamesCache();

        // Reset the list paging.
        updateListPaging(false);
    }

    /**
     * USAGE : Barangay ID
     * A function to pass the generated signature from the PhotoshopControl.
     * @param image
     */
    public void setSignature(WritableImage image) {
        mSignatureImage = image;
        mSignatureView.setImage(image);
    }

    public void setListener(OnResidentInfoFormListener listener) {
        mListener = listener;
    }

    public void setDisable(boolean bool) {
        mRootPane.setDisable(bool);
    }

    /**
     * Update the view of the form, depending on the requested information.
     * @param information
     */
    public void setInformation(byte information) {
        mInformation = information;
        switch (mInformation) {
            case INFORMATION_BARANGAY_ID :
                mBarangayIDPane.toFront();
                mActionLabel.setText("Barangay ID Form");
                break;
            case INFORMATION_BARANGAY_CLEARANCE :
                break;
            case INFORMATION_BUSINESS_CLEARANCE :
                break;
            case INFORMATION_BLOTTER :
        }
    }
}
