package etc;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

/**
 * Created by Dean on 03/08/2016.
 */
public class Views {

    ViewMethod vm = new ViewMethod();

    public void customBox(View v, String bgColor, String lineColor, int radius, int lineWidth){
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setStroke(lineWidth, Color.parseColor(lineColor));
        gd.setCornerRadius((float) vm.adjustedValue(radius));
        v.setBackgroundDrawable(gd);
    }
    public void customBoxUpperRadius(View v, String bgColor, String lineColor, int radius){
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setStroke(1, Color.parseColor(lineColor));
        gd.setCornerRadii(new float[]{vm.adjustedValue(radius), vm.adjustedValue(radius), vm.adjustedValue(radius), vm.adjustedValue(radius), 0, 0, 0, 0});
        v.setBackgroundDrawable(gd);
    }
    public void customBoxLowerRadius(View v, String bgColor, String lineColor, int radius){
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setStroke(1, Color.parseColor(lineColor));
        gd.setCornerRadii(new float[]{0, 0, 0, 0, vm.adjustedValue(radius), vm.adjustedValue(radius), vm.adjustedValue(radius), vm.adjustedValue(radius)});
        v.setBackgroundDrawable(gd);
    }

}
