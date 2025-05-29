package org.mobi.forexapplication.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Map;

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

    @ExceptionHandler(GlobalCustomException.class)
    public ResponseEntity<Map<String, String>> handleGlobalCustomException(GlobalCustomException ex) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "A null pointer exception occurred."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "An unexpected error occurred."));
    }
}
