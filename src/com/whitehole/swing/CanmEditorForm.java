/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whitehole.swing;

import com.whitehole.Whitehole;
import com.whitehole.rendering.SimpleGalaxyRenderer;
import com.whitehole.rendering.cache.RendererCache;
import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

public class CanmEditorForm extends javax.swing.JFrame {

    private GLCanvas glCanvas;
    private SimpleGalaxyRenderer renderer;
    
    public CanmEditorForm() {
        try {
            initComponents();
            setIconImage(Whitehole.ICON);
            
            GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
            caps.setSampleBuffers(true);
            caps.setNumSamples(8);
            caps.setHardwareAccelerated(true);
            
            glCanvas = new GLCanvas(caps, RendererCache.refContext);
            
            glCanvas.addGLEventListener    (renderer = new SimpleGalaxyRenderer("BigGalaxy", glCanvas));
            glCanvas.addMouseListener      (renderer);
            glCanvas.addMouseMotionListener(renderer);
            glCanvas.addMouseWheelListener (renderer);
            glCanvas.addKeyListener        (renderer);
            
            glCanvas.setSize(400, 400);
            
            glCanvas.setMaximumSize(new Dimension(32767, 32767));
            
            pnlGlCanvas.add(glCanvas);
            setSize(getContentPane().getPreferredSize());
            glCanvas.requestFocus();
        } catch (IOException ex) {
            Logger.getLogger(CanmEditorForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGlCanvas = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CANM Editor");

        javax.swing.GroupLayout pnlGlCanvasLayout = new javax.swing.GroupLayout(pnlGlCanvas);
        pnlGlCanvas.setLayout(pnlGlCanvasLayout);
        pnlGlCanvasLayout.setHorizontalGroup(
            pnlGlCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 816, Short.MAX_VALUE)
        );
        pnlGlCanvasLayout.setVerticalGroup(
            pnlGlCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGlCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGlCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlGlCanvas;
    // End of variables declaration//GEN-END:variables
}
