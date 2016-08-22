package javah.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class ResidentDeletionControl {

    @FXML
    private Label mNameLabel;

    private OnResidentDeletionListener mListener;

    public interface OnResidentDeletionListener {
        void onCancelButtonClicked();
        void onDeleteButtonClicked();
    }

    public void setListener(OnResidentDeletionListener listener) {
        mListener = listener;
    }

    public void setNameLabel(String name) {
        mNameLabel.setText("Remove " + name + "?");
    }

    @FXML
    public void onConfirmButtonClicked() {
        mListener.onDeleteButtonClicked();
    }

    @FXML
    public void onCancelButtonClicked() {
        mListener.onCancelButtonClicked();
    }
}
