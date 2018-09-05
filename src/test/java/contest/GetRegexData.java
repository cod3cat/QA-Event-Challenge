package contest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetRegexData {

	public static String returnString = null;

	public static String regIMDBLinkFromWiki(String response, String pat, int groupNum){

		Pattern IMDBLink = Pattern.compile(pat);
		Matcher matcher = IMDBLink.matcher(response);

		while (matcher.find()) { returnString = matcher.group(groupNum).toString();  }

		return returnString;        

	}

/*	public static String regWikiLink(String response, String pat){

		Pattern WikiLink = Pattern.compile(pat);
		Matcher matcher = WikiLink.matcher(response);

		while (matcher.find()) { returnString = matcher.group(1).toString();  }
		return returnString;        

	}*/

	public static List<String> regDirectorData(String Response, String pat){

		List<String> dirNames = new ArrayList<String>();
		List<String> syncList = Collections.synchronizedList(dirNames);
		Pattern DirectedBy = Pattern.compile(pat);    	
		Matcher matcher = DirectedBy.matcher(Response);
		
		while(matcher.find()) {
			syncList.add(matcher.group(1).toString());
		}

		return syncList;
	}
}