package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Created by ㅇㅇ on 2017-01-10.
 */

public class PPG_intro extends Activity {

    private SharedPreferences loginInfo;
    private String id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* 메뉴액티비티를 실행하고 로딩화면을 죽인다.*/
                loginInfo = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                id = loginInfo.getString("id", null);
                name = loginInfo.getString("name", null);

                if (id != null && name != null) {
                    Intent intent = new Intent(PPG_intro.this, PPG_measure.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    startActivity(intent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(PPG_intro.this, PPG_login.class);
                    PPG_intro.this.startActivity(mainIntent);
                    PPG_intro.this.finish();
                }
            }
        }, 2000);
    }
}
