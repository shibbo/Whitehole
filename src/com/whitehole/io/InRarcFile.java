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

package com.whitehole.io;

import java.io.IOException;

public class InRarcFile extends MemoryFile {
    public InRarcFile(RarcFile fs, String fullname) throws IOException {
        super(fs.getFileContents(fullname));
        
        filesystem = fs;
        fileName = fullname;
    }
    
    /**
     * Saves the file into its filesystem.
     * @throws IOException 
     */
    @Override
    public void save() throws IOException {
        filesystem.reinsertFile(this);
    }


    public RarcFile filesystem;
    public String fileName;
}
