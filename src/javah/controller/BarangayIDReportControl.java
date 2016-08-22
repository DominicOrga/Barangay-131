package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javah.Main;
import javah.container.BarangayID;
import javah.util.BarangayUtils;
import javah.util.DraggableSignature;

import javax.swing.*;
import java.text.SimpleDateFormat;

public class BarangayIDReportControl {

    public interface OnBarangayIDReportListener {
        void onCancelButtonClicked();
        void onSaveButtonClicked(BarangayID barangayID);
    }

    /**
     * The Pane containing the barangay ID components.
     */
    @FXML private Pane mBarangayIDPane;

    @FXML private Pane mRootPane;

    @FXML private Label mBarangayIDCode;
    @FXML private TextArea mAddressTextArea;
    @FXML private Label mDateIssuedLabel, mDateValidLabel;

    /**
     * The views for the resident.
     */
    @FXML private Label mResidentNameLabel;
    @FXML private ImageView mPhotoView;
    @FXML private ImageView mResSignatureView;

    /**
     * The views for the chairman.
     */
    @FXML private Label mChmNameLabel;
    @FXML private ImageView mChmSignatureView;

    @FXML private Button mPrintAndSaveButton, mPrintButton, mSaveButton;

    public static byte REQUEST_CREATE_REPORT = 1, REQUEST_DISPLAY_REPORT = 2;

    private BarangayID mBarangayID;

    private OnBarangayIDReportListener mListener;

    /**
     * Modifies the Signature views into draggable views.
     */
    private DraggableSignature mChmDraggableSignature;
    private DraggableSignature mResDraggableSignature;

    @FXML
    private void initialize() {
        mResDraggableSignature = new DraggableSignature(mResSignatureView);
        mChmDraggableSignature = new DraggableSignature(mChmSignatureView);

        reset();
    }

    @FXML
    public void onPrintAndSaveButtonClicked(ActionEvent actionEvent) {
        if (printReport()) {
            saveReport();
            mListener.onCancelButtonClicked();
            reset();
        }
    }

    /**
     * Print the report.
     * @param actionEvent
     */
    @FXML
    public void onPrintButtonClicked(ActionEvent actionEvent) {
        if (printReport()) {
            mListener.onCancelButtonClicked();
            reset();
        }
    }

    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {
        saveReport();
        mListener.onSaveButtonClicked(mBarangayID);
        reset();
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
        reset();
    }

    /**
     * Pass the barangay ID to be displayed in the report.
     * Determine the state of the scene, whether to create or to simply display the data.
     * @param barangayID contains the data to be displayed in the report.
     *                   If the barangayID does not have a unique ID, then state of scene is set to barangay ID creation.
     *                   Else, barangayID is already created and the state is simply to display the barangay ID.
     */
    public void setBarangayID(BarangayID barangayID, byte request) {
        mBarangayID = barangayID;

        // If on report creation state, then display the 'print & save' and 'print' buttons.
        if (request == REQUEST_CREATE_REPORT) {
            mPrintButton.setVisible(false);

            mPrintAndSaveButton.setVisible(true);
            mPrintAndSaveButton.setManaged(true);
            mSaveButton.setVisible(true);
            mSaveButton.setManaged(true);

            mChmDraggableSignature.setVisible(true);
        }

        // Set the image of the barangay ID, if any.
        mPhotoView.setImage(mBarangayID.getPhoto() != null ?
                new Image("file:" + mBarangayID.getPhoto()) : BarangayUtils.getDefaultDisplayPhoto());

        // Set the applicant name and barangay ID code.
        mBarangayIDCode.setText(mBarangayID.getID());
        mResidentNameLabel.setText(mBarangayID.getResidentName().toUpperCase());

        // Set the applicant signature, if any.
        if (mBarangayID.getResidentSignature() != null) {
            mResSignatureView.setImage(new Image("file:" + mBarangayID.getResidentSignature()));
            mResDraggableSignature.setVisible(request == REQUEST_CREATE_REPORT);
        }


        if (mBarangayID.getResidentSignatureDimension() != null) {
            double[] dimension = mBarangayID.getResidentSignatureDimension();

            mResDraggableSignature.setX(dimension[0]);
            mResDraggableSignature.setY(dimension[1]);
            mResDraggableSignature.setWidth(dimension[2]);
            mResDraggableSignature.setHeight(dimension[3]);
        }

        // Set the applicant address.
        mAddressTextArea.setText(mBarangayID.getAddress());

        // Set the date issued and validity of the barangay id.
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd yyyy");
        mDateIssuedLabel.setText(dateFormat.format(mBarangayID.getDateIssued()));
        mDateValidLabel.setText(dateFormat.format(mBarangayID.getDateValid()));

        mChmNameLabel.setText("Hon. " + mBarangayID.getChmName().toUpperCase());
        mChmSignatureView.setImage(new Image("file:" + mBarangayID.getChmSignature()));

        if (mBarangayID.getChmSignatureDimension() != null) {
            double[] dimension = mBarangayID.getChmSignatureDimension();

            mChmDraggableSignature.setX(dimension[0]);
            mChmDraggableSignature.setY(dimension[1]);
            mChmDraggableSignature.setWidth(dimension[2]);
            mChmDraggableSignature.setHeight(dimension[3]);
        }
    }

    public void setListener(OnBarangayIDReportListener listener) {
        mListener = listener;
    }

    /**
     * When the scene is reset, it takes the state of simply displaying the report.
     * Must always be called before setBarangayID.
     */
    public void reset() {
        mPrintButton.setVisible(true);

        mPrintAndSaveButton.setVisible(false);
        mPrintAndSaveButton.setManaged(false);
        mSaveButton.setVisible(false);
        mSaveButton.setManaged(false);
        mPrintButton.setVisible(true);

        mResDraggableSignature.setVisible(false);
        mChmDraggableSignature.setVisible(false);

        // Place the resident and chairman signature back to its default coordinate and dimension.
        // Note that mChmSignatureView always has an image.
        mResSignatureView.setImage(null);
        mResDraggableSignature.setX(60);
        mResDraggableSignature.setY(350);
        mResDraggableSignature.setWidth(210);
        mResDraggableSignature.setHeight(90);

        mChmDraggableSignature.setX(60);
        mChmDraggableSignature.setY(400);
        mChmDraggableSignature.setWidth(210);
        mChmDraggableSignature.setHeight(90);
    }

    private void saveReport() {
        // If the barangay ID contains a resident signature, then store its coordinates and dimension to mBarangayID.
        if (mBarangayID.getResidentSignature() != null) {
            double[] signatureDimension = new double[]{
                    mResDraggableSignature.getX(),
                    mResDraggableSignature.getY(),
                    mResDraggableSignature.getWidth(),
                    mResDraggableSignature.getHeight()};

            mBarangayID.setResidentSignatureDimension(signatureDimension);
        }

        // If the barangay IC contains a chairman signature (which always does), then store its coordinates and
        // dimension to mBarangayID.
        double[] signatureDimension = new double[]{
                mChmDraggableSignature.getX(),
                mChmDraggableSignature.getY(),
                mChmDraggableSignature.getWidth(),
                mChmDraggableSignature.getHeight()};

        mBarangayID.setChmSignatureDimension(signatureDimension);
    }

    /**
     *
     * @return true if printing is successful. Otherwise, return false.
     */
    private boolean printReport() {
        // Hide the draggable rectangles to make sure that they're not printed.
        mResDraggableSignature.setVisible(false);
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
                ImageView snapShot = new ImageView();
                snapShot.setImage(mBarangayIDPane.snapshot(null, null));

                snapShot.getTransforms().add(new Scale(0.5, 0.5));

                job.endJob();
            }

            return result;
        }

        return false;
    }
}
