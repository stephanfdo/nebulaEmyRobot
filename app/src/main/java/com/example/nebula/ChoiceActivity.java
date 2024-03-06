package com.example.nebula;


import static com.example.nebula.MyUtils.concludeSpeak;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.function.beans.EmotionsType;
import com.sanbot.opensdk.function.beans.LED;
import com.sanbot.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.speech.Grammar;
import com.sanbot.opensdk.function.beans.speech.RecognizeTextBean;
import com.sanbot.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.sanbot.opensdk.function.beans.wing.AbsoluteAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.NoAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.RelativeAngleWingMotion;
import com.sanbot.opensdk.function.unit.HDCameraManager;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.ModularMotionManager;
import com.sanbot.opensdk.function.unit.ProjectorManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.WingMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.hardware.ObstacleListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.RecognizeListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoiceActivity extends TopBaseActivity {
    private final static String TAG = "DIL-BAS";
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button3)
    Button button3;

    @BindView(R.id.exitButton)
    Button exitButton;
    @BindView(R.id.btn_motion3)
    Button btnraccon;

    @BindView(R.id.btn_motion2)
    Button btniiot;
    @BindView(R.id.btn_motion)
    Button button4;

    @BindView(R.id.btn_pro)
    Button btn_chat;

    @BindView(R.id.btnback)
    Button btnback;

    @BindView(R.id.handle)
    Button slider;
    public static boolean busy = false;

    private boolean yes=false;
    private Handler delayHandler = new Handler();
    private HDCameraManager hdCameraManager; //video, faceRec
    private HeadMotionManager headMotionManager;    //head movements
    private WingMotionManager wingMotionManager;    //hands movements

    private BroadcastReceiver broadcastReceiver;
    private SpeechManager speechManager; //voice, speechRec
    private SystemManager systemManager; //emotions
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private ModularMotionManager modularMotionManager; //wander
    private WheelMotionManager wheelMotionManager;
    private MediaPlayer mediaPlayer1;
    private static String recodnizeText;

    private boolean slide=false;
    private static boolean available=true;



    private byte handAb = AbsoluteAngleWingMotion.PART_RIGHT;
    private byte handRe = RelativeAngleWingMotion.PART_RIGHT;

    ProjectorManager projectorManager=(ProjectorManager)getUnitManager(FuncConstant.PROJECTOR_MANAGER);

    NoAngleWingMotion noAngleWingMotionUPLeft = new NoAngleWingMotion(NoAngleWingMotion.PART_LEFT, 4, NoAngleWingMotion.ACTION_UP);

    NoAngleWingMotion noAngleWingMotionDOWNLeft = new NoAngleWingMotion(NoAngleWingMotion.PART_LEFT, 4, NoAngleWingMotion.ACTION_DOWN);

    NoAngleWingMotion noAngleWingMotionDOWNRight = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 4, NoAngleWingMotion.ACTION_DOWN);

    NoAngleWingMotion noAngleWingMotionUPRight = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 4, NoAngleWingMotion.ACTION_UP);

    DistanceWheelMotion distanceWheelMotionForward = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN,5,500);
    //head motion
    LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(
            LocateAbsoluteAngleHeadMotion.ACTION_VERTICAL_LOCK,90,30
    );
    RelativeAngleHeadMotion relativeHeadMotionDOWN = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_DOWN, 30);

    RelativeAngleHeadMotion relativeAngleHeadMotionLeft = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFT,10);
    RelativeAngleHeadMotion relativeAngleHeadMotionRight = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_RIGHT,10);

    NoAngleWingMotion noAngleWingMotionSTOPRight = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 5, NoAngleWingMotion.ACTION_STOP);

    NoAngleWingMotion noAngleWingMotionSTOPLeft = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 5, NoAngleWingMotion.ACTION_STOP);


    DistanceWheelMotion distanceWheelMotionforward= new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN,5,600);


    SlidingDrawer simpleSlidingDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            register(ChoiceActivity.class);
            //screen always on
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);
            ButterKnife.bind(this);
            hdCameraManager = (HDCameraManager) getUnitManager(FuncConstant.HDCAMERA_MANAGER);
            systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
            hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
            wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
            speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
            wingMotionManager = (WingMotionManager) getUnitManager(FuncConstant.WINGMOTION_MANAGER);
            headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
            //float button of the system
            systemManager.switchFloatBar(true, getClass().getName());
            //mediaPlayer1 = MediaPlayer.create(this, R.raw.productntro);

            slide=false;
            simpleSlidingDrawer=(SlidingDrawer) findViewById(R.id.slidingdrawer);
        //LOAD handshakes stats
        MySettings.initializeXML();
        MySettings.loadHandshakes();
            //initialize speak
        MySettings.initializeSpeak();
            available=true;


            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                        speechManager.startSpeak("we started with Electrical Engineering,  then the electronic engineering,  Telecommunication,  at last could not least Information technology CMS, if you want to know anything of that product please type on my screen ", MySettings.getSpeakDefaultOption());
                        concludeSpeak(speechManager);

                }
            }, 4000);

            yes=false;
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(yes==false) {

                        finish();
                        Intent intent = new Intent(ChoiceActivity.this, MainActivity.class);
                        startActivity(intent);
                    }


                }
            }, 25000);




            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.exitButton)
                public void onClick(View view) {
                    wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));
                    finishAffinity();
                    System.exit(0);
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.button2)
                public void onClick(View view) {
                    wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));
                    Intent intent = new Intent(ChoiceActivity.this, Production1.class);
                    startActivity(intent);
                    speechManager.stopSpeak();
                    mediaPlayer1.stop();
                    finish();
                }
            });
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.button3)
                public void onClick(View view) {
                    wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));
                    Toast.makeText(ChoiceActivity.this, "speech  listner started", Toast.LENGTH_SHORT).show();
                    speechManager.doWakeUp();
                    speechManager.stopSpeak();
                    speechlistner();
                }
            });
            btniiot.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.btn_motion2)
                public void onClick(View view) {
                    yes = true;
                    wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));
                    Intent intent = new Intent(ChoiceActivity.this, Production4.class);
                    startActivity(intent);
                    speechManager.stopSpeak();
                    finish();
                }
            });

            btnraccon.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.btn_motion3)
                public void onClick(View view) {
                    yes = true;
                    wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));
                    speechManager.stopSpeak();
                    Intent intent = new Intent(ChoiceActivity.this, Production2.class);
                    startActivity(intent);
                    finish();
                }
            });

            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.btn_motion)
                public void onClick(View view) {
                    yes = true;
                    wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));
                    Intent intent = new Intent(ChoiceActivity.this, Production5.class);
                    startActivity(intent);
                    speechManager.stopSpeak();
                    //mediaPlayer1.stop();
                    finish();
                }
            });


            btn_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.btn_pro)
                public void onClick(View view) {
                    yes = true;
                    Intent intent = new Intent(ChoiceActivity.this, Production3.class);
                    startActivity(intent);
                   // mediaPlayer1.stop();
                    finish();

                }
            });

            btnback.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.btnback)
                public void onClick(View view) {
                    Intent intent = new Intent(ChoiceActivity.this, inquiries.class);
                    startActivity(intent);
                    mediaPlayer1.stop();
                    finish();
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
                Toast.makeText(ChoiceActivity.this,"text" + grammer1, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onRecognizeText(RecognizeTextBean recognizeTextBean) {

                Toast.makeText(ChoiceActivity.this, "rec text" + recognizeTextBean.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecognizeVolume(int i) {
            }

            @Override
            public void onStartRecognize() {
                Toast.makeText(ChoiceActivity.this, "start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopRecognize() {

                Toast.makeText(ChoiceActivity.this, "stop rec", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, int i1) {

            }
        });
    }


    Runnable objrunnable4=new Runnable() {
        @Override
        public void run() {

            hardWareManager.setOnHareWareListener(new ObstacleListener() {
                @Override
                public void onObstacleStatus(boolean b) {
                    if (!b && available==true) {
                        Toast.makeText(ChoiceActivity.this, "No One here", Toast.LENGTH_SHORT).show();
                        available=false;
                        Intent intent = new Intent(ChoiceActivity.this, MainActivity.class);
                        startActivity(intent);
                        mediaPlayer1.stop();
                        finish();
                    } else {
                        systemManager.showEmotion(EmotionsType.SPEAK);
                        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM_THREE_GROUP));

                    }
                }
            });
        }


    };




}