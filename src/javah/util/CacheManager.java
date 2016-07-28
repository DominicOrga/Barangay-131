package javah.util;

import javah.database.DatabaseControl;

import java.util.List;

/**
 * Cache the data that are essential for the fundamental functioning of the system.
 */
public class CacheManager {

    private List<String> mResidentIDsCache;
    private List<String> mResidentNamesCache;

    private DatabaseControl dbControl;

    public CacheManager() {
        dbControl = new DatabaseControl();

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
