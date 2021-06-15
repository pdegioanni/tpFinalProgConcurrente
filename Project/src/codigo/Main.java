package codigo;


public class Main {

	private static final int numeroHilos = 7;
	private static int[][] Secuencias = {{1},{2,4},{3,5},{6},{7,8,9,10}};
	private static Hilo[] hilos;
	private static Thread[] threads;
	//private static String[] Nombres = {"1","2","3","4","5","6", "7"};
	private static final int tiempoCorrida = 1000; //milisegundos
	private static RDP redDePetri;

	public static void main(String[] args) {
		iniciarPrograma();
    }

	public static void iniciarPrograma(){
		 hilos = new Hilo[numeroHilos];
		 redDePetri = new RDP();
		 Politica politica = new Politica(numeroHilos);
	     Monitor monitor = new Monitor(redDePetri,politica);
	     threads = new Thread[numeroHilos];
	     
	     for(int i=0; i<numeroHilos;i++) {
				if(i<4) hilos[i] = new Hilo("" +i,monitor, Secuencias[i]);
			 	else hilos[i] = new Hilo( "" +i,monitor, Secuencias[4]);
				threads[i] = new Thread(hilos[i], "" +i);
			}
	     /*
	     threads[0].start();
	     threads[1].start();
	     threads[2].start();
	     threads[3].start();
	     threads[4].start();*/
	    for(int j=0;j<numeroHilos;j++) {
	    	threads[j].start();
	    }
	     try {
				Thread.sleep(tiempoCorrida);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error al intentar dormir el hilo principal");
			}
			for(int k=0;k<numeroHilos;k++) {
				hilos[k].set_Fin();
			}
			for(int k=0;k<numeroHilos;k++) {
				try {
					threads[k].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("FIN");
	}
}
