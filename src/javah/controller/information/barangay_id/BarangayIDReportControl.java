package javah.controller.information.barangay_id;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class BarangayIDReportControl {

    @FXML private ImageView mResSignatureView;
    @FXML private ImageView mChmSignatureView;
    @FXML private TextArea mAddressTextArea;

    @FXML
    private void initialize() {
        mResSignatureView.setImage(new Image("file:" + System.getenv("PUBLIC") + "/Barangay131/Photos/0ac3bf17-954d-4dd2-a163-51408e8f0ba9.png"));
    }
}
