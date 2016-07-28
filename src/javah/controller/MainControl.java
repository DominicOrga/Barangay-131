package javah.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javah.util.CacheManager;

import java.io.IOException;
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

    private CacheManager mCacheManager;

    @FXML
    private void initialize() {

        mCacheManager = new CacheManager();

        // Initialize the mRectAnimTransitioner.
        mRectAnimTransitioner = new Rectangle();
        mRectAnimTransitioner.setWidth(mMenuGridPane.getWidth());
        mRectAnimTransitioner.setHeight(mResidentMenu.getHeight() - 1);

        // Initialize the information scenes.
        FXMLLoader fxmlLoader = new FXMLLoader();
        initializeResidentScene(fxmlLoader);

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

    /**
     * Initialize the resident scene and add it to mMainGridPane(1,0).
     * @param fxmlLoader
     */
    public void initializeResidentScene(FXMLLoader fxmlLoader) {
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("fxml/scene_resident.fxml"));
        fxmlLoader.setRoot(null);
        fxmlLoader.setController(null);

        try {
            mResidentScene = fxmlLoader.load();
            ResidentControl control = fxmlLoader.getController();
            control.setCacheManager(mCacheManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMainGridPane.add(mResidentScene, 1, 0);
    }

    private void clearPopupStackPane() {
        mMainGridPane.setEffect(null);
        mMainGridPane.setDisable(false);

        mPopupStackPane.getChildren().remove(0);
        mPopupStackPane.setVisible(false);
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
