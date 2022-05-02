package org.northwinds.app.sotachaser;

import com.univocity.parsers.annotations.Parsed;

public class SummitRecord {
    @Parsed private String summitCode;
    @Parsed private String associationName;
    @Parsed private String regionName;
    @Parsed private String summitName;
    @Parsed private int altM;
    @Parsed private int altFt;
    @Parsed private String gridRef1;  // Various formats
    @Parsed private String gridRef2;  // Various formats
    @Parsed private double longitude;
    @Parsed private double latitude;
    @Parsed private int points;
    @Parsed private int bonusPoints;
    @Parsed private String validFrom;  // Date in DD/MM/YYYY format
    @Parsed private String validTo;  // Date in DD/MM/YYYY format
    @Parsed private int activationCount;
    @Parsed private String activationDate;  // Date in DD/MM/YYYY format
    @Parsed private String activationCall;

    public String getSummitCode() {
        return summitCode;
    }

    public String getAssociationName() {
        return associationName;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getSummitName() {
        return summitName;
    }

    public int getAltM() {
        return altM;
    }

    public int getAltFt() {
        return altFt;
    }

    public String getGridRef1() {
        return gridRef1;
    }

    public String getGridRef2() {
        return gridRef2;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getPoints() {
        return points;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public int getActivationCount() {
        return activationCount;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public String getActivationCall() {
        return activationCall;
    }

    public void setSummitName(String summitName) {
        this.summitName = summitName;
    }

    public SummitRecord() {}

    @Override
    public String toString() {
        return "Summit{summitCode=" + summitCode +
                ", associationName=" + associationName +
                ", regionName=" + regionName +
                ", summitName=" + summitName + "}";
    }
}
