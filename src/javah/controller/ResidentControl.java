package javah.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javah.DatabaseControl;
import javah.utils.ListFilter;

import java.util.List;

public class ResidentControl {

    /**
     * Used for resident list paging. *Each page contains 40 residents.
     */
    @FXML private GridPane mResidentListGridPane;

    /**
     * The current page label of the resident list paging.
     */
    @FXML private Label mCurrentPageLabel;

    /**
     * The total number of pages of the resident list paging.
     */
    @FXML private Label mPageCountLabel;

    @FXML private TextField mSearchField;

    private DatabaseControl mDatabaseControl;

    /**
     * The list containing all the non-archived resident IDs.
     */
    private List<String> mResidentIDsCache;

    /**
     * The list containing all the non-archived resident names in proper format.
     */
    private List<String> mResidentNamesCache;

    /**
     * A volatile copy of the mResidentIDsCache used to search for non-archived residents.
     */
    private List<String> mResidentIDs;

    /**
     * A volatile copy of the mResidentNamesCache used to display the residents in the list paging.
     */
    private List<String> mResidentNames;

    /**
     * The array containing all the labels of the resident list paging.
     */
    private Label[][] mResidentLabels;

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

    @FXML
    private void initialize() {
        // Initialize the db conntroller.
        mDatabaseControl = new DatabaseControl();

        // Cache resident IDs and names from the database.
        List[] lists = mDatabaseControl.getResidentsIdAndName();
        mResidentIDsCache = lists[0];
        mResidentNamesCache = lists[1];

        // Create a volatile copy of the cached data.
        mResidentIDs = mResidentIDsCache;
        mResidentNames = mResidentNamesCache;

        // Initialize mResidentLabels with storage for 40 labels.
        mResidentLabels = new Label[20][2];

        // Populate mResidentLabels with 40 labels (2, 20) and display it in mResidentListGridPane.
        for (int col = 0; col < 2; col++)
            for (int row = 0; row < 20; row++) {
                Label label = new Label();
                label.setStyle("-fx-background-color: f4f4f4;" + "-fx-font-size: 20;");
                label.setAlignment(Pos.CENTER);
                label.setPrefHeight(500);
                label.setPrefWidth(1000);

                mResidentLabels[row][col] = label;
                mResidentListGridPane.add(label, col, row);
            }

        // Determine the initial number of Pages and set the default current page to 1.
        mResidentCount = mResidentIDs.size();
        mCurrentPage = 1;
        mPageCount = (int) Math.ceil(mResidentCount / 40.0);

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount + "");

        updateCurrentPage();
    }

    /**
     * Update current page with respect to mCurrentPage. That is, the value of mCurrentPage will determine the displayed
     * residents.
     */
    private void updateCurrentPage() {
        int firstIndex = (mCurrentPage - 1) * 40;
        int lastIndex = mCurrentPage * 40 > mResidentCount - 1 ? mResidentCount - 1 : mCurrentPage * 40;
        int currentIndex = firstIndex;

        for (int col = 0; col < 2; col++)
            for (int row = 0; row < 20; row++)
                if (currentIndex <= lastIndex) {
                    mResidentLabels[row][col].setText(mResidentNames.get(currentIndex));
                    currentIndex++;
                } else
                    mResidentLabels[row][col].setText("");
    }

    @FXML
    public void onSearchButtonClicked(Event event) {
        String keywords = mSearchField.getText();

        if (keywords.trim().equals("")) {
            mResidentIDs = mResidentIDsCache;
            mResidentNames = mResidentNamesCache;
        } else {
            String[] keywordsArray = keywords.split(" ");

            List[] lists = ListFilter.filterLists(mResidentIDsCache, mResidentNamesCache, keywordsArray);


            mResidentIDs = lists[0];
            mResidentNames = lists[1];
        }

        mResidentCount = mResidentIDs.size();
        mCurrentPage = 1;
        mPageCount = (int) Math.ceil(mResidentCount / 40.0);

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount + "");

        updateCurrentPage();

    }

    @FXML
    public void onSearchFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onSearchButtonClicked(null);
            mCurrentPageLabel.requestFocus();
        }
    }

    @FXML
    public void onNewResidentButtonClicked(ActionEvent actionEvent) {

    }

    @FXML
    public void onEditResidentButtonClicked(Event event) {
    }

    @FXML
    public void onDeleteResidentButtonClicked(Event event) {
    }

    @FXML
    public void onBackPageButtonClicked(Event event) {
        if (mCurrentPage > 1) {
            mCurrentPage -= 1;
            updateCurrentPage();
            mCurrentPageLabel.setText(mCurrentPage + "");
        }
    }

    @FXML
    public void onNextPageButtonClicked(Event event) {
        if(mCurrentPage < mPageCount) {
            mCurrentPage += 1;
            updateCurrentPage();
            mCurrentPageLabel.setText(mCurrentPage + "");
        }
    }
}
