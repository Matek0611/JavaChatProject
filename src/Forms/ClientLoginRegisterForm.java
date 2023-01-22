package Forms;

import ClientServer.Client;
import ClientServer.InvalidUserException;
import ClientServer.User;
import ClientServer.UserDataBaseException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientLoginRegisterForm extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JButton closeButton;
    private JButton registerButton;
    private JButton LoginButton;
    private JTextField nameLoginBox;
    private JPasswordField passwordLoginBox;
    private JTextField nameRegisterBox;
    private JPasswordField passwordRegisterBox;
    private JPasswordField passwordRegisterBox2;
    private Client client;

    public ClientLoginRegisterForm() {
        super("Dołączanie do czatu");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(500, 400));
        this.setResizable(true);
        this.setContentPane(mainPanel);
        this.pack();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        closeButton.addActionListener(e -> {
            this.setVisible(false);
            client.getClientMainForm().dispose();
            this.dispose();
        });

        registerButton.addActionListener(e -> {
            try {
                client.getUserDataBase().load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Błąd podczas wczytywania bazy danych użytkowników: " + e);
                return;
            }

            if (!User.isNameValid(nameRegisterBox.getText())) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowa nazwa użytkownika.");
                return;
            }

            if (client.getUserDataBase().hasUser(nameRegisterBox.getText())) {
                JOptionPane.showMessageDialog(null, "Użytkownik o podanej nazwie jest już zarejestrowany.");
                return;
            }

            String pwd1 = String.valueOf(passwordRegisterBox.getPassword());
            String pwd2 = String.valueOf(passwordRegisterBox2.getPassword());

            if (!pwd1.equals(pwd2) || !User.isPasswordValid(pwd1)) {
                JOptionPane.showMessageDialog(null, "Podane hasło jest nieprawidłowe.");
                return;
            }

            try {
                client.getUserDataBase().addUser(nameRegisterBox.getText(), pwd1);
                client.getUserDataBase().save();

                JOptionPane.showMessageDialog(null, "Konto zostało poprawnie stworzone i dodane do bazy.");
                clearRegisterTab();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Wystąpił błąd przy dodawaniu nowego użytkownika: \n" + ex);
            }
        });

        LoginButton.addActionListener(e -> {
            String userName = nameLoginBox.getText(), userPwd = String.valueOf(passwordLoginBox.getPassword());

            if (!User.isNameValid(userName) || !User.isPasswordValid(userPwd)) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowe dane użytkownika.");
                return;
            }

            client.openSession(userName, userPwd);
        });
    }

    public void showForm() {
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void hideForm() {
        this.setVisible(false);
    }

    public void clearLoginTab() {
        nameLoginBox.setText("");
        passwordLoginBox.setText("");
    }

    public void clearRegisterTab() {
        nameRegisterBox.setText("");
        passwordRegisterBox.setText("");
        passwordRegisterBox2.setText("");
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
