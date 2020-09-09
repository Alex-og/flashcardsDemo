package flashcards;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

public class CardService {
    static LinkedList<Card> flashCardlist = new LinkedList<>();
    static LinkedList<String> logAllLines = new LinkedList<>();
    static List<Card> errorList = new ArrayList<>();

    static final String THE_ACTION = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):";
    static final String INPUT_CARD = "The card:";
    static final String INPUT_DEFINITION = "The definition of the card:";
    static final String HOW_MANY_ASK = "How many times to ask?";
    static String PRINT_DEFINITION = "Print the definition of \"%s\":";
    static String CARD_EXIST = "The card \"%s\" already exists.";
    static String DEFINITION_EXIST = "The definition \"%s\" already exists.";
    static String WRONG_DEFINITION_ANSWER = "Wrong. The right answer is \"%s\".";
    static String WRONG_DEFINITION_ANSWER_BUT = "Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".";
    static String PAIR_ADDED = "The pair (%s) has been added.";
    static String CARD_REMOVED = "The card has been removed.";
    static String NO_SUCH_CARD = "Can't remove \"%s\": there is no such card.";
    static String CORRECT = "Correct!";
    static String FILE_NAME = "File name:";
    static String FILE_NOT_FOUND = "File not found.";
    static String CARDS_LOADED = "%d cards have been loaded.";
    static String CARDS_SAVED = "%d cards have been saved.";
    static String NO_CARDS_WITH_ERRORS = "There are no cards with errors.";
    static String HARDEST_CARD1 = "The hardest card is ";
    static String HARDEST_CARD2 = ". You have %d errors answering it.";
    static String RESET_STATS = "Card statistics has been reset.";
    static String LOG_SAVED = "The log has been saved.";
    static String BYE = "Bye bye!";

    static void inputAction(BufferedReader reader) throws IOException {
        while (true) {
            System.out.println(THE_ACTION);
            String input = reader.readLine();

            if (input.equals("exit")) {
                System.out.println(BYE);
                logAllLines.add("exit");
                logAllLines.add(BYE);
                break;
            }
            switch (input) {
                case "add":
                    CardService.add(reader);
                    break;
                case "remove":
                    CardService.remove(reader);
                    break;
                case "import":
                    CardService.importCards(reader);
                    break;
                case "export":
                    CardService.exportCards(reader);
                    break;
                case "ask":
                    CardService.ask(reader);
                    break;
                case "log":
                    CardService.log(reader);
                    break;
                case "hardest card":
                    CardService.hardestCard();
                    break;
                case "reset stats":
                    CardService.resetStats();
                    break;
                default: break;
            }
        }
    }

    static void add(BufferedReader reader) throws IOException {
        LinkedList<String> innerOperationsOfMethod = new LinkedList<>();
        System.out.println(INPUT_CARD);
        innerOperationsOfMethod.add(INPUT_CARD + "\n");
        String cardName = reader.readLine();
        innerOperationsOfMethod.add(cardName);
        for (Card c : flashCardlist) {
            if (cardName.equals(c.getTheCard())) {
                System.out.printf(CARD_EXIST + "\n\n", cardName);
                c.incrementCount();
                innerOperationsOfMethod.add(String.format(CARD_EXIST + "\n\n", cardName));
                return;
            }
        }
        System.out.println(INPUT_DEFINITION);
        innerOperationsOfMethod.add(INPUT_DEFINITION + "\n");
        String cardDefinition = reader.readLine();
        innerOperationsOfMethod.add(cardDefinition + "\n");
        for (Card c : flashCardlist) {
            if (cardDefinition.equals(c.getDefinition())) {
                System.out.printf(DEFINITION_EXIST + "\n\n", cardDefinition);
                c.incrementCount();
                innerOperationsOfMethod.add(String.format(DEFINITION_EXIST + "\n\n", cardDefinition));
                return;
            }
        }
        flashCardlist.add(new Card(cardName, cardDefinition));

        System.out.printf(PAIR_ADDED + "\n\n", "\"" + cardName + ":" + cardDefinition + "\"");
        innerOperationsOfMethod.add(String.format(PAIR_ADDED + "\n\n", "\"" + cardName + ":" + cardDefinition + "\""));
        writeOperationsOfMethodToLogList(innerOperationsOfMethod);
    }

    static void remove(BufferedReader reader) throws IOException {
        LinkedList<String> innerOperationsOfMethod = new LinkedList<>();
        System.out.println(INPUT_CARD);
        innerOperationsOfMethod.add(INPUT_CARD + "\n");
        String cardName = reader.readLine();
        innerOperationsOfMethod.add(cardName + "\n");
        if (flashCardlist.size() == 0 || !checkIfTermContainsInFlashcardList(cardName)) {
            System.out.printf(NO_SUCH_CARD + "\n\n", cardName);
            innerOperationsOfMethod.add(String.format(NO_SUCH_CARD + "\n\n", cardName));
        } else {
            errorList.removeIf(l -> l.getTheCard().equals(cardName));
            flashCardlist.stream().filter(l -> l.getTheCard().equals(cardName)).forEach(c -> c.setCount(0));
            flashCardlist.removeIf(l -> l.getTheCard().equals(cardName));
            //flashCardlist.stream().filter(l -> l.getTheCard().equals(cardName)).forEach(Card::incrementCount);

            System.out.printf(CARD_REMOVED + "\n\n", cardName);
            innerOperationsOfMethod.add(String.format(CARD_REMOVED + "\n\n", cardName));
        }
        writeOperationsOfMethodToLogList(innerOperationsOfMethod);
    }

    static void importCards(BufferedReader reader) throws IOException {
        LinkedList<String> innerOperationsOfMethod = new LinkedList<>();
        System.out.println(FILE_NAME);
        innerOperationsOfMethod.add(FILE_NAME + "\n");
        String fileName = reader.readLine();
        innerOperationsOfMethod.add(fileName + "\n");
        LinkedList<Card> flashCardlistDemo = new LinkedList<>();

        try {
            String allTextFromFile = readFileAsString(fileName);
            reader = new BufferedReader(new FileReader(fileName));
            if (reader.ready()) {
                Arrays.stream(allTextFromFile.split("\n")).forEach(s -> {
                    String[] oneCity = s.split(":");
                    Card newCard = new Card(oneCity[0], oneCity[1]);
                    if (oneCity.length > 2) {
                        newCard.setCount(Integer.parseInt(oneCity[2]));
                    } else {
                        newCard.setCount(0);
                    }
                    flashCardlistDemo.add(newCard);
                });
            }
            compareLists(flashCardlist, flashCardlistDemo);
            System.out.printf(CARDS_LOADED + "\n\n", flashCardlistDemo.size());
            innerOperationsOfMethod.add(String.format(CARDS_LOADED + "\n\n"
                    , flashCardlistDemo.size()));

        } catch (NoSuchFileException e) {
            System.out.println(FILE_NOT_FOUND + "\n");
            innerOperationsOfMethod.add(FILE_NOT_FOUND + "\n");
        }

        writeOperationsOfMethodToLogList(innerOperationsOfMethod);
    }

    private static void compareLists(LinkedList<Card> oldList, LinkedList<Card> loadedList) { //update the definition for same card
        LinkedList<Card> cloneLoadedList = new LinkedList<>(loadedList);
        if (oldList.size() == 0 && cloneLoadedList.size() > 0) {
            oldList.addAll(cloneLoadedList);
        } else {
            for (int i = 0; i < cloneLoadedList.size(); i++) {
                for (int j = 0; j < oldList.size(); j++) {

                    if (cloneLoadedList.get(i).getTheCard().equals(oldList.get(j).getTheCard())) {

                        oldList.set(j, cloneLoadedList.get(i));
                        cloneLoadedList.remove(loadedList.get(i));
                        break;
                    }
                }
            }
            if (cloneLoadedList.size() > 0) {
                oldList.addAll(cloneLoadedList);
            }
        }
    }

    public static void exportCards(BufferedReader reader) throws IOException {
        LinkedList<String> innerOperationsOfMethod = new LinkedList<>();
        System.out.println(FILE_NAME);
        innerOperationsOfMethod.add(FILE_NAME + "\n");
        String fileName = reader.readLine();
        innerOperationsOfMethod.add(fileName + "\n");
        wrightToFile(fileName);

        System.out.printf(CARDS_SAVED + "\n\n", flashCardlist.size());
        innerOperationsOfMethod.add(String.format(CARDS_SAVED + "\n\n", flashCardlist.size()));
        writeOperationsOfMethodToLogList(innerOperationsOfMethod);
    }

    private static void ask(BufferedReader reader) throws IOException {
        LinkedList<String> innerOperationsOfMethod = new LinkedList<>();
        System.out.println(HOW_MANY_ASK);
        innerOperationsOfMethod.add(HOW_MANY_ASK + "\n");
        int howManyAsk = 0;
        try {
            howManyAsk = Integer.parseInt(reader.readLine());
            innerOperationsOfMethod.add(howManyAsk + "\n");
        } catch (Exception e) {
            System.out.println("Invalid value. Please, enter a digit value");
        }


        for (int i = 0; i < howManyAsk; i++) {
            int randomI;
            if (i >= flashCardlist.size()) {
                randomI = new Random().nextInt(flashCardlist.size());
                handleAsk(reader, randomI);
            } else {
                handleAsk(reader, i);
            }

        }
        writeOperationsOfMethodToLogList(innerOperationsOfMethod);
    }

    static void log (BufferedReader reader) throws IOException {
        LinkedList<String> innerOperationsOfMethod = new LinkedList<>();
        innerOperationsOfMethod.add("log" + "\n");
        System.out.println(FILE_NAME);
        innerOperationsOfMethod.add(FILE_NAME + "\n");
        String fileName = reader.readLine();
        logToFile(fileName);
        writeOperationsOfMethodToLogList(innerOperationsOfMethod);
    }

    static void hardestCard() {
        LinkedList<String> innerOperationsOfMethod = new LinkedList<>();
        innerOperationsOfMethod.add("hardest card" + "\n");
        int maxCount = 0;
        for (Card cd : flashCardlist) {  // found the count of max errors
            if (cd.getCount() > maxCount) {
                maxCount = cd.getCount();
            }
        }

        for (Card cd : flashCardlist) {  //Searching for hardest cards with maximum errors
            if (cd.getCount() == maxCount && maxCount > 0 && errorList.size() == 0) {
                errorList.add(cd);
            } else if (cd.getCount() == maxCount && maxCount > 0 && errorList.size() > 0) {
                for (int i = 0; i < errorList.size(); i++) {
                    if (errorList.get(i).getTheCard().equalsIgnoreCase(cd.getTheCard())) {
                        errorList.set(i, cd);
                    } else if (errorList.contains(cd)) {
                        break;
                    } else {
                        errorList.add(cd);
                    }
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        if (errorList.size() != 0) {
            builder.append(HARDEST_CARD1);
            for (int i = 0; i < errorList.size(); i++) {
                if (i < errorList.size() - 1) {
                    builder.append("\"").append(errorList.get(i).getTheCard()).append("\", ");
                } else {
                    builder.append("\"").append(errorList.get(i).getTheCard()).append("\"");
                }
            }
        }
        String hardestCards = builder.toString();
        if (errorList.size() != 0) {
            System.out.print(hardestCards);
            System.out.printf(HARDEST_CARD2 + "\n\n", maxCount);
            innerOperationsOfMethod.add(String.format(hardestCards, builder) + "\n");
        }

        if (maxCount == 0) {
            System.out.print(NO_CARDS_WITH_ERRORS + "\n\n");
            innerOperationsOfMethod.add(NO_CARDS_WITH_ERRORS + "\n\n");
        }
        writeOperationsOfMethodToLogList(innerOperationsOfMethod);
    }

    static void resetStats() {
        logAllLines.add("reset stats");
        flashCardlist.forEach(Card::resetCount);
        errorList.clear();
        System.out.println(RESET_STATS + "\n");
        logAllLines.add(RESET_STATS + "\n");
    }

    private static void handleAsk(BufferedReader reader, int i) throws IOException {
        LinkedList<String> innerOperationsOfMethod = new LinkedList<>();
        System.out.printf(PRINT_DEFINITION + "\n", flashCardlist.get(i).getTheCard());
        innerOperationsOfMethod.add(String.format(PRINT_DEFINITION + "\n", flashCardlist.get(i).getTheCard()));
        String answerDefinition = reader.readLine();
        innerOperationsOfMethod.add(answerDefinition + "\n");

        if (flashCardlist.get(i).getDefinition().equals(answerDefinition)) {
            System.out.println(CORRECT + "\n");
            innerOperationsOfMethod.add(CORRECT + "\n");
        } else if (checkIfDefinitionContainsInFlashcardList(answerDefinition)) {
            System.out.printf(WRONG_DEFINITION_ANSWER_BUT + "\n"
                    , flashCardlist.get(i).getDefinition()
                    , rightDefinitionForCard(answerDefinition));
            flashCardlist.get(i).incrementCount();
            innerOperationsOfMethod.add(String.format(WRONG_DEFINITION_ANSWER_BUT + "\n"
                    , flashCardlist.get(i).getDefinition()
                    , rightDefinitionForCard(answerDefinition)));
        } else {
            System.out.printf(WRONG_DEFINITION_ANSWER + "\n", flashCardlist.get(i).getDefinition());
            flashCardlist.get(i).incrementCount();
            innerOperationsOfMethod.add(String.format(WRONG_DEFINITION_ANSWER + "\n"
                    , flashCardlist.get(i).getDefinition()));
        }
        writeOperationsOfMethodToLogList(innerOperationsOfMethod);
    }

    static String rightDefinitionForCard( String s) {
        String check = "";
        for (Card c : flashCardlist) {
            if (s.equals(c.getDefinition())) {
                check += c.getTheCard();
                break;
            }
        }
        return check;
    }


    static boolean checkIfTermContainsInFlashcardList(String term) {
        boolean check = false;
        for (Card c : flashCardlist) {
            if (term.equals(c.getTheCard())) {
                check = true;
                break;
            }
        }
        return check;
    }

    static boolean checkIfDefinitionContainsInFlashcardList(String term) {
        boolean check = false;
        for (Card c : flashCardlist) {
            if (term.equals(c.getDefinition())) {
                check = true;
                break;
            }
        }
        return check;
    }

    static int wrightToFile(String fileName) {
        int countSavedCards = 0;
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Card c : flashCardlist) {
                writer.write(c.getTheCard() + ":" + c.getDefinition() + ":" + c.getCount() + "\n");
                countSavedCards++;
            }
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
        return countSavedCards;
    }

    private static void logToFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String c : logAllLines) {
                writer.write(c);
            }
            System.out.println(LOG_SAVED + "\n");
            logAllLines.add(LOG_SAVED + "\n");
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    private static void writeOperationsOfMethodToLogList(LinkedList<String> list) {
        logAllLines.addAll(list);
    }
}