package com.example.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileNamingUI extends JFrame {
    private JTextField fileNameField;
    private JButton newButton;       
    private JPanel panel;         
    private JLabel fileNameLabel;   

    public FileNamingUI() {
        setTitle("File Naming UI");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        fileNameField = new JTextField(20);
        newButton = new JButton("New");
        fileNameLabel = new JLabel("Enter File Name: ");
        panel = new JPanel();

        panel.setLayout(new FlowLayout());
        panel.add(newButton);
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showFileNamingBox();
            }
        });

        add(panel);
        setVisible(true);
    }

    private void showFileNamingBox() {
        JDialog namingDialog = new JDialog(this, "Name Your File", true);
        namingDialog.setLayout(new FlowLayout());
        namingDialog.setSize(300, 150);
        namingDialog.setLocationRelativeTo(this);  
        namingDialog.add(fileNameLabel);
        namingDialog.add(fileNameField);
        JButton confirmButton = new JButton("Confirm");
        namingDialog.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fileName = fileNameField.getText();
                if (!fileName.isEmpty()) {
                    JOptionPane.showMessageDialog(FileNamingUI.this, "File Name: " + fileName);
                } else {
                    JOptionPane.showMessageDialog(FileNamingUI.this, "Please enter a file name!");
                }
                namingDialog.dispose();  
            }
        });
        namingDialog.setVisible(true);
    }
}


