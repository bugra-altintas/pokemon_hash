public class CengPoke {
	
	private Integer pokeKey;
	
	private String pokeName;
	private String pokePower;
	private String pokeType;
	
	public CengPoke(Integer pokeKey, String pokeName, String pokePower, String pokeType)
	{
		this.pokeKey = pokeKey;
		this.pokeName = pokeName;
		this.pokePower = pokePower;
		this.pokeType = pokeType;
	}
	
	// Getters
	
	public Integer pokeKey()
	{
		return pokeKey;
	}
	public String pokeName()
	{
		return pokeName;
	}
	public String pokePower()
	{
		return pokePower;
	}
	public String pokeType()
	{
		return pokeType;
	}
		
	// GUI method - do not modify
	public String fullName()
	{
		return "" + pokeKey() + "\t" + pokeName() + "\t" + pokePower() + "\t" + pokeType;
	}
		
	// Own Methods
	public static int log(int x, int b) {
		return (int) (Math.log(x) / Math.log(b));
	}
	public void print(){
		System.out.println("\t\t\t\t\"poke\": {");
		String binary = Integer.toBinaryString((int) pokeKey % CengPokeKeeper.getHashMod());
		int diff = log(CengPokeKeeper.getHashMod(),2) - binary.length();
		for(int i=0;i<diff;i++)
			binary = '0' + binary;
		System.out.println("\t\t\t\t\t\"hash\": " + binary + ",");
		System.out.println("\t\t\t\t\t\"pokeKey\": " + pokeKey + ",");
		System.out.println("\t\t\t\t\t\"pokeName\": " + pokeName + ",");
		System.out.println("\t\t\t\t\t\"pokePower\": " + pokePower + ",");
		System.out.println("\t\t\t\t\t\"pokeType\": " + pokeType);
		System.out.print("\t\t\t\t}");
	}
}
