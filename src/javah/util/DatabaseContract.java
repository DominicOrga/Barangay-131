package javah.util;

public class DatabaseContract {

    public static class ResidentEntry {
        public static final String TABLE_NAME = "BarangayDB.Resident";

        public static final String COLUMN_ID = "id";
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
        public static final String COLUMN_SIGNATURE = "signature";
    }
}
