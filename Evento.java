public class Evento
        implements Comparable<Evento> {

    public enum Tipo {
        CHEGADA,
        SAIDA
    }

    private Tipo tipo;
    private double tempo;

    public Evento(Tipo tipo, double tempo) {
        this.tipo = tipo;
        this.tempo = tempo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public double getTempo() {
        return tempo;
    }

    @Override
    public int compareTo(Evento outro) {

        int comparacao = Double.compare( this.tempo,outro.tempo);

        if (comparacao != 0) {
            return comparacao;
        }
        if (this.tipo == Tipo.SAIDA
                && outro.tipo == Tipo.CHEGADA) {

            return -1;
        }

        if (this.tipo == Tipo.CHEGADA
                && outro.tipo == Tipo.SAIDA) {

            return 1;
        }

        return 0;
    }
}