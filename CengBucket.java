import java.util.ArrayList;

public class CengBucket {

	private int localDepth;
	private ArrayList<CengPoke> pokes;
	private boolean visited;
	private boolean emptied;//change
	public CengBucket() {
		this.localDepth = 0;
		this.pokes = new ArrayList<CengPoke>(CengPokeKeeper.getBucketSize());
		this.visited = false;
		this.emptied = false;
	}
	public CengBucket(int localDepth) {
		this.localDepth = localDepth;
		this.pokes = new ArrayList<CengPoke>(CengPokeKeeper.getBucketSize());
		this.visited = false;
		this.emptied = false;
	}
	public CengBucket(CengBucket bucket){
		this.localDepth = bucket.getLocalDepth();
		this.pokes = new ArrayList<CengPoke>(bucket.getPokes());
		this.visited = bucket.isVisited();
		this.emptied = false;
	}
	public int getLocalDepth() {
		return localDepth;
	}

	public void setLocalDepth(int localDepth) {
		this.localDepth = localDepth;
	}
	public void incrLocalDepth(){ this.localDepth++; }
	public boolean getEmptied()  { return this.emptied; }
	public ArrayList<CengPoke> getPokes() {
		return pokes;
	}

	public void setPokes(ArrayList<CengPoke> pokes) {
		this.pokes = pokes;
	}

	public void addPoke(CengPoke poke){
		pokes.add(poke);
	}
	public void removePoke( Integer pokeKey) {
		int i=0;
		for(;i<pokes.size();i++){
			if(pokes.get(i).pokeKey() == pokeKey)
				break;
		}
		pokes.remove(i);
		if(pokes.size() == 0) emptied = true;
	}
	public int getSize(){ return pokes.size();}

	// GUI-Based Methods
	// These methods are required by GUI to work properly.

	public int pokeCount()
	{
		// TODO: Return the pokemon count in the bucket.
		return pokes.size();
	}
	
	public CengPoke pokeAtIndex(int index)
	{
		// TODO: Return the corresponding pokemon at the index.
		return pokes.get(index);
	}
	
	public int getHashPrefix()
	{
		// TODO: Return hash prefix length.
		return this.localDepth;
	}
	
	public Boolean isVisited()
	{
		// TODO: Return whether the bucket is found while searching.
		return this.visited;
	}
	
	// Own Methods
	public void print(){
		System.out.println("\t\t\"bucket\": {");
		System.out.println("\t\t\t\"hashLength\": " + localDepth + ",");
		System.out.println("\t\t\t\"pokes\": [");
		int size = pokes.size();
		for(int i=0; i<size;i++){
			pokes.get(i).print();
			if(!(i == size-1)){
				System.out.println(",");
				continue;
			}
			System.out.println();
		}
		System.out.println("\t\t\t]");
		System.out.println("\t\t}");
	}

}
