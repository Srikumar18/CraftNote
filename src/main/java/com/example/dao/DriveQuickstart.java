package com.example.dao;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriveQuickstart {
    private static final String APPLICATION_NAME = "Drive API Java Project";
    @SuppressWarnings("deprecation")
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/Users/srikumarv/Downloads/CraftNote-13-32-35/project1-440709-1d87874ad0c7.json";

    public static Drive getDriveService(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static String uploadFile(Drive service, String filePath, String folderId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(new java.io.File(filePath).getName());
        fileMetadata.setParents(Collections.singletonList(folderId));

        java.io.File filePathToUpload = new java.io.File(filePath);
        FileContent mediaContent = new FileContent("application/vnd.openxmlformats-officedocument.wordprocessingml.document", filePathToUpload);

        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        return file.getId();
    }

    public static File findFileInFolder(Drive service, String fileName, String folderId) throws IOException {
        String query = "name='" + fileName + "' and '" + folderId + "' in parents and trashed=false";
        Drive.Files.List request = service.files().list()
                .setQ(query)
                .setFields("files(id, name)");
        
        List<File> files = request.execute().getFiles();
        if (!files.isEmpty()) {
            return files.get(0);
        }
        System.out.println("File not found in folder.");
        return null;
    }
    public static void replaceFile(Drive service, String fileId, String newFilePath) throws IOException {
        java.io.File newFile = new java.io.File(newFilePath);
        FileContent mediaContent = new FileContent("application/vnd.openxmlformats-officedocument.wordprocessingml.document", newFile);

        service.files().update(fileId, null, mediaContent).execute();
    }
    public static String getOrCreateUserFolder(Drive service, String username) throws IOException {
        String documentsFolderId = DriveQuickstart.findOrCreateFolder(service, "Documents", null);
        return DriveQuickstart.findOrCreateFolder(service, username, documentsFolderId);
    }
    public static String findOrCreateFolder(Drive service, String username, String parentId) throws IOException {
        String query = "name='" + username + "' and mimeType='application/vnd.google-apps.folder' and trashed=false";
        if (parentId != null) {
            query += " and '" + parentId + "' in parents";
        }
        Drive.Files.List request = service.files().list()
            .setQ("name='" + username + "' and mimeType='application/vnd.google-apps.folder' and trashed=false")
            .setFields("files(id, name)");
        
        List<File> folders = new ArrayList<File>();
        folders=request.execute().getFiles();
    
        if (!folders.isEmpty()) {
            return folders.get(0).getId();
        } else {
            File fileMetadata = new File();
            fileMetadata.setName(username);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            if (parentId != null) {
                fileMetadata.setParents(Collections.singletonList(parentId));
            }
            File folder = service.files().create(fileMetadata)
                .setFields("id")
                .execute();
            return folder.getId();
        }
    }
    public static void shareFolder(Drive service, String emailAddress) throws IOException {
        String documentsFolderId = findOrCreateFolder(service, "Documents", null);
        Permission permission = new Permission()
                .setType("user")  
                .setRole("writer") 
                .setEmailAddress(emailAddress);  
        service.permissions().create(documentsFolderId, permission)
                .setFields("id")  
                .execute();  
    
        System.out.println("Folder shared with: " + emailAddress);
    }
    public static List<String> getDocxFilesInFolder(Drive service, String username, String folderId) throws IOException {
        List<String> docNames = new ArrayList<>();
        String query = "name='" + username + "' and mimeType='application/vnd.google-apps.folder' and trashed=false";
        FileList result = service.files().list().setQ(query).setSpaces("drive").execute();

        for (File file : result.getFiles()) {
            docNames.add(file.getName());
        }
        return docNames; 
    }
}