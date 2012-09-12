package Assignment1;

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
		if (a > 180)
			a = 360 - a;
		
		if (!latParts[2].equals(latParts_2[2]))	//account for E/W discrepancies
		{
			b = Double.parseDouble(latParts[0]) + Double.parseDouble(latParts_2[0]);
		}
		else
			b = Math.abs(Double.parseDouble(latParts[0]) - Double.parseDouble(latParts_2[0]));
		if (b > 180)
			b = 360 - b;
		
		//Euclidean geometry to find distance
		double a_2 = Math.pow(a, 2);
		double b_2 = Math.pow(b, 2);
		double dist = Math.sqrt(a_2 + b_2);
		
		if (dist > range) {
			output = false;
			System.out.println(_capital + "|" + geo.getName());
		}
		
		return output;
	}
	
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
