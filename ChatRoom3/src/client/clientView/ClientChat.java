package client.clientView;

import client.clientControl.ClientConnection;
import util.XMLUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by tonggezhu on 6/22/17.
 */
public class ClientChat extends JFrame {

    private String username;
    private int port;
    private String ip;

    private JPanel leftJP;
    private JPanel chatJP;
    private JPanel privateChatJP;
    private JPanel msgJP;

    private JPanel rightJP;
    private JPanel userListJP;

    private JPanel buttonJP;

    private JTextArea chatJTA;
    private JTextArea privateChatJTA;
    private JTextArea msgJTA;
    private JList<String> userListJList;

    private JScrollPane chatSP;
    private JScrollPane privateChatSP;
    private JScrollPane msgSP;
    private JScrollPane userListSP;

    private JButton sendJB;
    private JButton logoutJB;

    private JComboBox clear;

    private String[] userlist = null;

    private String selection = null;

    private ClientConnection clientConnection;

    public ClientChat(ClientConnection clientConnection, String username, int port, String ip) {
        this.clientConnection = clientConnection;
        this.username = username;
        this.port = port;
        this.ip = ip;

        chatWindowInitComponents();
    }

    private void chatWindowInitComponents() {

        leftJP = new JPanel();
        leftJP.setLayout(new BoxLayout(leftJP, BoxLayout.Y_AXIS));

        rightJP = new JPanel();
        rightJP.setLayout(new BoxLayout(rightJP, BoxLayout.Y_AXIS));

        chatJP = new JPanel();
        chatJP.setBorder(BorderFactory.createTitledBorder("Public Board"));

        privateChatJP = new JPanel();
        privateChatJP.setBorder(BorderFactory.createTitledBorder("Whisper Board"));

        msgJP = new JPanel();
        msgJP.setBorder(BorderFactory.createTitledBorder("Enter message"));

        userListJP = new JPanel();
        userListJP.setBorder(BorderFactory.createTitledBorder("Online users"));

        buttonJP = new JPanel();

        chatJTA = new JTextArea();
//        chatJTA.setColumns(40);
//        chatJTA.setRows(18);
        chatJTA.setColumns(25);
        chatJTA.setRows(9);

        chatJTA.setEditable(false);

        chatJTA.setLineWrap(true);
        chatJTA.setWrapStyleWord(false);

        privateChatJTA = new JTextArea();
//        privateChatJTA.setColumns(40);
//        privateChatJTA.setRows(12);
        privateChatJTA.setColumns(25);
        privateChatJTA.setRows(6);
        privateChatJTA.setEditable(false);

        privateChatJTA.setLineWrap(true);
        privateChatJTA.setWrapStyleWord(false);

        msgJTA = new JTextArea();
//        msgJTA.setColumns(40);
//        msgJTA.setRows(5);
        msgJTA.setColumns(25);
        msgJTA.setRows(3);

        msgJTA.setLineWrap(true);
        msgJTA.setWrapStyleWord(false);
        msgJTA.setEditable(true);
        msgJTA.setText("[" + username + " to all]: ");

        userListJList = new JList<>(userlist);
        userListJList.setVisibleRowCount(38);
        //userListJList.setPreferredSize(new Dimension(12, 38));
        userListJList.setPreferredSize(new Dimension(10, 19));
        userListJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //userListJList.addListSelectionListener(new UserListSelectionAction());
        userListJList.addMouseListener(new UserListMouserListener());


        sendJB = new JButton("Send");
        logoutJB = new JButton("Log out");

        clear = new JComboBox();
        clear.addItem("Clear");
        clear.addItem("Clear Public Board");
        clear.addItem("Clear Private Board");
        clear.addItem("Undo Selection");

        sendJB.addActionListener(new sendButtonListener());
        clear.addItemListener(new ClearComboxListener());
        logoutJB.addActionListener(new LogoutButtonListener());

        chatSP = new JScrollPane();
        userListSP = new JScrollPane();
        // userListSP.setPreferredSize(new Dimension(300, 600));
        userListSP.setPreferredSize(new Dimension(150, 300));
        privateChatSP = new JScrollPane();
        msgSP = new JScrollPane();

        chatSP.setViewportView(chatJTA);
        userListSP.setViewportView(userListJList);
        privateChatSP.setViewportView(privateChatJTA);
        msgSP.setViewportView(msgJTA);

        chatJP.add(chatSP);
        privateChatJP.add(privateChatSP);
        msgJP.add(msgSP);

        leftJP.add(chatJP);
        leftJP.add(privateChatJP);
        leftJP.add(msgJP);

        userListJP.add(userListSP);
        buttonJP.add(sendJB);
//        buttonJP.add(clear);
        buttonJP.add(logoutJB);

        rightJP.add(userListJP);
        rightJP.add(clear);
        rightJP.add(buttonJP);


        this.getContentPane().add(BorderLayout.WEST, leftJP);
        this.getContentPane().add(BorderLayout.EAST, rightJP);

        String title = username + " - " + ip + ":" + port;

        this.setTitle(title);
        this.setResizable(true);
        this.pack();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String xmlOut = XMLUtil.constructClientQuitXML(username);
                clientConnection.sendMessage(xmlOut);

                System.exit(0);
            }

        });
    }


    private class sendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String temp = msgJTA.getText();
            String content = temp.substring(temp.lastIndexOf("]: ") + 2);
            String receiver = (temp.substring((temp.lastIndexOf("to") + 2), (temp.lastIndexOf("]: ")))).trim();
            String xmlOut;

            if (selection == null && receiver.equals("all")) {
                xmlOut = XMLUtil.constructMessage2All(content, username);
                clientConnection.sendMessage(xmlOut);

            } else if (selection != null && selection.equals(receiver)) {
                xmlOut = XMLUtil.constructMessageWisper(content, username, selection);
                userListJList.clearSelection();
                selection = null;
                String msgPrivate = "You to " + XMLUtil.extractMessageReceiver(xmlOut) + ": "
                        + XMLUtil.extractContent(xmlOut);
                privateChatJTA.append(msgPrivate + '\n');
                clientConnection.sendMessage(xmlOut);

            } else if(selection == null && isExistingUser(receiver) && !username.equals(receiver)){
                xmlOut = XMLUtil.constructMessageWisper(content, username, receiver);
                String msgPrivate = "You to " + XMLUtil.extractMessageReceiver(xmlOut) + ": "
                        + XMLUtil.extractContent(xmlOut);
                privateChatJTA.append(msgPrivate + '\n');
                clientConnection.sendMessage(xmlOut);

            }else if(selection == null && username.equals(receiver)){
                JOptionPane.showMessageDialog(ClientChat.this,
                        "You can not send message to yourself", "Warning", JOptionPane.WARNING_MESSAGE);
                msgJTA.requestFocus();
            }
            else {
                JOptionPane.showMessageDialog(ClientChat.this,
                        "Please enter the correct message receiver", "Warning", JOptionPane.WARNING_MESSAGE);
                msgJTA.requestFocus();
                userListJList.clearSelection();

            }
            msgJTA.setText("[" + username + " to all]: ");


        }
    }

    private class ClearComboxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {

            String item = (String) clear.getSelectedItem();
            if (item.equals("Clear Public Board")) {
                chatJTA.setText("");

            } else if (item.equals("Clear Private Board")) {
                privateChatJTA.setText("");

            } else if (item.equals("Undo Selection")) {
                selection = null;
                userListJList.clearSelection();
                String msgHead = "[" + username + " to all]: ";
                msgJTA.setText(msgHead);
            }
        }
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String xmlOut = XMLUtil.constructClientQuitXML(username);
            clientConnection.sendMessage(xmlOut);

        }
    }

    private class UserListMouserListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                selection = userListJList.getSelectedValue();
                if (selection.equals(username)) {
                    JOptionPane.showMessageDialog(ClientChat.this,
                            "You can not send message to yourself", "Warning", JOptionPane.WARNING_MESSAGE);
                    msgJTA.requestFocus();
                    selection = null;
                    userListJList.clearSelection();
                } else {
                    String msgHead = "[" + username + " to " + selection + "]: ";
                    msgJTA.setText(msgHead);
                }
            }
        }



        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private boolean isExistingUser(String username) {
        for (String u: userlist) {
            if(username.equals(u)){
            return true;
            }
        }
        return false;
    }

    public JTextArea getChatJTA() {
        return chatJTA;
    }

    public JTextArea getPrivateChatJTA() {
        return privateChatJTA;
    }

    public JList<String> getUserListJList() {
        return userListJList;
    }

    public void setUserlist(String[] userlist) {
        this.userlist = userlist;
    }

    //    //only for test
//    public ClientChat() {
//
//        chatWindowInitComponents();
//    }
//
//    public static void main(String[] args) {
//        new ClientChat();
//    }

}
