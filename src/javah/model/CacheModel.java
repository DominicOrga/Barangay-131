package javah.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Cache the data that are essential for the fundamental functioning of the system.
 */
public class CacheModel {


    private List<String> mResidentIDsCache;
    private List<String> mResidentNamesCache;

    private List<String> mBarangayIDIDsCache;
    private List<String> mBarangayIDResidentIDCache;
    private List<Timestamp> mBarangayIDDateIssuedCache;

    private List<String> mBrgyClearanceIDsCache;
    private List<String> mBrgyClearanceResidentIDsCache;
    private List<Timestamp> mBrgyClearanceDateIssuedCache;


    private DatabaseModel dbControl;

    public CacheModel() {
        dbControl = new DatabaseModel();

        List[] lists = dbControl.getResidentEssentials();
        mResidentIDsCache = lists[0];
        mResidentNamesCache = lists[1];

        lists = dbControl.getBarangayIDEssentials();
        mBarangayIDIDsCache = lists[0];
        mBarangayIDResidentIDCache = lists[1];
        mBarangayIDDateIssuedCache = lists[2];

        lists = dbControl.getBarangayClearanceEssentials();
        mBrgyClearanceIDsCache = lists[0];
        mBrgyClearanceResidentIDsCache = lists[1];
        mBrgyClearanceDateIssuedCache = lists[2];
    }

    public List<String> getResidentIDsCache() {
        return mResidentIDsCache;
    }

    public List<String> getResidentNamesCache() {
        return mResidentNamesCache;
    }

    public List<String> getBarangayIDIDsCache() { return mBarangayIDIDsCache; }

    public List<String> getBarangayIDResidentIDCache() { return mBarangayIDResidentIDCache; }

    public List<Timestamp> getBarangayIDdateIssuedCache() { return mBarangayIDDateIssuedCache; }

    public List<String> getBrgyClearanceIDsCache() {
        return mBrgyClearanceIDsCache;
    }

    public List<String> getBrgyClearanceResidentIDsCache() {
        return mBrgyClearanceResidentIDsCache;
    }

    public List<Timestamp> getBrgyClearanceDateIssuedCache() {
        return mBrgyClearanceDateIssuedCache;
    }
}
