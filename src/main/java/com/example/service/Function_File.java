package com.example.service;

import com.example.ui.GUI;
import com.example.dao.DriveQuickstart;
import com.example.app.User;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;

import com.google.api.services.drive.Drive;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.security.GeneralSecurityException;
import java.util.*;


import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class Function_File {
    private GUI gui;
    private String fileName;
    private String fileAddress;
    private boolean isDocx;

    public Function_File(GUI gui) {
        this.gui = gui;
        if (this.gui.window.getTitle()!=null){
            fileName=gui.window.getTitle();
        }
    }

    public void newFile() {
        gui.textPane.setText("");
        gui.window.setTitle("New Document");
        fileName = null;
        fileAddress = null;
        isDocx = false;
    }

    public void open() {
        FileDialog fd = new FileDialog(gui.window, "Open", FileDialog.LOAD);
         fd.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".docx"));
         fd.setVisible(true);
     
         if (fd.getFile() != null) {
             fileName = fd.getFile();
             fileAddress = fd.getDirectory();
             gui.window.setTitle(fileName);
             isDocx = fileName.toLowerCase().endsWith(".docx");
             String filePath=Paths.get(fileAddress,fileName).toString();
     
             try (FileInputStream fis = new FileInputStream(new java.io.File(fileAddress, fileName))) {
                 StyledDocument doc = gui.textPane.getStyledDocument();
                 doc.remove(0, doc.getLength()); 
     
                 if (isDocx) {
                     try (XWPFDocument document = new XWPFDocument(fis)) {
                         for (XWPFParagraph paragraph : document.getParagraphs()) {
                             StringBuilder paragraphText = new StringBuilder();
                             SimpleAttributeSet attrs = new SimpleAttributeSet();
                             int alignment = paragraph.getAlignment().getValue();
                             switch (alignment) {
                                 case 1:
                                     StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
                                     break;
                                 case 2: 
                                     StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_RIGHT);
                                     break;
                                 case 3:
                                     StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_JUSTIFIED);
                                     break;
                                 default:
                                     StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
                                     break;
                             }
                             for (XWPFRun run : paragraph.getRuns()) {
                                 String text = run.getText(0); 
                                 if (text != null) {
                                     String colorString = run.getColor();
                                     Color color = Color.BLACK;
     
                                     if (colorString != null) {
                                         color = new Color(Integer.parseInt(colorString, 16)); 
                                     }
                                     @SuppressWarnings("deprecation")
                                     int fontSize = run.getFontSize() != -1 ? run.getFontSize() : 12; 
                                     String fontFamily = run.getFontFamily() != null ? run.getFontFamily() : "Serif"; 
                                     boolean isBold = run.isBold();
                                     boolean isItalic = run.isItalic();
                                     String styleName = "colorStyle" + Integer.toHexString(color.getRGB());
                                     Style style = doc.addStyle(styleName, null);
     
                                     StyleConstants.setForeground(style, color);
                                     StyleConstants.setFontSize(style, fontSize);
                                     StyleConstants.setFontFamily(style, fontFamily);
                                     StyleConstants.setBold(style, isBold);
                                     StyleConstants.setItalic(style, isItalic);
                                     doc.insertString(doc.getLength(), text, style);
                                 }
                             }
                         doc.setParagraphAttributes(doc.getLength(), 1, attrs, false);
                         doc.insertString(doc.getLength(), "\n", null);
                         }
                     }
                 }
     
                 final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                 Drive service = DriveQuickstart.getDriveService(HTTP_TRANSPORT); 
                 
                 String username = User.getName(); 
                 String folderId = DriveQuickstart.getOrCreateUserFolder(service, username);
                 
                 String fileId = DriveQuickstart.uploadFile(service, filePath, folderId);
                 System.out.println("File uploaded to Google Drive with ID: " + fileId);
     
             } catch (IOException | GeneralSecurityException | BadLocationException e) {
                 JOptionPane.showMessageDialog(gui.window, "Failed to open the file.", "Error", JOptionPane.ERROR_MESSAGE);
                 e.printStackTrace();
             }
        }
    }
    
    public void save() {
        try {
            String username;
            DocFirebase df=new DocFirebase();
            String invitedBy=df.checkIfSharedDoc(this.fileName);
            if (invitedBy!=null){
                username=invitedBy;
            }
            else{
                username=User.getName();
            }

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Drive service = DriveQuickstart.getDriveService(HTTP_TRANSPORT);
            
            String documentsFolderId = DriveQuickstart.getOrCreateUserFolder(service, "Documents");
            String folderId = DriveQuickstart.getOrCreateUserFolder(service, username);
            
            java.io.File tempFile = new java.io.File(this.fileName);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                saveDocx(fos);
            }  
            String fileId; 
            File fileid = DriveQuickstart.findFileInFolder(service, fileName, folderId);
                if (fileid != null) {
                    fileId=fileid.getId();
                    DriveQuickstart.replaceFile(service, fileId, tempFile.getAbsolutePath());
                } else {
                    fileId = DriveQuickstart.uploadFile(service, tempFile.getAbsolutePath(), folderId);
                }
                JOptionPane.showMessageDialog(gui.window, "File Saved!!");
                tempFile.delete();
                String fileLink = "https://drive.google.com/file/d/" + fileId + "/view";
                System.out.println("File saved to Google Drive. Access it here: " + fileLink);
                String emailAddress = "srikumar2310264@ssn.edu.in";  // The email address you want to share with
                DriveQuickstart.shareFolder(service, emailAddress);
        } catch (IOException | GeneralSecurityException e) {
            showError("Failed to save the document with formatting.");
            e.printStackTrace();
        }
    }
   
    public void download() {
        FileDialog fd = new FileDialog(gui.window, "Save a copy", FileDialog.SAVE);
        fd.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".docx"));
        fd.setVisible(true);

        if (fd.getFile() != null) {
            fileName = fd.getFile();
            String lowerFileName = fileName.toLowerCase();

            if (!lowerFileName.endsWith(".docx")) {
                fileName += ".docx";
            }
            isDocx = true;

            fileAddress = fd.getDirectory();
            gui.window.setTitle(fileName);

            try (FileOutputStream fos = new FileOutputStream(new java.io.File(fileAddress, fileName))) {
                if (isDocx) {
                    saveDocx(fos);
                }
            } catch (IOException e) {
                showError("Failed to save the file.");
                e.printStackTrace();
            }
        }
    }
    private void saveDocx(FileOutputStream fos) throws IOException {
                try (XWPFDocument document = new XWPFDocument()) {
                    StyledDocument doc = gui.textPane.getStyledDocument();
                    Element root = doc.getDefaultRootElement();
                    for(int j= 0;j<root.getElementCount();j++){
                    Element paragraphElement = root.getElement(j);
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun currentRun = paragraph.createRun();
                    StringBuilder currentText = new StringBuilder();
            
                    Color currentColor = null;
                    int currentFontSize = -1;
                    String currentFontFamily = null;
                    boolean isBold = false;
                    boolean isItalic = false;
                    AttributeSet attrs=null;
                    for (int i = paragraphElement.getStartOffset(); i < paragraphElement.getEndOffset(); i++) {
                        attrs = doc.getCharacterElement(i).getAttributes();
                        Color color = (Color) attrs.getAttribute(StyleConstants.Foreground);
                        int fontSize = StyleConstants.getFontSize(attrs);
                        String fontFamily = StyleConstants.getFontFamily(attrs);
                        boolean bold = StyleConstants.isBold(attrs); 
                        boolean italic = StyleConstants.isItalic(attrs);
            
                        String text;
                        try {
                            text = doc.getText(i, 1);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                            continue;
                        }

                        if ("\n".equals(text)) {
                            if (currentText.length() > 0) {
                                currentRun.setText(currentText.toString());
                                currentText.setLength(0);  
                            }
                            currentRun.addCarriageReturn(); 
                    continue;
                        }

                        if (color != currentColor || fontSize != currentFontSize || fontFamily != currentFontFamily || bold != isBold || italic != isItalic) {
                            if (currentText.length() > 0) {
                                currentRun.setText(currentText.toString());
                                currentText.setLength(0); 
                            }
            
                            currentRun = paragraph.createRun();
                            currentColor = color;
                            currentFontSize = fontSize;
                            currentFontFamily = fontFamily;
                            isBold = bold;
                            isItalic = italic;
            
                            if (currentColor != null) {
                                currentRun.setColor(String.format("%02X%02X%02X", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue()));
                            }
                            if (currentFontSize > 0) {
                                currentRun.setFontSize(currentFontSize);
                            }
                            if (currentFontFamily != null) {
                                currentRun.setFontFamily(currentFontFamily);
                            }
                            currentRun.setBold(isBold);
                            currentRun.setItalic(isItalic);
                        }
            
                        currentText.append(text);
                    }
            
                    if (currentText.length() > 0) {
                        currentRun.setText(currentText.toString());
                    }
                    AttributeSet paragraphAttrs = doc.getParagraphElement(paragraphElement.getStartOffset()).getAttributes();
                    int alignment = StyleConstants.getAlignment(attrs);
                    switch (alignment) {
                        case StyleConstants.ALIGN_CENTER:
                            paragraph.setAlignment(ParagraphAlignment.CENTER);
                            break;
                        case StyleConstants.ALIGN_RIGHT:
                            paragraph.setAlignment(ParagraphAlignment.RIGHT);
                            break;
                        case StyleConstants.ALIGN_JUSTIFIED:
                            paragraph.setAlignment(ParagraphAlignment.BOTH);
                            break;
                        default:
                            paragraph.setAlignment(ParagraphAlignment.LEFT);
                            break;
        }
    }
                    document.write(fos);
                }
            }
    private void showError(String message) {
        JOptionPane.showMessageDialog(gui.window, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void exit() {
        System.exit(0);
    }
}