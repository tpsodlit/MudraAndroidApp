package com.collection.tpwodloffline.nonenergy;


public class NonenModel {

    String Cname = "";
    String Scnum = "";
    String Ref = "";
    String Payable = "";
    String Module = "";

    public NonenModel(String name, String scnum, String payable, String ref, String Module) {
        this.Cname = name;
        this.Payable = payable;
        this.Scnum = scnum;
        this.Ref = ref;
        this.Module = Module;

    }
    public String getModule() {
        return Module;
    }
    public void setModule(String Module) {
        this.Module = Module;
    }

    public String getRef() {
        return Ref;
    }
    public void setRef(String Ref) {
        this.Ref = Ref;
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
