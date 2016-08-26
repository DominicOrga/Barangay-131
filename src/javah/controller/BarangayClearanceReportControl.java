package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.*;
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
import javafx.scene.transform.Scale;
import javah.Main;
import javah.container.BarangayClearance;
import javah.container.BarangayID;
import javah.contract.PreferenceContract;
import javah.model.PreferenceModel;
import javah.util.BarangayUtils;
import javah.util.DraggableSignature;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import java.util.Calendar;

public class BarangayClearanceReportControl {

    public interface OnBarangayClearanceReportListener {
        void onCancelButtonClicked();
        void onSaveButtonClicked(BarangayClearance barangayClearance);
    }

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

    public static final byte
            REQUEST_CREATE_REPORT = 1,
            REQUEST_DISPLAY_REPORT = 2,
            REQUEST_SNAPSHOT_REPORT = 3;

    private PreferenceModel mPrefModel;
    private BarangayClearance mBarangayClearance;
    private DraggableSignature mChmDraggableSignature, mSecDraggableSignature;
    private OnBarangayClearanceReportListener mListener;



    @FXML
    private void initialize() {
        // Disable horizontal scrolling of scroll pane.
        mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mChmDraggableSignature = new DraggableSignature(mChmSignature);
        mSecDraggableSignature = new DraggableSignature(mSecSignature);

        mChmDraggableSignature.setStroke(Color.BLACK);
        mSecDraggableSignature.setStroke(Color.BLACK);

        // All text areas are assigned a listener to auto adjust their height when the text no longer fits.
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
        if (printReport()) mListener.onCancelButtonClicked();
    }

    @FXML
    public void onPrintAndSaveButtonClicked(ActionEvent actionEvent) {
        if (printReport()) {
            saveSignatureDimensions();
            mListener.onCancelButtonClicked();
        }
    }

    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {
        saveSignatureDimensions();
        mListener.onSaveButtonClicked(mBarangayClearance);
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
    }

    private void saveSignatureDimensions() {
        // If the barangay clearance contains a chairman signature (which always does), then store its coordinates and
        // dimension to mBarangayID.
        double[] signatureDimension = new double[]{
                mChmDraggableSignature.getX(),
                mChmDraggableSignature.getY(),
                mChmDraggableSignature.getWidth(),
                mChmDraggableSignature.getHeight()};

        mBarangayClearance.setChmSignatureDimension(signatureDimension);

        // Save the dimension of the chairman signature to the preference model.
        mPrefModel.put(PreferenceContract.BRGY_CLEARANCE_CHM_SIGNATURE_DIMENSION,
                String.format("%.5f %.5f %.5f %.5f",
                        mChmDraggableSignature.getX(),
                        mChmDraggableSignature.getY(),
                        mChmDraggableSignature.getWidth(),
                        mChmDraggableSignature.getHeight()
                )
        );

        signatureDimension = new double[]{
                mSecDraggableSignature.getX(),
                mSecDraggableSignature.getY(),
                mSecDraggableSignature.getWidth(),
                mSecDraggableSignature.getHeight()};

        mBarangayClearance.setSecSignatureDimension(signatureDimension);

        // Save the dimension of the secretary signature to the preference model.
        mPrefModel.put(PreferenceContract.BRGY_CLEARANCE_SEC_SIGNATURE_DIMENSION,
                String.format("%.5f %.5f %.5f %.5f",
                        mSecDraggableSignature.getX(),
                        mSecDraggableSignature.getY(),
                        mSecDraggableSignature.getWidth(),
                        mSecDraggableSignature.getHeight()
                )
        );

        mPrefModel.save();
    }

    /**
     *
     * @return true if printing is successful. Otherwise, return false.
     */
    private boolean printReport() {
        // Hide the draggable rectangles to make sure that they're not printed.
        mSecDraggableSignature.setVisible(false);
        mChmDraggableSignature.setVisible(false);

        PrinterJob job = PrinterJob.createPrinterJob();

        // Start print setup if a printer is found.
        if(job != null){

            // Disable the report dialog when the print dialog is open.
            mRootPane.setDisable(true);
            boolean result = job.showPrintDialog(Main.mPrimaryStage); // Window must be your main Stage
            mRootPane.setDisable(false);

            // If the client cancels the printing, then no printing will occur.
            if (result) {
                // Create a new page layout with reduced margins.
                PageLayout pageLayout = job.getPrinter().createPageLayout(
                                job.getPrinter().getDefaultPageLayout().getPaper(),
                                PageOrientation.PORTRAIT, 0.5, 0.5, 0.5, 0.5);

                // Determine the scale value needed to fit mDocumentPane in the paper.
                double scaleValue = pageLayout.getPrintableWidth() / mDocumentPane.getBoundsInParent().getWidth();
                Scale tempScale = new Scale(scaleValue, scaleValue);

                // Temporarily apply the scale value to mDocumentPane. Reset scaleback to normal after printing.
                mDocumentPane.getTransforms().add(tempScale);

                job.printPage(pageLayout, mDocumentPane);
                job.endJob();

                mDocumentPane.getTransforms().removeAll(tempScale);
            }

            return result;
        }

        return false;
    }


    /**
     * Populate the report with the barangay clearance data.
     * Furthermore, update the scene depending on the request.
     * Can also return a snapshot of the barangay clearance.
     * @param barangayClearance
     */
    public Image setBarangayClearance(BarangayClearance barangayClearance, byte request) {
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
        mTotalYearsOfResidency.setText(mBarangayClearance.getTotalYearsResidency() +
                (mBarangayClearance.getTotalYearsResidency() > 1 ? " years" : " year"));
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

        switch (request) {
            case REQUEST_CREATE_REPORT :
                mPrintAndSaveButton.setVisible(true);
                mPrintAndSaveButton.setManaged(true);
                mSaveButton.setVisible(true);
                mSaveButton.setManaged(true);

                mPrintButton.setVisible(false);
                mPrintButton.setManaged(false);
                break;
            case REQUEST_DISPLAY_REPORT :
                mPrintAndSaveButton.setVisible(false);
                mPrintAndSaveButton.setManaged(false);
                mSaveButton.setVisible(false);
                mSaveButton.setManaged(false);

                mPrintButton.setVisible(true);
                mPrintButton.setManaged(true);
                break;
            case REQUEST_SNAPSHOT_REPORT :
                return mDocumentPane.snapshot(null, null);
        }

        return null;
    }

    public void setListener(OnBarangayClearanceReportListener listener) {
        mListener = listener;
    }

    public void setPreferenceModel(PreferenceModel prefModel) {
        mPrefModel = prefModel;
    }
}
