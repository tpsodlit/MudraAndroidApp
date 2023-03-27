package com.collection.tpwodloffline;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.collection.tpwodloffline.activity.CollectionDashBoard;
import com.cesu.internal.ConfigLoad;
import com.cesu.internal.GlobalData;
import com.cesu.internal.MovingFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class CollUtilitiesActivity extends AppCompatActivity {
    public static Connection dbConnection = null;
    public static GlobalData GlobalDataObj = null;
    public static ConfigLoad ConfigLoadObj = null;
    // private static int totrec=0;
    public static int print_flag=1;
    //public static String strInputURL = "http://portal.tpcentralodisha.com:8087/SBM_RT_OUT/";
    // public static String strInputFilePath = ".\\Downloads\\";
    public static String strInputFilePath = Environment.getExternalStorageDirectory()+"/cesuapp/input/";//here the input text file to be placed
    //public static String strInputFileName = "RT_IN.TXT";
    //public static String strInputFileName = "RT011215.TXT";
    // public static String strOutputFilePath = ".\\Uploads\\";
    public static String strOutputFilePath = Environment.getExternalStorageDirectory()+"/cesuapp/output/";//here the output text file to be placed
    public static String strOutputFileName = "RT_OUT.TXT";
    public static String strOutputHistFilePath = "/cesuapp/history/";
    private static String tasktype="";
    private static int totrec=0;
    private static int progressStatus = 0;
    private static Handler handler = new Handler();
    private static ProgressBar pb=null;
    private static TextView tv=null;
    private static TextView msg1=null;
    private static TextView msg2=null;
    private static TextView RTNm1=null;
    private static TextView RTNm2=null;
    private static TextView RTNm3=null;
    private static TextView RTNm4=null;
    private static   DatabaseAccess databaseAccess=null;
    private static int count=0;
    private static BufferedReader bufRead=null;
    private static  String strLine=null;
    private static ArrayList<String> listtFilefound = new ArrayList<String>();
    private static int totfile=0;
    private static String filefound="";
    static int fincnt=0;
    private static Context context ;
    private static int progrcnt=1;
    Button backbtn=null;
    Button dwldbtn=null;
    int filecount=0;
    String loadfilename=null;
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coll_utilities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Get the widgets reference from XML layout
        pb = (ProgressBar) findViewById(R.id.pb);
        tv = (TextView) findViewById(R.id.tv);
        msg1 = (TextView) findViewById(R.id.Msg1);
        msg2 = (TextView) findViewById(R.id.msg2);
        RTNm1=(TextView) findViewById(R.id.RTNm1);
        RTNm2=(TextView) findViewById(R.id.RTNm2);
        RTNm3=(TextView) findViewById(R.id.RTNm3);
        RTNm4=(TextView) findViewById(R.id.RTNm4);
        tv.setText("Click to Start");
        backbtn = (Button) findViewById(R.id.DwldBack);
        backbtn.setVisibility(Button.GONE);
        dwldbtn = (Button) findViewById(R.id.btn);
        dwldbtn.setVisibility(Button.VISIBLE);
        dwldbtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           //////////////////////////////
                                           totrec=0;
                                           progrcnt=1;//added on 09.06.2016
                                           loadfilename=null;//added on 09.06.2016
                                           tv.setText("Start DownLoading");
                                           Log.d("DemoApp", "Entering SBM Utilities");
                                           Bundle Dwnldval = getIntent().getExtras();
                                           String Param = Dwnldval.getString("param");
                                           String Paramtype1 = Dwnldval.getString("paramtype");
                                           tasktype="";
                                           tasktype=Param;//2 for collection
                                           Log.d("DemoApp", "Entering SBM Utilities" + Param);
                                           Log.d("DemoApp", "Entering SBM Utilities" + Paramtype1);
                                           //  ArrayList<String> listtFilefound = new ArrayList<String>();
                                           long lTimeStart = System.currentTimeMillis();
                                           //getDBConnectionSqlLite();
                                           try {
                                               // GlobalData.ConfigDataHash=ConfigLoad.ConfigLoad();
                                               //  print_flag=Integer.parseInt(GlobalData.ConfigDataHash.get("PRINT_FLAG").toString());;
                                               // dbConnection = CreateConnection.getConnectionSqlLite(GlobalData.ConfigDataHash);
                                           } catch (Exception e1) {
                                               e1.printStackTrace();
                                           }
                                           if (Param.equals("3") && Paramtype1.equals("I")) {
                                               tv.setText("Start DownLoading");
                                               progressStatus = 0;
                                               fincnt=0;
                                               filecount=0;
                                               //DownloadFileHTTP.DownloadFileHTTP(strInputURL, strInputFileName, strInputFilePath);
                                               listtFilefound = MovingFile.checkFile(strInputFilePath, ".TXT", "CL");
                                               Log.d("DemoApp", "2 Inside SBM " + listtFilefound.size());
                                               String filelist []=new String [listtFilefound.size()];//added 09.02.2016
                                               if (listtFilefound.size() > 0) {
                                                   for (int j = 0; j < listtFilefound.size(); j++) {
                                                       Log.d("DemoApp", "21 Inside SBM " + listtFilefound.get(j));
                                                       filelist [j]=listtFilefound.get(j);//added 09.02.2016
                                                       //SQLiteBulkDataIn.Import_All(strInputFilePath, strInputFileName);
                                                       //added by santi to know the total records in a file to maintain progress bar 01.01.2016
                                                       try {

                                                           LineNumberReader lnr = new LineNumberReader(new FileReader(new File(strInputFilePath + listtFilefound.get(j))));
                                                           lnr.skip(Long.MAX_VALUE);
                                                           totrec = totrec + (lnr.getLineNumber()+1);
                                                           Log.d("DemoApp", "row num is =" + totrec);//Add 1 because line index starts at 0
                                                           if(j==0) {
                                                               RTNm1.setText(listtFilefound.get(j)+"="+lnr.getLineNumber()+" records");
                                                           }
                                                           if(j==1) {
                                                               RTNm2.setText(listtFilefound.get(j)+"="+lnr.getLineNumber()+" records");
                                                           }
                                                           if(j==2) {
                                                               RTNm3.setText(listtFilefound.get(j)+"="+lnr.getLineNumber()+" records");
                                                           }
                                                           if(j==3) {
                                                               RTNm4.setText(listtFilefound.get(j)+"="+lnr.getLineNumber()+" records");
                                                           }
                                                           // Finally, the LineNumberReader object should be closed to prevent resource leak
                                                           lnr.close();

                                                       } catch (Exception e) {
                                                           Log.d("DemoApp", "row num is =" + e);
                                                       }
                                                       //end
                                                    /*   try {
                                                           databaseAccess = DatabaseAccess.getInstance(context);
                                                           databaseAccess.open();
                                                           totfile = j;
                                                           Log.d("DemoApp", "listtFilefound.get(j)"+listtFilefound.get(j));
                                                           Import_All(strInputFilePath, listtFilefound.get(j), totfile, totrec);
                                                           // databaseAccess.close();
                                                       } catch (Exception e) {
                                                           Log.d("DemoApp", "Error in Connection");
                                                       }*/
                                                       filecount=filecount+1;
                                                   }
                                                   // added on 09.06.2016
                                                   try {
                                                       databaseAccess = DatabaseAccess.getInstance(context);
                                                       databaseAccess.open();
                                                       Import_All(strInputFilePath, filelist, totfile, totrec);
                                                   }catch(Exception e){
                                                       Log.d("DemoApp", "Exception 123 "+e);
                                                   }

                                               }
                                               // Log.d("DemoApp", "totfile+1" + totfile + 1);
                                               Log.d("DemoApp", "listtFilefound.size()" + listtFilefound.size());

                                           } else if (Param.equals("1") && Paramtype1.equals("E")) {
                                               Log.d("DemoApp", "Entering 2");
                                               // SQLiteBulkDataOut.Export_All(strOutputFilePath, strOutputFileName);
                                           } else if (Param.equals("2") && Paramtype1.equals("O")) {
                                               //     SQLiteBulkDataOut.Export_One(args[1]);
                                           } else if (Param.equals("1") && Paramtype1.equals("C")) {
                                               //    CalculateBill.CalculateBill_All();
                                           } else if (Param.equals("1") && Paramtype1.length() == 8) {
                                               //   CalculateBill.CalculateBill(args[0]);
                                           } else {
                                           }
                                           long lTimeEnd = System.currentTimeMillis();
                                           double dTime = 1 + (lTimeEnd - lTimeStart) / 1000;
                                           Log.d("DemoApp", "  dTime =" + dTime);
                                       }

                                   }

        );//end
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reports = new Intent(getApplicationContext(), CollectionDashBoard.class);
                startActivity(reports);
                finish();
            }
        });

    }
    public void Import_All(String strInputFilePath, String strInputFileName[], int filecounter, int totrecord) throws SQLException
    {
        //  filefound = strInputFileName;
        //    String vv="";
        //     Log.d("DemoApp", "  strInputFileName[0] =" + strInputFileName[0]);//added on 09.06.2016
        //    Log.d("DemoApp", "  strInputFileName[1] =" + strInputFileName[1]);//added on 09.06.2016
        // new ProgressTask().execute(totrec,progrcnt);
        new ProgressTask().execute(strInputFileName);
    }
    // to handle download progress
    private class ProgressTask extends AsyncTask<String,Integer,Void> {

        protected void onPreExecute() {
            super.onPreExecute(); ///////???????
            //  prgs.setMax(100); // set maximum progress to 100.
            tv.setText("Start DownLoading Wait !!");
            pb.setMax(totrec - 1);
            fincnt=0;
            // progrcnt=1;
            Log.d("DemoApp", "  totrec =" + totrec);
            Log.d("DemoApp", "  fincnt =" + fincnt);
        }

        protected void onCancelled() {
            //  prgs.setMax(0); // stop the progress
            Log.v("Progress", "Cancelled");
        }

        protected Void doInBackground(String... totfilefound) {
            // int start=params[0];
            // int start1=params[1];
            loadfilename=null;

            //    Log.d("DemoApp", "  tofilefound[0] =" + totfilefound[0]);//added on 09.06.2016
            //    Log.d("DemoApp", "  tofilefound[1] =" + totfilefound[1]);//added on 09.06.2016
            int start=totrec;
            Log.d("DemoApp", "  start =" + start);
            Log.d("DemoApp", "  filecount =" + filecount);
            for(int i=0;i<filecount;i++) {
                if (i == 0) {
                    try {
                        SQLiteBulkDataIn.InitializeImport(tasktype);// deleting the data 2 for collection
                    } catch (Exception e) {
                        Log.d("DemoApp", "225 exception =" + e);
                    }
                }
                strLine = null;
                FileReader input = null;
                try {
                    loadfilename=totfilefound[i];
                    input = new FileReader(strInputFilePath + totfilefound[i]);
                } catch (Exception e) {
                    Log.d("DemoApp", "223 exception =" + e);
                }
                try {
                    bufRead = new BufferedReader(input);
                    count = 0;  // Line number of count
                    // Read first line
                    strLine = bufRead.readLine();
                } catch (Exception e) {
                    Log.d("DemoApp", "224 exception =" + e);
                }
                try {
                    //Log.d("DemoApp", "filefound " + filefound);
                    //   Log.d("DemoApp", "progressStatus =" + progressStatus);
                    //   Log.d("DemoApp", "start =" + start);
                    //    Log.d("DemoApp", "  fincntss =" + fincnt);
                    while (strLine != null && progressStatus < start) {

                        try {
                            if (strLine != "\n") {
                                SQLiteBulkDataIn.ProcessDataLine(strLine);
                                strLine = bufRead.readLine();
                            }
                        } catch (Exception e) {
                            Log.d("DemoApp", "222 exception =" + e);
                        }
                        // Log.d("DemoApp", "filefound " + filefound);
                        //   Log.d("DemoApp", "progressStatus =" + progressStatus);
                        //   Log.d("DemoApp", "start =" + start);
                        //   Log.d("DemoApp", "  fincntss =" + fincnt);
                        //    Log.d("DemoApp", "  countss =" + count);
                        publishProgress(progrcnt);
                        count++;
                        progrcnt++;
                        progressStatus += 1;
                    }

                } catch (Exception e) {
                    Log.d("DemoApp", "  Exception =" + e);

                }
            }//for loop
            //  Log.d("Progress", "11 " + strInputFilePath);
            //   Log.d("Progress", "12 " + filefound);
            //  Log.d("Progress", "13 " + totfile);
            //  Log.d("Progress", "14 " + totrec);
            return null;
        }
        protected void onProgressUpdate(Integer... values) {

            fincnt= values[0]+1;
            pb.setProgress(progressStatus);
            tv.setText("Running..." + fincnt + ">>" + (totrec-filecount));
            //   Log.d("DemoApp", "filefound in Connection" + filefound);
            msg1.setText("Reading From: " + loadfilename);
            msg2.setText("Wait!! donot press any key!! ");
            dwldbtn.setVisibility(Button.GONE);
        }
        protected void onPostExecute(Void result) {
            // async task finished
            try {
            }catch(Exception e){}
            tv.setText("Download Successful ");
            Log.d("DemoApp", "fincnt " + fincnt);
            Log.d("DemoApp","totrec "+totrec);
            Log.d("DemoApp","progressStatus ss "+progressStatus);

            // if(((fincnt-1))==totrec) {
            if(((fincnt-1))==progressStatus) {//CHANGE ON DT08082016
                fincnt=0;
                progrcnt=1;
                try {
                    bufRead.close();


                    String strSelectSQL_01 = "DELETE FROM COLL_SBM_DATA";
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    Log.d("DemoApp", "strSelectSQL_01" + strSelectSQL_01);
                    //String strSelectSQL_05 = "DELETE FROM BILL_SBM_DATA WHERE CONS_ACC='' ";
                    // DatabaseAccess.database.execSQL(strSelectSQL_05);
                    // Log.d("DemoApp", "strSelectSQL_05" + strSelectSQL_05);
                    String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA SELECT * FROM COLL_SBM_DATA_TEMP WHERE CONS_ACC !='' ";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                    DatabaseAccess.database.execSQL(strSelectSQL_02);
                    if(tasktype.equals("3")){
                        String strSelectSQL_03= "DELETE FROM File_desc where Version_flag=4 ";
                        DatabaseAccess.database.execSQL(strSelectSQL_03);
                        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_03);
                        String strSelectSQL_04 = "INSERT INTO file_desc (file_name,Version_flag,BILL_TOTAL_COUNT) values ('" + loadfilename + "',4,"+ totrec +") ";
                        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_04);
                        DatabaseAccess.database.execSQL(strSelectSQL_04);
                    }

                    databaseAccess.close();
                    //migrated to main screen
                    backbtn.setVisibility(Button.VISIBLE);
                    dwldbtn.setVisibility(Button.GONE);
                    msg2.setText("Press Back Button");
                } catch (Exception e) {
                    Log.d("DemoApp", "Error in Buffer close and data to main sbm_data");
                }
            }
        }

    }

}
