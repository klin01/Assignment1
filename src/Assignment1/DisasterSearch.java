package Assignment1;

/**
 * DisasterSearch.java
 * KL2495
 * An implementation of the IFactSearch class that specifically searches countries within a continent for a specific natural disaster and returns the countries affected by them.
 * I do this by searching the country profile page for the "Natural Hazards" field and doing a word search of the data under that header.
 * 
 * Data source: "https://www.cia.gov/"
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DisasterSearch implements IFactSearch{

	private String _continent; //continent to search
	private String _disaster; //natural hazard to search for
	
	private Hashtable<String, List<String>> mappings; //hashtable to map countries to continents, with continent as key
	
	/**
	 * Constructor for an object to search for countries susceptible to a specified natural disaster within a specific continent.
	 * @param continent : Continent to search in.
	 * @param disaster : Disaster to search for.
	 * @throws Exception
	 */
	public DisasterSearch(String continent, String disaster) throws Exception
	{
		_continent = continent;
		_disaster = disaster;
		
		FetchContinentCountryMappings();
	}
	
	/**
	 * Override method, implements the search function to find all countries within the specified continent susceptible to the specified disaster.
	 * @return : list of country names within the specified continent susceptible to the specified natural hazard
	 */
	public List<String> Search() throws Exception 
	{
		ArrayList<String> output = new ArrayList<String>();
		HTMLDocument doc; //fetch page with listing of all countries in factbook and urls to profiles
		try
		{
			doc = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/print/textversion.html");
		}
		catch (Exception ex)
		{
			throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
		}
		
		List<String> countries = mappings.get(_continent.toUpperCase()); //determine the relevant countries
		if (countries.size() == 0)
			throw new Exception("There is no continent of name " + _continent + ". Please reconstruct your search.");
		
		//loop through countries on page, looking for ones in the specified continent
		Elements countryElements = doc.Select(".category"); 
		for (int i = 0; i < countryElements.size(); i++)
		{
			Element country = countryElements.get(i);
			if (countries.contains(country.child(0).html()))
			{
				String dataUrl = "https://www.cia.gov/library/publications/the-world-factbook/" + country.child(0).attr("href").substring(3); //construct url to country profile
				HTMLDocument countryDoc; //fetch country profile data
				try
				{
					countryDoc = new HTMLDocument(dataUrl);
				}
				catch (Exception ex)
				{
					throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
				}
				
				//loop through elements on the page looking for the "Natural Hazard" field
				Elements dataCategories = countryDoc.Select(".category");
				for (int j = 0; j < dataCategories.size(); j++)
				{
					Element dataCategory = dataCategories.get(j);
					try
					{
						if (dataCategory.children().size() > 0 && dataCategory.child(0).html().trim().equalsIgnoreCase("Natural hazards")) //find "Natural Hazard" field on page
						{
							Element naturalHazardsEl = dataCategory.parent().parent().nextElementSibling(); //parse to Natural Hazard text
							if (naturalHazardsEl.child(0).child(0).className().equalsIgnoreCase("category_data"))
							{
								if (naturalHazardsEl.html().toUpperCase().contains(_disaster.toUpperCase()))
								{
									output.add(countryDoc.Select(".region_name1").get(0).html());
								}
							}
						}
					}
					catch (Exception ex) {
						throw new Exception("Encountered an unexpected page format. Unable to parse CIA data for " + countryDoc.Select(".region_name1").get(0).html() + ".");
					}
				}
			}
		}
		
		return output;
	}
	
	/**
	 * A helper method to map countries to continents in mappings hashtable.
	 * @throws Exception
	 */
	private void FetchContinentCountryMappings() throws Exception
	{
		mappings = new Hashtable<String, List<String>>();
		HTMLDocument mappingData = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/fields/2145.html");
		Elements countryContinentMappings = mappingData.Select(".fl_region");
		for (int i = 0; i < countryContinentMappings.size(); i++)
		{
			Element country = countryContinentMappings.get(i);
			String continent = ((Element)country.nextSibling()).html();
			
			if (country.child(0).html() == "France") //special case, France needs to be mapped directly to Europe instead of by territories, to keep things simple
				continent = "Europe";
			
			if (!mappings.containsKey(continent.toUpperCase()))
				mappings.put(continent.toUpperCase(), new ArrayList<String>());
			
			mappings.get(continent.toUpperCase()).add(country.child(0).html());
		}
	}

}
