package javah.contract;

/**
 * This class holds the keys and the context of the system's preference. The
 * Context where the preference will be stored. This is unfortunate, it turns
 * out that preferences are stored in the registry -
 * /HKEY_LOCAL_MACHINE/Software/Javasoft and a new key named 'Pref' must be
 * created for the preferences to work. Also, context must be an empty string
 * so that 'Pref' will be used, since other strings fail to work.
 */
public class PreferenceContract {

    /**
     * A key to make sure that the barangay agents are initialized during the first
     * installation of the application. The value can either be 0 or 1, where 0
     * represents that the barangay agents have not been initialized yet. Otherwise,
     * 1 is the value.
     */
    public static final String BARANGAY_AGENTS_INITIALIZED = "barangay_agents_initialized";

    public static final String CHAIRMAN_FIRST_NAME = "chairman_first_name";
    public static final String CHAIRMAN_MIDDLE_NAME = "chairman_middle_name";
    public static final String CHAIRMAN_LAST_NAME = "chairman_last_name";
    public static final String CHAIRMAN_AUXILIARY = "chairman_auxiliary";
    public static final String CHAIRMAN_SIGNATURE_PATH = "chairman_signature_path";
    public static final String CHAIRMAN_PHOTO_PATH = "chairman_photo_path";

    public static final String SECRETARY_FIRST_NAME = "secretary_first_name";
    public static final String SECRETARY_MIDDLE_NAME = "secretary_middle_name";
    public static final String SECRETARY_LAST_NAME = "secretary_last_name";
    public static final String SECRETARY_AUXILIARY = "secretary_auxiliary";
    public static final String SECRETARY_SIGNATURE_PATH = "secretary_signature_path";

    public static final String TREASURER_FIRST_NAME = "treasurer_first_name";
    public static final String TREASURER_MIDDLE_NAME = "treasurer_middle_name";
    public static final String TREASURER_LAST_NAME = "treasurer_last_name";
    public static final String TREASURER_AUXILIARY = "treasurer_auxiliary";

    /**
     * Keeps track of the previous coordinates and dimension of the signatures from the
     * reports to be used on the future reports.
     */
    public static final String BRGY_ID_CHM_SIGNATURE_DIMENSION = "brgy_id_chrm_sign_dim";
    public static final String BRGY_CLEARANCE_CHM_SIGNATURE_DIMENSION = "brgy_clearance_chm_sign_dim";
    public static final String BRGY_CLEARANCE_SEC_SIGNATURE_DIMENSION = "brgy_clearance_sec_sign_dim";
    public static final String BUSI_CLEARANCE_CHM_SIGNATURE_DIMENSION = "busi_clearance_chm_sign_dim";
    public static final String BUSI_CLEARANCE_SEC_SIGNATURE_DIMENSION = "busi_clearance_sec_sign_dim";

    public static final String KAGAWAD_1_FIRST_NAME = "kagawad_1_first_name";
    public static final String KAGAWAD_1_MIDDLE_NAME = "kagawad_1_middle_name";
    public static final String KAGAWAD_1_LAST_NAME = "kagawad_1_last_name";
    public static final String KAGAWAD_1_AUXILIARY = "kagawad_1_auxiliary";
    public static final String KAGAWAD_2_FIRST_NAME= "kagawad_2_first_name";
    public static final String KAGAWAD_2_MIDDLE_NAME= "kagawad_2_middle_name";
    public static final String KAGAWAD_2_LAST_NAME= "kagawad_2_last_name";
    public static final String KAGAWAD_2_AUXILIARY = "kagawad_2_auxiliary";
    public static final String KAGAWAD_3_FIRST_NAME= "kagawad_3_first_name";
    public static final String KAGAWAD_3_MIDDLE_NAME= "kagawad_3_middle_name";
    public static final String KAGAWAD_3_LAST_NAME= "kagawad_3_last_name";
    public static final String KAGAWAD_3_AUXILIARY = "kagawad_3_auxiliary";
    public static final String KAGAWAD_4_FIRST_NAME= "kagawad_4_first_name";
    public static final String KAGAWAD_4_MIDDLE_NAME= "kagawad_4_middle_name";
    public static final String KAGAWAD_4_LAST_NAME= "kagawad_4_last_name";
    public static final String KAGAWAD_4_AUXILIARY = "kagawad_4_auxiliary";
    public static final String KAGAWAD_5_FIRST_NAME= "kagawad_5_first_name";
    public static final String KAGAWAD_5_MIDDLE_NAME= "kagawad_5_middle_name";
    public static final String KAGAWAD_5_LAST_NAME= "kagawad_5_last_name";
    public static final String KAGAWAD_5_AUXILIARY = "kagawad_5_auxiliary";
    public static final String KAGAWAD_6_FIRST_NAME= "kagawad_6_first_name";
    public static final String KAGAWAD_6_MIDDLE_NAME= "kagawad_6_middle_name";
    public static final String KAGAWAD_6_LAST_NAME= "kagawad_6_last_name";
    public static final String KAGAWAD_6_AUXILIARY = "kagawad_6_auxiliary";
    public static final String KAGAWAD_7_FIRST_NAME= "kagawad_7_first_name";
    public static final String KAGAWAD_7_MIDDLE_NAME= "kagawad_7_middle_name";
    public static final String KAGAWAD_7_LAST_NAME= "kagawad_7_last_name";
    public static final String KAGAWAD_7_AUXILIARY = "kagawad_7_auxiliary";

    /* A two-dimensional array that will allow easy access to all the Kagawad Names. */
    public static final String[][] KAGAWAD_NAMES = new String[7][4];

    static {
        KAGAWAD_NAMES[0][0] = KAGAWAD_1_FIRST_NAME;
        KAGAWAD_NAMES[0][1] = KAGAWAD_1_MIDDLE_NAME;
        KAGAWAD_NAMES[0][2] = KAGAWAD_1_LAST_NAME;
        KAGAWAD_NAMES[0][3] = KAGAWAD_1_AUXILIARY;
        KAGAWAD_NAMES[1][0] = KAGAWAD_2_FIRST_NAME;
        KAGAWAD_NAMES[1][1] = KAGAWAD_2_MIDDLE_NAME;
        KAGAWAD_NAMES[1][2] = KAGAWAD_2_LAST_NAME;
        KAGAWAD_NAMES[1][3] = KAGAWAD_2_AUXILIARY;
        KAGAWAD_NAMES[2][0] = KAGAWAD_3_FIRST_NAME;
        KAGAWAD_NAMES[2][1] = KAGAWAD_3_MIDDLE_NAME;
        KAGAWAD_NAMES[2][2] = KAGAWAD_3_LAST_NAME;
        KAGAWAD_NAMES[2][3] = KAGAWAD_3_AUXILIARY;
        KAGAWAD_NAMES[3][0] = KAGAWAD_4_FIRST_NAME;
        KAGAWAD_NAMES[3][1] = KAGAWAD_4_MIDDLE_NAME;
        KAGAWAD_NAMES[3][2] = KAGAWAD_4_LAST_NAME;
        KAGAWAD_NAMES[3][3] = KAGAWAD_4_AUXILIARY;
        KAGAWAD_NAMES[4][0] = KAGAWAD_5_FIRST_NAME;
        KAGAWAD_NAMES[4][1] = KAGAWAD_5_MIDDLE_NAME;
        KAGAWAD_NAMES[4][2] = KAGAWAD_5_LAST_NAME;
        KAGAWAD_NAMES[4][3] = KAGAWAD_5_AUXILIARY;
        KAGAWAD_NAMES[5][0] = KAGAWAD_6_FIRST_NAME;
        KAGAWAD_NAMES[5][1] = KAGAWAD_6_MIDDLE_NAME;
        KAGAWAD_NAMES[5][2] = KAGAWAD_6_LAST_NAME;
        KAGAWAD_NAMES[5][3] = KAGAWAD_6_AUXILIARY;
        KAGAWAD_NAMES[6][0] = KAGAWAD_7_FIRST_NAME;
        KAGAWAD_NAMES[6][1] = KAGAWAD_7_MIDDLE_NAME;
        KAGAWAD_NAMES[6][2] = KAGAWAD_7_LAST_NAME;
        KAGAWAD_NAMES[6][3] = KAGAWAD_7_AUXILIARY;
    }
}
