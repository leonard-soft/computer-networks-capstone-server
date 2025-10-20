package org.example.tcp;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.dto.*;
import org.example.encrypt.GenerateAES;
import org.example.encrypt.encryptData;
import org.example.logs.ManageLogs;
import org.example.service.RoomService;
import org.example.service.UserService;

import org.example.entity.Player;
import org.example.jpa.Queries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.example.udp.UdpService;

public class TcpService {

    private final int port;
    private final UdpService udpService;
    private static final Map<String, OutputStream> connectedClients = new ConcurrentHashMap<>();
    public static final Map<Integer, GameSession> activeGameSessions = new ConcurrentHashMap<>();
    private final ManageLogs manageLogs = new ManageLogs();
    private final GenerateAES generateAES = new GenerateAES();;

    /**
     * A simple constructor to initialize the tcp
     * service protocol.
     *
     * @param port integer value that represents port
     */
    public TcpService(int port, UdpService udpService) {
        this.port = port;
        this.udpService = udpService;
    }

    /**
     * Sends a JSON-serialized message to a specific user if they are connected.
     * Looks up the user's output stream from the connectedClients map and sends the payload.
     * If the send fails, it assumes the client has disconnected and removes them from the map.
     *
     * @param username The username of the recipient.
     * @param payload  The object to be sent as the message payload.
     */
    private void sendMessageToUser(String username, Object payload) {
        OutputStream out = connectedClients.get(username);
        if (out != null) {
            try {
                String jsonResponse = new Gson().toJson(payload) + "\n";
                out.write(jsonResponse.getBytes());
                out.flush();
                manageLogs.saveLog("INFO", "Sent message to " + username + ": " + jsonResponse.trim());
            } catch (IOException e) {
                manageLogs.saveLog("ERROR", "Failed to send message to " + username + ", removing from connected clients: " + e.getMessage());
                connectedClients.remove(username);
            }
        } else {
            manageLogs.saveLog("WARN", "Could not send message: User " + username + " not found among connected clients.");
        }
    }

    /**
     * this function initialize the tcp service
     * and listen the commands from the client.
     *
     * @throws Exception for ServerSocket Mistakes
     */
    public void run() throws Exception {
        ServerSocket tcpSocket = new ServerSocket(this.port);
        manageLogs.saveLog("INFO", "TCP socket: listening in port " + this.port);
        generateAES.createKeysRandom();
        while (true) {
            Socket client = tcpSocket.accept();
            manageLogs.saveLog("INFO", "Client connected: " + client.getInetAddress());
            String key = generateAES.getKey();
            String iv = generateAES.getIv();
            String dataSend = key + ":" + iv;

            OutputStream outKey = client.getOutputStream();
            String jsonResponseKey = new Gson().toJson(dataSend) + "\n";
            outKey.write(jsonResponseKey.getBytes());
            outKey.flush();

            manageLogs.saveLog("INFO", "Sent server public key to client: " + client.getInetAddress());


            new Thread(() -> {
                String username = null;
                OutputStream out = null;
                try {
                    out = client.getOutputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    Gson gson = new Gson();
                    UserService userService = new UserService();
                    RoomService roomService = new RoomService();

                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            if (username != null) {
                                manageLogs.saveLog("INFO", "Client " + username + " disconnected.");
                                // userService.updateUserState(username, "offline");
                                connectedClients.remove(username);

                                // Notify UdpService to remove the player
                                Player player = new Queries().findPlayerByUsername(username);
                                if (player != null) {
                                    udpService.removePlayer(player.getUserId());
                                }
                            }
                            break;
                        }

                        Request req = gson.fromJson(line, Request.class);
                        manageLogs.saveLog("INFO", "Request received: " + req.getType() + " from " + (username != null ? username : "unknown user"));
                        switch (req.getType()) {
                            case "register":
                                RequestPayload requestPayload = gson.fromJson(gson.toJson(req.getPayload()), RequestPayload.class);
                                userService.registerUser(requestPayload);

                                try {
                                    String message = "register completed succesfully";
                                    RegisterResponseDTO register = new RegisterResponseDTO(true, message);
                                    String jsonResponse = gson.toJson(register) + "\n";
                                    out.write(jsonResponse.getBytes());
                                    out.flush();
                                } catch (Exception e) {
                                    RegisterResponseDTO errorResponse = new RegisterResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    out.write(jsonError.getBytes());
                                    out.flush();
                                }
                                break;
                            case "login":
                                try {
                                    String message;
                                    System.out.println("REQUEST TYPE: LOGIN");
                                    requestPayload = gson.fromJson(gson.toJson(req.getPayload()), RequestPayload.class);
                                    if (requestPayload == null || requestPayload.username == null) {
                                        throw new IllegalArgumentException("Invalid payload: Username is missing");
                                    }
                                    boolean success = userService.loginUser(requestPayload);
                                    if (success) {
                                        message = "Login successful";
                                        username = requestPayload.username;
                                        userService.updateUserState(username, "online");
                                        connectedClients.put(username, out);
                                        manageLogs.saveLog("INFO", "User " + username + " logged in and added to connected clients.");
                                    } else {
                                        message = "Invalid credentials";
                                    }
                                    LoginResponseDTO response = new LoginResponseDTO(success, message);
                                    String jsonResponse = gson.toJson(response) + "\n";
                                    sendEncryptedData(client.getInetAddress(), jsonResponse);
                                } catch (JsonSyntaxException | IllegalArgumentException e) {
                                    manageLogs.saveLog("ERROR", "Login error: " + e.getMessage());
                                    LoginResponseDTO errorResponse = new LoginResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    sendEncryptedData(client.getInetAddress(), jsonError);
                                }
                                break;
                            case "get_online_users":
                                try {
                                    List<String> onlineUsers = userService.getOnlineUsers();
                                    ConnectedUsersResponseDTO response = new ConnectedUsersResponseDTO(onlineUsers.toArray(new String[0]));
                                    String jsonResponse = gson.toJson(response) + "\n";
                                    sendEncryptedData(client.getInetAddress(), jsonResponse);
                                } catch (RuntimeException e) {
                                    manageLogs.saveLog("ERROR", "Error getting online users: " + e.getMessage());
                                    ConnectedUsersResponseDTO errorResponse = new ConnectedUsersResponseDTO(new String[0]);
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    sendEncryptedData(client.getInetAddress(), jsonError);
                                }
                                break;

                            case "create_game":
                                try {
                                    if (username == null) throw new IllegalArgumentException("Must be logged in");
                                    GameDTO game = roomService.createGameAndRegisterHost(username);
                                    RegisterResponseDTO response = new RegisterResponseDTO(true, "Game created: " + game.getGame_id());
                                    String jsonResponse = gson.toJson(response) + "\n";
                                    sendEncryptedData(client.getInetAddress(), jsonResponse);
                                } catch (Exception e) {
                                    manageLogs.saveLog("ERROR", "Error creating game: " + e.getMessage());
                                    RegisterResponseDTO errorResponse = new RegisterResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    sendEncryptedData(client.getInetAddress(), jsonError);
                                }
                                break;
                            case "get_active_games":
                                try {
                                    List<GameDTO> activeGames = roomService.getActiveGames();
                                    String jsonResponse = gson.toJson(activeGames) + "\n";
                                    sendEncryptedData(client.getInetAddress(), jsonResponse);
                                } catch (Exception e) {
                                    manageLogs.saveLog("ERROR", "Error getting active games: " + e.getMessage());
                                    RegisterResponseDTO errorResponse = new RegisterResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    sendEncryptedData(client.getInetAddress(), jsonError);
                                }
                                break;
                            case "SEND_INVITATION":
                                try {
                                    if (username == null) throw new IllegalStateException("User must be logged in to send invitations.");
                                    InvitationPayload invPayload = gson.fromJson(gson.toJson(req.getPayload()), InvitationPayload.class);
                                    roomService.createInvitation(invPayload.getGameId(), invPayload.getInvitedUsername());
                                    InvitationPayload notificationPayload = new InvitationPayload(username, invPayload.getInvitedUsername(), invPayload.getGameId());
                                    sendMessageToUser(invPayload.getInvitedUsername(), new NotificationDTO("GAME_INVITATION", notificationPayload));
                                } catch (Exception e) {
                                    manageLogs.saveLog("ERROR", "Error processing SEND_INVITATION: " + e.getMessage());
                                }
                                break;
                            case "DENY_INVITATION":
                                try {
                                    if (username == null) throw new IllegalStateException("User must be logged in to deny invitations.");
                                    InvitationPayload invPayload = gson.fromJson(gson.toJson(req.getPayload()), InvitationPayload.class);
                                    roomService.respondToInvitation(invPayload.getGameId(), username, false);
                                    Map<String, String> payload = Map.of("deniedBy", username, "gameId", String.valueOf(invPayload.getGameId()));
                                    sendMessageToUser(invPayload.getInviterUsername(), new NotificationDTO("INVITATION_DENIED", payload));
                                } catch (Exception e) {
                                    manageLogs.saveLog("ERROR", "Error processing DENY_INVITATION: " + e.getMessage());
                                }
                                break;
                            case "ACCEPT_INVITATION":
                                try {
                                    if (username == null) throw new IllegalStateException("User must be logged in to accept invitations.");
                                    InvitationPayload invPayload = gson.fromJson(gson.toJson(req.getPayload()), InvitationPayload.class);
                                    String inviterUsername = invPayload.getInviterUsername();
                                    int gameId = invPayload.getGameId();

                                    roomService.respondToInvitation(gameId, username, true);
                                    roomService.startGame(gameId);

                                    // Create and store the game session for the UDP service
                                    Queries queries = new Queries();
                                    Player inviter = queries.findPlayerByUsername(inviterUsername);
                                    Player invitee = queries.findPlayerByUsername(username);

                                    if (inviter != null && invitee != null) {
                                        GameSession session = new GameSession(gameId, inviter.getUserId(), invitee.getUserId());
                                        activeGameSessions.put(gameId, session);
                                        manageLogs.saveLog("INFO", "Game session created for game ID: " + gameId);
                                    } else {
                                        manageLogs.saveLog("ERROR", "Could not create game session for game ID: " + gameId + ". One or more players not found.");
                                    }

                                    Map<String, String> payload = Map.of("acceptedBy", username, "gameId", String.valueOf(gameId));
                                    NotificationDTO notification = new NotificationDTO("INVITATION_ACCEPTED", payload);

                                    sendMessageToUser(inviterUsername, notification);
                                    sendMessageToUser(username, notification);
                                } catch (Exception e) {
                                    manageLogs.saveLog("ERROR", "Error processing ACCEPT_INVITATION: " + e.getMessage());
                                    RegisterResponseDTO errorResponse = new RegisterResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    out.write(jsonError.getBytes());
                                    out.flush();
                                }
                                break;
                            default:
                                manageLogs.saveLog("WARN", "Unknown request type: " + req.getType());
                        }
                    }
                } catch (IOException | JsonSyntaxException e) {
                    if (username != null) {
                        manageLogs.saveLog("ERROR", "Error handling client " + username + ": " + e.getMessage());
                        UserService userService = new UserService();
                        userService.updateUserState(username, "offline");
                        connectedClients.remove(username);

                        // Notify UdpService to remove the player
                        Player player = new Queries().findPlayerByUsername(username);
                        if (player != null) {
                            udpService.removePlayer(player.getUserId());
                        }
                    }
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        manageLogs.saveLog("ERROR", "Error closing client socket: " + e.getMessage());
                    }
                }
            }).start();
        }
    }


    public void sendEncryptedData(InetAddress client, String data) {
        try {
            OutputStream out = connectedClients.get(client);
            if (out != null) {
                encryptData aes = new encryptData();
                String encryptedData = aes.encrypt(data);
                out.write((encryptedData + "\n").getBytes());
                out.flush();
                manageLogs.saveLog("INFO", "Data sent to client: " + client);
            }

        } catch (Exception e) {
            manageLogs.saveLog("ERROR", "Failed to send encrypted data: " + e.getMessage());
        }
    }

}
