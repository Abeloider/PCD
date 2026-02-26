package guion1;

public class Ejercicio3c_volatile {

    static volatile int compartida = 0;

    static class Incrementador implements Runnable {

        public void run() {
            for (int i = 0; i < 1000; i++) {

                // 1) leer compartida
                int local = compartida;

                // 2) esperar aleatorio
                try {
                    Thread.sleep((int) (Math.random() * 3));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 3) incrementar local
                local++;

                // 4) escribir compartida
                compartida = local;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(new Incrementador());
        Thread t2 = new Thread(new Incrementador());

        t1.start();
        t2.start();
        
        t1.join();
        t2.join();

        System.out.println("VOLATILE -> Final: " + compartida + " (esperado 2000)");
    }
}

// volatile garantiza visibilidad: si un hilo escribe, el otro puede leer el valor
// actualizado.
// Pero no hace atómica la secuencia (leer + modificar + escribir).
// Por tanto, pueden seguir ocurriendo intercalados donde ambos hilos lean el mismo
// valor y se pierdan incrementos.
// volatile arregla visibilidad, no atomicidad.
