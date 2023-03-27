package com.collection.tpwodloffline.model;

public class ResponseModel {
    private int statusCode;
    private int userid;
    private String issueFrom;
    private String issueTo;
    private float balance;


    // Getter Methods

    public int getStatusCode() {
        return statusCode;
    }

    public int getUserid() {
        return userid;
    }

    public String getIssueFrom() {
        return issueFrom;
    }

    public String getIssueTo() {
        return issueTo;
    }

    public float getBalance() {
        return balance;
    }

    // Setter Methods

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setIssueFrom(String issueFrom) {
        this.issueFrom = issueFrom;
    }

    public void setIssueTo(String issueTo) {
        this.issueTo = issueTo;
    }
}
