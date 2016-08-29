package javah.contract;

public class DatabaseContract {

    static class RowColumn {
        public static final String COLUMN_ID = "id";
    }

    public static class ResidentEntry extends RowColumn {
        public static final String TABLE_NAME = "BarangayDB.Resident";

        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_MIDDLE_NAME = "middle_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_AUXILIARY = "auxiliary";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_YEAR_OF_RESIDENCY = "year_of_residency";
        public static final String COLUMN_MONTH_OF_RESIDENCY = "month_of_residency";
        public static final String COLUMN_IS_ARCHIVED = "is_archived";
        public static final String COLUMN_ADDRESS_1 = "address_1";
        public static final String COLUMN_ADDRESS_2 = "address_2";
        public static final String COLUMN_BIRTH_DATE = "birth_date";
    }

    public static class BarangayIdEntry extends RowColumn {
        public static final String TABLE_NAME = "BarangayDB.Barangay_id";

        public static final String COLUMN_RESIDENT_ID = "resident_id";
        public static final String COLUMN_RESIDENT_NAME = "resident_name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_RESIDENT_SIGNATURE = "resident_signature";
        public static final String COLUMN_RESIDENT_SIGNATURE_DIMENSION = "res_signature_dim";
        public static final String COLUMN_CHAIRMAN_NAME = "chairman_name";
        public static final String COLUMN_CHAIRMAN_SIGNATURE = "chairman_signature";
        public static final String COLUMN_CHAIRMAN_SIGNATURE_DIMENSION = "chm_signature_dim";
        public static final String COLUMN_DATE_ISSUED = "date_issued";
        public static final String COLUMN_DATE_VALID = "date_valid";
    }

    public static class BarangayClearanceEntry extends RowColumn {
        public static final String TABLE_NAME = "BarangayDB.Barangay_clearance";

        public static final String COLUMN_RESIDENT_ID = "resident_id";
        public static final String COLUMN_RESIDENT_NAME = "resident_name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_YEAR_OF_RESIDENCY = "year_of_residency";
        public static final String COLUMN_TOTAL_YEARS_RESIDENCY = "total_years_residency";
        public static final String COLUMN_PURPOSE = "purpose";
        public static final String COLUMN_DATE_ISSUED = "date_issued";
        public static final String COLUMN_DATE_VALID = "date_valid";
        public static final String COLUMN_CHAIRMAN_NAME = "chairman_name";
        public static final String COLUMN_CHAIRMAN_PHOTO = "chairman_photo";
        public static final String COLUMN_CHAIRMAN_SIGNATURE = "chairman_signature";
        public static final String COLUMN_CHAIRMAN_SIGNATURE_DIMENSION = "chm_sign_dim";
        public static final String COLUMN_SECRETARY_NAME = "secretary_name";
        public static final String COLUMN_SECRETARY_SIGNATURE = "secretary_signature";
        public static final String COLUMN_SECRETARY_SIGNATURE_DIMENSION = "sec_sign_dim";
        public static final String COLUMN_TREASURER_NAME = "treasurer_name";
        public static final String COLUMN_KAGAWAD_1_NAME = "kagawad1_name";
        public static final String COLUMN_KAGAWAD_2_NAME = "kagawad2_name";
        public static final String COLUMN_KAGAWAD_3_NAME = "kagawad3_name";
        public static final String COLUMN_KAGAWAD_4_NAME = "kagawad4_name";
        public static final String COLUMN_KAGAWAD_5_NAME = "kagawad5_name";
        public static final String COLUMN_KAGAWAD_6_NAME = "kagawad6_name";
        public static final String COLUMN_KAGAWAD_7_NAME = "kagawad7_name";
    }

    public static class BusinessClearanceEntry extends RowColumn {
        public static final String TABLE_NAME = "BarangayDB.Business_clearance";

        public static final String COLUMN_CLIENT_NAME = "client_name";
        public static final String COLUMN_BUSINESS_NAME = "business_name";
        public static final String COLUMN_BUSINESS_TYPE = "business_type";
        public static final String COLUMN_BUSINESS_ADDRESS = "business_address";
        public static final String COLUMN_DATE_ISSUED = "date_issued";
        public static final String COLUMN_DATE_VALID = "date_valid";
        public static final String COLUMN_CHAIRMAN_NAME = "chairman_name";
        public static final String COLUMN_CHAIRMAN_SIGNATURE = "chairman_signature";
        public static final String COLUMN_CHAIRMAN_SIGNATURE_DIMENSION = "chm_sign_dim";
        public static final String COLUMN_SECRETARY_NAME = "secretary_name";
        public static final String COLUMN_SECRETARY_SIGNATURE = "secretary_signature";
        public static final String COLUMN_SECRETARY_SIGNATURE_DIMENSION = "sec_sign_dim";
    }
}
