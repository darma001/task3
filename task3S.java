import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    // List to store all client threads
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        int port = 12345;  // Port number for the server to listen on
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            // Accept clients and assign each one a new thread
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all connected clients
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Add client to the list of clients
    public static void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    // Remove client from the list
    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Set up input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Add client to the server's list
            ChatServer.addClient(this);
            System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                System.out.println("Received: " + message);
                ChatServer.broadcastMessage(message, this); // Broadcast message to other clients
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Cleanup and close connection
                ChatServer.removeClient(this);
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Send a message to the client
    public void sendMessage(String message) {
        out.println(message);
    }
}
