package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javah.contract.PreferenceContract;
import javah.model.PreferenceModel;

/**
 * A class that handles the login scene.
 */
public class LoginControl {

    /**
     * A listener for the LoginControl, used to inform the MainControl about
     * the transactions requested by the LoginControl.
     */
    public interface OnLoginControlListener {
        /**
         * Tell the MainControl to terminate the application.
         */
        void onExitButtonClicked();

        /**
         * Try to login into the application.
         */
        void onLoginButtonClicked();
    }

    /* A field for the password input. */
    @FXML private TextField mPasswordField;

    /* A label to display the error if the password did not match. */
    @FXML private Label mError;

    /* A reference to the universal preference model, used to get the current password. */
    private PreferenceModel mPrefModel;

    /* A listener for this controller. */
    private OnLoginControlListener mListener;

    /**
     * If the enter key is pressed from the Password field, then auto click the
     * confirm button.
     *
     * @param keyEvent
     *        The key event used to determine the key pressed.
     */
    @FXML
    public void onPasswordFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER)
            onLoginButtonClicked(null);
    }

    /**
     * Try to login into to application.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onLoginButtonClicked(ActionEvent actionEvent) {
        String password = mPrefModel.get(PreferenceContract.PASSWORD);

        if (password.equals(mPasswordField.getText())) {
            mListener.onLoginButtonClicked();
            mPasswordField.setText(null);
            mError.setVisible(false);
        } else
            mError.setVisible(true);
    }

    /**
     * Exit the application.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onExitButtonClicked(ActionEvent actionEvent) {
        mListener.onExitButtonClicked();
    }

    /**
     * Set the universal preference to this controller.
     *
     * @param prefModel
     *        The universal preference model.
     */
    public void setPreferenceModel(PreferenceModel prefModel) {
        mPrefModel = prefModel;
    }

    /**
     * Set the listener for this controller to be able to process this controllers
     * transactions.
     *
     * @param listener
     *        The listener of this controller.
     */
    public void setListener(OnLoginControlListener listener) {
        mListener = listener;
    }
}
