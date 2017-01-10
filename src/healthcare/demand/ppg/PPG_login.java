package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
    ImageView splash;
    LinearLayout ll_login;
    ImageView upper_logo;
    TextView tv_id, tv_pw;
    EditText et_id, et_pw;
    ImageView line_0, line_1;
    LinearLayout ll_auto;

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
        splashEvent();
        defineEvent();
    }

    private void adjustViews() {

        context = getApplicationContext();
        // VIEWS : DEFINE
        splash = (ImageView) findViewById(R.id.splash);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        upper_logo = (ImageView) findViewById(R.id.upper_logo);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_pw = (TextView) findViewById(R.id.tv_pw);
        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);
        line_0 = (ImageView) findViewById(R.id.line_0);
        line_1 = (ImageView) findViewById(R.id.line_1);
        tv_auto = (TextView) findViewById(R.id.tv_auto);
        //tv_find = (TextView)findViewById(R.id.tv_find); // NOT YET
        login = (TextView) findViewById(R.id.login);

        // ANIMS
        fade_in_1500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_1500);
        fade_out_1500 = AnimationUtils.loadAnimation(this, R.anim.fade_out_1500);
    }

    private void defineEvent() {
        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void splashEvent() {
        final Handler hd = new Handler();
        splash.startAnimation(fade_in_1500);
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.startAnimation(fade_out_1500);
                hd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        splash.setVisibility(View.GONE);
                        hd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ll_login.setVisibility(View.VISIBLE);
                                login.setVisibility(View.VISIBLE);
                                ll_login.startAnimation(fade_in_1500);
                                login.startAnimation(fade_in_1500);
                                hd.removeCallbacksAndMessages(null);
                            }
                        }, 500);
                    }
                }, 1000);
            }
        }, 1000);
    }

    public void login() {
        email = et_id.getText().toString();
        pwd = et_pw.getText().toString();
        String url = getString(R.string.server_url);

        connector = new Server_Connector();
        connector.addVariable("email", email);
        connector.addVariable("password", pwd);
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

                loginInfo = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                editor = loginInfo.edit();
                editor.putString("email", email);
                editor.putString("password", pwd);
                editor.commit();

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




}
