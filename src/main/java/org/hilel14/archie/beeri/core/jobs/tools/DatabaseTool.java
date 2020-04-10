package org.hilel14.archie.beeri.core.jobs.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;
import org.hilel14.archie.beeri.core.jobs.model.ImportFolderForm;

/**
 *
 * @author hilel14
 */
public class DatabaseTool {

    static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTool.class);
    private final Config config;

    public DatabaseTool(Config config) {
        this.config = config;
    }

    public void createImportFolderRecord(ImportFolderForm form, int filesCount) throws SQLException {
        LOGGER.debug("prepare to insert folder {} with {} files",
                form.getFolderName(), filesCount);
        String sql = "INSERT INTO import_folder_log (import_time, folder_name, files_count) " + "VALUES (?, ?, ?)";
        try (Connection connection = config.getDataSource().getConnection();) {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
            statement.setString(2, form.getFolderName());
            statement.setInt(3, filesCount);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                long key = rs.getLong(1);
                LOGGER.debug("key {} generated for folder {}", key, form.getFolderName());
                form.setImportFolderId(key);
            }
        }
    }

    public void createImportFileRecord(ImportFileTicket ticket) throws SQLException {
        String sql = "INSERT INTO import_file_log (id, import_folder_id, import_time, file_name) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection connection = config.getDataSource().getConnection();) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, ticket.getUuid());
            statement.setLong(2, ticket.getImportFolderForm().getImportFolderId());
            statement.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
            statement.setString(4, ticket.getFileName());
            statement.executeUpdate();
        }
    }

    public void updateImportFileRecord(ImportFileTicket ticket) throws SQLException {
        String sql = "UPDATE import_file_log " + "SET status_code = ?, warning_message = ? " + "WHERE id = ?";
        try (Connection connection = config.getDataSource().getConnection();) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, ticket.getImportStatusCode());
            statement.setString(2, ticket.getImportStatusText());
            statement.setString(3, ticket.getUuid());
            statement.executeUpdate();
        }
    }
}
