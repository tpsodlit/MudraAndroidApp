package com.collection.tpwodloffline.model;

public class EnergyModel {
    String Cname = "";
    String Scnum = "";
    String Payable = "";

    public EnergyModel(String name, String scnum, String payable) {
        this.Cname = name;
        this.Payable = payable;
        this.Scnum = scnum;
    }

    public String getCname() {
        return Cname;
    }

    public void setCname(String Cname) {
        this.Cname = Cname;
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
