package helloworld;

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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.ConnectEvent;

/**
 * Handler for suggesting vanity numbers, based on caller number
 */

public class BestVanityNumber implements RequestHandler <ConnectEvent, ConnectResponse>{
    //Mapping Digits to Characters:
    
    private static final int MAX_SIZE=500;
    private static final Map<Character, String> DIGIT_TO_CHARS = new HashMap<>();
static {
   DIGIT_TO_CHARS.put('2', "ABC");
   DIGIT_TO_CHARS.put('3', "DEF");
   DIGIT_TO_CHARS.put('4', "GHI");
   DIGIT_TO_CHARS.put('5', "JKL");
   DIGIT_TO_CHARS.put('6', "MNO");
   DIGIT_TO_CHARS.put('7', "PQRS");
   DIGIT_TO_CHARS.put('8', "TUV");
   DIGIT_TO_CHARS.put('9', "WXYZ");
    }

   
   
   
   // add read english words stored in a .txt locally into a set. Uses try catch to for error handling ensuring input validation
    private static final Set<String> DICTIONARY = new HashSet<>();

static {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(
        BestVanityNumber.class.getResourceAsStream("/words.txt")))) {

        String line;
        while ((line = br.readLine()) != null) {
            DICTIONARY.add(line.trim().toUpperCase()); // store in uppercase
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to load dictionary", e);
    }
}


@Override   //main abstract function of RequestHandler, implemened by BestVanityNumber
public ConnectResponse handleRequest(ConnectEvent event, Context context) {
    
    
    // create a dynamoDB table
    String tableName = System.getenv("VanityNumberTable");
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    DynamoDB dynamoDB = new DynamoDB(client);
    Table table = dynamoDB.getTable(tableName);
    long currentTimestamp = Instant.now().getEpochSecond();
    
    //initialize a ConnectResponse object
    ConnectResponse response = new ConnectResponse();

       // String phoneNumber = ""+12227459427"";
       Map<String, Object> parameters = event.getDetails().getParameters();
       String phoneNumber = parameters.get("CustomerNumber").toString();


       if (phoneNumber != null && phoneNumber.startsWith("+")) {
        phoneNumber = phoneNumber.replace("+", "");
    }
       
        List<String> vanityNumbers = getVanityNumbers(phoneNumber);
          table.putItem(new Item()
                .withPrimaryKey("customernumber", phoneNumber)
                .withList("VanityNumbers", vanityNumbers)
                .withNumber("timestamp", currentTimestamp)
                .withString("staticPartition", "all"));

                Map<String, String> outputMap = new HashMap<>();
                outputMap.put("VanityNumber1", vanityNumbers.get(0));
                outputMap.put("VanityNumber2", vanityNumbers.get(1));
                outputMap.put("VanityNumber3", vanityNumbers.get(2));
                
                response.setParameters(outputMap);
                return response;

    }

   private List<String> getVanityNumbers(String phoneNumber) {
        List<String> results = new ArrayList<>();
        generateVanityNumbers(phoneNumber, 0, new StringBuilder(), results);
        
        results.sort(Comparator.comparingDouble(this::scoreVanity).reversed());
        return results.size() > 5 ? results.subList(0, 5) : results;
    }
/*
 * The function utilizes a recursive approach to generate all possible combinations of vanity numbers based on the input phone number.
At each step of the recursion, the function appends characters corresponding to the current digit in the phone number (or the digit itself if no corresponding characters are found) to the current string, and then calls itself recursively with the next index.
After the recursive call, the appended character is removed from the current string to backtrack and explore other possibilities.
 */
    private void generateVanityNumbers(String phoneNumber, int index, StringBuilder current, List<String> results) {
        
        if(results.size()==MAX_SIZE){
            return;
        }; //prevent early memory bloat/overload    
        
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

    
    // the prepend method adds a 1800 prefix to the resulting vanity number
    
      private String prepend(String vanity) {
    return "1800" + vanity;
   }

        private double scoreVanity(String s) {
        double score = 0;
        score += countVowels(s) * 1.5;
        if (containsRealWord(s)) score += 5;
        return score;
    }

  

  
  // this function checks to see if generated vanity numbers contain a real english word composed of minimum 3 character
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

    //The countVowels method counts the number of vowels in a string, used for sorting.
    /*
     * After generating all possible vanity numbers, the function sorts them based on the count of vowels in each string.
Vanity numbers with a higher count of vowels are considered more desirable as they tend to be more memorable and easier to pronounce.
Sorting by vowel count ensures that the most appealing vanity numbers are presented first to the user.
     */
    
    private long countVowels(String s) {
        return s.chars().filter(c -> "AEIOU".indexOf(c) != -1).count();
     }
  }


// class cConnectResponse puts all 3 of the best 5 vanity numbers stored in dynamoDB into a map, and says the 3 vanity numbers as returned ConnectResponse 
class ConnectResponse {
    private Map<String, String> parameters;

    public ConnectResponse() {}

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
