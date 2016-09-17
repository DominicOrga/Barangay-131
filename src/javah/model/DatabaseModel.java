package javah.model;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javah.container.*;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import javah.contract.DatabaseContract.*;

/**
 * A class that that is connected to the system's database. It has the capability
 * to create, read, update and delete data from the database.
 */
public class DatabaseModel {

    /* A variable that holds the a cacheable connection the the database. */
    private MysqlDataSource mDataSource;

    /**
     * A constructor that establishes the connection.
     *
     * Bug: Apparently, using parametarized query with a data source connection to
     * allow connection pooling converts the expected results to their corresponding
     * column names. (e.g. 131-005 == resident_id).
     */
    public DatabaseModel() {
        // Initialize the data source.
        mDataSource = new MysqlDataSource();
        mDataSource.setURL("jdbc:mysql://localhost/BarangayDB");
        mDataSource.setUser("root");
        mDataSource.setPassword("horizon");
    }

    /**
     * Return the non-archived residents IDs and Names.
     *
     * @return an array of lists with elements:
     *         List[0] =  The resident IDs.
     *         List[1] = Formatted resident names.
     */
    public List[] getResidentEssentials() {

        List[] returnList = new List[2];
        List<String> residentsIdList = new ArrayList<>();
        List<String> residentNameList = new ArrayList<>();

        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s, %s FROM %s ORDER BY %s, %s, %s",
                            ResidentEntry.COLUMN_ID,
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.COLUMN_AUXILIARY,
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME)
                    );

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                residentsIdList.add(resultSet.getString(ResidentEntry.COLUMN_ID));

                String name = String.format("%s, %s %s.",
                        resultSet.getString(ResidentEntry.COLUMN_LAST_NAME),
                        resultSet.getString(ResidentEntry.COLUMN_FIRST_NAME),
                        Character.toUpperCase(resultSet.getString(ResidentEntry.COLUMN_MIDDLE_NAME).charAt(0))
                );

                String auxiliary = resultSet.getString(ResidentEntry.COLUMN_AUXILIARY);
                name += auxiliary == null ? "" : " " + auxiliary;

                residentNameList.add(name);
            }

            returnList[0] = residentsIdList;
            returnList[1] = residentNameList;

            dbConnection.close();
            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    /**
     * Return the Barangay ID Ids, resident ids and the issued date.
     *
     * @return an array of lists with elements:
     *         array[0] = Barangay ID IDs, used to specify a Barangay ID.
     *         array[1] = Resident IDs of the Barangay IDs, used to update the barangay ID
     *                    cached data when a specific resident is modified or dropped.
     *         array[2] = Issued date of each Barangay ID, used to sort the Barangay IDs within
     *                    the list paging.
     */
    public List[] getBarangayIDEssentials() {
        List[] result = new List[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Date>()};

        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            // Only query the barangay ID data with applicants that are not archived.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s FROM %s ORDER BY %s DESC",
                            BarangayIdEntry.COLUMN_ID,
                            BarangayIdEntry.COLUMN_RESIDENT_ID,
                            BarangayIdEntry.COLUMN_DATE_ISSUED,
                            BarangayIdEntry.TABLE_NAME,
                            BarangayIdEntry.COLUMN_DATE_ISSUED));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result[0].add(resultSet.getString(BarangayIdEntry.COLUMN_ID));
                result[1].add(resultSet.getString(BarangayIdEntry.COLUMN_RESIDENT_ID));
                result[2].add(resultSet.getDate(BarangayIdEntry.COLUMN_DATE_ISSUED));
            }

            dbConnection.close();
            preparedStatement.close();
            resultSet.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Return the Barangay Clearance Ids, resident ids and the issued date.
     *
     * @return an array of lists with elements:
     *         array[0] = Barangay clearance IDs, used to specify a barangay clearance.
     *         array[1] = Resident IDs of the barangay clearances, used to update the barangay
     *                    clearance cached data when a specific resident is modified or dropped.
     *         array[2] = Issued date of each barangay clearance, used to sort the barangay
     *                    clearance within the list paging.
     */
    public List[] getBarangayClearanceEssentials() {
        List[] result = new List[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Date>()};

        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            // Only query the barangay ID data with applicants that are not archived.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s FROM %s ORDER BY %s DESC",
                            BarangayClearanceEntry.COLUMN_ID,
                            BarangayClearanceEntry.COLUMN_RESIDENT_ID,
                            BarangayClearanceEntry.COLUMN_DATE_ISSUED,
                            BarangayClearanceEntry.TABLE_NAME,
                            BarangayClearanceEntry.COLUMN_DATE_ISSUED));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result[0].add(resultSet.getString(BarangayClearanceEntry.COLUMN_ID));
                result[1].add(resultSet.getString(BarangayClearanceEntry.COLUMN_RESIDENT_ID));
                result[2].add(resultSet.getDate(BarangayClearanceEntry.COLUMN_DATE_ISSUED));
            }

            dbConnection.close();
            preparedStatement.close();
            resultSet.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Return the business IDs and Names.
     *
     * @return an array of lists with elements:
     *         List[0] =  The business IDs.
     *         List[1] = Formatted resident names.
     */
    public List[] getBusinessEssentials() {

        List[] returnList = new List[2];
        List<String> businessIDs = new ArrayList<>();
        List<String> businessNames = new ArrayList<>();

        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s FROM %s ORDER BY %s",
                            BusinessEntry.COLUMN_ID,
                            BusinessEntry.COLUMN_BUSINESS_NAME,
                            BusinessEntry.TABLE_NAME,
                            BusinessEntry.COLUMN_BUSINESS_NAME)
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                businessIDs.add(resultSet.getString(BusinessEntry.COLUMN_ID));
                businessNames.add(resultSet.getString(BusinessEntry.COLUMN_BUSINESS_NAME));
            }

            returnList[0] = businessIDs;
            returnList[1] = businessNames;

            dbConnection.close();
            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    /**
     * Return the business clearance Ids, resident ids and the issued date.
     *
     * @return an array of lists with elements:
     *         array[0] = Business clearance IDs, used to specify a business clearance.
     *         array[1] = Business IDs of the business clearances, used to update the business
     *                    clearance cached data when a specific business is modified or dropped.
     *         array[2] = Issued date of each business clearance, used to sort the business
     *                    clearance within the list paging.
     */
    public List[] getBusinessClearanceEssentials() {
        List[] result = new List[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Date>()};

        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            // Only query the barangay ID data with applicants that are not archived.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s FROM %s ORDER BY %s DESC",
                            BusinessClearanceEntry.COLUMN_ID,
                            BusinessClearanceEntry.COLUMN_BUSINESS_ID,
                            BusinessClearanceEntry.COLUMN_DATE_ISSUED,
                            BusinessClearanceEntry.TABLE_NAME,
                            BusinessClearanceEntry.COLUMN_DATE_ISSUED)
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result[0].add(resultSet.getString(BusinessClearanceEntry.COLUMN_ID));
                result[1].add(resultSet.getString(BusinessClearanceEntry.COLUMN_BUSINESS_ID));
                result[2].add(resultSet.getDate(BusinessClearanceEntry.COLUMN_DATE_ISSUED));
            }

            dbConnection.close();
            preparedStatement.close();
            resultSet.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Create a resident and store it in the database.
     *
     * @param resident
     *        The resident data holding containing the information about the resident to be
     *        created.
     *
     * @return the generated resident ID for the resident. If the resident was not created,
     *         then return null.
     */
    public String createResident(Resident resident) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            String residentID = generateID(ResidentEntry.TABLE_NAME);

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_ID,
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.COLUMN_AUXILIARY,
                            ResidentEntry.COLUMN_BIRTH_DATE,
                            ResidentEntry.COLUMN_PHOTO,
                            ResidentEntry.COLUMN_YEAR_OF_RESIDENCY,
                            ResidentEntry.COLUMN_MONTH_OF_RESIDENCY,
                            ResidentEntry.COLUMN_ADDRESS_1,
                            ResidentEntry.COLUMN_ADDRESS_2)
            );

            statement.setString(1, residentID);
            statement.setString(2, resident.getFirstName());
            statement.setString(3, resident.getMiddleName());
            statement.setString(4, resident.getLastName());
            statement.setString(5, resident.getAuxiliary());
            statement.setDate(6, resident.getBirthDate());
            statement.setString(7, resident.getPhotoPath());
            statement.setInt(8, resident.getYearOfResidency());
            statement.setInt(9, resident.getMonthOfResidency());
            statement.setString(10, resident.getAddress1());
            statement.setString(11, resident.getAddress2());

            statement.execute();
            statement.close();
            dbConnection.close();

            return residentID;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create a barangay ID and store it into the database.
     *
     * @param barangayID
     *        The barangay ID containing the data about a specific resident to be created.
     *
     * @return the generated ID for the barangay ID. If the barangay ID creation fails, then
     *         return null.
     */
    public String createBarangayID(BarangayID barangayID) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            BarangayIdEntry.TABLE_NAME,
                            BarangayIdEntry.COLUMN_ID,
                            BarangayIdEntry.COLUMN_RESIDENT_ID,
                            BarangayIdEntry.COLUMN_RESIDENT_NAME,
                            BarangayIdEntry.COLUMN_ADDRESS,
                            BarangayIdEntry.COLUMN_PHOTO,
                            BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE,
                            BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE_DIMENSION,
                            BarangayIdEntry.COLUMN_CHAIRMAN_NAME,
                            BarangayIdEntry.COLUMN_CHAIRMAN_SIGNATURE,
                            BarangayIdEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION,
                            BarangayIdEntry.COLUMN_DATE_ISSUED,
                            BarangayIdEntry.COLUMN_DATE_VALID));

            statement.setString(1, barangayID.getID());
            statement.setString(2, barangayID.getResidentID());
            statement.setString(3, barangayID.getResidentName());
            statement.setString(4, barangayID.getAddress());
            statement.setString(5, barangayID.getPhoto());
            statement.setString(6, barangayID.getResidentSignature());

            double[] signatureDimension = barangayID.getResidentSignatureDimension();
            statement.setString(7, signatureDimension != null ?
                    String.format("%.5f %.5f %.5f %.5f",
                            signatureDimension[0],
                            signatureDimension[1],
                            signatureDimension[2],
                            signatureDimension[3]) : null);

            statement.setString(8, barangayID.getChmName());
            statement.setString(9, barangayID.getChmSignature());

            signatureDimension = barangayID.getChmSignatureDimension();
            statement.setString(10, String.format("%.5f %.5f %.5f %.5f",
                    signatureDimension[0], signatureDimension[1], signatureDimension[2], signatureDimension[3]));

            statement.setTimestamp(11, barangayID.getDateIssued());
            statement.setTimestamp(12, barangayID.getDateValid());

            statement.execute();

            statement.close();
            dbConnection.close();

            return barangayID.getID();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Create a barangay clearance and store it into the database.
     *
     * @param barangayClearance
     *        The barangay clearance to be created.
     *
     * @return the generated ID for the barangay clearance. If the barangay clearance creation
     *         fails, then return null.
     */
    public String createBarangayClearance(BarangayClearance barangayClearance) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, " +
                                    "%s, %s, %s, %s, %s, %s, %s) " +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            BarangayClearanceEntry.TABLE_NAME,
                            BarangayClearanceEntry.COLUMN_ID,
                            BarangayClearanceEntry.COLUMN_RESIDENT_ID,
                            BarangayClearanceEntry.COLUMN_RESIDENT_NAME,
                            BarangayClearanceEntry.COLUMN_ADDRESS,
                            BarangayClearanceEntry.COLUMN_YEAR_OF_RESIDENCY,
                            BarangayClearanceEntry.COLUMN_TOTAL_YEARS_RESIDENCY,
                            BarangayClearanceEntry.COLUMN_PURPOSE,
                            BarangayClearanceEntry.COLUMN_DATE_ISSUED,
                            BarangayClearanceEntry.COLUMN_DATE_VALID,
                            BarangayClearanceEntry.COLUMN_CHAIRMAN_NAME,
                            BarangayClearanceEntry.COLUMN_CHAIRMAN_PHOTO,
                            BarangayClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE,
                            BarangayClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION,
                            BarangayClearanceEntry.COLUMN_SECRETARY_NAME,
                            BarangayClearanceEntry.COLUMN_SECRETARY_SIGNATURE,
                            BarangayClearanceEntry.COLUMN_SECRETARY_SIGNATURE_DIMENSION,
                            BarangayClearanceEntry.COLUMN_TREASURER_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_1_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_2_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_3_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_4_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_5_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_6_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_7_NAME
                    ));

            statement.setString(1, barangayClearance.getID());
            statement.setString(2, barangayClearance.getResidentID());
            statement.setString(3, barangayClearance.getResidentName());
            statement.setString(4, barangayClearance.getAddress());
            statement.setInt(5, barangayClearance.getYearOfResidency());
            statement.setInt(6, barangayClearance.getTotalYearsResidency());
            statement.setString(7, barangayClearance.getPurpose());
            statement.setTimestamp(8, barangayClearance.getDateIssued());
            statement.setTimestamp(9, barangayClearance.getDateValid());
            statement.setString(10, barangayClearance.getChmName());
            statement.setString(11, barangayClearance.getChmPhoto());
            statement.setString(12, barangayClearance.getChmSignature());
            statement.setString(14, barangayClearance.getSecName());
            statement.setString(15, barangayClearance.getSecSignature());
            statement.setString(17, barangayClearance.getTreasurerName());

            for (int i = 0; i < 7; i++)
                statement.setString(18 + i, barangayClearance.getKagawadName(i));

            double[] signatureDimension = barangayClearance.getChmSignatureDimension();
            statement.setString(13, signatureDimension != null ?
                    String.format("%.5f %.5f %.5f %.5f",
                            signatureDimension[0],
                            signatureDimension[1],
                            signatureDimension[2],
                            signatureDimension[3]) : null);

            signatureDimension = barangayClearance.getSecSignatureDimension();
            statement.setString(16, signatureDimension != null ?
                    String.format("%.5f %.5f %.5f %.5f",
                            signatureDimension[0],
                            signatureDimension[1],
                            signatureDimension[2],
                            signatureDimension[3]) : null);

            statement.execute();
            statement.close();
            dbConnection.close();

            return barangayClearance.getID();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * Create a business and store it in the database.
     *
     * @param business
     *        The business data holding containing the information about the business to be
     *        created.
     *
     * @return the generated business ID of the business. If the business was not created,
     *         then return null.
     */
    public String createBusiness(Business business) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            String id = generateID(BusinessEntry.TABLE_NAME);

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, " +
                                    "%s, %s, %s, %s, %s, %s, %s) " +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            BusinessEntry.TABLE_NAME,
                            BusinessEntry.COLUMN_ID,
                            BusinessEntry.COLUMN_BUSINESS_NAME,
                            BusinessEntry.COLUMN_BUSINESS_TYPE,
                            BusinessEntry.COLUMN_BUSINESS_ADDRESS,
                            BusinessEntry.COLUMN_OWNER_1_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_1_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_1_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_1_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_2_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_2_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_2_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_2_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_3_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_3_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_3_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_3_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_4_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_4_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_4_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_4_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_5_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_5_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_5_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_5_AUXILIARY)
            );

            statement.setString(1, id);
            statement.setString(2, business.getName());
            statement.setString(3, business.getType());
            statement.setString(4, business.getAddress());

            int x = 5;
            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 4; j++)
                    statement.setString(x++, business.getOwners()[i][j]);

            statement.execute();
            statement.close();
            dbConnection.close();

            return id;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create a business clearance and store it into the database.
     *
     * @param businessClearance
     *        The business clearance to be created.
     *
     * @return the generated ID for the business clearance. If the business clearance creation
     *         fails, then return null.
     */
    public String createBusinessClearance(BusinessClearance businessClearance) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)" +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            BusinessClearanceEntry.TABLE_NAME,
                            BusinessClearanceEntry.COLUMN_ID,
                            BusinessClearanceEntry.COLUMN_DATE_ISSUED,
                            BusinessClearanceEntry.COLUMN_DATE_VALID,
                            BusinessClearanceEntry.COLUMN_CHAIRMAN_NAME,
                            BusinessClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE,
                            BusinessClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION,
                            BusinessClearanceEntry.COLUMN_SECRETARY_NAME,
                            BusinessClearanceEntry.COLUMN_SECRETARY_SIGNATURE,
                            BusinessClearanceEntry.COLUMN_SECRETARY_SIGNATURE_DIMENSION,
                            BusinessClearanceEntry.COLUMN_BUSINESS_ID,
                            BusinessClearanceEntry.COLUMN_BUSINESS_NAME,
                            BusinessClearanceEntry.COLUMN_BUSINESS_TYPE,
                            BusinessClearanceEntry.COLUMN_BUSINESS_ADDRESS,
                            BusinessClearanceEntry.COLUMN_OWNERS,
                            BusinessClearanceEntry.COLUMN_CLIENT)
            );

            statement.setString(1, businessClearance.getID());
            statement.setTimestamp(2, businessClearance.getDateIssued());
            statement.setTimestamp(3, businessClearance.getDateValid());
            statement.setString(4, businessClearance.getChmName());
            statement.setString(5, businessClearance.getChmSignature());
            statement.setString(7, businessClearance.getSecName());
            statement.setString(8, businessClearance.getSecSignature());
            statement.setString(10, businessClearance.getID());
            statement.setString(11, businessClearance.getBusinessName());
            statement.setString(12, businessClearance.getBusinessType());
            statement.setString(13, businessClearance.getBusinessAddress());
            statement.setString(14, businessClearance.getOwners());
            statement.setString(15, businessClearance.getClient());

            double[] signatureDimension = businessClearance.getChmSignatureDimension();
            statement.setString(6, signatureDimension != null ?
                    String.format("%.5f %.5f %.5f %.5f",
                            signatureDimension[0],
                            signatureDimension[1],
                            signatureDimension[2],
                            signatureDimension[3]) : null);

            signatureDimension = businessClearance.getSecSignatureDimension();
            statement.setString(9, signatureDimension != null ?
                    String.format("%.5f %.5f %.5f %.5f",
                            signatureDimension[0],
                            signatureDimension[1],
                            signatureDimension[2],
                            signatureDimension[3]) : null);

            statement.execute();
            statement.close();
            dbConnection.close();

            return businessClearance.getID();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get a specific resident from the database.
     *
     * @param residentId
     *        The ID of the resident to be fetched.
     *
     * @return the resident having the specified resident Id. Return null if no match is found.
     */
    public Resident getResident(String residentId) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?",
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.COLUMN_AUXILIARY,
                            ResidentEntry.COLUMN_BIRTH_DATE,
                            ResidentEntry.COLUMN_PHOTO,
                            ResidentEntry.COLUMN_YEAR_OF_RESIDENCY,
                            ResidentEntry.COLUMN_MONTH_OF_RESIDENCY,
                            ResidentEntry.COLUMN_ADDRESS_1,
                            ResidentEntry.COLUMN_ADDRESS_2,
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_ID
                    )
            );

            preparedStatement.setString(1, residentId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Resident resident = new Resident();

                resident.setId(residentId);
                resident.setFirstName(resultSet.getString(ResidentEntry.COLUMN_FIRST_NAME));
                resident.setMiddleName(resultSet.getString(ResidentEntry.COLUMN_MIDDLE_NAME));
                resident.setLastName(resultSet.getString(ResidentEntry.COLUMN_LAST_NAME));
                resident.setAuxiliary(resultSet.getString(ResidentEntry.COLUMN_AUXILIARY));
                resident.setBirthDate(resultSet.getDate(ResidentEntry.COLUMN_BIRTH_DATE));
                resident.setPhotoPath(resultSet.getString(ResidentEntry.COLUMN_PHOTO));
                resident.setYearOfResidency(resultSet.getShort(ResidentEntry.COLUMN_YEAR_OF_RESIDENCY));
                resident.setMonthOfResidency(resultSet.getShort(ResidentEntry.COLUMN_MONTH_OF_RESIDENCY));
                resident.setAddress1(resultSet.getString(ResidentEntry.COLUMN_ADDRESS_1));
                resident.setAddress2(resultSet.getString(ResidentEntry.COLUMN_ADDRESS_2));

                dbConnection.close();
                preparedStatement.close();
                resultSet.close();

                return resident;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get a specific barangay ID from the database.
     *
     * @param id
     *        The ID of the barangay ID to be fetched.
     *
     * @return the barangay ID having the specified ID. Return null if no match is found.
     */
    public BarangayID getBarangayID(String id) {
        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?",
                            BarangayIdEntry.COLUMN_RESIDENT_ID,
                            BarangayIdEntry.COLUMN_RESIDENT_NAME,
                            BarangayIdEntry.COLUMN_ADDRESS,
                            BarangayIdEntry.COLUMN_PHOTO,
                            BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE,
                            BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE_DIMENSION,
                            BarangayIdEntry.COLUMN_CHAIRMAN_NAME,
                            BarangayIdEntry.COLUMN_CHAIRMAN_SIGNATURE,
                            BarangayIdEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION,
                            BarangayIdEntry.COLUMN_DATE_ISSUED,
                            BarangayIdEntry.COLUMN_DATE_VALID,
                            BarangayIdEntry.TABLE_NAME,
                            BarangayIdEntry.COLUMN_ID
                    )
            );

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BarangayID barangayID = new BarangayID();

                barangayID.setID(id);
                barangayID.setResidentID(resultSet.getString(BarangayIdEntry.COLUMN_RESIDENT_ID));
                barangayID.setResidentName(resultSet.getString(BarangayIdEntry.COLUMN_RESIDENT_NAME));
                barangayID.setAddress(resultSet.getString(BarangayIdEntry.COLUMN_ADDRESS));
                barangayID.setPhoto(resultSet.getString(BarangayIdEntry.COLUMN_PHOTO));
                barangayID.setResidentSignature(resultSet.getString(BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE));

                String signatureDimension = resultSet.getString(BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE_DIMENSION);
                barangayID.setResidentSignatureDimension(signatureDimension != null ?
                        Arrays.asList(signatureDimension.split(" ")).stream().mapToDouble(Double::parseDouble).toArray() :
                        null);

                barangayID.setChmName(resultSet.getString(BarangayIdEntry.COLUMN_CHAIRMAN_NAME));
                barangayID.setChmSignature(resultSet.getString(BarangayIdEntry.COLUMN_CHAIRMAN_SIGNATURE));

                signatureDimension = resultSet.getString(BarangayIdEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION);
                barangayID.setChmSignatureDimension(signatureDimension != null ?
                        Arrays.asList(signatureDimension.split(" ")).stream().mapToDouble(Double::parseDouble).toArray() :
                        null);

                barangayID.setDateIssued(resultSet.getTimestamp(BarangayIdEntry.COLUMN_DATE_ISSUED));
                barangayID.setDateValid(resultSet.getTimestamp(BarangayIdEntry.COLUMN_DATE_VALID));

                dbConnection.close();
                preparedStatement.close();
                resultSet.close();

                return barangayID;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get a specific barangay ID from the database.
     *
     * @param id
     *        The ID of the barangay ID to be fetched.
     *
     * @return the barangay ID having the specified ID. Return null if no match is found.
     */
    public BarangayClearance getBarangayClearance(String id) {
        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, " +
                                    "%s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?",
                            BarangayClearanceEntry.COLUMN_ID,
                            BarangayClearanceEntry.COLUMN_RESIDENT_ID,
                            BarangayClearanceEntry.COLUMN_RESIDENT_NAME,
                            BarangayClearanceEntry.COLUMN_ADDRESS,
                            BarangayClearanceEntry.COLUMN_YEAR_OF_RESIDENCY,
                            BarangayClearanceEntry.COLUMN_TOTAL_YEARS_RESIDENCY,
                            BarangayClearanceEntry.COLUMN_PURPOSE,
                            BarangayClearanceEntry.COLUMN_DATE_ISSUED,
                            BarangayClearanceEntry.COLUMN_DATE_VALID,
                            BarangayClearanceEntry.COLUMN_CHAIRMAN_NAME,
                            BarangayClearanceEntry.COLUMN_CHAIRMAN_PHOTO,
                            BarangayClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE,
                            BarangayClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION,
                            BarangayClearanceEntry.COLUMN_SECRETARY_NAME,
                            BarangayClearanceEntry.COLUMN_SECRETARY_SIGNATURE,
                            BarangayClearanceEntry.COLUMN_SECRETARY_SIGNATURE_DIMENSION,
                            BarangayClearanceEntry.COLUMN_TREASURER_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_1_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_2_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_3_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_4_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_5_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_6_NAME,
                            BarangayClearanceEntry.COLUMN_KAGAWAD_7_NAME,
                            BarangayClearanceEntry.TABLE_NAME,
                            BarangayClearanceEntry.COLUMN_ID
                    )
            );

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BarangayClearance brgyClearance = new BarangayClearance();

                brgyClearance.setID(id);
                brgyClearance.setResidentID(resultSet.getString(BarangayClearanceEntry.COLUMN_RESIDENT_ID));
                brgyClearance.setResidentName(resultSet.getString(BarangayClearanceEntry.COLUMN_RESIDENT_NAME));
                brgyClearance.setAddress(resultSet.getString(BarangayClearanceEntry.COLUMN_ADDRESS));
                brgyClearance.setYearOfResidency(resultSet.getInt(BarangayClearanceEntry.COLUMN_YEAR_OF_RESIDENCY));
                brgyClearance.setTotalYearsResidency(resultSet.getInt(BarangayClearanceEntry.COLUMN_TOTAL_YEARS_RESIDENCY));
                brgyClearance.setPurpose(resultSet.getString(BarangayClearanceEntry.COLUMN_PURPOSE));
                brgyClearance.setDateIssued(resultSet.getTimestamp(BarangayClearanceEntry.COLUMN_DATE_ISSUED));
                brgyClearance.setDateValid(resultSet.getTimestamp(BarangayClearanceEntry.COLUMN_DATE_VALID));
                brgyClearance.setChmName(resultSet.getString(BarangayClearanceEntry.COLUMN_CHAIRMAN_NAME));
                brgyClearance.setChmPhoto(resultSet.getString(BarangayClearanceEntry.COLUMN_CHAIRMAN_PHOTO));
                brgyClearance.setChmSignature(resultSet.getString(BarangayClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE));
                brgyClearance.setSecName(resultSet.getString(BarangayClearanceEntry.COLUMN_SECRETARY_NAME));
                brgyClearance.setSecSignature(resultSet.getString(BarangayClearanceEntry.COLUMN_SECRETARY_SIGNATURE));
                brgyClearance.setTreasurerName(resultSet.getString(BarangayClearanceEntry.COLUMN_TREASURER_NAME));
                brgyClearance.setKagawadName(0, resultSet.getString(BarangayClearanceEntry.COLUMN_KAGAWAD_1_NAME));
                brgyClearance.setKagawadName(1, resultSet.getString(BarangayClearanceEntry.COLUMN_KAGAWAD_2_NAME));
                brgyClearance.setKagawadName(2, resultSet.getString(BarangayClearanceEntry.COLUMN_KAGAWAD_3_NAME));
                brgyClearance.setKagawadName(3, resultSet.getString(BarangayClearanceEntry.COLUMN_KAGAWAD_4_NAME));
                brgyClearance.setKagawadName(4, resultSet.getString(BarangayClearanceEntry.COLUMN_KAGAWAD_5_NAME));
                brgyClearance.setKagawadName(5, resultSet.getString(BarangayClearanceEntry.COLUMN_KAGAWAD_6_NAME));
                brgyClearance.setKagawadName(6, resultSet.getString(BarangayClearanceEntry.COLUMN_KAGAWAD_7_NAME));

                String signatureDimension = resultSet.getString(BarangayClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION);
                brgyClearance.setChmSignatureDimension(signatureDimension != null ?
                        Arrays.asList(signatureDimension.split(" ")).stream().mapToDouble(Double::parseDouble).toArray() : null);

                signatureDimension = resultSet.getString(BarangayClearanceEntry.COLUMN_SECRETARY_SIGNATURE_DIMENSION);
                brgyClearance.setSecSignatureDimension(signatureDimension != null ?
                        Arrays.asList(signatureDimension.split(" ")).stream().mapToDouble(Double::parseDouble).toArray() : null);

                dbConnection.close();
                preparedStatement.close();
                resultSet.close();

                return brgyClearance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the specified business from the database.
     *
     * @param id
     *        The ID of the business to be fetched.
     *
     * @return the business having the specified ID. Return null if no match is found.
     */
    public Business getBusiness(String id) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, " +
                                    "%s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?",
                            BusinessEntry.COLUMN_ID,
                            BusinessEntry.COLUMN_BUSINESS_NAME,
                            BusinessEntry.COLUMN_BUSINESS_TYPE,
                            BusinessEntry.COLUMN_BUSINESS_ADDRESS,
                            BusinessEntry.COLUMN_OWNER_1_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_1_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_1_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_1_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_2_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_2_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_2_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_2_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_3_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_3_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_3_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_3_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_4_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_4_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_4_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_4_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_5_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_5_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_5_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_5_AUXILIARY,
                            BusinessEntry.TABLE_NAME,
                            BusinessEntry.COLUMN_ID)
            );


            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Business business = new Business();

                business.setID(resultSet.getString(BusinessEntry.COLUMN_ID));
                business.setName(resultSet.getString(BusinessEntry.COLUMN_BUSINESS_NAME));
                business.setType(resultSet.getString(BusinessEntry.COLUMN_BUSINESS_TYPE));
                business.setAddress(resultSet.getString(BusinessEntry.COLUMN_BUSINESS_ADDRESS));

                String[][] owners = new String[5][4];
                for (int i = 0; i < 5; i++)
                    for (int j = 0; j < 4; j++)
                        owners[i][j] = resultSet.getString(BusinessEntry.OWNER_NAMES[i][j]);

                business.setOwners(owners);

                dbConnection.close();
                preparedStatement.close();
                resultSet.close();

                return business;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get a specific business ID from the database.
     *
     * @param id
     *        The ID of the barangay ID to be fetched.
     *
     * @return the barangay ID having the specified ID. Return null if no match is found.
     */
    public BusinessClearance getBusinessClearance(String id) {
        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s " +
                                    "FROM %s WHERE %s = ?",
                            BusinessClearanceEntry.COLUMN_ID,
                            BusinessClearanceEntry.COLUMN_DATE_ISSUED,
                            BusinessClearanceEntry.COLUMN_DATE_VALID,
                            BusinessClearanceEntry.COLUMN_CHAIRMAN_NAME,
                            BusinessClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE,
                            BusinessClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION,
                            BusinessClearanceEntry.COLUMN_SECRETARY_NAME,
                            BusinessClearanceEntry.COLUMN_SECRETARY_SIGNATURE,
                            BusinessClearanceEntry.COLUMN_SECRETARY_SIGNATURE_DIMENSION,
                            BusinessClearanceEntry.COLUMN_BUSINESS_ID,
                            BusinessClearanceEntry.COLUMN_BUSINESS_NAME,
                            BusinessClearanceEntry.COLUMN_BUSINESS_TYPE,
                            BusinessClearanceEntry.COLUMN_BUSINESS_ADDRESS,
                            BusinessClearanceEntry.COLUMN_OWNERS,
                            BusinessClearanceEntry.COLUMN_CLIENT
                    )
            );

            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BusinessClearance businessClearance = new BusinessClearance();

                businessClearance.setID(resultSet.getString(BusinessClearanceEntry.COLUMN_ID));
                businessClearance.setDateIssued(resultSet.getTimestamp(BusinessClearanceEntry.COLUMN_DATE_ISSUED));
                businessClearance.setDateValid(resultSet.getTimestamp(BusinessClearanceEntry.COLUMN_DATE_VALID));
                businessClearance.setChmName(resultSet.getString(BusinessClearanceEntry.COLUMN_CHAIRMAN_NAME));
                businessClearance.setChmSignature(resultSet.getString(BusinessClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE));
                businessClearance.setSecName(resultSet.getString(BusinessClearanceEntry.COLUMN_SECRETARY_NAME));
                businessClearance.setSecSignature(resultSet.getString(BusinessClearanceEntry.COLUMN_SECRETARY_SIGNATURE));
                businessClearance.setBusinessID(resultSet.getString(BusinessClearanceEntry.COLUMN_BUSINESS_ID));
                businessClearance.setBusinessName(resultSet.getString(BusinessClearanceEntry.COLUMN_BUSINESS_NAME));
                businessClearance.setBusinessType(resultSet.getString(BusinessClearanceEntry.COLUMN_BUSINESS_TYPE));
                businessClearance.setBusinessAddress(resultSet.getString(BusinessClearanceEntry.COLUMN_BUSINESS_ADDRESS));
                businessClearance.setOwners(resultSet.getString(BusinessClearanceEntry.COLUMN_OWNERS));
                businessClearance.setClient(resultSet.getString(BusinessClearanceEntry.COLUMN_CLIENT));

                String signatureDimension = resultSet.getString(BarangayClearanceEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION);
                businessClearance.setChmSignatureDimension(signatureDimension != null ?
                        Arrays.asList(signatureDimension.split(" ")).stream().mapToDouble(Double::parseDouble).toArray() : null);

                signatureDimension = resultSet.getString(BarangayClearanceEntry.COLUMN_SECRETARY_SIGNATURE_DIMENSION);
                businessClearance.setSecSignatureDimension(signatureDimension != null ?
                        Arrays.asList(signatureDimension.split(" ")).stream().mapToDouble(Double::parseDouble).toArray() : null);

                dbConnection.close();
                preparedStatement.close();
                resultSet.close();

                return businessClearance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Fetch all the specified barangay ID reusable data properties. Get the signature of the
     * resident from his/her latest barangay ID. Essential for determining the path, and
     * coordinate and dimension of the resulting barangay ID which will be used when creating a
     * new ID for the specific resident.
     *
     * @param residentId
     *        The resident ID of the resident which we want to get the previous signature from
     *        his/her latest barangay ID.
     *
     * @return an object array with elements:
     *         array[0] = The signature path of the resulting barangay ID.
     *         array[1] = an array containing the coordinates and dimension of the resulting
     *                    barangay ID with elements:
     *                    array[0] = x coordinate.
     *                    array[1] = y coordinate.
     *                    array[2] = width.
     *                    array[3] = height.
     *         Return null if the resident has not applied any barangay ID yet.
     */
    public Object[] getBarangayIDProperties(String residentId) {
        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s FROM %s WHERE %s = ? ORDER BY %s DESC LIMIT 1",
                            BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE,
                            BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE_DIMENSION,
                            BarangayIdEntry.TABLE_NAME,
                            BarangayIdEntry.COLUMN_RESIDENT_ID,
                            BarangayIdEntry.COLUMN_ID
                    )
            );

            preparedStatement.setString(1, residentId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String signaturePath = resultSet.getString(BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE);
                String signatureDimension = resultSet.getString(BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE_DIMENSION);

                dbConnection.close();
                preparedStatement.close();
                resultSet.close();

                // If no signature is stored in the latest barangay ID of the resident, then
                // return null.
                if (signaturePath == null || signaturePath == "") return null;

                String[] dimensionParsed = signatureDimension.split(" ");
                double[] dimension = new double[4];

                for (int i = 0; i < 4; i++)
                    dimension[i] = Double.parseDouble(dimensionParsed[i]);

                return new Object[]{signaturePath, dimension};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Fetch all the specified barangay clearance reusable data properties. Get the
     * purpose of the resident from his/her latest barangay clearance.
     *
     * @param residentId
     *        The resident ID of the resident which we want to get the previous signature from
     *        his/her latest barangay clearance.
     *
     * @return the purpose from the resulting barangay clearance.
     */
    public String getBarangayClearanceProperties(String residentId) {
        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s FROM %s WHERE %s = ? ORDER BY %s DESC LIMIT 1",
                            BarangayClearanceEntry.COLUMN_PURPOSE,
                            BarangayClearanceEntry.TABLE_NAME,
                            BarangayClearanceEntry.COLUMN_RESIDENT_ID,
                            BarangayClearanceEntry.COLUMN_ID
                    )
            );

            preparedStatement.setString(1, residentId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String purpose = resultSet.getString(BarangayClearanceEntry.COLUMN_PURPOSE);

                dbConnection.close();
                preparedStatement.close();
                resultSet.close();

                return purpose;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Update a resident information from the database.
     *
     * @param resident
     *        The resident with the information to be updated.
     */
    public void updateResident(Resident resident) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("UPDATE %s SET " +
                                    "%s = ?, %s = ?, %s = ?, %s = ?, " +
                                    "%s = ?, %s = ?, %s = ?, " +
                                    "%s = ?, %s = ?, %s = ? " +
                                    "WHERE %s = ?",
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.COLUMN_AUXILIARY,
                            ResidentEntry.COLUMN_BIRTH_DATE,
                            ResidentEntry.COLUMN_PHOTO,
                            ResidentEntry.COLUMN_YEAR_OF_RESIDENCY,
                            ResidentEntry.COLUMN_MONTH_OF_RESIDENCY,
                            ResidentEntry.COLUMN_ADDRESS_1,
                            ResidentEntry.COLUMN_ADDRESS_2,
                            ResidentEntry.COLUMN_ID));

            statement.setString(1, resident.getFirstName());
            statement.setString(2, resident.getMiddleName());
            statement.setString(3, resident.getLastName());
            statement.setString(4, resident.getAuxiliary());
            statement.setDate(5, resident.getBirthDate());
            statement.setString(6, resident.getPhotoPath());
            statement.setInt(7, resident.getYearOfResidency());
            statement.setInt(8, resident.getMonthOfResidency());
            statement.setString(9, resident.getAddress1());
            statement.setString(10, resident.getAddress2());
            statement.setString(11, resident.getId());

            statement.executeUpdate();
            statement.close();
            dbConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update a business information from the database.
     *
     * @param business
     *        The business with the information to be updated.
     */
    public void updateBusiness(Business business) {

        try {
            Connection dbConnection = mDataSource.getConnection();

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("UPDATE %s SET " +
                                    "%s = ?, %s = ?, %s = ?, %s = ?, " +
                                    "%s = ?, %s = ?, %s = ?, %s = ?, " +
                                    "%s = ?, %s = ?, %s = ?, %s = ?, " +
                                    "%s = ?, %s = ?, %s = ?, %s = ?, " +
                                    "%s = ?, %s = ?, %s = ?, %s = ?, " +
                                    "%s = ?, %s = ?, %s = ? " +
                                    "WHERE %s = ?",
                            BusinessEntry.TABLE_NAME,
                            BusinessEntry.COLUMN_BUSINESS_NAME,
                            BusinessEntry.COLUMN_BUSINESS_TYPE,
                            BusinessEntry.COLUMN_BUSINESS_ADDRESS,
                            BusinessEntry.COLUMN_OWNER_1_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_1_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_1_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_1_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_2_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_2_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_2_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_2_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_3_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_3_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_3_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_3_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_4_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_4_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_4_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_4_AUXILIARY,
                            BusinessEntry.COLUMN_OWNER_5_FIRST_NAME,
                            BusinessEntry.COLUMN_OWNER_5_MIDDLE_NAME,
                            BusinessEntry.COLUMN_OWNER_5_LAST_NAME,
                            BusinessEntry.COLUMN_OWNER_5_AUXILIARY,
                            BusinessEntry.COLUMN_ID));

            statement.setString(1, business.getName());
            statement.setString(2, business.getType());
            statement.setString(3, business.getAddress());

            int x = 4;
            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 4; j++)
                    statement.setString(x++, business.getOwners()[i][j]);

            statement.setString(24, business.getID());

            statement.executeUpdate();
            statement.close();
            dbConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a resident record from the database.
     *
     * @param residentId
     *        The resident ID of the resident to be deleted.
     */
    public void deleteResident(String residentId) {
        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("DELETE FROM %s WHERE %s = ?",
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_ID
                    )
            );

            preparedStatement.setString(1, residentId);
            preparedStatement.executeUpdate();

            dbConnection.close();
            preparedStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the specified business record from the database.
     *
     * @param businessID
     *        The ID of the business to be deleted.
     */
    public void deleteBusiness(String businessID) {
        try {
            Connection dbConnection = mDataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("DELETE FROM %s WHERE %s = ?",
                            BusinessEntry.TABLE_NAME,
                            BusinessEntry.COLUMN_ID
                    )
            );

            preparedStatement.setString(1, businessID);
            preparedStatement.executeUpdate();

            dbConnection.close();
            preparedStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate a unique id for the given table.
     *
     * @param tableName
     *        The table name to generate a unique ID from.
     *
     * @return the uniquely generated id.
     */
    public String generateID(String tableName) {
        try {
            Connection dbConnection = mDataSource.getConnection();

            // Generate a unique resident id.
            String residentId = "";
            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("SELECT %s FROM %s WHERE %s LIKE '%s%%' ORDER BY %s DESC LIMIT 1",
                            ResidentEntry.COLUMN_ID,
                            tableName,
                            ResidentEntry.COLUMN_ID,
                            Calendar.getInstance().get(Calendar.YEAR) - 2000,
                            ResidentEntry.COLUMN_ID));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String code = resultSet.getString(ResidentEntry.COLUMN_ID);
                int year = Integer.parseInt(code.substring(0, 2));
                int codeNo = Integer.parseInt(code.substring(3, 6)) + 1;

                residentId = year + "-" + (codeNo < 10 ? "00" + codeNo : codeNo < 100 ? "0" + codeNo : codeNo);
            } else {
                int year = Calendar.getInstance().get(Calendar.YEAR) - 2000;
                residentId = year + "-001";
            }

            statement.close();
            resultSet.close();
            dbConnection.close();

            return residentId;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
