package food_system.project.stiw2044.com.stiw2044_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView splashImage;
    TextView splashText , splashPercent;
    ProgressBar splashProgress;
    private int progress = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);

        splashImage = (ImageView)findViewById(R.id.splash_image);
        splashText = (TextView)findViewById(R.id.splash_text);
        splashProgress = (ProgressBar)findViewById(R.id.splash_progress);
        splashPercent = (TextView)findViewById(R.id.splash_percent);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.transition);
        splashImage.startAnimation(animation);
        splashText.startAnimation(animation);
        splashProgress.setProgress(0);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (progress < 100){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            splashPercent.setText(String.valueOf(progress)+"%");
                        }
                    });
                    splashProgress.setProgress(progress);
                    progress++;
                }else{
                    //closing the timer
                    timer.cancel();
                }
            }
        }, 0, 30); //repeats every 30ms
    }

}


