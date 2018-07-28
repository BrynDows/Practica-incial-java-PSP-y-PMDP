package interfaz;

/**
 * esta clase define los campos de los cuales debe estar compuesto un contacto a crear.
 * @author Bryan Tibán
 * 
 * @version 1.0
 */
public class Contacto 
{

	private String nombre;
	private String telefono;

	/**
	 * Constructor que debe recibir dos parámetros.
	 * @param nombre, nombre del contacto
	 * @param telefono, telefono del contacto
	 */
	public Contacto(String nombre, String telefono) {
		super();
		this.nombre = nombre;
		this.telefono = telefono;
	}
	
	/**
	 * @return, retorna el nombre
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * @param nombre, recibe el nombre por parámetro
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * @return, retorna el telefono
	 */
	public String getTelefono() {
		return telefono;
	}
	/**
	 * @param recibe el numero telefonico por parametro
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	
	
	
}
