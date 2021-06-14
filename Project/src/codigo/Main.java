package codigo;


public class Main {

	private static Thread threads[];
	private static int[] Secuencias[]= {{1},{2,4},{3,5},{6},{7,8,9,10}};
	private static final int numeroHilos = 5;
	private static RDP redDePetri;
	private static Hilo hilos[];
	private static String Nombres[] = {"1","2","3","4","5","6"};
	private static final int tiempoCorrida = 600000; //millisegundo
	
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
				hilos[i] = new Hilo(Nombres[i],monitor,Secuencias[i]);
				threads[i] = new Thread(hilos[i]);
			}
	     
	     threads[0].start();
	     threads[1].start();
	     threads[2].start();
	     threads[3].start();
	     threads[4].start();
	    /* for(int j=0;j<numeroHilos;j++) {
				threads[j].start();
			}*/
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
				};
			}
			System.out.println("FIN");
	}
}
