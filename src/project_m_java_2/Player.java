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
public class Player {
    String spielerName;
    Feld.content spielerFarbe;
    Startfeld[] startfeldArray;
    
    public Player(Startfeld[] Startfelder)  //für KI wäre ein Array vom Typ Feld sinnvoller
        {
            startfeldArray = Startfelder;
        }
}
