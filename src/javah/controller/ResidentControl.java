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
 * to cache queried data, specifically about the residents, from the database.
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
        void onDeleteResidentButtonClicked(Resident resident);
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

    /* A label displaying the name of the resident selected. */
    @FXML private Label mResidentName;

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
    @FXML private TextArea mAddress1;

    /**
     * A text area displaying the address 2 of the resident selected. Unlike
     * address 1, the address 2 can be null.
     */
    @FXML private TextArea mAddress2;

    /**
     * Allows editing of the resident selected. Tells the listener of this
     * resident control at the main control to open the Resident form pop-up
     * and populate the form with the resident selected data, to be modified
     * or updated.
     *
     * @see ResidentFormControl
     */
    @FXML private ImageView mEditButton;

    /**
     * Tells the listener of thie resident control to call the confirmation
     * dialog pop-up to confirm whether to delete the resident selected.
     *
     * @see ConfirmationDialogControl
     */
    @FXML private ImageView mDeleteButton;

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
     * Called before setCacheModel()
     */
    @FXML
    private void initialize() {
        // Initialize mResidentLabels with storage for 40 labels.
        mResidentLabels = new Label[40];

        // Populate mResidentLabels with 40 labels and display it in a matrix of 20x2 mResidentListPaging.
        for (int i = 0; i < 40; i++) {
            Label label = new Label();
            label.setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(500);
            label.setPrefWidth(1000);

            mResidentLabels[i] = label;
            mResidentListPaging.add(label, i % 2 == 0 ? 0 : 1, i / 2);

            // Add a label selected event listener to each label.
            final int labelIndex = i;
            label.setOnMouseClicked(event -> setResidentSelected(labelIndex));
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
        mListener.onEditResidentButtonClicked(mResidentSelected);
    }

    /**
     * Tell the main scene to show the resident delete confirmation dialog.
     * @param event
     */
    @FXML
    public void onDeleteResidentButtonClicked(Event event) {
        mListener.onDeleteResidentButtonClicked(mResidentSelected);
    }

    /**
     * Called after initialize() and is called in the MainControl.
     * Set the main scene as the listener to this object.
     * @param listener
     */
    public void setListener(OnResidentControlListener listener) {
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
        mResidentNames = mCacheModel.getResidentNamesCache();

        // Determine the initial number of Pages and set the default current page to 1.
        updateListPaging(false);
    }

    public void deleteSelectedResident() {
        mDatabaseModel.archiveResident(mResidentSelected.getId());
        mResidentIDs.remove(mResidentSelectedIndex);
        mResidentNames.remove(mResidentSelectedIndex);

        updateListPaging(true);
    }

    /**
     * todo: Cache storage should be stored in permanent cache rather than the volatile cache.
     * @param resident
     */
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
        mResidentCount = mResidentIDs.size();
        mPageCount = (int) Math.ceil(mResidentCount / 40.0);
        mCurrentPage = (int) Math.ceil(index / 39.0);

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount + "");

        // Display the default data when no resident is selected.
        updateCurrentPage();

        // Select the newly created resident.
        setResidentSelected(index % 40);
    }

    public void updateResident(Resident resident) {
        // Make a copy of the label selected index for reselecting.
        int labelSelectedIndex = mLabelSelectedIndex;

        mDatabaseModel.updateResident(resident);

        // Update the resident lists.
        int index = mResidentIDs.indexOf(resident.getId());
        mResidentNames.remove(index);

        String name = String.format("%s, %s %s.",
                resident.getLastName(), resident.getFirstName(), resident.getMiddleName().charAt(0));

        String auxiliary = resident.getAuxiliary();
        name += auxiliary != null ? " " + auxiliary + (auxiliary.matches("(Sr|Jr)") ? "." : "") : "";

        mResidentNames.add(index, name);

        updateCurrentPage();

        // Unselect the resident and select it again to update its displayed data.
        setResidentSelected(mLabelSelectedIndex);
        setResidentSelected(labelSelectedIndex);
    }

    public void setBlurListPaging(boolean blur) {
        mResidentListPaging.setStyle(blur ? CSSContract.STYLE_GRID_UNBORDERED : CSSContract.STYLE_GRID_BORDERED);
    }

    /**
     * Update the resident selected data displayed.
     * @param newLabelSelectedIndex is the index of the label containing the resident to be displayed. If it is equal
     *                              to -1, then the example data is displayed.
     */
    private void setResidentSelected(int newLabelSelectedIndex) {
        // Determine the index of the resident in place of the currently selected label.
        mResidentSelectedIndex = newLabelSelectedIndex + 40 * (mCurrentPage - 1);

        /**
         * If a resident is selected, then display its data.
         * If a resident is unselected or no resident is selected, then display the example data.
         */
        Consumer<Boolean> setDisplaySelectedResidentInfo = (isDisplayed) -> {

            if (isDisplayed) {
                mNoResidentSelectedPane.setVisible(false);

                // Query the data of the currently selected resident.
                mResidentSelected = mDatabaseModel.getResident(mResidentIDs.get(mResidentSelectedIndex));

                mResidentPhoto.setImage(mResidentSelected.getPhotoPath() != null ?
                        new Image("file:" + mResidentSelected.getPhotoPath()) : BarangayUtils.getDefaultDisplayPhoto());

                mResidentName.setText(mResidentNames.get(mResidentSelectedIndex));

                // Format birthdate to YYYY dd, mm
                // Set the displayed birth date.
                Calendar birthdate = Calendar.getInstance();
                birthdate.setTime(mResidentSelected.getBirthDate());
                int birthYear = birthdate.get(Calendar.YEAR);
                int birthDay = birthdate.get(Calendar.DAY_OF_MONTH);
                String birthMonth = BarangayUtils.convertMonthIntToString(birthdate.get(Calendar.MONTH));

                mBirthDate.setText("");
                mBirthDate.setText(String.format("%s %s, %s", birthMonth, birthDay, birthYear));

                Calendar currentDate = Calendar.getInstance();
                // Set the displayed age.
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


                // Set the displayed address 1.
                mAddress1.setText(mResidentSelected.getAddress1());

                // Set the displayed address 2.
                if(mResidentSelected.getAddress2() != null) {
                    mAddress2.setVisible(true);
                    mAddress2Label.setVisible(true);
                    mAddress2.setText(mResidentSelected.getAddress2());
                } else {
                    mAddress2.setVisible(false);
                    mAddress2Label.setVisible(false);
                }

                mDeleteButton.setVisible(true);
                mEditButton.setVisible(true);

            } else {
                mNoResidentSelectedPane.setVisible(true);
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
                    setDisplaySelectedResidentInfo.accept(true);
                }

            } else {
                // If there is a previous selection, unselect it.
                // Also, if the previously selected resident is selected again, then unselect it.
                if (newLabelSelectedIndex == -1 || mLabelSelectedIndex == newLabelSelectedIndex) {
                    mResidentLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
                    mLabelSelectedIndex = -1;
                    mResidentSelectedIndex = -1;
                    setDisplaySelectedResidentInfo.accept(false);

                // Unselect the previously selcted resident, then select the currently selected resident.
                } else {
                    mResidentLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED);
                    mResidentLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);

                    mLabelSelectedIndex = newLabelSelectedIndex;
                    setDisplaySelectedResidentInfo.accept(true);
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
        setResidentSelected(-1);

        int firstIndex = (mCurrentPage - 1) * 40;
        int lastIndex = mCurrentPage * 40 > mResidentCount - 1 ? mResidentCount - 1 : mCurrentPage * 40;
        int currentIndex = firstIndex;

        for (int i = 0; i < 40; i++) {
            if (currentIndex <= lastIndex) {
                mResidentLabels[i].setText(mResidentNames.get(currentIndex));
                currentIndex++;
            } else
                mResidentLabels[i].setText("");
        }
    }

    /**
     * Updates the list paging, mResidentCount and mPageCount.
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

}
