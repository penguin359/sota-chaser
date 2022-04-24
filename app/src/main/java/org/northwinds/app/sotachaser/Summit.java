package org.northwinds.app.sotachaser;

import com.opencsv.bean.CsvBindByName;

public class Summit {
	@CsvBindByName private String summitCode;
	@CsvBindByName private String associationName;
	@CsvBindByName private String regionName;
	@CsvBindByName private String summitName;
	@CsvBindByName private int altM;
	@CsvBindByName private int altFt;
	@CsvBindByName private String gridRef1;  // Various formats
	@CsvBindByName private String gridRef2;  // Various formats
	@CsvBindByName private double longitude;
	@CsvBindByName private double latitude;
	@CsvBindByName private int points;
	@CsvBindByName private int bonusPoints;
	@CsvBindByName private String validFrom;  // Date in DD/MM/YYYY format
	@CsvBindByName private String validTo;  // Date in DD/MM/YYYY format
	@CsvBindByName private int activationCount;
	@CsvBindByName private String activationDate;  // Date in DD/MM/YYYY format
	@CsvBindByName private String activationCall;

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

	public Summit() {}

	@Override
	public String toString() {
		return "Summit{summitCode=" + summitCode +
				", associationName=" + associationName +
				", regionName=" + regionName +
				", summitName=" + summitName + "}";
	}
}
