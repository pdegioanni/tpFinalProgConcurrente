public class SensibilizadaConTiempo {
	
	private final RDP red;
	private final long[] sensibilizadaConTiempo;

	/**
	 * Constructor de la clase SensibilizadaConTiempo
	 * @param red la red de petri que contiene la información de las transiciones temporales
	 * */
	public SensibilizadaConTiempo(RDP red) {
		this.red = red;
		sensibilizadaConTiempo = new long[red.getNumeroTransiciones()];
	}

	/**
	 * Método que registra el timestamp cuando una transición temporal está sensibilizada.
	 * @param transicion transición temporal sensibilizada
	 * */
	public void setNuevoTimeStamp(int transicion) {
		sensibilizadaConTiempo[transicion] = System.currentTimeMillis();
		//System.out.println("Timestamp --> "+SensibilizadaConTiempo[transicion]);
	}

	/**
	 * Metodo que inhabilita una transición por tiempo
	 * @param transicion la transición a inhabilitar
	 */

	public void setEsperando(int transicion) {
		try{
			red.setEsperando(transicion);
			Thread.sleep(red.getIntervalo().getDato(1, transicion)); //valor en milisegundos que debe esperar para que la transición esté habilitada
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que habilita una transición por tiempo
	 * @param transicion la transición a habilitar
	 */
	public void resetEsperando(int transicion) {
	       	red.resetEsperando(transicion);
	       	sensibilizadaConTiempo[transicion]=0;
	}

	/**
	 * @param transicion la transicion que se quiere saber si es temporal o no
	 * @return true si la transicion es temporal, false si no lo es
	 * */
	private boolean esTemporal(int transicion) {
		//System.out.println("Transicion : " + (transicion+1));
		return (red.getIntervalo().getDato(0, transicion) - red.getIntervalo().getDato(1, transicion)) != 0;
	}

	/**
	 * @param transicion la transicion que se quiere saber si es temporal y está sensibilizada
	 * @return true si la transicion es temporal y sensibilizada, false si no lo es
	 * */
	public boolean esTemporalSensibilizada(int transicion) {
		//	System.out.println("---> "+ transicion);
		return esTemporal(transicion) && red.estaSensibilizada(transicion);
	}
}
