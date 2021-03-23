import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
/**
 * @author Alberto Sánchez de la Nieta Pérez
 * Esta clase Liga, la cual contiene todos los metodos utilizados en el programa, incluida la conexcion a la base de datos.
 * La Base de datos creada en PhpMyAdmin se llama "Liga".
 * Se ha intentado modularizar todas las distintas operaciones, para que no queden metodos demasiado largos. De tal forma que 
 * los metodos sean lo mas cortos y precisos posibles para escalar el proyecto en un futuro.
 */
public class Liga {
	//Constantes de la BBDD
	static final String DATABASE_URL = "jdbc:mysql://localhost/Liga";
	static final String USER = "root";
	static final String PASSWORD = "";
	//manejadores
	java.sql.Statement st = null; //He tenido que ponner: "java.sql." como prefijo porque Statement me lo reconocia como otra clase distinta y no me dejaba manejar con sql.
	Connection con = null;
	ResultSet rs =null;
	//Atributos
	ArrayList<Jugador> listaJugadores = new ArrayList<>();
	ArrayList<Equipo> listaEquipos = new ArrayList<>();
	ArrayList<Integer> jugadoresEliminados= new ArrayList<>();
	ArrayList<Integer> equiposEliminados= new ArrayList<>();
	//Constructor de Liga que establece la conexion y se reasigna el manejador de consultas Statement
	public Liga() {
		try {
			conexionBaseDatos();//Llamada a metodo que conecta con la BBDD y crea las tablas si no existen previamente
		} catch (SQLException  e) {
			System.out.println(e.getMessage());
		}
		listaEquipos=leerListaEquipos(); //Debe leer primero los equipos porque es la tabla que tiene la pk_eqiupo que es clave ajena en la tabla de jugador
		listaJugadores=leerListaJugadores();
	}
	
	//Metodo que crea la conexion a la BBDD
	private void conexionBaseDatos() throws SQLException {
		// SE crea la conexion
		con = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
		System.out.println("==========  Se ha conectado con éxito a la Base de Datos.  ==========");
		// Se crea flujo para realizar consultas
		st = con.createStatement();
		// Se crean las tablas de la liga si no existen
		st.execute("CREATE TABLE IF NOT EXISTS Equipo (codigo_Equipo INT AUTO_INCREMENT,"
				+ "nombre VARCHAR(250), estadio VARCHAR(100), ciudad VARCHAR(100), num_socios INT (10),PRIMARY KEY(codigo_Equipo), UNIQUE(nombre))");
		st.execute(
				"CREATE TABLE IF NOT EXISTS Jugador (id_codigo_jugador INT AUTO_INCREMENT,"
						+ "nombre VARCHAR(50), apellidos VARCHAR(100), fecha_nac DATE, codigo_Equipo INT, "
						+ "PRIMARY KEY(id_codigo_jugador),UNIQUE(nombre,apellidos),FOREIGN KEY FK_EQUIPO(codigo_Equipo) REFERENCES Equipo(codigo_Equipo)) ");
	}
	//metodo que lee la tabla Equipo de la BBDD y la carga en el arraylist (memoria)
	private ArrayList<Equipo> leerListaEquipos() {
		try {
			rs = st.executeQuery("SELECT * FROM Equipo");
			while (rs.next()) {
				listaEquipos.add(new Equipo(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5)));//Se añade el equipo a la lista
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listaEquipos;
	}
	
	//metodo que lee la tabla jugador de la BBDD y la carga en el arraylist (memoria)
	private ArrayList<Jugador> leerListaJugadores() {
		try {
			rs = st.executeQuery("SELECT * FROM Jugador");
			while (rs.next()) {
				int codigo_equipo = rs.getInt(5); //Recojo el id de del equipo FK de tabla jugador
				//Para asignar el equipo de tipo Equipo recorro la lista y evaluo por el codigo_equipo
				//Si no encuentra ningun codigo igual se asignara null al atributo equipo
				Equipo equipo=null;
				for (Equipo e : listaEquipos) {
					if (e.getCodigo_Equipo() == codigo_equipo) {
						equipo=e; 
					}
				}if (equipo!=null) {
					listaJugadores.add(new Jugador(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4),equipo)); //Se añade a la lista el jugador con el equipo correspondiente
				}else {
					listaJugadores.add(new Jugador(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4),equipo)); //Se añade a la lista el jugador con el equipo a null
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listaJugadores;
	}

	//Metodo que dara de alta a un equipo segun lo que escriba el usuario
	public void altaEquipo()throws InputMismatchException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Dime el nombre del equipo:"); String nombre = sc.nextLine().toUpperCase();
		System.out.println("Dime el nombre del estadio:"); String estadio = sc.nextLine().toUpperCase();
		System.out.println("Dime el nombre de la ciudad:"); String ciudad = sc.nextLine().toUpperCase();
		System.out.println("Dime el numero de socios:"); int socios = sc.nextInt();
		sc.nextLine();//Limpia buffer
		try {
			rs = st.executeQuery("SELECT nombre FROM Equipo WHERE nombre='"+nombre+"'");
			if (!rs.next()) {
				Equipo equipo = new Equipo(nombre, estadio, ciudad, socios);
				listaEquipos.add(new Equipo(nombre, estadio, ciudad, socios));
				System.out.println("El equipo ha sido añadido con exito.");
			}else {
				System.out.println("\nEl equipo ya existe en la BBDD.\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//Metodo que dara de baja a un equipo, segun el ID elegido por el usuario
	public void bajaEquipo()throws InputMismatchException {
		Scanner sc = new Scanner(System.in);
		try {
			rs = st.executeQuery("SELECT codigo_Equipo FROM Equipo");
			if (!rs.next()) {
				System.out.println("\n======= No hay registros =======\n");
			} else {
				mostrarIdEquipos();// Funcion que muestra al usuario un listado con ID y nombre de los equipos, para que pueda elegir.
				System.out.println("\nIntroduce el ID del equipo que quieras dar de baja:\n"); int id=0;
				try {
					id = sc.nextInt();
				} catch (InputMismatchException e) {
					System.err.println("Has introducido un valor no valido.");
				}
				boolean existe = false;//Variable que controla si el id que ha dado el usuario corresponde a algun equipo o no
				rs = st.executeQuery("SELECT codigo_Equipo FROM Equipo");
				while (rs.next()) {
					System.out.println(rs.getInt(1));
					if (rs.getInt(1)==id) {
						equiposEliminados.add(id);
						existe=true;
					}
				}
				System.out.println(existe?"\nEliminado con exito.\n":"\nEl ID no corresponde a ningun equipo.\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//Metodo que permite dar de alta a un jugador
	public void altaJugador()throws InputMismatchException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Dime el nombre del jugador:"); String nombre = sc.nextLine().toUpperCase();
		System.out.println("Dime los apellidos del jugador:"); String apellidos = sc.nextLine().toUpperCase();
		System.out.println("Dime el año de nacimiento [YYYY]:"); int anio = sc.nextInt();
		System.out.println("Dime el mes de nacimiento [MM] :"); int mes = sc.nextInt();
		System.out.println("Dime el dia de nacimiento [DD] :"); int dia = sc.nextInt();
		sc.nextLine();//Limpia buffer
		Jugador jugador = null;
		//Si la lista de equipos esta vacia, no asignara ningun equipo, si tiene equipos, preguntara cual asignar (Muestra al usuario ID y nombre del equipo)
		if (!listaEquipos.isEmpty()) {
			mostrarIdEquipos(); //Funcion que muestra los id y nombres de los equipos para que el usuario pueda visualizar y elegir
			System.out.println("\nIntroduce a continuación el ID del equipo que quieras asignar.\n");
			int id = sc.nextInt();
			sc.nextLine();
			boolean existe=false;
			for (Equipo equipo : listaEquipos) {
				if (id == equipo.getCodigo_Equipo()) {
					jugador = new Jugador(nombre, apellidos, Date.valueOf(LocalDate.of(anio, mes, dia)), equipo);
					existe=true;
				}
			}
			if (existe==false) {
				jugador = new Jugador(nombre, apellidos, Date.valueOf(LocalDate.of(anio, mes, dia)), null);
			}
		}else {
			jugador = new Jugador(nombre, apellidos, Date.valueOf(LocalDate.of(anio, mes, dia)), null);
		}
		if (!listaJugadores.contains(jugador)) {
			listaJugadores.add(jugador);
			System.out.println("\nEl jugador ha sido añadido con éxito.\n");
		}else {
			System.out.println("\nEl jugador introducido ya se encuentra en la Base de Datos. No se puede duplicar.\n");
		}
	}
	
	//Metodo que dara de baja a un jugador segun el id que introduzca el usuario
	public void bajaJugador() throws InputMismatchException{
		Scanner sc = new Scanner(System.in);
		try {
			boolean existe = false;//Variable que controla si el id que ha dado el usuario corresponde a alun jugador o no
			rs = st.executeQuery("SELECT id_codigo_jugador FROM Jugador");
			if (!rs.next()) {
				System.out.println("\n======= No hay registros =======\n");
			} else {
				mostrarIdJugadores(); //Funcion que muestra los jugadores de la BBDD
				System.out.println("\nIntroduce el ID del jugador que quieras dar de baja:\n");
				int id = sc.nextInt();
				sc.nextLine();
				rs = st.executeQuery("SELECT id_codigo_jugador FROM Jugador");
				while (rs.next()) {
					if (id == rs.getInt(1)) {
						existe=true;
						jugadoresEliminados.add(id);
					}
				}
			}
			System.out.println(existe?"\nEliminado con éxito.\n":"\nEl ID no corresponde a ningún jugador.\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//Funcion que muestra el equipo que elija el usuario segun id.
	public void consultarEquipo() {
		Scanner sc = new Scanner(System.in);
		if (mostrarIdEquipos()) {//Funcion que muestra los id y nombres de los equipos para que el usuario pueda visualizar y elegir
			System.out.println("\nIntroduce el ID del equipo que quieres consultar:");
			int id=0;
			try {
				id = sc.nextInt();
			} catch (InputMismatchException e) {
				System.err.println("Has introducido un valor no válido.");
			}
			sc.nextLine();//Limpia Buffer
			Equipo equipo = comprobarIdEquipo(id);
			if (equipo != null) {
				System.out.println("\n======= Datos del Equipo =======\n");
				System.out.println(equipo);
			}else {
				System.out.println("\nEl ID introducido no corresponde a ningun equipo.\n");
			}
		}
		
	}
	//Metodo que comprueba que el id pasdo por parametro exista en la base datos
	//devuelve null si no existe, devuelve el equipo si existe
	private Equipo comprobarIdEquipo(int id) {
		for (Equipo equipo : listaEquipos) {
			if (equipo.getCodigo_Equipo() == id) return equipo;
		}
		return null;
	}
	//Metodo que comprueba que el id pasdo por parametro exista en la base datos
	//devuelve null si no existe, devuelve el jugador si existe
	private Jugador comprobarIdJugador(int id) {
		for (Jugador j : listaJugadores) {
			if (j.getId_codigo_jugador() == id) return j;
		}
		return null;
	}
	
	//Funcion que muestra el jugador que elija el usuario segun id.
	public void consultarJugador() {
		Scanner sc = new Scanner(System.in);
		if (mostrarIdJugadores()) {//Funcion que muestra los id y nombres de los jugadores para que el usuario pueda visualizar y elegir
			System.out.println("\nIntroduce el ID del jugador que quieres consultar:");
			int id=0;
			try {
				id = sc.nextInt();
			} catch (InputMismatchException e) {
				System.err.println("Has introducido un valor no válido.");
			}
			sc.nextLine();//Limpia Buffer
			Jugador jugador = comprobarIdJugador(id);
			if (jugador != null) {
				System.out.println("\n======= Datos del Jugador =======\n");
				System.out.println(jugador);
			}else {
				System.out.println("\nEl ID introducido no corresponde a ningun jugador.\n");
			}
		}
	}
	
	// Metodo que hace la llamada al metodo que modifica 
	public void modificarDatos() throws InputMismatchException{
		Scanner sc = new Scanner(System.in);
		int decision = menuModificarEquipoJugador();
		if (decision == 1) { //Modificara algun equipo
			modificarEquipo();
		}else {
			modificarJugador();
		}
	}
	
	//MEtodo que gestiona las llmadas que modifican el Equipo
	private void modificarEquipo() {
		Scanner sc = new Scanner(System.in);
		int decision=0;
		if (mostrarIdEquipos()) {
			int id = 0;
			Equipo equipo = null;
			do {
				System.out.println("\nIntroduce el ID del equipo que quieres modificar:");
				id = 0;
				try {
					id = sc.nextInt();
					sc.nextLine(); // Limpia buffer
				} catch (InputMismatchException e) {
					System.err.println("Has introducido un valor no válido.");
				}
				equipo = comprobarIdEquipo(id);// Metodo que comprobara si el id corresponde con algun equipo
			} while (equipo == null);
			decision = menuEleccionAtributoEquipo();
			switch (decision) {
			case 1:// Permite modificar el nombre del equipo
				modificarNombreEquipo(equipo);
				break;
			case 2:// Permite modificar el nombre del estadio
				modificarNombreEstadio(equipo);
				break;
			case 3:// Permite modificar el nombre de la Ciudad
				modificarNombreCiudad(equipo);
				break;
			case 4:// Permite modificar el numero de socios
				modificarNumeroSocios(equipo);
				break;
			}
		}
	}
	//MEtodo que se encarga de las llmadas que modifican el Jugador
	private void modificarJugador() {
		Scanner sc = new Scanner(System.in);
		int decision=0;
		if (mostrarIdJugadores()) {
			int id = 0;
			Jugador jugador = null;
			do {
				System.out.println("\nIntroduce el ID del jugador que quieres modificar:");
				id = 0;
				try {
					id = sc.nextInt();
				} catch (InputMismatchException e) {
					System.err.println("Has introducido un valor no válido.");
				}
				sc.nextLine(); // Limpia buffer
				jugador = comprobarIdJugador(id);// Metodo que comprobara si el id corresponde con algun equipo
			} while (jugador == null);
			decision = menuEleccionAtributoJugador();
			switch (decision) {
			case 1:// Permite modificar el nombre del jugador
				modificarNombreJugador(jugador);
				break;
			case 2:// Permite modificar el apellido del jugador
				modificarApellidosJugador(jugador);
				break;
			case 3:// Permite modificar la fecha de nacimiento del jugador
				try {
					modificarFechaNacJugador(jugador);
				} catch (InputMismatchException e) {
					System.err.println("Has introducido un valor no válido.");
				}
				break;
			case 4:// Permite modificar equipo del jugador
				modificarEquipoJugador(jugador);
				break;
			}
		}
	}
	//Metodo que modifica el nombre del jugador
	private void modificarNombreJugador(Jugador jugador) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce el nuevo nombre:");
		String nuevoNombre = sc.nextLine().toUpperCase();
		jugador.setNombre(nuevoNombre);
		try {
			st.executeUpdate("UPDATE Jugador SET nombre ='"+nuevoNombre+"' WHERE id_codigo_jugador = "+jugador.getId_codigo_jugador());
		} catch (SQLException e) {
			System.err.println("\nError. No se ha podido modificar.\n");;
		}
	}
	//Metodo que modifica los apellidos del jugador
	private void modificarApellidosJugador(Jugador jugador) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce el nuevo nombre:");
		String nuevoNombre = sc.nextLine().toUpperCase();
		jugador.setNombre(nuevoNombre);
		try {
			st.executeUpdate("UPDATE Jugador SET apellidos ='"+nuevoNombre+"' WHERE id_codigo_jugador = "+jugador.getId_codigo_jugador());
		} catch (SQLException e) {
			System.err.println("\nError. No se ha podido modificar.\n");;
		}
	}
	//Metodo que modifica la fecha de nacimiento del jugador
	private void modificarFechaNacJugador(Jugador jugador)throws InputMismatchException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce la nueva fecha de nacimiento:");
		System.out.println("Dime el año de nacimiento [YYYY]:"); int anio = sc.nextInt();
		System.out.println("Dime el mes de nacimiento [MM] :"); int mes = sc.nextInt();
		System.out.println("Dime el dia de nacimiento [DD] :"); int dia = sc.nextInt();
		sc.nextLine();//Limpia buffer
		jugador.setFecha_nac(Date.valueOf(LocalDate.of(anio, mes, dia)));
		try {
			st.executeUpdate("UPDATE Jugador SET fecha_nac ='"+Date.valueOf(LocalDate.of(anio, mes, dia))+"' WHERE id_codigo_jugador = "+jugador.getId_codigo_jugador());
		} catch (SQLException e) {
			System.err.println("\nError. No se ha podido modificar.\n");;
		}
	}
	//Metodo que modifica el equipo del jugador
	private void modificarEquipoJugador(Jugador jugador) {
		Scanner sc = new Scanner(System.in);
		System.out.println("\n¿Quires asignar otro equipo al jugador, o quieres dejarlo sin equipo?\n"
				+ "1. Asignar otro equipo.\n"
				+ "2. Dejar sin equipo.\n");
		int decision = 0;
		try {
			decision = sc.nextInt();
			sc.nextLine(); // Limpia buffer
		} catch (InputMismatchException e) {
			System.err.println("Has introducido un valor no válido.");
		}
		if (decision == 1) {
			mostrarIdEquipos();
			System.out.println("Introduce el ID del nuevo equipo:");
			int id = 0;
			try {
				id = sc.nextInt();
				sc.nextLine(); // Limpia buffer
			} catch (InputMismatchException e) {
				System.err.println("Has introducido un valor no válido.");
			}
			Equipo equipo = comprobarIdEquipo(id);
			if (equipo != null) {
				jugador.setEquipo(equipo);
				try {
					st.executeUpdate("UPDATE Jugador SET codigo_Equipo ='"+equipo.getCodigo_Equipo()+"' WHERE id_codigo_jugador = "+jugador.getId_codigo_jugador());
				} catch (SQLException e) {
					System.err.println("\nError. No se ha podido modificar.\n");;
				}
		
			}
		}else if (decision == 2) {
			jugador.setEquipo(null);
			try {
				st.executeUpdate("UPDATE Jugador SET codigo_Equipo = NULL WHERE id_codigo_jugador ="+jugador.getId_codigo_jugador());
			} catch (SQLException e) {
				System.err.println("\nError. No se ha podido modificar.\n");;
			}
		} 
	}
	
	//Metodo que modifica el nombre del equipo
	private void modificarNombreEquipo(Equipo equipo) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce el nuevo nombre:");
		String nuevoNombre = sc.nextLine().toUpperCase();
		equipo.setNombre(nuevoNombre);
		try {
			st.executeUpdate("UPDATE Equipo SET nombre ='"+nuevoNombre+"' WHERE codigo_Equipo = "+equipo.getCodigo_Equipo());
		} catch (SQLException e) {
			System.err.println("\nError. No se ha podido modificar.\n");;
		}
	}
	//Metodo que modifica el nombre del estadio del equipo
	private void modificarNombreEstadio(Equipo equipo) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce el nuevo estadio:");
		String nuevoEstadio = sc.nextLine().toUpperCase();
		equipo.setEstadio(nuevoEstadio);
		try {
			st.executeUpdate("UPDATE Equipo SET estadio ='"+nuevoEstadio+"' WHERE codigo_Equipo = "+equipo.getCodigo_Equipo());
		} catch (SQLException e) {
			System.err.println("\nError. No se ha podido modificar.\n");;
		}
	}
	//Metodo que modifica el nombre de la ciudad
	private void modificarNombreCiudad(Equipo equipo) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce la nueva ciudad:");
		String nuevaCiudad = sc.nextLine().toUpperCase();
		equipo.setCiudad(nuevaCiudad);
		try {
			st.executeUpdate("UPDATE Equipo SET ciudad ='"+nuevaCiudad+"' WHERE codigo_Equipo = "+equipo.getCodigo_Equipo());
		} catch (SQLException e) {
			System.err.println("\nError. No se ha podido modificar.\n");;
		}
	}
	//Metodo que modifica el numero de socios del equipo
	private void modificarNumeroSocios(Equipo equipo) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce el nuevo Nº de socios:");
		int socios=0;
		try {
			socios = sc.nextInt();
			sc.nextLine();
			equipo.setNum_socios(socios);
		} catch (InputMismatchException e) {
			System.err.println("Has introducido un valor no valido.");
		}
		try {
			st.executeUpdate("UPDATE Equipo SET num_socios ='"+socios+"' WHERE codigo_Equipo = "+equipo.getCodigo_Equipo());
		} catch (SQLException e) {
			System.err.println("\nError. No se ha podido modificar.\n");;
		}
	}
	
	//Metodo menu eleccion de atributos de equipo a modificar
	private int menuEleccionAtributoJugador() {
		Scanner sc = new Scanner(System.in);
		int decision = 0;
		do {
			System.out.println("¿Qué atributo quieres modificar?\n1. Nombre.\n2. Apellidos.\n3. Fecha de nacimiento.\n4. Equipo.\n");
			try {
				decision = sc.nextInt();
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.err.println("Has introducido un valor no valido. [1-4]");
			}
		} while (decision < 0 || decision > 4);
		return decision;
	}
	
	//Metodo menu eleccion de atributos de equipo a modificar
	private int menuEleccionAtributoEquipo() {
		Scanner sc = new Scanner(System.in);
		int decision = 0;
		do {
			System.out.println("¿Qué atributo quieres modificar?\n1. Nombre.\n2. Estadio\n3. Ciudad.\n4. Nº de socios.\n");
			try {
				decision = sc.nextInt();
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.err.println("Has introducido un valor no valido. [1-4]");
			}
		} while (decision < 0 || decision > 4);
		return decision;
	}
	
	//Metodo menu para eleccion de modificar: Jugador/Equipo
	private int menuModificarEquipoJugador() {
		Scanner sc = new Scanner(System.in);
		int decision = 0;
		do {
			System.out.println("¿Que quieres modificar?\n1. Equipo.\n2. Jugador\n");
			try {
				decision = sc.nextInt();
			} catch (InputMismatchException e) {
				System.err.println("Has introducido un valor no valido. [1-2]");
			}
			sc.nextLine();
		} while (decision != 1 && decision != 2);
		return decision;
	}
	
	//Metodo que recorre todas las listas para subir a la base de datos las altas y bajas de las tablas Jugador y Equipo
	public void guardar() {
		
		for (Equipo equipo : listaEquipos) {
			try {
				st.executeUpdate("INSERT INTO Equipo (nombre,estadio,ciudad,num_socios) VALUES ('" + equipo.getNombre()+"','"+equipo.getEstadio()+"','"+equipo.getCiudad()+"','"+equipo.getNum_socios()+"')");
			} catch (SQLException e) {
				 //Capturo en silencio las excepciones por los registros que hay ya en la base de datos, que impide que se vuelquen duplicados
				//la condicion es que el nombre del eequipo debe ser unico para la base de datos (Condicion UNIQUE)
			}
		}
		for (Jugador j : listaJugadores) {
			try {
				if (j.getEquipo()==null) {
					st.executeUpdate("INSERT INTO Jugador (nombre,apellidos,fecha_nac) VALUES ('" + j.getNombre()+"','"
							+j.getApellidos()+"','"+j.getFecha_nac()+"')");
				}else {
					st.executeUpdate("INSERT INTO Jugador (nombre,apellidos,fecha_nac,codigo_Equipo) VALUES ('" + j.getNombre()+"','"
							+j.getApellidos()+"','"+j.getFecha_nac()+"','"+j.getEquipo().getCodigo_Equipo()+"')"); //ERROR
				}	
			} catch (SQLException e) {
				 //Capturo en silencio las excepciones por los registros que hay ya en la base de datos, que impide que se vuelquen duplicados
				//la condicion es que el nombre y apellidos del jugador debe ser unico para la base de datos (Condicion UNIQUE)
			}
		}
		for (Integer id : equiposEliminados) {
			try {
				st.executeUpdate("DELETE FROM Equipo WHERE codigo_equipo='"+id+"'");
			} catch (SQLException e) {
				
			}
		}
		for (Integer id : jugadoresEliminados) {
			try {
				st.executeUpdate("DELETE FROM Jugador WHERE id_codigo_jugador='"+id+"'");
			} catch (SQLException e) {
				
			}
		}
		System.out.println("\nSe ha guardado con exito.\n");
	}
	
	//Metodo que muestra los id junto con el nombre del equipo, devuelve booleano si hay o no registros
	private boolean mostrarIdEquipos() {
		if (listaEquipos.isEmpty()) {
			System.out.println("\n======= No hay registros =======\n");
			return false;
		}else {
			System.out.println("\n======= Equipos en la Base de Datos =======\n");
			for (Equipo equipo : listaEquipos) {
				System.out.println("ID: "+equipo.getCodigo_Equipo()+" -----> Nombre del equipo: "+equipo.getNombre());
			}
			return true;
		}
		
	}
	//Metodo que muestra los id junto con el nombre del equipo
		private boolean mostrarIdJugadores() {
			if (listaJugadores.isEmpty()) {
				System.out.println("\n======= No hay registros =======\n");
				return false;
			}else {
				System.out.println("\n======= Jugadores en la Base de Datos =======\n");
				for (Jugador jugador : listaJugadores) {
					System.out.println("ID: "+jugador.getId_codigo_jugador()+" -----> Nombre del jugador: "+jugador.getNombre()+" "+jugador.getApellidos());
				}
				return true;
			}	
		}
	
}
