package OnlineWeather;

import java.io.BufferedReader;
import java.io.IOException;

/* Java 샘플 코드 */


import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ApiExplorer {
	private String sApiData;
	final StringBuffer sServiceKey = new StringBuffer("JTumoB1QZWNUG3AoO8al4LlYRiIhZTcxXZ7fpEHrTGznpsLeFJaHI8f3zL0%2B0fWDsV5QmVzdZAZZpwXewhIy%2Bg%3D%3D");
	
	public ApiExplorer(final int base_date, final String base_times, final int nx, final int ny) throws IOException {
		final String sBaseDate = Integer.toString(base_date);
		final String sBaseTime = base_times;
		final String sNX = Integer.toString(nx);
		final String sNY = Integer.toString(ny);
		
		StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + sServiceKey); /*Service Key*/
        //urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("JTumoB1QZWNUG3AoO8al4LlYRiIhZTcxXZ7fpEHrTGznpsLeFJaHI8f3zL0%2B0fWDsV5QmVzdZAZZpwXewhIy%2Bg%3D%3D", "UTF-8")); /*서비스 인증*/
        //발표날짜 
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(sBaseDate, "UTF-8"));
        //발표시간
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(sBaseTime, "UTF-8"));
        //좌표x
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(sNX, "UTF-8"));
        //좌표y
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(sNY, "UTF-8"));
        
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8"));
        
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        	System.out.println(urlBuilder);
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        
        StringBuilder sb = new StringBuilder();
        String line;
        
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        
        rd.close();
        conn.disconnect();
        
        sApiData = sb.toString();
        System.out.println(sb.toString());
	}

	public String getsApiData() {
		return sApiData;
	}
	
	private final String sCategoryCode[][] = {
			{"POP", "강수확률" }
	,		{"PTY", "강수형태" }
	,		{"R06", "6시간 강수량" }
	,		{"REH", "습도" }
	,		{"S06", "6시간 신적설" }
	,		{"SKY", "하늘상태" }
	,		{"T3H", "3시간 기온" }
	,		{"UUU", "풍속(동서성분)" }
	,		{"VVV", "풍속(남북성분)" }
	,		{"VEC", "풍향" }
	,		{"WSD", "풍속" }
	};
	
	public String[] getConvertWeatherValue(String sWetherStr, String sConvertStr[]) {
		String sReturnStr[] = new String[sConvertStr.length];
		int i=0;
		
		for(String sArrStr : sConvertStr) {
			sReturnStr[i++] = sWetherStr.replaceAll(".*<" + sArrStr + ">|</" + sArrStr + ">.*", "");
			
			if(sArrStr.equals("category")) {
				for(String[] sCateStr : sCategoryCode) {
					if(sReturnStr[i-1].equals(sCateStr[0])) {
						sReturnStr[i-1] = sCateStr[1];
					}
				}
			}
		}
		
		
		return sReturnStr;
	}
}