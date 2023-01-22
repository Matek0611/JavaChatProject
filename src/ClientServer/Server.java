package ClientServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static int PORT = 8098;
    private ServerSocket serverSocket;
    private final UserDataBase userDataBase;

    public Server() {
        this.serverSocket = null;
        this.userDataBase = new UserDataBase();
    }

    public UserDataBase getUserDataBase() {
        return userDataBase;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void start() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            this.userDataBase.load();

            log("Serwer został uruchomiony.");

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                this.userDataBase.load();

                Thread thread = (new Thread(new ServerConnection(this, socket)));
                thread.start();
            }
        } catch (Exception e) {
            log(e.toString());
            close();
        }
    }

    public void close() {
        try {
            if (serverSocket != null) {
                if (!serverSocket.isClosed())
                    serverSocket.close();
                serverSocket = null;
            }

            log("Serwer nie jest już aktywny.");
        }
        catch (Exception e) {
            log(e.toString());
        }
    }

    public boolean isConnected() {
        return serverSocket != null && !serverSocket.isClosed();
    }

    public String getActiveUserList() {
        StringBuilder list = new StringBuilder();

        for (ServerConnection connection: ServerConnection.connections) {
            if (connection.isConnected())
                list.append(connection.getLogin()).append(",");
        }

        if (list.toString().endsWith(","))
            list.deleteCharAt(list.length()-1);

        return list.toString();
    }

    public void log(Object message) {
        System.out.println(message + "\n");
    }
}
