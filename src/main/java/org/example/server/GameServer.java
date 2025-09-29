package org.example.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {

    private static final int TCP_SOCKET_PORT = 5000;
    private static final int UPD_SOCKET_PORT = 6000;

    private static final Map<String, String> loggedPlayers = new ConcurrentHashMap<>();

    
}
