/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mafiatrial_1_2;

import java.io.Serializable;

/**
 *
 * @author eilr__000
 */
public class CommunicationMessage implements Serializable {
    private String name;
    private String message;
    
    public CommunicationMessage(String the_name, String the_message) {
        this.name = the_name;
        this.message = the_message;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String the_name) {
        this.name = the_name;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String the_message) {
        this.message = the_message;
    }
}
