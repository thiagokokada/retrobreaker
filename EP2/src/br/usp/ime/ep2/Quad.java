package br.usp.ime.ep2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

class Quad {
    private float mPosX;
    private float mPosY;
    private float mScale;
    private float[] mVertices;
    private float[] mColors; 
    
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    
    private static final int FLOAT_SIZE_BYTES = Float.SIZE / 8;
    
    
    public Quad(float[] vertices, float[] colors, float pos_x, float pos_y ,float scale) {
    	mVertices = vertices;
    	mColors = colors;
    	mPosX = pos_x;
    	mPosY = pos_y;
    	mScale = scale;
    	
        ByteBuffer vbb = ByteBuffer.allocateDirect( mVertices.length * FLOAT_SIZE_BYTES );
        vbb.order( ByteOrder.nativeOrder() );
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put( mVertices );
        mVertexBuffer.position( 0 );

        ByteBuffer cbb = ByteBuffer.allocateDirect( mColors.length * FLOAT_SIZE_BYTES );
        cbb.order( ByteOrder.nativeOrder() );
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put( mColors );
        mColorBuffer.position( 0 );
    }
    
    
    public void setPosition( float x, float y ) {
        this.mPosX = x;
        this.mPosY = y;
    }
    
    public void setXPosition( float x) {
    	this.mPosX = x;
    }


    public void draw( GL10 gl ) {
        gl.glMatrixMode( GL10.GL_MODELVIEW );
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef( mPosX, mPosY, 0.0f );
        gl.glScalef( mScale, mScale, mScale );

        gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
        gl.glEnableClientState( GL10.GL_COLOR_ARRAY );
        
        gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, mVertexBuffer );
        gl.glColorPointer( 4, GL10.GL_FLOAT, 0, mColorBuffer );
        
        gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, 4 );
        
        gl.glDisableClientState( GL10.GL_VERTEX_ARRAY );
        gl.glDisableClientState( GL10.GL_COLOR_ARRAY );
        
        gl.glPopMatrix();
    }
}