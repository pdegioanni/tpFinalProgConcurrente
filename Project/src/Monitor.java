import java.util.concurrent.Semaphore;

public class Monitor {

	//Campos
	private final RDP red;
	private final Cola cola; 		//cola donde se pondran los hilos
	private final Politica politica;
	private final Log log;
	private final Log consola;
	private final SensibilizadaConTiempo temporizadas;
	private final Semaphore mutex;
	private int nTransicion;
	//private boolean retornaDelSleep;

	/**
	 * Constructor de la clase Monitor
	 * @param red  Red de petri que va a determinar el estado del sistema
	 * @param politica la política que determina qué trnasición se dispara en caso de conflicto
	 */
	public Monitor(RDP red,Politica politica) {
		System.out.println("COMIENZO DEL MONITOR");
		this.red = red;
		this.politica = politica;

		cola = new Cola(red.getNumeroTransiciones());
		mutex = new Semaphore(1,true); //Semáforo binario que determina si el monitor está ocupado o no
		temporizadas = new SensibilizadaConTiempo(red);
	//	retornaDelSleep = false;
		nTransicion = 0;

		this.log = new Log("log1.txt");
		this.consola = new Log("log2.txt");

		red.sensibilizar();
	}

	public void dispararTransicion(int siguienteEnHilo) {
		try{
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		boolean k = true;

		while(k){
			if(red.esTemporal(siguienteEnHilo)) {
				boolean retornaDelSleep = false;
				red.setNuevoTimeStamp(siguienteEnHilo); //Registra el momento en el que la transición quiere dispararC
				red.sensibilizar(); //Calcula el vector extendido para ver si la transicion temporal se puede disparar

				if(!red.estaSensibilizada(siguienteEnHilo)){ //Si esta fuera de la ventana temporal, devuelve el mutex y se va a dormir
					mutex.release();
					//Si no esta sensibilizada porque el contador es anterior a alpha hace un sleep
					retornaDelSleep = red.setEsperando(siguienteEnHilo);
				}

				//Luego del sleep, comprueba si ahora puede disparar. Si retornaDelSleep es falso significa que la
				//razon por la que no estaba sensibilizada no era relacionada al tiempo
				if(retornaDelSleep && red.estaSensibilizada(siguienteEnHilo)){
					try {
						mutex.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					k = false;
					break;
				}

				//retornaDelSleep = true;
				//System.out.println("Viene de un sleep");
				//System.out.println("Hilo "+ Thread.currentThread().getName() +" "+ (siguienteEnHilo+1));
				//temporizadas.resetEsperando(siguienteEnHilo);
				//System.out.println("Despues de un sleep");
				//red.imprimirVector(red.getSensibilizadas(),0);
				//System.out.println("=========================");
			}

		/*if(retornaDelSleep) {
			//System.out.println("Viene de un sleep");
			//System.out.println("Hilo "+ Thread.currentThread().getName() +" "+ (siguienteEnHilo+1));
			retornaDelSleep = false;
			temporizadas.resetEsperando(siguienteEnHilo);
			//System.out.println("Despues de un sleep");
			red.sensibilizar();
			red.imprimirVector(red.getSensibilizadas(),0);
			//System.out.println("=========================");
		}*/
				red.imprimirVector(red.getSensibilizadas(),0);
				red.imprimirVector(red.getVectorMA(), 2);

				//Esto no deberia ir adentro del if de abajo? solo registarlo en el log si se pudo hacer el disparo
				consola.registrarDisparo(red.imprimirSensibilidadas(red.getSensibilizadas()) +" Disparo "+(siguienteEnHilo+1),2);
				consola.registrarDisparo(red.imprimirVectorDeMarcado(red.getVectorMA()),2);//+"\n");

			k = red.disparar(siguienteEnHilo);// Hilo "+ Thread.currentThread().getName()
				System.out.println("#################################################  Disparo T"+ (siguienteEnHilo+1));
				if(k) {
					politica.registrarDisparo(siguienteEnHilo);
					if((siguienteEnHilo+1) == 10)
						log.registrarDisparo("T"+0,0);
					else 	log.registrarDisparo("T"+(siguienteEnHilo+1),0);

					Matriz m = calcularVsAndVc();
					//Para que salga del monitor
					if (!m.esNula()) { //Transiciones que estaban en la cola ahora pueden disparar
						nTransicion = politica.cual(m); //De las transiciones sensibilizadas que estan en la cola, cual deberia disparar?
						//System.out.println("TRANSICION A DESPERTAR :::::::> " + (nTransicion+1));
						cola.sacarDeCola(nTransicion);//Ya no esta esperando
					}
					k = false;  //No hay transiciones esperando para disparar
				}
				else {
					System.out.println("Encolar Transicion "+ (siguienteEnHilo+1));
					boolean agregado = cola.ponerEnCola(siguienteEnHilo); //solo se agrega si no hay otro hilo esperando por la misma transicion
					if(agregado) {
						mutex.release();
						cola.ponerEnCola(siguienteEnHilo);
						try {
							mutex.acquire();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						k = true;
					//System.out.println("Hilo ->"+ Thread.currentThread().getName()  );
					}
					else k=false;
				}
			}
		    //System.out.println("Saliendo : " + Thread.currentThread().getName());
			mutex.release();
		}
	/**
	 * Metodo que realiza la operacion And entre Vs y Vc, luego 
	 * se examina la matriz resultante y devuelve 1 o 0
	 * @return 
	 */
	public Matriz calcularVsAndVc(){
		Matriz Vs = red.getSensibilizadas();
		Matriz Vc = cola.quienesEstan();
		return Vs.getAnd(Vc);
	}

}
