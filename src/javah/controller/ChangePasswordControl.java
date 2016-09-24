package javah.controller;

import edu.vt.middleware.password.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javah.contract.PreferenceContract;
import javah.model.PreferenceModel;
import javah.util.BarangayUtils;

import java.util.Calendar;

/**
 * A class that handles the password manipulation of the application.
 */
public class ChangePasswordControl {


    /**
     * A controller that listens when the change or cancel button is clicked from this
     * controller.
     */
    public interface OnPasswordControlListener {
        /**
         * Tell the Account control that the password has been updated and take necessary
         * actions.
         */
        void onSaveButtonClicked();

        /**
         * Close the Change password scene from the main control.
         */
        void onCancelButtonClicked();
    }

    /* The root node of this view. Disabled when the confirmation dialog is visible. */
    @FXML private Pane mRootPane;

    /* An input field for the new password. */
    @FXML private TextField mNewPassword;

    /* A masked input field for the new password. */
    @FXML private PasswordField mNewPasswordMasked;

    /* Another input field to verify the new password. */
    @FXML private TextField mConfirmPasswordMasked;

    @FXML private ImageView mPasswordPeek;

    /* Displays the rules needed to be satisfied by the new password input. */
    @FXML private Text mRequirements;

    /* Displays the strength of the input from the mNewPassword. */
    @FXML private Label mPasswordStrength;

    /* A button to save the new password if all the rules are followed. */
    @FXML private Button mSaveButton;

    /**
     * A reference to the universal preference model. Used to acquire the current
     * password of the system or update the current password.
     */
    private PreferenceModel mPrefModel;

    /* Determines whether the new password is masked or not. */
    private boolean mIsNewPasswordMasked = true;

    private OnPasswordControlListener mListener;

    private String mCurrentPassword;

    @FXML
    public void initialize() {
        // Bind bi-directionally together the mNewPassword and mNewPasswordMasked.
        mNewPassword.textProperty().bindBidirectional(mNewPasswordMasked.textProperty());

        // Limit all the text input controls to 17.
        BarangayUtils.addTextLimitListener(mNewPassword, 17);
        BarangayUtils.addTextLimitListener(mNewPasswordMasked, 17);
        BarangayUtils.addTextLimitListener(mConfirmPasswordMasked, 17);

        String[] requirements = new String[]{
                "Length must be between 8 to 16 characters long.",
                "New password must not be equal to the previous one.",
                "New Password must not be weak.",
                "New password must match with the confirm password."
        };

        // Gain Level 1 (+1)
        CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
        charRule.getRules().add(new DigitCharacterRule(1));
        charRule.getRules().add(new UppercaseCharacterRule(1));
        charRule.getRules().add(new LowercaseCharacterRule(1));
        charRule.getRules().add(new NonAlphanumericCharacterRule(1));

        // Gain Level 2 (+4)
        LengthRule lengthRule1 = new LengthRule(8, 17);
        LengthRule lengthRule2 = new LengthRule(11, 17);
        LengthRule lengthRule3 = new LengthRule(14, 17);

        // Lose Level 1 (-2)
        NumericalSequenceRule numSeqRule1 = new NumericalSequenceRule(3, false);
        RepeatCharacterRegexRule repCharRule1 = new RepeatCharacterRegexRule(3);

        // Lose Level 2 (-4)
        NumericalSequenceRule numSeqRule2 = new NumericalSequenceRule(5, false);
        RepeatCharacterRegexRule repCharRule2 = new RepeatCharacterRegexRule(5);

        // Lose Level 3 (-8)
        NumericalSequenceRule numSeqRule3 = new NumericalSequenceRule(7, false);
        RepeatCharacterRegexRule repCharRule3 = new RepeatCharacterRegexRule(7);

        // Add a listener to the new password input field and determine the password strength
        // and rule violations (if any) of the inputted password at every inputted character.
        mNewPasswordMasked.textProperty().addListener((observable, oldValue, newValue) -> {
            int passwordStrength = 4;

            if (newValue == null) newValue = "";

            PasswordData passwordData = new PasswordData(new Password(newValue));

            // Test Gain Level 1. For every rule violated, subtract 1.
            RuleResult result = charRule.validate(passwordData);
            passwordStrength -= result.getDetails().size();

            // Test Gain Level 2.
            result = lengthRule1.validate(passwordData);
            if (result.isValid())
                passwordStrength += 4;

            result = lengthRule2.validate(passwordData);
            if (result.isValid())
                passwordStrength += 4;

            result = lengthRule3.validate(passwordData);
            if (result.isValid())
                passwordStrength += 4;

            // Test Lose Level 1.
            result = numSeqRule1.validate(passwordData);
            if (!result.isValid())
                passwordStrength -= 2;

            result = repCharRule1.validate(passwordData);
            if (!result.isValid())
                passwordStrength -= 2;

            // Test Lose Level 2.
            result = numSeqRule2.validate(passwordData);
            if (!result.isValid())
                passwordStrength -= 4;

            result = repCharRule2.validate(passwordData);
            if (!result.isValid())
                passwordStrength -= 4;

            // Test Lose Level 3.
            result = numSeqRule3.validate(passwordData);
            if (!result.isValid())
                passwordStrength -= 8;

            result = repCharRule3.validate(passwordData);
            if (!result.isValid())
                passwordStrength -= 8;

            if (passwordStrength >= 15) {
                mPasswordStrength.setText("Very Strong");
                mPasswordStrength.setTextFill(Color.GREEN);
            } else if (passwordStrength >= 10) {
                mPasswordStrength.setText("Strong");
                mPasswordStrength.setTextFill(Color.BLUE);
            } else if (passwordStrength >= 5) {
                mPasswordStrength.setText("Fair");
                mPasswordStrength.setTextFill(Color.DARKORANGE);
            } else {
                mPasswordStrength.setText("Weak");
                mPasswordStrength.setTextFill(Color.RED);
            }

            // New password must have a length greater than 8 but less than 16.
            if (newValue.length() < 8 || newValue.length() > 16) {
                mRequirements.setVisible(true);
                mRequirements.setText(requirements[0]);
                mSaveButton.setDisable(true);
                return;
            }

            // New password must not be equal to the previous one.
            if (mCurrentPassword != null)
                if (newValue.matches(mCurrentPassword)) {
                    mRequirements.setVisible(true);
                    mRequirements.setText(requirements[1]);
                    mSaveButton.setDisable(true);
                    return;
                }

            // Weak password not allowed.
            if (passwordStrength < 5) {
                mRequirements.setVisible(true);
                mRequirements.setText(requirements[2]);
                mSaveButton.setDisable(true);
                return;
            }

            if (mConfirmPasswordMasked == null ||
                    !mNewPasswordMasked.getText().equals(mConfirmPasswordMasked.getText())) {
                mRequirements.setVisible(true);
                mRequirements.setText(requirements[3]);
                mSaveButton.setDisable(true);
                return;
            }

            mRequirements.setVisible(false);
            mSaveButton.setDisable(false);
        });

        // Check if the Confirm Password field matches that of the new password field at every
        // character input at confirm password field.
        mConfirmPasswordMasked.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) newValue = "";

            if (mConfirmPasswordMasked == null || mNewPasswordMasked == null ||
                    !newValue.equals(mConfirmPasswordMasked.getText())) {
                mRequirements.setVisible(true);
                mRequirements.setText(requirements[3]);
                mSaveButton.setDisable(true);
                return;
            }

            mRequirements.setVisible(false);
            mSaveButton.setDisable(false);
        });

        // Take action when this is visible again.
        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            mRootPane.setDisable(false);
            // Clear the text fields.
            mNewPassword.setText(null);
            mConfirmPasswordMasked.setText(null);

            // Set the new password masked visible.
            setNewPasswordMaskedVisible(true);

            // Always get the current password when this view is shown.
            if (mPrefModel != null)
                mCurrentPassword = mPrefModel.get(PreferenceContract.PASSWORD, null);
        });
    }

    /**
     * If the mPassword is visible, then show mPasswordMasked on top of it.
     * If the mPassword is not visible, then hide the mPasswordMasked on top of it.
     *
     * @param mouseEvent
     *        The action event. No usage.
     */
    @FXML
    public void onPasswordPeekClicked(MouseEvent mouseEvent) {
        setNewPasswordMaskedVisible(!mIsNewPasswordMasked);
    }

    @FXML
    public void onCancelButtonClicked(ActionEvent actionEvent) {
        mListener.onCancelButtonClicked();
    }

    /**
     * Call the ConfirmationDialogControl to inform the client about the consequences
     * of changing the password.
     *
     * @param actionEvent
     *        The action event. No usage.
     *
     * @see ConfirmationDialogControl
     */
    @FXML
    public void onSaveButtonClicked(ActionEvent actionEvent) {
        // Save the password as a preference.
        mRootPane.setDisable(true);
        mListener.onSaveButtonClicked();
    }

    /**
     * Display or hide the new password masked.
     *
     * @param bool
     *        Determines whether to display or hide the new password masked.
     */
    private void setNewPasswordMaskedVisible(boolean bool) {
        mIsNewPasswordMasked = bool;

        if (bool) {
            mNewPasswordMasked.setVisible(true);
            mPasswordPeek.setImage(new Image("res/ic_visibility_off.png"));
            mNewPassword.setDisable(true);
        } else {
            mNewPasswordMasked.setVisible(false);
            mPasswordPeek.setImage(new Image("res/ic_visibility_on.png"));
            mNewPassword.setDisable(false);
        }
    }

    /**
     * Pass the universal preference model to this controller.
     *
     * @param prefModel
     *        The universal preference model.
     */
    public void setPreferenceModel(PreferenceModel prefModel) {
        mPrefModel = prefModel;
    }

    /**
     * Set the listener for this controller.
     *
     * @param listener
     *        The listener for this controller.
     */
    public void setListener(OnPasswordControlListener listener) {
        mListener = listener;
    }

    /**
     * Tell this controller to save the password stored in the input fields.
     * Close this controller after the saving process.
     *
     * @return the datetime which the password was updated.
     */
    public Calendar savePassword() {
        mPrefModel.put(PreferenceContract.PASSWORD, mNewPassword.getText());

        Calendar calendar = Calendar.getInstance();
        mPrefModel.put(PreferenceContract.LAST_PASSWORD_UPDATE, calendar.getTime().getTime() + "");

        mPrefModel.save();

        return calendar;
    }
}
