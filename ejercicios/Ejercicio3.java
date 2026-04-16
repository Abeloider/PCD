package ejercicios;

import java.util.concurrent.locks.*;
import java.util.Random;

// --- CLASE MONITOR ---
// Implementa la lógica de sincronización avanzada usando ReentrantLock y Condition
// según lo descrito en el apartado 2 del guion[cite: 222, 225].
class MonitorGimnasio {
    private final ReentrantLock lock = new ReentrantLock();
    
    // Variables de condición: permiten tener distintos wait sets (colas de espera)[cite: 224, 233].
    private final Condition[] esperaTorno = new Condition[3];
    private final Condition[] esperaZona = new Condition[4];
    private final Condition esperaBiciPremium = lock.newCondition();
    
    // Estado del recurso
    private final boolean[] tornoOcupado = new boolean[3];
    private final int[] maquinasOcupadas = new int[4]; // Máximo 5 por zona
    private boolean biciPremiumOcupada = false;
    
    // Contadores para las estadísticas de espera
    private final int[] hilosEsperandoTorno = new int[3];
    private final int[] hilosEsperandoZona = new int[4];
    private int hilosEsperandoBici = 0;

    public MonitorGimnasio() {
        for (int i = 0; i < 3; i++) esperaTorno[i] = lock.newCondition();
        for (int i = 0; i < 4; i++) esperaZona[i] = lock.newCondition();
    }

    // Método para acceder a los tornos
    public int entrarPorTorno(int id) throws InterruptedException {
        lock.lock(); // Obtención del cerrojo [cite: 259]
        try {
            // Lógica: elegir el primer torno libre o el que tenga menos cola
            int tornoElegido = 0;
            for (int i = 0; i < 3; i++) {
                if (!tornoOcupado[i]) { tornoElegido = i; break; }
                if (hilosEsperandoTorno[i] < hilosEsperandoTorno[tornoElegido]) tornoElegido = i;
            }

            hilosEsperandoTorno[tornoElegido]++;
            while (tornoOcupado[tornoElegido]) {
                esperaTorno[tornoElegido].await(); // El hilo espera en la cola del torno [cite: 235]
            }
            hilosEsperandoTorno[tornoElegido]--;
            tornoOcupado[tornoElegido] = true;
            return tornoElegido + 1;
        } finally {
            lock.unlock(); // Liberación del cerrojo en bloque finally [cite: 270, 288]
        }
    }

    public void salirTorno(int numTorno) {
        lock.lock();
        try {
            tornoOcupado[numTorno - 1] = false;
            esperaTorno[numTorno - 1].signal(); // Despierta a un hilo de esa cola específica [cite: 237]
        } finally {
            lock.unlock();
        }
    }

    public void entrarZona(int numZona) throws InterruptedException {
        lock.lock();
        try {
            hilosEsperandoZona[numZona]++;
            while (maquinasOcupadas[numZona] >= 5) {
                esperaZona[numZona].await(); 
            }
            hilosEsperandoZona[numZona]--;
            maquinasOcupadas[numZona]++;
        } finally {
            lock.unlock();
        }
    }

    public void salirZona(int numZona) {
        lock.lock();
        try {
            maquinasOcupadas[numZona]--;
            esperaZona[numZona].signal(); 
        } finally {
            lock.unlock();
        }
    }

    public void usarBiciPremium() throws InterruptedException {
        lock.lock();
        try {
            hilosEsperandoBici++;
            while (biciPremiumOcupada) {
                esperaBiciPremium.await();
            }
            hilosEsperandoBici--;
            biciPremiumOcupada = true;
        } finally {
            lock.unlock();
        }
    }

    public void liberarBiciPremium() {
        lock.lock();
        try {
            biciPremiumOcupada = false;
            esperaBiciPremium.signal();
        } finally {
            lock.unlock();
        }
    }

    public int[] getEstimacionZonas() {
        // No necesita lock porque se llama dentro de un bloque sincronizado en el Cliente
        return hilosEsperandoZona.clone();
    }

    public int getEsperaBici() {
        return hilosEsperandoBici;
    }
}

// --- CLASE HILO CLIENTE ---
class Cliente extends Thread {
    private final int id;
    private final MonitorGimnasio monitor;
    private static final String[] nombresZonas = {"Cardio", "Fuerza", "Funcional", "Estiramientos"};

    public Cliente(int id, MonitorGimnasio m) {
        this.id = id;
        this.monitor = m;
    }

    @Override
    public void run() {
        Random rand = new Random();
        try {
            // 1. Acceso por Tornos
            int tiempoTorno = rand.nextInt(5) + 1;
            int nTorno = monitor.entrarPorTorno(id);
            Thread.sleep(tiempoTorno); 
            monitor.salirTorno(nTorno);

            // 2. Elección de zona y parámetros
            int zonaElegida = rand.nextInt(4);
            int tiempoEntreno = rand.nextInt(50) + 10;
            boolean quiereBici = (zonaElegida == 0 && rand.nextDouble() < 0.3);

            // 3. Impresión obligatoria (Exclusión mutua de pantalla)
            synchronized (System.out) {
                int[] espera = monitor.getEstimacionZonas();
                System.out.println("--------------------------------------------------------------");
                System.out.println("Cliente " + id + " ha pasado por el torno: " + nTorno);
                System.out.println("Tiempo en el torno (acceso): " + tiempoTorno);
                System.out.println("Zona elegida: " + nombresZonas[zonaElegida]);
                System.out.println("Tiempo de entrenamiento: " + tiempoEntreno);
                System.out.println("Estimación de espera (sin incluirse a sí mismo):");
                System.out.printf("  Zona1(Cardio)=%d, Zona2(Fuerza)=%d, Zona3(Funcional)=%d, Zona4(Estiramientos)=%d\n", 
                                  espera[0], espera[1], espera[2], espera[3]);
                if (quiereBici) System.out.println("Espera bicicleta premium (si aplica)=" + monitor.getEsperaBici());
                System.out.println("--------------------------------------------------------------");
            }

            // 4. Proceso de Entrenamiento
            monitor.entrarZona(zonaElegida);
            
            if (quiereBici) {
                monitor.usarBiciPremium();
                Thread.sleep(tiempoEntreno); // Entrenamiento real
                monitor.liberarBiciPremium();
            } else {
                Thread.sleep(tiempoEntreno); // Entrenamiento real
            }
            
            monitor.salirZona(zonaElegida);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// --- CLASE PRINCIPAL ---
public class Ejercicio3 {
    public static void main(String[] args) {
        MonitorGimnasio monitor = new MonitorGimnasio();
        
        // Simulación con 50 hilos cliente
        for (int i = 1; i <= 50; i++) {
            new Cliente(i, monitor).start();
        }
    }
}