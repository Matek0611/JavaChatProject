package ClientServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnection implements Runnable {
    private Server server;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String login;
    private String password;

    public static final ArrayList<ServerConnection> connections = new ArrayList<>();

    public ServerConnection(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;

            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            server.log("Nowy użytkownik próbuje dołączyć do czatu...");

            login = bufferedReader.readLine().trim();
            User user = server.getUserDataBase().getUser(login);
            password = bufferedReader.readLine().trim();

            if (!user.getPassword().equals(password)) {
                bufferedWriter.write("ACCESS_DENIED");
                bufferedWriter.newLine();
                bufferedWriter.flush();

                server.log("Błędne hasło.");
                closeAll();
            }

            bufferedWriter.write("LOGIN_SUCCESS");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            connections.add(this);

            sendMessage("@U:" + server.getActiveUserList(), "$SERVER");
            sendMessage("Użytkownik " + login + " dołączył do czatu.", login);
        } catch (Exception e) {
            server.log("Użytkownik nie mógł się połączyć: " + e );
            closeAll();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                String message = bufferedReader.readLine();
                sendMessage(message.trim(), login);
            } catch (Exception e) {
                closeAll();
                break;
            }
        }
    }

    public void sendMessage(String message, String who) {
        ChatHistoryItem item = new ChatHistoryItem(who, message);

        server.log(connections.size());

        for (ServerConnection connection: connections) {
            if ((connection.login.equals(this.login) && !item.toString().startsWith("@U:"))) continue;

            try {
                connection.bufferedWriter.write(item.toString());
                connection.bufferedWriter.newLine();
                connection.bufferedWriter.flush();

                server.log("Wysłano wiadomość do " + connection.login + ": " + item);
            } catch (Exception e) {
                closeAll();
                break;
            }
        }
    }

    public void closeAll() {
        if (!connections.contains(this)) return;

        connections.remove(this);

        sendMessage("Użytkownik " + login + " wyszedł z czatu.", login);
        sendMessage("@U:" + server.getActiveUserList(), "$SERVER");

        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            server.log("Wystąpił błąd przy rozłączaniu użytkownika " + login + ": \n" + e);
        }
    }

    public String getLogin() {
        return login;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }
}
