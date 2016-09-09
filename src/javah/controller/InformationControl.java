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
import javah.container.BusinessClearance;
import javah.contract.CSSContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.util.BarangayUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class that manages the barangay ID, barangay clearance and business clearance.
 * Contains features such as report search, report sorting via date, creating and
 * printing reports.
 */
public class InformationControl {

    /**
     * An interface that tells the main control to open up a specified pop-up dialog.
     */
    public interface OnInformationControlListener {
        /**
         * Either launch the Resident Information Form or the Business Form dialogs
         * to create a report depending on the specified information.
         *
         * @param information
         *        Determines the type of form to show. Allowed values are:
         *        INFORMATION_BARANGAY_ID = 1
         *        INFORMATION_BARANGAY_CLEARANCE = 2
         *        INFORMATION_BUSINESS_CLEARANCE = 3
         *
         * @see ResidentInformationFormControl
         * @see BusinessFormControl
         */
        void onCreateReportButtonClicked(byte information);

        /**
         * Show the specified report for viewing and printing purposes.
         *
         * @param information
         *        Determines the type of report to show. Allowed values are:
         *        INFORMATION_BARANGAY_ID = 1
         *        INFORMATION_BARANGAY_CLEARANCE = 2
         *        INFORMATION_BUSINESS_CLEARANCE = 3
         * @param reportData
         *        The data of a specified report, used for populating the specified report pop-up.
         *
         * @see BarangayIDReportControl
         * @see BarangayClearanceReportControl
         * @see BusinessClearanceReportControl
         */
        void onViewButtonClicked(byte information, Object reportData);

        /**
         * todo: Update the method to cater for all report snap shot.
         *
         * @param barangayClearance
         * @return
         */
        Image OnRequestBarangayClearanceSnapshot(BarangayClearance barangayClearance);
    }

    /**
     * A contant representation of all type of information to distinguish what
     * information to handle. Used by the main control to determine what type of
     * information will be displayed by this controller. Furthermore, it is used
     * by this' listener to process callback events correctly.
     */
    public static final byte
            INFORMATION_BARANGAY_ID = 1,
            INFORMATION_BARANGAY_CLEARANCE = 2,
            INFORMATION_BUSINESS_CLEARANCE = 3;

    /**
     * A grid pane used to display the reports in a form of list paging. Total number
     * of reports to be displayed per page is 40.
     */
    @FXML private GridPane mListGridPane;

    /* Holds the current page label of the list paging. */
    @FXML private Label mCurrentPageLabel;

    /* Holds the total number of pages of the list paging. */
    @FXML private Label mPageCountLabel;

    /* A text field used for filtering the list paging. */
    @FXML private TextField mSearchField;

    /* A button to create a report based on the mInformation. */
    @FXML private Button mCreateButton;

    /* Buttons for navigating the list page. */
    @FXML private Button mBackPageButton, mNextPageButton;

    /* A pane to be displayed when no report is selected. */
    @FXML private Pane mNoReportSelectedPane;

    /* A button to view the report. */
    @FXML private Button mViewButton;

    /* Components of the barangay ID details. */
    @FXML Pane mBrgyIDDetailsPane;
    @FXML ImageView mIDImageView, mIDResSignatureView;
    @FXML Label mBarangayIDCode, mIDNameLabel;
    @FXML Label mIDDateIssued, mIDDateValid;

    /* FXML components of the barangay clearance details. */
    @FXML Pane mBrgyClearanceDetailsPane;
    @FXML ImageView mBrgyClearanceImage;

    /* The current type of information to be displayed. */
    private byte mInformation;

    /**
     * A reference to the universal database model. Used for creating or updating
     * reports.
     */
    private DatabaseModel mDatabaseModel;

    /**
     * A reference to the cache model. Used for getting the cached data to be
     * displayed in the list paging.
     */
    private CacheModel mCacheModel;

    /**
     * A volatile list which contains the all the report IDs available to be
     * displayed in the list paging.
     */
    private List<String> mReportIDs;

    /**
     * A volatile list which contains foreign IDs of all the reports with regards
     * to mInformation.
     */
    private List<String> mReportForeignIDs;

    /**
     * A volatile list which contains the names of all the reports with regards to
     * mInformation.
     */
    private List<String> mReportNames;

    /**
     * A volatile list which contains date Issuance of all the reports with regards
     * to mInformation.
     */
    private List<Timestamp> mReportDateIssuedList;

    /**
     * The value representing which label is selected from the list paging.
     * Value range is between 0 - 39. Value of -1 means no Label is selected.
     */
    private int mLabelSelectedIndex = -1;

    /* The array containing all the labels of the list paging. */
    private Label[] mGridLabels;

    /**
     * Holds the report ID assigned to a label.
     * Index is between 0 - 39, representing the label positions.
     */
    private String[] mReportIDToLabelLocation;

    /* Represents to current page of the list paging. */
    private int mCurrentPage;

    /* Represents the number of pages within the list paging. */
    private int mPageCount;

    /**
     * Represents the total number of labels to be used. Note, this does not reflect
     * the total number of labels, but rather, it is the totality of the label that are
     * currently used in a specified list page.
     */
    private int mLabelUseCount;

    /**
     * A listener to this controller for launching information forms and taking
     * snapshots of reports to be displayed in the details pane.
     *
     * @see OnInformationControlListener
     */
    private OnInformationControlListener mListener;

    /**
     * The current barangay ID selected. Only usable when mInformation is set to
     * Barangay ID.
     */
    private BarangayID mBarangayIDSelected;

    /**
     * The current barangay clearance selected. Only usable when mInformation is set to
     * Barangay Clearance.
     */
    private BarangayClearance mBrgyClearanceSelected;

    /**
     * The current barangay clearance selected. Only usable when mInformation is set to
     * Business Clearance.
     */
    private BusinessClearance mBusiClearanceSelected;

    /**
     * Initialize the grid labels and assign a click listener to each labels.
     * Called before setCacheModel().
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

        mBrgyIDDetailsPane.setVisible(false);
        mBrgyClearanceDetailsPane.setVisible(false);
    }

    /**
     * Update the resident list paging to display the residents that has a match with
     * the text in the search field. A blank search field will result to displaying
     * all the residents.
     *
     * @param event
     *        The click event. Note used.
     */
    @FXML
    public void onSearchButtonClicked(Event event) {
        String keywords = mSearchField.getText().trim();

        if (keywords.isEmpty()) {
            switch (mInformation) {
                case INFORMATION_BARANGAY_ID:
                    mReportIDs = mCacheModel.getBrgyIDIDsCache();
                    break;
                case INFORMATION_BARANGAY_CLEARANCE:
                    break;
                case INFORMATION_BUSINESS_CLEARANCE:
            }
        } else {
            switch (mInformation) {
                case INFORMATION_BARANGAY_ID:
                    mReportIDs = BarangayUtils.getFilteredIDs(
                            mCacheModel.getBrgyIDIDsCache(),
                            mCacheModel.getBrgyIDResidentNamesCache(),
                            keywords.split(" "));
                    break;
                case INFORMATION_BARANGAY_CLEARANCE:
                    break;
                case INFORMATION_BUSINESS_CLEARANCE:
            }
        }

        updateListPaging(false);
    }

    /**
     * If the Enter key is pressed within the search field, then automatically click
     * the search button.
     * @param event
     *        The key event. Note used.
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
     *
     * @param event
     *        The click event. Note used.
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
     *
     * @param event
     *        The click event. Note used.
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
     * Tell the main control to show the appropriate form dialog of the current
     * information displayed.
     *
     * @param actionEvent
     *        The click event. Note used.
     */
    @FXML
    public void onCreateReportButtonClicked(ActionEvent actionEvent) {
        mListener.onCreateReportButtonClicked(mInformation);
    }

    /**
     * Tell the main control to show the appropriate report dialog of the current
     * information displayed, populated by the currently selected report data with
     * regards to the current information.
     *
     * @param actionEvent
     *        The click event. Note used.
     */
    @FXML
    public void onViewButtonClicked(ActionEvent actionEvent) {
        switch (mInformation) {
            case INFORMATION_BARANGAY_ID : mListener.onViewButtonClicked(mInformation, mBarangayIDSelected); break;
            case INFORMATION_BARANGAY_CLEARANCE : mListener.onViewButtonClicked(mInformation, mBrgyClearanceSelected); break;
        }
    }

    /**
     * Update the report displayed with the currently selected report with regards to
     * mInformation.
     *
     * @param newLabelSelectedIndex
     *        The index of the label containing the report to be displayed. If it is
     *        equal to -1, then the mNoReportSelectedPane is displayed.
     */
    private void setReportToLabelSelectedIndex(int newLabelSelectedIndex) {

        /**
         * Either show a report details pane or the mNoReportSelectedPane.
         */
        Consumer<Boolean> showSelectedReportDetails = (show) -> {
            mViewButton.setVisible(show);

            if (show) {
                mNoReportSelectedPane.setVisible(false);

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

                    case INFORMATION_BARANGAY_CLEARANCE:
                        mBrgyClearanceSelected = mDatabaseModel.getBarangayClearance(mReportIDToLabelLocation[newLabelSelectedIndex]);
                        System.out.println(mBrgyClearanceSelected.getID());
                        Image image = mListener.OnRequestBarangayClearanceSnapshot(mBrgyClearanceSelected);
                        mBrgyClearanceImage.setImage(image);
                        break;
                }

            } else {
                mNoReportSelectedPane.setVisible(true);
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
     * Updates the list paging, mLabelUseCount and mPageCount.
     *
     * @param stayOnPage
     *        Determines whether current page should be maintained or not after the update.
     *        If the current page is no longer available, then move back, if possible.
     */
    private void updateListPaging(boolean stayOnPage) {
        mLabelUseCount = 0;
        int size = mReportIDs.size();

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
     * Update current page with respect to mCurrentPage. That is, the value of
     * mCurrentPage will determine the displayed report.
     * Moving from one page to another removes the resident selected.
     */
    private void updateCurrentPage() {
        // Make sure that no resident is selected when moving from one page to another.
        setReportToLabelSelectedIndex(-1);
        int reportIDIndex = 0;
        int precedingMonth = -1;
        int labelPosition = 0;
        int page = 1;
        Calendar calendar = Calendar.getInstance();

        // Find the index of the barangayID to be first displayed to the list paging current page.
        while (page < mCurrentPage) {
            calendar.setTime(mReportDateIssuedList.get(reportIDIndex));
            int month = calendar.get(Calendar.MONTH);

            if (precedingMonth != month) {
                precedingMonth = month;
                labelPosition += (labelPosition % 2 == 0) ? 2 : 3;
            }

            labelPosition++;
            reportIDIndex++;

            if (labelPosition > 40) {
                // Move to the next Page.
                reportIDIndex--;
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
        while (labelPosition <= 40 && reportIDIndex < mReportIDs.size()) {
            calendar.setTime(mReportDateIssuedList.get(reportIDIndex));
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
            mReportIDToLabelLocation[labelIndex] = mReportIDs.get(reportIDIndex);

            // Get the name of the resident applicant of the barangay ID.
            switch (mInformation) {
                case INFORMATION_BARANGAY_ID:
                    String id = mReportIDs.get(reportIDIndex);
                    int index = mCacheModel.getBrgyIDIDsCache().indexOf(id);
                    currentLabel.setText(mCacheModel.getBrgyIDResidentNamesCache().get(index));
                    break;
                case INFORMATION_BARANGAY_CLEARANCE: break;
                case INFORMATION_BUSINESS_CLEARANCE:
            }
            int index = mCacheModel.getResidentIDsCache().indexOf(mReportForeignIDs.get(reportIDIndex));
            currentLabel.setText(mCacheModel.getResidentNamesCache().get(index));

            labelPosition++;
            reportIDIndex++;
        }
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
//        mResidentIDs = mCacheModel.getResidentIDsCache();
//        mResidentNames = mCacheModel.getResidentNamesCache();

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
        }

        // Update the scene to match the information.
        switch (information) {
            case INFORMATION_BARANGAY_ID :
                mCreateButton.setText("New Barangay ID");
                mBrgyIDDetailsPane.setVisible(true);

                // The volatile cache should hold the cached data pertaining to the barangay id.
                mReportIDs = mCacheModel.getBrgyIDIDsCache();
                mReportForeignIDs = mCacheModel.getBrgyIDResidentIDsCache();
                mReportNames = mCacheModel.getBrgyIDResidentNamesCache();
                mReportDateIssuedList = mCacheModel.getBrgyIDDateIssuedCache();

                break;
            case INFORMATION_BARANGAY_CLEARANCE :
                mCreateButton.setText("New Barangay Clearance");
                mBrgyClearanceDetailsPane.setVisible(true);

                // The volatile cache should hold the cached data pertaining to the barangay clearance.
                mReportIDs = mCacheModel.getBrgyClearanceIDsCache();
                mReportForeignIDs = mCacheModel.getBrgyClearanceResidentIDsCache();
                mReportNames = mCacheModel.getBrgyIDResidentNamesCache();
                mReportDateIssuedList = mCacheModel.getBrgyClearanceDateIssuedCache();

                break;
            case INFORMATION_BUSINESS_CLEARANCE :
                break;
        }

        mInformation = information;

        // Refresh the list paging.
        updateListPaging(false);

    }

    public void createBarangayID(BarangayID barangayID) {
        // Create the barangay id.
        mDatabaseModel.createBarangayID(barangayID);

        // Before adding, make sure that the cache reference are pointing to the non-volatile cached data.
        mCacheModel.cacheBarangayID(barangayID);
        mReportIDs = mCacheModel.getBrgyIDIDsCache();

        // Update the list paging and select the newly created barangay id.
        updateListPaging(false);
        setReportToLabelSelectedIndex(2);
    }

    public void createBarangayClearance(BarangayClearance barangayClearance) {
        mDatabaseModel.createBarangayClearance(barangayClearance);

        // todo: before adding, make sure that the cache reference are pointing to the non-volatile cached data.
        // Place the new barangay id in the cached data.
        mCacheModel.cacheBarangayClearance(barangayClearance);
        mReportIDs = mCacheModel.getBrgyClearanceIDsCache();

        // Update the list paging and select the newly created barangay id.
        updateListPaging(false);
        setReportToLabelSelectedIndex(2);
    }
}
