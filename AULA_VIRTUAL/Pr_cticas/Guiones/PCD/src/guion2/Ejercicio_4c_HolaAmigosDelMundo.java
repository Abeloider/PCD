package guion2;

import java.util.concurrent.Semaphore;

/*
 * Hilo 1: imprime "hola "
 * - Espera su turno con sHola
 * - Imprime su palabra
 * - Da paso al siguiente hilo liberando sAmigos
 */
class HiloHola4c extends Thread {

    public void run() {
        for (int i = 0; i < Ejercicio_4c_HolaAmigosDelMundo.N; i++) {   // Repite N veces (una por frase)
            try {
                Ejercicio_4c_HolaAmigosDelMundo.sHola.acquire();        // Espera a que sea turno de "hola"
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Marca el hilo como interrumpido
                return;                              // Sale del hilo
            }
            System.out.print("hola ");               // Imprime la palabra sin salto de línea
            Ejercicio_4c_HolaAmigosDelMundo.sAmigos.release();          // Permite que continúe el hilo "amigos"
        }
    }
}

/*
 * Hilo 2: imprime "amigos "
 * - Espera con sAmigos (hasta que "hola" le dé paso)
 * - Imprime su palabra
 * - Da paso al siguiente liberando sDel
 */
class HiloAmigos4c extends Thread {

    public void run() {
        for (int i = 0; i < Ejercicio_4c_HolaAmigosDelMundo.N; i++) {
            try {
                Ejercicio_4c_HolaAmigosDelMundo.sAmigos.acquire();       // Espera a que "hola" haya impreso
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            System.out.print("amigos ");             // Imprime la palabra
            Ejercicio_4c_HolaAmigosDelMundo.sDel.release();             // Da turno al hilo "del"
        }
    }
}

/*
 * Hilo 3: imprime "del "
 * - Espera con sDel (hasta que "amigos" le dé paso)
 * - Imprime su palabra
 * - Da paso al siguiente liberando sMundo
 */
class HiloDel4c extends Thread {

    public void run() {
        for (int i = 0; i < Ejercicio_4c_HolaAmigosDelMundo.N; i++) {
            try {
                Ejercicio_4c_HolaAmigosDelMundo.sDel.acquire();         // Espera a que "amigos" haya impreso
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            System.out.print("del ");                // Imprime la palabra
            Ejercicio_4c_HolaAmigosDelMundo.sMundo.release();           // Da turno al hilo "mundo"
        }
    }
}

/*
 * Hilo 4: imprime "mundo" y salto de línea
 * - Espera con sMundo (hasta que "del" le dé paso)
 * - Imprime "mundo" con println (incluye salto de línea)
 * - Libera sHola para que empiece la siguiente frase
 */
class HiloMundo4c extends Thread {

    public void run() {
        for (int i = 0; i < Ejercicio_4c_HolaAmigosDelMundo.N; i++) {
            try {
                Ejercicio_4c_HolaAmigosDelMundo.sMundo.acquire();       // Espera a que "del" haya impreso
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            System.out.println("mundo");             // Termina la frase y hace salto de línea
            Ejercicio_4c_HolaAmigosDelMundo.sHola.release();            // Reinicia el ciclo: vuelve a tocar "hola"
        }
    }
}

/*
 * Clase principal:
 * - Define cuántas frases se imprimen (N)
 * - Crea los semáforos con el valor inicial correcto
 * - Lanza los 4 hilos
 * - Espera a que terminen con join()
 */
public class Ejercicio_4c_HolaAmigosDelMundo {
    public static final int N = 10;                  // Número de veces que queremos imprimir la frase

    /*
     * Semáforos para controlar el orden:
     * - sHola empieza en 1: permite que "hola" arranque inmediatamente.
     * - los demás empiezan en 0: obligan a esperar hasta que el anterior los libere.
     */
    public static final Semaphore sHola   = new Semaphore(1);
    public static final Semaphore sAmigos = new Semaphore(0);
    public static final Semaphore sDel    = new Semaphore(0);
    public static final Semaphore sMundo  = new Semaphore(0);

    public static void main(String[] args) {
        // Creamos un hilo por palabra (cada uno imprime siempre la misma palabra)
        Thread t1 = new HiloHola4c();
        Thread t2 = new HiloAmigos4c();
        Thread t3 = new HiloDel4c();
        Thread t4 = new HiloMundo4c();

        // Arrancamos todos: el orden real lo impondrán los semáforos
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // join() hace que el main espere a que terminen los 4 hilos
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mensaje final cuando ya se han impreso las 10 frases
        System.out.println("Fin del hilo principal");
    }
}

// Mediante el uso encadenado de semáforos, los cuatro hilos se sincronizan
// correctamente para imprimir "hola amigos del mundo" en el orden exacto y
// repetido N veces, independientemente de la planificación de los hilos.
