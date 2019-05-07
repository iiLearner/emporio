package functions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import Core.MainClass;
import Products.Electronics;
import Products.Lamp;
import Products.Product;
import Products.Wood;
import enums.Categories;
import enums.mUnit;
import enums.tColor;
import interfaces.FInterface;

public class Functions implements FInterface {
	/**
	 * overriding in order to use default values, in this case: 0
	 * @param emp
	 */
	public void HandleMenu(List<Product> emp) {HandleMenu(emp, 0);}
	
	/**
	 * Main program menu, completely handled inside itself.
	 * @param emp 		requires a list of type Emprorio
	 * @param option_	An optional parameter used to autoselect an option
	 */
	final static int INVALID_INTEGER = -1;
	private static void HandleMenu(List<Product> emp, int option_)
	{
		Functions f = new Functions();
		//open the scanner, mainly used for reading inputs
		Scanner mscanner = new Scanner(System.in);
		//local variable used to verify if the menu is being correctly answered
		boolean MenuSuccess=false;
		//local variable containing the menu option choosed by the user
		int option = 0;
		//using a while loop to make sure the user inserts correct value for menu
		while(!MenuSuccess && option_==0) {
			System.out.println("\n\n(====================+======================)");
			System.out.println("[1]. Calculate total value of the magazine.");
			System.out.println("[2]. Sell a product.");
			System.out.println("[3]. Insert a new product.");
			System.out.println("[4]. Search a product through unique code.");
			System.out.println("[5]. Save and exit");
			System.out.println("(====================+======================)");
			System.out.println("Select an option(1,2,3,4,5): ");
			//verify if the input range is correct
			try {
				option = Integer.parseInt(mscanner.nextLine());
				if(option < 0 || option > 6)
					System.out.println("[Error]. You must enter a NUMBER between 1 and 5");
				else
					MenuSuccess=true;
				
			} catch (NumberFormatException e) {
				System.err.println("A system error occurred, find the error details below:");
				System.err.println("Given input is not an integer, \"1, 2, 3, 4, 5\".");
			}
			
		}
		//magic part! this allows script to autoselect an option for the user!
		if(option_!= 0) option = option_;
		//process user input using a switch case
		switch (option) {
		case 1:
		{
			f.calculateTotalValue(emp, mscanner, f);
			break;
		}
		case 2:
		{
			f.sellProduct(emp, mscanner, f);
			break;
		}	
		case 3:
		{
			if(emp.size()>=new MainClass().MaxStorage)
			{
				System.err.println("Error! Max storage capacity reached, cannot add more products!");
				f.HandleMenu(emp);
				break;
			}
			else
			{
				f.insertNewItem(emp, mscanner, f);
			}
			break;
		}
		case 4:
		{
			f.searchProduct(emp, mscanner, f);
			break;
		}
		case 5:
		{
			System.out.println("Now saving the file...");
			f.SaveFile(emp);
			System.out.println("Successfully saved the file... the java application is about to exit.");
			System.exit(0);
			break;
		}
		default:
			break; // kind of useless as inputs are already checked however theres no harm having it anyway
		}
		mscanner.close(); // close the scanner to avoid memory leak
	}
	
	/**
	 * Load the configuration file with all the checks inside
	 * @return 	returns a list of type Product
	 * @throws IOException 
	 */
	public List<Product> loadconfig() throws IOException
	{
		System.out.println("Loading configuration file...");
		List<String> lines = new ArrayList<String>();
		File file = new File("emporio.txt");
		BufferedReader br = null;	
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e1) {
			System.err.println("File not found, now creating the file..");
			BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream("emporio.txt"), "utf-8"));
			bf.close();
			fr = new FileReader(file);
		}
		 br = new BufferedReader(fr);
		 String st; 
		 int errors=0;
		 try {
			while ((st = br.readLine()) != null)
				if(st.contains(";"))
					lines.add(st);
				else {
					System.err.println("[Error] Skipping a line due to possible syntax error!");
					errors++;
				}
		} catch (IOException e) {
			System.err.println("General error has occured, find the error details below: ");
			e.printStackTrace();
		}
	 	System.out.println("Configuration file succesfully loaded ("+lines.size()+" items found)");
	 	
	 	// load objects
	 	System.out.println("Creating objects...");
	 	List<Product> temp_list =  new ArrayList<>(); 
	 	for(int i = 0; i < lines.size(); i++)
	 	{
	 		if(i>=new MainClass().MaxStorage) {System.out.println("Max storage capacity exceeded, "+(lines.size()-i)+" item will not be loaded"); break;}
	 		String[] data = lines.get(i).split(";");
	 		Product ogg_emp;
	 		ogg_emp = LoadObject(data);
	 		switch(VerifyConditions(temp_list, ogg_emp))
	 		{
		 		case 1:
		 			temp_list.add(ogg_emp); break;
		 		case 997:
		 		{
		 			errors++;
		 			System.out.println("[Error] Error while loading object "+i+": unit of measure unrecognized.");
		 			break;
		 		}
		 		case 998:
		 		{
		 			errors++;
		 			System.out.println("[Error] Error while loading object "+i+": Duplicate.");
		 			break;
		 		}
		 		case 999:
		 		{
		 			errors++;
		 			System.out.println("[Error] Error while loading object "+i+": Negative stock amount.");
		 			break;
		 		}
		 		
	 		}
	 	}
	 	System.out.println("Sucessfully created "+temp_list.size()+"/"+new MainClass().MaxStorage+" objects ("+errors+" items skipped)");
	 	SaveFile(temp_list);
	 	try {
			//close the file and the handler once we are done using them to avoid memory leaks.
	 		br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 	//return the filtered list to the caller (main in this case)
	 	return temp_list;
	}
	
	public void searchProduct(List<Product> emp, Scanner mscanner, Functions f)
	{
		System.out.println("Please insert your unique code: ");
		System.out.println("Type 'exit' to cancel search.");
		String code = mscanner.nextLine();
		if(code.equals("exit")) f.HandleMenu(emp);
		long start = System.currentTimeMillis(); 
		System.out.println("Now searching for an item with code "+code+", please wait...\n");
		boolean found=true;
		for (Product emprorio : emp) 
		{
			if(emprorio.getUNIQUE_CODE().equals(code))
			{
				long end = System.currentTimeMillis();
				System.out.println("Item succcessfully found, the search took "+(end - start)+"ms:");
				System.out.println(emprorio.toString());
				System.out.println("\n\nPress any key to continue...");
				mscanner.nextLine();
				//call the function itself to show the main menu once the user is done with the current action
				f.HandleMenu(emp);
				break;
			}
			else
			{
				found=false;
			}
		}
		if(!found) System.out.println("Failed to find a product with the code \""+code+"\", try again!");
		//call the function itself to show the main menu AND autoselect the option 4 so that user gets asked
		//about the code directly
		HandleMenu(emp, 4);
	}
	
	/**
	 * handle the insert process of the new item/product
	 * @param emp
	 * @param mscanner
	 * @param f
	 */
	public void insertNewItem(List<Product> emp, Scanner mscanner, Functions f)
	{
		int temp_int=0, pieces = 0, kg=0,department=0;
		float price = 0, watts=0, that_strange_unit = 0;
		String name="INVALID_NAME";
		tColor color = null;
		Categories dep = null;
		boolean InnerMenuSuccess=false;
		while(!InnerMenuSuccess)
		{
			System.out.println("\n\n(====================+(NEW PRODUCT)+======================)");
			System.out.println("Select product department: ");
			System.out.println("[1]. Wood");
			System.out.println("[2]. Electronics");
			System.out.println("Select an option(1,2): ");
			//verify if the input range is correct
			try {
				department = Integer.parseInt(mscanner.nextLine());
				if(department< 0 || department > 2)
					System.out.println("[Error]. You must enter a NUMBER between 1 and 2");
				else
					InnerMenuSuccess=true;
				
			} catch (NumberFormatException e) {
				System.err.println("A system error occurred, find the error details below:");
				System.err.println("Given input is not an integer, \"1, 2\".");
				System.err.println("Java error: "+e.toString());
			}
		}
		do 
		{
			try {
				System.out.println("Insert the amount of available pieces: ");
				pieces = Integer.parseInt(mscanner.nextLine());
				if(pieces< 0)
					System.out.println("[Error]. You must enter a NUMBER bigger than 0.");
				} catch (NumberFormatException e) {
					System.err.println("A system error occurred, find the error details below:");
					System.err.println("Given input is not an integer.");
					System.err.println("Java error: "+e.toString());
					pieces = INVALID_INTEGER; // program manipulation, allows the program to go back to *do* statement
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}		
		} while(pieces <0);
		
		do 
		{
			try {
				System.out.println("Insert item's price: ");
				price = Float.parseFloat(mscanner.nextLine());
				if(price< 0)
					System.out.println("[Error]. You must enter a NUMBER bigger than 0.");
				} catch (NumberFormatException e) {
					System.err.println("A system error occurred, find the error details below:");
					System.err.println("Given input is not a float.");
					System.err.println("Java error: "+e.toString());
					price = INVALID_INTEGER; 
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}		
		} while(price < 0);
		String uuid = UUID.randomUUID().toString();
		System.out.println("Randomly generated uinque ID: "+uuid);
		do 
		{
			try {
				System.out.println("Insert item's name: ");
				name = (mscanner.nextLine());
				if(name.length() <0 || name.length() > 20)
					System.out.println("[Error]. Name length must be between 0 and 12 chars!");
				} catch (Exception e) {
					System.err.println("Java error: "+e.toString());
					name = ""; 
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}		
		} while(name.length() <0 || name.length() > 20);
		if(name.equals("Lamp") && department == 2)
		{
			do 
			{
				try {
					System.out.println("Insert item's that_strange_unit: ");
					that_strange_unit = Float.parseFloat((mscanner.nextLine()));
					if(that_strange_unit <0)
						System.out.println("[Error]. Value must be greater than 0!");
					} catch (Exception e) {
						System.err.println("Java error: "+e.toString());
						that_strange_unit = INVALID_INTEGER; 
						try {
							TimeUnit.MILLISECONDS.sleep(500);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}		
			} while(that_strange_unit <0);
		}
		switch(department)
		{
			case 1:
			{
				dep = Categories.Wood;
				do 
				{
					try {
						System.out.println("Insert Wood weight (kg): ");
						kg = Integer.parseInt(mscanner.nextLine());
						if(kg <0)
							System.out.println("[Error]. You must enter a NUMBER bigger than 0.");
						} catch (NumberFormatException e) {
							System.err.println("A system error occurred, find the error details below:");
							System.err.println("Given input is not a integer.");
							System.err.println("Java error: "+e.toString());
							kg = INVALID_INTEGER; 
							try {
								TimeUnit.MILLISECONDS.sleep(500);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}		
				} while(kg < 0);
				
				boolean aquireColorNameMenu=false;
				while(!aquireColorNameMenu)
				{
					
					System.out.println("\n\n(====================+(WOOD COLOR)+======================)");
					System.out.println("Select wood color: ");
					System.out.println("[1]. Pine");
					System.out.println("[2]. Maple");
					System.out.println("[3]. Mahogany");
					System.out.println("[4]. Oak");
					System.out.println("[5]. Beech");
					System.out.println("[6]. Spruce");
					System.out.println("[7]. Birch");
					System.out.println("Select an option(1,2, 3, 4, 5, 6, 7): ");
					//verify if the input range is correct
					try {
						temp_int = Integer.parseInt(mscanner.nextLine());
						if(temp_int< 0 || temp_int > 7)
							System.out.println("[Error]. You must enter a NUMBER between 1 and 7");
						else
							aquireColorNameMenu=true;	
					} catch (NumberFormatException e) {
						System.err.println("A system error occurred, find the error details below:");
						System.err.println("Given input is not an integer, \"1, 2, 3, 4, 5, 6, 7\".");
						System.err.println("Java error: "+e.toString());
					}
				}
				switch(temp_int) {
				case 1: {color = tColor.Pine; break;}
				case 2: {color = tColor.Maple; break;}
				case 3: {color = tColor.Mahogany; break;}
				case 4: {color = tColor.Oak; break;}
				case 5: {color = tColor.Beech; break;}
				case 6: {color = tColor.Spruce; break;}
				case 7: {color = tColor.Birch; break;}
				}
				Product Temp_Object = null;
				Temp_Object = new Wood(dep.toString(), "KG", pieces, price, uuid, name, kg, color.toString());
				emp.add(Temp_Object);
				SaveFile(emp);
				System.out.println("Congralatulations! Product succesfully inserted!");
				System.out.println("presss any key to go back to main menu");
				mscanner.nextLine();
				f.HandleMenu(emp);
			}
			case 2:
			{
				dep = Categories.Electronics;
				do {
					try {
						System.out.println("Insert product power (watts): ");
						watts = Float.parseFloat(mscanner.nextLine());
						if(watts <0)
							System.out.println("[Error]. You must enter a NUMBER bigger than 0.");
						} catch (NumberFormatException e) {
							System.err.println("A system error occurred, find the error details below:");
							System.err.println("Given input is not a float.");
							System.err.println("Java error: "+e.toString());
							watts = INVALID_INTEGER; 
							try {
								TimeUnit.MILLISECONDS.sleep(500);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}		
				} while(watts < 0);
				Product Temp_Object = null;
				if(name.equals("Lamp"))
					Temp_Object = new Lamp(dep.toString(), "WATT", pieces, price, uuid, name, watts, that_strange_unit);
				else
					Temp_Object = new Electronics(dep.toString(), "WATT", pieces, price, uuid, name, watts);
				
				emp.add(Temp_Object);
				SaveFile(emp);
				System.out.println("Congralatulations! Product succesfully inserted!");
				System.out.println("presss any key to go back to main menu");
				mscanner.nextLine();
				f.HandleMenu(emp);
			}
			break;	
		}
	}
	/**
	 * handle the sale of a product/sale
	 * @param emp
	 * @param mscanner
	 * @param f
	 */
	public void sellProduct(List<Product> emp, Scanner mscanner, Functions f)
	{
		System.out.println("Please insert unique code of the item to sell: ");
		String code = mscanner.nextLine();
		System.out.println("Now searching for an item with code "+code+", please wait...\n");
		boolean found=true;
		int quantity=0;
		for (Product emprorio : emp) 
		{
			if(emprorio.getUNIQUE_CODE().equals(code))
			{
				
				if(emprorio.getPIECES_AVAILABLE()<=0) {
					System.out.println("Product out of stock ¯\\_(ツ)_/¯, press any key to continue...");
					mscanner.nextLine();
					f.HandleMenu(emp);
				}
				do {
					try {
					System.out.println("Insert the quantity of "+emprorio.getNAME()+" to sell: ");
					quantity = Integer.parseInt(mscanner.nextLine());
					if(quantity< 0)
						System.out.println("[Error]. You must enter a NUMBER bigger than 0.");
					if(emprorio.getPIECES_AVAILABLE()<quantity)
					{
						System.out.println("Error! There are only "+emprorio.getPIECES_AVAILABLE()+" pieces of "+emprorio.getNAME()+" available on magazine.");
						quantity = INVALID_INTEGER;
					}
					} catch (NumberFormatException e) {
						System.err.println("A system error occurred, find the error details below:");
						System.err.println("Given input is not an integer.");
						System.err.println("Java error: ");
						e.printStackTrace();
						quantity = INVALID_INTEGER; // program manipulation, allows the program to go back to *do* statement
						try {
							TimeUnit.MILLISECONDS.sleep(500);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
				while(quantity<0 && emprorio.getPIECES_AVAILABLE() > quantity);

				System.out.println("Successfully sold "+quantity+" "+emprorio.getNAME()+"s worth $"+(emprorio.getPRICE()*quantity)+"");
				System.out.println("press any key to continue...");
				emprorio.setPIECES_AVAILABLE(emprorio.getPIECES_AVAILABLE()-quantity);
				SaveFile(emp);
				//call the function itself to show the main menu once the user is done with the current action
				mscanner.nextLine();
				f.HandleMenu(emp);
				break;
			}
			else
			{
				found=false;
			}
		}
		if(!found) System.err.println("Failed to find a product with the code \""+code+"\", try again!");
		//call the function itself to show the main menu AND autoselect the option 2 so that user gets asked
		//about the code directly
		HandleMenu(emp, 2);
	}
	/**
	 * Calculate the total value of the magazine
	 * @param emp
	 * @param mscanner
	 * @param f
	 */
	public void calculateTotalValue(List<Product> emp, Scanner mscanner, Functions f)
	{
		//getting current timestamp in millisecond to monitor the time take to search
		long start = System.currentTimeMillis(); 
		System.out.println("Now calculating total value, please wait...");
		
		NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();

		long end = System.currentTimeMillis();
		System.out.println("Total value successfully calculated in "+(end - start)+"ms ("+emp.size()+" items): "+defaultFormat.format(getTotalValue(emp))+"");
		System.out.println("Press any key to continue...");
		mscanner.nextLine(); // await user input of any kind
		//call the function itself to show the main menu once the user is done with the current action
		f.HandleMenu(emp);
	}
	
	/**
	 * @param emp
	 * @return
	 */
	public double getTotalValue(List<Product> emp)
	{
		double total=0;
		for (Product emprorio : emp) 
		{
			total += (emprorio.getPRICE())*(emprorio.getPIECES_AVAILABLE());
		}
		return total;
	}
	
	/**
	 * @param data
	 * @return
	 */
	private Product LoadObject(String[] data)
	{
		Product Togg = null;
		if(data[0].equals("Electronics") && !data[5].equals("Lamp"))
			Togg = new Electronics(data[0], data[1], Integer.parseInt(data[2]), Float.parseFloat(data[3]), data[4], data[5], Float.parseFloat(data[6])) {};
		else if(data[0].equals("Electronics") && data[5].equals("Lamp"))
			Togg = new Lamp(data[0], data[1], Integer.parseInt(data[2]), Float.parseFloat(data[3]), data[4], data[5], Float.parseFloat(data[6]), Float.parseFloat(data[7])) {};
		else if(data[0].equals("Wood"))
			Togg = new Wood(data[0], data[1], Integer.parseInt(data[2]), Float.parseFloat(data[3]), data[4], data[5], Integer.parseInt(data[6]), data[7]) {};
		return Togg;
	}
	
	/**s
	 * Verify all the required conditions, e.g: if 2 objects have same unique code
	 * @param temp_list		requires a list of type Emprorio
	 * @param data			requires an object of type Emprorio
	 * @return 				returns response as integer
	 */	public int VerifyConditions(List<Product> temp_list, Product data)
	{
		if(data.getPIECES_AVAILABLE() < 0) return 999;
		if(!data.getUNIT_OF_MEASURE().equals(mUnit.WATT.toString()) && !data.getUNIT_OF_MEASURE().equals(mUnit.KG.toString())) return 997;
		for(Product cur : temp_list)
		{
			if(cur.getUNIQUE_CODE().equals(data.getUNIQUE_CODE())) return 998;
		}
		return 1;
	}
	
	
	
	/**
	 * This function is used to save the file (storage)
	 * @param list				Main list containing the objects
	 * @throws IOException 
	 */
	public void SaveFile(List<Product> list)
	{
		FileWriter file = null;
		try {
			file = new FileWriter("emporio.txt");
		} catch (FileNotFoundException e) {
			System.err.println("File not found, now creating the file..");
			BufferedWriter bf = null;
			try {
				bf = new BufferedWriter(new OutputStreamWriter(
				          new FileOutputStream("emporio.txt"), "utf-8"));
				file = new FileWriter("emporio.txt");
			} catch (IOException io) {
				System.err.println("Error creating the file... find error details below: ");
				io.printStackTrace();
			}
			try {
				bf.close();
			} catch (IOException e1) {
				System.err.println("Error closing the file... find error details below: ");
				e1.printStackTrace();
			}			
		}
		catch(IOException io)
		{
			System.err.println("General error has occured, find error detaulss below");
			io.printStackTrace();
		}
		BufferedWriter bw = null;	
		bw = new BufferedWriter(file);
		
	 	for(Product emporio : list)
		{
			String str = "";
			if(emporio.getDEPARTMENT().equals("Electronics") && !emporio.getNAME().equals("Lamp"))
				str = ""+emporio.getDEPARTMENT()+";"+emporio.getUNIT_OF_MEASURE()+";"+emporio.getPIECES_AVAILABLE()+";"+emporio.getPRICE()+";"+emporio.getUNIQUE_CODE()+";"+emporio.getNAME()+";"+((Electronics) emporio).getWatts_()+"\n";
			else if(emporio.getDEPARTMENT().equals("Electronics") && emporio.getNAME().equals("Lamp"))
				str = ""+emporio.getDEPARTMENT()+";"+emporio.getUNIT_OF_MEASURE()+";"+emporio.getPIECES_AVAILABLE()+";"+emporio.getPRICE()+";"+emporio.getUNIQUE_CODE()+";"+emporio.getNAME()+";"+((Electronics) emporio).getWatts_()+";"+((Lamp) emporio).getThat_Strange_unit()+"\n";
			else if(emporio.getDEPARTMENT().equals("Wood"))
				str = ""+emporio.getDEPARTMENT()+";"+emporio.getUNIT_OF_MEASURE()+";"+emporio.getPIECES_AVAILABLE()+";"+emporio.getPRICE()+";"+emporio.getUNIQUE_CODE()+";"+emporio.getNAME()+";"+((Wood) emporio).getKg()+";"+((Wood) emporio).getTreeColor()+"\n";
			else
				System.out.println("Unrecognized category detected! Ignoring...");
			if(!str.isEmpty())
			{
				try {
					bw.write(str);
					bw.flush();
				} catch (IOException e) {
					System.out.println("Error closing the buffers... find error details below: ");
					e.printStackTrace();
				}
				
			}
		}
	 	try {
			bw.close();
			file.close();
		} catch (IOException e) {
			System.out.println("Error closing the buffers... find error details below: ");
			e.printStackTrace();
		}
	 	
	}

}
