package com.aariyan.imo_admin.Model;

public class UserModel {
    private String userId,userName,userPhone,userProfession,userPoints;

    public UserModel(){}

    public UserModel(String userId, String userName, String userPhone, String userProfession, String userPoints) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userProfession = userProfession;
        this.userPoints = userPoints;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserProfession() {
        return userProfession;
    }

    public void setUserProfession(String userProfession) {
        this.userProfession = userProfession;
    }

    public String getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(String userPoints) {
        this.userPoints = userPoints;
    }
}
