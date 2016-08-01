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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javah.container.Resident;
import javah.controller.resident.ResidentDeletionControl;
import javah.controller.resident.ResidentFormControl;
import javah.controller.resident.ResidentControl;
import javah.model.CacheModel;
import javah.model.DatabaseModel;

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
    private Pane mResidentScene, mBarangayIdScene, mBarangayClearanceScene, mBusinessClearanceScene, mBlotterScene;

    /**
     * The popup scenes.
     */
    private Pane mResidentDeletionScene, mResidentFormScene;

    /**
     * The scene controllers.
     */
    private ResidentControl mResidentControl;
    private ResidentDeletionControl mResidentDeletionControl;
    private ResidentFormControl mResidentFormControl;
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

    /**
     * Initialize the scenes, both menu and dialogs.
     * @throws Exception
     */
    @FXML
    private void initialize() throws Exception {
        mDatabaseModel = new DatabaseModel();
        mCacheModel = new CacheModel();

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
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("fxml/resident/scene_resident.fxml"));

        mResidentScene = fxmlLoader.load();
        mResidentControl = fxmlLoader.getController();

        mResidentControl.setDatabaseModel(mDatabaseModel);
        mResidentControl.setCacheModel(mCacheModel);

        mResidentControl.setListener(new ResidentControl.OnResidentSceneListener() {
            @Override
            public void onNewResidentButtonClicked() {
                mResidentControl.setBlurListPaging(true);
                showPopupScene(mResidentFormScene, false);
            }

            @Override
            public void onEditResidentButtonClicked(Resident resident) {

            }

            @Override
            public void onDeleteResidentButtonClicked(Resident resident) {
                mResidentControl.setBlurListPaging(true);
                mResidentDeletionControl.setNameLabel(resident.getFirstName());
                showPopupScene(mResidentDeletionScene, false);
            }
        });

        // Add the information scenes to the mMainGridPane.
        mMainGridPane.add(mResidentScene, 1, 0);


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

        // Initialize the resident deletion confirmation dialog.
        resetFXMLLoader.accept("fxml/resident/scene_resident_deletion.fxml");
        mResidentDeletionScene = fxmlLoader.load();
        mResidentDeletionControl = fxmlLoader.getController();

        mResidentDeletionControl.setListener(new ResidentDeletionControl.OnResidentDeletionListener() {
            @Override
            public void onDeleteButtonClicked() {
                mResidentControl.deleteSelectedResident();
                mResidentControl.setBlurListPaging(false);
                hidePopupScene(mResidentDeletionScene);
                mResidentControl.setBlurListPaging(false);
            }

            @Override
            public void onCancelButtonClicked() {
                mResidentControl.setBlurListPaging(false);
                hidePopupScene(mResidentDeletionScene);
                mResidentControl.setBlurListPaging(false);
            }
        });

        // Initialize the resident form dialog.
        resetFXMLLoader.accept("fxml/resident/scene_resident_form.fxml");
        mResidentFormScene = fxmlLoader.load();
        mResidentFormControl = fxmlLoader.getController();

        mResidentFormControl.setListener(new ResidentFormControl.OnResidentFormListener() {
            @Override
            public void onSaveButtonClicked(Resident resident) {
                mResidentControl.createResident(resident);
                mResidentControl.setBlurListPaging(false);
                hidePopupScene(mResidentFormScene);
            }

            @Override
            public void onCancelButtonClicked() {
                mResidentControl.setBlurListPaging(false);
                hidePopupScene(mResidentFormScene);
            }
        });

        // Add the dialog scenes to mPopupStackPane.
        addToPopupPane.accept(mResidentDeletionScene);
        addToPopupPane.accept(mResidentFormScene);
    }

    /**
     * Update the current menu selected.
     * @param menu clicked.
     */
    private void updateMenuSelected(byte menu) {

        /**
         * Manage menu slide animations.
         * @param menuPane to be animated.
         * @param isSelected determines the type of animation (selected or not selected) to be applied to the menuPane.
         */
        BiConsumer<Pane, Boolean> playMenuSlideAnimation = (menuPane, isSelected) -> {
            menuPane.setStyle(isSelected ? "-fx-background-color : chocolate; " +
                    "-fx-border-color : white; " +
                    "-fx-border-width: 0 0 1 0;" :
                    "-fx-background-color : #FF8600; " +
                            "-fx-border-color : white; " +
                            "-fx-border-width: 0 0 1 0");

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
                    if (isSelected)
                        mMainGridPane.getChildren().get(mMainGridPane.getChildren().indexOf(mResidentScene)).toFront();
                    break;

                case MENU_BARANGAY_CLEARANCE:
                    playMenuSlideAnimation.accept(mBarangayClearanceMenu, isSelected); break;

                case MENU_BARANGAY_ID:
                    playMenuSlideAnimation.accept(mBarangayIdMenu, isSelected);
                    if (isSelected)
                        mMainGridPane.getChildren().get(mMainGridPane.getChildren().indexOf(mBarangayIdScene)).toFront();
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

    private void hidePopupScene(Pane popupScene) {
        mMainGridPane.setEffect(null);
        mMainGridPane.setDisable(false);

        popupScene.setVisible(false);
        mPopupStackPane.setVisible(false);
    }

    private void showPopupScene(Pane popupScene, boolean isOtherPopupVisible) {
        // If a pop-up is visible aside from the popupScene, then no need to re-blur the mMainGridPane.
        if (!isOtherPopupVisible) {
            mMainGridPane.setEffect(new GaussianBlur());
            mMainGridPane.setDisable(true);
        }

        popupScene.setVisible(true);
        mPopupStackPane.setVisible(true);
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
}
