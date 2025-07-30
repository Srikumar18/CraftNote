package com.example.service;
import com.example.ui.GUI;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.List;

public class DocumentViewer {
    private JFrame mainFrame;
    private Drive driveService;
    private String folderId;
    private JPanel fileListPanel;

    public DocumentViewer(Drive driveService, String folderId) {
        this.driveService = driveService;
        this.folderId = folderId;
        
        mainFrame = new JFrame("Select Document");
        mainFrame.setSize(450, 500);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setBackground(Color.decode("#9BC9E0"));

        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(fileListPanel);
        mainFrame.add(scrollPane, BorderLayout.CENTER);

        loadDocumentList();
        mainFrame.setVisible(true);
    }

    public void loadDocumentList() {
        fileListPanel.removeAll();
        try {
            String query = "'" + folderId + "' in parents and mimeType='application/vnd.openxmlformats-officedocument.wordprocessingml.document' and trashed=false";
            FileList result = driveService.files().list().setQ(query).setSpaces("drive").execute();
            List<File> files = result.getFiles();

            if (files == null || files.isEmpty()) {
                JLabel noFilesLabel = new JLabel("No documents found.");
                fileListPanel.add(noFilesLabel);
            } else {
                for (File file : files) {
                    String fileName = file.getName();
                    String fileId = file.getId();
                    
                JButton fileButton = new JButton(fileName);
                fileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                fileButton.setPreferredSize(new Dimension(180, 30));
                fileButton.setMaximumSize(new Dimension(180, 30));
                fileButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openDocumentInGUI(fileId, fileName);
                    }
                });
                fileButton.setFocusable(false);
                fileListPanel.add(fileButton);
                fileListPanel.add(Box.createVerticalStrut(20));
            }
            
        }
            fileListPanel.revalidate();
            fileListPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDocumentInGUI(String fileId, String fileName) {
        try {
            InputStream inputStream = driveService.files().get(fileId).executeMediaAsInputStream();
            XWPFDocument document = new XWPFDocument(inputStream);

            GUI gui = new GUI(fileName);
            StyledDocument styledDoc = gui.textPane.getStyledDocument();
            styledDoc.remove(0, styledDoc.getLength());

            for (XWPFParagraph paragraph : document.getParagraphs()) {
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
            mainFrame.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Failed to load document: " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}