package org.hilel14.archie.beeri.core.guestbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel
 */
public class DbConnector {

    static final Logger LOGGER = LoggerFactory.getLogger(DbConnector.class);
    private final DataSource dataSource;
    private final String insertRecord = "INSERT INTO guest_book (doc_id,contact,remarks,creation_time) VALUES (?, ?, ?,?)";

    public DbConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertRecord(Map<String, String> record) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            PreparedStatement statement = connection.prepareStatement(insertRecord);
            statement.setString(1, record.get("id"));
            statement.setString(2, record.get("contact"));
            statement.setString(3, record.get("remarks"));
            statement.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
            statement.executeUpdate();
        }
    }

}
