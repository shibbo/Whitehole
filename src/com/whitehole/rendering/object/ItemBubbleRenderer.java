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

import com.whitehole.rendering.BmdRendererSingle;
import com.whitehole.rendering.GLRenderer;
import com.whitehole.rendering.GLRenderer.RenderInfo;
import com.whitehole.rendering.MultiRenderer;
import com.whitehole.vectors.Vector3;
import java.io.IOException;

public class ItemBubbleRenderer extends MultiRenderer {
    public ItemBubbleRenderer(RenderInfo info, String modelname, int arg7) throws IOException {
        long t = System.currentTimeMillis();
        boolean argIsUnique = arg7 != -1;
        renderers = new GLRenderer[argIsUnique ? 2 : 1];
        
        renderers[0] = new BmdRendererSingle(info, modelname);
        if (argIsUnique)
            renderers[1] = new BmdRendererSingle(info, "ItemBubble", new Vector3(0f,60f,0f), new Vector3());
        
        long x = System.currentTimeMillis() - t;
    }
    
    @Override
    public boolean boundToObjArg(int arg) {
        return arg == 7;
    }
}