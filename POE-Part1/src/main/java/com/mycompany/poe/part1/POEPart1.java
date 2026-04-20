/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.poe.part1;

import java.util.Scanner;

/**
 *
 * @author anomp
 */
public class POEPart1 {
   
    public static void main(String[] args){
        
	Scanner input = new Scanner(System.in);
                
                
	String firstnam, lastnam,usernam, passwor, phon;
        
	//Prompt and Receiving user inputs
        System.out.println("--------Register---------");
        System.out.print("Enter First Name: ");
        firstnam = input.nextLine();
        System.out.print("Enter Last Name: ");
        lastnam = input.nextLine();
	System.out.print("Enter Username: ");
	usernam = input.nextLine();
	System.out.print("Enter Password: ");
	passwor = input.nextLine();
	System.out.print("Enter Phone Number (starting with South African international code (+27)): ");
	phon = input.nextLine();
		
	Login login = new Login();//Create and instantiate Login object
        
	boolean  validatePhone = login.checkCellPhoneNumber(phon);
	boolean  validateUsername = login.checkUserName(usernam);
	boolean  validatePassword = login.checkPasswordComplexity(passwor);
        
        //Checks and validate username
	if(validateUsername == true){
	    System.out.println("Username successfully captured.");
	}else{
           System.out.println("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.");
	}
		
        
	if(validatePassword == true){
	    System.out.println("Password  successfully captured.");
	}else{
	    System.out.println("Password  is not correctly formatted; please ensure that the password contains at least eight characters, a capital and small letter, a number, and a special character.");
        }
        
	
        //Checks and validate phone number
	if(validatePhone == true){
	    System.out.println("Cell phone number successfully added.");
	}else{
	   System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
	}
        
         if(validateUsername == true && validatePassword == true && validatePhone == true ){
             
                System.out.println("------------Login--------------");
                System.out.println("enter username : ");
                String loginUsername = input.nextLine();
                System.out.println("enter Password : ");
                String loginPassword = input.nextLine();
                
                if (loginUsername.contains(usernam) && loginPassword.contains(passwor)){
                    System.out.println("welcome back, " + firstnam + "it is great to see you again" );
                }else {
                    System.out.println("Wrong credintials entered");
                }

        }else{
             System.out.println("Registration Failed");
         }
	}
    

}