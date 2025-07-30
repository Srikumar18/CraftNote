package com.example.app;

public class User {
    private static String name;
    private static String emailid;
    public static void setName(String username){
        name=username;
    }
    public static void setEmailid(String mailid){
        emailid=mailid;
    }
    public static String getName(){
        return name;
    }
    public static String getEmailid(){
        return emailid;
    }
}
