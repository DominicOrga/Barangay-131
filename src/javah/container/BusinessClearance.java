package javah.container;

import java.sql.Timestamp;

public class BusinessClearance {

    private String mID;
    private String mClientName;
    private String[] mAssociates;
    private String mBusinessName;
    private String mBusinessType;
    private String mBusinessAddress;
    private Timestamp mDateIssued;
    private Timestamp mDateValid;
    private String mChmName;
    private String mChmSignature;
    private double[] mChmSignatureDim;
    private String mSecName;
    private String mSecSignature;
    private double[] mSecSignatureDim;

    public double[] getSecSignatureDime() {
        return mSecSignatureDim;
    }

    public void setSecSignatureDim(double[] dimension) {
        mSecSignatureDim = dimension;
    }

    public String getSecSignature() {
        return mSecSignature;
    }

    public void setSecSignature(String signature) {
        mSecSignature = signature;
    }

    public String getSecName() {
        return mSecName;
    }

    public void setSecName(String name) {
        mSecName = name;
    }

    public double[] getChmSignatureDim() {
        return mChmSignatureDim;
    }

    public void setChmSignatureDim(double[] dimension) {
        mChmSignatureDim = dimension;
    }

    public String getChmSignature() {
        return mChmSignature;
    }

    public void setChmSignature(String signature) {
        mChmSignature = signature;
    }

    public String getChmName() {
        return mChmName;
    }

    public void setChmName(String name) {
        mChmName = name;
    }

    public Timestamp getDateValid() {
        return mDateValid;
    }

    public void setDateValid(Timestamp date) {
        mDateValid = date;
    }

    public Timestamp getDateIssued() {
        return mDateIssued;
    }

    public void setDateIssued(Timestamp date) {
        mDateIssued = date;
    }

    public String getBusinessAddress() {
        return mBusinessAddress;
    }

    public void setBusinessAddress(String address) {
        mBusinessAddress = address;
    }

    public String getBusinessType() {
        return mBusinessType;
    }

    public void setBusinessType(String businessType) {
        mBusinessType = businessType;
    }

    public String getBusinessName() {
        return mBusinessName;
    }

    public void setBusinessName(String businessName) {
        mBusinessName = businessName;
    }

    public String[] getAssociates() {
        return mAssociates;
    }

    public void setAssociates(String[] associates) {
        mAssociates = associates;
    }

    public String getClientName() {
        return mClientName;
    }

    public void setClientName(String clientName) {
        mClientName = clientName;
    }

    public String getID() {
        return mID;
    }

    public void setID(String id) {
        mID = id;
    }
}
