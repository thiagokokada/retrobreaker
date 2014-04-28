package com.demo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GLSurfaceView glSurfaceView;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        glSurfaceView = new TouchSurfaceView( this );
        setContentView( glSurfaceView );
        
        glSurfaceView.requestFocus();
        glSurfaceView.setFocusableInTouchMode( true );
    }


    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
}