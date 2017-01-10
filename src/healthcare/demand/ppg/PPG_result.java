package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.lxhrvapi.LXHrvAPI;

import etc.Server_Connector;

public class PPG_result extends Activity {
    /**
     * Called when the activity is first created.
     */

    private Context context;

    private int[] cardiacIntervalArr; //= new int[60];
    private int[] tach_ay; // = new int[61];

    private float result_ay[] = new float[15];

    private int bpm;
    private int cardiacIntervalArrSize;

    private long backPressedTime = 0;

    private TextView ratio;

    private ImageView needle;
    private TextView conclusion;
    private TextView[] tv = new TextView[4];
    private TextView[] result = new TextView[4];
    private SeekBar[] sb = new SeekBar[4];
    private TextView complete;

    private int score = 0;

    private SharedPreferences loginInfo;
    private SharedPreferences.Editor editor;

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

    private void adjustViews() {
        context = getApplicationContext();
        ratio = (TextView) findViewById(R.id.ratio);
        needle = (ImageView) findViewById(R.id.needle);
        conclusion = (TextView) findViewById(R.id.conclusion);
        complete = (TextView) findViewById(R.id.complete);


        tv[0] = (TextView) findViewById(R.id.tv_0);
        tv[1] = (TextView) findViewById(R.id.tv_1);
        tv[2] = (TextView) findViewById(R.id.tv_2);
        tv[3] = (TextView) findViewById(R.id.tv_3);

        result[0] = (TextView) findViewById(R.id.result_0);
        result[1] = (TextView) findViewById(R.id.result_1);
        result[2] = (TextView) findViewById(R.id.result_2);
        result[3] = (TextView) findViewById(R.id.result_3);


        sb[0] = (SeekBar) findViewById(R.id.sb_0);
        sb[1] = (SeekBar) findViewById(R.id.sb_1);
        sb[2] = (SeekBar) findViewById(R.id.sb_2);
        sb[3] = (SeekBar) findViewById(R.id.sb_3);

        sb[0].setMax(100);
        sb[1].setMax(100);
        sb[2].setMax(100);
        sb[3].setMax(180);
    }

    private void titleBar() {
        View titlebar = (View) findViewById(R.id.title_bar);
        TextView title = (TextView) titlebar.findViewById(R.id.tv_title);
        ImageView back = (ImageView) titlebar.findViewById(R.id.iv_title_back);

        title.setText("스트레스 측정");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PPG_measure.class);
                startActivity(i);
                finish();
            }
        });

        titlebar.setBackgroundColor(Color.parseColor("#ffffff"));
        ImageView menu = (ImageView) titlebar.findViewById(R.id.iv_titlebar_menu);
        if (menu.getVisibility() == View.GONE)
            menu.setVisibility(View.VISIBLE);

        PopupMenu popupMenu = new PopupMenu(PPG_result.this, menu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        loginInfo = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                        editor = loginInfo.edit();
                        editor.remove("id");
                        editor.remove("name");
                        editor.commit();

                        Intent Intent = new Intent(PPG_result.this, PPG_login.class);
                        PPG_result.this.startActivity(Intent);
                        PPG_result.this.finish();

                        Toast.makeText(context, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
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

    private void defineEvent() {
        setDegree((float) (1.8 * score));

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PPG_hrm.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.appear_from_left_300, R.anim.disappear_to_right_300);
            }
        });
    }

    private void setDegree(float angle) {
        RotateAnimation anim;
        anim = new RotateAnimation(0.0f, angle, Animation.RELATIVE_TO_SELF, .936073f, Animation.RELATIVE_TO_SELF, 0.5f); //.936073f
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


    @Override
    public void onBackPressed() {
        final long FINSH_INTERVAL_TIME = 2000;
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
