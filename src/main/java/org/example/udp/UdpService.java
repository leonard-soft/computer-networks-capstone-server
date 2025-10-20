package org.example.udp;

import com.google.gson.Gson;
import org.example.dto.*;
import org.example.encrypt.GenerateAES;
import org.example.encrypt.encryptData;
import org.example.logs.ManageLogs;
import org.example.rsa.GenerateRSAKeys;
import org.example.service.RoomService;
import org.example.tcp.TcpService;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class UdpService {

    private final DatagramSocket socket;
    private final ManageLogs manageLogs;
    private final Map<Integer, PlayerConnection> connections = new HashMap<>();
    private final GenerateAES aes;
    private final RoomService roomService;
    private final GenerateRSAKeys generateRSAKeys;
    private boolean running;

    public UdpService() throws SocketException {
        this.socket = new DatagramSocket(9876); // Using a fixed port for predictability
        this.manageLogs = new ManageLogs();
        this.aes = new GenerateAES();
        this.roomService = new RoomService();
        this.generateRSAKeys= new GenerateRSAKeys();
    }

    public void listen() {
        running = true;
        manageLogs.saveLog("INFO", "UDP service is now listening on port 9876");
        byte[] buffer = new byte[4096]; // Buffer for incoming data

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // Blocks until a packet is received

                // Deserialize the packet data back to a DataTransferDTO
                DataTransferDTO data = bytesToObject(packet.getData());
                if (data != null) {
                    // Process the received data
                    processPacket(data);
                }
            } catch (IOException e) {
                if (running) {
                    manageLogs.saveLog("ERROR", "UDP listen error: " + e.getMessage());
                }
            }
        }
    }

    private DataTransferDTO bytesToObject(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (DataTransferDTO) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            manageLogs.saveLog("ERROR", "Error deserializing UDP packet: " + e.getMessage());
            return null;
        }
    }

    private GameSession findGameSessionByPlayerId(int playerId) {
        for (GameSession session : TcpService.activeGameSessions.values()) {
            if (session.getPlayer1Id() == playerId || session.getPlayer2Id() == playerId) {
                return session;
            }
        }
        return null;
    }

    private void processPacket(DataTransferDTO data) {
        GameSession session = findGameSessionByPlayerId(data.getIdPlayer());

        if (session == null) {
            manageLogs.saveLog("WARN", "UDP packet from player " + data.getIdPlayer() + " with no active game session.");
            return;
        }

        switch (data.getEventType()) {
            case "PLAYER_MOVE":
                int otherPlayerId = (data.getIdPlayer() == session.getPlayer1Id()) ? session.getPlayer2Id() : session.getPlayer1Id();
                sendPacket(data, otherPlayerId);
                break;

            case "PLAYER_ATTACK":
                manageLogs.saveLog("INFO", "Player " + data.getIdPlayer() + " attacked in game " + session.getGameId());
                int targetPlayerId = (data.getIdPlayer() == session.getPlayer1Id()) ? session.getPlayer2Id() : session.getPlayer1Id();

                int damage = 10; // Fixed damage
                boolean gameOver = session.applyDamage(targetPlayerId, damage);

                Map<String, Object> payload = new HashMap<>();
                payload.put("targetId", targetPlayerId);
                payload.put("newHealth", (targetPlayerId == session.getPlayer1Id()) ? session.getPlayer1Health() : session.getPlayer2Health());
                payload.put("isGameOver", gameOver);

                DataTransferDTO damageResultDto = new DataTransferDTO(0, "DAMAGE_DEALT", payload);
                sendToBothPlayers(session, damageResultDto);

                if (gameOver) {
                    manageLogs.saveLog("INFO", "Game over for game ID: " + session.getGameId());
                    TcpService.activeGameSessions.remove(session.getGameId());
                }
                break;

            default:
                manageLogs.saveLog("WARN", "Unknown UDP event type: " + data.getEventType());
                break;
        }
    }

    private void sendPacket(DataTransferDTO data, int playerId) {
        PlayerConnection player = connections.get(playerId);
        if (player == null) {
            manageLogs.saveLog("WARN", "No connection info to send UDP packet to player: " + playerId);
            return;
        }

        try {
            Gson gson = new Gson();
            encryptData aesEncryptor = new encryptData();

            //  Obtener las llaves
            Keys keyPublic = TcpService.getKey(player.getIp());
            KeysAES aesKeys = roomService.findKeys(player.getIp());

            if (keyPublic == null || aesKeys == null) {
                manageLogs.saveLog("ERROR", "Missing keys for UDP encryption for player: " + playerId);
                return;
            }

            //  Enviar las llaves AES solo una vez
            if (!player.isAesKeysSent()) {
                String combinedAES = aesKeys.getKey() + ":" + aesKeys.getIv();
                String encryptedAES = generateRSAKeys.encryptData(
                        combinedAES,
                        generateRSAKeys.convertPublicKeyClient(keyPublic.getPublicUserKey())
                );

                byte[] aesBuffer = encryptedAES.getBytes();
                DatagramPacket aesPacket = new DatagramPacket(
                        aesBuffer,
                        aesBuffer.length,
                        player.getIp(),
                        player.getPort()
                );
                socket.send(aesPacket);

                player.setAesKeysSent(true); // Marca como enviadas las llaves
                manageLogs.saveLog("INFO", "AES keys sent to player " + playerId);
            }

            // Encriptar los datos con AES (ya que el cliente ya las tiene)
            String json = gson.toJson(data);
            String encryptedData = aesEncryptor.encrypt(json, player.getIp());

            byte[] dataBuffer = encryptedData.getBytes();
            DatagramPacket dataPacket = new DatagramPacket(
                    dataBuffer,
                    dataBuffer.length,
                    player.getIp(),
                    player.getPort()
            );
            socket.send(dataPacket);

            manageLogs.saveLog("INFO", "Encrypted UDP data sent to player: " + playerId);

        } catch (Exception e) {
            manageLogs.saveLog("ERROR", "Error sending UDP packet to player " + playerId + ": " + e.getMessage());
        }
    }



    private void sendToBothPlayers(GameSession session, DataTransferDTO data) {
        sendPacket(data, session.getPlayer1Id());
        sendPacket(data, session.getPlayer2Id());
    }

    private byte[] objectToBytes(Object object) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(object);
            return outputStream.toByteArray();
        } catch (IOException e) {
            manageLogs.saveLog("ERROR", "Error serializing object: " + e.getMessage());
            return new byte[0];
        }
    }

    public void savePlayer(PlayerConnection playerConnection) {
        if (playerConnection == null) {
            manageLogs.saveLog("WARN", "Attempted to save null PlayerConnection");
            return;
        }
        connections.put(playerConnection.getPlayerId(), playerConnection);
        manageLogs.saveLog("INFO", "Player UDP connection details saved for ID: " + playerConnection.getPlayerId());
    }

    public void close() {
        running = false;
        socket.close();
        manageLogs.saveLog("INFO", "UDP socket closed successfully.");
    }
}
