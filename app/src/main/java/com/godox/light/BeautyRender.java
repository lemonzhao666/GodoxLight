package com.godox.light;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;


public class BeautyRender extends BaseRender {
    private static final String TAG = "BeautyRender";
    private float opacity;
    private final int POSITION_COMPONENT_COUNT = 2;
    private final int TEXTTURE_COMPONENT_COUNT = 2;
    private int uTextureSamplerLocation;
    private int uPositionMatrix;
    private int uWidth;
    private int uHeight;
    private int uOpacity;
    private int mWidth;
    private int mHeight;
    private int uTextureMatrix;


    public BeautyRender(Context context, int rawPathId1, int rawPathId2) {
        super(context, rawPathId1, rawPathId2);
        setSmoothOpacity(1f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
    }

    @Override
    protected void setLocationAttr() {
        int aPositionLocation = glGetAttribLocation(program, "a_Position");
        int aTextureCoordLocation = glGetAttribLocation(program, "a_TextureCoordinates");
        uPositionMatrix = glGetUniformLocation(program, "u_PositionMatrix");
        //uTextureMatrix = glGetUniformLocation(program, "u_TextureMatrix");
        uTextureSamplerLocation = glGetUniformLocation(program, "u_TextureSampler");
        uWidth = glGetUniformLocation(program, "width");
        uHeight = glGetUniformLocation(program, "height");
        uOpacity = glGetUniformLocation(program, "opacity");
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aTextureCoordLocation, TEXTTURE_COMPONENT_COUNT, GL_FLOAT, false, 0, textureData);
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
        super.onSurfaceChanged(gl, width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        glUniform1f(uWidth, mWidth);
        glUniform1f(uHeight, mHeight);
        glUniform1f(uOpacity, opacity);
        //mSurfaceTexture.getTransformMatrix(textureMatrix);
        //glUniformMatrix4fv(uTextureMatrix, 1, false, textureMatrix,0);
        glUniformMatrix4fv(uPositionMatrix, 1, false, positionMatrix, 0);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    public void setSmoothOpacity(float percent) {
        if (percent <= 0) {
            opacity = 0.0f;
        } else {
            opacity = calculateOpacity(percent);
        }
    }

    private float calculateOpacity(float percent) {
        float result;
        result = (float) (1.0f - (1.0f - percent + 0.02) / 2.0f);
        return result;
    }
}
