import java.util.*;
public  class TestVanityNum {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		VanityNumbers vn = new VanityNumbers();
		String [] pnumbers = new String[5];
		
	  // test to see if +1 will be replaced
		
	
	  String phonenum1="+1364264895";
	  String phonenum2="+1228683451";
	  String phonenum3="+1227459427";
	  String phonenum4="+1466329844";
	  String phonenum5="+1736394334";
	  
	  pnumbers[0]= phonenum1;
	  pnumbers[1]= phonenum2;
	  pnumbers[2]= phonenum3;
	  pnumbers[3]= phonenum4;
	  pnumbers[4]= phonenum5;
	   
	  
	  
     for(int i=0; i<pnumbers.length; i++) {
		System.out.println("The 5 best vanity numbers stored in dynamoDB for " + "customer number: " +   pnumbers[i] + "\n");
		System.out.print("Based on vowel score only:" + "\t\t");System.out.print("Based on vowel/real word score:" + "\n");
		vn.showVanitynum(pnumbers[i]); 
		
		System.out.println("\n----------------------------------------------------------\n");
	   }	
		
     }
	}


