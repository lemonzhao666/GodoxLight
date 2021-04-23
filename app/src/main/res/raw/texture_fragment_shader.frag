#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_TextureCoordinates;
uniform samplerExternalOES u_TextureSampler;


void main()
{
    gl_FragColor = texture2D(u_TextureSampler, v_TextureCoordinates);
}