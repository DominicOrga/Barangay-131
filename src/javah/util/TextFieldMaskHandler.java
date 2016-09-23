package javah.util;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that adds a masking feature to text fields.
 */
public class TextFieldMaskHandler {
    /**
     * Contains all the masked text fields.
     */
    private static List<TextFieldMaskHolder> mMaskedTextFields;

    /**
     * Initialize the maskedTextFields list.
     */
    static {
        mMaskedTextFields = new ArrayList<>();
    }

    /**
     * A data holder class for the text field and its associated change listener.
     */
    private static class TextFieldMaskHolder {
        TextField mTextField;
        ChangeListener mChangeListener;
    }

    /**
     * Add mask to the text field if and only if it is not masked yet.
     *
     * @param textField
     *        The text field to be masked.
     */
    public static void addMask(TextField textField) {
        int x = mMaskedTextFields.size();

        // Check if the text field argument is already added. If it is, then stop the
        // function.
        for (int i = 0; i < x; i++)
            if (mMaskedTextFields.get(i).mTextField.equals(textField))
                return;


        ChangeListener changeListener = (ChangeListener<String>) (observable, oldValue, newValue) -> {
            if (newValue != null) {
                int strLength = newValue.length();
                String maskedStr = "";

                for (int i = 0; i < strLength; i++)
                    maskedStr += '\u2022';

                textField.setText(maskedStr);
            }
        };

        textField.textProperty().addListener(changeListener);

        TextFieldMaskHolder holder = new TextFieldMaskHolder();
        holder.mTextField = textField;
        holder.mChangeListener = changeListener;

        mMaskedTextFields.add(holder);
    }

    /**
     * Remove the mask from the specified text field if and only if it has a mask.
     *
     * @param textField
     *        The text field to be unmasked.
     */
    public static void removeMask(TextField textField) {
        int x = mMaskedTextFields.size();

        for (int i = 0; i < x; i++) {
            TextFieldMaskHolder textFieldMaskHolder = mMaskedTextFields.get(i);

            if (textFieldMaskHolder.mTextField.equals(textField)) {
                 textFieldMaskHolder.mTextField.textProperty().removeListener(textFieldMaskHolder.mChangeListener);

                mMaskedTextFields.remove(textFieldMaskHolder);
                return;
            }
        }
    }
}
