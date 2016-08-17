package javah.contract;

import javah.Main;

/**
 * This class holds the keys and the context of the system's preference
 */
public class PreferenceContract {

    // The Context where the preference will be stored.
    // This is unfortunate, it turns out that preferences are stored in the registry - /HKEY_LOCAL_MACHINE/Software/Javasoft
    // and a new key named 'Pref' must be created for the preferences to work.
    // Also, context must be an empty string so that 'Pref' will be used, since other strings fail to work.

    public static final String CHAIRMAN_FIRST_NAME = "chairman_first_name";
    public static final String CHAIRMAN_MIDDLE_NAME = "chairman_middle_name";
    public static final String CHAIRMAN_LAST_NAME = "chairman_last_name";
    public static final String CHAIRMAN_SIGNATURE_PATH = "chairman_signature_path";
    public static final String CHAIRMAN_PHOTO_PATH = "chairman_photo_path";

    public static final String SECRETARY_FIRST_NAME = "secretary_first_name";
    public static final String SECRETARY_MIDDLE_NAME = "secretary_middle_name";
    public static final String SECRETARY_LAST_NAME = "secretary_last_name";
    public static final String SECRETARY_SIGNATURE_PATH = "secretary_signature_path";

    public static final String TREASURER_FIRST_NAME = "treasurer_first_name";
    public static final String TREASURER_MIDDLE_NAME = "treasurer_middle_name";
    public static final String TREASURER_LAST_NAME = "treasurer_last_name";

    public static final String KAGAWAD_1_FIRST_NAME = "kagawad_1_first_name";
    public static final String KAGAWAD_1_MIDDLE_NAME = "kagawad_1_middle_name";
    public static final String KAGAWAD_1_LAST_NAME = "kagawad_1_last_name";
    public static final String KAGAWAD_2_FIRST_NAME= "kagawad_2_first_name";
    public static final String KAGAWAD_2_MIDDLE_NAME= "kagawad_2_middle_name";
    public static final String KAGAWAD_2_LAST_NAME= "kagawad_2_last_name";
    public static final String KAGAWAD_3_FIRST_NAME= "kagawad_3_first_name";
    public static final String KAGAWAD_3_MIDDLE_NAME= "kagawad_3_middle_name";
    public static final String KAGAWAD_3_LAST_NAME= "kagawad_3_last_name";
    public static final String KAGAWAD_4_FIRST_NAME= "kagawad_4_first_name";
    public static final String KAGAWAD_4_MIDDLE_NAME= "kagawad_4_middle_name";
    public static final String KAGAWAD_4_LAST_NAME= "kagawad_4_last_name";
    public static final String KAGAWAD_5_FIRST_NAME= "kagawad_5_first_name";
    public static final String KAGAWAD_5_MIDDLE_NAME= "kagawad_5_middle_name";
    public static final String KAGAWAD_5_LAST_NAME= "kagawad_5_last_name";
    public static final String KAGAWAD_6_FIRST_NAME= "kagawad_6_first_name";
    public static final String KAGAWAD_6_MIDDLE_NAME= "kagawad_6_middle_name";
    public static final String KAGAWAD_6_LAST_NAME= "kagawad_6_last_name";
    public static final String KAGAWAD_7_FIRST_NAME= "kagawad_7_first_name";
    public static final String KAGAWAD_7_MIDDLE_NAME= "kagawad_7_middle_name";
    public static final String KAGAWAD_7_LAST_NAME= "kagawad_7_last_name";
}
