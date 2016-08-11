package javah.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class will handle the create, read and update of values with regards to Json.
 */
public class PreferenceModel {

    /**
     * Its Json himself! Did I just assume its gender?
     */
    private JSONObject mJson;

    /**
     * The path of the mJson file.
     */
    private String mJsonPath = System.getenv("PUBLIC") + "/Barangay131/pref.json";

    public PreferenceModel() {

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

    public String get(String key) {
        Object obj = mJson.get(key);

        return obj == null ? null : (String) obj;
    }

    public void put(String key, String value) {
        mJson.put(key, value);
    }

    /**
     * Save the changes made with the Json file.
     * Called after clicking the save button at mBarangayAgentScene.
     */
    public void save() {
        try {
            FileWriter fileWriter = new FileWriter(mJsonPath);
            fileWriter.write(mJson.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
