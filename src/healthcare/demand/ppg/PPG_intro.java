package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by ㅇㅇ on 2017-01-10.
 */

public class PPG_intro extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.intro);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* 메뉴액티비티를 실행하고 로딩화면을 죽인다.*/
                Intent mainIntent = new Intent(PPG_intro.this, PPG_login.class);
                PPG_intro.this.startActivity(mainIntent);
                PPG_intro.this.finish();
            }
        }, 2000);
    }
}
