package javah.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javah.container.Resident;
import javah.contract.CSSContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.util.BarangayUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Serves as the central heart of the resident information system. Displays
 * all the residents within this' list paging. Calls varying dialogs to
 * create, update and delete residents from the Main Control. In addition,
 * this controller is connected to the Database model and has the authority
 * to update the Residents Table. Furthermore, it makes use of the Cache model
 * to cache queried data, specifically about the residents from the database.
 *
 * @see ResidentFormControl
 * @see MainControl
 * @see DatabaseModel
 * @see CacheModel
 */
public class ResidentControl {

    /**
     * Sends a task to this listener to launch a pop-up that can create, update or
     * delete a resident. The listener exists within the main control, since the
     * main control has the power to launch pop-ups.
     *
     * @version  %I%, %G%
     * @see MainControl
     */
    public interface OnResidentControlListener {

        /**
         * Tells this listener at the Main Control to show the resident form pop-up.
         * Sets the Resident Form Control to resident creation mode.
         *
         * @see MainControl
         * @see ResidentFormControl
         */
        void onNewResidentButtonClicked();

        /**
         * Tells this listener at the Main Control to show the resident form pop-up.
         * Sets the Resident Form Control to resident update mode.
         *
         * @param resident
         *        The resident to be displayed in the Resident Form for data updating.
         * @see MainControl
         * @see ResidentFormControl
         */
        void onEditResidentButtonClicked(Resident resident);

        /**
         * Tells this listener at the Main Control to show the Confirmation Dialog.
         * The confirmation dialog will contain a two buttons, specifically the
         * confirm button and cancel buttonn. If the confirm button is clicked,
         * then the parameter resident will be deleted. Otherwise, cancel the
         * resident deletion.
         *
         * @param resident
         *        The resident to be deleted.
         * @see MainControl
         * @see ConfirmationDialogControl
         */
        void onDeleteResidentButtonClicked();
    }

    /**
     * A grid pane used to display 40 residents at a time. The residents to
     * be displayed are determined by the current page.
     */
    @FXML private GridPane mResidentListPaging;

    /* A label signifying the current page within the resident list paging. */
    @FXML private Label mCurrentPageLabel;

    /* A total number of pages of the resident list paging. */
    @FXML private Label mPageCountLabel;

    /**
     * A text field used for filtering the residents to be displayed at the resident
     * list paging. The system filters the residents by name in alphabetical order.
     * If no text is present within the text field, then filter is considered as
     * disabled.
     */
    @FXML private TextField mSearchField;

    /* An Image view that displays the photo of the resident selected. */
    @FXML private ImageView mResidentPhoto;

    /* A text displaying the name of the resident selected. */
    @FXML private Text mResidentName;

    /* A label displaying the birth date of the resident selected. */
    @FXML private Label mBirthDate;

    /* A label displaying the age of the resident selected. */
    @FXML private Label mAge;

    /* A label displaying the year of residency of the resident selected. */
    @FXML private Label mResidentSince;

    /**
     * A label signifying the address 2 of the resident selected. If the resident
     * selected does not have an address 2 then the label is invisible. Else, the
     * label is set to visible.
     */
    @FXML private Label mAddress2Label;

    /**
     * A text area displaying the address 1 of the resident selected. Note that
     * address 1 cannot be null.
     */
    @FXML private Text mAddress1;

    /**
     * A text area displaying the address 2 of the resident selected. Unlike
     * address 1, the address 2 can be null.
     */
    @FXML private Text mAddress2;

    /**
     * A button that decrements the current page value, if possible. Then
     * calls the function to update the current page of the resident list
     * paging. The current page value cannot be less than 1.
     */
    @FXML private Button mBackPageButton;

    /**
     * A button that increments the current page value, if possible. Then
     * calls the function to update the current page of the resident list
     * paging. The current page values cannot exceed the value of the
     * total page count.
     */
    @FXML private Button mNextPageButton;

    /**
     * A pane that covers the resident details panel. If a resident is
     * selected then the pane is invisble. Else, the pane is set to
     * visible.
     */
    @FXML private Pane mNoResidentSelectedPane;

    /**
     * A reference to the database model instantiated from the main control.
     * Allows this resident controller to manage the residents table from
     * the database.
     *
     * @see DatabaseModel
     */
    private DatabaseModel mDatabaseModel;

    /**
     * A reference to the cache model instantiated from the main control.
     * Allows this resident controller to manage the cached data pertaining to the
     * resident.
     *
     * @see CacheModel
     */
    private CacheModel mCacheModel;

    /**
     * Holds the resident IDs that can be displayed at the list paging. The list can
     * either serve as a reference to the resident IDs cached data from the Cache Model
     * or have a filtered list of resident IDs.
     *
     * @see CacheModel
     */
    private List<String> mResidentIDs;

    /**
     * Holds the resident names that can be displayed at the list paging. The list can
     * either serve as a reference to the resident names cached data from the Cache
     * Model or have a filtered list of resident names.
     *
     * @see CacheModel
     */
    private List<String> mResidentNames;

    /**
     * The value representing the index of the selected resident. Value range is
     * between 0 and the number of residents minus 1. If the value is equal to -1,
     * then no resident is currently selected. Used for identifying the elected
     * resident data from the Resident IDs list and Resident names list.
     */
    private int mResidentSelectedIndex;

    /**
     * An array containing all the labels of the resident list paging. The total
     * number of labels contained within the array is exactly 40.
     */
    private Label[] mResidentLabels;

    /**
     * The value representing which label is selected from the resident list paging.
     * Value range is between 0 and 39. If the value is equal to -1, then no label
     * from the list paging is currently selected.
     */
    private int mLabelSelectedIndex;

    /**
     * Represents the current page of the resident list paging. The range is between
     * 1 and the page count.
     */
    private int mCurrentPage;

    /**
     * Represents the number of pages within the resident list paging. The value
     * is calculated by dividing the total number of residents by 40 and getting its
     * ceil.
     */
    private int mPageCount;

    /**
     * Represents the total number of residents within the resident list paging.
     * *Does not reflect cached residents.
     */
    private int mResidentCount;

    /**
     * Holds the data of the resident selected. The data will be used to show a
     * description about the selected resident at the stage.
     */
    private Resident mResidentSelected;

    /**
     * The listener of this controller. The listener is placed at the main control
     * so that it can launch the pop-ups needed to create, update or delete a
     * resident.
     *
     * @see OnResidentControlListener
     */
    private OnResidentControlListener mListener;

    /**
     * Initialize the label of the resident list paging. Each labels has its own mouse
     * click listener, which if clicked will set the label and the resident it contains
     * as selected. Automatically Called during initialization.
     */
    @FXML
    private void initialize() {
        mResidentLabels = new Label[40];

        for (int i = 0; i < 40; i++) {
            Label label = new Label();
            label.setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(500);
            label.setPrefWidth(1000);

            mResidentLabels[i] = label;
            mResidentListPaging.add(label, i % 2 == 0 ? 0 : 1, i / 2);

            final int labelIndex = i;
            label.setOnMouseClicked(event -> setResidentToLabelSelected(labelIndex));
        }


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

        if (keywords.trim().equals("")) {
            mResidentIDs = mCacheModel.getResidentIDsCache();
            mResidentNames = mCacheModel.getResidentNamesCache();
        } else {
            String[] keywordsArray = keywords.split(" ");

            mResidentIDs = BarangayUtils.getFilteredIDs(
                    mCacheModel.getResidentIDsCache(), mCacheModel.getResidentNamesCache(), keywordsArray);
        }

        updateListPaging(false);
    }

    /**
     * Listen to any key press from the search field. If the key pressed is the enter
     * key, then manually click the search button.
     *
     * @param event
     */
    @FXML
    public void onSearchFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            onSearchButtonClicked(null);
    }

    /**
     * Move the resident list paging to the previous page when possible. If moving
     * backwards is no longer possible, then hide the back page button. If moving
     * backwards from the last page, then re-enable the Next Page Button.
     *
     * @param event
     */
    @FXML
    public void onBackPageButtonClicked(Event event) {
        mCurrentPage -= 1;
        updateCurrentPage();
        mCurrentPageLabel.setText(mCurrentPage + "");

        if (mNextPageButton.isDisabled())
            mNextPageButton.setDisable(false);

        if (mCurrentPage == 1)
            mBackPageButton.setDisable(true);
    }

    /**
     * Move the resident list paging to the next page when possible. If moving
     * forward is no longer possible, then hide the next page button. If moving
     * forward from the start page, then re-enable the Back Page Button.
     *
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
     * Tell listener at the main control to show the resident form for resident
     * creation.
     *
     * @param actionEvent
     *
     * @see OnResidentControlListener
     * @see ResidentFormControl
     */
    @FXML
    public void onNewResidentButtonClicked(ActionEvent actionEvent) {
        mListener.onNewResidentButtonClicked();
    }

    /**
     * Tell the listener at the main control to show the resident form for
     * resident updating.
     *
     * @param event
     *
     * @see OnResidentControlListener
     * @see ResidentFormControl
     */
    @FXML
    public void onEditResidentButtonClicked(Event event) {
        mListener.onEditResidentButtonClicked(mResidentSelected);
    }

    /**
     * Tell the main scene to show the confirmation dialog. If yes is clicked, then
     * archive the resident.
     *
     * @param event
     */
    @FXML
    public void onDeleteResidentButtonClicked(Event event) {
        mListener.onDeleteResidentButtonClicked();
    }

    /**
     * Update the resident selected data displayed.
     *
     * @param newLabelSelectedIndex
     *        The index of the label containing the resident to be displayed. If it is equal
     *        to -1, then remove any displayed resident data.
     */
    private void setResidentToLabelSelected(int newLabelSelectedIndex) {
        // Determine the index of the resident in place of the currently selected label.
        mResidentSelectedIndex = newLabelSelectedIndex + 40 * (mCurrentPage - 1);

        /**
         * If a resident is selected, then display its data. If a resident is unselected or
         * no resident is selected, then do not display any data.
         */
        Consumer<Boolean> displayResidentInfo = (isDisplayed) -> {

            if (isDisplayed) {
                mNoResidentSelectedPane.setVisible(false);

                String residentSelectedID = mResidentIDs.get(mResidentSelectedIndex);

                mResidentSelected = mDatabaseModel.getResident(residentSelectedID);

                mResidentPhoto.setImage(null);

                if (mResidentSelected.getPhotoPath() != null)
                    mResidentPhoto.setImage(new Image("file:" + mResidentSelected.getPhotoPath()));

                // Note: mResidentIDs field can either reference the Resident IDs at the cache
                // model or take hold of a seperate filtered Resident IDs list. Thus, in
                // tracking the Resident Name, the cached resident IDs should always be used
                // to track the Resident Name.
                int i = mCacheModel.getResidentIDsCache().indexOf(residentSelectedID);

                mResidentName.setText(mResidentNames.get(i));

                Calendar birthdate = Calendar.getInstance();
                birthdate.setTime(mResidentSelected.getBirthDate());

                int birthYear = birthdate.get(Calendar.YEAR);
                int birthDay = birthdate.get(Calendar.DAY_OF_MONTH);
                String birthMonth = BarangayUtils.convertMonthIntToString(birthdate.get(Calendar.MONTH));

                mBirthDate.setText(String.format("%s %s, %s", birthMonth, birthDay, birthYear));

                // Set the displayed age.
                Calendar currentDate = Calendar.getInstance();
                int age = currentDate.get(Calendar.YEAR) - birthYear;

                if(birthYear != currentDate.get(Calendar.YEAR))
                    age -= birthdate.get(Calendar.MONTH) > currentDate.get(Calendar.MONTH) ||
                            (birthdate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                                    birthdate.get(Calendar.DAY_OF_MONTH) > currentDate.get(Calendar.DAY_OF_MONTH)) ? 1 : 0;

                mAge.setText(age + "");

                // Set the displayed year and month of residency.
                mResidentSince.setText(
                        mResidentSelected.getYearOfResidency() == -1 ?
                                "Birth" : BarangayUtils.convertMonthIntToString(mResidentSelected.getMonthOfResidency()) + " " +
                                mResidentSelected.getYearOfResidency());

                mAddress1.setText(mResidentSelected.getAddress1());

                // Set the displayed address 2, if any.
                if(mResidentSelected.getAddress2() != null) {
                    mAddress2.setVisible(true);
                    mAddress2Label.setVisible(true);
                    mAddress2.setText(mResidentSelected.getAddress2());
                } else {
                    mAddress2.setVisible(false);
                    mAddress2Label.setVisible(false);
                }

            } else {
                mNoResidentSelectedPane.setVisible(true);
            }
        };

        // If a label is clicked without containing any resident, then ignore the event.
        if (mResidentSelectedIndex < mResidentIDs.size()) {
            // if no previous resident is selected, then simply make the new selection.
            if (mLabelSelectedIndex == -1) {
                if (newLabelSelectedIndex != -1) {
                    mResidentLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);
                    mLabelSelectedIndex = newLabelSelectedIndex;
                    displayResidentInfo.accept(true);
                }

            } else {
                // If there is a previous selection, unselect it.
                // Also, if the previously selected resident is selected again, then unselect it.
                if (newLabelSelectedIndex == -1 || mLabelSelectedIndex == newLabelSelectedIndex) {
                    mResidentLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
                    mLabelSelectedIndex = -1;
                    mResidentSelectedIndex = -1;
                    displayResidentInfo.accept(false);

                    // Unselect the previously selcted resident, then select the currently selected resident.
                } else {
                    mResidentLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
                    mResidentLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);

                    mLabelSelectedIndex = newLabelSelectedIndex;
                    displayResidentInfo.accept(true);
                }
            }
        }
    }

    /**
     * Update current page with respect to Current Page. That is, the value of Current Page
     * will determine the displayed residents.
     *
     * Note: Moving from one page to another removes the resident selected.
     */
    private void updateCurrentPage() {
        // Make sure that no resident is selected when moving from one page to another.
        setResidentToLabelSelected(-1);

        int firstIndex = (mCurrentPage - 1) * 40;
        int lastIndex = mCurrentPage * 40 > mResidentCount - 1 ? mResidentCount - 1 : mCurrentPage * 40;
        int currentIndex = firstIndex;

        for (int i = 0; i < 40; i++) {
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
     * Update the resident count, page count and current page based on the mResidentIDs
     * and go back to the first page of the resident list paging without any resident
     * selected.
     *
     * @param stayOnPage determines whether current page should be maintained or not after the update.
     */
    private void updateListPaging(boolean stayOnPage) {
        mResidentCount = mResidentIDs.size();
        mPageCount = (int) Math.ceil(mResidentCount / 40.0);
        mCurrentPage = stayOnPage ? (mPageCount < mCurrentPage) ? mCurrentPage-- : mCurrentPage : 1;;

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount == 0 ? 1 + "" : mPageCount + "");

        // Disable the back page button if the current page is the first one.
        mBackPageButton.setDisable(mCurrentPage == 1 ? true : false);

        // Disable the next page button if the current page is the last one.
        mNextPageButton.setDisable(mCurrentPage >= mPageCount ? true : false);

        updateCurrentPage();
    }

    /**
     * Note: Called right after the initialize method.
     *
     * Set the listener of this controller.
     *
     * @param listener
     *        The listener to call the necessary dialogs for resident creation,
     *        update and deletion.
     *
     * @see OnResidentControlListener
     */
    public void setListener(OnResidentControlListener listener) {
        mListener = listener;
    }

    /**
     * Note: Called after the initialize method.
     *
     * Make a reference to the global database model.
     *
     * @param databaseModel
     *        The global database model shared throughout the system.
     *
     * @see DatabaseModel
     */
    public void setDatabaseModel(DatabaseModel databaseModel) {
        mDatabaseModel = databaseModel;
    }

    /**
     * Note: Called after the initialize method.
     *
     * Make a reference to the global cache model.
     *
     * @param cacheModel
     *        The global cache model throughout the system.
     *
     * @see CacheModel
     */
    public void setCacheModel(CacheModel cacheModel) {
        mCacheModel = cacheModel;
    }

    /**
     * Confirm if the user wants to delete the resident. If confirmed yes, then archive
     * the resident and remove the data of the deleted resident from the Resident's
     * cached data.
     */
    public void deleteSelectedResident() {
        mDatabaseModel.deleteResident(mResidentSelected.getId());

        mCacheModel.uncacheResident(mResidentSelected.getId());

        updateListPaging(true);
    }

    /**
     * Add the parameter resident to the database and include it at the cached Resident's
     * data.
     *
     * @param resident
     *        The resident to be created.
     */
    public void createResident(Resident resident) {
        // Create the resident and get its corresponding unique id.
        String residentId = mDatabaseModel.createResident(resident);
        resident.setId(residentId);

        // Cache the newly created resident.
        int index = mCacheModel.cacheResident(resident);

        mResidentIDs = mCacheModel.getResidentIDsCache();

        // Once the resident is created, the current page must be placed where the newly
        // created resident is inserted and must be auto selected.
        mResidentCount = mResidentIDs.size();
        mPageCount = (int) Math.ceil(mResidentCount / 40.0);
        mCurrentPage = (int) Math.ceil(index / 39.0);

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount + "");

        // Disable the back page button if the current page is the first one.
        mBackPageButton.setDisable(mCurrentPage == 1 ? true : false);

        // Disable the next page button if the current page is the last one.
        mNextPageButton.setDisable(mCurrentPage >= mPageCount ? true : false);

        // Display the default data when no resident is selected.
        updateCurrentPage();

        // Select the newly created resident.
        setResidentToLabelSelected(index % 40);
    }

    /**
     * Update the data of the resident from the database and set the name to the
     * Resident Names cached data.
     *
     * @param resident
     *        The resident to be updated.
     */
    public void updateResident(Resident resident) {
        mDatabaseModel.updateResident(resident);

        // Cache the resident to update.
        mCacheModel.cacheResident(resident);

        // Make a copy of the label selected index for reselecting.
        int labelSelectedIndex = mLabelSelectedIndex;

        updateCurrentPage();

        // Unselect the resident and select it again to update its displayed data.
        setResidentToLabelSelected(mLabelSelectedIndex);
        setResidentToLabelSelected(labelSelectedIndex);
    }

    /**
     * Prepare the list paging for blurring at the Main control whenever a pop-up
     * is displayed.
     *
     * @param blur
     *        The boolean to determine whether to prepare the list paging for blurring
     *        or unblurring.
     */
    public void setBlurListPaging(boolean blur) {
        mResidentListPaging.setStyle(blur ? CSSContract.STYLE_GRID_UNBORDERED : CSSContract.STYLE_GRID_BORDERED);
    }

    /**
     * Update the Resident IDs cached data and update the list paging. Call after
     * sending the Resident Scene to front.
     */
    public void resetCachedData() {
        mResidentIDs = mCacheModel.getResidentIDsCache();
        mResidentNames = mCacheModel.getResidentNamesCache();
        updateListPaging(false);
    }
}
