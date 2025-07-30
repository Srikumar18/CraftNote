package com.example.service;

import com.example.app.User;
import com.example.dao.DriveQuickstart;
import com.example.ui.GUI;
import java.awt.*;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;  
import org.apache.poi.xwpf.usermodel.XWPFParagraph; 
import javax.swing.text.StyledDocument;  
import javax.swing.text.Style;   
import javax.swing.text.StyleConstants;
import org.apache.poi.xwpf.usermodel.XWPFRun;  
import com.google.api.services.drive.model.File;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import java.util.HashMap;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;

public class DocFirebase {
    private Firestore db;

    public DocFirebase() {
        this.db = FirestoreClient.getFirestore(); 
    }
    public String checkIfSharedDoc(String docName){
        String invitedBy=null;
        try{
            ApiFuture<QuerySnapshot> query = db.collection("Documents")
            .whereEqualTo("documentName", docName) 
            .get();
            QuerySnapshot querySnapshot = query.get();
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0); 
                invitedBy = document.getString("invitedBy");
}
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return invitedBy;
    }
    public void sendDoc(String documentName, String invitedBy, String invitedTo) {
        List<String> inviteTo = new ArrayList<>();
        List<String> statusList = new ArrayList<>();
        try {
            DocumentReference recipientRef = db.collection("Documents").document("Documents\\" + invitedBy + "\\" + documentName);
            ApiFuture<DocumentSnapshot> future = recipientRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                inviteTo = (List<String>) document.get("invitedTo");
                if (inviteTo == null) {
                    inviteTo = new ArrayList<>();
                }
                
                statusList = (List<String>) document.get("status");
                if (statusList == null) {
                    statusList = new ArrayList<>();
                }
                if (!inviteTo.contains(invitedTo)) {
                    inviteTo.add(invitedTo);  
                    statusList.add("pending");  
                }
    
                Map<String, Object> updates = new HashMap<>();
                updates.put("invitedTo", inviteTo);
                updates.put("status", statusList);
    
                recipientRef.set(updates, SetOptions.merge());  
                System.out.println("Invite sent to: " + invitedTo);
            } else {
                System.out.println("Document does not exist. Creating new document...");
                
                inviteTo.add(invitedTo);  
                statusList.add("pending");  
    
                Map<String, Object> newDocData = new HashMap<>();
                newDocData.put("documentName", documentName);
                newDocData.put("invitedBy", invitedBy);
                newDocData.put("invitedTo", inviteTo);
                newDocData.put("status", statusList);
                recipientRef.set(newDocData); 
            }
        } catch (Exception e) {
            System.out.println("Error handling document: " + e.getMessage());
        }
    }

        public void accepted(String docName) {
            List<String> inviteTo = new ArrayList<>();
            List<String> status = new ArrayList<>();
            
            try {
                DocumentReference recipientRef = db.collection("Documents").document(docName);
                ApiFuture<DocumentSnapshot> future = recipientRef.get();
                DocumentSnapshot document = future.get();
    
                if (document.exists()) {
                    System.out.println("Document found: " + docName);
                    inviteTo = (List<String>) document.get("invitedTo");
                    status = (List<String>) document.get("status");
                    if (inviteTo == null || status == null) {
                        return;
                    }
                    int index = inviteTo.indexOf(User.getName());
                    
                    if (index != -1) {
                        status.set(index, "accepted");
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("status", status);
                        ApiFuture<WriteResult> writeResult = recipientRef.update(updates);
                        writeResult.get(); 
    
                        System.out.println("Status successfully updated for document: " + docName);
                    } else {
                        System.out.println("User " + User.getName() + " is not in the invited list.");
                    }
                } else {
                    System.out.println("Document does not exist: " + docName);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        public String displaySharedDocs() {
            JFrame frame = new JFrame("Shared Documents");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(400, 800);
            frame.setLayout(new BorderLayout());
            frame.setBackground(Color.decode("#9BC9E0"));
            String fileName=null;
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            CollectionReference colref = db.collection("Documents");
            ApiFuture<QuerySnapshot> future = colref.get();
        
            try {
                QuerySnapshot querySnapshot = future.get();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    String documentName = document.getString("documentName");
                    List<String> invitedTo = (List<String>) document.get("invitedTo");
                    List<String> status = (List<String>) document.get("status");
                    String invitedBy = document.getString("invitedBy"); 
                    fileName=documentName;
                    if (invitedTo.contains(User.getName())) {
                        int index = invitedTo.indexOf(User.getName());
                        if (status.get(index).equals("accepted")) {
                            JButton button = new JButton(documentName);
                            button.setAlignmentX(Component.CENTER_ALIGNMENT);
                            panel.add(Box.createVerticalStrut(20));
                            button.addActionListener(e -> {
                                try {
                                    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                                    Drive service = DriveQuickstart.getDriveService(HTTP_TRANSPORT);
                                    String folderId = DriveQuickstart.findOrCreateFolder(service, invitedBy, null); 
                                    File file = DriveQuickstart.findFileInFolder(service, documentName, folderId);
                                    String fileId=file.getId();
                                    if (fileId != null) {
                                        try {
                                            InputStream inputStream = service.files().get(fileId).executeMediaAsInputStream();
                                            XWPFDocument thisDocument = new XWPFDocument(inputStream);
                                            GUI gui = new GUI(documentName);
                                            StyledDocument styledDoc = gui.textPane.getStyledDocument();
                                            styledDoc.remove(0, styledDoc.getLength());
                                            for (XWPFParagraph paragraph : thisDocument.getParagraphs()) {
                                                for (XWPFRun run : paragraph.getRuns()) {
                                                    String text = run.getText(0);
                                                    if (text != null) {
                                                        Style style = styledDoc.addStyle(null, null);
                                                        StyleConstants.setFontFamily(style, run.getFontFamily() != null ? run.getFontFamily() : "SansSerif");
                                                        StyleConstants.setFontSize(style, run.getFontSize() > 0 ? run.getFontSize() : 12);
                                                        StyleConstants.setBold(style, run.isBold());
                                                        StyleConstants.setItalic(style, run.isItalic());
                                
                                                        String colorString = run.getColor();
                                                        if (colorString != null) {
                                                            Color color = new Color(Integer.parseInt(colorString, 16));
                                                            StyleConstants.setForeground(style, color);
                                                        } else {
                                                            StyleConstants.setForeground(style, Color.BLACK);
                                                        }

                                                        styledDoc.insertString(styledDoc.getLength(), text, style);
                                                    }
                                                }
                                                styledDoc.insertString(styledDoc.getLength(), "\n", null);
                                            }
                                
                                            inputStream.close();
                                            frame.dispose();  
                                        } catch (Exception g) {
                                            g.printStackTrace();
                                            JOptionPane.showMessageDialog(frame, "Failed to load document: " + documentName, "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } 
        
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(frame, "Error handling document.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
        
                            panel.add(button);  
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        
            frame.add(new JScrollPane(panel), BorderLayout.CENTER);
            frame.setVisible(true);
            return fileName;
        } 
}