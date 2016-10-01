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
import javah.contract.DatabaseContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.util.BarangayUtils;
import javah.util.NodeNameHandler;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
         * Called if the mCreateButton is pressed while the mState is equal to
         * STATE_SELECTION. Tell the BusinessClearanceReportControl to try to
         * create the business clearance.
         *
         * Called if the mCreateButton is pressed while the mState is equal to
         * STATE_UPDATE. Tell the InformationControl to reload the list paging,
         * since an update was issued
         *
         * @param businessClearance
         *        The business clearance to be sent to the BusinessClearanceReportControl.
         *        Can be null if a business was updated.
         *
         * @see BusinessClearanceReportControl
         */
        void onCreateButtonClicked(BusinessClearance businessClearance);

        /**
         * Close the business clearance form.
         */
        void onCancelButtonClicked();

        /**
         * Call the confirmation dialog to confirm whether or not delete the selected
         * business.
         *
         * @see ConfirmationDialogControl
         */
        void onDeleteButtonClicked();
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
        BarangayUtils.addTextLimitListener(mSearchField, 100);

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
            label.setOnMouseClicked(event -> setBusinessSelected(labelIndex));
        }

        // The horizontal bar of the scroll pane should always be hidden.
        mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Limit the text input length for each text input control.
        BarangayUtils.addTextLimitListener(mBusiNameField, 100);
        BarangayUtils.addTextLimitListener(mBusiTypeField, 100);
        BarangayUtils.addTextLimitListener(mAddressField, 125);
        BarangayUtils.addTextLimitListener(mClientFirstName, 30);
        BarangayUtils.addTextLimitListener(mClientMiddleName, 30);
        BarangayUtils.addTextLimitListener(mClientMiddleName, 30);
        BarangayUtils.addTextLimitListener(mSearchField, 100);


        // Reset the scene if the root pane is turned visible again.
        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setState(STATE_NO_SELECTION);
                mSearchField.setText(null);
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
        String keywords = mSearchField.getText().trim();

        if (keywords == null || keywords.isEmpty())
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
        // Update the appearance of the buttons that signifies business creation.
        setState(STATE_CREATION);
    }

    /**
     * Call the Confirmation Dialog to confirm whether or not to delete the selected
     * business.
     *
     * @param event
     *        The action event. No usage.
     */
    @FXML
    public void onDeleteButtonClicked(ActionEvent event) {
        mListener.onDeleteButtonClicked();
    }

    /**
     * Update the UI for updating the selected business.
     *
     * @param event
     *        The action event. No usage.
     */
    @FXML
    public void onEditButtonClicked(ActionEvent event) {
        setState(STATE_UPDATE);
    }

    /**
     * Note: used for STATE_CREATION and STATE_UPDATE
     *
     * If the extra owner is clicked, then allow the insertion of additional owners
     * with the help of mNodeNameHandler.
     *
     * @param event
     *        The action event. No usage.
     */
    @FXML
    public void onExtraOwnerClicked(Event event) {
        if (mIsExtraOwnerLabelClickable) {
            mExtraOwnerBox.setVisible(true);
            mExtraOwnerBox.setManaged(true);

            mExtraOwner.setText("Other Owners:");
            mExtraOwner.setTextFill(Color.BLACK);

            // Show at least one node name field.
            mNodeNameHandler.removeNodeNames();
            mNodeNameHandler.addName(null, null, null, null);

            mIsExtraOwnerLabelClickable = false;
        }
    }

    /**
     * If current state is STATE_SELECTION, then send the information of the selected
     * business to BusinessClearanceReportControl to generate a business clearanece.
     * After that, close the form.
     *
     * If current state is STATE_CREATION, then check if the data inserted are valid.
     * If all data are valid, then create the business and return to STATE_NO_SELECTION.
     *
     * If current state is STATE_UPDATE, then check if the data inserted are valid.
     * If all data are valid, then update the business information.
     *
     * @param actionEvent
     *        The action event. No usage.
     *
     * @see BarangayClearanceReportControl
     */
    @FXML
    public void onCreateButtonClicked(ActionEvent actionEvent) {

        /**
         * Check if the inputted data are valid.
         */
        BooleanSupplier isDataValid = () -> {
            boolean isValid = true;

            String businessName = mBusiNameField.getText();
            String businessType = mBusiTypeField.getText();
            String businessAddress = mAddressField.getText();
            String clientFirstName = mClientFirstName.getText();
            String clientMiddleName = mClientMiddleName.getText();
            String clientLastName = mClientLastName.getText();

            if (businessName == null || businessName.trim().isEmpty()) {
                isValid = false;
                mBusiNameField.setStyle(CSSContract.STYLE_TEXTFIELD_ERROR);
                mBusiNameError.setVisible(true);
            } else {
                mBusiNameField.setStyle(null);
                mBusiNameError.setVisible(false);
            }

            if (businessType == null || businessType.trim().isEmpty()) {
                isValid = false;
                mBusiTypeField.setStyle(CSSContract.STYLE_TEXTFIELD_ERROR);
                mBusiTypeError.setVisible(true);
            } else {
                mBusiTypeField.setStyle(null);
                mBusiTypeError.setVisible(false);
            }

            if (businessAddress == null || businessAddress.trim().isEmpty()) {
                isValid = false;
                mAddressField.setStyle(CSSContract.STYLE_TEXTAREA_ERROR);
                mAddressError.setVisible(true);
            } else {
                mAddressField.setStyle(CSSContract.STYLE_TEXTAREA_NO_ERROR);
                mAddressError.setVisible(false);
            }


            mClientFirstName.setStyle(clientFirstName == null || clientFirstName.trim().isEmpty() ?
                    CSSContract.STYLE_TEXTFIELD_ERROR : null
            );

            mClientMiddleName.setStyle(clientMiddleName == null || clientMiddleName.trim().isEmpty() ?
                    CSSContract.STYLE_TEXTFIELD_ERROR : null
            );

            mClientLastName.setStyle(clientLastName == null || clientLastName.trim().isEmpty() ?
                    CSSContract.STYLE_TEXTFIELD_ERROR : null
            );

            // If the extra owner label is not clickable, then that means that an extra owner
            // node name exists and therefore, needs to be validated.
            boolean result = mIsExtraOwnerLabelClickable ? true : mNodeNameHandler.validateNodeNames();

            if (clientFirstName == null || clientFirstName.trim().isEmpty() ||
                    clientMiddleName == null || clientMiddleName.trim().isEmpty() ||
                    clientLastName == null || clientLastName.trim().isEmpty() || !result) {

                isValid = false;
                mNameError.setVisible(true);
            } else
                mNameError.setVisible(false);

            return isValid;
        };

        /**
         * Create a business populated with the data from the input nodes.
         */
        Supplier<Business> createBusiness = () -> {
            Business business = new Business();
            business.setName(mBusiNameField.getText().trim());
            business.setType(mBusiTypeField.getText().trim());
            business.setAddress(mAddressField.getText().trim());

            String[][] owners =  new String[5][4];

            owners[0][0] = BarangayUtils.capitalizeString(mClientFirstName.getText().trim());
            owners[0][1] = BarangayUtils.capitalizeString(mClientMiddleName.getText().trim());;
            owners[0][2] = BarangayUtils.capitalizeString(mClientLastName.getText().trim());;

            String auxiliary = mClientAuxiliary.getValue().toString();
            owners[0][3] = auxiliary.equals("N/A") ? null : auxiliary;

            for (int i = 1; i < 5; i++) {
                String[] extraClientName = mNodeNameHandler.getName(i);

                if (extraClientName == null)
                    break;

                owners[i][0] = extraClientName[0];
                owners[i][1] = extraClientName[1];
                owners[i][2] = extraClientName[2];
                owners[i][3] = extraClientName[3];
            }

            business.setOwners(owners);

            return business;
        };

        // Take necessary action when the create button is clicked.
        switch (mState) {
            // Create the business clearance container to be submitted to the report
            // controller.
            case STATE_SELECTION:
                // Populate the business clearance and send it to the business clearance report.
                // After populating, send it to the business report control to generate a report.
                BusinessClearance businessClearance = new BusinessClearance();

                businessClearance.setID(mDatabaseModel.generateID(DatabaseContract.BusinessClearanceEntry.TABLE_NAME));

                businessClearance.setBusinessID(mBusinessSelected.getID());
                businessClearance.setBusinessName(mBusinessSelected.getName());
                businessClearance.setBusinessType(mBusinessSelected.getType());
                businessClearance.setBusinessAddress(mBusinessSelected.getAddress());

                String[][] owners = mBusinessSelected.getOwners();

                businessClearance.setClient(BarangayUtils.formatName(
                        owners[0][0],
                        owners[0][1],
                        owners[0][2],
                        owners[0][3]
                ));

                // Concatenate the owner names.
                // e.g. DOMINIC R. ORGA, FATIMA R. ORGA, AND JEANELLE C. CATABAY
                String ownersStr = "";
                for (int i = 4; i >= 0; i--) {
                    String firstName = owners[i][0];

                    if (firstName != null) {
                        String middleName = owners[i][1];
                        String lastName = owners[i][2];
                        String auxiliary = owners[i][3];

                        String fullName = BarangayUtils.formatName(firstName, middleName, lastName, auxiliary);

                        if (ownersStr.isEmpty() && i != 0)
                            ownersStr = " AT " + fullName;

                        else if (i != 0)
                            ownersStr = ", " + fullName + ownersStr;

                        else
                            ownersStr = fullName + ownersStr;
                    }
                }

                businessClearance.setOwners(ownersStr);

                System.out.println("*****Populate Business Clearance (Level Form)*****");
                System.out.println("BusinessClearanceFormControl - Business id: " + businessClearance.getBusinessID());
                System.out.println("BusinessClearanceFormControl - Business name: " + businessClearance.getBusinessName());
                System.out.println("BusinessClearanceFormControl - Business type: " + businessClearance.getBusinessType());
                System.out.println("BusinessClearanceFormControl - Business address: " + businessClearance.getBusinessAddress());
                System.out.println("BusinessClearanceFormControl - Business client: " + businessClearance.getClient());
                System.out.println("BusinessClearanceFormControl - Business owners: " + businessClearance.getOwners());

                mListener.onCreateButtonClicked(businessClearance);

                break;
            case STATE_CREATION:

                if (isDataValid.getAsBoolean()) {
                    // Create the business to be stored in the database.
                    Business business = createBusiness.get();

                    // Save the business to the database and cache.
                    business.setID(mDatabaseModel.createBusiness(business));
                    int index = mCacheModel.cacheBusiness(business);

                    // Reload the cached data.
                    mBusinessIDs = mCacheModel.getBusiIDsCache();

                    // Once the business is created, the current page must be placed where the newly
                    // created business is inserted and must be auto selected.
                    mBusinessCount = mBusinessIDs.size();
                    mPageCount = (int) Math.ceil(mBusinessCount / 10.0);
                    mCurrentPage = index / 10 + 1;

                    mCurrentPageLabel.setText(mCurrentPage + "");
                    mPageCountLabel.setText(mPageCount + "");

                    // Disable the back page button if the current page is the first one.
                    mBackPageButton.setDisable(mCurrentPage == 1 ? true : false);

                    // Disable the next page button if the current page is the last one.
                    mNextPageButton.setDisable(mCurrentPage >= mPageCount ? true : false);

                    // Display the default data when no business is selected.
                    updateCurrentPage();

                    // Select the newly created business.
                    setBusinessSelected(index % 10);
                }

                break;
            case STATE_UPDATE:
                if (isDataValid.getAsBoolean()) {
                    Business business = createBusiness.get();
                    business.setID(mBusinessSelected.getID());

                    mDatabaseModel.updateBusiness(business);
                    int index = mCacheModel.cacheBusiness(business);

                    mBusinessIDs = mCacheModel.getBusiIDsCache();

                    updateListPaging(false);

                    // Make sure to auto select the same business after the update.
                    mCurrentPage = index / 10 + 1;

                    mCurrentPageLabel.setText(mCurrentPage + "");

                    // Disable the back page button if the current page is the first one.
                    mBackPageButton.setDisable(mCurrentPage == 1 ? true : false);

                    // Disable the next page button if the current page is the last one.
                    mNextPageButton.setDisable(mCurrentPage >= mPageCount ? true : false);

                    // Display the default data when no business is selected.
                    updateCurrentPage();

                    // Select the newly created business.
                    setBusinessSelected(index % 10);

                    mListener.onCreateButtonClicked(null);
                }
        }
    }

    @FXML
    public void onAddressKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            mRootPane.requestFocus();
            keyEvent.consume();
        }
    }

    /**
     * If the current state is STATE_NO_SELECTION or STATE_SELECTION, then cancel
     * close the form.
     *
     * If the current state is STATE_CREATION, then cancel STATE_CREATION and go to
     * STATE_NO_SELECTION.
     *
     * If the current state is STATE_UPDATE, then cancel STATE_UPDATE, reset the
     * displayed data and go to STATE_SELECTION.
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
                setBusinessSelected(-1);
                setState(STATE_NO_SELECTION);
                break;
            case STATE_UPDATE:
                setState(STATE_SELECTION);
        }
    }

    /**
     * Update the user interface and its data with regards to the given state.
     *
     * @param state
     *        The state to be applied to this form.
     */
    private void setState(byte state) {

        /**
         * Display the extra owners of the selected business to the mExtraOwnersBox with
         * the use of the node name handler if extra owners exist.
         *
         * @param showButtons
         *        A boolean to determine whether to show the remove and add buttons of the node
         *        names or not.
         */
        Consumer<Boolean> displayExtraOwners = (showButtons) -> {

            mNodeNameHandler.setButtonsVisible(showButtons);

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
                }

                if (firstName == null || firstName.isEmpty())
                    break loop;

                String middleName = mBusinessSelected.getOwners()[i][1];
                String lastName = mBusinessSelected.getOwners()[i][2];
                String auxiliary = mBusinessSelected.getOwners()[i][3];

                mNodeNameHandler.addName(firstName, middleName, lastName, auxiliary);
            }
        };

        switch (state) {
            case STATE_NO_SELECTION:
                // Update the UI if only it is not set to STATE_NO_SELECTION.
                if (state != mState) {
                    // Disable the major node containers.
                    mListPagingPane.setDisable(false);
                    mMovePagePane.setDisable(false);
                    mScrollPane.setDisable(true);

                    // Make sure that the create button is disabled when no business is selected
                    // and the color of the create and cancel button are restored to default.
                    mCreateButton.setDisable(true);
                    mCreateButton.setText("Create");
                    mCreateButton.setStyle(CSSContract.STYLE_ORANGE_BUTTON);
                    mCancelButton.setStyle(CSSContract.STYLE_ORANGE_BUTTON);

                    // Hide the edit and delete buttons.
                    mEditButton.setVisible(false);
                    mDeleteButton.setVisible(false);

                    // Hide the extra owner components.
                    mExtraOwner.setVisible(false);
                    mExtraOwnerBox.setVisible(false);
                    mExtraOwnerBox.setManaged(false);

                    // Hide the error labels.
                    mBusiNameError.setVisible(false);
                    mBusiTypeError.setVisible(false);
                    mAddressError.setVisible(false);
                    mNameError.setVisible(false);

                    // Remove the date in the text input controls.
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
                    // Put a cover on top of the data input components to prevent data input.
                    mCoverPane.toFront();

                    mListPagingPane.setDisable(false);
                    mMovePagePane.setDisable(false);
                    mScrollPane.setDisable(false);

                    mCreateButton.setDisable(false);
                    mCreateButton.setText("Create");
                    mCreateButton.setStyle(CSSContract.STYLE_ORANGE_BUTTON);
                    mCancelButton.setStyle(CSSContract.STYLE_ORANGE_BUTTON);

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

                    displayExtraOwners.accept(false);
                break;
            case STATE_CREATION:
                // Make sure that no resident is selected.
                setBusinessSelected(-1);

                // Remove the cover on top of the input nodes to allow data input.
                mCoverPane.toBack();

                mListPagingPane.setDisable(true);
                mMovePagePane.setDisable(true);
                mScrollPane.setDisable(false);

                mCreateButton.setDisable(false);
                mCreateButton.setText("Create Business");
                mCreateButton.setStyle(CSSContract.STYLE_CHOCO_BUTTON);
                mCancelButton.setStyle(CSSContract.STYLE_RED_BUTTON);

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

                mNodeNameHandler.setButtonsVisible(true);

                break;
            case STATE_UPDATE:

                mCoverPane.toBack();

                mListPagingPane.setDisable(true);
                mMovePagePane.setDisable(true);
                mScrollPane.setDisable(false);

                mCreateButton.setDisable(false);
                mCreateButton.setText("Save Changes");
                mCreateButton.setStyle(CSSContract.STYLE_CHOCO_BUTTON);
                mCancelButton.setStyle(CSSContract.STYLE_RED_BUTTON);

                mEditButton.setVisible(false);
                mDeleteButton.setVisible(false);

                displayExtraOwners.accept(true);
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
        setBusinessSelected(-1);

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
    }

    /**
     * Update the business selected data displayed.
     *
     * @param newLabelSelectedIndex
     *        The index of the label containing the business to be displayed. If it is equal
     *        to -1, then the example data is displayed.
     */
    private void setBusinessSelected(int newLabelSelectedIndex) {
        // Determine the index of the business in place of the currently selected label.
        mBusinessSelectedIndex = newLabelSelectedIndex + 10 * (mCurrentPage - 1);

        /**
         * This nested function will update the state of the form, depending whether a resident is selected or not.
         */
        Consumer<Boolean> setDisplayResidentInfo = (isDisplayed) -> {
            if (isDisplayed) {
                mBusinessSelected = mDatabaseModel.getBusiness(mBusinessIDs.get(mBusinessSelectedIndex));
                setState(STATE_SELECTION);
            } else
                setState(STATE_NO_SELECTION);
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

    /**
     * Delete the selected business fom the database and cache.
     */
    public void deleteSelectedBusiness() {
        mDatabaseModel.deleteBusiness(mBusinessSelected.getID());
        mCacheModel.uncacheBusiness(mBusinessSelected.getID());

        mBusinessIDs = mCacheModel.getBusiIDsCache();

        updateListPaging(true);

        setState(STATE_NO_SELECTION);
    }

    /**
     * Disable the root pane when the confirmation dialog is displayed.
     *
     * @param bool
     *        Determines whether to disable or enabled this scene.
     */
    public void setDisable(boolean bool) {
        mRootPane.setDisable(bool);
    }
}
