# Check-In 1
### Discussions
- Discussed with Zack about filling which criteria we would want to focus on
- Discussed using third party libraries to support our application
- Discussed the difficulty of static checking
- Assigned roles for visualization components (John and Daniel will do front-end, everyone else wants to do backend)
### Candidates for Project
- An application that tracks the control flow of a program 
  - Check how many times we enter a specific branch
  - How much memory is allocated for a specific program
  - Create a control flow diagram for how many times a methods is called and the duration they are called
### Action Items
- Focus on an idea and decide as a group what specific use case we want to choose
- Meet on Sunday to discuss potential ideas for the next TA meeting

# Check-In 2
### Project Description (still may change)
Bar chart race: Visualization of total time taken for each method, over time. This would show how time is spent in the program at different points in that program’s execution (beginning, middle, end), in a fun and creative way.

**Features**:
- Ability to sketch the call graph
- ability to view the exection

**New additions since last discussion**:
- Adding arguments used to call each function
- adding line number of file name from where the function is called

**Planned Division of Responsibilities**:
- Visualization: Khalid and Jordan
- Other areas: John, Daniel, Radman

**Progress so Far**:

We are still discussing the project and hoping to finalize it before the next meeting. The reason is that our previous ideas doesn't seem to meet the requirements after our TA discussion and OH discussion on Mar 8th with Alex. But we have some libraries and ideas in mind to finalize the decisions and get started!

**TODO Roadmap / Next Steps**:
- Develop our existing project idea to make it feasible in order to meet the requirements based on our discussions or come up with a new plan by the end of the week Mar 11th
- Explore tools/frameworks needed for the project and create a project plan along with timelines
Proposed features of our existing program analysis project:
- Bar graph, where each bar represents a method call
- bars representing methods in execution will be increasing in length as time proceeds forward in an animation format
- methods are color coded to show at which level and by which parent each method is called (ability to detect and understand the call graph)
- Attach method arguments to each bar/method to see which inputs may be causing performance issues
- attach line numner to each bar/method to better identify the path of execution and detect where the source of the performance issue is

# Check-In 3

### New Changes:
After lots of discussion on extending our previous idea as well as discussing new ideas with Alex and Zack, we have come to a conclusion and decided to switch to a static program analysis project that approximates division by 0.

### First user study:

Our first user study went very well; after explaining our project idea and scope to a Java user, the participant believed that this would help him catch these small tiny mathematical bugs that he would usually overlook. This will help him identify some unexpected runtime errors in advance before having to run the program. The feedbacks were moslty concerned about how precise the analyser would be which gave us more insights, but the overall project idea was useful and interesting to get started with.

### Mockup:
- The main varState structure: Stack<Map<Local,State>>
- Stack hold Maps of live scopes where each Map element hold key-val pairs of all local variables within the given scope. When we exit a scop, we pop the last scope to remove out of scope variables:
State would be
  - Unknown = value of variable is unknown
  - DefinitelyZero = value of variable is definitely zero
  - DefinitelyNotZero = value of variable is definitely not zero
- Math operations:
  - For addition and multiplication, we can use the states to determine the result
  - Ex. DefinitelyNotZero * DefinitelyZero = DefinitelyZero

### Project Plan
- Use a library to convert Java source code to an AST representation (done by Khalid by March 20)
- Implement a visitor class (details still under discussion)
- Implement backend and track states (Unknown, DefinitelyZero, DefinitelyNotZero)

# Check-In 4

### Status of Implementation
- JavaParser code implemented to get AST
- basic project setup started on abstract varState, visitor, tests

### Timeline
- Final user study to be done by the next check-in, in similar fashion to the previous one. If we are far enough into implementation we can show the user the flow
#### by Check-In 5
- Finish visitor happy path with conditionals
    - includes abstract varState tracking, with stack for scope changes
    - should be well tested
- Begin checks for loops and recursion
- check for edge cases and bugs
#### After Check-In 5
- finish loops and recursion
- finish the small bugs and testing
- edit and submit video


# Check-In 5
#### Final User Study
The meat of the project has already been established and there is a good understanding for us on what the requirements are and what we need to accomplish. Therefore, the focus of the user study mainly revolved around interactability with the project and whether the analysis results is useful and intuitive. The participant mentioned that putting the source code in the dedicated folder is very simple and intuitive. In terms of getting the results, we have received feedback on how to better format the output of our progam for an easier readability. Other than that, our participant is very happy about what our program analysis is achieving. Also, it is nice that we do not have any false positive results.
#### Plans for  final video
We will follow the same video struture as last time which consists of the intro (what the program achieves, use case and user base), user studies and evolution of the program, challenges faced, and final results which would be a demo of a program that contains possibility of division by 0 cases.
#### Planned timeline
- Finish naive implementation by Mar 31
- Finish full implementation by Apr 3
- debug and make the video by the project deadline
#### Progress against the timeline planned
Although not perfectly matched with timeline, we are happy with where we are and have confidence that we will finish and produce a high quality project and project video in time.


