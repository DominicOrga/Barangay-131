package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.*;
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
import javah.container.BarangayClearance;
import javah.contract.PreferenceContract;
import javah.model.PreferenceModel;
import javah.util.BarangayUtils;
import javah.util.DraggableSignature;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A controller for the barangay clearance report. It has the functionality to
 * display, print and save a report.
 */
public class BarangayClearanceReportControl {

    /**
     * A listener for the BarangayClearanceReportControl. It listens for the cancel and
     * save button of the said controller. The listener is none other than the
     * MainControl.
     *
     * @see BarangayClearanceReportControl
     */
    public interface OnBarangayClearanceReportListener {
        /**
         * Tell the listener to close the barangay clearance report dialog.
         */
        void onCancelButtonClicked();

        /**
         * Tell the listener to pass the barangay clearance to the InformationControl
         * to be stored in the database. Also, close the barangay clearance report
         * dialog.
         *
         * @param barangayClearance
         *        The barangay clearance to be stored in the database.
         *
         * @see InformationControl
         */
        void onSaveButtonClicked(BarangayClearance barangayClearance);
    }

    /* Possible requests that can be made to determine the state of this controller. */
    public static final byte
            REQUEST_CREATE_REPORT = 1,
            REQUEST_DISPLAY_REPORT = 2,
            REQUEST_SNAPSHOT_REPORT = 3;

    /**
     * A node that serves as the root node of the barangay clearance report scene.
     * Disabled when the process dialog is open.
     */
    @FXML Pane mRootPane;

    /* Holds the chairman photo. */
    @FXML ImageView mChmPhoto;

    /* Barangay agent name holders just below the chairman photo. */
    @FXML Text mChmName;
    @FXML Text mKagawad1, mKagawad2, mKagawad3, mKagawad4, mKagawad5, mKagawad6, mKagawad7;
    @FXML Text mTrsrName;
    @FXML Text mSecName;

    /* The unique ID of the specified barangay clearance. */
    @FXML Label mID;

    /**
     * A pane representing the whole barangay clearance. During printing, this pane is
     * re-scaled first before being printed.
     */
    @FXML Pane mDocumentPane;

    /**
     * The line breaks used to seperate the paragraphs. Unfortunately, \n can only work
     * programmatically.
     */
    @FXML Text mTextLineBreak1, mTextLineBreak2, mTextLineBreak3;

    /* Texts that shows the fundamental data of the barangay clearance */
    @FXML Text mBrgyClearanceName, mBrgyClearanceAddress,
            mTotalYearsOfResidency, mYearOfResidency, mBrgyClearancePurpose,
            mBrgyClearanceDay, mBrgyClearanceDate, mDateValid;;

    /**
     * Image views holding the chairman and secretary signatures of the specified
     * barangay clearance.
     */
    @FXML ImageView mChmSignature, mSecSignature;

    /* Texts representing the printed name of the chairman and secretary. */
    @FXML Text mChmPrintedName, mSecPrintedName;

    /**
     * Note: used for REQUEST_DISPLAY_REPORT
     *
     * A button to print the specified barangay clearance.
     */
    @FXML Button mPrintButton;

    /**
     * Note: used for REQUEST_CREATE_REPORT
     *
     * Buttons to either save or print the barangay clearance and save it.
     */
    @FXML Button mPrintAndSaveButton, mSaveButton;

    /**
     * A scroll pane to traverse around the barangay clearace information.
     * The horizontal bar is set to invisible, since it won't be used.
     */
    @FXML ScrollPane mScrollPane;

    /**
     * A reference to the universal preference model, used for getting the barangay
     * official names and default coordinates of the signatures.
     */
    private PreferenceModel mPrefModel;

    /**
     * A holder for all the kagawad name nodes. Helps in shortening the code when
     * placing the particular names in the right kagawad text nodes.
     */
    private Text[] mKagawads;

    /* A reference to the barangay clearance itself. */
    private BarangayClearance mBarangayClearance;

    /* Transforms the chairman and secretary signature into a draggable image views. */
    private DraggableSignature mChmDraggableSignature, mSecDraggableSignature;

    /* The listener for this controller. Listens for any click button events. */
    private OnBarangayClearanceReportListener mListener;

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
        mTextLineBreak1.setText("\n\n");
        mTextLineBreak2.setText("\n\n");
        mTextLineBreak3.setText(".\n\n");

        mKagawads = new Text[]{mKagawad1, mKagawad2, mKagawad3, mKagawad4, mKagawad5, mKagawad6, mKagawad7};
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
            mListener.onSaveButtonClicked(mBarangayClearance);
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
        mListener.onSaveButtonClicked(mBarangayClearance);
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
     * barangay ID and the preference model.
     */
    private void saveSignatureDimensions() {
        // If the barangay clearance contains a chairman signature (which always does),
        // then store its coordinates and dimension to mBarangayClearance.
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
     * Set the listener for this controller.
     *
     * @param listener
     *        The listener for this controller.
     */
    public void setListener(OnBarangayClearanceReportListener listener) {
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
     * scene depending on the request. Also, return a snapshot of the barangay
     * clearance if requested.
     *
     * @param barangayClearance
     *        The barangay clearance to be displayed.
     */
    public Image setBarangayClearance(BarangayClearance barangayClearance, byte request) {
        mBarangayClearance = barangayClearance;
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
                mBarangayClearance.setDateIssued(new Timestamp(calendar.getTimeInMillis()));

                // Set the date validity of the barangay clearance.
                // Date validity is equal to (date of creation) + 364 days || 365 day (leap year).
                calendar.add(Calendar.DATE, 364);

                // Add one more day to the calendar if it is a leap year.
                if (calendar.isLeapYear(calendar.get(Calendar.YEAR)))
                    calendar.add(Calendar.DATE, 1);

                mBarangayClearance.setDateValid(new Timestamp(calendar.getTimeInMillis()));

                // Set the chairman name.
                mBarangayClearance.setChmName(BarangayUtils.formatName(
                        mPrefModel.get(PreferenceContract.CHAIRMAN_FIRST_NAME),
                        mPrefModel.get(PreferenceContract.CHAIRMAN_MIDDLE_NAME),
                        mPrefModel.get(PreferenceContract.CHAIRMAN_LAST_NAME),
                        mPrefModel.get(PreferenceContract.CHAIRMAN_AUXILIARY))
                );

                // Set the chairman photo.
                mBarangayClearance.setChmPhoto(mPrefModel.get(PreferenceContract.CHAIRMAN_PHOTO_PATH));

                // Set the chairman signature and dimension, if any.
                String signature = mPrefModel.get(PreferenceContract.CHAIRMAN_SIGNATURE_PATH);

                if (signature != null) {
                    mChmDraggableSignature.setVisible(true);
                    mBarangayClearance.setChmSignature(signature);

                    String dimensionStr = mPrefModel.get(PreferenceContract.BRGY_CLEARANCE_CHM_SIGNATURE_DIMENSION);

                    double[] dimension = (dimensionStr != null) ?
                            BarangayUtils.parseSignatureDimension(dimensionStr) : new double[]{465, 815, 210, 90};

                    mBarangayClearance.setChmSignatureDimension(dimension);
                }

                // Set the secretary name.
                mBarangayClearance.setSecName(BarangayUtils.formatName(
                        mPrefModel.get(PreferenceContract.SECRETARY_FIRST_NAME),
                        mPrefModel.get(PreferenceContract.SECRETARY_MIDDLE_NAME),
                        mPrefModel.get(PreferenceContract.SECRETARY_LAST_NAME),
                        mPrefModel.get(PreferenceContract.SECRETARY_AUXILIARY))
                );

                // Set the secretary signature and dimension, if any.
                signature = mPrefModel.get(PreferenceContract.SECRETARY_SIGNATURE_PATH);

                if (signature != null) {
                    mSecDraggableSignature.setVisible(true);
                    mBarangayClearance.setSecSignature(signature);

                    String dimensionStr = mPrefModel.get(PreferenceContract.BRGY_CLEARANCE_SEC_SIGNATURE_DIMENSION);

                    double[] dimension = (dimensionStr != null) ?
                            BarangayUtils.parseSignatureDimension(dimensionStr) : new double[]{230, 815, 210, 90};

                    mBarangayClearance.setSecSignatureDimension(dimension);
                }

                // Set the treasurer name.
                mBarangayClearance.setTreasurerName(BarangayUtils.formatName(
                        mPrefModel.get(PreferenceContract.TREASURER_FIRST_NAME),
                        mPrefModel.get(PreferenceContract.TREASURER_MIDDLE_NAME),
                        mPrefModel.get(PreferenceContract.TREASURER_LAST_NAME),
                        mPrefModel.get(PreferenceContract.TREASURER_AUXILIARY))
                );

                // Set the kagawad names.
                for (int i = 0; i < 7; i++) {
                    mBarangayClearance.setKagawadName(i, BarangayUtils.formatName(
                            mPrefModel.get(PreferenceContract.KAGAWAD_NAMES[i][0]),
                            mPrefModel.get(PreferenceContract.KAGAWAD_NAMES[i][1]),
                            mPrefModel.get(PreferenceContract.KAGAWAD_NAMES[i][2]),
                            mPrefModel.get(PreferenceContract.KAGAWAD_NAMES[i][3]))
                    );
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

        // Show the chairman photo, if any.
        mChmPhoto.setImage(mBarangayClearance.getChmPhoto() != null ?
                new Image("file:" + mBarangayClearance.getChmPhoto()) : null);


        mChmName.setText("Hon. " + mBarangayClearance.getChmName().toUpperCase());
        mSecName.setText(mBarangayClearance.getSecName().toUpperCase());
        mTrsrName.setText(mBarangayClearance.getTreasurerName().toUpperCase());
        mID.setText("131-" + mBarangayClearance.getID());

        mSecPrintedName.setText(mBarangayClearance.getSecName().toUpperCase());
        mChmPrintedName.setText("Hon. " + mBarangayClearance.getChmName().toUpperCase());

        // Show the chairman signature, if any.
        if (mBarangayClearance.getChmSignature() != null) {
            mChmSignature.setImage(new Image("file:" + mBarangayClearance.getChmSignature()));

            double[] dimension = mBarangayClearance.getChmSignatureDimension();

            mChmDraggableSignature.setX(dimension[0]);
            mChmDraggableSignature.setY(dimension[1]);
            mChmDraggableSignature.setWidth(dimension[2]);
            mChmDraggableSignature.setHeight(dimension[3]);
        }

        // Show the secretary signature, if any.
        if (mBarangayClearance.getSecSignature() != null) {
            mSecSignature.setImage(new Image("file:" + mBarangayClearance.getSecSignature()));

            double[] dimension = mBarangayClearance.getSecSignatureDimension();

            mSecDraggableSignature.setX(dimension[0]);
            mSecDraggableSignature.setY(dimension[1]);
            mSecDraggableSignature.setWidth(dimension[2]);
            mSecDraggableSignature.setHeight(dimension[3]);
        }

        // Show the kagawad names.
        for (int i = 0; i < 7; i++) {
            String kagawadName = mBarangayClearance.getKagawadName(i);

            if (kagawadName != null && !kagawadName.isEmpty()) {
                mKagawads[i].setVisible(true);
                mKagawads[i].setManaged(true);
                mKagawads[i].setText(kagawadName.toUpperCase());
            } else {
                mKagawads[i].setVisible(false);
                mKagawads[i].setManaged(false);
                mKagawads[i].setText(null);
            }
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

        // Set date validity.
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");

        mDateValid.setText(String.format("(Valid until %s)", dateFormat.format(mBarangayClearance.getDateValid())));

        return request == REQUEST_SNAPSHOT_REPORT ? mDocumentPane.snapshot(null, null) : null;
    }
}
