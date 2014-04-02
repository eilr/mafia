package com.yeonilnrichard.mafia;

import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yeonil on 4/1/14.
 */

/**
 * ServerEndpoint defines a new endpoint – the value specifies the URL path and allows PathParams as we’re used know from JAX-RS.
 * value=”/chat/{room}” allows the client to open a websocket connection to an URL
 * The value in curly braces may be injected as a path parameter in the lifecycle callback methods in the endpoint using javax.websocket.server.PathParam
 * we’re specifying an encoder and a decoder class. We need this because we’re using a custom DTO object to pass our chat data between server and client
 *
 * When a client opens the first connection to the server, he passes the chat-room he wants to enter as a path-parameter and we’re storing this value in the user properties map using session.getUserProperties()
 * when a chat participant posts a new message over the tcp connection to the server, we’re iterating over all open session, and each session that is assigned to the room the session/message is bound to, receives the decoded and re-encoded message
 * If we wanted to send a simple text or binary message we could have used session.getBasicRemote().sendBinary() or session.getBasicRemote().sendText()
 */
@ServerEndpoint(value = "/chat/{room}", encoders = ChatMessageEncoder.class, decoders = ChatMessageDecoder.class)
public class ChatEndpoint {
    private final Logger log  = Logger.getLogger(getClass().getName());

    @OnOpen
    public void open(final Session session, @PathParam("room") final String room) {
        log.info("session opened and bound to room: " + room);
        session.getUserProperties().put("room", room);
    }

    @OnMessage
    public void onMessage(final Session session, final ChatMessage chatMessage) {
        String room = (String) session.getUserProperties().get("room");
        try {
            for (Session s : session.getOpenSessions()) {
                if(s.isOpen() && room.equals(s.getUserProperties().get("room"))) {
                    s.getBasicRemote().sendObject(chatMessage);
                }
            }
        } catch (IOException | EncodeException e) {
            log.log(Level.WARNING, "onMessage failed", e);
        }

    }
}
