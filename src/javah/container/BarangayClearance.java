package javah.container;

import java.sql.Timestamp;

/**
 * A container for a specific barangay clearance data.
 *
 * @version %I%, %G%
 */
public class BarangayClearance {

    /* A string holding the unique ID of this barangay clearance. */
    private String mID;

    /* Holds the ID of the barangay clearance owner. */
    private String mResidentID;

    /* Holds the resident name registered to this barangay clearance */
    private String mResidentName;

    /* Holds the address registered to this barangay clearance. */
    private String mAddress;

    /* Holds the year of residency registered to this barangay clearance */
    private int mYearOfResidency;

    /* Holds the total years of residency registered to this barangay clearance. */
    private int mTotalYearsResidency;

    /* Holds the purpose registered to this barangay clearance. */
    private String mPurpose;

    /** Holds the date issued and date valid timestamp registered to this barangay
     * clearance. */
    private Timestamp mDateIssued, mDateValid;

    /* Holds the chairman name registered to this barangay clearance. */
    private String mChmName;

    /* Holds the chairman photo path registered to this barangay clearance. */
    private String mChmPhoto;

    /* Holds the chairman signature path registered to this barangay clearance. */
    private String mChmSignature;

    /* Holds the secretary name registered to this barangay clearance. */
    private String mSecName;

    /* Holds the secretary signature path registered to this barangay clearance. */
    private String mSecSignature;

    /* Holds the treasurer name registered to this barangay clearance. */
    private String mTreasurerName;

    /**
     * Contains the signature coordinates and dimensions of the chairman
     * signature in this barangay clearance report.
     *
     * Coordinates and dimensions are based from the mDocumentPane of the
     * BarangayClearanceReportControl.
     *
     * double[0] = X coordinate
     * double[1] = Y coordinate
     * double[2] = width
     * double[3] = height
     *
     * @see javah.controller.BarangayClearanceReportControl
     */
    private double[] mChmSignatureDimension = new double[4];

    /**
     * Contains the signature coordinates and dimensions of the secretary
     * signature in this barangay clearance report.
     *
     * Coordinates and dimensions are based from the mDocumentPane of the
     * BarangayClearanceReportControl.
     *
     * double[0] = X coordinate
     * double[1] = Y coordinate
     * double[2] = width
     * double[3] = height
     *
     * @see javah.controller.BarangayClearanceReportControl
     */
    private double[] mSecSignatureDimension = new double[4];

    /**
     * Contains an array of the seven the kagawad names of this barangay
     * clearance.
     */
    private String[] mKagawadNames = new String[7];

    /**
     * Sets the ID of this barangay clearance.
     * @param id
     *        The unique code of this barangay clearance.
     */
    public void setID(String id) {
        mID = id;
    }

    /**
     * @return the ID of the barangay clearance.
     */
    public String getID() {
        return mID;
    }

    /**
     * Sets the resident ID of thie barangay clearance.
     *
     * @param residentID
     *        The resident ID of this barangay clearance applicant.
     */
    public void setResidentID(String residentID) {
        mResidentID = residentID;
    }

    /**
     * @return the resident ID of this barangay clearance.
     */
    public String getResidentID() {
        return mResidentID;
    }

    /**
     * Sets the resident name of this barangay clearance.
     *
     * @param name
     *        The name of the applicant of this barangay clearance.
     */
    public void setResidentName(String name) {
        mResidentName = name;
    }

    /**
     * @return the name of the applicant of this barangay clearance.
     */
    public String getResidentName() {
        return mResidentName;
    }

    /**
     * Sets the address of this barangay clearance.
     *
     * @param address
     *        The address registered in this barangay clearance.
     */
    public void setAddress(String address) {
        mAddress = address;
    }

    /**
     * @return the address registered in this barangay clearance.
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     * Sets the year of residency of this barangay clearance.
     *
     * @param year
     *        The year of residency of the resident at the time of this barangay clearance
     *        registration. If the year of residency is since birth, then the value is -1.
     */
    public void setYearOfResidency(int year) {
        mYearOfResidency = year;
    }

    /**
     * @return the year of residency of this barangay clearance.
     */
    public int getYearOfResidency() {
        return mYearOfResidency;
    }

    /**
     * Sets the total years of residency of this barangay clearance.
     *
     * @param years
     *        The total number of years the applicant is considered a resident.
     */
    public void setTotalYearsResidency(int years) {
        mTotalYearsResidency = years;
    }

    /**
     * @return the total years of residency of this barangay clearance.
     */
    public int getTotalYearsResidency() {
        return mTotalYearsResidency;
    }

    /**
     * Sets the purpose of this barangay clearance.
     *
     * @param purpose
     *        The purpose of this barangay clearance.
     */
    public void setPurpose(String purpose) {
        mPurpose = purpose;
    }

    /**
     * @return the purpose of this barangay clearance.
     */
    public String getPurpose() {
        return mPurpose;
    }

    /**
     * Sets the date issued of this barangay clearance.
     *
     * @param date
     *        The datetime issuance of this barangay clearance.
     */
    public void setDateIssued(Timestamp date) {
        mDateIssued = date;
    }

    /**
     * @return datetime issuance of this barangay clearance.
     */
    public Timestamp getDateIssued() {
        return mDateIssued;
    }

    /**
     * Sets the date validity of this barangay clearance.
     *
     * @param date
     *        The datetime validity of this barangay clearance.
     */
    public void setDateValid(Timestamp date) {
        mDateValid = date;
    }

    /**
     * @return the datetime validity of this barangay clearance.
     */
    public Timestamp getDateValid() {
        return mDateValid;
    }

    /**
     * Sets the chairman name registered in this barangay clearance.
     *
     * @param name
              The chairman name of this barangay clearance.
     */
    public void setChmName(String name) {
        mChmName = name;
    }

    /**
     * @return the chairman name of this barangay clearance.
     */
    public String getChmName() {
        return mChmName;
    }

    /**
     * Sets the chairman photo path registered in this barangay clearance.
     *
     * @param photo
     *        The chairman photo path of this barangay clearance.
     */
    public void setChmPhoto(String photo) {
        mChmPhoto = photo;
    }

    /**
     * @return the chairman photo path of this barangay clearance.
     */
    public String getChmPhoto() {
        return mChmPhoto;
    }

    /**
     * Sets the chairman signature path registered in this barangay clearance.
     *
     * @param signature
     *        The chairman signature path of this barangay clearance.
     */
    public void setChmSignature(String signature) {
        mChmSignature = signature;
    }

    /**
     * @return the chairman signature path of this barangay clearance.
     */
    public String getChmSignature() {
        return mChmSignature;
    }

    /**
     * Sets the secretary name registered in this barangay clearance.
     *
     * @param name
     *        The secretary name of this barangay clearance.
     */
    public void setSecName(String name) {
        mSecName = name;
    }

    /**
     * @return the secretary name of this barangay clearance.
     */
    public String getSecName() {
        return mSecName;
    }

    /**
     * Sets the secretary signature path registered in this barangay clearance.
     *
     * @param signature
     *        The secretary signature path of this barangay clearance.
     */
    public void setSecSignature(String signature) {
        mSecSignature = signature;
    }

    /**
     * @return the secretary signature path of this barangay clearance.
     */
    public String getSecSignature() {
        return mSecSignature;
    }

    /**
     * Sets the treasurer name of this barangay clearance.
     *
     * @param name
     *        The treasurer name of this barangay clearance.
     */
    public void setTreasurerName(String name) {
        mTreasurerName = name;
    }

    /**
     * @return the treasurer name of this barangay clearance.
     */
    public String getTreasurerName() {
        return mTreasurerName;
    }

    /**
     * Sets the chairman signature coordinate and dimension of this barangay clearance.
     *
     * @param chmSignatureDimension
     *        The chairman signature coordinate and dimension of this barangay clearance.
     */
    public void setChmSignatureDimension(double[] chmSignatureDimension) {
        mChmSignatureDimension = chmSignatureDimension;
    }

    /**
     * @return the chairman signature coordinate and dimension of this barangay clearance.
     */
    public double[] getChmSignatureDimension() {
        return mChmSignatureDimension;
    }

    /**
     * Sets the secretary signature coordinate and dimension of this barangay
     * clearance.
     *
     * @param secSignatureDimension
     *        The secretary signature coordinate and dimension of this barangay clearance.
     */
    public void setSecSignatureDimension(double[] secSignatureDimension) {
        mSecSignatureDimension = secSignatureDimension;
    }

    /**
     * @return the secretary signature coordinate and dimension of this barangay clearance.
     */
    public double[] getSecSignatureDimension() {
        return  mSecSignatureDimension;
    }

    /**
     * Sets the kagawad name in their respective index within the list. The maximum kagawad
     * allowed is 7.
     *
     * @param kagawadIndex
     *        The kagawad index ranges between 0 to 6.
     * @param name
     *        The name of the kagawad which will be placed in mKagawadNames with respect to
     *        kagawadIndex.
     */
    public void setKagawadName(int kagawadIndex, String name) {
        mKagawadNames[kagawadIndex] = name;
    }

    /**
     * Get the kagawad from the mKagawadNames array with the index argument.
     *
     * @param kagawadIndex
     *        The kagawad index ranges between 0 to 6.
     *
     * @return the kagawad name from mKagawadNames with index kagawadIndex.
     */
    public String getKagawadName(int kagawadIndex) {
        return mKagawadNames[kagawadIndex];
    }
}

