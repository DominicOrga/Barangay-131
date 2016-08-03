package javah.controller.resident;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javah.container.Resident;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResidentFormControl {



    public interface OnResidentFormListener {
        void onSaveButtonClicked(Resident resident);
        void onCancelButtonClicked();
        void onTakePhotoButtonClicked();
    }

    @FXML
    private Pane mRootPane;

    @FXML
    private ImageView mResidentPhoto;

    @FXML
    private Label mNameError, mAddress1Error, mAddress2Error;

    @FXML
    private TextField mFirstName, mMiddleName, mLastName;

    @FXML
    private TextArea mAddress1, mAddress2;

    @FXML
    private ComboBox mBirthMonth, mBirthDay, mBirthYear, mYearOfResidency, mMonthOfResidency;

    @FXML
    private ImageView mActionIcon;

    @FXML
    private Label mActionLabel;

    @FXML
    private HBox mHeader;

    private Resident mResident;

    private OnResidentFormListener mListener;

    @FXML
    private void initialize() {
        mResident = new Resident();

        // Initialize birth year elements, which include the current year up to 1900.
        int year = Calendar.getInstance().get(Calendar.YEAR);

        List<Integer> yearList = new ArrayList<>();
        for (int i = year; i >= 1900; i--)
            yearList.add(i);

        mBirthYear.setItems(FXCollections.observableArrayList(yearList));
        // Set the birth year with the current year as the default value.
        mBirthYear.setValue(year);

        // Initialize the default birth day elements (31 days, since default month is January).
        List<Integer> dayList = new ArrayList<>();
        for (int i = 1; i <= 31; i++)
        dayList.add(i);

        mBirthDay.setItems(FXCollections.observableArrayList(dayList));
        // Set the birth day to 1 as the default value.
        mBirthDay.setValue(1);

        // Set a birth month listener to update the elements of the birth day in accordance to the selected birth month.
        mBirthMonth.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int monthSelected = newValue.intValue();

                List<Integer> dayList = new ArrayList<>();
                switch (monthSelected) {
                    // January, March, May, July, August, October, December
                    case 0: case 2: case 4: case 6: case 7: case 9: case 11:
                        for(int i = 1; i <= 31; i++)
                            dayList.add(i);
                        break;

                    // April, June, September, November
                    case 3: case 5: case 8: case 10:
                        for(int i = 1; i <= 30; i++)
                            dayList.add(i);
                        break;

                    // February (Including leap year)
                    default:
                        int year = Calendar.getInstance().get(Calendar.YEAR);
                        int j = (year % 400 == 0 || year % 4 == 0 && year % 10 != 0) ? 29 : 28;

                        for (int i = 1; i <= j; i++)
                            dayList.add(i);
                }

                mBirthDay.setItems(FXCollections.observableArrayList(dayList));
                mBirthDay.setValue(1);


            }
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
        mYearOfResidency.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() != 0)
                    mMonthOfResidency.setVisible(true);
                else
                    mMonthOfResidency.setVisible(false);
            }
        });


    }

    @FXML
    public void onCancelButtonClicked(ActionEvent event) {
        mListener.onCancelButtonClicked();
        mResident = null;
        resetForm();
    }

    /**
     * Verify whether the inputted data is valid. If the data is valid, then proceed to the MainControl to continue the
     * creation process.
     * @param event
     */
    @FXML
    public void onCreateButtonClicked(ActionEvent event) {
        boolean isDataValid = true;

        // Check name input.
        mFirstName.setStyle(mFirstName.getText().matches("[a-zA-Z\\s]+") ? null : "-fx-border-color: #FF3F3F;");
        mMiddleName.setStyle(mMiddleName.getText().matches("[a-zA-Z\\s]+") ? null : "-fx-border-color: #FF3F3F;");
        mLastName.setStyle(mLastName.getText().matches("[a-zA-Z\\s]+") ? null : "-fx-border-color: #FF3F3F;");

        if (mFirstName.getText().matches("[a-zA-Z\\s]+") &&
            mMiddleName.getText().matches("[a-zA-Z\\s]+") &&
            mLastName.getText().matches("[a-zA-Z\\s]+")) {

            mNameError.setVisible(false);
        } else {
            mNameError.setVisible(true);
            isDataValid = false;
        }

        // Check address 1 input.
        if(mAddress1.getText().matches("[a-zA-Z0-9\\.,'\\s]+")) {
            mAddress1Error.setVisible(false);
            mAddress1.setStyle("-fx-background-color: white; -fx-border-color: #BEBEBE");
        } else {
            mAddress1Error.setVisible(true);
            mAddress1.setStyle("-fx-background-color: white; -fx-border-color: #FF3F3F");
            isDataValid = false;
        }

        // Check address 2 input.
        if(mAddress2.getText().matches("([A-Za-z0-9\\.,'\\s]+)?")) {
            mAddress2Error.setVisible(false);
            mAddress2.setStyle("-fx-background-color: white; -fx-border-color: #BEBEBE");
        } else {
            mAddress2Error.setVisible(true);
            mAddress2.setStyle("-fx-background-color: white; -fx-border-color: #FF3F3F");
            isDataValid = false;
        }

        // If all the data are all valid, then create a Resident object and pass the data to it, then send it to the
        // main control.
        if(isDataValid) {
            if (mResident == null)
                mResident = new Resident();

            mResident.setFirstName(mFirstName.getText());
            mResident.setLastName(mLastName.getText());
            mResident.setMiddleName(mMiddleName.getText());
            mResident.setAddress1(mAddress1.getText());
            mResident.setAddress2(mAddress2.getText());

            // Store the birthdate of the resident.
            Calendar birthdate = Calendar.getInstance();
            birthdate.set(
                    (int) mBirthYear.getValue(),
                    convertMonthStringToInt(mBirthMonth.getValue().toString()),
                    (int) mBirthDay.getValue()
            );

            mResident.setBirthDate(new Date(birthdate.getTime().getTime()));

            // Store the value of the year of residency of the resident.
            String yearOfResidency = mYearOfResidency.getValue().toString();

            // Store the year and month of residency of the resident.
            if (yearOfResidency.equals("Birth"))
                mResident.setYearOfResidency((short) birthdate.get(Calendar.YEAR));
            else {
                mResident.setYearOfResidency(Short.parseShort(yearOfResidency));
                mResident.setMonthOfResidency((short) convertMonthStringToInt(mMonthOfResidency.getValue().toString()));
            }

            mListener.onSaveButtonClicked(mResident);
            mResident = null;
            resetForm();
        }
    }

    /**
     * Reference a photo to the resident.
     * @param event
     */
    @FXML
    public void onUploadPhotoButtonClicked(ActionEvent event) {
        // Disable the mRootPane while the upload window is displayed.
        mRootPane.setDisable(true);

        if(mResident == null) mResident = new Resident();
        // Setup the file chooser dialog.
        FileChooser fileChooser = new FileChooser();

        // Define the allowed file extensions.
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG Files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.PNG");
        FileChooser.ExtensionFilter extFilterJPEG = new FileChooser.ExtensionFilter("JPEG Files (*.jpeg)", "*.JPEG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterJPEG, extFilterPNG);

        // Show the file chooser dialog.
        Stage stage = new Stage();
        stage.setTitle("Choose Photo");
        File file = fileChooser.showOpenDialog(stage);

        // Enable the mRootPane after the upload window is displayed.
        mRootPane.setDisable(false);

        // A file is selected if its not equal to null.
        if(file != null) {
            // Update the resident photo with the copied image.
            System.out.println(file.toPath() + "");
            mResident.setPhotoPath(file.toPath() + "");
            Image image = new Image("file:" + file.toPath());
            mResidentPhoto.setImage(image);
        }
    }

    @FXML
    public void onTakePhotoButtonClicked(ActionEvent actionEvent) {
        mListener.onTakePhotoButtonClicked();
    }

    /**
     * When setResident is used(), we assume that we are editing the resident.
     * @param resident
     */
    public void setResident(Resident resident) {
        mResident = resident;
    }

    /**
     * Set MainControl as the listener to this form.
     * @param listener
     */
    public void setListener(OnResidentFormListener listener) {
        mListener = listener;
    }

    /**
     * Convert a string month to its corresponding int value.
     * @param monthStr
     * @return
     */
    private int convertMonthStringToInt(String monthStr) {
        switch(monthStr) {
            case "January" : return 0;
            case "February" : return 1;
            case "March" : return 2;
            case "April" : return 3;
            case "May" : return 4;
            case "June" : return 5;
            case "July" : return 6;
            case "August" : return 7;
            case "September" : return 8;
            case "October" : return 9;
            case "November" : return 10;
            default : return 11;
        }
    }

    /**
     * Reset the form to its default values.
     */
    private void resetForm() {
        mFirstName.setText("");
        mMiddleName.setText("");
        mLastName.setText("");
        mAddress1.setText("");
        mAddress2.setText("");
        mBirthMonth.getSelectionModel().selectFirst();
        mBirthDay.getSelectionModel().selectFirst();
        mBirthYear.getSelectionModel().selectFirst();
        mYearOfResidency.getSelectionModel().selectFirst();
        mMonthOfResidency.setVisible(false);
        mMonthOfResidency.getSelectionModel().selectFirst();
        mResidentPhoto.setImage(new Image("/res/ic_default_resident.png"));
    }
}
