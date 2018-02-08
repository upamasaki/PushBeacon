package univ.apu.tani.pushbeacon;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;




import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


//import static org.assertj.core.api.Assertions.assertThat;
import univ.apu.tani.pushbeacon.R.id;

import static android.R.attr.value;
import static android.os.SystemClock.sleep;

@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
//public class MainActivity extends AppCompatActivity {

    /*--------------------------- Init  Setting  ---------------------------*/
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private BluetoothAdapter mBtAdapter;
    private Button mBtBtn;

    String bluetooth_on_label = "Bluetooth ON";
    String bluetooth_off_label = "Bluetooth OFF";
    String bluetooth_none_label = "Bluetooth None";

    //ロケーションアクセスダイアログの許可
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    //必要な変数の定義
    // text_view1： activity_main.xml の TextView の id
    TextView DistanceTextView ;
    TextView RssiTextView ;
    TextView MinorTextView ;
    TextView MajorTextView ;

    //EditText editText ;

    /*------------------ Sound Info -----------------------------------------*/
    // SoundPool(効果音再生)
    public SoundPool mSoundPool,soundPool,soundPool2;
    public int[] mSoundId = new int[2]; // 使う効果音の数だけ配列作成
    public int sound1,sound2;
    // 音量関係
    AudioManager audioManager ;
    int currentVolume;
    int maxVolume ;
    float volumeRate ;
    private AudioAttributes audioAttributes;


    /*-------- ビーコン情報  --------*/
    public static final int ON = 1;
    public static final int OFF = 0;
    private static final int bid1 = 1;
    public static int beacon_flag=OFF;
    int Beacon_Mode = 1;

    /*-------- 色判定  --------*/
    int YellowLight = -100;
    int RedLight = -78;

    String uuid ;
    String major;
    String minor;


    int int_uuid;
    int int_major ;
    int int_minor ;
    int int_rssi;
    int Pre_Rssi=0;    int Pre2_Rssi=0;
    int Now_Rssi=0;
    int Rssi_double=0;
    int rssi_Box=0;
    int Rssi_diff = 0;
    int rssi_Average=0;
    int Rssi_Correct=0;
    int Pre_Rssi_Correct=0;
    int window_range = 5;
    int Rssi_Array[] = new int[window_range];
    boolean plot[] = new boolean[4];
    double beacon_distance = 0;
    int txpower;
    int START_UP_FLAG = ON;
    boolean intFlag = true;
    int intminor = 0;
    int count = 1;

    // データ保存用の変数
    private SharedPreferences dataStore;
    private EditText YellowEditText;
    private EditText RedEditText;

    private final String TAG = "MainActivity";
    private BluetoothManager _bluetoothManager;
    private BluetoothAdapter _bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback _leScanCallback;
    private BluetoothLeScanner _bluetoothLeScanner;
    private ScanCallback _scanCallback;

    /* 　++++++++++ 処理時間の計測方法  　++++++++++ */
    private long startTime;
    private long stopTime;
    TextView timeTextView;
    private long interval_time_average=0;
    private long time_Box=0;


    /*----------------------------------------------------------------------*/
    // MyAsyncTask task;
    EditText editText ;
    String text="";

    ToggleButton toggleButton,toggleButton2,toggleButton3; // トグルボタンの変数
    boolean checked,checked2,checked3;            //トグルボタンの真偽の変数


    //グラフを表示するプログラム
    LineChart mChart,mChart2;
    //LineData data;
    String[] names = new String[]{"x-value", "y-value", "z-value"};
    int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE};

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        CompoundButton toggle = (CompoundButton) findViewById(id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("MainActivity", "ON");
                    Log.i(TAG, "MainActivity ON");
                    checked = isChecked;
                } else {
                    Log.d("MainActivity", "OFF");
                    Log.i(TAG, "MainActivity OFF");
                    checked = isChecked;
                }
            }
        });
        */

        // Bluetooth ボタンの初期化 起動したときの状態を表示------------------------
        mBtBtn = (Button) findViewById(id.buttonPower);
        // BluetoothAdapterのインスタンス取得
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            mBtBtn.setText(bluetooth_none_label);
        } else {
            // ボタンのラベルの設定
            if (mBtAdapter.isEnabled()) {
                //mBtBtn.setText(bluetooth_on_label);
                //mBtBtn.setTextColor(0xffff0000);
            } else {
                //mBtBtn.setText(bluetooth_off_label);
                //mBtBtn.setTextColor(0xffcccccc);
            }
        }
        //---------------------------------------------------------------------------

        bluetooth_init();


        // ----- トグルボタンの設定-------------------------------------------------------------
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        // ToggleButton が On のときのテキストを設定
        toggleButton.setTextOn("SETTING");
        // ToggleButton が Off のときのテキストを設定
        toggleButton.setTextOff("SETTING");
        // ToggleButton が Off かどうかを設定
        toggleButton.setChecked(true);
        // ToggleButton が On かどうかを取得
        checked = toggleButton.isChecked();
        toggleButton.setOnCheckedChangeListener(this);
        // ------------------------------------------------------------------------------------
        // ----- トグルボタンの設定-------------------------------------------------------------
        toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        // ToggleButton が On のときのテキストを設定
        toggleButton2.setTextOn("G");
        // ToggleButton が Off のときのテキストを設定
        toggleButton2.setTextOff("'G'");
        // ToggleButton が Off かどうかを設定
        toggleButton2.setChecked(true);
        // ToggleButton が On かどうかを取得
        checked2 = toggleButton2.isChecked();
        toggleButton2.setOnCheckedChangeListener(this);
        // ------------------------------------------------------------------------------------
        // ----- トグルボタンの設定-------------------------------------------------------------
        toggleButton3 = (ToggleButton) findViewById(R.id.toggleButton3);
        // ToggleButton が On のときのテキストを設定
        toggleButton3.setTextOn("PLOT LIST");
        // ToggleButton が Off のときのテキストを設定
        toggleButton3.setTextOff("PLOT LIST");
        // ToggleButton が Off かどうかを設定
        toggleButton3.setChecked(true);
        // ToggleButton が On かどうかを取得
        checked3 = toggleButton3.isChecked();
        toggleButton3.setOnCheckedChangeListener(this);
        // ------------------------------------------------------------------------------------
        // 最初からINFOは見えないようにする
        Info_Invisible();
        Graph_Invisible();
        findViewById(id.Plot_LIST).setVisibility(View.INVISIBLE);


        //データ保存する初期設定
        // "DataStore"という名前でインスタンスを生成
        dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);

        //　RSSI のパラメータを取得
        YellowEditText = (EditText) findViewById(id.YellowEditText);
        YellowLight = Integer.parseInt(YellowEditText.getText().toString());// Int
        RedEditText = (EditText) findViewById(id.RedEditText);
        RedLight = Integer.parseInt(RedEditText.getText().toString());// Int


        // 効果音を使えるように読み込み
        // SoundPoolはLollipop以降使い方が変わりました
        audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(2)
                .build();
        //mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundId[0] = mSoundPool.load(getApplicationContext(), R.raw.personal_alarm_sample1, 1);
        mSoundId[1] = mSoundPool.load(getApplicationContext(), R.raw.piano41, 1);
        // load が終わったか確認する場合


        // SoundPoolのインスタンス作成
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundPool2 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        // 効果音をロードしておく。
        // 引数はContext、リソースID、優先度
        sound1 = soundPool.load(this, R.raw.meka_mi_radar05, 1);
        sound2 = soundPool.load(this, R.raw.meka_mi_radar04, 1);

        // 最初は信号機を消す
        findViewById(id.traffic_img).setVisibility(View.INVISIBLE);


        // ************************************************************************
        // Graph Create
        // ************************************************************************
        mChart = (LineChart) findViewById(id.chart1);
        mChart2 = (LineChart) findViewById(id.chart2);

        mChart.setDescription(""); // 表のタイトルを空にする
        mChart.setData(new LineData()); // 空のLineData型インスタンスを追加

        mChart2.setDescription(""); // 表のタイトルを空にする
        mChart2.setData(new LineData()); // 空のLineData型インスタンスを追加


        //++++++++++++++++++++++++++++++++++++++++++++++++++
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.argb(255,252,250,252));

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        //  ラインの凡例の設定
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setLabelsToSkip(9);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(-30.0f);
        leftAxis.setAxisMinValue(-100.0f);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        //++++++++++++++++++++++++++++++++++++++++++++++++++
        // enable touch gestures
        mChart2.setTouchEnabled(true);

        // enable scaling and dragging
        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(true);
        mChart2.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart2.setPinchZoom(true);

        // set an alternative background color
        mChart2.setBackgroundColor(Color.argb(255,252,250,252));

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        mChart2.setData(data2);

        //  ラインの凡例の設定
        Legend l2 = mChart2.getLegend();
        l2.setForm(Legend.LegendForm.LINE);
        l2.setTextColor(Color.BLACK);

        XAxis xl2 = mChart2.getXAxis();
        xl2.setTextColor(Color.BLACK);
        xl2.setLabelsToSkip(9);

        YAxis leftAxis2 = mChart2.getAxisLeft();
        leftAxis2.setTextColor(Color.BLACK);
        leftAxis2.setAxisMaxValue(002.0f);
        leftAxis2.setAxisMinValue(000.0f);
        leftAxis2.setStartAtZero(false);
        leftAxis2.setDrawGridLines(true);

        YAxis rightAxis2 = mChart2.getAxisRight();
        rightAxis2.setEnabled(false);


    }

    public void bluetooth_init(){



        _bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        _bluetoothAdapter = _bluetoothManager.getAdapter();
        // APIレベルで5以上かどうかを判定して処理を分けます。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            _bluetoothLeScanner = _bluetoothAdapter.getBluetoothLeScanner();
            Log.i(TAG, " initScanCallback();");
            initScanCallback();
            Log.i(TAG, "initLeScanCallback() -- END --;");
        } else {
            Log.i(TAG, "initLeScanCallback();");
            initLeScanCallback();
        }

        // text_view1： activity_main.xml の TextView の id
        RssiTextView = (TextView) findViewById(id.RSSIText);     //  RSSI
        MinorTextView = (TextView) findViewById(id.MinorText);    //  minor
        MajorTextView = (TextView) findViewById(id.MajorText);    //  major
        DistanceTextView = (TextView) findViewById(id.DistanceText);    //  XX

        //ロケーションアクセスをダイアログで許可する------------------------------------------
        // Android M Permission check
        Log.i(TAG, " Android M Permission check");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
        Log.i(TAG, " Android M Permission check end");
    }

    /*--------------------------- Bluetooth のボタンクリック処理  ---------------------------*/
    public void onClickBlue(View view) {
        //BluetoothAdapter取得
        BluetoothAdapter Bt = BluetoothAdapter.getDefaultAdapter();
        if(!Bt.equals(null)){
            /*--------------------------- Bluetooth対応端末の場合の処理-----------------------*/
            //Log("Bluetoothがサポートされてます。");
            //Toast toast_ok = Toast.makeText(MainActivity.this, "Bluetoothがサポートされてます。", Toast.LENGTH_LONG);
            //toast_ok.show();

            /*--------------------------- Bluetoothの設定がONであることを確認する-------------*/
            boolean btEnable = Bt.isEnabled();
            if(btEnable == true){
                //BluetoothがONだった場合の処理

                // - - - ON の場合は OFF にしない　- - -
                //ボタンを押しただけで「OFF」
                //mBtAdapter.disable();
                //mBtBtn.setText(bluetooth_off_label);
                //mBtBtn.setTextColor(0xffcccccc);

                Toast toast_on = Toast.makeText(MainActivity.this, "RUNNING ...", Toast.LENGTH_LONG);
                toast_on.show();

            }else{
                //BluetoothがOFFだった場合の処理
                Toast toast_off = Toast.makeText(MainActivity.this, "!! START !!", Toast.LENGTH_LONG);
                toast_off.show();

                //BluetoothAdapter#enableメソッドで, BluetoothをONに設定する.
                //ボタンを押しただけで「ON」
                mBtAdapter.enable();
                checked = true;
                //mBtBtn.setText(bluetooth_on_label);
                //mBtBtn.setTextColor(0xffff0000);


                //bluetooth_init();

                onResume();


                //OFFだった場合、ONにすることを促すダイアログを表示する画面に遷移
                //Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(btOn, REQUEST_ENABLE_BLUETOOTH);
            }
            // Startしたら Power マークと文字を消す 信号は青を表示
            findViewById(id.PowerIcon).setVisibility(View.INVISIBLE);
            findViewById(id.Start).setVisibility(View.INVISIBLE);
            findViewById(id.traffic_img).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(id.traffic_img)).setImageResource(R.drawable.blue_3);
        }else{
            /*--------------------------- Bluetooth非対応端末の場合の処理  -----------------------*/
            //Log("Bluetoothがサポートれていません。");
            Toast toast_null = Toast.makeText(MainActivity.this, "Bluetoothがサポートれていません。", Toast.LENGTH_LONG);
            toast_null.show();
            finish();
        }


    }




    @Override
    protected void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
        sleep(1000);
        if (!_bluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "BluetoothをONにして下さい", Toast.LENGTH_SHORT).show();
            //Log.i(TAG, "onResume()");
            return;
        }
        startBLEScan();




    }


    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();
        stopBLEScan();

        // SoundPool 解放
        mSoundPool.unload(mSoundId[0]);
        mSoundPool.unload(mSoundId[1]);
        mSoundPool.release();
    }

    private void initScanCallback() {
        Log.i(TAG, " initScanCallback() in funcution ;");
        _scanCallback = new ScanCallback() {
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                Log.i(TAG, " initScanCallback() - onBatchScanResults in funcution ;");
                super.onBatchScanResults(results);
                for (ScanResult result : results) {
                    scanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                }
            }

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.i(TAG, " initScanCallback() - onScanResult in funcution ;");
                super.onScanResult(callbackType, result);
                scanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.i(TAG, " initScanCallback() - onScanFailed in funcution ;");
                super.onScanFailed(errorCode);
            }
        };
        Log.i(TAG, " initScanCallback() in funcution end;");
    }

    private void initLeScanCallback() {
        Log.i(TAG, " initLeScanCallback() in funcution ;");
        _leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                scanResult(device, rssi, scanRecord);
            }
        };
    }

    @SuppressWarnings("deprecation")
    private void startBLEScan() {
        Log.i(TAG, "startBLEScan() ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "_bluetoothLeScanner.startScan(_scanCallback);");
            _bluetoothLeScanner.startScan(_scanCallback);
        } else {
            Log.i(TAG, "_bluetoothAdapter.startLeScan(_leScanCallback);");
            _bluetoothAdapter.startLeScan(_leScanCallback);
        }
    }

    @SuppressWarnings("deprecation")
    private void stopBLEScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            _bluetoothLeScanner.stopScan(_scanCallback);
        } else {
            _bluetoothAdapter.stopLeScan(_leScanCallback);
        }
    }

    private void scanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i(TAG, "> scanResult() in funcution ;");
        Log.i(TAG, " ++ scanRecord.length = " + scanRecord .length);
        if (scanRecord.length > 30) {

            //if(beacon_flag == OFF){


            uuid = getUUID(scanRecord);
            major = getMajor(scanRecord);
            minor = getMinor(scanRecord);
            int_rssi = rssi;

            //デバッグ用 Toast 表示
            //Toast toast_re = Toast.makeText(MainActivity.this, uuid, Toast.LENGTH_LONG);
            //toast_re.show();

            if(intFlag){
                intminor = Integer.parseInt(minor);
                intFlag = false;
            }

            Log.i(TAG, " Major  " + Integer.parseInt(major) + "," + Integer.parseInt(minor));


            if ((Integer.parseInt(major) == 700 || Integer.parseInt(minor) == 494) || (Integer.parseInt(major) == 6 && Integer.parseInt(minor) == 512) || (Integer.parseInt(major) == 512 && Integer.parseInt(minor) == 6) ) {
            //if(true){
                // 距離の計算
                beacon_distance = Math.pow(10,((-0-rssi)/20));

                //----桁数を調整-----
                String after_beacon_distance = String.format("%.2f", beacon_distance/100);

                // RSSI,MAJOR,MINOR を表示
                RssiTextView.setText("RSSI  =" + rssi);
                MajorTextView.setText("major = " + major);
                MinorTextView.setText("minor = " + minor);
                //textView4.setText(after_beacon_distance);
                DistanceTextView.setText("Distance = " + String.valueOf(beacon_distance));
                START_UP_FLAG = OFF;

                Log.i(TAG, " Distance [m] " + String.valueOf(beacon_distance));
                Log.i(TAG, " Init Minor " + String.valueOf(intminor));
                Log.i(TAG, " ++ uuid = " + uuid + ", major = " + major + ", minor = " + minor);




                // minor の値によって 検知画面を表示するかを判断する．
                //if (Integer.parseInt(minor) < 10000) {


                // トーストで設定されたことをを表示
                //Toast.makeText(getApplicationContext(), "!!＋＋歩行者検知＋＋!!", Toast.LENGTH_SHORT).show();


                // Average calc
                rssi_Box = rssi_Box +  rssi;
                rssi_Average = rssi_Box/count;
                if(Pre2_Rssi != 0){
                    Rssi_double = (Pre_Rssi + Pre2_Rssi + rssi)/3;
                    Rssi_diff = Pre_Rssi-rssi;
                }else{
                    Rssi_double = rssi;

                }
                Pre2_Rssi = Pre_Rssi;
                Pre_Rssi = rssi;

                // ***********************************************************
                // フィルターをかける
                // 平均と微分値を用いた計算
                /*
                if(Rssi_diff<5){
                    Rssi_Correct = rssi;

                }else if(Pre_Rssi_Correct != 0){
                    Rssi_Correct = (Pre_Rssi_Correct + rssi_Average)/2;
                }else{
                    Rssi_Correct = (rssi + rssi_Average)/2;
                }
                Pre_Rssi_Correct = Rssi_Correct;
                */


                Rssi_Array[count%window_range] = rssi;

                if (count>window_range) {
                    int sum = 0;
                    for (int i = 0; i < Rssi_Array.length; i++) {
                        sum += Rssi_Array[i];
                    }
                    Rssi_Correct = sum/window_range;
                }else{
                    Rssi_Correct = rssi;
                }


                // time section
                stopTime = System.currentTimeMillis();
                long time = stopTime - startTime;
                int second = (int) (time/1000);
                int comma = (int) (time % 1000);
                timeTextView =  (TextView)findViewById(R.id.timeTextView);
                timeTextView.setText((second + "'" + comma).toString());
                if((float)time/1000 < 2){
                    time_Box =  time_Box + time;
                    interval_time_average = time_Box/count;
                }
                count++;
                Log.i(TAG, " interbal " + (float)interval_time_average/1000);
                startTime = System.currentTimeMillis();




                //データ追加描画処理 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                // 電波強度
                LineData data = mChart.getData();
                if (data == null) {
                    return;

                }

                LineDataSet set = data.getDataSetByIndex(0);
                LineDataSet set_yellow = data.getDataSetByIndex(1);
                LineDataSet set_red = data.getDataSetByIndex(2);
                LineDataSet set_ave = data.getDataSetByIndex(3);
                LineDataSet set_double = data.getDataSetByIndex(4);
                LineDataSet rssi_diff = data.getDataSetByIndex(5);
                LineDataSet rssi_correct = data.getDataSetByIndex(6);


                if (set == null) {
                    set = new LineDataSet(null, "Beacon"); //  解説
                    set_yellow = new LineDataSet(null, "Y"); //  解説
                    set_red = new LineDataSet(null, "R"); //  解説
                    set_ave = new LineDataSet(null, "Ave"); //  解説
                    set_double = new LineDataSet(null, "Ave2"); //  解説
                    rssi_diff = new LineDataSet(null, "Diff"); //  解説
                    rssi_correct = new LineDataSet(null, "Correct"); //  解説

                    set.setColor(Color.GRAY);
                    set_yellow.setColor(Color.argb(255,255,204,0));
                    set_red.setColor(Color.RED);
                    set_ave.setColor(Color.BLACK);
                    set_double.setColor(Color.MAGENTA);
                    rssi_diff.setColor(Color.GREEN);
                    rssi_correct.setColor(Color.BLUE);

                    set_yellow.setCircleColor(Color.argb(255,255,204,0) );
                    set_red.setCircleColor(Color.argb(255,255,51,0) );
                    set.setCircleColor(Color.GRAY);
                    set_ave.setCircleColor(Color.BLACK);
                    set_double.setCircleColor(Color.MAGENTA);
                    rssi_diff.setCircleColor(Color.GREEN);
                    rssi_correct.setCircleColor(Color.BLUE);

                    set.setDrawValues(false);
                    set_red.setDrawValues(false);
                    set_yellow.setDrawValues(false);
                    set_ave.setDrawValues(false);
                    set_double.setDrawValues(false);
                    rssi_diff.setDrawValues(false);
                    rssi_correct.setDrawValues(false);

                    data.addDataSet(set);
                    data.addDataSet(set_yellow);
                    data.addDataSet(set_red);
                    data.addDataSet(set_ave);
                    data.addDataSet(set_double);
                    data.addDataSet(rssi_diff);
                    data.addDataSet(rssi_correct);
                }

                //  追加描画するデータを追加
                //SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                //data.addXValue(format.format(da));
                data.addXValue(getNowDate());
                data.addEntry(new Entry((float) rssi, set.getEntryCount()), 0);
                data.addEntry(new Entry((float) YellowLight, set_yellow.getEntryCount()), 1);
                data.addEntry(new Entry((float) RedLight, set_red.getEntryCount()), 2);
                data.addEntry(new Entry((float) rssi_Average, set_red.getEntryCount()), 3);
                data.addEntry(new Entry((float) Rssi_double, set_double.getEntryCount()), 4);
                data.addEntry(new Entry((float) Rssi_diff-80, set_double.getEntryCount()), 5);
                data.addEntry(new Entry((float) Rssi_Correct, set_double.getEntryCount()), 6);
                //  データを追加したら必ずよばないといけない
                mChart.notifyDataSetChanged();

                mChart.setVisibleXRangeMaximum(1000); //  解説

                mChart.moveViewToX(data.getXValCount() - 1001);   //  移動する

                mChart.notifyDataSetChanged();
                mChart.invalidate();



                //データ追加描画処理 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                // 処理時間
                LineData data2 = mChart2.getData();
                if (data2 == null) {
                    return;

                }

                LineDataSet set_time = data2.getDataSetByIndex(0);
                LineDataSet set_time_ave = data2.getDataSetByIndex(1);

                if (set_time == null) {
                    set_time = new LineDataSet(null, "Reception cycle"); //  解説
                    set_time_ave = new LineDataSet(null, "Average"); //  解説


                    set_time.setColor(Color.GRAY);
                    set_time_ave.setColor(Color.BLACK);


                    set_time.setCircleColor(Color.GRAY);
                    set_time_ave.setCircleColor(Color.BLACK);
                    set_time.setDrawValues(false);
                    set_time_ave.setDrawValues(false);

                    data2.addDataSet(set_time);
                    data2.addDataSet(set_time_ave);


                }

                //  追加描画するデータを追加
                //SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                //data.addXValue(format.format(da));
                data2.addXValue(getNowDate());
                data2.addEntry(new Entry((float) time/1000, set_time.getEntryCount()), 0);
                data2.addEntry(new Entry((float) interval_time_average/1000, set_time_ave.getEntryCount()), 1);

                //  データを追加したら必ずよばないといけない

                mChart2.notifyDataSetChanged();

                mChart2.setVisibleXRangeMaximum(1000); //  解説

                mChart2.moveViewToX(data2.getXValCount() - 1001);   //  移動する

                mChart2.notifyDataSetChanged();
                mChart2.invalidate();


                if(Beacon_Mode==0){  // MODEによって機能を変更
                    //アラーム画面へ遷移
                    if (checked) {
                        Intent intent_alarm_screen2 = new Intent(getApplication(), alerm_screen_2.class);
                        startActivity(intent_alarm_screen2);
                    }
                }else if(Beacon_Mode==1){// My Traffic Mode
                    Log.i(TAG, "Rssi: " + rssi + " Lght Y:" + YellowLight + " R:" + RedLight);
                    if(rssi > RedLight){
                        ((ImageView) findViewById(id.traffic_img)).setImageResource(R.drawable.red_3);
                        Sound_Play1();
                        Log.i(TAG, "Rssi: " + rssi + "   Redlight" + RedLight);
                    }else if(rssi > YellowLight){
                        ((ImageView) findViewById(id.traffic_img)).setImageResource(R.drawable.yellow_3);
                        Sound_Play2();
                        Log.i(TAG, "Rssi: " + rssi +  "   Yellow" + YellowLight);
                    }else{
                        ((ImageView) findViewById(id.traffic_img)).setImageResource(R.drawable.blue_3);
                    }




                }
                //}
            }
            // ※ここはUIスレッドではないので、実際画面に表示する場合は注意して下さい。
        }
    }

    private String getUUID(byte[] scanRecord) {
        String uuid = IntToHex2(scanRecord[9] & 0xff)
                + IntToHex2(scanRecord[10] & 0xff)
                + IntToHex2(scanRecord[11] & 0xff)
                + IntToHex2(scanRecord[12] & 0xff)
                + "-"
                + IntToHex2(scanRecord[13] & 0xff)
                + IntToHex2(scanRecord[14] & 0xff)
                + "-"
                + IntToHex2(scanRecord[15] & 0xff)
                + IntToHex2(scanRecord[16] & 0xff)
                + "-"
                + IntToHex2(scanRecord[17] & 0xff)
                + IntToHex2(scanRecord[18] & 0xff)
                + "-"
                + IntToHex2(scanRecord[19] & 0xff)
                + IntToHex2(scanRecord[20] & 0xff)
                + IntToHex2(scanRecord[21] & 0xff)
                + IntToHex2(scanRecord[22] & 0xff)
                + IntToHex2(scanRecord[23] & 0xff)
                + IntToHex2(scanRecord[24] & 0xff);
        return uuid;
    }

    private String getMajor(byte[] scanRecord) {
        String hexMajor = IntToHex2(scanRecord[25] & 0xff) + IntToHex2(scanRecord[26] & 0xff);
        return String.valueOf(Integer.parseInt(hexMajor, 16));
    }

    private String getMinor(byte[] scanRecord) {
        String hexMinor = IntToHex2(scanRecord[27] & 0xff) + IntToHex2(scanRecord[28] & 0xff);
        return String.valueOf(Integer.parseInt(hexMinor, 16));
    }

    // 16進2桁に変換
    @SuppressLint("DefaultLocale")
    private String IntToHex2(int i) {
        char hex_2[]     = { Character.forDigit((i >> 4) & 0x0f, 16), Character.forDigit(i & 0x0f, 16) };
        String hex_2_str = new String(hex_2);
        return hex_2_str.toUpperCase();
    }

    private void close(){
        finish();
    }

    // -----------------------------------------------------------------------------------------
    //トグルボタン（コンポーネント）を使う
    //イベントが取得された際にonCheckedChangedが実行されますが、
    // 引数の値として、１つ目がそのクリックされたオブジェクト、
    // ２つ目が現在のトグルスイッチのbooleanでの値になります。
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.toggleButton) {
            Log.i("onCheckedChanged", "clicked R.id.toggleButton1");

            // On Off　分岐
            if (isChecked) {
                // Setting Out
                Log.d("MainActivity", "ON");
                Log.i(TAG, "MainActivity ON");
                checked = isChecked;
                Info_Invisible();

            } else {
                // Setting In
                Log.d("MainActivity", "OFF");
                Log.i(TAG, "MainActivity OFF");
                checked = isChecked;
                Info_Visible();



                // Power Iconを元に戻す
                //((ImageView) findViewById(id.PowerIcon)).setImageResource(R.drawable.power3);
            }

        }

        if (buttonView.getId() == R.id.toggleButton2) {
            Log.i("onCheckedChanged", "clicked R.id.toggleButton1");

            // On Off　分岐
            if (isChecked) {
                // Setting Out
                Log.d("MainActivity", "ON");
                Log.i(TAG, "MainActivity ON");
                //checked = isChecked;
                Graph_Invisible();

            } else {
                // Setting In
                Log.d("MainActivity", "OFF");
                Log.i(TAG, "MainActivity OFF");
                //checked = isChecked;
                Graph_Visible();



                // Power Iconを元に戻す
                //((ImageView) findViewById(id.PowerIcon)).setImageResource(R.drawable.power3);
            }

        }


        if (buttonView.getId() == R.id.toggleButton3) {
            Log.i("onCheckedChanged", "clicked R.id.toggleButton1");

            // On Off　分岐
            if (isChecked) {
                // Setting Out
                Log.d("MainActivity", "ON");
                Log.i(TAG, "MainActivity ON");
                //checked = isChecked;
                Check_Box_Invisible();

            } else {
                // Setting In
                Log.d("MainActivity", "OFF");
                Log.i(TAG, "MainActivity OFF");
                //checked = isChecked;
                findViewById(id.Plot_LIST).setVisibility(View.VISIBLE);



                // Power Iconを元に戻す
                //((ImageView) findViewById(id.PowerIcon)).setImageResource(R.drawable.power3);
            }

        }

    }
    // -----------------------------------------------------------------------------------------

    // INFO を消す
    public void Info_Invisible(){
        findViewById(id.rssi_Parameters_list).setVisibility(View.INVISIBLE);
        findViewById(id.info_layout).setVisibility(View.INVISIBLE);
        findViewById(id.w_button).setVisibility(View.INVISIBLE);
        findViewById(id.r_button).setVisibility(View.INVISIBLE);
        findViewById(id.s_button).setVisibility(View.INVISIBLE);
        findViewById(id.toggleButton2).setVisibility(View.INVISIBLE);
    }
    // INFO を表示する
    public void Info_Visible(){
        findViewById(id.rssi_Parameters_list).setVisibility(View.VISIBLE);
        findViewById(id.info_layout).setVisibility(View.VISIBLE);
        findViewById(id.w_button).setVisibility(View.VISIBLE);
        findViewById(id.r_button).setVisibility(View.VISIBLE);
        findViewById(id.s_button).setVisibility(View.VISIBLE);
        findViewById(id.toggleButton2).setVisibility(View.VISIBLE);
    }

    public void Graph_Invisible(){
        findViewById(id.chart1).setVisibility(View.INVISIBLE);
        findViewById(id.chart2).setVisibility(View.INVISIBLE);

    }

    public void Graph_Visible(){
        findViewById(id.chart1).setVisibility(View.VISIBLE);
        findViewById(id.chart2).setVisibility(View.VISIBLE);
        //findViewById(id.Plot_LIST).setVisibility(View.VISIBLE);
        Check_Box_Invisible();
    }

    public void Check_Box_Invisible(){
        findViewById(id.checkBox_all_ave).setVisibility(View.INVISIBLE);
        findViewById(id.checkBox_diff).setVisibility(View.INVISIBLE);
        findViewById(id.checkBox_3data).setVisibility(View.INVISIBLE);
        findViewById(id.checkBox_moving).setVisibility(View.INVISIBLE);
    }


    public void Check_Box_Visible(){
        findViewById(id.checkBox_all_ave).setVisibility(View.VISIBLE);
        findViewById(id.checkBox_diff).setVisibility(View.VISIBLE);
        findViewById(id.checkBox_3data).setVisibility(View.VISIBLE);
        findViewById(id.checkBox_moving).setVisibility(View.VISIBLE);
    }

    public void Data_Write(View view){
        YellowLight = Integer.parseInt(((EditText) findViewById(id.YellowEditText)).getText().toString());// Int
        RedLight = Integer.parseInt(((EditText) findViewById(id.RedEditText)).getText().toString());// Int
        (Toast.makeText(MainActivity.this, "Data Writing .... Y:"+ YellowLight + " R:" + RedLight, Toast.LENGTH_LONG)).show();
        // 入力文字列を"Yello,Red"に書き込む
        SharedPreferences.Editor editor = dataStore.edit();
        editor.putInt("Yellow", YellowLight);
        editor.putInt("Red", RedLight);
        //editor.commit();
        editor.apply();
    }


    public void Data_Read(View view){
        (Toast.makeText(MainActivity.this, "Data Reading ....", Toast.LENGTH_LONG)).show();
        // データを読み込む

        Log.d("MainActivity", "Write1");
        YellowLight = dataStore.getInt("Yellow", 0);
        RedLight = dataStore.getInt("Red", 0);

        Log.d("MainActivity", "Write2");
        //RedLight = dataStore.getInt("Red", 0);
        (Toast.makeText(MainActivity.this, "Reading .... Y: " + YellowLight + "   R: " + RedLight, Toast.LENGTH_LONG)).show();
        Log.d("MainActivity", "Write3");
        if(!(YellowLight == 0) || !(RedLight == 0)) {
            Log.d("MainActivity", "Write4");
            (Toast.makeText(MainActivity.this, "Reading .... OK !!", Toast.LENGTH_LONG)).show();
            YellowEditText.setText(String.valueOf(YellowLight));
            RedEditText.setText(String.valueOf(RedLight));
        }

    }



    // ***************************************************************************************
    // ********    ここから音声関係    *******************************************************
    // ***************************************************************************************

    void alert_back_timer(int back_msec){
        HandlerThread handlerThread = new HandlerThread("foo");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                // X 秒後にアクティビティを終了します．
                beacon_flag = OFF;
                mSoundPool.release();
            }
        },back_msec);// X msec がここです．

    }



    public Thread sound_thread_1 = new Thread(new Runnable() {
        @Override
        public void run() {

            // something
            try {

                Thread.sleep(1000);
                mSoundPool.play(mSoundId[0], volumeRate, volumeRate, 0, 0, 1.0F); // 正解音を再生
                mSoundPool.play(mSoundId[1], volumeRate, volumeRate, 0, 0, 1.0F); // 正解音を再生
            } catch (InterruptedException e) {
                // TODO 自動生成された catch ブロック
            }
        }
    });

    public Thread sound_thread_2 = new Thread(new Runnable() {
        @Override
        public void run() {
            // something
            try {

                Thread.sleep(1900);
                //mSoundPool.play(mSoundId[1], 1.0f, 1.0f, 0, 0, 1.0f); // 正解音を再生
            } catch (InterruptedException e) {
                // TODO 自動生成された catch ブロック
            }
        }
    });


    public void Sound_Play(View view) {
        // 音量関係
        /*
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        volumeRate = (float)currentVolume / maxVolume;
        mSoundPool.play(mSoundId[0], volumeRate, volumeRate, 0, 0, 1.0F);
        */

    }

    public void Sound_Play1() {
        // 音量関係
        /*
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        volumeRate = (float)currentVolume / maxVolume;
        mSoundPool.play(mSoundId[0], volumeRate, volumeRate, 0, 0, 1.0F);
        */
        soundPool.play(sound1, 0.5f, 0.5f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
    }

    public void Sound_Play2() {
        // 音量関係
        /*
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        volumeRate = (float)currentVolume / maxVolume;
        mSoundPool.play(mSoundId[0], volumeRate, volumeRate, 0, 0, 1.0F);
        */
        soundPool.play(sound2, 0.5f, 0.5f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
    }

    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    public void Graph_Create(View view) {

    }
}
