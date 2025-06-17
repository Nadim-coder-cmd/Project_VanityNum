import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class VanityNumbers {

	
	 
	public VanityNumbers() {
		
	}
	
	
	
	public static final Map<Character, String> DIGIT_TO_CHARS = new HashMap<>();
	    {
	   DIGIT_TO_CHARS.put('2', "ABC");
	   DIGIT_TO_CHARS.put('3', "DEF");
	   DIGIT_TO_CHARS.put('4', "GHI");
	   DIGIT_TO_CHARS.put('5', "JKL");
	   DIGIT_TO_CHARS.put('6', "MNO");
	   DIGIT_TO_CHARS.put('7', "PQRS");
	   DIGIT_TO_CHARS.put('8', "TUV");
	   DIGIT_TO_CHARS.put('9', "WXYZ");
	    }
	    
	    private static final Set<String> DICTIONARY = new HashSet<>();

	    static {
	        try (BufferedReader br = new BufferedReader(new InputStreamReader(
	            VanityNumbers.class.getResourceAsStream("/words.txt")))) {

	            String line;
	            while ((line = br.readLine()) != null) {
	                DICTIONARY.add(line.trim().toUpperCase()); // store in uppercase
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to load dictionary", e);
	        }
	    }
	    
          
	    
	    
	    
	    
	    
	    public void showVanitynum(String phonenum) {
	    
	    	 if (phonenum != null && phonenum.startsWith("+")) {
	    	        phonenum = phonenum.replace("+1", "");
	    	    }
	    List<String> results1=getVanityNumbers_vowels(phonenum);
	    List<String> results2=getVanityNumbers_vowels_realword(phonenum);
	    
	    for(int i=0; i<results1.size(); i++) {
			System.out.print(results1.get(i) + "\t\t\t\t" + results2.get(i) + "\n");
			
			}
		}
	    
	    
	   
	    
	    public List<String> getVanityNumbers_vowels_realword(String phoneNumber) {
	        List<String> results = new ArrayList<>();
	        generateVanityNumbers(phoneNumber, 0, new StringBuilder(), results);
	        
	        results.sort(Comparator.comparingDouble(this::scoreVanity).reversed());
	        return results.size() > 5 ? results.subList(0, 5) : results;
	    }
	    
	    
	    
	    
	    public List<String> getVanityNumbers_vowels(String phoneNumber) {
	        List<String> results = new ArrayList<>();
	        generateVanityNumbers(phoneNumber, 0, new StringBuilder(), results);
	        
	        results.sort(Comparator.comparingDouble(this::countVowels).reversed());
	        return results.size() > 5 ? results.subList(0, 5) : results;
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    public void generateVanityNumbers(String phoneNumber, int index, StringBuilder current, List<String> results) {
	        if (index == phoneNumber.length()) {
	            results.add(prepend(current.toString()));
	            return;
	        }    
	        
	            // base case. therefore return only exits current recursive path, removes one frame off of recursion stack and
	            // return control to previous level 
	        
	            char digit = phoneNumber.charAt(index);
	            String letters = DIGIT_TO_CHARS.get(digit);
	        
	        if (letters != null) {
	            for (char letter : letters.toCharArray()) {
	                current.append(letter);
	                generateVanityNumbers(phoneNumber, index + 1, current, results);
	                
	                
	                current.deleteCharAt(current.length() - 1);  // called only when recursive call returns and generateVanityNumbers reaches base case
	            }
	        } else {
	            current.append(digit);
	            generateVanityNumbers(phoneNumber, index + 1, current, results);
	            current.deleteCharAt(current.length() - 1);
	        }
	    }
	    
	    
	    public String prepend(String vanity) {
	        return "1800" + vanity;
	       }
	    
	    
	    private double scoreVanity(String s) {
	        double score = 0;
	        score += countVowels(s) * 1.5;
	        if (containsRealWord(s)) score += 5;
	        return score;
	    }
	    
	   public long countVowels(String s) {
	        return s.chars().filter(c -> "AEIOU".indexOf(c) != -1).count();
	     }
	  
	   
	   private boolean containsRealWord(String vanity) {
		    for (int i = 0; i < vanity.length(); i++) {
		        for (int j = i + 3; j <= vanity.length(); j++) { // check words of len ≥ 3
		            if (DICTIONARY.contains(vanity.substring(i, j))) {
		                return true;
		            }
		        }
		    }
		    return false;
		}

}

	
	

