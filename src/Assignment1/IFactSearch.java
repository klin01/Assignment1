package Assignment1;

/**
 * IFactSearch.java
 * KL2495
 * A java interface for the different types of searches required for this assignment.
 */

import java.util.*;

public interface IFactSearch {
	
	/**
	 * A method signature for carrying out a search query and returning results.
	 * @return : a list of countries satisfying the search
	 * @throws Exception
	 */
	List<String> Search() throws Exception;
	
}
