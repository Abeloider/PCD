import java.util.concurrent.locks.*;

// Clase que actúa como Monitor
class Gimnasio {
    private ReentrantLock l = new ReentrantLock();
    
    // Variables Condición para las colas de espera
    private Condition[] cTornos = new Condition[3];
    private Condition[] cZonas = new Condition[4];
    private Condition cBici = l.newCondition();
    
    // Estado de los recursos
    private boolean[] tornoOcupado = new boolean[3];
    private int[] colaTorno = new int[3];
    
    private int[] maquinasLibres = {5, 5, 5, 5};
    private int[] colaZona = new int[4];
    
    private boolean biciOcupada = false;
    private int colaBici = 0;
    
    public Gimnasio() {
        for (int i = 0; i < 3; i++) {
            cTornos[i] = l.newCondition();
            tornoOcupado[i] = false;
            colaTorno[i] = 0;
        }
        for (int i = 0; i < 4; i++) {
            cZonas[i] = l.newCondition();
            colaZona[i] = 0;
        }
    }
    
    public int entrarTorno() throws InterruptedException {
        l.lock();
        try {
            int tornoElegido = -1;
            int i = 0;
            
            // Busca el primer torno libre usando un while en lugar de break
            while (i < 3 && tornoElegido == -1) {
                if (!tornoOcupado[i]) {
                    tornoElegido = i;
                }
                i++;
            }
            
            // Si todos están ocupados, elige el de menor cola
            if (tornoElegido == -1) {
                tornoElegido = 0;
                for (int j = 1; j < 3; j++) {
                    if (colaTorno[j] < colaTorno[tornoElegido]) {
                        tornoElegido = j;
                    }
                }
                
                colaTorno[tornoElegido]++;
                // Bucle while de espera de la condición (obligatorio en monitores)
                while (tornoOcupado[tornoElegido]) {
                    cTornos[tornoElegido].await();
                }
                colaTorno[tornoElegido]--;
            }
            
            tornoOcupado[tornoElegido] = true;
            return tornoElegido;
        } finally {
            l.unlock();
        }
    }
    
    public void salirTorno(int idTorno) {
        l.lock();
        try {
            tornoOcupado[idTorno] = false;
            cTornos[idTorno].signal(); 
        } finally {
            l.unlock();
        }
    }
    



    
// Método que unifica la lógica de elegir zona y la impresión.
// Lo hacemos dentro del monitor para asegurar la exclusión mutua sin mezclar 'synchronized'.
public int elegirZonaEImprimir(int numCliente, int torno, int tiempoTorno, int tiempoZona) {
    
    // 1. Protocolo de entrada al monitor (Guión 3)
    // Adquirimos el cerrojo para asegurar que ningún otro hilo (cliente) 
    // cambie el estado de las máquinas o imprima a la vez.
    lock.lock(); 
    
    try {
        int zonaElegida = -1;
        int zonasLibres = 0;
        
        // PASO 1: Contar cuántas zonas tienen al menos una máquina libre (máximo 5)
        for (int i = 0; i < 4; i++) {
            if (maquinasOcupadas[i] < 5) {
                zonasLibres++;
            }
        }
        
        // PASO 2: Toma de decisión según el enunciado
        if (zonasLibres > 0) {
            // Opción A: Hay máquinas libres. Elegimos una al azar SOLO entre las que tienen hueco.
            // Si hay 2 zonas libres, randomPick será 0 o 1.
            int randomPick = (int) (Math.random() * zonasLibres);
            
            int contador = 0; // Lleva la cuenta de las zonas libres por las que hemos pasado
            int j = 0;        // Índice para recorrer las 4 zonas
            
            // Recorremos las zonas usando un 'while' para no usar 'break'.
            // Paramos si llegamos al final (j < 4) o si ya hemos encontrado la zona (zonaElegida != -1).
            while (j < 4 && zonaElegida == -1) {
                // Solo nos interesan las zonas que tienen máquinas libres
                if (maquinasOcupadas[j] < 5) {
                    // Si el contador coincide con nuestro número aleatorio, nos quedamos con esta zona
                    if (contador == randomPick) {
                        zonaElegida = j;
                    }
                    contador++; // Hemos visto una zona libre, sumamos uno
                }
                j++;
            }
        } else {
            // Opción B: Todas las máquinas (las 20) están ocupadas.
            // El enunciado obliga a elegir la zona que tenga el menor tiempo de espera (menor cola).
            zonaElegida = 0; // Asumimos por defecto que la de menor cola es la 0 (Cardio)
            
            // Comparamos el tamaño de las colas de la zona 1, 2 y 3 con la que tenemos guardada
            for (int j = 1; j < 4; j++) {
                if (filaZonas[j] < filaZonas[zonaElegida]) {
                    zonaElegida = j; // Si encontramos una cola más pequeña, actualizamos
                }
            }
        }

        // PASO 3: Impresión por pantalla en Exclusión Mutua
        // Al estar aún dentro del try (y por tanto teniendo el 'lock'), 
        // nadie nos puede interrumpir la escritura en consola.
        String[] nombresZonas = {"Cardio", "Fuerza", "Funcional", "Estiramientos"};
        System.out.println("--------------------------------------------------------------");
        System.out.println("Cliente " + numCliente + " ha pasado por el torno: " + torno);
        System.out.println("Tiempo en el torno (acceso): " + tiempoTorno);
        System.out.println("Zona elegida: " + nombresZonas[zonaElegida]);
        System.out.println("Tiempo de entrenamiento: " + tiempoZona);
        System.out.println("Estimación de espera (sin incluirse a sí mismo):");
        
        // Imprimimos el estado actual de todas las colas
        System.out.printf("  Zona1(Cardio)=%d, Zona2(Fuerza)=%d, Zona3(Funcional)=%d, Zona4(Estiramientos)=%d\n", 
                          filaZonas[0], filaZonas[1], filaZonas[2], filaZonas[3]);
        
        // La cola de la bici premium solo aplica (y solo se imprime) si va a Cardio (zonaElegida == 0)
        if (zonaElegida == 0) {
            System.out.println("Espera bicicleta premium (si aplica)=" + filabici);
        }
        System.out.println("--------------------------------------------------------------");

        return zonaElegida;
        
    } finally {
        // Protocolo de salida del monitor (Guión 3)
        // Obligatorio hacerlo en un bloque finally para asegurar que, 
        // aunque haya errores, siempre devolvemos el cerrojo y no bloqueamos el sistema.
        lock.unlock();
    }
}
    
    public void entrarZona(int zona) throws InterruptedException {
        l.lock();
        try {
            if (maquinasLibres[zona] == 0) {
                colaZona[zona]++;
                while (maquinasLibres[zona] == 0) {
                    cZonas[zona].await();
                }
                colaZona[zona]--;
            }
            maquinasLibres[zona]--;
        } finally {
            l.unlock();
        }
    }
    
    public void salirZona(int zona) {
        l.lock();
        try {
            maquinasLibres[zona]++;
            cZonas[zona].signal();
        } finally {
            l.unlock();
        }
    }
    
    public void entrarBici() throws InterruptedException {
        l.lock();
        try {
            if (biciOcupada) {
                colaBici++;
                while (biciOcupada) {
                    cBici.await();
                }
                colaBici--;
            }
            biciOcupada = true;
        } finally {
            l.unlock();
        }
    }
    
    public void salirBici() {
        l.lock();
        try {
            biciOcupada = false;
            cBici.signal();
        } finally {
            l.unlock();
        }
    }
}

// Clase Hilo Cliente
class Cliente extends Thread {
    private Gimnasio gimnasio;
    private int idCliente;
    
    public Cliente(Gimnasio g, int id) {
        this.gimnasio = g;
        this.idCliente = id;
    }
    
    public void run() {
        try {
            // Tiempos aleatorios (Math.random devuelve un double entre 0.0 y 1.0)
            int X = 1 + (int) (Math.random() * 5); 
            int Y = 10 + (int) (Math.random() * 50); 
            
            // 1. Acceso mediante tornos
            int idTorno = gimnasio.entrarTorno();
            Thread.sleep(X); 
            gimnasio.salirTorno(idTorno);
            
            // 2. Elección de zona e impresión (Zona: 0=Cardio, 1=Fuerza, 2=Funcional, 3=Estiramientos)
            int zonaElegida = gimnasio.elegirZonaEImprimir(idCliente, idTorno, X, Y);
            
            boolean usarBici = false;
            if (zonaElegida == 0 && Math.random() < 0.3) {
                usarBici = true;
            }
            
            // Entrar a la zona de máquinas
            gimnasio.entrarZona(zonaElegida);
            
            // 3. Cola exclusiva de la bicicleta si está en Cardio y desea usarla
            if (usarBici) {
                gimnasio.entrarBici();
            }
            
            // 4. Entrenamiento concurrente (fuera del monitor)
            Thread.sleep(Y); 
            
            // Salir de los recursos
            if (usarBici) {
                gimnasio.salirBici();
            }
            gimnasio.salirZona(zonaElegida);
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class MainSimulador {
    public static void main(String[] args) {
        Gimnasio gimnasio = new Gimnasio();
        
        // Crear y lanzar 50 hilos cliente
        for (int i = 1; i <= 50; i++) {
            Cliente c = new Cliente(gimnasio, i);
            c.start();
        }
    }
}