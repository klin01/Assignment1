package Assignment1;

/**
 * HTMLDocument.java
 * KL2495
 * A java class to encapsulate the fetching of HTML data from a web request and the selection of elements using a CSS selector.
 * This was implemented using a third party java package, jsoup, a java HTML parser.
 * 
 * Source and documentation can be found at: "http://jsoup.org".
 */

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class HTMLDocument {

	public Document doc;
	
	/**
	 * Constructor for HTMLDocument object that pulls in the html data from specified source
	 * @param url : web address to query
	 * @throws Exception : web exception
	 */
	public HTMLDocument (String url) throws Exception
	{
		doc = Jsoup.connect(url).timeout(20 * 1000).get();
	}
	
	/**
	 * Accessor method for the HTML document fetched
	 * @return Document : the document fetched at the specified url
	 */
	public Document getDocument()
	{
		return doc;
	}
	
	/**
	 * Method to traverse and select HTML elements based on CSS selectors
	 * @param selector : any valid css selector
	 * @return Elements : html elements that satisfy the selector
	 */
	public Elements Select(String selector)
	{
		return doc.select(selector);
	}
}
