package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import etc.Server_Connector;

public class PPG_login extends Activity {
    /**
     * Called when the activity is first created.
     */

    Context context;

    // VIEWS
//    ImageView splash;
    LinearLayout ll_login;
    ImageView upper_logo;
    TextView tv_id, tv_pw;
    EditText et_id, et_pw;
    ImageView line_0, line_1;
    LinearLayout ll_auto;
    ImageView checkbox;
    TextView tv_auto;
    //TextView tv_find; // NOT YET
    TextView login;
    //TextView join; // NOT YET

    // ANIMS
    Animation fade_in_1500, fade_out_1500;

    // SERVER & PREFERENCES
    Server_Connector connector;

    String email, pwd; //request
    SharedPreferences loginInfo;
    SharedPreferences.Editor editor;
    String pref_id, pref_pw;
    String id, name; //response
    boolean isChecked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppg_login);

        adjustViews();
//        splashEvent();
        firstView();
        defineEvent();
    }

    private void adjustViews() {

        context = getApplicationContext();
        // VIEWS : DEFINE
//        splash = (ImageView) findViewById(R.id.splash);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        upper_logo = (ImageView) findViewById(R.id.upper_logo);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_pw = (TextView) findViewById(R.id.tv_pw);
        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);
        line_0 = (ImageView) findViewById(R.id.line_0);
        line_1 = (ImageView) findViewById(R.id.line_1);
        ll_auto = (LinearLayout) findViewById(R.id.ll_auto);
        checkbox = (ImageView) findViewById(R.id.checkbox);
        tv_auto = (TextView) findViewById(R.id.tv_auto);
        //tv_find = (TextView)findViewById(R.id.tv_find); // NOT YET
        login = (TextView) findViewById(R.id.login);
        //join = (TextView)findViewById(R.id.join); // NOT YET
        // VIEWS : READJUST & OPTIMIZATION
//        vm.resizeSingleView(splash, getResources(), R.drawable.ppg_demand, "frame", 345, 76);
//        vm.resizeSingleView(upper_logo, getResources(), R.drawable.ppg_logo, "linear", 1080, 989, 0, 80, 0, 0);
//        vm.reformSingleTextBasedView(context, tv_id, 42, "regular", "linear", 0, 0, 100, 0, 100, 0);
//        vm.reformSingleTextBasedView(context, tv_pw, 42, "regular", "linear", 0, 0, 100, 0, 100, 0);
//        vm.reformSingleTextBasedView(context, et_id, 40, "regular", "linear", 880, 70, 100, 0, 100, 0, 0, 0, 0, 0);
//        vm.reformSingleTextBasedView(context, et_pw, 40, "regular", "linear", 880, 70, 100, 0, 100, 0, 0, 0, 0, 0);
//        vm.resizeSingleView(line_0, "linear", 0, 0, 100, 0, 100, 50);
//        vm.resizeSingleView(line_1, "linear", 0, 0, 100, 0, 100, 20);
//        vm.resizeSingleView(ll_auto, "linear", 0, 0, 100, 0, 0, 0);
//        vm.resizeSingleView(checkbox, "linear", 60, 90);
//        vm.reformSingleTextBasedView(context, tv_auto, 36, "regular");
//        vm.reformSingleTextBasedView(context, login, 50, "bold", "frame", 880, 120, 0, 0, 0, 120);
//        vs.customBox(login, "#f5f5f5", "#444444", 60, 2);
        // ANIMS
        fade_in_1500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_1500);
        fade_out_1500 = AnimationUtils.loadAnimation(this, R.anim.fade_out_1500);
    }

    private void defineEvent() {

        ll_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked == false) {
                    isChecked = true;
                    checkbox.setImageResource(R.drawable.check_1);
                    tv_auto.setTextColor(Color.parseColor("#444444"));
                } else if (isChecked == true) {
                    isChecked = false;
                    checkbox.setImageResource(R.drawable.check_0);
                    tv_auto.setTextColor(Color.parseColor("#858585"));
                }
            }
        });

        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

//        ll_login.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        login.setTextColor(Color.parseColor("#f5f5f5"));
//                      //  vs.customBox(login, "#444444", "#444444", 60, 2);
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        login.setTextColor(Color.parseColor("#444444"));
//                    //    vs.customBox(login, "#f5f5f5", "#444444", 60, 2);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        login.setTextColor(Color.parseColor("#444444"));
//                      //  vs.customBox(login, "#f5f5f5", "#444444", 60, 2);
//                        login();
//                        break;
//                }
//                return true;
//            }
//        });
    }

//    private void splashEvent() {
//        final Handler hd = new Handler();
//        splash.startAnimation(fade_in_1500);
//        hd.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                splash.startAnimation(fade_out_1500);
//                hd.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        splash.setVisibility(View.GONE);
//                        hd.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                ll_login.setVisibility(View.VISIBLE);
//                                login.setVisibility(View.VISIBLE);
//                                ll_login.startAnimation(fade_in_1500);
//                                login.startAnimation(fade_in_1500);
//                                hd.removeCallbacksAndMessages(null);
//                            }
//                        }, 500);
//                    }
//                }, 1000);
//            }
//        }, 1000);
//    }

    public void login() {
        email = et_id.getText().toString();
        pwd = et_pw.getText().toString();
        String url = getString(R.string.server_url);

        connector = new Server_Connector();
        connector.addVariable("email", email);
        connector.addVariable("password", pwd);
        connector.execute(url + "login");
        connector.execute(url + "login");

        try {
            JSONArray arr = new JSONArray(connector.get().trim());
            if (arr.length() == 0) {
                Toast.makeText(context, "ID/PW를 확인해주세요", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "로그인 되었습니다", Toast.LENGTH_SHORT).show();


                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    id = String.valueOf(obj.getInt("id"));
                    name = obj.getString("name");
                }

                Intent intent = new Intent(PPG_login.this, PPG_measure.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.appear_from_right_300, R.anim.disappear_to_left_300);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                android.os.Process.killProcess(android.os.Process.myPid());
        }
        return false;
    }

    private void firstView() {
        loginInfo = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);

        pref_id = loginInfo.getString("et_id", "");
        pref_pw = loginInfo.getString("et_pw", "");
        isChecked = loginInfo.getBoolean("isChecked", isChecked);

        et_id.setText(pref_id);
        et_pw.setText(pref_pw);

        checkBox();
    }

    public void onStop() {
        super.onStop();
        loginInfo = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = loginInfo.edit();
        if (isChecked == true) {
            editor.putString("et_id", et_id.getText().toString());
            editor.putString("et_pw", et_pw.getText().toString());
            editor.putBoolean("isChecked", true);
        } else if (isChecked == false) {
            String none = "";
            editor.putString("et_id", none);
            editor.putString("et_pw", none);
            editor.putBoolean("isChecked", false);
        }
        editor.commit();
    }

    public void checkBox() {
        if (isChecked == false) checkbox.setImageResource(R.drawable.check_0);
        else if (isChecked == true) checkbox.setImageResource(R.drawable.check_1);
    }
}
