package etc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import healthcare.demand.ppg.R;


/**
 * Created by Dean on 03/08/2016.
 */
public class Events {

    Views vs = new Views();

    public void toastMessage(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }


    public void launchPreviousActivity(View v, final Activity activity, final Class cls){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, cls);
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(R.anim.appear_from_left_300, R.anim.disappear_to_right_300);
            }
        });
    }

    public void launchPreviousActivity(final Activity activity, final Class cls){

        Intent intent = new Intent(activity, cls);
        intent.putExtra("id", activity.getIntent().getStringExtra("id"));
        intent.putExtra("name", activity.getIntent().getStringExtra("name"));
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.appear_from_left_300, R.anim.disappear_to_right_300);
    }

    public void launchNextActivity(View v, final Activity activity, final Class cls){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, cls);
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(R.anim.appear_from_right_300, R.anim.disappear_to_left_300);
            }
        });
    }




}
