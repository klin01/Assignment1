package Assignment1;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PoliticalPartySearch implements IFactSearch{

	private String _continent;
	private int _partyCount;
	
	private Hashtable<String, List<String>> mappings;
	
	public PoliticalPartySearch(String continent, String partyCount) throws Exception
	{
		_continent = continent;
		_partyCount = Integer.parseInt(partyCount);
		
		FetchContinentCountryMappings();
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
		
		List<String> countries = mappings.get(_continent.toUpperCase());
		if (countries.size() == 0)
			throw new Exception("There is no continent of name " + _continent + ". Please reconstruct your search.");
		
		Elements countryElements = doc.Select(".category");
		for (int i = 0; i < countryElements.size(); i++)
		{
			Element country = countryElements.get(i);
			if (countries.contains(country.child(0).html()))
			{
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
						System.out.println();
					}
				}
			}
		}
		
		return output;
	}
	
	private void FetchContinentCountryMappings() throws Exception
	{
		mappings = new Hashtable<String, List<String>>();
		HTMLDocument mappingData = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/fields/2145.html");
		Elements countryContinentMappings = mappingData.Select(".fl_region");
		for (int i = 0; i < countryContinentMappings.size(); i++)
		{
			Element country = countryContinentMappings.get(i);
			String continent = ((Element)country.nextSibling()).html();
			
			if (country.child(0).html() == "France")
				continent = "Europe";
			
			if (!mappings.containsKey(continent.toUpperCase()))
				mappings.put(continent.toUpperCase(), new ArrayList<String>());
			
			mappings.get(continent.toUpperCase()).add(country.child(0).html());
		}
	}

}
