package com.example.ui;
import com.example.dao.DriveQuickstart;
import com.example.app.User;
import com.example.service.DocFirebase;
import com.example.service.DocumentViewer;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;

public class HomePage implements ActionListener {
    JFrame hwindow;
    JPanel newPanel, invitePanel, openMyPanel, openPanel;
    JButton newButton, openDocsButton;
    Drive driveService;

    public void createHomePage() {
        hwindow = new JFrame("Home Page");
        hwindow.setSize(1536, 860);
        hwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hwindow.getContentPane().setBackground(Color.decode("#9BC9E0"));
        hwindow.setLayout(null);
        Font titleFont = new Font("Calibri", Font.BOLD, 24);
        Font buttonFont = new Font("Calibri", Font.PLAIN, 18);

        newPanel = createPanelWithTitle("Create a New Document", titleFont, 160, 80, 500, 300);
        invitePanel = createPanelWithTitle("Invites for You", titleFont, 840, 80, 500, 300);
        openMyPanel = createPanelWithTitle("Open Documents You Own", titleFont, 160, 450, 500, 300);
        openPanel = createPanelWithTitle("Open Shared Documents", titleFont, 840, 450, 500, 300);

        newButton = createStyledButton("New Document", buttonFont, 150, 130, 200, 70);
        newButton.addActionListener(this);
        openDocsButton = createStyledButton("View My Documents", buttonFont, 150, 130, 200, 70);
        openDocsButton.addActionListener(e -> displayDocuments());

        JButton openShared = createStyledButton("View Shared Documents", buttonFont, 140, 130, 220, 70);
        openShared.addActionListener(e -> new DocFirebase().displaySharedDocs());

        JButton viewInv = createStyledButton("View Invites", buttonFont, 140, 130, 220, 70);
        viewInv.addActionListener(e -> showInvites());

        newPanel.add(newButton);
        invitePanel.add(viewInv);
        openMyPanel.add(openDocsButton);
        openPanel.add(openShared);

        hwindow.add(newPanel);
        hwindow.add(invitePanel);
        hwindow.add(openMyPanel);
        hwindow.add(openPanel);

        hwindow.setVisible(true);
    }

    private JPanel createPanelWithTitle(String titleText, Font titleFont, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(x, y, width, height);

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(titleFont);
        title.setForeground(Color.decode("#37474F"));
        title.setBounds(20, 50, width - 40, 30);
        panel.add(title);

        return panel;
    }

    private JButton createStyledButton(String text, Font font, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(Color.BLACK);
        button.setBounds(x, y, width, height);
        button.setFocusPainted(false);
        return button;
    }

    private void showInvites() {
        JFrame inviteFrame = new JFrame("Invites");
        inviteFrame.setSize(600, 400);
        inviteFrame.setLayout(null);
        inviteFrame.setLocationRelativeTo(null);
        inviteFrame.getContentPane().setBackground(Color.decode("#9BC9E0"));

        List<String> invites = Login.loginService.returnInvites();
        List<String> toRemove = new ArrayList<>();
        ListIterator<String> li = invites.listIterator();
        int yPosition = 40;

        while (li.hasNext()) {
            String emailId = li.next();
            JButton inviteButton = createStyledButton("Accept Invite from " + emailId, new Font("Arial", Font.PLAIN, 14), 50, yPosition, 500, 40);
            yPosition += 60;
            inviteButton.addActionListener(actionEvent -> {String[] options = {"Accept", "Delete", "Cancel"};
            int response = JOptionPane.showOptionDialog(
                inviteFrame,
                "What do you want to do?",
                "Invite options",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                "Cancel"
            );
    
            if (response == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(inviteFrame, "Accepted invite!");
                toRemove.add(emailId);
                inviteButton.setVisible(false);
                new DocFirebase().accepted("Documents\\" + emailId);
            } else if (response == JOptionPane.NO_OPTION) {
                int deleteConfirmation = JOptionPane.showConfirmDialog(
                    inviteFrame,
                    "Are you sure you want to delete this invite?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (deleteConfirmation == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(inviteFrame, "Invite deleted!");
                    toRemove.add(emailId);
                    inviteButton.setVisible(false);
                }
            }});
            inviteFrame.add(inviteButton);
        }

        JButton doneButton = createStyledButton("Done", new Font("Arial", Font.BOLD, 14), 200, yPosition + 20, 200, 40);
        doneButton.addActionListener(e -> finalizeInvites(inviteFrame, invites, toRemove));
        inviteFrame.add(doneButton);

        inviteFrame.setVisible(true);
    }

    
    private void finalizeInvites(JFrame inviteFrame, List<String> invites, List<String> toRemove) {
        inviteFrame.dispose();
        invites.removeAll(toRemove);
        Login.loginService.rewriteInvites(invites);
    }

    private void displayDocuments() {
        try {
            String folderId = DriveQuickstart.getOrCreateUserFolder(driveService, User.getName());
            DocumentViewer docViewer = new DocumentViewer(driveService, folderId);
            docViewer.loadDocumentList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(hwindow, "Error displaying documents.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public HomePage(){
        try{
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            this.driveService = DriveQuickstart.getDriveService(HTTP_TRANSPORT);
        } catch (GeneralSecurityException | IOException e){
            System.out.println(e.getMessage());
        }
        createHomePage();
        hwindow.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newButton) {
            String fileName = promptForFileName();
            if (fileName != null && !fileName.isEmpty()) {
                new GUI(fileName + ".docx");
            } else {
                new GUI();
            }
        }
    }

    private String promptForFileName() {
        JTextField fileNameField = new JTextField(20);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter new file name:"));
        panel.add(fileNameField);

        int option = JOptionPane.showConfirmDialog(hwindow, panel, "Name Your File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return (option == JOptionPane.OK_OPTION) ? fileNameField.getText().trim() : null;
    }
}