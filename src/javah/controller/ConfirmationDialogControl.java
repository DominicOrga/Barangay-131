package javah.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * A class for controlling the confirmation dialog, used to confirm user actions
 * such as deleting a resident or a business, or simply informing the user that
 * the webcam initialization failed when using the Photoshop control.
 *
 * @see PhotoshopControl
 * @see ResidentControl
 * @see BusinessFormControl
 */
public class ConfirmationDialogControl {

    /**
     * An interface that listens to the Confirmation Dialog Control.
     *
     * @see ConfirmationDialogControl
     */
    public interface OnConfirmationDialogListener {
        /**
         * Tell the main control that the confirmation dialog was cancelled.
         *
         * @param client
         *        The client who requested the confirmation dialog.
         */
        void onCancelButtonClicked(byte client);

        /**
         * Tell the main control that the confirmation dialog was confirmed.
         *
         * @param client
         *        The client who requested the confirmation dialog.
         */
        void onConfirmButtonClicked(byte client);
    }

    /* The possible clients of this dialog. */
    public static final byte
            CLIENT_RESIDENT_DELETION = 1,
            CLIENT_BUSINESS_DELETION = 2,
            CLIENT_WEBCAM_FAILURE = 3;

    /* The message of the dialog. */
    @FXML private Text mMessage;

    /**
     * A warning label when deleting a resident or a business. Not visible when showing
     * a confirmation dialog for webcam failure.
     */
    @FXML private Text mWarningText;

    /* A label to show the purpose of the dialog. */
    @FXML private Label mActionLabel;

    /* An image view to represent the purpose of the dialog. */
    @FXML private ImageView mActionIcon;

    /* The cancel button. */
    @FXML private Button mCancelButton;

    /* The controller requesting this dialog. */
    private byte mClient;

    /* A reference to that listener of this dialog. */
    private OnConfirmationDialogListener mListener;

    /**
     * Inform the listener that the confirm button was clicked.
     */
    @FXML
    public void onConfirmButtonClicked() {
        mListener.onConfirmButtonClicked(mClient);
    }

    /**
     * Information the listener that the cancel button was clicked.
     */
    @FXML
    public void onCancelButtonClicked() {
        mListener.onCancelButtonClicked(mClient);
    }

    /**
     * Set the listener of this dialog.
     *
     * @param listener
     *        The listener, usually the main control.
     */
    public void setListener(OnConfirmationDialogListener listener) {
        mListener = listener;
    }

    /**
     * Set the client of this confirmation dialog. If the client is
     * CLIENT_RESIDENT_DELETION or CLIENT_BUSINESS_DELETION, then the confirmation
     * dialog. If the client is CLIENT_WEBCAM_FAILURE, then hide the cancel button.
     *
     * Update the UI based on the client.
     *
     * @param client
     *        The client requesting this Confirmation Dialog.
     */
    public void setClient(byte client) {
        mClient = client;

        switch (mClient) {
            case CLIENT_RESIDENT_DELETION:
            case CLIENT_BUSINESS_DELETION:

                mActionLabel.setText(client == CLIENT_RESIDENT_DELETION ? "Delete Resident" : "Delete Business");

                mWarningText.setText("\nDeleted " + (client == CLIENT_RESIDENT_DELETION ? "resident" : "business") +
                        " cannot be recovered.");

                mActionIcon.setImage(new Image("res/ic_trash_bin.png"));

                mMessage.setText("Are you sure?");
                mWarningText.setVisible(true);
                mWarningText.setManaged(true);

                mCancelButton.setVisible(true);
                mCancelButton.setManaged(true);

                break;

            case CLIENT_WEBCAM_FAILURE:
                mActionLabel.setText("Webcam Failure");
                mMessage.setText("Webcam cannot be initialized. Either no webcam is connected or is being used by " +
                        "another application");

                mWarningText.setVisible(false);
                mWarningText.setManaged(false);

                mCancelButton.setVisible(false);
                mCancelButton.setManaged(false);
        }
    }
}
