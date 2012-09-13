package Assignment1;

/**
 * FlagSearch.java
 * KL2495
 * An implementation of the IFactSearch interface to search for all countries which have a specified color in their flags.
 * This is implemented by querying all country profile pages and navigating to the flag description header and doing a word
 * search on the text under that header.
 * 
 * Data source: "https://www.cia.gov/"
 */

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FlagSearch implements IFactSearch {

	private String _color; //flag color to search for
	
	/**
	 * Constructor for the object to search for countries with the specified flag color.
	 * @param color : Flag color to search for
	 */
	public FlagSearch(String color)
	{
		_color = color;
	}
	
	/**
	 * A method to search for all countries with a specified color in their flags.
	 * @return : list of countries with specified color in their flags
	 */
	public List<String> Search() throws Exception 
	{
		ArrayList<String> output = new ArrayList<String>();
		
		HTMLDocument doc; //fetch page with all countries and urls to their country profile web pages
		try
		{
			doc = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/print/textversion.html");
		}
		catch (Exception ex)
		{
			throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
		}
		
		//loop through countries on page and each country profile page
		Elements countryElements = doc.Select(".category");
		for (int i = 0; i < countryElements.size(); i++)
		{
			Element country = countryElements.get(i);
			if (country.child(0).html().contains("World"))
				continue;
			
			String dataUrl = "https://www.cia.gov/library/publications/the-world-factbook/" + country.child(0).attr("href").substring(3); //build url to country profile page
			HTMLDocument countryDoc; //fetch country profile data
			try
			{
				countryDoc = new HTMLDocument(dataUrl);
			}
			catch (Exception ex)
			{
				throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
			}
			
			//navigate to Flag description section on page and check field data for flag color
			Elements dataCategories = countryDoc.Select(".category");
			for (int j = 0; j < dataCategories.size(); j++)
			{
				Element dataCategory = dataCategories.get(j);
				try
				{
					if (dataCategory.children().size() > 0 && dataCategory.child(0).html().trim().equalsIgnoreCase("Flag description")) //find "Flag description" field on page
					{
						Element flagEl = dataCategory.parent().parent().nextElementSibling(); //parse to Flag description text
						if (flagEl.child(0).child(0).className().equalsIgnoreCase("category_data"))
						{
							String flagText = flagEl.child(0).child(0).html();
							if (flagText.toUpperCase().contains(_color.toUpperCase()))
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
