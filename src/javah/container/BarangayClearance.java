package javah.container;

import java.sql.Date;

public class BarangayClearance {
    private String mID;
    private String mResidentID;
    private String mResidentName;
    private String mAddress;
    private int mYearOfResidency;
    private int mTotalYearsResidency;
    private String mPurpose;
    private Date mDateIssued, mDateValid;

    private String mChmName;
    private String mChmPhoto;
    private String mChmSignature;

    private String mSecName;
    private String mSecSignature;

    private String mTreasurerName;

    private double[] mChmSignatureDimension = new double[4];
    private double[] mSecSignatureDimension = new double[4];

    private String[] mKagawadNames = new String[7];

    public void setID(String id) {
        mID = id;
    }

    public String getID() {
        return mID;
    }

    public void setResidentID(String residentID) {
        mResidentID = residentID;
    }

    public String getResidentID() {
        return mResidentID;
    }

    public void setResidentName(String name) {
        mResidentName = name;
    }

    public String getResidentName() {
        return mResidentName;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setYearOfResidency(int year) {
        mYearOfResidency = year;
    }

    public int getYearOfResidency() {
        return mYearOfResidency;
    }

    public void setTotalYearsResidency(int years) {
        mTotalYearsResidency = years;
    }

    public int getTotalYearsResidency() {
        return mTotalYearsResidency;
    }

    public void setPurpose(String purpose) {
        mPurpose = purpose;
    }

    public String getPurpose() {
        return mPurpose;
    }

    public void setDateIssued(Date date) {
        mDateIssued = date;
    }

    public Date getDateIssued() {
        return mDateIssued;
    }

    public void setDateValid(Date date) {
        mDateValid = date;
    }

    public Date getDateValid() {
        return mDateValid;
    }

    public void setChmName(String name) {
        mChmName = name;
    }

    public String getChmName() {
        return mChmName;
    }

    public void setChmPhoto(String photo) {
        mChmPhoto = photo;
    }

    public String getChmPhoto() {
        return mChmPhoto;
    }

    public void setmChmSignature(String signature) {
        mChmSignature = signature;
    }

    public String getChmSignature() {
        return mChmSignature;
    }

    public void setSecName(String name) {
        mSecName = name;
    }

    public String getSecName() {
        return mSecName;
    }

    public void setSecSignature(String signature) {
        mSecSignature = signature;
    }

    public String getSecSignature() {
        return mSecSignature;
    }

    public void setTreasurerName(String name) {
        mTreasurerName = name;
    }

    public String getTreasurerName() {
        return mTreasurerName;
    }

    public void setChmSignatureDimension(double[] chmSignatureDimension) {
        mChmSignatureDimension = chmSignatureDimension;
    }

    public double[] getChmSignatureDimension() {
        return mChmSignatureDimension;
    }

    public void setSecSignatureDimension(double[] secSignatureDimension) {
        mSecSignatureDimension = secSignatureDimension;
    }

    public double[] getSecSignatureDimension() {
        return  mSecSignatureDimension;
    }

    /**
     * Set the kagawad name in the list.
     * @param kagawadIndex between 0 ~ 6.
     * @param name
     */
    private void setKagawadName(byte kagawadIndex, String name) {
        mKagawadNames[kagawadIndex] = name;
    }

    /**
     * Get the kagawad from the Kagawad list.
     * @param kagawadIndex between 0 ~ 6.
     * @return
     */
    public String getKagawadName(byte kagawadIndex) {
        return mKagawadNames[kagawadIndex];
    }
}

