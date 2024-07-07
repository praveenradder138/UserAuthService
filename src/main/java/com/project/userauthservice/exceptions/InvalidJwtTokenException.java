package com.project.userauthservice.exceptions;

public class InvalidJwtTokenException extends Exception{

    public InvalidJwtTokenException(String message){
        super(message);
    }
}
