/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_m_java_2;

import java.awt.Color;
import java.awt.List;
import java.time.Clock;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;

/**
 *
 * @author Nikolas
 */
public class Spielfeld extends javax.swing.JFrame {

    Menue jfrm_menu;
    Einstellungen jfrm_einstellungen;
    int wurfzahl;
    int spielerAnzahl;
    CopyOnWriteArrayList<Player> player;                            //ArrayList wirft ConcurrentModificationError. Um nur einem Thread Zugriff zu gestatten benutzen wir CopyOnWriteArrayList
    ListIterator<Player> it;
    String playerName1;
    String playerName2;
    String playerName3;
    String playerName4;
    boolean cpu1 = false;
    boolean cpu2 = false;
    boolean cpu3 = false;
    boolean cpu4 = false;
    boolean schongewuerfelt = false;
    boolean blockZuSetzen = false;
    boolean someoneWon = false;
    Player spieler_red;
    Player spieler_green;
    Player spieler_yellow;
    Player spieler_blue;
    Player an_der_Reihe;
    Feld propagierender;

    /**
     * Creates new form Spielfeld
     */
    public Spielfeld(Menue Menu, Einstellungen einstellungen) {
        jfrm_menu = Menu;
        jfrm_einstellungen = einstellungen;                      //radioButtons einfügen um Auswahl zu treffen, wer anfangen darf/soll
        player = new CopyOnWriteArrayList();
        it = player.listIterator(0);                            //int index beginnt ab position index
        initComponents();
        nachbarn();
        initPlayers();
        //an_der_Reihe = spieler_red; 
        nextPlayer();
    }

    private void initPlayers() {
        for (Object c : jfrm_einstellungen.jpnl_einstellungen.getComponents()) {
            if (c.getClass() == JRadioButton.class) {
                JRadioButton jrb = (JRadioButton) c;
                if (jrb.isSelected()) {
                    spielerAnzahl = Integer.parseInt(jrb.getText());
                }
            } else if (c.getClass() == JTextField.class) {
                JTextField jtf = (JTextField) c;
                //String abc = new String();
                //abc += jtf.getName();
                if (jtf.getName().equals("tf_spielername_red")) {
                    playerName1 = jtf.getText();
                }
                if (jtf.getName().equals("tf_spielername_green")) {
                    playerName2 = jtf.getText();
                }
                if (jtf.getName().equals("tf_spielername_yellow")) {
                    playerName3 = jtf.getText();
                }
                if (jtf.getName().equals("tf_spielername_blue")) {
                    playerName4 = jtf.getText();
                }
            } else if (c.getClass() == JCheckBox.class) {
                JCheckBox jcb = (JCheckBox) c;
                if (jcb.getName().equals("jcb_cpu_red")) {
                    if (jcb.isSelected()) {
                        cpu1 = true;
                    }
                }
                if (jcb.getName().equals("jcb_cpu_green")) {
                    if (jcb.isSelected()) {
                        cpu2 = true;
                    }
                }
                if (jcb.getName().equals("jcb_cpu_yellow")) {
                    if (jcb.isSelected() && jcb.isEnabled()) {
                        cpu3 = true;
                    }
                }
                if (jcb.getName().equals("jcb_cpu_blue")) {
                    if (jcb.isSelected() && jcb.isEnabled()) {
                        cpu4 = true;
                    }
                }
            }

            // }
            // }
            //}
            //}
        }
        
        //Player anlegen und Startfelder ggf. enablen
        
        if (cpu1) {
            spieler_red = new CPU(playerName1, Feld.content.RED);
        } else {
            spieler_red = new Spieler(playerName1, Feld.content.RED);
        }
        player.add(spieler_red);

        if (cpu2) {
            spieler_green = new CPU(playerName2, Feld.content.GREEN);
        } else {
            spieler_green = new Spieler(playerName2, Feld.content.GREEN);
        }
        player.add(spieler_green);
        if (spielerAnzahl >= 3) {

            if (cpu3) {
                spieler_yellow = new CPU(playerName3, Feld.content.YELLOW);
            } else {
                spieler_yellow = new Spieler(playerName3, Feld.content.YELLOW);
            }
            player.add(spieler_yellow);
            playerButtonsEnablen(spieler_yellow.spielerFarbe);
            if (spielerAnzahl >= 4) {

                if (cpu4) {
                    spieler_blue = new CPU(playerName4, Feld.content.BLUE);
                } else {
                    spieler_blue = new Spieler(playerName4, Feld.content.BLUE);
                }
                player.add(spieler_blue);
                playerButtonsEnablen(spieler_blue.spielerFarbe);
            }
        }
    }

    private void propagiereRueckOptionen(Feld aktuellesFeld, int spruenge, Feld altesFeld, Feld.content spielerContent) {
        if (spruenge != 0) {
            if (aktuellesFeld.inhalt != Feld.content.BLOCK) {
                for (Feld nachbar : aktuellesFeld.nachbarn) {
                    if (nachbar != altesFeld) {
                        if (nachbar.getClass() != Startfeld.class) {
                            propagiereRueckOptionen(nachbar, spruenge - 1, aktuellesFeld, spielerContent);
                        }

                    }
                }
            }
        } else {
            if (aktuellesFeld.inhalt != spielerContent) {                                       //eigene Felder werden nicht gefärbt. Man kann also nicht auf eigene Figuren rücken
                aktuellesFeld.setBackground(Color.CYAN);
            }
            if (aktuellesFeld.inhalt == Feld.content.BLOCK) {
                aktuellesFeld.setText("BLOCK");
            }
            if (aktuellesFeld.inhalt.getStelle() <= spielerAnzahl && aktuellesFeld.inhalt != spielerContent) {
                //aktuellesFeld.setText("Gegner");

                switch (aktuellesFeld.inhalt) {
                    case RED:
                        //aktuellesFeld.setForeground(Color.RED);
                        aktuellesFeld.setText("Red");
                        break;
                    case GREEN:
                        //aktuellesFeld.setForeground(Color.GREEN);
                        aktuellesFeld.setText("Green");
                        break;
                    case YELLOW:
                        //aktuellesFeld.setForeground(Color.YELLOW);
                        aktuellesFeld.setText("Yellow");
                        break;
                    case BLUE:
                        //aktuellesFeld.setForeground(Color.BLUE);
                        aktuellesFeld.setText("Blue");
                        break;
                }
            }
            if (aktuellesFeld.inhalt == Feld.content.GOAL) {
                aktuellesFeld.setText("Ziel!");
            }
        }
    }

    private void nextPlayer() {
        if (it.hasNext()) {
            an_der_Reihe = (Player) it.next();
            //System.out.println("Spieler " + an_der_Reihe.spielerFarbe + ". Sie sind an der Reihe");
            jlbl_anDerReihe.setText("Spieler " + an_der_Reihe.spielerName + ": Bitte würfeln Sie.");
        } else {
            it = player.listIterator();
            an_der_Reihe = it.next();
            //System.out.println("Spieler " + an_der_Reihe.spielerFarbe + ". Sie sind an der Reihe");
            jlbl_anDerReihe.setText("Spieler " + an_der_Reihe.spielerName + ": Bitte würfeln Sie.");
        }
        jlbl_wuerfelzahl.setText("");                           //ist eigentlich bereits abgefangen, sieht aber für den Spieler besser aus
        schongewuerfelt = false;
        jbtn_wuerfeln.setEnabled(true);
        jbtn_aussetzen.setEnabled(false);
        propagiereZuruecksetzen();
        //playerButtonsDisablen();
        //playerButtonsEnablen();
    }

    private void playerButtonsDisablen() {
        for (Object c : jpnl_alleFelder.getComponents()) {
            if (c.getClass() == Startfeld.class) {
                Startfeld startfeld = (Startfeld) c;
                if (startfeld.inhalt != an_der_Reihe.spielerFarbe) {
                    startfeld.setEnabled(false);
                }
            }
        }
    }

    private void playerButtonsEnablen(Feld.content playerColor) {
        for (Object c : jpnl_alleFelder.getComponents()) {
            if (c.getClass() == Startfeld.class) {
                Startfeld startfeld = (Startfeld) c;
                if (startfeld.inhalt == playerColor){// && !startfeld.schonGeruecktWorden) {
                    startfeld.setEnabled(true);
                }
            }
        }
    }

    private void propagiereZuruecksetzen() {
        for (Object c : jpnl_alleFelder.getComponents()) {
            if (c.getClass() == Feld.class) {
                Feld feld = (Feld) c;

                switch (feld.inhalt) {
                    case RED:
                        feld.setBackground(Color.RED);
                        feld.setText("");
                        feld.setForeground(Color.BLACK);
                        break;

                    case GREEN:
                        feld.setBackground(Color.GREEN);
                        feld.setText("");
                        feld.setForeground(Color.BLACK);
                        break;
                    case YELLOW:
                        feld.setBackground(Color.YELLOW);
                        feld.setText("");
                        feld.setForeground(Color.BLACK);
                        break;
                    case BLUE:
                        feld.setBackground(Color.BLUE);
                        feld.setText("");
                        feld.setForeground(Color.BLACK);
                        break;
                    case BLACK:
                        feld.setBackground(Color.BLACK);
                        feld.setText("");
                        feld.setForeground(Color.BLACK);
                        break;
                    case BLOCK:
                        feld.setBackground(Color.WHITE);
                        feld.setText("");
                        feld.setForeground(Color.BLACK);
                        break;
                    case GOAL:
                        feld.setBackground(Color.MAGENTA);
                        break;
                }

            }
        }
    }

    private void ruecken(Feld propTer, Feld propDer) {
        Feld.content ursprungscontent = propTer.inhalt;
        propTer.inhalt = propDer.inhalt;
        propTer.setBackground(propDer.getBackground());
        if (propDer.getClass() == Startfeld.class) {
            propDer.setEnabled(false);
            //Startfeld sf = (Startfeld) propDer;
            propDer.schonGeruecktWorden = true;
        } else {
            propDer.inhalt = Feld.content.BLACK;
            propDer.setBackground(Color.BLACK);
        }

        switch (ursprungscontent) {                       //eigene Figuren können nicht geschlagen werden
            case RED:
                schlagen(ursprungscontent);
                break;
            case GREEN:
                schlagen(ursprungscontent);
                break;
            case YELLOW:
                schlagen(ursprungscontent);
                break;
            case BLUE:
                schlagen(ursprungscontent);
                break;
            case BLOCK:
                jlbl_anDerReihe.setText("Spieler " + an_der_Reihe.spielerName + ": Bitte Block setzen. Hinweis: unterste Reihe tabu.");
                blockZuSetzen = true;
                jbtn_wuerfeln.setEnabled(false);
                jbtn_aussetzen.setEnabled(false);
                break;
            case GOAL:
                gewinnen();
                break;
        }

        propagiereZuruecksetzen();

    }

    private void schlagen(Feld.content geschlagenerInhalt) {
        for (Object c : jpnl_alleFelder.getComponents()) {
            if (c.getClass() == Startfeld.class) {
                Startfeld startfeld = (Startfeld) c;
                if (startfeld.inhalt == geschlagenerInhalt) {
                    if (!startfeld.isEnabled() && startfeld.schonGeruecktWorden) {
                        startfeld.setEnabled(true);
                        startfeld.schonGeruecktWorden = false;
                        break;
                    }

                }
            }
        }
    }

    private void blockSetzen(Feld wirdBlock) {
        if (wirdBlock.entfernung_zum_ziel <= 36) {
            if (wirdBlock.inhalt == Feld.content.BLACK) {
                wirdBlock.inhalt = Feld.content.BLOCK;
                wirdBlock.setBackground(Color.WHITE);
                blockZuSetzen = false;
                //jbtn_wuerfeln.setEnabled(true);
                //jbtn_aussetzen.setEnabled(true);
                nextPlayer();
            }
        }
    }

    private void gewinnen() {
        //playerButtonsDisablen();
        someoneWon = true;
        jbtn_wuerfeln.setEnabled(false);
        jbtn_aussetzen.setEnabled(false);
        jlbl_anDerReihe.setText("Spieler " + an_der_Reihe.spielerName + ": Sie haben gewonnen!");
    }

    private void nachbarn() {
        //weise nachbarn zu
        jbtn_0_ziel.setNachbar(jbtn_1);
        jbtn_1.setNachbar(jbtn_0_ziel, jbtn_2_1, jbtn_2_2);
        jbtn_2_1.setNachbar(jbtn_1, jbtn_3_1);
        jbtn_2_2.setNachbar(jbtn_1, jbtn_3_2);
        jbtn_3_1.setNachbar(jbtn_2_1, jbtn_4_1);
        jbtn_3_2.setNachbar(jbtn_2_2, jbtn_4_2);
        jbtn_4_1.setNachbar(jbtn_3_1, jbtn_5_1);
        jbtn_4_2.setNachbar(jbtn_3_2, jbtn_5_2);
        jbtn_5_1.setNachbar(jbtn_4_1, jbtn_6_1);
        jbtn_5_2.setNachbar(jbtn_4_2, jbtn_6_2);
        jbtn_6_1.setNachbar(jbtn_5_1, jbtn_7_1);
        jbtn_6_2.setNachbar(jbtn_5_2, jbtn_7_2);
        jbtn_7_1.setNachbar(jbtn_6_1, jbtn_8_1);
        jbtn_7_2.setNachbar(jbtn_6_2, jbtn_8_2);
        jbtn_8_1.setNachbar(jbtn_7_1, jbtn_9_1);
        jbtn_8_2.setNachbar(jbtn_7_2, jbtn_9_2);
        jbtn_9_1.setNachbar(jbtn_8_1, jbtn_10_1);
        jbtn_9_2.setNachbar(jbtn_8_2, jbtn_10_2);
        jbtn_10_1.setNachbar(jbtn_9_1, jbtn_11_1);
        jbtn_10_2.setNachbar(jbtn_9_2, jbtn_11_2);
        jbtn_11_1.setNachbar(jbtn_10_1, jbtn_12_1);
        jbtn_11_2.setNachbar(jbtn_10_2, jbtn_12_2);
        jbtn_12_1.setNachbar(jbtn_11_1, jbtn_13_1);
        jbtn_12_2.setNachbar(jbtn_11_2, jbtn_13_2);
        jbtn_13_1.setNachbar(jbtn_12_1, jbtn_14_1);
        jbtn_13_2.setNachbar(jbtn_12_2, jbtn_14_2);
        jbtn_14_1.setNachbar(jbtn_13_1, jbtn_15_1);
        jbtn_14_2.setNachbar(jbtn_13_2, jbtn_15_2);
        jbtn_15_1.setNachbar(jbtn_14_1, jbtn_16_1);
        jbtn_15_2.setNachbar(jbtn_14_2, jbtn_16_2);
        jbtn_16_1.setNachbar(jbtn_15_1, jbtn_17_1);
        jbtn_16_2.setNachbar(jbtn_15_2, jbtn_17_2);
        jbtn_17_1.setNachbar(jbtn_16_1, jbtn_18_1);
        jbtn_17_2.setNachbar(jbtn_16_2, jbtn_18_2);
        jbtn_18_1.setNachbar(jbtn_17_1, jbtn_19_1);
        jbtn_18_2.setNachbar(jbtn_17_2, jbtn_19_1);
        jbtn_19_1.setNachbar(jbtn_18_1, jbtn_18_2, jbtn_20_1);
        jbtn_20_1.setNachbar(jbtn_19_1, jbtn_21_1);
        jbtn_21_1.setNachbar(jbtn_20_1, jbtn_22_1, jbtn_22_2);
        jbtn_22_1.setNachbar(jbtn_21_1, jbtn_23_1);
        jbtn_22_2.setNachbar(jbtn_21_1, jbtn_23_2);
        jbtn_23_1.setNachbar(jbtn_22_1, jbtn_24_1);
        jbtn_23_2.setNachbar(jbtn_22_2, jbtn_24_2);
        jbtn_24_1.setNachbar(jbtn_23_1, jbtn_25_1);
        jbtn_24_2.setNachbar(jbtn_23_2, jbtn_25_2);
        jbtn_25_1.setNachbar(jbtn_24_1, jbtn_26_1, jbtn_26_2);
        jbtn_25_2.setNachbar(jbtn_24_2, jbtn_26_3, jbtn_26_4);
        jbtn_26_1.setNachbar(jbtn_25_1, jbtn_27_1);
        jbtn_26_2.setNachbar(jbtn_25_1, jbtn_27_2);
        jbtn_26_3.setNachbar(jbtn_25_2, jbtn_27_2);
        jbtn_26_4.setNachbar(jbtn_25_2, jbtn_27_3);
        jbtn_27_1.setNachbar(jbtn_26_1, jbtn_28_1);
        jbtn_27_2.setNachbar(jbtn_26_2, jbtn_26_3);
        jbtn_27_3.setNachbar(jbtn_26_4, jbtn_28_2);
        jbtn_28_1.setNachbar(jbtn_27_1, jbtn_29_1);
        jbtn_28_2.setNachbar(jbtn_27_3, jbtn_29_2);
        jbtn_29_1.setNachbar(jbtn_28_1, jbtn_30_1, jbtn_30_2);
        jbtn_29_2.setNachbar(jbtn_28_2, jbtn_30_3, jbtn_30_4);
        jbtn_30_1.setNachbar(jbtn_29_1, jbtn_31_1);
        jbtn_30_2.setNachbar(jbtn_29_1, jbtn_31_2);
        jbtn_30_3.setNachbar(jbtn_29_2, jbtn_31_3);
        jbtn_30_4.setNachbar(jbtn_29_2, jbtn_31_4);
        jbtn_31_1.setNachbar(jbtn_30_1, jbtn_32_1);
        jbtn_31_2.setNachbar(jbtn_30_2, jbtn_32_2, jbtn_32_3);
        jbtn_31_3.setNachbar(jbtn_30_3, jbtn_32_4, jbtn_32_5);
        jbtn_31_4.setNachbar(jbtn_30_4, jbtn_32_6);
        jbtn_32_1.setNachbar(jbtn_31_1, jbtn_33_1);
        jbtn_32_2.setNachbar(jbtn_31_2, jbtn_33_2);
        jbtn_32_3.setNachbar(jbtn_31_2, jbtn_33_3);
        jbtn_32_4.setNachbar(jbtn_31_3, jbtn_33_3);
        jbtn_32_5.setNachbar(jbtn_31_3, jbtn_33_4);
        jbtn_32_6.setNachbar(jbtn_31_4, jbtn_33_5);
        jbtn_33_1.setNachbar(jbtn_32_1, jbtn_34_1, jbtn_34_2);
        jbtn_33_2.setNachbar(jbtn_32_2, jbtn_34_3, jbtn_34_4);
        jbtn_33_3.setNachbar(jbtn_32_3, jbtn_32_4);
        jbtn_33_4.setNachbar(jbtn_32_5, jbtn_34_5, jbtn_34_6);
        jbtn_33_5.setNachbar(jbtn_32_6, jbtn_34_7, jbtn_34_8);
        jbtn_34_1.setNachbar(jbtn_33_1, jbtn_35_1);
        jbtn_34_2.setNachbar(jbtn_33_1, jbtn_35_2);
        jbtn_34_3.setNachbar(jbtn_33_2, jbtn_35_2);
        jbtn_34_4.setNachbar(jbtn_33_2, jbtn_35_3);
        jbtn_34_5.setNachbar(jbtn_33_4, jbtn_35_3);
        jbtn_34_6.setNachbar(jbtn_33_4, jbtn_35_4);
        jbtn_34_7.setNachbar(jbtn_33_5, jbtn_35_4);
        jbtn_34_8.setNachbar(jbtn_33_5, jbtn_35_5);
        jbtn_35_1.setNachbar(jbtn_34_1, jbtn_36_1);
        jbtn_35_2.setNachbar(jbtn_34_2, jbtn_34_3, jbtn_36_2);
        jbtn_35_3.setNachbar(jbtn_34_4, jbtn_34_5, jbtn_36_3);
        jbtn_35_4.setNachbar(jbtn_34_6, jbtn_34_7, jbtn_36_4);
        jbtn_35_5.setNachbar(jbtn_34_8, jbtn_36_5);
        jbtn_36_1.setNachbar(jbtn_35_1, jbtn_37_1);
        jbtn_36_2.setNachbar(jbtn_35_2, jbtn_37_2);
        jbtn_36_3.setNachbar(jbtn_35_3, jbtn_37_3);
        jbtn_36_4.setNachbar(jbtn_35_4, jbtn_37_4);
        jbtn_36_5.setNachbar(jbtn_35_5, jbtn_37_5);
        jbtn_37_1.setNachbar(jbtn_36_1, jbtn_38_1);
        jbtn_37_2.setNachbar(jbtn_36_2, jbtn_38_2, jbtn_38_3);
        jbtn_37_3.setNachbar(jbtn_36_3, jbtn_38_4, jbtn_38_5);
        jbtn_37_4.setNachbar(jbtn_36_4, jbtn_38_6, jbtn_38_7);
        jbtn_37_5.setNachbar(jbtn_36_5, jbtn_38_8);
        jbtn_38_1.setNachbar(jbtn_37_1, jbtn_39_1);
        jbtn_38_2.setNachbar(jbtn_37_2, jbtn_39_1);
        jbtn_38_3.setNachbar(jbtn_37_2, jbtn_39_2);
        jbtn_38_4.setNachbar(jbtn_37_3, jbtn_39_2);
        jbtn_38_5.setNachbar(jbtn_37_3, jbtn_39_3);
        jbtn_38_6.setNachbar(jbtn_37_4, jbtn_39_3);
        jbtn_38_7.setNachbar(jbtn_37_4, jbtn_39_4);
        jbtn_38_8.setNachbar(jbtn_37_5, jbtn_39_4);
        jbtn_39_1.setNachbar(jbtn_38_1, jbtn_38_2, jbtn_40_red_1, jbtn_40_red_2, jbtn_40_red_3, jbtn_40_red_4, jbtn_40_red_5);
        jbtn_39_2.setNachbar(jbtn_38_3, jbtn_38_4, jbtn_40_green_1, jbtn_40_green_2, jbtn_40_green_3, jbtn_40_green_4, jbtn_40_green_5);
        jbtn_39_3.setNachbar(jbtn_38_5, jbtn_38_6, jbtn_40_yellow_1, jbtn_40_yellow_2, jbtn_40_yellow_3, jbtn_40_yellow_4, jbtn_40_yellow_5);
        jbtn_39_4.setNachbar(jbtn_38_7, jbtn_38_8, jbtn_40_blue_1, jbtn_40_blue_2, jbtn_40_blue_3, jbtn_40_blue_4, jbtn_40_blue_5);
        jbtn_40_red_1.setNachbar(jbtn_39_1);
        jbtn_40_red_2.setNachbar(jbtn_39_1);
        jbtn_40_red_3.setNachbar(jbtn_39_1);
        jbtn_40_red_4.setNachbar(jbtn_39_1);
        jbtn_40_red_5.setNachbar(jbtn_39_1);
        jbtn_40_green_1.setNachbar(jbtn_39_2);
        jbtn_40_green_2.setNachbar(jbtn_39_2);
        jbtn_40_green_3.setNachbar(jbtn_39_2);
        jbtn_40_green_4.setNachbar(jbtn_39_2);
        jbtn_40_green_5.setNachbar(jbtn_39_2);
        jbtn_40_yellow_1.setNachbar(jbtn_39_3);
        jbtn_40_yellow_2.setNachbar(jbtn_39_3);
        jbtn_40_yellow_3.setNachbar(jbtn_39_3);
        jbtn_40_yellow_4.setNachbar(jbtn_39_3);
        jbtn_40_yellow_5.setNachbar(jbtn_39_3);
        jbtn_40_blue_1.setNachbar(jbtn_39_4);
        jbtn_40_blue_2.setNachbar(jbtn_39_4);
        jbtn_40_blue_3.setNachbar(jbtn_39_4);
        jbtn_40_blue_4.setNachbar(jbtn_39_4);
        jbtn_40_blue_5.setNachbar(jbtn_39_4);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpnl_alleFelder = new javax.swing.JPanel();
        jbtn_34_5 = new Feld(Feld.content.BLACK, 34);
        jbtn_33_4 = new Feld(Feld.content.BLACK, 33);
        jbtn_36_4 = new Feld(Feld.content.BLACK, 36);
        jbtn_32_5 = new Feld(Feld.content.BLACK, 32);
        jbtn_36_3 = new Feld(Feld.content.BLACK, 36);
        jbtn_32_2 = new Feld(Feld.content.BLACK, 32);
        jbtn_36_2 = new Feld(Feld.content.BLACK, 36);
        jbtn_40_red_1 = new Startfeld(Feld.content.RED);
        jbtn_40_red_2 = new Startfeld(Feld.content.RED);
        jbtn_34_6 = new Feld(Feld.content.BLACK, 34);
        jbtn_40_red_3 = new Startfeld(Feld.content.RED);
        jbtn_35_4 = new Feld(Feld.content.BLOCK, 35);
        jbtn_40_red_5 = new Startfeld(Feld.content.RED);
        jbtn_34_7 = new Feld(Feld.content.BLACK, 34);
        jbtn_40_red_4 = new Startfeld(Feld.content.RED);
        jbtn_36_5 = new Feld(Feld.content.BLACK, 36);
        jbtn_37_5 = new Feld(Feld.content.BLACK, 37);
        jbtn_38_1 = new Feld(Feld.content.BLACK, 38);
        jbtn_39_1 = new Feld(Feld.content.BLACK, 39);
        jbtn_38_2 = new Feld(Feld.content.BLACK, 38);
        jbtn_3_1 = new Feld(Feld.content.BLACK, 3);
        jbtn_37_2 = new Feld(Feld.content.BLACK, 37);
        jbtn_2_2 = new Feld(Feld.content.BLACK, 2);
        jbtn_38_3 = new Feld(Feld.content.BLACK, 38);
        jbtn_7_1 = new Feld(Feld.content.BLACK, 7);
        jbtn_9_1 = new Feld(Feld.content.BLACK, 9);
        jbtn_3_2 = new Feld(Feld.content.BLACK, 3);
        jbtn_4_2 = new Feld(Feld.content.BLACK, 4);
        jbtn_5_2 = new Feld(Feld.content.BLACK, 5);
        jbtn_6_2 = new Feld(Feld.content.BLACK, 6);
        jbtn_7_2 = new Feld(Feld.content.BLACK, 7);
        jbtn_8_2 = new Feld(Feld.content.BLACK, 8);
        jbtn_40_green_1 = new Startfeld(Feld.content.GREEN);
        jbtn_40_green_2 = new Startfeld(Feld.content.GREEN);
        jbtn_40_green_3 = new Startfeld(Feld.content.GREEN);
        jbtn_40_green_5 = new Startfeld(Feld.content.GREEN);
        jbtn_0_ziel = new Feld(Feld.content.GOAL, 0);
        jbtn_40_green_4 = new Startfeld(Feld.content.GREEN);
        jbtn_2_1 = new Feld(Feld.content.BLACK, 2);
        jbtn_40_yellow_1 = new Startfeld(Feld.content.YELLOW);
        jbtn_1 = new Feld(Feld.content.BLOCK, 1);
        jbtn_40_yellow_2 = new Startfeld(Feld.content.YELLOW);
        jbtn_4_1 = new Feld(Feld.content.BLACK, 4);
        jbtn_40_yellow_3 = new Startfeld(Feld.content.YELLOW);
        jbtn_8_1 = new Feld(Feld.content.BLACK, 8);
        jbtn_40_yellow_5 = new Startfeld(Feld.content.YELLOW);
        jbtn_6_1 = new Feld(Feld.content.BLACK, 6);
        jbtn_40_yellow_4 = new Startfeld(Feld.content.YELLOW);
        jbtn_5_1 = new Feld(Feld.content.BLACK, 5);
        jbtn_9_2 = new Feld(Feld.content.BLACK, 9);
        jbtn_10_1 = new Feld(Feld.content.BLACK, 10);
        jbtn_10_2 = new Feld(Feld.content.BLACK, 10);
        jbtn_11_1 = new Feld(Feld.content.BLACK, 11);
        jbtn_12_1 = new Feld(Feld.content.BLACK, 12);
        jbtn_13_1 = new Feld(Feld.content.BLACK, 13);
        jbtn_14_1 = new Feld(Feld.content.BLACK, 14);
        jbtn_15_1 = new Feld(Feld.content.BLACK, 15);
        jbtn_16_1 = new Feld(Feld.content.BLACK, 16);
        jbtn_17_1 = new Feld(Feld.content.BLACK, 17);
        jbtn_40_blue_1 = new Startfeld(Feld.content.BLUE);
        jbtn_40_blue_2 = new Startfeld(Feld.content.BLUE);
        jbtn_40_blue_3 = new Startfeld(Feld.content.BLUE);
        jbtn_40_blue_5 = new Startfeld(Feld.content.BLUE);
        jbtn_40_blue_4 = new Startfeld(Feld.content.BLUE);
        jbtn_18_1 = new Feld(Feld.content.BLACK, 18);
        jbtn_19_1 = new Feld(Feld.content.BLOCK, 19);
        jbtn_18_2 = new Feld(Feld.content.BLACK, 18);
        jbtn_17_2 = new Feld(Feld.content.BLACK, 17);
        jbtn_16_2 = new Feld(Feld.content.BLACK, 16);
        jbtn_15_2 = new Feld(Feld.content.BLACK, 15);
        jbtn_14_2 = new Feld(Feld.content.BLACK, 14);
        jbtn_13_2 = new Feld(Feld.content.BLACK, 13);
        jbtn_12_2 = new Feld(Feld.content.BLACK, 12);
        jbtn_11_2 = new Feld(Feld.content.BLACK, 11);
        jbtn_20_1 = new Feld(Feld.content.BLOCK, 20);
        jbtn_21_1 = new Feld(Feld.content.BLOCK, 21);
        jbtn_22_1 = new Feld(Feld.content.BLACK, 22);
        jbtn_23_1 = new Feld(Feld.content.BLACK, 23);
        jbtn_22_2 = new Feld(Feld.content.BLACK, 22);
        jbtn_23_2 = new Feld(Feld.content.BLACK, 23);
        jbtn_24_1 = new Feld(Feld.content.BLACK, 24);
        jbtn_25_1 = new Feld(Feld.content.BLOCK, 25);
        jbtn_26_2 = new Feld(Feld.content.BLACK, 26);
        jbtn_27_2 = new Feld(Feld.content.BLACK, 27);
        jbtn_26_3 = new Feld(Feld.content.BLACK, 26);
        jbtn_25_2 = new Feld(Feld.content.BLOCK, 25);
        jbtn_24_2 = new Feld(Feld.content.BLACK, 25);
        jbtn_26_1 = new Feld(Feld.content.BLACK, 26);
        jbtn_27_1 = new Feld(Feld.content.BLACK, 27);
        jbtn_28_1 = new Feld(Feld.content.BLACK, 28);
        jbtn_29_1 = new Feld(Feld.content.BLACK, 29);
        jbtn_28_2 = new Feld(Feld.content.BLACK, 28);
        jbtn_26_4 = new Feld(Feld.content.BLACK, 26);
        jbtn_27_3 = new Feld(Feld.content.BLACK, 27);
        jbtn_29_2 = new Feld(Feld.content.BLACK, 29);
        jbtn_30_3 = new Feld(Feld.content.BLACK, 30);
        jbtn_31_3 = new Feld(Feld.content.BLACK, 31);
        jbtn_32_4 = new Feld(Feld.content.BLACK, 32);
        jbtn_30_2 = new Feld(Feld.content.BLACK, 30);
        jbtn_31_2 = new Feld(Feld.content.BLACK, 31);
        jbtn_32_3 = new Feld(Feld.content.BLACK, 32);
        jbtn_33_3 = new Feld(Feld.content.BLACK, 33);
        jbtn_30_4 = new Feld(Feld.content.BLACK, 30);
        jbtn_31_4 = new Feld(Feld.content.BLACK, 31);
        jbtn_32_6 = new Feld(Feld.content.BLACK, 32);
        jbtn_33_5 = new Feld(Feld.content.BLACK, 33);
        jbtn_34_8 = new Feld(Feld.content.BLACK, 34);
        jbtn_35_5 = new Feld(Feld.content.BLOCK, 35);
        jbtn_30_1 = new Feld(Feld.content.BLACK, 30);
        jbtn_31_1 = new Feld(Feld.content.BLACK, 31);
        jbtn_32_1 = new Feld(Feld.content.BLACK, 32);
        jbtn_33_1 = new Feld(Feld.content.BLACK, 33);
        jbtn_34_1 = new Feld(Feld.content.BLACK, 34);
        jbtn_35_1 = new Feld(Feld.content.BLOCK, 35);
        jbtn_39_2 = new Feld(Feld.content.BLACK, 39);
        jbtn_38_4 = new Feld(Feld.content.BLACK, 38);
        jbtn_37_3 = new Feld(Feld.content.BLACK, 37);
        jbtn_38_5 = new Feld(Feld.content.BLACK, 38);
        jbtn_39_3 = new Feld(Feld.content.BLACK, 39);
        jbtn_38_6 = new Feld(Feld.content.BLACK, 38);
        jbtn_37_4 = new Feld(Feld.content.BLACK, 37);
        jbtn_36_1 = new Feld(Feld.content.BLACK, 36);
        jbtn_38_7 = new Feld(Feld.content.BLACK, 38);
        jbtn_37_1 = new Feld(Feld.content.BLACK, 37);
        jbtn_39_4 = new Feld(Feld.content.BLACK, 39);
        jbtn_34_2 = new Feld(Feld.content.BLACK, 34);
        jbtn_38_8 = new Feld(Feld.content.BLACK, 38);
        jbtn_35_2 = new Feld(Feld.content.BLOCK, 35);
        jbtn_34_3 = new Feld(Feld.content.BLACK, 34);
        jbtn_33_2 = new Feld(Feld.content.BLACK, 33);
        jbtn_34_4 = new Feld(Feld.content.BLACK, 34);
        jbtn_35_3 = new Feld(Feld.content.BLOCK, 35);
        jbtn_aussetzen = new javax.swing.JButton();
        jbtn_wuerfeln = new javax.swing.JButton();
        jbtn_beenden = new javax.swing.JButton();
        jlbl_wuerfelzahl = new javax.swing.JLabel();
        jlbl_anDerReihe = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Project_M");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jpnl_alleFelder.setName("jpnl_alleFelder"); // NOI18N

        jbtn_34_5.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_34_5.setBorder(null);
        jbtn_34_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_33_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_33_4.setBorder(null);
        jbtn_33_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_36_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_36_4.setBorder(null);
        jbtn_36_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_32_5.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_32_5.setBorder(null);
        jbtn_32_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_36_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_36_3.setBorder(null);
        jbtn_36_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_32_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_32_2.setBorder(null);
        jbtn_32_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_36_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_36_2.setBorder(null);
        jbtn_36_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_red_1.setBackground(new java.awt.Color(255, 0, 0));
        jbtn_40_red_1.setBorder(null);
        jbtn_40_red_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_red_2.setBackground(new java.awt.Color(255, 0, 0));
        jbtn_40_red_2.setBorder(null);
        jbtn_40_red_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_34_6.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_34_6.setBorder(null);
        jbtn_34_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_red_3.setBackground(new java.awt.Color(255, 0, 0));
        jbtn_40_red_3.setBorder(null);
        jbtn_40_red_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_35_4.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_35_4.setBorder(null);
        jbtn_35_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_red_5.setBackground(new java.awt.Color(255, 0, 0));
        jbtn_40_red_5.setBorder(null);
        jbtn_40_red_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_34_7.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_34_7.setBorder(null);
        jbtn_34_7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_red_4.setBackground(new java.awt.Color(255, 0, 0));
        jbtn_40_red_4.setBorder(null);
        jbtn_40_red_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_36_5.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_36_5.setBorder(null);
        jbtn_36_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_37_5.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_37_5.setBorder(null);
        jbtn_37_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_38_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_38_1.setBorder(null);
        jbtn_38_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_39_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_39_1.setBorder(null);
        jbtn_39_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_38_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_38_2.setBorder(null);
        jbtn_38_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_3_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_3_1.setBorder(null);
        jbtn_3_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_37_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_37_2.setBorder(null);
        jbtn_37_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_2_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_2_2.setBorder(null);
        jbtn_2_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_38_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_38_3.setBorder(null);
        jbtn_38_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_7_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_7_1.setBorder(null);
        jbtn_7_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_9_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_9_1.setBorder(null);
        jbtn_9_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_3_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_3_2.setBorder(null);
        jbtn_3_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_4_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_4_2.setBorder(null);
        jbtn_4_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_5_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_5_2.setBorder(null);
        jbtn_5_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_6_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_6_2.setBorder(null);
        jbtn_6_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_7_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_7_2.setBorder(null);
        jbtn_7_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_8_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_8_2.setBorder(null);
        jbtn_8_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_green_1.setBackground(new java.awt.Color(0, 255, 0));
        jbtn_40_green_1.setBorder(null);
        jbtn_40_green_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_green_2.setBackground(new java.awt.Color(0, 255, 0));
        jbtn_40_green_2.setBorder(null);
        jbtn_40_green_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_green_3.setBackground(new java.awt.Color(0, 255, 0));
        jbtn_40_green_3.setBorder(null);
        jbtn_40_green_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_green_5.setBackground(new java.awt.Color(0, 255, 0));
        jbtn_40_green_5.setBorder(null);
        jbtn_40_green_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_0_ziel.setBackground(new java.awt.Color(255, 0, 255));
        jbtn_0_ziel.setBorder(null);
        jbtn_0_ziel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_green_4.setBackground(new java.awt.Color(0, 255, 0));
        jbtn_40_green_4.setBorder(null);
        jbtn_40_green_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_2_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_2_1.setBorder(null);
        jbtn_2_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_yellow_1.setBackground(java.awt.Color.yellow);
        jbtn_40_yellow_1.setBorder(null);
        jbtn_40_yellow_1.setEnabled(false);
        jbtn_40_yellow_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_1.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_1.setBorder(null);
        jbtn_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_yellow_2.setBackground(java.awt.Color.yellow);
        jbtn_40_yellow_2.setBorder(null);
        jbtn_40_yellow_2.setEnabled(false);
        jbtn_40_yellow_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_4_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_4_1.setBorder(null);
        jbtn_4_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_yellow_3.setBackground(java.awt.Color.yellow);
        jbtn_40_yellow_3.setBorder(null);
        jbtn_40_yellow_3.setEnabled(false);
        jbtn_40_yellow_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_8_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_8_1.setBorder(null);
        jbtn_8_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_yellow_5.setBackground(java.awt.Color.yellow);
        jbtn_40_yellow_5.setBorder(null);
        jbtn_40_yellow_5.setEnabled(false);
        jbtn_40_yellow_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_6_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_6_1.setBorder(null);
        jbtn_6_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_yellow_4.setBackground(java.awt.Color.yellow);
        jbtn_40_yellow_4.setBorder(null);
        jbtn_40_yellow_4.setEnabled(false);
        jbtn_40_yellow_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_5_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_5_1.setBorder(null);
        jbtn_5_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_9_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_9_2.setBorder(null);
        jbtn_9_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_10_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_10_1.setBorder(null);
        jbtn_10_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_10_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_10_2.setBorder(null);
        jbtn_10_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_11_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_11_1.setBorder(null);
        jbtn_11_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_12_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_12_1.setBorder(null);
        jbtn_12_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_13_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_13_1.setBorder(null);
        jbtn_13_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_14_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_14_1.setBorder(null);
        jbtn_14_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_15_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_15_1.setBorder(null);
        jbtn_15_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_16_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_16_1.setBorder(null);
        jbtn_16_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_17_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_17_1.setBorder(null);
        jbtn_17_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_blue_1.setBackground(new java.awt.Color(0, 0, 255));
        jbtn_40_blue_1.setBorder(null);
        jbtn_40_blue_1.setEnabled(false);
        jbtn_40_blue_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_blue_2.setBackground(new java.awt.Color(0, 0, 255));
        jbtn_40_blue_2.setBorder(null);
        jbtn_40_blue_2.setEnabled(false);
        jbtn_40_blue_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_blue_3.setBackground(new java.awt.Color(0, 0, 255));
        jbtn_40_blue_3.setBorder(null);
        jbtn_40_blue_3.setEnabled(false);
        jbtn_40_blue_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_blue_5.setBackground(new java.awt.Color(0, 0, 255));
        jbtn_40_blue_5.setBorder(null);
        jbtn_40_blue_5.setEnabled(false);
        jbtn_40_blue_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_40_blue_4.setBackground(new java.awt.Color(0, 0, 255));
        jbtn_40_blue_4.setBorder(null);
        jbtn_40_blue_4.setEnabled(false);
        jbtn_40_blue_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_18_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_18_1.setBorder(null);
        jbtn_18_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_19_1.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_19_1.setBorder(null);
        jbtn_19_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_18_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_18_2.setBorder(null);
        jbtn_18_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_17_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_17_2.setBorder(null);
        jbtn_17_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_16_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_16_2.setBorder(null);
        jbtn_16_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_15_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_15_2.setBorder(null);
        jbtn_15_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_14_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_14_2.setBorder(null);
        jbtn_14_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_13_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_13_2.setBorder(null);
        jbtn_13_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_12_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_12_2.setBorder(null);
        jbtn_12_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_11_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_11_2.setBorder(null);
        jbtn_11_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_20_1.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_20_1.setBorder(null);
        jbtn_20_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_21_1.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_21_1.setBorder(null);
        jbtn_21_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_22_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_22_1.setBorder(null);
        jbtn_22_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_23_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_23_1.setBorder(null);
        jbtn_23_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_22_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_22_2.setBorder(null);
        jbtn_22_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_23_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_23_2.setBorder(null);
        jbtn_23_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_24_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_24_1.setBorder(null);
        jbtn_24_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_25_1.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_25_1.setBorder(null);
        jbtn_25_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_26_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_26_2.setBorder(null);
        jbtn_26_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_27_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_27_2.setBorder(null);
        jbtn_27_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_26_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_26_3.setBorder(null);
        jbtn_26_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_25_2.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_25_2.setBorder(null);
        jbtn_25_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_24_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_24_2.setBorder(null);
        jbtn_24_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_26_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_26_1.setBorder(null);
        jbtn_26_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_27_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_27_1.setBorder(null);
        jbtn_27_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_28_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_28_1.setBorder(null);
        jbtn_28_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_29_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_29_1.setBorder(null);
        jbtn_29_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_28_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_28_2.setBorder(null);
        jbtn_28_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_26_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_26_4.setBorder(null);
        jbtn_26_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_27_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_27_3.setBorder(null);
        jbtn_27_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_29_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_29_2.setBorder(null);
        jbtn_29_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_30_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_30_3.setBorder(null);
        jbtn_30_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_31_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_31_3.setBorder(null);
        jbtn_31_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_32_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_32_4.setBorder(null);
        jbtn_32_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_30_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_30_2.setBorder(null);
        jbtn_30_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_31_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_31_2.setBorder(null);
        jbtn_31_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_32_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_32_3.setBorder(null);
        jbtn_32_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_33_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_33_3.setBorder(null);
        jbtn_33_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_30_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_30_4.setBorder(null);
        jbtn_30_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_31_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_31_4.setBorder(null);
        jbtn_31_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_32_6.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_32_6.setBorder(null);
        jbtn_32_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_33_5.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_33_5.setBorder(null);
        jbtn_33_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_34_8.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_34_8.setBorder(null);
        jbtn_34_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_35_5.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_35_5.setBorder(null);
        jbtn_35_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_30_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_30_1.setBorder(null);
        jbtn_30_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_31_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_31_1.setBorder(null);
        jbtn_31_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_32_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_32_1.setBorder(null);
        jbtn_32_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_33_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_33_1.setBorder(null);
        jbtn_33_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_34_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_34_1.setBorder(null);
        jbtn_34_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_35_1.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_35_1.setBorder(null);
        jbtn_35_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_39_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_39_2.setBorder(null);
        jbtn_39_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_38_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_38_4.setBorder(null);
        jbtn_38_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_37_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_37_3.setBorder(null);
        jbtn_37_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_38_5.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_38_5.setBorder(null);
        jbtn_38_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_39_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_39_3.setBorder(null);
        jbtn_39_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_38_6.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_38_6.setBorder(null);
        jbtn_38_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_37_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_37_4.setBorder(null);
        jbtn_37_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_36_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_36_1.setBorder(null);
        jbtn_36_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_38_7.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_38_7.setBorder(null);
        jbtn_38_7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_37_1.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_37_1.setBorder(null);
        jbtn_37_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_39_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_39_4.setBorder(null);
        jbtn_39_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_34_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_34_2.setBorder(null);
        jbtn_34_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_38_8.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_38_8.setBorder(null);
        jbtn_38_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_35_2.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_35_2.setBorder(null);
        jbtn_35_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_34_3.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_34_3.setBorder(null);
        jbtn_34_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_33_2.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_33_2.setBorder(null);
        jbtn_33_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_34_4.setBackground(new java.awt.Color(0, 0, 0));
        jbtn_34_4.setBorder(null);
        jbtn_34_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        jbtn_35_3.setBackground(new java.awt.Color(255, 255, 255));
        jbtn_35_3.setBorder(null);
        jbtn_35_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_ClickActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpnl_alleFelderLayout = new javax.swing.GroupLayout(jpnl_alleFelder);
        jpnl_alleFelder.setLayout(jpnl_alleFelderLayout);
        jpnl_alleFelderLayout.setHorizontalGroup(
            jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addComponent(jbtn_35_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtn_34_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_32_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbtn_32_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jbtn_28_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_31_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_30_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_29_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_30_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_31_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_33_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_34_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_35_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_34_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_33_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtn_32_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_34_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_33_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_32_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_35_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_34_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_37_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_38_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jbtn_36_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_31_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_30_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_33_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_34_6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jbtn_32_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_39_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_40_yellow_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_38_6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_28_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addComponent(jbtn_36_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jbtn_36_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpnl_alleFelderLayout.createSequentialGroup()
                                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addComponent(jbtn_29_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbtn_30_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addComponent(jbtn_35_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jbtn_34_7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jbtn_32_6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jbtn_31_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addComponent(jbtn_33_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbtn_34_8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbtn_35_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpnl_alleFelderLayout.createSequentialGroup()
                                            .addComponent(jbtn_37_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                            .addGap(57, 57, 57)
                                                            .addComponent(jbtn_40_blue_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                            .addGap(28, 28, 28)
                                                            .addComponent(jbtn_40_blue_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(jbtn_40_blue_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addGap(0, 0, Short.MAX_VALUE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                            .addComponent(jbtn_38_7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(jbtn_39_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jbtn_40_blue_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                            .addComponent(jbtn_38_8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(jbtn_37_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jbtn_40_blue_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addGap(57, 57, 57)
                                        .addComponent(jbtn_40_yellow_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(jbtn_40_yellow_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_40_yellow_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jbtn_40_yellow_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                            .addComponent(jbtn_9_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtn_8_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtn_7_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtn_6_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtn_5_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtn_4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtn_3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtn_2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jbtn_0_ziel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addComponent(jbtn_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_5_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_6_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_7_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_8_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_9_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                            .addComponent(jbtn_10_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtn_10_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addComponent(jbtn_11_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_12_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_13_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_14_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_15_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_16_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_17_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_18_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jbtn_24_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jbtn_23_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addComponent(jbtn_27_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbtn_26_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbtn_25_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jbtn_22_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jbtn_26_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jbtn_20_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addComponent(jbtn_21_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_22_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_23_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addComponent(jbtn_27_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_26_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addComponent(jbtn_25_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbtn_26_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbtn_27_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jbtn_24_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addComponent(jbtn_19_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_18_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_17_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_16_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_15_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_14_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_13_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_12_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_11_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_36_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jbtn_36_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_37_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_40_red_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addComponent(jbtn_38_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jbtn_40_red_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jbtn_40_red_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                                .addComponent(jbtn_39_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jbtn_38_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jbtn_37_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addGap(85, 85, 85)
                                    .addComponent(jbtn_40_red_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_40_red_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addGap(57, 57, 57)
                                    .addComponent(jbtn_40_green_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addGap(28, 28, 28)
                                    .addComponent(jbtn_40_green_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbtn_40_green_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_40_green_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jbtn_40_green_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_38_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_39_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_38_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addGap(95, 95, 95))
        );
        jpnl_alleFelderLayout.setVerticalGroup(
            jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jbtn_8_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_7_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_6_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_5_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_0_ziel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jbtn_2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jbtn_3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_9_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_5_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_6_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_7_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_8_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_9_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtn_10_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtn_10_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtn_11_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_12_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_13_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_14_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_15_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_16_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_17_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_18_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_19_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_18_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_17_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_16_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_15_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_14_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_13_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_12_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_11_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtn_20_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtn_21_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_22_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_23_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_22_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_23_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addComponent(jbtn_24_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtn_25_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_26_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_27_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_26_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_25_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_26_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_27_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_26_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_27_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jbtn_24_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtn_28_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_28_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_29_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_29_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_30_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_31_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_32_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_30_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_31_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_32_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_33_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_30_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_31_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_32_6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_33_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_34_8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_35_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addGap(39, 39, 39)
                                        .addComponent(jbtn_35_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnl_alleFelderLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_32_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_32_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_34_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_33_2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_34_4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_35_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_34_5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_33_4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_34_6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_35_4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_34_7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(jbtn_34_1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_31_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_32_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jbtn_30_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_34_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_33_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jbtn_35_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jbtn_36_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbtn_36_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbtn_36_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jbtn_36_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtn_37_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_37_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_38_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_39_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_38_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_37_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_38_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_39_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_38_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_37_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_38_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_39_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_38_6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_37_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_38_7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_39_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_38_8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_40_red_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_40_red_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_40_red_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_40_red_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_40_red_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_40_green_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_40_green_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_40_green_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_40_green_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_40_green_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_40_yellow_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_40_yellow_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_40_yellow_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_40_yellow_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jbtn_40_yellow_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_40_blue_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_40_blue_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_40_blue_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_40_blue_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jbtn_40_blue_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jbtn_36_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jbtn_aussetzen.setText("Aussetzen");
        jbtn_aussetzen.setEnabled(false);
        jbtn_aussetzen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_aussetzenActionPerformed(evt);
            }
        });

        jbtn_wuerfeln.setText("Würfeln");
        jbtn_wuerfeln.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_wuerfelnActionPerformed(evt);
            }
        });

        jbtn_beenden.setText("Beenden");
        jbtn_beenden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_beendenActionPerformed(evt);
            }
        });

        jlbl_anDerReihe.setName("jlbl_anDerReihe"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpnl_alleFelder, javax.swing.GroupLayout.PREFERRED_SIZE, 986, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtn_aussetzen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtn_beenden)
                        .addGap(29, 29, 29))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtn_wuerfeln)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbl_wuerfelzahl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(179, 179, 179)
                        .addComponent(jlbl_anDerReihe, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpnl_alleFelder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbl_anDerReihe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtn_wuerfeln, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlbl_wuerfelzahl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtn_beenden)
                    .addComponent(jbtn_aussetzen))
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

    private void jbtn_wuerfelnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_wuerfelnActionPerformed
        // TODO add your handling code here:
        Random Zahlenfee = new Random();
        wurfzahl = Zahlenfee.nextInt(6) + 1;
        jlbl_wuerfelzahl.setText("" + wurfzahl);
        schongewuerfelt = true;
        jbtn_aussetzen.setEnabled(true);
        jbtn_wuerfeln.setEnabled(false);
        jlbl_anDerReihe.setText("Spieler " + an_der_Reihe.spielerName + ": Bitte rücken Sie. Eigene Figur anklicken, um Rückoptionen anzeigen zu lassen.");
    }//GEN-LAST:event_jbtn_wuerfelnActionPerformed

    private void jbtn_ClickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_ClickActionPerformed
        // TODO add your handling code here:
        Feld myfeld = (Feld) evt.getSource();
        if (!someoneWon) {
            if (!blockZuSetzen) {
                if (schongewuerfelt) {
                    if (myfeld.getBackground() != Color.CYAN) {
                        propagiereZuruecksetzen();
                    }
                    if (myfeld.getBackground() == Color.CYAN) {
                        ruecken(myfeld, propagierender);
                        if (!blockZuSetzen && !someoneWon) {
                            nextPlayer();
                        }
                    } else if (myfeld.inhalt == an_der_Reihe.spielerFarbe) {
                        propagierender = myfeld;
                        propagiereRueckOptionen(myfeld, wurfzahl, null, myfeld.inhalt);
                    }
                }
            } else {
                blockSetzen(myfeld);
            }
        }

        //propagiereRueckOptionen(myfeld, wurfzahl, null, myfeld.inhalt);

    }//GEN-LAST:event_jbtn_ClickActionPerformed

    private void jbtn_aussetzenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_aussetzenActionPerformed
        // TODO add your handling code here:
        //schongewuerfelt = false;
        //jbtn_wuerfeln.setEnabled(true);
        //jbtn_aussetzen.setEnabled(false);
        nextPlayer();
    }//GEN-LAST:event_jbtn_aussetzenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /*
    protected javax.swing.JButton jbtn_0_ziel;
    protected javax.swing.JButton jbtn_1;
    protected javax.swing.JButton jbtn_10_1;
    protected javax.swing.JButton jbtn_10_2;
    protected javax.swing.JButton jbtn_11_1;
    protected javax.swing.JButton jbtn_11_2;
    protected javax.swing.JButton jbtn_12_1;
    protected javax.swing.JButton jbtn_12_2;
    protected javax.swing.JButton jbtn_13_1;
    protected javax.swing.JButton jbtn_13_2;
    protected javax.swing.JButton jbtn_14_1;
    protected javax.swing.JButton jbtn_14_2;
    protected javax.swing.JButton jbtn_15_1;
    protected javax.swing.JButton jbtn_15_2;
    protected javax.swing.JButton jbtn_16_1;
    protected javax.swing.JButton jbtn_16_2;
    protected javax.swing.JButton jbtn_17_1;
    protected javax.swing.JButton jbtn_17_2;
    protected javax.swing.JButton jbtn_18_1;
    protected javax.swing.JButton jbtn_18_2;
    protected javax.swing.JButton jbtn_19_1;
    protected javax.swing.JButton jbtn_20_1;
    protected javax.swing.JButton jbtn_21_1;
    protected javax.swing.JButton jbtn_22_1;
    protected javax.swing.JButton jbtn_22_2;
    protected javax.swing.JButton jbtn_23_1;
    protected javax.swing.JButton jbtn_23_2;
    protected javax.swing.JButton jbtn_24_1;
    protected javax.swing.JButton jbtn_24_2;
    protected javax.swing.JButton jbtn_25_1;
    protected javax.swing.JButton jbtn_25_2;
    protected javax.swing.JButton jbtn_26_1;
    protected javax.swing.JButton jbtn_26_2;
    protected javax.swing.JButton jbtn_26_3;
    protected javax.swing.JButton jbtn_26_4;
    protected javax.swing.JButton jbtn_27_1;
    protected javax.swing.JButton jbtn_27_2;
    protected javax.swing.JButton jbtn_27_3;
    protected javax.swing.JButton jbtn_28_1;
    protected javax.swing.JButton jbtn_28_2;
    protected javax.swing.JButton jbtn_29_1;
    protected javax.swing.JButton jbtn_29_2;
    protected javax.swing.JButton jbtn_2_1;
    protected javax.swing.JButton jbtn_2_2;
    protected javax.swing.JButton jbtn_30_1;
    protected javax.swing.JButton jbtn_30_2;
    protected javax.swing.JButton jbtn_30_3;
    protected javax.swing.JButton jbtn_30_4;
    protected javax.swing.JButton jbtn_31_1;
    protected javax.swing.JButton jbtn_31_2;
    protected javax.swing.JButton jbtn_31_3;
    protected javax.swing.JButton jbtn_31_4;
    protected javax.swing.JButton jbtn_32_1;
    protected javax.swing.JButton jbtn_32_2;
    protected javax.swing.JButton jbtn_32_3;
    protected javax.swing.JButton jbtn_32_4;
    protected javax.swing.JButton jbtn_32_5;
    protected javax.swing.JButton jbtn_32_6;
    protected javax.swing.JButton jbtn_33_1;
    protected javax.swing.JButton jbtn_33_2;
    protected javax.swing.JButton jbtn_33_3;
    protected javax.swing.JButton jbtn_33_4;
    protected javax.swing.JButton jbtn_33_5;
    protected javax.swing.JButton jbtn_34_1;
    protected javax.swing.JButton jbtn_34_2;
    protected javax.swing.JButton jbtn_34_3;
    protected javax.swing.JButton jbtn_34_4;
    protected javax.swing.JButton jbtn_34_5;
    protected javax.swing.JButton jbtn_34_6;
    protected javax.swing.JButton jbtn_34_7;
    protected javax.swing.JButton jbtn_34_8;
    protected javax.swing.JButton jbtn_35_1;
    protected javax.swing.JButton jbtn_35_2;
    protected javax.swing.JButton jbtn_35_3;
    protected javax.swing.JButton jbtn_35_4;
    protected javax.swing.JButton jbtn_35_5;
    protected javax.swing.JButton jbtn_36_1;
    protected javax.swing.JButton jbtn_36_2;
    protected javax.swing.JButton jbtn_36_3;
    protected javax.swing.JButton jbtn_36_4;
    protected javax.swing.JButton jbtn_36_5;
    protected javax.swing.JButton jbtn_37_1;
    protected javax.swing.JButton jbtn_37_2;
    protected javax.swing.JButton jbtn_37_3;
    protected javax.swing.JButton jbtn_37_4;
    protected javax.swing.JButton jbtn_37_5;
    protected javax.swing.JButton jbtn_38_1;
    protected javax.swing.JButton jbtn_38_2;
    protected javax.swing.JButton jbtn_38_3;
    protected javax.swing.JButton jbtn_38_4;
    protected javax.swing.JButton jbtn_38_5;
    protected javax.swing.JButton jbtn_38_6;
    protected javax.swing.JButton jbtn_38_7;
    protected javax.swing.JButton jbtn_38_8;
    protected javax.swing.JButton jbtn_39_1;
    protected javax.swing.JButton jbtn_39_2;
    protected javax.swing.JButton jbtn_39_3;
    protected javax.swing.JButton jbtn_39_4;
    protected javax.swing.JButton jbtn_3_1;
    protected javax.swing.JButton jbtn_3_2;
    protected javax.swing.JButton jbtn_40_blue_1;
    protected javax.swing.JButton jbtn_40_blue_2;
    protected javax.swing.JButton jbtn_40_blue_3;
    protected javax.swing.JButton jbtn_40_blue_4;
    protected javax.swing.JButton jbtn_40_blue_5;
    protected javax.swing.JButton jbtn_40_green_1;
    protected javax.swing.JButton jbtn_40_green_2;
    protected javax.swing.JButton jbtn_40_green_3;
    protected javax.swing.JButton jbtn_40_green_4;
    protected javax.swing.JButton jbtn_40_green_5;
    protected javax.swing.JButton jbtn_40_red_1;
    protected javax.swing.JButton jbtn_40_red_2;
    protected javax.swing.JButton jbtn_40_red_3;
    protected javax.swing.JButton jbtn_40_red_4;
    protected javax.swing.JButton jbtn_40_red_5;
    protected javax.swing.JButton jbtn_40_yellow_1;
    protected javax.swing.JButton jbtn_40_yellow_2;
    protected javax.swing.JButton jbtn_40_yellow_3;
    protected javax.swing.JButton jbtn_40_yellow_4;
    protected javax.swing.JButton jbtn_40_yellow_5;
    protected javax.swing.JButton jbtn_4_1;
    protected javax.swing.JButton jbtn_4_2;
    protected javax.swing.JButton jbtn_5_1;
    protected javax.swing.JButton jbtn_5_2;
    protected javax.swing.JButton jbtn_6_1;
    protected javax.swing.JButton jbtn_6_2;
    protected javax.swing.JButton jbtn_7_1;
    protected javax.swing.JButton jbtn_7_2;
    protected javax.swing.JButton jbtn_8_1;
    protected javax.swing.JButton jbtn_8_2;
    protected javax.swing.JButton jbtn_9_1;
    protected javax.swing.JButton jbtn_9_2;
    */
    private javax.swing.JButton jbtn_aussetzen;
    private javax.swing.JButton jbtn_beenden;
    private javax.swing.JButton jbtn_wuerfeln;
    private javax.swing.JLabel jlbl_anDerReihe;
    private javax.swing.JLabel jlbl_wuerfelzahl;
    private javax.swing.JPanel jpnl_alleFelder;
    // End of variables declaration//GEN-END:variables

    protected Feld jbtn_0_ziel;
    protected Feld jbtn_1;
    protected Feld jbtn_10_1;
    protected Feld jbtn_10_2;
    protected Feld jbtn_11_1;
    protected Feld jbtn_11_2;
    protected Feld jbtn_12_1;
    protected Feld jbtn_12_2;
    protected Feld jbtn_13_1;
    protected Feld jbtn_13_2;
    protected Feld jbtn_14_1;
    protected Feld jbtn_14_2;
    protected Feld jbtn_15_1;
    protected Feld jbtn_15_2;
    protected Feld jbtn_16_1;
    protected Feld jbtn_16_2;
    protected Feld jbtn_17_1;
    protected Feld jbtn_17_2;
    protected Feld jbtn_18_1;
    protected Feld jbtn_18_2;
    protected Feld jbtn_19_1;
    protected Feld jbtn_20_1;
    protected Feld jbtn_21_1;
    protected Feld jbtn_22_1;
    protected Feld jbtn_22_2;
    protected Feld jbtn_23_1;
    protected Feld jbtn_23_2;
    protected Feld jbtn_24_1;
    protected Feld jbtn_24_2;
    protected Feld jbtn_25_1;
    protected Feld jbtn_25_2;
    protected Feld jbtn_26_1;
    protected Feld jbtn_26_2;
    protected Feld jbtn_26_3;
    protected Feld jbtn_26_4;
    protected Feld jbtn_27_1;
    protected Feld jbtn_27_2;
    protected Feld jbtn_27_3;
    protected Feld jbtn_28_1;
    protected Feld jbtn_28_2;
    protected Feld jbtn_29_1;
    protected Feld jbtn_29_2;
    protected Feld jbtn_2_1;
    protected Feld jbtn_2_2;
    protected Feld jbtn_30_1;
    protected Feld jbtn_30_2;
    protected Feld jbtn_30_3;
    protected Feld jbtn_30_4;
    protected Feld jbtn_31_1;
    protected Feld jbtn_31_2;
    protected Feld jbtn_31_3;
    protected Feld jbtn_31_4;
    protected Feld jbtn_32_1;
    protected Feld jbtn_32_2;
    protected Feld jbtn_32_3;
    protected Feld jbtn_32_4;
    protected Feld jbtn_32_5;
    protected Feld jbtn_32_6;
    protected Feld jbtn_33_1;
    protected Feld jbtn_33_2;
    protected Feld jbtn_33_3;
    protected Feld jbtn_33_4;
    protected Feld jbtn_33_5;
    protected Feld jbtn_34_1;
    protected Feld jbtn_34_2;
    protected Feld jbtn_34_3;
    protected Feld jbtn_34_4;
    protected Feld jbtn_34_5;
    protected Feld jbtn_34_6;
    protected Feld jbtn_34_7;
    protected Feld jbtn_34_8;
    protected Feld jbtn_35_1;
    protected Feld jbtn_35_2;
    protected Feld jbtn_35_3;
    protected Feld jbtn_35_4;
    protected Feld jbtn_35_5;
    protected Feld jbtn_36_1;
    protected Feld jbtn_36_2;
    protected Feld jbtn_36_3;
    protected Feld jbtn_36_4;
    protected Feld jbtn_36_5;
    protected Feld jbtn_37_1;
    protected Feld jbtn_37_2;
    protected Feld jbtn_37_3;
    protected Feld jbtn_37_4;
    protected Feld jbtn_37_5;
    protected Feld jbtn_38_1;
    protected Feld jbtn_38_2;
    protected Feld jbtn_38_3;
    protected Feld jbtn_38_4;
    protected Feld jbtn_38_5;
    protected Feld jbtn_38_6;
    protected Feld jbtn_38_7;
    protected Feld jbtn_38_8;
    protected Feld jbtn_39_1;
    protected Feld jbtn_39_2;
    protected Feld jbtn_39_3;
    protected Feld jbtn_39_4;
    protected Feld jbtn_3_1;
    protected Feld jbtn_3_2;
    protected Startfeld jbtn_40_blue_1;
    protected Startfeld jbtn_40_blue_2;
    protected Startfeld jbtn_40_blue_3;
    protected Startfeld jbtn_40_blue_4;
    protected Startfeld jbtn_40_blue_5;
    protected Startfeld jbtn_40_green_1;
    protected Startfeld jbtn_40_green_2;
    protected Startfeld jbtn_40_green_3;
    protected Startfeld jbtn_40_green_4;
    protected Startfeld jbtn_40_green_5;
    protected Startfeld jbtn_40_red_1;
    protected Startfeld jbtn_40_red_2;
    protected Startfeld jbtn_40_red_3;
    protected Startfeld jbtn_40_red_4;
    protected Startfeld jbtn_40_red_5;
    protected Startfeld jbtn_40_yellow_1;
    protected Startfeld jbtn_40_yellow_2;
    protected Startfeld jbtn_40_yellow_3;
    protected Startfeld jbtn_40_yellow_4;
    protected Startfeld jbtn_40_yellow_5;
    protected Feld jbtn_4_1;
    protected Feld jbtn_4_2;
    protected Feld jbtn_5_1;
    protected Feld jbtn_5_2;
    protected Feld jbtn_6_1;
    protected Feld jbtn_6_2;
    protected Feld jbtn_7_1;
    protected Feld jbtn_7_2;
    protected Feld jbtn_8_1;
    protected Feld jbtn_8_2;
    protected Feld jbtn_9_1;
    protected Feld jbtn_9_2;
}
