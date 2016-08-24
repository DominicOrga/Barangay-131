package javah.util;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class BarangayUtils {

    private static Image mDefaultDisplayPhoto;

    static {
        mDefaultDisplayPhoto = new Image("res/ic_default_resident_white_bg.png");
    }

    /**
     * Filter the lists in ascending order, priority level not ignored,with regards to the keywords.
     * @param residentIds
     * @param residentNames
     * @param keywords
     * @return a new filtered lists of resident IDs List[0] and resident names List[1].
     */
    public static List[] filterLists(List<String> residentIds, List<String> residentNames, String[] keywords) {
        int listSize = residentIds.size();

        // Store the priority level of each residents.
        List<Integer> residentPriorities = new ArrayList<>();

        // The resident priorities list must have a size equal to the resident lists with 0 as default values.
        for (int i = 0; i < listSize; i++)
            residentPriorities.add(0);

        // Lower case all keywords.
        for (int i = 0; i < keywords.length; i++)
            keywords[i] = keywords[i].toLowerCase();

        boolean isExistingMatchFound = false;

        // For every keyword existing on a resident name, its priority will increase.
        for (int i = 0; i < listSize; i++) {
            String residentName = residentNames.get(i).toLowerCase();
            for (int j = 0; j < keywords.length; j++)
                if (residentName.contains(keywords[j].toLowerCase())) {
                    residentPriorities.set(i, residentPriorities.get(i) + 1);
                    isExistingMatchFound = true;
                }
        }

        // If all the priority levels are 0, then immediately return the resident lists with empty elements.
        if (!isExistingMatchFound)
            return new List[]{new ArrayList<>(), new ArrayList<>()};

        // Clone the lists.
        List<String> residentIdsTemp = new ArrayList<>();
        List<String> residentNamesTemp = new ArrayList<>();

        // Cloning...
        for(int i = 0; i < residentIds.size(); i++) {
            residentIdsTemp.add(residentIds.get(i));
            residentNamesTemp.add(residentNames.get(i));
        }

        // Remove the residents from the lists if their priority is 0.
        for (int i = 0; i < residentPriorities.size(); i++)
            if (residentPriorities.get(i) == 0) {
                residentPriorities.remove(i);
                residentIdsTemp.remove(i);
                residentNamesTemp.remove(i);

                i -= 1;
            }

        // Update list size to be equiavlent with the reduced residents list.
        listSize = residentPriorities.size();

        // Make a list storing the sorted resident IDs and names according to their priority level.
        // Residents with a priority level less than 1 will not be added in the list.
        List<String> newResidentIds = new ArrayList<>();
        List<String> newResidentNames = new ArrayList<>();

        // Use selection sorting to build the new resident lists.
        for (int i = 0; i < listSize - 1; i++) {
            // Determine the index of the resident with the highest priority.
            int highestPriorityIndex = 0;
            for (int j = 1; j < residentPriorities.size(); j++)
                if (residentPriorities.get(j) > residentPriorities.get(highestPriorityIndex))
                    highestPriorityIndex = j;

            // Add the resident with the highest priority to the new resident lists.
            newResidentIds.add(residentIdsTemp.get(highestPriorityIndex));
            newResidentNames.add(residentNamesTemp.get(highestPriorityIndex));

            // remove the resident with the highest priority from the original resident lists, so that
            // the highest priority resident will not be reiterated from the list and the next resident with the
            // highest priority will be determined.
            residentIdsTemp.remove(highestPriorityIndex);
            residentNamesTemp.remove(highestPriorityIndex);
            residentPriorities.remove(highestPriorityIndex);
        }

        // Only 1 element will remain inside the temporary resident lists - the resident with the lowest priority.
        // Transfer them to the new Resident lists.
        newResidentIds.add(residentIdsTemp.get(0));
        newResidentNames.add(residentNamesTemp.get(0));

        List[] lists = new List[]{newResidentIds, newResidentNames};

        return lists;
    }

    /**
     * Convert a string month to its corresponding int value.
     * @param monthStr
     * @return
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
     * @param monthValue
     * @return
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

    public static Image getDefaultDisplayPhoto() {
        return mDefaultDisplayPhoto;
    }

    public static double[] parseSignatureDimension(String signatureDimension) {
        if (signatureDimension == null) return null;

        String[] dimensionParsed = signatureDimension.split(" ");
        double[] dimension = new double[4];

        for (int i = 0; i < 4; i++)
            dimension[i] = Double.parseDouble(dimensionParsed[i]);

        return dimension;
    }

    /**
     * Auto resizes text areas.
     * @param textArea
     * @param widthLimit the width limit given to the text area.
     */
    public static void addAutoResizeListener(TextArea textArea, double widthLimit) {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            // Get the width of the text area.
            FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
            double width = fontLoader.computeStringWidth(newValue, textArea.getFont());

            // Each line has a maximum legth of 220. Add a new line to fit the text.
            double newLine = width / widthLimit;

            textArea.setPrefHeight(30 + newLine * 20);
            textArea.setMinHeight(textArea.getPrefHeight());
        });
    }

}
