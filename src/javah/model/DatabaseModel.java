package javah.model;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javah.container.Resident;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javah.util.DatabaseContract.*;


public class DatabaseModel {
    private static MysqlDataSource dataSource;

    /**
     * Bug: Apparently, using parametarized query with a data source connection to allow connection pooling converts the
     * expected results to their corresponding column names. (e.g. 131-005 == resident_id).
     */
    static {
        dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost/BarangayDB");
        dataSource.setUser("root");
        dataSource.setPassword("horizon");
    }

    /**
     * Return the non-archived residents IDs and Names.
     * @return an array of lists, where List[0] contains the resident IDs, while List[1] contains the concatenated
     * resident names.
     */
    public List[] getResidentsIdAndName() {

        List[] returnList = new List[2];
        List<String> residentsIdList = new ArrayList<>();
        List<String> residentNameList = new ArrayList<>();

        try {
            Connection dbConnection = dataSource.getConnection();

            // Use String.format as a workaround to the bug when using parameterized query.
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = 0 ORDER BY %s",
                            ResidentEntry.COLUMN_RESIDENT_ID,
                            ResidentEntry.COLUMN_FIRST_NAME,
                            ResidentEntry.COLUMN_MIDDLE_NAME,
                            ResidentEntry.COLUMN_LAST_NAME,
                            ResidentEntry.TABLE_NAME,
                            ResidentEntry.COLUMN_IS_ARCHIVED,
                            ResidentEntry.COLUMN_LAST_NAME)
                    );

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                residentsIdList.add(resultSet.getString(ResidentEntry.COLUMN_RESIDENT_ID));

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
                            ResidentEntry.COLUMN_RESIDENT_ID
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

                return resident;
            }

            dbConnection.close();
            preparedStatement.close();
            resultSet.close();
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
                            ResidentEntry.COLUMN_RESIDENT_ID
                    )
            );

            preparedStatement.setString(1, residentId);
            preparedStatement.executeUpdate();

            dbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
