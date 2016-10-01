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

import javah.Main;
import javah.container.BarangayClearance;
import javah.container.BarangayID;
import javah.container.Resident;
import javah.contract.CSSContract;
import javah.contract.DatabaseContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.util.BarangayUtils;

import javax.imageio.ImageIO;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 *  A class that handles the form forms of the residents, such as the
 *  Barangay Clearance Form and Barangay ID Form.
 */
public class ResidentInformationFormControl {

    /**
     * A listener for the ResidentInformaionFormControl.
     *
     * @see ResidentInformationFormControl
     */
    public interface OnResidentInfoFormListener {
        /**
         * Note: used for FORM_BARANGAY_ID
         *
         * Call the photoshop control to upload a signature image for the barangay ID.
         *
         * @see PhotoshopControl
         */
        void onUploadButtonClicked();

        /**
         * Note: used for FORM_BARANGAY_ID
         *
         * Call the photoshop control to capture a signature image for the barangay ID.
         *
         * @see PhotoshopControl
         */
        void onCaptureButtonClicked();

        /**
         * Tell the main control to close the Resident Information form.
         */
        void onCancelButtonClicked();

        /**
         * Generate a report based on the data given on the form.
         *
         * @param data
         *        To be used in the report generation.
         * @param formType
         *        The form used to determine what type of report to generate, which can either
         *        be a barangay clearance or a barangay ID report.
         */
        void onCreateButtonClicked(Object data, byte formType);
    }

    /* Updates this controller's UI, catering to what form to create. */
    public static final byte FORM_BARANGAY_ID = 1, FORM_BARANGAY_CLEARANCE = 2;

    /**
     * Disables the resident info form while the photoshop control is open for an
     * image signature request.
     */
    @FXML Pane mRootPane;

    /* The grid pane containing the list of residents. */
    @FXML GridPane mResidentGridPane;

    /* A search field for filtering the resident list paging. */
    @FXML TextField mSearchField;

    /* Page labels for the current page and page count. */
    @FXML Label mCurrentPageLabel, mPageCountLabel;

    /* Buttons for the back page and next page. */
    @FXML Button mBackPageButton, mNextPageButton;

    /* A label displaying what form is taking place. */
    @FXML Label mActionLabel;

    /**
     * A button to create a report with the specified form.
     *
     * Note: Disabled when no resident is selected from the resident list paging.
     */
    @FXML Button mCreateButton;

    /**
     * Note: used for FORM_BARANGAY_ID
     *
     * A node containing the child nodes for the barangay ID form.
     */
    @FXML Pane mBarangayIDPane;

    /**
     * Note: used for FORM_BARANGAY_ID
     *
     * Radio buttons to choose what resident address will be used for the
     * barangay ID.
     */
    @FXML RadioButton mAddress1RadioButton, mAddress2RadioButton;

    /**
     * Note: used for FORM_BARANGAY_ID
     * Text area for the resident addresses for the barangay ID.
     */
    @FXML TextArea mAddress1TextArea, mAddress2TextArea;

    /**
     * Note: used for FORM_BARANGAY_ID
     *
     * The image view used to hold the signature of the resident for the
     * barangay ID.
     */
    @FXML ImageView mSignatureView;

    /**
     * Note: used for FORM_BARANGAY_ID
     *
     * Buttons to call the photoshop control to either upload or capture a
     * signature image for the barangay ID.
     *
     * @see PhotoshopControl
     */
    @FXML Button mSignatureUploadButton, mSignatureCaptureButton;

    /**
     * Note: used for FORM_BARANGAY_CLEARANCE
     *
     * A node containing the child nodes for the barangay ID form.
     */
    @FXML Pane mBrgyClearancePane;

    /**
     * Note: used for FORM_BARANGAY_CLEARANCE
     *
     * Radio buttons to choose what resident address will be used for the
     * barangay clearance.
     */
    @FXML RadioButton mBrgyClearanceAddress1RadioButton, mBrgyClearanceAddress2RadioButton;

    /**
     * Note: used for FORM_BARANGAY_CLEARANCE
     *
     * Text area for the resident addresses for the barangay clearance.
     */
    @FXML TextArea mBrgyClearanceAddress1Text, mBrgyClearanceAddress2Text;

    /**
     * Note: used for FORM_BARANGAY_CLEARANCE
     *
     * A text field to input the barangay clearance purpose.
     */
    @FXML TextField mBrgyClearancePurpose;

    /**
     * Note: used for FORM_BARANGAY_CLEARANCE
     *
     * Shows an error if the purpose text field is null or empty.
     */
    @FXML Label mPurposeError;

    /**
     * A reference to the universal cache model. Used for getting the Residents cached
     * data to display the residents in the list paging.
     */
    private CacheModel mCacheModel;

    /**
     * A reference to the universe database model. Used to query the form of the
     * resident selected.
     */
    private DatabaseModel mDatabaseModel;

    /**
     * Determines the type of form to be displayed, which can either be
     * FORM_BARANGAY_ID or FORM_BARANGAY_CLEARANCE.
     */
    private byte mFormType;

    /**
     * The labels in the resident grid pane.
     * Takes hold of the resident with respect to the mCurrentPage.
     */
    private Label[] mResidentLabels;

    /* Represents the current page of the resident list paging. */
    private int mCurrentPage;

    /* Represents the number of pages within the resident list paging. */
    private int mPageCount;

    /**
     * Represents the total number of  filtered residents within the resident
     * list paging.
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
     * A volatile copy of the mResidentIDsCache used to display the residents in the
     * list paging. This list can be filtered with the search field. Thus, making it
     * volatile.
     */
    private List<String> mResidentIDs;

    /* A reference to the resident names cache. */
    private List<String> mResidentNames;

    /**
     * Note: used for FORM_BARANGAY_ID
     * If this variable is not equal to null, then a signature image is to be
     * permanently stored and passed to the mBarangayID.
     */
    private WritableImage mSignatureImage;

    /* A listener for this controller. */
    private OnResidentInfoFormListener mListener;

    /* The currently resident selected. */
    private Resident mResidentSelected;

    /**
     * Note: used for FORM_BARANGAY_ID
     *
     * Holds the barangay ID of the selected resident.
     */
    private BarangayID mBarangayID;

    /**
     * Note: used for FORM_BARANGAY_CLEARANCE
     *
     * Holds the barangay clearance of the selected resident.
     */
    private BarangayClearance mBarangayClearance;

    /**
     * Initialize the components of the scene.
     */
    @FXML
    private void initialize() {
        BarangayUtils.addTextLimitListener(mSearchField, 90);

        mResidentLabels = new Label[10];

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
        ToggleGroup brgyIDToggleGroup = new ToggleGroup();
        mAddress1RadioButton.setToggleGroup(brgyIDToggleGroup);
        mAddress2RadioButton.setToggleGroup(brgyIDToggleGroup);

        ToggleGroup brgyClearanceToggleGroup = new ToggleGroup();
        mBrgyClearanceAddress1RadioButton.setToggleGroup(brgyClearanceToggleGroup);
        mBrgyClearanceAddress2RadioButton.setToggleGroup(brgyClearanceToggleGroup);

        BarangayUtils.addTextLimitListener(mBrgyClearancePurpose, 100);

        // Reset the scene if the root pane is turned visible again.
        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mSignatureImage = null;

                mSearchField.setText(null);

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

                mBrgyClearanceAddress1RadioButton.setSelected(true);
                mBrgyClearanceAddress1RadioButton.setDisable(true);
                mBrgyClearanceAddress2RadioButton.setDisable(true);

                mBrgyClearanceAddress1Text.setText(null);
                mBrgyClearanceAddress2Text.setText(null);

                mBrgyClearancePurpose.setText(null);
                mBrgyClearancePurpose.setDisable(true);

                // Update the volatile cache to make sure that they have the updated cache.
                mResidentIDs = mCacheModel.getResidentIDsCache();

                // Reset the list paging.
                updateListPaging(false);
            }
        });
    }

    /**
     * Update the resident list paging to display the residents that has a match with
     * the text in the search field. A blank search field will result to displaying all
     * the residents.
     *
     * @param event
     */
    @FXML
    public void onSearchButtonClicked(Event event) {
        String keywords = mSearchField.getText();

        if (keywords.trim().equals(""))
            mResidentIDs = mCacheModel.getResidentIDsCache();
        else {
            String[] keywordsArray = keywords.split(" ");

            mResidentIDs = BarangayUtils.getFilteredIDs(
                    mCacheModel.getResidentIDsCache(), mResidentNames, keywordsArray);
        }

        updateListPaging(false);
        mCurrentPageLabel.requestFocus();
    }

    /**
     * If the Enter key is pressed within the search field, then automatically click
     * the search button.
     *
     * @param event
     *        The callback event. Not used.
     */
    @FXML
    public void onSearchFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            onSearchButtonClicked(null);
    }

    /**
     * Move the resident list paging to the previous page when possible.
     *
     * @param actionEvent
     *        The callback event. Note used.
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
     *
     * @param actionEvent
     *        The callback event. Not used.
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
     * Note: used for FORM_BARANGAY_ID
     *
     * Call the photoshop control to upload a signature image for the barangay ID.
     *
     * @param actionEvent
     *        The callback event. Not used.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onUploadButtonClicked();
    }

    /**
     * Note: used for Barangay_ID_FORM
     *
     * Call the photoshop control to capture a signature image for the barangay ID.
     *
     * @param actionEvent
     *        The callback event. Not used.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onCaptureButtonClicked();
    }

    /**
     * Close the form.
     *
     * @param actionEvent
     *        The callback event. Not used.
     */
    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
    }

    /**
     * Pass the gathered data to the correspoding report control, then close this form.
     *
     * @param actionEvent
     *        The action event. Never used.
     *
     * @see BarangayClearanceReportControl
     * @see BarangayIDReportControl
     */
    @FXML void onCreateButtonClicked(ActionEvent actionEvent) {
        switch (mFormType) {
            case FORM_BARANGAY_ID:
                // Generate a temporary unique id for the barangay id.
                mBarangayID.setID(mDatabaseModel.generateID(DatabaseContract.BarangayIdEntry.TABLE_NAME));

                mBarangayID.setResidentID(mResidentSelected.getId());

                mBarangayID.setResidentName(BarangayUtils.formatName(
                        mResidentSelected.getFirstName(),
                        mResidentSelected.getMiddleName(),
                        mResidentSelected.getLastName(),
                        mResidentSelected.getAuxiliary())
                );

                mBarangayID.setAddress(mAddress1RadioButton.isSelected() ?
                        mResidentSelected.getAddress1() : mResidentSelected.getAddress2());

                mBarangayID.setPhoto(mResidentSelected.getPhotoPath());

                // Store the uploaded or captured signature (if any) permanently in
                // Barangay131/Signatures/ and return the path.
                if (mSignatureImage != null) {
                    try {
                        // Save the photo in the approriate directory with a unique uuid name.
                        String targetImage = Main.SIGNATURE_DIR_PATH + "/" + UUID.randomUUID() + ".png";

                        File file = new File(targetImage);
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(mSignatureImage, null);
                        ImageIO.write(
                                renderedImage,
                                "png",
                                file);

                        // Store the path of the signature to the barangay ID.
                        mBarangayID.setResidentSignature(targetImage);
                        mBarangayID.setResidentSignatureDimension(null);

                        mSignatureImage = null;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Pass the generated barangay ID to the Main Control in order to be processed into a report.
                mListener.onCreateButtonClicked(mBarangayID, FORM_BARANGAY_ID);
                break;

            case FORM_BARANGAY_CLEARANCE:

                if (mBrgyClearancePurpose.getText() == null || mBrgyClearancePurpose.getText().trim().isEmpty()) {
                    mPurposeError.setVisible(true);
                    mBrgyClearancePurpose.setStyle(CSSContract.STYLE_TEXTAREA_ERROR);
                    return;
                } else {
                    mPurposeError.setVisible(false);
                    mBrgyClearancePurpose.setStyle(CSSContract.STYLE_TEXTAREA_NO_ERROR);
                }

                // Generate a temporary unique id for the barangay clearance.
                mBarangayClearance.setID(mDatabaseModel.generateID(DatabaseContract.BarangayClearanceEntry.TABLE_NAME));

                mBarangayClearance.setResidentID(mResidentSelected.getId());

                mBarangayClearance.setResidentName(BarangayUtils.formatName(
                        mResidentSelected.getFirstName(),
                        mResidentSelected.getMiddleName(),
                        mResidentSelected.getLastName(),
                        mResidentSelected.getAuxiliary())
                );

                mBarangayClearance.setAddress(mBrgyClearanceAddress1RadioButton.isSelected() ?
                        mResidentSelected.getAddress1() : mResidentSelected.getAddress2());

                mBarangayClearance.setYearOfResidency(mResidentSelected.getYearOfResidency());

                // Compute the total years of residency of the resident.
                Calendar birthdate = Calendar.getInstance();
                birthdate.setTime(mResidentSelected.getBirthDate());

                Calendar currentDate = Calendar.getInstance();

                int totalYears = currentDate.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);

                // If year of residency is equal to -1, then that means that the resident is a resident since birth.
                if (mResidentSelected.getYearOfResidency() == -1)
                    // Date Subtraction Algorithm.
                    totalYears -= currentDate.get(Calendar.YEAR) > birthdate.get(Calendar.YEAR) &&
                                    currentDate.get(Calendar.MONTH) == birthdate.get(Calendar.MONTH) &&
                                    currentDate.get(Calendar.DAY_OF_MONTH) < birthdate.get(Calendar.DAY_OF_MONTH) ||
                                    currentDate.get(Calendar.MONTH) < birthdate.get(Calendar.MONTH) ?
                                    1 : 0;

                else
                    totalYears -= currentDate.get(Calendar.YEAR) > birthdate.get(Calendar.YEAR) &&
                            currentDate.get(Calendar.MONTH) < birthdate.get(Calendar.MONTH) ? 1 : 0;

                mBarangayClearance.setTotalYearsResidency(totalYears);

                mBarangayClearance.setPurpose(mBrgyClearancePurpose.getText().trim());

                // Pass the generated barangay clearance to the Main Control in order to be processed into a report.
                mListener.onCreateButtonClicked(mBarangayClearance, FORM_BARANGAY_CLEARANCE);
                break;
        }
    }

    /**
     * Updates the list paging, mResidentCount and mPageCount.
     *
     * @param stayOnPage
     *        Determines whether current page should be maintained or not after the update.
     */
    private void updateListPaging(boolean stayOnPage) {
        mResidentCount = mResidentIDs.size();
        mPageCount = (int) Math.ceil(mResidentCount / 10.0);
        mCurrentPage = stayOnPage ? (mPageCount < mCurrentPage) ? mCurrentPage-- : mCurrentPage : 1;

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount == 0 ? "1" : mPageCount + "");

        // Disable the back page button if the current page is the first one.
        mBackPageButton.setDisable(mCurrentPage == 1 ? true : false);

        // Disable the next page button if the current page is the last one.
        mNextPageButton.setDisable(mCurrentPage >= mPageCount ? true : false);

        updateCurrentPage();
    }

    /**
     * Update current page with respect to mCurrentPage. That is, the value of
     * mCurrentPage will determine the displayed residents. In addition, moving from
     * one page to another removes the resident selected.
     */
    private void updateCurrentPage() {
        // Make sure that no resident is selected when moving from one page to another.
        setResidentSelected(-1);

        int firstIndex = (mCurrentPage - 1) * 10;
        int lastIndex = mCurrentPage * 10 > mResidentCount - 1 ? mResidentCount - 1 : mCurrentPage * 10;
        int currentIndex = firstIndex;

        for (int i = 0; i < 10; i++) {
            if (currentIndex <= lastIndex) {
                String id = mResidentIDs.get(i + firstIndex);
                int index = mCacheModel.getResidentIDsCache().indexOf(id);

                mResidentLabels[i].setText(mResidentNames.get(index));
                currentIndex++;
            } else
                mResidentLabels[i].setText("");
        }
    }

    /**
     * Update the resident selected data displayed.
     *
     * @param newLabelSelectedIndex
     *        The index of the label containing the resident to be displayed. If it is equal
     *        to -1, then the example data is displayed.
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

                // Enable the create button.
                mCreateButton.setDisable(false);


                // A resident is selected, display their date required for the form creation.
                switch (mFormType) {
                    case FORM_BARANGAY_ID:
                        // Enable the upload and capture buttons.
                        mSignatureUploadButton.setDisable(false);
                        mSignatureCaptureButton.setDisable(false);

                        // Populate the address text areas with the selected resident's address.
                        mAddress1RadioButton.setDisable(false);
                        mAddress1RadioButton.setSelected(true);
                        mAddress1TextArea.setText(mResidentSelected.getAddress1());

                        if (mResidentSelected.getAddress2() != null && !mResidentSelected.getAddress2().trim().isEmpty()) {
                            mAddress2RadioButton.setDisable(false);
                            mAddress2TextArea.setText(mResidentSelected.getAddress2());
                        } else {
                            mAddress2RadioButton.setDisable(true);
                            mAddress2TextArea.setText(null);
                        }

                        // If the resident has a previous barangay ID, then check if it has a signature and get it.
                        Object[] result = mDatabaseModel.getBarangayIDProperties(mResidentSelected.getId());

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
                        // This is to ensure that no new Image will be stored at the Application directory,
                        // since we already have a reference to the same image.
                        mSignatureImage = null;
                        break;

                    case FORM_BARANGAY_CLEARANCE:

                        mBrgyClearanceAddress1RadioButton.setDisable(false);
                        mBrgyClearanceAddress1RadioButton.setSelected(true);
                        mBrgyClearanceAddress1Text.setText(mResidentSelected.getAddress1());

                        if (mResidentSelected.getAddress2() != null && !mResidentSelected.getAddress2().trim().isEmpty()) {
                            mBrgyClearanceAddress2RadioButton.setDisable(false);
                            mBrgyClearanceAddress2Text.setText(mResidentSelected.getAddress2());
                        } else {
                            mBrgyClearanceAddress2RadioButton.setDisable(true);
                            mBrgyClearanceAddress2Text.setText(null);
                        }

                        mBrgyClearancePurpose.setDisable(false);

                        // Check if the resident has a previous barangay clearance. If there is, then get its purpose
                        // and display it.
                        String purpose = mDatabaseModel.getBarangayClearanceProperties(mResidentSelected.getId());
                        mBrgyClearancePurpose.setText(purpose);
                        break;
                }

            } else {
                mCreateButton.setDisable(true);
                // No resident is selected, so update the UI.
                switch (mFormType) {
                    case FORM_BARANGAY_ID:
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

                        // mSignatureImage is set to null every time a new resident is selected or unselected.
                        mSignatureImage = null;
                        break;

                    case FORM_BARANGAY_CLEARANCE:
                        // Disable the address buttons.
                        mBrgyClearanceAddress1RadioButton.setSelected(true);
                        mBrgyClearanceAddress1RadioButton.setDisable(true);
                        mBrgyClearanceAddress2RadioButton.setDisable(true);
                        mBrgyClearancePurpose.setDisable(true);

                        // Clear the text input controls.
                        mBrgyClearanceAddress1Text.setText(null);
                        mBrgyClearanceAddress2Text.setText(null);
                        mBrgyClearancePurpose.setText(null);

                        break;

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
     * Note: used for FORM_BARANGAY_ID
     *
     * A function to pass the generated signature from the PhotoshopControl for the
     * barangay ID.
     *
     * @param image
     *        The image returned by the photoshop control.
     *
     * @see PhotoshopControl
     */
    public void setSignature(WritableImage image) {
        mSignatureImage = image;
        mSignatureView.setImage(image);
    }

    /**
     * Set the listener for this controller.
     *
     * @param listener
     *        The listener for this controller.
     */
    public void setListener(OnResidentInfoFormListener listener) {
        mListener = listener;
    }

    /**
     * Note: used for FORM_BARANGAY_ID
     *
     * Disable this form if the photoshop is currently open.
     *
     * @param bool
     *        Determines whether to disable or enabled the form.
     */
    public void setDisable(boolean bool) {
        mRootPane.setDisable(bool);
    }

    /**
     * Update the view of the form, depending on the requested form.
     *
     * @param form
     *        The type of form to create.
     */
    public void setFormType(byte form) {
        mFormType = form;

        switch (mFormType) {
            case FORM_BARANGAY_ID:
                mBarangayIDPane.toFront();
                mActionLabel.setText("Barangay ID Form");
                mBarangayID = new BarangayID();
                break;
            case FORM_BARANGAY_CLEARANCE:
                mBrgyClearancePane.toFront();
                mActionLabel.setText("Barangay Clearance Form");
                mBarangayClearance = new BarangayClearance();
                break;
        }
    }

    /**
     * Set the universal cache model to this controller. Used to display the residents
     * in the list paging.
     *
     * @param cacheModel
     *        The universal cache model.
     */
    public void setCacheModel(CacheModel cacheModel) {
        mCacheModel = cacheModel;
        mResidentNames = mCacheModel.getResidentNamesCache();
    }

    /**
     * Set the universal database model to this controller. Used to load the information
     * of the currently selected resident.
     *
     * @param databaseModel
     *        The universal database model.
     */
    public void setDatabaseModel(DatabaseModel databaseModel) {
        mDatabaseModel = databaseModel;
    }
}
