package com.example.nebula;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.nebula.ImprovedSentimentAnalysis.analyzeSentiment;
import static com.example.nebula.MyUtils.rotateAtRelativeAngle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.nebula.MyUtils.concludeSpeak;
import static com.example.nebula.MyUtils.temporaryEmotion;


import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.beans.OperationResult;
import com.sanbot.opensdk.function.beans.EmotionsType;
import com.sanbot.opensdk.function.beans.FaceRecognizeBean;
import com.sanbot.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.speech.Grammar;
import com.sanbot.opensdk.function.beans.speech.RecognizeTextBean;
import com.sanbot.opensdk.function.beans.wing.AbsoluteAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.NoAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.RelativeAngleWingMotion;
import com.sanbot.opensdk.function.unit.HDCameraManager;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.ModularMotionManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.WingMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.hardware.TouchSensorListener;
import com.sanbot.opensdk.function.unit.interfaces.media.FaceRecognizeListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sanbot.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.WakenListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class  MainActivity extends TopBaseActivity {
    private final static String TAG = "DIL-SPLASH";
    public static boolean busy = false;
    public static boolean hdcamera = false;
    public static boolean face = false;
    public static boolean wakeup  = false;
    public static boolean first  = false;
    public static boolean recognize = false;
    private byte handAb = AbsoluteAngleWingMotion.PART_RIGHT;
    private byte handRe = RelativeAngleWingMotion.PART_LEFT;
    private boolean slide=false;

    @BindView(R.id.exitButton)
    Button exitButton;

    @BindView(R.id.backbutton)
    Button startbtn;

    @BindView(R.id.handle)
    Button slider;

    @BindView(R.id.welcomeimg)
    ImageView welcomeimg;
    @BindView(R.id.litening)
    ImageView listening;
    private Handler delayHandler = new Handler();
    // creating variables for our
    // widgets in xml file.
    private ImageButton sendMsgIB;

    private TextView textView;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    private String textToType;

    private GifImageView gifImageView;

    private GifImageView gifImageView1;
    private String sentiment;


    private int index = 0;
    // creating a variable for
    // our volley request queue.
    private RequestQueue mRequestQueue;
    // creating a variable for array list and adapter class.
    private ArrayList<MessageModel> messageModalArrayList;
   // private MessageRVAdapter messageRVAdapter;
    private ModularMotionManager modularMotionManager; //wander
    private HDCameraManager hdCameraManager;
    private HardWareManager hardWareManager;
    private WheelMotionManager wheelMotionManager;
    private SpeechManager speechManager; //voice, speechRec
    private SystemManager systemManager; //emotions
    private HeadMotionManager headMotionManager;    //head movements
    private WingMotionManager wingMotionManager;    //hands movements


    //head motion
    LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(
            LocateAbsoluteAngleHeadMotion.ACTION_VERTICAL_LOCK,45,40
    );
    RelativeAngleHeadMotion relativeHeadMotionDOWN = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_DOWN, 45);

    RelativeAngleHeadMotion relativeHeadMotionUP = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_UP, 35);
    NoAngleWingMotion noAngleWingMotionUP = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 4, NoAngleWingMotion.ACTION_UP);
    NoAngleWingMotion noAngleWingMotionDOWN = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 4, NoAngleWingMotion.ACTION_DOWN);
    NoAngleWingMotion noAngleWingMotionSTOP = new NoAngleWingMotion(NoAngleWingMotion.PART_RIGHT, 4, NoAngleWingMotion.ACTION_STOP);
    RelativeAngleHeadMotion relativeAngleHeadMotionLeft = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFT,28);
    RelativeAngleHeadMotion relativeAngleHeadMotionRight = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_RIGHT,28);
    SlidingDrawer simpleSlidingDrawer;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            register(MainActivity.class);
            //screen always on
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            hdCameraManager = (HDCameraManager) getUnitManager(FuncConstant.HDCAMERA_MANAGER);
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

            slide = false;

            welcomeimg.setVisibility(View.VISIBLE);
            listening.setVisibility(View.INVISIBLE);

            gifImageView1=findViewById(R.id.gifImageView1);
            gifImageView1.setImageResource(R.drawable.welcome);


            gifImageView = findViewById(R.id.gifImageView);
            gifImageView.setImageResource(R.drawable.audio);
            simpleSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);

            gifImageView.setVisibility(View.INVISIBLE);

            first  = false;
            wakeup =false;

            hdcamera=false;
            face=false;
            recognize=false;
            //LOAD handshakes stats
            MySettings.initializeXML();
            MySettings.loadHandshakes();

            //initialize speak
            MySettings.initializeSpeak();


            // on below line we are initializing all our views.

            textView= findViewById(R.id.idtextview);

            // below line is to initialize our request queue.
            mRequestQueue = Volley.newRequestQueue(MainActivity.this);
            mRequestQueue.getCache().clear();

            // creating a new array list
            messageModalArrayList = new ArrayList<>();

            speechTest();
            hdcameralistner();
            initListener();           //hdcameralistner();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechManager.startSpeak("Hi, You can ask anything or questions related to nebula institute from me,  by touching my head ", MySettings.getSpeakDefaultOption());
                    concludeSpeak(speechManager);

                }
            }, 2000);






            startbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.backbutton)
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                @OnClick(R.id.exitButton)
                public void onClick(View view) {
                    finish();
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


    private void sendMessage(String userMsg) throws UnsupportedEncodingException {
        Toast.makeText(MainActivity.this, userMsg, Toast.LENGTH_SHORT).show();


        messageModalArrayList.add(new MessageModel(userMsg, USER_KEY));
        //messageRVAdapter.notifyDataSetChanged();

        String encodedQuestion = URLEncoder.encode(userMsg, "UTF-8");
        String url = "http://api.brainshop.ai/get?bid=178952&key=SKSZgRyt8A5xfs3E&uid=[uid]&msg=" + encodedQuestion;

        // creating a variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // on below line we are making a json object request for a get request and passing our url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // in on response method we are extracting data
                    // from json response and adding this response to our array list.
                    String botResponse = response.getString("cnt");
                    messageModalArrayList.add(new MessageModel(botResponse, BOT_KEY));
                    sentiment = analyzeSentiment(botResponse);

                    if(sentiment=="Positive"){
                        positivemovements();
                    }else if(sentiment=="Negative"){
                        negativemovements();
                    }else if(sentiment=="nutral"){
                        nuturalmovements();

                    }else if (sentiment=="Productions"){
                        finish();
                        Intent intent = new Intent(MainActivity.this, ChoiceActivity.class);
                        startActivity(intent);
                    }else if (sentiment=="introduction"){
                        finish();
                        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                        startActivity(intent);
                    }


                    index=0;
                    textToType=botResponse.toString();
                    typeText();

                    gifImageView.setVisibility(View.VISIBLE);
                    speechManager.startSpeak(botResponse.toString(), MySettings.getSpeakDefaultOption());
                    concludeSpeak(speechManager);

                    wakeup=false;

                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gifImageView.setVisibility(View.INVISIBLE);
                            textView.setText("");
                            if(wakeup==false) {
                                speechManager.doWakeUp();
                            }
                            speechlistner();
                            recognize=true;
                        }
                    }, 7000);


                    // notifying our adapter as data changed.
                   // messageRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    // handling error response from bot.
                    messageModalArrayList.add(new MessageModel("No response", BOT_KEY));
                   // messageRVAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling.
                messageModalArrayList.add(new MessageModel("Sorry no response found", BOT_KEY));
                Toast.makeText(MainActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }



    public void speechlistner(){
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                recognize=true;
                String grammer1=grammar.getText();
                String grammer2=grammar.getText().toString().trim();
                Toast.makeText(MainActivity.this,"text" + grammer1, Toast.LENGTH_SHORT).show();
                try {
                    sendMessage(grammer2);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }

            @Override
            public void onRecognizeText(RecognizeTextBean recognizeTextBean) {
                Toast.makeText(MainActivity.this, "rec text" + recognizeTextBean.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecognizeVolume(int i) {

            }

            @Override
            public void onStartRecognize() {
                Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopRecognize() {
                Toast.makeText(MainActivity.this, "stop rec", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, int i1) {

            }
        });
    }



        public void hdcameralistner () {
            if (hdcamera == false) {
                hdCameraManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(List<FaceRecognizeBean> list) {
                if(face==false) {
                    face = true;
                    hdcamera = true;
                    welcomeimg.setVisibility(View.INVISIBLE);
                    speechManager.doWakeUp();
                    speechlistner();

                }
            }
        });
    }

    }


    public void speechTest(){
        speechManager.setOnSpeechListener(new WakenListener() {
            @Override
            public void onWakeUp() {
                wakeup=true;
                listening.setVisibility(View.VISIBLE);
                //Toast.makeText(MainActivity.this, "listner wakeup", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSleep() {
                listening.setVisibility(View.INVISIBLE);
                //Toast.makeText(MainActivity.this, "listner sleep", Toast.LENGTH_SHORT).show();
                wakeup=false;
                first = true;
                if(recognize==false){
                    //speechManager.doWakeUp();
                   // speechlistner();
                }else{
                   // hdcamera=false;
                    //face=false;
                    hdcameralistner();
                }
            }
            @Override
            public void onWakeUpStatus(boolean b) {

            }
        });
    }



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
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                                if(wakeup==true && first == true){
                                        speechManager.doSleep();
                                }
                                welcomeimg.setVisibility(View.INVISIBLE);
                                speechlistner();
                                break;
                        }
                    }
                }
        );
    }



    public void positivemovements(){
        systemManager.showEmotion(EmotionsType.SMILE);
        headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotionRight);
        rotateAtRelativeAngle(wheelMotionManager, 10);
        AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(handAb, 4, 70);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);

        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotionLeft);
                rotateAtRelativeAngle(wheelMotionManager, -10);
                AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(handRe, 4, 70);
                wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
            }
        }, 3000);

        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));

            }
        }, 6000);

    }


    public void negativemovements(){
        //hand down
        AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(handAb, 5, 180);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        //back rotation
        rotateAtRelativeAngle(wheelMotionManager, 15);
        //cry face
        temporaryEmotion(systemManager, EmotionsType.CRY);
        //down the head
        headMotionManager.doRelativeAngleMotion(relativeHeadMotionDOWN);
        //up head after 10 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rotateAtRelativeAngle(wheelMotionManager, -15);
                headMotionManager.doRelativeAngleMotion(relativeHeadMotionUP);
            }
        }, 5000);

    }



    public void nuturalmovements(){
        systemManager.showEmotion(EmotionsType.SPEAK);
        headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotionRight);
        rotateAtRelativeAngle(wheelMotionManager, 10);
        AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(handAb, 4, 70);
        wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);

        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotionLeft);
                rotateAtRelativeAngle(wheelMotionManager, -10);
                AbsoluteAngleWingMotion absoluteAngleWingMotion = new AbsoluteAngleWingMotion(handRe, 4, 70);
                wingMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
            }
        }, 3000);

        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wingMotionManager.doNoAngleMotion(new NoAngleWingMotion(NoAngleWingMotion.PART_BOTH, 5,NoAngleWingMotion.ACTION_RESET));
            }
        }, 6000);

    }





    private void typeText() {
        // Check if there are still characters to type
        if (index < textToType.length()) {

            // Append the next character to the TextView
            textView.setText(textToType.substring(0, index + 1));

            // Move to the next character after a delay (adjust as needed)
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    index++;
                    typeText();
                }
            }, 40); // Adjust the delay for typing speed
        }
    }

public void listner(){
      OperationResult operationResult = speechManager.isSpeaking();
      if(operationResult.getResult().equals("1")){
          gifImageView.setVisibility(View.VISIBLE);

      }else {
          gifImageView.setVisibility(View.INVISIBLE);

      }
}



}






