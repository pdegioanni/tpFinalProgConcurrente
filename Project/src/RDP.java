import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class RDP {

/////////Marcado inicial/////////////////////////////////////////////////////////
	private String [] transiciones;
	private String[] plazas;

	private Matriz VectorMarcadoActual, VectorExtendido, VectorSensibilizado, vectorInhibicion;
	private final Matriz matrizIncidencia, matrizInhibicion, matrizIdentidad, matrizEntrada;
	private final Matriz intervalo;
    private final Matriz vectorZ;
    private final int numeroPlazas;
	private final int numeroTransiciones;
    
	private final List<Matriz> invariantes;
	private Scanner input;
	private final long[] sensibilizadaConTiempo;
	
/**
 * Constructor de la Red de Petri con el marcado inicial leido desde archivos de texto
 *
 * */
	public RDP() {
	
	numeroTransiciones = cargarTransiciones("Project/matrices/M.I.txt");	//Extraccion de la cantidad de transiciones.
	numeroPlazas = cargarPlazas("Project/matrices/M.I.txt");				//Extraccion de la cantidad de plazas.

	// Generación de Matrices
	invariantes = cargarInvariantes("Project/matrices/InvTrans.txt");
	matrizIncidencia = new Matriz(numeroPlazas,numeroTransiciones);
	matrizInhibicion = new Matriz(numeroTransiciones,numeroPlazas);
	matrizIdentidad = new Matriz(numeroTransiciones,numeroTransiciones);
	matrizEntrada = new Matriz(numeroPlazas,numeroTransiciones);

	intervalo = new Matriz(2,numeroTransiciones);

	//Vectores
	VectorMarcadoActual = new Matriz(numeroPlazas,1);
	VectorSensibilizado = new Matriz(numeroTransiciones, 1);
	vectorInhibicion = new Matriz(numeroTransiciones, 1);
	VectorExtendido = new Matriz(numeroTransiciones, 1);
    vectorZ = new Matriz(numeroTransiciones, 1);
	
	//Carga de datos
	matrizIncidencia.cargarMatriz("Project/matrices/M.I.txt");
	matrizInhibicion.cargarMatriz("Project/matrices/M.B.txt");
	VectorMarcadoActual.cargarMatriz("Project/matrices/VMI.txt");
	intervalo.cargarMatriz("Project/matrices/IZ.txt");
	matrizIdentidad.cargarIdentidad();
	matrizEntrada.cargarMatriz("Project/matrices/M.Pre.txt");
	sensibilizadaConTiempo = new long[numeroTransiciones];
	
	//sensibilizarVectorZ();
    }
	
    private List<Matriz> cargarInvariantes(String pathI) {

		List<Matriz> invariantes = new ArrayList<>();
		try {
			input = new Scanner(new File(pathI));
			while (input.hasNextLine()) {
				String line = input.nextLine();
				Matriz inv = new Matriz(1, numeroTransiciones);

				for (int columna = 0; columna < line.length (); columna ++) {
					char c = line.charAt (columna);
					if(c == 1) inv.setDato(1, columna, 1);
				}

				invariantes.add(inv);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return invariantes;
	}
    //METODOS PRIVADOS
	//-----------------------------------------------------
	/**
	 * Este metodo devuelve la cantidad de transiciones disponibles en la red
	 * @param pathMI la ruta al archivo de texto que contiene la matriz de incidencia
	 * @return transiciones de la red
	 */
	private int cargarTransiciones(String pathMI){
		int nrotrans = 0;
		try {
			input = new Scanner(new File(pathMI));
			//while (input.hasNextLine()) {
			String line = input.nextLine();
			for (int columna = 0; columna < line.length (); columna ++) {
				char c = line.charAt (columna);
				if(c == '1' || c == '0') nrotrans ++ ;
			}
			setStringTranciones(nrotrans);
				//break;
			//}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return nrotrans;
	 }

	/**
	 * Este metodo devuelve la cantidad de plazas disponible en la red
	 * @param pathMI la ruta al archivo de texto que contiene la matriz de incidencia
	 * @return Plazas de la red
	 */
	private int cargarPlazas(String pathMI) {
		int nroPlazas = 0;
		try {
			input = new Scanner(new File(pathMI));
			while (input.hasNextLine()) {
				 input.nextLine();
				 nroPlazas ++;
			}
			setStringPlazas(nroPlazas);
		}
	  catch (IOException e) {
		  e.printStackTrace();
	  }
		return nroPlazas;
	}

	/**
	 * Completa el string de Transiciones con la cantidad correspondiente
	 * @param nroT: la cantidad de transiciones en la matriz de incidencia.
	 */
	private void setStringTranciones(int nroT) {
		transiciones = new String[nroT];
		for(int t = 1; t<nroT+1; t++) {	transiciones[t-1] = "T"+t; }
	}

	/**
	 * Completa el string de Plazas con la cantidad correspondiente
	 * @param nroP: la cantidad de plazas en la matriz de incidencia.
	 */
	private void setStringPlazas(int nroP) {
		plazas = new String[nroP];
		for(int p = 1; p<nroP+1; p++) { plazas[p-1] = "P"+p; }
	}
	/**
	 * Metodo que calcula el vector sensibilizado
	 */
	private void sensibilizarVectorE() {
		for (int i = 0; i < numeroTransiciones; i++) {
			int e = 1; //Por defecto transición sensibilizada
			for (int j = 0; j < numeroPlazas; j++) {
				if (VectorMarcadoActual.getDato(j, 0) < matrizEntrada.getDato(j, i)) {
					e = 0; //Si el valor del vector es menor al valor requerido para la transición, no se sensibiliza
				}
				VectorSensibilizado.setDato(i, 0, e);
			}
		}
	}

	/**
	 * Metodo que calcula el vector de transiciones des-sensibilizadas por los arcos inhibidores
	 */
	private void sensibilizarVectorB() {
		Matriz Q = new Matriz(numeroPlazas, 1);
		for(int i=0; i < numeroPlazas; i++) {
			if(VectorMarcadoActual.getDato(i, 0) != 0 ) {
				Q.setDato(i, 0, 1);
			}
			else {
				Q.setDato(i, 0, 0);
			}
		}
		vectorInhibicion = matrizInhibicion.getMultiplicacion(Q);
		for(int i = 0; i < vectorInhibicion.getNumFilas(); i++) {
			if(vectorInhibicion.getDato(i, 0)>1){
				vectorInhibicion.setDato(i, 0, 1);
			}
		}
		vectorInhibicion = vectorInhibicion.getComplemento();
	}

	/**
	 * Metodo que calcula el vector Z de transiciones des-sensibilizadas por tiempo
	 */
	private void sensibilizarVectorZ() {
		for(int k = 0 ; k < numeroTransiciones; k++) {
			if(!esTemporal(k)) {
				vectorZ.setDato(k,0,1); //Si la transición no es temporal, siempre está habilitada para el vector z
			}
			else{
				if((intervalo.getDato(0,k) < sensibilizadaConTiempo[k])
						&& (sensibilizadaConTiempo[k]<intervalo.getDato(1,k))) {
					vectorZ.setDato(k,0,1); //Es temporal y esta dentro del intervalo de tiempo
				}
				else{
					vectorZ.setDato(k,0,0); //Esta fuera del intervalo de tiempo
				}
			}
		}
		//vectorZ.imprimirMatriz();
	}
	
	//METODOS PUBLICOS
	//-----------------------------------------------------
	/**
	 * Metodo que sensibiliza las transiciones y carga el vector extendido
	 */
	public void sensibilizar() {
		sensibilizarVectorB();
		sensibilizarVectorE();
		sensibilizarVectorZ();
		
		VectorExtendido = VectorSensibilizado.getAnd(vectorInhibicion).getAnd(vectorZ);
	}
    
	/**
	 * Este metodo dispara una transicion de la rdp indicada por parametro
	 *@param transicion : numero de transicion a disparar.
	 *@return : -0 retorna 0 si el disparo no es exitoso.
	 *          -1 retorna 1 si el disparo es exitoso.
	 */
	public boolean disparar(int transicion){
		if(!estaSensibilizada(transicion)) {
			return false;
		}
        Matriz aux = matrizIncidencia.getMultiplicacion(matrizIdentidad.getColumna(transicion));
		VectorMarcadoActual = VectorMarcadoActual.getSuma(aux);
		sensibilizar();
		return true;
	}

	/**
	 * Este metodo muestra el vector indicado por parametro
	 * @param vector vector a imprimir.
	 */
	public void imprimirVector(Matriz vector , int Tipo) {
		//System.out.println("\n");
		if(Tipo == 0) {
			for(int n=0 ; n<vector.getNumFilas() ; n++) System.out.print(transiciones[n] +":" + vector.getDato(n, 0) +" ");
		}
		else if(Tipo > 0) {
			for(int n=0 ; n<vector.getNumFilas(); n++) System.out.print(plazas[n] +":" +  vector.getDato(n, 0) +" ");
		}
		System.out.println("\n");

	}

	/**
	 * Método que imprime el vector de transiciones sensibilizadas
	 * @param vector El vector de sensibilizado
	 * */
	public String imprimirSensibilidadas(Matriz vector) {
		//System.out.println("\n");
		StringBuilder transicionesSensibilizadas = new StringBuilder();

		for(int n=0 ; n<vector.getNumFilas() ; n++) {
			transicionesSensibilizadas.append(transiciones[n]).append(":").append(vector.getDato(n, 0)).append(" ");
		}
		return transicionesSensibilizadas.toString();
	}

	/**
	 * Método que imprime el vector de marcado actual
	 *
	 * */

	public String imprimirVectorDeMarcado(Matriz vector) {
		//System.out.println("\n");
		StringBuilder marcadoActual = new StringBuilder();
		for(int n=0 ; n<vector.getNumFilas() ; n++)
		{
			marcadoActual.append(plazas[n]).append(":").append(vector.getDato(n, 0)).append(" ");
		}
		return marcadoActual.toString();
	}

	/**
	 * @return Vector de marcado actual
	 * */

	public Matriz getVectorMA() {
		return VectorMarcadoActual;
	}

	/**
	 * Devuelve true si la transicion esta habilitada de acuerdo al vector extendido
	 * @param transicion : transicion la que se desea saber si está habilitada o no.
	 * @return verdadero estan habilitadas
	 */
	public boolean estaSensibilizada(int transicion) {
		return VectorExtendido.getDato(transicion,0) == 1;
	}

	/**
	 * Metodo que devuelve el vector con las transiciones sensibilizadas
	 * @return vector extendido
	 */
	public Matriz getSensibilizadas() {
		return VectorExtendido;
	}

	public void setNuevoTimeStamp(int transicion) {
		sensibilizadaConTiempo[transicion] = System.currentTimeMillis();
	}

	/**
	 * Metodo que inhabilita una transición por tiempo
	 * @param transicion la transición a inhabilitar
	 */

	public boolean setEsperando(int transicion) {
		//Caso 1: se quiere disparar antes del intervalo inferior
		if(sensibilizadaConTiempo[transicion] < intervalo.getDato(0,transicion)){
			//Duerme el tiempo necesario hasta estar sensibilizada
			try{
				TimeUnit.MILLISECONDS.sleep(intervalo.getDato(0,transicion) - sensibilizadaConTiempo[transicion]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				return true;
			}
		}
		//Caso 2: no se puede disparar porque ya supero el valor superior
		if(sensibilizadaConTiempo[transicion] > intervalo.getDato(1,transicion)){
			return false;
		}
		return false;
	}

	/**
	 * Metodo que habilita una transición por tiempo
	 * @param transicion la transición a habilitar
	 */
	public void resetEsperando(int transicion)
	{
		vectorZ.setDato(0, transicion, 1);
		//VectorZ.imprimirMatriz();
	}

	/**
	 * Metodo que devuelve la matriz de inhibicion
	 * @return matriz inhibicion
	 */
	public Matriz getMatrizInhibicion() {
		return vectorInhibicion;
	}

	/**
	 * @return la lista de los invariantes de transición
	 * */
	public List<Matriz> getInvariantes() {
		return invariantes;
	}

	/**
	 * @return el número de transiciones
	 * */
    public int getNumeroTransiciones() {
    	return numeroTransiciones;
    }

    /**
	 * @return la matriz de intervalos
	 * */
	public Matriz getIntervalo() {
		return intervalo;
	}

	public boolean esTemporal(int transicion){
		return (intervalo.getDato(0, transicion) - intervalo.getDato(1, transicion)) != 0;
	}

	public long [] getSensibilizadaConTiempo() {
		return sensibilizadaConTiempo;
	}

	public Matriz getVectorZ() {
		return vectorZ;
	}
}
