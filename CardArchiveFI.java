import java.util.*;

/**
 * Card Archive: Game Elements (Finnish version)
 *
 * A tool for coming up with game ideas.
 * Displays random game mechanics, play styles, genres
 * and other features you can find in games of all types.
 *
 * See the instructions for using the program from line 205 onwards.
 *
 * @author Lauri Kosonen
 * @version 2019-01-07
 */
public class CardArchiveFI {
    private static final String PROGRAM_VERSION = "v2.0, 2019-01-07";
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
        String title = "PELIELEMENTTIKORTIT";
        System.out.println();
        System.out.println(title);
        System.out.println();

        boolean runMainProgram = true;

        // Creates the deck
        initDeck();

        if (deck.size() == 0) {
            System.out.println("Ei kortteja!");
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

                            // Prints the card's index number
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
                                System.out.println("< Korttia ei ole! >");
                            }

                            // Prevents further card adding and
                            // hand checking if the deck is empty
                            if (drawnCardAmount == deck.size()) {
                                emptyDeck = true;
                                incompleteHand = (j < cardsInHand - 1);

                                if (!showAll && (cardsInHand < deck.size()) &&
                                     (incompleteHand || SHUFFLE_DECK_FOR_EACH_HAND)) {
                                    System.out.println("Kortit loppuivat!");
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
            String firstCommand = shortenLowerCase(cmdArgs[0], 3);

            // Show all cards
            if (firstCommand.equals("all") || firstCommand.equals("kai")) {
                showAll = true;
                return true;
            }
            // Show only cards that belong to a certain category
            else if (firstCommand.equals("cat") || firstCommand.equals("kat")) {
                return parseShowCategoryCommand(cmdArgs);
            }
            // Show deck stats
            else if (firstCommand.equals("sta") || firstCommand.equals("inf")
                     || firstCommand.equals("tie")) {
                printStats();
                return false;
            }
            // Show instructions
            else if (firstCommand.equals("hel") || firstCommand.equals("?")
                     || firstCommand.equals("ohj")|| firstCommand.equals("apu")) {
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
    * Shortens the given string and makes it lower case.
    * If the string is too short for the given charCount,
    * it is returned without changes.
    *
    * @param s          a string
    * @param charCount  how long should the resulting string be
    * @return a shortened, lower case string
    */
    private static String shortenLowerCase(String s, int charCount) {
        if (charCount > 0 && s.length() > charCount) {
            return s.substring(0, charCount).toLowerCase();
        } else {
            return s;
        }
    }

   /**
    * Prints information about Card Archive:
    * - how many cards there are
    * - what categories there are
    * - how many cards there are in each category
    * - current program version and credits.
    */
    private static void printStats() {
        System.out.println("Kortteja: " + deck.size());
        System.out.println("Kategorioita: " + categorySizes.size());
        for (int i = 0; i < categorySizes.size(); i++) {
            System.out.format("[%d. %s] koko: %d\n",
                i, categoryName(i), categorySizes.get(i));
        }

        System.out.println("\nOhjelman versio: " + PROGRAM_VERSION);
        System.out.println("Tekij"+a2+": Lauri Kosonen");
    }

   /**
    * Prints instructions on how to use this program.
    */
    private static void printInstructions() {
        System.out.println("Ohjeet:");
        System.out.println("- Aja komentokehotteessa kirjoittamalla komento t"+a2+"ss"+a2+" muodossa: java CardArchiveFI sy"+o2+"te1 sy"+o2+"te2");
        System.out.println("- J"+a2+"t"+a2+" sy"+o2+"tteet pois ajaaksesi ohjelman oletusasetuksilla");
        System.out.println("- Mahdolliset sy"+o2+"tteet:");
        System.out.println("  - Sy"+o2+"t"+a2+" yksi numero n"+a2+"hd"+a2+"ksesi niin monta korttia");
        System.out.println("  - Sy"+o2+"t"+a2+" kaksi numeroa n"+a2+"hd"+a2+"ksesi niin monta k"+a2+"tt"+a2+" ja niin monta korttia joka k"+a2+"dess"+a2+"");
        System.out.println("  - Sy"+o2+"t"+a2+" kategorian nimi n"+a2+"hd"+a2+"ksesi sen kortit");
        System.out.println("  - Sy"+o2+"t"+a2+" \"kategoria\" tai \"kat\" ja sen j"+a2+"lkeen kategorian numero n"+a2+"hd"+a2+"ksesi sen kortit");
        System.out.println("  - Sy"+o2+"t"+a2+" \"kaikki\" n"+a2+"hd"+a2+"ksesi kaikki kortit");
        System.out.println("  - Sy"+o2+"t"+a2+" \"tiedot\" tai \"info\" n"+a2+"hd"+a2+"ksesi mm. kuinka monta korttia ja mit"+a2+" kategorioita on");
        System.out.println("  - Sy"+o2+"t"+a2+" \"ohjeet\" tai \"?\" n"+a2+"hd"+a2+"ksesi n"+a2+"m"+a2+" ohjeet");
        System.out.println("- Paina Enter-n"+a2+"pp"+a2+"int"+a2+" ajaaksesi ohjelman");
        System.out.println("- Paina yl"+a2+"nuolin"+a2+"pp"+a2+"int"+a2+" valitaksesi edellisen komennon uudestaan");
        System.out.println("- Joka ajolla saat eri tuloksia sy"+o2+"tteist"+a2+" riippuen");
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
                    System.out.format("Kategorian numeron t"+a2+"ytyy olla 0 - %d.\n",
                        categorySizes.size() - 1);
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Ole hyv"+a2+" ja sy"+o2+"t"+a2+" \"kategoria\" tai \"kat\" ja sen " +
                                   "j"+a2+"lkeen kategorian numero n"+a2+"hd"+a2+"ksesi sen kortit.");
            }
        }
        else {
            System.out.println("Ole hyv"+a2+" ja sy"+o2+"t"+a2+" kategorian " +
                               "numero n"+a2+"hd"+a2+"ksesi sen kortit.");
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
                System.out.println("Kategoria on tyhj"+a2+".");
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
                    System.out.println("Sy"+o2+"te ei kelpaa.\n");
                    printInstructions();
                    handAmount = 0;
                    cardsInHand = 0;
                    return;
                }

                if (handAmount < 1 || cardsInHand < 1) {
                    System.out.println("Ole hyv"+a2+" ja sy"+o2+"t"+a2 +
                        " kaksi positiivista kokonaislukua");
                    handAmount = 0;
                    cardsInHand = 0;
                }
                else if (cardsInHand > deck.size() ||
                         (!SHUFFLE_DECK_FOR_EACH_HAND &&
                          handAmount > deck.size())) {
                    System.out.format("Sy"+o2+"te ei kelpaa - pakassa on %d korttia.",
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
            categoryName = shortenLowerCase(categoryName, 3);

            for (int i = 0; i < categorySizes.size(); i++) {
                String catCandidate = shortenLowerCase(categoryName(i), 3);
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
                return "Ryhm" + a2;
            }
            case 1: {
                return "Maailma";
            }
            case 2: {
                return "Vaikutus";
            }
            case 3: {
                return "Hahmo";
            }
            case 4: {
                return "Liike";
            }
            case 5: {
                return "Pohdinta";
            }
            case 6: {
                return "Taistelu";
            }
            case 7: {
                return "Kyvyt";
            }
            case 8: {
                return "Esineet";
            }
            case 9: {
                return "Tavoite";
            }
            case 10: {
                return ""+a1+a2+"ni & kuva";
            }
            case 11: {
                return "Sekalaiset";
            }

            // Prints an error message if the
            // category's number is out of limits
            default: {
                return "VIRHE";
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
        initCard(0, "Moninpeli");
        initCard(0, "Paikallinen moninpeli");
        initCard(0, "Yhteistyö-/versuspeli");
        initCard(0, "Epäsymmetrinen moninpeli");
        initCard(0, "Eriaikainen moninpeli");
        initCard(0, "Pelaajat aloittavat eri aikaan");
        initCard(0, "Pelaajat voivat liittyä peliin tai poistua pelistä milloin vain");
        initCard(0, "Pelinjohtaja");
        initCard(0, "Mentori");
        initCard(0, "Erotuomari");
        initCard(0, "Pelaajakillat");
        initCard(0, "Ryhmittymien välinen sota");
        initCard(0, "Liittyminen eri ryhmiin");
        initCard(0, "Yleneminen arvoasteikolla");
        initCard(0, "Hahmojen/yksiköiden komentaminen");
        initCard(0, "Hahmojen/yksiköiden värvääminen");
        initCard(0, "Vihollisten käännyttäminen pelaajan tiimiin");
        initCard(0, "Kuolleet pelaajat vaihtavat tiimiä");
        initCard(0, "Väliaikainen liitto yhteistä uhkaa vastaan");
        initCard(0, "Oman tiimin pettäminen");
        initCard(0, "Toisen tiimin jäsenen esittäminen");
        initCard(0, "Liittolaisten kutsuminen paikalle");
        initCard(0, "Tekoälyn ohjaama apuri/toveri/lemmikki");
        initCard(0, "Tekoälyn ohjaamat kätyrit");
        initCard(0, "Epätavallinen keino kommunikoida");
        initCard(0, "Viestien jättäminen muille");
        initCard(0, "Yleisölle puhuminen");
        initCard(0, "Perhe");
        initCard(0, "Väkijoukko/lauma");
        initCard(0, "Parviäly");
        initCard(0, "Salaseura");
        // World
        initCard(1, "Avoin maailma");
        initCard(1, "Ympäristön tutkiminen");
        initCard(1, "Päivän ja yön vaihtelu");
        initCard(1, "Muuttuva sää");
        initCard(1, "Villieläimet");
        initCard(1, "Suuret korkeuserot");
        initCard(1, "Pitkät etäisyydet");
        initCard(1, "Avara tyhjyys");
        initCard(1, "Ahtaat tilat");
        initCard(1, "Maanalaiset tunnelit");
        initCard(1, "Kumpuileva maasto");
        initCard(1, "Maapallon ulkopuolinen maasto");
        initCard(1, "Karu tai vaarallinen maasto");
        initCard(1, "Hylätty paikka/esine");
        initCard(1, "Mahdoton ympäristö");
        initCard(1, "Rinnakkaismaailmat");
        initCard(1, "Turva sisällä, vaara ulkona");
        initCard(1, "Ympäristön vaarat");
        initCard(1, "Vaaroja sataa taivaalta");
        initCard(1, "Tulva");
        initCard(1, "Polttava kuumuus");
        initCard(1, "Jäätävä kylmyys");
        initCard(1, "Tappava kaasu/neste/säteily");
        initCard(1, "Näkymättömät seinät tai lattiat");
        initCard(1, "Esteet, jotka päästävät läpi vain tiettyjä asioita");
        initCard(1, "Satunnaisesti luotu alue");
        initCard(1, "Salaiset alueet");
        initCard(1, "Vähitellen paljastuvat alueet kartalla");
        initCard(1, "Tarkistuspisteet");
        initCard(1, "Hubi/keskusalue, jolta pääsee helposti muille alueille");
        initCard(1, "Valinnaiset kerättävät");
        initCard(1, "Valinnaiset aktiviteetit");
        initCard(1, "Satunnaiset tapahtumat");
        initCard(1, "Maailma elää vaikka pelaaja ei olisikaan paikalla");
        // Influence
        initCard(2, "Alueen hallinta");
        initCard(2, "Ympäristön muokkaaminen");
        initCard(2, "Ympäristö muokkautuu itsestään");
        initCard(2, "Esineiden/ympäristön tuhoaminen");
        initCard(2, "Kenttäeditori");
        initCard(2, "Tukikohdan rakentaminen");
        initCard(2, "Kodin sisustaminen");
        initCard(2, "Kaupunkien tai muiden asutusten perustaminen");
        initCard(2, "Vaikuttaminen ekosysteemiin");
        initCard(2, "Alueista voi tehdä turvallisia");
        initCard(2, "Joitakin sääntöjä voi rikkoa, tarkoituksellisesti tai ei");
        initCard(2, "Ryhmittymät ja maine");
        initCard(2, "Tutkimus ja teknologia");
        initCard(2, "Palautteen saaminen neuvonantajilta/asukkailta/asiakkailta");
        initCard(2, "Ihmisten pitäminen tyytyväisinä");
        initCard(2, "Osallistuminen politiikkaan");
        initCard(2, "Asioista äänestäminen");
        initCard(2, "Oikeusistunto");
        initCard(2, "Yrittäjyys");
        initCard(2, "Pelaajat vaikuttavat pelimaailman talouteen");
        initCard(2, "Asioiden kiinnittäminen seiniin");
        initCard(2, "Ansojen ja esteiden luominen");
        initCard(2, "Kiivettävien pintojen luominen");
        initCard(2, "Teiden luominen nopeampaa liikkumista varten");
        initCard(2, "Ajoneuvon muokkaaminen");
        initCard(2, "Tehtävien automatisointi");
        initCard(2, "Virran ohjaaminen eri järjestelmiin");
        initCard(2, "Kuolleet/jäätyneet hahmot jäävät osiksi ympäristöä");
        // Character
        initCard(3, "Hahmon muokkaaminen");
        initCard(3, "Tason kasvattaminen");
        initCard(3, "Taitopuu");
        initCard(3, "Hahmoluokat");
        initCard(3, "Useampi ohjattava hahmo/yksikkö");
        initCard(3, "Ei ohjattavaa hahmoa");
        initCard(3, "Hahmon tarpeista huolehtiminen");
        initCard(3, "Hahmon tunteet");
        initCard(3, "Kasvaminen aikuiseksi");
        initCard(3, "Uskonto");
        initCard(3, "Karmasysteemi");
        initCard(3, "Luonteenpiirteet");
        initCard(3, "Voimalisät ja heikennykset");
        initCard(3, "Siunaukset ja kiroukset");
        initCard(3, "Taudit ja lääkkeet");
        initCard(3, "Hulluus");
        initCard(3, "Vankeus");
        initCard(3, "Tippumisvahinko");
        initCard(3, "Räsynukkefysiikat");
        initCard(3, "Tasojen/taitojen/voiman ostaminen");
        initCard(3, "Valeasut");
        initCard(3, "Muodon muuttaminen");
        initCard(3, "Henkimuoto");
        initCard(3, "Petomuoto");
        initCard(3, "Biologiset parannukset");
        initCard(3, "Mekaaniset/kybernettiset parannukset");
        initCard(3, "Jumalalliset/epäpyhät parannukset");
        initCard(3, "Hahmon koko on tärkeä");
        initCard(3, "Hahmon ulkonäkö vaikuttaa peliin");
        initCard(3, "Hahmon vamma vaikuttaa peliin");
        // Navigation
        initCard(4, "Tasohyppely/kiipeily");
        initCard(4, "Pikamatkustus/teleportaatio");
        initCard(4, "Rajoitettu liikkuvuus/liikepisteet");
        initCard(4, "Nopeus");
        initCard(4, "Mäenlasku");
        initCard(4, "Grindaaminen");
        initCard(4, "Seiniin ja kattoon tarraaminen");
        initCard(4, "Heiluminen köysillä tai vastaavilla");
        initCard(4, "Seiväshyppy");
        initCard(4, "Seinäjuoksu");
        initCard(4, "Kieriminen/pyöriminen/kiertorataliike");
        initCard(4, "Putoavilla esineillä hyppely");
        initCard(4, "Toiminta raiteilla");
        initCard(4, "Paluu aiemmille alueille");
        initCard(4, "Oikotiet");
        initCard(4, "Ajoneuvot");
        initCard(4, "Ajoneuvon kutsuminen paikalle");
        initCard(4, "Ajoneuvon ohjaaminen tiiminä");
        initCard(4, "Matkustaminen ajoneuvon kyytiläisenä");
        initCard(4, "Salamatkustus");
        initCard(4, "Ratsastus");
        initCard(4, "Uiminen");
        initCard(4, "Sukeltaminen");
        initCard(4, "Liitäminen");
        initCard(4, "Lentäminen");
        initCard(4, "Maan alle kaivautuminen");
        initCard(4, "Merimatkustus");
        initCard(4, "Avaruusmatkustus");
        initCard(4, "Rautatieliikenne");
        initCard(4, "Liikenne");
        initCard(4, "Salakuljetus");
        initCard(4, "Haaksirikko");
        initCard(4, "Planeetan kaarevuus vaikuttaa peliin");
        // Thinking
        initCard(5, "Pulmat");
        initCard(5, "Tiettyjen kykyjen/työkalujen käyttäminen tiettyihin kohteisiin");
        initCard(5, "Uusien kykyjen/työkalujen käyttö vanhoilla alueilla");
        initCard(5, "Vanhojen tapahtumien/kenttien toistaminen uuden tiedon kanssa");
        initCard(5, "Selvän ottaminen alueesta ennen sille astumista");
        initCard(5, "Fysiikkapainotteinen peli");
        initCard(5, "Epätavallinen painovoima");
        initCard(5, "Menneiden/tulevien tapahtumien näkeminen");
        initCard(5, "Neljä klassista elementtiä");
        initCard(5, "Valonsäteiden uudelleenohjaus");
        initCard(5, "Ketjun muodostaminen kahden pään välille");
        initCard(5, "Hahmojen/esineiden paikkojen vaihtaminen keskenään");
        initCard(5, "Vaihtoehdot keskusteluissa");
        initCard(5, "Moraaliset valinnat");
        initCard(5, "Sopimuksen tekeminen");
        initCard(5, "Terveyden vaihtaminen johonkin");
        initCard(5, "Vapauden vaihtaminen johonkin");
        initCard(5, "Tietokanta/muistikirja/hirviöopas");
        initCard(5, "Esineiden kuvaukset");
        initCard(5, "Asiakirjojen tai äänitteiden tarkastelu");
        initCard(5, "Ympäristön yksityiskohtien tarkastelu");
        initCard(5, "Esineet ja kojeet, joille ei anneta selityksiä");
        initCard(5, "Muistiinpanojen ottaminen on suositeltavaa");
        initCard(5, "Salapoliisin työ");
        initCard(5, "Vihjeiden tulkitseminen");
        initCard(5, "Koodit ja salatut viestit");
        initCard(5, "Ohjelmointi/hakkerointi");
        initCard(5, "Kaavan tunnistaminen");
        initCard(5, "Asioiden painaminen muistiin");
        initCard(5, "Totuus ja valheet");
        initCard(5, "Vetäminen ja työntäminen");
        initCard(5, "Ovet ja avaimet");
        initCard(5, "Kytkimet");
        initCard(5, "Tietovisa");
        // Combat
        initCard(6, "Taistelu");
        initCard(6, "Ilmataistelu");
        initCard(6, "Meritaistelu");
        initCard(6, "Avaruustaistelu");
        initCard(6, "Mech-taistelu");
        initCard(6, "Taistelulajit");
        initCard(6, "Tainnutusiskut");
        initCard(6, "Iskut, jotka tekevät jatkuvaa vahinkoa");
        initCard(6, "Räjähtävät iskut");
        initCard(6, "Komboiskut");
        initCard(6, "Lähitaisteluaseet");
        initCard(6, "Heittoaseet ja niiden poiminen takaisin");
        initCard(6, "Tuliaseet ja ammukset");
        initCard(6, "Kohteeseen lukittuvat aseet");
        initCard(6, "Miinat");
        initCard(6, "Tarkkuusammunta etäältä");
        initCard(6, "Salamurha");
        initCard(6, "Vihollisten työntäminen alas jyrkänteiltä");
        initCard(6, "Hyökkäysten väistely");
        initCard(6, "Hyökkäysten kimmottaminen");
        initCard(6, "Ystävän satuttaminen");
        initCard(6, "Heikkoudet ja vastustuskyky iskuille");
        initCard(6, "Iskujen kohdistaminen kohteen tiettyihin osiin");
        initCard(6, "Aseita voi löytää maailmasta");
        initCard(6, "Käytetty ase vaihtuu taposta");
        initCard(6, "Maailmasta löytyvien esineiden käyttäminen ammuksina");
        initCard(6, "Lopullinen kuolema");
        initCard(6, "Ei uudelleenheräämistä ennen erän/taistelun loppumista");
        initCard(6, "Pomovihollinen");
        initCard(6, "Piileskelevät viholliset");
        initCard(6, "Räjähtävät viholliset");
        initCard(6, "Itseään parantavat viholliset");
        initCard(6, "Viholliset, jotka luovat lisää vihollisia");
        initCard(6, "Viholliset, jotka voimistavat muita");
        initCard(6, "Viholliset, jotka eivät hyökkää ilman ärsykettä");
        initCard(6, "Heikot, pakenevat viholliset, joilta saa hyvää tavaraa");
        initCard(6, "Vihollisaallot");
        initCard(6, "Vihollisia tulee loputtomasti");
        initCard(6, "Viholliset antavat muille tietoa pelaajan olinpaikasta");
        initCard(6, "Viholliset eivät voi poistua omilta alueiltaan");
        // Abilities
        initCard(7, "Kyvyt, jotka käytön jälkeen vaativat hetken jäähtymistä");
        initCard(7, "Taikuuden käyttäminen");
        initCard(7, "Psyykkiset kyvyt");
        initCard(7, "Esineiden leijuttaminen");
        initCard(7, "Mielenhallinta");
        initCard(7, "Kloonaus");
        initCard(7, "Kuolleiden herättäminen henkiin");
        initCard(7, "Ajan manipulointi");
        initCard(7, "Toiminnan nauhoittaminen ja toistaminen");
        initCard(7, "Sähkö/salamat");
        initCard(7, "Kilpi/haavoittumattomuus");
        initCard(7, "Näkymättömyys");
        initCard(7, "Hiiviskely/piileskely");
        initCard(7, "Palautuva kunto");
        initCard(7, "Tiimitoverien parantaminen");
        initCard(7, "Kaatuneiden tiimitoverien elvyttäminen");
        initCard(7, "Herääminen uudelleen tiimitoverin viereen");
        initCard(7, "Second wind -elpyminen");
        initCard(7, "Hahmoihin tarrautuminen");
        initCard(7, "Heitettyjen/lentävien esineiden ottaminen kiinni");
        initCard(7, "Hahmojen/otusten ottaminen kiinni");
        initCard(7, "Kaiken kerääminen osaksi itseä");
        initCard(7, "Hahmot/esineet voivat yhdistyä yhdeksi kokonaisuudeksi");
        initCard(7, "Mielen siirtäminen toiseen kehoon");
        initCard(7, "Herääminen uudelleen eri olentona");
        initCard(7, "Kummittelu");
        initCard(7, "Piirtäminen");
        initCard(7, "Valokuvaus");
        initCard(7, "Kohteiden merkkaus");
        initCard(7, "Erikoisnäkökyky");
        initCard(7, "Jonkin tai jostakin poispäin katsominen saa jotakin aikaan");
        initCard(7, "Läheisyys joihinkin asioihin vaikuttaa jotenkin");
        initCard(7, "Jotain tapahtuu vain, kun pelaaja liikkuu");
        initCard(7, "Hahmo/esine voi mennä seinistä läpi");
        initCard(7, "Asioihin törmääminen");
        initCard(7, "Esineet kimpoavat pinnoista");
        initCard(7, "Esineiden pysäyttäminen paikoilleen");
        initCard(7, "Kykyjen tai esineiden menettämistä vaaditaan etenemiseen");
        // Items
        initCard(8, "Tavaraluettelo");
        initCard(8, "Esineiden etsiminen");
        initCard(8, "Nikkarointi");
        initCard(8, "Ostaminen ja myyminen");
        initCard(8, "Vaihtaminen");
        initCard(8, "Lahjat");
        initCard(8, "Taikajuomien valmistus");
        initCard(8, "Myrkyt ja vastalääkkeet");
        initCard(8, "Puhelin/radiopuhelin");
        initCard(8, "Kiikarit/kaukoputki");
        initCard(8, "Kiipeilykoukku");
        initCard(8, "Korjaustyökalu");
        initCard(8, "Kaivuutyökalu");
        initCard(8, "Maanmuokkaus/-viljelytyökalut");
        initCard(8, "Soittimet");
        initCard(8, "Palkan saaminen tai maksaminen");
        initCard(8, "Käteinen tavaraluetteloesineenä");
        initCard(8, "Kartta tavaraluetteloesineenä");
        initCard(8, "Kartoissa on erikoistietoa");
        initCard(8, "Tavaraluettelo esineenä maailmassa");
        initCard(8, "Esineet, jotka kertovat maailmasta");
        initCard(8, "Harhautusesineet");
        initCard(8, "Leivänmurupolkuesineet");
        initCard(8, "Esineiden jättäminen syötiksi");
        initCard(8, "Esineiden varastoiminen maailmaan");
        initCard(8, "Täytettävät astiat");
        initCard(8, "Esineiden harvinaisuustasot");
        initCard(8, "Esineillä on rajoitettu kestävyys");
        initCard(8, "Esineiden hajottaminen raaka-aineiksi");
        initCard(8, "Kuolleet hahmot pudottavat varusteensa");
        initCard(8, "Vihollisten varusteiden vieminen");
        initCard(8, "Esineillä on satunnaiset ominaisuudet");
        initCard(8, "Esineitä voi parantaa helyjä lisäämällä");
        initCard(8, "Esineitä voi parantaa käyttämällä niitä");
        initCard(8, "Esineitä voi parantaa nikkaroinnilla/taikuudella");
        initCard(8, "Uusiutuvat resurssit/esineet maailmassa");
        initCard(8, "Resurssien vähyys");
        initCard(8, "Varusteiden ulkonäön muuttaminen");
        initCard(8, "Ryhmittymät käyttävät eri varusteita, valuuttoja tai ajoneuvoja");
        // Goal
        initCard(9, "Kilpailu");
        initCard(9, "Huippupistelista");
        initCard(9, "Turnaus");
        initCard(9, "Pisteet");
        initCard(9, "Tehtävät");
        initCard(9, "Opetus");
        initCard(9, "Kilpa-ajo");
        initCard(9, "Stunttien tekeminen");
        initCard(9, "Rytmipeli");
        initCard(9, "Strategiapeli");
        initCard(9, "Rahantekopeli");
        initCard(9, "Jumalpeli");
        initCard(9, "Pelaaja päättää tavoitteet");
        initCard(9, "Tavoitetta ei ole selkeästi kerrottu");
        initCard(9, "Tavoitteen paikka on satunnainen");
        initCard(9, "Epäonnistuminen muuttaa tavoitetta");
        initCard(9, "Pisteitä voi varastaa vastustajalta");
        initCard(9, "Selviäminen vastustajaa kauemmin");
        initCard(9, "Selviäminen niin kauan kuin mahdollista");
        initCard(9, "Esteiden väistely");
        initCard(9, "Pääsy pisteeseen maailmassa");
        initCard(9, "Pakeneminen joltakin/jostakin");
        initCard(9, "Kodin tai muun turvallisen paikan löytäminen");
        initCard(9, "Hahmojen pelastaminen");
        initCard(9, "Kohteen puolustaminen");
        initCard(9, "Alueen puolustaminen");
        initCard(9, "Esineen vieminen maaliin");
        initCard(9, "Kuorman toimittaminen");
        initCard(9, "Jonkin pitäminen piilossa vastustajalta");
        initCard(9, "Tasapainon säilyttäminen");
        initCard(9, "Erikoishaasteet");
        initCard(9, "Päivittäiset haasteet");
        initCard(9, "Eri pelimuodot");
        initCard(9, "Kukkulan kuningas");
        initCard(9, "Lipunryöstö");
        initCard(9, "Tiimitappomatsi");
        initCard(9, "Kaikki kaikkia vastaan");
        initCard(9, "Taistelu viimeiseen selviytyjään asti");
        initCard(9, "Hiekkalaatikkotila rajoittamattomilla resursseilla");
        // Audio & Visuals
        initCard(10, "2D");
        initCard(10, "3D");
        initCard(10, "Ensimmäisen persoonan näkymä");
        initCard(10, "Kolmannen persoonan näkymä");
        initCard(10, "Sivulta kuvattu näkymä");
        initCard(10, "Ylhäältä alas kuvattu näkymä");
        initCard(10, "Isometrinen näkymä");
        initCard(10, "Virtuaalitodellisuus");
        initCard(10, "Lisätty todellisuus");
        initCard(10, "Äänet ovat tärkeitä");
        initCard(10, "Värit ovat tärkeitä");
        initCard(10, "Valo ja pimeys");
        initCard(10, "Pelkistetty HUD tai ei HUD:ia ollenkaan");
        initCard(10, "Läheiset vaarat antavat ääni- tai kuvallisen merkin");
        initCard(10, "Vihollisen kunto näkyy ruudulla");
        initCard(10, "Toisen pelaajan silmien läpi näkeminen");
        initCard(10, "Hahmojen/esineiden näkeminen seinien läpi");
        initCard(10, "Hahmojen/esineiden polut näkyvät");
        initCard(10, "Pelikameraa ei voi vapaasti liikuttaa");
        initCard(10, "Rajoittunut näkö");
        initCard(10, "Rajoittunut kuulo");
        initCard(10, "Tavallista parempi kuulo");
        initCard(10, "Pelaajaa ohjaava nuoli/viiva");
        initCard(10, "Ääninäyttely");
        initCard(10, "Kertoja");
        initCard(10, "Pelinsisäinen musiikkisoitin pelin ääniraidalle");
        initCard(10, "Äänien tai musiikin luominen");
        // Miscellaneous
        initCard(11, "Mobiilipeli");
        initCard(11, "Liikeohjaus");
        initCard(11, "Epätavalliset kontrollit");
        initCard(11, "Peli vaatii hiiren osoitinta");
        initCard(11, "Pelissä tarvitaan vain yhtä tai kahta nappia");
        initCard(11, "Peli kestää vain minuutin tai pari");
        initCard(11, "Vuoropohjainen peli");
        initCard(11, "Sanapeli");
        initCard(11, "Lautapeli");
        initCard(11, "Kortit");
        initCard(11, "Uhkapeli");
        initCard(11, "Sattuma ja todennäköisyys");
        initCard(11, "Rikos ja rangaistus");
        initCard(11, "Keksitty kieli");
        initCard(11, "Hahmon, paikan tai esineen nimeäminen");
        initCard(11, "Maanviljely");
        initCard(11, "Metsästys");
        initCard(11, "Ruoanlaitto");
        initCard(11, "Rajoitettu määrä yrityksiä/elämiä");
        initCard(11, "Tallennuspisteet");
        initCard(11, "Pelin tallentaminen milloin vain");
        initCard(11, "Pelin aloittaminen alusta vaikeampana");
        initCard(11, "Häviö tekee uhasta vaikeamman");
        initCard(11, "Peli päättyy vain pelaajan häviöön");
        initCard(11, "Säädettävät pelimuuttujat");
        initCard(11, "Satunnaiset pelimuuttujat");
        initCard(11, "Komedia");
        initCard(11, "Draama");
        initCard(11, "Romanttinen");
        initCard(11, "Musikaali");
        initCard(11, "Mysteeri");
        initCard(11, "Trilleri");
        initCard(11, "Kauhu");
    }

   /**
    * Initializes a card.
    *
    * @param category    a category's index
    * @param name        the card's name
    */
    private static void initCard(int category, String name) {

        // Replaces scandinavian letters with working chars
        name = replaceScandinavianLetters(name);

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
    * Replaces scandinavian letters in the given string with working chars.
    *
    * @param s a string
    * @return a string without broken scandinavian letters
    */
    private static String replaceScandinavianLetters(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            String scand = "" + s.charAt(i) + s.charAt(i + 1);
            if ("Ä".equals(scand)) {
                s = replaceChar(s, i, a1, true);
            }
            else if ("ä".equals(scand)) {
                s = replaceChar(s, i, a2, true);
            }
            else if ("Ö".equals(scand)) {
                s = replaceChar(s, i, o1, true);
            }
            else if ("ö".equals(scand)) {
                s = replaceChar(s, i, o2, true);
            }
        }

        return s;
    }

   /**
    * Replaces a character in a string with another.
    *
    * @param s                  a string
    * @param i                  replaced char's index
    * @param c                  replacing char
    * @param removeNextChar     should the char after the replaced char be removed
    * @return a string
    */
    private static String replaceChar(String s, int i, char c, boolean removeNextChar) {
        if (i == 0) {
            s = c + s.substring(1);
        }
        else if (i == s.length() - 1) {
            s = s.substring(0, i) + c;
        }
        else {
            s = s.substring(0, i) + c + s.substring(i + 1);
        }

        if (removeNextChar && (i + 1) < s.length()) {
            String end = "";
            if (i + 2 < s.length()) {
                end = s.substring(i + 2);
            }
            s = s.substring(0, i + 1) + end;
        }

        return s;
    }

   /**
    * Shuffles the deck.
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

   /**
    * Formats the index that is printed before a card's name.
    * Depending on how many digits the last index has, adds
    * spaces before the index number.
    *
    * @param cardIndex  the current card's index
    * @param maxIndex   the last card's index
    * @return formatted card index
    */
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
