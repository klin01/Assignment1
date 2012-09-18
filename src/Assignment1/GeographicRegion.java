package Assignment1;

/**
 * GeographicRegion.java
 * KL2495
 * A helper class to represent a capital city for use in the VacationSearch class. Each GeographicRegion stores the name of the capital,
 * the name of the country, and the geographic coordinates of the capital.
 * There is also a helper method to determine if another geographic region is within a specified degree range. The algorithm for determining
 * acceptable range is rectangular, in that if a square with side of length of the range can fit over both GeographicRegions, then the two 
 * are considered within range.
 */

public class GeographicRegion {

	private String _capital;
	private String _region;
	private String _lat;
	private String _long;
	
	public GeographicRegion(String capital, String region, String longitude, String latitude)
	{
		_capital = capital;
		_region = region;
		_long = longitude;
		_lat = latitude;
	}
	
	/**
	 * A method to determine whether or not two GeographicRegions are within a specified range of eachother.
	 * @param range : maximum range for acceptance
	 * @param geo : GeographicRegion to compare to
	 * @return : boolean of whether or not the two regions are in range
	 */
	public boolean WithinRange(int range, GeographicRegion geo)
	{
		boolean output = true;
		
		String[] longParts = _long.split(" ");
		String[] latParts = _lat.split(" ");
		String[] longParts_2 = geo.getLong().split(" ");
		String[] latParts_2 = geo.getLat().split(" ");
		double a, b;
		
		if (!longParts[2].equals(longParts_2[2]))	//account for N/S discrepancies
		{
			a = Double.parseDouble(longParts[0]) + Double.parseDouble(longParts_2[0]);
		}
		else
			a = Math.abs(Double.parseDouble(longParts[0]) - Double.parseDouble(longParts_2[0]));
		
		if (!latParts[2].equals(latParts_2[2]))	//account for E/W discrepancies
		{
			b = Double.parseDouble(latParts[0]) + Double.parseDouble(latParts_2[0]);
		}
		else
			b = Math.abs(Double.parseDouble(latParts[0]) - Double.parseDouble(latParts_2[0]));
		
		//measure proximity by square formed by "range" variable
		if (a > range || b > range)
			output = false;
		
		return output;
	}
	
	
	//accessor methods
	public String getLat()
	{
		return _lat;
	}
	public String getLong()
	{
		return _long;
	}
	
	public String getName()
	{
		return _capital + " (" + _region + ")";
	}	
	
}
