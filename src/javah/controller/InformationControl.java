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
import javafx.scene.text.Text;
import javah.container.BarangayClearance;
import javah.container.BarangayID;
import javah.container.BusinessClearance;
import javah.contract.CSSContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.util.BarangayUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
         *        FORM_BARANGAY_ID = 1
         *        FORM_BARANGAY_CLEARANCE = 2
         *        INFORMATION_BUSINESS_CLEARANCE = 3
         *
         * @see ResidentInformationFormControl
         * @see BusinessClearanceFormControl
         */
        void onCreateReportButtonClicked(byte information);

        /**
         * Show the specified report for viewing and printing purposes.
         *
         * @param information
         *        Determines the type of report to show. Allowed values are:
         *        FORM_BARANGAY_ID = 1
         *        FORM_BARANGAY_CLEARANCE = 2
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
         * @param report
         * @return
         */
        Image onRequestReportSnapshot(Object report);
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

    /* The nodes within the details pane. */
    @FXML ImageView mReportSnapshot;
    @FXML Text mDateIssued, mDateValid;

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

    /* A non-volatile list which contains the all the report IDs of mInformation. */
    private List<String> mActualReportIDs;

    /**
     * A volatile list which contains the all the report IDs available to be
     * displayed in the list paging with regards to the search filter. The IDs
     * stored in the list will determine the allowed reports to be displayed.
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

    /**
     * An array that contains the first report IDs per page. The index will
     * serve as the representation of each page report start. That is,
     * index 0 = page 1. The element will be the index of the mReportIDs,
     * signifying the first IDs to display on a specified page.
     */
    private List<Integer> mFirstReportIDIndexPerPage;

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
        BarangayUtils.addTextLimitListener(mSearchField, 90);
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
            label.setOnMouseClicked(event -> setLabelSelectedIndex(labelIndex));
        }
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

        mReportIDs = keywords == null || keywords.isEmpty() ?
                mActualReportIDs : BarangayUtils.getFilteredIDs(mActualReportIDs, mReportNames, keywords.split(" "));

        setLabelSelectedIndex(mLabelSelectedIndex);
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
            case INFORMATION_BARANGAY_ID :
                mListener.onViewButtonClicked(mInformation, mBarangayIDSelected);
                break;

            case INFORMATION_BARANGAY_CLEARANCE :
                mListener.onViewButtonClicked(mInformation, mBrgyClearanceSelected);
                break;

            case INFORMATION_BUSINESS_CLEARANCE :
                mListener.onViewButtonClicked(mInformation, mBusiClearanceSelected);
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
    private void setLabelSelectedIndex(int newLabelSelectedIndex) {

        /**
         * Either show a report details pane or the mNoReportSelectedPane.
         */
        Consumer<Boolean> showSelectedReportDetails = (show) -> {
            mViewButton.setVisible(show);

            if (show) {
                mNoReportSelectedPane.setVisible(false);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");
                Image image = null;

                switch (mInformation) {
                    case INFORMATION_BARANGAY_ID:
                        mBarangayIDSelected = mDatabaseModel.getBarangayID(mReportIDToLabelLocation[newLabelSelectedIndex]);
                        image = mListener.onRequestReportSnapshot(mBarangayIDSelected);

                        mDateIssued.setText(dateFormat.format(mBarangayIDSelected.getDateIssued()));
                        mDateValid.setText(dateFormat.format(mBarangayIDSelected.getDateValid()));

                        break;

                    case INFORMATION_BARANGAY_CLEARANCE:
                        mBrgyClearanceSelected = mDatabaseModel.getBarangayClearance(mReportIDToLabelLocation[newLabelSelectedIndex]);
                        image = mListener.onRequestReportSnapshot(mBrgyClearanceSelected);

                        mDateIssued.setText(dateFormat.format(mBrgyClearanceSelected.getDateIssued()));
                        mDateValid.setText(dateFormat.format(mBrgyClearanceSelected.getDateValid()));
                        break;

                    case INFORMATION_BUSINESS_CLEARANCE:
                        mBusiClearanceSelected = mDatabaseModel.getBusinessClearance(mReportIDToLabelLocation[newLabelSelectedIndex]);
                        image = mListener.onRequestReportSnapshot(mBusiClearanceSelected);

                        mDateIssued.setText(dateFormat.format(mBusiClearanceSelected.getDateIssued()));
                        mDateValid.setText(dateFormat.format(mBusiClearanceSelected.getDateValid()));
                }

                mReportSnapshot.setImage(image);

            } else
                mNoReportSelectedPane.setVisible(true);
        };

        // If newLabelSelectedIndex is equal to -1, then clear the details.
        if (newLabelSelectedIndex == -1) {
            mLabelSelectedIndex = -1;
            showSelectedReportDetails.accept(false);
            return;
        }

        // This is where the code selection and unselection view update happens.
        // If a label is clicked without containing any resident, then ignore the event.sc
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

        // Re-initialize the mFirstReportIDIndexPerPage and its first elements is equal to 0,
        // to signify that the first page starts with index 0.
        mFirstReportIDIndexPerPage = new ArrayList<>();
        mPageCount = 0;

        for (int i = 0; i < size; i++) {
            // Check if the label count is stepping on another page. If it is, then i index is
            // holding the index of the first ID of the other page.
            if (mLabelUseCount == 40 * mPageCount) {
                // Move to the next page.
                mFirstReportIDIndexPerPage.add(i);
                mPageCount++;
                // Make sure that the month is displayed first in every page.
                precedingMonth = -1;
            }

            if (mReportIDs != mActualReportIDs) {
                int index = mActualReportIDs.indexOf(mReportIDs.get(i));
                calendar.setTime(mReportDateIssuedList.get(index));
            } else
                calendar.setTime(mReportDateIssuedList.get(i));

            int month = calendar.get(Calendar.MONTH);

            // Check if a month label needs to be displayed.
            if (month != precedingMonth) {

                // Check if the month label with at least one report will fit in the current page.
                // If not, then move to the next page and the respective labels.
                int pageEmptyLabelCount = 40 * mPageCount - mLabelUseCount;
                if (pageEmptyLabelCount <= 3) {
                    // Consume all left labels within the current page to move to the next page.
                    mLabelUseCount += pageEmptyLabelCount;

                    if (mLabelUseCount == 40 * mPageCount) {
                        // Move to the next page.
                        mFirstReportIDIndexPerPage.add(i);
                        mPageCount++;
                    }
                }

                precedingMonth = month;
                // Consume the right amount of labels for displaying the month.
                mLabelUseCount += (mLabelUseCount % 2 == 0) ? 2 : 3;
            }

            // Consume a single label.
            mLabelUseCount++;
        }

//        System.out.println("InformationControl - Label Used Count = " + mLabelUseCount);

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
     * mCurrentPage will determine the displayed reports.
     * Moving from one page to another removes the resident selected.
     */
    private void updateCurrentPage() {
        // Make sure that no resident is selected when moving from one page to another.
        setLabelSelectedIndex(mLabelSelectedIndex);

        // Reset to default the label placements within the list paging.
        mListGridPane.getChildren().removeAll(mGridLabels);

        // If no reports to be displayed, then populate the grid pane and be done with it.
        if (mFirstReportIDIndexPerPage.size() == 0) {
            for (int i = 0; i < 40; i++) {
                mReportIDToLabelLocation[i] = null;
                Label label = mGridLabels[i];
                label.setText(null);
                mListGridPane.add(label, i % 2 == 0 ? 0 : 1, i / 2, 1, 1);
            }
            return;
        }

        int reportIndex = mFirstReportIDIndexPerPage.get(mCurrentPage - 1);

        Calendar calendar = Calendar.getInstance();
        int precedingMonth = -1;

        // Fill out all the labels of the current page.
        for (int i = 0; i < 40; i++, reportIndex++) {
            Label currentLabel;

            if (reportIndex >= mReportIDs.size()) {
                mReportIDToLabelLocation[i] = null;
                currentLabel = mGridLabels[i];
                currentLabel.setText(null);
                mListGridPane.add(currentLabel, i % 2 == 0 ? 0 : 1, i / 2, 1, 1);
            } else {
                int index = mReportIDs != mActualReportIDs ?
                        mActualReportIDs.indexOf(mReportIDs.get(reportIndex)) : reportIndex;

                calendar.setTime(mReportDateIssuedList.get(index));

                int month = calendar.get(Calendar.MONTH);

                // Check if a month label needs to be displayed.
                if (month != precedingMonth) {
                    precedingMonth = month;

                    // If the label index is at its peak that a month label together with a single
                    // record cannot be displayed, then simply fill out the remaining labels with
                    // null and break the loop.
                    if (i >= 37) {
                        for (int j = i; j < 40; j++) {
//                            mReportIDToLabelLocation[j] = null;
                            currentLabel = mGridLabels[j];
                            currentLabel.setText(null);
                            mListGridPane.add(currentLabel, j % 2 == 0 ? 0 : 1, j / 2, 1, 1);
                        }
                        break;
                    }

                    // Make sure that the month label must occupy both columns.
                    if (i % 2 != 0) {
                        mReportIDToLabelLocation[i] = null;
                        currentLabel = mGridLabels[i];
                        currentLabel.setText(null);
                        mListGridPane.add(currentLabel, i % 2 == 0 ? 0 : 1, i / 2, 1, 1);
                        i++;
                    }

                    // The date month label must span 2 columns.
                    mReportIDToLabelLocation[i] = null;
                    currentLabel = mGridLabels[i];
                    mListGridPane.add(currentLabel, i % 2 == 0 ? 0 : 1, i / 2, 2, 1);
                    i += 2;

                    // The date month labels must have a background color of brown and text fill of white.
                    currentLabel.setStyle(CSSContract.STYLE_DATE_HEADER);

                    // Set the date text to the label.
                    String date = BarangayUtils.convertMonthIntToString(calendar.get(Calendar.MONTH)) + " " +
                            calendar.get(Calendar.YEAR);
                    currentLabel.setText(date);
                }

                mReportIDToLabelLocation[i] = mActualReportIDs.get(index);
                currentLabel = mGridLabels[i];
                mListGridPane.add(currentLabel, i % 2 == 0 ? 0 : 1, i / 2, 1, 1);
                currentLabel.setText(mReportNames.get(index));
                currentLabel.setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
            }
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

        // Update the scene to match the information.
        switch (information) {
            case INFORMATION_BARANGAY_ID :
                mCreateButton.setText("New Barangay ID");

                // The volatile cache should hold the cached data pertaining to the barangay id.
                mReportIDs = mCacheModel.getBrgyIDIDsCache();
                mActualReportIDs = mCacheModel.getBrgyIDIDsCache();
                mReportForeignIDs = mCacheModel.getBrgyIDResidentIDsCache();
                mReportNames = mCacheModel.getBrgyIDResidentNamesCache();
                mReportDateIssuedList = mCacheModel.getBrgyIDDateIssuedCache();

                break;
            case INFORMATION_BARANGAY_CLEARANCE :
                mCreateButton.setText("New Barangay Clearance");

                // The volatile cache should hold the cached data pertaining to the barangay clearance.
                mReportIDs = mCacheModel.getBrgyClearanceIDsCache();
                mActualReportIDs = mCacheModel.getBrgyClearanceIDsCache();
                mReportForeignIDs = mCacheModel.getBrgyClearanceResidentIDsCache();
                mReportNames = mCacheModel.getBrgyClearanceResidentNamesCache();
                mReportDateIssuedList = mCacheModel.getBrgyClearanceDateIssuedCache();

                break;
            case INFORMATION_BUSINESS_CLEARANCE :
                mCreateButton.setText("New Business Clearance");
//                mBrgyClearanceDetailsPane.setVisible(true);

                // The volatile cache should hold the cached data pertaining to the barangay clearance.
                mReportIDs = mCacheModel.getBusiClearanceIDsCache();
                mActualReportIDs = mCacheModel.getBusiClearanceIDsCache();
                mReportForeignIDs = mCacheModel.getBusiClearanceBusiIDsCache();
                mReportNames = mCacheModel.getBusiClearanceBusiNamesCache();
                mReportDateIssuedList = mCacheModel.getBusiClearanceDateIssuedCache();
                break;
        }

        mInformation = information;

        // Refresh the list paging.
        updateListPaging(false);

    }

    /**
     * Store the specified barangay ID in the database and data cache.
     *
     * @param barangayID
     *        The barangay ID to be stored.
     */
    public void createBarangayID(BarangayID barangayID) {
        // Create the barangay id.
        mDatabaseModel.createBarangayID(barangayID);

        // Before adding, make sure that the cache reference are pointing to the non-volatile cached data.
        mCacheModel.cacheBarangayID(barangayID);

        mReportIDs = mCacheModel.getBrgyIDIDsCache();

        // Update the list paging and select the newly created barangay id.
        updateListPaging(false);
        setLabelSelectedIndex(2);
    }

    /**
     * Store the specified barangay clearance in the database and data cache.
     *
     * @param barangayClearance
     *        The barangay clearance to be stored.
     */
    public void createBarangayClearance(BarangayClearance barangayClearance) {
        mDatabaseModel.createBarangayClearance(barangayClearance);

        // Place the new barangay id in the cached data.
        mCacheModel.cacheBarangayClearance(barangayClearance);

        mReportIDs = mCacheModel.getBrgyClearanceIDsCache();

        // Update the list paging and select the newly created barangay id.
        updateListPaging(false);
        setLabelSelectedIndex(2);
    }

    /**
     * Store the specified business clearance in the database and data cache.
     *
     * @param businessClearance
     *        The business clearance to be stored.
     */
    public void createBusinessClearance(BusinessClearance businessClearance) {
        mDatabaseModel.createBusinessClearance(businessClearance);

        // Place the new barangay id in the cached data.
        mCacheModel.cacheBusinessClearance(businessClearance);

        mReportIDs = mCacheModel.getBusiClearanceIDsCache();

        // Update the list paging and select the newly created barangay id.
        updateListPaging(false);
        setLabelSelectedIndex(2);
    }

    /**
     * Update the list paging from the MainControl if a new business was deleted or updated.
     */
    public void updateListPaging() {
        switch (mInformation) {
            case INFORMATION_BARANGAY_ID:
                mReportIDs = mCacheModel.getBrgyIDIDsCache();
                break;

            case INFORMATION_BARANGAY_CLEARANCE:
                mReportIDs = mCacheModel.getBrgyClearanceIDsCache();
                break;

            case INFORMATION_BUSINESS_CLEARANCE:
                mReportIDs = mCacheModel.getBusiClearanceIDsCache();
        }

        updateListPaging(false);
    }
}
