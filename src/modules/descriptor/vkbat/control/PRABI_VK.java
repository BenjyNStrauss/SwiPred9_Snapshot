package modules.descriptor.vkbat.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringJoiner;

import assist.base.Assist;
import assist.util.LabeledList;
import biology.descriptor.VKPred;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import modules.descriptor.vkbat.exceptions.server.NotSupportedByPrabiException;
import utilities.LocalToolBase;

/**
 * Contains methods for getting vkbat predictions from PRABI server
 * 
 * @author Benjy Strauss
 *
 */

public class PRABI_VK extends LocalToolBase {
	private static final String PRABI_SEQ_START_FLAG = "<CODE>";
	private static final String PRABI_SEQ_END_FLAG = "</CODE>";
	
	/**
	 * Gets a secondary structure prediction from the PRABI server:
	 * 		"https://npsa-prabi.ibcp.fr/cgi-bin/..."
	 * @param chain: the chain to predict the structure for
	 * @param predMethod
	 * @param replaceExisting 
	 * @return: string containing secondary structure prediction
	 * @throws NotSupportedByPrabiException: if PRABI does not support the given prediction method
	 */
	public static String getPRABIServer(AminoChain<?> chain, VKPred predMethod) throws NotSupportedByPrabiException {
		Objects.requireNonNull(predMethod, "Null prediciton method!");
		Objects.requireNonNull(chain, "Null chain to predict!");
		
		qpl("Getting " + predMethod + " for: " + chain.id().mostUseful() + " using PRABI.");
		
		LabeledList<String> predictionLines = new LabeledList<String>();
		String querySeq = FastaCrafter.textSequenceForVkbat(chain);
		if(querySeq.length() == 0) { return ""; }
		
		String predURL = predMethod.batchURL_PRABI();
		
		String prediction = null;
		
		try  {
			URL url = new URL(predURL);
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection) con;
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			
			Map<String,String> args = new HashMap<>();
			//add the sequence to the arguments
			switch(predMethod) {
			case DSC:
				args.put("title", chain.id().mostUseful());
				args.put("ali_width", "70");	//no break intentionally
			default:
				args.put("notice", querySeq);
			}
			
			StringJoiner joiner = new StringJoiner("&");
			for(Map.Entry<String, String> element : args.entrySet()) {
				joiner.add(URLEncoder.encode(element.getKey(), "UTF-8") + "=" + URLEncoder.encode(element.getValue(), "UTF-8"));
			}
			
			byte[] stringBytes = joiner.toString().getBytes(StandardCharsets.UTF_8);
			http.setFixedLengthStreamingMode(stringBytes.length);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			http.connect();
			
			try(OutputStream os = http.getOutputStream()) {
			    os.write(stringBytes);
			}
			
			//qp("flag0");
			//qp("http: " + http);
			
			InputStream response = http.getInputStream();
		    Scanner scanner = new Scanner(response);
		    
		    boolean dataLine = false;
		    
		    while(scanner.hasNext()) {
		    	
				String line = scanner.nextLine();
				//qp(line);
				if(line.contains(PRABI_SEQ_START_FLAG)) { dataLine = true;  continue; }
				if(line.contains(PRABI_SEQ_END_FLAG)) { break; }
				
				if(dataLine) {
					//qp("flag C");
					predictionLines.add(line);
				}
		    }
		    //qp("flag1");
		    scanner.close();
		} catch (Exception e) {
			qerrl("Error retrieving (PRABI): " + chain.id().standard() + " ["+predMethod+"]");
			if(e.getMessage().contains("Connection refused")) {
				qerrl("\t"+e.getMessage());
			} else {
				e.printStackTrace();
			}
		}
		//qp("flag2");
		prediction = parsePRABI(predictionLines);
		return prediction;
	}
	
	/**
	 * Gets a secondary structure prediction from the PRABI server:
	 * 		"https://npsa-prabi.ibcp.fr/cgi-bin/..."
	 * @param chain: the chain to predict the structure for
	 * @param predMethod
	 * @param replaceExisting 
	 * @return: string containing secondary structure prediction
	 * @throws NotSupportedByPrabiException: if PRABI does not support the given prediction method
	 */
	public static String getPRABIServer(String querySeq, VKPred predMethod) throws NotSupportedByPrabiException {
		Objects.requireNonNull(predMethod, "Null prediciton method!");
		Objects.requireNonNull(querySeq, "Null sequence to predict!");
		
		LabeledList<String> predictionLines = new LabeledList<String>();
		if(querySeq.length() == 0) { return ""; }
		
		String predURL = predMethod.batchURL_PRABI();
		
		String prediction = null;
		
		try  {
			URL url = new URL(predURL);
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection) con;
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			
			Map<String,String> args = new HashMap<>();
			//add the sequence to the arguments
			switch(predMethod) {
			case DSC:
				args.put("title", "Prabi Query");
				args.put("ali_width", "70");	//no break intentionally
			default:
				args.put("notice", querySeq);
			}
			
			StringJoiner joiner = new StringJoiner("&");
			for(Map.Entry<String, String> element : args.entrySet()) {
				joiner.add(URLEncoder.encode(element.getKey(), "UTF-8") + "=" + URLEncoder.encode(element.getValue(), "UTF-8"));
			}
			
			byte[] stringBytes = joiner.toString().getBytes(StandardCharsets.UTF_8);
			http.setFixedLengthStreamingMode(stringBytes.length);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			http.connect();
			
			try(OutputStream os = http.getOutputStream()) {
			    os.write(stringBytes);
			}
			
			//qp("flag0");
			//qp("http: " + http);
			
			InputStream response = http.getInputStream();
		    Scanner scanner = new Scanner(response);
		    
		    boolean dataLine = false;
		    
		    while(scanner.hasNext()) {
		    	
				String line = scanner.nextLine();
				//qp(line);
				if(line.contains(PRABI_SEQ_START_FLAG)) { dataLine = true;  continue; }
				if(line.contains(PRABI_SEQ_END_FLAG)) { break; }
				
				if(dataLine) {
					//qp("flag C");
					predictionLines.add(line);
				}
		    }
		    //qp("flag1");
		    scanner.close();
		} catch (Exception e) {
			qerrl("Error retrieving (PRABI): " + querySeq + " ["+predMethod+"]");
			if(e.getMessage().contains("Connection refused")) {
				qerrl("\t"+e.getMessage());
			} else {
				e.printStackTrace();
			}
		}
		//qp("flag2");
		prediction = parsePRABI(predictionLines);
		return prediction;
	}
	
	/**
	 * Parses a PRABI result
	 * @param predictionLines
	 * @return
	 */
	private static String parsePRABI(LabeledList<String> predictionLines) {
		if(predictionLines.size() == 0) { return null; }
		
		//remove formatting lines
		predictionLines.remove(0);
		predictionLines.remove(0);
		StringBuilder predBuilder = new StringBuilder();
		
		for(int index = 0; index < predictionLines.size(); ++index) {
			//determine a prediction line
			if(index % 2 == 1) {
				predBuilder.append(Assist.removeHTML(predictionLines.get(index)));
			}
		}
		
		return predBuilder.toString();
	}
}
