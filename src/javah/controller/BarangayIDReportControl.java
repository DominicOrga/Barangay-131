package javah.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
 * the viewing of already created reports. Has the functionality the
 * barangay ID reports.
 */
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

    @FXML private Label mID;
    @FXML private Text mAddress;
    @FXML private Text mDateIssued, mDateValid;

    /**
     * The views for the resident.
     */
    @FXML private Text mResName;
    @FXML private ImageView mResPhoto;
    @FXML private ImageView mResSignature;

    @FXML private JFXCheckBox mMirrorIDCheckBox;

    /**
     * The views for the chairman.
     */
    @FXML private Text mChmName;
    @FXML private ImageView mChmSignature;

    @FXML private Button mPrintAndSaveButton, mPrintButton, mSaveButton;

    public static byte REQUEST_CREATE_REPORT = 1, REQUEST_DISPLAY_REPORT = 2;

    private PreferenceModel mPrefModel;

    private BarangayID mBarangayID;

    private OnBarangayIDReportListener mListener;

    /**
     * Modifies the Signature views into draggable views.
     */
    private DraggableSignature mChmDraggableSignature;
    private DraggableSignature mResDraggableSignature;

    @FXML
    private void initialize() {
        mResDraggableSignature = new DraggableSignature(mResSignature);
        mChmDraggableSignature = new DraggableSignature(mChmSignature);

        mResDraggableSignature.setStroke(Color.BLACK);
        mChmDraggableSignature.setStroke(Color.BLACK);

        mMirrorIDCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                mBarangayIDPane.setRotate(newValue ? 180 : 0));
    }

    @FXML
    public void onPrintAndSaveButtonClicked(ActionEvent actionEvent) {
        if (printReport()) {
            saveSignatureDimensions();
            mListener.onSaveButtonClicked(mBarangayID);
            mMirrorIDCheckBox.setSelected(false);
        }
    }

    /**
     * Print the report.
     * @param actionEvent
     */
    @FXML
    public void onPrintButtonClicked(ActionEvent actionEvent) {
        printReport();
        mMirrorIDCheckBox.setSelected(false);
    }

    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {
        saveSignatureDimensions();
        mListener.onSaveButtonClicked(mBarangayID);
        mMirrorIDCheckBox.setSelected(false);
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
        mMirrorIDCheckBox.setSelected(false);
    }

    public void setPreferenceModel(PreferenceModel prefModel) {
        mPrefModel = prefModel;
    }

    /**
     * Pass the barangay ID to be displayed in the report.
     * Determine the state of the scene, whether to create or to simply display the data.
     * Update the user interface based on the type of report.
     *
     * @param barangayID contains the data to be displayed in the report.
     *                   If the barangayID does not have a unique ID, then state of scene is set to barangay ID creation.
     *                   Else, barangayID is already created and the state is simply to display the barangay ID.
     */
    public void setBarangayID(BarangayID barangayID, byte request) {
        // Reset the UI, first.
        mResSignature.setImage(null);
        mChmSignature.setImage(null);

        mBarangayID = barangayID;


        // If on report creation state, then display the 'print & save' and 'print' buttons.
        if (request == REQUEST_CREATE_REPORT) {
            mPrintButton.setVisible(false);
            mResDraggableSignature.setVisible(false);
            mChmDraggableSignature.setVisible(false);

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
                mBarangayID.setChmSignature(chmSignature);

                String dimensionStr = mPrefModel.get(PreferenceContract.BRGY_ID_CHM_SIGNATURE_DIMENSION);

                double[] dimension = (dimensionStr != null) ?
                        BarangayUtils.parseSignatureDimension(dimensionStr) : new double[]{60, 400, 210, 90};

                mChmDraggableSignature.setX(dimension[0]);
                mChmDraggableSignature.setY(dimension[1]);
                mChmDraggableSignature.setWidth(dimension[2]);
                mChmDraggableSignature.setHeight(dimension[3]);

                mBarangayID.setChmSignatureDimension(dimension);
            }

            // The date issued will be the current date.
            GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
            mBarangayID.setDateIssued(new Timestamp(calendar.getTimeInMillis()));

            // Set the date validity of the barangay ID.
            // Date validity is equal to (date of creation) + (1 year)||(365 days) - (1 day).
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

        // Set the image of the barangay ID, if any.
        mResPhoto.setImage(mBarangayID.getPhoto() != null ? new Image("file:" + mBarangayID.getPhoto()) : null);

        // Set the applicant name and barangay ID code.
        mID.setText(mBarangayID.getID());
        mResName.setText(mBarangayID.getResidentName().toUpperCase());

        // Set the applicant signature, if any.
        if (mBarangayID.getResidentSignature() != null) {
            mResSignature.setImage(new Image("file:" + mBarangayID.getResidentSignature()));

            mResDraggableSignature.setVisible(request == REQUEST_CREATE_REPORT);

            if (mBarangayID.getResidentSignatureDimension() != null) {
                double[] dimension = mBarangayID.getResidentSignatureDimension();

                mResDraggableSignature.setX(dimension[0]);
                mResDraggableSignature.setY(dimension[1]);
                mResDraggableSignature.setWidth(dimension[2]);
                mResDraggableSignature.setHeight(dimension[3]);
            } else {
                mResDraggableSignature.setX(60);
                mResDraggableSignature.setY(350);
                mResDraggableSignature.setWidth(210);
                mResDraggableSignature.setHeight(90);
            }
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
            mChmDraggableSignature.setVisible(request == REQUEST_CREATE_REPORT);

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
}
