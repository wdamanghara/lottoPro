package com.example.lottopro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import com.example.lottopro.Adapter.ButtonAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.constellation.*
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.random_lotto.*
import kotlinx.android.synthetic.main.main.adView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)


        MobileAds.initialize(this) {}
        var  mAdView = adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        // Disable the `NetworkOnMainThreadException` and make sure it is just logged.
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())


        // 로또 번호
        var slottoApi = LottoNumberMaker.getNumber()?.toTypedArray()
        // 로또 회차
        var slottoApiCount = LottoNumberMaker.getCount()
        // 로또 날짜
        var slottoApiData = LottoNumberMaker.getDate().replace("-",".")


//        Toast.makeText(applicationContext, "날짜 ${slottoApiData}", Toast.LENGTH_LONG).show()

        textView1.setText("$slottoApiData")
        textView2.setText("${slottoApiCount}회차")

        val sAddGridPram = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )


        linearLayoutBallApi.removeAllViews()

        val sAddGrid: GridView = GridView(this)
        sAddGrid.layoutParams = sAddGridPram
        sAddGrid.numColumns = 8

        sAddGrid.background = ContextCompat.getDrawable(this, R.drawable.lotto_grid_view)
        sAddGrid.horizontalSpacing = 10
        sAddGrid.verticalSpacing = 10
        linearLayoutBallApi.addView(sAddGrid)
        var sAdapter = ButtonAdapter(this, slottoApi)
        sAddGrid.adapter = sAdapter


        var sIntent = Intent();
        button1.setOnClickListener{
            sIntent = Intent(this@MainActivity, ConstellationActivity::class.java)
            startActivity(sIntent);
        }
        button2.setOnClickListener{
            sIntent = Intent(this@MainActivity, RandomLottoActivity::class.java)
            startActivity(sIntent);
        }
        button3.setOnClickListener{
            sIntent = Intent(this@MainActivity, PatternLottoActivity::class.java)
            startActivity(sIntent);
        }
        button4.setOnClickListener{
            sIntent = Intent(this@MainActivity, SelectLottoActivity::class.java)
            startActivity(sIntent);
        }

    }

    override fun onResume() {
        super.onResume()
    }


    // 레이아웃에 마진 적용 할때 쓰는 함수
    fun View.margin(
        left: Float? = null,
        top: Float? = null,
        right: Float? = null,
        bottom: Float? = null
    ) {
        layoutParams<ViewGroup.MarginLayoutParams> {
            left?.run { leftMargin = dpToPx(this) }
            top?.run { topMargin = dpToPx(this) }
            right?.run { rightMargin = dpToPx(this) }
            bottom?.run { bottomMargin = dpToPx(this) }
        }
    }

    inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
        if (layoutParams is T) block(layoutParams as T)
    }

    fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    ).toInt()
    // 레이아웃에 마진 적용 할때 쓰는 함수
}