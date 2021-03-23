/**
 * @author Alberto Sánchez de la Nieta Pérez
 * Esta clase define las diferentes instacias que se creen de Equipo.
 */
public class Equipo {
	//Atributos de clase
	private int codigo_Equipo;
	private String nombre;
	private String estadio;
	private String ciudad;
	private int num_socios;
	//Constructor con parametros, incluido el ID del jugador
	public Equipo(int codigo_Equipo, String nombre, String estadio, String ciudad, int num_socios) {
		this.codigo_Equipo=codigo_Equipo;
		this.nombre = nombre;
		this.estadio = estadio;
		this.ciudad = ciudad;
		this.num_socios = num_socios;
	}
	//Constructor con parametros, sin incluir el atributo ID
	public Equipo(String nombre, String estadio, String ciudad, int num_socios) {
		this.nombre = nombre;
		this.estadio = estadio;
		this.ciudad = ciudad;
		this.num_socios = num_socios;
	}
	//Constructor sin parametros
	public Equipo() {
	}
	//Metodos set y get de la clase
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEstadio() {
		return estadio;
	}

	public void setEstadio(String estadio) {
		this.estadio = estadio;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public int getNum_socios() {
		return num_socios;
	}

	public void setNum_socios(int num_socios) {
		this.num_socios = num_socios;
	}

	public int getCodigo_Equipo() {
		return codigo_Equipo;
	}
	//Metodo sobreescrito para impedir introducir en memoria dos objetos iguales segun nombre.
	@Override
	public boolean equals(Object obj) {
		return this.nombre.equalsIgnoreCase(((Equipo)obj).nombre);
	}
	//Metodo sobreescrito que devuelve en forma de cadena, toda la informacion del Equipo
	@Override
	public String toString() {
		return "ID de equipo: " + codigo_Equipo + " ---> nombre: " + nombre + ", estadio: " + estadio + ", ciudad: "
				+ ciudad + ", numero de socios: " + num_socios;
	}
	
}
