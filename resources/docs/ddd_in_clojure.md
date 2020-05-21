
Domain-driven Design in Clojure

This text delivers a quick and practical introduction of DDD and its use in a FP language.

1) Understanding the Domain

Surely in some point of your life you have done that kind of big project in your house, like tiling the bathroom or finally building a tree-house for your kids, and even when at the end of such projects a cloud of proud surrounds you when you're putting the tools back in the garage, you know that the final result could have being much better if you could have known all that you know now, after the task is finished. But hey! you learned a new Domain! you not mastered yet... but you learn it.

What is a Domain? A Domain is the corpus of knowledge that is necessary to operate effectively in a particular sphere of life. If your whole life has passed in the city and suddenly you want to be a farmer, you will need to learn a big Domain of stuff if you want to own a successful farm. So, Domain is a type of new concept but they are existed since the ancient Egyptians or the Mayans. As human beings, all the domains that we learn through our lives are acquired: someone taught them to us; some Domains, like "How to drive a vehicle properly", "10 Guitar Chords For Beginners" or "How to make Pizza" are relatively easy and can be learnt in few days, others are so big, complex and important that we invented a particular institution to teach them: the school, and since our knowledge has become wider and harder to master, basic school was not enough and then we invented the College. Doctors, psychologists and lawyers must spend years in College learning a particular Domain before they can get a license and exercise their professions.

So, Domains are existed since many centuries ago, but since the 60's we have a new tool to make them easier: the computer. For centuries, lawyers or public accountants could attend only a certain number of customers before being overwhelmed by the amount of job that implies to take too many clients. But now they can attend much more people and earn much more money because the computers do automatically many things that were made manually before. If I'm a lawyer I can see all the documents, status and reviews of a commercial trial that my firm is involved, if I'm an accountant I can collate, put and withdraw money without the need to leave the office. In the 19th century the first industrial revolution automatised the physical activity, tireless steel and steam replaced the feeble biological muscles, now with computers we can go further and automatise hard working symbolic tasks through software. Doing so, we can relieve a part of the our brains of unnecessary duties, and that improves the world in several ways. The combination of "Modern Domains + Computers" has produced a seemly endless explosion of wealth and new products, but also it created a particular problem: how software is make and how it "should" be make?

Developers typically don't code by the sake of the code itself, they code software in order to reach a particular goal, and that goal almost always lies in a field that they don't master. When developing a new app for a bank, or a new system for a travel agency or for a Health Record Enterprise, programmers must be informed enough to understand what they are doing, but at the same time they must give up the attempt to encompass the whole Domain. In other words, if developers want to get the objective they must work under a Domain that they vastly ignore. How that is possible? In fact this is a curious situation, lawyers usually don't work under doctors' guides and chemists don't must think as a psychiatrist to perform well in their activities, but software developers are normally in "foreign lands" at the moment of fulfil their duties.

So, when we talk about software development we talk about two clearly identified groups of people: those who master the Software Development process and people that master the Domain involved with that particular project (we could say, a group of doctors that want a system for their laboratory). In a perfect and unrealistic world, the ideal situation would be that the software group would go to the university to study medicine and vice-versa, the health-care group would be sent to the college to study a computer related field for few years. If that would be the case, the amount and good level of communication would be amazing for the project because both groups have a "Shared Mental Model": when a doctor mention a weird Greek word for a disease or a type of surgical intervention, the developers will understand it thoroughly and when the programmers talk about the micro-services or non-SQL the doctors will get it without the need of a large, tortuous and semi-effective explanation. Sadly, that ideal scenario will likely never be the case. Instead we need to prepare the field to facilitate the communication and understanding of those two groups in order to achieve the maximum level of understanding and exchange of information. As you will learn, the first steps in the DDD process, involve a copious and vigorous interchange of concepts and information between those two groups.

This steps already existed before the invention of DDD, but they did in a implicit, unprofessional and many times, unaware way. To the process through which a person acquires a Domain is called "Transfer Domain", and for many years it was made under an amateur milieu; one or several programmers were hired and then they tried to understand the task asking randomly questions and having informal talks with the Domain experts in the hall of the office or in the parking lot, that almost always leads to a lot of inaccuracies and mistakes, and all that costs time and money. DDD makes clear that the settled communication in a professional scenario with assigned employees and a discussion schedule is very important if a company wants to end with a good return for its investment.

If you think about it, the phenomenon of "Transfer Domain" still happens, and most of the time it does in a chaotic and non-professional way. When a company hires a new developer for an existing product such developer must transit for the process, but many times the company assigned him/her a task that requires a high understanding of the Domain just few weeks after the person was hired. Companies should be aware of that process and they should have support material to make it shorter and less hard. Mastering a Domain is not dependant of your seniority as developer, is just a process that all programmers must pass before be confident altering an inherited code.

Then the first step in DDD is to have a room with the two groups. Is always good that someone make the presentations, shows the written objectives and goals of the project, the schedule of the meetings for the next days and also set a open and friendly atmosphere. The discussion inside the work-group is in some ways similar to a "Brain Storm" session, but it has an important difference: all the participants in the discussion are aware that some members of the groups could don't know some concepts, and so they must explain their opinions in a broader and pedagogical way. In DDD the session that has a name "Event Stormig", this kind of session include a broader groups of people, not just the Domain expertd and the software deleoperes, anyone who has some inetrest in the orject shusl be nvited to particate to ask and answer questions. One advice when doing this large work group is to upholster the walls of the room with flip-chart sheets. Things that are being discussed should be written and erased from the white board but the conclusions must be written in the walls. Is also a good idea to have post-it notes with different colours to signal pending features. At the end of the scheduled meetings a clear set of features and their priority must emerge, but overall we must have a "Shared Model", a blue print that all the participants inside the project knows and agrees to follow. At the end, DDD generates explicit and public knowledge.

1) Untangle the problem in sets of processes. Every process is a desired feature.
2) Every feature is a unidirectional process built with functions.
3) A process is a thread of transformation that starts with an designed Data Structure.
4) 





2) Modeling the Domain

Typically, when we talk about DDD, Object Oriented Programming is used to model the Domain. But OOP has well documented and expensive disadvantages. OOP implies a multi-directional, multie-state approach that tends to path toward a heavy and confuse set of entities. Fortunately, OOP is not the only option that we have. We really believe that FP offers a superior way to do the job, it is just a more clear and straightforward manner to express the set of ideas that underlies a software project.

So, instead of objects, we'll use the two fundamental concepts under FP: Data Structures and Functions. In the case of those structures will focus in the holly grail of Clojure development: maps. We love maps as our main abstraction entity and for very good reasons, they are very transparent, so flexible and so simple to think about, Clojure has a lot of amazing functions to query and transform maps in creative ways. Another advantage of maps is that they will reduce the boilerplate in your application. Besides, maps no only "travel" well inside the language, with the help of the Extensible Data Notation(EDN) --or failing this JSON--, they can travel over the wires.




3) Implementing the Model






