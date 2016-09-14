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
import javah.contract.PreferenceContract;
import javah.model.PreferenceModel;
import javah.util.BarangayUtils;
import javah.util.DraggableSignature;

import java.util.Calendar;

public class BarangayClearanceReportControl {

    public interface OnBarangayClearanceReportListener {
        void onCancelButtonClicked();
        void onSaveButtonClicked(BarangayClearance barangayClearance);
    }

    @FXML Pane mRootPane;

    @FXML ImageView mChmPhoto;
    @FXML Text mChmName;
    @FXML Text mKagawad1, mKagawad2, mKagawad3,
            mKagawad4, mKagawad5, mKagawad6,
            mKagawad7;
    @FXML Text mTrsrName;
    @FXML Text mSecName;
    @FXML Label mBrgyClearanceIDLabel;

    /**
     * Components within the document.
     */
    @FXML Pane mDocumentPane;
    @FXML ImageView mChmSignature, mSecSignature;
    @FXML Text mChmPrintedName, mSecPrintedName;
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

        // This is needed since \n only works programmatically.
        mTextLineBreak1.setText("\n\n");
        mTextLineBreak2.setText("\n\n");
    }

    @FXML
    public void onPrintButtonClicked(ActionEvent actionEvent) {
        if (printReport()) mListener.onSaveButtonClicked(mBarangayClearance);;
    }

    @FXML
    public void onPrintAndSaveButtonClicked(ActionEvent actionEvent) {
        if (printReport()) {
            saveSignatureDimensions();
            mListener.onSaveButtonClicked(mBarangayClearance);
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
            boolean result = job.showPrintDialog(Main.PRIMARY_STAGE); // Window must be your main Stage
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
                new Image("file:" + mBarangayClearance.getChmPhoto()) : null);

        mChmName.setText("Hon. " + mBarangayClearance.getChmName().toUpperCase());
        mSecName.setText(mBarangayClearance.getSecName().toUpperCase());
        mTrsrName.setText(mBarangayClearance.getTreasurerName().toUpperCase());
        mBrgyClearanceIDLabel.setText("131-" + mBarangayClearance.getID());

        if (mBarangayClearance.getKagawadName(0) != null) {
            mKagawad1.setText(mBarangayClearance.getKagawadName(0).toUpperCase());
            mKagawad1.setVisible(true);
            mKagawad1.setManaged(true);
        } else {
            mKagawad1.setVisible(false);
            mKagawad1.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(1) != null) {
            mKagawad2.setText(mBarangayClearance.getKagawadName(1).toUpperCase());
            mKagawad2.setVisible(true);
            mKagawad2.setManaged(true);
        } else {
            mKagawad2.setVisible(false);
            mKagawad2.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(2) != null) {
            mKagawad3.setVisible(true);
            mKagawad3.setManaged(true);
        } else {
            mKagawad3.setVisible(false);
            mKagawad3.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(3) != null) {
            mKagawad4.setText(mBarangayClearance.getKagawadName(3).toUpperCase());
            mKagawad4.setVisible(true);
            mKagawad4.setManaged(true);
        } else {
            mKagawad4.setVisible(false);
            mKagawad4.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(4) != null) {
            mKagawad5.setText(mBarangayClearance.getKagawadName(4).toUpperCase());
            mKagawad5.setVisible(true);
            mKagawad5.setManaged(true);
        } else {
            mKagawad5.setVisible(false);
            mKagawad5.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(5) != null) {
            mKagawad6.setText(mBarangayClearance.getKagawadName(5).toUpperCase());
            mKagawad6.setVisible(true);
            mKagawad6.setManaged(true);
        } else {
            mKagawad6.setVisible(false);
            mKagawad6.setManaged(false);
        }

        if (mBarangayClearance.getKagawadName(6) != null) {
            mKagawad7.setText(mBarangayClearance.getKagawadName(6).toUpperCase());
            mKagawad7.setVisible(true);
            mKagawad7.setManaged(true);
        } else {
            mKagawad7.setVisible(false);
            mKagawad7.setManaged(false);
        }

        mSecPrintedName.setText(mBarangayClearance.getSecName().toUpperCase());
        mChmPrintedName.setText("Hon. " + mBarangayClearance.getChmName().toUpperCase());

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
            mChmDraggableSignature.setX(265);
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
            mSecDraggableSignature.setX(25);
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
