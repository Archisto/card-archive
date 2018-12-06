import java.util.*;

/**
 * Card Archive: Game Elements
 *
 * A tool for coming up with game ideas.
 * Displays random game mechanics, play styles, genres
 * and other features you can find in games of all types.
 *
 * @author Lauri Kosonen
 * @version 2018-12-06
 */
public class CardArchive {
    private static final String PROGRAM_VERSION = "v1.8, 2018-12-06";
    private static final boolean SHUFFLE_DECK_FOR_EACH_HAND = false;
    private static final boolean PRINT_CATEGORIES = true;
    private static List<Card> deck;
    private static Hand[] hands;
    private static List<Integer> categorySizes;
    private static List<Integer> categoryFirstCardIndexes;
    private static int handAmount = 0;
    private static int cardsInHand = 0;
    private static int drawnCardAmount = 0;
    private static boolean showAll = false;
    private static int shownCategory = -1;

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

        boolean runMainProgram = true;

        // Creates the deck
        initDeck();

        if (deck.size() == 0) {
            System.out.println("There are no cards!");
            runMainProgram = false;
        }
        else {
            // Parses special commands
            runMainProgram = parseSpecialCommands(args);
        }

        if (runMainProgram) {

            // Initializes the number of hands generated
            // and how many cards are in a hand
            initHandAmountAndSize(args);

            // Continues if the number of hands and hand size are positive
            if (handAmount > 0 && (cardsInHand > 0 || showAll)) {
                boolean emptyDeck = false;
                boolean incompleteHand = false;

                if (!showAll && shownCategory < 0) {
                    shuffleDeck();
                }

                // Creates and checks the hands
                hands = new Hand[handAmount];
                for (int i = 0; i < handAmount; i++) {
                    if (!emptyDeck) {

                        // Initializes the current hand
                        hands[i] = new Hand(cardsInHand);

                        // Adds cards to the hand
                        for (int j = 0; j < cardsInHand; j++) {
                            if (shownCategory < 0) {
                                drawCard(i, j);
                            }
                            else {
                                drawCardInCategory(i, j, shownCategory);
                            }

                            // Prints the card number
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
                            if (drawnCardAmount == deck.size()) {
                                emptyDeck = true;
                                incompleteHand = (j < cardsInHand - 1);

                                if (!showAll && (cardsInHand < deck.size()) &&
                                     (incompleteHand || SHUFFLE_DECK_FOR_EACH_HAND)) {
                                    System.out.println("No more cards!");
                                }

                                break;
                            }
                        }

                        System.out.println("------");

                        if (SHUFFLE_DECK_FOR_EACH_HAND) {
                            shuffleDeck();
                            emptyDeck = false;
                        }
                    }
                }
            }
        }
    }

   /**
    * Parses the user input for any special commands.
    * The keywords include "all", "category", "stats" and "help".
    *
    * @param cmdArgs the arguments given in command line
    * @returns will the main program be run
    */
    private static boolean parseSpecialCommands(String[] cmdArgs) {
        if (cmdArgs.length > 0) {
            String firstCommand = cmdArgs[0].toLowerCase();

            // Show all cards
            if (firstCommand.equals("all")) {
                showAll = true;
                return true;
            }
            // Show only cards that belong to a certain category
            else if (firstCommand.equals("category") || firstCommand.equals("cat")) {
                return parseShowCategoryCommand(cmdArgs);
            }
            // Show deck stats
            else if (firstCommand.equals("stats") || firstCommand.equals("info")) {
                printStats();
                return false;
            }
            // Show instructions
            else if (firstCommand.equals("help") || firstCommand.equals("?")) {
                printInstructions();
                return false;
            }
            // Show only cards that belong to a certain category
            else {
                int categoryNumber = categoryNumber(firstCommand);
                if (categoryNumber >= 0) {
                    shownCategory = categoryNumber;
                    return true;
                }
            }
        }

        return true;
    }

   /**
    * Prints information about Card Archive:
    * - how many cards there are
    * - what categories there are
    * - how many cards there are in each category
    * - current program version and credits.
    */
    private static void printStats() {
        System.out.println("Cards: " + deck.size());
        System.out.println("Categories: " + categorySizes.size());
        for (int i = 0; i < categorySizes.size(); i++) {
            System.out.format("[%d. %s] size: %d\n",
                i, categoryName(i), categorySizes.get(i));
        }

        System.out.println("\nCurrent program version: " + PROGRAM_VERSION);
        System.out.println("Created by Lauri Kosonen");
    }

   /**
    * Prints instructions on how to use this program.
    */
    private static void printInstructions() {
        System.out.println("How to use this program:");
        System.out.println("- Execute in command line by writing the command in this format: java CardArchive argument1 argument2");
        System.out.println("- Leave arguments out to run the program using the default settings");
        System.out.println("- Possible arguments:");
        System.out.println("  - Input one number to view that many cards");
        System.out.println("  - Input two numbers to view that many hands and cards in each hand");
        System.out.println("  - Input a category's name to view the cards in it");
        System.out.println("  - Input \"category\" or \"cat\" followed by a category number to view the cards in it");
        System.out.println("  - Input \"all\" to view all cards");
        System.out.println("  - Input \"stats\" or \"info\" to see how many cards and what categories are there");
        System.out.println("  - Input \"help\" or \"?\" to see these instructions");
        System.out.println("- Press the Enter key to run the program");
        System.out.println("- Press the Up arrow key to insert the previous command and run the program again");
        System.out.println("- With each run you get different results depending on the arguments");
    }

   /**
    * Parses the user input for which category's cards will be displayed.
    *
    * Showing all cards in a category depends on the deck
    * not being shuffled. Each category's first card's index
    * in an unshuffled deck has been recorded and will be used
    * in conjunction with the categories' sizes to get the
    * correct cards from the deck.
    *
    * @param cmdArgs the arguments given in command line
    * @returns will the main program be run
    */
    private static boolean parseShowCategoryCommand(String[] cmdArgs) {
        if (cmdArgs.length > 1) {
            try {
                int input = Integer.parseInt(cmdArgs[1]);
                if (input >= 0 && input < categorySizes.size()) {
                    shownCategory = input;
                    return true;
                }
                else {
                    System.out.format("The category number must be between 0 and %d.\n",
                        categorySizes.size() - 1);
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Please input \"category\" or \"cat\" followed by " +
                                   "a category number to view the cards in it.");
            }
        }
        else {
            System.out.println("Please input also a category's " +
                               "number to view the cards in it.");
        }

        return false;
    }

   /**
    * Initializes the number of hands generated
    * and how many cards is in one hand.
    * Takes the command line arguments and parses
    * them into integers.
    * Giving no arguments defaults in the hand number of 1, hand size of 5.
    *
    * @param cmdArgs the arguments given in command line
    */
    private static void initHandAmountAndSize(String[] cmdArgs) {

        // Displays all cards of a certain category
        if (shownCategory >= 0) {
            handAmount = 1;
            cardsInHand = categorySizes.get(shownCategory);
            if (cardsInHand == 0) {
                System.out.println("The category is empty.");
            }
        }
        // Attempts to parse the input into two integers and
        // prints an error message if the input is invalid
        else if (cmdArgs.length > 0) {
            if (showAll) {
                handAmount = 1;
                cardsInHand = deck.size();
            }
            else {
                try {
                    // If there's only one command line argument,
                    // the argument is for the number of cards in hand
                    if (cmdArgs.length == 1) {
                        handAmount = 1;
                        cardsInHand = Integer.parseInt(cmdArgs[0]);
                    }
                    // If there's two or more, the first command
                    // line argument is for the number of hands and
                    // the second is for the number of cards in hand
                    else {
                        handAmount = Integer.parseInt(cmdArgs[0]);
                        cardsInHand = Integer.parseInt(cmdArgs[1]);
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println("The given input is not valid.\n");
                    printInstructions();
                    handAmount = 0;
                    cardsInHand = 0;
                    return;
                }

                if (handAmount < 1 || cardsInHand < 1) {
                    System.out.println("Please input two positive integers.");
                    handAmount = 0;
                    cardsInHand = 0;
                }
                else if (cardsInHand > deck.size() ||
                         (!SHUFFLE_DECK_FOR_EACH_HAND &&
                          handAmount > deck.size())) {
                    System.out.format("Invalid input - the deck has %d cards.",
                        deck.size());
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
    * Returns a category's number based on the given name.
    * Returns -1 if the name doesn't match with any category.
    * Only the first 3 letters are checked,
    * so the argument can be shortened.
    *
    * @param categoryName a category's name
    * @return the number of a category or -1 for error
    */
    public static int categoryNumber(String categoryName) {
        if (categoryName != null && categoryName.length() >= 3) {
            categoryName = categoryName.toLowerCase().substring(0, 3);

            for (int i = 0; i < categorySizes.size(); i++) {
                String catCandidate =
                    categoryName(i).toLowerCase().substring(0, 3);
                if (categoryName.equals(catCandidate)) {
                    return i;
                }
            }
        }

        return -1;
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
                return "Abilities";
            }
            case 8: {
                return "Items";
            }
            case 9: {
                return "Goal";
            }
            case 10: {
                return "Audio & Visuals";
            }
            case 11: {
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
    * Creates the deck.
    */
    private static void initDeck() {
        deck = new ArrayList<Card>();
        categorySizes = new ArrayList<Integer>();
        categoryFirstCardIndexes = new ArrayList<Integer>();

        // Group
        initCard(0, "Multiplayer");
        initCard(0, "Local multiplayer");
        initCard(0, "Co-op/versus game");
        initCard(0, "Asymmetrical multiplayer");
        initCard(0, "Asynchronous multiplayer");
        initCard(0, "Turn-based gameplay");
        initCard(0, "Players start at different times");
        initCard(0, "Game master");
        initCard(0, "Mentor");
        initCard(0, "Referee");
        initCard(0, "Player guilds");
        initCard(0, "Faction war");
        initCard(0, "Commanding players/units");
        initCard(0, "Recruiting characters/units");
        initCard(0, "Converting enemies to the player's team");
        initCard(0, "Membership in different groups");
        initCard(0, "Rising through the ranks");
        initCard(0, "Temporary alliance against a common enemy");
        initCard(0, "Betraying the player's own team");
        initCard(0, "Being undercover as a member of a different team");
        initCard(0, "Dead players change sides");
        initCard(0, "Summonable allies");
        initCard(0, "AI-controlled sidekick/companion/pet");
        initCard(0, "AI-controlled minions");
        initCard(0, "Unusual forms of communication");
        initCard(0, "Leaving messages for other players");
        initCard(0, "Speaking to a crowd");
        initCard(0, "Family");
        initCard(0, "Crowd/horde");
        initCard(0, "Hive mind");
        // World
        initCard(1, "Open world");
        initCard(1, "Exploration");
        initCard(1, "Day-night cycle");
        initCard(1, "Changing weather");
        initCard(1, "Wildlife");
        initCard(1, "Large height differences");
        initCard(1, "Long distances");
        initCard(1, "Vast emptiness");
        initCard(1, "Confined spaces");
        initCard(1, "Underground tunnels");
        initCard(1, "Rolling hills");
        initCard(1, "Alien landscape");
        initCard(1, "Hellscape");
        initCard(1, "Impossible environment");
        initCard(1, "Parallel worlds");
        initCard(1, "Safety indoors, danger outdoors");
        initCard(1, "Environmental hazards");
        initCard(1, "Hazards rain from above");
        initCard(1, "Flood");
        initCard(1, "Burning heat");
        initCard(1, "Freezing cold");
        initCard(1, "Deadly gas/liquid/radiation");
        initCard(1, "Invisible walls or platforms");
        initCard(1, "Barriers that only let specific things through");
        initCard(1, "Randomly generated area");
        initCard(1, "Secret areas");
        initCard(1, "Fog of war");
        initCard(1, "Checkpoints");
        initCard(1, "Hub area");
        initCard(1, "Optional collectibles");
        initCard(1, "Optional activities");
        initCard(1, "Random events");
        initCard(1, "The world lives even if the player is away");
        initCard(1, "Abandoned place/object");
        // Influence
        initCard(2, "Controlling an area");
        initCard(2, "Reshaping the environment");
        initCard(2, "The environment reshapes itself");
        initCard(2, "Destroying objects/environment");
        initCard(2, "Areas can be made safe");
        initCard(2, "Level editor");
        initCard(2, "Base building");
        initCard(2, "Home decoration");
        initCard(2, "Founding cities");
        initCard(2, "Factions and reputation");
        initCard(2, "Research and technology");
        initCard(2, "Getting feedback from advisors/customers");
        initCard(2, "Keeping people happy");
        initCard(2, "Participating in politics");
        initCard(2, "Voting on subjects");
        initCard(2, "Court of law");
        initCard(2, "Running a business");
        initCard(2, "Creating traps/obstacles/barricades");
        initCard(2, "Creating climbable surfaces");
        initCard(2, "Creating roads for faster travel");
        initCard(2, "Adding things to walls");
        initCard(2, "Automating tasks");
        initCard(2, "Rerouting power to different systems");
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
        initCard(3, "Growing up");
        initCard(3, "Religion");
        initCard(3, "Karma system");
        initCard(3, "Power-ups and downs");
        initCard(3, "Blessings and curses");
        initCard(3, "Diseases and cures");
        initCard(3, "Insanity");
        initCard(3, "Imprisonment");
        initCard(3, "Fall damage");
        initCard(3, "Buying levels/skills/power");
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
        initCard(4, "Limited mobility/movement points");
        initCard(4, "Speed");
        initCard(4, "Quick downhill movement");
        initCard(4, "Grinding on rails");
        initCard(4, "Grabbing onto walls and ceiling");
        initCard(4, "Swinging on ropes or such");
        initCard(4, "Pole vaulting");
        initCard(4, "Wall running");
        initCard(4, "Rolling/spinning/orbiting");
        initCard(4, "Jumping on falling objects");
        initCard(4, "On-rails action");
        initCard(4, "Backtracking");
        initCard(4, "Vehicles");
        initCard(4, "Summonable vehicles");
        initCard(4, "Vehicles for hire");
        initCard(4, "Operating a vehicle as a team");
        initCard(4, "Boarding vehicles as a stowaway");
        initCard(4, "Hitchhiking");
        initCard(4, "Riding");
        initCard(4, "Swimming");
        initCard(4, "Diving");
        initCard(4, "Gliding");
        initCard(4, "Flying");
        initCard(4, "Burrowing through the ground");
        initCard(4, "Sea travel");
        initCard(4, "Space travel");
        initCard(4, "Train/handcar");
        initCard(4, "Traffic");
        initCard(4, "Smuggling");
        initCard(4, "Shipwreck");
        initCard(4, "The planet's curvature influences gameplay");
        // Thinking
        initCard(5, "Puzzles");
        initCard(5, "Using certain tools/skills on certain objects");
        initCard(5, "Scouting an area before entering");
        initCard(5, "Repeating past events/levels with new knowledge");
        initCard(5, "Physics-based gameplay");
        initCard(5, "Unusual gravity");
        initCard(5, "Witnessing past/future events");
        initCard(5, "The four classical elements");
        initCard(5, "Redirecting beams of light");
        initCard(5, "Conversation options");
        initCard(5, "Moral choices");
        initCard(5, "Making a deal");
        initCard(5, "Exchanging health for something");
        initCard(5, "Exchanging freedom for something");
        initCard(5, "Database");
        initCard(5, "Item descriptions");
        initCard(5, "Unexplained items and contraptions");
        initCard(5, "Taking notes is recommended");
        initCard(5, "Detective work");
        initCard(5, "Deciphering clues");
        initCard(5, "Passcodes and encrypted messages");
        initCard(5, "Programming/hacking");
        initCard(5, "Pattern recognition");
        initCard(5, "Memorization");
        initCard(5, "Truth and lies");
        initCard(5, "Pulling and pushing");
        initCard(5, "Doors and keys");
        initCard(5, "Switches");
        initCard(5, "Quiz");
        // Combat
        initCard(6, "Combat");
        initCard(6, "Aerial combat");
        initCard(6, "Naval combat");
        initCard(6, "Space combat");
        initCard(6, "Mech combat");
        initCard(6, "Martial arts");
        initCard(6, "Stun attacks");
        initCard(6, "Damage-over-time attacks");
        initCard(6, "Explosive attacks");
        initCard(6, "Combo attacks");
        initCard(6, "Melee weapons");
        initCard(6, "Throwing weapons and picking them up again");
        initCard(6, "Ranged weapons and ammunition");
        initCard(6, "Target-locking weapons");
        initCard(6, "Mines");
        initCard(6, "Sniping from a long distance");
        initCard(6, "Assassination");
        initCard(6, "Dodging attacks");
        initCard(6, "Deflecting attacks");
        initCard(6, "Friendly fire");
        initCard(6, "Weaknesses and resistances to attacks");
        initCard(6, "Weapons can be found in the world");
        initCard(6, "Equipped weapon changes after getting a kill");
        initCard(6, "Using objects in the world as ammunition");
        initCard(6, "Pushing enemies off ledges");
        initCard(6, "Permanent death");
        initCard(6, "No respawning until the round/wave ends");
        initCard(6, "Boss enemy");
        initCard(6, "Stealthy enemies");
        initCard(6, "Exploding enemies");
        initCard(6, "Regenerating enemies");
        initCard(6, "Enemies which spawn more enemies");
        initCard(6, "Enemies which give power-ups to others");
        initCard(6, "Enemies which won't attack unless provoked");
        initCard(6, "Weak, fleeing enemies with good loot");
        initCard(6, "Enemy waves");
        initCard(6, "Enemies spawn constantly");
        initCard(6, "Enemies alert others of the player");
        initCard(6, "Enemies can't leave their territories");
        initCard(6, "Enemy grows stronger when it defeats the player");
        // Abilities
        initCard(7, "Abilities with cooldowns");
        initCard(7, "Wielding magic");
        initCard(7, "Telekinesis");
        initCard(7, "Cloning");
        initCard(7, "Necromancy");
        initCard(7, "Electricity/lightning");
        initCard(7, "Shield/invulnerability");
        initCard(7, "Manipulating time");
        initCard(7, "Mind control");
        initCard(7, "Invisibility");
        initCard(7, "Stealth/hiding");
        initCard(7, "Regenerating health");
        initCard(7, "Healing teammates");
        initCard(7, "Reviving fallen teammates");
        initCard(7, "Respawning next to a teammate");
        initCard(7, "Second wind revival");
        initCard(7, "Grabbing onto characters");
        initCard(7, "Catching thrown/flying objects");
        initCard(7, "Capturing characters/creatures");
        initCard(7, "Absorbing everything to oneself");
        initCard(7, "Characters/objects can fuse and form a single entity");
        initCard(7, "Transfering one's mind into a different body");
        initCard(7, "Reincarnating as a different being");
        initCard(7, "Photography");
        initCard(7, "Marking targets");
        initCard(7, "Special sight ability");
        initCard(7, "Looking at/away from something causes an effect");
        initCard(7, "Being affected by staying close to certain things");
        initCard(7, "Something happens only if the player moves");
        initCard(7, "A character/an object can go through walls");
        initCard(7, "Objects bounce off surfaces");
        initCard(7, "Fixing objects in place");
        // Items
        initCard(8, "Inventory");
        initCard(8, "Looting");
        initCard(8, "Crafting");
        initCard(8, "Buying and selling");
        initCard(8, "Trading");
        initCard(8, "Gifts");
        initCard(8, "Brewing potions");
        initCard(8, "Poisons and antidotes");
        initCard(8, "Phone/walkie-talkie");
        initCard(8, "Binoculars/spyglass");
        initCard(8, "Grappling hook");
        initCard(8, "Repair tool");
        initCard(8, "Excavation tool");
        initCard(8, "Landscaping/farming tools");
        initCard(8, "Musical instruments");
        initCard(8, "Earning or paying salary");
        initCard(8, "Factions use different currencies");
        initCard(8, "Cash as an inventory item");
        initCard(8, "World map as an inventory item");
        initCard(8, "World map shows special information");
        initCard(8, "Unreliable world map");
        initCard(8, "Inventory as an in-world object");
        initCard(8, "Lore items");
        initCard(8, "Decoy items");
        initCard(8, "Breadcrumb trail items");
        initCard(8, "Leaving items behind as bait");
        initCard(8, "Storing items within the world");
        initCard(8, "Fillable containers");
        initCard(8, "Item rarity levels");
        initCard(8, "Limited item durability");
        initCard(8, "Breaking items down to their components");
        initCard(8, "Dead characters drop their equipment");
        initCard(8, "Taking enemies' equipment");
        initCard(8, "Items have random stats");
        initCard(8, "Items can be enhanced by adding trinkets");
        initCard(8, "Items can be enhanced by using them");
        initCard(8, "Items can be enhanced by crafting/magic");
        initCard(8, "Renewable resources/items in the world");
        initCard(8, "Scarcity of resources");
        initCard(8, "Equipment transmogrification");
        // Goal
        initCard(9, "Competition");
        initCard(9, "Leaderboard");
        initCard(9, "Tournament with multiple matches");
        initCard(9, "Score");
        initCard(9, "Quests");
        initCard(9, "Education");
        initCard(9, "Racing");
        initCard(9, "Rhythm action");
        initCard(9, "Gambling");
        initCard(9, "Doing stunts");
        initCard(9, "Tycoon game");
        initCard(9, "God game");
        initCard(9, "Goals are decided by the player");
        initCard(9, "The objective is not explicit");
        initCard(9, "The objective's position is random");
        initCard(9, "Failure changes the objective");
        initCard(9, "Points can be stolen from the opponent");
        initCard(9, "Outlasting the opponent");
        initCard(9, "Surviving as long as possible");
        initCard(9, "Evading obstacles");
        initCard(9, "Escaping from something/somewhere");
        initCard(9, "Protecting a target");
        initCard(9, "Rescuing characters");
        initCard(9, "Defending an area");
        initCard(9, "Finding a home");
        initCard(9, "Finding items");
        initCard(9, "Taking an item to the goal");
        initCard(9, "Delivering cargo/a payload");
        initCard(9, "Hiding something from the opponent");
        initCard(9, "Revisiting levels with new tools/powers");
        initCard(9, "Keeping balance");
        initCard(9, "Making two ends connect");
        initCard(9, "Special challenges");
        initCard(9, "Daily challenges");
        initCard(9, "Different game modes");
        initCard(9, "King of the Hill");
        initCard(9, "Capture the Flag");
        initCard(9, "Team Deathmatch");
        initCard(9, "Free-for-All");
        initCard(9, "Last Man Standing");
        initCard(9, "Sandbox mode with unlimited resources");
        // Audio & Visuals
        initCard(10, "2D");
        initCard(10, "3D");
        initCard(10, "First-person view");
        initCard(10, "Third-person view");
        initCard(10, "Side-scrolling view");
        initCard(10, "Top-down view");
        initCard(10, "Isometric view");
        initCard(10, "Virtual reality");
        initCard(10, "Augmented reality");
        initCard(10, "Sounds/voices are important");
        initCard(10, "Colors are important");
        initCard(10, "Lighting and shadows");
        initCard(10, "Minimal or no HUD");
        initCard(10, "Seeing through another player's eyes");
        initCard(10, "Seeing characters/objects through walls");
        initCard(10, "The game camera cannot be freely moved");
        initCard(10, "Unusual vision");
        initCard(10, "Enhanced hearing");
        initCard(10, "Limited vision");
        initCard(10, "Limited hearing");
        initCard(10, "A character/an object leaves a trail after it");
        initCard(10, "Characters'/objects' paths are visualized");
        initCard(10, "A guiding arrow/line");
        initCard(10, "Voice acting");
        initCard(10, "Narrator");
        initCard(10, "In-game music player for the soundtrack");
        // Miscellaneous
        initCard(11, "Motion controls");
        initCard(11, "Unusual controls");
        initCard(11, "Word game");
        initCard(11, "Board game");
        initCard(11, "Cards");
        initCard(11, "Chance and probability");
        initCard(11, "Naming a character/an object");
        initCard(11, "Farming");
        initCard(11, "Hunting");
        initCard(11, "Cooking");
        initCard(11, "Drawing");
        initCard(11, "Haunting");
        initCard(11, "Limited tries/lives");
        initCard(11, "Save points");
        initCard(11, "Saving the game anytime");
        initCard(11, "New game +");
        initCard(11, "Random gameplay modifiers");
        initCard(11, "Comedy");
        initCard(11, "Drama");
        initCard(11, "Romance");
        initCard(11, "Musical");
        initCard(11, "Mystery");
        initCard(11, "Thriller");
        initCard(11, "Horror"); 
    }

   /**
    * Initializes a card.
    *
    * @param category    a category's index
    * @param name        the card's name
    */
    private static void initCard(int category, String name) {

        // Creates the card
        deck.add(new Card(category, name));

        // Adds the current category and all previous
        // missing ones to the category size list; also
        // records the index of the category's first card.
        if (categorySizes.size() <= category) {

            // Missing categories
            int categoryCount = categorySizes.size();
            for (int i = 0; i < category - categoryCount; i++) {
                categorySizes.add(0);
                categoryFirstCardIndexes.add(-1);
            }

            // Current category
            categorySizes.add(1);
            categoryFirstCardIndexes.add(deck.size() - 1);
        }
        // Increases the size of the current category and, if the category
        // was empty, records the index of the category's first card
        else {
            int startingCategorySize = categorySizes.get(category);
            categorySizes.set(category, startingCategorySize + 1);

            if (startingCategorySize == 0) {
                categoryFirstCardIndexes.set(category, deck.size() - 1);
            }
        }
    }

   /**
    * Shuffles the deck.
    * Does nothing if every card is shown.
    */
    private static void shuffleDeck() {
        double rand;
        Card temp;
        for (int i = 0; i < deck.size(); i++) {
            rand = Math.random();
            int randCardIndex = (int) (rand * deck.size());
            temp = deck.get(randCardIndex);
            deck.set(randCardIndex, deck.get(i));
            deck.set(i, temp);
        }

        drawnCardAmount = 0;
    }

   /**
    * Takes a card from the deck and adds it to a hand.
    *
    * @param handIndex  a hand's index
    * @param cardIndex  a card's index
    */
    private static void drawCard(int handIndex, int cardIndex) {
        if (handIndex < handAmount && cardIndex < cardsInHand) {
            hands[handIndex].addCard(cardIndex, deck.get(drawnCardAmount));
            drawnCardAmount++;
        }
    }

   /**
    * Takes a card which belongs a given category and adds it to a hand.
    *
    * @param handIndex  a hand's index
    * @param cardIndex  a card's index
    * @param category   a category's index
    */
    private static void drawCardInCategory(int handIndex, int cardIndex, int category) {
        if (category >= 0 && category < categorySizes.size() &&
             cardIndex < cardsInHand) {
            hands[handIndex].addCard(cardIndex,
                deck.get(categoryFirstCardIndexes.get(category) + cardIndex));
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
