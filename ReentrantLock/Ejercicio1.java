package ejercicios;

import java.util.concurrent.locks.ReentrantLock;

public class Ejercicio1 {
	public static ReentrantLock lock=new ReentrantLock();

	public static void main (String[] args) {
		for(int i=0; i<10; i++){
		    Cuadrado c= new Cuadrado(1,2,3,4,5,6,7,8,9);
		    Suma s= new Suma(1,2,3,4,5,6,7,8,9);
		    
		    
		    c.start();
		    try {
		    	c.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    
		    s.start();
		    try {
				s.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}
}

class Cuadrado extends Thread{
	private int matriz[][];
	
	public Cuadrado(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
		this.matriz= new int[3][3];
		this.matriz[0][0]=a1;
		this.matriz[0][1]=a2;
		this.matriz[0][2]=a3;
		this.matriz[1][0]=a4;
		this.matriz[1][1]=a5;
		this.matriz[1][2]=a6;
		this.matriz[2][0]=a7;
		this.matriz[2][1]=a8;
		this.matriz[2][2]=a9;
	}
	
	public void run() {
		Ejercicio1.lock.lock();
		try{
			int suma;
			int[][] resultado= new int[3][3];
			System.out.println("Cuadrado:");
			for (int i = 0; i < 3; i++) {
	            for (int j = 0; j < 3; j++) {
	            	suma=0;
	                for (int k = 0; k < 3; k++) {
	                    suma += matriz[i][k] * matriz[k][j];
	                }
	                resultado[i][j]=suma;
					System.out.print(resultado[i][j]+" ");
	            }
				System.out.println();
	        }
			System.out.println();
		}
		finally {
			Ejercicio1.lock.unlock();
		}
	}
}

class Suma extends Thread{
	private int matriz[][];
	
	public Suma(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
		this.matriz= new int[3][3];
		this.matriz[0][0]=a1;
		this.matriz[0][1]=a2;
		this.matriz[0][2]=a3;
		this.matriz[1][0]=a4;
		this.matriz[1][1]=a5;
		this.matriz[1][2]=a6;
		this.matriz[2][0]=a7;
		this.matriz[2][1]=a8;
		this.matriz[2][2]=a9;
	}
	
	public void run() {
		Ejercicio1.lock.lock();
		try{
			System.out.println("Suma:");
			for (int i = 0; i < 3; i++) {
	            for (int j = 0; j < 3; j++) {
	                    matriz[i][j] = matriz[i][j] * 2;
						System.out.print(matriz[i][j]+" ");
	                }
	            System.out.println();
	            }
			System.out.println();
	        }
		finally {
			Ejercicio1.lock.unlock();
		}
	}
}