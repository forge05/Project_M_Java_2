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
public class Menue extends javax.swing.JFrame {

    JFrame jfrm_einstellungen;
    /**
     * Creates new form Menue
     */
    public Menue() {
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

        jbtn_neuesSpiel = new javax.swing.JButton();
        jbtn_einstellungen = new javax.swing.JButton();
        jbtn_beenden = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jbtn_neuesSpiel.setLabel("Neues Spiel");
        jbtn_neuesSpiel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_neuesSpielActionPerformed(evt);
            }
        });

        jbtn_einstellungen.setLabel("Einstellungen");
        jbtn_einstellungen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_einstellungenActionPerformed(evt);
            }
        });

        jbtn_beenden.setLabel("Beenden");
        jbtn_beenden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_beendenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(124, 124, 124)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbtn_einstellungen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtn_beenden, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtn_neuesSpiel, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(147, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtn_neuesSpiel)
                .addGap(28, 28, 28)
                .addComponent(jbtn_einstellungen)
                .addGap(28, 28, 28)
                .addComponent(jbtn_beenden)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jbtn_neuesSpiel.getAccessibleContext().setAccessibleName("jbtn_neuesSpiel");
        jbtn_einstellungen.getAccessibleContext().setAccessibleName("jbtn_einstellungen");
        jbtn_beenden.getAccessibleContext().setAccessibleName("jbtn_beenden");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtn_beendenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_beendenActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtn_beendenActionPerformed

    private void jbtn_einstellungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_einstellungenActionPerformed
        // TODO add your handling code here:
        jfrm_einstellungen = new Einstellungen(this);
        jfrm_einstellungen.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jbtn_einstellungenActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Menue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menue().setVisible(true);
            }
        });
    }
    
    private void jbtn_neuesSpielActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_neuesSpielActionPerformed
        // TODO add your handling code here:
        JFrame jfrm_spielfeld = new Spielfeld(this, jfrm_einstellungen);
        jfrm_spielfeld.dispose();
        jfrm_spielfeld.setVisible(true);
        this.setVisible(false);
        
    }//GEN-LAST:event_jbtn_neuesSpielActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbtn_beenden;
    private javax.swing.JButton jbtn_einstellungen;
    private javax.swing.JButton jbtn_neuesSpiel;
    // End of variables declaration//GEN-END:variables
}
