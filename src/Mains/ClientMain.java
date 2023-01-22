package Mains;

import ClientServer.Client;
import Forms.ClientLoginRegisterForm;
import Forms.ClientMainForm;

public class ClientMain {
    public static void main(String[] args) {
        ClientMainForm clientMainForm = new ClientMainForm();
        ClientLoginRegisterForm clientLoginRegisterForm = new ClientLoginRegisterForm();

        Client client = new Client(clientMainForm, clientLoginRegisterForm);
        clientMainForm.setClient(client);
        clientLoginRegisterForm.setClient(client);

        client.start();
    }
}
