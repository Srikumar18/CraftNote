package com.example.ui;

import com.example.app.User;
import com.example.dao.FirebaseInitializer;
import com.example.service.DocFirebase;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.List;
import java.util.*;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.DocumentSnapshot;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;

public class Registration implements ActionListener{
    JFrame rwindow;
    JPanel panel2; 
    JLabel title, nameLabel, usernameLabel, emailLabel, passwordLabel, signupLabel; 
    JTextField Name, Username, Email;
    JPasswordField Password,Reenter; 
    JButton registerButton, loginButton; 
    JCheckBox showpwd;
    private String email;
    public void createRegpage(){
        rwindow=new JFrame("Registration page");
        rwindow.setSize(1536,860);
        rwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rwindow.getContentPane().setBackground(Color.decode("#9BC9E0"));
        rwindow.setLayout(null);

        JPanel panel2=new JPanel();
        panel2.setBounds(603,230,400,420);
        panel2.setLayout(null);

        JLabel title=new JLabel("<html><i>Sign Up</i></html>");
        title.setBounds(0, 30, 400, 45);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel nameLabel=new JLabel("Name: ");
        nameLabel.setBounds(50,110,100,30);
        nameLabel.setFont(new Font("Calibri",Font.BOLD,18));
        Name=new JTextField();
        Name.setBounds(150,110,200,30);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50,150,100,30);
        usernameLabel.setFont(new Font("Calibri",Font.BOLD,18));
        Username = new JTextField();
        Username.setBounds(150,150,200,30);

        JLabel emailLabel=new JLabel("Email ID: ");
        emailLabel.setBounds(50,190,100,30);
        emailLabel.setFont(new Font("Calibri",Font.BOLD,18));
        Email=new JTextField();
        Email.setBounds(150,190,200,30);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50,230,100,30);
        passwordLabel.setFont(new Font("Calibri",Font.BOLD,18));
        Password = new JPasswordField();
        Password.setBounds(150,230,200,30);

        JLabel reenterLabel= new JLabel("Confirm:");
        reenterLabel.setBounds(50,270,100,30);
        reenterLabel.setFont(new Font("Calibri",Font.BOLD,18));
        Reenter=new JPasswordField();
        Reenter.setBounds(150,270,200,30);

        showpwd= new JCheckBox("Show password");
        showpwd.setBounds(150,310, 150,30);
        showpwd.addActionListener(e ->{
            if (showpwd.isSelected()){
                Password.setEchoChar((char) 0);
                Reenter.setEchoChar((char) 0);
            }
            else{
            Password.setEchoChar('\u2022');
            Reenter.setEchoChar('\u2022');
            }
        });

        registerButton=new JButton("Register");
        registerButton.setBounds(150,350,100,30);
        registerButton.setFocusable(false);
        registerButton.setHorizontalTextPosition(JButton.CENTER);
        registerButton.setVerticalTextPosition(JButton.CENTER);
        registerButton.setFont(new Font("Calibri",Font.BOLD,14));
        registerButton.addActionListener(this);

        panel2.add(title);
        panel2.add(nameLabel);
        panel2.add(Name);
        panel2.add(usernameLabel);
        panel2.add(Username);
        panel2.add(emailLabel);
        panel2.add(Email);
        panel2.add(passwordLabel);
        panel2.add(Password);
        panel2.add(reenterLabel);
        panel2.add(Reenter);
        panel2.add(showpwd);
        panel2.add(registerButton);
        rwindow.add(panel2);
    }
     public Registration(){
        createRegpage();
        rwindow.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==registerButton){
            String name=Name.getText();
            email=Email.getText();
            String username = Username.getText();
            String password = new String(Password.getPassword()); 
            String reenter= new String(Reenter.getPassword());
            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(rwindow, "Please enter the details!");
                return; 
            }
            boolean bool=false;
            for (char s : name.toCharArray()){
                if (!(Character.isLetter(s) || Character.isWhitespace(s))){
                    JOptionPane.showMessageDialog(rwindow, "Name must not contain numbers or special characters");
                    bool=true;
                    break;
                }
            }
            if (bool){
                Name.requestFocus();
                return;
            }
            bool=false;
            if (username.length()<5){
                bool=true;
            }
            if (bool){
                JOptionPane.showMessageDialog(rwindow, "Username must be atleast 5 characters long");
                Username.requestFocus();
                return;
            }
            bool=false;
            boolean at=false;
            boolean dot=false;
            String domain="";
            for (char s : email.toCharArray()){
                if (s=='@'){
                    at=true;
                    domain=email.substring(email.indexOf("@")+1);
                }
                if (s=='.')
                    dot=true;
            }
            try{ 
            InetAddress address=InetAddress.getByName(domain);
            bool=address.isReachable(1000);
            }
            catch(Exception ex){
                System.out.println("Incorrect domain name used");
            }
            finally{
            if (!(bool && at && dot)){
                JOptionPane.showMessageDialog(rwindow, "Enter a valid email id");
                Email.requestFocus();
                return;
            }
            }
            
            boolean num=false;
            boolean spl=false;
            for (char s: password.toCharArray()){
                if ((Character.isDigit(s))){
                    num=true;
                }
            }
            spl=password.matches(".[^a-zA-Z0-9].");
            if (!(spl && num && password.length()>=8)){
                JOptionPane.showMessageDialog(rwindow, "Password must be atleast 8 characters long and must contain atleast 1 special character and 1 number.\nEnter a valid password");
                Password.requestFocus();
                return;
            }
            if (!(password.equals(reenter))){
                JOptionPane.showMessageDialog(rwindow, "Password does not match");
                Password.requestFocus();
                return;
            }
            RegistrationService registrationService = new RegistrationService();
            if (registrationService.register(username, password, email)) {
                JOptionPane.showMessageDialog(rwindow, "Registration Successful!");
                rwindow.dispose(); 
                new Login();
            } else {
                JOptionPane.showMessageDialog(rwindow, "OOPS!! Username already exists");
            }
            
        }
    }
    public String getEmail(){
        return email;
    }
}

class RegistrationService {
    private Firestore db;
    public RegistrationService() {
        this.db = FirebaseInitializer.getFirestore();
    }

    public boolean register(String username, String password, String email) {
        try {

            ApiFuture<QuerySnapshot> query = db.collection("users")
                                               .whereEqualTo("username", username)
                                               .get();
            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            if (!documents.isEmpty()) {
                System.out.println("Username already exists. Please choose a different one.");
                return false;
            } else {
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", username);
                userData.put("password", password); 
                userData.put("email", email);

                DocumentReference newUserRef = db.collection("users").document(username); // Use username as document ID
                ApiFuture<com.google.cloud.firestore.WriteResult> result = newUserRef.set(userData);
                
                System.out.println("User registered successfully: " + result.get().getUpdateTime());
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public List<String> sendInvite(String myName, String inviteName) {
        List<String> invites = new ArrayList<>();
        
            System.out.println("Attempting to send invite from: " + myName + " to: " + inviteName);
        
            if (inviteName == null || inviteName.isEmpty()) {
            return invites;
            }
            if (myName == null || myName.isEmpty()) {
                return invites;
            }
            try{
            DocumentReference recipientRef = db.collection("users").document(inviteName);
            ApiFuture<DocumentSnapshot> future = recipientRef.get();
            DocumentSnapshot document = future.get();
    
            if (document.exists()) {
                invites = (List<String>) document.get("invites");
                if (invites == null) {
                    invites = new ArrayList<>(); 
                }

                if (!invites.contains(User.getName()+"\\"+myName)) {
                    invites.add(User.getName()+"\\"+myName);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("invites", invites); 
    
                    recipientRef.update(updates);
                    System.out.println("Invite sent to: " + inviteName);

                    GUI.invites=invites;

                    DocFirebase df=new DocFirebase();
                    df.sendDoc(myName,User.getName(),inviteName); 
                } else {
                    System.out.println("Invite has already been sent to this email.");
                }
            } else {
                System.out.println("User with email " + inviteName + " is not registered.");
            }
        } catch (Exception e) {
            System.err.println("Error sending invite: " + e.getMessage());
            e.printStackTrace();
        }
        return invites;
    }
}