package codigo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


//import Monitor.Matriz;
//import Monitor.SensibilizadasConTiempo;

public class RDP {

/////////Marcado inicial/////////////////////////////////////////////////////////
	private String [] Transiciones;
	private String[] Plazas;

	private Matriz VectorMarcadoActual, VectorExtendido, VectorSensibilizado, VectorInhibicion;
	private Matriz Incidencia,Inhibicion,Identidad;
	private Matriz Intervalo;
    private Matriz VectorZ;
    private final int numeroPlazas;
	private final int numeroTransiciones;



	private final List<Matriz> invariantes;
	//private final List<List<Integer>> conflictos;
	private Scanner input;
	private long clk [] ,Q[]; 
	private int [] Tim;
	private int flag_count;
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
	invariantes = cargarInvariantes("matrices/InvTrans.txt");
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
	Tim  = new int[numeroTransiciones];


	//Carga de datos
	/*Incidencia.cargarMatriz("src/matrices/M.I.txt");
	Inhibicion.cargarMatriz("src/matrices/M.B.txt");
	VectorMarcadoActual.cargarMatriz("src/matrices/VMI.txt");
	Identidad.cargarIdentidad();
	IEntrada.cargarMatriz("src/matrices/M.Pre.txt");*/
	//conflictos = cargarConflictos();
}

	/*private List<List<Integer>> cargarConflictos() {
		List<List<Integer>> conf = new ArrayList<>();
		for (int i = 0; i< IEntrada.getNumFilas(); i++){
			List<Integer> c = new ArrayList<>();
			for (int j = 0; i< IEntrada.getNumColumnas(); j++){
				if(IEntrada.getDato(i,j) == 1){
					c.add(j);
				}
			}
			if(c.size()>1) conf.add(c);
		}
		return conf;
	}*/

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
		//System.out.println("Plazas -->"+Plazas);
		return nroPlazas;
	}


	/**
	 * Completa el string de Transiciones con la cantidad correspondiente
	 * @param nroT: la cantidad de transiciones en la matriz de incidencia.
	 */
	private void setStringTranciones(int nroT) {
		Transiciones = new String[nroT];
		for(int t = 1; t<nroT+1; t++) {	Transiciones[t-1] = "T"+t; }
	}
	/**
	 * Completa el string de Plazas con la cantidad correspondiente
	 * @param nroP: la cantidad de plazas en la matriz de incidencia.
	 */
	private void setStringPlazas(int nroP) {
		Plazas = new String[nroP];
		for(int p = 1; p<nroP+1; p++) { Plazas[p-1] = "P"+p; }
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
		sensibilizarVectorB();
		sensibilizarVectorE();
        sensibilizarVectorZ();
		VectorExtendido = VectorSensibilizado.getAnd(VectorInhibicion).getAnd(VectorZ.getTranspuesta());
    }
    public void sensibilizarVectorZ() {
    	//Intervalo.imprimirMatriz();
	    Matriz Auxiliar  = new Matriz(1,numeroTransiciones);
		Auxiliar =VectorSensibilizado.getAnd(VectorInhibicion);;
		int flag = 0;
		for(int fila = 0 ; fila<numeroTransiciones; fila++) {
	         if((Auxiliar.getDato(fila,0) == 1 ) 
	        	&& (Intervalo.getDato(1, fila)-Intervalo.getDato(0, fila) > 0)
	        	&& (Tim[fila] == 0)
	        		 ){ //(Beta - Alpha) es mayor a cero y se sensibilizo.
	        	 System.out.println("Sensibilizada           : "+Intervalo.getDato(1, fila) + " Transicion " + (fila+1));
	        	 System.out.println("Intervalo               : [" +Intervalo.getDato(0, fila) + " - "+ Intervalo.getDato(1, fila)+"]");
	        	 if(Q[fila] == 0 ) 
	        	 {
				  		clk[fila] = System.currentTimeMillis();
				  		Q[fila] = System.currentTimeMillis();
				  		flag = 1;
				 }
				 else
				 {						
					    Q[fila] =System.currentTimeMillis(); //Tiempo transcurrido desde que se inicio el contador 
						if((Q[fila] -clk[fila])!=0) {
						Q[fila] = Q[fila] -clk[fila];
				 }
				}
			}
			else Q[fila] = 0;
		}
		/*
		 * Me fijo cuales Q son distintos de cero para cargar un cero o un uno en el vector Tim
		 * 
		 */
		
		for(int k = 0 ; k<numeroTransiciones; k++) {
		    if( (Q[k]<= (Intervalo.getDato(1, k)*1000)) && (Auxiliar.getDato(k,0) == 1) && (Q[k] !=0) )
			{
				System.out.println("Entro");
				VectorZ.setDato(0, k, 0);
			}
			else if( (Q[k] > (Intervalo.getDato(1, k)*1000))
					&& (Auxiliar.getDato(k,0) == 1) 
					&& (Q[k] !=0) &&  (flag != 1)) //Tiempo cumplido
			{
				System.out.println("Caso especial");
				Q[k]=0;
				clk[k]=0;
				Tim[k]=1;
				VectorZ.setDato(0, k, 1);
			}
			else
		    { 
				//Primera vez.
				if((flag == 1) && (Q[k] > 0) )
				{
					System.out.println("setear");
					VectorZ.setDato(0, k, 0);
				}
				else {
					VectorZ.setDato(0, k, 1);
			
				}
		    }
		}
		imprimirQ();
		//VectorZ = Tim;
		System.out.println("VectorZ");
		VectorZ.imprimirMatriz();
	    
	}
    
	public void imprimirQ(){ 
		System.out.print("Q   : ");
		for(int i = 0 ;i< Q.length ; i++) {
			System.out.print(Q[i]+ " ");
		}
		System.out.println();
		System.out.print("clk : ");
		for(int i = 0 ;i< clk.length ; i++) {
			System.out.print(clk[i]+ " ");
		}
		System.out.println();
		System.out.print("Tim : ");
		for(int i = 0 ;i< Tim.length ; i++) {
			System.out.print(Tim[i]+ " ");
		}
		//VectorZ.imprimirMatriz();
		System.out.println("\n====================================");
		}
	/**
	 * Este metodo dispara una transicion de la rdp indicada por parametro, teniendo en cuenta el modo indicado por parametro
	 *@param transicion : numero de transicion.
	 *@return : -0 retorna 0 si el disparo no es exitoso.
	 *          -1 retorna 1 si el disparo es exitoso.
	 */
	public boolean Disparar(int transicion){
		boolean k;
		
	/*	k = testVentanaTiempo(transicion);// Pregunto si ya cumplio la ventana de tiempo
		
		if(k==true)
		{
			System.out.println("Resetea");
			Tim[transicion] = 0;//Entonces ya cumplio si ventana de tiempo solo le resta dispararse
			//sensibilizar();
		}
		else
		
		{*/
			System.out.println("Actualiza el tiempo");
			sensibilizarVectorZ();
			boolean flag;
			flag = Tim_esta_vacio();
			if(flag == false)
			{
				sensibilizar();
			}
			
		//}
		
		
		if(!estaSensibilizada(transicion))
		{
			// sensibilizar(); //Actualiza el vector extendido, solo cambia el vectorZ
			 return false;
		}
		k = testVentanaTiempo(transicion);
		if(k==true)
		{
			System.out.println("Resetea");
			
			if((transicion == 1) )
			{
				System.out.println("Resetear "+ transicion + " y " + (transicion+1) );
				Tim[transicion] = 0;
				Tim[transicion+1] = 0;
			}
			if(transicion == 2)
			{
				System.out.println("Resetear "+ transicion + " y " + (transicion+1) );
				Tim[transicion] = 0;
				Tim[transicion-1] = 0;
			}
			else Tim[transicion] = 0;//Entonces ya cumplio si ventana de tiempo solo le resta dispararse
			//sensibilizar();
		}
		Matriz aux = Incidencia.getMultiplicacion(Identidad.getColumna(transicion)); //Probleamas con el numero de la transicion
		VectorMarcadoActual = VectorMarcadoActual.getSuma(aux);
		sensibilizar();
		return true;
	}
	
	
	public boolean Tim_esta_vacio() {
	
		for(int t : Tim) {
		   if(t == 1)
		   {
			   flag_count ++;
			   return false;
		   }
		}
		return true; // no hay ninguna transicion que haya completado su ventana de tiempo
	}
	/**
	 * Este metodo nos dice que ya cumplio con el tiempo de la ventana de tiempo
	 * @param transicion
	 */
	public boolean testVentanaTiempo(int transicion) {
	
		if(Tim[transicion] == 1)
		{
			return true;  // Retorna que ya cumplio su tiempo
		}
		else return false;
	}
	//public Matriz getVectorZ() { return VectorZ; }
	
	public Matriz getVectorZ() { return VectorZ; }
	/**
	 * Devuelve true si la transicion esta habilitada de acuerdo al vector extendido
	 * @param transicion : transicion la que se desea saber si est� habilitada o no.
	 * @return verdadero estan habilitadas
	 */
	public boolean estaSensibilizada(int transicion) {
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
	public String sensibilidadas(Matriz vector) {
		//System.out.println("\n");
		String Transiciones_sensibilizadas = "";
		
			for(int n=0 ; n<vector.getNumFilas() ; n++)
				{
				Transiciones_sensibilizadas += Transiciones[n] +":" + vector.getDato(n, 0) +" ";
				}
		return Transiciones_sensibilizadas;
}
	public String Marcado(Matriz vector) {
		//System.out.println("\n");
		String Marcado_actual = "";
		for(int n=0 ; n<vector.getNumFilas() ; n++)
				{
				Marcado_actual += Plazas[n] +":" + vector.getDato(n, 0) +" ";
				}
		return Marcado_actual;
}
	public Matriz getVectorMA() { return VectorMarcadoActual; }
	public int numero_t() { return numeroTransiciones; }
	public List<Matriz> getInvariantes() {
		return invariantes;
	}
}
