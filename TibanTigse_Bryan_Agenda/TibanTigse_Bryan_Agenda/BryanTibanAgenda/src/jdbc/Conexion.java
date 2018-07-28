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
 * métodos capaces de crear una una nuevo esquema en la base datos y métodos
 * capaces de insertar nuevos elementos en la misma, como así tambien métodos
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
	 * Método que permite crear la conexión, los pasos que sigue son los siguientes:
	 * 1.llama al método conectar() el cual devulve un número entero, si dicho
	 * número es igual a 1 quiere decir que la conexión con la BD agenda se realiza
	 * correctamente, si el resultado es 2 significa que la BD agenda no existe, por
	 * lo tanto este método Conexion() crea una nueva BD agenda.
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
	 * Este método cierra la conexión previamente habiendo añadido en un fichero
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
	 * Este método intenta realizar dos conexiones, dependiendo de la BD a la que se
	 * conecte retornará diferentes valores: 1. La primera conexión se intenta
	 * realizar con la BD agenda si el resultado es satisfactorio se retorna un
	 * entero 1 2. La segunda conexión se da cuando la primera resulta fallida, es
	 * decir cuando la BD agenda no existe, es entonces cuando la conexión se
	 * realiza a un esquema el cual no se puede eliminar en Mysql, que es el esquema
	 * TEST, si la conexión con esta BD resulta satisfactoria retorna un 2, según el
	 * entero retornado será el constructor de esta clase quien aplique las medidas
	 * correspondientes.
	 * 
	 * @return se retorna un entero el cual sus valores son solo 1 o 2, si es 1
	 *         significa que la conexión con BD agenda ha sido satisfactoria si es 2
	 *         significa que la conexión con BD agenda ha fallado, será enteonces
	 *         cuando el constructor de esta clase tome las medidas adecuadas para
	 *         crear la conexión con una nueva BD agenda.
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
	 * Este méotodo devuelve el número de elementos existentes en la BD agenda.
	 * 
	 * @return devuelve un entero Integer, el cual es el número de filas existentes
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
	 * Este método devuleve las filas ( la intención es que solo devuelva una fila)
	 * que será el elemento el cual se está buscando, para la búsqueda hace uso de
	 * parámetros.
	 * 
	 * @param nombre,
	 *            este parámatro debe coincidir exactamente con el nombre del
	 *            contacto a buscar.
	 * @param telefono,
	 *            este parámetro debe coincidir exactamente con el teléfono del
	 *            contacto a buscar
	 * @return retorna un ResultSet, el cual deberá ser tratado de manera que
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
	 * Este método permite añadir contactos a la base de datos
	 * 
	 * @param contacto,
	 *            corresponde con el nombre del contacto a buscar
	 * @return retorna un Boolean, el cual si resulta ser TRUE, significa que el
	 *         contacto se ha añadido correctamente, y si resulta ser FALSE,
	 *         significa que el usuario no se ha podido añadir a la BD, generalemte
	 *         significará que el contacto ya existe
	 * @throws Exception
	 * @throws CadenaDemasiadoLargaException,
	 *             esta excepción se genera cuando el nombre o telefono del contacto
	 *             supera los 50 caracteres
	 * @throws MySQLIntegrityConstraintViolationException,
	 *             esta excepción se da cuando se intenta añadir un contacto igual a
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
	 * Este método lista todos los contactos.
	 * 
	 * @return retorna un ResultSet que deberá ser recorrido
	 * @throws SQLException
	 */
	public ResultSet listAlllistAllContactos() throws SQLException {
		cursor = null;
		terminal = con.createStatement();
		cursor = terminal.executeQuery("select * from contactos");

		return cursor;
	}

	/**
	 * Este método se encarga de vaciar la BD agenda y posteriormente con los nuevos
	 * resultados llama al método inserCOntactoEnFichero()
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
	 * Este método inserta los contactos existentes hasta el momento en un fichero
	 * con extensión .csv, "./agenda.csv"
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
	 * Este método borra a priori, un solo contacto de la BD, los elementos
	 * necesarios para el borrado se le pasa por parámetro diferentes valores.
	 * 
	 * @param nombre,
	 *            debe ser igual al nombre del contacto existente en la BD que se
	 *            quiere borrar.
	 * @param telefono,
	 *            debe ser igual al teléfono del contacto existente en la BD que se
	 *            quiere borrar.
	 * @return retorna un entero. el cual si el borrado ha sido satisfactorio
	 *         devuelve un 1, en caso contrario retorna un 0.
	 * @throws SQLException
	 * @throws CadenaDemasiadoLargaException,
	 *             esta excepción se da cuando por paramentro se le han pasado
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
	 * Confirma los cambios realizados desde el último commit en la BD.
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		con.commit();
	}

	/**
	 * Revierte los cambios realizados desde el último commit realizado.
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		con.rollback();
	}

	/**
	 * Este método modifica un contacto.
	 * 
	 * @param telefonoActual,
	 *            este parámetro corresponde con el telefono del contaco a
	 *            modificar.
	 * @param nombreActual,este
	 *            parámetro corresponde con el nombre del contaco a modificar.
	 * @param telefonoNuevo,
	 *            este parámetro corresponde con el NUEVO telefono del contaco a
	 *            modificar.
	 * @param nombreNuevo,
	 *            este parámetro corresponde con el NUEVO nombre del contaco a
	 *            modificar.
	 * @return retorna un entero Integer, el cual si es 0 significará que la
	 *         actualización no se ha realizado, si retorna 1 significa que la
	 *         actualización se ha realizado correctamente.
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
