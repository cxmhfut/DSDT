package com.zy.dsdt.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table USER.
 */
public class User {

    private String uno;
    private String uname;
    private String upassword;
    private String uclass;
    private String uclassname;
    private String uschool;
    private String uteacher;
    private Integer upermission;
    private Integer ucredit;

    public User() {
    }

    public User(String uno) {
        this.uno = uno;
    }

    public User(String uno, String uname, String upassword, String uclass, String uclassname, String uschool, String uteacher, Integer upermission, Integer ucredit) {
        this.uno = uno;
        this.uname = uname;
        this.upassword = upassword;
        this.uclass = uclass;
        this.uclassname = uclassname;
        this.uschool = uschool;
        this.uteacher = uteacher;
        this.upermission = upermission;
        this.ucredit = ucredit;
    }

    public String getUno() {
        return uno;
    }

    public void setUno(String uno) {
        this.uno = uno;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    public String getUclass() {
        return uclass;
    }

    public void setUclass(String uclass) {
        this.uclass = uclass;
    }

    public String getUclassname() {
        return uclassname;
    }

    public void setUclassname(String uclassname) {
        this.uclassname = uclassname;
    }

    public String getUschool() {
        return uschool;
    }

    public void setUschool(String uschool) {
        this.uschool = uschool;
    }

    public String getUteacher() {
        return uteacher;
    }

    public void setUteacher(String uteacher) {
        this.uteacher = uteacher;
    }

    public Integer getUpermission() {
        return upermission;
    }

    public void setUpermission(Integer upermission) {
        this.upermission = upermission;
    }

    public Integer getUcredit() {
        return ucredit;
    }

    public void setUcredit(Integer ucredit) {
        this.ucredit = ucredit;
    }

}
