public class CengHashRow {

	private String hashPrefix;
	private CengBucket bucket;

	public CengHashRow() {
		this.hashPrefix = "";
		this.bucket = new CengBucket();
	}

	public CengHashRow(String hashPrefix, CengBucket bucket) {
		this.hashPrefix = hashPrefix;
		this.bucket = bucket;
	}
	public CengHashRow(CengHashRow row){
		this.hashPrefix = row.hashPrefix();
		this.bucket = new CengBucket(row.getBucket());
	}

	public void setBucket(CengBucket bucket) {
		this.bucket = bucket;
	}
	public void addPoke(CengPoke poke){ this.bucket.addPoke(poke); }
	public void incrLocalDepth(){ this.bucket.incrLocalDepth(); }
	public void setHashPrefix(String s){ this.hashPrefix = s; }
	public void incrHashPrefix(char x){ this.hashPrefix+=x; }
// GUI-Based Methods
	// These methods are required by GUI to work properly.
	
	public String hashPrefix()
	{
		// TODO: Return row's hash prefix (such as 0, 01, 010, ...)
		/*if(!hashPrefix.equals("")){
			return hashPrefix;
		}*/
		return hashPrefix;
	}
	
	public CengBucket getBucket()
	{
		// TODO: Return the bucket that the row points at.
		return bucket;
	}
	
	public boolean isVisited()
	{
		// TODO: Return whether the row is used while searching.
		return false;		
	}
	
	// Own Methods
	public void print(){
		System.out.println("\t\"row\": {");
		System.out.println("\t\t\"hashPref\": " + hashPrefix);
		bucket.print();
		System.out.println("\t}");
	}
}
