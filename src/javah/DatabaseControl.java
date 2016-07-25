package javah;

import javah.DatabaseContract.*;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseControl {
    private static MysqlDataSource dataSource;

    static {
        dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost/BarangayDB");
        dataSource.setUser("root");
        dataSource.setPassword("horizon");
    }

    /**
     * Return the non-archived residents IDs and Names.
     * @return an array of lists, where List[0] contains the resident IDs, while List[1] contains the concatenated resident names.
     */
    public List[] getResidentsIdAndName() {

        List[] returnList = new List[2];
        List<String> residentsIdList = new ArrayList<>();
        List<String> residentNameList = new ArrayList<>();

        try {
            Connection dbConnection = dataSource.getConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(
                    String.format("SELECT ?, ?, ?, ? FROM %s WHERE ? = 0 ORDER BY ?", ResidentEntry.TABLE_NAME));

            preparedStatement.setString(1, ResidentEntry.COLUMN_RESIDENT_ID);
            preparedStatement.setString(2, ResidentEntry.COLUMN_FIRST_NAME);
            preparedStatement.setString(3, ResidentEntry.COLUMN_MIDDLE_NAME);
            preparedStatement.setString(4, ResidentEntry.COLUMN_LAST_NAME);
            preparedStatement.setString(5, ResidentEntry.COLUMN_IS_ARCHIVED);
            preparedStatement.setString(6, ResidentEntry.COLUMN_LAST_NAME);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                residentsIdList.add(resultSet.getString(ResidentEntry.COLUMN_RESIDENT_ID));

                residentNameList.add(String.format("%s, %s %s.",
                        resultSet.getString(ResidentEntry.COLUMN_LAST_NAME),
                        resultSet.getString(ResidentEntry.COLUMN_FIRST_NAME),
                        resultSet.getString(ResidentEntry.COLUMN_MIDDLE_NAME).charAt(0)));
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

}
