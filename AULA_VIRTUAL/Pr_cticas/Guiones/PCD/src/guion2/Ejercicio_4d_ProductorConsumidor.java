package guion2;

import java.util.concurrent.Semaphore;

/**
 * Productor/Consumidor con buffer limitado usando semáforos.
 *
 * Semáforos típicos:
 *  - empty: cuántos huecos libres hay en el buffer (inicial = TAM_BUFFER)
 *  - full:  cuántos elementos hay para consumir (inicial = 0)
 *  - mutex: exclusión mutua para acceder al buffer (inicial = 1)
 */
public class Ejercicio_4d_ProductorConsumidor {

    // Tamaño del buffer (capacidad máxima)
    private static final int TAM_BUFFER = 5;

    // Número de elementos que producirá cada productor
    private static final int ITEMS_POR_PRODUCTOR = 50;

    // Número de productores y consumidores (puede cambiarse)
    private static final int NUM_PRODUCTORES = 2;
    private static final int NUM_CONSUMIDORES = 2;

    // Buffer circular: guardamos enteros como ejemplo de "producto"
    private static final int[] buffer = new int[TAM_BUFFER];

    // Índices para insertar y extraer en modo circular
    private static int in = 0;   // donde inserta el productor
    private static int out = 0;  // donde extrae el consumidor

    // Semáforos:
    
    // al principio todo está vacío => TAM_BUFFER huecos
    private static final Semaphore empty = new Semaphore(TAM_BUFFER);
    
    // al principio no hay nada para consumir
    private static final Semaphore full  = new Semaphore(0);
    
    // cerrojo (binario) para proteger buffer/in/out
    private static final Semaphore mutex = new Semaphore(1);

    /**
     * Inserta un elemento en el buffer (sección crítica).
     * Se asume que ya se ha hecho empty.acquire() y mutex.acquire().
     */
    private static void insertar(int item) {
        buffer[in] = item;
        in = (in + 1) % TAM_BUFFER; // avanza circularmente
    }

    /**
     * Extrae un elemento del buffer (sección crítica).
     * Se asume que ya se ha hecho full.acquire() y mutex.acquire().
     */
    private static int extraer() {
        int item = buffer[out];
        out = (out + 1) % TAM_BUFFER; // avanza circularmente
        return item;
    }

    /**
     * Hilo Productor:
     * - Genera items
     * - Espera a que haya hueco (empty)
     * - Entra en sección crítica (mutex)
     * - Inserta
     * - Sale (mutex) y avisa de que hay un elemento más (full)
     */
    static class Productor extends Thread {
        private final int id;
        private int siguienteItem = 1; // contador local para generar “productos”

        Productor(int id) {
            this.id = id;
        }

        public void run() {
            for (int i = 0; i < ITEMS_POR_PRODUCTOR; i++) {
                int item = (id * 1000) + (siguienteItem++); // item identificable por productor

                try {
                    empty.acquire(); // espera a que exista al menos 1 hueco libre en el buffer
                    mutex.acquire(); // entra a sección crítica (protege buffer e índices)

                    // ---- SECCIÓN CRÍTICA: tocar buffer/in ----
                    insertar(item);
                    System.out.println("Productor " + id + " produce: " + item);
                    // ----------------------------------------

                    mutex.release(); // sale de sección crítica
                    full.release();  // indica que hay 1 elemento más disponible para consumir

                    // (Opcional) simular trabajo/variabilidad
                    Thread.sleep((int) (Math.random() * 50));

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /**
     * Hilo Consumidor:
     * - Espera a que haya algo para consumir (full)
     * - Entra en sección crítica (mutex)
     * - Extrae
     * - Sale (mutex) y avisa de que hay un hueco más libre (empty)
     */
    static class Consumidor extends Thread {
        private final int id;
        private final int itemsAConsumir;

        Consumidor(int id, int itemsAConsumir) {
            this.id = id;
            this.itemsAConsumir = itemsAConsumir;
        }

        public void run() {
            for (int i = 0; i < itemsAConsumir; i++) {
                try {
                    full.acquire();  // espera a que exista al menos 1 elemento en el buffer
                    mutex.acquire(); // entra a sección crítica

                    // ---- SECCIÓN CRÍTICA: tocar buffer/out ----
                    int item = extraer();
                    System.out.println("Consumidor " + id + " consume: " + item);
                    // ------------------------------------------

                    mutex.release(); // sale de sección crítica
                    empty.release(); // indica que hay 1 hueco libre más

                    // (Opcional) simular trabajo/variabilidad
                    Thread.sleep((int) (Math.random() * 80));

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        // Total producido = NUM_PRODUCTORES * ITEMS_POR_PRODUCTOR
        int totalProducido = NUM_PRODUCTORES * ITEMS_POR_PRODUCTOR;

        // Repartimos lo que consume cada consumidor (aquí lo repartimos “lo más igual posible”)
        int base = totalProducido / NUM_CONSUMIDORES;
        int extra = totalProducido % NUM_CONSUMIDORES;

        Thread[] productores = new Thread[NUM_PRODUCTORES];
        Thread[] consumidores = new Thread[NUM_CONSUMIDORES];

        // Crear y arrancar productores
        for (int p = 0; p < NUM_PRODUCTORES; p++) {
            productores[p] = new Productor(p + 1);
            productores[p].start();
        }

        // Crear y arrancar consumidores
        for (int c = 0; c < NUM_CONSUMIDORES; c++) {
            int items = base + (c < extra ? 1 : 0); // reparte el “resto”
            consumidores[c] = new Consumidor(c + 1, items);
            consumidores[c].start();
        }

        // Esperar a que terminen todos
        try {
            for (Thread t : productores) t.join();
            for (Thread t : consumidores) t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Fin del hilo principal");
    }
}
