package com.example.data.curriculum

import com.example.i18n.AppLanguage
import kotlin.random.Random

data class EnglishTopic(
    val id: Int, // 1..500
    val cefr: String, // "A1", "A2", "B1", "B2", "C1", "C2"
    val title: String,
    val grammarFocus: String,
    val keyWords: List<String>,
    val lessonSummary: String,
    val sampleSentence: String
)

object EnglishCurriculum {
    val cefrLevels = listOf("ALL", "A1", "A2", "B1", "B2", "C1", "C2")

    private data class LevelTemplate(
        val cefr: String,
        val count: Int,
        val themes: List<String>,
        val grammarPoints: List<Pair<String, List<Triple<String, String, List<String>>>>>,
        val vocabSets: List<List<Triple<String, String, String>>> // word, meaning, sentence_with_blank
    )

    private val templates = listOf(
        LevelTemplate(
            cefr = "A1",
            count = 84,
            themes = listOf(
                "Greetings & Introductions", "Family & Relatives", "Numbers & Counting", "Days, Months & Seasons",
                "Classroom Objects", "Colors & Shapes", "Daily Morning Routine", "Food & Drinks", "At the Supermarket",
                "My Home & Rooms", "Telling the Time", "Jobs & Professions", "City Places & Directions", "Weather Basics"
            ),
            grammarPoints = listOf(
                "Verb 'To Be' (am / is / are)" to listOf(
                    Triple("She ___ a diligent student at Study Kai.", "is", listOf("am", "are", "be")),
                    Triple("They ___ from Tashkent and Istanbul.", "are", listOf("is", "am", "was")),
                    Triple("I ___ ready for today's English lesson.", "am", listOf("is", "are", "be")),
                    Triple("We ___ happy to learn new words together.", "are", listOf("is", "am", "has")),
                    Triple("My favorite color ___ emerald green.", "is", listOf("are", "am", "were"))
                ),
                "Present Simple Affirmative" to listOf(
                    Triple("He ___ breakfast at seven every morning.", "eats", listOf("eat", "eating", "to eat")),
                    Triple("Students ___ English in the morning class.", "study", listOf("studies", "studying", "is study")),
                    Triple("The library ___ at nine o'clock sharp.", "opens", listOf("open", "opening", "are open")),
                    Triple("My teacher ___ clearly on the whiteboard.", "writes", listOf("write", "writing", "wrote")),
                    Triple("We ___ to school by bus on weekdays.", "go", listOf("goes", "going", "is go"))
                ),
                "Articles (a / an / the)" to listOf(
                    Triple("I need ___ umbrella because it is raining.", "an", listOf("a", "two", "those")),
                    Triple("She bought ___ new notebook for math class.", "a", listOf("an", "some", "many")),
                    Triple("Look at ___ moon in the night sky!", "the", listOf("a", "an", "any")),
                    Triple("He eats ___ apple every afternoon.", "an", listOf("a", "the", "few")),
                    Triple("There is ___ clock on the classroom wall.", "a", listOf("an", "these", "much"))
                ),
                "Prepositions of Place (in / on / at)" to listOf(
                    Triple("The textbook is ___ the wooden desk.", "on", listOf("in", "under to", "into")),
                    Triple("Our English lessons start ___ 8:30 AM.", "at", listOf("on", "in", "by")),
                    Triple("The pencils are ___ my blue backpack.", "in", listOf("at", "between", "over")),
                    Triple("She lives ___ a quiet neighborhood.", "in", listOf("at", "on", "to")),
                    Triple("Wait for me ___ the school entrance.", "at", listOf("inside on", "between", "onto"))
                ),
                "Plural Nouns & Demonstratives" to listOf(
                    Triple("There are three ___ on the kitchen table.", "boxes", listOf("box", "boxs", "a box")),
                    Triple("___ books right here belong to Aziza.", "These", listOf("This", "That", "An")),
                    Triple("Two ___ are playing in the school yard.", "children", listOf("child", "childs", "childrens")),
                    Triple("Can you pass me ___ pen over there?", "that", listOf("those", "these", "them")),
                    Triple("How many ___ do you speak fluently?", "languages", listOf("language", "a language", "languaging"))
                ),
                "Can / Can't for Ability" to listOf(
                    Triple("My brother ___ swim very fast in the pool.", "can", listOf("cans", "to can", "is can")),
                    Triple("___ you spell your surname, please?", "Can", listOf("Are", "Does", "Have")),
                    Triple("Sorry, I ___ hear you clearly right now.", "can't", listOf("don't can", "not can", "isn't")),
                    Triple("Birds ___ fly high above the tall trees.", "can", listOf("is", "are", "does")),
                    Triple("She can ___ three songs on the piano.", "play", listOf("plays", "playing", "played"))
                )
            ),
            vocabSets = listOf(
                listOf(
                    Triple("morning", "the early part of the day", "We greet our classmates every ___."),
                    Triple("family", "parents and children living together", "My ___ eats dinner together at seven."),
                    Triple("notebook", "a book of blank or lined pages for writing", "Write the new vocabulary in your ___."),
                    Triple("breakfast", "the first meal of the day", "She drinks tea with toast for ___."),
                    Triple("teacher", "a person who helps students learn", "Our ___ explains every topic clearly.")
                ),
                listOf(
                    Triple("market", "a place where groceries and goods are sold", "They buy fresh apples at the local ___."),
                    Triple("kitchen", "the room where food is cooked", "Dad is preparing soup in the ___."),
                    Triple("weather", "the state of rain, sun, wind, or snow", "Check the ___ before walking to school."),
                    Triple("library", "a quiet building with books to read and borrow", "I borrow English storybooks from the ___."),
                    Triple("friend", "someone you know well and trust", "My best ___ helps me practice speaking.")
                )
            )
        ),
        LevelTemplate(
            cefr = "A2",
            count = 84,
            themes = listOf(
                "Past Experiences & Holidays", "Shopping & Prices", "Healthy Habits & Sports", "Transport & Travel Plans",
                "Hobbies & Free Time", "Comparing Cities & Nature", "At the Doctor's Clinic", "Technology in Daily Life",
                "Weekend Celebrations", "School Subjects & Schedules", "Cooking & Recipes", "Weather Forecasts",
                "Making Invitations", "Describing Appearance & Character"
            ),
            grammarPoints = listOf(
                "Past Simple (Regular & Irregular)" to listOf(
                    Triple("Yesterday we ___ a fascinating museum downtown.", "visited", listOf("visit", "are visiting", "will visit")),
                    Triple("She ___ her dictionary at home this morning.", "forgot", listOf("forgets", "forgetted", "forgetting")),
                    Triple("Did they ___ the train to Samarkand on time?", "catch", listOf("caught", "catches", "catching")),
                    Triple("He ___ very tired after the football match.", "felt", listOf("feel", "feeled", "feeling")),
                    Triple("We didn't ___ any homework over the holiday.", "have", listOf("had", "has", "having"))
                ),
                "Present Continuous for Now & Plans" to listOf(
                    Triple("Listen! Someone ___ the violin upstairs.", "is playing", listOf("plays", "played", "play")),
                    Triple("We ___ our grandparents next Saturday.", "are visiting", listOf("visit", "visited", "is visiting")),
                    Triple("Why ___ you wearing a heavy coat indoors?", "are", listOf("is", "do", "does")),
                    Triple("She ___ an email to her math tutor right now.", "is writing", listOf("writes", "wrote", "write")),
                    Triple("They ___ staying in a hotel near the park.", "are", listOf("is", "am", "was"))
                ),
                "Comparatives & Superlatives" to listOf(
                    Triple("This exercise is ___ than the previous one.", "easier", listOf("more easy", "easyer", "easiest")),
                    Triple("Mount Everest is the ___ mountain on Earth.", "highest", listOf("higher", "most high", "high")),
                    Triple("Reading books is ___ interesting than watching ads.", "more", listOf("most", "much", "many")),
                    Triple("Today's test was the ___ of the whole semester.", "best", listOf("goodest", "better", "more good")),
                    Triple("Trains are usually ___ comfortable than crowded buses.", "more", listOf("very", "most", "much"))
                ),
                "Countable & Uncountable Quantifiers" to listOf(
                    Triple("How ___ water should athletes drink daily?", "much", listOf("many", "few", "several")),
                    Triple("There are only a ___ seats left in the hall.", "few", listOf("little", "much", "less")),
                    Triple("Do you have ___ information about the bus route?", "any", listOf("many", "a few", "several")),
                    Triple("We need ___ sugar and three eggs for the cake.", "some", listOf("many", "few", "a")),
                    Triple("How ___ languages are taught at your campus?", "many", listOf("much", "little", "less"))
                ),
                "Future with 'Going To' & 'Will'" to listOf(
                    Triple("Look at those dark clouds! It ___ rain soon.", "is going to", listOf("will be", "goes", "going")),
                    Triple("I promise I ___ call you after class ends.", "will", listOf("am going", "was", "did")),
                    Triple("She ___ study medicine at university next year.", "is going to", listOf("will to", "go to", "are going to")),
                    Triple("They won't ___ late for the morning assembly.", "be", listOf("are", "being", "been")),
                    Triple("What are you ___ do during the winter break?", "going to", listOf("go to", "will", "went to"))
                )
            ),
            vocabSets = listOf(
                listOf(
                    Triple("luggage", "bags and suitcases carried while traveling", "Pack your ___ before heading to the station."),
                    Triple("healthy", "in good physical and mental condition", "Eating vegetables helps you stay ___."),
                    Triple("discount", "a reduction from the usual price", "Students get a twenty percent ___ on books."),
                    Triple("schedule", "a planned list of times for events or classes", "Check your weekly ___ for the math exam."),
                    Triple("invite", "to ask someone politely to come to an event", "Let's ___ the whole group to our celebration.")
                ),
                listOf(
                    Triple("recipe", "instructions for preparing a dish", "Follow this simple ___ to bake fresh bread."),
                    Triple("forecast", "a prediction about future weather", "The morning ___ says it will be sunny."),
                    Triple("ticket", "a pass that allows travel or entry", "Keep your train ___ until you exit the station."),
                    Triple("borrow", "to take and use something temporarily", "May I ___ your calculator for five minutes?"),
                    Triple("polite", "showing good manners and respect", "It is always ___ to say thank you.")
                )
            )
        ),
        LevelTemplate(
            cefr = "B1",
            count = 83,
            themes = listOf(
                "Work, Careers & Interviews", "Education & Study Strategies", "Digital Media & Communication",
                "Environment & Recycling", "Cultural Traditions & Festivals", "Health, Nutrition & Fitness",
                "Storytelling & Past Events", "Travel Adventures & Tourism", "Volunteering & Community",
                "Science & Everyday Inventions", "Consumer Habits & Budgeting", "City Living vs Countryside",
                "Problem Solving at School", "Movies, Books & Reviews"
            ),
            grammarPoints = listOf(
                "Present Perfect (for / since / already / yet)" to listOf(
                    Triple("She has lived in Ankara ___ five years.", "for", listOf("since", "during", "from")),
                    Triple("Have you finished your English essay ___?", "yet", listOf("already", "since", "ago")),
                    Triple("I have known my study partner ___ 2021.", "since", listOf("for", "during", "while")),
                    Triple("They have ___ completed twenty vocabulary tests.", "already", listOf("yet", "ever", "since")),
                    Triple("He has never ___ a live astronomy lecture before.", "attended", listOf("attend", "attending", "attends"))
                ),
                "First & Second Conditionals" to listOf(
                    Triple("If it rains tomorrow, we ___ stay indoors and read.", "will", listOf("would", "are", "did")),
                    Triple("If I had more free time, I ___ learn Japanese.", "would", listOf("will", "am", "have")),
                    Triple("You will improve faster if you ___ every day.", "practice", listOf("will practice", "practiced", "practicing")),
                    Triple("If she ___ the answer, she would tell the class.", "knew", listOf("knows", "will know", "know")),
                    Triple("Unless you hurry, you ___ miss the morning ferry.", "will", listOf("would", "won't", "had"))
                ),
                "Passive Voice (Present & Past)" to listOf(
                    Triple("English ___ spoken in many international schools.", "is", listOf("are", "was", "has")),
                    Triple("This historic bridge ___ built two centuries ago.", "was", listOf("is", "were", "has")),
                    Triple("All test scores ___ saved automatically in the app.", "are", listOf("is", "was", "be")),
                    Triple("The winning poem was ___ by a Grade 9 student.", "written", listOf("wrote", "write", "writing")),
                    Triple("Fresh bread is ___ in that bakery every morning.", "baked", listOf("bake", "baking", "bakes"))
                ),
                "Relative Clauses (who / which / where / whose)" to listOf(
                    Triple("The teacher ___ taught us geometry won an award.", "who", listOf("which", "where", "what")),
                    Triple("That is the quiet café ___ we review flashcards.", "where", listOf("which", "who", "whom")),
                    Triple("Here is the dictionary ___ includes audio examples.", "which", listOf("who", "where", "whose")),
                    Triple("Do you know the student ___ essay came first?", "whose", listOf("who", "which", "where")),
                    Triple("The course ___ started in September is very popular.", "that", listOf("who", "where", "whom"))
                ),
                "Modal Verbs of Obligation & Advice" to listOf(
                    Triple("You ___ bring a dictionary; the exam is closed-book.", "mustn't", listOf("don't have", "needn't to", "aren't")),
                    Triple("I think you ___ get eight hours of sleep before finals.", "should", listOf("must to", "ought", "have")),
                    Triple("Students ___ wear uniforms at this academy—it's optional.", "don't have to", listOf("mustn't", "shouldn't", "can't")),
                    Triple("Passengers ___ fasten their seatbelts during takeoff.", "must", listOf("should to", "can to", "might to")),
                    Triple("You look exhausted; you ___ take a short break.", "ought to", listOf("mustn't", "shouldn't to", "have"))
                )
            ),
            vocabSets = listOf(
                listOf(
                    Triple("achievement", "something accomplished through effort and skill", "Graduating with honors was a proud ___."),
                    Triple("environment", "the natural world of air, water, and land", "Planting trees helps protect our local ___."),
                    Triple("opportunity", "a favorable chance to learn or advance", "Studying abroad is an exciting ___ for growth."),
                    Triple("confident", "feeling sure about your abilities", "Regular speaking practice makes learners more ___."),
                    Triple("efficient", "achieving maximum results with minimal wasted effort", "Flashcards are an ___ way to review vocabulary.")
                ),
                listOf(
                    Triple("deadline", "the latest time by which a task must be finished", "Submit your science report before Friday's ___."),
                    Triple("volunteer", "someone who offers help without payment", "She works as a weekend ___ at the animal shelter."),
                    Triple("tradition", "a custom passed down through generations", "Hosting tea for guests is a warm cultural ___."),
                    Triple("interview", "a formal conversation to evaluate a candidate", "He prepared thoughtful answers for his job ___."),
                    Triple("improve", "to make or become better in quality or skill", "Daily reading will steadily ___ your writing.")
                )
            )
        ),
        LevelTemplate(
            cefr = "B2",
            count = 83,
            themes = listOf(
                "Global Economy & Entrepreneurship", "Scientific Research & Ethics", "Media Literacy & Journalism",
                "Urban Planning & Sustainability", "Psychology & Memory", "Higher Education & Scholarships",
                "Cross-Cultural Communication", "Renewable Energy Solutions", "Artificial Intelligence in Society",
                "Literature & Critical Reading", "Leadership & Teamwork", "Public Speaking & Debate",
                "Climate Systems & Biodiversity", "History of Ideas & Discovery"
            ),
            grammarPoints = listOf(
                "Third & Mixed Conditionals" to listOf(
                    Triple("If we had checked the forecast, we ___ brought umbrellas.", "would have", listOf("will have", "would", "had")),
                    Triple("She would be fluent now if she ___ practiced earlier.", "had", listOf("has", "would", "did")),
                    Triple("If they hadn't missed the flight, they ___ here now.", "would be", listOf("would have been", "will be", "were")),
                    Triple("I wouldn't have solved it unless you ___ explained the step.", "had", listOf("have", "would", "did")),
                    Triple("Had he studied harder, he ___ passed with distinction.", "would have", listOf("will have", "had", "has"))
                ),
                "Reported Speech & Reporting Verbs" to listOf(
                    Triple("She told me that she ___ completed the research paper.", "had", listOf("has", "will", "is")),
                    Triple("The mentor advised us ___ verify every primary source.", "to", listOf("that", "for", "of")),
                    Triple("He asked where the conference ___ take place.", "would", listOf("will", "is", "did")),
                    Triple("They denied ___ any confidential data from the lab.", "sharing", listOf("to share", "share", "shared")),
                    Triple("The coordinator reminded everyone ___ submit on time.", "to", listOf("for", "about", "that"))
                ),
                "Participle Clauses & Gerunds" to listOf(
                    Triple("___ finished the final chapter, Maya closed her book.", "Having", listOf("Have", "Being", "Had")),
                    Triple("I look forward to ___ your team in Tashkent soon.", "meeting", listOf("meet", "met", "be met")),
                    Triple("___ by clear diagrams, the concept felt intuitive.", "Supported", listOf("Supporting", "Support", "Supports")),
                    Triple("It is no use ___ over spilled milk; let's fix it.", "crying", listOf("to cry", "cry", "cried")),
                    Triple("___ through the data, she spotted an anomaly.", "Scanning", listOf("Scanned", "Scan", "To scanning"))
                ),
                "Modals of Deduction (must / might / can't have)" to listOf(
                    Triple("The lights are off; everyone ___ left the building already.", "must have", listOf("can't have", "shouldn't have", "need have")),
                    Triple("He ___ been at the library—I saw him in London!", "can't have", listOf("must have", "should have", "may")),
                    Triple("She looks thrilled; she ___ won the math scholarship.", "might have", listOf("can't have", "wouldn't have", "mustn't have")),
                    Triple("You ___ left your keys inside; let's check the desk.", "could have", listOf("can't have", "mustn't", "wouldn't")),
                    Triple("They ___ have practiced for months to perform so smoothly.", "must", listOf("can't", "needn't", "shouldn't"))
                ),
                "Future Continuous & Future Perfect" to listOf(
                    Triple("By next June, we ___ completed all 500 English topics.", "will have", listOf("will be", "are having", "have")),
                    Triple("At 9 PM tonight, I ___ studying calculus formulas.", "will be", listOf("will have", "am being", "have been")),
                    Triple("By the time you arrive, the seminar ___ begun.", "will have", listOf("will be", "is", "begins")),
                    Triple("This time next week, she ___ presenting her thesis.", "will be", listOf("will have", "had been", "is been")),
                    Triple("Within two years, solar output ___ doubled in the region.", "will have", listOf("will be", "was", "doubles"))
                )
            ),
            vocabSets = listOf(
                listOf(
                    Triple("sustainable", "able to be maintained without depleting resources", "Cities are investing in ___ public transit."),
                    Triple("hypothesis", "a testable explanation proposed for investigation", "The experiment confirmed our initial ___."),
                    Triple("persuasive", "convincing and effective in influencing thought", "She delivered a ___ argument during the debate."),
                    Triple("collaboration", "working jointly with others toward a shared goal", "Global progress thrives on cross-border ___."),
                    Triple("resilient", "able to recover quickly from challenges", "A ___ mindset turns setbacks into lessons.")
                ),
                listOf(
                    Triple("analyze", "to examine methodically in detail", "Scientists ___ survey trends before publishing."),
                    Triple("significant", "sufficiently great or important to be worthy of attention", "There was a ___ rise in test accuracy."),
                    Triple("innovation", "a new method, idea, or product", "Clean battery ___ is reshaping transportation."),
                    Triple("evaluate", "to judge the quality, value, or accuracy of something", "Critically ___ sources before citing them."),
                    Triple("coherent", "logical, clear, and consistent", "Organize your essay into a ___ structure.")
                )
            )
        ),
        LevelTemplate(
            cefr = "C1",
            count = 83,
            themes = listOf(
                "Academic Discourse & Rhetoric", "Macroeconomics & Monetary Policy", "Cognitive Biases & Decision Theory",
                "Bioethics & Genetics", "Geopolitics & Diplomacy", "Architectural Philosophy",
                "Linguistic Typology & Semantics", "Data Science & Algorithmic Fairness", "Constitutional Law & Governance",
                "Literary Modernism & Criticism", "Behavioral Economics", "Astrophysics & Cosmology",
                "Environmental Policy Negotiation", "Historiography & Primary Sources"
            ),
            grammarPoints = listOf(
                "Negative Inversion & Emphasis" to listOf(
                    Triple("Not only ___ the theorem hold, but it also generalizes cleanly.", "does", listOf("it does", "is", "has")),
                    Triple("Seldom ___ I encountered such a lucid academic exposition.", "have", listOf("I have", "did I have", "am")),
                    Triple("Hardly ___ the lecture begun when the projector dimmed.", "had", listOf("has", "did", "was")),
                    Triple("Under no circumstances ___ confidential drafts be leaked.", "should", listOf("they should", "mustn't", "will not")),
                    Triple("Little ___ they realize how pivotal the discovery would prove.", "did", listOf("they did", "had", "were"))
                ),
                "Cleft Sentences & Fronting" to listOf(
                    Triple("It was the final paragraph ___ crystallized her thesis.", "that", listOf("what", "whom", "where")),
                    Triple("What researchers need most ___ reliable longitudinal data.", "is", listOf("are", "were", "being")),
                    Triple("___ surprised the panel was his effortless command of idiom.", "What", listOf("That", "Which", "Whom")),
                    Triple("All I want to emphasize ___ the urgency of peer review.", "is", listOf("are", "be", "were")),
                    Triple("It is precisely in ambiguity ___ diplomatic skill shines.", "that", listOf("what", "which", "whom"))
                ),
                "Subjunctive & Unreal Past" to listOf(
                    Triple("The committee insisted that the report ___ revised promptly.", "be", listOf("is", "was", "will be")),
                    Triple("It is high time policymakers ___ long-term infrastructure.", "prioritized", listOf("prioritize", "will prioritize", "prioritizing")),
                    Triple("I would rather you ___ disclose the figures prematurely.", "didn't", listOf("don't", "won't", "not")),
                    Triple("Suppose the catalyst ___ absent; would the reaction proceed?", "were", listOf("is", "will be", "has")),
                    Triple("Essential though speed ___ be, rigor cannot be sacrificed.", "may", listOf("must", "is", "was"))
                ),
                "Advanced Concession & Discourse Markers" to listOf(
                    Triple("___ the complexity of the proof, the lecture remained accessible.", "Notwithstanding", listOf("Although", "Even", "Despite of")),
                    Triple("Much ___ I admire his style, I dispute his premise.", "as", listOf("though that", "despite", "however")),
                    Triple("She persisted in her inquiry, ___ the dwindling funding.", "irrespective of", listOf("regardless", "even though", "in spite")),
                    Triple("The hypothesis is intriguing; ___, empirical backing is sparse.", "nonetheless", listOf("therefore", "consequently", "moreover")),
                    Triple("___ difficult the problem looked, they cracked it systematically.", "However", listOf("Whatever", "Despite", "Although"))
                )
            ),
            vocabSets = listOf(
                listOf(
                    Triple("ubiquitous", "present, appearing, or found everywhere", "Smartphones have become ___ across modern campuses."),
                    Triple("ambiguous", "open to more than one interpretation", "Avoid ___ wording in legal contracts."),
                    Triple("corroborate", "to confirm or give support to a theory or finding", "Independent trials helped ___ the initial study."),
                    Triple("pragmatic", "dealing with things sensibly and realistically", "They adopted a ___ approach to budget limits."),
                    Triple("mitigate", "to make something less severe or painful", "Coastal wetlands help ___ storm surge damage.")
                ),
                listOf(
                    Triple("paradigm", "a typical example, pattern, or model of something", "Quantum computing challenges the classical ___."),
                    Triple("discrepancy", "an illogical or surprising lack of compatibility", "Auditors investigated the ___ between the ledgers."),
                    Triple("eloquent", "fluent or persuasive in speaking or writing", "Her ___ keynote captivated the auditorium."),
                    Triple("ephemeral", "lasting for a very short time", "Fame in digital trends is often ___."),
                    Triple("substantiate", "to provide evidence to support the truth of a claim", "Cite peer-reviewed sources to ___ your thesis.")
                )
            )
        ),
        LevelTemplate(
            cefr = "C2",
            count = 83,
            themes = listOf(
                "Epistemology & Philosophy of Mind", "Stylistic Nuance & Register Mastery", "International Jurisprudence",
                "Poetics, Metaphor & Irony", "Complex Systems & Emergence", "Diplomatic Pragmatics",
                "Etymology & Semantic Shift", "Aesthetics & Art Criticism", "Game Theory & Strategic Equilibrium",
                "Socio-Linguistics & Identity", "Advanced Syntactic Compression", "Rhetorical Tropes & Persuasion",
                "Philosophy of Science", "Mastery Synthesis & Colloquial Idiom"
            ),
            grammarPoints = listOf(
                "Ellipsis, Substitution & Nominalization" to listOf(
                    Triple("Had the envoy anticipated friction, she would have acted—and ___.", "decisively", listOf("decisive", "decision", "deciding")),
                    Triple("The council approved the measure, ___ with misgivings.", "albeit", listOf("despite", "even", "whereas")),
                    Triple("His ___ of the anomaly spared the lab months of toil.", "elucidation", listOf("elucidate", "elucidating", "elucidated")),
                    Triple("One model relies on priors; the other dispenses with ___ altogether.", "them", listOf("those of", "its", "oneself")),
                    Triple("Rarely, if ___, has a debut monograph stirred such debate.", "ever", listOf("never", "always", "seldom"))
                ),
                "Inversion with 'Were / Should / Had' & Archaic Register" to listOf(
                    Triple("___ the treaty to collapse, regional trade would stagnate.", "Were", listOf("Was", "If were", "Should be")),
                    Triple("___ any discrepancy arise, consult the appendix immediately.", "Should", listOf("If should", "Would", "Were")),
                    Triple("Be that as it ___, the empirical record remains intact.", "may", listOf("can", "might be", "will")),
                    Triple("So ___ was the prose that every clause repaid re-reading.", "taut", listOf("tautly", "tautness", "tauter")),
                    Triple("Far be it from me ___ question your methodology.", "to", listOf("from", "of", "for"))
                ),
                "Subtle Nuance, Collocation & Register" to listOf(
                    Triple("Her critique was ___ yet undeniably fair.", "incisive", listOf("incised", "incising", "incision")),
                    Triple("The treaty's wording left ___ room for unilateral action.", "scant", listOf("scarce of", "few", "narrowly")),
                    Triple("He ___ any suggestion that the data had been curated.", "repudiated", listOf("declined to", "refused for", "negated of")),
                    Triple("An ___ observer might mistake brevity for shallowness.", "uninitiated", listOf("uninitial", "ininitiated", "non-initiate")),
                    Triple("The symposium ___ a fierce yet cordial exchange of views.", "engendered", listOf("manufactured", "fabricated", "concocted"))
                )
            ),
            vocabSets = listOf(
                listOf(
                    Triple("perspicacious", "having a ready insight into and understanding of things", "A ___ critic notices the subtlest structural flaw."),
                    Triple("obfuscate", "to render obscure, unclear, or unintelligible", "Jargon should clarify ideas, never ___ them."),
                    Triple("equivocal", "open to more than one interpretation; ambiguous", "The spokesperson gave an ___ reply to avoid committing."),
                    Triple("parsimonious", "unwilling to spend resources or using the simplest explanation", "Scientists favor a ___ model with fewer assumptions."),
                    Triple("quintessential", "representing the most perfect or typical example", "That sonnet is the ___ work of its era.")
                ),
                listOf(
                    Triple("recalcitrant", "having an obstinately uncooperative attitude", "Negotiators struggled to persuade the ___ faction."),
                    Triple("sycophant", "a person who acts obsequiously toward someone important", "Leaders need candid advisors, not a ___."),
                    Triple("vicissitude", "a change of circumstances or fortune, typically unwelcome", "Stoicism teaches calm amid life's inevitable ___s."),
                    Triple("inexorable", "impossible to stop or prevent", "Time marches forward with ___ steadiness."),
                    Triple("laconic", "using very few words concisely", "Her ___ summary captured the entire debate in two lines.")
                )
            )
        )
    )

    val allTopics: List<EnglishTopic> by lazy {
        val list = ArrayList<EnglishTopic>(500)
        var nextId = 1
        for (tpl in templates) {
            for (i in 0 until tpl.count) {
                val theme = tpl.themes[i % tpl.themes.size]
                val cycle = (i / tpl.themes.size) + 1
                val grammar = tpl.grammarPoints[i % tpl.grammarPoints.size]
                val vocab = tpl.vocabSets[i % tpl.vocabSets.size]
                val title = if (cycle == 1) theme else "$theme — Part $cycle"
                val words = vocab.map { it.first }
                val firstVocab = vocab.first()
                list.add(
                    EnglishTopic(
                        id = nextId++,
                        cefr = tpl.cefr,
                        title = title,
                        grammarFocus = grammar.first,
                        keyWords = words,
                        lessonSummary = "${tpl.cefr} Topic #${nextId - 1}: Master '${grammar.first}' within the context of $theme, practicing high-frequency vocabulary (${words.joinToString(", ")}).",
                        sampleSentence = firstVocab.third.replace("___", firstVocab.first)
                    )
                )
            }
        }
        list
    }

    fun getTopicsByCefr(cefr: String): List<EnglishTopic> =
        if (cefr == "ALL") allTopics else allTopics.filter { it.cefr == cefr }

    /**
     * Generates exactly 30 topic-grounded multiple-choice questions for a given topic
     * with prompts and explanations in the selected Settings language.
     */
    fun generateTestQuestions(
        topic: EnglishTopic,
        language: AppLanguage = AppLanguage.ENGLISH
    ): List<QuizQuestion> {
        val tpl = templates.first { it.cefr == topic.cefr }
        val topicIndexInLevel = (topic.id - 1)
        val primaryGrammar = tpl.grammarPoints[topicIndexInLevel % tpl.grammarPoints.size]
        val secondaryGrammar = tpl.grammarPoints[(topicIndexInLevel + 1) % tpl.grammarPoints.size]
        val vocabSet = tpl.vocabSets[topicIndexInLevel % tpl.vocabSets.size]
        val altVocabSet = tpl.vocabSets[(topicIndexInLevel + 1) % tpl.vocabSets.size]
        val combinedVocab = vocabSet + altVocabSet

        val rng = Random(topic.id * 7919L + 104729L)
        val questions = ArrayList<QuizQuestion>(30)

        for (qIdx in 0 until 30) {
            val questionType = qIdx % 5
            val q = when (questionType) {
                0 -> {
                    val bank = primaryGrammar.second
                    val item = bank[(qIdx / 5) % bank.size]
                    val prompt = when (language) {
                        AppLanguage.UZBEK -> "[${topic.cefr} • ${topic.title}] '${primaryGrammar.first}' qoidasi bo'yicha to'g'ri shaklni tanlang:\n\"${item.first}\""
                        AppLanguage.TURKISH -> "[${topic.cefr} • ${topic.title}] '${primaryGrammar.first}' kuralına göre doğru biçimi seçin:\n\"${item.first}\""
                        AppLanguage.RUSSIAN -> "[${topic.cefr} • ${topic.title}] Выберите правильную форму по теме '${primaryGrammar.first}':\n\"${item.first}\""
                        AppLanguage.ENGLISH -> "[${topic.cefr} • ${topic.title}] Choose the correct form for '${primaryGrammar.first}':\n\"${item.first}\""
                    }
                    val explanation = when (language) {
                        AppLanguage.UZBEK -> "'${topic.title}' mavzusi '${primaryGrammar.first}' qoidasini o'rgatadi. To'g'ri javob: '${item.second}'."
                        AppLanguage.TURKISH -> "'${topic.title}' konusu '${primaryGrammar.first}' yapısını kapsar. Doğru cevap: '${item.second}'."
                        AppLanguage.RUSSIAN -> "Тема '${topic.title}' посвящена правилу '${primaryGrammar.first}'. Правильный ответ: '${item.second}'."
                        AppLanguage.ENGLISH -> "Topic '${topic.title}' focuses on ${primaryGrammar.first}. Correct choice: '${item.second}'."
                    }
                    buildShuffledMcq(
                        id = qIdx + 1,
                        prompt = prompt,
                        correct = item.second,
                        distractors = item.third,
                        explanation = explanation,
                        language = language,
                        rng = rng
                    )
                }
                1 -> {
                    val target = combinedVocab[(qIdx / 5) % combinedVocab.size]
                    val distractors = combinedVocab.filter { it.first != target.first }.map { it.first }.distinct().take(3)
                    val prompt = when (language) {
                        AppLanguage.UZBEK -> "[${topic.cefr} Lug'at • ${topic.title}] Quyidagi ma'noga mos inglizcha so'zni toping:\n\"${target.second}\""
                        AppLanguage.TURKISH -> "[${topic.cefr} Kelime • ${topic.title}] Aşağıdaki anlama uyan İngilizce kelime hangisidir?\n\"${target.second}\""
                        AppLanguage.RUSSIAN -> "[${topic.cefr} Словарь • ${topic.title}] Какое английское слово соответствует этому значению?\n\"${target.second}\""
                        AppLanguage.ENGLISH -> "[${topic.cefr} Vocabulary • ${topic.title}] Which word matches this meaning?\n\"${target.second}\""
                    }
                    val sample = target.third.replace("___", target.first)
                    val explanation = when (language) {
                        AppLanguage.UZBEK -> "'${target.first}' so'zining ma'nosi: ${target.second}. Misol: $sample"
                        AppLanguage.TURKISH -> "'${target.first}' kelimesinin anlamı: ${target.second}. Örnek: $sample"
                        AppLanguage.RUSSIAN -> "Слово '${target.first}' означает: ${target.second}. Пример: $sample"
                        AppLanguage.ENGLISH -> "'${target.first}' means: ${target.second}. Example: $sample"
                    }
                    buildShuffledMcq(
                        id = qIdx + 1,
                        prompt = prompt,
                        correct = target.first,
                        distractors = distractors,
                        explanation = explanation,
                        language = language,
                        rng = rng
                    )
                }
                2 -> {
                    val target = combinedVocab[(qIdx / 5 + 1) % combinedVocab.size]
                    val distractors = combinedVocab.filter { it.first != target.first }.map { it.first }.distinct().take(3)
                    val prompt = when (language) {
                        AppLanguage.UZBEK -> "[Kontekst • ${topic.title}] Gapni mazmunan to'g'ri to'ldiring:\n\"${target.third}\""
                        AppLanguage.TURKISH -> "[Bağlam • ${topic.title}] Cümleyi anlamına uygun olarak tamamlayın:\n\"${target.third}\""
                        AppLanguage.RUSSIAN -> "[Контекст • ${topic.title}] Дополните предложение подходящим словом:\n\"${target.third}\""
                        AppLanguage.ENGLISH -> "[Context Cloze • ${topic.title}] Complete the sentence naturally:\n\"${target.third}\""
                    }
                    val explanation = when (language) {
                        AppLanguage.UZBEK -> "Ushbu gapda '${target.first}' (${target.second}) grammatik va ma'no jihatdan mos keladi."
                        AppLanguage.TURKISH -> "Bu cümlede '${target.first}' (${target.second}) anlam ve dilbilgisi açısından doğrudur."
                        AppLanguage.RUSSIAN -> "В данном контексте слово '${target.first}' (${target.second}) подходит по смыслу и грамматике."
                        AppLanguage.ENGLISH -> "In this context, '${target.first}' (${target.second}) fits grammatically and semantically."
                    }
                    buildShuffledMcq(
                        id = qIdx + 1,
                        prompt = prompt,
                        correct = target.first,
                        distractors = distractors,
                        explanation = explanation,
                        language = language,
                        rng = rng
                    )
                }
                3 -> {
                    val bank = secondaryGrammar.second
                    val item = bank[(qIdx / 5) % bank.size]
                    val prompt = when (language) {
                        AppLanguage.UZBEK -> "[Grammatika takrori • ${secondaryGrammar.first}] To'g'ri javobni tanlang:\n\"${item.first}\""
                        AppLanguage.TURKISH -> "[Dilbilgisi Tekrarı • ${secondaryGrammar.first}] Doğru seçeneği işaretleyin:\n\"${item.first}\""
                        AppLanguage.RUSSIAN -> "[Повтор грамматики • ${secondaryGrammar.first}] Выберите точный вариант:\n\"${item.first}\""
                        AppLanguage.ENGLISH -> "[Grammar Review • ${secondaryGrammar.first}] Select the accurate completion:\n\"${item.first}\""
                    }
                    val explanation = when (language) {
                        AppLanguage.UZBEK -> "'${secondaryGrammar.first}' qoidasiga ko'ra '${item.second}' gapni to'g'ri to'ldiradi."
                        AppLanguage.TURKISH -> "'${secondaryGrammar.first}' kuralına göre cümleyi '${item.second}' doğru tamamlar."
                        AppLanguage.RUSSIAN -> "По правилу '${secondaryGrammar.first}' предложение верно дополняет '${item.second}'."
                        AppLanguage.ENGLISH -> "Under ${secondaryGrammar.first}, '${item.second}' completes the sentence accurately."
                    }
                    buildShuffledMcq(
                        id = qIdx + 1,
                        prompt = prompt,
                        correct = item.second,
                        distractors = item.third,
                        explanation = explanation,
                        language = language,
                        rng = rng
                    )
                }
                else -> {
                    val target = combinedVocab[(qIdx / 5 + 2) % combinedVocab.size]
                    val distractors = combinedVocab.filter { it.second != target.second }.map { it.second }.distinct().take(3)
                    val prompt = when (language) {
                        AppLanguage.UZBEK -> "[Ma'no tekshiruvi • ${topic.title}] \"${target.first}\" so'zining to'g'ri ta'rifi qaysi?"
                        AppLanguage.TURKISH -> "[Anlam Kontrolü • ${topic.title}] \"${target.first}\" kelimesinin en doğru tanımı hangisidir?"
                        AppLanguage.RUSSIAN -> "[Проверка значения • ${topic.title}] Какое определение лучше всего подходит для \"${target.first}\"?"
                        AppLanguage.ENGLISH -> "[Meaning Check • ${topic.title}] What is the best definition of \"${target.first}\"?"
                    }
                    val sample = target.third.replace("___", target.first)
                    val explanation = when (language) {
                        AppLanguage.UZBEK -> "'${target.first}' — '${target.second}'. Misol: $sample"
                        AppLanguage.TURKISH -> "'${target.first}' anlamı: '${target.second}'. Örnek: $sample"
                        AppLanguage.RUSSIAN -> "'${target.first}' означает '${target.second}'. Пример: $sample"
                        AppLanguage.ENGLISH -> "'${target.first}' means '${target.second}'. Sample: $sample"
                    }
                    buildShuffledMcq(
                        id = qIdx + 1,
                        prompt = prompt,
                        correct = target.second,
                        distractors = distractors,
                        explanation = explanation,
                        language = language,
                        rng = rng
                    )
                }
            }
            questions.add(q)
        }
        return questions
    }

    private fun buildShuffledMcq(
        id: Int,
        prompt: String,
        correct: String,
        distractors: List<String>,
        explanation: String,
        language: AppLanguage,
        rng: Random
    ): QuizQuestion {
        val pool = (listOf(correct) + distractors.filter { it != correct }.take(3)).distinct().toMutableList()
        while (pool.size < 4) {
            val fallback = when (language) {
                AppLanguage.UZBEK -> "Boshqa javob (${pool.size})"
                AppLanguage.TURKISH -> "Hiçbiri (${pool.size})"
                AppLanguage.RUSSIAN -> "Другой вариант (${pool.size})"
                AppLanguage.ENGLISH -> "None of the above (${pool.size})"
            }
            pool.add(fallback)
        }
        pool.shuffle(rng)
        val correctIdx = pool.indexOf(correct)
        return QuizQuestion(
            id = id,
            prompt = prompt,
            options = pool,
            correctIndex = correctIdx,
            explanation = explanation
        )
    }
}
