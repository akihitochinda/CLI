Bonus part A2: Prepare a half-page write-up, explaining how you are incorporating command pattern into this assignment. 

   Because we know commands that we need to implement, we made it intuitive and extensible by using command pattern for interaction and separating by its purposes. This structure makes the CLI interaction intuitive, the parser maps user input directly to a command object, then command.execute() runs the business logic. Additional interface like post strategy or feature decorator can be easily embedded into the program.

   1. Each user action becomes a command
   Defined a shared Command interface and one concrete command class for each interaction:
   CreateCommand → create posts, asks for content, author, type, tags
   AddFeatureCommand → add features like/share/comment, adds decorators
   StrategyCommand → apply visibility strategies
   SaveTemplateCommand → save post templates, stores a cloned post
   CloneCommand → clone saved templates, restores a template
   PublishCommand, LikeCommand, ShareCommand → post actions

   2. Separation of concerns
   The command pattern separates and new commands can be added without changing the existing command workflow:
   input parsing (Main)
   command selection (parseCommand)
   command execution (Command.execute())
   post logic (SocialPostManager and SocialPost)

   3. How other patterns fit in
   Builder → creating a post
   Strategy → SocialPost can adopt different visibility rules via Strategy, creating the right strategy object and applies it to the post
   Decorator → Features like/share/comment are implemented as decorators, wrapping the current post component without changing SocialPost
   Observer → SocialPost keeps observers and calls notifyObservers() on events like publish/like/share, separating notifications from core post logic and staying decoupled from command handling


Bonus part B2: Prepare a one-page write-up, explaining your graphical user interface (how it works, and what it shows).

   1. How it works
   It creates command objects and executes them using Social Post Manager as backend:
   AddFeatureCommand(manager, "like")
   StrategyCommand(manager, "public")
   SaveTemplateCommand(manager, template)
   CloneCommand(manager, template)
   PublishCommand(manager)
   LikeCommand(manager)
   ShareCommand(manager)
   GUI reads the fields and builds the post when of creating post with:
   new SocialPost
    .PostBuilder()
    .setContent(...)
    .setAuthor(...)
    .setType(...)
    .addTag(...)
    .build()

   2. What it shows
   Input controls (text fields and drop-down)
   Buttons for actions (create posts, add like/share/comment features, apply visibility strategies, save post templates, clone saved templates, and Publish, Like and Share buttons)
   Output area (the same console messages as the current post system)