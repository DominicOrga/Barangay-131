package javah.container;

import java.sql.Timestamp;

/**
 * A class that serves as a data container for a specific business clearance.
 */
public class BusinessClearance {

    /* Holds the ID of this business clerance. */
    private String mID;

    /**
     * A long string containing the concatenated names of the owners. The format
     * of the string is: owner 1 full name, ... and owner n full name.
     */
    private String mOwners;

    /* The ID of the business for this business clearance. */
    private String mBusinessID;

    /* The name of the business registered in this business clearance. */
    private String mBusinessName;

    /* The type of business registered in this business clearance. */
    private String mBusinessType;

    /* The address of the business registered in this business clearance. */
    private String mBusinessAddress;

    /**
     * A timestamp signifying the date and time of registration of this business
     * clearance.
     */
    private Timestamp mDateIssued;

    /* A timestamp signifying the date and time validity of this business clearance. */
    private Timestamp mDateValid;

    /* The chairman name registered in this business clearance. */
    private String mChmName;

    /* The chairman signature path of this business clearance. */
    private String mChmSignature;

    /**
     * Contains the signature coordinates and dimensions of the chairman
     * signature in this business clearance.
     *
     * Coordinates and dimensions are based from the mDocumentPane of the
     * Business Clearance Report Control.
     *
     * double[0] = X coordinate
     * double[1] = Y coordinate
     * double[2] = width
     * double[3] = height
     *
     * @see javah.controller.BusinessClearanceReportControl
     */
    private double[] mChmSignatureDim;

    /* The secretary name registered in this business clearance. */
    private String mSecName;

    /* The secretary signature path of this business clearance. */
    private String mSecSignature;

    /**
     * Contains the signature coordinates and dimensions of the secretary
     * signature in this business clearance.
     *
     * Coordinates and dimensions are based from the mDocumentPane of the
     * Business Clearance Report Control.
     *
     * double[0] = X coordinate
     * double[1] = Y coordinate
     * double[2] = width
     * double[3] = height
     *
     * @see javah.controller.BusinessClearanceReportControl
     */
    private double[] mSecSignatureDim;

    /**
     * Set the ID of this business clearance.
     *
     * @param id
     *        The ID assigned to this business clearance.
     */
    public void setID(String id) {
        mID = id;
    }

    /** 
     * Get the ID of this business clearance.
     *
     * @return the ID of this business clearance.
     */
    public String getID() {
        return mID;
    }

    /**
     * Set the owners registered in this business clearance.
     *
     * @param owners
     *        The string containing the concatenated names of all the owners with the format:
     *        0 <= n <= 4
     *        Owner 0 full name, Owner 1 full name (if any), ... and Owner n full name.
     */
    public void setOwners(String owners) {
        mOwners = owners;
    }

    /**
     * Get the concatenated names of the owners registered in this business clearance.
     *
     * @return the owner names.
     */
    public String getOwners() {
        return mOwners;
    }

    /**
     * Set the business ID of this business clearance.
     * 
     * @param id
     *        The ID of the business of this business clearance. 
     */
    public void setBusinessID(String id) {
        mBusinessID = id;
    }

    /**
     * Get the ID of business of this business clearance.
     *
     * @return the ID of the business of this business clearance.
     */
    public String getBusinessID() {
        return mBusinessID;
    }

    /**
     * Set the name of the business of this business clearance.
     *
     * @param name
     *        The name of the business of this business clearance.
     */
    public void setBusinessName(String name) {
        mBusinessName = name;
    }

    /**
     * Get the business name of this business clearance.
     *
     * @return the business name of this business clearance.
     */
    public String getBusinessName() {
        return mBusinessName;
    }

    /**
     * Set the type of the business of this business clearance.
     *
     * @param type
     *        The type of the business of this business clearance.
     */
    public void setBusinessType(String type) {
        mBusinessType = type;
    }

    /**
     * Get the business type of this business clearance.
     *
     * @return the business type of this business clearance.
     */
    public String getBusinessType() {
        return mBusinessType;
    }

    /**
     * Set the business address registered in this business clearance.
     *
     * @param address
     *        The business address of this business clearance.
     */
    public void setBusinessAddress(String address) {
        mBusinessAddress = address;
    }

    /**
     * Get the business address registered in this business clearance.
     *
     * @return the business address of this business clearance.
     */
    public String getBusinessAddress() {
        return mBusinessAddress;
    }

    /**
     * Set the date issued of this business clearance.
     *
     * @param date
     *        The datetime issuance of this business clearance.
     */
    public void setDateIssued(Timestamp date) {
        mDateIssued = date;
    }

    /**
     * Get the datetime issuance of this business clearance.
     *
     * @return datetime issuance of this business clearance.
     */
    public Timestamp getDateIssued() {
        return mDateIssued;
    }

    /**
     * Set the date validity of this business clearance.
     *
     * @param date
     *        The datetime validity of this business clearance.
     */
    public void setDateValid(Timestamp date) {
        mDateValid = date;
    }

    /**
     * Get the datetime validity of this business clearance.
     *
     * @return the datetime validity of this business clearance.
     */
    public Timestamp getDateValid() {
        return mDateValid;
    }

    /**
     * Sets the chairman name registered in this barangay clearance.
     *
     * @param name
     *        The chairman name of this barangay clearance.
     */
    public void setChmName(String name) {
        mChmName = name;
    }

    /**
     * Get the chairman name of this business clearance.
     *
     * @return the chairman name of this barangay clearance.
     */
    public String getChmName() {
        return mChmName;
    }

    /**
     * Set the chairman signature path registered in this barangay clearance.
     *
     * @param signature
     *        The chairman signature path of this barangay clearance.
     */
    public void setChmSignature(String signature) {
        mChmSignature = signature;
    }

    /**
     * Get the chairman signature path of this business clearance.
     *
     * @return the chairman signature path of this barangay clearance.
     */
    public String getChmSignature() {
        return mChmSignature;
    }

    /**
     * Set the secretary name registered in this barangay clearance.
     *
     * @param name
     *        The secretary name of this barangay clearance.
     */
    public void setSecName(String name) {
        mSecName = name;
    }

    /**
     * Get the secretary name of this business clearance.
     *
     * @return the secretary name of this barangay clearance.
     */
    public String getSecName() {
        return mSecName;
    }

    /**
     * Set the secretary signature path registered in this barangay clearance.
     *
     * @param signature
     *        The secretary signature path of this barangay clearance.
     */
    public void setSecSignature(String signature) {
        mSecSignature = signature;
    }

    /**
     * Get the secretary signature path of this business clearance.
     *
     * @return the secretary signature path of this barangay clearance.
     */
    public String getSecSignature() {
        return mSecSignature;
    }

    /**
     * Set the chairman signature coordinate and dimension of this barangay clearance.
     *
     * @param chmSignatureDimension
     *        The chairman signature coordinate and dimension of this barangay clearance.
     *
     *        Coordinates and dimensions are based from the mDocumentPane of the
     *        Business Clearance Report Control.
     *
     *        double[0] = X coordinate
     *        double[1] = Y coordinate
     *        double[2] = width
     *        double[3] = height
     */
    public void setChmSignatureDimension(double[] chmSignatureDimension) {
        mChmSignatureDim = chmSignatureDimension;
    }

    /**
     * Get the chairman signature coordinate and dimension.
     *
     * @return the chairman signature coordinate and dimension of this barangay clearance.
     */
    public double[] getChmSignatureDimension() {
        return mChmSignatureDim;
    }

    /**
     * Set the secretary signature coordinate and dimension of this barangay
     * clearance.
     *
     * @param secSignatureDimension
     *        The secretary signature coordinate and dimension of this barangay clearance.
     *
     *        Coordinates and dimensions are based from the mDocumentPane of the
     *        Business Clearance Report Control.
     *
     *        double[0] = X coordinate
     *        double[1] = Y coordinate
     *        double[2] = width
     *        double[3] = height
     *
     * @see javah.controller.BusinessClearanceReportControl
     */
    public void setSecSignatureDimension(double[] secSignatureDimension) {
        mSecSignatureDim = secSignatureDimension;
    }

    /**
     * Get the secretary signature coordinate and dimension.
     *
     * @return the secretary signature coordinate and dimension of this barangay clearance.
     */
    public double[] getSecSignatureDimension() {
        return  mSecSignatureDim;
    }


}
