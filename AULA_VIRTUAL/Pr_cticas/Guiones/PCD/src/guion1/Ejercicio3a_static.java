package guion1;

public class Ejercicio3a_static {

    static int compartida = 0;

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

        System.out.println("STATIC -> Final: " + compartida + " (esperado 2000)");
    }
}

// static solo consigue que la variable sea compartida.
// Pero el incremento “en 4 pasos” no es atómico.
// Se produce condición de carrera: dos hilos leen el mismo valor y acaban escribiendo 
// el mismo resultado, perdiendo incrementos.
