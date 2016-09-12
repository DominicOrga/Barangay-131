package javah.util;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains the commonly used function throughout the application.
 * Due to the common usage of the functions across the application, they are
 * declared static.
 */
public class BarangayUtils {

    /** The default display photo. */
    private static Image mDefaultDisplayPhoto;

    /**
     * Initialize the default display photo.
     */
    static {
        mDefaultDisplayPhoto = new Image("res/ic_default_resident_white_bg.png");
    }

    /**
     * Filter the IDs list in descending order with the use of priority level.
     * Priority level is calculated by how many keywords exists in the given name.
     *
     * @param ids
     *        The IDs to be filtered.
     * @param names
     *        The names which will determine the order of the IDs by matching it to the
     *        keywords.
     * @param keywords
     *        The keywords for filtering the IDs list.
     *
     * @return a new filtered list of IDs List.
     */
    public static List getFilteredIDs(List<String> ids, List<String> names, String[] keywords) {
        // Lower case all keywords.
        int keywordLength = keywords.length;
        for (int i = 0; i < keywordLength; i++)
            keywords[i] = keywords[i].toLowerCase();

        // Take hold of all the total number of matches of each resident name that has at
        // least one match with the keywords.
        List<Integer> priorityList = new ArrayList<>();

        // Take hold of all the resident IDs that has at least one match with the keywords.
        //
        // Note: The elements between the Priority and Filtered ID list have a relationship
        // among the same index. Therefore, they are always connected.S
        List<String> filteredIDs = new ArrayList<>();

        // Match each resident names with the keywords. If at least one match is found,
        // then store its ID to the filtered IDs list and store the number of matches to
        // the priority list.
        int listSize = ids.size();
        for (int i = 0; i < listSize; i++) {
            String residentName = names.get(i).toLowerCase();
            int matchCount = 0;

            for (int j = 0; j < keywordLength; j++)
                if (residentName.contains(keywords[j]))
                    matchCount++;

            if (matchCount > 0) {
                filteredIDs.add(ids.get(i));
                priorityList.add(matchCount);
            }
        }

        System.out.println("BarangayUtils - filtered ID first: " + filteredIDs.get(0));

        // Use Selection Sorting to sort the Filtered ID List with the help of sorting
        // the Priority List.
        listSize = filteredIDs.size();
        for (int i = 0; i < listSize; i++) {
            int highestPriorityIndex = i;

            for (int j = i; j < listSize; j++) {
                if (priorityList.get(j) > priorityList.get(highestPriorityIndex))
                    highestPriorityIndex = j;
            }

            int highestVal = priorityList.get(highestPriorityIndex);

            priorityList.set(highestPriorityIndex, priorityList.get(i));
            priorityList.set(i, highestVal);

            String highestID = filteredIDs.get(highestPriorityIndex);

            filteredIDs.set(highestPriorityIndex, filteredIDs.get(i));
            filteredIDs.set(i, highestID);

            System.out.println("BarangayUtils - Highest ID per Loop: " + highestID);

        }

        return filteredIDs;
    }

    /**
     * Format the string wherein the character of every word in the string is
     * capitalized, while living the other characters in lower cased letter.
     *
     * @param str
     *        The string to format.
     * @return the formatted string.
     */
    public static String capitalizeString(String str) {
        str = str.toLowerCase().trim();
        String[] subStr = str.split(" ");
        String strFormatted = "";

        for(int i = 0; i < subStr.length; i++)
            strFormatted += subStr[i].substring(0, 1).toUpperCase() + subStr[i].substring(1);

        return strFormatted;
    }

    /**
     * Format the specified name in the following format -
     * FIRST_NAME MIDDLE_INITIAL. LAST_NAME AUX
     *
     * @param firstName
     *        The first name.
     * @param middleName
     *        The middle name.
     * @param lastName
     *        The last name.
     * @param auxiliary
     *        The auxiliary of the name.
     *
     * @return the formatted name.
     */
    public static String formatName(String firstName, String middleName, String lastName, String auxiliary) {
        firstName = capitalizeString(firstName);
        middleName = capitalizeString(middleName);
        lastName = capitalizeString(lastName);

        String formattedName = firstName + " " + middleName.charAt(0) + ". " + lastName;
        formattedName += auxiliary == null ? "" : " " + auxiliary;

        return formattedName;
    }

    /**
     * Convert a string month to its corresponding int value.
     *
     * @param monthStr
     *        The month in string.
     *
     * @return the month in int.
     */
    public static int convertMonthStringToInt(String monthStr) {
        switch(monthStr) {
            case "January" : return 0;
            case "February" : return 1;
            case "March" : return 2;
            case "April" : return 3;
            case "May" : return 4;
            case "June" : return 5;
            case "July" : return 6;
            case "August" : return 7;
            case "September" : return 8;
            case "October" : return 9;
            case "November" : return 10;
            default : return 11;
        }
    }

    /**
     * Convert an int month to its corresponding string value.
     *
     * @param monthValue
     *        The month in int.
     *
     * @return the month in String.
     */
    public static String convertMonthIntToString(int monthValue) {
        switch (monthValue) {
            case 0 : return "January";
            case 1 : return "February";
            case 2 : return "March";
            case 3 : return "April";
            case 4 : return "May";
            case 5 : return "June";
            case 6 : return "July";
            case 7 : return "August";
            case 8 : return "September";
            case 9 : return "October";
            case 10 : return "November";
            default : return "December";
        }
    }

    /**
     * Fetch the default display photo.
     *
     * @return the default display photo.
     */
    public static Image getDefaultDisplayPhoto() {
        return mDefaultDisplayPhoto;
    }

    /**
     * Parse the signature dimension into an array of double, since signature
     * dimensions are in string value when extracted from the database.
     *
     * @param signatureDimension
     *        The array containing the parsed signature dimension.
     *        The array contains the elements:
     *        array[0] = x coordinate.
     *        array[1] = y coordinate.
     *        array[2] = width.
     *        array[3] = height.
     *
     * @return the array containing the parsed signature dimension.
     */
    public static double[] parseSignatureDimension(String signatureDimension) {
        if (signatureDimension == null) return null;

        String[] dimensionParsed = signatureDimension.split(" ");
        double[] dimension = new double[4];

        for (int i = 0; i < 4; i++)
            dimension[i] = Double.parseDouble(dimensionParsed[i]);

        return dimension;
    }

    /**
     * Auto resize text areas vertically when the text do not fit the width limit.
     *
     * @param textArea
     *        The text area to be manipulated.
     * @param widthLimit
     *        The width limit given to the text area.
     */
    public static void addAutoResizeListener(TextArea textArea, double widthLimit) {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            // Get the width of the text area.
            FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
            double width = fontLoader.computeStringWidth(newValue, textArea.getFont());

            // Each line has a maximum legth of 220. Add a new line to fit the text.
            int newLine = (int) (width / widthLimit);

            textArea.setPrefHeight(30 + newLine * 20);
            textArea.setMinHeight(textArea.getPrefHeight());
        });
    }

    /**
     * Limit the text within a any TextInputControl child classes to a certain length.
     *
     * @param textField
     *        The text field to be handled.
     * @param length
     *        The maximum length of text allowed within the specified text field.
     */
    public static void addTextLimitListener(TextInputControl textField, double length) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > length)
                textField.setText(oldValue);
        });
    }

}