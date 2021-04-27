package com.godox.light;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.godox.light.filter.BeautyFilter;
import com.godox.light.filter.GpuFilter;
import com.godox.light.view.GLTextureView;
import com.zlm.base.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class GpuRenderer implements GLTextureView.Renderer {
    private GpuFilter filter;
    private final Queue<Runnable> runOnDraw;
    private final Queue<Runnable> runOnDrawEnd;
    protected FloatBuffer vertexData;
    protected FloatBuffer textureData;
    private final int BYTES_PER_FLOAT = 4;
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
    private int mOESTextureId;
    private SurfaceTexture mSurfaceTexture;

    public GpuRenderer(GpuFilter gpuFilter) {
        this.filter = gpuFilter;
        runOnDraw = new LinkedList<>();
        runOnDrawEnd = new LinkedList<>();

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
        glClearColor(0f, 0.0f, 0.0f, 0.0f);
        glDisable(GL_DEPTH_TEST);
         mOESTextureId = TextureHelper.createOESTextureObject();
        //SurfaceTexture与纹理id相关联，方便摄像头的数据能够传递给OpenGL
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
        filter.ifNeedInit();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        filter.outputWidth = width;
        filter.outputHeight = height;
        filter.onReady(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        runAll(runOnDraw);
        filter.onDraw(mOESTextureId, vertexData, textureData);
        runAll(runOnDrawEnd);
        mSurfaceTexture.updateTexImage();
    }

    public void setFilter(GpuFilter filter) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GpuFilter oldFilter = GpuRenderer.this.filter;
                GpuRenderer.this.filter = filter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                GpuRenderer.this.filter.ifNeedInit();
                GpuRenderer.this.filter.onReady(filter.outputWidth,filter.outputHeight);
                glUseProgram(GpuRenderer.this.filter.getProgram());
            }
        });
    }
    public void enableBeauty(final boolean isChecked) {
        //因为操作滤镜只能在 GLThread里面操作，所以要这样写
//        .queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                if (isChecked) {
//                    mBeautyFilter = new BeautyFilter(R.raw.beauty_vertex_shader,R.raw.beauty_fragment_shader);
//                    mBeautyFilter.onReady(mWidth, mHeight);
//                } else {
//                    mBeautyFilter = null;
//                }
//            }
//        });
    }
    private void runOnDraw(Runnable runnable) {
         synchronized (runOnDraw) {
            runOnDraw.add(runnable);
        }
    }

    protected void runOnDrawEnd(final Runnable runnable) {
        synchronized (runOnDrawEnd) {
            runOnDrawEnd.add(runnable);
        }
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

}
