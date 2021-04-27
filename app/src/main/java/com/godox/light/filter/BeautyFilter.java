package com.godox.light.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.blankj.utilcode.util.LogUtils;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;

public class BeautyFilter extends GpuFilter {
    private int glUniformPositionMatrix;
    private int glUniformWidth;
    private int glUniformHeight;
    private int glUniformOpacity;
    private float opacity = 0.7f;
    private float[] positionMatrix = new float[16];
    private static final String TAG = "BeautyFilter";


    public BeautyFilter(Context context, int vertexShaderPath, int fragmentShaderPath) {
        super(context, vertexShaderPath, fragmentShaderPath);
    }

    @Override
    public void onInit() {
        super.onInit();
        LogUtils.dTag(TAG,"onInit");
        glUniformPositionMatrix = glGetUniformLocation(glProgramId, "u_PositionMatrix");
        glUniformWidth = glGetUniformLocation(glProgramId, "width");
        glUniformHeight = glGetAttribLocation(glProgramId, "height");
        glUniformOpacity = glGetAttribLocation(glProgramId, "opacity");
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        setIdentityM(positionMatrix, 0);
        LogUtils.dTag(TAG,"isFont = "+isFont);
        if (!isFont) {
            rotateM(positionMatrix, 0, -90f, 0, 0, 1f);
        } else {
            rotateM(positionMatrix, 0, 90f, 0, 0, 1f);
            scaleM(positionMatrix, 0, 1f, -1f, 1f);
        }
        glUniformMatrix4fv(glUniformPositionMatrix, 1, false, positionMatrix, 0);
        glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        setInteger(glUniformWidth,outputWidth);
        setInteger(glUniformHeight,outputHeight);
        setFloat(glUniformOpacity,opacity);
    }

    @Override
    public int onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        return mFrameBuffer[0];
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
