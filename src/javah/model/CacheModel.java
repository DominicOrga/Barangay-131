package javah.model;

import java.util.List;

/**
 * Cache the data that are essential for the fundamental functioning of the system.
 */
public class CacheModel {

    private List<String> mResidentIDsCache;
    private List<String> mResidentNamesCache;

    private DatabaseModel dbControl;

    public CacheModel() {
        dbControl = new DatabaseModel();

        List[] lists = dbControl.getResidentsIdAndName();
        mResidentIDsCache = lists[0];
        mResidentNamesCache = lists[1];

    }

    public List<String> getResidentIDsCache() {
        return mResidentIDsCache;
    }

    public List<String> getmResidentNamesCache() {
        return mResidentNamesCache;
    }
}
