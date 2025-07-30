package com.example.ui;

import com.example.app.User;
import com.example.dao.FirebaseInitializer;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;


public class Login implements ActionListener{
    JFrame window;
    JButton loginButton;
    JTextField Username;
    JPasswordField Password;
    JPanel panel1;
    JLabel signupLabel;
    public static LoginService loginService;

    public void createLoginPage(){
        window=new JFrame("Homepage");
        window.setSize(1536,860);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(Color.decode("#9BC9E0"));
        window.setLayout(null);

        panel1=new JPanel();
        panel1.setBounds(603,230,400,380);
        panel1.setLayout(null);

        JLabel title=new JLabel("CraftNote");
        title.setBounds(130,30,220,45);
        title.setFont(new Font("Britannic Bold", Font.BOLD, 27));

        JLabel tagline = new JLabel("<html><center>Your ideas, our canvas!</center></html>");
        tagline.setBounds(120, 70, 220, 20);
        tagline.setFont(new Font("Calibri", Font.BOLD, 14));
        tagline.setForeground(Color.DARK_GRAY);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50,110,100,30);
        usernameLabel.setFont(new Font("Calibri",Font.BOLD,18));
        Username = new JTextField();
        Username.setBounds(150,110,200,30);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50,170,100,30);
        passwordLabel.setFont(new Font("Calibri",Font.BOLD,18));
        Password = new JPasswordField();
        Password.setBounds(150,170,200,30);

        loginButton=new JButton("Login");
        loginButton.setBounds(160,240,80,30);
        loginButton.setFocusable(false);
        loginButton.setHorizontalTextPosition(JButton.CENTER);
        loginButton.setVerticalTextPosition(JButton.CENTER);
        loginButton.setFont(new Font("Calibri",Font.BOLD,14));
        loginButton.addActionListener(this);

        signupLabel = new JLabel("<html><i>Don't have an account? </i><u style='color:blue; cursor:pointer;'>Sign Up</u></html>");
        signupLabel.setFont(new Font("Calibri",Font.PLAIN,16));
        signupLabel.setBounds(90,290,300,30);
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                window.dispose(); 
                new Registration(); 
            }
        });
        panel1.add(title);
        panel1.add(tagline);
        panel1.add(usernameLabel);
        panel1.add(Username);
        panel1.add(passwordLabel);
        panel1.add(Password);
        panel1.add(loginButton);
        panel1.add(signupLabel);
        window.add(panel1);   
    }

    public Login(){
        createLoginPage();
        window.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==loginButton){
            String username = Username.getText();
            String password = new String(Password.getPassword());
            System.out.println("Username: " + username + ", Password: " + password);
            loginService = new LoginService();
            if (loginService.login(username, password)==1) {
                System.out.println(User.getName()+" ,"+User.getEmailid());
                window.dispose();
                new HomePage();
            } else if (loginService.login(username, password)==0){
                JOptionPane.showMessageDialog(window, "Incorrect Password");
            } else{
                System.out.println(username+" "+password);
                JOptionPane.showMessageDialog(window, "Invalid Username: Username doesn't exist");
            }
        }
    }
}

class LoginService {
    private Firestore db;
    public LoginService() {
        this.db = FirebaseInitializer.getFirestore();
    }
    public Firestore getFirestore(){
        return this.db;
    }
    public int login(String username, String password) {
        try {
            ApiFuture<QuerySnapshot> query = db.collection("users")
                                               .whereEqualTo("username", username)
                                               .get();

            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            if (!documents.isEmpty()) {
                QueryDocumentSnapshot document = documents.get(0);

                String storedPassword = document.getString("password");

                if (password.equals(storedPassword)) {
                    String mailid = document.getString("email");
                    User.setEmailid(mailid);
                    User.setName(username);
                    return 1;
                    

                } else {
                    return 0;
                }
            } else {
            }
        } catch (Exception e) {
            System.err.println("Error checking credentials: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    List <String> returnInvites(){
        List<String> invites=new ArrayList<String>();
        try{
        DocumentReference recipientRef = db.collection("users").document(User.getName());
            ApiFuture<DocumentSnapshot> future = recipientRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                invites= (List<String>) document.get("invites");
                if (invites == null) {
                    invites = new ArrayList<>(); 
                }
    }
            else
                invites= new ArrayList<String>();
        }
        catch(Exception e){
            System.out.println("Interruption exception");
        }
            return invites;
    }
    void rewriteInvites(List <String> invites){
        try{
        DocumentReference recipientRef = db.collection("users").document(User.getName());
            Map<String, Object> updates=new HashMap<>();
            updates.put("invites",invites);
            recipientRef.update(updates);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}