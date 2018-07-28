package interfaz;

import java.sql.SQLException;


/**
 * Esta clase contiende el método main y la instancia de la clase JFVentanaPrincipal que permite iniciar el programa.
 * @author Bryan Tibán
 *
 */
public class Lanzador {

	public static void main(String[] args) {
		try {
			JFVentanaPrincipal JF = new JFVentanaPrincipal();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
