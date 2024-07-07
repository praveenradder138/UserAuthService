package com.project.userauthservice.exceptions;

public class ExistingUserException extends Exception{

    public ExistingUserException(String message){
        super(message);
    }
}
