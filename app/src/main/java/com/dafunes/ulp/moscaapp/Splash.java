package com.dafunes.ulp.moscaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Diego on 23/03/2018.
 */

public class Splash extends AppCompatActivity {
    private TextView tvSplash;
    private ImageView ivSplash;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        tvSplash= (TextView)findViewById( R.id.tvSplash );
        ivSplash= (ImageView) findViewById( R.id.ivSplash );

        Animation myanim= AnimationUtils.loadAnimation( getApplicationContext(), R.anim.mytransition );
        tvSplash.startAnimation( myanim );
        ivSplash.startAnimation( myanim );
        final Intent i =new Intent( getApplicationContext(), Home.class );
        i.setFlags( i.FLAG_ACTIVITY_NO_ANIMATION );
        Thread timer =new Thread(  ){
            public void run(){
                try{
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity( i );
                    finish();
                }
            }
        };
            timer.start();
    }
}
