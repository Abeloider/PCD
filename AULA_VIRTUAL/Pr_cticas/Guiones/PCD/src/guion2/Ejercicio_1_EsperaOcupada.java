package guion2;

public class Ejercicio_1_EsperaOcupada {
	
	public static volatile int v = 0;

	public static void main(String[] args) {
		
		Thread a = new Hilo();
		Thread b = new Hilo();
		
		a.start();
		b.start();
		
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finalizando hilo principal");
		
	}
}

class Hilo extends Thread {
	
	public void run() {
		
		for (int i = 0; i < 10; i++) {
			
			while (Ejercicio_1_EsperaOcupada.v == 1) {
				; // espera activa
			}
			
//			try {
//				Thread.sleep(Math.round((Math.random() * 1000)));
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}

			Ejercicio_1_EsperaOcupada.v = 1;
			
			System.out.println("Entrando a la seccion critica " + Thread.currentThread().getName());
			
			try {
				Thread.sleep(Math.round((Math.random() * 1000)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Saliendo de la seccion critica " + Thread.currentThread().getName());
			
			Ejercicio_1_EsperaOcupada.v = 0;
			
		}
	}
}

// Se ha intentado exclusión mutua con una variable volatile y espera activa, pero no
// se garantiza que nunca entren los dos hilos a la sección crítica a la vez (puede
// haber condición de carrera al comprobar v==0 y luego poner v=1).
