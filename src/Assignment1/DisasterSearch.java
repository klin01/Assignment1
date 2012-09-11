package Assignment1;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DisasterSearch implements IFactSearch{

	private String _continent;
	private String _disaster;
	
	private Hashtable<String, List<String>> mappings;
	
	public DisasterSearch(String continent, String disaster) throws Exception
	{
		_continent = continent;
		_disaster = disaster;
		
		FetchContinentCountryMappings();
	}
	
	@Override
	public List<String> Search() throws Exception 
	{
		ArrayList<String> output = new ArrayList<String>();
		HTMLDocument doc = new HTMLDocument("https://www.cia.gov/library/publications/the-world-factbook/print/textversion.html");
		
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
				HTMLDocument countryDoc = new HTMLDocument(dataUrl);
				
				Elements dataCategories = countryDoc.Select(".category");
				for (int j = 0; j < dataCategories.size(); j++)
				{
					Element dataCategory = dataCategories.get(j);
					try
					{
						if (dataCategory.children().size() > 0 && dataCategory.child(0).html().trim().equalsIgnoreCase("Natural hazards")) //find "Natural Hazard" field on page
						{
							Element naturalHazardsEl = dataCategory.parent().parent().nextElementSibling(); //parse to Natural Hazard text
							if (naturalHazardsEl.html().toUpperCase().contains(_disaster.toUpperCase()))
							{
								output.add(countryDoc.Select(".region_name1").get(0).html());
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
			
			if (!mappings.containsKey(continent.toUpperCase()))
				mappings.put(continent.toUpperCase(), new ArrayList<String>());
			
			mappings.get(continent.toUpperCase()).add(country.child(0).html());
		}
	}

}
