public class GeradorAleatorio {

    private long a;
    private long c;
    private long m;
    private long previous;

    private int quantidadeGerada;
    private int limite;

    public GeradorAleatorio(
            long seed,
            long a,
            long c,
            long m,
            int limite) {

        this.previous = seed;
        this.a = a;
        this.c = c;
        this.m = m;

        this.limite = limite;
        this.quantidadeGerada = 0;
    }

    public double nextRandom() {

        if (quantidadeGerada >= limite) {
            throw new IllegalStateException("Limite de números aleatórios atingido.");
        }
        previous = ((a * previous) + c) % m;
        quantidadeGerada++;
        return (double) previous / m;
    }

    public double uniforme(double minimo, double maximo) {
        double x = nextRandom();
        return minimo + (maximo - minimo) * x;
    }

    public int getQuantidadeGerada() {
        return quantidadeGerada;
    }

    public boolean atingiuLimite() {
        return quantidadeGerada >= limite;
    }

    public int getLimite() {
        return limite;
    }
}