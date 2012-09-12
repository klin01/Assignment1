package Assignment1;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LandlockSearch implements IFactSearch {

	public LandlockSearch()
	{
		
	}
	
	@Override
	public List<String> Search() throws Exception 
	{
		ArrayList<String> output = new ArrayList<String>();
		
		HTMLDocument doc;
		try
		{
			doc = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/print/textversion.html");
		}
		catch (Exception ex)
		{
			throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
		}
		
		Elements countryElements = doc.Select(".category");
		for (int i = 0; i < countryElements.size(); i++)
		{
			Element country = countryElements.get(i);
			if (country.child(0).html().contains("World"))
				continue;
			
			String dataUrl = "https://www.cia.gov/library/publications/the-world-factbook/" + country.child(0).attr("href").substring(3);
			HTMLDocument countryDoc;
			
			try
			{
				countryDoc = new HTMLDocument(dataUrl);
			}
			catch (Exception ex)
			{
				throw new Exception("Unable to query the CIA factbook. You may have been locked out of their servers. Change your IP and try again or wait a few hours.");
			}
			
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
