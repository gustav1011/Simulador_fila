public class Cliente {

    private int id;
    private double tempoChegada;
    private double tempoSaida;

    public Cliente(int id, double tempoChegada) {
        this.id = id;
        this.tempoChegada = tempoChegada;
    }

    public int getId() {
        return id;
    }

    public double getTempoChegada() {
        return tempoChegada;
    }

    public double getTempoSaida() {
        return tempoSaida;
    }

    public void setTempoSaida(double tempoSaida) {
        this.tempoSaida = tempoSaida;
    }
}