package week15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class week15 {
	public static void main(String[] args) throws IOException, ParseException {
		String clientId = "J2RNuhh2I7xGnZEoVZVy";
		String clinetSecret = "UTwxZz_Eve";
		
		Scanner s = new Scanner(System.in);
		System.out.print("검색어를 입력하세요: ");
		String words = s.next();
		
		String text = URLEncoder.encode(words, "UTF-8");
		String apiURL = "https://openapi.naver.com/v1/search/movie.json?query="+text; //json 결과
		//String apiURL = "https://developers.naver.com/docs/serviceapi/search/movie/movie.md#영화"+text; //xml 결과
		URL url = new URL(apiURL);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("X-naver-Client-Id", clientId);
		con.setRequestProperty("X-Naver-Client-Secret", clinetSecret);
		
		int responseCode = con.getResponseCode();
		BufferedReader br;
		if(responseCode == 200) { //정상 호출
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else { //에러 발생
			br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = br.readLine()) != null) {
			response.append(inputLine);
		}
		br.close();
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
		JSONArray infoArray = (JSONArray) jsonObject.get("items");
		
		for(int i=0; i<infoArray.size(); i++) {
			System.out.println("=item_"+i+"==================================");
			JSONObject itemObject = (JSONObject) infoArray.get(i);
			System.out.println("title:\t"+itemObject.get("title"));
			System.out.println("subtitle:\t"+itemObject.get("subtitle"));
			System.out.println("director:\t"+itemObject.get("director"));
			System.out.println("actor:\t"+itemObject.get("actor"));
			System.out.println("userRating:\t"+itemObject.get("userRating") + "\n");
		}
	}
}
