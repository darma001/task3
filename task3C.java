import java.io.*;
import java.net.*;

public class ChatClient {

    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        String serverAddress = "localhost";  // The server's IP address
        int port = 12345;  // The port the server is listening on

        try (Socket socket = new Socket(serverAddress, port)) {
            // Create input and output streams for the client
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Start a thread to listen for incoming messages
            Thread listenerThread = new Thread(new MessageListener());
            listenerThread.start();

            // Send messages to the server
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);  // Send message to the server
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This thread listens for incoming messages from the server
    static class MessageListener implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("Message from server: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
