/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mafiatrial_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author eilr__000
 */
public class Communicator_1 implements MessageListener {
    public static final String TOPIC01 = "jms/Topic01"; //pub
    public static final String TOPIC02 = "jms/Topic02"; //sub
    
    @Override
    public void onMessage(Message the_msg) {
        System.out.println("2");
        try {//*/
            ObjectMessage objectMessage = (ObjectMessage) the_msg;
            CommunicationMessage communicationMessage = (CommunicationMessage) objectMessage.getObject();
            System.out.print("Sender: " + communicationMessage.getName());
            System.out.print(" | Message: " + communicationMessage.getMessage());
        } catch (JMSException ex) {
            ex.printStackTrace();
        }//*/
    }
    
    public void subscribe(TopicConnection the_topicConnection, Topic the_topic) throws JMSException {
        TopicSession subscribeSession = the_topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber topicSubscriber = subscribeSession.createSubscriber(the_topic);
        topicSubscriber.setMessageListener(this);
    }
    
    public void publish(TopicConnection the_topicConnection, Topic the_topic) throws IOException, JMSException {
        TopicSession publishSession = the_topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicPublisher topicPublisher = publishSession.createPublisher(the_topic);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Please enter your username:");
        String username = reader.readLine();
        String message = null;
        ObjectMessage objectMessage = null;
        while(true) {
            message = reader.readLine();
            if (message.equalsIgnoreCase("exit")) {
                the_topicConnection.close();
                System.exit(0);
            } else {
                objectMessage = publishSession.createObjectMessage();
                objectMessage.setObject(new CommunicationMessage(username, message));
                topicPublisher.publish(objectMessage);
            }
        }
    }
    
    public static void main(String[] args) throws JMSException, NamingException, IOException {
        Communicator_1 comm1 = new Communicator_1();
        Context initialContext = comm1.getInitialContext();
        Topic topic01 = (Topic) initialContext.lookup(comm1.TOPIC01);
        Topic topic02 = (Topic) initialContext.lookup(comm1.TOPIC02);
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) initialContext.lookup("GFConnectionFactory");
        TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();
        topicConnection.start();
        comm1.subscribe(topicConnection, topic02);
        comm1.publish(topicConnection, topic01);
    }
    
    public static Context getInitialContext() throws JMSException, NamingException {
        Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        properties.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
        properties.setProperty("java.naming.provider.url", "iiop://localhost:8080");
        return new InitialContext(properties);
    }
}
