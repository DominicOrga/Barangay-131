package javah.container;

import java.sql.Date;

public class BarangayID {
    private String mID;
    private String mResidentID;
    private String mResidentName;
    private String mAddress;
    private String mPhoto;
    private String mResidentSignature;
    private String mChmName;
    private String mChmSignature;
    private Date mDateIssued;
    private Date mDateValid;

    /**
     * This are special variable to determine the coordinates and dimensions of the barangay ID signatures.
     * Index: [0] = x, [1] = y, [2] = width, [3] = height.
     */
    private Double[] mResidentSignatureDimension;
    private Double[] mChmSignatureDimension;

    public String getID() {
        return mID;
    }

    public void setID(String id) {
        mID = id;
    }

    public String getResidentID() {
        return mResidentID;
    }

    public void setResidentID(String resID) {
        mResidentID = resID;
    }

    public String getResidentName() {
        return mResidentName;
    }

    public void setResidentName(String name) {
        mResidentName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getResidentSignature() {
        return mResidentSignature;
    }

    public void setResidentSignature(String signature) {
        mResidentSignature = signature;
    }

    public Double[] getResidentSignatureDimension() {
        return mResidentSignatureDimension;
    }

    public void setResidentSignatureDimension(Double[] dimension) {
        mResidentSignatureDimension = dimension;
    }

    public String getChmName() {
        return mChmName;
    }

    public void setChmName(String name) {
        mChmName = name;
    }

    public String getChmSignature() {
        return mChmSignature;
    }

    public void setChmSignature(String signature) {
        mChmSignature = signature;
    }

    public Double[] getChmSignatureDimension() {
        return mChmSignatureDimension;
    }

    public void setChmSignatureDimension(Double[] dimension) {
        mChmSignatureDimension = dimension;
    }

    public void setDateIssued(Date dateIssued) {
        mDateIssued = dateIssued;
    }

    public Date getDateIssued() {
        return mDateIssued;
    }

    public void setDateValid(Date dateValid) {
        mDateValid = dateValid;
    }

    public Date getDateValid() {
        return mDateValid;
    }

}
