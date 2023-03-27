package com.collection.tpwodloffline.activity

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collection.tpwodloffline.DatabaseAccess
import com.collection.tpwodloffline.R
import com.collection.tpwodloffline.adapter.EnergySearchadapter
import com.collection.tpwodloffline.model.EnergyModel
import com.collection.tpwodloffline.utils.SharedPreferenceClass

class EnergySearchActivity : AppCompatActivity() {

    var details = ArrayList<HashMap<String, String>>()
    var searchAdapter: EnergySearchadapter? = null
    var recyclerview: RecyclerView? = null
    var searchView: AppCompatAutoCompleteTextView? = null
    var progress_frame: FrameLayout? = null
    private val dataListEnergy = ArrayList<EnergyModel>()
    private val originalData = ArrayList<EnergyModel>()
    var Cname: String? = null
    var Scnum: String? = null
    var Payable: String? = null
    var nodata: TextView? = null
    var searchnow: ImageView? = null
    var databaseAccess: DatabaseAccess? = null
    var shf: SharedPreferenceClass? = null
    var rdg_enrgySCNo: RadioButton? = null
    var rdg_energyName: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        setContentView(R.layout.activity_energy_search)

        shf = SharedPreferenceClass(this)
        searchnow = findViewById(R.id.searchnow)
        nodata = findViewById(R.id.textView25)
        recyclerview = findViewById(R.id.recyclerview)
        searchView = findViewById(R.id.searchView)
        progress_frame = findViewById(R.id.progress_frame)
        rdg_enrgySCNo = findViewById(R.id.rdg_enrgySCNo);
        rdg_energyName = findViewById(R.id.rdg_energyName);

        rdg_enrgySCNo?.setOnClickListener {
            searchView?.inputType = InputType.TYPE_CLASS_TEXT
        }
        rdg_energyName?.setOnClickListener {
            searchView?.inputType = InputType.TYPE_CLASS_TEXT
        }

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL, false
            )
        recyclerview!!.layoutManager = layoutManager
        searchAdapter =
            EnergySearchadapter(this, dataListEnergy)
        recyclerview!!.adapter = searchAdapter

        searchView!!.requestFocus()
        searchView!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    recyclerview!!.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(
                seq: CharSequence,
                start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                keywords: CharSequence,
                start: Int, before: Int, count: Int
            ) {
                val keyword = keywords.toString()
                var whereClause: String = "";

                if (rdg_enrgySCNo?.isChecked == true && keywords.length >= 7) {
                    whereClause = "CONS_ACC like '$keyword%'"
                    getData(keyword, whereClause)
                } else if (rdg_energyName?.isChecked == true && keywords.length >= 4) {
                    whereClause = "CON_NAME like '%$keyword%'"
                    getData(keyword, whereClause)
                }


//                if (count >= 0) {
//                    recyclerview!!.visibility = View.GONE
//                } else {
//                    getData(keyword)
//                }
            }
        })
    }

    private fun getData(keyword: String, whereClause: String) {

        try {
            progress_frame?.visibility = View.VISIBLE
            dataListEnergy.clear()
            recyclerview?.visibility = View.VISIBLE

            databaseAccess = DatabaseAccess.getInstance(this)
            databaseAccess?.open()

            val strUpdateSQL_01 =
                "Select CON_NAME, CONS_ACC,Cur_TOTAL from CUST_DATA Where $whereClause"
            val rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null)
            while (rs.moveToNext()) {
                Cname = rs.getString(0)
                Scnum = rs.getString(1)
                Payable = rs.getString(2)

                if ((Cname.toString().lowercase()).contains(keyword) ||
                    (Scnum.toString().lowercase()).contains(keyword)
                ) {
                    val dataModelEnrgy = EnergyModel(Cname, Scnum, Payable)
                    dataListEnergy.add(dataModelEnrgy)
                }
            }
            if (dataListEnergy.size >= 0) {
                originalData.addAll(dataListEnergy)
                searchAdapter =
                    EnergySearchadapter(this, dataListEnergy)
                recyclerview!!.adapter = searchAdapter
                progress_frame?.visibility = View.GONE
                nodata?.visibility = View.GONE
                recyclerview?.visibility = View.VISIBLE
            } else {
                nodata?.visibility = View.VISIBLE
                recyclerview?.visibility = View.GONE
                progress_frame?.visibility = View.VISIBLE
            }

            databaseAccess?.close()
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }
}