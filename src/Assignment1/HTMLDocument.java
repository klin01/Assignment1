package Assignment1;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class HTMLDocument {

	public Document doc;
	
	public HTMLDocument (String url) throws Exception
	{
		doc = Jsoup.connect(url).get();
	}
	
	public Document getDocument()
	{
		return doc;
	}
	
	public Elements Select(String selector)
	{
		return doc.select(selector);
	}
}
