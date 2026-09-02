public class Main {

    public static void main(String[] args) {

        long seed = 10;
        long a = 214013;
        long c = 2531011;
        long m = 4294967296L;

        int quantidadeAleatorios = 100000;

        // =========================
        // G/G/1/5
        // Chegada: 3 até 5
        // Atendimento: 4 até 5
        // =========================

        GeradorAleatorio gerador1 =
                new GeradorAleatorio(
                        seed,
                        a,
                        c,
                        m,
                        quantidadeAleatorios
                );

        SimuladorFila fila1 =
                new SimuladorFila(
                        gerador1,
                        3,
                        5,
                        4,
                        5,
                        1,
                        5
                );

        fila1.simular();
        fila1.mostrarResultados();


        // =========================
        // G/G/2/5
        // Chegada: 3 até 5
        // Atendimento: 4 até 5
        // =========================

        GeradorAleatorio gerador2 =
                new GeradorAleatorio(
                        seed,
                        a,
                        c,
                        m,
                        quantidadeAleatorios
                );

        SimuladorFila fila2 =
                new SimuladorFila(
                        gerador2,
                        3,
                        5,
                        4,
                        5,
                        2,
                        5
                );

        fila2.simular();
        fila2.mostrarResultados();
    }
}