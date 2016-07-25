package javah.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;
import javah.DatabaseControl;

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
    @FXML private Label mTotalPageLabel;

    private DatabaseControl mDatabaseControl;

    /**
     * The list containing all the non-archived resident IDs.
     */
    private List<String> mResidentIdList;

    /**
     * The list containing all the non-archived resident names in proper format.
     */
    private List<String> mResidentNameList;

    @FXML
    private void initialize() {
        // Initialize the db conntroller.
        mDatabaseControl = new DatabaseControl();

        // Get resident IDs and names from the database.
        List[] lists = mDatabaseControl.getResidentsIdAndName();
        mResidentIdList = lists[0];
        mResidentNameList = lists[1];

        //Populate mResidentListGridPane with 40 labels (2, 20).
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 20; j++) {
                Label label = new Label();
                label.setStyle("-fx-background-color: f4f4f4;" + "-fx-font-size: 20;");
                label.setAlignment(Pos.CENTER);
                label.setPrefHeight(500);
                label.setPrefWidth(1000);

                mResidentListGridPane.add(label, i, j);
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

    }

    @FXML
    public void onNextPageButtonClicked(Event event) {

    }
}
