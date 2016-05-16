/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_m_java_2;

//import javax.swing.JButton;

/**
 *
 * @author Nikolas
 */
public class Feld extends myFeld {
    
    int entfernung_zum_ziel;
    content inhalt;
    Feld[] nachbarn;
    
    public Feld(content c, int distanz){
        entfernung_zum_ziel = distanz;
        inhalt = c;
    }
    
    public void setNachbarn(Feld ...felder){
        nachbarn = felder;
    }
    
    
    public enum content {
    RED,
    GREEN,
    YELLOW,
    BLUE,
    BLACK,
    GOAL,
    BLOCK;
    }
}

