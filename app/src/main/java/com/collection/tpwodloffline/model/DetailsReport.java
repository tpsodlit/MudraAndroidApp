package com.collection.tpwodloffline.model;

public class DetailsReport {
    private String date="";
    private String month="";
    private String consumerAccount="";
    private String amountReceived="";
    private String mrNumber="";
    private String receiptDate="";
    private String reportType="";
    private String consumerName="";



    public DetailsReport(String date, String month, String consumerAcoount, String amountReceived, String mrNumber, String receiptDate,String reportType,String consumerName) {
        this.date = date;
        this.month = month;
        this.consumerAccount = consumerAcoount;
        this.amountReceived = amountReceived;
        this.mrNumber = mrNumber;
        this.receiptDate = receiptDate;
        this.reportType=reportType;
        this.consumerName=consumerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getConsumerAccount() {
        return consumerAccount;
    }

    public void setConsumerAccount(String consumerAccount) {
        this.consumerAccount = consumerAccount;
    }

    public String getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(String amountReceived) {
        this.amountReceived = amountReceived;
    }

    public String getMrNumber() {
        return mrNumber;
    }

    public void setMrNumber(String mrNumber) {
        this.mrNumber = mrNumber;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }
}
