package org.hilel14.archie.beeri.core.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.sql.DataSource;


/**
 *
 * @author hilel14
 */
public class ReportGenerator {

    static final Logger LOGGER = LoggerFactory.getLogger(ReportGenerator.class);
    private final DataSource dataSource;
    private final String getImportFolders = "SELECT * FROM import_folder_log ORDER BY import_time DESC";
    private final String getImportFolder = "SELECT * FROM import_folder_log WHERE id = ?";
    private final String getImportFiles = "SELECT * FROM import_file_log WHERE import_folder_id = ?";

    public ReportGenerator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ImportFolderRecord> getImportFolders() throws SQLException {
        List<ImportFolderRecord> importFolders = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();) {
            PreparedStatement statement = connection.prepareStatement(getImportFolders);
            try (ResultSet rs = statement.executeQuery();) {
                while (rs.next()) {
                    ImportFolderRecord record = new ImportFolderRecord();
                    record.setId(rs.getLong("id"));
                    record.setImportTime(rs.getTimestamp("import_time"));
                    record.setFolderName(rs.getString("folder_name"));
                    record.setFileCount(rs.getInt("files_count"));
                    importFolders.add(record);
                }
            }
        }
        LOGGER.debug("{} import folder records found", importFolders.size());
        return importFolders;
    }

    public ImportFolderRecord getImportFolder(long importFolderId) throws SQLException {
        ImportFolderRecord record = new ImportFolderRecord();
        try (Connection connection = dataSource.getConnection();) {
            PreparedStatement statement = connection.prepareStatement(getImportFolder);
            statement.setLong(1, importFolderId);
            try (ResultSet rs = statement.executeQuery();) {
                if (rs.next()) {
                    record.setId(rs.getLong("id"));
                    record.setImportTime(rs.getTimestamp("import_time"));
                    record.setFolderName(rs.getString("folder_name"));
                    record.setFileCount(rs.getInt("files_count"));
                }
            }
        }
        List<ImportFileRecord> importFiles = getFilesByFolder(importFolderId);
        record.setFiles(importFiles);
        return record;
    }

    public List<ImportFileRecord> getFilesByFolder(long importFolderId) throws SQLException {
        List<ImportFileRecord> importFiles = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();) {
            PreparedStatement statement = connection.prepareStatement(getImportFiles);
            statement.setLong(1, importFolderId);
            try (ResultSet rs = statement.executeQuery();) {
                while (rs.next()) {
                    ImportFileRecord record = new ImportFileRecord();
                    record.setId(rs.getString("id"));
                    record.setImportFolderId(rs.getLong("import_folder_id"));
                    record.setImportTime(rs.getTimestamp("import_time"));
                    record.setFileName(rs.getString("file_name"));
                    record.setStatusCode(rs.getInt("status_code"));
                    record.setWarningMessage(rs.getString("warning_message"));
                    importFiles.add(record);
                }
            }
        }
        return importFiles;
    }

}
