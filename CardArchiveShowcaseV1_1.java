import java.util.*;

/**
 * Card Archive: Game Elements
 
 * Game element cards for brainstorming.
 * The program displays random game mechanics,
 * features, genres and other elements.
 *
 * @author Lauri Kosonen
 * @version 2018-11-19
 */
public class CardArchiveShowcaseV1_1 {
    private static final int DECK_SIZE = 46;
    private static final boolean SHUFFLE_DECK_FOR_EACH_HAND = false;
    private static final boolean PRINT_CATEGORIES = true;
    private static Card[] deck; 
    private static Hand[] hands; 
    private static int handAmount = 0;
    private static int cardsInHand = 0;
    private static int currInitCardIndex = 0;
    private static int drawnCardAmount = 0;
    private static boolean showAll = false;

    // Ä, ä, Ö and ö characters, respectively,
    // by their ASCII numbers
    private static char a1 = (char) 196; // 'Ä'
    private static char a2 = (char) 228; // 'ä'
    private static char o1 = (char) 214; // 'Ö'
    private static char o2 = (char) 246; // 'ö'

   /**
    * Runs the program.
    */
    public static void main(String[] args)
    {
        // Prints the title of the program
        String title = "CARD ARCHIVE: GAME ELEMENTS";
        System.out.println();
        System.out.println(title);
        System.out.println();
        
        // Initializes the number of hands generated
        // and how many cards is in a hand
        initHandAmountAndSize(args);
        
        // Continues only if the number of 
        // hands and hand size are positive
        if (handAmount > 0 && cardsInHand > 0) {
            
            // Creates and shuffles the deck
            initDeck();

            boolean emptyDeck = false;
            boolean incompleteHand = false;
            
            // Creates and checks the hands
            hands = new Hand[handAmount];
            for (int i = 0; i < handAmount; i++) {
                if (!emptyDeck) {
                    
                    // Initializes the current hand
                    hands[i] = new Hand(cardsInHand);

                    // Adds cards to the hand
                    for (int j = 0; j < cardsInHand; j++) {
                        drawCard(i, j);
                        
                        System.out.print(formatCardIndex(j, cardsInHand));
                        
                        // Prints the current card
                        if (hands[i].getCard(j) != null) {
                            
                            // Prints the card's category
                            if (PRINT_CATEGORIES) {
                                System.out.print("[" + categoryName(
                                    hands[i].getCard(j).getCategory()) + "] ");
                            }
                            
                            // Prints the card's name
                            System.out.println(hands[i].getCard(j).getName());
                        }
                        else {
                            System.out.println("--- Card does not exist! ---");
                        }

                        // Prevents further card adding and
                        // hand checking if the deck is empty
                        if (drawnCardAmount == DECK_SIZE) {
                            emptyDeck = true;
                            incompleteHand = (j < cardsInHand - 1);
                            
                            if (!showAll && (incompleteHand ||
                                  !SHUFFLE_DECK_FOR_EACH_HAND)) {
                                System.out.println("No more cards!");
                            }
                            
                            break;
                        }
                    }
                    
                    System.out.println("------");
                    
                    if (SHUFFLE_DECK_FOR_EACH_HAND) {
                        shuffleDeck();
                        emptyDeck = false;
                        incompleteHand = false;
                    }
                }
            }
        }
    }
    
   /**
    * Initializes the number of hands generated
    * and how many cards is in one hand.
    * Takes the command line arguments and parses
    * them into integers.
    * Giving no arguments defaults in the hand number of 1, hand size of 5.
    *
    * @param handAmountArgs the arguments given in command line
    */
    private static void initHandAmountAndSize(String[] handAmountArgs) {
        
        // Attempts to parse the input into two integers and
        // prints an error message if the input is invalid
        if (handAmountArgs.length > 0) {
            showAll = handAmountArgs[0].toLowerCase().equals("all");
            if (showAll) {
                handAmount = 1;
                cardsInHand = DECK_SIZE;
            }
            else {
                try {
                    // If there's only one command line argument,
                    // the argument is for the number of cards in hand
                    if (handAmountArgs.length == 1) {
                        handAmount = 1;
                        cardsInHand = Integer.parseInt(handAmountArgs[0]);
                    }
                    // If there's two or more, the first command
                    // line argument is for the number of hands and
                    // the second is for the number of cards in hand
                    else {
                        handAmount = Integer.parseInt(handAmountArgs[0]);
                        cardsInHand = Integer.parseInt(handAmountArgs[1]);
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println("The given input is not valid.");
                    handAmount = 0;
                    cardsInHand = 0;
                }
                
                if (handAmount < 1 || cardsInHand < 1) {
                    System.out.println("Please input two positive integers.");
                    handAmount = 0;
                    cardsInHand = 0;
                }
                else if (cardsInHand > DECK_SIZE ||
                    (!SHUFFLE_DECK_FOR_EACH_HAND && handAmount > DECK_SIZE)) {
                    System.out.format("Invalid input - deck has %d cards.", DECK_SIZE);
                    System.out.println();
                    handAmount = 0;
                    cardsInHand = 0;
                }
            }
        }
        // Default number of hands and cards in hand
        else {
            handAmount = 1;
            cardsInHand = 5;
        }
    }
    
    /**
    * Returns a category's name based on the given number.
    *
    * @param categoryNumber a category's number
    * @return the name of a category or an error string
    */
    public static String categoryName(int categoryNumber) {
        switch (categoryNumber) {
            case 0: {
                return "Group";
            }
            case 1: {
                return "World";
            }
            case 2: {
                return "Influence";
            }
            case 3: {
                return "Character";
            }
            case 4: {
                return "Thinking";
            }
            case 5: {
                return "Combat";
            }
            case 6: {
                return "Competition";
            }
            case 7: {
                return "Items";
            }
            case 8: {
                return "Goal";
            }
            case 9: {
                return "Miscellaneous";
            }
            
            // Prints an error message if the
            // category's number is out of limits
            default: {
                return "ERROR";
            }
        }
    }
    
   /**
    * Creates and shuffles the deck.
    */
    private static void initDeck() {
        
        // Creates the deck
        deck = new Card[DECK_SIZE];
        currInitCardIndex = 0;

        // Group
        initCard(0, "Summonable allies");
        initCard(0, "AI-controlled minions");
        // World
        initCard(1, "Underground tunnels");
        initCard(1, "Hazards rain from above");
        initCard(1, "Invisible walls or platforms");
        initCard(1, "Checkpoints");
        // Influence
        initCard(2, "The environment reshapes itself");
        initCard(2, "Automating tasks");
        // Character
        initCard(3, "Character classes");
        initCard(3, "Controlling a part of the world");
        initCard(3, "Abilities with cooldowns");
        initCard(3, "The character's disability influences gameplay");
        // Thinking
        initCard(4, "Wielding magic");
        initCard(4, "Seeing characters/objects through walls");
        // Combat
        initCard(5, "Regenerating health");
        initCard(5, "Stun attacks");
        initCard(5, "Damage-over-time attacks");
        initCard(5, "Explosive attacks");
        initCard(5, "Exploding enemies");
        initCard(5, "Enemies which spawn more enemies");
        initCard(5, "Enemies alert other enemies of the player");
        initCard(5, "Enemies spawn constantly");
        initCard(5, "Enemies can't leave their territories");
        // Competition
        initCard(6, "Tournament with multiple matches");
        initCard(6, "Summonable vehicles");
        initCard(6, "Vehicles for hire");
        initCard(6, "Hitchhiking");
        initCard(6, "Swimming");
        initCard(6, "Diving");
        initCard(6, "Space travel");
        initCard(6, "Sea travel");
        initCard(6, "Doing stunts");
        initCard(6, "Grinding on rails");
        initCard(6, "No respawning until the round/wave ends");
        initCard(6, "Random gameplay modifiers");
        // Items
        initCard(7, "Brewing potions");
        initCard(7, "Items have random stats");
        // Goal
        initCard(8, "Failure changes the objective");
        initCard(8, "Escaping from something/somewhere");
        initCard(8, "Defending an area");
        initCard(8, "Catching thrown/flying objects");
        initCard(8, "Last Man Standing");
        initCard(8, "The objective is not explicit");
        // Miscellaneous
        initCard(9, "Augmented reality");
        initCard(9, "Colors are important");
        initCard(9, "Objects bounce off surfaces");
        
        // Shuffles the deck
        shuffleDeck();
    }
    
    private static void initCard(int category, String name) {
        if (currInitCardIndex < deck.length) {
            deck[currInitCardIndex] = new Card(category, name);
            currInitCardIndex++;
        }
    }
    
   /**
    * Shuffles the deck.
    * Does nothing if every card is shown.
    */
    private static void shuffleDeck() {
        if (!showAll) {
            double rand;
            Card temp;
            for (int i = 0; i < deck.length; i++) {
                rand = Math.random();
                int randCardIndex = (int) (rand * deck.length);
                temp = deck[randCardIndex];
                deck[randCardIndex] = deck[i];
                deck[i] = temp;
            }
            
            drawnCardAmount = 0;
        }
    }
    
   /**
    * Takes a card from the deck and adds it to a hand.
    *
    * @param handIndex  a hand's index
    * @param cardIndex  a card's index
    */
    private static void drawCard(int handIndex, int cardIndex) {
        if (handIndex < handAmount && cardIndex < cardsInHand) {
            hands[handIndex].addCard(cardIndex, deck[drawnCardAmount]);
            drawnCardAmount++;
        }
    }
    
   /**
    * Sorts the given hand by the cards' categories.
    *
    * @param hand   a hand
    */
    private static void sortHandByCategory(Hand hand) {
        for (int i = 0; i < cardsInHand; i++) {
            int smallest = i;
            for (int j = i + 1; j < cardsInHand; j++) {
                if (hand.getCard(j).getCategory() <
                      hand.getCard(smallest).getCategory()) {
                    smallest = j;
                }
            }
            
            Card temp = hand.getCard(i);
            hand.setCard(i, hand.getCard(smallest));
            hand.setCard(smallest, temp);
        }
    }
    
   /**
    * Returns the given card's category and name as a string.
    *
    * @param card   a card
    * @return the card's category's and its own name
    */
    public static String cardCatAndName(Card card) {
        return categoryName(card.getCategory()) +
               " - " + card.getName();
    }
    
    private static String formatCardIndex(int cardIndex, int maxIndex) {
        String formattedCardIndex = "";
        
        // Presented list numbering starts from 1
        cardIndex++;
                
        int figuresInCardIndex = 1;
        for (int i = cardIndex; i / 10 >= 1; i = i / 10) {
            figuresInCardIndex++;
        }
        
        int figuresInMaxIndex = 1;
        for (int j = maxIndex; j / 10 >= 1; j = j / 10) {
            figuresInMaxIndex++;
        }

        for (int k = figuresInCardIndex; k < figuresInMaxIndex; k++) {
            formattedCardIndex += " ";
        }
        
        formattedCardIndex += cardIndex + " - ";
        
        return formattedCardIndex;
    }
}

/**
 * An object which holds card information:
 * category (integer) and name (string).
 */
class Card {
    private int category;
    private String name;

   /**
    * Gets the card's category.
    *
    * @return the card's category
    */
    public int getCategory() {
        return category;
    }
    
   /**
    * Gets the card's name.
    *
    * @return the card's name
    */
    public String getName() {
        return name;
    }
    
   /**
    * Class constructor.
    *
    * @param category   a category
    * @param name       a name
    */
    public Card(int category, String name) {
        this.category = category;
        this.name = name;
    }
}

/**
 * An object which stores drawn cards.
 */
class Hand {
    private Card[] cards;
    
   /**
    * Gets the amount of cards in the hand.
    *
    * @return the length of the cards array
    */
    public int getCardAmount() {
        return cards.length;
    }
    
   /**
    * Gets a card.
    *
    * @param cardIndex  the index of the card
    * @return the card in the given index
    */
    public Card getCard(int cardIndex) {
        return cards[cardIndex];
    }
    
   /**
    * Sets a card.
    *
    * @param cardIndex  the index of the card
    * @param card       the card to be set
    */
    public void setCard(int cardIndex, Card card) {
        cards[cardIndex] = card;
    }
    
   /**
    * Class constructor.
    *
    * @param cardAmount the number of cards in the hand
    */
    public Hand(int cardAmount) {
        cards = new Card[cardAmount];
    }
    
   /**
    * Adds a card to the hand.
    *
    * @param cardIndex  the index of the card
    * @param card       the card to be added
    */
    public void addCard(int cardIndex, Card card) {
        if (cardIndex >= 0 && cardIndex < cards.length) {
            cards[cardIndex] = card;
        }
    }
}
