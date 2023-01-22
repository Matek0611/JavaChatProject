package Forms;

import ClientServer.ChatHistoryItem;
import ClientServer.Client;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientMainForm extends JFrame {
    private JPanel mainPanel;
    private JButton sendButton;
    private JTextField sendTextBox;
    private JTextArea chatHistoryLines;
    private JButton logoutButton;
    private JTextArea usersList;
    private JLabel userNameLabel;
    private Client client;

    public ClientMainForm() {
        super("Czat");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(600, 500));
        this.setResizable(false);
        this.setContentPane(mainPanel);
        this.pack();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.chatHistoryLines.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.usersList.setFont(this.chatHistoryLines.getFont());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                client.closeSession();
            }
        });

        logoutButton.addActionListener(e -> System.exit(0));

        sendButton.addActionListener(e -> {
            String msg = sendTextBox.getText().trim();
            ChatHistoryItem item = new ChatHistoryItem(client.getLogin(), msg);

            if (msg.isBlank()) return;

            client.sendMessage(msg);
            chatHistoryLines.append(item + "\n");
            sendTextBox.setText("");
        });
    }

    public void showForm() {
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void hideForm() {
        this.setVisible(false);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void clearData() {
        chatHistoryLines.setText("");
        sendTextBox.setText("");
        usersList.setText("");
        userNameLabel.setText("");
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JTextField getSendTextBox() {
        return sendTextBox;
    }

    public JTextArea getChatHistoryLines() {
        return chatHistoryLines;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JTextArea getUsersList() {
        return usersList;
    }

    public JLabel getUserNameLabel() {
        return userNameLabel;
    }
}
