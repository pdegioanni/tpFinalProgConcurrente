package codigo;

import java.util.concurrent.Semaphore;


public class Monitor {

	//Campos
	private RDP red;
	private Log log;
	private Semaphore mutex;
	//private Object lockTransicion;
	private int nTransicion;
	//private Semaphore W;
	private Col cola; 			//cola donde se pondran los hilos
	private Politica politica;
	private boolean continuar;
	//private Matriz and;			//matriz que contiene el resultado Vc&Vs
	/**
	 * Constructor de la clase Monitor
	 */
	public Monitor(RDP red,Politica politica) {
		continuar = true;
		this.log = new Log();
		this.politica = politica;
		this.red = red; 										//la red sobre la cual se trabajara
		red.sensibilizar();
		cola = new Col(red.getSensibilizadas().getNumFilas());
		mutex = new Semaphore(1,true);							//el semaforo que se utilizara, solo uno puede entrar y es justo.
		//lockTransicion = new Object();
		nTransicion = 0;
	}
     ////////////////////Metodos
	 /**
     * Este metodo dispara una transicion de la rdp indicada por parametro, teniendo en cuenta el modo indicado por parametro
     * y recalcula el vector de sensibilizadas tambien tiene en cuenta si la transicion a disparar esta o no sensibilizada.
     *@param siguienteEnHilo numero de transicion.
     *@return : -0 retorna 0 si el disparo no es exitoso. 
     *          -1 retorna 1 si el disparo no es exitoso.
     */
	public synchronized void dispararTransicion(int siguienteEnHilo) {
		//System.out.println("Se va a disparar: T"+ n_transicion);
		
		/*try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		try {
			mutex.acquire(); //Cola de entrada al monitor
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		boolean k = true; //Hay un proceso dentro del monitor
		Matriz m;
		//nTransicion = siguienteEnHilo;
		//setNTransicion(siguienteEnHilo);

		while(k) {
			//System.out.printf("Hilo %s quiere disparar transicion T%d\n", Thread.currentThread().getName(), nTransicion+1);
			/*System.out.println("-----------------------------------------------------");
			red.mostrar(red.getSensibilizadas(), 0);
			red.mostrar(red.getVectorMA(), 2);
			red.mostrar(red.getMatrizInhibicion(), 0);*/

			k = red.Disparar(siguienteEnHilo);//, modo_de_disparo);
			if(k){ //Disparo exitoso
				System.out.printf("##### Hilo %s disparo T%d\n", Thread.currentThread().getName(), siguienteEnHilo+1);
				System.out.println("Sensibilizadas");
				red.mostrar(red.getSensibilizadas(), 0);
				//red.mostrar(red.getMatrizInhibicion(), 0);
				//red.mostrar(red.getVectorMA(), 2);
				System.out.println("========================================================");

				//Ver si alguno de las transiciones que estaban en espera ahora pueden dispararse
				politica.registrarDisparo(siguienteEnHilo);
				log.registrarDisparo("T"+ (siguienteEnHilo+1));
				m = calcularVsAndVc();
				System.out.println("m: ");
				//red.mostrar(m, 0);
				if (m.esNula()) {
					k = false;  //No hay transiciones esperando para disparar
					System.out.println("No hay transiciones compatibles en la cola");
					System.out.println(cola.imprimirCola());
					nTransicion = -1;
					//nTransicion = politica.cual(red.getSensibilizadas());//que hilo?
					//setNTransicion(politica.cual(m));
					//System.out.println("Nuevo numero de transicion T" + (nTransicion+1));
					//notifyAll();
				}
				else {
					nTransicion = politica.cual(m);//que hilo?
					//setNTransicion(politica.cual(m));
					System.out.println("Nuevo numero de transicion T" + (nTransicion+1));
					System.out.println(cola.imprimirCola());
					cola.sacarDeCola(nTransicion);//este metodo obtiene el siguiente hilo debido a que el anterior disparó exitosamente
					notifyAll(); //Despierta a todos los hilos para que tome el monitor el siguiente hilo que estaba esperando
					//do{
						/*try{
							System.out.printf("Hilo %s a dormir\n", Thread.currentThread().getName());
							wait(2000);
						} catch (InterruptedException e){
							e.printStackTrace();
						}
						System.out.println("zombie");
						red.mostrar(m, 0);*/
					//} while (!m.esNula());
					k = false; //Para que libere el monitor una vez que no queden hilos en la cola
				}
			}
			else {
				cola.ponerEnCola(siguienteEnHilo);//Se lo pone en la cola porque todavía no se cumplió la condición para que pueda dispararse
				System.out.printf("Hilo %s esperando para disparar T%d\n", Thread.currentThread().getName(), siguienteEnHilo + 1);
				//mutex.release();//el hilo actual libera el monitor
				System.out.println(cola.imprimirCola());
				notifyAll();
				do{
					try {
						wait();
					} catch (InterruptedException e) {
						continuar = false;
						notifyAll();
					}
					//System.out.printf("Hilo %s siguiente en hilo T%d, nTransicion %d\n", Thread.currentThread().getName(), siguienteEnHilo + 1, nTransicion + 1);
				} while ((siguienteEnHilo != nTransicion) || (!continuar));
				//if(!continuar) {
					System.out.printf("Hilo %s ahora puede disparar T%d\n", Thread.currentThread().getName(), siguienteEnHilo + 1);
					k = true;
				//}
			}
		}
		System.out.printf("Hilo %s deja el monitor\n", Thread.currentThread().getName());
		mutex.release();//el hilo actual libera el monitor
	}
	/**
	 * Metodo que realiza la operacion And entre Vs y Vc, luego 
	 * se examina la matriz resultante y devuelve 1 o 0
	 * @return 
	 */
	public Matriz calcularVsAndVc(){
		Matriz Vs = red.getSensibilizadas();
		Matriz Vc = cola.quienesEstan();
		Matriz m = Vs.getAnd(Vc);
		return m;
	}
	public void setFin(){
		continuar = false;
		notifyAll();
	}
	/*private void setNTransicion(int nuevaTransicion){
		synchronized (lockTransicion) {
			nTransicion = nuevaTransicion;
		}
	}
	private int getNTransicion(){
		synchronized (lockTransicion) {
			return nTransicion;
		}
	}*/
	/*void imprimir() {
			System.out.println("----------------------------------MONITOR----------------------------------\n");
     }*/

}
