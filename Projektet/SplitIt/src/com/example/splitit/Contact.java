package com.example.splitit;

import android.app.Application;


public class Contact{
	
	String name;
	int debt;
	
	public  Contact(String name){
		
		name = this.name;
		debt = 0;
		
	}
	
	// DANIEL TESTAR BRANCHING WIIIHOOO
	
	
	public void accumulateDebt(int debt){
		
		this.debt += debt;
		
	}
	public String getName(){
		return name;
	}
	public int getDebt(){
		return debt;
	}
	
	
}
