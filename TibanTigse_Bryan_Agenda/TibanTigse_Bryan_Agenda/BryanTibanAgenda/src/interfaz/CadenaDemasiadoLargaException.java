package interfaz;

/**
 * Esta clase hereda de Exception, y se debe dar cuando los caracteres introducidos en un campo sean demasiado largos.
 * @author Bryan Tib�n
 *
 */
public class CadenaDemasiadoLargaException extends Exception
{

	/**
	 * Este m�todo llama a la superClase de la cual hereda (Exception) y recibe por par�metro un mensaje
	 * @param msg, mensaje a mostrar al momento de darse la excepci�n.
	 */
	public CadenaDemasiadoLargaException(String msg)
	{
		super(msg);
	}
	
}
