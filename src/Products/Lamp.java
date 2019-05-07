package Products;

public class Lamp extends Electronics {

	private float That_Strange_unit;
	public Lamp(String dp, String unit, int pieces, float price, String code, String name, float watt_, float That_Strange_unit_)
	{
		super(dp, unit, pieces, price, code, name, watt_);
		setThat_Strange_unit(That_Strange_unit_);
	}
	public float getThat_Strange_unit() {
		return That_Strange_unit;
	}
	public void setThat_Strange_unit(float that_Strange_unit) {
		That_Strange_unit = that_Strange_unit;
	}
	public String toString()
	{
		String toReturn;
		toReturn = super.toString() + "\nMade goods: "+this.getThat_Strange_unit()/this.getWatts_()+"";
		return toReturn;
	}
}
