package javah.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javah.container.Business;
import javah.container.BusinessClearance;
import javah.contract.CSSContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.util.BarangayUtils;
import javah.util.NodeNameHandler;

import java.util.List;
import java.util.function.Consumer;

/**
 * A class controller for the business clearance form. It can fill out forms for
 * report generation with the saved data.
 */
public class BusinessClearanceFormControl {

    /**
     * An interface listener for the BusinessClearanceFormControl which detects
     * whether a business clearance form is to be generated into a report or
     * the report creation is cancelled with a click of some buttons.
     *
     * @see BusinessClearanceFormControl
     */
    public interface OnBusinessClearanceFormListener {

        /**
         * Tell the BusinessClearanceReportControl to try to create the business
         * clearance.
         *
         * Called if the mCreateButton is pressed while the mState is equal to
         * STATE_SELECTION.
         *
         * @param businessClearance
         *        The business clearance to be sent to the BusinessClearanceReportControl.
         *
         * @see BusinessClearanceReportControl
         */
        void onCreateButtonClicked(BusinessClearance businessClearance);

        /**
         * Close the business clearance form.
         */
        void onCancelButtonClicked();
    }

    /* Possible states of the UI of the form. */
    private final byte STATE_NO_SELECTION = 1, STATE_SELECTION = 2, STATE_CREATION = 3, STATE_UPDATE = 4;

    /**
     * Disables the resident info form while the photoshop control is open for an
     * image signature request.
     */
    @FXML Pane mRootPane;

    /* A pane containing all search field and the resident list paging. */
    @FXML Pane mListPagingPane;

    /* The grid pane containing the list of businesses. */
    @FXML GridPane mBusinessGridPane;

    /* A search field for filtering the resident list paging. */
    @FXML TextField mSearchField;

    /* A pane containing the back and next page buttons. */
    @FXML Pane mMovePagePane;

    /* Page labels for the current page and page count. */
    @FXML Label mCurrentPageLabel, mPageCountLabel;

    /* Buttons for the back page and next page. */
    @FXML Button mBackPageButton, mNextPageButton;

    /* A button for editing and deleting the selected business' data. */
    @FXML Button mEditButton, mDeleteButton;


    /* A pane containing the input fields for the business clearance. */
    @FXML ScrollPane mScrollPane;

    /* A pane used to cover the scroll pane to make its child nodes un-clickable. */
    @FXML Pane mCoverPane;

    /* Nodes for inputting the data of the business. */
    @FXML TextField mBusiNameField, mBusiTypeField;
    @FXML TextArea mAddressField;

    /**
     * Nodes for inputting the name of the business owner requesting the business
     * clearance.
     */
    @FXML TextField mClientFirstName, mClientMiddleName, mClientLastName;
    @FXML ComboBox mClientAuxiliary;

    /**
     * A clickable label, wherein if clicked, then a node name handler reveals a
     * node name container to be able to insert another owner. Maximum of 4 associates.
     */
    @FXML Label mExtraOwner;

    /* A node that will contain the other owner node names, if any. */
    @FXML VBox mExtraOwnerBox;

    /* Labels to show the errors which input nodes is empty. */
    @FXML Label mBusiNameError, mBusiTypeError, mAddressError, mNameError;

    /* Buttons to either create a report or cancel the report creation. */
    @FXML Button mCreateButton, mCancelButton;

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
     * The labels in the business grid pane.
     * Takes hold of the business with respect to the mCurrentPage.
     */
    private Label[] mBusinessLabels;

    /* Represents the current page of the resident list paging. */
    private int mCurrentPage;

    /* Represents the number of pages within the resident list paging. */
    private int mPageCount;

    /**
     * Represents the total number of filtered businesses within the business
     * list paging.
     */
    private int mBusinessCount;

    /**
     * The value representing the index of the selected business.
     * Value range is between 0 - [mBusinessIDs.size() - 1].
     */
    private int mBusinessSelectedIndex;

    /**
     * The value representing which label is selected from the business list paging.
     * Value range is between 0 - 9.
     */
    private int mLabelSelectedIndex;

    /* The currently selected business. */
    private Business mBusinessSelected;

    /**
     * A volatile copy of the mBusinessIDsCache used to display the businesses in the
     * list paging. This list can be filtered with the search field. Thus, making it
     * volatile.
     */
    private List<String> mBusinessIDs;

    /* A reference to the business names cache. */
    private List<String> mBusinessNames;

    /* determines what the current state of this form is. */
    private byte mState;

    /* Manages the names of the other clients, if any. */
    private NodeNameHandler mNodeNameHandler;

    /* A listener for this controller. */
    private OnBusinessClearanceFormListener mListener;

    /**
     * A boolean to determine whether the extra owner nodes are already added,
     * therefore, rendering mExtraOwner unclickable. Othewise, the mExtraOwner
     * is clickable to allow the inclusion of extra owners.
     */
    private boolean mIsExtraOwnerLabelClickable;

    /**
     * Initialize the components of the scene.
     */
    @FXML
    private void initialize() {
        mNodeNameHandler = new NodeNameHandler(mExtraOwnerBox, 4, NodeNameHandler.OPERATION_ZERO_TO_MANY);

        mBusinessLabels = new Label[10];

        for (int i = 0; i < 10; i++) {
            Label label = new Label();
            label.setStyle(CSSContract.STYLE_LABEL_UNSELECTED_WHITE);
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(500);
            label.setPrefWidth(1000);

            mBusinessLabels[i] = label;
            mBusinessGridPane.add(label, 0, i);

            // Add a label selected event listener to each label.
            final int labelIndex = i;
            label.setOnMouseClicked(event -> setResidentSelected(labelIndex));
        }

        mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Reset the scene if the root pane is turned visible again.
        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setState(STATE_NO_SELECTION);
                mBusinessIDs = mCacheModel.getBusiIDsCache();
                updateListPaging(false);
            }
        });

        // Every time the Extra Owner box adjusts in height due to addition or removal of node
        // names, then always set the vertical scroll pane at the bottom.
        mExtraOwnerBox.heightProperty().addListener(observable -> mScrollPane.setVvalue(1));

        // Set a listener for the node name handler to detect whether a node name is visible
        // or not. If no more node name is visible from the node name handler, then set the
        // mExtraOwner label as clickable to allow insertion of another node names.
        mNodeNameHandler.setListener((nodeNameVisibleCount) -> {
            if (nodeNameVisibleCount == 0) {
                mExtraOwner.setText("Other Owners?");
                mExtraOwner.setTextFill(Color.valueOf("#FF3F3F"));
                mExtraOwner.setVisible(true);
                mIsExtraOwnerLabelClickable = true;
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
            mBusinessIDs = mCacheModel.getBusiIDsCache();
        else {
            String[] keywordsArray = keywords.split(" ");

            mBusinessIDs = BarangayUtils.getFilteredIDs(
                    mCacheModel.getBusiIDsCache(), mBusinessNames, keywordsArray);
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
     * Update the UI suitable for creating a new business.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onNewBusiButtonClicked(ActionEvent actionEvent) {
        // Make sure that no resident is selected.
        setResidentSelected(-1);

        // Update the appearance of the buttons that signifies business creation.
        mCreateButton.setText("Create Business");
        mCreateButton.setStyle(CSSContract.STYLE_CHOCO_BUTTON);

        mCancelButton.setStyle(CSSContract.STYLE_RED_BUTTON);

        setState(STATE_CREATION);
    }

    @FXML
    public void onDeleteButtonClicked(Event event) {

    }

    @FXML
    public void onEditButtonClicked(Event event) {

    }

    @FXML
    public void onExtraOwnerClicked(Event event) {
        if (mIsExtraOwnerLabelClickable) {
            mExtraOwnerBox.setVisible(true);
            mExtraOwnerBox.setManaged(true);

            mExtraOwner.setText("Other Owners:");
            mExtraOwner.setTextFill(Color.BLACK);

            // Show at least one node name field.
            mNodeNameHandler.removeNodeNames();
            mNodeNameHandler.setIsButtonsVisible(true);
            mNodeNameHandler.addName(null, null, null, null);

            mIsExtraOwnerLabelClickable = false;
        }
    }

    /**
     * If current state is STATE_CREATION, then check if the data inserted are valid.
     * If all data are valid, then create the business and return to STATE_NO_SELECTION.
     *
     *
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onCreateButtonClicked(ActionEvent actionEvent) {

    }

    /**
     * If the current state is STATE_NO_SELECTION or STATE_SELECTION, then cancel
     * close the form.
     *
     * If the current state is STATE_CREATION, then cancel STATE_CREATION and go to
     * STATE_NO_SELECTION.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {

        switch (mState) {
            case STATE_NO_SELECTION: case STATE_SELECTION:
                mListener.onCancelButtonClicked();
                break;
            case STATE_CREATION:
                setResidentSelected(-1);

                mCreateButton.setText("Create");
                mCreateButton.setStyle(CSSContract.STYLE_ORANGE_BUTTON);
                mCancelButton.setStyle(CSSContract.STYLE_ORANGE_BUTTON);
                setState(STATE_NO_SELECTION);
                break;
            case STATE_UPDATE:
        }
    }

    private void setState(byte state) {


        switch (state) {
            case STATE_NO_SELECTION:
                if (state != mState) {
                    mListPagingPane.setDisable(false);
                    mMovePagePane.setDisable(false);
                    mScrollPane.setDisable(true);
                    mCreateButton.setDisable(true);

                    mEditButton.setVisible(false);
                    mDeleteButton.setVisible(false);

                    mExtraOwner.setVisible(false);
                    mExtraOwnerBox.setVisible(false);
                    mExtraOwnerBox.setManaged(false);

                    mBusiNameError.setVisible(false);
                    mBusiTypeError.setVisible(false);
                    mAddressError.setVisible(false);
                    mNameError.setVisible(false);

                    mBusiNameField.setText(null);
                    mBusiTypeField.setText(null);
                    mAddressField.setText(null);
                    mClientFirstName.setText(null);
                    mClientMiddleName.setText(null);
                    mClientLastName.setText(null);
                    mClientAuxiliary.setValue("N/A");
                }

                break;
            case STATE_SELECTION:
                if (state != mState) {
                    mCoverPane.toFront();

                    mListPagingPane.setDisable(false);
                    mMovePagePane.setDisable(false);
                    mScrollPane.setDisable(false);
                    mCreateButton.setDisable(false);

                    mEditButton.setVisible(true);
                    mDeleteButton.setVisible(true);

                    mBusiNameError.setVisible(false);
                    mBusiTypeError.setVisible(false);
                    mAddressError.setVisible(false);
                    mNameError.setVisible(false);
                }

                    mBusiNameField.setText(mBusinessSelected.getName());
                    mBusiTypeField.setText(mBusinessSelected.getType());
                    mAddressField.setText(mBusinessSelected.getAddress());

                    // Set the client owner name.
                    mClientFirstName.setText(mBusinessSelected.getOwners()[0][0]);
                    mClientMiddleName.setText(mBusinessSelected.getOwners()[0][1]);
                    mClientLastName.setText(mBusinessSelected.getOwners()[0][2]);

                    mClientAuxiliary.setValue(mBusinessSelected.getOwners()[0][3] == null ?
                            "N/A" : mBusinessSelected.getOwners()[0][3]);

                    mNodeNameHandler.removeNodeNames();

                    // Set the other clients name, if any.
                    loop:
                    for (int i = 1; i < 5; i++) {
                        String firstName = mBusinessSelected.getOwners()[i][0];

                        // Test if at least one extra owner name exists.
                        if (i == 1) {
                            // No extra owner exists.
                            if (firstName == null || firstName.isEmpty()) {
                                mExtraOwner.setVisible(false);
                                mExtraOwnerBox.setVisible(false);
                                mExtraOwnerBox.setManaged(false);
                                break loop;
                            }

                            mExtraOwner.setVisible(true);
                            mExtraOwner.setText("Other Owners:");
                            mExtraOwner.setTextFill(Color.BLACK);

                            mExtraOwnerBox.setVisible(true);
                            mExtraOwnerBox.setManaged(true);

                            mNodeNameHandler.setIsButtonsVisible(false);
                        }

                        if (firstName == null || firstName.isEmpty())
                            break loop;

                        String middleName = mBusinessSelected.getOwners()[i][1];
                        String lastName = mBusinessSelected.getOwners()[i][2];
                        String auxiliary = mBusinessSelected.getOwners()[i][3];

                        mNodeNameHandler.addName(firstName, middleName, lastName, auxiliary);
                    }

                break;
            case STATE_CREATION:

                mCoverPane.toBack();

                mListPagingPane.setDisable(true);
                mMovePagePane.setDisable(true);
                mScrollPane.setDisable(false);
                mCreateButton.setDisable(false);

                mEditButton.setVisible(false);
                mDeleteButton.setVisible(false);

                mBusiNameField.setText(null);
                mBusiTypeField.setText(null);
                mAddressField.setText(null);
                mClientFirstName.setText(null);
                mClientMiddleName.setText(null);
                mClientLastName.setText(null);
                mClientAuxiliary.setValue("N/A");

                mExtraOwner.setText("Other Owners?");
                mExtraOwner.setTextFill(Color.valueOf("#FF3F3F"));
                mExtraOwner.setVisible(true);
                mIsExtraOwnerLabelClickable = true;

                break;
            case STATE_UPDATE:

                mCoverPane.toBack();

                mListPagingPane.setDisable(true);
                mMovePagePane.setDisable(true);
                mScrollPane.setDisable(false);
                mCreateButton.setDisable(false);

                mEditButton.setVisible(false);
        }

        mState = state;
    }

    /**
     * Updates the list paging, mBusinessCount and mPageCount.
     *
     * @param stayOnPage
     *        Determines whether current page should be maintained or not after the update.
     */
    private void updateListPaging(boolean stayOnPage) {
        mBusinessCount = mBusinessIDs.size();
        mPageCount = (int) Math.ceil(mBusinessCount / 10.0);
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
     * Update current page with respect to mCurrentPage. That is, the value of
     * mCurrentPage will determine the displayed residents. In addition, moving from
     * one page to another removes the resident selected.
     */
    private void updateCurrentPage() {
        // Make sure that no resident is selected when moving from one page to another.
        setResidentSelected(-1);

        int firstIndex = (mCurrentPage - 1) * 10;
        int lastIndex = mCurrentPage * 10 > mBusinessCount - 1 ? mBusinessCount - 1 : mCurrentPage * 10;
        int currentIndex = firstIndex;

        for (int i = 0; i < 10; i++) {
            if (currentIndex <= lastIndex) {
                String id = mBusinessIDs.get(i + firstIndex);
                int index = mCacheModel.getBusiIDsCache().indexOf(id);

                mBusinessLabels[i].setText(mBusinessNames.get(index));
                currentIndex++;
            } else
                mBusinessLabels[i].setText("");
        }

        System.out.println("BusinessClearanceFormControl - Business IDs: " + mBusinessIDs);
    }

    /**
     * Update the business selected data displayed.
     *
     * @param newLabelSelectedIndex
     *        The index of the label containing the business to be displayed. If it is equal
     *        to -1, then the example data is displayed.
     */
    private void setResidentSelected(int newLabelSelectedIndex) {
        // Determine the index of the business in place of the currently selected label.
        mBusinessSelectedIndex = newLabelSelectedIndex + 10 * (mCurrentPage - 1);

        /**
         * This nested function will update the state of the form, depending whether a resident is selected or not.
         */
        Consumer<Boolean> setDisplayResidentInfo = (isDisplayed) -> {
            if (isDisplayed) {
                mBusinessSelected = mDatabaseModel.getBusiness(mBusinessIDs.get(mBusinessSelectedIndex));
                setState(STATE_SELECTION);
            } else {
                setState(STATE_NO_SELECTION);
            }
        };

        // This is where the code selection and unselection view update happens.

        // If a label is clicked without containing any resident, then ignore the event.
        if (mBusinessSelectedIndex < mBusinessIDs.size()) {
            // if no previous resident is selected, then simply make the new selection.
            if (mLabelSelectedIndex == -1) {
                if (newLabelSelectedIndex != -1) {
                    mBusinessLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);
                    mLabelSelectedIndex = newLabelSelectedIndex;
                    setDisplayResidentInfo.accept(true);
                }

            } else
                // If there is a previous selection, unselect it.
                // Also, if the previously selected resident is selected again, then unselect it.
                if (newLabelSelectedIndex == -1 || mLabelSelectedIndex == newLabelSelectedIndex) {
                    mBusinessLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED_WHITE);
                    mLabelSelectedIndex = -1;
                    mBusinessSelectedIndex = -1;
                    setDisplayResidentInfo.accept(false);

                    // Unselect the previously selcted resident, then select the currently selected resident.
                } else {
                    mBusinessLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED_WHITE);
                    mBusinessLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);

                    mLabelSelectedIndex = newLabelSelectedIndex;
                    setDisplayResidentInfo.accept(true);
                }
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
        mBusinessNames = mCacheModel.getBusiNamesCache();
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

    /**
     * Set the listener for this controller.
     *
     * @param listener
     *        The listener for this controller.
     */
    public void setListener(OnBusinessClearanceFormListener listener) {
        mListener = listener;
    }
}
