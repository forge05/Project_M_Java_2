package project_m_java_2;

public class CPU extends Player{
    
    public CPU(String cpuName, Feld.content cpuFarbe, Startfeld... startfelder){
        super(startfelder);
        this.playerName = cpuName;
        this.playerFarbe = cpuFarbe;
    }
    
}
