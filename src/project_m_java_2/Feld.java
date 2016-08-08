/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_m_java_2;

import javax.swing.JButton;

/**
 *
 * @author Nikolas
 */
public class Feld extends JButton{
    
    Feld[] nachbarn;
    content inhalt;
    int entfernungZumZiel;
    
//    public Feld(){
//    }
    
//    public Feld(content c, int distanz){
//        entfernung_zum_ziel = distanz;
//        inhalt = c;
//    }

    public void setAttributes(Feld.content inhalt, int distanz){
        this.inhalt = inhalt;
        this.entfernungZumZiel = distanz;
    }
    
    public void setNachbar(Feld... felder) {
        nachbarn = felder;
    }   
    public enum content{
        RED(1), GREEN(2), YELLOW(3), BLUE(4), BLACK(5), GOAL(6), BLOCK(7);

        public final int stelle;
        
        content(int stelle) {
            this.stelle = stelle;
        }
    }
    

}

