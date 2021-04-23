package com.godox.light;

import android.content.Context;
import android.graphics.SurfaceTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;


public class PreviewRender extends BaseRender {
    private static final String TAG = "BeautyRender";
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int ST_COMPONENT_COUNT = 2;
    private  float[] positionMatrix = new float[16];
    private int uTextureSamplerLocation;
    private int uPositionMatrix;

    public PreviewRender(Context context,int rawPath1,int rawPathId2) {
        super(context,rawPath1,rawPathId2);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl,config);
    }

    @Override
    protected void setLocationAttr() {
        int aPositionLocation = glGetAttribLocation(program, "a_Position");
        int aTextureCoordLocation = glGetAttribLocation(program, "a_TextureCoordinates");
        uTextureSamplerLocation = glGetUniformLocation(program, "uTextureSampler");
        uPositionMatrix = glGetUniformLocation(program, "u_PositionMatrix");

        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aTextureCoordLocation, ST_COMPONENT_COUNT, GL_FLOAT, false, 0, textureData);
        glEnableVertexAttribArray(aTextureCoordLocation);

        //激活纹理单元0
        glActiveTexture(GL_TEXTURE0);
        //绑定外部纹理到纹理单元0
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        //将激活的纹理单元传递到着色器里面
        glUniform1i(uTextureSamplerLocation, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
       super.onSurfaceChanged(gl,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        glUniformMatrix4fv(uPositionMatrix, 1, false, positionMatrix,0);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void setPositionMatrix(boolean isFont) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        setIdentityM(positionMatrix, 0);
        if(!isFont){
            rotateM(positionMatrix,0,-90f,0,0,1f);
        }else{
            rotateM(positionMatrix,0,90f,0,0,1f);
            scaleM(positionMatrix,0,1f,-1f,1f);
        }
    }
}
