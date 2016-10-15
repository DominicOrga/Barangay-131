package javah.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javah.Main;
import javah.container.BarangayClearance;
import javah.container.BarangayID;
import javah.container.BusinessClearance;
import javah.container.Resident;
import javah.contract.CSSContract;
import javah.contract.PreferenceContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.model.PreferenceModel;
import javah.util.LogoutTimer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A controller that serves as the brain of the application. All existing scenes
 * within ths system are pre-loaded and managed by this controller. Furthermore,
 * this controller acts as a communication bridge for scenes to communicate.
 */
public class MainControl {

    /* A pane containing all the pop-up dialogs of the system. */
    @FXML private StackPane mPopupStackPane;

    /**
     * A pane created specifically for the pop-up login pane. The pop-up login pane is
     * displayed when the max idle time is reached. Due to its volatile nature, a
     * unique pane is provided for the pop-up login pane to be appear above all other
     * scenes.
     */
    @FXML private StackPane mPopupLoginPane;

    /* A pane that serves as the root node of this controller's view. */
    @FXML private GridPane mMainGridPane;

    /**
     * A container for the menu buttons, settings, time, welcome message, etc. Loaded to
     * edit the display of the menu buttons to determine which one is currently selected.
     */
    @FXML private GridPane mMenuGridPane;

    /* Menu buttons for selecting what information to display. */
    @FXML private Pane mResidentMenu, mBarangayClearanceMenu, mBarangayIdMenu, mBusinessClearanceMenu;

    /* Labels to display the last login datetime.*/
    @FXML private Label mLastLoginDate, mLastLoginTime;

    /* Labels to display the last password update datetime. */
    @FXML private Label mLastPwdUpdateDate, mLastPwdUpdateTime;

    /*  scene to display the residents. */
    private Pane mResidentScene;

    /**
     * A scene to display the information. Information instances are the Barangay ID,
     * Barangay Clearance and Business clearance.
     */
    private Pane mInformationScene;

    /**
     * A controller for the resident scene and handles the CRUD operations regarding
     * resident records.
     */
    private ResidentControl mResidentControl;

    /**
     * A controller for the information scene and handles the CRUD operations regarding
     * the Barangay IDs, Barangay Clearances and Business Clearances.
     */
    private InformationControl mInformationControl;

    //------------- POP-UP SCENES ----------------//
    private Pane mConfirmationDialogScene;
    private Pane mPhotoshopScene;
    private Pane mBarangayAgentScene;
    private Pane mChangePasswordScene;
    private Pane mSecurityScene;
    private Pane mLoginScene;

    private Pane mResidentFormScene;
    private Pane mResidentInfoFormScene;
    private Pane mBusiClearanceFormScene;

    private Pane mBarangayIDReportScene;
    private Pane mBrgyClearanceReportScene;
    private Pane mBusiClearanceReportScene;

    //------------- POP-UP SCENES CONTROLLERS ----------------//
    private ConfirmationDialogControl mConfirmationDialogControl;
    private ResidentFormControl mResidentFormControl;
    private PhotoshopControl mPhotoshopControl;
    private BarangayAgentControl mBarangayAgentControl;
    private ResidentInformationFormControl mResidentInformationFormControl;
    private BarangayIDReportControl mBarangayIDReportControl;
    private BarangayClearanceReportControl mBrgyClearanceReportControl;
    private BusinessClearanceFormControl mBusiClearanceFormControl;
    private BusinessClearanceReportControl mBusiClearanceReportControl;
    private ChangePasswordControl mChangePasswordControl;
    private SecurityControl mSecurityControl;
    private LoginControl mLoginControl;

    /* Represent each menu used to navigate which information to display. */
    private final byte MENU_RESIDENT = 1,
            MENU_BARANGAY_ID = 2,
            MENU_BARANGAY_CLEARANCE = 3,
            MENU_BUSINESS_CLEARANCE = 4;

    /* Holds the value of the currently selected menu. */
    private byte mMenuSelected;

    /* The rectangle object used to assist menu animation. */
    private Rectangle mRectAnimTransitioner;

    /**
     * Universal references to the data storages of the application.
     * This variables are shared to the different controllers.
     */
    private CacheModel mCacheModel;
    private DatabaseModel mDatabaseModel;
    private PreferenceModel mPreferenceModel;

    /**
     * A class that logouts the application if the user is idle for a specified amount
     * of time. Max idle time can be modified at the security control.
     *
     * @see SecurityControl
     */
    private LogoutTimer mLogoutTimer;

    /**
     * Initialize all the scenes and controllers.
     *
     * @throws IOException
     *         An exception that may occur during the fxml loading of the views.
     */
    @FXML
    private void initialize() throws IOException {
        mLogoutTimer = new LogoutTimer();
        mLogoutTimer.setListener(() -> setLogout(true));

        // Initialize the models.
        mDatabaseModel = new DatabaseModel();
        mCacheModel = new CacheModel();
        mCacheModel.startCache(mDatabaseModel);
        mPreferenceModel = new PreferenceModel();

        // Update the last password update date time labels.
        String pwdDateTime = mPreferenceModel.get(PreferenceContract.LAST_PASSWORD_UPDATE, null);

        if (pwdDateTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(Long.valueOf(pwdDateTime)));

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");

            mLastPwdUpdateDate.setText(dateFormat.format(calendar.getTime()));
            mLastPwdUpdateTime.setText(timeFormat.format(calendar.getTime()));
        }

        // Initialize the mRectAnimTransitioner.
        // Used in updateMenuSelected() to aid in animation.
        mRectAnimTransitioner = new Rectangle();
        mRectAnimTransitioner.setWidth(mMenuGridPane.getWidth());
        mRectAnimTransitioner.setHeight(mResidentMenu.getHeight() - 1);

        // Initialize the fxml loader to load all the scenes of the application.
        FXMLLoader fxmlLoader = new FXMLLoader();

        /**
         * Allow for easy reuse of the FXML loader to load all the scenes.
         */
        Consumer<String> resetFXMLLoader = (location) -> {
            fxmlLoader.setLocation(getClass().getClassLoader().getResource(location));
            fxmlLoader.setRoot(null);
            fxmlLoader.setController(null);
        };

        // Initialize the Resident scene.
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("fxml/scene_resident.fxml"));
        mResidentScene = fxmlLoader.load();
        mResidentControl = fxmlLoader.getController();

        mResidentControl.setDatabaseModel(mDatabaseModel);
        mResidentControl.setCacheModel(mCacheModel);

        mResidentControl.setListener(new ResidentControl.OnResidentControlListener() {
            @Override
            public void onNewResidentButtonClicked() {
                showPopupScene(mResidentFormScene, false);
            }

            @Override
            public void onEditResidentButtonClicked(Resident resident) {
                showPopupScene(mResidentFormScene, false);
                mResidentFormControl.setResident(resident);
            }

            @Override
            public void onDeleteResidentButtonClicked() {
                showPopupScene(mConfirmationDialogScene, false);
                mConfirmationDialogControl.setClient(ConfirmationDialogControl.CLIENT_RESIDENT_DELETION);
            }
        });

        // Initialize the Information Scene.
        resetFXMLLoader.accept("fxml/scene_information.fxml");
        mInformationScene = fxmlLoader.load();
        mInformationControl = fxmlLoader.getController();

        mInformationControl.setCacheModel(mCacheModel);
        mInformationControl.setDatabaseModel(mDatabaseModel);

        mInformationControl.setListener(new InformationControl.OnInformationControlListener() {
            @Override
            public void onCreateReportButtonClicked(byte information) {

                switch (information) {
                    case InformationControl.INFORMATION_BARANGAY_ID :
                        showPopupScene(mResidentInfoFormScene, false);
                        mResidentInformationFormControl.setFormType(ResidentInformationFormControl.FORM_BARANGAY_ID);

                        break;
                    case InformationControl.INFORMATION_BARANGAY_CLEARANCE :
                        showPopupScene(mResidentInfoFormScene, false);
                        mResidentInformationFormControl.setFormType(ResidentInformationFormControl.FORM_BARANGAY_CLEARANCE);

                        break;
                    case InformationControl.INFORMATION_BUSINESS_CLEARANCE :
                        showPopupScene(mBusiClearanceFormScene, false);
                        break;
                }
            }

            @Override
            public void onViewButtonClicked(byte information, Object reportData) {

                switch (information) {
                    case InformationControl.INFORMATION_BARANGAY_ID :
                        mBarangayIDReportControl.setBarangayID(
                                (BarangayID) reportData, BarangayIDReportControl.REQUEST_DISPLAY_REPORT);
                        showPopupScene(mBarangayIDReportScene, false);
                        break;

                    case InformationControl.INFORMATION_BARANGAY_CLEARANCE:
                        mBrgyClearanceReportControl.setBarangayClearance(
                                (BarangayClearance) reportData, BarangayClearanceReportControl.REQUEST_DISPLAY_REPORT);
                        showPopupScene(mBrgyClearanceReportScene, false);
                        break;

                    case InformationControl.INFORMATION_BUSINESS_CLEARANCE:
                        mBusiClearanceReportControl.setBusinessClearance(
                                (BusinessClearance) reportData, BusinessClearanceReportControl.REQUEST_DISPLAY_REPORT);
                        showPopupScene(mBusiClearanceReportScene, false);
                        break;
                }
            }

            @Override
            public Image onRequestReportSnapshot(Object report) {
                // Image snap shot works only when the pane to be shot is visible.
                if (report instanceof BarangayID) {
                    showPopupScene(mBarangayIDReportScene, false);

                    Image image = mBarangayIDReportControl.setBarangayID(
                            (BarangayID) report, BarangayIDReportControl.REQUEST_SNAPSHOT_REPORT);

                    hidePopupScene(mBarangayIDReportScene, false);

                    return image;

                } else if (report instanceof BarangayClearance) {
                    showPopupScene(mBrgyClearanceReportScene, false);

                    Image image = mBrgyClearanceReportControl.setBarangayClearance(
                            (BarangayClearance) report, BarangayClearanceReportControl.REQUEST_SNAPSHOT_REPORT);

                    hidePopupScene(mBrgyClearanceReportScene, false);

                    return image;
                } else {
                    showPopupScene(mBusiClearanceReportScene, false);

                    Image image = mBusiClearanceReportControl.setBusinessClearance(
                            (BusinessClearance) report, BusinessClearanceReportControl.REQUEST_SNAPSHOT_REPORT);

                    hidePopupScene(mBusiClearanceReportScene, false);

                    return image;
                }
            }


        });

        // Add the information scenes to the mMainGridPane.
        mMainGridPane.add(mResidentScene, 1, 0);
        mMainGridPane.add(mInformationScene, 1, 0);

        // The default selected menu must be the resident menu.
        updateMenuSelected(MENU_RESIDENT);

        /**
         * Initially add the pop-ups to the mPopupStackPane. *Add, but not visible.
         */
        Consumer<Pane> addToPopupPane = (Pane popupPane) -> {
            popupPane.setVisible(false);
            mPopupStackPane.getChildren().add(popupPane);
            mPopupStackPane.setAlignment(popupPane, Pos.CENTER);
        };

        // Initialize the photoshop dialog.
        resetFXMLLoader.accept("fxml/scene_photoshop.fxml");
        mPhotoshopScene = fxmlLoader.load();
        mPhotoshopControl = fxmlLoader.getController();

        mPhotoshopControl.setListener(new PhotoshopControl.OnPhotoshopListener() {
            @Override
            public void onAcceptButtonClicked(byte client, WritableImage image) {
                hidePopupScene(mPhotoshopScene, true);

                // Return the image to the requesting client and re-enable their controller.
                switch (client) {
                    case PhotoshopControl.CLIENT_RESIDENT_PHOTO:
                        mResidentFormControl.setDisable(false);
                        mResidentFormControl.setPhoto(image);
                        break;
                    case PhotoshopControl.CLIENT_CHAIRMAN_PHOTO:
                        mBarangayAgentControl.setDisable(false);
                        mBarangayAgentControl.setChmPhoto(image);
                        break;
                    case PhotoshopControl.CLIENT_CHAIRMAN_SIGNATURE:
                        mBarangayAgentControl.setDisable(false);
                        mBarangayAgentControl.setChmSignature(image);
                        break;
                    case PhotoshopControl.CLIENT_SECRETARY_SIGNATURE:
                        mBarangayAgentControl.setDisable(false);
                        mBarangayAgentControl.setSecSignature(image);
                        break;
                    case PhotoshopControl.CLIENT_ID_SIGNATURE:
                        mResidentInformationFormControl.setDisable(false);
                        mResidentInformationFormControl.setSignature(image);
                        break;
                }
            }

            @Override
            public void onCancelButtonClicked(byte client) {
                hidePopupScene(mPhotoshopScene, true);

                // Re-enable the controller of the client.
                switch (client) {
                    case PhotoshopControl.CLIENT_RESIDENT_PHOTO:
                        mResidentFormControl.setDisable(false);
                        break;
                    case PhotoshopControl.CLIENT_CHAIRMAN_PHOTO:
                    case PhotoshopControl.CLIENT_CHAIRMAN_SIGNATURE:
                    case PhotoshopControl.CLIENT_SECRETARY_SIGNATURE:
                        mBarangayAgentControl.setDisable(false);
                        break;
                    case PhotoshopControl.CLIENT_ID_SIGNATURE:
                        mResidentInformationFormControl.setDisable(false);
                        break;
                }
            }

            @Override
            public void onWebcamInitializeError() {
                showPopupScene(mConfirmationDialogScene, true);
                mConfirmationDialogControl.setClient(ConfirmationDialogControl.CLIENT_WEBCAM_FAILURE);
            }
        });

        // Initialize the barangay agent setup dialog.
        resetFXMLLoader.accept("fxml/scene_barangay_agent.fxml");
        mBarangayAgentScene = fxmlLoader.load();
        mBarangayAgentControl = fxmlLoader.getController();
        mBarangayAgentControl.setPreferenceModel(mPreferenceModel);

        mBarangayAgentControl.setListener(new BarangayAgentControl.OnBarangayAgentListener() {
            @Override
            public void onChmUploadButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_CHAIRMAN_PHOTO, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }

            @Override
            public void onChmCaptureButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_CHAIRMAN_PHOTO, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onChmSignatureUploadButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_CHAIRMAN_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }

            @Override
            public void onChmSignatureCaptureButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_CHAIRMAN_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onSecSignatureUploadButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_SECRETARY_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }

            @Override
            public void onSecSignatureCaptureButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_SECRETARY_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onFinished() {
                hidePopupScene(mBarangayAgentScene, false);

                if (mPreferenceModel.get(PreferenceContract.BARANGAY_AGENTS_INITIALIZED, "0").equals("0"))
                    setLogout(true);
            }
        });

        // Initialize the resident deletion confirmation dialog.
        resetFXMLLoader.accept("fxml/scene_confirmation_dialog.fxml");
        mConfirmationDialogScene = fxmlLoader.load();
        mConfirmationDialogControl = fxmlLoader.getController();

        mConfirmationDialogControl.setListener(new ConfirmationDialogControl.OnConfirmationDialogListener() {
            @Override
            public void onConfirmButtonClicked(byte client) {
                switch (client) {
                    case ConfirmationDialogControl.CLIENT_RESIDENT_DELETION:
                        hidePopupScene(mConfirmationDialogScene, false);
                        mResidentControl.deleteSelectedResident();
                        break;

                    case ConfirmationDialogControl.CLIENT_BUSINESS_DELETION:
                        hidePopupScene(mConfirmationDialogScene, true);
                        mBusiClearanceFormControl.deleteSelectedBusiness();
                        mBusiClearanceFormControl.setDisable(false);

                        mInformationControl.updateListPaging();
                        break;

                    case ConfirmationDialogControl.CLIENT_WEBCAM_FAILURE:
                        hidePopupScene(mConfirmationDialogScene, true);
                        mPhotoshopControl.onCancelButtonClicked(null);
                        break;

                    case ConfirmationDialogControl.CLIENT_CHANGE_PASSWORD:
                        // This must come before hiding the change password scene. Else, the password will
                        // be turned to null.
                        Calendar calendar = mChangePasswordControl.savePassword();

                        hidePopupScene(mConfirmationDialogScene, true);
                        hidePopupScene(mChangePasswordScene, true);

                        mSecurityControl.setDisable(false);
                        mSecurityControl.updateDisplayedPassword();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                        mLastPwdUpdateDate.setText(dateFormat.format(calendar.getTime()));

                        dateFormat = new SimpleDateFormat("hh:mm aaa");
                        mLastPwdUpdateTime.setText(dateFormat.format(calendar.getTime()));
                        break;

                    case ConfirmationDialogControl.CLIENT_LOGOUT:
                        hidePopupScene(mConfirmationDialogScene, false);
                        setLogout(true);
                }
            }

            @Override
            public void onCancelButtonClicked(byte client) {
                switch (client) {
                    case ConfirmationDialogControl.CLIENT_RESIDENT_DELETION:
                        hidePopupScene(mConfirmationDialogScene, false);
                        break;

                    case ConfirmationDialogControl.CLIENT_BUSINESS_DELETION:
                        hidePopupScene(mConfirmationDialogScene, true);
                        mBusiClearanceFormControl.setDisable(false);
                        break;

                    case ConfirmationDialogControl.CLIENT_WEBCAM_FAILURE:
                        hidePopupScene(mConfirmationDialogScene, true);
                        break;

                    case ConfirmationDialogControl.CLIENT_CHANGE_PASSWORD:
                        hidePopupScene(mConfirmationDialogScene, true);
                        mChangePasswordControl.setDisable(false);
                        break;
                    case ConfirmationDialogControl.CLIENT_LOGOUT:
                        hidePopupScene(mConfirmationDialogScene, false);
                }
            }
        });

        // Initialize the resident form dialog.
        resetFXMLLoader.accept("fxml/scene_resident_form.fxml");
        mResidentFormScene = fxmlLoader.load();
        mResidentFormControl = fxmlLoader.getController();

        mResidentFormControl.setListener(new ResidentFormControl.OnResidentFormListener() {
            @Override
            public void onSaveButtonClicked(Resident resident) {
                hidePopupScene(mResidentFormScene, false);
                // If the returned resident has an ID, then simply update the resident information. Otherwise,
                // create a new resident.
                if (resident.getId() != null)
                    mResidentControl.updateResident(resident);
                else
                    mResidentControl.createResident(resident);
            }

            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mResidentFormScene, false);
            }

            @Override
            public void onTakePhotoButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mResidentFormControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_RESIDENT_PHOTO, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onUploadButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mResidentFormControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_RESIDENT_PHOTO, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }
        });

        // Initialize the resident info form dialog.
        resetFXMLLoader.accept("fxml/scene_resident_information_form.fxml");
        mResidentInfoFormScene = fxmlLoader.load();
        mResidentInformationFormControl = fxmlLoader.getController();

        mResidentInformationFormControl.setCacheModel(mCacheModel);
        mResidentInformationFormControl.setDatabaseModel(mDatabaseModel);

        mResidentInformationFormControl.setListener(new ResidentInformationFormControl.OnResidentInfoFormListener() {
            @Override
            public void onUploadButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mResidentInformationFormControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_ID_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }

            @Override
            public void onCaptureButtonClicked() {
                showPopupScene(mPhotoshopScene, true);
                mResidentInformationFormControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_ID_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mResidentInfoFormScene, false);
            }

            @Override
            public void onCreateButtonClicked(Object data, byte formType) {
                hidePopupScene(mResidentInfoFormScene, false);

                switch (formType) {
                    case ResidentInformationFormControl.FORM_BARANGAY_ID:
                        mBarangayIDReportControl.setBarangayID((BarangayID) data, BarangayIDReportControl.REQUEST_CREATE_REPORT);
                        showPopupScene(mBarangayIDReportScene, false);
                        break;

                    case ResidentInformationFormControl.FORM_BARANGAY_CLEARANCE:
                        mBrgyClearanceReportControl.setBarangayClearance(
                                (BarangayClearance) data, BarangayClearanceReportControl.REQUEST_CREATE_REPORT);
                        showPopupScene(mBrgyClearanceReportScene, false);
                        break;
                }

            }
        });

        // Initialize the barangay ID report.
        resetFXMLLoader.accept("fxml/scene_barangay_id_report.fxml");
        mBarangayIDReportScene = fxmlLoader.load();
        mBarangayIDReportControl = fxmlLoader.getController();

        mBarangayIDReportControl.setPreferenceModel(mPreferenceModel);
        mBarangayIDReportControl.setListener(new BarangayIDReportControl.OnBarangayIDReportListener() {
            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mBarangayIDReportScene, false);
            }

            @Override
            public void onSaveButtonClicked(BarangayID barangayID) {
                hidePopupScene(mBarangayIDReportScene, false);

                mInformationControl.createBarangayID(barangayID);
            }
        });

        // Initialize the barangay clearance report.
        resetFXMLLoader.accept("fxml/scene_barangay_clearance_report.fxml");
        mBrgyClearanceReportScene = fxmlLoader.load();

        mBrgyClearanceReportControl = fxmlLoader.getController();
        mBrgyClearanceReportControl.setPreferenceModel(mPreferenceModel);

        mBrgyClearanceReportControl.setListener(new BarangayClearanceReportControl.OnBarangayClearanceReportListener() {
            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mBrgyClearanceReportScene, false);
            }

            @Override
            public void onSaveButtonClicked(BarangayClearance barangayClearance) {
                hidePopupScene(mBrgyClearanceReportScene, false);

                mInformationControl.createBarangayClearance(barangayClearance);
            }
        });

        // Initialize the business clearance form.
        resetFXMLLoader.accept("fxml/scene_business_clearance_form.fxml");
        mBusiClearanceFormScene = fxmlLoader.load();

        mBusiClearanceFormControl = fxmlLoader.getController();
        mBusiClearanceFormControl.setCacheModel(mCacheModel);
        mBusiClearanceFormControl.setDatabaseModel(mDatabaseModel);
        mBusiClearanceFormControl.setListener(new BusinessClearanceFormControl.OnBusinessClearanceFormListener() {
            @Override
            public void onCreateButtonClicked(BusinessClearance businessClearance) {
                if (businessClearance == null)
                    mInformationControl.updateListPaging();
                else {
                    hidePopupScene(mBusiClearanceFormScene, false);
                    showPopupScene(mBusiClearanceReportScene, false);
                    mBusiClearanceReportControl.setBusinessClearance(
                            businessClearance, BusinessClearanceReportControl.REQUEST_CREATE_REPORT);
                }
            }

            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mBusiClearanceFormScene, false);
            }

            @Override
            public void onDeleteButtonClicked() {
                showPopupScene(mConfirmationDialogScene, true);
                mConfirmationDialogControl.setClient(ConfirmationDialogControl.CLIENT_BUSINESS_DELETION);
                mBusiClearanceFormControl.setDisable(true);
            }
        });

        // Initialize the business clearance report.
        resetFXMLLoader.accept("fxml/scene_business_clearance_report.fxml");
        mBusiClearanceReportScene = fxmlLoader.load();

        mBusiClearanceReportControl = fxmlLoader.getController();
        mBusiClearanceReportControl.setPreferenceModel(mPreferenceModel);
        mBusiClearanceReportControl.setListener(new BusinessClearanceReportControl.OnBusinessClearanceReportListener() {
            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mBusiClearanceReportScene, false);
            }

            @Override
            public void onSaveButtonClicked(BusinessClearance businessClearance) {
                hidePopupScene(mBusiClearanceReportScene, false);

                mInformationControl.createBusinessClearance(businessClearance);
            }
        });

        // Initialize the change password scene.
        resetFXMLLoader.accept("fxml/scene_change_password.fxml");
        mChangePasswordScene = fxmlLoader.load();

        mChangePasswordControl = fxmlLoader.getController();
        mChangePasswordControl.setPreferenceModel(mPreferenceModel);
        mChangePasswordControl.setListener(new ChangePasswordControl.OnPasswordControlListener() {
            @Override
            public void onSaveButtonClicked(boolean isFirstPassword) {
                if (isFirstPassword) {
                    mChangePasswordControl.savePassword();

                    hidePopupScene(mChangePasswordScene, false);
                    mSecurityControl.updateDisplayedPassword();

                    onSettingsButtonClicked(null);

                    String datetime = mPreferenceModel.get(PreferenceContract.LAST_PASSWORD_UPDATE, null);

                    if (datetime != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(Long.valueOf(datetime)));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                        mLastPwdUpdateDate.setText(dateFormat.format(calendar.getTime()));

                        dateFormat = new SimpleDateFormat("hh:mm aaa");
                        mLastPwdUpdateTime.setText(dateFormat.format(calendar.getTime()));
                    }
                    return;
                }

                showPopupScene(mConfirmationDialogScene, true);
                mConfirmationDialogControl.setClient(ConfirmationDialogControl.CLIENT_CHANGE_PASSWORD);
                mChangePasswordControl.setDisable(true);
            }

            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mChangePasswordScene, true);
                mSecurityControl.setDisable(false);
            }
        });

        // Initialize the security scene.
        resetFXMLLoader.accept("fxml/scene_security.fxml");
        mSecurityScene = fxmlLoader.load();

        mSecurityControl = fxmlLoader.getController();
        mSecurityControl.setPreferenceModel(mPreferenceModel);
        mSecurityControl.updateDisplayedPassword();

        mSecurityControl.setListener(new SecurityControl.OnSecurityControlListener() {
            @Override
            public void onDoneButtonClicked() {
                hidePopupScene(mSecurityScene, false);

            }

            @Override
            public void onChangePasswordButtonClicked() {
                showPopupScene(mChangePasswordScene, true);
                mSecurityControl.setDisable(true);
            }

            @Override
            public void onIdleComboBoxValueChanged(int newValue) {
                mLogoutTimer.start(newValue * 60);
            }
        });

        // Add the dialog scenes to mPopupStackPane.
        addToPopupPane.accept(mPhotoshopScene);
        addToPopupPane.accept(mBarangayAgentScene);
        addToPopupPane.accept(mConfirmationDialogScene);
        addToPopupPane.accept(mResidentFormScene);
        addToPopupPane.accept(mResidentInfoFormScene);
        addToPopupPane.accept(mBarangayIDReportScene);
        addToPopupPane.accept(mBrgyClearanceReportScene);
        addToPopupPane.accept(mBusiClearanceFormScene);
        addToPopupPane.accept(mBusiClearanceReportScene);
        addToPopupPane.accept(mChangePasswordScene);
        addToPopupPane.accept(mSecurityScene);

        // Initialize the login scene.
        resetFXMLLoader.accept("fxml/scene_login.fxml");
        mLoginScene = fxmlLoader.load();

        mLoginControl = fxmlLoader.getController();
        mLoginControl.setPreferenceModel(mPreferenceModel);
        mLoginControl.setListener(new LoginControl.OnLoginControlListener() {
            @Override
            public void onExitButtonClicked() {
                System.exit(0);
            }

            @Override
            public void onLoginButtonClicked(byte action) {
                switch (action) {
                    case LoginControl.ACTION_LOGIN:
                        setLogout(false);
                        break;

                    case LoginControl.ACTION_RESET:
                        mPreferenceModel.delete();
                        System.exit(0);
                }
            }
        });

        // The Login scene is a unique pop-up dialog due to its volatility of suddenly
        // appearing on top of everything when the max idle time is reached. Thus,
        // it is given its own pop-up pane.
        mPopupLoginPane.getChildren().add(mLoginScene);
        mPopupLoginPane.setAlignment(mLoginScene, Pos.CENTER);

        // When the application hasn't been initialized yet. Then initialize the first
        // data of the preference model.
        if (mPreferenceModel.get(PreferenceContract.BARANGAY_AGENTS_INITIALIZED, "0").equals("0")) {
            mPreferenceModel.delete();
            showPopupScene(mChangePasswordScene, false);
        } else
            setLogout(true);
    }

    /**
     * Select the resident menu.
     *
     * @param event
     *        The action event. No usage.
     */
    @FXML
    public void onResidentMenuClicked(Event event) {
        if(mMenuSelected != MENU_RESIDENT)
            updateMenuSelected(MENU_RESIDENT);
    }

    /**
     * Select the barangay clearance menu.
     *
     * @param event
     *        The action event. No usage.
     */
    @FXML
    public void onBarangayClearanceMenuClicked(Event event) {
        if(mMenuSelected != MENU_BARANGAY_CLEARANCE)
            updateMenuSelected(MENU_BARANGAY_CLEARANCE);
    }

    /**
     * Select the barangay ID menu.
     *
     * @param event
     *        The action event. No usage.
     */
    @FXML
    public void onBarangayIdMenuClicked(Event event) {
        if(mMenuSelected != MENU_BARANGAY_ID)
            updateMenuSelected(MENU_BARANGAY_ID);
    }

    /**
     * Select the business clearance menu.
     *
     * @param event
     *        The action event. No usage.
     */
    @FXML
    public void onBusinessClearanceMenuClicked(Event event) {
        if(mMenuSelected != MENU_BUSINESS_CLEARANCE)
            updateMenuSelected(MENU_BUSINESS_CLEARANCE);
    }

    /**
     * Show the barangay agent setup form.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onSettingsButtonClicked(ActionEvent actionEvent) {
        showPopupScene(mBarangayAgentScene, false);
    }

    /**
     * Show the security scene.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onSecurityButtonClicked(ActionEvent actionEvent) {
        showPopupScene(mSecurityScene, false);
    }

    /**
     * Backup the database and the whole application data folder.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onBackupButtonClicked(ActionEvent actionEvent) {
        // Setup the save file chooser dialog.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Backup File");
        fileChooser.setInitialFileName("Brgy131-bak");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));

        File targetFile = fileChooser.showSaveDialog(Main.getPrimaryStage());

        // If a target file is chosen, then start backup process.
        if (targetFile != null)
            try {
                // Generate a compressed backup file and place it in the application data directory.
                // Backup file is named 'brgy131_bak.rar'.
                Process p = Runtime.getRuntime().exec("cmd.exe /c start /wait c:\\mysql\\backup.exe");
                p.waitFor();

                // Once the back up file is generated, transfer it to the target path specified by
                // the user.
                File sourceFile = new File(System.getenv("PUBLIC") + "/brgy131-bak.rar");
                Files.move(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Restore a specified backup file.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onRestoreButtonClicked(ActionEvent actionEvent) {
        // Setup the file chooser dialog.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Restore Backup file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));

        // Get the source file.
        File sourceFile = fileChooser.showOpenDialog(Main.getPrimaryStage());

        if (sourceFile != null)
            try {
                // Create the target file.
                File targetFile = new File(System.getenv("PUBLIC") + "/brgy131-bak.rar");

                // Copy the source file to the target file.
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Run recovery batch file.
                Process p = Runtime.getRuntime().exec("cmd.exe /c start /wait c:\\mysql\\recover.exe");
                p.waitFor();

                // Close the application once the recovery is done.
                System.exit(0);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    /**
     * Logout the application.
     *
     * @param actionEvent
     *        The action event. No usage.
     */
    @FXML
    public void onLogoutButtonClicked(ActionEvent actionEvent) {
        showPopupScene(mConfirmationDialogScene, false);
        mConfirmationDialogControl.setClient(ConfirmationDialogControl.CLIENT_LOGOUT);
    }
    /**
     * Update the current menu selected.
     * @param menu clicked.
     */
    private void updateMenuSelected(byte menu) {
        if (mMenuSelected == menu) return;

        /**
         * Manage menu slide animations.
         * @param menuPane to be animated.
         * @param isSelected determines the type of animation (selected or not selected) to be applied to the menuPane.
         */
        BiConsumer<Pane, Boolean> playMenuSlideAnimation = (menuPane, isSelected) -> {
            menuPane.setStyle(isSelected ? CSSContract.STYLE_MENU_SELECTED : CSSContract.STYLE_MENU_UNSELECTED);

            menuPane.getChildren().remove(mRectAnimTransitioner);
            menuPane.getChildren().add(mRectAnimTransitioner);
            menuPane.getChildren().get(menuPane.getChildren().size() - 1).toBack();

            Thread thread = new Thread(new Task() {
                @Override
                protected Object call() throws Exception {
                    for (int i = 0; i < 10; i++) {
                        final int j = i;
                        Platform.runLater(() -> mMenuGridPane.setMargin(menuPane, new Insets(0, 0, 0, isSelected ? j : 9 - j)));
                        Thread.sleep(10);
                    }
                    return null;
                }
            });

            thread.setDaemon(true);
            thread.start();
        };

        /**
         * set the menu referenced by mMenuSelected as either selected or not selected.
         * @param isSelected is true if the menu is to be selected, otherwise unselect it.
         */
        Consumer<Boolean> setMenuSelected = (isSelected) -> {
            switch (mMenuSelected) {
                case MENU_RESIDENT:
                    playMenuSlideAnimation.accept(mResidentMenu, isSelected);
                    if (isSelected) {
                        mResidentScene.toFront();
                        mResidentControl.resetCachedData();
                    }
                    break;

                case MENU_BARANGAY_CLEARANCE:
                    playMenuSlideAnimation.accept(mBarangayClearanceMenu, isSelected);
                    if (isSelected) {
                        mInformationControl.setInformation(InformationControl.INFORMATION_BARANGAY_CLEARANCE);
                        mInformationScene.toFront();
                    }
                    break;

                case MENU_BARANGAY_ID:
                    playMenuSlideAnimation.accept(mBarangayIdMenu, isSelected);
                    if (isSelected) {
                        mInformationControl.setInformation(InformationControl.INFORMATION_BARANGAY_ID);
                        mInformationScene.toFront();
                    }
                    break;

                case MENU_BUSINESS_CLEARANCE:
                    playMenuSlideAnimation.accept(mBusinessClearanceMenu, isSelected);
                    if (isSelected) {
                        mInformationControl.setInformation(InformationControl.INFORMATION_BUSINESS_CLEARANCE);
                        mInformationScene.toFront();
                    }

                    break;
            }
        };

        // Update the previous menu selected to 'unselected'.
        setMenuSelected.accept(false);

        // Assign the new menu selected to mMenuSelected.
        mMenuSelected = menu;

        setMenuSelected.accept(true);
        // Add the currently selected menu to the mMainGridPane and update its visual state in the mMenuGridPane
        // to 'selected'.
    }

    /**
     * Hide the specified pop-up.
     *
     * @param popupScene
     *        The pop-up scene to be hidden.
     * @param isOtherPopupVisible
     *        Determines whether other pop-ups are still visible within the pop-up stack pane.
     *        If yes, then do not hide the pop-up stack pane. Otherwise, hide it.
     */
    private void hidePopupScene(Pane popupScene, boolean isOtherPopupVisible) {
        if (isOtherPopupVisible)
            popupScene.setVisible(false);
        else {
            mMainGridPane.setEffect(null);
            mMainGridPane.setDisable(false);
            popupScene.setVisible(false);

            mPopupStackPane.setVisible(false);

            switch (mMenuSelected) {
                case MENU_RESIDENT : mResidentControl.setBlurListPaging(false); break;
                default : mInformationControl.setBlurListPaging(false); break;
            }
        }
    }

    /**
     * Show the specified pop-up.
     *
     * @param popupScene
     *        The pop-up scene to be show.
     * @param isOtherPopupVisible
     *        Determines whether other pop-ups are already visible within the pop-up stack pane.
     *        If yes, then do not show pop-up stack pane, since it is already visible. Otherwise,
     *        show it.
     */
    private void showPopupScene(Pane popupScene, boolean isOtherPopupVisible) {
        // If a pop-up is visible aside from the popupScene, then no need to re-blur the mMainGridPane.
        if (!isOtherPopupVisible) {
            mMainGridPane.setEffect(new GaussianBlur());
            mMainGridPane.setDisable(true);
        }

        popupScene.setVisible(true);
        popupScene.toFront();
        mPopupStackPane.setVisible(true);

        switch (mMenuSelected) {
            case MENU_RESIDENT : mResidentControl.setBlurListPaging(true); break;
            default : mInformationControl.setBlurListPaging(true); break;
        }
    }

    /**
     * Login or logout the application.
     *
     * @param bool
     *        Determines whether to login or logout the application.
     */
    private void setLogout(boolean bool) {
        if (bool) {
            mLogoutTimer.stop();
            mPopupLoginPane.setVisible(true);

            if (mPopupStackPane.isVisible()) {
                mPopupStackPane.setEffect(new GaussianBlur());
                mPopupStackPane.setDisable(true);
            } else {
                mMainGridPane.setEffect(new GaussianBlur());
                mMainGridPane.setDisable(true);

                switch (mMenuSelected) {
                    case MENU_RESIDENT : mResidentControl.setBlurListPaging(true); break;
                    default : mInformationControl.setBlurListPaging(true); break;
                }
            }


        } else {
            String value = mPreferenceModel.get(PreferenceContract.MAX_IDLE_DURATION, "5");
            mLogoutTimer.start(Integer.valueOf(value) * 60);

            mPopupLoginPane.setVisible(false);

            if (mPopupStackPane.isVisible()) {
                mPopupStackPane.setEffect(null);
                mPopupStackPane.setDisable(false);
            } else {
                mMainGridPane.setEffect(null);
                mMainGridPane.setDisable(false);

                switch (mMenuSelected) {
                    case MENU_RESIDENT : mResidentControl.setBlurListPaging(false); break;
                    default : mInformationControl.setBlurListPaging(false); break;
                }
            }

            String datetime = mPreferenceModel.get(PreferenceContract.LAST_LOGIN, null);

            if (datetime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(Long.valueOf(datetime)));

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");

                mLastLoginDate.setText(dateFormat.format(calendar.getTime()));
                mLastLoginTime.setText(timeFormat.format(calendar.getTime()));
            } else {
                mLastLoginDate.setText(null);
                mLastLoginTime.setText(null);
            }

            Calendar calendar = Calendar.getInstance();
            mPreferenceModel.put(PreferenceContract.LAST_LOGIN, calendar.getTime().getTime() + "");
            mPreferenceModel.save(false);
        }
    }
}
