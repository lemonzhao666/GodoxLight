package com.godox.light;

import android.content.Context;
import android.graphics.SurfaceTexture;

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
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;


public class PreviewRender implements GLTextureView.Renderer {
    private static final String TAG = "BeautyRender";
    private Context context;
//    private final FloatBuffer vertexData;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int ST_COMPONENT_COUNT = 2;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + ST_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int mOESTextureId;
    private SurfaceTexture mSurfaceTexture;
    private int uTextureSamplerLocation;

    public PreviewRender(Context context) {
        this.context = context;
//        float[] screenVerticesWithTriangles = {
//                -1f, -1f, 0f, 1f,
//                1f, -1f, 1f, 1f,
//                -1f, 1f, 0f, 0f,
//                1f, 1f, 1f, 0f
//        };
//        vertexData = ByteBuffer
//                .allocateDirect(screenVerticesWithTriangles.length * BYTES_PER_FLOAT)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        vertexData.put(screenVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.eTag(TAG, "onSurfaceCreated");
//        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
//        mOESTextureId = TextureHelper.createOESTextureObject();
//        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
//        String vertexShaderSource = TextResourceReader.readResoucetText(context, R.raw.texture_vertex_shader);
//        String fragmentShaderSource = TextResourceReader.readResoucetText(context, R.raw.texture_fragment_shader);
//        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
//        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
//        int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
//        ShaderHelper.volidateProgram(program);
//        glUseProgram(program);
//
//        int aPositionLocation = glGetAttribLocation(program, "a_Position");
//        int aTextureCoordLocation = glGetAttribLocation(program, "a_TextureCoordinates");
//        uTextureSamplerLocation = glGetUniformLocation(program, "uTextureSampler");
//
//        vertexData.position(0);
//        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
//        glEnableVertexAttribArray(aPositionLocation);
//
//        vertexData.position(2);
//        glVertexAttribPointer(aTextureCoordLocation, ST_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
//        glEnableVertexAttribArray(aTextureCoordLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.dTag(TAG, "onSurfaceChanged");
        glViewport(0, 0, width, height);
//        final float aspectRatio = width > height ?
//                (float) width / (float) height :
//                (float) height / (float) width;
//
//        if (width > height) {
//            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//        } else {
//            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtils.dTag(TAG, "onDrawFrame");
//        glClear(GL_COLOR_BUFFER_BIT);
//        if (mSurfaceTexture != null) {
//            //更新纹理图像
//            mSurfaceTexture.updateTexImage();
//            //获取外部纹理的矩阵，用来确定纹理的采样位置，没有此矩阵可能导致图像翻转等问题
////            mSurfaceTexture.getTransformMatrix(transformMatrix);
//        }
//        //激活纹理单元0
//        glActiveTexture(GL_TEXTURE0);
//        //绑定外部纹理到纹理单元0
//        glBindTexture(GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
//        //将激活的纹理单元传递到着色器里面
//        glUniform1i(uTextureSamplerLocation, 0);
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }
}
