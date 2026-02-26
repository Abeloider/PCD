package guion1;

public class Ejercicio3b_object {

    static class Caja {
        int valor = 0;
    }

    static class Incrementador implements Runnable {
        private final Caja caja;

        public Incrementador(Caja caja) {
            this.caja = caja;
        }

        public void run() {
            for (int i = 0; i < 1000; i++) {

                // 1) leer "variable compartida" (campo del objeto)
                int local = caja.valor;

                // 2) esperar aleatorio
                try {
                    Thread.sleep((int) (Math.random() * 3));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 3) incrementar local
                local++;

                // 4) escribir de vuelta
                caja.valor = local;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Caja caja = new Caja();

        Thread t1 = new Thread(new Incrementador(caja));
        Thread t2 = new Thread(new Incrementador(caja));

        t1.start();
        t2.start();
        
        t1.join();
        t2.join();

        System.out.println("OBJECT -> Final: " + caja.valor + " (esperado 2000)");
    }
}

// Pasar el objeto por referencia sí comparte el dato (caja.valor).
// Pero el problema sigue siendo el mismo: el bloque leer→esperar→incrementar→escribir
// no es atómico.
// Por eso siguen existiendo actualizaciones perdidas (lost updates).
// cambiar de static int a “objeto con campo int” NO arregla nada si se sigue con el
// mismo patrón de actualización.
