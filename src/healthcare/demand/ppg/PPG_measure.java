package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import etc.Events;
import etc.ViewMethod;
import etc.Views;

public class PPG_measure extends Activity {
    /**
     * Called when the activity is first created.
     */

    ViewMethod vm = new ViewMethod();
    Events events = new Events();
    Views vs = new Views();
    //
    Context context;
    //
    FrameLayout fl_circle;
    ImageView camlight;
    TextView how;
    TextView measure;
    //
    int numBack = 0; // TO FORCE EXIT

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppg_measure);

        adjustViews();

        defineEvent();

    }

    private void adjustViews() {
        context = getApplicationContext();
        //
        ///////////// DEFINE ELEMENTS OF TITLEBAR
        //
        fl_circle = (FrameLayout) findViewById(R.id.fl_circle);
        camlight = (ImageView) findViewById(R.id.camlight);
        how = (TextView) findViewById(R.id.how);
        measure = (TextView) findViewById(R.id.measure);
        //
        vm.resizeSingleView(fl_circle, "frame", 652, 652, 0, 250, 0, 0);
        vm.reformSingleTextBasedView(context, how, 43, "regular", "frame", 0, 0, 0, 1100, 0, 0);
        vm.reformSingleTextBasedView(context, measure, 43, "regular", "frame", 653, 100, 0, 0, 0, 150);
        //vs.customBox(measure, "#4cb3b6", "#4cb3b6", 1, 50);
        //

    }

    private void defineEvent() {
        measure.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //vs.customBox(measure, "#f5f5f5", "#4cb3b6", 1, 50);
                        measure.setTextColor(Color.parseColor("#080e3d"));
                        break;

                    case MotionEvent.ACTION_UP:
                        //vs.customBox(measure, "#4cb3b6", "#4cb3b6", 1, 50);
                        measure.setTextColor(Color.parseColor("#ffffff"));
                        //
                        Intent intent = new Intent(PPG_measure.this, PPG_hrm.class);
                        intent.putExtra("id", getIntent().getStringExtra("id"));
                        intent.putExtra("name", getIntent().getStringExtra("name"));
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.appear_from_right_300, R.anim.disappear_to_left_300);

                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK :
                ///////// TOAST EVENT

                if(numBack == 0){
                    numBack = 1;
                    events.toastMessage(context, "뒤로 가기 버튼을 한번 더 누르면 종료됩니다");
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