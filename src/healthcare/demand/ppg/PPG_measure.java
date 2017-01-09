package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import etc.Events;

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
        titleBar();
    }

    private void titleBar(){
        View titlebar = (View)findViewById(R.id.title_bar);
        TextView title = (TextView)titlebar.findViewById(R.id.tv_title);
        ImageView back = (ImageView)titlebar.findViewById(R.id.iv_title_back);

        title.setText("스트레스 측정 (맥파/호흡)");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PPG_login.class);
                startActivity(i);
                finish();
            }
        });

        ImageView menu = (ImageView)titlebar.findViewById(R.id.iv_titlebar_menu);
        PopupMenu popupMenu = new PopupMenu(context, menu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.logout:
                        Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show();
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
//        vm.resizeSingleView(fl_circle, "frame", 652, 652, 0, 250, 0, 0);
//        vm.reformSingleTextBasedView(context, how, 43, "regular", "frame", 0, 0, 0, 1100, 0, 0);
//        vm.reformSingleTextBasedView(context, measure, 43, "regular", "frame", 653, 100, 0, 0, 0, 150);
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