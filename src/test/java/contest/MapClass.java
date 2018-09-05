package contest;

import java.util.concurrent.ConcurrentHashMap;

public class MapClass {
	//Different methods to store director names from wiki and imdb
	public static ConcurrentHashMap<String, String> wikitable;
	public static ConcurrentHashMap<String, String> imdbtable;

	public static ConcurrentHashMap<String, String> wikiDirectors(String param, String directors) {

		wikitable = new ConcurrentHashMap<String, String>();

		wikitable.put(param, directors);

		return wikitable;
	}

	public static ConcurrentHashMap<String, String> imdbDirectors(String param, String directors) {

		imdbtable = new ConcurrentHashMap<String, String>();

		imdbtable.put(param, directors);

		return imdbtable;
	}
}
