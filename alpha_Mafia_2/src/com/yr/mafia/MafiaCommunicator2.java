package com.yr.mafia;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Yeonil on 4/1/14.
 */
public class MafiaCommunicator2 implements MessageListener {
    public static final String TOPIC01 = "jms/Topic02";
    public static final String TOPIC02 = "jms/Topic01";

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            CommunicationMessage communicationMessage = (CommunicationMessage)objectMessage.getObject();
            System.out.print("Sender:" + communicationMessage.getName());
            System.out.println(" | Message: " + communicationMessage.getMessage());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static Context getInitialContext() throws JMSException, NamingException {
        Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial","com.sun.enterprise.naming.SerialInitContextFactory");
        properties.setProperty("java.naming.factory.url.pkgs","com.sun.enterprise.naming");
        properties.setProperty("java.naming.provider.url", "iiop://localhost:8080");
        return new InitialContext(properties);
    }

    public void subscribe(TopicConnection topicConnection, Topic topic) throws JMSException {
        TopicSession subscribeSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber topicSubscriber = subscribeSession.createSubscriber(topic);
        topicSubscriber.setMessageListener(this);
    }

    public void publish(TopicConnection topicConnection, Topic topic) throws JMSException, IOException {
        TopicSession publishSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicPublisher topicPublisher = publishSession.createPublisher(topic);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter user name : ");
        String username = reader.readLine();
        String message = null;
        ObjectMessage objectMessage = null;
        while(true) {
            message = reader.readLine();
            if (message.equalsIgnoreCase("exit")) {
                topicConnection.close();
                System.exit(0);
            } else {
                objectMessage = publishSession.createObjectMessage();
                objectMessage.setObject(new CommunicationMessage(username, message));
                topicPublisher.publish(objectMessage);
            }
        }
    }

    public static void main(String[] args) throws JMSException, NamingException, IOException{
        MafiaCommunicator2 communicator1 = new MafiaCommunicator2();
        Context initialContext = communicator1.getInitialContext();
        Topic topic1 = (Topic)initialContext.lookup(MafiaCommunicator2.TOPIC01);
        Topic topic2 = (Topic)initialContext.lookup(MafiaCommunicator2.TOPIC02);
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) initialContext.lookup("GFConnectionFactory");
        TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();
        topicConnection.start();
        communicator1.subscribe(topicConnection, topic1);
        communicator1.publish(topicConnection, topic2);

    }
}
