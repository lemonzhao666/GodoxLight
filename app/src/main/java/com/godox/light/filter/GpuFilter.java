package com.godox.light.filter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.zlm.base.ShaderHelper;
import com.zlm.base.TextResourceReader;
import com.zlm.base.TextureHelper;

import java.nio.FloatBuffer;
import java.util.LinkedList;

import static android.opengl.GLES11Ext.*;
import static android.opengl.GLES20.*;


public  class GpuFilter {
    private Context context;
    private final int vertexShaderPath;
    private final int fragmentShaderPath;
    protected int glProgramId;
    private boolean isInitialized;
    private final LinkedList<Runnable> runOnDraw;
    private int glAttribPosition;
    private int glAttribTextureCoordinate;
    private int glUniformTextureSampler;
    public boolean isFont;
    public int outputWidth;
    public int outputHeight;
    protected int[] mFrameBuffer;

    public GpuFilter(Context context, int vertexShaderPath, int fragmentShaderPath) {
        runOnDraw = new LinkedList<>();
        this.context = context;
        this.vertexShaderPath = vertexShaderPath;
        this.fragmentShaderPath = fragmentShaderPath;
    }

    private final void init() {
        onInit();
        onInitialized();
    }

    public void onInit() {
        String vertexShaderSource = TextResourceReader.readResoucetText(context, vertexShaderPath);
        String fragmentShaderSource = TextResourceReader.readResoucetText(context, fragmentShaderPath);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        glProgramId = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        glAttribPosition = glGetAttribLocation(glProgramId, "a_Position");
        glAttribTextureCoordinate = glGetAttribLocation(glProgramId, "a_TextureCoordinates");
        glUniformTextureSampler = glGetUniformLocation(glProgramId, "u_TextureSampler");
        isInitialized = true;
    }

    public void onInitialized() {

    }

    public void onReady(int width,int height){
        mFrameBuffer = new int[1];
        int textureObjectId = TextureHelper.createOESTextureObject();
        glGenFramebuffers(1, mFrameBuffer, 0);

        glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureObjectId);
        glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);

        // 根据颜色参数，宽高等信息，为上面的纹理ID，生成一个2D纹理
        glTexImage2D(GL_TEXTURE_EXTERNAL_OES, 0, GL_RGBA, width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_EXTERNAL_OES, textureObjectId, 0);

        glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);
    }

    public void ifNeedInit() {
        if (!isInitialized) init();
    }



    public int onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        glUseProgram(glProgramId);
        runPendingOnDrawTasks();
        if (!isInitialized) {
            return -1;
        }
        cubeBuffer.position(0);
        glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        glEnableVertexAttribArray(glAttribPosition);
        textureBuffer.position(0);
        glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        glEnableVertexAttribArray(glAttribTextureCoordinate);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
        glUniform1i(glUniformTextureSampler, 0);

        onDrawArraysPre();
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        return textureId;
    }

    protected void runPendingOnDrawTasks() {
        synchronized (runOnDraw) {
            while (!runOnDraw.isEmpty()) {
                runOnDraw.removeFirst().run();
            }
        }
    }

    protected void onDrawArraysPre() {
    }

    public final void destroy() {
        isInitialized = false;
        glDeleteProgram(glProgramId);
        onDestroy();
    }

    public void onDestroy() {

    }

    public int getProgram() {
        return glProgramId;
    }
    protected void runOnDraw(final Runnable runnable) {
        synchronized (runOnDraw) {
            runOnDraw.addLast(runnable);
        }
    }
    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }
}
