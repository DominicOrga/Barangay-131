package javah.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javah.Main;
import javah.container.BarangayID;
import javah.contract.PreferenceContract;
import javah.model.PreferenceModel;
import javah.util.BarangayUtils;
import javah.util.DraggableSignature;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A class that handles the generation of the Barangay ID reports and
 * the viewing of already created reports.
 */
public class BarangayIDReportControl {

    /**
     * An interface that serves as a listener to the BarangayIDReportControl.
     *
     * @see BarangayIDReportControl
     */
    public interface OnBarangayIDReportListener {
        /**
         * Hide the Barangay ID report.
         */
        void onCancelButtonClicked();

        /**
         * Pass the generated barangay ID to the Information Control to be permanently
         * stored in the database.
         *
         * @param barangayID
         *        The barangay ID to be saved.
         *
         * @see InformationControl
         */
        void onSaveButtonClicked(BarangayID barangayID);
    }
    /* Determines specified usage of the report. */
    public static byte REQUEST_CREATE_REPORT = 1, REQUEST_DISPLAY_REPORT = 2, REQUEST_SNAPSHOT_REPORT = 3;

    /* Disables the barangay ID report while printing initialization is in process. */
    @FXML private Pane mRootPane;

    /* The Pane containing the barangay ID. */
    @FXML private Pane mBarangayIDPane;

    /**
     * NOTE: used for REQUEST_SNAPSHOT_REPORT
     *
     * A node of the mBarangayIDPane which takes hold of the front part of the
     * barangay ID.
     */
    @FXML private Pane mIDFront;

    /* A label displaying the unique ID of the barangay ID. */
    @FXML private Label mID;

    /* A text displaying the address registered in the barangay ID. */
    @FXML private Text mAddress;

    /* Texts displaying the date issuance and date validity of the barangay ID. */
    @FXML private Text mDateIssued, mDateValid;

    /* A text representing the resident's name. */
    @FXML private Text mResName;

    /* An image view holding the photo of the resident. */
    @FXML private ImageView mResPhoto;

    /* An image view holding the signature of the resident, if any. */
    @FXML private ImageView mResSignature;

    /* A text representing the chairman's name. */
    @FXML private Text mChmName;

    /* An image view holding the signature of the chairman. */
    @FXML private ImageView mChmSignature;

    /**
     * A checkbox to mirror the barangay ID, since our client requested
     * this feature.
     */
    @FXML private JFXCheckBox mMirrorIDCheckBox;

    /**
     * Note: used for REQUEST_DISPLAY_REPORT
     *
     * A button to print the report.
     */
    @FXML private Button mPrintButton;

    /**
     * Note: used for REQUEST_CREATE_REPORT
     *
     * A button to save the report.
     */
    @FXML private Button mSaveButton;

    /**
     * Note: used for REQUEST_CREATE_REPORT
     *
     * A button to print and save the report.
     */
    @FXML private Button mPrintAndSaveButton;

    /**
     * Note: used for REQUEST_CREATE_REPORT
     *
     * A reference to the universal Preference model. Acquires the information
     * regarding the barangay official's data needed for populating the required data
     * of the report.
     */
    private PreferenceModel mPrefModel;

    /* A container for this barangay ID's data. */
    private BarangayID mBarangayID;

    /* A listener for this controller. */
    private OnBarangayIDReportListener mListener;

    /* Enhances the signature views into resizable and draggable views. */
    private DraggableSignature mChmDraggableSignature, mResDraggableSignature;

    /**
     * A constructor that initializes the components of this controller.
     */
    @FXML
    private void initialize() {
        mResDraggableSignature = new DraggableSignature(mResSignature);
        mChmDraggableSignature = new DraggableSignature(mChmSignature);

        mResDraggableSignature.setStroke(Color.BLACK);
        mChmDraggableSignature.setStroke(Color.BLACK);

        // Flip the image if the mMirrorIDCheckBox is selected.
        mMirrorIDCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                mBarangayIDPane.setRotate(newValue ? 180 : 0));
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
            mMirrorIDCheckBox.setSelected(false);
            mListener.onSaveButtonClicked(mBarangayID);
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
        if (printReport()) {
            mMirrorIDCheckBox.setSelected(false);
            mListener.onCancelButtonClicked();
        }
    }

    /**
     * Tell the information control to save the report.
     *
     * @param actionEvent
     *        The action event. Never used.
     *
     * @see MainControl
     */
    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {
        saveSignatureDimensions();
        mMirrorIDCheckBox.setSelected(false);
        mListener.onSaveButtonClicked(mBarangayID);
    }

    /**
     * Close the report.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mMirrorIDCheckBox.setSelected(false);
        mListener.onCancelButtonClicked();

    }

    /**
     * Store the resident signature coordinates and dimension to the barangay ID and
     * store the chairman signature coordinates and dimension to the preference model.
     */
    private void saveSignatureDimensions() {
        // If the barangay IC contains a chairman signature (which always does), then store its coordinates and
        // dimension to mBarangayID.
        double[] signatureDimension = new double[]{
                mChmDraggableSignature.getX(),
                mChmDraggableSignature.getY(),
                mChmDraggableSignature.getWidth(),
                mChmDraggableSignature.getHeight()};

        mBarangayID.setChmSignatureDimension(signatureDimension);

        // Save the dimension of the chairman signature to the preference model.
        mPrefModel.put(PreferenceContract.BRGY_ID_CHM_SIGNATURE_DIMENSION,
                String.format("%.5f %.5f %.5f %.5f",
                        mChmDraggableSignature.getX(),
                        mChmDraggableSignature.getY(),
                        mChmDraggableSignature.getWidth(),
                        mChmDraggableSignature.getHeight()
                )
        );

        mPrefModel.save();

        double[] resSignatureDimension = new double[] {
                mResDraggableSignature.getX(),
                mResDraggableSignature.getY(),
                mResDraggableSignature.getWidth(),
                mResDraggableSignature.getHeight()
        };

        mBarangayID.setResidentSignatureDimension(resSignatureDimension);

    }

    /**
     * Try to print the report.
     *
     * @return true if printing was successful. Otherwise, return false.
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
            boolean result = job.showPrintDialog(Main.PRIMARY_STAGE); // Window must be your main Stage
            mRootPane.setDisable(false);

            // If the client cancels the printing, then no printing will occur.
            if (result) {
                // Determine the scale value needed to fit mBarangayIDPane in the paper.
                Scale tempScale = new Scale(0.5, 0.5);

                // Temporarily apply the scale value to mDocumentPane. Reset scaleback to normal after printing.
                mBarangayIDPane.getTransforms().add(tempScale);

                job.printPage(mBarangayIDPane);
                job.endJob();

                mBarangayIDPane.getTransforms().remove(tempScale);
            }

            return result;
        }

        return false;
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
     * Set the listener for this controller.
     *
     * @param listener
     *        The listener for this controller.
     */
    public void setListener(OnBarangayIDReportListener listener) {
        mListener = listener;
    }

    /**
     * Pass the barangay ID to be displayed in the report.
     * Determine and update the state of the scene, whether to create or to simply
     * display the data.
     *
     * @param barangayID
     *        Contains the data to be displayed in the report.
     * @param request
     *        The request to whether create a report or display a created report.
     */
    public Image setBarangayID(BarangayID barangayID, byte request) {
        // Reset the UI, first.
        mResSignature.setImage(null);
        mChmSignature.setImage(null);

        mBarangayID = barangayID;

        // If on report creation, then update the UI and populate the barangay ID with
        // the required data from the preference model before displaying the barangay ID.
        if (request == REQUEST_CREATE_REPORT) {
            mResDraggableSignature.setVisible(false);
            mChmDraggableSignature.setVisible(false);

            mPrintButton.setVisible(false);
            mPrintAndSaveButton.setVisible(true);
            mPrintAndSaveButton.setManaged(true);
            mSaveButton.setVisible(true);
            mSaveButton.setManaged(true);

            // Set the chairman data. If the report has not been created, then get the data
            // from the preference and pass it to the Barangay ID. Otherwise, simply display
            // they chairman data.
            String chmName = BarangayUtils.formatName(
                    mPrefModel.get(PreferenceContract.CHAIRMAN_FIRST_NAME),
                    mPrefModel.get(PreferenceContract.CHAIRMAN_MIDDLE_NAME),
                    mPrefModel.get(PreferenceContract.CHAIRMAN_LAST_NAME),
                    mPrefModel.get(PreferenceContract.CHAIRMAN_AUXILIARY)
            );

            mBarangayID.setChmName(chmName);

            String chmSignature = mPrefModel.get(PreferenceContract.CHAIRMAN_SIGNATURE_PATH);

            if (chmSignature != null) {
                mChmDraggableSignature.setVisible(true);

                mBarangayID.setChmSignature(chmSignature);

                String dimensionStr = mPrefModel.get(PreferenceContract.BRGY_ID_CHM_SIGNATURE_DIMENSION);

                double[] dimension = (dimensionStr != null) ?
                        BarangayUtils.parseSignatureDimension(dimensionStr) : new double[]{60, 400, 210, 90};

                mBarangayID.setChmSignatureDimension(dimension);
            }

            if (mBarangayID.getResidentSignature() != null) {
                mResDraggableSignature.setVisible(true);

                double[] dimension = (mBarangayID.getResidentSignatureDimension() != null) ?
                        mBarangayID.getResidentSignatureDimension() : new double[]{60, 350, 210, 90};

                mBarangayID.setResidentSignatureDimension(dimension);
            }

            // The date issued will be the current date.
            GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
            mBarangayID.setDateIssued(new Timestamp(calendar.getTimeInMillis()));

            // Set the date validity of the barangay ID.
            // Date validity is equal to (date of creation) + 364 days || 365 day (leap year).
            calendar.add(Calendar.DATE, 364);

            // Add one more day to the calendar if it is a leap year.
            if (calendar.isLeapYear(calendar.get(Calendar.YEAR)))
                calendar.add(Calendar.DATE, 1);

            mBarangayID.setDateValid(new Timestamp(calendar.getTimeInMillis()));

        } else {
            mResDraggableSignature.setVisible(false);
            mChmDraggableSignature.setVisible(false);

            mPrintAndSaveButton.setVisible(false);
            mPrintAndSaveButton.setManaged(false);
            mSaveButton.setVisible(false);
            mSaveButton.setManaged(false);
            mPrintButton.setVisible(true);
        }

        // Start populating the report.

        // Set the image of the barangay ID, if any.
        mResPhoto.setImage(mBarangayID.getPhoto() != null ? new Image("file:" + mBarangayID.getPhoto()) : null);

        // Set the applicant name and barangay ID code.
        mID.setText(mBarangayID.getID());
        mResName.setText(mBarangayID.getResidentName().toUpperCase());

        // Set the applicant signature, if any.
        if (mBarangayID.getResidentSignature() != null) {
            mResSignature.setImage(new Image("file:" + mBarangayID.getResidentSignature()));

            double[] dimension = mBarangayID.getResidentSignatureDimension();

            mResDraggableSignature.setX(dimension[0]);
            mResDraggableSignature.setY(dimension[1]);
            mResDraggableSignature.setWidth(dimension[2]);
            mResDraggableSignature.setHeight(dimension[3]);
        }

        // Set the applicant address.
        mAddress.setText(mBarangayID.getAddress());

        mChmName.setText("Hon." + mBarangayID.getChmName().toUpperCase());

        // Display the date issuance and validity.
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd yyyy");
        mDateIssued.setText(dateFormat.format(mBarangayID.getDateIssued()));
        mDateValid.setText(dateFormat.format(mBarangayID.getDateValid()));

        if (mBarangayID.getChmSignature() != null) {
            mChmSignature.setImage(new Image("file:" + mBarangayID.getChmSignature()));

            double[] dimension = mBarangayID.getChmSignatureDimension();

            mChmDraggableSignature.setX(dimension[0]);
            mChmDraggableSignature.setY(dimension[1]);
            mChmDraggableSignature.setWidth(dimension[2]);
            mChmDraggableSignature.setHeight(dimension[3]);
        }

        // If the request is simply to take a snapshot of the report, then let them have it.
        return request == REQUEST_SNAPSHOT_REPORT ? mIDFront.snapshot(null, null) : null;
    }
}
