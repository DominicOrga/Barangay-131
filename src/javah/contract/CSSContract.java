package javah.contract;

/**
 * A class that contains a list of static CSS properties for the fxml components.
 */
public class CSSContract {

    private static String[] prop;

    static {
        prop = new String[18];

        prop[0] = "-fx-background-color: #0080FF;"; // neon blue
        prop[1] = "-fx-background-color: #F4F4F4;"; // greyish white
        prop[2] = "-fx-background-color: #FF3f3f;"; // red
        prop[3] = "-fx-background-color: #FF861B;"; // orange
        prop[4] = "-fx-background-color: black;";
        prop[5] = "-fx-background-color: chocolate;";
        prop[6] = "-fx-background-color: white;";

        prop[7] = "-fx-border-color: #BEBEBE;"; // Grey
        prop[8] = "-fx-border-color: #FF3F3F;"; // Red
        prop[9] = "-fx-border-color: white;";

        prop[10] = "-fx-border-width: 0 0 1 0;";

        prop[11] = "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 2.0);";

        prop[12] = "-fx-font-size: 20;";

        prop[13] = "-fx-text-fill: chocolate;";
        prop[14] = "-fx-text-fill: white;";

        prop[15] = "-fx-hgap: 1;";
        prop[16] = "-fx-vgap: 1;";
        prop[17] = "-fx-padding: 1;";
    }

    public static final String STYLE_LABEL_SELECTED = prop[0] + prop[12] + prop[14];
    public static final String STYLE_LABEL_UNSELECTED = prop[1] + prop[12];
    public static final String STYLE_LABEL_UNSELECTED_WHITE = prop[6] + prop[12];
    public static final String STYLE_GRID_BORDERED = prop[4] + prop[15] + prop[16] + prop[17];
    public static final String STYLE_GRID_UNBORDERED = prop[1];
    public static final String STYLE_TEXTFIELD_ERROR = prop[8];

    public static final String STYLE_TEXTAREA_NO_ERROR = prop[6] + prop[7];
    public static final String STYLE_TEXTAREA_ERROR = prop[6] + prop[8];

    public static final String STYLE_MENU_SELECTED = prop[5] + prop[9] + prop[10];
    public static final String STYLE_MENU_UNSELECTED = prop[3] + prop[9] + prop[10];

    public static final String STYLE_DATE_HEADER = prop[1] + prop[13] + prop[12];

    public static final String STYLE_COMBO_BOX = prop[6] + prop[7];

    public static final String STYLE_RED_BUTTON = prop[2] + prop[11];
    public static final String STYLE_CHOCO_BUTTON = prop[5] + prop[11];
    public static final String STYLE_ORANGE_BUTTON = prop[3] + prop[11];
}
