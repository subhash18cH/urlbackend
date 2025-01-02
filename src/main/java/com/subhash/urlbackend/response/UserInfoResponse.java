package com.subhash.urlbackend.response;


public class UserInfoResponse {

    private Integer id;

    private String userName;

    private String email;

    public UserInfoResponse(Integer id, String email, String userName) {
        this.id = id;
        this.email = email;
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
