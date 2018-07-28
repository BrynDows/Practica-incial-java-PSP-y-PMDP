package jdbc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import interfaz.CadenaDemasiadoLargaException;
import interfaz.Contacto;

/**
 * Esta clase permite gestionar la conexion con la base de datos. Contiene
 * m�todos capaces de crear una una nuevo esquema en la base datos y m�todos
 * capaces de insertar nuevos elementos en la misma, como as� tambien m�todos
 * capaces de modificar y eliminar elementos de la base de datos.
 * 
 * @author Bryan Tiban
 * @version 1.0
 */
public class Conexion {
	private String bd = "Bryan_tiban_agenda";
	private String user = "dam2"; // cambiar a dam2
	private String pwd = "dam2";
	private String driver = "com.mysql.jdbc.Connection";
	static Connection con = null;
	private String url = "jdbc:mysql://localhost/" + bd;
	private Statement terminal;
	private ResultSet cursor;

	/**
	 * M�todo que permite crear la conexi�n, los pasos que sigue son los siguientes:
	 * 1.llama al m�todo conectar() el cual devulve un n�mero entero, si dicho
	 * n�mero es igual a 1 quiere decir que la conexi�n con la BD agenda se realiza
	 * correctamente, si el resultado es 2 significa que la BD agenda no existe, por
	 * lo tanto este m�todo Conexion() crea una nueva BD agenda.
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Conexion() throws ClassNotFoundException, SQLException {
		int opcion = conectar();
		String createBD = "create database if not exists agenda ";
		String createTable = "create table contactos( " + " nombre varchar(50) not null, "
				+ " telefono varchar(50) not null," + " Constraint primary key PK_ID (nombre,telefono)" + ");";
		if (opcion == 2) {
			try {
				terminal = con.createStatement();
				terminal.executeUpdate(createBD);
				terminal.executeUpdate("use agenda");
				terminal.executeUpdate(createTable);
				con.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Este m�todo cierra la conexi�n previamente habiendo a�adido en un fichero
	 * ubicado en ./agenda.csv todos los contactos existentes hasta el momento en la
	 * BD
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public void cerrarConexion() throws SQLException, IOException {
		insertarContactoEnFichero();
		con.close();
	}

	/**
	 * Este m�todo intenta realizar dos conexiones, dependiendo de la BD a la que se
	 * conecte retornar� diferentes valores: 1. La primera conexi�n se intenta
	 * realizar con la BD agenda si el resultado es satisfactorio se retorna un
	 * entero 1 2. La segunda conexi�n se da cuando la primera resulta fallida, es
	 * decir cuando la BD agenda no existe, es entonces cuando la conexi�n se
	 * realiza a un esquema el cual no se puede eliminar en Mysql, que es el esquema
	 * TEST, si la conexi�n con esta BD resulta satisfactoria retorna un 2, seg�n el
	 * entero retornado ser� el constructor de esta clase quien aplique las medidas
	 * correspondientes.
	 * 
	 * @return se retorna un entero el cual sus valores son solo 1 o 2, si es 1
	 *         significa que la conexi�n con BD agenda ha sido satisfactoria si es 2
	 *         significa que la conexi�n con BD agenda ha fallado, ser� enteonces
	 *         cuando el constructor de esta clase tome las medidas adecuadas para
	 *         crear la conexi�n con una nueva BD agenda.
	 */
	public int conectar() {
		int ok = 0;
		try {
			Class.forName(driver);
			con = (Connection) DriverManager.getConnection(url, user, pwd);
			if (con != null) {
				ok = 1;
				con.setAutoCommit(false);
			}
		} catch (ClassNotFoundException e) {
			// driver no encontrado
		} catch (SQLException e) {
			try {
				this.url = "jdbc:mysql://localhost/test";
				Class.forName(driver);
				con = (Connection) DriverManager.getConnection(url, user, pwd);
				if (con != null) {
					ok = 2;
					con.setAutoCommit(false);
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return ok;
	}

	/**
	 * Este m�otodo devuelve el n�mero de elementos existentes en la BD agenda.
	 * 
	 * @return devuelve un entero Integer, el cual es el n�mero de filas existentes
	 *         en la BD
	 * @throws SQLException
	 */
	public int count() throws SQLException {
		int res = 0;
		String query = "select * from contactos";

		terminal = con.createStatement();
		ResultSet count = terminal.executeQuery(query);
		count.last();
		res = count.getRow();

		return res;
	}

	/**
	 * Este m�todo devuleve las filas ( la intenci�n es que solo devuelva una fila)
	 * que ser� el elemento el cual se est� buscando, para la b�squeda hace uso de
	 * par�metros.
	 * 
	 * @param nombre,
	 *            este par�matro debe coincidir exactamente con el nombre del
	 *            contacto a buscar.
	 * @param telefono,
	 *            este par�metro debe coincidir exactamente con el tel�fono del
	 *            contacto a buscar
	 * @return retorna un ResultSet, el cual deber� ser tratado de manera que
	 *         permita certificar la existencia, o no existencia del contacto
	 * @throws SQLException
	 */
	public ResultSet buscarContacto(String nombre, String telefono) throws SQLException {
		String query = "select * from contactos where nombre like '" + nombre + "' AND telefono like '" + telefono
				+ "'";
		cursor = null;
		terminal = con.createStatement();
		terminal.executeUpdate("use agenda");
		cursor = terminal.executeQuery(query);

		return cursor;
	}

	/**
	 * Este m�todo permite a�adir contactos a la base de datos
	 * 
	 * @param contacto,
	 *            corresponde con el nombre del contacto a buscar
	 * @return retorna un Boolean, el cual si resulta ser TRUE, significa que el
	 *         contacto se ha a�adido correctamente, y si resulta ser FALSE,
	 *         significa que el usuario no se ha podido a�adir a la BD, generalemte
	 *         significar� que el contacto ya existe
	 * @throws Exception
	 * @throws CadenaDemasiadoLargaException,
	 *             esta excepci�n se genera cuando el nombre o telefono del contacto
	 *             supera los 50 caracteres
	 * @throws MySQLIntegrityConstraintViolationException,
	 *             esta excepci�n se da cuando se intenta a�adir un contacto igual a
	 *             uno ya existente
	 */
	public boolean aniadirContacto(Contacto contacto)
			throws Exception, CadenaDemasiadoLargaException, MySQLIntegrityConstraintViolationException {
		if (contacto.getNombre().length() > 50 || contacto.getTelefono().length() > 50) {
			throw new CadenaDemasiadoLargaException("La cadena debe ser menor o igual a 50 caracteres");
		}
		String query = "insert into contactos values ( '" + contacto.getNombre().toUpperCase() + "' , '"
				+ contacto.getTelefono() + "' )";
		Statement sql = null;
		boolean ok = false;
		sql = con.createStatement();
		int sentencia = sql.executeUpdate(query);
		if (sentencia != 0) {
			ok = true;
		}
		insertarContactoEnFichero();
		return ok;
	}

	/**
	 * Este m�todo lista todos los contactos.
	 * 
	 * @return retorna un ResultSet que deber� ser recorrido
	 * @throws SQLException
	 */
	public ResultSet listAlllistAllContactos() throws SQLException {
		cursor = null;
		terminal = con.createStatement();
		cursor = terminal.executeQuery("select * from contactos");

		return cursor;
	}

	/**
	 * Este m�todo se encarga de vaciar la BD agenda y posteriormente con los nuevos
	 * resultados llama al m�todo inserCOntactoEnFichero()
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void vaciarAgenda() throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		DriverManager.getConnection("jdbc:mysql://localhost/test", "dam2", "dam2");
		terminal = con.createStatement();
		terminal.executeUpdate("drop database if exists agenda");
		con.commit();
		try {
			insertarContactoEnFichero();
		} catch (IOException e) {
		}

	}

	/**
	 * Este m�todo inserta los contactos existentes hasta el momento en un fichero
	 * con extensi�n .csv, "./agenda.csv"
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	public void insertarContactoEnFichero() throws IOException, SQLException {
		File fichero = new File("./agenda.csv");
		ResultSet elementos = listAlllistAllContactos();// listallcontactos devuelve toda la tabla
		FileWriter fw = new FileWriter(fichero);
		BufferedWriter bw = new BufferedWriter(fw);
		// elementos.first();
		while (elementos.next()) {// se recorre la tabla y se escribe en el fichero
			bw.write(elementos.getString("nombre") + " " + elementos.getString("telefono"));
			bw.newLine();
		}
		bw.close();
	}

	/**
	 * Este m�todo borra a priori, un solo contacto de la BD, los elementos
	 * necesarios para el borrado se le pasa por par�metro diferentes valores.
	 * 
	 * @param nombre,
	 *            debe ser igual al nombre del contacto existente en la BD que se
	 *            quiere borrar.
	 * @param telefono,
	 *            debe ser igual al tel�fono del contacto existente en la BD que se
	 *            quiere borrar.
	 * @return retorna un entero. el cual si el borrado ha sido satisfactorio
	 *         devuelve un 1, en caso contrario retorna un 0.
	 * @throws SQLException
	 * @throws CadenaDemasiadoLargaException,
	 *             esta excepci�n se da cuando por paramentro se le han pasado
	 *             cadenas demasiado largas, supeiores a 50 caracteres.
	 */
	public int borrarContacto(String nombre, String telefono) throws SQLException, CadenaDemasiadoLargaException {
		nombre = nombre.toUpperCase();
		if (nombre.length() > 50) {
			throw new CadenaDemasiadoLargaException("La cadena debe ser menor o igual a 50 caracteres");
		}
		int line = 0;
		String query = "Delete From contactos where nombre like '" + nombre + "' AND telefono like '" + telefono + "'";

		terminal = con.createStatement();
		line = terminal.executeUpdate(query);

		return line;
	}

	/**
	 * Confirma los cambios realizados desde el �ltimo commit en la BD.
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		con.commit();
	}

	/**
	 * Revierte los cambios realizados desde el �ltimo commit realizado.
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		con.rollback();
	}

	/**
	 * Este m�todo modifica un contacto.
	 * 
	 * @param telefonoActual,
	 *            este par�metro corresponde con el telefono del contaco a
	 *            modificar.
	 * @param nombreActual,este
	 *            par�metro corresponde con el nombre del contaco a modificar.
	 * @param telefonoNuevo,
	 *            este par�metro corresponde con el NUEVO telefono del contaco a
	 *            modificar.
	 * @param nombreNuevo,
	 *            este par�metro corresponde con el NUEVO nombre del contaco a
	 *            modificar.
	 * @return retorna un entero Integer, el cual si es 0 significar� que la
	 *         actualizaci�n no se ha realizado, si retorna 1 significa que la
	 *         actualizaci�n se ha realizado correctamente.
	 * @throws SQLException
	 */
	public int modificarContacto(String telefonoActual, String nombreActual, String telefonoNuevo, String nombreNuevo)
			throws SQLException {
		nombreNuevo.toUpperCase();
		int line = 0;
		String query = "update contactos set  nombre= '" + nombreNuevo + "' , telefono = '" + telefonoNuevo
				+ "' where nombre like '" + nombreActual + "' AND telefono like '" + telefonoActual + "' ";

		terminal = con.createStatement();
		line = terminal.executeUpdate(query);
		return line;
	}
}
