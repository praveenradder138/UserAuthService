package com.project.userauthservice.exceptions;

public class UnverifiedEmailException extends Exception{
    public UnverifiedEmailException(String message){
        super(message);
    }
}
