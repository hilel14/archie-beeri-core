package org.hilel14.archie.beeri.core.reports;

import org.hilel14.archie.beeri.core.reports.ImportFileRecord;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class ImportFolderRecord {

    static final Logger LOGGER = LoggerFactory.getLogger(ImportFolderRecord.class);

    private long id;
    private Date importTime;
    private String folderName;
    private int fileCount;
    List<ImportFileRecord> files;

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
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the importTime
     */
    public Date getImportTime() {
        return importTime;
    }

    /**
     * @param importTime the importTime to set
     */
    public void setImportTime(Date importTime) {
        this.importTime = importTime;
    }

    /**
     * @return the folderName
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * @param folderName the folderName to set
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * @return the fileCount
     */
    public int getFileCount() {
        return fileCount;
    }

    /**
     * @param fileCount the fileCount to set
     */
    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    /**
     * @return the files
     */
    public List<ImportFileRecord> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<ImportFileRecord> files) {
        this.files = files;
    }
}
