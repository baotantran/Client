import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private Socket connection;
    private DataOutputStream output;
    private DataInputStream input;
    private String message = "";
    private String serverIP;


    public Client(String host) {
        super("Bao messenger - client");
        serverIP = host;
        userText = new JTextField();
        add(userText, BorderLayout.NORTH);
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

    // setup and run the application
    public void startRunning() {
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException e) {
            System.out.println(e.getMessage());
            showMessage("\n Cliend terminate connection");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // connect to server
    private void connectToServer() throws IOException {
        showMessage("Attempting connection... \n");
        connection = new Socket(InetAddress.getByName(serverIP), 1234);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }

    // set up streams to send and receive messages
    private void setupStreams() throws IOException {
        input = new DataInputStream(connection.getInputStream());
        output = new DataOutputStream(connection.getOutputStream());
        //output.flush();
        showMessage("\n Streams are ready!");
    }

    // while chatting with server
    private void whileChatting() throws IOException {
        ableToType(true);
        do{
                message = input.readUTF();
                showMessage("\n" + message);
        } while(!message.equals("SERVER - END"));
    }

    // Close connection after finished
    private void closeConnection() {
        showMessage("\n closing connection...");
        ableToType(false);
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send message to the server
    private void sendMessage(String message) {
        try {
            output.writeUTF("CLIENT - " + message);
            output.flush();
            showMessage("\nCLIENT - " + message);
        } catch (IOException e) {
            chatWindow.append("\n can't send message");
        }
    }

    // show message in the chat window
    private void showMessage(final String message) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(message);
                    }
                }
        );
    }

    // enable the user to type
    private void ableToType(final boolean state) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(state);
                    }
                }
        );
    }
}
