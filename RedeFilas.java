import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class RedeFilas {

    private final GeradorAleatorio gerador;
    private final PriorityQueue<Evento> eventos;
    private final Map<Integer, Fila> filas;
    private final Map<Integer, Map<Integer, Double>> roteamentos;
    private double tempoGlobal;

    public RedeFilas(GeradorAleatorio gerador) {
        this.gerador = gerador;
        this.eventos = new PriorityQueue<>();
        this.filas = new LinkedHashMap<>();
        this.roteamentos = new LinkedHashMap<>();
        this.tempoGlobal = 0.0;
    }

    public void adicionarFila(
            String nome,
            double minChegada,
            double maxChegada,
            double minAtendimento,
            double maxAtendimento,
            int numServidores,
            int capacidade,
            boolean comChegadaExterna,
            double primeiroTempoChegada,
            Map<Integer, Double> roteamento) {

        int id = filas.size() + 1;
        Fila fila = new Fila(
                id,
                nome,
                minChegada,
                maxChegada,
                minAtendimento,
                maxAtendimento,
                numServidores,
                capacidade,
                comChegadaExterna
        );

        filas.put(id, fila);

        Map<Integer, Double> rotas = new HashMap<>();
        if (roteamento != null && !roteamento.isEmpty()) {
            rotas.putAll(roteamento);
        } else {
            rotas.put(-1, 1.0);
        }
        roteamentos.put(id, rotas);

        if (comChegadaExterna) {
            eventos.add(new Evento(Evento.Tipo.CHEGADA, primeiroTempoChegada, id, true));
        }
    }

    public void simular() {
        while (!eventos.isEmpty() && !gerador.atingiuLimite()) {
            Evento evento = eventos.poll();
            if (evento == null) {
                break;
            }

            Fila fila = filas.get(evento.getFilaId());
            if (fila == null) {
                continue;
            }

            double delta = evento.getTempo() - tempoGlobal;
            if (delta > 0.0) {
                fila.acumularTempoEstado(tempoGlobal, evento.getTempo());
            }

            tempoGlobal = evento.getTempo();

            if (evento.getTipo() == Evento.Tipo.CHEGADA) {
                processarChegada(fila, evento.isOrigemExterna());
            } else {
                processarSaida(fila);
            }
        }
    }

    private void processarChegada(Fila fila, boolean origemExterna) {
        if (origemExterna && fila.temChegadaExterna() && !gerador.atingiuLimite()) {
            double intervalo = gerador.uniforme(fila.minChegada, fila.maxChegada);
            eventos.add(new Evento(Evento.Tipo.CHEGADA, tempoGlobal + intervalo, fila.id, true));
        }

        if (fila.getPopulacao() >= fila.capacidade) {
            fila.incrementarPerdas();
            return;
        }

        fila.incrementarPopulacao();

        if (fila.getPopulacao() <= fila.numServidores) {
            agendarSaida(fila.id);
        }
    }

    private void processarSaida(Fila fila) {
        if (fila.getPopulacao() > 0) {
            fila.decrementarPopulacao();
        }

        if (fila.getPopulacao() >= fila.numServidores) {
            agendarSaida(fila.id);
        }

        rotearCliente(fila.id);
    }

    private void agendarSaida(int filaId) {
        Fila fila = filas.get(filaId);
        if (fila == null || gerador.atingiuLimite()) {
            return;
        }

        double atendimento = gerador.uniforme(fila.minAtendimento, fila.maxAtendimento);
        double tempoSaida = tempoGlobal + atendimento;
        eventos.add(new Evento(Evento.Tipo.SAIDA, tempoSaida, filaId, false));
    }

    private void rotearCliente(int filaId) {
        Map<Integer, Double> rotas = roteamentos.get(filaId);
        if (rotas == null || rotas.isEmpty()) {
            return;
        }

        int destino = escolherDestino(rotas);
        if (destino == -1) {
            return;
        }

        Fila proximaFila = filas.get(destino);
        if (proximaFila == null) {
            return;
        }

        if (proximaFila.getPopulacao() >= proximaFila.capacidade) {
            proximaFila.incrementarPerdas();
            return;
        }

        eventos.add(new Evento(Evento.Tipo.CHEGADA, tempoGlobal, destino, false));
    }

    private int escolherDestino(Map<Integer, Double> rotas) {
        if (rotas.size() == 1 && rotas.containsKey(-1)) {
            return -1;
        }

        double valor = gerador.nextRandom();
        double acumulado = 0.0;

        for (Map.Entry<Integer, Double> entrada : rotas.entrySet()) {
            if (entrada.getKey() == -1) {
                continue;
            }
            acumulado += entrada.getValue();
            if (valor <= acumulado) {
                return entrada.getKey();
            }
        }

        return -1;
    }

    public void mostrarResultados() {
        System.out.println("\n========================================");
        System.out.println("REDE DE FILAS EM TANDEM");
        System.out.println("========================================");
        System.out.printf("Tempo global da simulação: %.6f%n", tempoGlobal);
        System.out.printf("Aleatórios utilizados: %d%n", gerador.getQuantidadeGerada());

        for (Fila fila : filas.values()) {
            fila.mostrarResultados(tempoGlobal);
        }
    }

    public double getTempoGlobal() {
        return tempoGlobal;
    }

    private static class Fila {

        private final int id;
        private final String nome;
        private final double minChegada;
        private final double maxChegada;
        private final double minAtendimento;
        private final double maxAtendimento;
        private final int numServidores;
        private final int capacidade;
        private final boolean chegadaExterna;

        private int populacao;
        private long perdas;
        private final double[] tempoEstados;

        public Fila(
                int id,
                String nome,
                double minChegada,
                double maxChegada,
                double minAtendimento,
                double maxAtendimento,
                int numServidores,
                int capacidade,
                boolean chegadaExterna) {

            this.id = id;
            this.nome = nome;
            this.minChegada = minChegada;
            this.maxChegada = maxChegada;
            this.minAtendimento = minAtendimento;
            this.maxAtendimento = maxAtendimento;
            this.numServidores = numServidores;
            this.capacidade = capacidade;
            this.chegadaExterna = chegadaExterna;
            this.populacao = 0;
            this.perdas = 0;
            this.tempoEstados = new double[capacidade + 1];
        }

        public void acumularTempoEstado(double tempoAnterior, double tempoAtual) {
            double delta = tempoAtual - tempoAnterior;
            if (delta > 0.0) {
                tempoEstados[populacao] += delta;
            }
        }

        public void incrementarPopulacao() {
            populacao++;
        }

        public void decrementarPopulacao() {
            if (populacao > 0) {
                populacao--;
            }
        }

        public int getPopulacao() {
            return populacao;
        }

        public void incrementarPerdas() {
            perdas++;
        }

        public long getPerdas() {
            return perdas;
        }

        public boolean temChegadaExterna() {
            return chegadaExterna;
        }

        public void mostrarResultados(double tempoGlobal) {
            System.out.println("\n------------------------------");
            System.out.printf("%s%n", nome);
            System.out.printf("G/G/%d/%d%n", numServidores, capacidade);
            System.out.printf("Chegada externa: %s%n", chegadaExterna ? "Sim" : "Não");
            System.out.printf("Clientes perdidos: %d%n", getPerdas());

            double populacaoMedia = 0.0;
            double somaProbabilidades = 0.0;

            for (int i = 0; i <= capacidade; i++) {
                double probabilidade = 0.0;
                if (tempoGlobal > 0.0) {
                    probabilidade = tempoEstados[i] / tempoGlobal;
                }

                somaProbabilidades += probabilidade;
                populacaoMedia += i * probabilidade;

                System.out.printf(
                        "Estado %d -> Tempo: %.6f | Probabilidade: %.6f (%.2f%%)%n",
                        i,
                        tempoEstados[i],
                        probabilidade,
                        probabilidade * 100.0
                );
            }

            System.out.printf("Soma das probabilidades: %.10f%n", somaProbabilidades);
            System.out.printf("População média: %.6f%n", populacaoMedia);
        }
    }
}
