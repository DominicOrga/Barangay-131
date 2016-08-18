package javah.controller.information.barangay_id;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javah.container.BarangayID;
import javah.util.DraggableSignature;

import java.text.SimpleDateFormat;

public class BarangayIDReportControl {

    public interface OnBarangayIDReportListener {
        void onCancelButtonClicked();
        void onSaveButtonClicked(BarangayID barangayID);
        void onPrintButtonClicked();
    }

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

    @FXML private Button mPrintAndSaveButton, mPrintButton, mSaveButton, mCancelButton;

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
    }

    @FXML
    public void onPrintAndSaveButtonClicked(ActionEvent actionEvent) {

    }

    @FXML
    public void onPrintButtonClicked(ActionEvent actionEvent) {

    }

    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {

    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
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
        mPhotoView.setImage(
                new Image(mBarangayID.getPhoto() != null ?
                        "file:" + mBarangayID.getPhoto() : "/res/ic_default_resident_white_bg.png"));

        // Set the applicant name and barangay ID code.
        mBarangayIDCode.setText(mBarangayID.getID());
        mResidentNameLabel.setText(mBarangayID.getResidentName().toUpperCase());

        // Set the applicant signature, if any.
        if (mBarangayID.getResidentSignature() != null) {
            mResSignatureView.setImage(new Image("file:" + mBarangayID.getResidentSignature()));
            mResDraggableSignature.setVisible(true);
        }

        if (mBarangayID.getResidentSignatureDimension() != null) {
            Double[] dimension = mBarangayID.getResidentSignatureDimension();

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
            Double[] dimension = mBarangayID.getChmSignatureDimension();

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

        mPhotoView.setImage(null);

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
}
