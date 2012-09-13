package Assignment1;

/**
 * LandlockSearch.java
 * KL2495
 * An implementation of the IFactSearch interface that searches for all countries that are landlocked.
 * This is done by searching all country profile pages for their "Coastline" data fields and checking
 * them for the presence of a "landlocked" keyword.
 * 
 * Data source: "https://www.cia.gov/"
 */

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LandlockSearch implements IFactSearch {

	/**
	 * Constructor for search for landlocked countries
	 */
	public LandlockSearch()
	{
		
	}
	
	/**
	 * An override search method to find all landlocked countries in the world.
	 * @return : list of landlocked countries
	 */
	public List<String> Search() throws Exception 
	{
		ArrayList<String> output = new ArrayList<String>();
		
		HTMLDocument doc; //fetch data from cia factbook about all countries and the urls to their profile pages
		try
		{
			doc = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/print/textversion.html");
		}
		catch (Exception ex)
		{
			throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
		}
		
		//loop through all countries and their profile pages
		Elements countryElements = doc.Select(".category");
		for (int i = 0; i < countryElements.size(); i++)
		{
			Element country = countryElements.get(i);
			if (country.child(0).html().contains("World")) //skips "World" profile page
				continue;
			
			String dataUrl = "https://www.cia.gov/library/publications/the-world-factbook/" + country.child(0).attr("href").substring(3); //build url to country profile page
			HTMLDocument countryDoc; //fetches profile page data
			try
			{
				countryDoc = new HTMLDocument(dataUrl);
			}
			catch (Exception ex)
			{
				throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
			}
			
			//navigate to the "Coastline" field and check for presence of "landlocked" keyword
			Elements dataCategories = countryDoc.Select(".category");
			for (int j = 0; j < dataCategories.size(); j++)
			{
				Element dataCategory = dataCategories.get(j);
				try
				{
					if (dataCategory.children().size() > 0 && dataCategory.child(0).html().trim().equalsIgnoreCase("Coastline")) //find "Coastline" field on page
					{
						Element coastlineEl = dataCategory.parent().parent().nextElementSibling(); //parse to Coastline text
						if (coastlineEl.child(0).child(0).className().equalsIgnoreCase("category_data"))
						{
							String coastlineText = coastlineEl.child(0).child(0).html();
							if (coastlineText.contains("landlocked"))
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
		
		return output;
	}

}
