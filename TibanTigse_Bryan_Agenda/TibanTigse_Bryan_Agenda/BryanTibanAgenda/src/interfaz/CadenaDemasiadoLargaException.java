package interfaz;

/**
 * Esta clase hereda de Exception, y se debe dar cuando los caracteres introducidos en un campo sean demasiado largos.
 * @author Bryan Tibán
 *
 */
public class CadenaDemasiadoLargaException extends Exception
{

	/**
	 * Este método llama a la superClase de la cual hereda (Exception) y recibe por parámetro un mensaje
	 * @param msg, mensaje a mostrar al momento de darse la excepción.
	 */
	public CadenaDemasiadoLargaException(String msg)
	{
		super(msg);
	}
	
}
