package healthcare.demand.ppg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import etc.ImageProcessing;

public class PPG_hrm extends Activity {

    private static final String TAG = "hrm";
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;

    private static WakeLock wakeLock = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE {
        GREEN, RED
    }

    ;

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    //
    private static int formerCardiacInterval = 0;
    private static int updatedCardiacInterval = 0;
    private static int[] cardiacIntervalArr = new int[200];
    private static int cardiacIntervalIndex = 0;
    private static int tmpInterval;
    //
    public static Context c;
    public static Activity activity;
    //
    public static int heartRate = 0;
    //
    public static AtomicInteger timeSum = new AtomicInteger(0);
    //
    /////////////////////////////// VIEWS ///////////////////////////////////

    Context context;

    static public ProgressBar progress;
    static public TextView bpm;
    static public ImageView heart;
    TextView unit;
    FrameLayout canvas;
    static public TextView explanation;
    static public FrameLayout cover_graph;
    static public Animation disappearedToRight200;
    /////////////////////////////////////////////////////////////////////////
    Handler mHd = new Handler();
    public static AtomicBoolean isDone = new AtomicBoolean(false);

    public static String id;
    public static String name;
    // 161123
    public static TextView ci;

    long backPressedTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hrm);

        c = getBaseContext();
        activity = this;
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");

        adjustViews();
        mentEvent();

        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        image = findViewById(R.id.image);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        titleBar();
    }

    //
    public void adjustViews() {
        context = getApplicationContext();
        progress = (ProgressBar) findViewById(R.id.progress);
        bpm = (TextView) findViewById(R.id.bpm);
        heart = (ImageView) findViewById(R.id.heart);
        unit = (TextView) findViewById(R.id.unit);
        canvas = (FrameLayout) findViewById(R.id.canvas);
        explanation = (TextView) findViewById(R.id.explanation);
        cover_graph = (FrameLayout) findViewById(R.id.cover_graph);

        progress.setMax(59500);
        //
        disappearedToRight200 = AnimationUtils.loadAnimation(this, R.anim.disappear_to_right_500);
        // 161123
        ci = (TextView) findViewById(R.id.ci);
    }

    private void titleBar() {
        View titlebar = (View) findViewById(R.id.title_bar);
        TextView title = (TextView) titlebar.findViewById(R.id.tv_title);
        ImageView back = (ImageView) titlebar.findViewById(R.id.iv_title_back);

        back.setVisibility(View.VISIBLE);

        title.setText("???????????? ?????? (??????/??????)");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PPG_measure.class);
                startActivity(i);
                overridePendingTransition(R.anim.appear_from_right_300, R.anim.disappear_to_left_300);
                finish();
            }
        });

        ImageView menu = (ImageView) titlebar.findViewById(R.id.iv_titlebar_menu);
        if (menu.getVisibility() != View.GONE)
            menu.setVisibility(View.GONE);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();

        camera = Camera.open();

        startTime = System.currentTimeMillis();

        //// TEMPORARY INITIATION 161123 ////
        timeSum.set(0);
        cardiacIntervalIndex = 0;
        cardiacIntervalArr = new int[200];
        /////////////////////////////////////
    }

    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();

        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private static PreviewCallback previewCallback = new PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), width, height);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    //
                    heart.setImageResource(R.drawable.heart_big);
                    //

                    // CALCULATE THE CARDIAC INTERVAL WHEN COUNT OF 'beats' INCREASED
                    //// SET THE UPPER LIMITATION 1200
                    updatedCardiacInterval = (int) System.currentTimeMillis();

                    tmpInterval = updatedCardiacInterval - formerCardiacInterval;

                    formerCardiacInterval = updatedCardiacInterval;

                    if (tmpInterval > 333 && tmpInterval < 1200) { //**

                        cardiacIntervalArr[cardiacIntervalIndex] = tmpInterval;

                        ci.setText(tmpInterval + " msec"); ////////////////////////////////////////// 161123

                        timeSum.set(timeSum.get() + tmpInterval);

                        if (timeSum.get() + 1000 < 60000)
                            cover_graph.startAnimation(disappearedToRight200);

                        progress.setProgress(timeSum.get());

                        Log.d("MeasuringTime", timeSum.get() + "");

                        Log.d("CARDIAC INTERVAL", cardiacIntervalArr[cardiacIntervalIndex] + "");

                        cardiacIntervalIndex++;

                        Log.d("CARDIAC INTERVAL INDEX", cardiacIntervalIndex + "");

                        if (timeSum.get() + 1000 >= 60000) { //// 1MIN = 60,000 MSEC
                            isDone.set(true);
                            explanation.setText("????????? ?????????????????????!");
                            Handler initHd = new Handler();
                            initHd.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Log.e("adfadfa", "1213121231212312");
                                    Intent intent = new Intent(c, PPG_result.class);
                                    intent.putExtra("ci", cardiacIntervalArr);
                                    intent.putExtra("bpm", heartRate);
                                    intent.putExtra("length", cardiacIntervalIndex - 1);
                                    intent.putExtra("id", id);
                                    intent.putExtra("name", name);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    PPG_hrm.c.startActivity(intent);
                                    activity.finish();
                                }
                            }, 2000);
                        }
                    }
                    //
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
                heart.setImageResource(R.drawable.heart_small);
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                image.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 4) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                heartRate = beatsAvg;
                bpm.setText(String.valueOf(beatsAvg) + " ");
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                //Log.e("Preview-surfaceCallback", "Exception in setPreviewDisplay()", t); /////////// REMOVABLE
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }

    private void mentEvent() {
        if (isDone.get() == false) explanation.setText("?????? ????????????\n????????? ???????????????.");
        mHd.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDone.get() == false)
                    explanation.setText("?????????????????? ???????????????\n?????????????????? ??? ??? ????????? ????????????\n?????? ?????? ?????? ????????? ???????????????.");
                mHd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isDone.get() == false)
                            explanation.setText("???????????? ?????? ?????? ????????????\n60~100bpm?????????.");
                        mHd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isDone.get() == false)
                                    explanation.setText("1??? ????????? ?????? ?????? ????????????\n???????????? ???????????????.");
                                mHd.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mHd.removeCallbacksAndMessages(null);
                                        if (isDone.get() == false)
                                            explanation.setText("????????? ????????? ?????????,\n??????????????? ?????? ?????? ?????????.");
                                        mentEvent();
                                    }
                                }, 6000);
                            }
                        }, 6000);
                    }
                }, 10000);
            }
        }, 6000);
    }

    @Override
    public void onBackPressed() {
        final long FINSH_INTERVAL_TIME = 2000;
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            finish();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "'??????'?????????????????????????????????????????????.", Toast.LENGTH_SHORT).show();
        }
    }


}
