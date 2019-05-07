package Products;

public class Wood extends Product {

	private String treeColor;
	private int kg;
	public Wood(String dp, String unit, int pieces, float price, String code, String name, int kg_, String color)
	{
		super(dp, unit, pieces, price, code, name);
		setKg(kg_);
		setTreeColor(color);
	}
	public String getTreeColor() {
		return treeColor;
	}
	public void setTreeColor(String treeColor) {
		this.treeColor = treeColor;
	}
	public  int getKg() {
		return kg;
	}

	public  void setKg(int kg) {
		this.kg = kg;
	}
	public String toString()
	{
		String toReturn;
		toReturn = super.toString() + "Item weight: "+this.getKg()+"\nItem color: "+this.getTreeColor()+"";
		return toReturn;
	}
}
