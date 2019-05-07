package Core;
import java.io.IOException;
import java.util.List;

import Products.Product;
import functions.Functions;
import interfaces.FInterface;

/*	
 * 				public   protected   private 
 * Attributi       1         1         15 
 * Metodi          34        0          2              
 * 
 * 				classi   attributi   metodi 
 * Uso di final   0         2         0 
 * Uso di static  0         1         9 
 *
 */



/**
 * @author Saeed Muhammad
 *
 */

public class MainClass {

	//max number of items our store can have defined in a final variable which means it cannot be changed *final*
		public final int MaxStorage = 1000;
		/**
		 * program main method, once launched this is where the code starts getting executed
		 * @param args
		 */
	public static void main(String[] args) {
		//an instance of the object is required as the methods are not *static* (methods that implement an interface cannot be static) 
		FInterface j_Interface = new Functions();
		// create main list that contains all VALID objects with type emporio
		List<Product> emp = null;
		//Load objects from config file and assign them to the previously created list
		try {
			emp = j_Interface.loadconfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//show the main menu to the user
		j_Interface.HandleMenu(emp);

	}

}