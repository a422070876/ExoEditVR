package com.hyq.hm.exovr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Created by 海米 on 2018/10/26.
 */

public class GLRenderer {

    private Context context;
    private int aPositionHandle;
    private int programId;

    private final float[] projectionMatrix= new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private final float[] angleMatrix = new float[16];


    private final float[] mSTMatrix = new float[16];
    private int uMatrixHandle;
    private int uSTMatrixHandle;

    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;

    private int screenWidth,screenHeight;

    private int[] textures;
    private int textureId;

    private Sphere sphere;


    private SurfaceTexture surfaceTexture;

    public GLRenderer(Context context) {
        this.context = context;
        sphere = new Sphere(18,100,200);
    }
    public void onSurfaceCreated(){
        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vertext_shader);
        String fragmentShader= ShaderUtils.readRawTextFile(context, R.raw.fragment_sharder);
        programId=ShaderUtils.createProgram(vertexShader,fragmentShader);
        aPositionHandle= GLES20.glGetAttribLocation(programId,"aPosition");
        uMatrixHandle=GLES20.glGetUniformLocation(programId,"uMatrix");
        uSTMatrixHandle=GLES20.glGetUniformLocation(programId,"uSTMatrix");
        uTextureSamplerHandle=GLES20.glGetUniformLocation(programId,"sTexture");
        aTextureCoordHandle=GLES20.glGetAttribLocation(programId,"aTexCoord");

        textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        textureId = textures[0];
        surfaceTexture = new SurfaceTexture(textureId);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }
    public void release(){
        if(surfaceTexture != null){
            GLES20.glDeleteProgram(programId);
            GLES20.glDeleteTextures(1,textures,0);
            surfaceTexture.release();
            surfaceTexture = null;
        }
    }
    public void onSurfaceChanged(int width, int height) {
        screenWidth=width; screenHeight=height;
        float ratio=(float)width/height;

        Matrix.perspectiveM(projectionMatrix, 0, 90f, ratio,  1, 50);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f,-1.0f,
                0.0f, 1.0f, 0.0f);
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    public float xAngle=0f;
    public float yAngle=90f;
    public float zAngle;

    public void onDrawFrame(){
        if(surfaceTexture == null){
            return;
        }
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(mSTMatrix);

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glViewport(0,0,screenWidth,screenHeight);
        GLES20.glUseProgram(programId);
        Matrix.setIdentityM(modelMatrix,0);

        Matrix.rotateM(modelMatrix, 0, -xAngle, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, -yAngle, 0, 1, 0);
        Matrix.rotateM(modelMatrix, 0, -zAngle, 0, 0, 1);

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);



//        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
//        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);


////        Matrix.setIdentityM(angleMatrix,0);
////        Matrix.rotateM(angleMatrix,0,90,1,0,0);
//
//
//        Matrix.multiplyMM(angleMatrix, 0, modelMatrix, 0, angleMatrix, 0);
//        Matrix.setRotateM(mMVPMatrix,0,90,0,0,1);
////        Matrix.rotateM(mMVPMatrix,0,180,0,1,0);
////        Matrix.rotateM(mMVPMatrix,0,90,0,0,1);
//        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, viewMatrix, 0);
//        Matrix.multiplyMM(modelViewMatrix, 0, mMVPMatrix, 0, angleMatrix, 0);
//
////        Matrix.rotateM(modelViewMatrix,0,90 ,0,1,0);
////        if(ax >= 90 && ax <= 270){
////            Matrix.rotateM(modelViewMatrix,0,ay ,1,0,0);
////        }else{
////            Matrix.rotateM(modelViewMatrix,0,ay ,0,1,0);
////        }
//        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);


//        Matrix.rotateM(mMVPMatrix,0,ax,-1,0,0);
//        Matrix.rotateM(mMVPMatrix,0,ax ,0,0,1);
//        Matrix.rotateM(mMVPMatrix,0,ay,0,1,0);
        GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,mMVPMatrix,0);
        GLES20.glUniformMatrix4fv(uSTMatrixHandle,1,false,mSTMatrix,0);

        sphere.uploadVerticesBuffer(aPositionHandle);
        sphere.uploadTexCoordinateBuffer(aTextureCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
        GLES20.glUniform1i(uTextureSamplerHandle,0);

        sphere.draw();

    }
}
