**CraftNote \- Document Editor**  
**Overview**

This is a collaborative text editor built for multi-users to edit a document in real-time. It is enhanced with features such as authentication of the users, document editing with the options of text formatting, cloud storage integration via Google Drive, and collaboration functionality. The application uses Firebase for user authentication and document sharing, besides integrating with Google Drive for cloud storage.

**Table of Contents**  
1\. Installation Instructions  
2\. Project Structure  
3\. How to Run the Project  
4\. Dependencies  
5\. Usage  
6\. Future Improvements  
7\. Contributors  
8\. License

**1\. Installation Instructions**

To run the project locally, please read the following instructions.

Prerequisites:

Make sure you have the following software installed:

  \- Java (JDK 8 or above): Because this project uses Java, ensure the Java Development Kit (JDK) is installed.  
\- Download JDK (https://adoptopenjdk.net/)  
\- Maven: The project uses Maven for dependency management and building.  
\- Download Maven (https://maven.apache.org/install.html)  
\- Google Account: For integration with the Google Drive API  
\- Firebase Account: For Firebase Authentication and real-time data management  
Setup Procedure  
To Set Up:  
1\. Clone the Repository  
   Clone the project repository from GitHub using Git.  
   git clone https://github.com/your-username/your-project-repository.git  
   cd your-project-repository  
2\. Install Java and Maven:  
   
  Follow the official installation guides to get Java and Maven onto your machine.   
  For checking the Java version:

java \-version  
   For checking Maven version:  
     mvn \-version  
3\. Google Drive API Setup:

   Create a project in the Google Cloud Console and enable the Google Drive API.  
   Download the OAuth 2.0 credentials JSON file (for example, credentials.json).  
   Put the credentials.json file at the top level of your project directory.  
4\. Firebase Setup:

\- Create a Firebase project from the Firebase Console (https://console.firebase.google.com/).  
   Setup Firebase Authentication and Realtime Database.  
   Download the Firebase Admin SDK JSON file and place it in your project directory, like firebase-adminsdk.json.  
5\. Maven Dependency Setup:

\- The necessary dependencies, such as the Google Drive API and Firebase SDK, must be in the pom.xml file. When running Maven, it will automatically add all of the required dependencies.  
6\. Maven Build:

   Run the Maven Build to download necessary dependencies:  
   mvn clean install

**2\. Project Structure**

Below is a general overview of how the project is structured:

your-project-directory/

│

├── com/

│   └── example/  
│       ├── ui/            \# User Interface (Login, Registration, Document Editing)  
│       ├── service/       \# Core business logic (Document Handling, Firebase/Google API interactions)  
│       ├── dao/           \# Data access layer (File storage, Firebase interaction)  
│       ├── app/           \# Application entry point (Main class, User class)  
│  
├── target/                \# Compiled .class files and build artifacts  
├── src/                   \# Source code directory  
│  
├── credentials.json       \# Google API credentials file  
├── firebase-adminsdk.json \# Firebase Admin SDK credentials file  
├── pom.xml                 \# Maven project file  
\- com.example.ui: Classes related to user interface, such as the Login page, registration, and document editor.  
\- com.example.service: Business logic on how to handle files, store documents and integrate with Google Drive/ Firebase  
\- com.example.dao: Data access classes, including interactions with Firebase and document file storage.  
\- com.example.app: files for the main application: the entry point (Main.java), user model (User.java), and so on.  
\- replace the paths of the two json files as per the path in your device

**3\. How to Run the Project**

1\. Launching an Application  
   The Main.java file, which is the main entry point of the application, finds itself in the package com.example.app. You can run your application from the command line or an IDE such as IntelliJ IDEA or Eclipse. Following the building of the project, you can launch the application from the terminal as follows:

mvn exec:java \-Dexec.mainClass="com.example.app.Main"

2\. Login / Register:

   \- To login use the login screen using your Firebase credentials  
   \- In case you are a new user, the registration screen can be used to create an account (email, password).  
3\. Document Editing:

   After login:  
   You can  
   \- Create a new document, or  
   open an existing one.  
\- Format the document with text formatting options such as bold, italics, underline, font size, and font color.  
4\. Collaborate:

   Be able to invite collaborators to work on the document. The system permits inviting people through the username.  
Collaborators may edit the document after accepting the invite.  
5\. Storage of Google Drive

\- Documents are saved to your Google Drive using the Google Drive API.  
  You can open and save documents directly from/to Google Drive.

**4\. Dependencies**

This project relies on the following key dependencies:

\- Google Drive API: To store and retrieve documents in the cloud.  
\- Firebase SDK: User authentication, real-time database, and sharing of documents.  
\- Swing (Java GUI): This will help us create a graphical user interface (GUI) for the text editor.  
\- JUnit: This is for unit testing application functionality  
Both of these dependencies are then handled using Maven and downloaded automatically when we build the project using mvn clean install

**5\. Usage**

Once built, we can run the application and do the following:

\- Register or Log in with Firebase Authentication.  
\- Create, edit, and format documents (supports text formatting options like bold, italics, underline, font size, and color).  
\- Ask others to collaborate on the document by entering their username.  
\- Save and load documents from Google Drive.  
\- Be able to manage document errors well with proper use of exception handling.

**6\. Future Enhancements**

Some possible future features that can be added:

1\. Real-time Collaboration: Enable multiple users to edit documents simultaneously based on live updates and change tracking, much like Google Docs.  
2\. Support of 2-factor authentication or Twofactor verification: providing users with an additional layer of security for their accounts  
3\. Enhanced formatting: support for formatting advanced document features, including tables, headers, footers, images, and hyperlinks  
4\. Custom Themes / Skins: support diverse themes in the editor.  
5\. AI Integration: Incorporate features such as spell check, grammar correction, and text suggestion.

**7\. Contribution**

Srikumar V
Sandhiya R  
Sowmya Anand  

**8\. License**

This project is All Rights Reserved. You may not use, distribute, or modify this project without permission.

**Note:**

\- If you encounter any issues during the setup or usage, please don't hesitate to open an issue in the repository.  
\- For further clarification on any of the steps, refer to the Google API documentation or Firebase documentation for troubleshooting and configuration help.
