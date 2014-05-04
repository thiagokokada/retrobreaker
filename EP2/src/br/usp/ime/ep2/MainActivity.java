package br.usp.ime.ep2;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GLSurfaceView mGlSurfaceView;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        mGlSurfaceView = new TouchSurfaceView( this );
        setContentView( mGlSurfaceView );
        
        mGlSurfaceView.requestFocus();
        mGlSurfaceView.setFocusableInTouchMode( true );
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGlSurfaceView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mGlSurfaceView.onPause();
    }
}