package org.hilel14.archie.beeri.core.jobs.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Set;

/**
 * Related to ImportAttributes in archie-beeri-ws project
 *
 * @author hilel14
 */
public class ImportFolderForm {

    // global attributes
    private String folderName;
    private String textAction; // recognize (ocr), extrat (pdf), skip
    private String addFileNamesTo; // dcTitle, dcDescription, sortCode
    // solr document fields
    private String dcTitle;
    private String dcDate;
    private Set<String> dcCreators;
    private String dcDescription;
    private Set<String> dcSubjects;
    private String dcType;
    private String dcIsPartOf;
    private String storageLocation2;
    private String dcAccessRights;
    // database fields
    private long importFolderId;

    public static ImportFolderForm unmarshal(String attributes)
            throws IOException {
        return new ObjectMapper().readValue(attributes, ImportFolderForm.class);
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
     * @return the addFileNamesTo
     */
    public String getAddFileNamesTo() {
        return addFileNamesTo;
    }

    /**
     * @param addFileNamesTo the addFileNamesTo to set
     */
    public void setAddFileNamesTo(String addFileNamesTo) {
        this.addFileNamesTo = addFileNamesTo;
    }

    /**
     * @return the dcTitle
     */
    public String getDcTitle() {
        return dcTitle;
    }

    /**
     * @param dcTitle the dcTitle to set
     */
    public void setDcTitle(String dcTitle) {
        this.dcTitle = dcTitle;
    }

    /**
     * @return the dcDate
     */
    public String getDcDate() {
        return dcDate;
    }

    /**
     * @param dcDate the dcDate to set
     */
    public void setDcDate(String dcDate) {
        this.dcDate = dcDate;
    }

    /**
     * @return the dcCreators
     */
    public Set<String> getDcCreators() {
        return dcCreators;
    }

    /**
     * @param dcCreators the dcCreators to set
     */
    public void setDcCreators(Set<String> dcCreators) {
        this.dcCreators = dcCreators;
    }

    /**
     * @return the dcDescription
     */
    public String getDcDescription() {
        return dcDescription;
    }

    /**
     * @param dcDescription the dcDescription to set
     */
    public void setDcDescription(String dcDescription) {
        this.dcDescription = dcDescription;
    }

    /**
     * @return the dcType
     */
    public String getDcType() {
        return dcType;
    }

    /**
     * @param dcType the dcType to set
     */
    public void setDcType(String dcType) {
        this.dcType = dcType;
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
     * @return the dcIsPartOf
     */
    public String getDcIsPartOf() {
        return dcIsPartOf;
    }

    /**
     * @param dcIsPartOf the dcIsPartOf to set
     */
    public void setDcIsPartOf(String dcIsPartOf) {
        this.dcIsPartOf = dcIsPartOf;
    }

    /**
     * @return the dcSubjects
     */
    public Set<String> getDcSubjects() {
        return dcSubjects;
    }

    /**
     * @param dcSubjects the dcSubjects to set
     */
    public void setDcSubjects(Set<String> dcSubjects) {
        this.dcSubjects = dcSubjects;
    }

    /**
     * @return the textAction
     */
    public String getTextAction() {
        return textAction;
    }

    /**
     * @param textAction the textAction to set
     */
    public void setTextAction(String textAction) {
        this.textAction = textAction;
    }

    /**
     * @return the storageLocation2
     */
    public String getstorageLocation2() {
        return storageLocation2;
    }

    /**
     * @param storageLocation2 the storageLocation2 to set
     */
    public void setstorageLocation2(String storageLocation2) {
        this.storageLocation2 = storageLocation2;
    }

    /**
     * @return the dcAccessRights
     */
    public String getDcAccessRights() {
        return dcAccessRights;
    }

    /**
     * @param dcAccessRights the dcAccessRights to set
     */
    public void setDcAccessRights(String dcAccessRights) {
        this.dcAccessRights = dcAccessRights;
    }

}
