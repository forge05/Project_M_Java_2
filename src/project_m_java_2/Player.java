package project_m_java_2;

public class Player {
    String playerName;
    Feld.content playerFarbe;
    Startfeld[] startfeldArray;
    
    public Player(Startfeld[] Startfelder)  //für KI wäre ein Array vom Typ Feld sinnvoller
        {
            startfeldArray = Startfelder;
        }
}
