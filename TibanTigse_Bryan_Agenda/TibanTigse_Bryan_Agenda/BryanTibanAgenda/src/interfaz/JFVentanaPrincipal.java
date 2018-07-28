package interfaz;


import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import jdbc.Conexion;
import javax.swing.table.DefaultTableModel;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;


/**
 * Clase en la que se define la primera interfaz del programa, es la ventana principal, y sus funciones son las de
 * añadir, borrar y listar contactos principalmente, también se compone de un menú con opciones de "salir" y "Vaciar agenda" y un botón 
 * editar que derivará a la clase JDEditar.
 * @author Bryan Tibán
 
 */
public class JFVentanaPrincipal extends JFrame
{
	private Dimension d = new Dimension();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuMas = new JMenu();
	private JMenuItem mntmCerrar = new JMenuItem();
	private JSeparator separator = new JSeparator();
	private JMenuItem mntmVaciarAgenda = new JMenuItem();
	private JScrollPane scrollPane = new JScrollPane();
	private JPanel panelCrear = new JPanel();
	private JTextField campoNombre = new JTextField();
	private JButton btnCrear = new JButton();
	private JTextField campoTelefono = new JTextField();
	private JTable tabla = new JTable();
	private JButton btnEliminar = new JButton();
	private JButton btnEditar = new JButton();
	private JLabel lblNombre = new JLabel();
	private JLabel lblTelfono = new JLabel();
	private Conexion conexion ;
	
	/**
	 * Consttructor de la clase, instancia a su vez un objeto de la clase jdbc.Conexion.
	 * @throws SQLException
	 */
	public JFVentanaPrincipal() throws SQLException
	{
		try {
			conexion = new Conexion();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getContentPane().setLayout(null);
		setUndecorated(true);
		d= Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(d.width/3, d.height/3);
		setSize(500, 500);
		iniciarComponentes();
		
		setVisible(true);
	}
	
	/**
	 * Inicia los componentes de la interfaz
	 * @throws SQLException
	 */
	private void iniciarComponentes() throws SQLException
	{
		panelConTabla();
		barraMenu();
		panelCrear();
		
		cargarTabla();
		
	}
	
	/**
	 * Contiene la construcción de la barra, menú, items y eventos correspondientes.
	 */
	public void barraMenu()
	{
		menuBar.setBounds(0, 0, 500, 26);
		menuMas.setText("Más"); //barra
			menuBar.add(menuMas);	//menu
				mntmCerrar.setText("Salir");
				menuMas.add(mntmCerrar); //item
				mntmCerrar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
					menuMas.add(separator);
				mntmVaciarAgenda.setText("Vaciar Agenda");
				mntmVaciarAgenda.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
				menuMas.add(mntmVaciarAgenda);
					
		getContentPane().add(menuBar);
		
		mntmCerrar.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				try {
					conexion.commit();
					conexion.insertarContactoEnFichero();
					System.exit(0);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		mntmVaciarAgenda.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				int r = JOptionPane.showConfirmDialog(null, "Se eliminarán todos los contactos");
				if(r==0)
				{
					try {
							conexion.insertarContactoEnFichero();
							conexion.vaciarAgenda();
							
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(null, "La aplicación se va a reiniciar, no se preocupe","Reinicio necesario",JOptionPane.WARNING_MESSAGE);
							Lanzador.main(null);
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
				}
				else
				{
					try {
						conexion.rollback();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
	}

	/**
	 * Construcción de un JScrollPane y una JTable.
	 */
	public void panelConTabla()
	{
		scrollPane.setBounds(18, 256, 470, 231);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Contactos"));
		tabla.setCellSelectionEnabled(false);
		tabla.setEnabled(false);
		
		scrollPane.setViewportView(tabla);
		getContentPane().add(scrollPane);
	}

	/**
	 * Construcción de un panel , su contenido y eventos, de los cuales a destacar el bloqueo de determinadas teclas al momento
	 * de introducir texto.
	 */
	public void panelCrear()
	{
		panelCrear.setLayout(null);
		panelCrear.setBounds(18, 39, 470, 204);
		panelCrear.setBorder(BorderFactory.createTitledBorder("Crear contacto"));
		
		lblNombre.setText("Nombre:");
		lblTelfono.setText("Teléfono:");
		lblNombre.setBounds(44, 75, 56, 16);
		lblTelfono.setBounds(44, 112, 56, 16);
		
		campoNombre.setBounds(140, 71, 240, 25);
		campoNombre.setColumns(10);
		campoTelefono.setBounds(140, 109, 240, 22);
		campoTelefono.setColumns(10);
		
		btnCrear.setBounds(56, 154, 97, 25);
		btnEliminar.setBounds(186, 154, 97, 25);
		btnCrear.setText("Crear");
		btnEliminar.setText("Eliminar");
		btnEditar.setBounds(311, 154, 97, 25);
		btnEditar.setText("Editar");
		
		panelCrear.add(btnEditar);
		panelCrear.add(lblNombre);
		panelCrear.add(lblTelfono);
		panelCrear.add(btnCrear);
		panelCrear.add(btnEliminar);
		panelCrear.add(campoNombre);
		panelCrear.add(campoTelefono);
		getContentPane().add(panelCrear);
		
		campoTelefono.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyTyped(KeyEvent ke)
			{
				char caracter = ke.getKeyChar();
				if(caracter<'0' || caracter>'9')
				{
					ke.consume();
				}
			}
		});
		
		btnCrear.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(!campoNombre.getText().equals("") && !campoTelefono.getText().equals(""))
				{
					boolean ok=false;
					try{
						ok = conexion.aniadirContacto(new Contacto(campoNombre.getText(), campoTelefono.getText()));
						conexion.commit();
					}catch (MySQLIntegrityConstraintViolationException e1) {
						JOptionPane.showMessageDialog(null, "Ya existe un contacto con este número");
					}catch (CadenaDemasiadoLargaException e1) {
						JOptionPane.showMessageDialog(null, "Cadena demasiado larga","Exceso de caracteres",JOptionPane.ERROR_MESSAGE);
					}catch (Exception e1) {
						e1.printStackTrace();
					}
					
					if(ok==true)
					{
						JOptionPane.showMessageDialog(null, "Contacto creado","Satisfactorio",JOptionPane.INFORMATION_MESSAGE);
						campoNombre.setText(""); campoTelefono.setText("");
						try{
							cargarTabla();
						}catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Los campos no pueden estar vacíos","Campo vacío",JOptionPane.ERROR_MESSAGE);
				}
				
		}});
		
		btnEliminar.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(!campoNombre.getText().equals("")  && !campoTelefono.getText().equals(""))
				{
					try {
						int ok = conexion.borrarContacto(campoNombre.getText(),campoTelefono.getText());
						if(ok>=1)
						{
							int JopOk = JOptionPane.showConfirmDialog(null, "¿Está seguro?");
							if(JopOk==0){
								conexion.commit();
								cargarTabla();
								campoNombre.setText(""); campoTelefono.setText("");
							}
							else
							{
								conexion.rollback();
							}
							
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Consulte la lista de contactos, "+campoNombre.getText()+" "+campoTelefono.getText()+" no existe", "Contacto no existe",JOptionPane.ERROR_MESSAGE);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (CadenaDemasiadoLargaException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Los campos no pueden estar vacíos","Campo vacío",JOptionPane.ERROR_MESSAGE);
				}
				

	
			}
		});
		
		btnEditar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					ResultSet rs = conexion.buscarContacto(campoNombre.getText(), campoTelefono.getText());
					rs.last();
					int ok=rs.getRow();
					if(ok==1)
					{
						if(campoNombre.getText().equals("") && campoNombre.getText().equals(""))
						{
							JOptionPane.showMessageDialog(null, "Ayudese de la lista para rellenar los dos campos", "campos vacíos",JOptionPane.ERROR_MESSAGE);
						}
						else
						{
							new JDEditar(conexion, campoNombre.getText(), campoTelefono.getText());
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No existe este contacto, ayudesde de la lista para rellenar los campos", "Contacto no existe",JOptionPane.ERROR_MESSAGE);
					}
					
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
			}
		});
	}

	/**
	 * Rellena el JTable con los datos existentes en la BD desde el último commit.
	 * @throws SQLException
	 */
	public void cargarTabla() throws SQLException{
		int  x =conexion.count();
		String [][] rows = new String[x][2];
		
		ResultSet cursor = conexion.listAlllistAllContactos();
		for(int i=0;i<=x-1;i++)
		{
			cursor.next();
			rows[i][0]=cursor.getString("nombre");
			rows[i][1]=cursor.getString("telefono");
		}
		tabla.setModel(new DefaultTableModel(
			rows,
			new String[] {
				"NOMBRE", "TELEFONO"
			}
		));
	}
}
