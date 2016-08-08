package project_m_java_2;

public class Spieler extends Player{
    
    public Spieler(String spielerName, Feld.content spielerFarbe, Startfeld... startfelder){
        super(startfelder);
        this.playerName = spielerName;
        this.playerFarbe = spielerFarbe;
    }
}
