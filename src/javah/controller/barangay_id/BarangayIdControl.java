package javah.controller.barangay_id;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javah.container.Resident;
import javah.contract.CSSContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.util.BarangayUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class BarangayIdControl {

    /**
     * An interface that tells the main scene to open up a manipulation resident dialog.
     */
    public interface OnResidentSceneListener {
        void onNewResidentButtonClicked();
        void onEditResidentButtonClicked(Resident resident);
        void onDeleteResidentButtonClicked(Resident resident);
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

    /**
     * Widgets representing the information of the selected resident.
     */
    @FXML private ImageView mResidentPhoto;
    @FXML private Label mResidentName, mBirthDate, mAge, mResidentSince, mAddress2Label;
    @FXML private TextArea mAddress1, mAddress2;

    /**
     * Edit or manipulate the currently selected resident with this buttons.
     */
    @FXML private ImageView mEditButton, mDeleteButton;

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

    private List<String> mBarangayIDIDs;
    private List<String> mBarangayIDResidentIDs;
    private List<Date> mBarangayIDsDateIssued;

    /**
     * The value representing which label is selected from the list paging.
     * Value range is between 0 - 39.
     */
    private int mLabelSelectedIndex = -1;

    /**
     * The value representing the index of the selected resident.
     * Value range is between 0 - [mResidentIDs.size() - 1].
     */
    private int mResidentSelectedIndex;

    /**
     * The array containing all the labels of the resident list paging.
     */
    private Label[] mGridLabels;

    private String[] mBarangayIDLabelLocation;

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

    private Resident mBarangayIDSelected;

    private OnResidentSceneListener mListener;

    /**
     * Called before setCacheModel()
     */
    @FXML
    private void initialize() {

        // Initialize mGridLabels with storage for 40 labels.
        mGridLabels = new Label[40];

        mBarangayIDLabelLocation = new String[40];

        // Populate mGridLabels with 40 labels and display it in a matrix of 20x2 mListGridPane.
        for (int i = 0; i < 40; i++) {
            Label label = new Label();
            label.setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(500);
            label.setPrefWidth(1000);

            mGridLabels[i] = label;
//            mListGridPane.add(label, i / 20, i >= 20 ? i - 20 : i);

            // Add a label selected event listener to each label.
            final int labelIndex = i;
            label.setOnMouseClicked(event -> setBarangayIDSelected(labelIndex));
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
            mResidentNames = mCacheModel.getmResidentNamesCache();
        } else {
            String[] keywordsArray = keywords.split(" ");

            List[] lists = BarangayUtils.filterLists(
                    mCacheModel.getResidentIDsCache(), mCacheModel.getmResidentNamesCache(), keywordsArray);


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
    public void onBackPageButtonClicked(Event event) {
        if (mCurrentPage > 1) {
            mCurrentPage -= 1;
            updateCurrentPage();
            mCurrentPageLabel.setText(mCurrentPage + "");
        }
    }

    /**
     * Move the resident list paging to the next page when possible.
     * @param event
     */
    @FXML
    public void onNextPageButtonClicked(Event event) {
        if(mCurrentPage < mPageCount) {
            mCurrentPage += 1;
            updateCurrentPage();
            mCurrentPageLabel.setText(mCurrentPage + "");
        }
    }

    /**
     * Tell the main scene to show the resident creation dialog.
     * @param actionEvent
     */
    @FXML
    public void onNewResidentButtonClicked(ActionEvent actionEvent) {
        mListener.onNewResidentButtonClicked();
    }

    /**
     * Tell the main scenne to show the resident update dialog.
     * @param event
     */
    @FXML
    public void onEditResidentButtonClicked(Event event) {
        mListener.onEditResidentButtonClicked(mBarangayIDSelected);
    }

    /**
     * Tell the main scene to show the resident delete confirmation dialog.
     * @param event
     */
    @FXML
    public void onDeleteResidentButtonClicked(Event event) {
        mListener.onDeleteResidentButtonClicked(mBarangayIDSelected);
    }

    /**
     * Called after initialize() and is called in the MainControl.
     * Set the main scene as the listener to this object.
     * @param listener
     */
    public void setListener(OnResidentSceneListener listener) {
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
        // Create a volatile copy of the cached data.
        mResidentIDs = mCacheModel.getResidentIDsCache();
        mResidentNames = mCacheModel.getmResidentNamesCache();
        mBarangayIDIDs = mCacheModel.getBarangayIDIDsCache();
        mBarangayIDResidentIDs = mCacheModel.getBarangayIDResidentIDCache();
        mBarangayIDsDateIssued = mCacheModel.getBarangayIDdateIssuedCache();

        System.out.println("barangay id id's: " + mBarangayIDIDs);

        // Determine the initial number of Pages and set the default current page to 1.
        updateListPaging(false);
    }

    public void deleteSelectedResident() {
        mDatabaseModel.archiveResident(mBarangayIDSelected.getId());
        mResidentIDs.remove(mResidentSelectedIndex);
        mResidentNames.remove(mResidentSelectedIndex);

        updateListPaging(true);
    }

    public void createResident(Resident resident) {
        // Create the resident and get its corresponding unique id.
        String residentId = mDatabaseModel.createResident(resident);

        // Format the resident name to be inserted in the list.
        String residentName = String.format("%s, %s %s.",
                resident.getLastName(),
                resident.getFirstName(),
                resident.getMiddleName().toUpperCase().charAt(0));

        // Add the resident name to the resident names list and then sort it alphabetically.
        mResidentNames.add(residentName);
        Collections.sort(mResidentNames, String.CASE_INSENSITIVE_ORDER);

        // Get the index of the resident name within the list after insertion.
        int index = mResidentNames.indexOf(residentName);

        // Use the acquired index to insert the resident ID to the resident IDs list.
        mResidentIDs.add(index, residentId);

        // Once the resident is created, the current page must be placed where the newly created resident is inserted and
        // must be auto selected.
        mLabelUseCount = mResidentIDs.size();
        mPageCount = (int) Math.ceil(mLabelUseCount / 40.0);
        mCurrentPage = (int) Math.ceil(index / 39.0);

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount + "");

        // Display the default data when no resident is selected.
        updateCurrentPage();

        // Select the newly created resident.
        setBarangayIDSelected(index % 40);
    }

    public void updateResident(Resident resident) {
        // Make a copy of the label selected index for reselecting.
        int labelSelectedIndex = mLabelSelectedIndex;

        mDatabaseModel.updateResident(resident);

        // Update the resident lists.
        int index = mResidentIDs.indexOf(resident.getId());
        mResidentNames.remove(index);
        mResidentNames.add(index, String.format("%s, %s %s.",
                resident.getLastName(), resident.getFirstName(), resident.getMiddleName().charAt(0)));

        updateCurrentPage();

        // Unselect the resident and select it again to update its displayed data.
        setBarangayIDSelected(mLabelSelectedIndex);
        setBarangayIDSelected(labelSelectedIndex);
    }

    public void setBlurListPaging(boolean blur) {
        mListGridPane.setStyle(blur ? CSSContract.STYLE_GRID_BORDERED : CSSContract.STYLE_GRID_UNBORDERED);
    }

    /**
     * Update the barangay ID selected data displayed.
     * @param newLabelSelectedIndex is the index of the label containing the barangay ID to be displayed. If it is equal
     *                              to -1, then the example data is displayed.
     */
    private void setBarangayIDSelected(int newLabelSelectedIndex) {
        // Determine the index of the resident in place of the currently selected label.
        mResidentSelectedIndex = newLabelSelectedIndex + 40 * (mCurrentPage - 1);

        /**
         * If a barangay ID is selected, then display its data.
         * If a barangay ID is unselected or no barangay ID is selected, then display the example data.
         */
        Consumer<Boolean> setDisplaySelectedResidentInfo = (isDisplayed) -> {
            if (isDisplayed) {

                // Query the data of the currently selected resident.
                mBarangayIDSelected = mDatabaseModel.getResident(mResidentIDs.get(mResidentSelectedIndex));

            } else {
                mResidentPhoto.setImage(new Image("/res/ic_default_resident.png"));
                mResidentName.setText("");
                mBirthDate.setText("");
                mAge.setText("");
                mResidentSince.setText("");

                mAddress1.setText("");
                mAddress2.setVisible(false);
                mAddress2Label.setVisible(false);

                mDeleteButton.setVisible(false);
                mEditButton.setVisible(false);
            }
        };

        // This is where the code selection and unselection view update happens.
        // If a label is clicked without containing any resident, then ignore the event.
        if (!mBarangayIDLabelLocation[newLabelSelectedIndex].isEmpty()) {
            // if no previous resident is selected, then simply make the new selection.
            if (mLabelSelectedIndex == -1) {
                if (newLabelSelectedIndex != -1) {
                    mGridLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);
                    mLabelSelectedIndex = newLabelSelectedIndex;
                }

            } else {
                // If there is a previous selection, unselect it.
                // Also, if the previously selected resident is selected again, then unselect it.
                if (newLabelSelectedIndex == -1 || mLabelSelectedIndex == newLabelSelectedIndex) {
                    mGridLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
                    mLabelSelectedIndex = -1;
                    mResidentSelectedIndex = -1;

                    // Unselect the previously selcted resident, then select the currently selected resident.
                } else {
                    mGridLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
                    mGridLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);

                    mLabelSelectedIndex = newLabelSelectedIndex;
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
//        setBarangayIDSelected(-1);
        int barangayIDIndex = 0;
        int precedingMonth = -1;
        int labelPosition = 0;
        int page = 1;
        Calendar calendar = Calendar.getInstance();

        // Find the index of the barangayID to be first displayed to the list paging current page.
        while (page < mCurrentPage) {
            calendar.setTime(mBarangayIDsDateIssued.get(barangayIDIndex));
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
            mGridLabels[i].setText("");
            mListGridPane.add(mGridLabels[i], i % 2 == 0 ? 0 : 1, i / 2, 1, 1);
        }

        // Clear the Label references to the barangay ID's.
        Arrays.fill(mBarangayIDLabelLocation, "");

        System.out.println(barangayIDIndex < mBarangayIDIDs.size());
        System.out.println(barangayIDIndex);
        System.out.println(mBarangayIDIDs.size());
        // Determine the barangay ID placement within the labels.
        while (labelPosition <= 40 && barangayIDIndex < mBarangayIDIDs.size()) {
            calendar.setTime(mBarangayIDsDateIssued.get(barangayIDIndex));
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

            // Store the barangay ID in mBarangayIDLabelLocation which corresponds to the label location.
            mBarangayIDLabelLocation[labelIndex] = mBarangayIDIDs.get(barangayIDIndex);

            // Get the name of the resident applicant of the barangay ID.
            int index = mResidentIDs.indexOf(mBarangayIDResidentIDs.get(barangayIDIndex));
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
        int size = mBarangayIDsDateIssued.size();

        int precedingMonth = -1;
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < size; i++) {
            calendar.setTime(mBarangayIDsDateIssued.get(i));
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

        updateCurrentPage();
    }
}
