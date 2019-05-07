package interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Scanner; 
import Products.Product;
import functions.Functions;

public interface FInterface {

	public List<Product> loadconfig() throws IOException;
	int VerifyConditions(List<Product> temp_list, Product data);
	void HandleMenu(List<Product> emp);
	void insertNewItem(List<Product> emp, Scanner mscanner, Functions f);
	void searchProduct(List<Product> emp, Scanner mscanner, Functions f);
	void sellProduct(List<Product> emp, Scanner mscanner, Functions f);
	void calculateTotalValue(List<Product> emp, Scanner mscanner, Functions f);
	double getTotalValue(List<Product> emp);
	void SaveFile(List<Product> list);
}
