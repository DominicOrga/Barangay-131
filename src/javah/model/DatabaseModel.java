package javah.model;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javah.container.BarangayClearance;
import javah.container.BarangayID;
import javah.container.Resident;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import javah.contract.DatabaseContract.*;


public class DatabaseModel {
    private MysqlDataSource dataSource;

    /**
     * Determines what type of data are to be stored.
     */
    private byte BINARY_PHOTO = 0,
                 BINARY_SIGNATURE = 1;

    /**
     * The path pointing to the data directories of the application.
     */
    private String mPhotoDirectoryPath, mSignatureDirectoryPath;

    /**
     * Bug: Apparently, using parametarized query with a data source connection to allow connection pooling converts the
     * expected results to their corresponding column names. (e.g. 131-005 == resident_id).
     */
    public DatabaseModel() {
        // Initialize the data source.
        dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost/BarangayDB");
        dataSource.setUser("root");
        dataSource.setPassword("horizon");

        // Make path towards the root folder 'Barangay131' at C:\Users\Public and its sub-folders - 'Photos' and 'Signature'.
        String dataDirectoryPath = System.getenv("PUBLIC") + "/Barangay131";

        mPhotoDirectoryPath = dataDirectoryPath + "/Photos";
        mSignatureDirectoryPath = dataDirectoryPath + "/Signatures";

        // Create the directories if not yet created.
        File photoDirectory = new File(mPhotoDirectoryPath);
        if(!photoDirectory.exists())
            photoDirectory.mkdir();

        File signatureDirectory = new File(mSignatureDirectoryPath);
        if(!signatureDirectory.exists())
            signatureDirectory.mkdir();

    }

    /**
     * Return the non-archived residents IDs and Names.
     * @return an array of lists, where List[0] contains the resident IDs, while List[1] contains the concatenated
     * resident names.
     */
    public List[] getResidentEssentials() {

        List[] returnList = new List[2];
        List<String> residentsIdList = new ArrayList<>();
        List<String> residentNameList = new ArrayList<>();

        try {
            Connection dbConnection = dataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = 0 ORDER BY %s, %s, %s",
                            ResidentEntry.COLUMN_ID,
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_IS_ARCHIVED,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME)
                    );

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                residentsIdList.add(resultSet.getString(ResidentEntry.COLUMN_ID));

                residentNameList.add(String.format("%s, %s %s.",
                        resultSet.getString(ResidentEntry.COLUMN_LAST_NAME),
                        resultSet.getString(ResidentEntry.COLUMN_FIRST_NAME),
                        resultSet.getString(ResidentEntry.COLUMN_MIDDLE_NAME).toUpperCase().charAt(0)));
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

    public Resident getResident(String residentId) {

        try {
            Connection dbConnection = dataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?",
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
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

    public void archiveResident(String residentId) {
        try {
            Connection dbConnection = dataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("UPDATE %s SET %s = 1 WHERE %s = ?",
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_IS_ARCHIVED,
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

    public String createResident(Resident resident) {

        try {
            Connection dbConnection = dataSource.getConnection();

            String residentID = generateID(ResidentEntry.TABLE_NAME);

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, default)",
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_ID,
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.COLUMN_BIRTH_DATE,
                            ResidentEntry.COLUMN_PHOTO,
                            ResidentEntry.COLUMN_YEAR_OF_RESIDENCY,
                            ResidentEntry.COLUMN_MONTH_OF_RESIDENCY,
                            ResidentEntry.COLUMN_ADDRESS_1,
                            ResidentEntry.COLUMN_ADDRESS_2,
                            ResidentEntry.COLUMN_IS_ARCHIVED));

            statement.setString(1, residentID);
            statement.setString(2, resident.getFirstName());
            statement.setString(3, resident.getMiddleName());
            statement.setString(4, resident.getLastName());
            statement.setDate(5, resident.getBirthDate());
            statement.setString(6, resident.getPhotoPath());
            statement.setInt(7, resident.getYearOfResidency());
            statement.setInt(8, resident.getMonthOfResidency());
            statement.setString(9, resident.getAddress1());
            statement.setString(10, resident.getAddress2());

            statement.execute();

            statement.close();
            dbConnection.close();

            return residentID;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateResident(Resident resident) {

        try {
            Connection dbConnection = dataSource.getConnection();

            PreparedStatement statement = dbConnection.prepareStatement(
                    String.format("UPDATE %s SET " +
                            "%s = ?, %s = ?, %s = ?, " +
                            "%s = ?, %s = ?, %s = ?, " +
                            "%s = ?, %s = ?, %s = ? " +
                            "WHERE %s = ?",
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_FIRST_NAME, ResidentEntry.COLUMN_MIDDLE_NAME, ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.COLUMN_BIRTH_DATE, ResidentEntry.COLUMN_PHOTO, ResidentEntry.COLUMN_YEAR_OF_RESIDENCY,
                            ResidentEntry.COLUMN_MONTH_OF_RESIDENCY, ResidentEntry.COLUMN_ADDRESS_1, ResidentEntry.COLUMN_ADDRESS_2,
                            ResidentEntry.COLUMN_ID));

            statement.setString(1, resident.getFirstName());
            statement.setString(2, resident.getMiddleName());
            statement.setString(3, resident.getLastName());
            statement.setDate(4, resident.getBirthDate());
            statement.setString(5, resident.getPhotoPath());
            statement.setInt(6, resident.getYearOfResidency());
            statement.setInt(7, resident.getMonthOfResidency());
            statement.setString(8, resident.getAddress1());
            statement.setString(9, resident.getAddress2());
            statement.setString(10, resident.getId());

            statement.executeUpdate();

            statement.close();
            dbConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the Barangay ID's Ids, resident ids, and their issued date.
     * @return an array of lists, where List[0] contains the Barangay ID's IDs, List[1] contains the resident IDs
     * of each Barangay ID, while List[2] is the issued date of each barangay ID.
     */
    public List[] getBarangayIDEssentials() {
        List[] result = new List[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Date>()};

        try {
            Connection dbConnection = dataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            // Only query the barangay ID data with applicants that are not archived.

            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s.%s, %s.%s, %s.%s FROM %s JOIN %s ON %s.%s = %s.%s WHERE %s.%s=0 ORDER BY %s DESC",
                            BarangayIdEntry.TABLE_NAME, BarangayIdEntry.COLUMN_ID,
                            BarangayIdEntry.TABLE_NAME, BarangayIdEntry.COLUMN_RESIDENT_ID,
                            BarangayIdEntry.TABLE_NAME, BarangayIdEntry.COLUMN_DATE_ISSUED,
                            BarangayIdEntry.TABLE_NAME,
                            ResidentEntry.TABLE_NAME,
                            BarangayIdEntry.TABLE_NAME, BarangayIdEntry.COLUMN_RESIDENT_ID,
                            ResidentEntry.TABLE_NAME, ResidentEntry.COLUMN_ID,
                            ResidentEntry.TABLE_NAME, ResidentEntry.COLUMN_IS_ARCHIVED,
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

    public String createBarangayID(BarangayID barangayID) {

        try {
            Connection dbConnection = dataSource.getConnection();

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

            statement.setDate(11, barangayID.getDateIssued());
            statement.setDate(12, barangayID.getDateValid());

            statement.execute();

            statement.close();
            dbConnection.close();

            return barangayID.getID();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public BarangayID getBarangayID(String id) {
        try {
            Connection dbConnection = dataSource.getConnection();

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
                barangayID.setChmSignature(resultSet.getString(BarangayIdEntry.COLUMN_RESIDENT_SIGNATURE));

                signatureDimension = resultSet.getString(BarangayIdEntry.COLUMN_CHAIRMAN_SIGNATURE_DIMENSION);
                barangayID.setChmSignatureDimension(signatureDimension != null ?
                        Arrays.asList(signatureDimension.split(" ")).stream().mapToDouble(Double::parseDouble).toArray() :
                        null);

                barangayID.setDateIssued(resultSet.getDate(BarangayIdEntry.COLUMN_DATE_ISSUED));
                barangayID.setDateValid(resultSet.getDate(BarangayIdEntry.COLUMN_DATE_VALID));

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
     * Get the signature of the resident from his/her latest barangay ID.
     * @param residentId
     * @return null if no barangay ID exists for the resident.
     *         Else, return the Object[2] index: [0] = String 'signature path' and [1] = Double[4] 'signature dimension'.
     *         Where Double[4] index: [0] = x, [1] = y, [2] = width, [3] height;
     */
    public Object[] getResidentSignatureFromBarangayID(String residentId) {
        try {
            Connection dbConnection = dataSource.getConnection();

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

                // If no signature is stores in the latest barangay ID of the resident, then return null.
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
     * Returns the Barangay Clearance's Ids, resident ids, and their issued date.
     * @return an array of lists, where List[0] contains the Barangay clearance's IDs, List[1] contains the resident IDs
     * of each Barangay clearance, while List[2] is the issued date of each barangay clearance.
     */
    public List[] getBarangayClearanceEssentials() {
        List[] result = new List[]{new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Date>()};

        try {
            Connection dbConnection = dataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            // Only query the barangay ID data with applicants that are not archived.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s.%s, %s.%s, %s.%s FROM %s JOIN %s ON %s.%s = %s.%s WHERE %s.%s=0 ORDER BY %s DESC",
                            BarangayClearanceEntry.TABLE_NAME, BarangayClearanceEntry.COLUMN_ID,
                            BarangayClearanceEntry.TABLE_NAME, BarangayClearanceEntry.COLUMN_RESIDENT_ID,
                            BarangayClearanceEntry.TABLE_NAME, BarangayClearanceEntry.COLUMN_DATE_ISSUED,
                            BarangayClearanceEntry.TABLE_NAME,
                            ResidentEntry.TABLE_NAME,
                            BarangayClearanceEntry.TABLE_NAME, BarangayClearanceEntry.COLUMN_RESIDENT_ID,
                            ResidentEntry.TABLE_NAME, ResidentEntry.COLUMN_ID,
                            ResidentEntry.TABLE_NAME, ResidentEntry.COLUMN_IS_ARCHIVED,
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

    public String createBarangayClearance(BarangayClearance barangayClearance) {

        try {
            Connection dbConnection = dataSource.getConnection();

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
            statement.setDate(8, barangayClearance.getDateIssued());
            statement.setDate(9, barangayClearance.getDateValid());
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

    public BarangayClearance getBarangayClearance(String id) {
        try {
            Connection dbConnection = dataSource.getConnection();

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
                brgyClearance.setDateIssued(resultSet.getDate(BarangayClearanceEntry.COLUMN_DATE_ISSUED));
                brgyClearance.setDateValid(resultSet.getDate(BarangayClearanceEntry.COLUMN_DATE_VALID));
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
     * Generate a unique id for the given table.
     * @param tableName
     * @return the uniquely generated id.
     */
    public String generateID(String tableName) {
        try {
            Connection dbConnection = dataSource.getConnection();

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
