package ejercicios;

import java.util.Random;
import java.util.concurrent.Semaphore;
import Ejercicio2.Panel; // Importamos tu clase Panel

public class Ejercicio2 {
    // Definimos los 3 paneles como recursos compartidos [cite: 66, 67]
    public static Panel[] paneles = {
        new Panel("Panel 0", 10, 10),
        new Panel("Panel 1", 450, 10),
        new Panel("Panel 2", 900, 10)
    };

    // Semáforo general para controlar el acceso a los 3 paneles 
    public static Semaphore semPaneles = new Semaphore(3);
    
    // Semáforos binarios para que cada panel individual sea usado en exclusión mutua 
    public static Semaphore[] mutexPanel = {
        new Semaphore(1), 
        new Semaphore(1), 
        new Semaphore(1)
    };

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
        // El enunciado pide repetir 3 veces la secuencia completa [cite: 327]
        for (int iteracion = 0; iteracion < 3; iteracion++) {
            
            // --- PASO 1, 2 y 3: Cálculo (Fuera de semáforos para máxima eficiencia) ---
            int resultado[][] = new int[4][5];
            for (int deportista = 0; deportista < 4; deportista++) {
                for (int ejercicio = 0; ejercicio < 5; ejercicio++) {
                    int mejorMarca = 0;
                    for (int intento = 0; intento < 5; intento++) {
                        int marca = rand.nextInt(100);
                        if (marca > mejorMarca) mejorMarca = marca;
                    }
                    resultado[deportista][ejercicio] = mejorMarca;
                }
            }

            // --- PASO 4: Uso de paneles con Semáforos [cite: 270, 284] ---
            try {
                Ejercicio2.semPaneles.acquire(); // Reservar uno de los 3 huecos libres
                
                // Buscar cuál de los 3 paneles está libre realmente
                for (int i = 0; i < 3; i++) {
                    if (Ejercicio2.mutexPanel[i].tryAcquire()) { // Si el panel i está libre
                        try {
                            String salida = "Usando panel P" + i + " el hilo (equipo) " + id + "\n";
                            salida += "Matriz R (mejores marcas):\n";
                            for (int f = 0; f < 4; f++) {
                                for (int c = 0; c < 5; c++) {
                                    salida += resultado[f][c] + " ";
                                }
                                salida += "\n";
                            }
                            salida += "Terminando de usar panel P" + i + " el hilo " + id + "\n";
                            
                            Ejercicio2.paneles[i].escribir_mensaje(salida);
                            Thread.sleep(1000); // Para poder ver la escritura
                        } finally {
                            Ejercicio2.mutexPanel[i].release(); // Liberar el panel i [cite: 292]
                        }
                        break; 
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Ejercicio2.semPaneles.release(); // Liberar el hueco del semáforo general [cite: 318]
            }
        }
    }
}