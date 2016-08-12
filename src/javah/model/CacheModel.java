package javah.model;

import javah.util.DatabaseContract;
import javah.util.DatabaseContract.*;

import java.sql.Date;
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

        List[] lists = dbControl.getResidentsIdAndName();
        mResidentIDsCache = lists[0];
        mResidentNamesCache = lists[1];

        mBarangayIDIDsCache =
                (List<String>)(List<?>) dbControl.getData(BarangayIdEntry.TABLE_NAME, BarangayIdEntry.COLUMN_ID);
        mBarangayIDResidentIDCache =
                (List<String>)(List<?>) dbControl.getData(BarangayIdEntry.TABLE_NAME, BarangayIdEntry.COLUMN_RESIDENT_ID);
        mBarangayIDdateIssuedCache =
                (List<Date>)(List<?>) dbControl.getData(BarangayIdEntry.TABLE_NAME, BarangayIdEntry.COLUMN_DATE_ISSUED);
    }

    public List<String> getResidentIDsCache() {
        return mResidentIDsCache;
    }

    public List<String> getmResidentNamesCache() {
        return mResidentNamesCache;
    }

    public List<String> getBarangayIDIDsCache() { return mBarangayIDIDsCache; }

    public List<String> getBarangayIDResidentIDCache() { return mBarangayIDResidentIDCache; }

    public List<Date> getBarangayIDdateIssuedCache() { return mBarangayIDdateIssuedCache; }
}
