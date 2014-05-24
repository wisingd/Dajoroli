package com.example.splitit;
/**
 * A class with some miscellaneous methods.
 * @author Johannes
 *
 */
public class Miscellaneous {
	
	/**
	 * A method that returns a string which states what the new debts have resulted in.
	 * Depending on if a new debt has increased or decreased the total debt of the user to the contact
	 * different strings are written. The string also differs depending on what the total situation is
	 * with the contact.
	 * 
	 * @param addingOrSubtracting 	true if the debt has increased else false
	 * @param newamount 			the amount before the addition of the last amount
	 * @param oldamount				the amount after the addition of the last amount
	 * @param name					the name of the contact with who the debt situation has been updated
	 * @return
	 */
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
