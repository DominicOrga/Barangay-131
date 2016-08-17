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
}
