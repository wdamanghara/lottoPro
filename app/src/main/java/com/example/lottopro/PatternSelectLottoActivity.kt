package com.example.lottopro

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.lottopro.Adapter.ButtonAdapter
import com.example.lottopro.DataBase.PatternSelSql
import com.example.lottopro.DataBase.SqlHelper
import com.example.lottopro.Str.LottoNum
import com.example.lottopro.Str.PatternSelNum
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.header_lotto.*
import kotlinx.android.synthetic.main.pattern.*
import kotlinx.android.synthetic.main.pattern_select_num.*
import kotlinx.android.synthetic.main.select_lotto.*
import kotlinx.android.synthetic.main.select_lotto.lottoGridLayout
import kotlinx.android.synthetic.main.select_lotto.saveBtn
import timber.log.Timber

class PatternSelectLottoActivity : AppCompatActivity() {
    private lateinit var  gDb: SqlHelper
    private lateinit var  gPatternDb: PatternSelSql
    private var gPatternList:List<PatternSelNum> = ArrayList<PatternSelNum>()
    private var gSelLotto = ArrayList<Int>()
    private var gPatternSelectList : List<String>? = listOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree() )
        gDb = SqlHelper(this)
        gPatternDb = PatternSelSql(this)
        setContentView(R.layout.pattern_select_num)

        MobileAds.initialize(this) {}
        var  mAdView = patternSelAdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val sToolbar = lottoHeader as Toolbar?
        setSupportActionBar(sToolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        var gMaxCol = 9
        var gMaxRow = 5
        gPatternList = gPatternDb.gPatternSelNum

        lottoGridLayout.columnCount = gMaxCol
        lottoGridLayout.rowCount = gMaxRow
        saveBtn.text = "번호 저장"

        drawLottoSelect()

        saveBtn.setOnClickListener {
            patSelectBtn()
        }

        viewBackBtn.setOnClickListener {
            finish()
        }

        allCheckBtn.setOnClickListener {
            gPatternSelectList = ArrayList<String>()
            gSelLotto = ArrayList<Int>()

            for(i in 1..46) {
                (gPatternSelectList as ArrayList<String>).add(i.toString())
            }

            drawLottoSelect()
        }
    }

    private fun drawLottoSelect() {
        var gMaxSelLotto = 45
        var gMaxLottoNum = 46

        lottoGridLayout.removeAllViews()

        if (gPatternList.isNotEmpty()) {
            gPatternSelectList = gPatternList[0].number?.split(',')
        }

        for (i in 1 until gMaxLottoNum) {
            var sIsClick = false
            var sBall = "pattern_$i"
            var sUnBall = "pattern_un_$i"
            val sCol : GridLayout.Spec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1F)
            val sRow : GridLayout.Spec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1F)
            var sGridParam : GridLayout.LayoutParams

            sIsClick = gPatternSelectList?.indexOf(i.toString()) != -1

            val sBtn = ImageButton(this).apply {
                this.setOnClickListener {
                    if(sIsClick) {
                        this.setBackgroundResource(
                            resources.getIdentifier(sUnBall, "drawable", packageName)
                        )
                        gSelLotto.remove(i)
                        gSelLotto.sort()

                        sIsClick = false
                    } else {
                        if (gMaxSelLotto === gSelLotto.size) {
                            Toast.makeText(applicationContext, "선택은 45개 까지입니다.", Toast.LENGTH_LONG).show();
                            return@setOnClickListener
                        }

                        this.setBackgroundResource(
                            resources.getIdentifier(sBall, "drawable", packageName)
                        )

                        gSelLotto.add(i.toInt())
                        gSelLotto.sort()

                        sIsClick = true
                    }
                }
            }

            if(gPatternSelectList?.indexOf(i.toString()) != -1) {
                sBtn.setBackgroundResource(resources.getIdentifier(sBall, "drawable", packageName))
                gSelLotto.add(i.toInt())
            } else {
                sBtn.setBackgroundResource(resources.getIdentifier(sUnBall, "drawable", packageName))
            }

            sBtn.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            sGridParam = GridLayout.LayoutParams(sRow, sCol)
            lottoGridLayout.addView(sBtn, sGridParam)
        }
    }

    private fun patSelectBtn() {
        if (gSelLotto.size < 6) {
            Toast.makeText(applicationContext, "6개 이상의 숫자를 선택해 주세요", Toast.LENGTH_SHORT).show()
            return
        }

        gSelLotto.sort()

        for(value in gPatternList) {
            var sDeletePattern = PatternSelNum(value.id, "")
            gPatternDb.deletePatternSel(sDeletePattern)
        }

        val sStrArray = gSelLotto.map{ it.toString() }.toTypedArray()
        val sLottoNum = PatternSelNum(0, sStrArray.joinToString(","))

        gPatternDb.addPatternSel(sLottoNum)

        val sIntent = Intent(this@PatternSelectLottoActivity, PatternLottoActivity::class.java)
        startActivity(sIntent);
    }

    // 레이아웃에 마진 적용 할때 쓰는 함수
    fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
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
    fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
    // 레이아웃에 마진 적용 할때 쓰는 함수
}

