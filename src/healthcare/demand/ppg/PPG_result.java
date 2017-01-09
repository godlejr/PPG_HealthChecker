package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;

import com.example.user.lxhrvapi.LXHrvAPI;

import etc.Events;
import etc.Server_Connector;
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
    }

    private void adjustViews() {
        context = getApplicationContext();
        //
        //
        sv = (ScrollView) findViewById(R.id.sv);
        anb = (TextView) findViewById(R.id.anb);
        ratio = (TextView) findViewById(R.id.ratio);
        fl_meter = (FrameLayout) findViewById(R.id.fl_meter);
        needle = (ImageView) findViewById(R.id.needle);
        conclusion = (TextView) findViewById(R.id.conclusion);
        complete = (TextView) findViewById(R.id.complete);
        //
        //
        vm.resizeSingleView(sv, "frame", 0, 0, 0, 0, 0, 0);
        vm.reformSingleTextBasedView(context, anb, 30, "regular", "linear", 0, 0, 0, 50, 90, 10);
        vm.reformSingleTextBasedView(context, ratio, 48, "bold", "linear", 0, 0, 0, 0, 90, 20);
        vm.resizeSingleView(fl_meter, "linear", 1080, 492);
        vm.resizeSingleView(needle, "frame", 438, 49, 126, 0, 0, 0);
        vm.reformSingleTextBasedView(context, conclusion, 120, "light", "linear", 1080, 328); //// DEFAULT FONT SIZE 120
        vm.reformSingleTextBasedView(context, complete, 43, "light", "linear", 653, 100, 0, 38, 0, 150); /////////////////////////////////
        //
        vm.reformMultipleTextView(this, context, "tv_", tv, new int[]{0, 1, 2, 3}, 30, "regular");
        vm.reformMultipleTextView(this, context, "result_", result, new int[]{0, 1, 2, 3}, 36, "bold");
        vm.resizeMultipleView(this, new int[]{0, 1, 2, 3}, "fl_", fl, "linear", 900, 0);
        vm.resizeMultipleView(this, new int[]{0, 1, 2, 3}, "sb_", sb, "linear", 900, 5, 0, 43, 0, 62);
        //
        sb[0].setMax(100);
        sb[1].setMax(100);
        sb[2].setMax(100);
        sb[3].setMax(180);
    }

    private void defineEvent() {
        setDegree((float) (1.8 * score));


        complete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //vs.customBox(complete, "#4cb3b6", "#4cb3b6", 1, 50);
                        //complete.setTextColor(Color.parseColor("#080e3d"));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        //vs.customBox(complete, "#ffffff", "#4cb3b6", 1, 50);
                        //complete.setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case MotionEvent.ACTION_UP:
                        //vs.customBox(complete, "#ffffff", "#4cb3b6", 1, 50);
                        //complete.setTextColor(Color.parseColor("#ffffff"));
                        evnts.launchPreviousActivity(PPG_result.this, PPG_measure.class); // 다시 측정하기
                        break;
                }

                return true;
            }
        });
    }

    private void setDegree(float angle) {
        RotateAnimation anim;
        anim = new RotateAnimation(0.0f, angle, Animation.RELATIVE_TO_SELF, .936073f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(3000);
        anim.setFillAfter(true);
        needle.startAnimation(anim);
    }

    private void analysis() {

        for (int i = 0; i < cardiacIntervalArrSize; i++) {
            tach_ay[i + 1] = cardiacIntervalArr[i];
            Log.d("CardiacIntervalArray[" + i + "]", cardiacIntervalArr[i] + "");
        }

        LXHrvAPI ana = new LXHrvAPI();
        ana.Set_Fault_HBI_Ref(15); //  CARDIAC INTERVAL ARRAY'S TOLERANCE OF ABNORMAL DATA

        int mi_state = ana.HRV_Analysis_for_One_Min(tach_ay, cardiacIntervalArrSize);

        if (mi_state == -1) {

        } else {
            ana.GetHrvResult(result_ay);
            //
            setResults();
            saveResults();
        }
    }

    private void saveResults() {
        Server_Connector connector = new Server_Connector();
        String url = getString(R.string.server_url);
        connector.addVariable("aa", Float.toString(result_ay[5])); //자율신경 활성
        connector.addVariable("sns", Float.toString(result_ay[4])); //교감신경
        connector.addVariable("psns", Float.toString(result_ay[3])); //부교감신경
        connector.addVariable("ans", Float.toString(result_ay[2])); //자율신경
        connector.addVariable("hrv", Float.toString(result_ay[6])); //심박변이도
        connector.addVariable("stress", Float.toString(result_ay[14]));  ///스트레스
        connector.execute(url + getIntent().getStringExtra("id") + "/insert_result");
    }

    private void setResults() {

        ratio.setText((int) result_ay[5] + ":" + (100 - (int) result_ay[5]));
        ////////////////////////////////////////////////////////////////////////
        score = (int) result_ay[14];
        //////////////////////////// 스트레스 크기 판정값 ////////////////////////
        if (score >= 0 && score < 30) conclusion.setText("\"매우 낮음\"");
        else if (score >= 30 && score < 40) conclusion.setText("\"낮음\"");
        else if (score >= 40 && score < 60) conclusion.setText("\"보통\"");
        else if (score >= 60 && score < 70) conclusion.setText("\"높음\"");
        else if (score >= 70) conclusion.setText("\"매우 높음\"");
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
        if (result_ay[6] < 3.34) result[3].setText("매우 작음");
        else if (result_ay[6] >= 3.34 && result_ay[6] < 6.04) result[3].setText("작음");
        else if (result_ay[6] >= 6.04 && result_ay[6] < 11.44) result[3].setText("표준");
        else if (result_ay[6] >= 11.44 && result_ay[6] < 14.14) result[3].setText("큼");
        else if (result_ay[6] >= 14.14) result[3].setText("매우 큼");
        /////////////////////////////////////////////////////////////////////////////
        sb[0].setProgress((int) (result_ay[4] * 10));
        sb[1].setProgress((int) (result_ay[3] * 10));
        sb[2].setProgress((int) (result_ay[2] * 10));
        sb[3].setProgress((int) (result_ay[6] * 10));
    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                ///////// TOAST EVENT
//
//                if (numBack == 0) {
//                    numBack = 1;
//                    evnts.toastMessage(context, "뒤로 가기 버튼을 한번 더 누르면 종료됩니다");
//                    final Handler hd = new Handler();
//                    hd.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            hd.removeCallbacksAndMessages(null);
//
//                            hd.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    numBack = 0;
//                                }
//                            }, 1000);
//                        }
//                    }, 1000);
//                } else {
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                }
//                break;
//        }
//        return false;
//    }


    @Override
    public void onBackPressed() {
        final long FINSH_INTERVAL_TIME = 2000;
        long backPressedTime = 0;
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            this.finish();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "'뒤로'버튼을한번더누르시면종료됩니다.", Toast.LENGTH_SHORT).show();
        }


    }
}
