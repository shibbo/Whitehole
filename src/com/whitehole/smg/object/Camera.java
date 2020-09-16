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

package com.whitehole.smg.object;

import com.whitehole.Settings;
import com.whitehole.swing.PropertyGrid;
import com.whitehole.smg.BcsvFile;
import com.whitehole.smg.ZoneArchive;

public class Camera extends AbstractObj {
    
    public enum CameraType {
        Cube,
        Game,
        Other,
        Event,
        Start
    }
    
    public Camera(ZoneArchive zone, BcsvFile.Entry entry) {
        this.type = "camera";
        this.zone = zone;
        
        data = entry;
        
        name = (String)data.get("id");
        
        switch (name.charAt(0))
        {
            case 'c':
                cameraType = CameraType.Cube;
                break;
            case 'g':
                cameraType = CameraType.Game;
                break;
            case 'o':
                cameraType = CameraType.Other;
                break;
            case 'e':
                cameraType = CameraType.Event;
                break;
            case 's':
                cameraType = CameraType.Start;
                break;
        }
 
        renderer = null;
        uniqueID = -1;
    }
    
    public Camera(ZoneArchive zone, int game, String objname) {
        this.zone = zone;
        
        data = new BcsvFile.Entry();
        
        name = objname;
        renderer = null;
        uniqueID = -1;
    }
    
    @Override
    public int save() {
        data.put("id", name);
        return 0;
    }

    @Override
    public void getProperties(PropertyGrid panel) {
        panel.addCategory("cam_settings", "Settings");
        panel.addField("camtype", getFieldName("camtype"), "string", null, data.get("camtype"), "Default");
        
        panel.addField("woffset.X", getFieldName("woffset.X"), "float", null, data.get("woffset.X"), "Default");
        panel.addField("woffset.Y", getFieldName("woffset.Y"), "float", null, data.get("woffset.Y"), "Default");
        panel.addField("woffset.Z", getFieldName("woffset.Z"), "float", null, data.get("woffset.Z"), "Default");
        
        panel.addField("loffset", getFieldName("loffset"), "float", null, data.get("loffset"), "Default");
        panel.addField("loffsetv", getFieldName("loffsetv"), "float", null, data.get("loffsetv"), "Default");
        
        panel.addField("roll", getFieldName("roll"), "float", null, data.get("roll"), "Default");
        panel.addField("fovy", getFieldName("fovy"), "float", null, data.get("fovy"), "Default");
        
        panel.addField("camint", getFieldName("camint"), "int", null, data.get("camint"), "Default");
        
        panel.addField("upper", getFieldName("upper"), "float", null, data.get("upper"), "Default");
        panel.addField("lower", getFieldName("fovy"), "float", null, data.get("lower"), "Default");
        
        panel.addField("gdint", getFieldName("gdint"), "int", null, data.get("gdint"), "Default");
        
        panel.addField("uplay", getFieldName("uplay"), "float", null, data.get("uplay"), "Default");
        panel.addField("lplay", getFieldName("lplay"), "float", null, data.get("lplay"), "Default");
        
        panel.addField("pushdelay", getFieldName("pushdelay"), "int", null, data.get("pushdelay"), "Default");
        panel.addField("pushdelaylow", getFieldName("pushdelaylow"), "int", null, data.get("pushdelaylow"), "Default");
        panel.addField("udown", getFieldName("udown"), "int", null, data.get("udown"), "Default");
        panel.addField("vpanuse", getFieldName("vpanuse"), "int", null, data.get("vpanuse"), "Default");
        
        panel.addField("vpanaxis.X", getFieldName("vpanaxis.X"), "float", null, data.get("vpanaxis.X"), "Default");
        panel.addField("vpanaxis.Y", getFieldName("vpanaxis.Y"), "float", null, data.get("vpanaxis.Y"), "Default");
        panel.addField("vpanaxis.Z", getFieldName("vpanaxis.Z"), "float", null, data.get("vpanaxis.Z"), "Default");
        
        // the following are optional
        String flags[] = { "flag.noreset", "flag.nofovy", "flag.lofserpoff", "flag.antibluroff", "flag.collisionoff", "flag.subjectiveoff" };
        
        for (String flag : flags)
        {
            // it is possible for this to not be present
            if (data.containsKey(flag))
            {
                // all of the flags are integers
                panel.addField(flag, getFieldName(flag), "int", null, data.get(flag), "Default");
            }
        }
        
        panel.addField("dist", getFieldName("dist"), "float", null, data.get("dist"), "Default");
        
        panel.addField("axis.X", getFieldName("axis.X"), "float", null, data.get("axis.X"), "Default");
        panel.addField("axis.Y", getFieldName("axis.Y"), "float", null, data.get("axis.Y"), "Default");
        panel.addField("axis.Z", getFieldName("axis.Z"), "float", null, data.get("axis.Z"), "Default");
        
        panel.addField("wpoint.X", getFieldName("wpoint.X"), "float", null, data.get("wpoint.X"), "Default");
        panel.addField("wpoint.Y", getFieldName("wpoint.Y"), "float", null, data.get("wpoint.Y"), "Default");
        panel.addField("wpoint.Z", getFieldName("wpoint.Z"), "float", null, data.get("wpoint.Z"), "Default");
        
        // I haven't seen this used in SMG2, but it still has code for loading it
        // we only check X because it's logical that if X exists, the other two axis exist
        if (data.containsKey("up.X"))
        {
            panel.addField("up.X", getFieldName("up.X"), "float", null, data.get("up.X"), "Default");
            panel.addField("up.Y", getFieldName("up.Y"), "float", null, data.get("up.Y"), "Default");
            panel.addField("up.Z", getFieldName("up.Z"), "float", null, data.get("up.Z"), "Default");
        }
        
        // angleA is not guaranteed to be present, but angleB is
        if (data.containsKey("angleA"))
        {
            panel.addField("angleA", getFieldName("angleA"), "float", null, data.get("angleA"), "Default");
        }
        
        panel.addField("angleB", getFieldName("angleB"), "float", null, data.get("angleB"), "Default");
        
        panel.addField("num1", getFieldName("num1"), "int", null, data.get("num1"), "Default");
        panel.addField("num2", getFieldName("num2"), "int", null, data.get("num2"), "Default");
        
        if (cameraType == CameraType.Event)
        {
            panel.addCategory("cam_event_settings", "Event Settings");
            
            // it is not guaranteed that all of these event flags will be present
            String[] eventSettings = { "eflag.enableErpFrame", "eflag.enableEndErpFrame", "camendint", "evfrm", "evpriority" };
            
            for (String eventSetting : eventSettings)
            {
                if (data.containsKey(eventSetting))
                {
                    panel.addField(eventSetting, getFieldName(eventSetting), "int", null, data.get(eventSetting), "Default");
                }
            }
        }
        
        if (cameraType == CameraType.Game)
        {
            panel.addCategory("cam_game_settings", "Game Settings");

            String[] gameSettings = { "gflag.thru", "gflag.enableEndErpFrame", "gflag.camendint" };
            
            for (String gameSetting : gameSettings)
            {
                if (data.containsKey(gameSetting))
                {
                    panel.addField(gameSetting, getFieldName(gameSetting), "int", null, data.get(gameSetting), "Default");
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return (String)data.get("id");
    }
    
    CameraType cameraType;
}