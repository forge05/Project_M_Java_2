/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_m_java_2;

import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Nikolas
 */
public class Einstellungen extends javax.swing.JFrame {

    
    Menue jfrm_Menu;
    /**
     * Creates new form Einstellungen
     */
    public Einstellungen(Menue Menu) {
        jfrm_Menu = Menu;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg_anz_spieler = new javax.swing.ButtonGroup();
        jpnl_einstellungen = new javax.swing.JPanel();
        tf_spielername_blue = new javax.swing.JTextField();
        jrb_anz_3 = new javax.swing.JRadioButton();
        jcb_cpu_red = new javax.swing.JCheckBox();
        jrb_anz_4 = new javax.swing.JRadioButton();
        jcb_spu_green = new javax.swing.JCheckBox();
        jlbl_anz_spieler = new javax.swing.JLabel();
        jcb_cpu_yellow = new javax.swing.JCheckBox();
        jlbl_spieler1 = new javax.swing.JLabel();
        jcb_cpu_blue = new javax.swing.JCheckBox();
        jlbl_spieler2 = new javax.swing.JLabel();
        jbtn_zurueck = new javax.swing.JButton();
        jlbl_spieler3 = new javax.swing.JLabel();
        jlbl_spieler4 = new javax.swing.JLabel();
        tf_spielername_red = new javax.swing.JTextField();
        tf_spielername_green = new javax.swing.JTextField();
        tf_spielername_yellow = new javax.swing.JTextField();
        jrb_anz_2 = new javax.swing.JRadioButton();
        jbtn_starten = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tf_spielername_blue.setText("Blue");
        tf_spielername_blue.setName("tf_spielername_blue"); // NOI18N

        bg_anz_spieler.add(jrb_anz_3);
        jrb_anz_3.setText("3");
        jrb_anz_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_anz_3ActionPerformed(evt);
            }
        });

        jcb_cpu_red.setText("CPU");
        jcb_cpu_red.setName("jcb_cpu_red"); // NOI18N

        bg_anz_spieler.add(jrb_anz_4);
        jrb_anz_4.setSelected(true);
        jrb_anz_4.setText("4");
        jrb_anz_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_anz_4ActionPerformed(evt);
            }
        });

        jcb_spu_green.setText("CPU");
        jcb_spu_green.setName("jcb_cpu_green"); // NOI18N

        jlbl_anz_spieler.setText("Anzahl Spieler");

        jcb_cpu_yellow.setText("CPU");
        jcb_cpu_yellow.setName("jcb_cpu_yellow"); // NOI18N

        jlbl_spieler1.setText("Spieler 1");

        jcb_cpu_blue.setText("CPU");
        jcb_cpu_blue.setName("jcb_cpu_blue"); // NOI18N

        jlbl_spieler2.setText("Spieler 2");

        jbtn_zurueck.setText("Zurück zum Menü");
        jbtn_zurueck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_zurueckActionPerformed(evt);
            }
        });

        jlbl_spieler3.setText("Spieler 3");

        jlbl_spieler4.setText("Spieler 4");

        tf_spielername_red.setText("Red");
        tf_spielername_red.setName("tf_spielername_red"); // NOI18N

        tf_spielername_green.setText("Green");
        tf_spielername_green.setName("tf_spielername_green"); // NOI18N

        tf_spielername_yellow.setText("Yellow");
        tf_spielername_yellow.setName("tf_spielername_yellow"); // NOI18N

        bg_anz_spieler.add(jrb_anz_2);
        jrb_anz_2.setText("2");
        jrb_anz_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrb_anz_2ActionPerformed(evt);
            }
        });

        jbtn_starten.setText("Spiel starten");
        jbtn_starten.setActionCommand("");
        jbtn_starten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_startenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpnl_einstellungenLayout = new javax.swing.GroupLayout(jpnl_einstellungen);
        jpnl_einstellungen.setLayout(jpnl_einstellungenLayout);
        jpnl_einstellungenLayout.setHorizontalGroup(
            jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnl_einstellungenLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnl_einstellungenLayout.createSequentialGroup()
                        .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jlbl_spieler2)
                            .addComponent(jlbl_spieler1)
                            .addComponent(jlbl_spieler3)
                            .addComponent(jlbl_spieler4))
                        .addGap(18, 18, 18)
                        .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jpnl_einstellungenLayout.createSequentialGroup()
                                .addComponent(tf_spielername_red, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jcb_cpu_red))
                            .addGroup(jpnl_einstellungenLayout.createSequentialGroup()
                                .addComponent(tf_spielername_green, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addComponent(jcb_spu_green))
                            .addGroup(jpnl_einstellungenLayout.createSequentialGroup()
                                .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tf_spielername_yellow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tf_spielername_blue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jcb_cpu_blue)
                                    .addComponent(jcb_cpu_yellow)))))
                    .addGroup(jpnl_einstellungenLayout.createSequentialGroup()
                        .addComponent(jlbl_anz_spieler)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jrb_anz_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrb_anz_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrb_anz_4))
                    .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jbtn_starten, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtn_zurueck, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jpnl_einstellungenLayout.setVerticalGroup(
            jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnl_einstellungenLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrb_anz_2)
                    .addComponent(jrb_anz_3)
                    .addComponent(jrb_anz_4)
                    .addComponent(jlbl_anz_spieler))
                .addGap(26, 26, 26)
                .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbl_spieler1)
                    .addComponent(tf_spielername_red, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcb_cpu_red))
                .addGap(18, 18, 18)
                .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbl_spieler2)
                    .addComponent(tf_spielername_green, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcb_spu_green))
                .addGap(18, 18, 18)
                .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbl_spieler3)
                    .addComponent(tf_spielername_yellow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcb_cpu_yellow))
                .addGap(18, 18, 18)
                .addGroup(jpnl_einstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbl_spieler4)
                    .addComponent(tf_spielername_blue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcb_cpu_blue))
                .addGap(18, 18, 18)
                .addComponent(jbtn_starten)
                .addGap(3, 3, 3)
                .addComponent(jbtn_zurueck)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpnl_einstellungen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jpnl_einstellungen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtn_zurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_zurueckActionPerformed
        // TODO add your handling code here:
        jfrm_Menu.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jbtn_zurueckActionPerformed

    private void jbtn_startenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_startenActionPerformed
        // TODO add your handling code here:
        Frame jfrm_spielfeld = new Spielfeld(jfrm_Menu, this);
        //jfrm_spielfeld.dispose();
        jfrm_spielfeld.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jbtn_startenActionPerformed

    private void jrb_anz_3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrb_anz_3ActionPerformed
        // TODO add your handling code here:
        tf_spielername_blue.setEnabled(false);
        jcb_cpu_blue.setEnabled(false);
        tf_spielername_yellow.setEnabled(true);
        jcb_cpu_yellow.setEnabled(true);
    }//GEN-LAST:event_jrb_anz_3ActionPerformed

    private void jrb_anz_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrb_anz_2ActionPerformed
        // TODO add your handling code here:
        tf_spielername_blue.setEnabled(false);
        jcb_cpu_blue.setEnabled(false);
        tf_spielername_yellow.setEnabled(false);
        jcb_cpu_yellow.setEnabled(false);
    }//GEN-LAST:event_jrb_anz_2ActionPerformed

    private void jrb_anz_4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrb_anz_4ActionPerformed
        // TODO add your handling code here:
        tf_spielername_blue.setEnabled(true);
        jcb_cpu_blue.setEnabled(true);
        tf_spielername_yellow.setEnabled(true);
        jcb_cpu_yellow.setEnabled(true);
    }//GEN-LAST:event_jrb_anz_4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_anz_spieler;
    private javax.swing.JButton jbtn_starten;
    private javax.swing.JButton jbtn_zurueck;
    private javax.swing.JCheckBox jcb_cpu_blue;
    private javax.swing.JCheckBox jcb_cpu_red;
    private javax.swing.JCheckBox jcb_cpu_yellow;
    private javax.swing.JCheckBox jcb_spu_green;
    private javax.swing.JLabel jlbl_anz_spieler;
    private javax.swing.JLabel jlbl_spieler1;
    private javax.swing.JLabel jlbl_spieler2;
    private javax.swing.JLabel jlbl_spieler3;
    private javax.swing.JLabel jlbl_spieler4;
    public javax.swing.JPanel jpnl_einstellungen;
    private javax.swing.JRadioButton jrb_anz_2;
    private javax.swing.JRadioButton jrb_anz_3;
    private javax.swing.JRadioButton jrb_anz_4;
    private javax.swing.JTextField tf_spielername_blue;
    private javax.swing.JTextField tf_spielername_green;
    public javax.swing.JTextField tf_spielername_red;
    private javax.swing.JTextField tf_spielername_yellow;
    // End of variables declaration//GEN-END:variables
}
