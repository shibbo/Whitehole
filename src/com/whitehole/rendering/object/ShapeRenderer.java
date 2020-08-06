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

import com.whitehole.rendering.BmdRenderer;

public class ShapeRenderer extends BmdRenderer {
    public ShapeRenderer(RenderInfo info, String objname, short modelno) {
        if (modelno < 100 && modelno > -1) {
            ctor_loadModel(objname + String.format("%1$02d", modelno));
            ctor_uploadData(info);
        }
    }
    
    @Override
    public boolean boundToProperty() {
        return true;
    }
}