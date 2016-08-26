package javah.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javah.container.BarangayClearance;
import javah.container.BarangayID;
import javah.contract.CSSContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.util.BarangayUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

public class InformationControl {

    /**
     * An interface that tells the main scene to open up a manipulation resident dialog.
     */
    public interface OnInformationControlListener {
        void onCreateReportButtonClicked(byte information);
        void onViewButtonClicked(byte information, Object reportData);
        Image OnRequestBarangayClearanceSnapshot(BarangayClearance barangayClearance);
    }

    /**
     * Used for resident list paging. *Each page contains 40 residents.
     */
    @FXML private GridPane mListGridPane;

    /**
     * The current page label of the resident list paging.
     */
    @FXML private Label mCurrentPageLabel;

    /**
     * The total number of pages of the resident list paging.
     */
    @FXML private Label mPageCountLabel;

    /**
     * The search field used for specialized resident query.
     */
    @FXML private TextField mSearchField;

    @FXML private Button mCreateButton;
    @FXML private Button mBackPageButton, mNextPageButton;

    @FXML private Button mViewButton;

    /**
     * FXML components of the barangay ID details.
     */
    @FXML Pane mBrgyIDDetailsPane;
    @FXML ImageView mIDImageView, mIDResSignatureView;
    @FXML Label mBarangayIDCode, mIDNameLabel;
    @FXML Label mIDDateIssued, mIDDateValid;

    /**
     * FXML components of the barangay clearance details.
     */
    @FXML Pane mBrgyClearanceDetailsPane;
    @FXML ImageView mBrgyClearanceImage;

    public static final byte
            INFORMATION_BARANGAY_ID = 1,
            INFORMATION_BARANGAY_CLEARANCE = 2,
            INFORMATION_BUSINESS_CLEARANCE = 3,
            INFORMATION_BLOTTER = 4;

    /**
     * The current information to be displayed.
     */
    private byte mInformation;

    private DatabaseModel mDatabaseModel;

    private CacheModel mCacheModel;

    /**
     * A volatile copy of the mResidentIDsCache used to search for non-archived residents.
     */
    private List<String> mResidentIDs;

    /**
     * A volatile copy of the mResidentNamesCache used to display the residents in the list paging.
     */
    private List<String> mResidentNames;

    /**
     * Volatile copy of the cached data based on the information displayed.
     */
    private List<String> mReportIDs;
    private List<String> mReportResidentIDs;
    private List<Timestamp> mReportDateIssuedList;

    /**
     * The value representing which label is selected from the list paging.
     * Value range is between 0 - 39.
     */
    private int mLabelSelectedIndex = -1;

    /**
     * The array containing all the labels of the list paging.
     */
    private Label[] mGridLabels;

    /**
     * Holds to report ID assigned to a label.
     * Index is between 0 - 39, representing the label positions.
     */
    private String[] mReportIDToLabelLocation;

    /**
     * Represents the current page of the resident list paging.
     */
    private int mCurrentPage;

    /**
     * Represents the number of pages within the resident list paging.
     */
    private int mPageCount;

    /**
     * Represents the total number of labels to be used.
     * *Does not reflect the total number of labels, but rather, it is the totality of the label use and re-use.
     */
    private int mLabelUseCount;

    private OnInformationControlListener mListener;

    private BarangayID mBarangayIDSelected;
    private BarangayClearance mBrgyClearanceSelected;

    /**
     * Called before setCacheModel()
     */
    @FXML
    private void initialize() {
        // Initialize mGridLabels with storage for 40 labels.
        mGridLabels = new Label[40];

        mReportIDToLabelLocation = new String[40];

        // Populate mGridLabels with 40 labels and display it in a matrix of 20x2 mListGridPane.
        for (int i = 0; i < 40; i++) {
            Label label = new Label();
            label.setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(500);
            label.setPrefWidth(1000);

            mGridLabels[i] = label;
            // Add a label selected event listener to each label.
            final int labelIndex = i;
            label.setOnMouseClicked(event -> setReportToLabelSelectedIndex(labelIndex));
        }


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
    }

    /**
     * If the Enter key is pressed within the search field, then automatically click the search button.
     * @param event
     */
    @FXML
    public void onSearchFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onSearchButtonClicked(null);
            mCurrentPageLabel.requestFocus();
        }
    }

    /**
     * Move the resident list paging to the previous page when possible.
     * @param event
     */
    @FXML
    public void onBackPageButtonClicked(ActionEvent event) {
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
    public void onNextPageButtonClicked(ActionEvent event) {
        mCurrentPage += 1;
        updateCurrentPage();
        mCurrentPageLabel.setText(mCurrentPage + "");

        if (mBackPageButton.isDisable())
            mBackPageButton.setDisable(false);

        if (mCurrentPage == mPageCount)
            mNextPageButton.setDisable(true);
    }

    /**
     * Tell the main scene to show the appropriate form dialog of the current information displayed.
     * @param actionEvent
     */
    @FXML
    public void onCreateReportButtonClicked(ActionEvent actionEvent) {
        mListener.onCreateReportButtonClicked(mInformation);
    }

    @FXML
    public void onViewButtonClicked(ActionEvent actionEvent) {
        switch (mInformation) {
            case INFORMATION_BARANGAY_ID : mListener.onViewButtonClicked(mInformation, mBarangayIDSelected); break;
            case INFORMATION_BARANGAY_CLEARANCE : mListener.onViewButtonClicked(mInformation, mBrgyClearanceSelected); break;
        }
    }

    /**
     * Update the barangay ID selected data displayed.
     * @param newLabelSelectedIndex is the index of the label containing the barangay ID to be displayed. If it is equal
     *                              to -1, then the example data is displayed.
     */
    private void setReportToLabelSelectedIndex(int newLabelSelectedIndex) {
        Consumer<Boolean> showSelectedReportDetails = (show) -> {
            mViewButton.setVisible(show);

            if (show)
                switch (mInformation) {
                    case INFORMATION_BARANGAY_ID:
                        // Get the label selected.
                        mBarangayIDSelected = mDatabaseModel.getBarangayID(mReportIDToLabelLocation[newLabelSelectedIndex]);

                        mIDImageView.setImage(mBarangayIDSelected.getPhoto() != null ?
                                new Image("file:" + mBarangayIDSelected.getPhoto()) : BarangayUtils.getDefaultDisplayPhoto());

                        mBarangayIDCode.setText(mBarangayIDSelected.getID());
                        mIDNameLabel.setText(mBarangayIDSelected.getResidentName().toUpperCase());

                        if (mBarangayIDSelected.getResidentSignature() != null) {
                            mIDResSignatureView.setImage(new Image("file:" + mBarangayIDSelected.getResidentSignature()));

                            double[] dimension = mBarangayIDSelected.getResidentSignatureDimension();

                            mIDResSignatureView.setX(dimension[0]);
                            mIDResSignatureView.setY(dimension[1]);
                            mIDResSignatureView.setFitWidth(dimension[2]);
                            mIDResSignatureView.setFitHeight(dimension[3]);
                        } else
                            mIDResSignatureView.setImage(null);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd yyyy");
                        mIDDateIssued.setText(dateFormat.format(mBarangayIDSelected.getDateIssued()));
                        mIDDateValid.setText(dateFormat.format(mBarangayIDSelected.getDateValid()));

                        break;

                    case INFORMATION_BARANGAY_CLEARANCE :
                        mBrgyClearanceSelected = mDatabaseModel.getBarangayClearance(mReportIDToLabelLocation[newLabelSelectedIndex]);
                        System.out.println(mBrgyClearanceSelected.getID());
                        Image image = mListener.OnRequestBarangayClearanceSnapshot(mBrgyClearanceSelected);
                        mBrgyClearanceImage.setImage(image);
                        break;
                }

            else
                switch (mInformation) {
                    case INFORMATION_BARANGAY_ID:
                        mIDImageView.setImage(BarangayUtils.getDefaultDisplayPhoto());
                        mBarangayIDCode.setText("00-000");
                        mIDNameLabel.setText(null);
                        mIDResSignatureView.setImage(null);
                        mIDDateIssued.setText(null);
                        mIDDateValid.setText(null);
                        break;

                    case INFORMATION_BARANGAY_CLEARANCE:

                }
        };

        // If newLabelSelectedIndex is equal to -1, then clear the details.
        if (newLabelSelectedIndex == -1) {
            mLabelSelectedIndex = -1;
            showSelectedReportDetails.accept(false);
            return;
        }

        // This is where the code selection and unselection view update happens.
        // If a label is clicked without containing any resident, then ignore the event.
        if (mReportIDToLabelLocation[newLabelSelectedIndex] != null) {
            // if no previous resident is selected, then simply make the new selection.
            if (mLabelSelectedIndex == -1) {
                if (newLabelSelectedIndex != -1) {
                    mGridLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);
                    mLabelSelectedIndex = newLabelSelectedIndex;
                    showSelectedReportDetails.accept(true);
                }

            } else {
                // If there is a previous selection, unselect it.
                // Also, if the previously selected resident is selected again, then unselect it.
                if (newLabelSelectedIndex == -1 || mLabelSelectedIndex == newLabelSelectedIndex) {
                    mGridLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
                    mLabelSelectedIndex = -1;
                    showSelectedReportDetails.accept(false);

                    // Unselect the previously selcted resident, then select the currently selected resident.
                } else {
                    mGridLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
                    mGridLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);

                    mLabelSelectedIndex = newLabelSelectedIndex;
                    showSelectedReportDetails.accept(true);
                }
            }
        }
    }

    /**
     * Update current page with respect to mCurrentPage. That is, the value of mCurrentPage will determine the displayed
     * residents.
     * Moving from one page to another removes the resident selected.
     */
    private void updateCurrentPage() {
        // Make sure that no resident is selected when moving from one page to another.
        setReportToLabelSelectedIndex(-1);
        int barangayIDIndex = 0;
        int precedingMonth = -1;
        int labelPosition = 0;
        int page = 1;
        Calendar calendar = Calendar.getInstance();

        // Find the index of the barangayID to be first displayed to the list paging current page.
        while (page < mCurrentPage) {
            calendar.setTime(mReportDateIssuedList.get(barangayIDIndex));
            int month = calendar.get(Calendar.MONTH);

            if (precedingMonth != month) {
                precedingMonth = month;
                labelPosition += (labelPosition % 2 == 0) ? 2 : 3;
            }

            labelPosition++;
            barangayIDIndex++;

            if (labelPosition > 40) {
                // Move to the next Page.
                barangayIDIndex--;
                page++;
                labelPosition = 0;
                precedingMonth = -1;
            }
        }

        // Reset to default the label placements within the list paging.
        mListGridPane.getChildren().removeAll(mGridLabels);

        for (int i = 0; i < 40; i++) {
            mGridLabels[i].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
            mGridLabels[i].setText(null);
            mListGridPane.add(mGridLabels[i], i % 2 == 0 ? 0 : 1, i / 2, 1, 1);
        }

        // Clear the Label references to the barangay ID's.
        Arrays.fill(mReportIDToLabelLocation, null);

        // Determine the barangay ID placement within the labels.
        while (labelPosition <= 40 && barangayIDIndex < mReportIDs.size()) {
            calendar.setTime(mReportDateIssuedList.get(barangayIDIndex));
            int month = calendar.get(Calendar.MONTH);

            if (precedingMonth != month) {
                precedingMonth = month;

                labelPosition += (labelPosition % 2 == 0) ? 1 : 0;

                // If the date label can no longer be placed or a single report for a specific date cannot be displayed,
                // then break the operation since those reports are already for the next page.
                if (labelPosition >= 39) return;

                // Place the date month in the current label.
                int labelIndex = labelPosition - 1;
                Label currentLabel = mGridLabels[labelIndex];

                // The date month label must span 2 columns.
                mListGridPane.getChildren().remove(currentLabel);
                mListGridPane.add(currentLabel, labelIndex % 2 == 0 ? 0 : 1, labelIndex / 2, 2, 1);

                // The date month labels must have a background color of brown and text fill of white.
                currentLabel.setStyle(CSSContract.STYLE_DATE_HEADER);

                // Set the date text to the label.
                String date = BarangayUtils.convertMonthIntToString(calendar.get(Calendar.MONTH)) + " " +
                        calendar.get(Calendar.YEAR);
                currentLabel.setText(date);

                labelPosition += 2;
            }

            int labelIndex = labelPosition - 1;
            Label currentLabel = mGridLabels[labelIndex];

            // Store the barangay ID in mReportIDToLabelLocation which corresponds to the label location.
            mReportIDToLabelLocation[labelIndex] = mReportIDs.get(barangayIDIndex);

            // Get the name of the resident applicant of the barangay ID.
            int index = mResidentIDs.indexOf(mReportResidentIDs.get(barangayIDIndex));
            currentLabel.setText(mResidentNames.get(index));

            labelPosition++;
            barangayIDIndex++;
        }
    }

    /**
     * Updates the list paging, mLabelUseCount and mPageCount.
     * @param stayOnPage determines whether current page should be maintained or not after the update.
     */
    private void updateListPaging(boolean stayOnPage) {
        mLabelUseCount = 0;
        int size = mReportDateIssuedList.size();

        int precedingMonth = -1;
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < size; i++) {
            calendar.setTime(mReportDateIssuedList.get(i));
            int month = calendar.get(Calendar.MONTH);

            if (precedingMonth != month) {
                precedingMonth = month;
                mLabelUseCount += (mLabelUseCount % 2 == 0) ? 2 : 3;
            }

            mLabelUseCount++;
        }

        mPageCount = (int) Math.ceil(mLabelUseCount / 40.0);
        mCurrentPage = stayOnPage ? (mPageCount < mCurrentPage) ? mCurrentPage-- : mCurrentPage : 1;

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount + "");

        // Disable the back page button if the current page is the first one.
        mBackPageButton.setDisable(mCurrentPage == 1 ? true : false);

        // Disable the next page button if the current page is the last one.
        mNextPageButton.setDisable(mCurrentPage >= mPageCount ? true : false);

        updateCurrentPage();
    }

    /**
     * Called after initialize() and is called in the MainControl.
     * Set the main scene as the listener to this object.
     * @param listener
     */
    public void setListener(OnInformationControlListener listener) {
        mListener = listener;
    }

    /**
     * Called after initialize() and is called in the MainControl.
     * Make a reference to the global database model.
     * @param databaseModel
     */
    public void setDatabaseModel(DatabaseModel databaseModel) {
        mDatabaseModel = databaseModel;
    }

    /**
     * Called after initialize() and is called in the MainControl.
     * Make a reference to the global cache model and start initializing the variables that are initially
     * dependent on some cached data.
     * @param cacheModel
     */
    public void setCacheModel(CacheModel cacheModel) {
        mCacheModel = cacheModel;
    }

    public void setBlurListPaging(boolean blur) {
        mListGridPane.setStyle(blur ? CSSContract.STYLE_GRID_UNBORDERED : CSSContract.STYLE_GRID_BORDERED);
    }

    /**
     * Set the information to be displayed.
     * @param information
     */
    public void setInformation(byte information){
        // Reset the resident volatile cache.
        mResidentIDs = mCacheModel.getResidentIDsCache();
        mResidentNames = mCacheModel.getResidentNamesCache();

        // Hide the previous Information details pane used.
        switch (mInformation) {
            case INFORMATION_BARANGAY_ID :
                mBrgyIDDetailsPane.setVisible(false);
                break;
            case INFORMATION_BARANGAY_CLEARANCE :
                mBrgyClearanceDetailsPane.setVisible(false);
                break;
            case INFORMATION_BUSINESS_CLEARANCE :
                break;
            case INFORMATION_BLOTTER :
        }

        // Update the scene to match the information.
        switch (information) {
            case INFORMATION_BARANGAY_ID :
                mCreateButton.setText("New Barangay ID");
                mBrgyIDDetailsPane.setVisible(true);

                // The volatile cache should hold the cached data pertaining to the barangay id.
                mReportIDs = mCacheModel.getBarangayIDIDsCache();
                mReportResidentIDs = mCacheModel.getBarangayIDResidentIDCache();
                mReportDateIssuedList = mCacheModel.getBarangayIDdateIssuedCache();

                break;
            case INFORMATION_BARANGAY_CLEARANCE :
                mCreateButton.setText("New Barangay Clearance");
                mBrgyClearanceDetailsPane.setVisible(true);

                // The volatile cache should hold the cached data pertaining to the barangay clearance.
                mReportIDs = mCacheModel.getBrgyClearanceIDsCache();
                mReportResidentIDs = mCacheModel.getBrgyClearanceResidentIDsCache();
                mReportDateIssuedList = mCacheModel.getBrgyClearanceDateIssuedCache();

                break;
            case INFORMATION_BUSINESS_CLEARANCE :
                break;
            case INFORMATION_BLOTTER :
        }

        mInformation = information;

        // Refresh the list paging.
        updateListPaging(false);

    }

    public void createBarangayID(BarangayID barangayID) {
        // Create the barangay id.
        mDatabaseModel.createBarangayID(barangayID);

        // todo: before adding, make sure that the cache reference are pointing to the non-volatile cached data.
        // Place the new barangay id in the cached data.
        mReportDateIssuedList.add(0, barangayID.getDateIssued());
        mReportResidentIDs.add(0, barangayID.getResidentID());
        mReportIDs.add(0, barangayID.getID());

        // Update the list paging and select the newly created barangay id.
        updateListPaging(false);
        setReportToLabelSelectedIndex(2);
    }

    public void createBarangayClearance(BarangayClearance barangayClearance) {
        mDatabaseModel.createBarangayClearance(barangayClearance);

        // todo: before adding, make sure that the cache reference are pointing to the non-volatile cached data.
        // Place the new barangay id in the cached data.
        mReportDateIssuedList.add(0, barangayClearance.getDateIssued());
        mReportResidentIDs.add(0, barangayClearance.getResidentID());
        mReportIDs.add(0, barangayClearance.getID());

        // Update the list paging and select the newly created barangay id.
        updateListPaging(false);
        setReportToLabelSelectedIndex(2);
    }
}
