package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class BarangayClearanceReportControl {

    @FXML Pane mRootPane;

    @FXML ImageView mChmPhoto;
    @FXML TextArea mChmNameText;
    @FXML TextArea mKagawad1Text, mKagawad2Text, mKagawad3Text,
            mKagawad4Text, mKagawad5Text, mKagawad6Text,
            mKagawad7Text;
    @FXML TextArea mTrsrNameText;
    @FXML TextArea mSecNameText;

    @FXML Pane mDocumentPane;
    @FXML TextArea mBodyTextArea;
    @FXML ImageView mChmSignature, mSecSignature;
    @FXML TextArea mChmPrintedName, mSecPrintedName;

    @FXML Button mPrintButton, mPrintAndSaveButton, mSaveButton;

    @FXML ScrollPane mScrollPane;

    @FXML
    private void initialize() {
        // Disable horizontal scrolling of scroll pane.
        mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    @FXML
    public void onPrintButtonClicked(ActionEvent actionEvent) {

    }

    @FXML
    public void onPrintAndSaveButtonClicked(ActionEvent actionEvent) {

    }

    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {

    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {

    }

}
