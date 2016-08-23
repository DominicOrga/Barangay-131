package javah.model;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

/**
 * Cache the data that are essential for the fundamental functioning of the system.
 */
public class CacheModel {


    private List<String> mResidentIDsCache;
    private List<String> mResidentNamesCache;

    private List<String> mBarangayIDIDsCache;
    private List<String> mBarangayIDResidentIDCache;
    private List<Date> mBarangayIDdateIssuedCache;

    private DatabaseModel dbControl;

    public CacheModel() {
        dbControl = new DatabaseModel();

        List[] lists = dbControl.getResidentEssentials();
        mResidentIDsCache = lists[0];
        mResidentNamesCache = lists[1];

        lists = dbControl.getBarangayIDEssentials();
        System.out.println(Arrays.asList(lists[2]));
        mBarangayIDIDsCache = lists[0];
        mBarangayIDResidentIDCache = lists[1];
        mBarangayIDdateIssuedCache = lists[2];

        System.out.println(Arrays.asList(mBarangayIDIDsCache));
    }

    public List<String> getResidentIDsCache() {
        return mResidentIDsCache;
    }

    public List<String> getResidentNamesCache() {
        return mResidentNamesCache;
    }

    public List<String> getBarangayIDIDsCache() { return mBarangayIDIDsCache; }

    public List<String> getBarangayIDResidentIDCache() { return mBarangayIDResidentIDCache; }

    public List<Date> getBarangayIDdateIssuedCache() { return mBarangayIDdateIssuedCache; }
}
