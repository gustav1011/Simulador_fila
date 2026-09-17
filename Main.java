import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        long seed = 10;
        long a = 214013;
        long c = 2531011;
        long m = 4294967296L;
        int quantidadeAleatorios = 100000;

        GeradorAleatorio gerador = new GeradorAleatorio(seed, a, c, m, quantidadeAleatorios);

        RedeFilas rede = new RedeFilas(gerador);

        Map<Integer, Double> roteamentoFila1 = new HashMap<>();
        roteamentoFila1.put(2, 1.0);

        Map<Integer, Double> roteamentoFila2 = new HashMap<>();
        roteamentoFila2.put(-1, 1.0);

        rede.adicionarFila(
                "Fila 1",
                1.0,
                5.0,
                4.0,
                5.0,
                2,
                3,
                true,
                2.5,
                roteamentoFila1
        );

        rede.adicionarFila(
                "Fila 2",
                0.0,
                0.0,
                1.0,
                3.0,
                1,
                5,
                false,
                0.0,
                roteamentoFila2
        );

        rede.simular();
        rede.mostrarResultados();
    }
}