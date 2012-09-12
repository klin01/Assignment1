package Assignment1;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;

public class VacationSearch implements IFactSearch {

	private int _maxDistance;
	private List<GeographicRegion> capitals;
	
	public VacationSearch(int maxDistance) throws Exception
	{
		_maxDistance = maxDistance;
		
		FetchCapitalsFromFactbook();
	}
	
	@Override
	public List<String> Search() throws Exception 
	{
		List<String> output = new ArrayList<String>();
		
		List<GeographicRegion> temp = new ArrayList<GeographicRegion>();
		
		for (int k = 0; k < capitals.size(); k++)
		{
			temp.add(capitals.get(k));
						
			for (int i = 0; i < capitals.size(); i++)
			{
				if (i == k)
					continue;
				
				boolean toBeAdded = true;
				
				for (int j = 0; j < temp.size(); j++)
				{
					if (!capitals.get(i).WithinRange(_maxDistance, temp.get(j)))
					{
						toBeAdded = false;
					}
				}
				
				if (toBeAdded)
				{
					temp.add(capitals.get(i));
				}
			}
			
			if (temp.size() > output.size()) 
			{
				output.clear();
				for (int i = 0; i < temp.size(); i++)
				{
					output.add(temp.get(i).getName());
				}
			}
			temp.clear();
		}
		
		output.add("Total: " + output.size());
		return output;
	}
	
	public void FetchCapitalsFromFactbook() throws Exception
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

}
