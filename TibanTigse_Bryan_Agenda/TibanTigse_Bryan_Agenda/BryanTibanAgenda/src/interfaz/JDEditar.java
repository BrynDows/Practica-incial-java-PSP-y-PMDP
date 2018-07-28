package interfaz;

import javax.swing.JDialog;

import jdbc.Conexion;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JButton;

/**
 * Esta es la segunda ventana del programa, en la cual se editan los contactos.
 * @author Bryan Tibán
 *
 */
public class JDEditar extends JDialog
{
	private Conexion conexion;
	private String nombreActual;
	private String telefonoActual;
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu mnVolver = new JMenu();
	private JMenuItem mntmVolver= new JMenuItem();
	private JTextField campoNombre= new JTextField();
	private JTextField campoTelefono = new JTextField();
	private JLabel lblNuevoNombre = new JLabel();
	private JLabel lblNuevoTelfono = new JLabel();
	private JPanel panel = new JPanel();
	private JButton btnAceptar = new JButton();
	private JButton btnCancelar = new JButton();
	private JLabel txtTelefono = new JLabel();
	private JPanel paneltxt = new JPanel();
	private JLabel txtNombre = new JLabel();
	private Dimension d = new Dimension();
	
	
	/**
	 * Este método inicia la ventana al momento de ser instanciada la clase
	 * @param conexion, esta ventana debe recibir la Conexión instanciada en la Ventana principal para poder seguir comunicandose con
	 * la BD
	 * @param nombreActual, dado que al abrir esta ventana la idea es mostrar los datos del contacto a editar en un
	 * javax.swing.JLabel, se necesita para ello recibir dichos datos, es por ello que este constructor recibe el NOMBRE del contacto a editar
	 * @param telefonoActual, dado que al abrir esta ventana la idea es mostrar los datos del contacto a editar en un
	 * javax.swing.JLabel, se necesita para ello recibir dichos datos, es por ello que este constructor recibe el TELEFONO del contacto a editar
	 */
	public JDEditar(Conexion conexion, String nombreActual, String telefonoActual) 
	{
		this.conexion=conexion;
		this.nombreActual=nombreActual;
		this.telefonoActual=telefonoActual;
		
		
		getContentPane().setLayout(null);
		
		setUndecorated(true);
		d= Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(d.width/3, d.height/3);
		
		menuBar.setBounds(0, 0, 482, 26);
		mnVolver.setText("Volver");
		menuBar.add(mnVolver);
		mntmVolver.setText("Volver");
		mnVolver.add(mntmVolver);
		
		lblNuevoNombre.setText("Nuevo nombre:");
		lblNuevoNombre.setBounds(110, 109, 93, 16);
		lblNuevoTelfono.setText("Nuevo teléfono:");
		lblNuevoTelfono.setBounds(110, 138, 92, 16);
		
		campoNombre.setBounds(235, 109, 116, 22);
		campoNombre.setColumns(10);
		campoTelefono.setBounds(235, 144, 116, 22);
		campoTelefono.setColumns(10);
		
		
		
		panelLabels();
		panelBotones();
		
		getContentPane().add(menuBar); 
		getContentPane().add(campoNombre);
		getContentPane().add(campoTelefono);
		getContentPane().add(lblNuevoNombre);
		getContentPane().add(lblNuevoTelfono);
		
		
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
		
		mntmVolver.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setSize(500, 240);
		setVisible(true);
	}
	
	/**
	 * Construcción de un panel y su contenido, dos objetos javax.swing.JLabel.
	 */
	public void panelLabels()
	{
		paneltxt.setBounds(77, 39, 318, 57);
		txtNombre.setText(this.nombreActual);
		txtTelefono.setText(this.telefonoActual);
		
		paneltxt.add(txtNombre);
		paneltxt.add(txtTelefono);
		getContentPane().add(paneltxt);
	}
	
	/**
	 * Construcción de un panel con su contenido y eventos programados.
	 */
	public void panelBotones()
	{
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.setBounds(130, 179, 205, 35);

		btnAceptar.setText("Aceptar");
		btnCancelar.setText("Cancelar");
		panel.add(btnAceptar);
		panel.add(btnCancelar);
		getContentPane().add(panel);
		
		btnAceptar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(!campoNombre.getText().equals("") && !campoTelefono.getText().equals(""))
				{
					try {
						int Jop = JOptionPane.showConfirmDialog(null, "El contacto "+getNombreActual()+" "+getTelefonoActual()+" va a modifcarse");
						if(Jop==0)
						{
							conexion.modificarContacto(getTelefonoActual(), getNombreActual(), campoTelefono.getText(), campoNombre.getText());
							conexion.commit();
							Lanzador.main(null);
						}
						else
						{
							conexion.rollback();
						}
						
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Los campos no pueden estar vacíos","campos vacíos",JOptionPane.ERROR_MESSAGE);
				}
					
				
			}
		});
		
		btnCancelar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				campoNombre.setText("");
				campoTelefono.setText("");
			}
		});
	}



	/**
	 * getter del nombre pasado por parametro al constructor
	 * @return, retorna el nombre pasado por parámetro
	 */
	public String getNombreActual() {
		return nombreActual;
	}



	/**
	 * getter del telefono pasado por parametro al constructor
	 * @return, retorna el telefono pasado por parámetro
	 */
	public String getTelefonoActual() {
		return telefonoActual;
	}

}
