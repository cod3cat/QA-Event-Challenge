package contest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class RunnerClass {

	//Set base URLs here
	public static final String wikiBase = "https://en.wikipedia.org/wiki/";
	public static final String googleBase = "http://www.google.com/search?q=";

	//Since our scope is limited, regular expressions are hard-coded here
	public static final String glink1 = "\"r\"><a\\D+q=(\\D+wikipedia.org\\D+\\/";
	public static final String glink2 = "[\\(0-9_film\\)]*)&amp;sa";
	public static final String wikiDirectorNames = "\">(\\b.*?)<\\/a>";
	public static final String wikiDirectorList = ">Directed by<.*>(\\D+)<\\/td>";
	public static final String imdbLinkRegEx = "https:\\/\\/www.imdb.com\\/title\\/tt[0-9]*\\/";

	//Placeholder for storing the api response from different sources
	public String gResponse = null;
	public String wResponse = null;
	public String iResponse = null;

	//Store imdb movie url and director names
	public String IMDBLinkWiki = null;
	public String imdbDirectorNames = "";

	public int noOfDirectors;

	MapClass obj = new MapClass();

	@Parameters({"TestParam"})
	@Test()
	public void contestRun(String param){

		//Replace space with underscore because that's how api wants it
		param = param.replaceAll(" ","_");

		//Create some lists and then their synchronized version so that they are thread safe?
		List<String> dirList = new ArrayList<String>();
		List<String> dirNames = new ArrayList<String>();
		List<String> imdbDirNames = new ArrayList<String>();

		List<String> SyncdirList = Collections.synchronizedList(dirList);
		List<String> SyncdirNames = Collections.synchronizedList(dirNames);
		List<String> SyncimdbDirNames = Collections.synchronizedList(imdbDirNames);

		//Get the current thread ID
		long id = Thread.currentThread().getId();
		//System.out.println(param +" running on thread " +id);

		//Save response from google search and extract wiki link for the movie 
		gResponse = HttpClientClass.returnResponseBody(googleBase+param);
		String wikiUrlFromRegex = GetRegexData.regIMDBLinkFromWiki(gResponse, glink1+param.replaceAll("[^A-Za-z_]","")+glink2, 1);

		//System.out.println("Wiki Link from google is   " +wikiUrlFromRegex);

		//Save response from wiki link and extract imdb link
		wResponse = HttpClientClass.returnResponseBody(wikiUrlFromRegex);
		IMDBLinkWiki = GetRegexData.regIMDBLinkFromWiki(wResponse.toString(), imdbLinkRegEx, 0);

		//System.out.println("IMDB Link is   " +IMDBLinkWiki);

		//Get director names from wiki
		SyncdirList = GetRegexData.regDirectorData(wResponse.toString(), wikiDirectorList);
		SyncdirNames = GetRegexData.regDirectorData(SyncdirList.get(0), wikiDirectorNames);

		System.out.println("Wiki Director List for " +param+ "  " +SyncdirNames);
		System.out.println();

		//Add director names to map
		MapClass.wikiDirectors(id-10+ " " +param.replaceAll("_", " "), SyncdirNames.toString());

		noOfDirectors = SyncdirNames.size();
		//System.out.println("Number of Directors for movie  " +param+ " is   " +noOfDirectors);

		//Based on number of directors, regexe to extract names changes.Hence multiple options
		if(noOfDirectors == 1) {

			iResponse = HttpClientClass.returnResponseBody(IMDBLinkWiki);
			SyncimdbDirNames = GetRegexData.regDirectorData(iResponse, "\"director\": \\{\\D+\\d+\\D+\"name\":.*?(\\b.*)\"");
			System.out.println("imdb Director List for " +param+ "  " +SyncimdbDirNames);
			System.out.println();
			MapClass.imdbDirectors(id-10+ " "+param.replaceAll("_", " "), SyncimdbDirNames.toString());
		}
		else if (noOfDirectors == 2) {

			iResponse = HttpClientClass.returnResponseBody(IMDBLinkWiki);
			SyncimdbDirNames = GetRegexData.regDirectorData(iResponse, "(\\\"director\\\":\\D+\\S+\\D+\\S+\\D+])");
			SyncimdbDirNames = GetRegexData.regDirectorData(SyncimdbDirNames.toString(), "\"name\":\\s+\"(\\b.*?)\"");

			System.out.println("imdb Director Links for " +param+ "  " +SyncdirNames);
			System.out.println();
			MapClass.imdbDirectors(id-10 +" "+param.replaceAll("_", " "), SyncimdbDirNames.toString());
		}
		else {
			String url = IMDBLinkWiki+"fullcredits?ref_=tt_ov_dr#directors/";
			iResponse = HttpClientClass.returnResponseBody(url);
			SyncimdbDirNames = GetRegexData.regDirectorData(iResponse, "\\/\\?ref_=ttfc_fc_dr\\d+\"\\D>\\s+(\\b.*)");
			System.out.println("imdb Director Links for " +param+ "  " +SyncimdbDirNames);
			System.out.println();
			MapClass.imdbDirectors(id-10+ " "+param.replaceAll("_", " "), SyncimdbDirNames.toString());
		}
	}

	@AfterSuite
	public static void match() {

		//At the end, if both maps match, project is working correctly
		if(MapClass.wikitable.toString().equalsIgnoreCase(MapClass.imdbtable.toString())) {

			System.out.println("WIKI Table");
			Enumeration<?> wikinames = MapClass.wikitable.keys();

			while(wikinames.hasMoreElements()) {
				String key = (String) wikinames.nextElement();
				System.out.println("Key: " +key+ " & Value: " +
						MapClass.wikitable.get(key));
			}

			System.out.println("IMDB Table");
			Enumeration<?> imdbnames = MapClass.imdbtable.keys();

			while(imdbnames.hasMoreElements()) {
				String key = (String) imdbnames.nextElement();
				System.out.println("Key: " +key+ " & Value: " +
						MapClass.imdbtable.get(key));
			}

		}else {

			System.out.println("Table contents do not match");
			System.out.println(MapClass.imdbtable);
			System.out.println();
			System.out.println(MapClass.wikitable);

		}
	}

}
