/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_m_java_2;

import javax.swing.JFrame;

/**
 *
 * @author Nikolas
 */
public class Spielfeld extends javax.swing.JFrame {

    JFrame jfrm_menu;
    JFrame jfrm_einstellungen;
    /**
     * Creates new form Spielfeld
     */
    public Spielfeld(JFrame Menu, JFrame Einstellungen) {
        jfrm_menu = Menu;
        jfrm_einstellungen = Einstellungen;
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

        jbtn_beenden = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jbtn_beenden.setText("Beenden");
        jbtn_beenden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_beendenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(1127, Short.MAX_VALUE)
                .addComponent(jbtn_beenden)
                .addGap(26, 26, 26))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(681, Short.MAX_VALUE)
                .addComponent(jbtn_beenden)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void jbtn_beendenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_beendenActionPerformed
        // TODO add your handling code here:
        jfrm_menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jbtn_beendenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbtn_beenden;
    // End of variables declaration//GEN-END:variables
}