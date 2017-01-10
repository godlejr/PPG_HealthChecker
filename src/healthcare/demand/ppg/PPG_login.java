package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import etc.Server_Connector;

public class PPG_login extends Activity {
    /**
     * Called when the activity is first created.
     */

    private Context context;

    private EditText et_id, et_pw;

    // SERVER & PREFERENCES
    private Server_Connector connector;

    private String email, pwd; //request
    private SharedPreferences loginInfo;
    private SharedPreferences.Editor editor;
    private String id, name; //response

    private long backPressedTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppg_login);

        adjustViews();
        defineEvent();
    }

    private void adjustViews() {

        context = getApplicationContext();

        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);

    }

    private void defineEvent() {

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
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
                editor.putString("id",id);
                editor.putString("name",name);
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
