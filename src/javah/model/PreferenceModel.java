package javah.model;

import javah.contract.PreferenceContract;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class will handle the create, read and update of values with regards to
 * Json. The JSON file is stored in the user's public directory -
 * Users/public/Barangay131/pref.json
 *
 * All information stored in the JSON file are related to the Barangay Officials.
 */
public class PreferenceModel {

    /* Its Json himself! Wait, did I just assume its gender? */
    private JSONObject mJson;

    /* A path of the mJson file */
    private String mJsonPath = System.getenv("PUBLIC") + "/Barangay131/pref.json";

    /**
     * A constructor that makes the connection to the JSON file. The JSON file is
     * created if it hasn't been yet.
     */
    public PreferenceModel() {
        initialize();
    }

    /**
     * Initialize or reinitialize the JSON file.
     */
    private void initialize() {
        // Initially, try to create the mJson file if it is not yet created.
        try {
            File jsonFile = new File(mJsonPath);

            if(jsonFile.createNewFile()) {
                // When the file is created, add '{}' as initial strings so that it can be parsed by the JSON parser.
                FileWriter writer = new FileWriter(jsonFile);
                writer.write("{}");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse the file and store it to mJson.
        try {
            JSONParser parser = new JSONParser();
            mJson = (JSONObject) parser.parse(new FileReader(mJsonPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a specified data from the preference.
     *
     * @param key
     *        The key to determine the data to be fetched.
     *
     * @return the value based on the key.
     */
    public String get(String key) {
        Object obj = mJson.get(key);
        return obj == null ? null : (String) obj;
    }

    /**
     * Get a specified data from the preference.
     *
     * @param key
     *        The key to determine the data to be fetched.
     * @param defValue
     *        The default value of no value was fetched.
     *
     * @return the value based on the key.
     */
    public String get(String key, String defValue) {
        Object obj = mJson.get(key);
        return obj == null ? defValue : (String) obj;
    }

    /**
     * Store the specified data to the Preference.
     *
     * @param key
     *        The key to determine where to store the value.
     * @param value
     *        The value to be stored in the specified key.
     */
    public void put(String key, String value) {
        mJson.put(key, value);
    }

    /**
     * Save the changes made with the Json file. Called after clicking the save button
     * at mBarangayAgentScene.
     *
     * @param isBrgyAgentCalling
     *        Determines if the save function is called from the barangay clearance, since it
     *        is the last stage of the initialization process of the system.
     */
    public void save(boolean isBrgyAgentCalling) {
        if(isBrgyAgentCalling)
            mJson.put(PreferenceContract.BARANGAY_AGENTS_INITIALIZED, "1");

        try {
            FileWriter fileWriter = new FileWriter(mJsonPath);
            fileWriter.write(mJson.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the preferences. Used when the application will be reset.
     */
    public void delete() {
        File file = new File(mJsonPath);

        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        initialize();
    }
}
