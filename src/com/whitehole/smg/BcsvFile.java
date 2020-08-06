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

package com.whitehole.smg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import com.whitehole.io.FileBase;

public class BcsvFile  {
    public BcsvFile(FileBase file) throws IOException {
        this.file = file;
        file.setBigEndian(true);
        
        if (file.getLength() == 0) {
            fields = new LinkedHashMap<>();
            entries = new ArrayList<>();
            
            return;
        }

        file.position(0);
        int entrycount = file.readInt();
        int fieldcount = file.readInt();
        int dataoffset = file.readInt();
        int entrydatasize = file.readInt();
        
        fields = new LinkedHashMap<>(fieldcount);
        entries = new ArrayList<>(entrycount);

        int stringtableoffset = (int)(dataoffset + (entrycount * entrydatasize));

        for (int i = 0; i < fieldcount; i++) {
            Field field = new Field();
            file.position(0x10 + (0xC * i));

            field.nameHash = file.readInt();
            field.mask = file.readInt();
            field.entryOffset = file.readShort();
            field.shiftAmount = file.readByte();
            field.type = file.readByte();

            String fieldname = BcsvFile.hashToFieldName(field.nameHash);
            field.name = fieldname;
            fields.put(field.nameHash, field);
        }

        for (int i = 0; i < entrycount; i++) {
            Entry entry = new Entry();

            for (Field field: fields.values()) {
                file.position(dataoffset + (i * entrydatasize) + field.entryOffset);

                Object val = null;
                switch (field.type) {
                    case 0:
                    case 3:
                        val = (int)((file.readInt() & field.mask) >>> field.shiftAmount);
                        break;

                    case 4:
                        val = (short)((file.readShort() & field.mask) >>> field.shiftAmount);
                        break;

                    case 5:
                        val = (byte)((file.readByte() & field.mask) >>> field.shiftAmount);
                        break;

                    case 2:
                        val = file.readFloat();
                        break;

                    case 6:
                        int str_offset = file.readInt();
                        file.position(stringtableoffset + str_offset);
                        val = file.readString("SJIS", 0);
                        break;

                    default:
                        throw new IOException("Bcsv: unsupported data type " + field.type);
                }

                entry.put(field.nameHash, val);
            }

            entries.add(entry);
        }
    }

    public BcsvFile(BcsvFile file) {
         this.entries = file.entries;
         this.fields = file.fields;
    }
    
    public void save() throws IOException {
        int[] datasizes = { 4, -1, 4, 4, 2, 1, 4 };
        int entrysize = 0;

        for (Field field : fields.values()) {
            short fieldend = (short)(field.entryOffset + datasizes[field.type]);
            if (fieldend > entrysize) entrysize = fieldend;
        }
        
        entrysize = ((entrysize + 3) & ~3);

        int dataoffset = (int)(0x10 + (0xC * fields.size()));
        int stringtableoffset = (int)(dataoffset + (entries.size() * entrysize));
        int curstring = 0;

        file.setLength(stringtableoffset);

        file.position(0);
        file.writeInt(entries.size());
        file.writeInt(fields.size());
        file.writeInt(dataoffset);
        file.writeInt(entrysize);

        for (Field field : fields.values()) {
            file.writeInt(field.nameHash);
            file.writeInt(field.mask);
            file.writeShort(field.entryOffset);
            file.writeByte(field.shiftAmount);
            file.writeByte(field.type);
        }

        int i = 0;
        HashMap<String, Integer> stringoffsets = new HashMap<>();

        for (Entry entry : entries) {
            for (Field field : fields.values()) {
                int valoffset = (int)(dataoffset + (i * entrysize) + field.entryOffset);
                file.position(valoffset);

                switch (field.type) {
                    case 0:
                    case 3:
                        {
                            int val = file.readInt();
                            val &= ~field.mask;
                            try {
                                val |= (((int)entry.get(field.nameHash) << field.shiftAmount) & field.mask);
                            } catch (ClassCastException ex) {
                                System.out.println(field.name + " is somehow an int!");
                            }
                            file.position(valoffset);
                            file.writeInt(val);
                        }
                        break;

                    case 4:
                        {
                            short val = file.readShort();
                            val &= (short)(~field.mask);
                            val |= (short)(((short)entry.get(field.nameHash) << field.shiftAmount) & field.mask);

                            file.position(valoffset);
                            file.writeShort(val);
                        }
                        break;

                    case 5:
                        {
                            byte val = file.readByte();
                            val &= (byte)(~field.mask);
                            val |= (byte)(((byte)entry.get(field.nameHash) << field.shiftAmount) & field.mask);

                            file.position(valoffset);
                            file.writeByte(val);
                        }
                        break;

                    case 2:
                        try{
                            file.writeFloat((float)entry.get(field.nameHash));
                        } catch(ClassCastException ex) {
                            System.out.println(field.name + " is a pecking float!!!");
                        }
                        break;

                    case 6:
                        {
                            String val = (String)entry.get(field.nameHash);
                            if (stringoffsets.containsKey(val))
                                file.writeInt(stringoffsets.get(val));
                            else {
                                stringoffsets.put(val, curstring);
                                file.writeInt(curstring);
                                file.position(stringtableoffset + curstring);
                                curstring += file.writeString("SJIS", val, 0);
                            }
                        }
                        break;
                }
            }

            i++;
        }
        
        i = (int)file.getLength();
        file.position(i);
        int aligned_end = (i + 0x1F) & ~0x1F;
        for (; i < aligned_end; i++)
            file.writeByte((byte)0x40);

        file.save();
    }

    public void close() throws IOException {
        file.close();
    }


    public Field addField(String name, int offset, int type, int mask, int shift, Object defaultval) {
        int[] datasizes = { 4, -1, 4, 4, 2, 1, 4 };

        addHash(name); // hehe

        if (type == 2 || type == 6) {
            mask = 0xFFFFFFFF;
            shift = 0;
        }

        if (offset == -1) {
            for (Field field : fields.values()) {
                short fieldend = (short)(field.entryOffset + datasizes[field.type]);
                if (fieldend > offset) offset = fieldend;
            }
        }

        Field newfield = new Field();
        newfield.name = name;
        newfield.nameHash = BcsvFile.fieldNameToHash(name);
        newfield.mask = mask;
        newfield.shiftAmount = (byte)shift;
        newfield.type = (byte)type;
        newfield.entryOffset = (short)offset;
        fields.put(newfield.nameHash, newfield);

        for (Entry entry : entries)
            entry.put(name, defaultval);

        return newfield;
    }

    public void removeField(String name) {
        int hash = BcsvFile.fieldNameToHash(name);
        fields.remove(hash);

        for (Entry entry : entries)
            entry.remove(hash);
    }


    public static class Field {
        public int nameHash;
        public int mask;
        public short entryOffset;
        public byte shiftAmount;
        public byte type;

        public String name;
    }

    public static class Entry extends LinkedHashMap<Integer, Object> {
        public Object get(String key) {
            return get(BcsvFile.fieldNameToHash(key));
        }
        
        public Object getOrDefault(String key, Object val) {
            return getOrDefault(BcsvFile.fieldNameToHash(key), val);
        }
        
        public void put(String key, Object val) {
            put(BcsvFile.fieldNameToHash(key), val);
        }

        public boolean containsKey(String key) {
            return this.containsKey(BcsvFile.fieldNameToHash(key));
        }
    }


    private FileBase file;
    public LinkedHashMap<Integer, Field> fields;
    public List<Entry> entries;
    
    public void dumpData()
    {
        for(Entry e : entries)
        {
            System.out.println("new entry");
            
            for(Object o : e.values()) {
                System.out.println("entry val = " + o.toString());
            }
            
            System.out.println("\n\n");
        }
    }
    
    public static int fieldNameToHash(String field) {
        int ret = 0;
        for (char ch : field.toCharArray()) {
            ret *= 0x1F;
            ret += ch;
        }
        return ret;
    }

    public static String hashToFieldName(int hash) {
        if (!LOOKUP.containsKey(hash))
            return String.format("[%1$08X]", hash);

        return LOOKUP.get(hash);
    }

    public static void addHash(String field) {
        int hash = fieldNameToHash(field);
        if (!LOOKUP.containsKey(hash))
            LOOKUP.put(hash, field);
    }
    
    public static void populateHashTable() {
        try {
            if (!LOOKUPFILE.exists()) {
                LOOKUPFILE.createNewFile();
                return;
            }
            
            try (BufferedReader br = new BufferedReader(new FileReader(LOOKUPFILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    
                    if (line.length() == 0)
                        continue;
                    if (line.charAt(0) == '#')
                        continue;
                    
                    addHash(line);
                }
            }
        }
        catch (IOException ex) {}
    }
    
    public static final File LOOKUPFILE = new File("hashlookup.txt");
    public static final HashMap<Integer, String> LOOKUP = new HashMap();
}