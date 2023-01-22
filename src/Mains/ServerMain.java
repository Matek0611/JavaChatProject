package Mains;

import ClientServer.Server;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
