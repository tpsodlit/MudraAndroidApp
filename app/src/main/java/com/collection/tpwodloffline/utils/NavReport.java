package com.collection.tpwodloffline.utils;


public class NavReport {

    String Cname = "";
    String Clat = "";
    String Clang = "";
    String Scnum = "";
    String Payable = "";
    Double distance=0.0;

    public NavReport(String name, String clat, String clang, String scnum, String payable, Double distance) {
        this.Cname = name;
        this.Clat = clat;
        this.Clang = clang;
        this.Scnum = scnum;
        this.Payable = payable;
        this.distance = distance;

    }
    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double Cname) {
        this.distance = distance;
    }

    public String getCname() {
        return Cname;
    }
    public void setCname(String Cname) {
        this.Cname = Cname;
    }

    public String getClat() {
        return Clat;
    }

    public void setClat(String Clat) {
        this.Clat = Clat;
    }

    public String getClang() {
        return Clang;
    }

    public void setClang(String Clang) {
        this.Clang = Clang;
    }

    public String getScnum() {
        return Scnum;
    }

    public void setScnum(String Scnum) {
        this.Scnum = Scnum;
    }

    public String getPayable() {
        return Payable;
    }

    public void setPayable(String Payable) {
        this.Payable = Payable;
    }


}
