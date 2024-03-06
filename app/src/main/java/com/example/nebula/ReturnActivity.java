package com.example.nebula;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static com.example.nebula.MyUtils.concludeSpeak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.speech.Grammar;
import com.sanbot.opensdk.function.beans.speech.RecognizeTextBean;
import com.sanbot.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.NoAngleWheelMotion;
import com.sanbot.opensdk.function.beans.wing.AbsoluteAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.NoAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.RelativeAngleWingMotion;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.ModularMotionManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.WingMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.WakenListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReturnActivity extends TopBaseActivity {

    private boolean slide=false;

    private boolean yes=false;
    @BindView(R.id.exitButton)
    Button exitButton;

    @BindView(R.id.handle)
    Button slider;

    @BindView(R.id.btnyes)
    Button btnyes;

    @BindView(R.id.btnno)
    Button btnno;

    MediaPlayer mediaPlayer7;
    SlidingDrawer simpleSlidingDrawer;




    private final static String TAG = "DIL-SPLASH";

    public static boolean busy = false;
    private byte handAb = AbsoluteAngleWingMotion.PART_RIGHT;
    private byte handRe = RelativeAngleWingMotion.PART_RIGHT;

    private ModularMotionManager modularMotionManager; //wander
    private HardWareManager hardWareManager;
    private WheelMotionManager wheelMotionManager;
    private SpeechManager speechManager; //voice, speechRec
    private SystemManager systemManager; //emotions
    private HeadMotionManager headMotionManager;    //head movements
    private WingMotionManager wingMotionManager;

    public static boolean find=false;

    private MediaPlayer mediaPlayer1;
    private static String recodnizeText;


    private static boolean available=true;
    private static boolean available4=true;

    private Handler delayHandler = new Handler();


    //hands movements
    //head motion
    LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(
            LocateAbsoluteAngleHeadMotion.ACTION_VERTICAL_LOCK,90,30
    );

    DistanceWheelMotion distanceWheelMotionforward= new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN,4,800);
    DistanceWheelMotion distanceWheelMotionfright= new DistanceWheelMotion(DistanceWheelMotion.ACTION_RIGHT_FORWARD_RUN,5,500);
    DistanceWheelMotion distanceWheelMotionfleft= new DistanceWheelMotion(DistanceWheelMotion.ACTION_LEFT_FORWARD_RUN,5,500);
    DistanceWheelMotion distanceWheelMotionStop= new DistanceWheelMotion(DistanceWheelMotion.ACTION_STOP_RUN,5,100);

    NoAngleWheelMotion noAngleWheelMotionleft=new NoAngleWheelMotion(NoAngleWheelMotion.ACTION_LEFT_FORWARD, 5,5000 );
    NoAngleWheelMotion noAngleWheelMotionRight=new NoAngleWheelMotion(NoAngleWheelMotion.ACTION_RIGHT_FORWARD, 5,500);
    NoAngleWingMotion noAngleWingMotionUP = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 5, NoAngleWingMotion.ACTION_UP);
    NoAngleWingMotion noAngleWingMotionDOWN = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 5, NoAngleWingMotion.ACTION_DOWN);
    NoAngleWingMotion noAngleWingMotionSTOP = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 5, NoAngleWingMotion.ACTION_STOP);

    RelativeAngleHeadMotion relativeHeadMotionDOWN = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_DOWN, 30);
    RelativeAngleHeadMotion relativeAngleHeadMotionLeft = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFT,10);
    RelativeAngleHeadMotion relativeAngleHeadMotionRight = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_RIGHT,10);


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        try {
            register(ChoiceActivity.class);
            //screen always on
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_return);
            ButterKnife.bind(this);
            speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
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


            yes = false;
            slide = false;
            simpleSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechManager.startSpeak("If you want to know anything ,  regarding our courses,  please say yes ", MySettings.getSpeakDefaultOption());
                    concludeSpeak(speechManager);

                }
            }, 1000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechManager.doWakeUp();
                    speechlistner();
                    speechTest();

                }
            }, 3000);



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(yes==false) {
                        finish();
                        Intent intent = new Intent(ReturnActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }, 30000);




            btnyes.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.btnyes)
                public void onClick(View view) {
                    yes = true;
                    finish();
                    Intent intent = new Intent(ReturnActivity.this, ChoiceActivity.class);
                    startActivity(intent);
                }
            });

            btnno.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.btnno)
                public void onClick(View view) {
                    yes = true;
                    finish();
                    Intent intent = new Intent(ReturnActivity.this, SequenceActivity.class);
                    startActivity(intent);
                }
            });

            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.exitButton)
                public void onClick(View view) {
                    //mediaPlayer7.stop();
                    finishAffinity();
                    System.exit(0);
                }
            });

            slider.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.handle)
                public void onClick(View view) {
                    if (slide == false) {
                        simpleSlidingDrawer.animateOpen();
                        slide = true;
                    } else {
                        simpleSlidingDrawer.animateClose();
                        slide = false;
                    }
                }
            });


        }catch(Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();

        }


    }
    @Override
    protected void onMainServiceConnected() {

    }



    public void speechlistner(){
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                String grammer1=grammar.getText();
                Toast.makeText(ReturnActivity.this,grammer1, Toast.LENGTH_SHORT).show();
                if ("yes".equals(grammer1)) {
                    yes = true;
                    finish();
                    Intent intent = new Intent(ReturnActivity.this, ChoiceActivity.class);
                    startActivity(intent);
                } else if ("no".equals(grammer1)) {
                    yes = true;
                    finish();
                    Intent intent = new Intent(ReturnActivity.this, SequenceActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ReturnActivity.this, "not detect", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public void onRecognizeText(RecognizeTextBean recognizeTextBean) {

            }

            @Override
            public void onRecognizeVolume(int i) {

            }

            @Override
            public void onStartRecognize() {

            }

            @Override
            public void onStopRecognize() {

            }

            @Override
            public void onError(int i, int i1) {

            }
        });
    }




    public void speechTest(){
        speechManager.setOnSpeechListener(new WakenListener() {
            @Override
            public void onWakeUp() {
                Toast.makeText(ReturnActivity.this, "listner wakeup", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSleep() {
                Toast.makeText(ReturnActivity.this, "listner sleep", Toast.LENGTH_SHORT).show();
                    speechManager.doWakeUp();
                    speechlistner();

            }
            @Override
            public void onWakeUpStatus(boolean b) {

            }
        });
    }







}