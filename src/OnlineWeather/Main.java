package OnlineWeather;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static void main(String[] args) {
		try {
			ApiExplorer api = new ApiExplorer(20190404, "0800", 57, 127);
			
			String sWeatherStr = api.getsApiData();
			sWeatherStr = sWeatherStr.replaceAll(".+?<items>", "");
			
			String sWeatherStrArray[] = sWeatherStr.split("<item>");
			
			//System.out.println(sWeatherStr);
			
			String sItemStr[] = {"category", "baseDate", "baseTime",  
							"fcstDate", "fcstTime", "fcstValue", "nx", "ny"};
			
			for(String sArray : sWeatherStrArray) {
				//System.out.println(sArray);
				
				String sReturnStr[] = api.getConvertWeatherValue(sArray, sItemStr);
				
				for (int i = 0; i < sReturnStr.length; i++) {
					System.out.println(sItemStr[i]+"::"+sReturnStr[i]);					
				}	
				
				System.out.println("");
			}
			
//			  Pattern p = Pattern.compile("(^[0-9]*$)");
//		        
//		        int onlyNum;
//		        String inputVal;
//		        Scanner iStream = new Scanner(System.in);
//		        
//		        inputVal = iStream.nextLine();
//		        Matcher m = p.matcher(inputVal);
//		        
//		        if(m.find())
//		        {
//		            onlyNum = Integer.parseInt(inputVal);
//		            System.out.println(onlyNum);
//		        }
//		        else
//		        {
//		            System.out.println("숫자가 아닌데..?");
//		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
