package org.hilel14.archie.beeri.core.reports;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author hilel14
 */
public class ImportFileRecord {

    private String id;
    private long importFolderId;
    private Timestamp importTime;
    private String fileName;
    private int statusCode;
    private String warningMessage;

    public String getImportTimeStampFormatted() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(importTime);
    }

    public String getImportTimeFormatted() {
        return new SimpleDateFormat("HH:mm:ss").format(importTime);
    }

    public String getImportDateFormatted() {
        return new SimpleDateFormat("dd 'ב'MMMM yyyy", Locale.forLanguageTag("he")).format(importTime);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the importFolderId
     */
    public long getImportFolderId() {
        return importFolderId;
    }

    /**
     * @param importFolderId the importFolderId to set
     */
    public void setImportFolderId(long importFolderId) {
        this.importFolderId = importFolderId;
    }

    /**
     * @return the importTime
     */
    public Timestamp getImportTime() {
        return importTime;
    }

    /**
     * @param importTime the importTime to set
     */
    public void setImportTime(Timestamp importTime) {
        this.importTime = importTime;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the warningMessage
     */
    public String getWarningMessage() {
        return warningMessage;
    }

    /**
     * @param warningMessage the warningMessage to set
     */
    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

}
