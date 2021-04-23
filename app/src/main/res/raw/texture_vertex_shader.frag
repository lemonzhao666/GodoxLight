attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;
varying vec2 v_TextureCoordinates;
uniform mat4 u_PositionMatrix;
uniform mat4 u_TextureMatrix;
void main()
{
    gl_Position = u_PositionMatrix * a_Position;
   // v_TextureCoordinates =  (u_TextureMatrix * vec4(a_TextureCoordinates, 0, 1.0)).xy;
    v_TextureCoordinates = a_TextureCoordinates;
}