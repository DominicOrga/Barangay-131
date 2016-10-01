package javah.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javah.contract.PreferenceContract;
import javah.model.PreferenceModel;
import javah.util.BarangayUtils;
import javah.util.LogoutTimer;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A class that manages the security of the application.
 */
public class SecurityControl {

    /**
     * A listener for the SecurityControl.
     *
     * @see SecurityControl
     */
    public interface OnSecurityControlListener {
        /**
         * Take necessary actions when mDoneButton is clicked. That is, inform the
         * MainControl if the Idle max duration was changed and close SecuritControl scene.
         */
        void onDoneButtonClicked();

        /**
         * Tell the MainControl to launch the change password scene.
         */
        void onChangePasswordButtonClicked();

        /**
         * Update the LogoutTimer in the MainControl when the idle combo box value has
         * changed.
         *
         * @see LogoutTimer
         */
        void onIdleComboBoxValueChanged(int newValue);
    }

    /* Disables the scene when the change password scene is displayed. */
    @FXML private Pane mRootPane;

    /* Displays the first 3 character of the current password. */
    @FXML private TextField mPassword;

    /**
     * A label which is shown when the user wants to update the password even when
     * 3 days have not passed yet since the recent update.
     */
    @FXML private TextFlow mWarning;

    /**
     * A part of the mWarning, used to inform the user about the previous datetime in
     * which the password was updated.
     */
    @FXML private Text mLastUpdate;

    /**
     * A Combo box for picking the max idle duration to which the application can be
     * idle before being automatically logged out.
     */
    @FXML private ComboBox<String> mIdleComboBox;

    /* A reference to the universal preference model. */
    private PreferenceModel mPrefModel;

    /* A listener for this controller. */
    private OnSecurityControlListener mListener;

    /**
     * Set a listener to the mPassword to divert any focus to it.
     */
    @FXML
    private void initialize() {
        BarangayUtils.addTextLimitListener(mPassword, 16);

        mPassword.focusedProperty().addListener((observable, oldValue, newValue) -> mRootPane.requestFocus());

        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> mWarning.setVisible(false));

        mIdleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                String value = newValue.split(" ")[0];

                mPrefModel.put(PreferenceContract.MAX_IDLE_DURATION, value);
                mPrefModel.save(false);

                if (mListener != null)
                    mListener.onIdleComboBoxValueChanged(Integer.valueOf(value));
            }
        });
    }

    @FXML
    public void onChangePasswordButtonClicked(MouseEvent mouseEvent) {
        String datetime = mPrefModel.get(PreferenceContract.LAST_PASSWORD_UPDATE, null);

        if (datetime == null)
            mListener.onChangePasswordButtonClicked();
        else {
            Date lastPasswordUpdate = new Date(Long.valueOf(datetime));

            Calendar allowPasswordUpdate = Calendar.getInstance();
            allowPasswordUpdate.setTime(new Date(Long.valueOf(datetime)));

            // Add 3 days to the last password update.
            allowPasswordUpdate.add(Calendar.DAY_OF_MONTH, 3);

            Calendar currentDay = Calendar.getInstance();

            // If the last password update added by 3 days is less than the current day, then
            // allow password update process.
            if (allowPasswordUpdate.compareTo(currentDay) < 0) {
                mListener.onChangePasswordButtonClicked();
            } else {
                if (!mWarning.isVisible()) {
                    mWarning.setVisible(true);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, YYYY hh:mm aaa");
                    mLastUpdate.setText(simpleDateFormat.format(lastPasswordUpdate.getTime()));
                }
            }
        }
    }

    /**
     * Save the new idle time as a preference if it was changed. Also, close this scene.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onDoneButtonClicked(ActionEvent actionEvent) {
        int idleTimeNew = Integer.valueOf(((String) mIdleComboBox.getValue()).split(" ")[0]);

        int idleTimeOld = Integer.valueOf(mPrefModel.get(PreferenceContract.MAX_IDLE_DURATION, "0"));

        if (idleTimeNew != idleTimeOld) {
            mPrefModel.put(PreferenceContract.MAX_IDLE_DURATION, idleTimeNew + "");
            mPrefModel.save(false);
        }

        mListener.onDoneButtonClicked();
    }

    /**
     * Update the displayed password in mPassword. Called from the MainControl when
     * the password was updated and once in the initialization.
     */
    public void updateDisplayedPassword() {
        String password = mPrefModel.get(PreferenceContract.PASSWORD, null);

        if (password != null && !password.isEmpty())
            mPassword.setText(password.substring(0, 3) +
                    "\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022");

        System.out.println("SecurityControl - Password updated to: " + password);
    }

    /**
     * Set the listener for this controller.
     *
     * @param listener
     *        The listener for this controller.
     */
    public void setListener(OnSecurityControlListener listener) {
        mListener = listener;
    }

    /**
     * Pass the universal preference model to this controller.
     *
     * @param prefModel
     *        The universal preference model.
     */
    public void setPreferenceModel(PreferenceModel prefModel) {
        mPrefModel = prefModel;

        String value = mPrefModel.get(PreferenceContract.MAX_IDLE_DURATION, "5");
        value += value.equals("1") ? " min" : " mins";
        mIdleComboBox.setValue(value);
    }

    /**
     * Disable this scene from the MainControl when the ChangePassword scene is visible.
     *
     * @param bool
     *        Determines whether to disable or enable this scene.
     */
    public void setDisable(boolean bool) {
        mRootPane.setDisable(bool);
    }

}
