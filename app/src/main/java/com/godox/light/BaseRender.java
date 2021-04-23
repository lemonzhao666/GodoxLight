package com.godox.light;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.godox.light.view.GLTextureView;
import com.zlm.base.ShaderHelper;
import com.zlm.base.TextResourceReader;
import com.zlm.base.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;

public abstract class BaseRender implements GLTextureView.Renderer {
    private Context context;
    protected FloatBuffer vertexData;
    protected FloatBuffer textureData;
    private final int BYTES_PER_FLOAT = 4;
    protected float[] positionMatrix = new float[16];
    protected float[] textureMatrix = new float[16];
    protected int mOESTextureId;
    protected SurfaceTexture mSurfaceTexture;
    protected int rawPathId1;
    protected int rawPathId2;
    protected int program;
    protected String TAG;

    public BaseRender(Context context, int rawPathId1, int rawPathId2) {
        this.context = context;
        this.rawPathId1 = rawPathId1;
        this.rawPathId2 = rawPathId2;
        TAG = getClass().getSimpleName();
        float[] vertexCoors = {
                -1f, -1f,
                1f, -1f,
                -1f, 1f,
                1f, 1f,
        };

        float[] textureCoors = {
                0f, 1f,
                1f, 1f,
                0f, 0f,
                1f, 0f
        };
        vertexData = ByteBuffer
                .allocateDirect(vertexCoors.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(vertexCoors);
        vertexData.position(0);
        textureData = ByteBuffer
                .allocateDirect(textureCoors.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureData.put(textureCoors);
        textureData.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.dTag(TAG, "onSurfaceCreated");
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        mOESTextureId = TextureHelper.createOESTextureObject();
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
        String vertexShaderSource = TextResourceReader.readResoucetText(context, rawPathId1);
        String fragmentShaderSource = TextResourceReader.readResoucetText(context, rawPathId2);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        ShaderHelper.volidateProgram(program);
        glUseProgram(program);
        setLocationAttr();
    }

    protected abstract void setLocationAttr();

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.dTag(TAG, "onSurfaceChanged");
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtils.dTag(TAG, "onDrawFrame");
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
        }
    }
    public void releaseProgram() {
        GLES20.glDeleteProgram(program);
        program = 0;
    }
    public void setPositionMatrix(boolean isFont) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        setIdentityM(positionMatrix, 0);
        if (!isFont) {
            rotateM(positionMatrix, 0, -90f, 0, 0, 1f);
        } else {
            rotateM(positionMatrix, 0, 90f, 0, 0, 1f);
            scaleM(positionMatrix, 0, 1f, -1f, 1f);
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }
}
