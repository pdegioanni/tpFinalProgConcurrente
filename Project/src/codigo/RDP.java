package codigo;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


//import Monitor.Matriz;
//import Monitor.SensibilizadasConTiempo;

public class RDP {

/////////Marcado inicial/////////////////////////////////////////////////////////
	
//private int [] VE  = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; // Indica el numero de transciones sensibilizadas
//private String[] Plazas = {"P1","P10","P11","P12","P13","P2","P3","P4","P5","P6","P7","P8","P9"};
//private String [] Transiciones = {"T1","T10","T2","T3","T4","T5","T6","T7","T8","T9"};
// private int [] Places = {0,1,10,13,14,15,17,18,19,20,22,23,24,25,26,27,28,29,3,30,31,4,5,8,9};
	private String [] Transiciones;
	private String[] Plazas;

	private Matriz VectorMarcadoActual, VectorExtendido, VectorSensibilizado, VectorInhibicion;
	private Matriz Incidencia,Inhibicion,Identidad;
	private Matriz Intervalo;
    private Matriz VectorZ;
	private final int numeroPlazas;
	private final int numeroTransiciones;
	private Scanner input;
	private long clk [] ,Q[]; 
	//private SensibilizadasConTiempo gestionarTiempo;
	private Matriz IEntrada ;//,ISalida,
	//private Matriz VectorMarcadoInicial,  VectorMarcadoNuevo;

////////////////////////////////////////////////////////////////////////////////////////////////////
//EL VALOR ACTUAL ES IGUAL AL INICIAL
////Constructor
public RDP() {
	
	numeroTransiciones = cargarTransiciones("matrices/M.I.txt");	//Extraccion de la cantidad de transiciones.
	numeroPlazas = cargarPlazas("matrices/M.I.txt");				//Extraccion de la cantidad de plazas.

	//Matrices
	Incidencia = new Matriz(numeroPlazas,numeroTransiciones);
	Inhibicion = new Matriz(numeroTransiciones,numeroPlazas);
	Identidad = new Matriz(numeroTransiciones,numeroTransiciones);
	IEntrada = new Matriz(numeroPlazas,numeroTransiciones);
	Intervalo = new Matriz(2,numeroTransiciones);
	//ISalida = new Matriz(numeroPlazas,numeroTransiciones);

	//Vectores
	VectorMarcadoActual = new Matriz(numeroPlazas,1);
	VectorSensibilizado = new Matriz(numeroTransiciones, 1);
	VectorInhibicion = new Matriz(numeroTransiciones, 1);
	VectorExtendido = new Matriz(numeroTransiciones, 1);
	VectorZ = new Matriz(1,numeroTransiciones);
	
    //Carga de datos
	Incidencia.cargarMatriz("matrices/M.I.txt");
	Inhibicion.cargarMatriz("matrices/M.B.txt");
	VectorMarcadoActual.cargarMatriz("matrices/VMI.txt");
	Intervalo.cargarMatriz("matrices/IZ.txt");
	Identidad.cargarIdentidad();
	IEntrada.cargarMatriz("matrices/M.Pre.txt");
	Q = new long[numeroTransiciones];
	clk = new long[numeroTransiciones];
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
		//System.out.println("Plazas -->"+Plazas);
		return nroPlazas;
	}
	/**
	 * Completa el string de Transiciones con la cantidad correspondiente
	 * @param nroT: la cantidad de transiciones en la matriz de incidencia.
	 */
	private void setStringTranciones(int nroT) {
		Transiciones = new String[nroT];
		int j = 0;
		for(int t = 0; t<nroT; t++) {
			j = t + 1;
			Transiciones[t] = "T"+j;
		}
	}
	/**
	 * Completa el string de Plazas con la cantidad correspondiente
	 * @param nroP: la cantidad de plazas en la matriz de incidencia.
	 */
	private void setStringPlazas(int nroP) {
		Plazas = new String[nroP];
		int j = 0;
		for(int p = 0; p<nroP; p++)
			{
			  j = p+1;
			  Plazas[p] = "P"+j;
			}
	}
	/**
	 * Metodo que calcula el vector Inhibicion
	 */
	private void sensibilizarVectorB() {
		Matriz Q = new Matriz(numeroPlazas, 1);
		for(int i=0; i < Q.getNumFilas(); i++) {
			
			 if(VectorMarcadoActual.getDato(i, 0) != 0 ) Q.setDato(i, 0, 1);
			else Q.setDato(i, 0, 0);

		}
		//Inhibicion.imprimirMatriz();
		VectorInhibicion = Inhibicion.getMultiplicacion(Q);
		for(int i=0; i < VectorInhibicion.getNumFilas(); i++) {
			if(VectorInhibicion.getDato(i, 0)>1)VectorInhibicion.setDato(i, 0, 1);
		}
		VectorInhibicion = VectorInhibicion.getComplemento();
		//VectorInhibicion.imprimirMatriz();
	}
	/**
	 * Metodo que calcula el vector sensibilizado
	 */
	private void sensibilizarVectorE() {
		
		
		//Incidencia.imprimirMatriz();
	//	System.out.println(Incidencia.getNumColumnas());
		//System.out.println(Incidencia.getNumFilas());
		//VectorSensibilizado.imprimirMatriz();
		/*for(int j=0; j<Incidencia.getNumColumnas(); j++){
			for(int i = 0; i< Incidencia.getNumFilas(); i++){
				
				if((Incidencia.getDato(i,j) == -1) && (VectorMarcadoActual.getDato(0,i) > 0)) 
					{
					System.out.println("true");
					VectorSensibilizado.imprimirMatriz();
					VectorSensibilizado.setDato(j,0, 1);
					VectorSensibilizado.imprimirMatriz();
					//break;
					}
					
				else { 
					System.out.println("false");
					VectorSensibilizado.imprimirMatriz();
					VectorSensibilizado.setDato(j, 0, 0);
				
				}
			}
		}*/
		
		for (int i = 0; i < IEntrada.getNumColumnas(); i++) {
			int e = 1;
			for (int j = 0; j < Incidencia.getNumFilas(); j++) {
				if (VectorMarcadoActual.getDato(j, 0) < IEntrada.getDato(j, i)) {
					
					e = 0;
				}
				VectorSensibilizado.setDato(i, 0, e);
			}
		}
		
		//VectorSensibilizado.imprimirMatriz();
	}
	//METODOS PUBLICOS
	//-----------------------------------------------------
	/**
	 * Metodo que sensibiliza las transiciones y carga el vector extendido
	 */
	public void sensibilizar() {
		//VectorMarcadoActual.imprimirMatriz();
		sensibilizarVectorB();
		sensibilizarVectorE();
		sensibilizarVectorZ();
		VectorExtendido = VectorSensibilizado.getAnd(VectorInhibicion).getAnd(VectorZ.getTranspuesta());
	}
    public void sensibilizarVectorZ() {
	    Matriz Tim  = new Matriz(1,numeroTransiciones); 
		Matriz Auxiliar  = new Matriz(1,numeroTransiciones);
		Auxiliar =VectorSensibilizado.getAnd(VectorInhibicion);;
		int flag = 0;
		for(int fila = 0 ; fila<numeroTransiciones; fila++) {
	         if((Auxiliar.getDato(fila,0) == 1 ) 
	        	&& (Intervalo.getDato(1, fila)-Intervalo.getDato(0, fila) > 0)){ //(Beta - Alpha) es mayor a cero y se sensibilizo.
	        	 System.out.println("Sensibilizad : "+Intervalo.getDato(1, fila) + " tansicion " + (fila+1));
	        	 System.out.println("---> " +Intervalo.getDato(1, fila) + " -- "+ Intervalo.getDato(0, fila));
	        	 System.out.println("Transiciones con tiempo :"+ fila);
	        	 if(Q[fila] == 0 ) {
				  		clk[fila] = System.currentTimeMillis();
				  		Q[fila] = System.currentTimeMillis();
				  		flag = 1;
				  		
			       }
				else{						
						Q[fila] =System.currentTimeMillis(); //Tiempo transcurrido desde que se inicio el contador 
						if((Q[fila] -clk[fila])!=0) {
						Q[fila] = Q[fila] -clk[fila];
					}
				}
			}
			else Q[fila] = 0;
		}
		for(int k = 0 ; k<numeroTransiciones; k++) {
		    if( (Q[k]<= (Intervalo.getDato(1, k)*1000)) && (Auxiliar.getDato(k,0) == 1) && (Q[k] !=0) )
					
			{
				System.out.println("Entro");
				Tim.setDato(0, k, 0);
			}
			else if( (Q[k] > (Intervalo.getDato(1, k)*1000)) && (Auxiliar.getDato(k,0) == 1) && (Q[k] !=0) &&  (flag != 1))
			{
				System.out.println("Caso especial");
				Q[k]=0;
				Tim.setDato(0, k, 1);
			}
			else
		    { 
				//Q[k]=0;
				if((flag == 1) && (Q[k] > 0) )
				{
					System.out.println("setear");
					Tim.setDato(0, k, 0);
				}
				else {
				Tim.setDato(0, k, 1);
			
				}
		    }//
		}
		
		VectorZ = Tim;
		System.out.println("VectorZ");
		VectorZ.imprimirMatriz();
	}
    
	/**
	 * Este metodo dispara una transicion de la rdp indicada por parametro, teniendo en cuenta el modo indicado por parametro
	 *@param transicion : numero de transicion.
	 *@return : -0 retorna 0 si el disparo no es exitoso.
	 *          -1 retorna 1 si el disparo es exitoso.
	 */
	public boolean Disparar(int transicion){
		if(!estaSensibilizada(transicion))
			{
			 System.out.println("ACa" + transicion );
			 sensibilizar();
			 return false;
			}
		Matriz aux = Incidencia.getMultiplicacion(Identidad.getColumna(transicion)); //Probleamas con el numero de la transicion
		VectorMarcadoActual = VectorMarcadoActual.getSuma(aux);
		System.out.println("sensibilizar");
		sensibilizar();
		return true;
	}
	//public Matriz getVectorZ() { return VectorZ; }
	public void imprimirQ(){ 
		System.out.println("....Q....");
		for(int i = 0 ;i< Q.length ; i++) {
			System.out.print(Q[i]+ " ");
		}
		System.out.println();
		System.out.println(".....clk....");
		for(int i = 0 ;i< clk.length ; i++) {
			System.out.print(clk[i]+ " ");
		}
		System.out.println();
		VectorZ.imprimirMatriz();
		System.out.println("====================================");
		}
	public Matriz getVectorZ() { return VectorZ; }
	/**
	 * Devuelve true si la transicion esta habilitada de acuerdo al vector extendido
	 * @param transicion : transicion la que se desea saber si está habilitada o no.
	 * @return verdadero estan habilitadas
	 */
	public boolean estaSensibilizada(int transicion) 
	{ 
		if(VectorExtendido.getDato(transicion,0) == 1) 
			return true; 
		else return false;
	}
	/**
	 * Metodo que devuelve el vector con las transiciones sensibilizadas
	 * @return vector extendido
	 */
	public Matriz getSensibilizadas() { return VectorExtendido; }
	/**
	 * Metodo que devuelve la matriz de inhibicion
	 * @return matriz inhibicion
	 */
	public Matriz getMatrizInhibicion() { return VectorInhibicion; }
	/**
	 * Este metodo muestra el vector indicado por parametro
	 * @param vector vector a imprimir.
	 */
	public void mostrar(Matriz vector ,int Tipo) {
			//System.out.println("\n");
			if(Tipo == 0) {
				for(int n=0 ; n<vector.getNumFilas() ; n++) System.out.print(Transiciones[n] +":" + vector.getDato(n, 0) +" ");
			}
			else if(Tipo > 0) {
				for(int n=0 ; n<vector.getNumFilas(); n++) System.out.print(Plazas [n] +":" +  vector.getDato(n, 0) +" ");
			}
			System.out.println("\n");
	}

	public Matriz getVectorMA() { return VectorMarcadoActual; }
	public int numero_t() { return numeroTransiciones; }
}
