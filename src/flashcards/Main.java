package flashcards;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;


public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        varargImport(args);
        CardService.inputAction(reader);
        reader.close();
        varargExport(args);
    }

    private static void varargExport(String... args) { //autosave all cards to a file before exiting the program
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-export")) {
                String fileName = args[i + 1];
                int countOfSavedCards = CardService.wrightToFile(fileName);
                System.out.printf("%s cards have been saved." + "\n", countOfSavedCards);
            }
        }
    }

    private static void varargImport(String... args) throws IOException { //autoload immediately after the start of the program
        int countOfSavedCards = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-import")) {
                String fileName = args[i + 1];

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(fileName));
                    while (reader.ready()) {
                        String[] oneCity = reader.readLine().split(":");
                        Card newCard = new Card(oneCity[0], oneCity[1]);
                        if (oneCity.length > 2) {
                            newCard.setCount(Integer.parseInt(oneCity[2]));
                        } else {
                            newCard.setCount(0);
                        }
                        CardService.flashCardlist.add(newCard);
                        countOfSavedCards++;
                    }
                } catch (NoSuchFileException e) {
                    System.out.println("File not found" + "\n");
                }
                System.out.printf("%s cards have been loaded." + "\n", countOfSavedCards);
            }
        }
    }
}