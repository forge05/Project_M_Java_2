/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_m_java_2;

/**
 *
 * @author Nikolas
 */
public class Spieler extends Player{
    
    public Spieler(String name, Feld.content inhalt){
        this.spielerName = name;
        this.spielerFarbe = inhalt;
    }
}