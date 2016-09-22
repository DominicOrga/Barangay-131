package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javah.Main;
import javah.container.BusinessClearance;
import javah.contract.PreferenceContract;
import javah.model.PreferenceModel;
import javah.util.BarangayUtils;
import javah.util.DraggableSignature;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A class that handles the business clearance report.
 */
public class BusinessClearanceReportControl {

    /**
     * A listener for the BusinessClearanceReportControl. It listens for the cancel and
     * save button of the said controller. The listener is none other than the
     * MainControl.
     *
     * @see BusinessClearanceReportControl
     */
    public interface OnBusinessClearanceReportListener {
        /**
         * Tell the listener to close the business clearance report dialog.
         */
        void onCancelButtonClicked();

        /**
         * Tell the listener to pass the business clearance to the InformationControl
         * to be stored in the database. Also, close the business clearance report
         * dialog.
         *
         * @param businessClearance
         *        The business clearance to be stored in the database.
         *
         * @see InformationControl
         */
        void onSaveButtonClicked(BusinessClearance businessClearance);
    }

    /* Possible requests that can be made to determine the state of this controller. */
    public static final byte
            REQUEST_CREATE_REPORT = 1,
            REQUEST_DISPLAY_REPORT = 2,
            REQUEST_SNAPSHOT_REPORT = 3;

    /**
     * A node that serves as the root node of the business clearance report scene.
     * Disabled when the process dialog is open.
     */
    @FXML
    Pane mRootPane;

    /* The unique ID of the specified business clearance. */
    @FXML
    Label mID;

    /**
     * A pane representing the whole business clearance. During printing, this pane is
     * re-scaled first before being printed.
     */
    @FXML Pane mDocumentPane;

    /**
     * The line breaks used to seperate the paragraphs. Unfortunately, \n can only work
     * programmatically.
     */
    @FXML Text mLineBreak1, mLineBreak2;

    /* Components representing the data of the business clearace. */
    @FXML Text mBusiName, mBusiOwners, mBusiAddress, mBusiType, mBusiClient, mDateValid;

    /**
     * Image views holding the chairman and secretary signatures of the specified
     * business clearance.
     */
    @FXML ImageView mChmSignature, mSecSignature;

    /* Texts representing the printed name of the chairman and secretary. */
    @FXML Text mChmPrintedName, mSecPrintedName;

    /**
     * Note: used for REQUEST_DISPLAY_REPORT
     *
     * A button to print the specified business clearance.
     */
    @FXML
    Button mPrintButton;

    /**
     * Note: used for REQUEST_CREATE_REPORT
     *
     * Buttons to either save or print the business clearance and save it.
     */
    @FXML Button mPrintAndSaveButton, mSaveButton;

    /**
     * A scroll pane to traverse around the business clearance information.
     * The horizontal bar is set to invisible, since it won't be used.
     */
    @FXML
    ScrollPane mScrollPane;

    /**
     * A reference to the universal preference model, used for getting the barangay
     * official names and default coordinates of the signatures.
     */
    private PreferenceModel mPrefModel;

    /* A reference to the business clearance itself. */
    private BusinessClearance mBusinessClearance;

    /* Transforms the chairman and secretary signature into a draggable image views. */
    private DraggableSignature mChmDraggableSignature, mSecDraggableSignature;

    /* The listener for this controller. Listens for any click button events. */
    private OnBusinessClearanceReportListener mListener;

    /**
     * initialize the components needed for this controller.
     */
    @FXML
    private void initialize() {
        // Disable horizontal scrolling of scroll pane.
        mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mChmDraggableSignature = new DraggableSignature(mChmSignature);
        mSecDraggableSignature = new DraggableSignature(mSecSignature);

        mChmDraggableSignature.setStroke(Color.BLACK);
        mSecDraggableSignature.setStroke(Color.BLACK);

        // This is needed since \n only works programmatically.
        mLineBreak1.setText("\n\n");
        mLineBreak2.setText("\n\n");
    }

    /**
     * Print and tell the information control to save the report. If the printing is
     * successful, then try to save the report and close the report. Otherwise, nothing
     * happens.
     *
     * @param actionEvent
     *        The action event. No usage.
     *
     * @see InformationControl
     */
    @FXML
    public void onPrintAndSaveButtonClicked(ActionEvent actionEvent) {
        if (printReport()) {
            saveSignatureDimensions();
            mListener.onSaveButtonClicked(mBusinessClearance);
        }
    }

    /**
     * Print the and close the report. If printing is unsuccessful, the do nothing.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onPrintButtonClicked(ActionEvent actionEvent) {
        if (printReport())
            mListener.onCancelButtonClicked();
    }

    /**
     * Tell the information control to save the report.
     *
     * @param actionEvent
     *        The action event. Never used.
     *
     * @see InformationControl
     */
    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {
        saveSignatureDimensions();
        mListener.onSaveButtonClicked(mBusinessClearance);
    }

    /**
     * Close the report.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
    }

    /**
     * Store the chairman and secretary signature coordinates and dimension to the
     * business clearance and the preference model.
     */
    private void saveSignatureDimensions() {
        // If the barangay clearance contains a chairman signature (which always does),
        // then store its coordinates and dimension to mBarangayClearance.
        double[] signatureDimension = new double[]{
                mChmDraggableSignature.getX(),
                mChmDraggableSignature.getY(),
                mChmDraggableSignature.getWidth(),
                mChmDraggableSignature.getHeight()};

        mBusinessClearance.setChmSignatureDimension(signatureDimension);

        // Save the dimension of the chairman signature to the preference model.
        mPrefModel.put(PreferenceContract.BUSI_CLEARANCE_CHM_SIGNATURE_DIMENSION,
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

        mBusinessClearance.setSecSignatureDimension(signatureDimension);

        // Save the dimension of the secretary signature to the preference model.
        mPrefModel.put(PreferenceContract.BUSI_CLEARANCE_SEC_SIGNATURE_DIMENSION,
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
     * Try to print the report.
     *
     * @return true if printing was successful. Otherwise, return false.
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
            boolean result = job.showPrintDialog(Main.getPrimaryStage()); // Window must be your main Stage
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
     * Set the listener for this controller.
     *
     * @param listener
     *        The listener for this controller.
     */
    public void setListener(OnBusinessClearanceReportListener listener) {
        mListener = listener;
    }

    /**
     * Pass the universal preference model to this controller. It will be used to
     * gather barangay official data required for the report creation.
     *
     * @param prefModel
     *        The universal preference model.
     */
    public void setPreferenceModel(PreferenceModel prefModel) {
        mPrefModel = prefModel;
    }

    /**
     * Populate the report with the barangay clearance data. Furthermore, update the
     * scene depending on the request. Also, return a snapshot of the business
     * clearance if requested.
     *
     * @param businessClearance
     *        The business clearance to be displayed.
     */
    public Image setBusinessClearance(BusinessClearance businessClearance, byte request) {
        mBusinessClearance = businessClearance;
        mChmSignature.setImage(null);
        mSecSignature.setImage(null);

        // Update the UI of the report based on the request. Also, of the request is report creation,
        // then get the required data from the Preference Model.
        switch (request) {
            case REQUEST_CREATE_REPORT:
                mSecDraggableSignature.setVisible(false);
                mChmDraggableSignature.setVisible(false);

                mPrintAndSaveButton.setVisible(true);
                mPrintAndSaveButton.setManaged(true);
                mSaveButton.setVisible(true);
                mSaveButton.setManaged(true);
                mPrintButton.setVisible(false);

                // Get the required data from the preference model.

                // Set the date issued.
                GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
                mBusinessClearance.setDateIssued(new Timestamp(calendar.getTimeInMillis()));

                // Set the date validity of the barangay clearance.
                // Date validity is equal to (date of creation) + 364 days || 365 day (leap year).
                calendar.add(Calendar.DATE, 364);

                // Add one more day to the calendar if it is a leap year.
                if (calendar.isLeapYear(calendar.get(Calendar.YEAR)))
                    calendar.add(Calendar.DATE, 1);

                mBusinessClearance.setDateValid(new Timestamp(calendar.getTimeInMillis()));

                // Set the chairman name.
                mBusinessClearance.setChmName(BarangayUtils.formatName(
                        mPrefModel.get(PreferenceContract.CHAIRMAN_FIRST_NAME),
                        mPrefModel.get(PreferenceContract.CHAIRMAN_MIDDLE_NAME),
                        mPrefModel.get(PreferenceContract.CHAIRMAN_LAST_NAME),
                        mPrefModel.get(PreferenceContract.CHAIRMAN_AUXILIARY))
                );

                // Set the chairman signature and dimension, if any.
                String signature = mPrefModel.get(PreferenceContract.CHAIRMAN_SIGNATURE_PATH);

                if (signature != null) {
                    mChmDraggableSignature.setVisible(true);
                    mBusinessClearance.setChmSignature(signature);

                    String dimensionStr = mPrefModel.get(PreferenceContract.BUSI_CLEARANCE_CHM_SIGNATURE_DIMENSION);

                    double[] dimension = (dimensionStr != null) ?
                            BarangayUtils.parseSignatureDimension(dimensionStr) : new double[]{475, 810, 210, 90};

                    mBusinessClearance.setChmSignatureDimension(dimension);
                }

                // Set the secretary name.
                mBusinessClearance.setSecName(BarangayUtils.formatName(
                        mPrefModel.get(PreferenceContract.SECRETARY_FIRST_NAME),
                        mPrefModel.get(PreferenceContract.SECRETARY_MIDDLE_NAME),
                        mPrefModel.get(PreferenceContract.SECRETARY_LAST_NAME),
                        mPrefModel.get(PreferenceContract.SECRETARY_AUXILIARY))
                );

                // Set the secretary signature and dimension, if any.
                signature = mPrefModel.get(PreferenceContract.SECRETARY_SIGNATURE_PATH);

                if (signature != null) {
                    mSecDraggableSignature.setVisible(true);
                    mBusinessClearance.setSecSignature(signature);

                    String dimensionStr = mPrefModel.get(PreferenceContract.BUSI_CLEARANCE_SEC_SIGNATURE_DIMENSION);

                    double[] dimension = (dimensionStr != null) ?
                            BarangayUtils.parseSignatureDimension(dimensionStr) : new double[]{25, 810, 210, 90};

                    mBusinessClearance.setSecSignatureDimension(dimension);
                }

                break;

            default:
                mSecDraggableSignature.setVisible(false);
                mChmDraggableSignature.setVisible(false);

                mPrintAndSaveButton.setVisible(false);
                mPrintAndSaveButton.setManaged(false);
                mSaveButton.setVisible(false);
                mSaveButton.setManaged(false);
                mPrintButton.setVisible(true);
        }

        // Start populating the report with data.

        mBusiName.setText(mBusinessClearance.getBusinessName().toUpperCase());
        mBusiOwners.setText(mBusinessClearance.getOwners().toUpperCase());
        mBusiType.setText(mBusinessClearance.getBusinessType().toUpperCase());
        mBusiAddress.setText(mBusinessClearance.getBusinessAddress());
        mBusiClient.setText(mBusinessClearance.getClient().toUpperCase());

        mID.setText("131-" + mBusinessClearance.getID());

        mSecPrintedName.setText(mBusinessClearance.getSecName().toUpperCase());
        mChmPrintedName.setText("Hon. " + mBusinessClearance.getChmName().toUpperCase());

        // Show the chairman signature, if any.
        if (mBusinessClearance.getChmSignature() != null) {
            mChmSignature.setImage(new Image("file:" + mBusinessClearance.getChmSignature()));

            double[] dimension = mBusinessClearance.getChmSignatureDimension();

            mChmDraggableSignature.setX(dimension[0]);
            mChmDraggableSignature.setY(dimension[1]);
            mChmDraggableSignature.setWidth(dimension[2]);
            mChmDraggableSignature.setHeight(dimension[3]);
        }

        // Show the secretary signature, if any.
        if (mBusinessClearance.getSecSignature() != null) {
            mSecSignature.setImage(new Image("file:" + mBusinessClearance.getSecSignature()));

            double[] dimension = mBusinessClearance.getSecSignatureDimension();

            mSecDraggableSignature.setX(dimension[0]);
            mSecDraggableSignature.setY(dimension[1]);
            mSecDraggableSignature.setWidth(dimension[2]);
            mSecDraggableSignature.setHeight(dimension[3]);
        }

        // Set date validity.
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");

        mDateValid.setText(String.format("(Valid until %s)", dateFormat.format(mBusinessClearance.getDateValid())));

        return request == REQUEST_SNAPSHOT_REPORT ? mDocumentPane.snapshot(null, null) : null;
    }
}
