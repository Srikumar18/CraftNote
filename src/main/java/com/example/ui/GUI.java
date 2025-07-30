package com.example.ui;

import com.example.app.User;
import com.example.dao.DriveQuickstart;
import com.example.service.Function_File;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.core.ApiFuture;
import com.google.api.services.drive.Drive;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI implements ActionListener {
    JScrollPane scrollPane;
    JMenuBar menuBar;
    JMenu menuFile, menuColor,menuFont,menuInvite;
    JMenuItem iNew, iOpen, iSave, iSaveAs, iExit, iInvite;
    JMenuItem iColorPicker,iFontFamily,iFontStyle,iFontSize; // Menu item for color picker
    Timer autoSaveTimer;
    JToolBar toolBar;
    private SimpleAttributeSet defaultAttributes;
    public static List<String> invites;
    public String fileName;
    public JFrame window;
    public JTextPane textPane;
    Function_File file;

    public GUI() {
        mainWindow();
        createTextArea();
        createToolbar();
        createMenuBar();
        createFileMenu();
        createAlignmentMenu();
        createInviteMenu();
        window.setVisible(true);
        defaultAttributes=new SimpleAttributeSet();
    }
    public GUI(String fileName){
        mainWindow(fileName);
        createTextArea();
        createToolbar();
        createMenuBar();
        createFileMenu();
        createAlignmentMenu();
        createInviteMenu();
        window.setVisible(true);
        defaultAttributes=new SimpleAttributeSet();
    }
    
    public void mainWindow() {
        window = new JFrame("CraftsNote");
        this.file= new Function_File(this);
        window.setSize(1536, 860);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void mainWindow(String fileName) {
        window = new JFrame(fileName);
        this.file= new Function_File(this);
        window.setSize(1536, 860);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        JComboBox<String> fontFamilyDropdown = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames());
        fontFamilyDropdown.addActionListener(e -> {
            String selectedFont = (String) fontFamilyDropdown.getSelectedItem();
            setFontFamily(selectedFont);
        });
        toolbar.add(new JLabel("Font: "));
        toolbar.add(fontFamilyDropdown);
    
        JComboBox<Integer> fontSizeDropdown = new JComboBox<>(new Integer[]{8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40});
        fontSizeDropdown.addActionListener(e -> {
            int selectedSize = (int) fontSizeDropdown.getSelectedItem();
            setFontSize(selectedSize);
        });
        toolbar.add(new JLabel("Size: "));
        toolbar.add(fontSizeDropdown);
    
        JButton boldButton = new JButton("B");
        boldButton.setFont(new Font("Arial", Font.BOLD, 12));
        boldButton.addActionListener(e -> setFontStyle(Font.BOLD));
        toolbar.add(boldButton);
    
        JButton italicButton = new JButton("I");
        italicButton.setFont(new Font("Arial", Font.ITALIC, 12));
        italicButton.addActionListener(e -> setFontStyle(Font.ITALIC));
        toolbar.add(italicButton);
    
        JButton colorButton = new JButton("Color");
        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose Text Color", Color.BLACK);
            if (color != null) {
                setTextColor(color);
            }
        });
        toolbar.add(colorButton);

        JButton leftAlignButton = new JButton("Left");
        leftAlignButton.addActionListener(e -> setAlignment(StyleConstants.ALIGN_LEFT));
        toolbar.add(leftAlignButton);

        JButton centerAlignButton = new JButton("Center");
        centerAlignButton.addActionListener(e -> setAlignment(StyleConstants.ALIGN_CENTER));
        toolbar.add(centerAlignButton);

        JButton rightAlignButton = new JButton("Right");
        rightAlignButton.addActionListener(e -> setAlignment(StyleConstants.ALIGN_RIGHT));
        toolbar.add(rightAlignButton);

        JButton justifyAlignButton = new JButton("Justify");
        justifyAlignButton.addActionListener(e -> setAlignment(StyleConstants.ALIGN_JUSTIFIED));
        toolbar.add(justifyAlignButton);
        UndoManager undoManager = new UndoManager();
        textPane.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        Action undoAction = new AbstractAction("Undo") {
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        };

        Action redoAction = new AbstractAction("Redo") {
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        };
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        undoButton.addActionListener(undoAction);
        redoButton.addActionListener(redoAction);
        toolbar.add(undoButton);
        toolbar.add(redoButton);

        window.add(toolbar, BorderLayout.NORTH);
    }

    public void createTextArea() {
        textPane = new JTextPane(); 
        scrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        Font defFont = new Font("Comic Sans MS", Font.PLAIN, 20);
        textPane.setFont(defFont);
        
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet leftAlign = new SimpleAttributeSet();
        StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), leftAlign, false);
    
        window.add(scrollPane);
    }

    public void createMenuBar() {
        menuBar = new JMenuBar();
        window.setJMenuBar(menuBar);
        menuFile = new JMenu("File");
        menuBar.add(menuFile);
        menuInvite=new JMenu("Invite");
        menuBar.add(menuInvite);
    }

    public void createFileMenu() {
        iNew = new JMenuItem("New");
        iNew.addActionListener(this);
        iNew.setActionCommand("New");
        menuFile.add(iNew);

        iOpen = new JMenuItem("Open");
        iOpen.addActionListener(this);
        iOpen.setActionCommand("Open");
        menuFile.add(iOpen);

        iSave = new JMenuItem("Save");
        iSave.addActionListener(this);
        iSave.setActionCommand("Save");
        menuFile.add(iSave);

        iSaveAs = new JMenuItem("Save Copy");
        iSaveAs.addActionListener(this);
        iSaveAs.setActionCommand("Save Copy");
        menuFile.add(iSaveAs);

        iExit = new JMenuItem("Exit");
        iExit.addActionListener(this);
        iExit.setActionCommand("Exit");
        menuFile.add(iExit);
    }

    public void createInviteMenu(){
        iInvite=new JMenuItem("Send Invite");
        iInvite.addActionListener(e ->{
            String fileName=null;
            file.save();
            try{
                Drive service = DriveQuickstart.getDriveService(GoogleNetHttpTransport.newTrustedTransport());
                String folderId=DriveQuickstart.getOrCreateUserFolder(service, User.getName());
                fileName=DriveQuickstart.findFileInFolder(service, window.getTitle(),folderId).getName();
                } catch (Exception p){
                    System.out.println(p.getMessage());
                }
            if (fileName==null){
                JOptionPane.showMessageDialog(window,"You are restricted from sending invites");
            }
            else{
                String invName=JOptionPane.showInputDialog(window,"Enter the username to invite");
                JOptionPane.showMessageDialog(window,"YAY");
                RegistrationService rs = new RegistrationService();
                
                
                
                invites=rs.sendInvite(fileName,invName);
            }
        });
        menuInvite.add(iInvite);
    }
    public void setTextColor(Color color) {
        StyleConstants.setForeground(defaultAttributes, color);
        textPane.setCharacterAttributes(defaultAttributes, false);
    }


    private String defaultFontFamily = "SansSerif";
    private void setFontFamily(String fontFamily) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, fontFamily);
        if (start != end) {
            doc.setCharacterAttributes(start, end - start, attr, false);
        } else {
            textPane.setCharacterAttributes(attr, true);
    }
    }

    private void setFontStyle(int fontStyle) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();

        Element element = doc.getCharacterElement(start);
        AttributeSet existingAttrs = element.getAttributes();
        boolean isCurrentlyBold = StyleConstants.isBold(existingAttrs);
        boolean isCurrentlyItalic = StyleConstants.isItalic(existingAttrs);
        boolean isCurrentlyUnderlined = StyleConstants.isUnderline(existingAttrs);

        if (fontStyle == 1) { 
            StyleConstants.setBold(attr,!isCurrentlyBold);
            StyleConstants.setItalic(attr,isCurrentlyItalic);
            StyleConstants.setUnderline(attr, isCurrentlyUnderlined);
        } else if (fontStyle == 2) {
            StyleConstants.setBold(attr, isCurrentlyBold);
            StyleConstants.setItalic(attr,!isCurrentlyItalic);
            StyleConstants.setUnderline(attr, isCurrentlyUnderlined);
        }else if(fontStyle == 3){
            StyleConstants.setBold(attr, isCurrentlyBold);
            StyleConstants.setItalic(attr, isCurrentlyItalic);
            StyleConstants.setUnderline(attr, !isCurrentlyUnderlined);
        }
        else {
            StyleConstants.setBold(attr, false);
            StyleConstants.setItalic(attr, false);
            StyleConstants.setUnderline(attr, false);
        }
        doc.setCharacterAttributes(start, end - start, attr, false);
    }
    private int currentFontSize = 12;
    private void setFontSize(int fontSize) {
        currentFontSize = fontSize;  
        StyleConstants.setFontSize(defaultAttributes, fontSize);
        textPane.setCharacterAttributes(defaultAttributes, false);
    }
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        switch (command) {
            case "New":
                file.newFile();
                break;
            case "Open":
                file.open();
                break;
            case "Save":
                file.save();
                break;
            case "Save Copy":
                file.download();
                break;
            case "Exit":
                file.exit();
                break;
            default:
                break;
        }

    }
    public void createAlignmentMenu() {
        JMenu menuAlign = new JMenu("Alignment");
        
        JMenuItem alignLeft = new JMenuItem("Left");
        alignLeft.addActionListener(e -> setAlignment(StyleConstants.ALIGN_LEFT));
        menuAlign.add(alignLeft);
        
        JMenuItem alignCenter = new JMenuItem("Center");
        alignCenter.addActionListener(e -> setAlignment(StyleConstants.ALIGN_CENTER));
        menuAlign.add(alignCenter);
        
        JMenuItem alignRight = new JMenuItem("Right");
        alignRight.addActionListener(e -> setAlignment(StyleConstants.ALIGN_RIGHT));
        menuAlign.add(alignRight);
        
        JMenuItem alignJustify = new JMenuItem("Justify");
        alignJustify.addActionListener(e -> setAlignment(StyleConstants.ALIGN_JUSTIFIED));
        menuAlign.add(alignJustify);

    }

    private void setAlignment(int alignment) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet alignmentAttr = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignmentAttr, alignment);
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        doc.setParagraphAttributes(start, end - start, alignmentAttr, false);
    }

    public JTextPane getTextPane() {
        return textPane;
    }
}