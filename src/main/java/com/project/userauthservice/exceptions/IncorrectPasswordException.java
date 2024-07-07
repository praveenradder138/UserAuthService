package com.project.userauthservice.exceptions;

public class IncorrectPasswordException extends Exception{
    public IncorrectPasswordException(String message){
        super(message);
    }
}
