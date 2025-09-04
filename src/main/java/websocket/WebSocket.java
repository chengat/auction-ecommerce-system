package websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.*;
import javax.websocket.server.*;

@ServerEndpoint("/auction/{auctionId}")
public class WebSocket {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session, @PathParam("auctionId") String auctionId) {
    	System.out.println("Connection request for " + auctionId);
        session.getUserProperties().put("auctionId", auctionId);
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("auctionId") String auctionId) {
        // Handle incoming WebSocket messages (e.g., bid updates)
        broadcastToAuctionParticipants(auctionId, message);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        throwable.printStackTrace();
    }

    public static void broadcastAuctionUpdate(String auctionId, String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                String sessionAuctionId = (String) session.getUserProperties().get("auctionId");
                if (auctionId.equals(sessionAuctionId)) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void broadcastToAuctionParticipants(String auctionId, String message) {
        broadcastAuctionUpdate(auctionId, message);
    }
}