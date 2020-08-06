/*
    © 2012 - 2019 - Whitehole Team

    Whitehole is free software: you can redistribute it and/or modify it under
    the terms of the GNU General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version.

    Whitehole is distributed in the hope that it will be useful, but WITHOUT ANY 
    WARRANTY; See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along 
    with Whitehole. If not, see http://www.gnu.org/licenses/.
*/

package com.whitehole.rendering.object;

import com.whitehole.rendering.GLRenderer;
import com.whitehole.vectors.Color4;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

public class AreaRenderer extends GLRenderer {
    public AreaRenderer(Color4 color, Shape shape) {
        areaColor = color;
        areaShape = shape;
    }
    
    @Override
    public void close(GLRenderer.RenderInfo info) throws GLException {}
    
    @Override
    public boolean isScaled() {
        return true;
    }

    @Override
    public boolean gottaRender(GLRenderer.RenderInfo info) throws GLException {
        return (info.renderMode != GLRenderer.RenderMode.TRANSLUCENT);
    }

    @Override
    public void render(GLRenderer.RenderInfo info) throws GLException {
        if (info.renderMode == GLRenderer.RenderMode.TRANSLUCENT)
            return;
        
        GL2 gl = info.drawable.getGL().getGL2();

        if (info.renderMode != GLRenderer.RenderMode.PICKING) {
            for (int i = 0; i < 8; i++) {
                try {
                    if(gl.isFunctionAvailable("glActiveTexture"))
                        gl.glActiveTexture(GL2.GL_TEXTURE0 + i);
                    gl.glDisable(GL2.GL_TEXTURE_2D);
                }
                catch (GLException ex) {}
            }
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glDepthFunc(GL2.GL_LEQUAL);
            gl.glColor3f(areaColor.r, areaColor.g, areaColor.b);
            try {
                gl.glUseProgram(0);
            }
            catch (GLException ex) {}
            gl.glLineWidth(4f);
        }else{
            gl.glLineWidth(8f);
        }
        
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glCullFace(GL2.GL_FRONT);
        
        switch(areaShape) {
            case CUBE: makeBox(info, 500f); break;
            case CENTEREDCUBE: makeBox(info, 0f); break;
            case SPHERE: makeSphere(info); break;
            case CYLINDER: makeCylinder(info); break;
            case CONE: makeCone(info); break;
        }
        
        gl.glLineWidth(1.5f);
    }
    
    public void makeBox(GLRenderer.RenderInfo info, float ytrans) {
        GL2 gl = info.drawable.getGL().getGL2();
        
        gl.glTranslatef(0f, ytrans, 0f);
        gl.glBegin(GL2.GL_LINE_STRIP);
        gl.glVertex3f(SIZE, SIZE, SIZE);
        gl.glVertex3f(-SIZE, SIZE, SIZE);
        gl.glVertex3f(-SIZE, SIZE, -SIZE);
        gl.glVertex3f(SIZE, SIZE, -SIZE);
        gl.glVertex3f(SIZE, SIZE, SIZE);
        gl.glVertex3f(SIZE, -SIZE, SIZE);
        gl.glVertex3f(-SIZE, -SIZE, SIZE);
        gl.glVertex3f(-SIZE, -SIZE, -SIZE);
        gl.glVertex3f(SIZE, -SIZE, -SIZE);
        gl.glVertex3f(SIZE, -SIZE, SIZE);
        gl.glEnd();
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(-SIZE, SIZE, SIZE);
        gl.glVertex3f(-SIZE, -SIZE, SIZE);
        gl.glVertex3f(-SIZE, SIZE, -SIZE);
        gl.glVertex3f(-SIZE, -SIZE, -SIZE);
        gl.glVertex3f(SIZE, SIZE, -SIZE);
        gl.glVertex3f(SIZE, -SIZE, -SIZE);
        gl.glEnd();
        gl.glTranslatef(0f, 0f, 0f);
    }
    
    public void makeSphere(GLRenderer.RenderInfo info) {
        GL2 gl = info.drawable.getGL().getGL2();
        GLU glu = new GLU();
        GLUquadric sphere = glu.gluNewQuadric();
        
        gl.glTranslatef(0f, 0f, 0f);
        gl.glRotatef(90f, 1f, 0f, 0f);
        glu.gluQuadricDrawStyle(sphere, GLU.GLU_LINE);
        glu.gluSphere(sphere, SIZE, 28, 8);
        glu.gluDeleteQuadric(sphere);
        gl.glTranslatef(0f, 0f, 0f);
    }
    
    public void makeCylinder(GLRenderer.RenderInfo info) {
        GL2 gl = info.drawable.getGL().getGL2();
        GLU glu = new GLU();
        GLUquadric cylinder = glu.gluNewQuadric();
        gl.glScalef(1, 0.5f, 1);
        gl.glTranslatef(0f, 1000f, 0f);
        gl.glRotatef(90f, 1f, 0f, 0f);
        glu.gluQuadricDrawStyle(cylinder, GLU.GLU_LINE);
        glu.gluCylinder(cylinder, SIZE, SIZE, SIZE * 2, 28, 1);
        glu.gluDeleteQuadric(cylinder);
        gl.glTranslatef(0f, 0f, 0f);
    }
    
    public void makeCone(GLRenderer.RenderInfo info) {
        GL2 gl = info.drawable.getGL().getGL2();
        GLU glu = new GLU();
        GLUquadric cylinder = glu.gluNewQuadric();
        
        gl.glTranslatef(0f, 1000f, 0f);
        gl.glRotatef(90f, 1f, 0f, 0f);
        glu.gluQuadricDrawStyle(cylinder, GLU.GLU_LINE);
        glu.gluCylinder(cylinder, 0, SIZE, SIZE * 2, 28, 1);
        glu.gluDeleteQuadric(cylinder);
        gl.glTranslatef(0f, 0f, 0f);
    }
    
    private static final float SIZE = 500f;
    private final Shape areaShape;
    private final Color4 areaColor;
    
    public static enum Shape {
        UNDEFINED,
        CUBE,
        CENTEREDCUBE,
        SPHERE,
        CYLINDER,
        BOWL,
        CONE;
    }
}