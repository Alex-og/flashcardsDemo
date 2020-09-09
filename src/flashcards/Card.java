package flashcards;



public class Card {
    String theCard;
    String definition;
    int count = 0;

    public Card(String theCard, String definition) {
        this.theCard = theCard;
        this.definition = definition;
    }

    public String getTheCard() {
        return theCard;
    }

    public String getDefinition() {
        return definition;
    }

    void incrementCount() {
        count++;
    }
    void resetCount() {
        this.count = 0;
    }
    int getCount() {
        return this.count;
    }
    void setCount(int i) {
        this.count = i;
    }
}
