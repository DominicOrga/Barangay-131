package javah.controller.information.barangay_id;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javah.contract.CSSContract;
import javah.model.CacheModel;
import javah.util.BarangayUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 *  This class will handle the barangay ID form.
 */
public class BarangayIDFormControl {

    /**
     * The grid pane containing the list of residents.
     */
    @FXML GridPane mResidentGridPane;
    @FXML TextField mSearchField;
    @FXML Label mCurrentPageLabel, mPageCountLabel;

    /**
     * The labels in the resident grid pane.
     * Takes hold of the resident with respect to the mCurrentPage.
     */
    private Label[] mResidentLabels = new Label[10];

    private CacheModel mCacheModel;

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

    public void setCacheModel(CacheModel cacheModel) {
        mCacheModel = cacheModel;

        mResidentNames = mCacheModel.getResidentNamesCache();
        mResidentIDs = mCacheModel.getResidentIDsCache();

        // Determine the initial number of Pages and set the default current page to 1.
        updateListPaging(false);
    }

    /**
     * Updates the list paging, mResidentCount and mPageCount.
     * @param stayOnPage determines whether current page should be maintained or not after the update.
     */
    private void updateListPaging(boolean stayOnPage) {
        mResidentCount = mResidentIDs.size();
        mPageCount = (int) Math.ceil(mResidentCount / 10.0);
        mCurrentPage = stayOnPage ? (mPageCount < mCurrentPage) ? mCurrentPage-- : mCurrentPage : 1;;

        mCurrentPageLabel.setText(mCurrentPage + "");
        mPageCountLabel.setText(mPageCount == 0 ? 1 + "" : mPageCount + "");

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

        Consumer<Boolean> setDisplayResidentInfo = (isDisplayed) -> {
            if (isDisplayed) {

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
                }

            } else
                // If there is a previous selection, unselect it.
                // Also, if the previously selected resident is selected again, then unselect it.
                if (newLabelSelectedIndex == -1 || mLabelSelectedIndex == newLabelSelectedIndex) {
                    mResidentLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED_WHITE);
                    mLabelSelectedIndex = -1;
                    mResidentSelectedIndex = -1;

                    // Unselect the previously selcted resident, then select the currently selected resident.
                } else {
                    mResidentLabels[mLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_UNSELECTED_WHITE);
                    mResidentLabels[newLabelSelectedIndex].setStyle(CSSContract.STYLE_LABEL_SELECTED);

                    mLabelSelectedIndex = newLabelSelectedIndex;
                }
        }
    }


}
