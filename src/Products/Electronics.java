package Products;

public class Electronics extends Product {

private float watts_;
	
	public Electronics(String dp, String unit, int pieces, float price, String code, String name, float watt_)
	{
		super(dp, unit, pieces, price, code, name);
		setWatts_(watt_);
	}
	public float getWatts_() {
		return watts_;
	}
	public void setWatts_(float watts_) {
		this.watts_ = watts_;
	}
	public String toString()
	{
		String toReturn;
		toReturn = super.toString() + "Item watt's: "+this.getWatts_()+"";
		return toReturn;
	}
}
