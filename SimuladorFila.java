import java.util.PriorityQueue;

public class SimuladorFila {

    private GeradorAleatorio gerador;
    private PriorityQueue<Evento> eventos;

    private double minChegada;
    private double maxChegada;
    private double minAtendimento;
    private double maxAtendimento;

    private int numServidores;
    private int capacidade;
    private int populacao;

    private long perdas;

    private double tempoGlobal;
    private double[] tempoEstados;

    public SimuladorFila(
            GeradorAleatorio gerador,
            double minChegada,
            double maxChegada,
            double minAtendimento,
            double maxAtendimento,
            int numServidores,
            int capacidade) {

        this.gerador = gerador;
        this.minChegada = minChegada;
        this.maxChegada = maxChegada;
        this.minAtendimento = minAtendimento;
        this.maxAtendimento = maxAtendimento;
        this.numServidores = numServidores;
        this.capacidade = capacidade;

        this.populacao = 0;
        this.perdas = 0;
        this.tempoGlobal = 0;

        this.tempoEstados = new double[capacidade + 1];
        this.eventos = new PriorityQueue<>();
    }

    public void simular() {

        eventos.add(new Evento(
                Evento.Tipo.CHEGADA,
                3.0
        ));

        while (!eventos.isEmpty() && !gerador.atingiuLimite()) {

            Evento evento = eventos.poll();

            double delta = evento.getTempo() - tempoGlobal;

            tempoEstados[populacao] += delta;

            tempoGlobal = evento.getTempo();

            if (evento.getTipo() == Evento.Tipo.CHEGADA) {
                processarChegada();
            } else {
                processarSaida();
            }
        }
    }

    private void processarChegada() {

        if (!gerador.atingiuLimite()) {

            double intervalo = gerador.uniforme( minChegada,maxChegada);

            double proximaChegada = tempoGlobal + intervalo;

            eventos.add(new Evento( Evento.Tipo.CHEGADA,proximaChegada));
        }

        if (gerador.atingiuLimite()) {
            return;
        }

        if (populacao >= capacidade) {
            perdas++;
            return;
        }

        populacao++;

        if (populacao <= numServidores) {
            agendarSaida();
        }
    }

    private void processarSaida() {

        populacao--;

        if (populacao >= numServidores) {
            agendarSaida();
        }
    }

    private void agendarSaida() {

        if (gerador.atingiuLimite()) {
            return;
        }

        double atendimento = gerador.uniforme(
                minAtendimento,
                maxAtendimento
        );

        double tempoSaida = tempoGlobal + atendimento;

        eventos.add(new Evento(
                Evento.Tipo.SAIDA,
                tempoSaida
        ));
    }

    public void mostrarResultados() {

        System.out.println("\n==============================");
        System.out.printf("G/G/%d/%d%n", numServidores, capacidade);
        System.out.println("==============================");

        System.out.printf("Tempo Global: %.6f%n", tempoGlobal);
        System.out.printf(
                "Aleatórios utilizados: %d%n",
                gerador.getQuantidadeGerada()
        );
        System.out.printf(
                "Clientes perdidos: %d%n",
                perdas
        );

        System.out.println("\nEstados da fila:");

        double populacaoMedia = 0;
        double somaProbabilidades = 0;

        for (int i = 0; i <= capacidade; i++) {

            double probabilidade = 0;

            if (tempoGlobal > 0) {
                probabilidade = tempoEstados[i] / tempoGlobal;
            }

            somaProbabilidades += probabilidade;
            populacaoMedia += i * probabilidade;

            System.out.printf(
                    "Estado %d -> Tempo: %.6f | Probabilidade: %.6f (%.2f%%)%n",
                    i,
                    tempoEstados[i],
                    probabilidade,
                    probabilidade * 100
            );
        }

        System.out.printf(
                "%nSoma das probabilidades: %.10f%n",
                somaProbabilidades
        );

        System.out.printf(
                "População média: %.6f%n",
                populacaoMedia
        );
    }
}