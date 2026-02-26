package guion2;

class VariableCompartida2b {

    private int var;

    public VariableCompartida2b(int val) {
        var = val;
    }
    
    public int getVar2b() {
        return var;
    }

    public synchronized void incrementa2b(String id) {

        int temp = var;

        try {
            Thread.sleep((int) Math.round(Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        temp++;
        var = temp;

        System.out.println(id + " " + var);
    }

}

class Hilo2b extends Thread {

    private final String id;
    private final VariableCompartida2b compar;

    public Hilo2b(String id, VariableCompartida2b compar) {
        this.id = id;
        this.compar = compar;
    }

    public void run() {
        for (int i = 0; i < 1000; i++) {
            compar.incrementa2b(id);
        }
    }
}

public class Ejercicio_2b_SynchronizedPrintln {

    public static void main(String[] args) {

        VariableCompartida2b compar = new VariableCompartida2b(0);

        Thread a = new Hilo2b("Hilo a", compar);
        Thread b = new Hilo2b("Hilo b", compar);

        a.start();
        b.start();

        try {
            a.join();
            b.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Hilo principal " + compar.getVar2b());
    }
}

// Al estar incrementa2b() sincronizado e incluir también la impresión dentro de
// la sección crítica, los incrementos y los println se hacen en exclusión mutua,
// evitando condiciones de carrera, y el hilo principal acaba mostrando un valor
// final correcto de 2000, aunque el orden de Hilo a e Hilo b sea impredecible.
