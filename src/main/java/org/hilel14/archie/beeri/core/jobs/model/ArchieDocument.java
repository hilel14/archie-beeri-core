package org.hilel14.archie.beeri.core.jobs.model;

import java.util.Set;

/**
 *
 * @author hilel14
 */
public class ArchieDocument {

    private String id;
    // dublin-core fields
    private String dcTitle;
    private String dcDate;
    private Set<String> dcCreator;
    private String dcDescription;
    private String dcType;
    private String dcFormat;
    private Set<String> dcSubject;
    private String storageLocation;
    private String dcIsPartOf;
    private String dcAccessRights;
    // other fields
    private long sortCode;

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
     * @return the dcCreator
     */
    public Set<String> getDcCreator() {
        return dcCreator;
    }

    /**
     * @param dcCreator the dcCreator to set
     */
    public void setDcCreator(Set<String> dcCreator) {
        this.dcCreator = dcCreator;
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
     * @return the dcFormat
     */
    public String getDcFormat() {
        return dcFormat;
    }

    /**
     * @param dcFormat the dcFormat to set
     */
    public void setDcFormat(String dcFormat) {
        this.dcFormat = dcFormat;
    }

    /**
     * @return the dcSubject
     */
    public Set<String> getDcSubject() {
        return dcSubject;
    }

    /**
     * @param dcSubject the dcSubject to set
     */
    public void setDcSubject(Set<String> dcSubject) {
        this.dcSubject = dcSubject;
    }

    /**
     * @return the storageLocation
     */
    public String getStorageLocation() {
        return storageLocation;
    }

    /**
     * @param storageLocation the storageLocation to set
     */
    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
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
     * @return the sortCode
     */
    public long getSortCode() {
        return sortCode;
    }

    /**
     * @param sortCode the sortCode to set
     */
    public void setSortCode(long sortCode) {
        this.sortCode = sortCode;
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
