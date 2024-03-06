package com.example.nebula;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.nebula.MyUtils.concludeSpeak;
import static com.example.nebula.MyUtils.sleepy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.function.beans.EmotionsType;
import com.sanbot.opensdk.function.beans.FaceRecognizeBean;
import com.sanbot.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.NoAngleWheelMotion;
import com.sanbot.opensdk.function.beans.wing.AbsoluteAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.RelativeAngleWingMotion;
import com.sanbot.opensdk.function.unit.HDCameraManager;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.ModularMotionManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.WingMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.media.FaceRecognizeListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.WakenListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

public class StartActivity extends TopBaseActivity {
    private final static String TAG = "DIL-SPLASH";

    public static boolean busy = false;

    public static boolean hdcamera = false;

    public static boolean face = false;
    public static boolean detect=false;
    public static boolean irdetect=false;

    public static boolean pirdetect=false;

    @BindView(R.id.exitButton)
    Button exitButton;

    @BindView(R.id.handle)
    Button slider;

    @BindView(R.id.welcomeimg)
    ImageView welcome;

    @BindView(R.id.welcome)
    ImageView welcomeimg2;

    private HDCameraManager hdCameraManager;
    private ModularMotionManager modularMotionManager; //wander
    private HardWareManager hardWareManager;
    private WheelMotionManager wheelMotionManager;
    private SpeechManager speechManager; //voice, speechRec
    private SystemManager systemManager; //emotions
    private HeadMotionManager headMotionManager;    //head movements
    private WingMotionManager wingMotionManager;

    public static boolean find=false;
    public static boolean obstacle=false;

    public static boolean obstacle2=false;
    public static boolean slide=false;

    private Handler delayHandler = new Handler();

    private byte handAb = AbsoluteAngleWingMotion.PART_RIGHT;
    private byte handRe = RelativeAngleWingMotion.PART_RIGHT;

    //hands movements
    //head motion
    LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(
            LocateAbsoluteAngleHeadMotion.ACTION_VERTICAL_LOCK,0,40
    );

    DistanceWheelMotion distanceWheelMotionforward= new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN,4,200);
    DistanceWheelMotion distanceWheelMotionfright= new DistanceWheelMotion(DistanceWheelMotion.ACTION_RIGHT_FORWARD_RUN,5,500);
    DistanceWheelMotion distanceWheelMotionfleft= new DistanceWheelMotion(DistanceWheelMotion.ACTION_LEFT_FORWARD_RUN,5,500);
    DistanceWheelMotion distanceWheelMotionStop= new DistanceWheelMotion(DistanceWheelMotion.ACTION_STOP_RUN,5,100);

    NoAngleWheelMotion noAngleWheelMotionleft=new NoAngleWheelMotion(NoAngleWheelMotion.ACTION_LEFT_FORWARD, 5,5000 );
    NoAngleWheelMotion noAngleWheelMotionRight=new NoAngleWheelMotion(NoAngleWheelMotion.ACTION_RIGHT_FORWARD, 5,500);

    RelativeAngleHeadMotion relativeHeadMotionDOWN = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_DOWN, 30);
    RelativeAngleHeadMotion relativeAngleHeadMotionLeft = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFT,10);
    RelativeAngleHeadMotion relativeAngleHeadMotionRight = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_RIGHT,10);
    SlidingDrawer simpleSlidingDrawer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        try {
            register(ChoiceActivity.class);
            //screen always on
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.start_activity);
            ButterKnife.bind(this);
            speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
            hdCameraManager=(HDCameraManager) getUnitManager(FuncConstant.HDCAMERA_MANAGER);
            systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
            modularMotionManager = (ModularMotionManager) getUnitManager(FuncConstant.MODULARMOTION_MANAGER);
            hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
            wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
            headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
            wingMotionManager = (WingMotionManager) getUnitManager(FuncConstant.WINGMOTION_MANAGER);
            //float button of the system
            systemManager.switchFloatBar(true, getClass().getName());

            //check app permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 12);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 12);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, 12);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{INTERNET}, 12);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 12);

            }
            simpleSlidingDrawer=(SlidingDrawer) findViewById(R.id.slidingdrawer);

           // mediaPlayer = MediaPlayer.create(this, R.raw.welcome);

            welcome.setVisibility(View.INVISIBLE);

            welcomeimg2.setVisibility(View.VISIBLE);
            detect=false;
            slide=false;
            irdetect=false;
            obstacle=false;
            pirdetect=false;
            obstacle2=false;
            face=false;
            hdcamera=false;

            busy = false;
            //LOAD handshakes stats
            MySettings.initializeXML();
            MySettings.loadHandshakes();

            //initialize speak
            MySettings.initializeSpeak();


            GifImageView gifImageView = findViewById(R.id.gifImageView);
            gifImageView.setImageResource(R.drawable.welcome);



            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.exitButton)
                public void onClick(View view) {
                    wanderOffNow();
                    finishAffinity();
                    System.exit(0);
                }
            });

            slider.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.handle)
                public void onClick(View view) {
                    if(slide==false){
                        simpleSlidingDrawer.animateOpen();
                        slide=true;
                    }
                    else {
                        simpleSlidingDrawer.animateClose();
                        slide=false;
                    }
                }
            });

            headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);
            hdcameralistner();
            //wanderOnNow();
            //initHardwareListeners();
            //initialize body

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //hands down
                    AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(AbsoluteAngleWingMotion.PART_BOTH, 8, 180);
                    wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
                    //initially sets the wander to on
                }
            }, 1000);


        }catch(Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();

        }


    }
    @Override
    protected void onMainServiceConnected() {

    }
    public void wanderOnNow() {
        Toast.makeText(StartActivity.this, "Wander is calling " + MySettings.isWanderAllowed()+" now", Toast.LENGTH_SHORT).show();
        if (!busy) {
            MySettings.setWanderAllowed(true);
            Toast.makeText(StartActivity.this, "Wander " + MySettings.isWanderAllowed()+" now", Toast.LENGTH_SHORT).show();
            modularMotionManager.switchWander(MySettings.isWanderAllowed());
            Log.i(TAG, "Wander " + MySettings.isWanderAllowed() + " now");
        }
    }
    public void wanderOffNow() {
        MySettings.setWanderAllowed(false);
        Toast.makeText(StartActivity.this, "Wander off now", Toast.LENGTH_SHORT).show();
        modularMotionManager.switchWander(false);
        Log.i(TAG, "Wander forced off now");
    }


    public void hdcameralistner() {


        if (hdcamera == false) {
            hdCameraManager.setMediaListener(new FaceRecognizeListener() {
                @Override
                public void recognizeResult(List<FaceRecognizeBean> list) {
                    if(face==false) {
                        welcome.setVisibility(View.VISIBLE);
                        welcomeimg2.setVisibility(View.INVISIBLE);
                        speechManager.startSpeak("Hello, Welcome to the SLT-Mobitel NEBULA Institute of Technology", MySettings.getSpeakDefaultOption());
                        concludeSpeak(speechManager);
                       // mediaPlayer.start();
                        //hand up
                        //up the head
                        headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);
                        //hand up
                        AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(handAb, 5, 70);
                        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
                        //rotate head
                        headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotionRight);

                        face = true;
                        hdcamera = true;

                        Toast.makeText(StartActivity.this, "Smiling", Toast.LENGTH_SHORT).show();
                        systemManager.showEmotion(EmotionsType.SMILE);
                        //say hi

                        pirdetect = true;


                        delayHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    // wanderOffNow();
                                    Intent intent = new Intent(StartActivity.this, HandShake.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    Toast.makeText(StartActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }, 4000);


                    }
                }
            });


        }
    }







    public void distanceForward(){
        wheelMotionManager.doDistanceMotion(distanceWheelMotionforward);
        sleepy(0.5);
    }

    public void right(){
        wheelMotionManager.doNoAngleMotion(noAngleWheelMotionRight);
        sleepy(0.5);
    }

    public void left(){
        wheelMotionManager.doNoAngleMotion(noAngleWheelMotionleft);
        sleepy(0.5);
    }

    public void distanceStop(){
        wheelMotionManager.doDistanceMotion(distanceWheelMotionStop);
        sleepy(0.5);

    }


    private class WheelMotionAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // Run your wheel moving methods one after another here
            right();
            left();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // This method is called after doInBackground has completed
            // You can start the next method or perform other actions here
        }
    }





    public void speech(){
        speechManager.setOnSpeechListener(new WakenListener() {
            @Override
            public void onWakeUp() {

            }

            @Override
            public void onSleep() {

            }

            @Override
            public void onWakeUpStatus(boolean b) {

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the PIR sensor listener when the activity is destroyed
        hardWareManager.setOnHareWareListener(null);
    }




}