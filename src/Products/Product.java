package Products;

public abstract class Product {

	private String DEPARTMENT;
	private String UNIT_OF_MEASURE;
	private int PIECES_AVAILABLE = 0;
	private float PRICE ;
	private String UNIQUE_CODE;
	private String NAME;
	
	// constructor
		public Product(String dp, String unit, int pieces, float price, String code, String name)
		{
			setDEPARTMENT(dp);
			setUNIT_OF_MEASURE(unit);
			setPIECES_AVAILABLE(pieces);
			setPRICE(price);
			setUNIQUE_CODE(code);
			setNAME(name);
		}
		
		public String getDEPARTMENT() {
			return DEPARTMENT;
		}

		public void setDEPARTMENT(String dEPARTMENT) {
			DEPARTMENT = dEPARTMENT;
		}

		public String getUNIT_OF_MEASURE() {
			return UNIT_OF_MEASURE;
		}

		public void setUNIT_OF_MEASURE(String uNIT_OF_MEASURE) {
			UNIT_OF_MEASURE = uNIT_OF_MEASURE;
		}

		public int getPIECES_AVAILABLE() {
			return PIECES_AVAILABLE;
		}

		public void setPIECES_AVAILABLE(int pIECES_AVAILABLE) {
			PIECES_AVAILABLE = pIECES_AVAILABLE;
		}

		public float getPRICE() {
			return PRICE;
		}

		public void setPRICE(float pRICE) {
			PRICE = pRICE;
		}

		public String getUNIQUE_CODE() {
			return UNIQUE_CODE;
		}

		public void setUNIQUE_CODE(String uNIQUE_CODE) {
			UNIQUE_CODE = uNIQUE_CODE;
		}

		public String getNAME() {
			return NAME;
		}

		public void setNAME(String nAME) {
			NAME = nAME;
		}
		public String toString()
		{
			String toReturn;
			toReturn = "Department: "+this.getDEPARTMENT()+"\nMeasure unit: "+this.getUNIT_OF_MEASURE()+"\nPieces available in magazine: "+this.getPIECES_AVAILABLE()+"\nPrice: "+this.getPRICE()+"\nUnique code: "+this.getUNIQUE_CODE()+"\nItem name: "+this.getNAME()+"\n";
			return toReturn;
		}
}
