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

package com.whitehole;

import com.whitehole.io.ExternalFile;
import com.whitehole.io.MsbfFile;
import com.whitehole.io.MsbtFile;
import com.whitehole.io.InRarcFile;
import com.whitehole.io.RarcFile;
import com.whitehole.smg.BcsvFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Scraper
{
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        String dir = "D:\\SMGOld\\clean\\DATA\\files\\LocalizeData\\UsEnglish\\MessageData";
        String wantedFile = "msbf";
        
        if(args.length == 2) {
            dir = args[0].toLowerCase();
            wantedFile = args[1].toLowerCase();
        }
        
        File gameDir = new File(dir);
        
        for(File f : gameDir.listFiles()) {
            RarcFile rfs = new RarcFile(new ExternalFile(f.getAbsolutePath()));
            
            for(String s : rfs.getAllFileDirs()) {
                if(!s.endsWith(wantedFile))
                    continue;
                
                InRarcFile rf = (InRarcFile) rfs.openFile(s);
                
                switch(wantedFile) {
                    case "msbf":
                        MsbfFile msbf = new MsbfFile(rf);
                        //msbf.dumpData();
                        break;
                    case "msbt":
                        MsbtFile msbt = new MsbtFile(rf);
                        //msbt.dumpData();
                        break;
                    case "bcsv":
                        BcsvFile bcsv = new BcsvFile(rf);
                        bcsv.dumpData();
                        break;
                }
            }
        }
    }
}