package com.example.batalfirebasefinalsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    //Variables
    Animation bott_anim;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home = new Intent(MainActivity.this, Landing.class);
                finish();
                startActivity(home);
            }
        }, 3000);

        //Animations
        bott_anim = AnimationUtils.loadAnimation(this,R.anim.splash_anim);

        refs();

        //Assign
        logo.setAnimation(bott_anim);
    }
    //Hooks
    public void refs(){
            logo = findViewById(R.id.splash_logo);
        }

}
