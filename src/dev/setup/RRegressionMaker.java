package dev.setup;

import java.util.ArrayList;

import assist.util.Pair;
import utilities.LocalToolBase;

/**
 * Designed to set up a script to do Logistic Regression in R with the Java Data
 * TODO Debug, does not work yet
 * @author Benjy Strauss
 *
 */

@SuppressWarnings({"unchecked", "rawtypes"})
public class RRegressionMaker extends LocalToolBase {
	private static final String CSV_NAME = "";
	
	//7 descriptors
	//Entropy-6
	private static final Pair<String, Integer> E6 = new Pair<String, Integer>("E6", 7);
	//Entropy-20
	private static final Pair<String, Integer> E20 = new Pair<String, Integer>("E20", 8);
	
	//IsUnstruct (IU)
	private static final Pair<String, Integer> IsU = new Pair<String, Integer>("IsU", 9);
	//Vkabat (VK)
	private static final Pair<String, Integer> VK = new Pair<String, Integer>("VK", 10);
	
	//Amber95 of Dominant (AD)
	private static final Pair<String, Integer> AMBER_D = new Pair<String, Integer>("AD", 12);
	//Amber95 of Consensus (AC)
	private static final Pair<String, Integer> AMBER_C = new Pair<String, Integer>("AC", 13);
	//Amber95 Weighted Average (AW)
	private static final Pair<String, Integer> AMBER_W = new Pair<String, Integer>("AW", 14);
	
	private static final Pair[][] combinations = { {E6}, {E20}, {E6, E20}, {IsU}, {IsU, E6},
			{IsU, E20}, {VK}, {VK, E6}, {VK, E20}, {VK, IsU}, {VK, IsU, E6}, {VK, IsU, E20},
			
			{AMBER_D}, {AMBER_D, E6}, {AMBER_D, E20}, {AMBER_D, E6, E20}, {AMBER_D, IsU},
			{AMBER_D, IsU, E6}, {AMBER_D, IsU, E20}, {AMBER_D, VK}, {AMBER_D, VK, E6},
			{AMBER_D, VK, E20}, {AMBER_D, VK, IsU}, {AMBER_D, VK, IsU, E6}, {AMBER_D, VK, IsU, E20},
			
			{AMBER_C}, {AMBER_C, E6}, {AMBER_C, E20}, {AMBER_C, E6, E20}, {AMBER_C, IsU},
			{AMBER_C, IsU, E6}, {AMBER_C, IsU, E20}, {AMBER_C, VK}, {AMBER_C, VK, E6},
			{AMBER_C, VK, E20}, {AMBER_C, VK, IsU}, {AMBER_C, VK, IsU, E6}, {AMBER_C, VK, IsU, E20},
			
			{AMBER_W}, {AMBER_W, E6}, {AMBER_W, E20}, {AMBER_W, E6, E20}, {AMBER_W, IsU},
			{AMBER_W, IsU, E6}, {AMBER_W, IsU, E20}, {AMBER_W, VK}, {AMBER_W, VK, E6},
			{AMBER_W, VK, E20}, {AMBER_W, VK, IsU}, {AMBER_W, VK, IsU, E6}, {AMBER_W, VK, IsU, E20}
	};
	
	private static final Pair[] metrics = {
			new Pair<String, Integer>("isSwitch_H", 22),
			new Pair<String, Integer>("isSwitch_O", 23),
			new Pair<String, Integer>("isSwitch_S", 24),
			new Pair<String, Integer>("isSwitch_U", 25),
			new Pair<String, Integer>("isSwitch_HO", 26),
			new Pair<String, Integer>("isSwitch_HS", 27),
			new Pair<String, Integer>("isSwitch_HU", 28),
			new Pair<String, Integer>("isSwitch_OU", 29),
			new Pair<String, Integer>("isSwitch_OS", 30),
			new Pair<String, Integer>("isSwitch_SU", 31),
			new Pair<String, Integer>("isSwitch_HOS", 32),
			new Pair<String, Integer>("isSwitch_HOU", 33),
			new Pair<String, Integer>("isSwitch_HSU", 34),
			new Pair<String, Integer>("isSwitch_OSU", 35),
			new Pair<String, Integer>("isSwitch_HOSU", 36)
	};
	
	private static ArrayList<String> calls = new ArrayList<String>();
	
	public static void main(String[] args) {
		ArrayList<String> fileLines = new ArrayList<String>();
		fileLines.add("library(ROCR)");
		fileLines.add("");
		fileLines.add("learn_raw <- read.csv('"+ CSV_NAME + "', header = TRUE)");
		fileLines.add("");
		for(int i = 0; i < metrics.length; ++i) {
			fileLines.addAll(makeAllLearningSets(i));
			fileLines.add("");
		}
		
		for(int i = 0; i < metrics.length; ++i) {
			fileLines.addAll(makeAllGLMCalls(i));
			fileLines.add("");
		}
		fileLines.add("");
		writeFileLines("regression/input/rreg.txt", fileLines);
	}
	
	/**
	 * E6, E20, IU, VK, AD, AC, AW
	 * @return
	 */
	private static ArrayList<String> makeAllLearningSets(int testFor) {
		ArrayList<String> learningSets = new ArrayList<String>();
		
		for(Pair<String, Integer> pair[]: combinations) {
			learningSets.add(getDescriptorSubsetFromPairCombination(testFor, pair));
		}
		return learningSets;
	}
	
	/**
	 * 
	 * @param testFor
	 * @return
	 */
	private static ArrayList<String> makeAllGLMCalls(int testFor) {
		ArrayList<String> learningSets = new ArrayList<String>();
		
		String prefix = (String) metrics[testFor].x;
		StringBuilder builder = new StringBuilder();
		
		for(Pair<String, Integer> pair[]: combinations) {
			builder.setLength(0);
			String baseModelName = getModelName(testFor, pair);
			builder.append("out_" + baseModelName);
			builder.append(" <- glm(");
			builder.append(prefix);
			builder.append("~.,family=binomial(link='logit'),data=");
			builder.append(baseModelName);
			builder.append(")");
			learningSets.add(builder.toString());
			calls.add(builder.toString());
		}
		
		return learningSets;
	}
	
	/**
	 * Obtains a model name from a given metric and descriptors
	 * @param metric
	 * @param descriptors
	 * @return: the model's name
	 */
	private static String getModelName(int metric, Pair<String, Integer>... descriptors) {
		String prefix = (String) metrics[metric].x;
		prefix = prefix.substring("isSwitch_".length());
		
		StringBuilder learnBuilder = new StringBuilder(prefix + "_model_");
		for(Pair<String, Integer> pair: descriptors) {
			learnBuilder.append(pair.x + "_");
		}
		trimLastChar(learnBuilder);
		return learnBuilder.toString();
	}
	
	/**
	 * 
	 * @param metric
	 * @param descriptors
	 * @return
	 */
	private static String getDescriptorSubsetFromPairCombination(int metric, Pair<String, Integer>... descriptors) {
		StringBuilder subsetBuilder = new StringBuilder(" <- subset(learn_raw, select=c(");
		for(Pair<String, Integer> pair: descriptors) {
			subsetBuilder.append(pair.y + ",");
		}
		trimLastChar(subsetBuilder);
		
		subsetBuilder.append("," + metrics[metric].y);
		
		subsetBuilder.append("))");
		return "" + getModelName(metric, descriptors) + subsetBuilder;
	}
}
