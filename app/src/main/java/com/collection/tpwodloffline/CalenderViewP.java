package com.collection.tpwodloffline;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.collection.tpwodloffline.activity.PaySummary;

import java.util.Calendar;

public class CalenderViewP extends AppCompatActivity {
    CalendarView simpleCalendarView;
    private String vstrCons_no = "";
    private String vstrpayamt ="";
    private String vstrchqno = "";
    private String vstrchqdt = "";
    private String Paymode = "";
    private String BankName = "";
    private String BankID = "";
    private String custID="";
    private String TransID="";
    private String SelChoice="";
    private String BalFetch="";
    private String namefetch="";
    private String MobileNofetch="";
    private CalendarView calendarView;
    private TextView dateDisplay;
    private static TextView strcdate ;
    private  Bundle pmtsmrytemp1 =null;
    private String bankNameOther="";
    //  private String autoSelectDate="";
    String strdate="";
    private String micrNo="";
    private  String fromScreen="";
    private String moneyNo="";
    private String fromActivity="";
    private boolean manual;

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_view_p);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        strcdate=(TextView)findViewById(R.id.cdate);
        pmtsmrytemp1 = new Bundle();
        Button submtbtn = (Button) findViewById(R.id.submtbtn);
        try{
            Bundle pmtsmrytemp = getIntent().getExtras();
            vstrCons_no = pmtsmrytemp.getString("vstrCons_no");
            vstrpayamt = pmtsmrytemp.getString("vstrpayamt");
            vstrchqno = pmtsmrytemp.getString("vstrchqno");
            vstrchqdt = pmtsmrytemp.getString("vstrchqdt");
            Paymode = pmtsmrytemp.getString("Paymode");
            BankName = pmtsmrytemp.getString("BankName");
            BankID = pmtsmrytemp.getString("BankID");
            custID= pmtsmrytemp.getString("custID");
            TransID= pmtsmrytemp.getString("TransID");
            SelChoice= pmtsmrytemp.getString("SelChoice");
            BalFetch= pmtsmrytemp.getString("BalFetch");
            namefetch= pmtsmrytemp.getString("namefetch");
            MobileNofetch=pmtsmrytemp.getString("MobileNofetch");
            bankNameOther=pmtsmrytemp.getString("otherBankName");
            micrNo=pmtsmrytemp.getString("micr_no");
            moneyNo=pmtsmrytemp.getString("moneyId");
            fromActivity=pmtsmrytemp.getString("from");
            manual=pmtsmrytemp.getBoolean("manual");

            fromScreen=pmtsmrytemp.getString("from");

            System.out.println("dcv=="+fromScreen);
            // autoSelectDate=pmtsmrytemp.getString("autoSelectDate");
            strdate=vstrchqdt;


            Log.d("DemoApp", " vstrCons_no   " + vstrCons_no);
            Log.d("DemoApp", " vstrpayamt   " + vstrpayamt);
            Log.d("DemoApp", " vstrchqno   " + vstrchqno);
            Log.d("DemoApp", " vstrchqdt   " + vstrchqdt);
        }catch(Exception e){e.printStackTrace();}
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        // dateDisplay = (TextView) findViewById(R.id.date_display);
//        dateDisplay.setText("Date: ");



        try {

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, 2);
            calendarView.setMaxDate(cal.getTimeInMillis());
            cal.add(Calendar.MONTH, -3);
            calendarView.setMaxDate(System.currentTimeMillis() - 1000);

         /*   if (Paymode.equalsIgnoreCase("money")){
                int yy = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH);
                int dd = cal.get(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.MONTH, mm);
                cal.set(Calendar.DAY_OF_MONTH, dd-10);
                cal.set(Calendar.YEAR, yy );
                long now = System.currentTimeMillis() - 1000;
                calendarView.setMinDate(now-(1000*60*60*24*10));
               // dp_time.setMaxDate(now+(1000*60*60*24*7));


            }*/



            //   cal.add(Calendar.MONTH, 1);



            // calendarView.setDate( cal.getTimeInMillis(),false,true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // perform setOnDateChangeListener event on CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            // display the selected date by using a toast
            //Toast.makeText(getApplicationContext(), dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
            //  String strdate = dayOfMonth + "-" + month + "-" + year;
            //  Log.d("DemoApp", " strdate   " + strdate);

            //   int strmonth = month;
            //   Log.d("DemoApp", " strmonth   " + strmonth);

            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                // dateDisplay.setText("Date: " + i2 + " / " + i1 + " / " + i);

                Toast.makeText(getApplicationContext(), "Selected Date:" + i2  + (i1+1) + i, Toast.LENGTH_LONG).show();

                i1=i1+1;
                if(i2<10){
                    strdate="0"+i2;
                }else{
                    strdate=Integer.toString(i2);
                }
                if(i1<10){
                    strdate=strdate+"0"+i1;
                }else{
                    strdate=strdate+i1;
                }
                strdate = strdate + i;


                strcdate.setText("you ve selected:"+strdate);
            }

            /*
                if (strmonth != 0) {
                     Intent intentback = new Intent(getApplicationContext(), PaySummary.class);

                    intentback.putExtras(pmtsmrytemp1);
                     startActivity(intentback);

                }
            }*/
        });



        submtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent   intentback;
                System.out.println("dcv=="+fromScreen);

                if (fromScreen.equalsIgnoreCase("non_account")){
                    intentback = new Intent(getApplicationContext(), PaySummaryNonAcnt.class);

                }
                else {
                    intentback = new Intent(getApplicationContext(), PaySummary.class);

                }

                pmtsmrytemp1.putString("vstrCons_no", vstrCons_no);
                pmtsmrytemp1.putString("vstrpayamt", vstrpayamt);
                pmtsmrytemp1.putString("vstrchqno", vstrchqno);
                pmtsmrytemp1.putString("vstrchqdt", strdate);
                pmtsmrytemp1.putString("Paymode", Paymode);
                pmtsmrytemp1.putString("BankName", BankName);
                pmtsmrytemp1.putString("BankID", BankID);
                pmtsmrytemp1.putString("strdate", strdate);
                pmtsmrytemp1.putString("strID", "1");
                pmtsmrytemp1.putString("custID", custID);
                pmtsmrytemp1.putString("TransID", TransID);
                pmtsmrytemp1.putString("SelChoice", SelChoice);
                pmtsmrytemp1.putString("BalFetch", BalFetch);
                pmtsmrytemp1.putString("namefetch", namefetch);
                pmtsmrytemp1.putString("MobileNofetch", MobileNofetch);
                pmtsmrytemp1.putString("otherBankName",bankNameOther);
                pmtsmrytemp1.putString("micr_no",micrNo);
                pmtsmrytemp1.putString("moneyId",moneyNo);
                pmtsmrytemp1.putString("from",fromActivity);
                pmtsmrytemp1.putBoolean("manual",manual);
                pmtsmrytemp1.putBoolean("firstTime",false);

                if (pmtsmrytemp1!=null){
                    intentback.putExtras(pmtsmrytemp1);
                }
                startActivity(intentback);
                finish();
            }
        });
    }
}
