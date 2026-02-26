package guion2;

class VariableCompartida3a {

	private final Object cerrojo = new Object(); // semáforo binario
    
	private int var;

    public VariableCompartida3a(int val) {
        var = val;
    }

    public int getVar3a() {
        return var;
    }

    // Método NO sincronizado completo: solo el bloque es sección crítica
    public void incrementa3a(String id) {

        synchronized (cerrojo) {

            int temp = var;

            try {
                Thread.sleep((int) Math.round(Math.random()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            temp++;
            var = temp;

            // println dentro del cerrojo
            System.out.println(id + " " + var);
        }
    }
}

class Hilo3a extends Thread {

    private final String id;
    private final VariableCompartida3a compartida;

    public Hilo3a(String id, VariableCompartida3a compartida) {
        this.id = id;
        this.compartida = compartida;
    }

    public void run() {
        for (int i = 0; i < 1000; i++) {
            compartida.incrementa3a(id);
        }
    }
}

public class Ejercicio_3a_BloqueSynchronized {

    public static void main(String[] args) {

        VariableCompartida3a compartida = new VariableCompartida3a(0);

        Thread a = new Hilo3a("Hilo a", compartida);
        Thread b = new Hilo3a("Hilo b", compartida);

        a.start();
        b.start();

        try {
            a.join();
            b.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Hilo principal " + compartida.getVar3a());
    }
}

// La sección crítica queda protegida mediante un synchronized sobre un cerrojo explícito
// "cerrojo", garantizando exclusión mutua en el incremento y en el println, por lo que el
// contador final que imprime el hilo principal es correcto (2000).
