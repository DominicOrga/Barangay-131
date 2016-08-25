package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javah.container.BarangayClearance;
import javah.util.BarangayUtils;
import javah.util.DraggableSignature;

import java.util.Calendar;

public class BarangayClearanceReportControl {

    @FXML Pane mRootPane;

    @FXML ImageView mChmPhoto;
    @FXML TextArea mChmNameText;
    @FXML TextArea mKagawad1Text, mKagawad2Text, mKagawad3Text,
            mKagawad4Text, mKagawad5Text, mKagawad6Text,
            mKagawad7Text;
    @FXML TextArea mTrsrNameText;
    @FXML TextArea mSecNameText;
    @FXML Label mBrgyClearanceIDLabel;

    /**
     * Components within the document.
     */
    @FXML Pane mDocumentPane;
    @FXML ImageView mChmSignature, mSecSignature;
    @FXML TextArea mChmPrintedName, mSecPrintedName;
    @FXML TextFlow mTextFlow;
    /**
     * The line breaks used to seperate the paragraphs.
     * Unfortunately, new line \n can only  work programmatically.
     */
    @FXML Text mTextLineBreak1, mTextLineBreak2;
    @FXML Text mBrgyClearanceName, mBrgyClearanceAddress,
            mTotalYearsOfResidency, mYearOfResidency, mBrgyClearancePurpose,
            mBrgyClearanceDay, mBrgyClearanceDate;

    @FXML Button mPrintButton, mPrintAndSaveButton, mSaveButton;

    @FXML ScrollPane mScrollPane;

    private BarangayClearance mBarangayClearance;

    private DraggableSignature mChmDraggableSignature, mSecDraggableSignature;

    public static byte REQUEST_CREATE_REPORT = 1, REQUEST_DISPLAY_REPORT = 2;

    @FXML
    private void initialize() {
        // Disable horizontal scrolling of scroll pane.
        mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mChmDraggableSignature = new DraggableSignature(mChmSignature);
        mSecDraggableSignature = new DraggableSignature(mSecSignature);

        mChmDraggableSignature.setStroke(Color.BLACK);
        mSecDraggableSignature.setStroke(Color.BLACK);

        BarangayUtils.addAutoResizeListener(mChmNameText, 220);
        BarangayUtils.addAutoResizeListener(mSecNameText, 220);
        BarangayUtils.addAutoResizeListener(mTrsrNameText, 220);
        BarangayUtils.addAutoResizeListener(mKagawad1Text, 220);
        BarangayUtils.addAutoResizeListener(mKagawad2Text, 220);
        BarangayUtils.addAutoResizeListener(mKagawad3Text, 220);
        BarangayUtils.addAutoResizeListener(mKagawad4Text, 220);
        BarangayUtils.addAutoResizeListener(mKagawad5Text, 220);
        BarangayUtils.addAutoResizeListener(mKagawad6Text, 220);
        BarangayUtils.addAutoResizeListener(mKagawad7Text, 220);
        BarangayUtils.addAutoResizeListener(mChmPrintedName, 220);
        BarangayUtils.addAutoResizeListener(mSecPrintedName, 220);

        // This is needed since \n only works programmatically.
        mTextLineBreak1.setText("\n\n");
        mTextLineBreak2.setText("\n\n");
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

    /**
     * Populate the report with the barangay clearance data.
     * Furthermore, update the scene depending on the request.
     * @param barangayClearance
     */
    public void setBarangayClearance(BarangayClearance barangayClearance, byte request) {
        mBarangayClearance = barangayClearance;

        mChmPhoto.setImage(mBarangayClearance.getChmPhoto() != null ?
                new Image("file:" + mBarangayClearance.getChmPhoto()) : BarangayUtils.getDefaultDisplayPhoto());

        mChmNameText.setText("Hon. " + mBarangayClearance.getChmName().toUpperCase());
        mSecNameText.setText(mBarangayClearance.getSecName().toUpperCase());
        mTrsrNameText.setText(mBarangayClearance.getTreasurerName().toUpperCase());
        mBrgyClearanceIDLabel.setText("131-" + mBarangayClearance.getID());

        if (mBarangayClearance.getKagawadName(0) != null) {
            mKagawad1Text.setText(mBarangayClearance.getKagawadName(0).toUpperCase());
            mKagawad1Text.setVisible(true);
            mKagawad1Text.setManaged(true);
        } else {
            mKagawad1Text.setVisible(false);
            mKagawad1Text.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(1) != null) {
            mKagawad2Text.setText(mBarangayClearance.getKagawadName(1).toUpperCase());
            mKagawad2Text.setVisible(true);
            mKagawad2Text.setManaged(true);
        } else {
            mKagawad2Text.setVisible(false);
            mKagawad2Text.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(2) != null) {
            mKagawad3Text.setText(mBarangayClearance.getKagawadName(2).toUpperCase());
            mKagawad3Text.setVisible(true);
            mKagawad3Text.setManaged(true);
        } else {
            mKagawad3Text.setVisible(false);
            mKagawad3Text.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(3) != null) {
            mKagawad4Text.setText(mBarangayClearance.getKagawadName(3).toUpperCase());
            mKagawad4Text.setVisible(true);
            mKagawad4Text.setManaged(true);
        } else {
            mKagawad4Text.setVisible(false);
            mKagawad4Text.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(4) != null) {
            mKagawad5Text.setText(mBarangayClearance.getKagawadName(4).toUpperCase());
            mKagawad5Text.setVisible(true);
            mKagawad5Text.setManaged(true);
        } else {
            mKagawad5Text.setVisible(false);
            mKagawad5Text.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(5) != null) {
            mKagawad6Text.setText(mBarangayClearance.getKagawadName(5).toUpperCase());
            mKagawad6Text.setVisible(true);
            mKagawad6Text.setManaged(true);
        } else {
            mKagawad6Text.setVisible(false);
            mKagawad6Text.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(6) != null) {
            mKagawad7Text.setText(mBarangayClearance.getKagawadName(6).toUpperCase());
            mKagawad7Text.setVisible(true);
            mKagawad7Text.setManaged(true);
        } else {
            mKagawad7Text.setVisible(false);
            mKagawad7Text.setManaged(false);
        }

        mSecPrintedName.setText(mBarangayClearance.getSecName().toUpperCase());
        mChmPrintedName.setText(mBarangayClearance.getChmName().toUpperCase());

        if (mBarangayClearance.getChmSignature() != null) {
            mChmSignature.setImage(new Image("file:" + mBarangayClearance.getChmSignature()));
            mChmDraggableSignature.setVisible(request == REQUEST_CREATE_REPORT ? true : false);
        } else {
            mChmSignature.setImage(null);
            mChmDraggableSignature.setVisible(false);
        }

        double[] signatureDimension = mBarangayClearance.getChmSignatureDimension();

        if (signatureDimension != null) {
            mChmDraggableSignature.setX(signatureDimension[0]);
            mChmDraggableSignature.setY(signatureDimension[1]);
            mChmDraggableSignature.setWidth(signatureDimension[2]);
            mChmDraggableSignature.setHeight(signatureDimension[3]);
        } else {
            mChmDraggableSignature.setX(25);
            mChmDraggableSignature.setY(25);
            mChmDraggableSignature.setWidth(210);
            mChmDraggableSignature.setHeight(90);
        }

        if (mBarangayClearance.getSecSignature() != null) {
            mSecSignature.setImage(new Image("file:" + mBarangayClearance.getSecSignature()));
            mSecDraggableSignature.setVisible(request == REQUEST_CREATE_REPORT ? true : false);
        } else {
            mSecSignature.setImage(null);
            mSecDraggableSignature.setVisible(false);
        }

        signatureDimension = mBarangayClearance.getSecSignatureDimension();

        if (signatureDimension != null) {
            mSecDraggableSignature.setX(signatureDimension[0]);
            mSecDraggableSignature.setY(signatureDimension[1]);
            mSecDraggableSignature.setWidth(signatureDimension[2]);
            mSecDraggableSignature.setHeight(signatureDimension[3]);
        } else {
            mSecDraggableSignature.setX(265);
            mSecDraggableSignature.setY(25);
            mSecDraggableSignature.setWidth(210);
            mSecDraggableSignature.setHeight(90);
        }

        // Fill out the document body.
        mBrgyClearanceName.setText(mBarangayClearance.getResidentName().toUpperCase());
        mBrgyClearanceAddress.setText(mBarangayClearance.getAddress());
        mTotalYearsOfResidency.setText(mBarangayClearance.getTotalYearsResidency() + "year/s");
        mYearOfResidency.setText(mBarangayClearance.getYearOfResidency() == -1 ?
                "birth" : mBarangayClearance.getYearOfResidency() + "");
        mBrgyClearancePurpose.setText(mBarangayClearance.getPurpose());

        Calendar dateIssued = Calendar.getInstance();
        dateIssued.setTime(mBarangayClearance.getDateIssued());

        int day = dateIssued.get(Calendar.DAY_OF_MONTH);
        String daySuffix;
        switch (day % 10) {
            case 1 : daySuffix = "st"; break;
            case 2 : daySuffix = "nd"; break;
            case 3 : daySuffix = "rd"; break;
            default : daySuffix = "th";
        }

        mBrgyClearanceDay.setText(day + daySuffix);
        mBrgyClearanceDate.setText(
                BarangayUtils.convertMonthIntToString(dateIssued.get(Calendar.MONTH)) + ", " + dateIssued.get(Calendar.YEAR));
    }
}
