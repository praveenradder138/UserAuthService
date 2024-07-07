package com.project.userauthservice.exceptions;

public class EmailVerificationTokenException extends Exception{
    public EmailVerificationTokenException(String message){
        super(message);
    }
}
