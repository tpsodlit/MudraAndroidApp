package com.collection.tpwodloffline.nonenergy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collection.tpwodloffline.DatabaseAccess
import com.collection.tpwodloffline.R
import com.collection.tpwodloffline.adapter.NavAdapter
import com.collection.tpwodloffline.adapter.Searchadapter
import com.collection.tpwodloffline.utils.SharedPreferenceClass

class SearchActivity : AppCompatActivity() {

    var details = ArrayList<HashMap<String, String>>()
    var searchAdapter: Searchadapter? = null
    var recyclerview: RecyclerView? = null
    var searchView: AppCompatAutoCompleteTextView? = null
    var progress_frame: FrameLayout? = null

    private val nonenList = ArrayList<NonenModel>()
    private val originalData = ArrayList<NonenModel>()
    var navAdapter: NavAdapter? = null
    var Cname: String? = null
    var Scnum:String? = null
    var Ref:String? = null
    var Module:String? = null
    var Payable:String? = null
    var nodata:TextView? = null
    var searchnow:ImageView? = null
    var databaseAccess: DatabaseAccess? = null
    var shf:SharedPreferenceClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.colorPrimarynonentop)
        setContentView(R.layout.activity_search)

        shf = SharedPreferenceClass(this)
        searchnow = findViewById(R.id.searchnow)
        nodata = findViewById(R.id.textView25)
        recyclerview = findViewById(R.id.recyclerview)
        searchView = findViewById(R.id.searchView)
        progress_frame = findViewById(R.id.progress_frame)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerview!!.layoutManager = layoutManager
        searchAdapter =
            Searchadapter(this, nonenList)
        recyclerview!!.adapter = searchAdapter

        searchView!!.requestFocus()


        searchView!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.isEmpty()){
                    recyclerview!!.visibility = View.GONE
                }
            }
            override fun beforeTextChanged(seq: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(keywords: CharSequence, start: Int, before: Int, count: Int) {
                val keyword = keywords.toString()
                if (count == 0) {
                    recyclerview!!.visibility = View.GONE
                } else {
                    getData(keyword)
                }
            }
        })

        /*searchnow?.setOnClickListener {
            val keyword:String = searchView?.text.toString()

            if(keyword!=""){
                getData(keyword)
            }else{
                Toast.makeText(this, "Enter keyword to search", Toast.LENGTH_SHORT)
                    .show()
            }

        }*/
    }

    /*private fun getMasterData(keyword: String) {
        progress_frame?.visibility = View.VISIBLE
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, ServerLinks().Url,
            Response.Listener { response ->
                try {
                    recyclerview!!.visibility = View.VISIBLE
                    details.clear()
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getBoolean("status")
                    if(status){
                        val jsonArray = jsonObject.getJSONArray("data")
                        if(jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                val jObject = jsonArray.getJSONObject(i)
                                val map = HashMap<String, String>()
                                map["id"] = jObject.getString("id")
                                map["name"] = jObject.getString("name")

                                if ((jObject.getString("name").lowercase()).contains(keyword)) {
                                    if (!details.contains(map)) {
                                        details.add(map)
                                    }
                                }

                            }

                            searchAdapter?.notifyDataSetChanged()

                            recyclerview!!.visibility = View.VISIBLE
                            progress_frame.visibility = View.GONE
                        }else{
                            val map = HashMap<String, String>()
                            progress_frame.visibility = View.GONE
                            if(mtype==1||mtype==7){
                                recyclerview!!.visibility = View.VISIBLE
                                map["id"] = "0"
                                map["name"] = keyword
                                if (!details.contains(map)) {
                                    details.add(map)
                                }
                                searchAdapter?.notifyDataSetChanged()

                            }else{
                                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT)
                                    .show()
                                recyclerview!!.visibility = View.GONE

                            }
                        }


                    }

                    if (details.size > 0) {

                    } else {



                    }

                } catch (jsn: JSONException) {
                    progress_frame.visibility = View.GONE
                    recyclerview!!.visibility = View.GONE
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                progress_frame.visibility = View.GONE
                recyclerview!!.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Something went wrong! Try after some time",
                    Toast.LENGTH_LONG
                ).show()
            }) {

            override fun getBody(): ByteArray {
                val pj = postjson()
                pj.keys = keyword
                pj.masterType = mtype
                pj.parentId = pid?.toInt()
                val str = Gson().toJson(pj)
                return str.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

        }
        val socketTimeout = 60000
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = retryPolicy
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }*/

    private fun getData(keyword: String) {

        try {

            val nsc = shf?.getValue_string("NSC_flag")
            val csc = shf?.getValue_string("CSC_flag")
            val dnd = shf?.getValue_string("DND_flag")
            val frm = shf?.getValue_string("FRM_flag")
            var whereClause ="";
            if(nsc=="1")
            {

                whereClause+= "('NSC'";
            }
            if(csc=="1")
            {
                if(whereClause.isNullOrEmpty()) {
                    whereClause += "('CSC'"
                }
                else
                {
                    whereClause += ", 'CSC'"
                }
            }
            if(dnd=="1")
            {
                if(whereClause.isNullOrEmpty()) {
                    whereClause += "('DND'"
                }
                else
                {
                    whereClause+= ", 'DND'"
                }
            }
            if(frm=="1")
            {
                if(whereClause.isNullOrEmpty()) {
                    whereClause += "('FRM'"
                }
                else
                {
                    whereClause+= ", 'FRM'"
                }
            }

            whereClause += ")"

            progress_frame?.visibility = View.VISIBLE
            nonenList.clear()
            recyclerview?.visibility = View.VISIBLE
            /*val imm =
                applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(recyclerview?.windowToken, 0)*/
            databaseAccess = DatabaseAccess.getInstance(this)
            databaseAccess?.open()
            val strUpdateSQL_01 =
                "select CON_NAME,REF_REG_NO,REF_MODULE,SCNO,AMOUNT from NONENERGY_DATA WHERE REF_MODULE IN $whereClause"
            val rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null)
            while (rs.moveToNext()) {
                Cname = rs.getString(0)
                Ref = rs.getString(1)
                Module = rs.getString(2)
                Scnum = rs.getString(3)
                Payable = rs.getString(4)

                if ((Cname.toString().lowercase()).contains(keyword)||(Scnum.toString().lowercase()).contains(keyword)||(Ref.toString().lowercase()).contains(keyword)) {
                    val nonenModel = NonenModel(Cname, Scnum, Payable, Ref, Module)
                    nonenList.add(nonenModel)
                }
                if (nonenList.size >= 0) {
                    originalData.addAll(nonenList)
                    searchAdapter =
                        Searchadapter(this, nonenList)
                    recyclerview!!.adapter = searchAdapter
                    progress_frame?.visibility = View.GONE
                    nodata?.visibility = View.GONE
                    recyclerview?.visibility = View.VISIBLE
                } else {
                    nodata?.visibility = View.VISIBLE
                    recyclerview?.visibility = View.GONE
                    progress_frame?.visibility = View.VISIBLE

                }
            }

            databaseAccess?.close()

        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

}