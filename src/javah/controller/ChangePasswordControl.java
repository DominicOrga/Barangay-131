package javah.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @FXML private Label mPasswordStrengthLabel;

    /**
     * A reference to the universal preference model. Used to acquire the current
     * password of the system or update the current password.
     */
    private PreferenceModel mPrefModel;

    /* Determines whether the new password is masked or not. */
    private boolean mIsNewPasswordMasked = true;

    private OnPasswordControlListener mListener;

    /* The strength of the newly inputted password. */
    private int mPasswordStrength;

    @FXML
    public void initialize() {
        // Bind bi-directionally together the mNewPassword and mNewPasswordMasked.
        mNewPassword.textProperty().bindBidirectional(mNewPasswordMasked.textProperty());
        mNewPassword.setText("");
        mConfirmPasswordMasked.setText("");

        mRequirements.setVisible(false);

        // Limit all the text input controls to 16.
        BarangayUtils.addTextLimitListener(mNewPassword, 16);
        BarangayUtils.addTextLimitListener(mNewPasswordMasked, 16);
        BarangayUtils.addTextLimitListener(mConfirmPasswordMasked, 16);

        // Additions:
        Pattern aPattern = Pattern.compile("\\d");
        Pattern bPattern = Pattern.compile("[a-z]");
        Pattern cPattern = Pattern.compile("[A-Z]");
        Pattern dPattern = Pattern.compile("[^(a-z)(A-Z)(\\d)]");
        Pattern ePattern = Pattern.compile("(.)(\\d)(.)");
        Pattern fPattern = Pattern.compile("(.)[^(a-z)(A-Z)(\\d)](.)");

        // Deductions:
        Pattern jPattern = Pattern.compile("[A-Z]{2}");
        Pattern kPattern = Pattern.compile("[a-z]{2}");
        Pattern lPattern = Pattern.compile("\\d{2}");

        String[] letters = new String[]{"abc", "bcd", "cde", "def", "efg", "fgh", "ghi", "hij", "ijk",
                "jkl", "klm", "lmn", "mno", "nop", "opq", "pqr", "qrs", "rst", "stu", "tuv", "uvw", "vwx", "wxy", "xyz"};
        String[] lettersRev = new String[]{"cba", "dcb", "edc", "fed", "gfe", "hgf", "ihg", "jih", "kji",
                "lkj", "mlk", "nml", "onm", "pon", "qpo", "rqp", "srq", "tsr", "uts", "vut", "wvu", "xwv", "yxw", "zyx"};

        String[] numbers = new String[]{"012", "123", "234", "345", "456", "567", "678", "789", "890"};
        String[] numbersRev = new String[]{"210", "321", "432", "543", "654", "765", "876", "987", "098"};

        String[] symbols = new String[]{")!@", "!@#", "@#$", "#$%", "$%^", "%^&", "^&*", "&*(", "*()"};
        String[] symbolsRev = new String[]{"@!)", "#@!", "$#@", "%$#", "^%$", "&^%", "*&^", "(*&", ")(*"};

        String[] passwordStrength = new String[]{"Too Short", "Very Weak", "Weak", "Good", "Strong", "Very Strong"};

        mNewPasswordMasked.textProperty().addListener((observable, oldValue, newValue) -> {
            mRequirements.setVisible(false);

            if (newValue == null || newValue.isEmpty()) {
                mPasswordStrengthLabel.setText(passwordStrength[0]);
                mPasswordStrengthLabel.setTextFill(Color.RED);
                return;
            }

            mPasswordStrength = 0;
            int passwordLength = newValue.length();

            //---------------- Additions --------------//
            // Numbers:
            Matcher aMatcher = aPattern.matcher(newValue);
            int a = 0;
            while (aMatcher.find()) a++;

            // Lowercase letters:
            Matcher bMatcher = bPattern.matcher(newValue);
            int b = 0;
            while (bMatcher.find()) b++;

            // Uppercase letters:
            Matcher cMatcher = cPattern.matcher(newValue);
            int c = 0;
            while (cMatcher.find()) c++;

            // Symbols:
            Matcher dMatcher = dPattern.matcher(newValue);
            int d = 0;
            while (dMatcher.find()) d++;

            // Middle numbers:
            Matcher eMatcher = ePattern.matcher(newValue);
            int e = 0;
            int start = 0;
            while (eMatcher.find(start)) {
                e++;
                start = eMatcher.end() - 2;
                if (eMatcher.hitEnd()) break;
            }

            // Middle symbols:
            Matcher fMatcher = fPattern.matcher(newValue);
            int f = 0;
            start = 0;
            while (fMatcher.find(start)) {
                f++;
                start = fMatcher.end() - 2;
                if (fMatcher.hitEnd()) break;
            }

            // Requirements:
            int g = a > 0 ? 1 : 0;
            g += b > 0 ? 1 : 0;
            g += c > 0 ? 1 : 0;
            g += d > 0 ? 1 : 0;
            g += passwordLength > 7 ? 1 : 0;

            //------------- Deductions ----------------//
            // Letters only:
            int h = (a == 0 && d == 0) ? b + c : 0;

            // Numbers only:
            int i = (b == 0 && c == 0 && d == 0) ? a : 0;

            // Consecutive uppercase letters:
            Matcher jMatcher = jPattern.matcher(newValue);
            int j = 0;
            start = 0;
            while (jMatcher.find(start)) {
                j++;
                start = jMatcher.end() - 1;
                if (jMatcher.hitEnd()) break;
            }

            // Consecutive lowercase letters:
            Matcher kMatcher = kPattern.matcher(newValue);
            int k = 0;
            start = 0;
            while (kMatcher.find(start)) {
                k++;
                start = kMatcher.end() - 1;
                if (kMatcher.hitEnd()) break;
            }

            // Consecutive numbers:
            Matcher lMatcher = lPattern.matcher(newValue);
            int l = 0;
            start = 0;
            while (lMatcher.find(start)) {
                l++;
                start = lMatcher.end() - 1;
                if (lMatcher.hitEnd()) break;
            }

            double m = 0;
            int repChar = 0;
            int n = 0, o = 0, p = 0;
            for (int x = 0; x < 24; x++) {
                // Repeat character (Case insensitive):
                if (x < passwordLength) {
                    boolean bool = false;

                    for (int y = 0; y < passwordLength; y++)
                        if (newValue.charAt(x) == newValue.charAt(y) && x != y) {
                            bool = true;
                            m += Math.abs(passwordLength * 1.0 / (y - x));
                        }

                    if (bool) {
                        repChar++;
                        int unqChar = passwordLength - repChar;
                        m = (unqChar != 0 ) ? Math.ceil(m / unqChar) : Math.ceil(m);
                    }
                }

                // Sequential letters:
                if (newValue.contains(letters[x]) || newValue.contains(lettersRev[x]))
                    n++;

                // Sequential numbers:
                if (x < 9 && (newValue.contains(numbers[x]) || newValue.contains(numbersRev[x])))
                    o++;

                // Sequential symbols:
                if (x < 9 && (newValue.contains(symbols[x]) || newValue.contains(symbolsRev[x])))
                    p++;
            }

            mPasswordStrength += passwordLength * 4;
            mPasswordStrength += (b == 0) ? 0 : (passwordLength - b) * 2;
            mPasswordStrength += (c == 0) ? 0 : (passwordLength - c) * 2;
            mPasswordStrength += (a * 4) + (d * 6) + (e * 2) + (f * 2);
            mPasswordStrength += a == 0 || b == 0 || c == 0 || d == 0 || passwordLength < 8 ? 0 : g * 2;

            mPasswordStrength -= h + i + m + (j * 2) + (k * 2) + (l * 2) + (n * 3) + (o * 3) + (p * 3);

            if (mPasswordStrength >= 80) {
                mPasswordStrengthLabel.setText(passwordStrength[5]);
                mPasswordStrengthLabel.setTextFill(Color.BLUE);
            } else if (mPasswordStrength >= 60) {
                mPasswordStrengthLabel.setText(passwordStrength[4]);
                mPasswordStrengthLabel.setTextFill(Color.GREEN);
            } else if (mPasswordStrength >= 40) {
                mPasswordStrengthLabel.setText(passwordStrength[3]);
                mPasswordStrengthLabel.setTextFill(Color.YELLOW);
            } else if (mPasswordStrength >= 20) {
                mPasswordStrengthLabel.setText(passwordStrength[2]);
                mPasswordStrengthLabel.setTextFill(Color.ORANGE);
            } else {
                mPasswordStrengthLabel.setText(passwordStrength[1]);
                mPasswordStrengthLabel.setTextFill(Color.RED);
            }
        });

        mConfirmPasswordMasked.textProperty().addListener((observable, oldValue, newValue) -> mRequirements.setVisible(false));

        // Take action when this is visible again.
        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            mRootPane.setDisable(false);
            // Clear the text fields.
            mNewPassword.setText(null);
            mConfirmPasswordMasked.setText(null);

            mRequirements.setVisible(false);

            // Set the new password masked visible.
            setNewPasswordMaskedVisible(true);
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
        if (mNewPassword.getText().length() < 8) {
            mRequirements.setText("Password must have at least a length of 8.");
            mRequirements.setVisible(true);
            return;
        }
        if (mPasswordStrength < 40) {
            mRequirements.setText("Password must not be weak.");
            mRequirements.setVisible(true);
            return;
        }

        String currentPassword = mPrefModel.get(PreferenceContract.PASSWORD, "");
        if (mNewPassword.getText().equals(currentPassword)) {
            mRequirements.setText("New password must not be equal to the current password.");
            mRequirements.setVisible(true);
            return;
        }

        if (!mNewPassword.getText().equals(mConfirmPasswordMasked.getText())) {
            mRequirements.setText("New password must match with the confirm password.");
            mRequirements.setVisible(true);
            return;
        }

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

        System.out.println("ChangePasswordControl - Password Saved. " + mNewPassword.getText());

        return calendar;
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
