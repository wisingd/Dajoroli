package com.example.splitit;

public class Miscellaneous {
	
	public static String debtUpdateMessage( boolean addingOrSubtracting, int newamount, int oldamount, String name){
		
		String snew = Integer.toString(newamount);

		String string = "";

		if (addingOrSubtracting){
			if ( newamount > 0 && oldamount >= 0){
				string = name +"'s debt to you has increased to " + snew + " kr.";
			}
			else if (newamount > 0){
				string = "Your debt situation has changed! " + name + " now owes you " + snew + " kr."; 
			}
			else if(newamount == 0){
				string = "You are now even with " + name + ".";
			}
			else{
				snew = Integer.toString(Math.abs(newamount));
				string = "Your debt to " + name + " has decreased to " + snew + " kr.";
			}
		}
		else{
			if(newamount < 0 && oldamount <= 0){
				snew = Integer.toString(Math.abs(newamount));

				string = "Your debt to " + name + " has increased to " + snew + " kr.";
			}
			else if(newamount < 0){
				snew = Integer.toString(Math.abs(newamount));

				string = "Your debt situtation has changed! You now owe " + name + " " + snew + " kr."; 
			}
			else if (newamount == 0){
				string = "You are now even with " + name + ".";
			}
			else {
				string = name + "'s debt to you has decreased to " + snew + "kr.";
			}
			
		}
		return string;
	}

}