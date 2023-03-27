package com.collection.tpwodloffline.utils;

public class Constants {
    public static final String userIdPref = "usrId";
    public static final String passwordPref = "pwdVal";
    public static final String datePref = "sysDate";
    public static final String isFirstTimeLoginPref = "isFirstTime";
    public static final String isDataSynced = "isDataSynced";
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;


    //OTS implementation
    public static final String confTitleDialog = "Confirmation";
    public static final String otsConfTitleDialog = "OTS Confirmation";
    public static final String confTitleDialogExtra = confTitleDialog + " \n₹";
    public static final String confTitleDialogExtraAdv = " as Advance Payment";

    public static final String msgPayfirst = "You have collected ₹";
    public static final String msgPaylast = " from consumer.";
    public static final String btnOTSPrint = "OTS Print";
    public static final String bodyMsgOTSNormalPrint = "Reprint OTS / Current. Please " +
            "select one of them.";
    public static final String btnPrint = "Print";
    public static final String btnOTSAmnt = "OTS";
    public static final String btnCurrAmnt = "Current";

    public static final String title = "Collection limit";
    public static final String titleError = "Error Occurred";
    public static final String msgError = "Intimate the case to IT " +
            "center" + "\n" + "for checking";

    public static final String msg_one = "You can collect only once in a day from one " +
            "consumer.";
    public static final String msg_two = "You can collect only two times in a day from one " +
            "consumer.";
    public static final String msg = "You can collect after sync pending data for this consumer.";
    public static final String titleBal = "Balance Not Available";
    public static final String msgBal = "Deposit Cash and Contact " +
            "Divisional / Agency" + "\n " + "Finance Section";
    public static final String titleWarning = "Warning..!";

    public static final String btnOk = "Ok";
    public static final String btnClose = "Close";
    public static final String amountLessThan = "Amount cannot be less than 0";

//    public static final String msgPayfirst = "You have collected ₹";
//    public static final String msgPaylast = " from consumer.";

    public static String msgWithPayableExtraTitle(String payableAmount) {
        return confTitleDialogExtra + payableAmount + confTitleDialogExtraAdv;
    }

    public static final String bodyMsgOTSORNormal = "Collect Current / OTS Amount. Please " +
            "select one of them.";

    public static final String titleCurrentDate = "Please Check Current Date";
    public static final String bodyMsgDate = "Change the Date and try again !!";

    public static String msgWithPayable(String payableAmount) {
        return msgPayfirst + payableAmount + msgPaylast;
    }

    public static final String btnConfirm = "Confirm";
    public static final String btnCancel = "Cancel";

    public static final String titleEnableData = "Enable Data";
    public static final String bodyEnableData = "Enable Data & Retry";

    //Ezetap api Constants
    public static final String division = "division";
    public static final String cons_no = "cons_no";
    public static final String cons_name = "cons_name";
    public static final String cons_mobile = "cons_mobile";
    public static final String cons_latLong = "cons_latLong";
    public static final String localTid = "localTid";
    public static final String paymentID = "paymentID";
    public static final String agentID = "agentID";
    public static final String franchiseID = "franchiseID";
    public static final String localamount = "localamount";
    public static final String txnAmount = "txnAmount";
    public static final String txnDate = "txnDate";
    public static final String txnStatus = "txnStatus";
    public static final String txnID = "txnID";
    public static final String paymentMode = "paymentMode";
    public static final String recDate = "recDate";
    public static final String error = "error";
    public static final String deviceId = "deviceId";
}
