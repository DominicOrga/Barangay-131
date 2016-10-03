package javah.model;

import javah.contract.PreferenceContract;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.Key;
import java.util.Base64;

/**
 * This class will handle the create, read and update of values with regards to
 * Json. The JSON file is stored in the user's public directory -
 * Users/public/Barangay131/pref.json
 *
 * All information stored in the JSON file are related to the Barangay Officials.
 */
public class PreferenceModel {

    // The encryption algorithm for encrypting or decrypting the JSON file.
    private final String ENCRYTION_ALGORITHM = "AES";

    // A key used for AES encrypting and decrypting the text from the JSON file.
    private final String KEY = "W4mkgo31nslG43Ks";

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
    public void initialize() {
        // Initially, try to create the mJson file if it is not yet created.
        try {
            File jsonFile = new File(mJsonPath);

            if(jsonFile.createNewFile()) {
                // When the file is created, add '{}' as initial strings so that it can be parsed
                // by the JSON parser.
                FileWriter writer = new FileWriter(jsonFile);
                writer.write("+rstZ2DltG5LlhC1mk0lcQ==");
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Decrypt the JSON file, then parse and store it to mJson.
        try {
            FileReader fileReader = new FileReader(mJsonPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Cipher decipher = Cipher.getInstance(ENCRYTION_ALGORITHM);
            Key secretKey = new SecretKeySpec(KEY.getBytes(), ENCRYTION_ALGORITHM);
            decipher.init(Cipher.DECRYPT_MODE, secretKey);

            String cipherText = bufferedReader.readLine();

            String decipherText = new String(decipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes())));

            JSONParser parser = new JSONParser();
            mJson = (JSONObject) parser.parse(decipherText);
        } catch (Exception e) {
            // If the json file cannot be deciphered (if someone meddled with it manually),
            // then delete it. After that, re-initialize the preference model.
            e.printStackTrace();
            delete();
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
     * Encrypt the Json string then save the changes made within the Json file.
     * Called after clicking the save buttonat mBarangayAgentScene.
     *
     * @param isBrgyAgentCalling
     *        Determines if the save function is called from the barangay clearance, since it
     *        is the last stage of the initialization process of the system.
     */
    public void save(boolean isBrgyAgentCalling) {
        if(isBrgyAgentCalling)
            mJson.put(PreferenceContract.BARANGAY_AGENTS_INITIALIZED, "1");

        try {
            String jsonString = mJson.toJSONString();

            Cipher cipher = Cipher.getInstance(ENCRYTION_ALGORITHM);
            Key secretKey = new SecretKeySpec(KEY.getBytes(), ENCRYTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            String cipherText = Base64.getEncoder().encodeToString(cipher.doFinal(jsonString.getBytes()));

            FileWriter fileWriter = new FileWriter(mJsonPath);
            fileWriter.write(cipherText);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the preferences. Used when the application preferences will be reset.
     */
    public void delete() {
        try {
            // Fucking bug! Deleting a file needs System.gc() called before deletion.
            System.gc();
            Files.deleteIfExists(new File(mJsonPath).toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initialize();
    }
}
