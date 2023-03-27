package com.collection.tpwodloffline;

import android.database.Cursor;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by cesu-user on 04-06-2018.
 */
public class SQLiteBulkDataIn {
    public static int iFlagBad = 0;
    public static void InitializeImport(String Dtype) throws SQLException
    {
        Log.d("DemoApp", "Entering SBM Bulk Data In");
        // Statement statement = null;

        try {
            //   if (SbmUtilities.print_flag == 1) System.out.println("##### Calling Connection Class...");
            //  statement = SbmUtilities.dbConnection.createStatement();
            //   if (SbmUtilities.print_flag == 1) System.out.println("##### Calling Connection Class Successful ...");
            // statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //  statement.execute(strSelectSQL_01);
            if(Dtype.equals("3")){
                String strSelectSQL_01 = "DELETE FROM COLL_SBM_DATA_BKP WHERE (strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', RECPT_DATE)*12 + strftime('%m', RECPT_DATE))>3";
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                Log.d("DemoApp", "strSelectSQL_01 " + strSelectSQL_01);
                String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA_BKP SELECT * FROM COLL_SBM_DATA WHERE COLL_FLG != 0";
                DatabaseAccess.database.execSQL(strSelectSQL_02);
                Log.d("DemoApp", "strSelectSQL_02 " + strSelectSQL_02);
                Log.d("DemoApp", "Deleting SBM coll  Data ");
                String strSelectSQL_03= "DELETE FROM COLL_SBM_DATA_TEMP";
                Log.d("DemoApp", "strSelectSQL_03 " + strSelectSQL_03);
                //String strSelectSQL_01= "DELETE FROM BILL_SBM_DATA";
                DatabaseAccess.database.execSQL(strSelectSQL_03);
                Log.d("DemoApp", "Deleting SBM Bulk Data ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int ProcessDataLine(String strDataLine) throws SQLException
    {
        iFlagBad = 0;
        String strResult="";
        // Statement statement = null;
        try {
            //  statement = SbmUtilities.dbConnection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // statement.setQueryTimeout(30);  // set timeout to 30 sec.

        String strSelectSQL_01= "SELECT COMPANY_ID, TABLE_NAME, FIELD_NAME, POSITION, DATA_TYPE, DATA_LENGTH, DATA_PEC,"
                + " FIELD_DESC, DATA_DEL, RECORD_TYPE FROM MST_INTF_FORMAT WHERE RECORD_TYPE = '81' ORDER BY POSITION";
        // ResultSet rs = statement.executeQuery(strSelectSQL_01);
        Cursor rs=DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        String strCompanyId = null;
        String strTableName = null;
        String strFieldName = null;
        int iPosition = 0;
        String strDataType = null;
        int iLength = 0;
        int iDecimalPos = 0;
        String strDataFormat = null;
        String strDelim = null;
        String strRecordType = null;
        int iLoop = 0;
        String strSQL = null;
        String strSQLPart1 = null;
        String strSQLPart2 = null;
        String strSQLPart3 = null;
        String strTokenValue = null;

        while(rs.moveToNext())
        {
            strCompanyId = rs.getString(0);
            strTableName = rs.getString(1);
            //strTableName= "COLL_SBM_DATA";
            strFieldName = rs.getString(2);
            iPosition = rs.getInt(3);
            strDataType = rs.getString(4);
            iLength = rs.getInt(5);
            iDecimalPos = rs.getInt(6);
            strDataFormat = rs.getString(7);
            strDelim = rs.getString(8);
            strRecordType = rs.getString(9);

            strTokenValue = getTokensFL(strDataLine, iPosition, iLength).trim(); //.replace(" ", "");

            iLoop++;
            if (iLoop == 1)
            {
                strSQLPart1 = "INSERT INTO " + strTableName + "(" + strFieldName;
                if (strDataType.startsWith("VARCHAR"))
                {
                    strSQLPart2 = ") VALUES( '" + strTokenValue + "'";;
                }
                else if (strDataType.startsWith("DATE"))
                {
                    strTokenValue = convertDateFormat(strTokenValue, strDataFormat, "YYYY-MM-DD");
                    strSQLPart2 = ") VALUES( '" + strTokenValue + "'";
                }
                else
                {
                    strSQLPart2 = ") VALUES( " + strTokenValue;
                }
                strSQLPart3 = ")";
            }
            else
            {
                // strTokenValue == null || strTokenValue == "" || strTokenValue.isEmpty() ||
                if (strTokenValue.equals(""))
                {}
                else
                {
                    strSQLPart1 = strSQLPart1 + ", " + strFieldName;
                    if (strDataType.startsWith("VARCHAR"))
                    {
                        strSQLPart2 = strSQLPart2 + ", '" + strTokenValue + "'";
                    }
                    else if (strDataType.startsWith("DATE"))
                    {
                        strTokenValue = convertDateFormat(strTokenValue, strDataFormat, "YYYY-MM-DD");
                        strSQLPart2 = strSQLPart2 + ", '" + strTokenValue + "'";
                    }
                    else
                    {
                        strSQLPart2 = strSQLPart2 + ", " + strTokenValue;
                    }
                }
            }
        }
        if (iLoop > 0) strSQL = strSQLPart1 + strSQLPart2 + strSQLPart3;
        //if (SbmUtilities.print_flag == 1) System.out.println("##### strSQL ::" + strSQL);

        if (iFlagBad == 0)
        {
            try{
                // statement.execute(strSQL);
                Log.d("DemoApp", "strSQL "+strSQL);
                DatabaseAccess.database.execSQL(strSQL);
            } catch (Exception SQLEx)
            {
                iFlagBad = 1;
            }
        }

        if (iFlagBad == 1)
        {
            // Implementation for Bad Data Handling
        }

        return 0;
    }

    public static String getTokensFL(String strDataLine, int iPosition, int iLength)
    {
        //if (SbmUtilities.print_flag == 1) System.out.println("#####"+ iPosition + ";" + iLength + ";" + strDataLine.length()+ ";" + strDataLine);
        String strTokenValue = "";
        int iDataLineLen = strDataLine.length();
        // Log.d("DemoApp", "21 Inside getTokensFL "+strDataLine.length());
        if ( iPosition <= iDataLineLen)
        {
            strTokenValue = strDataLine.substring(iPosition-1, java.lang.Math.min(iPosition+iLength-1,iDataLineLen));
        }
        //  Log.d("DemoApp", "22 Inside getTokensFL " + strTokenValue);
        return strTokenValue;
    }
    /////
    /////
    //////
    public static String convertDateFormat(String strTokenValue, String strDataFormat, String strDataFormatOut)
    {
        String strTokenValueRevDt = "";
        String strTokenValueOrgDt = strTokenValue;
        int idxSDate = strDataFormat.indexOf("DD");
        int idxSMonth =strDataFormat.indexOf("MM");
        int idxSYear = strDataFormat.indexOf("Y");
        int idxEYear = strDataFormat.lastIndexOf("Y");
        int idxSHour = strDataFormat.indexOf("HH");

        try{
            strTokenValueRevDt = strTokenValueOrgDt.substring(idxSYear-1, idxEYear) + "-" +
                    strTokenValueOrgDt.substring(idxSMonth-1, idxSMonth+1) + "-" +
                    strTokenValueOrgDt.substring(idxSDate-1, idxSDate+1);
            //System.out.println("##222### DD, MM, YYYY, HH"+ idxSDate + ";" + idxSMonth + ";" + idxSYear + ";" + idxEYear
            //		+ ";" + idxSHour + ";" + strTokenValueOrgDt + ";" + strTokenValueRevDt);
        }
        catch (Exception e)
        {
            strTokenValueRevDt = "1900-01-01";
           iFlagBad = 1;
        }
        return strTokenValueRevDt;
    }


}

