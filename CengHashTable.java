import java.util.ArrayList;

public class CengHashTable {
	private int globalDepth;
	private int empty;
	private ArrayList<CengHashRow> directories;
	public CengHashTable()
	{
		// TODO: Create a hash table with only 1 row.
		this.globalDepth = 0;
		this.empty = 0;
		this.directories = new ArrayList<CengHashRow>();
		CengHashRow firstRow = new CengHashRow();
		this.directories.add(firstRow);
		//create the container
	}
	public int getGlobalDepth() {
		return globalDepth;
	}

	public void incrGlobalDepth() {
		this.globalDepth++;
	}
	public void incrEmpty(){ this.empty++; }

	public void deletePoke(Integer pokeKey)
	{
		int hashValue = (int) pokeKey % CengPokeKeeper.getHashMod();
		String binary = Integer.toBinaryString(hashValue);
		int diff = log(CengPokeKeeper.getHashMod(),2) - binary.length();
		for(int i=0;i<diff;i++)
			binary = '0' + binary;
		String x = globalDepth > 0 ? binary.substring(0,this.globalDepth) : "0";
		CengHashRow row = rowAtIndex(Integer.parseInt(x,2));
		ArrayList<CengPoke> p = new ArrayList<>(row.getBucket().getPokes());
		boolean found = false;
		for(int i=0;i<p.size();i++){
			if(p.get(i).pokeKey() == pokeKey){
				found = true;
				break;
			}
		}
		if(!found){
			System.out.println("\"search\": {");
			System.out.println("}");
			return;
		}
		String hashPref = row.hashPrefix();
		int localDepth = row.getBucket().getLocalDepth();
		String basePref = hashPref.substring(0,localDepth);
		for(int i=0;i<getGlobalDepth() - localDepth;i++)
			basePref += "0";
		if(globalDepth == 0) basePref = "0";
		int base = Integer.parseInt(basePref,2);
		int row_count = (int) Math.pow(2,getGlobalDepth()-localDepth);
		boolean emptied = false;
		for(int i=base;i<base+row_count;i++){
			CengHashRow r = rowAtIndex(i);
			CengBucket bucket = r.getBucket();
			bucket.removePoke(pokeKey);
			if(bucket.getPokes().size() == 0) emptied = true;
			r.setBucket(bucket);
			directories.set(i,r);
		}
		if(emptied) incrEmpty();
		System.out.println("\"delete\": {");
		System.out.println("\t\"emptyBucketNum\": " + empty);
		System.out.println("}");
		// TODO: Empty Implementation
	}

	public void addPoke(CengPoke poke)
	{
		String hashPrefix = hash(poke.pokeKey());
		if(this.globalDepth != 0){//implement
			int index = Integer.parseInt(hashPrefix,2);//index of hashrow
			CengHashRow row = rowAtIndex(index);
			if(row.getBucket().getPokes().size() >= CengPokeKeeper.getBucketSize()){
				boolean flag = split(index,poke);
				if(!flag)//Redistribution did not occur! Adding again
					addPoke(poke);
			}
			else{
				int localDepth = row.getBucket().getLocalDepth();
				String hashPref = row.hashPrefix();
				String basePref = hashPref.substring(0,localDepth);
				for(int i=0;i<getGlobalDepth() - localDepth;i++)
					basePref += "0";
				int base = Integer.parseInt(basePref,2);
				int row_count = (int) Math.pow(2,getGlobalDepth()-localDepth);
				for(int i = base; i<base+row_count;i++){
					CengHashRow rowi = rowAtIndex(i);
					rowi.addPoke(poke);
					directories.set(i,rowi);
				}
			}
		}
		else{
			CengHashRow row = rowAtIndex(0);
			if(row.getBucket().getPokes().size() == CengPokeKeeper.getBucketSize()){
				boolean flag = split(0,poke);
				if(!flag)//Redistribution did not occur! Adding again
					addPoke(poke);
			}
			else{
				row.addPoke(poke);
				directories.set(0,row);
			}
		}
		// TODO: Empty Implementation
	}
	public boolean split(int index,CengPoke poke){
		CengHashRow row1 = rowAtIndex(index);
		if(row1.getBucket().getLocalDepth() == getGlobalDepth()){ //enlarge the table
			incrGlobalDepth();
			row1.incrLocalDepth();
			CengBucket bucket2 = new CengBucket(row1.getBucket().getLocalDepth());
			CengHashRow row2 = new CengHashRow(row1.hashPrefix() + '1',bucket2);
			row1.incrHashPrefix('0');
			redistribute(row1,row2,poke);
			directories.set(index,row1);
			directories.add(index+1,row2);
			for(int i=0;i<Math.pow(2,globalDepth);i+=2){//enlarge the hash table
				if(i!=index){
					CengHashRow first = rowAtIndex(i);//copy the row
					CengHashRow second = new CengHashRow(first);
					first.incrHashPrefix('0');//update prefixes
					second.incrHashPrefix('1');
					directories.set(i,first);
					directories.add(i+1,second);//add new row
					index++;
				}
			}
			return row2.getBucket().getPokes().size() != 0 && row1.getBucket().getPokes().size() != 0 ;//redistribution successful or failed
		}
		else{//do not enlarge the hashtable
			int localDepth = row1.getBucket().getLocalDepth();
			boolean flag = false;
			String hashPref = row1.hashPrefix();
			String basePref = hashPref.substring(0,localDepth);
			for(int i=0;i<getGlobalDepth() - localDepth;i++)
				basePref += "0";
			int base = Integer.parseInt(basePref,2);
			int last = base + (int) Math.pow(2,getGlobalDepth()-localDepth) - 1;
			String lastPref = Integer.toBinaryString(last);
			int diff = getGlobalDepth() - lastPref.length();
			for(int i=0;i<diff;i++)
				lastPref = '0' + lastPref;
			CengHashRow base_row = new CengHashRow(rowAtIndex(base));
			base_row.incrLocalDepth();
			CengBucket bucket_new = new CengBucket(base_row.getBucket().getLocalDepth());
			CengHashRow last_row = new CengHashRow(lastPref,bucket_new);
			redistribute(base_row,last_row,poke);
			flag = base_row.getBucket().getPokes().size() != 0 && last_row.getBucket().getPokes().size() != 0 ;
			int row_count = (int) Math.pow(2,getGlobalDepth()-base_row.getBucket().getLocalDepth());
			int i;
			for(i = base;i<base+row_count;i++){//handle the other rows in same group
				CengHashRow row = new CengHashRow(rowAtIndex(i).hashPrefix(),base_row.getBucket());
				directories.set(i, row);
			}
			for(;i<=last;i++) {
				CengHashRow row = new CengHashRow(rowAtIndex(i).hashPrefix(), last_row.getBucket());
				directories.set(i, row);
			}
			return flag;
		}
	}
	public void redistribute(CengHashRow row1, CengHashRow row2, CengPoke poke){
		int localDepth = row1.getBucket().getLocalDepth();
		CengBucket bucket1 = row1.getBucket();
		ArrayList<CengPoke> bucketx = new ArrayList<CengPoke>(bucket1.getPokes());
		CengBucket bucket2 = row2.getBucket();
		int size = CengPokeKeeper.getBucketSize();
		for(int i=0;i<size;i++){//distribute pokes in bucket1
			String hashPrefix = hash(bucketx.get(i).pokeKey()).substring(0,localDepth);
			if(row2.hashPrefix().startsWith(hashPrefix)){
				bucket2.addPoke(bucketx.get(i));// update other rows pointing to that bucket
				bucket1.getPokes().remove(bucketx.get(i)); //update other rows pointing to that bucket
			}
		}
		//place the new poke
		String hashPrefix = hash(poke.pokeKey()).substring(0,localDepth);
		if(row1.hashPrefix().startsWith(hashPrefix)){
			if(row1.getBucket().getPokes().size() < size){
				bucket1.addPoke(poke);//update other buckets pointing to that bucket
			}
		}
		else if(row2.hashPrefix().startsWith(hashPrefix)){
			if(row2.getBucket().getPokes().size() < size){
				bucket2.addPoke(poke);//update other buckets pointing to that bucket
			}
		}
		row1.setBucket(bucket1);
		row2.setBucket(bucket2);
	}

	
	public void searchPoke(Integer pokeKey)
	{
		int hashValue = (int) pokeKey % CengPokeKeeper.getHashMod();
		String binary = Integer.toBinaryString(hashValue);
		int diff = log(CengPokeKeeper.getHashMod(),2) - binary.length();
		for(int i=0;i<diff;i++)
			binary = '0' + binary;
		String x = globalDepth > 0 ? binary.substring(0,this.globalDepth) : "0";
		CengHashRow row = rowAtIndex(Integer.parseInt(x,2));
		ArrayList<CengPoke> p = new ArrayList<>(row.getBucket().getPokes());
		boolean found = false;
		for(int i=0;i<p.size();i++){
			if(p.get(i).pokeKey() == pokeKey){
				found = true;
				break;
			}
		}
		System.out.println("\"search\": {");
		if(!found){
			System.out.println("}");
			return;
		}
		int localDepth = row.getBucket().getLocalDepth();
		String hashPref = row.hashPrefix();
		String basePref = hashPref.substring(0,localDepth);
		for(int i=0;i<getGlobalDepth() - localDepth;i++)
			basePref += "0";
		if(globalDepth == 0) basePref = "0";
		int base = Integer.parseInt(basePref,2);
		int row_count = (int) Math.pow(2,getGlobalDepth()-localDepth);
		for(int i=base;i<base+row_count;i++){
			CengHashRow r = rowAtIndex(i);
			r.setVisited();
			r.print();
			if(!(i==base+row_count-1)){
				System.out.println(",");
				continue;
			}
			System.out.println();
		}
		System.out.println("}");
		// TODO: Empty Implementation
	}
	// GUI-Based Methods
	// These methods are required by GUI to work properly.
	
	public int prefixBitCount()
	{
		// TODO: Return table's hash prefix length.
		return this.globalDepth;
	}
	
	public int rowCount()
	{
		// TODO: Return the count of HashRows in table.
		return directories.size();
	}
	
	public CengHashRow rowAtIndex(int index)
	{
		// TODO: Return corresponding hashRow at index.
		return directories.get(index);
	}
	
	// Own Methods
	public void print()
	{
		System.out.println("\"table\": {");
		int size = directories.size();
		for(int i = 0;i<size;i++){
			directories.get(i).print();
			if(!(i == size-1)){
				System.out.println(",");
				continue;
			}
			System.out.println();
		}
		System.out.println("}");
		// TODO: Empty Implementation
	}
	public static int log(int x, int b) {
		return (int) (Math.log(x) / Math.log(b));
	}
	public String hash (Integer pokeKey){
		int hashValue = (int) pokeKey % CengPokeKeeper.getHashMod();
		String binary = Integer.toBinaryString(hashValue);
		int diff = log(CengPokeKeeper.getHashMod(),2) - binary.length();
		for(int i=0;i<diff;i++)
			binary = '0' + binary;
		return binary.substring(0,this.globalDepth);
	}


}
