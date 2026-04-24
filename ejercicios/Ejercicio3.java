//no utilizamos el monitor con synchronized ya que este solo nos permite disponer de una cola de espera

package ejercicios;

import java.util.Random;
import java.util.concurrent.locks.*;


class Monitores{
	
	// creamos el candado lock 
	private ReentrantLock lock = new ReentrantLock(); 
	
	// creamos la zona de tornos, la zona de maquinas y la zona de bici premium 
    private Condition[] tornos = new Condition[3];
    private Condition[] maquinas = new Condition[4];
	private Condition biciPremium = lock.newCondition(); 
		
	// vemos si de los 3 tornos esta ocupado o no 
	private boolean[] tornoOcupado = new boolean[3]; 
	// vemos si en la zona de maquinas ha llegado a 5 maquinas ocupadas como maximo 
	private int[] maquinasOcupadas = new int[4]; 
	// de momento la bici esta libre
	private boolean biciPremiumOcupada = false; 

    // Contadores para las estadísticas 
    private final int[] filaTornos = new int[3]; // 
    private final int[] filaZonas = new int[4];
    private int filabici = 0;

    public Monitores() {
        for (int i = 0; i < 3; i++) tornos[i] = lock.newCondition();
        for (int i = 0; i < 4; i++) maquinas[i] = lock.newCondition();
    }
	
	// metodo para acceder a los tornos 
	public int entrarTorno() throws InterruptedException {
		lock.lock(); 
		try {
			// elegimos el mejor torno 
            int mejorTorno = 0;
            // cogemos el que menor tiempo de espera (en clientes) tenga
			for (int i = 0; i < 3; i++) {
                if (filaTornos[i] < filaTornos[mejorTorno]) {
                    mejorTorno = i;
                }
            }
            // incrementamos el contador de la fila del torno
            filaTornos[mejorTorno]++;
            // mientras que el torno este ocupado nos esperamos en la cola 
            while (tornoOcupado[mejorTorno]) {
                tornos[mejorTorno].await();
            } 
            // cuando el torno este libre decrementamos el contador 
            filaTornos[mejorTorno]--;
            // ocupamos el torno elegido
            tornoOcupado[mejorTorno] = true;
            // devolvemos el numero del torno (1, 2 o 3)
            return mejorTorno + 1; // Devolvemos el número del torno (1, 2 o 3)
		}finally {
			lock.unlock();
		}
 	}
	// metodo para salir torno 
	public void salirTorno(int numTorno) {
        lock.lock();
        try {
            // implementamos la logica de salir del torno 
            tornoOcupado[numTorno - 1] = false;
            tornos[numTorno - 1].signal(); 

        } finally {
            lock.unlock();
        }
    }

	
// METODO PARA ENTRAR A LA ZONA DE MAQUINAS
	public void entrarZona(int zona) throws InterruptedException {
		lock.lock();
		try {
			filaZonas[zona]++;
			while(maquinasOcupadas[zona] >= 5){ {
				maquinas[zona].await();
			}
			filaZonas[zona]--;
			maquinasOcupadas[zona]++;
		}
	}finally{
			lock.unlock();
		}
	}
// METODO PARA SALIR DE LA ZONA DE MAQUINAS
	public void salirZona(int zona) throws InterruptedException {
		lock.lock();
		try {
			maquinasOcupadas[zona]--;
			maquinas[zona].signal();
		} 
		finally {
			lock.unlock();
		}
	}

// METODO PARA USAR LA BICI PREMIUM
	public void usarBiciPremium() throws InterruptedException{
		lock.lock();
		try {
			filabici++;
			while(biciPremiumOcupada) {
				biciPremium.await();
			}
			filabici--;
			biciPremiumOcupada = true;
		}
		finally{
			lock.unlock();
		}
	}
// METODO PARA LIBERAR LA BICI PREMIUM
	public void liberarBiciPremium() throws InterruptedException{
		lock.lock();
		try {
			biciPremiumOcupada = false;
			biciPremium.signal(); 
		}
		finally{
			lock.unlock();
		}
	}

	public int[] getTiempoEspera() {
		return filaZonas.clone(); // Devolvemos una copia del array para evitar modificaciones externas
	}
	public int getEsperaBici() {
		return filabici;
	}
}
// CLASE HILO CLIENTE 
class cliente extends Thread{
	private int num;
	private Monitores monitor;
	private String[] nombresZonas = {"Cardio", "Fuerza", "Funcional", "Estiramientos"};
	
	public cliente(int num, Monitores monitor) {
		this.num = num;
		this.monitor = monitor;
	}

	public void run(){
		Random rand = new Random();
		try {
			int tiempoTorno=rand.nextInt(5)+1;
			int torno=monitor.entrarTorno();
			Thread.sleep(tiempoTorno);//Esperamos el tiempo señalado de validación en el torno
			monitor.salirTorno(torno);
			int zonaElegida = rand.nextInt(4); // Elegimos una de las 4 zonas al azar
			int tiempoZona = rand.nextInt(50) + 1; // Tiempo de uso en la zona 
			boolean usoBici=false;
			if(zonaElegida==0) { // Si elige la zona de cardio, tiene un 50% de probabilidad de usar la bici premium
				usoBici = rand.nextInt(100)<30;
			}
			//Imprimir
			synchronized (System.out) {
                int[] espera = monitor.getTiempoEspera();
                System.out.println("--------------------------------------------------------------");
                System.out.println("Cliente " + num + " ha pasado por el torno: " + torno);
                System.out.println("Tiempo en el torno (acceso): " + tiempoTorno);
                System.out.println("Zona elegida: " + nombresZonas[zonaElegida]);
                System.out.println("Tiempo de entrenamiento: " + tiempoZona);
                System.out.println("Estimación de espera (sin incluirse a sí mismo):");
                System.out.printf("  Zona1(Cardio)=%d, Zona2(Fuerza)=%d, Zona3(Funcional)=%d, Zona4(Estiramientos)=%d\n", 
                                  espera[0], espera[1], espera[2], espera[3]);
                if (quiereBici) System.out.println("Espera bicicleta premium (si aplica)=" + monitor.getEsperaBici());
                System.out.println("--------------------------------------------------------------");
            }



			monitor.entrarZona(zonaElegida);
			if(usoBici){
				monitor.usarBiciPremium();
				Thread.sleep(tiempoZona); // Tiempo de entrenamiento
				monitor.liberarBiciPremium();
			}
			else {
				Thread.sleep(tiempoZona); // Tiempo de entrenamiento
			}
			monitor.salirZona(zonaElegida);
		} catch (InterruptedException e) {
		}
	}
}





public class Ejercicio3 {
    public static void main(String[] args) {
        Monitores monitor = new Monitores();
        
        // Simulación con 50 hilos cliente
        for (int i = 1; i <= 50; i++) {
            new cliente(i, monitor).start();
        }
    }
}