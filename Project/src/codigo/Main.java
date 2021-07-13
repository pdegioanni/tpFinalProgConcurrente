package codigo;


//import java.util.ArrayList;
//import java.util.List;

public class Main {

	private static final int numeroHilos = 7;
	//private static int[][] Secuencias = {{1},{2,4},{3,5},{6},{7,8,9,10}};
	private static int[][] secComunes = {{1},{6}};
	private static int[][] secInvariante = {{2,4},{3,5},{7,8,9,10}};
	private static Hilo[] hilos;
	private static Thread[] threads;
	//private static String[] Nombres = {"1","2","3","4","5","6", "7"};
	private static final int tiempoCorrida =30000; // 60000; //milisegundos
	private static RDP redDePetri;
	//private List<int[]> invariantes;

	public static void main(String[] args) {
		iniciarPrograma();
    }

	public static void iniciarPrograma(){
		 hilos = new Hilo[numeroHilos];

		redDePetri = new RDP();
		Politica politica = new Politica(secInvariante);
		Monitor monitor = new Monitor(redDePetri,politica);

		threads = new Thread[numeroHilos];

		hilos[0] = new Hilo("T1", monitor, secComunes[0]);
		hilos[1] = new Hilo("T2T4", monitor, secInvariante[0]);
	    hilos[2] = new Hilo("T3T5", monitor, secInvariante[1]);
		hilos[3] = new Hilo("T6", monitor, secComunes[1]);
		hilos[4] = new Hilo("T7T8T9T10", monitor, secInvariante[2]);
		hilos[5] = new Hilo("T7T8T9T10", monitor, secInvariante[2]);
		hilos[6] = new Hilo("T7T8T9T10", monitor, secInvariante[2]);
		/*for(int i = 4; i<numeroHilos; i++){
			hilos[i] = new Hilo("T7T8T9T10", monitor, secInvariante[2]);
		}*/
        
   
		for(int i=0; i<numeroHilos;i++) {
			threads[i] = new Thread(hilos[i], "" +i);
		}
		//threads[1].setPriority(Thread.MAX_PRIORITY);
		//threads[2].setPriority(Thread.MAX_PRIORITY);

		for(int i=0; i<numeroHilos;i++) {
			threads[i].start();
		}
	     
	   /*  threads[0].start();
	       threads[1].start();
	       threads[2].start();
	       threads[3].start();
	       threads[4].start();*/
	       //threads[5].start();
	     try {
				Thread.sleep(tiempoCorrida);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error al intentar dormir el hilo principal");
			}
	     
			for(int k=0;k<numeroHilos;k++) {
				hilos[k].set_Fin();;
			}
			for(int k=0;k<numeroHilos;k++) {
				threads[k].interrupt();;
			}
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error al intentar dormir el hilo principal");
			}
			politica.imprimirVecesPorInvariante();
			politica.imprimirDisparos();
			System.out.println("FIN");
	}
}
