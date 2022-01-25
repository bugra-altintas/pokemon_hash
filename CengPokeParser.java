import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CengPokeParser {

	public static ArrayList<CengPoke> parsePokeFile(String filename)
	{
		ArrayList<CengPoke> pokeList = new ArrayList<CengPoke>();

		// You need to parse the input file in order to use GUI tables.
		// TODO: Parse the input file, and convert them into CengPokes
		BufferedReader br
				= null;
		try {
			br = new BufferedReader(new FileReader("pokemons.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = new String();
		// Condition holds true till
		while (true)
			// Print the string
		{
			try {
				if (!((line = br.readLine()) != null)) break;
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] fields = line.split("\t");
			CengPoke poke = new CengPoke(Integer.parseInt(fields[1]),fields[2],fields[3],fields[4]);
			pokeList.add(poke);
		}

		return pokeList;
	}
	
	public static void startParsingCommandLine() throws IOException
	{
		Scanner input = new Scanner(System.in);
		// TODO: Start listening and parsing command line -System.in-.
		while(true){
			String command = input.nextLine();
			String[] fields = command.split("\t");
			switch (fields[0]){
				case "add":
					//System.out.println("Adding");
					CengPoke poke = new CengPoke(Integer.parseInt(fields[1]),fields[2],fields[3],fields[4]);
					CengPokeKeeper.addPoke(poke);
					break;
				case "search":
					//System.out.println("Searching");
					CengPokeKeeper.searchPoke(Integer.parseInt(fields[1]));
					break;
				case "delete":
					//System.out.println("Deleting");
					CengPokeKeeper.deletePoke(Integer.parseInt(fields[1]));
					break;
				case "print":
					//System.out.println("Printing");
					CengPokeKeeper.printEverything();
					break;
				case "quit":
				default:
					//System.out.println("Quitting");
					return;
			}

		}
		// There are 5 commands:
		// 1) quit : End the app. Print nothing, call nothing.
		// 2) add : Parse and create the poke, and call CengPokeKeeper.addPoke(newlyCreatedPoke).
		// 3) search : Parse the pokeKey, and call CengPokeKeeper.searchPoke(parsedKey).
		// 4) delete: Parse the pokeKey, and call CengPokeKeeper.removePoke(parsedKey).
		// 5) print : Print the whole hash table with the corresponding buckets, call CengPokeKeeper.printEverything().

		// Commands (quit, add, search, print) are case-insensitive.
	}
}
