package com.example.hmacauth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "User_")
public class LiferayUser {

    @Id
    @Column(name = "userId")
    private Long userId;

    @Column(name = "screenName")
    private String screenName;

    @Column(name = "emailAddress")
    private String emailAddress;

    @Column(name = "password_")
    private String password;

    @Column(name = "passwordEncrypted")
    private boolean passwordEncrypted;

    @Column(name = "status")
    private int status;

    public Long getUserId() {
        return userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPasswordEncrypted() {
        return passwordEncrypted;
    }

    public int getStatus() {
        return status;
    }
}
