package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.lxhrvapi.LXHrvAPI;

import etc.Data;
import etc.Events;
import etc.PHPReader;
import etc.ViewMethod;
import etc.Views;

public class PPG_result extends Activity {
    /**
     * Called when the activity is first created.
     */

    Context context;
    //
    int[] cardiacIntervalArr; //= new int[60];
    int[] tach_ay; // = new int[61];
    //
    float result_ay[] = new float[15];
    //
    int bpm;
    int cardiacIntervalArrSize;
    ////////////////////////////////// VIEWS ////////////////////////////////
    ViewMethod vm = new ViewMethod();
    Views vs = new Views();
    Events evnts = new Events();
    //
    //
    ScrollView sv;
    TextView anb;
    TextView ratio;
    FrameLayout fl_meter;
    ImageView needle;
    TextView conclusion;
    TextView[] tv = new TextView[4];
    FrameLayout[] fl = new FrameLayout[4];
    TextView[] result = new TextView[4];
    SeekBar[] sb = new SeekBar[4];
    TextView complete;
    //
    int score = 0;

    //////
    Data func = new Data();
    String date;

    ///
    int numBack = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppg_result);

        cardiacIntervalArrSize = getIntent().getIntExtra("length", 0);
        cardiacIntervalArr = new int[cardiacIntervalArrSize];
        cardiacIntervalArr = getIntent().getIntArrayExtra("ci");
        tach_ay = new int[cardiacIntervalArrSize + 1];
        bpm = getIntent().getIntExtra("bpm", 0);

        adjustViews();

        analysis();

        defineEvent();

        titleBar();
    }

    private void titleBar(){
        View titlebar = (View)findViewById(R.id.title_bar);
        TextView title = (TextView)titlebar.findViewById(R.id.tv_title);
        ImageView back = (ImageView)titlebar.findViewById(R.id.iv_title_back);

        title.setText("스트레스 측정");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PPG_measure.class);
                startActivity(i);

            }
        });

        titlebar.setBackgroundColor(Color.parseColor("#ffffff"));
        ImageView menu = (ImageView)titlebar.findViewById(R.id.iv_titlebar_menu);
        if(menu.getVisibility() == View.GONE)
            menu.setVisibility(View.VISIBLE);

        PopupMenu popupMenu = new PopupMenu(PPG_result.this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.logout:
                        Toast.makeText(PPG_result.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

    }



    private void adjustViews(){
        context = getApplicationContext();
        //
        //
        //sv = (ScrollView)findViewById(R.id.sv);
        anb = (TextView)findViewById(R.id.anb);
        ratio = (TextView)findViewById(R.id.ratio);
        fl_meter = (FrameLayout)findViewById(R.id.fl_meter);
        needle = (ImageView)findViewById(R.id.needle);
        conclusion = (TextView)findViewById(R.id.conclusion);
        complete = (TextView)findViewById(R.id.complete);
        //
        //
//        vm.resizeSingleView(sv, "frame", 0, 0, 0, 0, 0, 0);
//        vm.reformSingleTextBasedView(context, anb, 30, "regular", "linear", 0, 0, 0, 50, 90, 10);
//        vm.reformSingleTextBasedView(context, ratio, 48, "bold", "linear", 0, 0, 0, 0, 90, 20);
//        vm.resizeSingleView(fl_meter, "linear", 1080, 492);
//        vm.resizeSingleView(needle, "frame", 438, 49, 126, 0, 0, 0);
//        vm.reformSingleTextBasedView(context, conclusion, 120, "light", "linear", 1080, 328); //// DEFAULT FONT SIZE 120
//        vm.reformSingleTextBasedView(context, complete, 43, "light", "linear", 653, 100, 0, 38, 0, 150); /////////////////////////////////
//        //


      //  vm.reformMultipleTextView(this, context, "tv_", tv, new int[]{0,1,2,3}, 30, "regular");
      //  vm.resizeMultipleView(this, new int[]{0,1,2,3}, "fl_", fl, "linear", 900, 0);
      //  vm.resizeMultipleView(this, new int[]{0,1,2,3}, "sb_", sb, "linear", 900, 5, 0, 43, 0, 62);
        //


        tv[0] = (TextView)findViewById(R.id.tv_0);
        tv[1] = (TextView)findViewById(R.id.tv_1);
        tv[2] = (TextView)findViewById(R.id.tv_2);
        tv[3] = (TextView)findViewById(R.id.tv_3);

        result[0] = (TextView)findViewById(R.id.result_0);
        result[1] = (TextView)findViewById(R.id.result_1);
        result[2] = (TextView)findViewById(R.id.result_2);
        result[3] = (TextView)findViewById(R.id.result_3);


        sb[0] = (SeekBar)findViewById(R.id.sb_0);
        sb[1] = (SeekBar)findViewById(R.id.sb_1);
        sb[2] = (SeekBar)findViewById(R.id.sb_2);
        sb[3] = (SeekBar)findViewById(R.id.sb_3);

        sb[0].setMax(100);
        sb[1].setMax(100);
        sb[2].setMax(100);
        sb[3].setMax(180);
    }

    private void defineEvent(){
        setDegree((float)(1.8*score));


        complete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        //vs.customBox(complete, "#4cb3b6", "#4cb3b6", 1, 50);
                        //complete.setTextColor(Color.parseColor("#080e3d"));
                        break;
                    case MotionEvent.ACTION_CANCEL :
                        //vs.customBox(complete, "#ffffff", "#4cb3b6", 1, 50);
                        //complete.setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case MotionEvent.ACTION_UP :
                        //vs.customBox(complete, "#ffffff", "#4cb3b6", 1, 50);
                        //complete.setTextColor(Color.parseColor("#ffffff"));
                        evnts.launchPreviousActivity(PPG_result.this, PPG_measure.class); // 다시 측정하기
                        break;
                }

                return true;
            }
        });
    }

    private void setDegree(float angle){
        RotateAnimation anim;
        anim = new RotateAnimation(0.0f, angle, Animation.RELATIVE_TO_SELF, .936073f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(3000);
        anim.setFillAfter(true);
        needle.startAnimation(anim);
    }

    private void analysis(){

        for(int i = 0; i < cardiacIntervalArrSize; i++){
            tach_ay[i+1] = cardiacIntervalArr[i];
            Log.d("CardiacIntervalArray["+i+"]", cardiacIntervalArr[i]+"");
        }

        LXHrvAPI ana = new LXHrvAPI();
        ana.Set_Fault_HBI_Ref(15); //  CARDIAC INTERVAL ARRAY'S TOLERANCE OF ABNORMAL DATA

        int mi_state = ana.HRV_Analysis_for_One_Min(tach_ay, cardiacIntervalArrSize);

        if(mi_state == -1){

        }
        else{
            ana.GetHrvResult(result_ay);
            //
            setResults();
            saveResults();
        }
    }
    private void saveResults(){
        PHPReader php = new PHPReader();
        php.addVariable("id", getIntent().getStringExtra("id"));
        date = func.getDate(date);
        php.addVariable("date", date);
        php.addVariable("time", func.getTime());
        php.addVariable("dbName", "ppg");
        php.addVariable("AA", Float.toString(result_ay[5])); //자율신경 활성
        php.addVariable("SNS", Float.toString(result_ay[4])); //교감신경
        php.addVariable("PSNS", Float.toString(result_ay[3])); //부교감신경
        php.addVariable("ANS", Float.toString(result_ay[2])); //자율신경
        php.addVariable("HRV", Float.toString(result_ay[6])); //심박변이도
        php.addVariable("STRESS", Float.toString(result_ay[14]));  ///스트레스
        php.execute("http://1.234.63.165/h2o/insert_ppg.php");
    }

    private void setResults(){

        ratio.setText((int)result_ay[5]+":"+(100-(int)result_ay[5]));
        ////////////////////////////////////////////////////////////////////////
        score = (int)result_ay[14];
        //////////////////////////// 스트레스 크기 판정값 ////////////////////////
        if(score >= 0 && score < 30) conclusion.setText("\"매우 낮음\"");
        else if(score >= 30 && score < 40) conclusion.setText("\"낮음\"");
        else if(score >= 40 && score < 60) conclusion.setText("\"보통\"");
        else if(score >= 60 && score < 70) conclusion.setText("\"높음\"");
        else if(score >= 70) conclusion.setText("\"매우 높음\"");
        //////////////////////////// 교감신경 활성도 //////////////////////////////
        if (result_ay[4] >= 0 && result_ay[4] < 2.) result[0].setText("매우 낮음");
        else if (result_ay[4] >= 2. && result_ay[4] < 4.) result[0].setText("낮음");
        else if (result_ay[4] >= 4. && result_ay[4] < 6.) result[0].setText("평균");
        else if (result_ay[4] >= 6. && result_ay[4] < 8.) result[0].setText("높음");
        else if (result_ay[4] >= 8.) result[0].setText("매우 높음");
        /////////////////////////// 부교감신경 활성도 //////////////////////////////
        if (result_ay[3] >= 0 && result_ay[3] < 2.) result[1].setText("매우 낮음");
        else if (result_ay[3] >= 2. && result_ay[3] < 4.) result[1].setText("낮음");
        else if (result_ay[3] >= 4. && result_ay[3] < 6.) result[1].setText("평균");
        else if (result_ay[3] >= 6. && result_ay[3] < 8.) result[1].setText("높음");
        else if (result_ay[3] >= 8.) result[1].setText("매우 높음");
        /////////////////////////// 자율신경 활성도 /////////////////////////////////
        if (result_ay[2] >= 0 && result_ay[2] < 2.) result[2].setText("매우 낮음");
        else if (result_ay[2] >= 2. && result_ay[2] < 4.) result[2].setText("낮음");
        else if (result_ay[2] >= 4. && result_ay[2] < 6.) result[2].setText("평균");
        else if (result_ay[2] >= 6. && result_ay[2] < 8.) result[2].setText("높음");
        else if (result_ay[2] >= 8.) result[2].setText("매우 높음");
        /////////////////////////// 심박변이도 HRV //////////////////////////////////
        if(result_ay[6] < 3.34) result[3].setText("매우 작음");
        else if (result_ay[6] >= 3.34 && result_ay[6] < 6.04) result[3].setText("작음");
        else if (result_ay[6] >= 6.04 && result_ay[6] < 11.44) result[3].setText("표준");
        else if (result_ay[6] >= 11.44 && result_ay[6] < 14.14) result[3].setText("큼");
        else if (result_ay[6] >= 14.14) result[3].setText("매우 큼");
        /////////////////////////////////////////////////////////////////////////////
        sb[0].setProgress((int)(result_ay[4]*10));
        sb[1].setProgress((int)(result_ay[3]*10));
        sb[2].setProgress((int)(result_ay[2]*10));
        sb[3].setProgress((int)(result_ay[6]*10));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK :
                ///////// TOAST EVENT

                if(numBack == 0){
                    numBack = 1;
                    evnts.toastMessage(context, "뒤로 가기 버튼을 한번 더 누르면 종료됩니다");
                    final Handler hd = new Handler();
                    hd.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hd.removeCallbacksAndMessages(null);

                            hd.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    numBack = 0;
                                }
                            }, 1000);
                        }
                    }, 1000);
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
        }
        return false;
    }
}
