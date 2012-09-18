package Assignment1;

/**
 * VacationSearch.java
 * KL2495
 * An implementation of the IFactSearch interface to search for the most capitals of countries that can be visited within a certain degree range of each other.
 * This is done by first compiling a list of all capital cities and their geographic coordinates. I've written a GeographicRegion class to store each capital city
 * as a unit of data, along with it's host country and geographic coordinates. Then I algorithmically assemble all possible sets of capital cities and check each assembled
 * set for the satisfaction of the condition "all capitals must be within X degrees of each other". Acceptance for this condition is based on a rectangular measurement, i.e.
 * a square of side length of the max distance must be able to cover both capitals in question.
 */

import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;

public class VacationSearch implements IFactSearch {

	private int _maxDistance; //measurement of max distance in degrees between any two capitals
	private List<GeographicRegion> capitals; //list of all capitals fetched from the CIA factbook
	private List<List<GeographicRegion>> vacationPath;
	/**
	 * Constructor for VacationSearch implementation. Takes a parameter of max distance between capitals along vacation path.
	 * @param maxDistance
	 * @throws Exception
	 */
	public VacationSearch(int maxDistance) throws Exception
	{
		_maxDistance = maxDistance;
		
		FetchCapitalsFromFactbook();
	}
	
	/**
	 * Method to call for generating all subsets of capitals and finding the largest set that satisfies the vacationing condition.
	 * @return : List of names of all capitals in the subset that satisfies the vacationing condition.
	 */
	public List<String> Search() throws Exception 
	{
		List<String> output = new ArrayList<String>();
		long subsetGenInitTime = System.currentTimeMillis();
		List<List<GeographicRegion>> subsets = generateSubsets(capitals);
		System.out.println("Time to generate subsets: " + (System.currentTimeMillis() - subsetGenInitTime)/1000.00);
		List<GeographicRegion> mostCaps = new ArrayList<GeographicRegion>();
		for (int i = 0; i < subsets.size(); i++)
		{
			if (mostCaps.size() < subsets.get(i).size())
			{
				mostCaps = subsets.get(i);
			}
		}
		for (int i = 0; i < mostCaps.size(); i++)
		{
			output.add(mostCaps.get(i).getName());
		}
		
		return output;
	}
	
	/**
	 * Method to fetch all capitals from the CIA factbook from source page:
	 * https://www.cia.gov/library/publications/the-world-factbook/appendix/appendix-f.html
	 * @throws Exception
	 */
	private void FetchCapitalsFromFactbook() throws Exception
	{
		capitals = new ArrayList<GeographicRegion>();
		
		HTMLDocument doc;
		try
		{
			doc = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/appendix/appendix-f.html");
		}
		catch (Exception ex)
		{
			throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
		}
		
		Elements data = doc.Select(".category_data");
		for (int i = 1; i < data.size(); i = i + 4) //skip the first element returned, it's just text; count by fours to traverse rows
		{
			if (data.get(i).child(0).children().size() > 0)
				data.get(i).child(0).child(0).remove();;
			String name = data.get(i).child(0).html();
			if (!name.contains("capital")) //skip non-capitals
				continue;
			
			String capital = name.substring(0, name.indexOf("("));
			String region = data.get(i + 1).html();
			String latitude = data.get(i + 2).html();
			String longitude = data.get(i + 3).html();
			
			GeographicRegion geo = new GeographicRegion(capital, region, longitude, latitude);
			capitals.add(geo);
		}
	}
	
	/**
	 * Method to generate subsets of all the capitals fetched from the CIA factbook. Works recursively to build the
	 * list.
	 * @param leftovers : A list of all the capitals to be placed into subsets.
	 * @return : A list of subsets of the input GeographicRegions
	 */
	private List<List<GeographicRegion>> generateSubsets(List<GeographicRegion> leftovers)
	{
		List<List<GeographicRegion>> output = new ArrayList<List<GeographicRegion>>();
		
		if (leftovers.size() == 0)
		{
			output.add(leftovers);
		}
		else
		{
			GeographicRegion removed = leftovers.remove(0);
			output = generateSubsets(leftovers);
			int maxLength = 0;
			for (int i = output.size() - 1; i >= 0; i--)
			{
				if (maxLength < output.get(i).size())
					maxLength = output.get(i).size();
				
				List<GeographicRegion> temp = new ArrayList<GeographicRegion>();
				temp.addAll(output.get(i));
				temp.add(removed);
				if (checkSetIsWithinRange(temp))
				{
					output.add(temp);
					if (maxLength < temp.size())
						maxLength = temp.size();
				}
			}
			
			int maxDisparity = maxLength - leftovers.size();
			
			for (int i = output.size() - 1; i >= 0; i--)
			{
				if (output.get(i).size() - leftovers.size() < maxDisparity)
					output.remove(i);
			}
		}
		
		return output;
	}
	
	/**
	 * Method to check if all the GeographicRegions within a list satisfy the vacationing condition.
	 * @param geos : list of GeographicRegions to test
	 * @return : boolean which indicates whether the vacationing condition is satisfied
	 */
	private boolean checkSetIsWithinRange(List<GeographicRegion> geos)
	{
		boolean output = true;
		
		for (int i = 0; i < geos.size(); i++)
		{
			for (int j = i + 1; j < geos.size(); j++)
			{
				if (!geos.get(i).WithinRange(_maxDistance, geos.get(j)))
				{
					output = false;
					break;
				}
			}
			if (!output)
				break;
		}
		
		return output;
	}
	
//	private List<List<GeographicRegion>> groupRegions(List<GeographicRegion> leftovers)
//	{
//		for (int i = 0; i < vacationPath.size(); i++)
//		{
//			
//		}
//	}

}
