import java.util.ArrayList;

public class CengHashTable {
	private int globalDepth;
	private ArrayList<CengHashRow> directories;
	public CengHashTable()
	{
		// TODO: Create a hash table with only 1 row.
		this.globalDepth = 0;
		this.directories = new ArrayList<CengHashRow>();
		CengHashRow firstRow = new CengHashRow();
		this.directories.add(firstRow);
		//create the container
		System.out.println("Hash Table Created!");
	}
	public int getGlobalDepth() {
		return globalDepth;
	}

	public void setGlobalDepth(int globalDepth) {
		this.globalDepth = globalDepth;
	}

	public void incrGlobalDepth() {
		this.globalDepth++;
	}

	public ArrayList<CengHashRow> getDirectories() {
		return directories;
	}

	public void setDirectories(ArrayList<CengHashRow> directories) {
		this.directories = directories;
	}



	public void deletePoke(Integer pokeKey)
	{
		int hashValue = (int) pokeKey % CengPokeKeeper.getHashMod();
		CengHashRow row = rowAtIndex(hashValue);
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
		String zeros = "";
		int diff = globalDepth - row.getBucket().getLocalDepth(); //make last diff bits zero
		for(int i=0;i<diff;i++)
			zeros += '0';
		String hashPref = row.hashPrefix();
		String index_base = hashPref.substring(0,row.getBucket().getLocalDepth()) + zeros;
		int base = Integer.parseInt(index_base,2);
		int count = 0;
		for(int i=0;i<Math.pow(2,diff);i++){
			CengHashRow r = rowAtIndex(base+i);
			CengBucket bucket = r.getBucket();
			bucket.removePoke(pokeKey);
			if(bucket.getPokes().size() == 0) count++;
			r.setBucket(bucket);
			directories.set(base+i,r);
		}
		System.out.println("\"delete\": {");
		System.out.println("\t\"emptyBucketNum\": " + count);
		System.out.println("}");
		// TODO: Empty Implementation
	}

	public void addPoke(CengPoke poke)
	{
		//System.out.println("Adding: " + poke.pokeKey());
		String hashPrefix = hash(poke.pokeKey());
		if(this.globalDepth != 0){//implement
			int index = Integer.parseInt(hashPrefix,2);//index of hashrow
			CengHashRow row = rowAtIndex(index);
			//System.out.println("Adding to: " + directories.get(index).hashPrefix());
			if(row.getBucket().getPokes().size() == CengPokeKeeper.getBucketSize()){
				//implement
				boolean flag = split(index,poke);
				if(!flag){
					System.out.println("Redistribution did not occur! Adding again");
					addPoke(poke);
				}
			}
			else{
				row.addPoke(poke);
				directories.set(index,row);
				int ld = row.getBucket().getLocalDepth();
				if(globalDepth > ld){
					for(int i = 1;i<Math.pow(2,globalDepth-ld);i++){
						CengHashRow rowi = rowAtIndex(index+i);
						rowi.addPoke(poke);
						directories.set(index+i,rowi);
					}
				}
			}
		}
		else{
			CengHashRow row = rowAtIndex(0);
			if(row.getBucket().getPokes().size() == CengPokeKeeper.getBucketSize()){
				//implement
				boolean flag = split(0,poke);
			}
			else{
				row.addPoke(poke);
				directories.set(0,row);
			}
		}
		// TODO: Empty Implementation
	}
	public boolean split(int index,CengPoke poke){
		//System.out.println("Splitting index: " + index);
		CengHashRow row1 = rowAtIndex(index);
		//System.out.println("HASHPREF: " + row1.hashPrefix());
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
			//implement
			String zeros = "";
			String zeros2 = "";
			int diff = globalDepth - row1.getBucket().getLocalDepth(); //make last diff bits zero
			for(int i=0;i<diff;i++)
				zeros += '0';
			for(int i=0;i<diff-1;i++)
				zeros2 += '0';
			//System.out.println(zeros + " - " + zeros2);
			String hashPref = row1.hashPrefix();
			String basepref = hashPref.substring(0,row1.getBucket().getLocalDepth()) + zeros;
			String index_basepref = hashPref.substring(0,row1.getBucket().getLocalDepth()+1) + zeros2;
			//System.out.println("HashPref: " + hashPref + " Local depth:" + row1.getBucket().getLocalDepth());
			//System.out.println("Base pref: " + basepref + " Index_base: " + index_basepref);
			row1.incrLocalDepth();
			CengBucket bucket2 = new CengBucket(row1.getBucket().getLocalDepth());
			CengHashRow row2 = new CengHashRow(row1.hashPrefix(), bucket2); //to change
			//row1.setHashPrefix(pref);
			int base = Integer.parseInt(basepref,2);
			int index_base = Integer.parseInt(index_basepref,2);
			//System.out.println("base: " + base + " index_base:" + index_base);
			row1 = rowAtIndex(base);
			row1.incrLocalDepth();
			redistribute(row1,row2,poke);
			//update related entries
			directories.set(index,row2);
			directories.set(base,row1);
			//hope it works
			CengBucket bucketn = new CengBucket(row1.getBucket());
			base++;
			while(base != index_base){
				CengHashRow rown = rowAtIndex(base);
				rown.incrLocalDepth();
				rown.setBucket(bucketn);
				directories.set(base,rown);
				base++;
			}
			while(index_base != index){
				CengHashRow rown = rowAtIndex(index_base);
				rown.incrLocalDepth();
				rown.setBucket(bucketn);
				directories.set(index_base,rown);
				index_base++;
			}
		}
		return true;
	}
	public void redistribute(CengHashRow row1, CengHashRow row2, CengPoke poke){
		ArrayList<CengHashRow> rows = new ArrayList<CengHashRow>(2);
		CengBucket bucket1 = row1.getBucket();
		ArrayList<CengPoke> bucketx = new ArrayList<CengPoke>(bucket1.getPokes());
		CengBucket bucket2 = row2.getBucket();
		//System.out.println("Redist: bucket1: " + bucket1.pokeAtIndex(0).pokeKey());
		int size = CengPokeKeeper.getBucketSize();
		for(int i=0;i<size;i++){//distribute pokes in bucket1
			//System.out.println("Bucket_x size: "+bucketx.size());
			String hashPrefix = hash(bucketx.get(i).pokeKey());
			//System.out.println("Checking: " + bucketx.get(i).pokeKey() + " HashPrefix: " + hashPrefix);
			if(hashPrefix.equals(row2.hashPrefix())){
				bucket2.addPoke(bucketx.get(i));// update other rows pointing to that bucket
				bucket1.getPokes().remove(bucketx.get(i)); //update other rows pointing to that bucket
			}
		}
		//place the new poke
		String hashPrefix = hash(poke.pokeKey());
		System.out.println("Hash Prefix of poke: " + hashPrefix);
		System.out.println("Hash Prefixx of row1: " + row1.hashPrefix() +" row2: " + row2.hashPrefix());
		if(hashPrefix.equals(row1.hashPrefix())){
			if(row1.getBucket().getPokes().size() < size){
				bucket1.addPoke(poke);//update other buckets pointing to that bucket
			}
		}
		else if(hashPrefix.equals(row2.hashPrefix())){
			if(row2.getBucket().getPokes().size() < size){
				bucket2.addPoke(poke);//update other buckets pointing to that bucket
			}
		}
		row1.setBucket(bucket1);
		row2.setBucket(bucket2);
		rows.add(row1);
		rows.add(row2);
		//return rows;
	}

	
	public void searchPoke(Integer pokeKey)
	{
		int hashValue = (int) pokeKey % CengPokeKeeper.getHashMod();
		CengHashRow row = rowAtIndex(hashValue);
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
		String zeros = "";
		int diff = globalDepth - row.getBucket().getLocalDepth(); //make last diff bits zero
		for(int i=0;i<diff;i++)
			zeros += '0';
		String hashPref = row.hashPrefix();
		String index_base = hashPref.substring(0,row.getBucket().getLocalDepth()) + zeros;
		int base = Integer.parseInt(index_base,2);
		for(int i=0;i<Math.pow(2,diff);i++){
			CengHashRow r = rowAtIndex(base+i);
			r.print();
		}
		System.out.println("}");
		// TODO: Empty Implementation
	}
	
	public void print()
	{
		System.out.println("\"table\": {");
		for (CengHashRow r: directories) {
			r.print();
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


}
