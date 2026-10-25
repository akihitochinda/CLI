# AI-Generated Code Refrection — Sentiment Analyzer (option2)

---

## 1. Where the AI Struggled / Needed Correction

    Design issue and testing:
    • Design patterns not applied proactively
    AI produced a working but flat design until explicitly told to use Factory Method and Strategy. It would have put mode-selection logic directly in main() with no abstraction.
    • OO structures not suggested up front
    AI didn't propose interfaces (AnalyzerMode, ScoringStrategy) or factory classes on its own; these had to be requested. Without prompting, concrete classes were wired together directly.
    • Testing assumptions about environment
    AI assumed a uniform runtime environment — e.g., `System.lineSeparator()` usage was inconsistent, and whitespace/punctuation stripping in `tokenizeReview` was not robust across all edge cases without correction.

## 2. Critical Evaluation

    General Design Quality:
    • Responsibilities are distributed across focused classes (ReportGenerator, etc.), and the entry point sentimentAnalyzer.main() is kept simple. However, the initial design required significant human guidance to reach this quality level. ReportGenerator is slightly large (scoring + CSV writing + statistics printing), while the inner ReviewResult class helps contain that.

    Object-Oriented Principles:
    • Encapsulation — fields are private throughout; state is accessed only via getters.
    • Abstraction — interfaces define clean contracts that hide implementation details from callers.
    • Inheritance / Polymorphism — each Mode both implement single mode interface; ScoreGenerator implements scoring strategy. 
    • Cohesion — each class has one clear job.

    SOLID principles:
    • Single responsibility principle - each class has one reason to change
    • Open/Closed principle - New modes can be added by creating a new AnalyzerMode implementation and one branch in AnalyzerModeFactory — no existing class needs editing.
    • Liskov substitution principle - Both Modes honour the full contract of AnalyzerMode#run(), objects of a superclass can be replaceable with objects of its subclasses without breaking the application.
    • Interface Segregation principle - AnalyzerMode exposes only run(); ScoringStrategy exposes only score(). Neither interface forces implementors to provide unrelated methods.

    Design patterns:
    • Factory method - creator, product interface, concrete products
    • Strategy pattern - strategy interface, concrete strategy, intended context

## 3. SOLID principle violation

    Dependency Inversion Principle — "High-level modules should not depend on low-level modules; both should depend on abstractions.":
    • Both InteractiveMode and ReportGenerator are high-level classes that should depend on the ScoringStrategy abstraction — that is exactly the interface defined for this purpose. Instead, they new the concrete class SentimentScoreGenerator directly.

    // InteractiveMode.java
    SentimentScoreGenerator result = new SentimentScoreGenerator(lexicon, reviewText);

    // ReportGenerator.java
    SentimentScoreGenerator generator = new SentimentScoreGenerator(sentimentLexicon, reviewText);

    If the scoring algorithm is swapped (e.g., for an ML-based scorer), both classes must be edited. The fix would be to inject a ScoringStrategy through the constructor of each class, so the dependency flows toward the interface rather than the concrete implementation.