package javah.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * A class that handles the password manipulation of the application.
 */
public class PasswordController {

    /**
     * A controller that listens when the change or cancel button is clicked from this
     * controller.
     */
    public interface OnPasswordControlListener {
        /**
         * Tell the Account control that the password has been updated and take necessary
         * actions.
         */
        void onPasswordChanged();
    }

    /* The root node of this view. Disabled when the confirmation dialog is visible. */
    @FXML private Pane mRootPane;

    /* An input field for the new password. */
    @FXML private PasswordField mNewPassword;

    /* Another input field to verify the new password. */
    @FXML private PasswordField mConfirmPassword;

    /* Displays the rule violated by the new password input. */
    @FXML private Text mError;

    /* Displays the strength of the input from the mNewPassword. */
    @FXML private Label mPasswordStrength;

    /* Buttons to either save or cancel the password update process. */
    @FXML private Button mSaveButton, mCancelButton;

    /* Determines whether to show the text within the password fields. */
    private boolean mIsNewPasswordPeekEnabled, mIsConfirmPasswordPeekEnabled;


    @FXML
    public void initialize() {

        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            mNewPassword.setText(null);
            mConfirmPassword.setText(null);


            mError.setVisible(false);

            mPasswordStrength.setText("Weak");
            mPasswordStrength.setTextFill(Color.valueOf("#FF3F3F"));

            mSaveButton.setDisable(true);
        });
    }

}
