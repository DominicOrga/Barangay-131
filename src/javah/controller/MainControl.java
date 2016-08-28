package javah.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javah.container.BarangayClearance;
import javah.container.BarangayID;
import javah.container.Resident;
import javah.contract.CSSContract;
import javah.model.CacheModel;
import javah.model.DatabaseModel;
import javah.model.PreferenceModel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MainControl {

    /**
     * The container for the pop up dialogs or forms.
     */
    @FXML private StackPane mPopupStackPane;

    /**
     * The designated root view of the main scene at the back of the mPopupStackPane.
     * Loaded in order to apply gaussian blur to the background when a pop up dialog is displayed.
     */
    @FXML private GridPane mMainGridPane;

    /**
     * The container for the menu buttons, settings, time, welcome message and logout button.
     * Loaded to edit the display of the menu buttons to determine which one is currently selected.
     */
    @FXML private GridPane mMenuGridPane;

    /**
     * The Welcome message at the first row and first column of mMenuGridPane.
     */
    @FXML private Label mUserLabel;

    /**
     * The menu buttons.
     */
    @FXML private Pane mResidentMenu, mBarangayClearanceMenu, mBarangayIdMenu, mBusinessClearanceMenu, mBlotterMenu;

    /**
     * The information scenes.
     */
    private Pane mResidentScene, mInformationScene;

    /**
     * The information scene controllers
     */
    private ResidentControl mResidentControl;
    private InformationControl mInformationControl;

    /**
     * The popup scenes. (CONFIRMATION)
     */
    private Pane mResidentDeletionScene;

    /**
     * The popup scenes. (FORMS)
     */
    private Pane mPhotoshopScene, mBarangayAgentScene;
    private Pane mResidentFormScene;
    private Pane mResidentInfoFormScene;

    /**
     * The popup scenes. (REPORTS)
     */
    private Pane mBarangayIDReportScene;
    private Pane mBrgyClearanceReportScene;

    /**
     * The popup scene controllers.
     */
    private ConfirmationDialogControl mConfirmationDialogControl;
    private ResidentFormControl mResidentFormControl;
    private PhotoshopControl mPhotoshopControl;
    private BarangayAgentControl mBarangayAgentControl;
    private ResidentInformationFormControl mResidentInformationFormControl;
    private BarangayIDReportControl mBarangayIDReportControl;
    private BarangayClearanceReportControl mBrgyClearanceReportControl;


    /**
     * Key-value pairs to represent each menu.
     */
    public final byte MENU_RESIDENT = 1,
            MENU_BARANGAY_ID = 2,
            MENU_BARANGAY_CLEARANCE = 3,
            MENU_BUSINESS_CLEARANCE = 4,
            MENU_BLOTTER = 5;

    /**
     * Holds the value of the currently selected menu.
     */
    private byte mMenuSelected;

    /**
     * The rectangle object used to assist menu animation.
     */
    private Rectangle mRectAnimTransitioner;

    private CacheModel mCacheModel;
    private DatabaseModel mDatabaseModel;
    private PreferenceModel mPreferenceModel;

    /**
     * Initialize all the scenes.
     * @throws Exception
     */
    @FXML
    private void initialize() throws Exception {

        mDatabaseModel = new DatabaseModel();
        mCacheModel = new CacheModel();
        mPreferenceModel = new PreferenceModel();

        // Initialize the mRectAnimTransitioner.
        // *Used in updateMenuSelected() to aid in animation.
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
                mResidentControl.setBlurListPaging(true);
                showPopupScene(mResidentFormScene, false);
            }

            @Override
            public void onEditResidentButtonClicked(Resident resident) {
                mResidentControl.setBlurListPaging(true);
                mResidentFormControl.setResident(resident);
                showPopupScene(mResidentFormScene, false);
            }

            @Override
            public void onDeleteResidentButtonClicked(Resident resident) {
                mResidentControl.setBlurListPaging(true);
                mConfirmationDialogControl.setNameLabel(resident.getFirstName());
                showPopupScene(mResidentDeletionScene, false);
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
                mInformationControl.setBlurListPaging(true);
                showPopupScene(mResidentInfoFormScene, false);

                switch (information) {
                    case InformationControl.INFORMATION_BARANGAY_ID :
                        mResidentInformationFormControl.setInformation(ResidentInformationFormControl.INFORMATION_BARANGAY_ID);

                        break;
                    case InformationControl.INFORMATION_BARANGAY_CLEARANCE :
                        mResidentInformationFormControl.setInformation(ResidentInformationFormControl.INFORMATION_BARANGAY_CLEARANCE);
                        break;
                    case InformationControl.INFORMATION_BUSINESS_CLEARANCE : break;
                    case InformationControl.INFORMATION_BLOTTER :
                }

                mResidentInformationFormControl.reset();
            }

            @Override
            public void onViewButtonClicked(byte information, Object reportData) {
                switch (information) {
                    case InformationControl.INFORMATION_BARANGAY_ID :
                        mInformationControl.setBlurListPaging(true);
                        mBarangayIDReportControl.setBarangayID(
                                (BarangayID) reportData, BarangayIDReportControl.REQUEST_DISPLAY_REPORT);
                        showPopupScene(mBarangayIDReportScene, false);
                        break;
                    case InformationControl.INFORMATION_BARANGAY_CLEARANCE:
                        mInformationControl.setBlurListPaging(true);
                        mBrgyClearanceReportControl.setBarangayClearance(
                                (BarangayClearance) reportData, BarangayClearanceReportControl.REQUEST_DISPLAY_REPORT);
                        showPopupScene(mBrgyClearanceReportScene, false);
                        break;
                    case InformationControl.INFORMATION_BUSINESS_CLEARANCE: break;
                    case InformationControl.INFORMATION_BLOTTER:
                }
            }

            @Override
            public Image OnRequestBarangayClearanceSnapshot(BarangayClearance barangayClearance) {
                // Image snap shot works only when the pane to be shot is visible.
                showPopupScene(mBrgyClearanceReportScene, false);
                Image image = mBrgyClearanceReportControl.setBarangayClearance(barangayClearance, BarangayClearanceReportControl.REQUEST_SNAPSHOT_REPORT);
                hidePopupScene(mBrgyClearanceReportScene, false);

                return image;
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
        });

        // Initialize the barangay agent setup dialog.
        resetFXMLLoader.accept("fxml/scene_barangay_agent.fxml");
        mBarangayAgentScene = fxmlLoader.load();
        mBarangayAgentControl = fxmlLoader.getController();
        mBarangayAgentControl.setPreferenceModel(mPreferenceModel);

        mBarangayAgentControl.setListener(new BarangayAgentControl.OnBarangayAgentListener() {
            @Override
            public void onChmUploadButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_CHAIRMAN_PHOTO, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }

            @Override
            public void onChmCaptureButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_CHAIRMAN_PHOTO, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onChmSignatureUploadButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_CHAIRMAN_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }

            @Override
            public void onChmSignatureCaptureButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_CHAIRMAN_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onSecSignatureUploadButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_SECRETARY_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }

            @Override
            public void onSecSignatureCaptureButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mBarangayAgentControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_SECRETARY_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mBarangayAgentScene, false);

                // When the barangay agent form scene is displayed, then blur the list paging of the
                // current menu selected.
                switch (mMenuSelected) {
                    case MENU_RESIDENT : mResidentControl.setBlurListPaging(false); break;
                    default : mInformationControl.setBlurListPaging(false); break;
                }
            }

            @Override
            public void onSaveButtonClicked() {
                hidePopupScene(mBarangayAgentScene, false);

                // When the barangay agent form scene is displayed, then blur the list paging of the
                // current menu selected.
                switch (mMenuSelected) {
                    case MENU_RESIDENT : mResidentControl.setBlurListPaging(false); break;
                    default : mInformationControl.setBlurListPaging(false); break;
                }
            }
        });

        // Initialize the resident deletion confirmation dialog.
        resetFXMLLoader.accept("fxml/scene_confirmation_dialog.fxml");
        mResidentDeletionScene = fxmlLoader.load();
        mConfirmationDialogControl = fxmlLoader.getController();

        mConfirmationDialogControl.setListener(new ConfirmationDialogControl.OnResidentDeletionListener() {
            @Override
            public void onDeleteButtonClicked() {
                hidePopupScene(mResidentDeletionScene, false);
                mResidentControl.deleteSelectedResident();
                mResidentControl.setBlurListPaging(false);
            }

            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mResidentDeletionScene, false);
                mResidentControl.setBlurListPaging(false);
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

                mResidentControl.setBlurListPaging(false);

            }

            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mResidentFormScene, false);
                mResidentControl.setBlurListPaging(false);
            }

            @Override
            public void onTakePhotoButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mResidentFormControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_RESIDENT_PHOTO, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onUploadButtonClicked() {
                mPhotoshopControl.reset();
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
        mResidentInformationFormControl.setPreferenceModel(mPreferenceModel);

        mResidentInformationFormControl.setListener(new ResidentInformationFormControl.OnResidentInfoFormListener() {
            @Override
            public void onUploadButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mResidentInformationFormControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_ID_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_UPLOAD);
            }

            @Override
            public void onCaptureButtonClicked() {
                mPhotoshopControl.reset();
                showPopupScene(mPhotoshopScene, true);
                mResidentInformationFormControl.setDisable(true);
                mPhotoshopControl.setClient(PhotoshopControl.CLIENT_ID_SIGNATURE, PhotoshopControl.REQUEST_PHOTO_CAPTURE);
            }

            @Override
            public void onCancelButtonClicked() {
                hidePopupScene(mResidentInfoFormScene, false);
                mInformationControl.setBlurListPaging(false);
            }

            @Override
            public void onCreateButtonClicked(Object data, byte information) {
                hidePopupScene(mResidentInfoFormScene, false);

                switch (information) {
                    case ResidentInformationFormControl.INFORMATION_BARANGAY_ID :
                        mBarangayIDReportControl.setBarangayID((BarangayID) data, BarangayIDReportControl.REQUEST_CREATE_REPORT);
                        showPopupScene(mBarangayIDReportScene, false);
                        break;

                    case ResidentInformationFormControl.INFORMATION_BARANGAY_CLEARANCE:
                        mBrgyClearanceReportControl.setBarangayClearance(
                                (BarangayClearance) data, BarangayClearanceReportControl.REQUEST_CREATE_REPORT);
                        showPopupScene(mBrgyClearanceReportScene, false);
                        break;

                    case ResidentInformationFormControl.INFORMATION_BUSINESS_CLEARANCE: break;
                    case ResidentInformationFormControl.INFORMATION_BLOTTER: break;
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
                mInformationControl.setBlurListPaging(false);
            }

            @Override
            public void onSaveButtonClicked(BarangayID barangayID) {
                hidePopupScene(mBarangayIDReportScene, false);
                mInformationControl.setBlurListPaging(false);

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
                mInformationControl.setBlurListPaging(false);
            }

            @Override
            public void onSaveButtonClicked(BarangayClearance barangayClearance) {
                hidePopupScene(mBrgyClearanceReportScene, false);
                mInformationControl.setBlurListPaging(false);

                mInformationControl.createBarangayClearance(barangayClearance);
            }
        });

        // Add the dialog scenes to mPopupStackPane.
        addToPopupPane.accept(mPhotoshopScene);
        addToPopupPane.accept(mBarangayAgentScene);
        addToPopupPane.accept(mResidentDeletionScene);
        addToPopupPane.accept(mResidentFormScene);
        addToPopupPane.accept(mResidentInfoFormScene);
        addToPopupPane.accept(mBarangayIDReportScene);
        addToPopupPane.accept(mBrgyClearanceReportScene);
    }

    @FXML
    public void onResidentMenuClicked(Event event) {
        if(mMenuSelected != MENU_RESIDENT)
            updateMenuSelected(MENU_RESIDENT);
    }

    @FXML
    public void onBarangayClearanceMenuClicked(Event event) {
        if(mMenuSelected != MENU_BARANGAY_CLEARANCE)
            updateMenuSelected(MENU_BARANGAY_CLEARANCE);
    }

    @FXML
    public void onBarangayIdMenuClicked(Event event) {
        if(mMenuSelected != MENU_BARANGAY_ID)
            updateMenuSelected(MENU_BARANGAY_ID);
    }

    @FXML
    public void onBusinessClearanceMenuClicked(Event event) {
        if(mMenuSelected != MENU_BUSINESS_CLEARANCE)
            updateMenuSelected(MENU_BUSINESS_CLEARANCE);
    }

    @FXML
    public void onBlotterMenuClicked(Event event) {
        if(mMenuSelected != MENU_BLOTTER)
            updateMenuSelected(MENU_BLOTTER);
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
                    if (isSelected)
                        mInformationControl.setInformation(InformationControl.INFORMATION_BARANGAY_CLEARANCE);
                    mInformationScene.toFront();
                    break;

                case MENU_BARANGAY_ID:
                    playMenuSlideAnimation.accept(mBarangayIdMenu, isSelected);
                    if (isSelected)
                        mInformationControl.setInformation(InformationControl.INFORMATION_BARANGAY_ID);
                    mInformationScene.toFront();
                    break;

                case MENU_BUSINESS_CLEARANCE:
                    playMenuSlideAnimation.accept(mBusinessClearanceMenu, isSelected); break;
                case MENU_BLOTTER:
                    playMenuSlideAnimation.accept(mBlotterMenu, isSelected);
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

    private void hidePopupScene(Pane popupScene, boolean isOtherPopupVisible) {
        if (isOtherPopupVisible)
            popupScene.setVisible(false);
        else {
            mMainGridPane.setEffect(null);
            mMainGridPane.setDisable(false);
            popupScene.setVisible(false);

            mPopupStackPane.setVisible(false);
        }

    }

    private void showPopupScene(Pane popupScene, boolean isOtherPopupVisible) {
        // If a pop-up is visible aside from the popupScene, then no need to re-blur the mMainGridPane.
        if (!isOtherPopupVisible) {
            mMainGridPane.setEffect(new GaussianBlur());
            mMainGridPane.setDisable(true);
        }

        popupScene.setVisible(true);
        popupScene.toFront();
        mPopupStackPane.setVisible(true);
    }

    /**
     * Show the barangay agent setup form.
     * @param mouseEvent
     */
    public void onSettingsButtonClicked(MouseEvent mouseEvent) {
        showPopupScene(mBarangayAgentScene, false);
        // Initialize the data first.
        mBarangayAgentControl.resetScene();

        switch (mMenuSelected) {
            case MENU_RESIDENT : mResidentControl.setBlurListPaging(true); break;
            default : mInformationControl.setBlurListPaging(true); break;
        }
    }
}
