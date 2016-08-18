package javah.controller.information.barangay_id;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javah.container.BarangayID;

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

    private BarangayID mBarangayID;

    private OnBarangayIDReportListener mListener;

    @FXML
    private void initialize() {

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
    public void setBarangayID(BarangayID barangayID) {
        mBarangayID = barangayID;

        // If on report creation state, then display the 'print & save' and 'print' buttons.
        if (mBarangayID.getID() == null) {
            mPrintButton.setVisible(false);

            mPrintAndSaveButton.setVisible(true);
            mPrintAndSaveButton.setManaged(true);
            mSaveButton.setVisible(true);
            mSaveButton.setManaged(true);
        }

        // Set the image of the barangay ID, if any.
        mPhotoView.setImage(
                new Image(mBarangayID.getPhoto() != null ?
                        "file:" + mBarangayID.getPhoto() : "/res/ic_default_resident_white_bg.png"));

        // Set the applicant name and barangay ID code.
        mBarangayIDCode.setText(mBarangayID.getID());
        mResidentNameLabel.setText(mBarangayID.getResidentName().toUpperCase());

        // Set the applicant signature, if any.
        if (mBarangayID.getResidentSignature() != null)
            mResSignatureView.setImage(new Image("file:" + mBarangayID.getResidentSignature()));

        if (mBarangayID.getResidentSignatureDimension() != null) {
            Double[] dimension = mBarangayID.getResidentSignatureDimension();

            mResSignatureView.setTranslateX(dimension[0]);
            mResSignatureView.setTranslateY(dimension[1]);
            mResSignatureView.setFitWidth(dimension[2]);
            mResSignatureView.setFitHeight(dimension[3]);
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

            mChmSignatureView.setTranslateX(dimension[0]);
            mChmSignatureView.setTranslateY(dimension[1]);
            mChmSignatureView.setFitWidth(dimension[2]);
            mChmSignatureView.setFitHeight(dimension[3]);
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

        // Place the resident and chairman signature back to its default coordinate and dimension.
        // Note that mChmSignatureView always has an image.
        mResSignatureView.setImage(null);
        mResSignatureView.setTranslateX(0);
        mResSignatureView.setTranslateY(0);
        mResSignatureView.setFitWidth(210);
        mResSignatureView.setFitHeight(90);

        mChmSignatureView.setTranslateX(0);
        mChmSignatureView.setTranslateX(0);
        mChmSignatureView.setFitWidth(210);
        mChmSignatureView.setFitHeight(90);
    }
}
