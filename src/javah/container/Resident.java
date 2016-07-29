package javah.container;

import java.sql.Date;

public class Resident {
    private String mResidentId;
    private String mFirstName, mLastName;
    private String mMiddleName;
    private String mPhotoPath;
    private short mYearOfResidency, mMonthOfResidency;
    private String mAddress1, mAddress2;
    private Date mBirthDate;
    private String mSignature;

    public void setId(String id) {
        mResidentId = id;
    }

    public String getId() {
        return mResidentId;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setMiddleName(String middleName) {
        mMiddleName = middleName;
    }

    public String getMiddleName() {
        return mMiddleName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setBirthDate(Date date) {
        mBirthDate = date;
    }

    public Date getBirthDate() {
        return mBirthDate;
    }

    public void setYearOfResidency(short year) {
        mYearOfResidency = year;
    }

    public int getYearOfResidency() {
        return mYearOfResidency;
    }

    public void setMonthOfResidency(short month) {
        mMonthOfResidency = month;
    }

    public int getMonthOfResidency() {
        return mMonthOfResidency;
    }

    public void setAddress1(String address1) {
        mAddress1 = address1;
    }

    public String getAddress1() {
        return mAddress1;
    }

    public void setAddress2(String address2) {
        mAddress2 = address2;
    }

    public String getAddress2() {
        return mAddress2;
    }

    public void setSignature(String signaturePath) { mSignature = signaturePath; }

    public String getSignature() { return mSignature; }

    @Override
    public String toString() {
        return mFirstName + " " + mMiddleName + " " + mLastName;
    }

    public String toStringLastNameFirst() {
        return mLastName + ", " + mFirstName + " " + mMiddleName;
    }
}
