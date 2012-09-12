/**
 * 
 */
package Assignment1;

/**
 * @author Kevin Lin (KL2495)
 *
 */

import java.util.List;
import java.util.Scanner;

public class CIAFactbookUI {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Scanner scanner = new Scanner(System.in);
		IFactSearch search;
		
		System.out.println("Welcome! Use this application to query the CIA World Factbook.");
		PrintOptions();

		while (true)
		{
			try
			{
				System.out.print("\nEnter an option: ");
				String input = scanner.nextLine();
				List<String> results = null;
				
				String continent;
				String disaster;
				String count;
				String color;
				switch (input) 
				{					
					case "1":
						System.out.print("Enter a continent: ");
						continent = scanner.nextLine();
						System.out.print("Enter a disaster type: ");
						disaster = scanner.nextLine();
						
						search = new DisasterSearch(continent, disaster);
						results = search.Search();
						break;
					case "2":
						System.out.print("Enter a continent: ");
						continent = scanner.nextLine();
						System.out.print("Enter a count: ");
						count = scanner.nextLine();
						
						search = new PoliticalPartySearch(continent, count);
						results = search.Search();
						break;
					case "3":
						System.out.print("Enter a flag color: ");
						color = scanner.nextLine();
						
						search = new FlagSearch(color);
						results = search.Search();
						break;
					case "4":
						search = new LandlockSearch();
						results = search.Search();
						break;
					case "5":
						System.out.println("Enter a maximum distance in degrees: ");
						count = scanner.nextLine();
						
						search = new VacationSearch(Integer.parseInt(count));
						results = search.Search();
						break;
					case "list-options":
						PrintOptions();
						break;
					case "quit":
						System.out.println("\nHope you were satisfied!\nQuitting...");
						System.exit(0);
					default:
						System.out.println("You have not entered a valid option.");
						break;
				}
				
				if (results != null && results.size() > 0)
				{
					System.out.println("--------------------------------------");
					System.out.println("Your query has returned the following:");
					for (int i = 0; i < results.size(); i++)
					{
						System.out.println(results.get(i));
					}
				}
				else if (results != null && results.size() == 0)
				{
					System.out.println("--------------------------------------");
					System.out.println("There are no results for your query.");
				}
			}
			catch (Exception ex)
			{
				System.out.println("Your query has ended with an unexpected error. Please review the error and try your query again." + "\n"
						+ ex.getMessage());
			}
		}
	}
	
	private static void PrintOptions()
	{
		System.out.println("Here are your options:" + "\n"
				+ "1 : List countries in \"South America\" that are prone to \"earthquakes\"." + "\n"
				+ "2 : List countries in \"Asia\" with more than \"10\" political parties." + "\n"
				+ "3 : Find all countries that have the color \"blue\" in their flag." + "\n"
				+ "4 : Find all landlocked countries." + "\n"
				+ "5 : Find a list of lat/long coordinates and countries/capitals that can be visited, maximizing the number visited where capitals are within \"10\" degrees of each other." + "\n"
				+ "list-options : Relists these options." + "\n"
				+ "quit : Exits the program.");
	}

}
