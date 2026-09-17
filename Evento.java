public class Evento
        implements Comparable<Evento> {

    public enum Tipo {
        CHEGADA,
        SAIDA
    }

    private Tipo tipo;
    private double tempo;
    private int filaId;
    private boolean origemExterna;

    public Evento(Tipo tipo, double tempo) {
        this(tipo, tempo, -1, false);
    }

    public Evento(Tipo tipo, double tempo, int filaId) {
        this(tipo, tempo, filaId, false);
    }

    public Evento(Tipo tipo, double tempo, int filaId, boolean origemExterna) {
        this.tipo = tipo;
        this.tempo = tempo;
        this.filaId = filaId;
        this.origemExterna = origemExterna;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public double getTempo() {
        return tempo;
    }

    public int getFilaId() {
        return filaId;
    }

    public boolean isOrigemExterna() {
        return origemExterna;
    }

    @Override
    public int compareTo(Evento outro) {

        int comparacao = Double.compare(this.tempo, outro.tempo);

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

        return Integer.compare(this.filaId, outro.filaId);
    }
}