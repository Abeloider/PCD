package ejercicios;

import Ejercicio2.Panel;
import java.util.Random;
import java.util.concurrent.Semaphore; 

public class Ejercicio2 {
    // Definimos los 3 paneles como recursos compartidos
    public static Panel[] paneles = {
        new Panel("Panel 1", 10, 10),
        new Panel("Panel 2", 450, 10),
        new Panel("Panel 3", 900, 10)
    };

    // Semáforo general donde lo inicializamos a 3 ya que es el numero de paneles disponibles
    public static Semaphore general = new Semaphore(3); 
    
    // Semáforos binarios para que cada panel individual sea usado en exclusión mutua 
	// para que cada panel solo pueda ser usado por un equipo a la vez
    public static Semaphore[] binario = {
        new Semaphore(1), 
        new Semaphore(1), 
        new Semaphore(1)
    };


	// ejecutamos el programa 10 veces para ver com se van usando los paneles
    public static void main(String[] args) {
        Thread[] equipos = new Thread[10];
        for (int i = 0; i < 10; i++) {
            equipos[i] = new Equipo(i);
            equipos[i].start();
        }
    }
}

class Equipo extends Thread {
	
    private int id; 

    public Equipo(int id) {
        this.id = id;
    }

    public void run() {
        Random rand = new Random();
		// tenemos que hacerlo 3 veces 
        for (int iteracion = 0; iteracion < 3; iteracion++) {
            int resultado[][] = new int[4][5]; // Matriz para almacenar los mejores resultados de cada deportista
            for (int deportista = 0; deportista < 4; deportista++) { // Para cada deportista / fila
                for (int ejercicio = 0; ejercicio < 5; ejercicio++) { // Para cada ejercicio / columna
                    int mejorMarca = 0; // Variable para almacenar la mejor marca del deportista en el ejercicio 
                    for (int intento = 0; intento < 5; intento++) { // Cada deportista tiene 5 intentos por ejercicio
                        int marca = rand.nextInt(100);  // genera un numero aleatiorio entre 0 al 99 
                        if (marca > mejorMarca) mejorMarca = marca; // actualizamos la mejor marca si marca es mejor que la mejor marca actual 
                    }
                    resultado[deportista][ejercicio] = mejorMarca; // guardamos la mejor marca en el resultado 
                }
            }

			// APLICACIOIN DE SEMAFOROS

            try {
                Ejercicio2.general.acquire(); // Reservar uno de los 3 huecos libres
                
                // Buscar cuál de los 3 paneles está libre realmente
                for (int i = 0; i < 3; i++) {
                    boolean escrito=false;
                    if (Ejercicio2.binario[i].tryAcquire() && escrito==false) { // Si el panel i está libre
                        try {
							// Creacion del mensaje a imprimir
                            String salida = "Usando panel P" + i + " el hilo (equipo) " + id + "\n";
                            salida += "Matriz R (mejores marcas):\n";
                            for (int f = 0; f < 4; f++) {
                                for (int c = 0; c < 5; c++) {
                                    salida += resultado[f][c] + " ";
                                }
                                salida += "\n";
                            }
                            salida += "Terminando de usar panel P" + i + " el hilo " + id + "\n";
                            
                            Ejercicio2.paneles[i].escribir_mensaje(salida); //LLamada a la funcion de escritura
                            Thread.sleep(1000);
                            escrito=true;
                        } finally {
                            Ejercicio2.binario[i].release(); //Libera el panel binario
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Ejercicio2.general.release(); // Libera el panel general
            }
        }
    }
}