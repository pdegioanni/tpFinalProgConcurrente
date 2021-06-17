package codigo;

import java.util.concurrent.Semaphore;


public class Monitor {

	//Campos
	private RDP red;
	private Cola cola; 		//cola donde se pondran los hilos
	private Politica politica;
	private boolean continuar;
	private Log log;
	private int nTransicion;
	//private Semaphore mutex;

	/**
	 * Constructor de la clase Monitor
	 */
	public Monitor(RDP red,Politica politica) {
		this.red = red; 										//la red sobre la cual se trabajara
		this.politica = politica;
		this.log = new Log();
		cola = new Cola(red.getSensibilizadas().getNumFilas());
		continuar = true;
		nTransicion = 0;
		//mutex = new Semaphore(1,true);						//el semaforo que se utilizara, solo uno puede entrar y es justo.
		red.sensibilizar();
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

		/*try {
			mutex.acquire(); //Cola de entrada al monitor

		}
		catch (Exception e) {
			e.printStackTrace();

		}*/

		boolean k = true; //Hay un proceso dentro del monitor
		Matriz m;

		while(k) {
			//System.out.printf("Hilo %s quiere disparar transicion T%d\n", Thread.currentThread().getName(), nTransicion+1);
			/*System.out.println("-----------------------------------------------------");
			red.mostrar(red.getSensibilizadas(), 0);
			red.mostrar(red.getVectorMA(), 2);
			red.mostrar(red.getMatrizInhibicion(), 0);*/

			k = red.Disparar(siguienteEnHilo);

			if(k){ //Disparo exitoso

				System.out.printf("##### Hilo %s disparo T%d\n", Thread.currentThread().getName(), siguienteEnHilo+1);
				System.out.println("Sensibilizadas");
				red.mostrar(red.getSensibilizadas(), 0);
				//red.mostrar(red.getMatrizInhibicion(), 0);
				//red.mostrar(red.getVectorMA(), 2);
				System.out.println("========================================================");

				politica.registrarDisparo(siguienteEnHilo); //Lleva la cuenta de la cantidad de veces que se dispara cada invariante.
				log.registrarDisparo("T"+ (siguienteEnHilo+1)); //Escribe en el log

				m = calcularVsAndVc(); //Ver si alguno de las transiciones que estaban en la cola de espera ahora pueden dispararse
				System.out.println("m: ");

				if (m.esNula()) {
					k = false;  //No hay transiciones esperando para disparar
					System.out.println("No hay transiciones compatibles en la cola");
					System.out.println(cola.imprimirCola()); //Imprime cola de hilos esperando
					nTransicion = -1; //Para que no avance ninguno de los hilos que estan en el wait

				}
				else { //Transiciones que estaban en la cola ahora pueden disparar
					nTransicion = politica.cual(m); //De las transiciones sensibilizadas que estan en la cola, cual deberia disparar?
					System.out.println("Nuevo numero de transicion T" + (nTransicion+1));
					System.out.println(cola.imprimirCola());
					cola.sacarDeCola(nTransicion);//Ya no esta esperando
					notifyAll(); //Despierta a todos los hilos para que tome el monitor el que este esperando por la nueva transicion definida
					k = false; //Para que salga del monitor
				}
			}
			else {
				//mutex.release();//el hilo actual libera el monitor
				//Se lo pone en la cola porque todavía no se cumplió la condición para que pueda dispararse
				boolean encola = cola.ponerEnCola(siguienteEnHilo); //Solo se agrega si no hay otro hilo esperando por la misma transicion
				if(encola){
					System.out.printf("Hilo %s esperando para disparar T%d\n", Thread.currentThread().getName(), siguienteEnHilo + 1);
					System.out.println(cola.imprimirCola());
					notifyAll(); //Despierta al resto antes de irse a dormir
					do{
						try {
							wait();
						} catch (InterruptedException e) {
							continuar = false;
							k = false;
							//notifyAll();
						}
						//System.out.printf("Hilo %s siguiente en hilo T%d, nTransicion %d\n", Thread.currentThread().getName(), siguienteEnHilo + 1, nTransicion + 1);
					} while ((siguienteEnHilo != nTransicion) || !continuar);
					if(continuar) {
						System.out.printf("Hilo %s ahora puede disparar T%d\n", Thread.currentThread().getName(), siguienteEnHilo + 1);
						k = true; //Para que dispare en vez de salir del monitor
					}
				}


			}
		}
		System.out.printf("Hilo %s deja el monitor\n", Thread.currentThread().getName());
		//mutex.release();//el hilo actual libera el monitor
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

}
