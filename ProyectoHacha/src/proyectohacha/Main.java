package proyectohacha;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
	
	Scanner sc = new Scanner(System.in);
	Archivos archivo = new Archivos();
	
	
	//Método que generará el menú de la aplicación mediante terminal
	private void generarmenu() {
		System.out.println("Elije una opción:\n1. Elegir un archivo\n2. Dividir el archivo\n3. Unir archivos\n4. Salir");
	}
	
	//Metodo que ejecutara otro metodo segun la eleccion del usuario
	private void ejecucion() {
		int eleccion = 0;
		
		//Se repite mientras la eleccion no sea 4
		do {
			try {
				generarmenu();
				eleccion = sc.nextInt();
				
				switch (eleccion) {
				case 1:
					//Seleccion del archivo a dividir
					archivo.selectorArchivo();
					break;
				case 2:
					//Division del archivo
					archivo.partes();
					break;
				case 3:
					//Union de las particiones
					archivo.direccionParticiones();
					archivo.datosXML();
					break;
				case 4:
					//Salida de la aplicacion
					System.out.println("Saliendo...");
					break;
				default:
					//Error al elegir una opcion incorrecta
					System.out.println("Tienes que elegir una de las cuatro opciones");
				}
			} catch (InputMismatchException e) {
				//Error al utilizar un caracter que no sea un entero
				System.out.println("Procura escribir un número");
				sc.nextLine();
				generarmenu();
			}
		} while (eleccion != 5);
		
	}
	
	//Metodo principal que inicializa la aplicacion
	public static void main(String[] args) {
		Main user = new Main();
		user.ejecucion();
	}

}
