package com.yr.mafia;

import java.io.Serializable;

/**
 * Created by Yeonil on 4/1/14.
 */
public class CommunicationMessage implements Serializable{
    private String name;
    private String message;

    public CommunicationMessage(String name, String message) {
        this.name = name;
        this.message = message;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
