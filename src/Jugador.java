import java.sql.Date;
/**
 * @author Alberto Sánchez de la Nieta Pérez
 * Esta clase define las diferentes instacias que se creen de Jugador.
 */
public class Jugador {
	//Atributos de clase
	private int id_codigo_jugador=0;
	private String nombre;
	private String apellidos;
	private Date fecha_nac;
	private Equipo equipo;
	//Constructor con parametros, incluido el ID del jugador
	public Jugador(int id_codigo_jugador, String nombre, String apellidos, Date fecha_nac, Equipo equipo) {
		this.id_codigo_jugador=id_codigo_jugador;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.fecha_nac = fecha_nac;
		this.equipo = equipo;
	}
	//Constructor con parametros, sin incluir el atributo ID
	public Jugador( String nombre, String apellidos, Date fecha_nac, Equipo equipo) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.fecha_nac = fecha_nac;
		this.equipo = equipo;
	}
	//Constructor sin parametros
	public Jugador() {
	}
	
	//Metodos set y get de la clase
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Date getFecha_nac() {
		return fecha_nac;
	}

	public void setFecha_nac(Date fecha_nac) {
		this.fecha_nac = fecha_nac;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}

	public int getId_codigo_jugador() {
		return id_codigo_jugador;
	}
	//Metodo sobreescrito para impedir introducir en memoria dos objetos iguales segun nombre, apellidos y fecha de nacimiento
	@Override
	public boolean equals(Object obj) {
		return this.nombre.equalsIgnoreCase(((Jugador)obj).nombre) && this.apellidos.equalsIgnoreCase(((Jugador)obj).apellidos) && 
				this.fecha_nac.equals(((Jugador)obj).fecha_nac) ;
	}
	//Metodo sobreescrito que devuelve en forma de cadena, toda la informacion del Jugador
	@Override
	public String toString() {
		return "ID de jugador: " + id_codigo_jugador + " ---> nombre: " + nombre + " " + apellidos
				+ ", fecha de nacimiento: " + fecha_nac + ", equipo: " + equipo; 
	}
	
}
