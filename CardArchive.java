import java.util.*;

/**
 * Card Archive: Game Elements
 *
 * Game element cards for brainstorming.
 * The program displays random game mechanics,
 * features, genres and other elements.
 *
 * @author Lauri Kosonen
 * @version 2018-12-03
 */
public class CardArchive {
    private static final int DECK_SIZE = 295;
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
                            System.out.println("< Card does not exist! >");
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
                return "Navigation";
            }
            case 5: {
                return "Thinking";
            }
            case 6: {
                return "Combat";
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
        initCard(0, "Multiplayer");
        initCard(0, "Local multiplayer");
        initCard(0, "Co-op/versus game");
        initCard(0, "Asymmetrical multiplayer");
        initCard(0, "Asynchronous multiplayer");
        initCard(0, "Turn-based gameplay");
        initCard(0, "Game master");
        initCard(0, "Player guilds");
        initCard(0, "Faction war");
        initCard(0, "Commanding players/units");
        initCard(0, "Recruiting characters/units");
        initCard(0, "Membership in different groups");
        initCard(0, "Temporary alliance against a common enemy");
        initCard(0, "Betraying the player's own team");
        initCard(0, "Being undercover as a member of a different team");
        initCard(0, "Dead players change sides");
        initCard(0, "Summonable allies");
        initCard(0, "Pets/familiars");
        initCard(0, "AI-controlled minions");
        initCard(0, "Blending in crowds");
        initCard(0, "Leaving messages for other players");
        initCard(0, "Seeing through another player's eyes");
        initCard(0, "Players start at different times");
        // World
        initCard(1, "Open world");
        initCard(1, "Exploration");
        initCard(1, "Day-night cycle");
        initCard(1, "Changing weather");
        initCard(1, "Large height differences");
        initCard(1, "Confined spaces");
        initCard(1, "Underground tunnels");
        initCard(1, "Environmental hazards");
        initCard(1, "Hazards rain from above");
        initCard(1, "Flood");
        initCard(1, "Burning heat");
        initCard(1, "Freezing cold");
        initCard(1, "Toxic gas");
        initCard(1, "Invisible walls or platforms");
        initCard(1, "Randomly generated area");
        initCard(1, "Secret areas");
        initCard(1, "Fog of war");
        initCard(1, "Checkpoints");
        initCard(1, "Hub area");
        initCard(1, "Optional collectibles");
        initCard(1, "Optional activities");
        initCard(1, "Random events");
        initCard(1, "The world lives even if the player is away");
        initCard(1, "Parallel worlds");
        // Influence
        initCard(2, "Controlling an area");
        initCard(2, "Reshaping the environment");
        initCard(2, "The environment reshapes itself");
        initCard(2, "Destroying objects/environment");
        initCard(2, "Level editor");
        initCard(2, "Base building");
        initCard(2, "Home decoration");
        initCard(2, "Factions and reputation");
        initCard(2, "Research and technology");
        initCard(2, "Keeping a population happy");
        initCard(2, "Participating in politics");
        initCard(2, "Running a business");
        initCard(2, "Creating traps/obstacles/barricades");
        initCard(2, "Creating climbable surfaces");
        initCard(2, "Creating roads for faster travel");
        initCard(2, "Adding things to walls");
        initCard(2, "Automating tasks");
        initCard(2, "Rerouting power to different systems");
        initCard(2, "Areas can be made safe");
        initCard(2, "Dead/frozen characters remain as parts of the environment");
        // Character
        initCard(3, "Character customization");
        initCard(3, "Leveling up");
        initCard(3, "Skill tree");
        initCard(3, "Character classes");
        initCard(3, "Multiple controllable characters");
        initCard(3, "Controlling a part of the world");
        initCard(3, "Looking after the character's needs");
        initCard(3, "The character's feelings");
        initCard(3, "Karma system");
        initCard(3, "Power-ups and downs");
        initCard(3, "Blessings and curses");
        initCard(3, "Fall damage");
        initCard(3, "Shield/invulnerability");
        initCard(3, "Abilities with cooldowns");
        initCard(3, "Buying levels/skills/power");
        initCard(3, "Cloning");
        initCard(3, "Disguises");
        initCard(3, "Changing form");
        initCard(3, "Spirit form");
        initCard(3, "Beast form");
        initCard(3, "Biological enhancements");
        initCard(3, "Mechanical/cybernetic enhancements");
        initCard(3, "Divine enhancements");
        initCard(3, "Unholy enhancements");
        initCard(3, "The character's size is important");
        initCard(3, "The character's appearance influences gameplay");
        initCard(3, "The character's disability influences gameplay");
        // Navigation
        initCard(4, "Platforming/climbing");
        initCard(4, "Fast travel/teleportation");
        initCard(4, "Speed");
        initCard(4, "Quick downhill movement");
        initCard(4, "Grinding on rails");
        initCard(4, "Swinging on ropes or such");
        initCard(4, "Pole vaulting");
        initCard(4, "Wall running");
        initCard(4, "On-rails action");
        initCard(4, "Backtracking");
        initCard(4, "Vehicles");
        initCard(4, "Summonable vehicles");
        initCard(4, "Vehicles for hire");
        initCard(4, "Operating a vehicle as a team");
        initCard(4, "Boarding vehicles as a stowaway");
        initCard(4, "Hitchhiking");
        initCard(4, "Swimming");
        initCard(4, "Diving");
        initCard(4, "Gliding");
        initCard(4, "Flying");
        initCard(4, "Sea travel");
        initCard(4, "Space travel");
        initCard(4, "Train/handcar");
        // Thinking
        initCard(5, "Puzzles");
        initCard(5, "Using certain tools/skills on certain objects");
        initCard(5, "Scouting an area before entering");
        initCard(5, "Repeating past events/levels with new knowledge");
        initCard(5, "Physics-based gameplay");
        initCard(5, "Unusual gravity");
        initCard(5, "Invisibility");
        initCard(5, "Mind control");
        initCard(5, "Manipulating time");
        initCard(5, "Witnessing past/future events");
        initCard(5, "Wielding magic");
        initCard(5, "The four classical elements");
        initCard(5, "Redirecting beams of light");
        initCard(5, "Special sight ability");
        initCard(5, "Seeing characters/objects through walls");
        initCard(5, "Super hearing");
        initCard(5, "Conversation options");
        initCard(5, "Moral choices");
        initCard(5, "Database");
        initCard(5, "Item descriptions");
        initCard(5, "Taking notes is recommended");
        initCard(5, "Detective work");
        initCard(5, "Deciphering clues");
        initCard(5, "Passcodes and encrypted messages");
        initCard(5, "Programming/hacking");
        initCard(5, "Pattern recognition");
        initCard(5, "Truth and lies");
        initCard(5, "Pulling and pushing");
        initCard(5, "Doors and keys");
        initCard(5, "Switches");
        initCard(5, "Quiz");
        // Combat
        initCard(6, "Combat");
        initCard(6, "Aerial combat");
        initCard(6, "Naval combat");
        initCard(6, "Mech combat");
        initCard(6, "Regenerating health");
        initCard(6, "Permanent death");
        initCard(6, "Stun attacks");
        initCard(6, "Damage-over-time attacks");
        initCard(6, "Explosive attacks");
        initCard(6, "Combo attacks");
        initCard(6, "Melee weapons");
        initCard(6, "Ranged weapons and ammunition");
        initCard(6, "Target-locking weapons");
        initCard(6, "Mines");
        initCard(6, "Sniping from a long distance");
        initCard(6, "Marking targets");
        initCard(6, "Dodging attacks");
        initCard(6, "Deflecting attacks");
        initCard(6, "Friendly fire");
        initCard(6, "Weaknesses and resistances to attacks");
        initCard(6, "Weapons can be found in the world");
        initCard(6, "Equipped weapon changes after getting a kill");
        initCard(6, "Using objects in the world as ammunition");
        initCard(6, "Taking enemies' equipment");
        initCard(6, "Pushing enemies off ledges");
        initCard(6, "Healing teammates");
        initCard(6, "Reviving fallen teammates");
        initCard(6, "Respawning next to a teammate");
        initCard(6, "Second wind revival");
        initCard(6, "No respawning until the round/wave ends");
        initCard(6, "Boss enemy");
        initCard(6, "Stealthy enemies");
        initCard(6, "Exploding enemies");
        initCard(6, "Regenerating enemies");
        initCard(6, "Enemies which spawn more enemies");
        initCard(6, "Enemies which give power-ups to others");
        initCard(6, "Weak, fleeing enemies with good loot");
        initCard(6, "Enemy swarms");
        initCard(6, "Enemy waves");
        initCard(6, "Enemies spawn constantly");
        initCard(6, "Enemies alert others of the player");
        initCard(6, "Enemies can't leave their territories");
        initCard(6, "Enemy grows stronger when it defeats the player");
        // Items
        initCard(7, "Inventory");
        initCard(7, "Looting");
        initCard(7, "Crafting");
        initCard(7, "Buying and selling");
        initCard(7, "Trading");
        initCard(7, "Gifts");
        initCard(7, "Brewing potions");
        initCard(7, "Poisons and antidotes");
        initCard(7, "Binoculars/spyglass");
        initCard(7, "Photography camera");
        initCard(7, "Grappling hook");
        initCard(7, "Repair tool");
        initCard(7, "Excavation tool");
        initCard(7, "Landscaping/farming tools");
        initCard(7, "Musical instruments");
        initCard(7, "Money is handled like normal stackable items");
        initCard(7, "Factions use different currencies");
        initCard(7, "World map as an inventory item");
        initCard(7, "Lore items");
        initCard(7, "Decoy items");
        initCard(7, "Breadcrumb trail items");
        initCard(7, "Leaving items behind as bait");
        initCard(7, "Storing items within the world");
        initCard(7, "Fillable containers");
        initCard(7, "Item rarity levels");
        initCard(7, "Limited item durability");
        initCard(7, "Breaking items down to their components");
        initCard(7, "Dead characters drop their equipment");
        initCard(7, "Items have random stats");
        initCard(7, "Items can be enhanced by adding trinkets");
        initCard(7, "Items can be enhanced by using them");
        initCard(7, "Items can be enhanced by crafting/magic");
        initCard(7, "Renewable resources/items in the world");
        initCard(7, "Scarcity of resources");
        initCard(7, "Equipment transmogrification");
        // Goal
        initCard(8, "Competition");
        initCard(8, "Leaderboard");
        initCard(8, "Tournament with multiple matches");
        initCard(8, "Score");
        initCard(8, "Quests");
        initCard(8, "Racing");
        initCard(8, "Stealth");
        initCard(8, "Rhythm action");
        initCard(8, "Gambling");
        initCard(8, "Doing stunts");
        initCard(8, "Tycoon game");
        initCard(8, "God game");
        initCard(8, "Goals are decided by the player");
        initCard(8, "The objective is not explicit");
        initCard(8, "The objective's position is random");
        initCard(8, "Failure changes the objective");
        initCard(8, "Points can be stolen from the opponent");
        initCard(8, "A guiding arrow/line");
        initCard(8, "Outlasting the opponent");
        initCard(8, "Surviving as long as possible");
        initCard(8, "Evading obstacles");
        initCard(8, "Escaping from something/somewhere");
        initCard(8, "Protecting a target");
        initCard(8, "Rescuing characters");
        initCard(8, "Defending an area");
        initCard(8, "Finding items");
        initCard(8, "Taking an item to the goal");
        initCard(8, "Catching thrown/flying objects");
        initCard(8, "Hiding something from the opponent");
        initCard(8, "Revisiting levels with new tools/powers");
        initCard(8, "Different game modes");
        initCard(8, "King of the Hill");
        initCard(8, "Capture the Flag");
        initCard(8, "Team Deathmatch");
        initCard(8, "Free-for-All");
        initCard(8, "Last Man Standing");
        initCard(8, "Delivering a Payload");
        initCard(8, "Sandbox mode with unlimited resources");
        // Miscellaneous
        initCard(9, "2D");
        initCard(9, "3D");
        initCard(9, "First-person view");
        initCard(9, "Third-person view");
        initCard(9, "Side-scrolling view");
        initCard(9, "Top-down view");
        initCard(9, "Isometric view");
        initCard(9, "Virtual reality");
        initCard(9, "Augmented reality");
        initCard(9, "Motion controls");
        initCard(9, "Board game");
        initCard(9, "Cards");
        initCard(9, "Chance and probability");
        initCard(9, "Lighting and shadows");
        initCard(9, "Farming");
        initCard(9, "Hunting");
        initCard(9, "Cooking");
        initCard(9, "The game camera cannot be freely moved");
        initCard(9, "Minimal or no HUD");
        initCard(9, "Voice acting");
        initCard(9, "In-game music player for the soundtrack");
        initCard(9, "Sounds/voices are important");
        initCard(9, "Colors are important");
        initCard(9, "Fixing objects in place");
        initCard(9, "Objects bounce off surfaces");
        initCard(9, "Grabbing onto characters");
        initCard(9, "A character/an object leaves a trail after it");
        initCard(9, "Naming a character/an object");
        initCard(9, "Saving the game anytime");
        initCard(9, "New game +");
        initCard(9, "Random gameplay modifiers");

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
