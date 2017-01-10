package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class PPG_measure extends Activity {
    /**
     * Called when the activity is first created.
     */

    private Context context;
    private TextView measure;

    private SharedPreferences loginInfo;
    private SharedPreferences.Editor editor;

    private long backPressedTime = 0;

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
        ImageView back = (ImageView)titlebar.findViewById(R.id.iv_title_back);

        back.setVisibility(View.GONE);

        ImageView menu = (ImageView)titlebar.findViewById(R.id.iv_titlebar_menu);
        PopupMenu popupMenu = new PopupMenu(context, menu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.logout:

                        loginInfo = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                        editor = loginInfo.edit();
                        editor.remove("id");
                        editor.remove("name");
                        editor.commit();

                        Intent Intent = new Intent(PPG_measure.this, PPG_login.class);
                        PPG_measure.this.startActivity(Intent);
                        PPG_measure.this.finish();

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

    private void adjustViews() {
        context = getApplicationContext();
        measure = (TextView) findViewById(R.id.measure);
    }

    private void defineEvent() {
        measure.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        measure.setTextColor(Color.parseColor("#080e3d"));
                        break;

                    case MotionEvent.ACTION_UP:
                        measure.setTextColor(Color.parseColor("#ffffff"));

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