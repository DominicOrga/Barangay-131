package javah.model;

import javah.container.*;

import java.sql.Timestamp;
import java.util.ArrayList;
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
     * gathered from the mResidentNamesCache with the help of mBrgyClearanceResidentIDsCache.
     *
     * Cardinality |mBrgyClearanceIDsCache| == |mBrgyClearanceResidentIDsCache| ==
     *             |mBrgyClearanceNamesCache| == |mBrgyClearanceDateIssuedCache|
     */
    private List<String> mBrgyClearanceResidentNamesCache = new ArrayList<>();

    /**
     * A list containing the date issuance of all the Barangay clearances, used to sort
     * the barangay clearance within the Information Control list paging.
     *
     * Cardinality |mBrgyClearanceIDsCache| == |mBrgyClearanceResidentIDsCache| ==
     *             |mBrgyClearanceNamesCache| == |mBrgyClearanceDateIssuedCache|
     *
     * @see javah.controller.InformationControl
     */
    private List<Timestamp> mBrgyClearanceDateIssuedCache;

    /**
     * A list containing all the IDs from the Business table, used to specify a
     * business from the database.
     *
     * Cardinality |mBusinessIDsCache| == |mBusinessNamesCache|.
     */
    private List<String> mBusinessIDsCache;

    /**
     * A list containing all the names from the Business table. The names have a
     * format of Last Name, First Name, Middle Initial. Used for displaying the
     * businesses in the Information List Paging of the Information Control.
     *
     * Cardinality |mBusinessIDsCache| == |mBusinessNamesCache|.
     *
     * @see javah.controller.InformationControl
     */
    private List<String> mBusinessNamesCache;

    /**
     * A list containing the IDs of all the business clearance, used to specify a
     * business clearance from the database.
     *
     * Cardinality |mBusiClearanceIDsCache| == |mBusiClearanceResidentIDsCache| ==
     *             |mBusiClearanceBusiNamesCache| == |mBusiClearanceDateIssuedCache|
     */
    private List<String> mBusiClearanceIDsCache;

    /**
     * A list containing the business IDs of all the business clearances, used to
     * determine the actual names of the business of the business clearance by getting
     * a specified index from mBusiClearanceBusiIDsCache and using the resulting
     * index to acquire the specified name.
     *
     * Cardinality |mBusiClearanceIDsCache| == |mBusiClearanceResidentIDsCache| ==
     *             |mBusiClearanceBusiNamesCache| == |mBusiClearanceDateIssuedCache|
     */
    private List<String> mBusiClearanceBusiIDsCache;

    /**
     * A list containing the actual names of the business. That is, its elements is
     * gathered from the mBusinessNamesCache with the help of mBusiClearanceBusiIDsCache.
     *
     * Cardinality |mBusiClearanceIDsCache| == |mBusiClearanceResidentIDsCache| ==
     *             |mBusiClearanceBusiNamesCache| == |mBusiClearanceDateIssuedCache|
     */
    private List<String> mBusiClearanceBusiNamesCache = new ArrayList<>();

    /**
     * A list containing the date issuance of all the Barangay clearances, used to sort
     * the business clearance within the Information Control list paging.
     *
     * Cardinality |mBusiClearanceIDsCache| == |mBusiClearanceResidentIDsCache| ==
     *             |mBusiClearanceBusiNamesCache| == |mBusiClearanceDateIssuedCache|
     *
     * @see javah.controller.InformationControl
     */
    private List<Timestamp> mBusiClearanceDateIssuedCache;

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

        lists = databaseModel.getBusinessEssentials();
        mBusinessIDsCache = lists[0];
        mBusinessNamesCache = lists[1];

        System.out.println("CacheModel - Business Names: " + mBusinessNamesCache);

        lists = databaseModel.getBusinessClearanceEssentials();
        mBusiClearanceIDsCache = lists[0];
        mBusiClearanceBusiIDsCache = lists[1];
        mBusiClearanceDateIssuedCache = lists[2];

        int barangayIDCount = mBarangayIDIDsCache.size();
        int barangayClearanceCount = mBrgyClearanceIDsCache.size();
        int businessClearanceCount = mBusiClearanceIDsCache.size();

        // A variable taking the value of the highest count between the barangay ID,
        // barangay clearance and business clearance..
        int count = barangayIDCount >= barangayClearanceCount ?
                (barangayIDCount >= businessClearanceCount ? barangayIDCount : businessClearanceCount ) :
                (barangayClearanceCount >= businessClearanceCount ? barangayClearanceCount : businessClearanceCount);

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

            if (i < businessClearanceCount) {
                String businessId = mBusiClearanceBusiIDsCache.get(i);
                int index = mBusinessIDsCache.indexOf(businessId);
                mBusiClearanceBusiNamesCache.add(mBusinessNamesCache.get(index));
            }
        }
    }

    /**
     * Fetch the resident IDs cache.
     *
     * @return the resident IDs cache.
     */
    public List<String> getResidentIDsCache() {
        return mResidentIDsCache;
    }

    /**
     * Fetch the resident names cache.
     *
     * @return the resident names cache.
     */
    public List<String> getResidentNamesCache() {
        return mResidentNamesCache;
    }

    /**
     * Fetch the barangay ID IDs cache.
     *
     * @return the barangay ID IDs cache.
     */
    public List<String> getBrgyIDIDsCache() { return mBarangayIDIDsCache; }

    /**
     * Fetch the Barangay ID Resident IDs cache.
     *
     * @return the Barangay ID Resident IDs cache.
     */
    public List<String> getBrgyIDResidentIDsCache() { return mBarangayIDResidentIDsCache; }

    /**
     * Fetch the Barangay ID Resident Names Cache.
     *
     * @return the Barangay ID Resident Names Cache.
     */
    public List<String> getBrgyIDResidentNamesCache() {
        return mBarangayIDNamesCache;
    }

    /**
     * Fetch the Barangay ID Date Issued cache.
     *
     * @return the Barangay ID Date Issued cache.
     */
    public List<Timestamp> getBrgyIDDateIssuedCache() { return mBarangayIDDateIssuedCache; }

    /**
     * Fetch the Barangay Clearance IDs Cache.
     *
     * @return the Barangay Clearance IDs cache.
     */
    public List<String> getBrgyClearanceIDsCache() {
        return mBrgyClearanceIDsCache;
    }

    /**
     * Fetch the Barangay Clearance Resident IDs cache.
     *
     * @return the Barangay Clearance Resident IDs cache.
     */
    public List<String> getBrgyClearanceResidentIDsCache() {
        return mBrgyClearanceResidentIDsCache;
    }

    /**
     * Fetch the Barangay Clearance Resident Names Cache.
     *
     * @return the Barangay Clearance Resident Names Cache.
     */
    public List<String> getBrgyClearanceResidentNamesCache() {
        return mBrgyClearanceResidentNamesCache;
    }

    /**
     * Fetch the Barangay Clearance Date Issued Cache.
     *
     * @return the Barangay Clearance Date Issued Cache.
     */
    public List<Timestamp> getBrgyClearanceDateIssuedCache() {
        return mBrgyClearanceDateIssuedCache;
    }

    /**
     * Fetch the Business IDs Cache.
     *
     * @return the Business IDs Cache.
     */
    public List<String> getBusiIDsCache() {
        return mBusinessIDsCache;
    }

    /**
     * Fetch the Business Names Cache.
     *
     * @return the Business Names Cache.
     */
    public List<String> getBusiNamesCache() {
        return mBusinessNamesCache;
    }

    /**
     * Fetch the Business Clearance IDs Cache.
     *
     * @return the Business Clearance IDs Cache.
     */
    public List<String> getBusiClearanceIDsCache() {
        return mBusiClearanceIDsCache;
    }

    /**
     * Fetch the Business Clearance Business Names Cache.
     *
     * @return the Business Clearance Business Names Cache.
     */
    public List<String> getBusiClearanceBusiNamesCache() {
        return mBusiClearanceBusiNamesCache;
    }

    /**
     * Fetch the Business Clearance Business IDs Cache.
     *
     * @return the Business Clearance Business IDs Cache.
     */
    public List<String> getBusiClearanceBusiIDsCache() {
        return mBusiClearanceBusiIDsCache;
    }

    /**
     * Fetch the Business Clearance Date Issued Cache.
     *
     * @return the Business Clearance Date Issued Cache.
     */
    public List<Timestamp> getBusiClearanceDateIssuedCache() {
        return mBusiClearanceDateIssuedCache;
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
                if (i < barangayIDCount && mBarangayIDResidentIDsCache.get(i).equals(id))
                    mBarangayIDNamesCache.set(i, name);

                if (i < barangayClearanceCount && mBrgyClearanceResidentIDsCache.get(i).equals(id)) {
                    mBrgyClearanceResidentNamesCache.set(i, name);
                }
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
            if (i < barangayIDCount && mBarangayIDResidentIDsCache.get(i).equals(id)) {
                index = mBarangayIDResidentIDsCache.indexOf(id);

                mBarangayIDIDsCache.remove(index);
                mBarangayIDResidentIDsCache.remove(index);
                mBarangayIDNamesCache.remove(index);
                mBarangayIDDateIssuedCache.remove(index);
            }

            if (i < barangayClearanceCount && mBrgyClearanceResidentIDsCache.get(i).equals(id)) {
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
        mBrgyClearanceResidentNamesCache.add(0, mResidentNamesCache.get(index));
    }

    /**
     * Add or update a business from the cached data. If a business is to be updated,
     * then update the business's name from the mBusinessNamesCache and
     * mBusiClearanceBusiNamesCache. If a business is added, then simply update the
     * mBusinessIDsCache and mBusinessNamesCache.
     *
     * @param business
     *        The business to be cached.
     *
     * @return the index of the resident added or updated from the cached data.
     */
    public int cacheBusiness(Business business) {
        String id = business.getID();
        String name = business.getName();

        // Update the caches related to the Business.
        if (mBusinessIDsCache.contains(id)) {

            // Update the name within the mBusinessNamesCache.
            int index = mBusinessIDsCache.indexOf(id);
            mBusinessNamesCache.remove(index);
            mBusinessNamesCache.add(index, name);

            // Update the name within the mBusiClearanceBusiNamesCache.
            int busiClearanceIDCount = mBusiClearanceIDsCache.size();

            // Check if the updated resident has a barangay ID or a barangay clearance. If
            // there is, then update the mBarangayIDNamesCache or mBrgyClearanceNamesCache.
            for (int i = 0; i < busiClearanceIDCount; i++) {
                if (mBusiClearanceBusiNamesCache.get(i).equals(id))
                    mBusiClearanceBusiNamesCache.set(i, name);
            }

            return index;

        } else {

            mBusinessNamesCache.add(name);
            Collections.sort(mBusinessNamesCache, String.CASE_INSENSITIVE_ORDER);

            // Get the index of the business name within the list after insertion.
            int index = mBusinessNamesCache.indexOf(name);

            // Use the acquired index to insert the business ID to the business IDs cache.
            mBusinessIDsCache.add(index, id);

            return index;
        }
    }

    /**
     * Delete a specific business from the cached data. Thus, affecting the business
     * cache and reports cache of the specified business.
     *
     * @param id
     *        The ID of the business to be removed from the cached data.
     */
    public void uncacheBusiness(String id) {
        int index = mBusinessIDsCache.indexOf(id);

        mBusinessIDsCache.remove(index);
        mBusinessNamesCache.remove(index);

        int busiClearanceCount = mBusiClearanceIDsCache.size();

        // Check if the business to be deleted has a business clearance. If there is,
        // then delete it from the mBusiClearanceBusiNamesCache.
        for (int i = 0; i < busiClearanceCount; i++)
            if (mBusiClearanceBusiIDsCache.get(i).equals(id)) {
                index = mBusiClearanceBusiIDsCache.indexOf(id);

                mBusiClearanceIDsCache.remove(index);
                mBusiClearanceBusiIDsCache.remove(index);
                mBusiClearanceBusiNamesCache.remove(index);
                mBusiClearanceDateIssuedCache.remove(index);
            }
    }

    /**
     * Cache the specified businness clearance.
     *
     * @param businessClearance
     *        The business clearance to be cached.
     */
    public void cacheBusinessClearance(BusinessClearance businessClearance) {
        mBrgyClearanceIDsCache.add(0, businessClearance.getID());
        mBrgyClearanceResidentIDsCache.add(0, businessClearance.getBusinessID());
        mBrgyClearanceDateIssuedCache.add(0, businessClearance.getDateIssued());

        int index = mResidentIDsCache.indexOf(businessClearance.getBusinessID());
        mBarangayIDNamesCache.add(0, mResidentNamesCache.get(index));
    }
}
