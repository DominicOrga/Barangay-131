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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    private Text[] mKagawads;

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

        mKagawads = new Text[]{mKagawad1, mKagawad2, mKagawad3, mKagawad4, mKagawad5, mKagawad6, mKagawad7};
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
        mBrgyClearanceIDLabel.setText("131-" + mBarangayClearance.getID());

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
            mKagawads[i].setText(mBarangayClearance.getKagawadName(i));
            mKagawads[i].setManaged(mKagawads[i].getText() != null && !mKagawads[i].getText().trim().isEmpty());

            System.out.println(mKagawads[i].isManaged());
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

        return request == REQUEST_SNAPSHOT_REPORT ? mDocumentPane.snapshot(null, null) : null;
    }

    public void setListener(OnBarangayClearanceReportListener listener) {
        mListener = listener;
    }

    public void setPreferenceModel(PreferenceModel prefModel) {
        mPrefModel = prefModel;
    }
}
