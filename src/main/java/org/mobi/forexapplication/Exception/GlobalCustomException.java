package org.mobi.forexapplication.Exception;



public class GlobalCustomException extends RuntimeException{

    public GlobalCustomException(String message) {
        super(message);
    }

    public static GlobalCustomException UserNotFound(String username) {
        return new GlobalCustomException("User not found: " + username);
    }

    public static GlobalCustomException UserIdNotFound(Long UserID){
        return new GlobalCustomException("User Not Found: "+ UserID);
    }
}
