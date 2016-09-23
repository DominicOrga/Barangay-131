package javah.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javah.container.Resident;
import javah.contract.CSSContract;
import javah.util.BarangayUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A controller class for managing the resident form, either for update or
 * creation purposes.
 */
public class ResidentFormControl {

    /**
     * An interface listener for the Resident form control.
     *
     * @see ResidentFormControl
     */
    public interface OnResidentFormListener {
        /**
         * Tell the Resident control to save the data of the specified resident when the
         * Resident form control save button is clicked.
         *
         * @param resident
         *        The resident to be saved.
         *
         * @see ResidentControl
         */
        void onSaveButtonClicked(Resident resident);

        /**
         * Tell the Main Control to close the Resident Form.
         */
        void onCancelButtonClicked();

        /**
         * Tell the Photoshop control to capture a profile photo for the Resident Form.
         *
         * @see PhotoshopControl
         */
        void onTakePhotoButtonClicked();

        /**
         * Tell the Photoshop control to upload an image for the Resident Form.
         *
         * @see PhotoshopControl
         */
        void onUploadButtonClicked();
    }

    /* Disables the whole resident form while the photoshop is active. */
    @FXML Pane mRootPane;

    /* An Image view for the display photo of the resident. */
    @FXML private ImageView mResidentPhotoView;

    /* Labels to specify the data input errors. */
    @FXML private Label mNameError, mAddress1Error, mAddress2Error;

    /**
     * Text fields and combo box to input the name of the resident.
     * Max length for each text field is 30.
     */
    @FXML private TextField mFirstName, mMiddleName, mLastName;
    @FXML private ComboBox mAuxiliary;

    /**
     * Text areas for the address of the reaident.
     * Max length for each text areas is 150.
     */
    @FXML private TextArea mAddress1, mAddress2;

    /**
     * A combo box for the birth month of the resident.
     * Months are displayed in string, such as January.
     */
    @FXML private ComboBox mBirthMonth;

    /**
     * A combo box for the day of birth of the resident.
     * Days range varies depending on the birth month, which can either be 1 - 30,
     * 1 - 31, 1 - 28 or 1-29.
     */
    @FXML private ComboBox mBirthDay;

    /* A combo box with values of the year starting from 1900 to current year. */
    @FXML private ComboBox mBirthYear;

    /**
     * A combo box to determine the year of residency of the resident.
     * Values are string which include 'Birth' and years from 1900 to the current year.
     * If the current value of the combo box is Birth, then the mMonthOfResidency is
     * hidden, since the birth date will be used instead in determining the years of
     * residency of the resident.
     */
    @FXML private ComboBox mYearOfResidency;

    /**
     * A combo box to determine the month of residency of the resident.
     * The months are represented as strings, such as January. No usage
     * if the mYearOfResidency's value is 'Birth'.
     */
    @FXML private ComboBox mMonthOfResidency;

    /**
     * Displays the actions of the this controller, which is to either update or create
     * a resident.
     */
    @FXML private ImageView mActionIcon;
    @FXML private Label mActionLabel;

    /* A resident container containing the data of the resident. */
    private Resident mResident;

    /**
     * Takes hold of the image captured or uploaded by the photoshop control. The image
     * is null if the image was not changed.
     *
     * @see PhotoshopControl
     */
    private WritableImage mResidentPhoto;

    /* A reference to the listener of this controller. */
    private OnResidentFormListener mListener;

    /**
     * Initialize the components of the scene.
     */
    @FXML
    private void initialize() {
        // Set name text fields to have a maximum input of 30.
        BarangayUtils.addTextLimitListener(mFirstName, 30);
        BarangayUtils.addTextLimitListener(mMiddleName, 30);
        BarangayUtils.addTextLimitListener(mLastName, 30);
        BarangayUtils.addTextLimitListener(mAddress1, 125);
        BarangayUtils.addTextLimitListener(mAddress2, 125);

        // Initialize birth year elements, which include the current year up to 1900.
        int year = Calendar.getInstance().get(Calendar.YEAR);

        List<Integer> yearList = new ArrayList<>();
        for (int i = year; i >= 1900; i--)
            yearList.add(i);

        mBirthYear.setItems(FXCollections.observableArrayList(yearList));
        // Set the birth year with the current year as the default value.
        mBirthYear.setValue(year);

        // Initialize the default birth day elements
        // (31 days, since default month is January).
        List<Integer> dayList = new ArrayList<>();
        for (int i = 1; i <= 31; i++)
        dayList.add(i);

        mBirthDay.setItems(FXCollections.observableArrayList(dayList));
        // Set the birth day to 1 as the default value.
        mBirthDay.setValue(1);

        // Set a birth month listener to update the elements of the birth day in accordance
        // to the selected birth month.
        mBirthMonth.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int monthSelected = newValue.intValue();

            List<Integer> dayList1 = new ArrayList<>();
            switch (monthSelected) {
                // January, March, May, July, August, October, December
                case 0: case 2: case 4: case 6: case 7: case 9: case 11:
                    for(int i = 1; i <= 31; i++)
                        dayList1.add(i);
                    break;

                // April, June, September, November
                case 3: case 5: case 8: case 10:
                    for(int i = 1; i <= 30; i++)
                        dayList1.add(i);
                    break;

                // February (Including leap year)
                default:
                    int year1 = Calendar.getInstance().get(Calendar.YEAR);
                    int j = (year1 % 400 == 0 || year1 % 4 == 0 && year1 % 10 != 0) ? 29 : 28;

                    for (int i = 1; i <= j; i++)
                        dayList1.add(i);
            }

            mBirthDay.setItems(FXCollections.observableArrayList(dayList1));
            mBirthDay.setValue(1);
        });

        // Initialize the year of residency elements.
        List<String> yearOfResidencyList = new ArrayList<>();
        yearOfResidencyList.add("Birth");
        for (int i = year; i >= 1900; i--)
            yearOfResidencyList.add(i + "");

        mYearOfResidency.setItems(FXCollections.observableArrayList(yearOfResidencyList));
        // Set the year of residency to 'Birth' as the default value.
        mYearOfResidency.setValue("Birth");

        // Set a year of residency listener to determine whether its value is 'Birth' or not. If not, then display
        // the mMonthOfResidency.
        mYearOfResidency.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            mMonthOfResidency.setVisible(newValue.intValue() != 0);
        });

        // Reset the resident form every time it is set to visible.
        mRootPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mResident = null;

                mResidentPhotoView.setImage(null);

                mFirstName.setStyle(null);
                mMiddleName.setStyle(null);
                mLastName.setStyle(null);
                mAddress1.setStyle(CSSContract.STYLE_TEXTAREA_NO_ERROR);
                mAddress2.setStyle(CSSContract.STYLE_TEXTAREA_NO_ERROR);

                mFirstName.setText(null);
                mMiddleName.setText(null);
                mLastName.setText(null);
                mAuxiliary.setValue("N/A");
                mAddress1.setText(null);
                mAddress2.setText(null);
                mBirthMonth.getSelectionModel().selectFirst();
                mBirthDay.getSelectionModel().selectFirst();
                mBirthYear.getSelectionModel().selectFirst();
                mYearOfResidency.getSelectionModel().selectFirst();
                mMonthOfResidency.setVisible(false);
                mMonthOfResidency.getSelectionModel().selectFirst();

                // Update the form UI back to its former glory.
                mActionLabel.setText("New Resident");
                mActionIcon.setImage(new Image("res/ic_new_resident.png"));
            }
        });
    }

    /**
     * Tell the Resident Control that the cancel button was clicked.
     *
     * @param event
     *        The callback event. Never used.
     */
    @FXML
    public void onCancelButtonClicked(ActionEvent event) {

        TextFieldMaskHandler.removeMask(mFirstName);
//        mListener.onCancelButtonClicked();
    }

    /**
     * Verify whether the inputted data is valid. If the data is valid, then proceed
     * to send the Resident to the resident control for creation or update.
     *
     * @param event
     *        The callback event. Never used.
     */
    @FXML
    public void onCreateButtonClicked(ActionEvent event) {
        TextFieldMaskHandler.addMask(mFirstName);
//
//        boolean isDataValid = true;
//
//        String firstName = mFirstName.getText();
//        String middleName = mMiddleName.getText();
//        String lastName = mLastName.getText();
//
//        // Check name input.
//        mFirstName.setStyle(firstName != null && !firstName.trim().isEmpty() ? null : CSSContract.STYLE_TEXTFIELD_ERROR);
//        mMiddleName.setStyle(middleName != null && !middleName.trim().isEmpty() ? null : CSSContract.STYLE_TEXTFIELD_ERROR);
//        mLastName.setStyle(lastName != null && !lastName.trim().isEmpty() ? null : CSSContract.STYLE_TEXTFIELD_ERROR);
//
//        if (firstName != null && middleName != null && lastName != null &&
//                !firstName.trim().isEmpty() && !middleName.trim().isEmpty() &&
//                !lastName.trim().isEmpty()) {
//
//            mNameError.setVisible(false);
//        } else {
//            mNameError.setVisible(true);
//            isDataValid = false;
//        }
//
//        // Check address 1 input.
//        if(mAddress1.getText() != null && !mAddress1.getText().trim().isEmpty()) {
//            mAddress1Error.setVisible(false);
//            mAddress1.setStyle(CSSContract.STYLE_TEXTAREA_NO_ERROR);
//        } else {
//            mAddress1Error.setVisible(true);
//            mAddress1.setStyle(CSSContract.STYLE_TEXTAREA_ERROR);
//            isDataValid = false;
//        }
//
//        // If all the data are all valid, then create a Resident object and pass the data to it, then send it to the
//        // main control.
//        if(isDataValid) {
//            if (mResident == null) mResident = new Resident();
//
//            mResident.setFirstName(BarangayUtils.capitalizeString(firstName));
//            mResident.setMiddleName(BarangayUtils.capitalizeString(middleName));
//            mResident.setLastName(BarangayUtils.capitalizeString(lastName));
//
//
//            String auxiliary = mAuxiliary.getValue().toString();
//            mResident.setAuxiliary(auxiliary.equals("N/A") ? null : auxiliary);
//
//            mResident.setAddress1(mAddress1.getText());
//            mResident.setAddress2(mAddress2.getText());
//
//            // Store the birthdate of the resident.
//            Calendar birthdate = Calendar.getInstance();
//            birthdate.set(
//                    (int) mBirthYear.getValue(),
//                    BarangayUtils.convertMonthStringToInt(mBirthMonth.getValue().toString()),
//                    (int) mBirthDay.getValue()
//            );
//
//            mResident.setBirthDate(new Date(birthdate.getTime().getTime()));
//
//            // Store the value of the year of residency of the resident.
//            String yearOfResidency = mYearOfResidency.getValue().toString();
//
//            // Store the year and month of residency of the resident.
//            if (yearOfResidency.equals("Birth"))
//                mResident.setYearOfResidency((short) -1);
//            else {
//                mResident.setYearOfResidency(Short.parseShort(yearOfResidency));
//                mResident.setMonthOfResidency(
//                        (short) BarangayUtils.convertMonthStringToInt(mMonthOfResidency.getValue().toString()));
//            }
//
//            // Store the image permanently in Barangay131/Photos and return the path.
//            if (mResidentPhoto != null) {
//                try {
//                    // Save the photo in the approriate directory with a unique uuid name.
//                    String targetImage = Main.PHOTO_DIR_PATH + "/" + UUID.randomUUID() + ".png";
//
//                    File file = new File(targetImage);
//                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(mResidentPhoto, null);
//                    ImageIO.write(
//                            renderedImage,
//                            "png",
//                            file);
//
//                    // Store the path of the photo to the resident to be saved in the database.
//                    mResident.setPhotoPath(targetImage);
//
//                    mResidentPhoto = null;
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            mListener.onSaveButtonClicked(mResident);
//        }
    }

    /**
     * Call the Photoshop control to upload an image for the resident photo.
     *
     * @param event
     *        The callback event. Never used.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onUploadPhotoButtonClicked(ActionEvent event) {
       mListener.onUploadButtonClicked();
    }

    /**
     * Call the Photoshop control to capture a photo for the resident photo.
     *
     * @param actionEvent
     *        The callback event. Never used.
     *
     * @see PhotoshopControl
     */
    @FXML
    public void onTakePhotoButtonClicked(ActionEvent actionEvent) {
        mListener.onTakePhotoButtonClicked();
    }

    /**
     * When setResident() is used, we assume that we are editing the resident.
     * Thus, populate the form with the resident's data and update the scene.
     *
     * @param resident
     *        The resident to be updated.
     */
    public void setResident(Resident resident) {
        // Update the form UI suitable for Updating a resident.
        mActionLabel.setText("Update Resident");
        mActionIcon.setImage(new Image("res/ic_edit_resident.png"));

        // Populate the form with the resident's data.
        mResident = resident;

        if (resident.getPhotoPath() != null)
            mResidentPhotoView.setImage(new Image("file:" + resident.getPhotoPath()));

        mFirstName.setText(resident.getFirstName());
        mMiddleName.setText(resident.getMiddleName());
        mLastName.setText(resident.getLastName());
        mAuxiliary.setValue(resident.getAuxiliary() == null ? "N/A" : resident.getAuxiliary());
        mAddress1.setText(resident.getAddress1());

        if (resident.getAddress2() != null)
            mAddress2.setText(resident.getAddress2());

        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(resident.getBirthDate());

        mBirthMonth.setValue(BarangayUtils.convertMonthIntToString(birthDate.get(Calendar.MONTH)));
        mBirthDay.setValue(birthDate.get(Calendar.DAY_OF_MONTH));
        mBirthYear.setValue(birthDate.get(Calendar.YEAR));

        if (resident.getYearOfResidency() == -1)
            mYearOfResidency.setValue("Birth");
        else {
            mYearOfResidency.setValue(resident.getYearOfResidency() + "");
            mMonthOfResidency.setValue(BarangayUtils.convertMonthIntToString(resident.getMonthOfResidency()));
            mMonthOfResidency.setVisible(true);
        }
    }

    /**
     * Set the listener to this controller.
     *
     * @param listener
     *        The listener to this controller.
     */
    public void setListener(OnResidentFormListener listener) {
        mListener = listener;
    }

    /**
     * Disable or enable this form. Used when the photoshop control is visible.
     *
     * @param disable
     *        The boolean to determine whether to enable or disable the form.
     */
    public void setDisable(boolean disable) {
        mRootPane.setDisable(disable);
    }

    /**
     * Called by PhotoshopControl to pass the requested photo back to the resident form.
     *
     * @param residentPhoto
     *        The image coming from the Photoshop control.
     *
     * @see PhotoshopControl
     */
    public void setPhoto(WritableImage residentPhoto) {
        mResidentPhoto = residentPhoto;
        mResidentPhotoView.setImage(residentPhoto);
    }

}
