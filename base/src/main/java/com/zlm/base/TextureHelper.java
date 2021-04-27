package com.zlm.base;

import android.opengl.GLES20;
import android.util.Log;

import com.blankj.utilcode.util.ThreadUtils;

import static android.opengl.GLES11Ext.*;
import static android.opengl.GLES20.*;

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int createOESTextureObject() {
        int[] textureObjectIds = new int[1];
        //生成一个纹理
        glGenTextures(1, textureObjectIds, 0);
        Log.d(TAG, "mOESTextureId[0] = " + textureObjectIds[0]+ "isMainThread = "+ThreadUtils.isMainThread());
        //将此纹理绑定到外部纹理上
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureObjectIds[0]);
        //设置纹理过滤参数
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
        return textureObjectIds[0];
    }

}
