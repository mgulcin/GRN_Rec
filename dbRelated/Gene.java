package dbRelated;
import java.util.HashMap;


public class Gene {
	String stdName;
	String sysName;
	String description;
	HashMap<String, Double> timeSeriesMap;
	
	public Gene(String stdName, String sysName) {
		super();
		this.stdName = stdName;
		this.sysName = sysName;
	}
	
	public String getStdName() {
		return stdName;
	}

	public void setStdName(String stdName) {
		this.stdName = stdName;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HashMap<String, Double> getTimeSeriesMap() {
		return timeSeriesMap;
	}

	public void setTimeSeriesMap(HashMap<String, Double> timeSeriesMap) {
		this.timeSeriesMap = timeSeriesMap;
	}
	
	public void insertIntoTimeSeriesMap(String timeSerieName, Double value) {
		timeSeriesMap.put(timeSerieName, value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stdName == null) ? 0 : stdName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gene other = (Gene) obj;
		if (stdName == null) {
			if (other.stdName != null)
				return false;
		} else if (!stdName.equals(other.stdName))
			return false;
		return true;
	}
	
	
	
	
}
