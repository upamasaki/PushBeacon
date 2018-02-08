package univ.apu.tani.pushbeacon;

import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.os.SystemClock.sleep;
import static univ.apu.tani.pushbeacon.MainActivity.OFF;
import static univ.apu.tani.pushbeacon.MainActivity.beacon_flag;

public class alerm_screen_2 extends AppCompatActivity {

    int back_msec = 10000;//アラームが鳴ってからアクティビティが終了するまでの時間[msec]
    Boolean isDisp;
    private ImageView img;

    /*------------------ Sound Info -----------------------------------------*/
    // SoundPool(効果音再生)
    public SoundPool mSoundPool;
    public int[] mSoundId = new int[2]; // 使う効果音の数だけ配列作成


    // 音量関係
    /*
    AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
    int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
    int maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    float volumeRate = (float)currentVolume / maxVolume;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerm_screen_2);


        // X 秒後にアクティビティを終了します．
        alert_back_timer(back_msec);
        image_changer();



    }

    void alert_back_timer(int back_msec){
        HandlerThread handlerThread = new HandlerThread("foo");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                // X 秒後にアクティビティを終了します．
                beacon_flag = OFF;
                mSoundPool.release();
                finish();
            }
        },back_msec);// X msec がここです．

    }

    void image_changer(){

        img = (ImageView) findViewById(R.id.imageView);

        //setBackgroundDrawableがAPI level 16以上でdeprecatedになっている.
        //API level 15以下と16以上の両方をサポートする場合は以下のように分岐させないといけない.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            img.setBackground(ContextCompat.getDrawable(alerm_screen_2.this, R.drawable.image_animation));
        } else {
            img.setBackgroundDrawable(ContextCompat.getDrawable(alerm_screen_2.this, R.drawable.image_animation));
        }

        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
        // アニメーションの開始
        frameAnimation.start();



    }

    public Thread sound_thread = new Thread(new Runnable() {
        @Override
        public void run() {
            // 音量関係
            AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            int maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            float volumeRate = (float)currentVolume / maxVolume;
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

    @Override
    protected void onResume() {
        super.onResume();

        // 効果音を使えるように読み込み
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundId[0] = mSoundPool.load(getApplicationContext(), R.raw.danger, 1);
        mSoundId[1] = mSoundPool.load(getApplicationContext(), R.raw.piano41, 1);


        sound_thread.start();
        sound_thread_2.start();
        //mSoundPool.release();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // リリース
        mSoundPool.release();
    }

}
