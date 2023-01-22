package ClientServer;

import Forms.ClientLoginRegisterForm;
import Forms.ClientMainForm;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
    private final UserDataBase userDataBase;
    private final ClientMainForm clientMainForm;
    private final ClientLoginRegisterForm clientLoginRegisterForm;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String login = "";
    private String password = "";
    private boolean closed = false;
    private Thread listening;

    public static String HOST_NAME = "127.0.0.1";

    public Client(ClientMainForm clientMainForm, ClientLoginRegisterForm clientLoginRegisterForm) {
        this.userDataBase = new UserDataBase();
        this.clientMainForm = clientMainForm;
        this.clientLoginRegisterForm = clientLoginRegisterForm;
    }

    public UserDataBase getUserDataBase() {
        return userDataBase;
    }

    public ClientMainForm getClientMainForm() {
        return clientMainForm;
    }

    public ClientLoginRegisterForm getClientLoginRegisterForm() {
        return clientLoginRegisterForm;
    }

    public String getLogin() {
        return login;
    }

    public void start() {
        clientLoginRegisterForm.showForm();
    }

    public void log(Object message) {
        System.out.println(message + "\n");
    }

    public void openSession(String name, String pswd) {
        try {
            socket = new Socket(HOST_NAME, Server.PORT);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            login = name;
            password = pswd;
            closed = false;

            clientMainForm.clearData();

            bufferedWriter.write(login);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.write(password);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String response = bufferedReader.readLine();
            response = ("" + response).trim();

            if (!response.equals("LOGIN_SUCCESS"))
                throw new InvalidUserException("Niepoprawne dane użytkownika.");

            clientMainForm.getUserNameLabel().setText(login);

            clientMainForm.showForm();
            clientLoginRegisterForm.hideForm();

            listening = listenServer();
            listening.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Wystąpił błąd: \n" + e);
            closeSession();
        }
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            log("Wystąpił błąd: \n" + e);
            closeSession();
        }
    }

    public void closeSession() {
        if (closed) return;
        closed = true;

        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            log("Wystąpił błąd: \n" + e);
        }

        socket = null;
        bufferedWriter = null;
        bufferedReader = null;
        login = "";
        password = "";

        clientLoginRegisterForm.dispose();
    }

    public boolean isSessionOpen() {
        return socket != null && socket.isConnected();
    }

    public Thread listenServer() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                String response;

                while (socket.isConnected()) {
                    try {
                        response = bufferedReader.readLine();
                        response = response.trim();

                        log(response);

                        if (response.startsWith("@U:")) {
                            String[] userlist = response.substring(3).trim().split(",");

                            StringBuilder sb = new StringBuilder();
                            sb.append("Aktywni użytkownicy: \n");
                            for (String user: userlist) {
                                sb.append(" - ").append(user).append("\n");
                            }

                            clientMainForm.getUsersList().setText(sb.toString());
                        } else {
                            clientMainForm.getChatHistoryLines().append(response + "\n");
                        }
                    } catch (Exception e) {
                        log("Wystąpił błąd: \n" + e);
                        closeSession();
                        break;
                    }
                }

                System.out.println("Koniec");
            }
        });
    }
}
