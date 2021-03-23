import java.util.*;
/**
 * @author Alberto Sánchez de la Nieta Pérez
 * Esta clase que contiene el main del programa, desde donde se haran las llamdas a los metodos de la clase Liga 
 * La conexion a la base de datos se realiza cuando se crea el objeto Liga
 * El nombre de la base de datos creada en PHPmyAdmin se llama "Liga"
 */
public class Principal_MySQL {
	
	public static void main(String[] args) {
		
		Liga lfp = new Liga(); //Se crea el objeto desde el que se manejara todas las opciones que decida el usuarrio
		int eleccion=0;
		boolean salir=false;
		do {
			Scanner sc = new Scanner(System.in);
			System.out.println("\n======= Menu Principal =======\n\n"
					+ "1. Alta equipo.\n"
					+ "2. Baja equipo.\n"
					+ "3. Alta jugador.\n"
					+ "4. Baja jugador.\n"
					+ "5. Consultar equipo.\n"
					+ "6. Consultar jugador.\n"
					+ "7. Modificar datos \n"
					+ "8. Guardar.\n"
					+ "9. Salir.\n");
			try {
				eleccion = sc.nextInt();
			} catch (InputMismatchException e) {
				System.err.println("Has introducido letras.");
			}
			if (eleccion<=0 || eleccion > 8) {
				System.err.println("Valores permitidos [1-8]");
			}
			switch (eleccion) {
			case 1:
				try {
					lfp.altaEquipo();
				} catch (InputMismatchException e) {//Capturo aqui para no llenar el metodo de try-catch,y no se vuelva ilegible
					System.err.println("\nHas introducido un valor no válido.\n");
				}
				break;
			case 2:
				lfp.bajaEquipo();
				break;
			case 3:
				try {
					lfp.altaJugador();
				} catch (InputMismatchException e) { //Capturo aqui para no llenar el metodo de try-catch,y no se vuelva ilegible
					System.err.println("\nHas introducido un valor no válido.\n");
				}
				break;
			case 4:
				lfp.bajaJugador();
				break;
			case 5:
				lfp.consultarEquipo();
				break;
			case 6:
				lfp.consultarJugador();
				break;
			case 7:
				lfp.modificarDatos();
				break;
			case 8:
				lfp.guardar();
				break;
			case 9:
				salir=true;
				System.out.println("======= Fin del programa =======");
				break;
			}
		} while (!salir);
			
	}

}
