package com.whitehole.worldmapObject;

import com.whitehole.rendering.BmdRenderer;
import com.whitehole.rendering.GLRenderer;
import com.whitehole.smg.BcsvFile;
import javax.media.opengl.GL2;

public class GalaxyPreview extends WorldmapPoint {
    public GalaxyPreview(BcsvFile.Entry entry, WorldmapPoint base) {
        super(base.entry);
        entryGP = entry;
    }
    
    public GalaxyPreview(BcsvFile.Entry entry, BcsvFile.Entry pointEntry) {
        super(pointEntry);
        entryGP = entry;
    }
    
    @Override
    public void initRenderer(GLRenderer.RenderInfo info) {
        modelRenderer = new BmdRenderer(info,(String)entryGP.get("MiniatureName"));
        super.initRenderer(info);
    }
    
    @Override
    public void closeRenderer(GLRenderer.RenderInfo info) {
        if (modelRenderer == null)
            return;
        
        modelRenderer = null;
        super.closeRenderer(info);
    }
    
    @Override
    public void render(GLRenderer.RenderInfo info, GLRenderer pointRenderer) {
        GL2 gl = info.drawable.getGL().getGL2();
        
        gl.glPushMatrix();
        gl.glTranslatef((float)entry.get(-726582764), (float)entry.get(-726582763), (float)entry.get(-726582762));
        if(pointRenderer == null)
            pointRenderer = new BmdRenderer(info, "MiniRoutePoint");
        pointRenderer.render(info);
        gl.glTranslatef((float)entryGP.get(1370777937), (float)entryGP.get(1370777938), (float)entryGP.get(1370777939));
        gl.glScalef((float)entryGP.get(-827224888), (float)entryGP.get(-827224888), (float)entryGP.get(-827224888));
        if(entryGP.get(-454084808).equals("BossGalaxyLv3"))
            gl.glRotatef(-45f, 0f, 1f, 0f);
        
        modelRenderer.render(info);
        gl.glPopMatrix();
    }
    
    @Override
    public String getName(){
        return "["+entry.get(70793394)+"] " + entryGP.get("StageName");
    }
    
    BmdRenderer modelRenderer;
    public BcsvFile.Entry entryGP;
}
