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

package com.whitehole.swing;

import com.whitehole.io.MsbfFile;
import com.whitehole.io.RarcFile;
import java.awt.event.KeyEvent;
import com.whitehole.Whitehole;
import com.whitehole.io.MsbfFile.Flow;
import com.whitehole.io.MsbfFile.FlowEntry;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class MsbfEditorForm extends javax.swing.JFrame {
    
    public MsbfEditorForm(String arcName, String fileName) throws IOException {
        arc = new RarcFile(Whitehole.game.filesystem.openFile(arcName));
        msbf = new MsbfFile(arc.openFile(fileName));
        
        setTitle("Msbf Editor - Editing " + fileName);
        
        // ********
        // UI setup
        // ********
        
        initComponents();
        
        ArrayList<JSpinner> spnList = new ArrayList<>(Arrays.asList(spnUnk0, spnUnk1, spnUnk2, spnUnk3, spnUnk4, spnUnk5, spnIndex));
        for(JSpinner s : spnList) {
            SpinnerModel spnMdl = new SpinnerNumberModel(0, 0, 65535, 1);
            s.setModel(spnMdl);
            JFormattedTextField txt = ((JSpinner.NumberEditor) s.getEditor()).getTextField();
            ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        }
        
        cbxEntryChooser.addActionListener((ActionEvent e) -> {
            reloadEditor(cbxEntryChooser.getSelectedIndex(), false);
        });
        
        setLocationRelativeTo(null);
        setIconImage(Whitehole.ICON);
        
        reloadShorts(msbf.shorts.size() - 1, false);
        reloadEditor(msbf.flowList.size() - 1, false);
    }
    
    private void reloadEditor(int newIndex, boolean deleting) {
        
         if(msbf.flowList.isEmpty()) { // just deleted last flow/entry, no need to save
            cbxEntryChooser.setModel(new DefaultComboBoxModel<>(new String[] {"<empty>"}));
            cbxEntryChooser.setEnabled(false);
            
            txtName.setText("<nothing here>");
            txtName.setEditable(false);
            lblIndex.setVisible(false);
            spnIndex.setVisible(false);
            
            spnUnk0.setEnabled(false);
            spnUnk1.setEnabled(false);
            spnUnk2.setEnabled(false);
            spnUnk3.setEnabled(false);
            spnUnk4.setEnabled(false);
            spnUnk5.setEnabled(false);
        } else {
            if(prevIndex == -2)
                prevIndex = msbf.flowList.size() - 1;
            
            cbxEntryChooser.setEnabled(true);
            spnUnk0.setEnabled(true);
            spnUnk1.setEnabled(true);
            spnUnk2.setEnabled(true);
            spnUnk3.setEnabled(true);
            spnUnk4.setEnabled(true);
            spnUnk5.setEnabled(true);
            
            // populate the model - has name ? name : index
            String[] msgMdl = new String[msbf.flowList.size()];
            for(int i = 0; i < msbf.flowList.size(); i++) {
                Object e = msbf.flowList.get(i);
                if(e instanceof FlowEntry)
                    msgMdl[i] = "[" + i + "] " + ((FlowEntry) e).label;
                else
                    msgMdl[i] = Integer.toString(i);
            }

            cbxEntryChooser.setModel(new DefaultComboBoxModel<>(msgMdl));
            cbxEntryChooser.setSelectedIndex(newIndex);
            
            if(!deleting) {
                try {
                    spnUnk0.commitEdit();
                    spnUnk1.commitEdit();
                    spnUnk2.commitEdit();
                    spnUnk3.commitEdit();
                    spnUnk4.commitEdit();
                    spnUnk5.commitEdit();
                    spnIndex.commitEdit();
                } catch(ParseException e) {
                    e.printStackTrace();
                    return;
                }
                
                Object o = msbf.flowList.get(prevIndex); // TODO del all
                if(o instanceof FlowEntry) {
                    FlowEntry pastEntry = (FlowEntry) o;
                    pastEntry.flow.unk0 = (int) spnUnk0.getValue();
                    pastEntry.flow.unk1 = (int) spnUnk1.getValue();
                    pastEntry.flow.unk2 = (int) spnUnk2.getValue();
                    pastEntry.flow.unk3 = (int) spnUnk3.getValue();
                    pastEntry.flow.unk4 = (int) spnUnk4.getValue();
                    pastEntry.flow.unk5 = (int) spnUnk5.getValue();
                    pastEntry.label = txtName.getText();
                    pastEntry.index = (int) spnIndex.getValue();
                    msbf.flowList.set(prevIndex, pastEntry);
                } else {
                    Flow pastFlow = (Flow) o;
                    pastFlow.unk0 = (int) spnUnk0.getValue();
                    pastFlow.unk1 = (int) spnUnk1.getValue();
                    pastFlow.unk2 = (int) spnUnk2.getValue();
                    pastFlow.unk3 = (int) spnUnk3.getValue();
                    pastFlow.unk4 = (int) spnUnk4.getValue();
                    pastFlow.unk5 = (int) spnUnk5.getValue();
                }
            }

            if(msbf.flowList.get(newIndex) instanceof FlowEntry) {
                FlowEntry entry = (FlowEntry) msbf.flowList.get(newIndex);
                spnUnk0.setValue(entry.flow.unk0);
                spnUnk1.setValue(entry.flow.unk1);
                spnUnk2.setValue(entry.flow.unk2);
                spnUnk3.setValue(entry.flow.unk3);
                spnUnk4.setValue(entry.flow.unk4);
                spnUnk5.setValue(entry.flow.unk5);
                
                if(!updating)
                    txtName.setText(entry.label);
                else
                    updating = false;
                
                txtName.setEditable(true);
                lblIndex.setVisible(true);
                spnIndex.setVisible(true);
                spnIndex.setValue(entry.index);
            } else {
                Flow flow = (Flow) msbf.flowList.get(newIndex);
                spnUnk0.setValue(flow.unk0);
                spnUnk1.setValue(flow.unk1);
                spnUnk2.setValue(flow.unk2);
                spnUnk3.setValue(flow.unk3);
                spnUnk4.setValue(flow.unk4);
                spnUnk5.setValue(flow.unk5);
                txtName.setText("No entry here!");
                txtName.setEditable(false);
                lblIndex.setVisible(false);
                spnIndex.setVisible(false);
                
                System.out.println(flow.unk0);
            }
            
            try {
                spnUnk0.commitEdit();
                spnUnk1.commitEdit();
                spnUnk2.commitEdit();
                spnUnk3.commitEdit();
                spnUnk4.commitEdit();
                spnUnk5.commitEdit();
                spnIndex.commitEdit();
            } catch(ParseException e) {
                e.printStackTrace();
                return;
            }
            
            prevIndex = newIndex;
        }
        pack();
    }
    
    private void reloadShorts(int index, boolean deleting) {
        if(prevShort == -2 && !msbf.shorts.isEmpty())
            prevShort = msbf.shorts.size() - 1;
        
        if(!deleting && !msbf.shorts.isEmpty()) {
            if(txtShort.getText().length() != 0)
                msbf.shorts.set(prevShort, (char) Integer.parseInt(txtShort.getText()));
            else
                msbf.shorts.set(prevShort, (char) 0);
        }
        
        for(int i = 0; i < msbf.shorts.size(); i++) {
            if(msbf.shorts.get(i) > 255)
                msbf.shorts.set(i, (char) 255);
        }
        
        // Set model
        if(msbf.shorts.isEmpty()) {
            cbxShortChooser.setModel(new DefaultComboBoxModel<>(new String[]{"<empty>"}));
            
            txtShort.setText("");
            txtShort.setEnabled(false);
            
            cbxShortChooser.setEnabled(false);
            btnDelShort.setEnabled(false);
            
            index = 0;
        } else {
            txtShort.setEnabled(true);
            
            cbxShortChooser.setEnabled(true);
            btnDelShort.setEnabled(true);
            
            // short value to string
            String[] shortModel = new String[msbf.shorts.size()];
            for(int i = 0; i < msbf.shorts.size(); i++)
                shortModel[i] = "[" + i + "] " + Integer.toString(msbf.shorts.get(i));
            
            cbxShortChooser.setModel(new DefaultComboBoxModel<>(shortModel));
            
            prevShort = index;
            txtShort.setText(Integer.toString(msbf.shorts.get(index)));
        }
        cbxShortChooser.setSelectedIndex(index);
        
        txtShort.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cbxEntryChooser = new javax.swing.JComboBox<>();
        spnUnk0 = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        spnUnk1 = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        spnUnk2 = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        spnUnk3 = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        spnUnk4 = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        spnUnk5 = new javax.swing.JSpinner();
        txtName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnAddEntry = new javax.swing.JButton();
        btnDelEntry = new javax.swing.JButton();
        lblIndex = new javax.swing.JLabel();
        spnIndex = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        cbxShortChooser = new javax.swing.JComboBox<>();
        txtShort = new javax.swing.JTextField();
        btnAddShort = new javax.swing.JButton();
        btnDelShort = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Msbf Editor");
        setName("Msbf Editor"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        cbxEntryChooser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<not initialized>" }));

        jLabel1.setText("unk0");

        jLabel2.setText("unk1");

        jLabel3.setText("unk2");

        jLabel4.setText("unk3");

        jLabel5.setText("unk4");

        jLabel6.setText("unk5");

        txtName.setText("<not initialized>");
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameKeyTyped(evt);
            }
        });

        jLabel7.setText("Name:");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnAddEntry.setText("Add entry");
        btnAddEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEntryActionPerformed(evt);
            }
        });

        btnDelEntry.setText("Delete entry");
        btnDelEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelEntryActionPerformed(evt);
            }
        });

        lblIndex.setText("Index:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDelEntry)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddEntry)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxEntryChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnUnk0, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnUnk1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnUnk2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnUnk3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnUnk4, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnUnk5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblIndex)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxEntryChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnUnk0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(spnUnk1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(spnUnk2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(spnUnk3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(spnUnk4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnUnk5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(lblIndex)
                    .addComponent(spnIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setText("Shorts:");

        cbxShortChooser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<not initialized>" }));
        cbxShortChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxShortChooserActionPerformed(evt);
            }
        });

        txtShort.setText("0");
        txtShort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtShortKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtShortKeyTyped(evt);
            }
        });

        btnAddShort.setText("Add");
        btnAddShort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddShortActionPerformed(evt);
            }
        });

        btnDelShort.setText("Delete");
        btnDelShort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelShortActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddShort))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(txtShort)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelShort))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cbxShortChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(btnAddShort, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxShortChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtShort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelShort, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            msbf.save();
            arc.save();
            arc.close();
            setVisible(false);
            dispose();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEntryActionPerformed
        int x = JOptionPane.showOptionDialog(null, "Add flow or entry?",
                "Choose option",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {"Flow", "Entry"}, "Flow");
        if(x == 1)
            msbf.addEmptyEntry(false);
        else
            msbf.addEmptyEntry(true);
        reloadEditor(msbf.flowList.size() - 1, false);
        
        btnDelEntry.setEnabled(true);
    }//GEN-LAST:event_btnAddEntryActionPerformed

    private void btnDelEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelEntryActionPerformed
        if(msbf.flowList.isEmpty())
            return;
        
        msbf.flowList.remove(cbxEntryChooser.getSelectedIndex());
        reloadEditor(msbf.flowList.size() - 1, true);
        
        if(msbf.flowList.isEmpty())
            btnDelEntry.setEnabled(false);
    }//GEN-LAST:event_btnDelEntryActionPerformed

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        SwingUtilities.invokeLater(() -> {
            final int curPos = txtName.getCaretPosition();

            updating = true;
            int index = cbxEntryChooser.getSelectedIndex();
            String[] msgMdl = new String[msbf.flowList.size()];
            for(int i = 0; i < msbf.flowList.size(); i++) {
                Object e = msbf.flowList.get(i);
                if(i == index) {
                    msgMdl[i] = txtName.getText() + evt.getKeyChar();
                    continue;
                }
                if(e instanceof FlowEntry)
                    msgMdl[i] = ((FlowEntry) e).label;
                else
                    msgMdl[i] = Integer.toString(i);
            }
            cbxEntryChooser.setModel(new DefaultComboBoxModel<>(msgMdl));
            cbxEntryChooser.setSelectedIndex(index);
            
            SwingUtilities.invokeLater(() -> {
                txtName.setCaretPosition(curPos == -1 ? 0 : curPos);
            });
        });
    }//GEN-LAST:event_txtNameKeyTyped

    private void txtShortKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtShortKeyPressed
        SwingUtilities.invokeLater(() -> {
            System.out.println(evt.getKeyCode());
            
            int index = txtShort.getCaretPosition();
            
            reloadShorts(cbxShortChooser.getSelectedIndex(), false);
            
            String txt = txtShort.getText();
            
            if(!txt.equals("0") && index <= txt.length())
                txtShort.setCaretPosition(index);
            else
                txtShort.setCaretPosition(txt.length());
        });
    }//GEN-LAST:event_txtShortKeyPressed

    private void cbxShortChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxShortChooserActionPerformed
        reloadShorts(cbxShortChooser.getSelectedIndex(), false);
    }//GEN-LAST:event_cbxShortChooserActionPerformed

    private void txtShortKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtShortKeyTyped
        if(!(Character.isDigit(evt.getKeyChar())
                || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE || evt.getKeyCode() == KeyEvent.VK_LEFT
                || evt.getKeyCode() == KeyEvent.VK_RIGHT))
            evt.consume();
    }//GEN-LAST:event_txtShortKeyTyped

    private void btnAddShortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddShortActionPerformed
        int index = cbxShortChooser.getSelectedIndex();
        
        msbf.shorts.add(index, (char) 0x00);
        
        reloadShorts(index + (msbf.shorts.size() == 1 ? 0 : 1), false); // if first short was just added, index is 0
    }//GEN-LAST:event_btnAddShortActionPerformed

    private void btnDelShortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelShortActionPerformed
        if(msbf.shorts.isEmpty())
            return;
        
        msbf.shorts.remove(cbxShortChooser.getSelectedIndex());
        
        int index = cbxShortChooser.getSelectedIndex();
        
        reloadShorts(index - (index == 0 ? 0 : 1), true);
    }//GEN-LAST:event_btnDelShortActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            msbf.close();
            arc.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEntry;
    private javax.swing.JButton btnAddShort;
    private javax.swing.JButton btnDelEntry;
    private javax.swing.JButton btnDelShort;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbxEntryChooser;
    private javax.swing.JComboBox<String> cbxShortChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblIndex;
    private javax.swing.JSpinner spnIndex;
    private javax.swing.JSpinner spnUnk0;
    private javax.swing.JSpinner spnUnk1;
    private javax.swing.JSpinner spnUnk2;
    private javax.swing.JSpinner spnUnk3;
    private javax.swing.JSpinner spnUnk4;
    private javax.swing.JSpinner spnUnk5;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtShort;
    // End of variables declaration//GEN-END:variables
    public MsbfFile msbf; RarcFile arc;
    private int prevIndex = -2, prevShort = -2;
    private boolean updating = false;
}
