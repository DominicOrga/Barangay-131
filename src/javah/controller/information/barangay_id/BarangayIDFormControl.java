package javah.controller.information.barangay_id;

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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 *  This class will handle the barangay ID form.
 */
public class BarangayIDFormControl {

    public interface OnBarangayIDFormListener {
        void onUploadButtonClicked();
        void onCaptureButtonClicked();
        void onCancelButtonClicked();
        void onCreateButtonClicked(BarangayID barangayID);
    }

    /**
     * The grid pane containing the list of residents.
     */
    @FXML Pane mRootPane;
    @FXML GridPane mResidentGridPane;
    @FXML TextField mSearchField;
    @FXML Label mCurrentPageLabel, mPageCountLabel;

    @FXML RadioButton mAddress1RadioButton, mAddress2RadioButton;
    @FXML TextArea mAddress1TextArea, mAddress2TextArea;

    @FXML Button mSignatureUploadButton, mSignatureCaptureButton;
    @FXML ImageView mSignatureView;

    @FXML Button mCreateButton;

    @FXML Button mBackPageButton, mNextPageButton;

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

    private WritableImage mSignatureImage;

    private OnBarangayIDFormListener mListener;

    private Resident mResidentSelected;

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
     * @param event
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
     * @param event
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

    @FXML
    public void onUploadButtonClicked(ActionEvent actionEvent) {
        mListener.onUploadButtonClicked();
    }

    @FXML
    public void onCaptureButtonClicked(ActionEvent actionEvent) {
        mListener.onCaptureButtonClicked();
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
    }

    @FXML void onCreateButtonClicked(ActionEvent actionEvent) {
        BarangayID barangayID = new BarangayID();

        barangayID.setResidentID(mResidentSelected.getId());
        barangayID.setResidentName(String.format("%s %s. %s",
                mResidentSelected.getFirstName(),
                mResidentSelected.getMiddleName().charAt(0),
                mResidentSelected.getLastName()));

        barangayID.setAddress(mAddress1RadioButton.isSelected() ?
                mResidentSelected.getAddress1() : mResidentSelected.getAddress2());

        barangayID.setPhoto(mResidentSelected.getPhotoPath());

        // Store the image permanently in Barangay131/Photos and return the path.
        if (mSignatureImage != null) {
            try {
                // Save the photo in the approriate directory with a unique uuid name.
                String imagePath = System.getenv("PUBLIC") + "/Barangay131/Photos/" + UUID.randomUUID() + ".png";

                File file = new File(imagePath);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(mSignatureImage, null);
                ImageIO.write(
                        renderedImage,
                        "png",
                        file);

                // Store the path of the signature to the barangay ID.
                barangayID.setResidentSignature(imagePath);

                // Store the path of the signature to the resident selected.
                mDatabaseModel.updateResidentSignature(mResidentSelected.getId(), imagePath);

                mSignatureImage = null;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PreferenceModel prefModel = new PreferenceModel();

        barangayID.setChmName(String.format("%s %s. %s",
                prefModel.get(PreferenceContract.CHAIRMAN_FIRST_NAME),
                prefModel.get(PreferenceContract.CHAIRMAN_MIDDLE_NAME).charAt(0),
                prefModel.get(PreferenceContract.CHAIRMAN_LAST_NAME)));

        barangayID.setChmSignature(prefModel.get(PreferenceContract.CHAIRMAN_SIGNATURE_PATH));

        // The date issued will be the current date.
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        barangayID.setDateIssued(new Date(calendar.getTime().getTime()));

        // Set the date validity of the barangay ID.
        // Date validity is equal to (date of creation) + (1 year)||(365 days) - (1 day).
        calendar.add(Calendar.DATE, 364);

        // Add one more day to the calendar if it is a leap year.
        if (calendar.isLeapYear(calendar.get(Calendar.YEAR)))
            calendar.add(Calendar.DATE, 1);

        barangayID.setDateValid(new Date(calendar.getTime().getTime()));

        // Pass the generated barangay ID to the Main Control in order to be processed into a report.
        mListener.onCreateButtonClicked(barangayID);
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
                // Enable the upload and capture buttons.
                mSignatureUploadButton.setDisable(false);
                mSignatureCaptureButton.setDisable(false);

                // Enable the create button.
                mCreateButton.setDisable(false);

                mResidentSelected = mDatabaseModel.getResident(mResidentIDs.get(mResidentSelectedIndex));

                mAddress1RadioButton.setDisable(false);
                mAddress1TextArea.setText(mResidentSelected.getAddress1());

                if (!mResidentSelected.getAddress2().isEmpty()) {
                    mAddress2RadioButton.setDisable(false);
                    mAddress2TextArea.setText(mResidentSelected.getAddress2());
                } else {
                    mAddress2RadioButton.setDisable(true);
                    mAddress2TextArea.setText(null);
                }

                mSignatureView.setImage(mResidentSelected.getSignature() != null ?
                        new Image("file:" + mResidentSelected.getSignature()) : null);

            } else {
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
     * A function to pass the generated signature from the PhotoshopControl.
     * @param image
     */
    public void setSignature(WritableImage image) {
        mSignatureImage = image;
        mSignatureView.setImage(image);
    }

    public void setListener(OnBarangayIDFormListener listener) {
        mListener = listener;
    }

    public void setDisable(boolean bool) {
        mRootPane.setDisable(bool);
    }


}
