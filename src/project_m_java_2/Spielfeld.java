package project_m_java_2;

import java.awt.Color;
import java.util.Random;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Spielfeld extends javax.swing.JFrame {

    Menue jfrm_menu;
    Einstellungen jfrm_einstellungen;
    int wurfzahl;
    int playerAnzahl;
    String playerName1;
    String playerName2;
    String playerName3;
    String playerName4;
    boolean schonGewuerfelt = false;
    boolean blockZuSetzen = false;
    boolean someoneWon = false;
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Player yourTurn;
    Feld propagierender;
    //ArrayList wirft ConcurrentModificationError. 
    //Um nur einem Thread Zugriff zu gestatten benutzen wir CopyOnWriteArrayList
    CopyOnWriteArrayList<Player> allePlayer;
    ListIterator<Player> iter;
    Startfeld sf;
    
    /**
     * Creates new form Spielfeld
     * @param Menu
     * @param einstellungen
     */
    public Spielfeld(Menue Menu, Einstellungen einstellungen) {
        jfrm_menu = Menu;
        jfrm_einstellungen = einstellungen;
        allePlayer = new CopyOnWriteArrayList();
        initComponents();
        erstellePlayer();
        iter = allePlayer.listIterator();
        setNeighbors();
        setAttributes();
        nextPlayer();
    }

    private void erstellePlayer() {
        boolean cpu1 = false;
        boolean cpu2 = false;
        boolean cpu3 = false;
        boolean cpu4 = false;
        for (Object c : jfrm_einstellungen.jpnl_einstellungen.getComponents()) {
            if (c.getClass() == JRadioButton.class) {
                JRadioButton jrb = (JRadioButton) c;
                if (jrb.isSelected()) {
                    playerAnzahl = Integer.parseInt(jrb.getText());
                }
            } else if (c.getClass() == JTextField.class) {
                JTextField jtf = (JTextField) c;
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
        }
        //Player anlegen und Startfelder ggf. enablen
        if (cpu1) {
            player1 = new CPU(playerName1, Feld.content.RED, jbtn_red_1, jbtn_red_2, jbtn_red_3, jbtn_red_4, jbtn_red_5);
        } else {
            player1 = new Spieler(playerName1, Feld.content.RED, jbtn_red_1, jbtn_red_2, jbtn_red_3, jbtn_red_4, jbtn_red_5);
        }
        allePlayer.add(player1);
        jlbl_playerName1.setText(playerName1);
        if (cpu2) {
            player2 = new CPU(playerName2, Feld.content.GREEN, jbtn_green_1, jbtn_green_2, jbtn_green_3, jbtn_green_4, jbtn_green_5);
        } else {
            player2 = new Spieler(playerName2, Feld.content.GREEN, jbtn_green_1, jbtn_green_2, jbtn_green_3, jbtn_green_4, jbtn_green_5);
        }
        allePlayer.add(player2);
        jlbl_playerName2.setText(playerName2);
        if (playerAnzahl > 2) {
            if (cpu3) {
                player3 = new CPU(playerName3, Feld.content.YELLOW, jbtn_yellow_1, jbtn_yellow_2, jbtn_yellow_3, jbtn_yellow_4, jbtn_yellow_5);
            } else {
                player3 = new Spieler(playerName3, Feld.content.YELLOW, jbtn_yellow_1, jbtn_yellow_2, jbtn_yellow_3, jbtn_yellow_4, jbtn_yellow_5);
            }
            allePlayer.add(player3);
            jlbl_playerName3.setText(playerName3);
            if (playerAnzahl > 3) {
                if (cpu4) {
                    player4 = new CPU(playerName4, Feld.content.BLUE, jbtn_blue_1, jbtn_blue_2, jbtn_blue_3, jbtn_blue_4, jbtn_blue_5);
                } else {
                    player4 = new Spieler(playerName4, Feld.content.BLUE, jbtn_yellow_1, jbtn_yellow_2, jbtn_yellow_3, jbtn_yellow_4, jbtn_yellow_5);
                }
                allePlayer.add(player4);
                jlbl_playerName4.setText(playerName4);
            }
        }
    }

    private void resetSpielfeld() {
        setAttributes();
        //booleans zurücksetzen
        someoneWon = false;
        blockZuSetzen = false;
        schonGewuerfelt = false;
        //neu zeichnen
        iter = allePlayer.listIterator();
        nextPlayer();
        //Startfelder zurücksetzen und alle Felder mit ihrem neuen-alten Content zeichnen
        for (Object c : jpnl_alleFelder.getComponents()) {
            //if (c.getClass() == Feld.class) {
            if(c instanceof Feld){                            //Startfelder werden auch mit true weitergeleitet
                Feld feld = (Feld) c;
                if(feld instanceof Startfeld){
                    sf = (Startfeld) feld;
                    sf.schonGeruecktWorden = false;
                }
                feld.setBackground(getColorFromContent(feld.inhalt));
            }
        }
        //Buttons disablen
        playerButtonsDisablen();
        //Actionbuttons zurücksetzen
        jbtn_wuerfeln.setEnabled(true);
        jbtn_aussetzen.setEnabled(false);
    }

    private Color getColorFromContent(Feld.content c) {
        switch (c) {
            case RED:
                return Color.RED;
            case GREEN:
                return Color.GREEN;
            case YELLOW:
                return Color.YELLOW;
            case BLUE:
                return Color.BLUE;
            case BLACK:
                return Color.BLACK;
            case GOAL:
                return Color.MAGENTA;
            case BLOCK:
                return Color.WHITE;
            //default : return null;                        //möglich, wollen aber lieber eine Farbe als null zurückgeben
            default:
                return Color.PINK;
        }
    }

    private void propagiereRueckOptionen(Feld aktuellesFeld, int spruenge, Feld altesFeld, Feld.content playerContent) {
        if (spruenge != 0) {
            if (aktuellesFeld.inhalt != Feld.content.BLOCK) {
                for (Feld nachbar : aktuellesFeld.nachbarn) {
                    if (nachbar != altesFeld) {
                        //if (nachbar.getClass() != Startfeld.class) {
                        propagiereRueckOptionen(nachbar, spruenge - 1, aktuellesFeld, playerContent);
                        //}
                    }
                }
            }
        } else if (aktuellesFeld.inhalt != playerContent) {                                       //eigene Felder werden nicht gefärbt. Man kann also nicht auf eigene Figuren rücken
            aktuellesFeld.setBackground(Color.GRAY);
            if (aktuellesFeld.inhalt == Feld.content.BLOCK) {
                aktuellesFeld.setText(Feld.content.BLOCK.toString());
            }
            if (aktuellesFeld.inhalt.stelle <= playerAnzahl && aktuellesFeld.inhalt != playerContent) {       //enum kennt implizit keine Zahlenwerte für die Inhalte
                aktuellesFeld.setText(aktuellesFeld.inhalt.toString());
                aktuellesFeld.setForeground(getColorFromContent(aktuellesFeld.inhalt));
            } else if (aktuellesFeld.inhalt == Feld.content.GOAL) {
                aktuellesFeld.setText(Feld.content.GOAL.toString());
            }
        }
    }

    private void nextPlayer() {
        if (!(iter.hasNext())) {
            iter = allePlayer.listIterator();
        }
        yourTurn = iter.next();
        jlbl_anleitungen.setText("Spieler " + yourTurn.playerName + 
                ": Bitte würfeln Sie.");
        jlbl_wurfzahl.setText("");                           //ist eigentlich bereits abgefangen, sieht aber für den Spieler besser aus
        schonGewuerfelt = false;
        jbtn_wuerfeln.setEnabled(true);
        jbtn_aussetzen.setEnabled(false);
        playerButtonsDisablen();
        rueckOptionenZuruecksetzen();
    }

    private void playerButtonsDisablen() {
        for (Object c : jpnl_alleFelder.getComponents()) {
            if (c.getClass() == Startfeld.class) {
                sf = (Startfeld) c;
                if (sf.inhalt != yourTurn.playerFarbe) {
                    sf.setEnabled(false);
                } else if (!sf.schonGeruecktWorden) {
                    sf.setEnabled(true);
                }
            }
        }
    }
    
    private void rueckOptionenZuruecksetzen() {
        for (Object c : jpnl_alleFelder.getComponents()) {                  //Möglicherweise Startfelder filtern
            if (c.getClass() == Feld.class) {
                Feld feld = (Feld) c;
                feld.setBackground(getColorFromContent(feld.inhalt));
                if (feld.inhalt != Feld.content.GOAL) {
                    feld.setText("");
                    feld.setForeground(Color.BLACK);
                }
            }
        }
    }

    private void ruecken(Feld propTer, Feld propDer) {
        Feld.content ursprungscontent = propTer.inhalt;
        propTer.inhalt = propDer.inhalt;
        propTer.setBackground(propDer.getBackground());
        
        if (propDer.getClass() == Startfeld.class) {
            sf = (Startfeld) propDer;
            sf.setEnabled(false);
            sf.schonGeruecktWorden = true;
        } else {
            propDer.inhalt = Feld.content.BLACK;
            propDer.setBackground(Color.BLACK);
        }

        switch (ursprungscontent) {                       //eigene Figuren können nicht geschlagen werden
            case RED:           //fallthrough
            case GREEN:         //fallthrough
            case YELLOW:        //fallthrough
            case BLUE:          //fallthrough
                schlagen(ursprungscontent);
                break;
            case BLOCK:
                blockZuSetzen = true;
                jlbl_anleitungen.setText("Spieler " + yourTurn.playerName + 
                        ": Bitte Block setzen. Hinweis: unterste Reihe tabu.");
                jbtn_aussetzen.setEnabled(false);
                break;
            case GOAL:
                gewinnen();
                break;
        }
        rueckOptionenZuruecksetzen();
    }

    private void schlagen(Feld.content geschlagenerInhalt) {
        for (Object c : jpnl_alleFelder.getComponents()) {
            if (c.getClass() == Startfeld.class) {
                sf = (Startfeld) c;
                if (sf.inhalt == geschlagenerInhalt) {
                    if (!sf.isEnabled() && sf.schonGeruecktWorden) {
                        sf.setEnabled(true);
                        sf.schonGeruecktWorden = false;
                        break;
                    }
                }
            }
        }
    }

    private void blockieren(Feld wirdBlock) {
        wirdBlock.inhalt = Feld.content.BLOCK;
        wirdBlock.setBackground(Color.WHITE);
        blockZuSetzen = false;
    }

    private void gewinnen() {               //JOptionPane öffnen
        someoneWon = true;
        jbtn_wuerfeln.setEnabled(false);
        jbtn_aussetzen.setEnabled(false);
        JOptionPane.showMessageDialog(null, "Spieler " + yourTurn.playerName + 
                ": Sie haben gewonnen!");
        jlbl_anleitungen.setText("Spieler " + yourTurn.playerName + 
                ": Sie haben gewonnen!");
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
        jbtn_0_ziel = new project_m_java_2.Feld();
        jbtn_7_1 = new project_m_java_2.Feld();
        jbtn_6_1 = new project_m_java_2.Feld();
        jbtn_5_1 = new project_m_java_2.Feld();
        jbtn_4_1 = new project_m_java_2.Feld();
        jbtn_9_2 = new project_m_java_2.Feld();
        jbtn_3_1 = new project_m_java_2.Feld();
        jbtn_2_1 = new project_m_java_2.Feld();
        jbtn_1 = new project_m_java_2.Feld();
        jbtn_5_2 = new project_m_java_2.Feld();
        jbtn_8_1 = new project_m_java_2.Feld();
        jbtn_6_2 = new project_m_java_2.Feld();
        jbtn_7_2 = new project_m_java_2.Feld();
        jbtn_8_2 = new project_m_java_2.Feld();
        jbtn_2_2 = new project_m_java_2.Feld();
        jbtn_3_2 = new project_m_java_2.Feld();
        jbtn_4_2 = new project_m_java_2.Feld();
        jbtn_12_1 = new project_m_java_2.Feld();
        jbtn_16_1 = new project_m_java_2.Feld();
        jbtn_13_1 = new project_m_java_2.Feld();
        jbtn_14_1 = new project_m_java_2.Feld();
        jbtn_15_1 = new project_m_java_2.Feld();
        jbtn_14_2 = new project_m_java_2.Feld();
        jbtn_10_2 = new project_m_java_2.Feld();
        jbtn_13_2 = new project_m_java_2.Feld();
        jbtn_12_2 = new project_m_java_2.Feld();
        jbtn_11_2 = new project_m_java_2.Feld();
        jbtn_17_1 = new project_m_java_2.Feld();
        jbtn_18_1 = new project_m_java_2.Feld();
        jbtn_19 = new project_m_java_2.Feld();
        jbtn_9_1 = new project_m_java_2.Feld();
        jbtn_10_1 = new project_m_java_2.Feld();
        jbtn_11_1 = new project_m_java_2.Feld();
        jbtn_18_2 = new project_m_java_2.Feld();
        jbtn_24_1 = new project_m_java_2.Feld();
        jbtn_20 = new project_m_java_2.Feld();
        jbtn_21 = new project_m_java_2.Feld();
        jbtn_23_1 = new project_m_java_2.Feld();
        jbtn_22_1 = new project_m_java_2.Feld();
        jbtn_22_2 = new project_m_java_2.Feld();
        jbtn_25_2 = new project_m_java_2.Feld();
        jbtn_26_1 = new project_m_java_2.Feld();
        jbtn_26_4 = new project_m_java_2.Feld();
        jbtn_23_2 = new project_m_java_2.Feld();
        jbtn_24_2 = new project_m_java_2.Feld();
        jbtn_25_1 = new project_m_java_2.Feld();
        jbtn_27_3 = new project_m_java_2.Feld();
        jbtn_29_1 = new project_m_java_2.Feld();
        jbtn_27_1 = new project_m_java_2.Feld();
        jbtn_28_1 = new project_m_java_2.Feld();
        jbtn_28_2 = new project_m_java_2.Feld();
        jbtn_32_3 = new project_m_java_2.Feld();
        jbtn_30_3 = new project_m_java_2.Feld();
        jbtn_33_3 = new project_m_java_2.Feld();
        jbtn_32_4 = new project_m_java_2.Feld();
        jbtn_31_3 = new project_m_java_2.Feld();
        jbtn_29_2 = new project_m_java_2.Feld();
        jbtn_30_2 = new project_m_java_2.Feld();
        jbtn_31_2 = new project_m_java_2.Feld();
        jbtn_26_2 = new project_m_java_2.Feld();
        jbtn_27_2 = new project_m_java_2.Feld();
        jbtn_26_3 = new project_m_java_2.Feld();
        jbtn_15_2 = new project_m_java_2.Feld();
        jbtn_16_2 = new project_m_java_2.Feld();
        jbtn_17_2 = new project_m_java_2.Feld();
        jbtn_35_2 = new project_m_java_2.Feld();
        jbtn_34_3 = new project_m_java_2.Feld();
        jbtn_33_1 = new project_m_java_2.Feld();
        jbtn_34_7 = new project_m_java_2.Feld();
        jbtn_30_4 = new project_m_java_2.Feld();
        jbtn_35_5 = new project_m_java_2.Feld();
        jbtn_34_2 = new project_m_java_2.Feld();
        jbtn_36_1 = new project_m_java_2.Feld();
        jbtn_33_2 = new project_m_java_2.Feld();
        jbtn_31_4 = new project_m_java_2.Feld();
        jbtn_32_6 = new project_m_java_2.Feld();
        jbtn_30_1 = new project_m_java_2.Feld();
        jbtn_34_6 = new project_m_java_2.Feld();
        jbtn_35_3 = new project_m_java_2.Feld();
        jbtn_35_4 = new project_m_java_2.Feld();
        jbtn_34_1 = new project_m_java_2.Feld();
        jbtn_32_2 = new project_m_java_2.Feld();
        jbtn_33_5 = new project_m_java_2.Feld();
        jbtn_35_1 = new project_m_java_2.Feld();
        jbtn_32_1 = new project_m_java_2.Feld();
        jbtn_34_8 = new project_m_java_2.Feld();
        jbtn_33_4 = new project_m_java_2.Feld();
        jbtn_32_5 = new project_m_java_2.Feld();
        jbtn_34_5 = new project_m_java_2.Feld();
        jbtn_31_1 = new project_m_java_2.Feld();
        jbtn_34_4 = new project_m_java_2.Feld();
        jbtn_38_8 = new project_m_java_2.Feld();
        jbtn_37_1 = new project_m_java_2.Feld();
        jbtn_39_4 = new project_m_java_2.Feld();
        jbtn_37_3 = new project_m_java_2.Feld();
        jbtn_38_6 = new project_m_java_2.Feld();
        jbtn_38_4 = new project_m_java_2.Feld();
        jbtn_37_2 = new project_m_java_2.Feld();
        jbtn_38_3 = new project_m_java_2.Feld();
        jbtn_39_1 = new project_m_java_2.Feld();
        jbtn_38_7 = new project_m_java_2.Feld();
        jbtn_37_5 = new project_m_java_2.Feld();
        jbtn_38_2 = new project_m_java_2.Feld();
        jbtn_39_2 = new project_m_java_2.Feld();
        jbtn_38_5 = new project_m_java_2.Feld();
        jbtn_39_3 = new project_m_java_2.Feld();
        jbtn_37_4 = new project_m_java_2.Feld();
        jbtn_38_1 = new project_m_java_2.Feld();
        jbtn_36_2 = new project_m_java_2.Feld();
        jbtn_36_3 = new project_m_java_2.Feld();
        jbtn_36_4 = new project_m_java_2.Feld();
        jbtn_36_5 = new project_m_java_2.Feld();
        jbtn_red_1 = new project_m_java_2.Startfeld();
        jbtn_red_2 = new project_m_java_2.Startfeld();
        jbtn_red_3 = new project_m_java_2.Startfeld();
        jbtn_red_4 = new project_m_java_2.Startfeld();
        jbtn_red_5 = new project_m_java_2.Startfeld();
        jbtn_green_1 = new project_m_java_2.Startfeld();
        jbtn_green_2 = new project_m_java_2.Startfeld();
        jbtn_green_3 = new project_m_java_2.Startfeld();
        jbtn_green_4 = new project_m_java_2.Startfeld();
        jbtn_green_5 = new project_m_java_2.Startfeld();
        jbtn_yellow_5 = new project_m_java_2.Startfeld();
        jbtn_yellow_1 = new project_m_java_2.Startfeld();
        jbtn_yellow_2 = new project_m_java_2.Startfeld();
        jbtn_yellow_3 = new project_m_java_2.Startfeld();
        jbtn_yellow_4 = new project_m_java_2.Startfeld();
        jbtn_blue_5 = new project_m_java_2.Startfeld();
        jbtn_blue_4 = new project_m_java_2.Startfeld();
        jbtn_blue_2 = new project_m_java_2.Startfeld();
        jbtn_blue_1 = new project_m_java_2.Startfeld();
        jbtn_blue_3 = new project_m_java_2.Startfeld();
        jbtn_aussetzen = new javax.swing.JButton();
        jbtn_wuerfeln = new javax.swing.JButton();
        jbtn_beenden = new javax.swing.JButton();
        jlbl_wurfzahl = new javax.swing.JLabel();
        jlbl_anleitungen = new javax.swing.JLabel();
        jbtn_reset = new javax.swing.JButton();
        jlbl_playerName1 = new javax.swing.JLabel();
        jlbl_playerName2 = new javax.swing.JLabel();
        jlbl_playerName3 = new javax.swing.JLabel();
        jlbl_playerName4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Project_M");
        setResizable(false);

        jpnl_alleFelder.setName("jpnl_alleFelder"); // NOI18N

        jbtn_0_ziel.setBackground(new java.awt.Color(255, 0, 255));
        jbtn_0_ziel.setBorder(null);
        jbtn_0_ziel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_7_1.setBackground(java.awt.Color.black);
        jbtn_7_1.setBorder(null);
        jbtn_7_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_6_1.setBackground(java.awt.Color.black);
        jbtn_6_1.setBorder(null);
        jbtn_6_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_5_1.setBackground(java.awt.Color.black);
        jbtn_5_1.setBorder(null);
        jbtn_5_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_4_1.setBackground(java.awt.Color.black);
        jbtn_4_1.setBorder(null);
        jbtn_4_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_9_2.setBackground(java.awt.Color.black);
        jbtn_9_2.setBorder(null);
        jbtn_9_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_3_1.setBackground(java.awt.Color.black);
        jbtn_3_1.setBorder(null);
        jbtn_3_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_2_1.setBackground(java.awt.Color.black);
        jbtn_2_1.setBorder(null);
        jbtn_2_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_1.setBackground(java.awt.Color.white);
        jbtn_1.setBorder(null);
        jbtn_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_5_2.setBackground(java.awt.Color.black);
        jbtn_5_2.setBorder(null);
        jbtn_5_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_8_1.setBackground(java.awt.Color.black);
        jbtn_8_1.setBorder(null);
        jbtn_8_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_6_2.setBackground(java.awt.Color.black);
        jbtn_6_2.setBorder(null);
        jbtn_6_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_7_2.setBackground(java.awt.Color.black);
        jbtn_7_2.setBorder(null);
        jbtn_7_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_8_2.setBackground(java.awt.Color.black);
        jbtn_8_2.setBorder(null);
        jbtn_8_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_2_2.setBackground(java.awt.Color.black);
        jbtn_2_2.setBorder(null);
        jbtn_2_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_3_2.setBackground(java.awt.Color.black);
        jbtn_3_2.setBorder(null);
        jbtn_3_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_4_2.setBackground(java.awt.Color.black);
        jbtn_4_2.setBorder(null);
        jbtn_4_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_12_1.setBackground(java.awt.Color.black);
        jbtn_12_1.setBorder(null);
        jbtn_12_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_16_1.setBackground(java.awt.Color.black);
        jbtn_16_1.setBorder(null);
        jbtn_16_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_13_1.setBackground(java.awt.Color.black);
        jbtn_13_1.setBorder(null);
        jbtn_13_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_14_1.setBackground(java.awt.Color.black);
        jbtn_14_1.setBorder(null);
        jbtn_14_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_15_1.setBackground(java.awt.Color.black);
        jbtn_15_1.setBorder(null);
        jbtn_15_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_14_2.setBackground(java.awt.Color.black);
        jbtn_14_2.setBorder(null);
        jbtn_14_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_10_2.setBackground(java.awt.Color.black);
        jbtn_10_2.setBorder(null);
        jbtn_10_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_13_2.setBackground(java.awt.Color.black);
        jbtn_13_2.setBorder(null);
        jbtn_13_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_12_2.setBackground(java.awt.Color.black);
        jbtn_12_2.setBorder(null);
        jbtn_12_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_11_2.setBackground(java.awt.Color.black);
        jbtn_11_2.setBorder(null);
        jbtn_11_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_17_1.setBackground(java.awt.Color.black);
        jbtn_17_1.setBorder(null);
        jbtn_17_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_18_1.setBackground(java.awt.Color.black);
        jbtn_18_1.setBorder(null);
        jbtn_18_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_19.setBackground(java.awt.Color.white);
        jbtn_19.setBorder(null);
        jbtn_19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_9_1.setBackground(java.awt.Color.black);
        jbtn_9_1.setBorder(null);
        jbtn_9_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_10_1.setBackground(java.awt.Color.black);
        jbtn_10_1.setBorder(null);
        jbtn_10_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_11_1.setBackground(java.awt.Color.black);
        jbtn_11_1.setBorder(null);
        jbtn_11_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_18_2.setBackground(java.awt.Color.black);
        jbtn_18_2.setBorder(null);
        jbtn_18_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_24_1.setBackground(java.awt.Color.black);
        jbtn_24_1.setBorder(null);
        jbtn_24_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_20.setBackground(java.awt.Color.white);
        jbtn_20.setBorder(null);
        jbtn_20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_21.setBackground(java.awt.Color.white);
        jbtn_21.setBorder(null);
        jbtn_21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_23_1.setBackground(java.awt.Color.black);
        jbtn_23_1.setBorder(null);
        jbtn_23_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_22_1.setBackground(java.awt.Color.black);
        jbtn_22_1.setBorder(null);
        jbtn_22_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_22_2.setBackground(java.awt.Color.black);
        jbtn_22_2.setBorder(null);
        jbtn_22_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_25_2.setBackground(java.awt.Color.white);
        jbtn_25_2.setBorder(null);
        jbtn_25_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_26_1.setBackground(java.awt.Color.black);
        jbtn_26_1.setBorder(null);
        jbtn_26_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_26_4.setBackground(java.awt.Color.black);
        jbtn_26_4.setBorder(null);
        jbtn_26_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_23_2.setBackground(java.awt.Color.black);
        jbtn_23_2.setBorder(null);
        jbtn_23_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_24_2.setBackground(java.awt.Color.black);
        jbtn_24_2.setBorder(null);
        jbtn_24_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_25_1.setBackground(java.awt.Color.white);
        jbtn_25_1.setBorder(null);
        jbtn_25_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_27_3.setBackground(java.awt.Color.black);
        jbtn_27_3.setBorder(null);
        jbtn_27_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_29_1.setBackground(java.awt.Color.black);
        jbtn_29_1.setBorder(null);
        jbtn_29_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_27_1.setBackground(java.awt.Color.black);
        jbtn_27_1.setBorder(null);
        jbtn_27_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_28_1.setBackground(java.awt.Color.black);
        jbtn_28_1.setBorder(null);
        jbtn_28_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_28_2.setBackground(java.awt.Color.black);
        jbtn_28_2.setBorder(null);
        jbtn_28_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_32_3.setBackground(java.awt.Color.black);
        jbtn_32_3.setBorder(null);
        jbtn_32_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_30_3.setBackground(java.awt.Color.black);
        jbtn_30_3.setBorder(null);
        jbtn_30_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_33_3.setBackground(java.awt.Color.black);
        jbtn_33_3.setBorder(null);
        jbtn_33_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_32_4.setBackground(java.awt.Color.black);
        jbtn_32_4.setBorder(null);
        jbtn_32_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_31_3.setBackground(java.awt.Color.black);
        jbtn_31_3.setBorder(null);
        jbtn_31_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_29_2.setBackground(java.awt.Color.black);
        jbtn_29_2.setBorder(null);
        jbtn_29_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_30_2.setBackground(java.awt.Color.black);
        jbtn_30_2.setBorder(null);
        jbtn_30_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_31_2.setBackground(java.awt.Color.black);
        jbtn_31_2.setBorder(null);
        jbtn_31_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_26_2.setBackground(java.awt.Color.black);
        jbtn_26_2.setBorder(null);
        jbtn_26_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_27_2.setBackground(java.awt.Color.black);
        jbtn_27_2.setBorder(null);
        jbtn_27_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_26_3.setBackground(java.awt.Color.black);
        jbtn_26_3.setBorder(null);
        jbtn_26_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_15_2.setBackground(java.awt.Color.black);
        jbtn_15_2.setBorder(null);
        jbtn_15_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_16_2.setBackground(java.awt.Color.black);
        jbtn_16_2.setBorder(null);
        jbtn_16_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_17_2.setBackground(java.awt.Color.black);
        jbtn_17_2.setBorder(null);
        jbtn_17_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_35_2.setBackground(java.awt.Color.white);
        jbtn_35_2.setBorder(null);
        jbtn_35_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_34_3.setBackground(java.awt.Color.black);
        jbtn_34_3.setBorder(null);
        jbtn_34_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_33_1.setBackground(java.awt.Color.black);
        jbtn_33_1.setBorder(null);
        jbtn_33_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_34_7.setBackground(java.awt.Color.black);
        jbtn_34_7.setBorder(null);
        jbtn_34_7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_30_4.setBackground(java.awt.Color.black);
        jbtn_30_4.setBorder(null);
        jbtn_30_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_35_5.setBackground(java.awt.Color.white);
        jbtn_35_5.setBorder(null);
        jbtn_35_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_34_2.setBackground(java.awt.Color.black);
        jbtn_34_2.setBorder(null);
        jbtn_34_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_36_1.setBackground(java.awt.Color.black);
        jbtn_36_1.setBorder(null);
        jbtn_36_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_33_2.setBackground(java.awt.Color.black);
        jbtn_33_2.setBorder(null);
        jbtn_33_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_31_4.setBackground(java.awt.Color.black);
        jbtn_31_4.setBorder(null);
        jbtn_31_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_32_6.setBackground(java.awt.Color.black);
        jbtn_32_6.setBorder(null);
        jbtn_32_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_30_1.setBackground(java.awt.Color.black);
        jbtn_30_1.setBorder(null);
        jbtn_30_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_34_6.setBackground(java.awt.Color.black);
        jbtn_34_6.setBorder(null);
        jbtn_34_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_35_3.setBackground(java.awt.Color.white);
        jbtn_35_3.setBorder(null);
        jbtn_35_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_35_4.setBackground(java.awt.Color.white);
        jbtn_35_4.setBorder(null);
        jbtn_35_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_34_1.setBackground(java.awt.Color.black);
        jbtn_34_1.setBorder(null);
        jbtn_34_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_32_2.setBackground(java.awt.Color.black);
        jbtn_32_2.setBorder(null);
        jbtn_32_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_33_5.setBackground(java.awt.Color.black);
        jbtn_33_5.setBorder(null);
        jbtn_33_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_35_1.setBackground(java.awt.Color.white);
        jbtn_35_1.setBorder(null);
        jbtn_35_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_32_1.setBackground(java.awt.Color.black);
        jbtn_32_1.setBorder(null);
        jbtn_32_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_34_8.setBackground(java.awt.Color.black);
        jbtn_34_8.setBorder(null);
        jbtn_34_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_33_4.setBackground(java.awt.Color.black);
        jbtn_33_4.setBorder(null);
        jbtn_33_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_32_5.setBackground(java.awt.Color.black);
        jbtn_32_5.setBorder(null);
        jbtn_32_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_34_5.setBackground(java.awt.Color.black);
        jbtn_34_5.setBorder(null);
        jbtn_34_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_31_1.setBackground(java.awt.Color.black);
        jbtn_31_1.setBorder(null);
        jbtn_31_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_34_4.setBackground(java.awt.Color.black);
        jbtn_34_4.setBorder(null);
        jbtn_34_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_38_8.setBackground(java.awt.Color.black);
        jbtn_38_8.setBorder(null);
        jbtn_38_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_37_1.setBackground(java.awt.Color.black);
        jbtn_37_1.setBorder(null);
        jbtn_37_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_39_4.setBackground(java.awt.Color.black);
        jbtn_39_4.setBorder(null);
        jbtn_39_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_37_3.setBackground(java.awt.Color.black);
        jbtn_37_3.setBorder(null);
        jbtn_37_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_38_6.setBackground(java.awt.Color.black);
        jbtn_38_6.setBorder(null);
        jbtn_38_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_38_4.setBackground(java.awt.Color.black);
        jbtn_38_4.setBorder(null);
        jbtn_38_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_37_2.setBackground(java.awt.Color.black);
        jbtn_37_2.setBorder(null);
        jbtn_37_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_38_3.setBackground(java.awt.Color.black);
        jbtn_38_3.setBorder(null);
        jbtn_38_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_39_1.setBackground(java.awt.Color.black);
        jbtn_39_1.setBorder(null);
        jbtn_39_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_38_7.setBackground(java.awt.Color.black);
        jbtn_38_7.setBorder(null);
        jbtn_38_7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_37_5.setBackground(java.awt.Color.black);
        jbtn_37_5.setBorder(null);
        jbtn_37_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_38_2.setBackground(java.awt.Color.black);
        jbtn_38_2.setBorder(null);
        jbtn_38_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_39_2.setBackground(java.awt.Color.black);
        jbtn_39_2.setBorder(null);
        jbtn_39_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_38_5.setBackground(java.awt.Color.black);
        jbtn_38_5.setBorder(null);
        jbtn_38_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_39_3.setBackground(java.awt.Color.black);
        jbtn_39_3.setBorder(null);
        jbtn_39_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_37_4.setBackground(java.awt.Color.black);
        jbtn_37_4.setBorder(null);
        jbtn_37_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_38_1.setBackground(java.awt.Color.black);
        jbtn_38_1.setBorder(null);
        jbtn_38_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_36_2.setBackground(java.awt.Color.black);
        jbtn_36_2.setBorder(null);
        jbtn_36_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_36_3.setBackground(java.awt.Color.black);
        jbtn_36_3.setBorder(null);
        jbtn_36_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_36_4.setBackground(java.awt.Color.black);
        jbtn_36_4.setBorder(null);
        jbtn_36_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_36_5.setBackground(java.awt.Color.black);
        jbtn_36_5.setBorder(null);
        jbtn_36_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_red_1.setBackground(java.awt.Color.red);
        jbtn_red_1.setBorder(null);
        jbtn_red_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_red_2.setBackground(java.awt.Color.red);
        jbtn_red_2.setBorder(null);
        jbtn_red_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_red_3.setBackground(java.awt.Color.red);
        jbtn_red_3.setBorder(null);
        jbtn_red_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_red_4.setBackground(java.awt.Color.red);
        jbtn_red_4.setBorder(null);
        jbtn_red_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_red_5.setBackground(java.awt.Color.red);
        jbtn_red_5.setBorder(null);
        jbtn_red_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_green_1.setBackground(java.awt.Color.green);
        jbtn_green_1.setBorder(null);
        jbtn_green_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_green_2.setBackground(java.awt.Color.green);
        jbtn_green_2.setBorder(null);
        jbtn_green_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_green_3.setBackground(java.awt.Color.green);
        jbtn_green_3.setBorder(null);
        jbtn_green_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_green_4.setBackground(java.awt.Color.green);
        jbtn_green_4.setBorder(null);
        jbtn_green_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_green_5.setBackground(java.awt.Color.green);
        jbtn_green_5.setBorder(null);
        jbtn_green_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_yellow_5.setBackground(java.awt.Color.yellow);
        jbtn_yellow_5.setBorder(null);
        jbtn_yellow_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_yellow_1.setBackground(java.awt.Color.yellow);
        jbtn_yellow_1.setBorder(null);
        jbtn_yellow_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_yellow_2.setBackground(java.awt.Color.yellow);
        jbtn_yellow_2.setBorder(null);
        jbtn_yellow_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_yellow_3.setBackground(java.awt.Color.yellow);
        jbtn_yellow_3.setBorder(null);
        jbtn_yellow_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_yellow_4.setBackground(java.awt.Color.yellow);
        jbtn_yellow_4.setBorder(null);
        jbtn_yellow_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_blue_5.setBackground(java.awt.Color.blue);
        jbtn_blue_5.setBorder(null);
        jbtn_blue_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_blue_4.setBackground(java.awt.Color.blue);
        jbtn_blue_4.setBorder(null);
        jbtn_blue_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_blue_2.setBackground(java.awt.Color.blue);
        jbtn_blue_2.setBorder(null);
        jbtn_blue_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_blue_1.setBackground(java.awt.Color.blue);
        jbtn_blue_1.setBorder(null);
        jbtn_blue_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        jbtn_blue_3.setBackground(java.awt.Color.blue);
        jbtn_blue_3.setBorder(null);
        jbtn_blue_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_clickActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpnl_alleFelderLayout = new javax.swing.GroupLayout(jpnl_alleFelder);
        jpnl_alleFelder.setLayout(jpnl_alleFelderLayout);
        jpnl_alleFelderLayout.setHorizontalGroup(
            jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jbtn_0_ziel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addComponent(jbtn_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addComponent(jbtn_8_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtn_10_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jbtn_20, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addComponent(jbtn_18_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_19, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jbtn_32_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_31_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_35_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_34_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_33_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jbtn_36_1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_30_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_28_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addComponent(jbtn_27_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_26_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(jbtn_24_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_25_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_23_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                        .addComponent(jbtn_22_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jbtn_21, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                        .addComponent(jbtn_26_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jbtn_27_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addComponent(jbtn_29_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_30_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_31_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_32_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_33_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jbtn_36_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addComponent(jbtn_34_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_35_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_34_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_32_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addComponent(jbtn_33_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_34_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jbtn_36_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_35_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
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
                                .addComponent(jbtn_12_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jbtn_28_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jbtn_22_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_26_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jbtn_23_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_24_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                        .addComponent(jbtn_25_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jbtn_26_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jbtn_27_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jbtn_32_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                        .addComponent(jbtn_32_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jbtn_31_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jbtn_30_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_29_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addComponent(jbtn_34_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbtn_33_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addComponent(jbtn_38_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbtn_39_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addComponent(jbtn_38_6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbtn_37_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                    .addComponent(jbtn_34_6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jbtn_36_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jbtn_35_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_yellow_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jbtn_yellow_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                        .addComponent(jbtn_yellow_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(57, 57, 57)))
                                                .addGap(57, 57, 57)))
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addGap(27, 27, 27)
                                            .addComponent(jbtn_yellow_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jbtn_yellow_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_38_7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_39_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_38_8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addComponent(jbtn_30_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jbtn_32_6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jbtn_31_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addComponent(jbtn_34_7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbtn_33_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbtn_34_8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_blue_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jbtn_blue_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                        .addComponent(jbtn_blue_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(57, 57, 57)))
                                                .addGap(57, 57, 57)))
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addGap(27, 27, 27)
                                            .addComponent(jbtn_blue_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jbtn_blue_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_37_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_red_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jbtn_red_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jbtn_red_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_38_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbtn_39_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtn_38_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGap(84, 84, 84)
                                .addComponent(jbtn_red_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbtn_red_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtn_37_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_38_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_39_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_38_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_37_3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jbtn_green_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jbtn_green_1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                            .addComponent(jbtn_green_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(57, 57, 57)))
                                    .addGap(57, 57, 57)))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jbtn_green_4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbtn_green_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtn_11_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_10_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_9_2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_35_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_36_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_37_5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnl_alleFelderLayout.setVerticalGroup(
            jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                .addComponent(jbtn_0_ziel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtn_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_5_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_6_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_7_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_8_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_9_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_5_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_6_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_8_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_7_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_9_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtn_10_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_10_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_11_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_12_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_13_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_14_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_15_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_17_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_18_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_16_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_11_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_12_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_13_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_14_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_15_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_16_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_17_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_18_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_23_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_23_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_22_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_22_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_24_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_24_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jbtn_25_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_25_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_26_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_26_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_26_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_27_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_26_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_27_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jbtn_27_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jbtn_28_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jbtn_28_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(jbtn_29_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jbtn_30_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jbtn_31_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jbtn_32_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jbtn_33_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jbtn_32_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jbtn_31_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jbtn_30_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(jbtn_30_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jbtn_29_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_30_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jbtn_31_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jbtn_31_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtn_32_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_32_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_32_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jbtn_32_6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtn_34_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_33_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_35_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_34_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_35_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_34_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_33_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_34_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_35_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_34_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_33_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_34_6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_35_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_34_7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_33_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtn_34_8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_36_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_36_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_36_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jbtn_38_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jbtn_37_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jbtn_39_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jbtn_38_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jbtn_38_8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_39_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_38_7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_37_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jbtn_38_5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jbtn_37_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_39_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_38_6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_green_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(4, 4, 4)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_green_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_green_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_green_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_green_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_yellow_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(4, 4, 4)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_yellow_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_yellow_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_yellow_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_yellow_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                        .addComponent(jbtn_blue_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(4, 4, 4)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_blue_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_blue_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jbtn_blue_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbtn_blue_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                                .addComponent(jbtn_36_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_37_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_38_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_39_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_38_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtn_red_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_red_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_red_3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpnl_alleFelderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jbtn_red_4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtn_red_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jpnl_alleFelderLayout.createSequentialGroup()
                        .addComponent(jbtn_35_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtn_36_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtn_37_5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        jlbl_anleitungen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbl_anleitungen.setName("jlbl_anleitungen"); // NOI18N

        jbtn_reset.setText("Reset");
        jbtn_reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_resetActionPerformed(evt);
            }
        });

        jlbl_playerName1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jlbl_playerName2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jlbl_playerName3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jlbl_playerName4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbtn_aussetzen, javax.swing.GroupLayout.PREFERRED_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jbtn_wuerfeln, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbl_wurfzahl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102)
                .addComponent(jlbl_anleitungen, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jbtn_reset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtn_beenden, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(jlbl_playerName1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addComponent(jlbl_playerName2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addComponent(jlbl_playerName3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(87, 87, 87)
                        .addComponent(jlbl_playerName4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jpnl_alleFelder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpnl_alleFelder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlbl_playerName1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbl_playerName2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbl_playerName3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbl_playerName4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlbl_wurfzahl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtn_wuerfeln, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlbl_anleitungen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtn_reset, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtn_beenden, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtn_aussetzen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jbtn_reset.getAccessibleContext().setAccessibleName("jbtn_reset");
        jlbl_playerName1.getAccessibleContext().setAccessibleName("jlbl_playername1");
        jlbl_playerName2.getAccessibleContext().setAccessibleName("jlbl_playername2");
        jlbl_playerName3.getAccessibleContext().setAccessibleName("jlbl_playername3");
        jlbl_playerName4.getAccessibleContext().setAccessibleName("jlbl_playername4");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbtn_beendenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_beendenActionPerformed
        // TODO add your handling code here:
        jfrm_menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jbtn_beendenActionPerformed

    private void jbtn_wuerfelnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_wuerfelnActionPerformed
        // TODO add your handling code here:
        Random zahlenfee = new Random();
        wurfzahl = zahlenfee.nextInt(6) + 1;
        jlbl_wurfzahl.setText(Integer.toString(wurfzahl));
        //jlbl_wurfzahl.setText(String.valueOf(wurfzahl));  //geht auch
        //jlbl_wurfzahl.setText("" + wurfzahl);            //geht auch
        //jlbl_wurfzahl.setText(wurfzahl.toString());       //geht nicht
        schonGewuerfelt = true;
        jbtn_aussetzen.setEnabled(true);
        jbtn_wuerfeln.setEnabled(false);
        jlbl_anleitungen.setText("Spieler " + yourTurn.playerName + 
                ": Bitte rücken Sie. Eigene Figur anklicken, um Rückoptionen anzeigen zu lassen.");
    }//GEN-LAST:event_jbtn_wuerfelnActionPerformed

    private void jbtn_aussetzenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_aussetzenActionPerformed
        // TODO add your handling code here:
        nextPlayer();
    }//GEN-LAST:event_jbtn_aussetzenActionPerformed

    private void jbtn_resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_resetActionPerformed
        // TODO add your handling code here:
        resetSpielfeld();
    }//GEN-LAST:event_jbtn_resetActionPerformed

    private void jbtn_clickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_clickActionPerformed
        // TODO add your handling code here:
        Feld myField = (Feld)evt.getSource();
        if (!someoneWon){
            if (schonGewuerfelt){
                if (!blockZuSetzen){
                    if(myField.getBackground() != Color.GRAY)
                        rueckOptionenZuruecksetzen();
                    if (myField.getBackground() == Color.GRAY){
                        ruecken(myField, propagierender);
                        if (!blockZuSetzen && !someoneWon){
                            nextPlayer();
                        }
                    }
                    else if (myField.inhalt == yourTurn.playerFarbe){
                        propagierender = myField;
                        propagiereRueckOptionen(myField, wurfzahl, null, myField.inhalt);
                    }
                }
                else if (myField.inhalt == Feld.content.BLACK && myField.entfernungZumZiel <= 36){
                    blockieren(myField);
                    nextPlayer();
                }
            }
        }
    }//GEN-LAST:event_jbtn_clickActionPerformed
    
    private void setNeighbors() {
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
        jbtn_18_1.setNachbar(jbtn_17_1, jbtn_19);
        jbtn_18_2.setNachbar(jbtn_17_2, jbtn_19);
        jbtn_19.setNachbar(jbtn_18_1, jbtn_18_2, jbtn_20);
        jbtn_20.setNachbar(jbtn_19, jbtn_21);
        jbtn_21.setNachbar(jbtn_20, jbtn_22_1, jbtn_22_2);
        jbtn_22_1.setNachbar(jbtn_21, jbtn_23_1);
        jbtn_22_2.setNachbar(jbtn_21, jbtn_23_2);
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
        jbtn_39_1.setNachbar(jbtn_38_1, jbtn_38_2); //, jbtn_40_red_1, jbtn_40_red_2, jbtn_40_red_3, jbtn_40_red_4, jbtn_40_red_5);                     // Nachbarschaft in die Startfelder wird bewusst weggelassen
        jbtn_39_2.setNachbar(jbtn_38_3, jbtn_38_4); //, jbtn_40_green_1, jbtn_40_green_2, jbtn_40_green_3, jbtn_40_green_4, jbtn_40_green_5);           // weil man nicht zurück in die startfelder rücken darf
        jbtn_39_3.setNachbar(jbtn_38_5, jbtn_38_6); //, jbtn_40_yellow_1, jbtn_40_yellow_2, jbtn_40_yellow_3, jbtn_40_yellow_4, jbtn_40_yellow_5);      // Außerdem wird damit in rücken verhindert, dass die Rekursion
        jbtn_39_4.setNachbar(jbtn_38_7, jbtn_38_8); //, jbtn_40_blue_1, jbtn_40_blue_2, jbtn_40_blue_3, jbtn_40_blue_4, jbtn_40_blue_5);                // zurück in die Startfelder geht
        jbtn_red_1.setNachbar(jbtn_39_1);
        jbtn_red_2.setNachbar(jbtn_39_1);
        jbtn_red_3.setNachbar(jbtn_39_1);
        jbtn_red_4.setNachbar(jbtn_39_1);
        jbtn_red_5.setNachbar(jbtn_39_1);
        jbtn_green_1.setNachbar(jbtn_39_2);
        jbtn_green_2.setNachbar(jbtn_39_2);
        jbtn_green_3.setNachbar(jbtn_39_2);
        jbtn_green_4.setNachbar(jbtn_39_2);
        jbtn_green_5.setNachbar(jbtn_39_2);
        jbtn_yellow_1.setNachbar(jbtn_39_3);
        jbtn_yellow_2.setNachbar(jbtn_39_3);
        jbtn_yellow_3.setNachbar(jbtn_39_3);
        jbtn_yellow_4.setNachbar(jbtn_39_3);
        jbtn_yellow_5.setNachbar(jbtn_39_3);
        jbtn_blue_1.setNachbar(jbtn_39_4);
        jbtn_blue_2.setNachbar(jbtn_39_4);
        jbtn_blue_3.setNachbar(jbtn_39_4);
        jbtn_blue_4.setNachbar(jbtn_39_4);
        jbtn_blue_5.setNachbar(jbtn_39_4);
    }

    private void setAttributes() {
        jbtn_0_ziel.setAttributes(Feld.content.GOAL, 0);
        jbtn_1.setAttributes(Feld.content.BLOCK, 1);
        jbtn_2_1.setAttributes(Feld.content.BLACK, 2);
        jbtn_2_2.setAttributes(Feld.content.BLACK, 2);
        jbtn_3_1.setAttributes(Feld.content.BLACK, 3);
        jbtn_3_2.setAttributes(Feld.content.BLACK, 3);
        jbtn_4_1.setAttributes(Feld.content.BLACK, 4);
        jbtn_4_2.setAttributes(Feld.content.BLACK, 4);
        jbtn_5_1.setAttributes(Feld.content.BLACK, 5);
        jbtn_5_2.setAttributes(Feld.content.BLACK, 5);
        jbtn_6_1.setAttributes(Feld.content.BLACK, 6);
        jbtn_6_2.setAttributes(Feld.content.BLACK, 6);
        jbtn_7_1.setAttributes(Feld.content.BLACK, 7);
        jbtn_7_2.setAttributes(Feld.content.BLACK, 7);
        jbtn_8_1.setAttributes(Feld.content.BLACK, 8);
        jbtn_8_2.setAttributes(Feld.content.BLACK, 8);
        jbtn_9_1.setAttributes(Feld.content.BLACK, 9);
        jbtn_9_2.setAttributes(Feld.content.BLACK, 9);
        jbtn_10_1.setAttributes(Feld.content.BLACK, 10);
        jbtn_10_2.setAttributes(Feld.content.BLACK, 10);
        jbtn_11_1.setAttributes(Feld.content.BLACK, 11);
        jbtn_11_2.setAttributes(Feld.content.BLACK, 11);
        jbtn_12_1.setAttributes(Feld.content.BLACK, 12);
        jbtn_12_2.setAttributes(Feld.content.BLACK, 12);
        jbtn_13_1.setAttributes(Feld.content.BLACK, 13);
        jbtn_13_2.setAttributes(Feld.content.BLACK, 13);
        jbtn_14_1.setAttributes(Feld.content.BLACK, 14);
        jbtn_14_2.setAttributes(Feld.content.BLACK, 14);
        jbtn_15_1.setAttributes(Feld.content.BLACK, 15);
        jbtn_15_2.setAttributes(Feld.content.BLACK, 15);
        jbtn_16_1.setAttributes(Feld.content.BLACK, 16);
        jbtn_16_2.setAttributes(Feld.content.BLACK, 16);
        jbtn_17_1.setAttributes(Feld.content.BLACK, 17);
        jbtn_17_2.setAttributes(Feld.content.BLACK, 17);
        jbtn_18_1.setAttributes(Feld.content.BLACK, 18);
        jbtn_18_2.setAttributes(Feld.content.BLACK, 18);
        jbtn_19.setAttributes(Feld.content.BLOCK, 19);
        jbtn_20.setAttributes(Feld.content.BLOCK, 20);
        jbtn_21.setAttributes(Feld.content.BLOCK, 21);
        jbtn_22_1.setAttributes(Feld.content.BLACK, 22);
        jbtn_22_2.setAttributes(Feld.content.BLACK, 22);
        jbtn_23_1.setAttributes(Feld.content.BLACK, 23);
        jbtn_23_2.setAttributes(Feld.content.BLACK, 23);
        jbtn_24_1.setAttributes(Feld.content.BLACK, 24);
        jbtn_24_2.setAttributes(Feld.content.BLACK, 24);
        jbtn_25_1.setAttributes(Feld.content.BLOCK, 25);
        jbtn_25_2.setAttributes(Feld.content.BLOCK, 25);
        jbtn_26_1.setAttributes(Feld.content.BLACK, 26);
        jbtn_26_2.setAttributes(Feld.content.BLACK, 26);
        jbtn_26_3.setAttributes(Feld.content.BLACK, 26);
        jbtn_26_4.setAttributes(Feld.content.BLACK, 26);
        jbtn_27_1.setAttributes(Feld.content.BLACK, 27);
        jbtn_27_2.setAttributes(Feld.content.BLACK, 27);
        jbtn_27_3.setAttributes(Feld.content.BLACK, 27);
        jbtn_28_1.setAttributes(Feld.content.BLACK, 28);
        jbtn_28_2.setAttributes(Feld.content.BLACK, 28);
        jbtn_29_1.setAttributes(Feld.content.BLACK, 29);
        jbtn_29_2.setAttributes(Feld.content.BLACK, 29);
        jbtn_30_1.setAttributes(Feld.content.BLACK, 30);
        jbtn_30_2.setAttributes(Feld.content.BLACK, 30);
        jbtn_30_3.setAttributes(Feld.content.BLACK, 30);
        jbtn_30_4.setAttributes(Feld.content.BLACK, 30);
        jbtn_31_1.setAttributes(Feld.content.BLACK, 31);
        jbtn_31_2.setAttributes(Feld.content.BLACK, 31);
        jbtn_31_3.setAttributes(Feld.content.BLACK, 31);
        jbtn_31_4.setAttributes(Feld.content.BLACK, 31);
        jbtn_32_1.setAttributes(Feld.content.BLACK, 32);
        jbtn_32_2.setAttributes(Feld.content.BLACK, 32);
        jbtn_32_3.setAttributes(Feld.content.BLACK, 32);
        jbtn_32_4.setAttributes(Feld.content.BLACK, 32);
        jbtn_32_5.setAttributes(Feld.content.BLACK, 32);
        jbtn_32_6.setAttributes(Feld.content.BLACK, 32);
        jbtn_33_1.setAttributes(Feld.content.BLACK, 33);
        jbtn_33_2.setAttributes(Feld.content.BLACK, 33);
        jbtn_33_3.setAttributes(Feld.content.BLACK, 33);
        jbtn_33_4.setAttributes(Feld.content.BLACK, 33);
        jbtn_33_5.setAttributes(Feld.content.BLACK, 33);
        jbtn_34_1.setAttributes(Feld.content.BLACK, 34);
        jbtn_34_2.setAttributes(Feld.content.BLACK, 34);
        jbtn_34_3.setAttributes(Feld.content.BLACK, 34);
        jbtn_34_4.setAttributes(Feld.content.BLACK, 34);
        jbtn_34_5.setAttributes(Feld.content.BLACK, 34);
        jbtn_34_6.setAttributes(Feld.content.BLACK, 34);
        jbtn_34_7.setAttributes(Feld.content.BLACK, 34);
        jbtn_34_8.setAttributes(Feld.content.BLACK, 34);
        jbtn_35_1.setAttributes(Feld.content.BLOCK, 35);
        jbtn_35_2.setAttributes(Feld.content.BLOCK, 35);
        jbtn_35_3.setAttributes(Feld.content.BLOCK, 35);
        jbtn_35_4.setAttributes(Feld.content.BLOCK, 35);
        jbtn_35_5.setAttributes(Feld.content.BLOCK, 35);
        jbtn_36_1.setAttributes(Feld.content.BLACK, 36);
        jbtn_36_2.setAttributes(Feld.content.BLACK, 36);
        jbtn_36_3.setAttributes(Feld.content.BLACK, 36);
        jbtn_36_4.setAttributes(Feld.content.BLACK, 36);
        jbtn_36_5.setAttributes(Feld.content.BLACK, 36);
        jbtn_37_1.setAttributes(Feld.content.BLACK, 37);
        jbtn_37_2.setAttributes(Feld.content.BLACK, 37);
        jbtn_37_3.setAttributes(Feld.content.BLACK, 37);
        jbtn_37_4.setAttributes(Feld.content.BLACK, 37);
        jbtn_37_5.setAttributes(Feld.content.BLACK, 37);
        jbtn_38_1.setAttributes(Feld.content.BLACK, 38);
        jbtn_38_2.setAttributes(Feld.content.BLACK, 38);
        jbtn_38_3.setAttributes(Feld.content.BLACK, 38);
        jbtn_38_4.setAttributes(Feld.content.BLACK, 38);
        jbtn_38_5.setAttributes(Feld.content.BLACK, 38);
        jbtn_38_6.setAttributes(Feld.content.BLACK, 38);
        jbtn_38_7.setAttributes(Feld.content.BLACK, 38);
        jbtn_38_8.setAttributes(Feld.content.BLACK, 38);
        jbtn_39_1.setAttributes(Feld.content.BLACK, 39);
        jbtn_39_2.setAttributes(Feld.content.BLACK, 39);
        jbtn_39_3.setAttributes(Feld.content.BLACK, 39);
        jbtn_39_4.setAttributes(Feld.content.BLACK, 39);
        jbtn_red_1.setAttributes(Feld.content.RED, 40);
        jbtn_red_2.setAttributes(Feld.content.RED, 40);
        jbtn_red_3.setAttributes(Feld.content.RED, 40);
        jbtn_red_4.setAttributes(Feld.content.RED, 40);
        jbtn_red_5.setAttributes(Feld.content.RED, 40);
        jbtn_green_1.setAttributes(Feld.content.GREEN, 40);
        jbtn_green_2.setAttributes(Feld.content.GREEN, 40);
        jbtn_green_3.setAttributes(Feld.content.GREEN, 40);
        jbtn_green_4.setAttributes(Feld.content.GREEN, 40);
        jbtn_green_5.setAttributes(Feld.content.GREEN, 40);
        jbtn_yellow_1.setAttributes(Feld.content.YELLOW, 40);
        jbtn_yellow_2.setAttributes(Feld.content.YELLOW, 40);
        jbtn_yellow_3.setAttributes(Feld.content.YELLOW, 40);
        jbtn_yellow_4.setAttributes(Feld.content.YELLOW, 40);
        jbtn_yellow_5.setAttributes(Feld.content.YELLOW, 40);
        jbtn_blue_1.setAttributes(Feld.content.BLUE, 40);
        jbtn_blue_2.setAttributes(Feld.content.BLUE, 40);
        jbtn_blue_3.setAttributes(Feld.content.BLUE, 40);
        jbtn_blue_4.setAttributes(Feld.content.BLUE, 40);
        jbtn_blue_5.setAttributes(Feld.content.BLUE, 40);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private project_m_java_2.Feld jbtn_0_ziel;
    private project_m_java_2.Feld jbtn_1;
    private project_m_java_2.Feld jbtn_10_1;
    private project_m_java_2.Feld jbtn_10_2;
    private project_m_java_2.Feld jbtn_11_1;
    private project_m_java_2.Feld jbtn_11_2;
    private project_m_java_2.Feld jbtn_12_1;
    private project_m_java_2.Feld jbtn_12_2;
    private project_m_java_2.Feld jbtn_13_1;
    private project_m_java_2.Feld jbtn_13_2;
    private project_m_java_2.Feld jbtn_14_1;
    private project_m_java_2.Feld jbtn_14_2;
    private project_m_java_2.Feld jbtn_15_1;
    private project_m_java_2.Feld jbtn_15_2;
    private project_m_java_2.Feld jbtn_16_1;
    private project_m_java_2.Feld jbtn_16_2;
    private project_m_java_2.Feld jbtn_17_1;
    private project_m_java_2.Feld jbtn_17_2;
    private project_m_java_2.Feld jbtn_18_1;
    private project_m_java_2.Feld jbtn_18_2;
    private project_m_java_2.Feld jbtn_19;
    private project_m_java_2.Feld jbtn_20;
    private project_m_java_2.Feld jbtn_21;
    private project_m_java_2.Feld jbtn_22_1;
    private project_m_java_2.Feld jbtn_22_2;
    private project_m_java_2.Feld jbtn_23_1;
    private project_m_java_2.Feld jbtn_23_2;
    private project_m_java_2.Feld jbtn_24_1;
    private project_m_java_2.Feld jbtn_24_2;
    private project_m_java_2.Feld jbtn_25_1;
    private project_m_java_2.Feld jbtn_25_2;
    private project_m_java_2.Feld jbtn_26_1;
    private project_m_java_2.Feld jbtn_26_2;
    private project_m_java_2.Feld jbtn_26_3;
    private project_m_java_2.Feld jbtn_26_4;
    private project_m_java_2.Feld jbtn_27_1;
    private project_m_java_2.Feld jbtn_27_2;
    private project_m_java_2.Feld jbtn_27_3;
    private project_m_java_2.Feld jbtn_28_1;
    private project_m_java_2.Feld jbtn_28_2;
    private project_m_java_2.Feld jbtn_29_1;
    private project_m_java_2.Feld jbtn_29_2;
    private project_m_java_2.Feld jbtn_2_1;
    private project_m_java_2.Feld jbtn_2_2;
    private project_m_java_2.Feld jbtn_30_1;
    private project_m_java_2.Feld jbtn_30_2;
    private project_m_java_2.Feld jbtn_30_3;
    private project_m_java_2.Feld jbtn_30_4;
    private project_m_java_2.Feld jbtn_31_1;
    private project_m_java_2.Feld jbtn_31_2;
    private project_m_java_2.Feld jbtn_31_3;
    private project_m_java_2.Feld jbtn_31_4;
    private project_m_java_2.Feld jbtn_32_1;
    private project_m_java_2.Feld jbtn_32_2;
    private project_m_java_2.Feld jbtn_32_3;
    private project_m_java_2.Feld jbtn_32_4;
    private project_m_java_2.Feld jbtn_32_5;
    private project_m_java_2.Feld jbtn_32_6;
    private project_m_java_2.Feld jbtn_33_1;
    private project_m_java_2.Feld jbtn_33_2;
    private project_m_java_2.Feld jbtn_33_3;
    private project_m_java_2.Feld jbtn_33_4;
    private project_m_java_2.Feld jbtn_33_5;
    private project_m_java_2.Feld jbtn_34_1;
    private project_m_java_2.Feld jbtn_34_2;
    private project_m_java_2.Feld jbtn_34_3;
    private project_m_java_2.Feld jbtn_34_4;
    private project_m_java_2.Feld jbtn_34_5;
    private project_m_java_2.Feld jbtn_34_6;
    private project_m_java_2.Feld jbtn_34_7;
    private project_m_java_2.Feld jbtn_34_8;
    private project_m_java_2.Feld jbtn_35_1;
    private project_m_java_2.Feld jbtn_35_2;
    private project_m_java_2.Feld jbtn_35_3;
    private project_m_java_2.Feld jbtn_35_4;
    private project_m_java_2.Feld jbtn_35_5;
    private project_m_java_2.Feld jbtn_36_1;
    private project_m_java_2.Feld jbtn_36_2;
    private project_m_java_2.Feld jbtn_36_3;
    private project_m_java_2.Feld jbtn_36_4;
    private project_m_java_2.Feld jbtn_36_5;
    private project_m_java_2.Feld jbtn_37_1;
    private project_m_java_2.Feld jbtn_37_2;
    private project_m_java_2.Feld jbtn_37_3;
    private project_m_java_2.Feld jbtn_37_4;
    private project_m_java_2.Feld jbtn_37_5;
    private project_m_java_2.Feld jbtn_38_1;
    private project_m_java_2.Feld jbtn_38_2;
    private project_m_java_2.Feld jbtn_38_3;
    private project_m_java_2.Feld jbtn_38_4;
    private project_m_java_2.Feld jbtn_38_5;
    private project_m_java_2.Feld jbtn_38_6;
    private project_m_java_2.Feld jbtn_38_7;
    private project_m_java_2.Feld jbtn_38_8;
    private project_m_java_2.Feld jbtn_39_1;
    private project_m_java_2.Feld jbtn_39_2;
    private project_m_java_2.Feld jbtn_39_3;
    private project_m_java_2.Feld jbtn_39_4;
    private project_m_java_2.Feld jbtn_3_1;
    private project_m_java_2.Feld jbtn_3_2;
    private project_m_java_2.Feld jbtn_4_1;
    private project_m_java_2.Feld jbtn_4_2;
    private project_m_java_2.Feld jbtn_5_1;
    private project_m_java_2.Feld jbtn_5_2;
    private project_m_java_2.Feld jbtn_6_1;
    private project_m_java_2.Feld jbtn_6_2;
    private project_m_java_2.Feld jbtn_7_1;
    private project_m_java_2.Feld jbtn_7_2;
    private project_m_java_2.Feld jbtn_8_1;
    private project_m_java_2.Feld jbtn_8_2;
    private project_m_java_2.Feld jbtn_9_1;
    private project_m_java_2.Feld jbtn_9_2;
    private javax.swing.JButton jbtn_aussetzen;
    private javax.swing.JButton jbtn_beenden;
    private project_m_java_2.Startfeld jbtn_blue_1;
    private project_m_java_2.Startfeld jbtn_blue_2;
    private project_m_java_2.Startfeld jbtn_blue_3;
    private project_m_java_2.Startfeld jbtn_blue_4;
    private project_m_java_2.Startfeld jbtn_blue_5;
    private project_m_java_2.Startfeld jbtn_green_1;
    private project_m_java_2.Startfeld jbtn_green_2;
    private project_m_java_2.Startfeld jbtn_green_3;
    private project_m_java_2.Startfeld jbtn_green_4;
    private project_m_java_2.Startfeld jbtn_green_5;
    private project_m_java_2.Startfeld jbtn_red_1;
    private project_m_java_2.Startfeld jbtn_red_2;
    private project_m_java_2.Startfeld jbtn_red_3;
    private project_m_java_2.Startfeld jbtn_red_4;
    private project_m_java_2.Startfeld jbtn_red_5;
    private javax.swing.JButton jbtn_reset;
    private javax.swing.JButton jbtn_wuerfeln;
    private project_m_java_2.Startfeld jbtn_yellow_1;
    private project_m_java_2.Startfeld jbtn_yellow_2;
    private project_m_java_2.Startfeld jbtn_yellow_3;
    private project_m_java_2.Startfeld jbtn_yellow_4;
    private project_m_java_2.Startfeld jbtn_yellow_5;
    private javax.swing.JLabel jlbl_anleitungen;
    private javax.swing.JLabel jlbl_playerName1;
    private javax.swing.JLabel jlbl_playerName2;
    private javax.swing.JLabel jlbl_playerName3;
    private javax.swing.JLabel jlbl_playerName4;
    private javax.swing.JLabel jlbl_wurfzahl;
    private javax.swing.JPanel jpnl_alleFelder;
    // End of variables declaration//GEN-END:variables
}
