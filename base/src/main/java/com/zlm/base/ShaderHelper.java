package com.zlm.base;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";
    public static int compileVertexShader(String shaderCode){
       return compileShader(GLES20.GL_VERTEX_SHADER,shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        //创建shader
        int shaderId = GLES20.glCreateShader(type);
        if (shaderId == 0) {
            Log.d(TAG, "创建shader失败");
            return 0;
        }
        //着色器和shader关联
        GLES20.glShaderSource(shaderId, shaderCode);
        //编译shader源代码
        GLES20.glCompileShader(shaderId);
        //取出编译结果
        int[] compileStatus = new int[1];
        //取出shaderId的编译状态并把他写入compileStatus的0索引
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        Log.d(TAG, "编译状态 "+GLES20.glGetShaderInfoLog(shaderId));

        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderId);
            Log.d(TAG, "创建shader失败");
            return 0;
        }

        return shaderId;
    }

    public static int linkProgram(int mVertexshader, int mFragmentshader) {
        //创建程序对象
        int programId = GLES20.glCreateProgram();
        if (programId == 0) {
            Log.d(TAG, "创建program失败");
            return 0;
        }
        //依附着色器
        GLES20.glAttachShader(programId, mVertexshader);
        GLES20.glAttachShader(programId, mFragmentshader);
        //链接程序
        GLES20.glLinkProgram(programId);
        //检查链接状态
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        Log.d(TAG, "链接程序" + GLES20.glGetProgramInfoLog(programId));
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programId);
            Log.d(TAG, "链接program失败");
            return 0;
        }

        return programId;

    }

    public static boolean volidateProgram(int program) {
        GLES20.glValidateProgram(program);
        int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        Log.d(TAG, "当前openl情况" + validateStatus[0] + "/" + GLES20.glGetProgramInfoLog(program));

        return validateStatus[0] != 0;
    }
}
