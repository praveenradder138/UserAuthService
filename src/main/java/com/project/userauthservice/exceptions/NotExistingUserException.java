package com.project.userauthservice.exceptions;

public class NotExistingUserException extends Exception{

    public NotExistingUserException(String message){
        super(message);
    }
}
