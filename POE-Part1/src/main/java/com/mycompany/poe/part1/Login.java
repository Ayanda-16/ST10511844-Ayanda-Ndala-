
package com.mycompany.poe.part1;

public class Login{
	
    public Login() {
        
    }
    
    public boolean  checkUserName(String username){
        
        return username.contains("_") && username.length() <= 5;
    }
    
    public boolean  checkPasswordComplexity(String password){
        
	String capital = ".*[A-Z].*";//Checks capital letters
	String small = ".*[a-z].*";//Checks small letters
	String special = ".*[!@#$%^&*(),.?\":{}|<>].*";//Check special characters
	String digit = ".*\\d.*";//Check digit characters
        return password.length() >= 8 && password.matches(capital) && password.matches(small) && password.matches(digit) &&password.matches(special);
    }
    
    public boolean  checkCellPhoneNumber(String phone){
        
	String saCode = "+27";
	String firstThreeChars = phone.substring(0, 3); // Gets characters from index 0 to 2 (inclusive)
        
	int fourthDigit = Character.getNumericValue(phone.charAt(3));//Gets and convert the fourth digit
        return phone.length() <= 12 && firstThreeChars.equals(saCode) && fourthDigit >= 6 && fourthDigit <= 8;
    }
    
    
    public String registerUser(String username, String password, String phone){
        
	boolean  validatePhone = checkCellPhoneNumber(phone);
	boolean  validateUsername = checkUserName(username);
	boolean  validatePassword = checkPasswordComplexity(password);
        
        if(validatePhone == true && validateUsername == true && validatePassword == true){
          return "User is successfully registered";
        }else{
          return "User registration failed!!!!!";
        }
    }
    
    
    public boolean loginUser(String username, String password){
	boolean  validUsername = checkUserName(username);
	boolean  validPassword = checkPasswordComplexity(password);
        
        return validUsername == true && validPassword == true;
    }
    
    
    public String returnLoginStatus(String username, String password){
	boolean  validUsername = checkUserName(username);
	boolean  validPassword = checkPasswordComplexity(password);
        
	if(validUsername == true && validPassword == true){
	    return "A successful login";
	}else{
	    return "A failed login";
	}
    }
}