package Assignment1;

/**
 * PoliticalPartySearch.java
 * KL2495
 * An implementation of the IFactSearch.java interface that specifically searches for countries within a continent that has at least a certain number of political parties.
 * I do this by searching the country profile page for the "Political parties" header and doing a count of the political parties, which are listed under that header
 * separated by semicolons.
 * 
 * Data source: "https://www.cia.gov/"
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PoliticalPartySearch implements IFactSearch{

	private String _continent; //continent to search in
	private int _partyCount; //minimum number of parties to search for
	
	private Hashtable<String, List<String>> mappings;
	
	/**
	 * Constructor for the PoliticalPartySearch class
	 * @param continent : continent to search in
	 * @param partyCount : minimum number of political parties to search for
	 * @throws Exception
	 */
	public PoliticalPartySearch(String continent, String partyCount) throws Exception
	{
		_continent = continent;
		_partyCount = Integer.parseInt(partyCount);
		
		FetchContinentCountryMappings();
	}
	
	/**
	 * An override method to search a specific for all the countries that has a certain minimum number of political parties.
	 * @return : list of countries within the specified continent with at least the minimum number of political parties
	 */
	public List<String> Search() throws Exception 
	{
		ArrayList<String> output = new ArrayList<String>();
		HTMLDocument doc; //fetch web page containing all countries and links to country profiles
		try
		{
			doc = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/print/textversion.html");
		}
		catch (Exception ex)
		{
			throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
		}
		
		//get relevant countries from the specified country
		List<String> countries = mappings.get(_continent.toUpperCase());
		if (countries.size() == 0)
			throw new Exception("There is no continent of name " + _continent + ". Please reconstruct your search.");
		
		//loop through all the countries on the fetched page and check if they are part of the continent specified
		Elements countryElements = doc.Select(".category");
		for (int i = 0; i < countryElements.size(); i++)
		{
			Element country = countryElements.get(i);
			if (countries.contains(country.child(0).html()))
			{
				String dataUrl = "https://www.cia.gov/library/publications/the-world-factbook/" + country.child(0).attr("href").substring(3); //grab country profile url
				HTMLDocument countryDoc; //fetch data from country profile page
				try
				{
					countryDoc = new HTMLDocument(dataUrl);
				}
				catch (Exception ex)
				{
					throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
				}
				
				//loop through elements on the profile page, looking for the "Political parties and leaders" field
				Elements dataCategories = countryDoc.Select(".category");
				for (int j = 0; j < dataCategories.size(); j++)
				{
					Element dataCategory = dataCategories.get(j);
					try
					{
						if (dataCategory.children().size() > 0 && dataCategory.child(0).html().trim().equalsIgnoreCase("Political parties and leaders")) //find "Political parties and leaders" field on page
						{
							Element partyEl = dataCategory.parent().parent().nextElementSibling(); //parse to Political parties and leaders text
							if (partyEl.child(0).child(0).className().equalsIgnoreCase("category_data"))
							{
								String partyText = partyEl.child(0).child(0).html();
								if (partyText.split(";").length > _partyCount)
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
	 * Method to map countries to continents in mappings hashtable.
	 * @throws Exception
	 */
	private void FetchContinentCountryMappings() throws Exception
	{
		mappings = new Hashtable<String, List<String>>();
		HTMLDocument mappingData = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/fields/2145.html"); //fetch data from webpage that has mappings
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
