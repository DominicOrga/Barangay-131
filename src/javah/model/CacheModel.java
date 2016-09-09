package javah.model;

import javah.container.BarangayClearance;
import javah.container.BarangayID;
import javah.container.Resident;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class that caches the information data from the database to constant
 * connection with the database and avoid memory leaks.
 */
public class CacheModel {

    /**
     * A list containing all the IDs from the Resident table, used to specify a
     * resident from the database.
     *
     * Cardinality |mResidentIDsCache| == |mResidentNamesCache|.
     */
    private List<String> mResidentIDsCache;

    /**
     * A list containing all the names from the Resident table. The names have a
     * format of Last Name, First Name, Middle Initial. Used for displaying the
     * residents in the Resident List Paging of the Resident Control.
     *
     * Cardinality |mResidentIDsCache| == |mResidentNamesCache|.
     *
     * @see javah.controller.ResidentControl
     */
    private List<String> mResidentNamesCache;

    /**
     * A list containing the IDs of all the Barangay IDs, used to specify a barangay ID
     * from the database.
     *
     * Cardinality |mBarangayIDIDsCache| == |mBarangayIDResidentIDsCache| ==
     *             |mBarangayIDNamesCache| == |mBarangayIDDateIssuedCache|
     */
    private List<String> mBarangayIDIDsCache;

    /**
     * A list containing the Resident IDs of all the Barangay IDs, used to determine the
     * actual names of the applicant of the Barangay ID by getting a specified index from
     * mResidentIDsCache and using the resulting index to acquire the specified name.
     *
     * Cardinality |mBarangayIDIDsCache| == |mBarangayIDResidentIDsCache| ==
     *             |mBarangayIDNamesCache| == |mBarangayIDDateIssuedCache|
     */
    private List<String> mBarangayIDResidentIDsCache;

    /**
     * A list containing the actual names of the applicant. That is, its elements is
     * gathered from the mResidentNamesCache with the help of mBarangayIDResidentIDsCache.
     *
     * Cardinality |mBarangayIDIDsCache| == |mBarangayIDResidentIDsCache| ==
     *             |mBarangayIDNamesCache| == |mBarangayIDDateIssuedCache|
     */
    private List<String> mBarangayIDNamesCache = new ArrayList<>();

    /**
     * A list containing the Resident IDs of all the Barangay IDs, used to sort
     * the barangay ID within the Information Control list paging.
     *
     * Cardinality |mBarangayIDIDsCache| == |mBarangayIDResidentIDsCache| ==
     *             |mBarangayIDNamesCache| == |mBarangayIDDateIssuedCache|
     *
     * @see javah.controller.InformationControl
     */
    private List<Timestamp> mBarangayIDDateIssuedCache;

    /**
     * A list containing the IDs of all the barangay clearance, used to specify a
     * barangay clearance from the database.
     *
     * Cardinality |mBrgyClearanceIDsCache| == |mBrgyClearanceResidentIDsCache| ==
     *             |mBrgyClearanceNamesCache| == |mBrgyClearanceDateIssuedCache|
     */
    private List<String> mBrgyClearanceIDsCache;

    /**
     * A list containing the Resident IDs of all the Barangay clearances, used to
     * determine the actual names of the applicant of the Barangay clearance by getting
     * a specified index from mBrgyClearanceResidentIDsCache and using the resulting
     * index to acquire the specified name.
     *
     * Cardinality |mBrgyClearanceIDsCache| == |mBrgyClearanceResidentIDsCache| ==
     *             |mBrgyClearanceNamesCache| == |mBrgyClearanceDateIssuedCache|
     */
    private List<String> mBrgyClearanceResidentIDsCache;

    /**
     * A list containing the actual names of the applicant. That is, its elements is
     * gathered from the mResidentNamesCache with the help of mBarangayIDResidentIDsCache.
     *
     * Cardinality |mBrgyClearanceIDsCache| == |mBrgyClearanceResidentIDsCache| ==
     *             |mBrgyClearanceNamesCache| == |mBrgyClearanceDateIssuedCache|
     */
    private List<String> mBrgyClearanceResidentNamesCache = new ArrayList<>();

    /**
     * A list containing the Resident IDs of all the Barangay clearances, used to sort
     * the barangay clearance within the Information Control list paging.
     *
     * Cardinality |mBrgyClearanceIDsCache| == |mBrgyClearanceResidentIDsCache| ==
     *             |mBrgyClearanceNamesCache| == |mBrgyClearanceDateIssuedCache|
     *
     * @see javah.controller.InformationControl
     */
    private List<Timestamp> mBrgyClearanceDateIssuedCache;


    /**
     * Get a reference to the universal database model to start caching data.
     *
     * @param databaseModel
     *        The universal database model from the main control.
     */
    public void startCache(DatabaseModel databaseModel) {
        List[] lists = databaseModel.getResidentEssentials();
        mResidentIDsCache = lists[0];
        mResidentNamesCache = lists[1];

        lists = databaseModel.getBarangayIDEssentials();
        mBarangayIDIDsCache = lists[0];
        mBarangayIDResidentIDsCache = lists[1];
        mBarangayIDDateIssuedCache = lists[2];

        lists = databaseModel.getBarangayClearanceEssentials();
        mBrgyClearanceIDsCache = lists[0];
        mBrgyClearanceResidentIDsCache = lists[1];
        mBrgyClearanceDateIssuedCache = lists[2];

        int barangayIDCount = mBarangayIDIDsCache.size();
        int barangayClearanceCount = mBrgyClearanceIDsCache.size();

        // A variable taking the value of the highest count between the barangay ID and clearance.
        int count = barangayIDCount >= barangayClearanceCount ? barangayIDCount : barangayClearanceCount;

        // Populate the barangay ID and clearance names cache.
        for (int i = 0; i < count; i++) {
            if (i < barangayIDCount) {
                String residentId = mBarangayIDResidentIDsCache.get(i);
                int index = mResidentIDsCache.indexOf(residentId);
                mBarangayIDNamesCache.add(mResidentNamesCache.get(index));
            }

            if (i < barangayClearanceCount) {
                String residentId = mBrgyClearanceResidentIDsCache.get(i);
                int index = mResidentIDsCache.indexOf(residentId);
                mBrgyClearanceResidentNamesCache.add(mResidentNamesCache.get(index));
            }
        }
        System.out.println("CacheModel - Barangay ID Count: " + mBarangayIDNamesCache);
        System.out.println("CacheModel - Barangay ID Names: " + mBarangayIDNamesCache);
        System.out.println("CacheModel - Barangay ID Names: " + mBarangayIDNamesCache);
    }

    public List<String> getResidentIDsCache() {
        return mResidentIDsCache;
    }

    public List<String> getResidentNamesCache() {
        return mResidentNamesCache;
    }

    public List<String> getBrgyIDIDsCache() { return mBarangayIDIDsCache; }

    public List<String> getBrgyIDResidentIDsCache() { return mBarangayIDResidentIDsCache; }

    public List<String> getBrgyIDResidentNamesCache() {
        return mBarangayIDNamesCache;
    }

    public List<Timestamp> getBrgyIDDateIssuedCache() { return mBarangayIDDateIssuedCache; }

    public List<String> getBrgyClearanceIDsCache() {
        return mBrgyClearanceIDsCache;
    }

    public List<String> getBrgyClearanceResidentIDsCache() {
        return mBrgyClearanceResidentIDsCache;
    }

    public List<String> getBrgyClearanceResidentNamesCache() {
        return mBrgyClearanceResidentNamesCache;
    }

    public List<Timestamp> getBrgyClearanceDateIssuedCache() {
        return mBrgyClearanceDateIssuedCache;
    }

    /**
     * Add or update a resident from the cached data. If a resident is to be updated,
     * then update the resident's name from the mResidentNamesCache,
     * mBarangayIDResidentNamesCache and mBrgyClearanceResidentNamesCache. If a
     * resident is added, then simply update the mResidentIDsCache and
     * mResidentNamesCache.
     *
     * @param resident
     *        The resident to be cached.
     *
     * @return the index of the resident added or updated from the cached data.
     */
    public int cacheResident(Resident resident) {
        String id = resident.getId();
        String name = String.format("%s, %s %s.",
                resident.getLastName(),
                resident.getFirstName(),
                Character.toUpperCase(resident.getMiddleName().charAt(0))
        );

        name += resident.getAuxiliary() == null ? "" : " " + resident.getAuxiliary();

        // Update the caches related to the Resident.
        if (mResidentIDsCache.contains(id)) {

            // Update the name within the mResidentNamesCache.
            int index = mResidentIDsCache.indexOf(id);
            mResidentNamesCache.remove(index);
            mResidentNamesCache.add(index, name);

            // Update the name within the mBarangayIDNamesCache.
            int barangayIDCount = mBarangayIDIDsCache.size();
            int barangayClearanceCount = mBrgyClearanceIDsCache.size();

            // A variable taking the value of the highest count between the barangay ID and
            // clearance.
            int count = barangayIDCount >= barangayClearanceCount ? barangayIDCount : barangayClearanceCount;

            // Check if the updated resident has a barangay ID or a barangay clearance. If
            // there is, then update the mBarangayIDNamesCache or mBrgyClearanceNamesCache.
            for (int i = 0; i < count; i++) {
                if (i < barangayIDCount && mBarangayIDResidentIDsCache.get(i) == id)
                    mBarangayIDNamesCache.set(i, name);

                if (i < barangayClearanceCount && mBrgyClearanceResidentIDsCache.get(i) == id)
                    mBrgyClearanceResidentNamesCache.set(i, name);
            }

            return index;

        } else {
            mResidentNamesCache.add(name);
            Collections.sort(mResidentNamesCache, String.CASE_INSENSITIVE_ORDER);

            // Get the index of the resident name within the list after insertion.
            int index = mResidentNamesCache.indexOf(name);

            // Use the acquired index to insert the resident ID to the resident IDs list.
            mResidentIDsCache.add(index, id);

            return index;
        }
    }

    /**
     * Delete a specific resident from the cached data. Thus, affecting the resident
     * cache and reports cache of the specified resident.
     *
     * @param id
     *        The ID of the resident to be removed from the cached data.
     */
    public void uncacheResident(String id) {
        int index = mResidentIDsCache.indexOf(id);

        mResidentIDsCache.remove(index);
        mResidentNamesCache.remove(index);

        int barangayIDCount = mBarangayIDIDsCache.size();
        int barangayClearanceCount = mBrgyClearanceIDsCache.size();

        // A variable taking the value of the highest count between the barangay ID and
        // clearance.
        int count = barangayIDCount >= barangayClearanceCount ? barangayIDCount : barangayClearanceCount;

        // Check if the resident to be deleted has a barangay ID or a barangay clearance. If
        // there is, then delete it from the mBarangayIDNamesCache or mBrgyClearanceNamesCache.
        for (int i = 0; i < count; i++) {
            if (i < barangayIDCount && mBarangayIDResidentIDsCache.get(i) == id) {
                index = mBarangayIDResidentIDsCache.indexOf(id);

                mBarangayIDIDsCache.remove(index);
                mBarangayIDResidentIDsCache.remove(index);
                mBarangayIDNamesCache.remove(index);
                mBarangayIDDateIssuedCache.remove(index);
            }

            if (i < barangayClearanceCount && mBrgyClearanceResidentIDsCache.get(i) == id) {
                index = mBrgyClearanceResidentIDsCache.indexOf(id);

                mBrgyClearanceIDsCache.remove(index);
                mBrgyClearanceResidentIDsCache.remove(index);
                mBrgyClearanceResidentNamesCache.remove(index);
                mBrgyClearanceDateIssuedCache.remove(index);
            }
        }
    }

    /**
     * Cache the specified barangay ID.
     *
     * @param barangayID
     *        The barangay ID to be cached.
     */
    public void cacheBarangayID(BarangayID barangayID) {

        mBarangayIDIDsCache.add(0, barangayID.getID());
        mBarangayIDResidentIDsCache.add(0, barangayID.getResidentID());
        mBarangayIDDateIssuedCache.add(0, barangayID.getDateIssued());

        int index = mResidentIDsCache.indexOf(barangayID.getResidentID());
        mBarangayIDNamesCache.add(0, mResidentNamesCache.get(index));
    }

    /**
     * Cache the specified barangay clearance.
     *
     * @param barangayClearance
     *        The barangay clearance to be cached.
     */
    public void cacheBarangayClearance(BarangayClearance barangayClearance) {
        mBrgyClearanceIDsCache.add(0, barangayClearance.getID());
        mBrgyClearanceResidentIDsCache.add(0, barangayClearance.getResidentID());
        mBrgyClearanceDateIssuedCache.add(0, barangayClearance.getDateIssued());

        int index = mResidentIDsCache.indexOf(barangayClearance.getResidentID());
        mBarangayIDNamesCache.add(0, mResidentNamesCache.get(index));
    }
}
