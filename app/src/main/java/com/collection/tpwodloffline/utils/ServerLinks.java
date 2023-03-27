package com.collection.tpwodloffline.utils;

public class ServerLinks {

    //public static String baseUrl = "http://collectionapi.tpwesternodisha.com/";
    public static String baseUrl = "http://staging.tpwesternodisha.com/";

    //https://services.tpsouthernodisha.com/api/Misc/PaymentDetais?CompID=3&ConsRef=2922011230&ConsNo=215101140053&BillMth=202104
    public static String baseUrl_payments = "https://services.tpsouthernodisha.com/api/Misc/PaymentDetais?";

    public static String collAppAuth = baseUrl+"collAppAuth.aspx?";
    public static String mcollection1 = baseUrl+"mcollection1.aspx?";

    //public static String DynamicReport = baseUrl+"DynamicReport.aspx?"; //1.0
    //public static String DynamicReport = baseUrl+"DynamicReport_v2.aspx?"; //2.0
    public static String download_data = baseUrl+"api/downloadData?"; //3.0
    public static String download_OTSdata = baseUrl+"api/fetchOTSDataFromFG?";
    public static String DynamicReportNe = baseUrl+"DynamicReport_ne.aspx?";// Non-Energy API

    //public static String CollInfo = baseUrl+"CollInfo.aspx?";//archived
    public static String postPayment = baseUrl+"postPayment.aspx?";
    public static String postPayment_ne = baseUrl+"postPayment_ne.aspx?";

    public static String postPaymentOTS = baseUrl + "postPayment_ots.aspx?";

    public static String ValidityExtend = baseUrl+"ValidityExtend.aspx?";
    public static String deniedConsumer = baseUrl+"deniedConsumer.aspx?";
    public static String logData = baseUrl+"logData.aspx?";

    public static String fetchBalance = baseUrl+"api/fetchBalance?";

    public static String ezytapUrl = baseUrl + "api/ezytabpayment";
    public static String BillName = "MONEY RECEIPT.";
    public static String BillName2 = "MONEY RECEIPT";

    public static String ezeTapName = "PAX";
    public static String ezeTapModelSBI = "X990";
    public static String ezeTapModelHDFC = "A910";

    public static String ReleaseDate = "090323";
}
