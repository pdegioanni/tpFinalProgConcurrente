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

private Matriz IEntrada,ISalida,Incidencia,Inhibicion,Identidad;
private Matriz VectorMarcadoInicial, VectorMarcadoActual, VectorMarcadoNuevo;
private Matriz VectorExtendido, VectorSensibilizado, VectorInhibicion;
//private SensibilizadasConTiempo gestionarTiempo;
private final int numeroTransiciones;
private final int numeroPlazas;
private Scanner input;

////////////////////////////////////////////////////////////////////////////////////////////////////
//EL VALOR ACTUAL ES IGUAL AL INICIAL
////Constructor 
public RDP() {
	
	numeroTransiciones = cargarTransiciones("matrices/M.I.txt");	//Extraccion de la cantidad de transiciones.
	numeroPlazas = cargarPlazas("matrices/M.I.txt");				//Extraccion de la cantidad de plazas.

	//Matrices
	Incidencia = new Matriz(numeroPlazas,numeroTransiciones);
	IEntrada = new Matriz(numeroPlazas,numeroTransiciones);
	ISalida = new Matriz(numeroPlazas,numeroTransiciones);
	Inhibicion = new Matriz(numeroPlazas,numeroTransiciones);
	Identidad = new Matriz(numeroTransiciones,numeroTransiciones);

	//Vectores
	VectorMarcadoInicial = new Matriz(1,numeroPlazas);
	VectorMarcadoActual = new Matriz(1,numeroPlazas);
	VectorMarcadoNuevo = new Matriz(1,numeroPlazas);
	VectorSensibilizado = new Matriz(1,numeroTransiciones);
	VectorInhibicion = new Matriz(1,numeroTransiciones);
	VectorExtendido = new Matriz(1,numeroTransiciones);
	
	//Carga de datos
	Incidencia.cargarMatriz("C:\\Users\\Administrador\\Desktop\\TP final\\M.I.txt");
	IEntrada.cargarMatriz("C:\\Users\\Administrador\\Desktop\\TP final\\M.Pre.txt");
	ISalida.cargarMatriz("C:\\Users\\Administrador\\Desktop\\TP final\\M.Post.txt");
	Inhibicion.cargarMatriz("C:\\Users\\Administrador\\Desktop\\TP final\\M.B.txt");
	Identidad.cargarIdentidad();
	VectorMarcadoInicial.cargarMatriz("C:\\Users\\Administrador\\Desktop\\TP final\\VMI.txt");
	VectorMarcadoActual.cargarMatriz("C:\\Users\\Administrador\\Desktop\\TP final\\VMI.txt");
	
	//sensibilizar();
}
/**
 * Este metodo devuelve la cantidad de transiciones disponibles en la red
 * @param pathMI la ruta al archivo de texto que contiene la matriz de incidencia
 * @return transiciones de la red    
 */
private int cargarTransiciones(String pathMI){
	int nrotrans = 0;
	System.out.println("Path: "+pathMI);
	try { 
		input = new Scanner(new File(pathMI));
		//while (input.hasNextLine()) {
		String line = input.nextLine();
		for (int columna = 0; columna < line.length (); columna ++) {
			char c = line.charAt (columna);
			if(c == '1' || c == '0') nrotrans ++ ;
				//Transiciones[nrotrans]= "T"+nrotrans;
		}
		setStringTranciones(nrotrans);
			//break;
		//}
	} 
	catch (IOException e) {
		e.printStackTrace();
	}
	//System.out.println("transiciones --->"+transiciones);
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
			 //Plazas[nroPlazas] = "P"+nroPlazas;
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

	private void setStringTranciones(int nroT) {
		Transiciones = new String[nroT];
		for(int t = 0; t<nroT; t++) Transiciones[t] = "T"+t;
	}
	private void setStringPlazas(int nroP) {
		Plazas = new String[nroP];
		for(int p = 0; p<nroP; p++) Transiciones[p] = "P"+p;
	}

/** 
 * Este metodo calcula las transiciones sensibilizadas de rdp (VE)
 * @param transicion : transicion la que se desea saber si está habilitada o no.
 * @return verdadero estan habilitadas 
 */
public boolean estaSensibilizada(int transicion) {
	if(VectorExtendido.getDato(0, transicion)==1){
		return true;
	}
	else {
		return false;
	}    	
}
/**
 * Metodo que devuelve el vector con las transiciones sensibilizadas
 * @return vector extendido
 */
public Matriz getSensibilizadas() {
	return VectorExtendido;
}
/**
 * Metodo que devuelve la matriz de inhibicion
 * @return matriz inhibicion
 */
public Matriz getMatrizInhibicion() {
	return VectorInhibicion;
}
/**
 * Metodo que sensibiliza las transiciones y carga el vector extendido
 */
public void sensibilizar() {
	sensibilizarVectorB();
	sensibilizarVectorE();
	//Ex = E and B and L and V and G and Z cambiar aca si algo se agrega
	VectorExtendido = VectorSensibilizado.getAnd(VectorInhibicion);
}
/**
 * Metodo que calcula el vector Inhibicion
 */
public void sensibilizarVectorB() {
	//Para obtener el vector B neceitamos la formula B=H*Q
	//vector Q donde cada elemento es cero si la marca de la plaza es distinta de cero, uno en otro caso
	Matriz Q = new Matriz(VectorMarcadoActual.getNumFilas(),VectorMarcadoActual.getNumColumnas());
	for(int i=0; i<Q.getNumFilas(); i++) {
		for(int j=0; j<Q.getNumColumnas(); j++) {
			if(VectorMarcadoActual.getDato(i, j)==0) {//duda 1 o 0?
				Q.setDato(i, j, 0);
			}
			else {
				Q.setDato(i, j, 1);
			}
		}
	}//[1xn]=[1xnumeroDePlazas] si hacemos una transposicion => nx1   
	//H [nxm]=[numeroDePlazasxnumeroDeTransiciones], si o si hay que transponer =>mxn
	Inhibicion.imprimirMatriz();
	Matriz H = Inhibicion.getTranspuesta();
	Matriz B = H.getMultiplicacion(Q.getTranspuesta());
	VectorInhibicion = B.getComplemento().getTranspuesta();
}
/**
 * Metodo que calcula el vector sensibilizado
 */
public void sensibilizarVectorE() {
	for (int i = 0; i < IEntrada.getNumColumnas(); i++) {
		int e = 1;
		for (int j = 0; j < IEntrada.getNumFilas(); j++) {
			if (VectorMarcadoActual.getDato(0, j) < IEntrada.getDato(j, i)) {
				e = 0;
			}
			VectorSensibilizado.setDato(0, i, e);
		}
	}
	//System.out.println("sensibilizar VE");
	//VectorSensibilizado.imprimirMatriz();
}
/**
 * Este metodo dispara una transicion de la rdp indicada por parametro, teniendo en cuenta el modo indicado por parametro 
 *@param n_T : numero de transicion. 
 *@param flag indica el modo de disparo: 
 *			 -true : modo de disparo explicito, modifica el vector de marcado actual VMA
 *           -false : modo de disparo implicito, no modifica el vector de marcado actual VMA
 *@return : -0 retorna 0 si el disparo no es exitoso. 
 *          -1 retorna 1 si el disparo es exitoso.
 */
public boolean Disparar(int transicion){
	System.out.println("T -->"+transicion);
	transicion = posicion(transicion);
	System.out.println("POSI -->"+transicion);
	if(!estaSensibilizada(transicion)) {
		return false;
	}
	Matriz aux = Incidencia.getMultiplicacion(Identidad.getColumna(transicion)).getTranspuesta(); //Probleamas con el numero de la transiscion
    VectorMarcadoActual =VectorMarcadoActual.getSuma(aux);
	sensibilizar();
	return true;
}
/**
 * Este metodo muestra el vector indicado por parametro 
 * @param vector vector a imprimir. 
 */ 
public void mostrar(Matriz vector ,int Tipo) {
	    //System.out.println("\n");
		if(Tipo == 0) {
			for(int n=0 ; n<vector.getNumColumnas() ; n++) System.out.print(Transiciones[n] +":" + vector.getDato(0, n) +" ");
		}
		else if(Tipo > 0) {
			for(int n=0 ; n<vector.getNumColumnas() ; n++) System.out.print(Plazas [n] +":" +  vector.getDato(0, n) +" ");
		}
		System.out.println("\n");
}

public Matriz getVectorMA() {
	return VectorMarcadoActual; 
}
public int posicion(int val) {
	int pos = 0;
	for(int n=0 ; n<Transiciones.length ; n++)
	{
	
	String[] parts = Transiciones[n].split("T");
	//System.out.println(" --->"+parts[1]);
		if(Integer.parseInt(parts[1])==val) {
			pos = n;
			break;
		}
	}
	return pos;
}

}
