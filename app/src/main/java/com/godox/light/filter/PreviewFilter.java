package com.godox.light.filter;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;

public class PreviewFilter extends GpuFilter {
    private float[] positionMatrix = new float[16];
    private int glUniformPositionMatrix;
    private static final String TAG = "PreviewFilter";

    public PreviewFilter(Context context, int vertexShaderPath, int fragmentShaderPath) {
        super(context, vertexShaderPath, fragmentShaderPath);
    }

    @Override
    public void onInit() {
        super.onInit();
        LogUtils.dTag(TAG,"onInit");
        glUniformPositionMatrix = glGetUniformLocation(glProgramId, "u_PositionMatrix");
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
    }

}
