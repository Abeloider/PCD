package semeforos;

import java.util.Random;

public class semaforo {

}

class Equipo extends Thread {
public void run() {
	Random rand = new Random();
		int resultado[][] = new int[4][5];
		for (int deportista = 0; deportista < 4; deportista++) { // deportistas por hilos
			int ejercicios[][] = new int[5][5];
			// creamos una tabla de ejercicios con puntuaciones
			for (int i = 0; i < 5; i++) { // fila
				for (int j = 0; j < 5; j++) { // columna
					ejercicios[i][j] = rand.nextInt(100);
					if (ejercicios[i][j] > resultado[deportista][i]) {
						resultado[deportista][i] = ejercicios[i][j];
					}
				}
                System.out.print(resultado[deportista][i] + " ");
			}
            System.out.println();
		}
	}
}
