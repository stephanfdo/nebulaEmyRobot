package com.example.nebula;


import static com.example.nebula.MyUtils.concludeSpeak;
import static com.example.nebula.MyUtils.rotateAtRelativeAngle;
import static com.example.nebula.MyUtils.sleepy;
import static com.example.nebula.MyUtils.temporaryEmotion;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import com.sanbot.opensdk.function.beans.LED;
import com.sanbot.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.wing.AbsoluteAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.NoAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.RelativeAngleWingMotion;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.WingMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.hardware.ObstacleListener;
import com.sanbot.opensdk.function.unit.interfaces.hardware.TouchSensorListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

public class HandShake extends TopBaseActivity {

    private final static String TAG = "IGOR-SHK";

    @BindView(R.id.exitButton)
    Button exitButton;
    @BindView(R.id.exit)
    Button skip;

    @BindView(R.id.hiimg)
    ImageView helloimg;

    @BindView(R.id.handimg)
    ImageView handimg;

    @BindView(R.id.sadimg)
    ImageView sadimg;

    @BindView(R.id.handle)
    Button slider;
    //robot managers
    private SpeechManager speechManager; //voice, speechRec
    private HeadMotionManager headMotionManager;    //head movements
    private WingMotionManager wingMotionManager;    //hands movements
    private SystemManager systemManager; //emotions
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager; //wheels

    private boolean listner=false;
    private boolean available6=true;
    private boolean slide=false;

    //boolean to understand if it is in the position waiting the touch of the hand
    private boolean waitingTouchPosition = false;
    Handler waitingTouchHandler = new Handler();
    Handler incitement = new Handler();

    //hand motion
    private byte handAb = AbsoluteAngleWingMotion.PART_RIGHT;
    private byte handRe = RelativeAngleWingMotion.PART_RIGHT;
    NoAngleWingMotion noAngleWingMotionUP = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 5, NoAngleWingMotion.ACTION_UP);
    NoAngleWingMotion noAngleWingMotionDOWN = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 5, NoAngleWingMotion.ACTION_DOWN);
    NoAngleWingMotion noAngleWingMotionSTOP = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 5, NoAngleWingMotion.ACTION_STOP);


    //head motion
    LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(
            LocateAbsoluteAngleHeadMotion.ACTION_VERTICAL_LOCK,90,30
    );
    RelativeAngleHeadMotion relativeHeadMotionDOWN = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_DOWN, 30);
    RelativeAngleHeadMotion relativeAngleHeadMotionLeft = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFT,10);
    RelativeAngleHeadMotion relativeAngleHeadMotionRight = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_RIGHT,10);

    SlidingDrawer simpleSlidingDrawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            register(HandShake.class);
            //screen always on
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            super.onCreate(savedInstanceState);
            //set view
            setContentView(R.layout.activity_shake);
            ButterKnife.bind(this);
            //init managers
            speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
            headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
            wingMotionManager = (WingMotionManager) getUnitManager(FuncConstant.WINGMOTION_MANAGER);
            hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
            systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
            wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
           // mediaPlayer1 = MediaPlayer.create(this, R.raw.handshake);
           // mediaPlayer2 = MediaPlayer.create(this, R.raw.sad);
           // mediaPlayer6 = MediaPlayer.create(this,R.raw.nicetomeetyou);

            listner=false;
            slide=false;
            simpleSlidingDrawer=(SlidingDrawer) findViewById(R.id.slidingdrawer);
            sadimg.setVisibility(View.INVISIBLE);
            helloimg.setVisibility(View.VISIBLE);
            handimg.setVisibility(View.INVISIBLE);

            available6=true;
            //Thread objBgThread6=new Thread(objrunnable6);
            //objBgThread6.start();

            GifImageView gifImageView = findViewById(R.id.gifImageView);
            gifImageView.setImageResource(R.drawable.welcome);


            //initialize listeners
            initListener();

            //initialize speak
            MySettings.initializeSpeak();

            //update handshakesTextView

            //initialize body after 1 sec
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //hands down
                    AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(AbsoluteAngleWingMotion.PART_BOTH, 8, 180);
                    wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
                    //head up
                    headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);
                    //calls first meeting presentation
                    firstMeeting();

                }
            }, 1000);

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timeWaitingExpired();
                }
            });

            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.exitButton)
                public void onClick(View view) {
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

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * initialize listeners
     */
    private void initListener() {
        //hardware touch
        hardWareManager.setOnHareWareListener(
                new TouchSensorListener() {
                    @Override
                    public void onTouch(int i, boolean b) {

                    }

                    @Override
                    public void onTouch(int part) {
                        switch (part) {
                            case 9:
                                Log.i("hwmanager", "touching hand left");
                                if (waitingTouchPosition) {
                                    Log.i(TAG, "shake hand called");
                                    shakeHand();
                                }
                                break;
                            case 10:
                                Log.i("hwmanager", "touching hand right" );
                                //if is waiting in the position
                                if (waitingTouchPosition) {
                                    Log.i(TAG, "shake hand called");
                                    shakeHand();
                                }
                                break;
                        }
                    }
                }
        );
    }

    @Override
    protected void onMainServiceConnected() {

    }


    /****** my functions *******/
    public void firstMeeting() {

        //up the head
        headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);

        //self presentation
        /*if (!mediaPlayer1.isPlaying()) {
            mediaPlayer1.start();
        }
         */
        speechManager.startSpeak("Hi, I am emy ", MySettings.getSpeakDefaultOption());
        concludeSpeak(speechManager);
        //hand up
        AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(handAb, 5, 70);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        //rotate body
        rotateAtRelativeAngle(wheelMotionManager, 350);
        //rotate head
        headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotionRight);

        //waiting touch
        waitingTouchPosition = true;
        Log.i(TAG, "waitingTouchPosition = true");

        //waiting touch too much start waiting
        waitingTouchHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeWaitingExpired();
            }
        }, 1000 * MySettings.getSeconds_waitingTouch());

        //incitement in middle
        incitement.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500 * MySettings.getSeconds_waitingTouch());
    }

    public void shakeHand() {
        //SHAKING HAND MOMENT
        //cancel "waiting touch too much" response
        waitingTouchHandler.removeCallbacksAndMessages(null);
        incitement.removeCallbacksAndMessages(null);
        Log.i(TAG, "handler waitingTouchHandler deleted!");

        //waiting touch false, no more waiting
        waitingTouchPosition = false;
        Log.i(TAG, "waitingTouchPosition = false ");

        speechManager.startSpeak("Hi, Nice to meet you, Lets see about nebula institute ", MySettings.getSpeakDefaultOption());
        concludeSpeak(speechManager);

        handimg.setVisibility(View.VISIBLE);
        helloimg.setVisibility(View.INVISIBLE);

        //happy face
        temporaryEmotion(systemManager, EmotionsType.SMILE, 5);

        //flicker leds
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_PURPLE));
        //led off
        //hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE, (byte) 1, (byte) 1));

        //Shake hands
        /*
        //absolute shaking
        sleepy(1);
        absoluteAngleWingMotion = new AbsoluteAngleWingMotion( handAb, 8, 50);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        sleepy(1);
        absoluteAngleWingMotion = new AbsoluteAngleWingMotion( handAb, 8, 70);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        sleepy(1);
        absoluteAngleWingMotion = new AbsoluteAngleWingMotion( handAb, 8, 50);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        sleepy(1);
        absoluteAngleWingMotion = new AbsoluteAngleWingMotion( handAb, 8, 70);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        */

        /*
        //relative shaking
        RelativeAngleWingMotion relativeAngleWingMotionUP = new RelativeAngleWingMotion(handRe, 10, RelativeAngleWingMotion.ACTION_UP, 10);
        RelativeAngleWingMotion relativeAngleWingMotionDOWN = new RelativeAngleWingMotion(handRe, 10, RelativeAngleWingMotion.ACTION_DOWN, 10);
        wingMotionManager.doRelativeAngleMotion(relativeAngleWingMotionDOWN);
        sleepy(1);
        wingMotionManager.doRelativeAngleMotion(relativeAngleWingMotionUP);
        sleepy(1);
        wingMotionManager.doRelativeAngleMotion(relativeAngleWingMotionDOWN);
        sleepy(1);
        wingMotionManager.doRelativeAngleMotion(relativeAngleWingMotionUP);
        sleepy(1);
        */


        //motion without angle
        wingMotionManager.doNoAngleMotion(noAngleWingMotionUP);
        sleepy(0.5);
        wingMotionManager.doNoAngleMotion(noAngleWingMotionDOWN);
        sleepy(0.5);
        wingMotionManager.doNoAngleMotion(noAngleWingMotionUP);
        sleepy(0.5);
        wingMotionManager.doNoAngleMotion(noAngleWingMotionDOWN);
        sleepy(0.5);
        wingMotionManager.doNoAngleMotion(noAngleWingMotionUP);
        sleepy(0.5);
        wingMotionManager.doNoAngleMotion(noAngleWingMotionDOWN);



        //hands down (reset position)
        wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));

        //back rotation
        rotateAtRelativeAngle(wheelMotionManager, 10);
        //rotate back head
        headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotionLeft);


        finish();
        if (listner == false) {

            //starts dialog activity
            Intent myIntent = new Intent(HandShake.this, videoActivity.class);
            HandShake.this.startActivity(myIntent);
            //finish
            finish();
        }
    }

    public void timeWaitingExpired() {

        speechManager.startSpeak("No handshake , ok lets move on, Lets see about nebula institute", MySettings.getSpeakDefaultOption());
        concludeSpeak(speechManager);
        Log.i(TAG, "no touched hand in time");
        //waiting touch false
        waitingTouchPosition = false;
        Log.i(TAG, "waitingTouchPosition = false ");
        //remove incitement if it's still there
        incitement.removeCallbacksAndMessages(null);
        //hand down
        AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(handAb, 5, 180);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        //back rotation
        rotateAtRelativeAngle(wheelMotionManager, 10);

        sadimg.setVisibility(View.VISIBLE);
        helloimg.setVisibility(View.INVISIBLE);

        //back head
        headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotionLeft);
        //cry face
        temporaryEmotion(systemManager, EmotionsType.CRY);
        //down the head
        headMotionManager.doRelativeAngleMotion(relativeHeadMotionDOWN);
        //up head after 10 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);
            }
        }, 4000);

        //sad sentence


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listner == false) {
                    finish();
                    Intent myIntent = new Intent(HandShake.this, videoActivity.class);
                    HandShake.this.startActivity(myIntent);
                    //finish

                    finish();
                }
            }
        }, 5000);



    }


    Runnable objrunnable6=new Runnable() {
        @Override
        public void run() {

            hardWareManager.setOnHareWareListener(new ObstacleListener() {
                @Override
                public void onObstacleStatus(boolean b) {
                    if (!b && available6==true) {
                        listner=true;
                        Toast.makeText(HandShake.this, "No One here", Toast.LENGTH_SHORT).show();
                        available6=false;
                        Intent intent = new Intent(HandShake.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    } else {
                        systemManager.showEmotion(EmotionsType.KISS);
                        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));

                    }
                }
            });
        }


    };


}