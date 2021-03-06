A Very Gentle Introduction To Relational & Functional Programming
====

This tutorial will guide you through the magic and fun of combining relational programming (also known as logic programming) with functional programming. This tutorial does not assume that you have any knowledge of Lisp, Clojure, Java, or even functional programming. The only thing this tutorial assumes is that you are not afraid of using the command line and you have used at least one programming language before in your life.

Work in Progress
----

This tutorial is very much a work in progress. It's possible to get through the first parts and learn something, but expect a considerable amount of fleshing out in the next couple of weeks.

Why Logic Programming?
----

What's the point of writing programs in the relational paradigm? First off, aesthetics dammit. 

Logic programs are simply beautiful as they often have a declarative nature which trumps even the gems found in functional programming languages. Logic programs use search, and thus they are often not muddied up by algorithmic details. If you haven't tried Prolog before, relational programming will at times seem almost magical.

However, I admit, the most important reason to learn the relational paradigm is because it's FUN.

First Steps
----

Ok, we're ready to begin. Type <code>lein repl</code> or <code>cake repl</code>, this will drop you into the Clojure prompt. First lets double check that everything went ok. Enter the following at the Clojure REPL:

```clj
user=> (require 'clojure.core.logic)
```

The REPL should print nil and it should return control to you. If it doesn't file an issue for this tutorial and I'll look into it. If all goes well run the following:

```clj
user=> (load "logic_tutorial/tut1")
```

You'll see some harmless warnings, then run the following:

```clj
user=> (in-ns 'logic-tutorial.tut1)
```

Your prompt will change and you're now working in a place that has the magic of relational programming available to you. The REPL prompt will show <code>logic-tutorial.tut1</code>, we're going show <code>tut1</code> to keep things concise.

Question & Answer
----

Unlike most programming systems, with relational programming we can actually ask the computer questions. But before we ask the computer questions, we need define some facts! The first thing we want the computer to know about is that there are men:

```clj
tut1=> (defrel man x)
#'tut1/man
```

And then we want to define some men:

```clj
tut1=> (fact man 'Bob)
nil
tut1=> (fact man 'John)
nil
```

Now we can ask who are men. Questions are always asked with <code>run</code> or <code>run*</code>. By convention we'll declare a logic variable <code>q</code> and ask the computer to give use the possible values for <code>q</code>. Here's an example.

```clj
tut1=>  (run 1 [q] (man q))
(John)
```

We're asking the computer to give us at least one answer to the question - "Who is a man?".  We can ask for more than one answer:

```clj
tut1=> (run 2 [q] (man q))
(John Bob)
```

Now that is pretty cool. What happens if we ask for even more answers?

```clj
tut1=> (run 3 [q] (man q))
(John Bob)
```

The same result. That's because we’ve only told the computer that two men exist in the world. It can't give results for things it doesn't know about. Let's define another kind of relationship and a fact:

```clj
tut1=> (defrel fun x)
#'tut1/fun
tut1=> (fact fun 'Bob)
nil
```

Let's ask a new kind of question:

```clj
tut1=> (run* [q] (man q) (fun q))
(Bob)
```

There's a couple of new things going on here. We use <code>run\*</code>. This means we want all the answers the computer can find. The question itself is formulated differently than before because we're asking who is a man *and* is fun. Enter in the following:

```clj
tut1=> (defrel woman x)
#'tut1/woman
tut1=> (fact woman 'Lucy)
nil
tut1=> (fact woman 'Mary)
nil
tut1=> (defrel likes x y)
#'tut1/likes
```

Relations don't have to be a about a single entity. We can define relationship between things!

```clj
tut1=> (fact likes 'Bob 'Mary)
nil
tut1=> (fact likes 'John 'Lucy)
nil
tut1=> (run* [q] (likes 'Bob q))
(Mary)
```

We can now ask who likes who! Let's try this:

```clj
tut1=> (run* [q] (likes 'Mary q))
()
```

Hmm that doesn't work. This is because we never actually said *who Mary liked*, only that Bob liked Mary. Try the following:

```clj
tut1=> (fact likes 'Mary 'Bob)
nil
tut1=> (run* [q] (fresh [x y] (likes x y) (== q [x y])))
([Mary Bob] [Bob Mary] [John Lucy])
```

Wow that's a lot of new information. The <code>fresh</code> expression isn't something we've seen before. Why do we need it? By convention <code>run</code> returns single values for <code>q</code> which answer the question. In this case we want to know who likes who. This means we need to create logic variables to store these values in. We then assign both these values to <code>q</code> by putting them in a Clojure vector (which is like an array in other programming languages).

Try the following:

```clj
tut1=> (run* [q] (fresh [x y] (likes x y) (likes y x) (== q [x y])))
([Mary Bob] [Bob Mary])
```

Here we only want the list of people who like each other. Note that the order of how we pose our question doesn't really matter:

```clj
tut1=> (run* [q] (fresh [x y] (likes x y) (== q [x y]) (likes y x)))
([Mary Bob] [Bob Mary])
```

Some Genealogy
----

We've actually predefined some interesting relations in the <code>tut1</code> file that we'll try out first before we take a closer look:

```clj
tut1=> (fact parent 'John 'Bobby)
nil
tut1=> (fact male 'Bobby)
nil
```

We can now run this query:

```clj
tut1=> (run* [q] (son q 'John))
(Bobby)
```

Let's add another fact:

```clj
tut1=> (fact parent 'George 'John) 
nil
tut1=> (run* [q] (grandparent q 'Bobby))
(George)
```

Huzzah! We can define relations in terms of other relations! Composition to the rescue. But how does this work exactly?

Let's take a moment to look at what's in the file. At the top of the file after the namespace declaration you'll see that some relations have been defined:

```clj
(defrel parent x y)
(defrel male x)
(defrel female x)
```

After this there are some functions:

```clj
(defn child [x y]
  (parent y x))

(defn son [x y]
  (all
    (child x y)
    (male x)))

(defn daughter [x y]
  (all
    (child x y)
    (female x)))
```

We can define relations as functions! Play around with defining some new facts and using these relations to pose questions about these facts. If you're feeling particularly adventurous, write a new relation and use it.

Primitives
----

Let's step back for a moment. <code>core.logic</code> is built upon a small set of primitives - they are <code>run</code>, <code>fresh</code>, <code>==</code>, and <code>conde</code>. We're already pretty familiar with <code>run</code>, <code>fresh</code>, and <code>==</code>. <code>run</code> is simple, it let's us <code>run</code> our logic programs. <code>fresh</code> is also pretty simple, it lets us declare new logic variables. <code>==</code> is a bit mysterious and we've never even seen <code>conde</code> before.

Unification
----

Earlier I lied about assignment when using the <code>==</code> operator. The <code>==</code> operator means that we want to unify two terms. This means we'd like the computer to take two things and attempt to make them equal. If logic variables occur in either of the terms, the computer will try to bind that logic variable to what ever value matches in the other term. If the computer can't make two terms equal, it fails - this is why sometimes we don't see any results.

Consider the following:

```clj
tut1=> (run* [q] (== 5 5))
(_.0)
```

Whoa, what does that mean? It means that our question was fine, but that we never actually unified <code>q</code> with anything - <code>_.0</code> just means we have a logic variable that was never bound to a concrete value.

```clj
tut1=> (run* [q] (== 5 4))
()
```

It's impossible to make 5 and 4 equal to each other, the computer lets us know that no successful answers exist for the question we posed.

```clj
tut1=> (run* [q] (== q 5) (== q 5))
(5)
tut1=> (run* [q] (== q 5) (== q 4))
()
```

Once we've unified a logic variable to a concrete value we can unify it again with that value, but we cannot unify with a concrete value that is not equal to what it is currently bound to.

Here's an example showing that we can unify complex terms:

```clj
tut1=> (run* [q] (fresh [x y] (== [x 2] [1 y]) (== q [x y])))
([1 2])
```

This shows that in order for the two terms <code>[x 2]</code> and <code>[1 y]</code> to be unified, the logic varialbe <code>x</code> must be bound to 1 and the logic variable <code>y</code> must be bound to 2.

Note is perfectly fine to unify two variable to each other:

```clj
tut1=> (run* [q] (fresh [x y] (== x y) (== q [x y])))
([_.0 _.0])
tut1=> (run* [q] (fresh [x y] (== x y) (== y 1) (== q [x y])))
([1 1])
```

Multiple Universes
----

By now we're already familiar with conjuction, that is, logical **and**.

```clj
(run* [q] (fun q) (likes q 'Mary))
```

We know now to read this as find <code>q</code> such that <code>q</code> is fun **and** <code>q</code> likes Mary.

But how to express logical **or**?

```clj
(run* [q]
  (conde
    ((fun q))
    ((like q 'Mary))))
```

The above does exactly that - find <code>q</code> such that <code>q</code> is fun *or* <code>q</code> likes Mary. This is the essence of how we get multiple answers from <code>core.logic</code>.

Magic Tricks
----

By now we're tired of genealogy. Let's go back to the cozy world of Computer Science. One of the very first things people introduce in CS are arrays and/or lists. It’s often convenient to take two lists and join them together. In Clojure this functionality exists via <code>concat</code>. However we're going to look at a relational version of the function called <code>appendo</code>. While <code>appendo</code> is certainly be slower than <code>concat</code> it has magical powers that <code>concat</code> does not have.

First we'll want to load the next tutorial and switch into it's namespace.

```clj
tut1=> (in-ns 'user)
nil
user=> (load "logic_tutorial/tut2")
nil
user=> (in-ns 'logic-tutorial.tut2)
nil
```

Relational functions are written quite differently than their functional counterparts. Instead of return value, we usually make the final parameter be output variable that we'll unify the answer to. This makes it easier to compose relations together. This also means that relational programs in general look quite different from functional programs.

Open <code>src/logic-tutorial/tut2.clj</code>. You'll find the definition for <code>myappendo</code> which is identical to the <code>appendo</code> defined in core.logic. 

```clj
(defne myappendo [x y z]
  ([() _ y])
  ([[?a . ?d] _ [?a . ?r]] (myappendo ?d y ?r)))
```

<code>defne</code> above is syntax sugar around <code>matche</code>. It creates a function (named <code>myappendo</code>) that accepts three arguments. It matches x, y and z with the three arguments in the list at the beginning of each clause. In this case the first clause is matched cons style. <code>?a</code> is declared as the first item and <code>?d</code> is declared as the rest. The second item is ignored, the third is split in the same way. Note that the <code>?a</code> is shared here on the first and third parameter of the match. This makes the first item in x, the first item in z. Assuming the x and z can be matched as above, appendo is recursively called on the rest of each list (including y, untouched). Something similar to the shared <code>?a</code> is going on when the recursion bottoms out (when x is empty). In this clause y is declared as z. This makes y the tail of a cons like expression (specifically the <code>?r</code>) from the previous recursive call.

//Source: http://objectcommando.com/blog/2011/10/13/appendo-the-great/

We can pass in logic variables in any one of it's three arguments. Note that <code>appendo</code> can infer it's inputs!
This definition uses pattern matching - it can decrease the amount of boiler plate we have to write for many programs.


Now try the following:

```clj
tut2=> (run* [q] (myappendo [1 2] [3 4] q))
([1 2 3 4])
```

Seems reasonable. Now try this:

```clj
tut2=> (run* [q] (myappendo [1 2] q [1 2 3 4]))
([3 4])
```

<code>matche</code>

TODO

matche sugar: Wildcards

matche sugar: List destructuring

matche sugar: Combining wildcards and destructuring

matche sugar: Implicit variables

//Source: https://github.com/frenchy64/Logic-Starter/wiki


Zebras
----

There's a classic old puzzle sometimes to referred to as the Zebra puzzle, sometimes as Einstein's puzzle. Writing an algorithm for solving the constraint is a bit tedious - relational programming allows us to just describe the constraints and it can produce the correct answer for us.

```clj
tut2=> (in-ns 'user)
nil
user=> (load "logic_tutorial/tut3")
nil
user=> (in-ns 'logic-tutorial.tut3)
nil
```

The puzzle is described in the following manner.

If you look in <code>src/logic_tutorial/tut3.clj</code> you'll find the following code:

```clj
(defne righto [x y l]
  ([_ _ [x y . ?r]])
  ([_ _ [_ . ?r]] (righto x y ?r)))

(defn nexto [x y l]
  (conde
    ((righto x y l))
    ((righto y x l))))

(defn zebrao [hs]
  (macro/symbol-macrolet [_ (lvar)]
    (all
     (== [_ _ [_ _ 'milk _ _] _ _] hs)                         
     (firsto hs ['norwegian _ _ _ _])                         
     (nexto ['norwegian _ _ _ _] [_ _ _ _ 'blue] hs)       
     (righto [_ _ _ _ 'ivory] [_ _ _ _ 'green] hs)         
     (membero ['englishman _ _ _ 'red] hs)                    
     (membero [_ 'kools _ _ 'yellow] hs)                      
     (membero ['spaniard _ _ 'dog _] hs)                      
     (membero [_ _ 'coffee _ 'green] hs)                      
     (membero ['ukrainian _ 'tea _ _] hs)                     
     (membero [_ 'lucky-strikes 'oj _ _] hs)                  
     (membero ['japanese 'parliaments _ _ _] hs)              
     (membero [_ 'oldgolds _ 'snails _] hs)                   
     (nexto [_ _ _ 'horse _] [_ 'kools _ _ _] hs)          
     (nexto [_ _ _ 'fox _] [_ 'chesterfields _ _ _] hs))))
```

That is the entirety of the program. Let's run it:

```clj
tut3=> (run 1 [q] (zebrao q))
([[norwegian kools _.0 fox yellow] [ukrainian chesterfields tea horse blue] [englishman oldgolds milk snails red] [spaniard lucky-strikes oj dog ivory] [japanese parliaments coffee _.1 green]])
```

But how fast is it?

```clj
tut3=> (dotimes [_ 100] (time (doall (run 1 [q] (zebrao q)))))
```

On my machine, after the JVM has had time to warm up, I see the puzzle can be solved in as little as 3 milliseconds. The Zebra puzzle in and of itself is hardly very interesting. However if such complex constraints can be described and solved so quickly, <code>core.logic</code> is very likely fast enough to be applied to reasoning about types! Only time will tell, but I encourage people to investigate such applications.

Next Steps
----

Hopefully this short tutorial has revealed some of the beauty of relational programming. To be sure, relational programming as I've presented here has its limitations. Yet, people are actively working on surmounting those limitations in more ways than I really have time to document here.

While you can get along just fine as a programmer without using relational programming, many aspects of the tools we use today will seem mysterious without a basic understanding how relational programming works. It also allows to add features to our languages that are otherwise harder to implement. For example the elegant type systems (and type inferencing) found in Standard ML and Haskell would be fascinating to model via **core.logic**. I also think that a efficient predicate dispatch system that gives ML pattern matching performance with the open-ended nature of CLOS generic methods would be easily achievable via **core.logic**.

Resources
---

If you found this tutorial interesting and would like to learn more I recommend the following books to further you understanding of the relational paradigm.

* [The Reasoned Schemer](http://mitpress.mit.edu/catalog/item/default.asp?ttype=2&tid=10663)
* [Paradigms of Artificial Intelligence Programming](http://norvig.com/paip.html)
* [Prolog Programming For Artificial Intelligence](http://www.amazon.com/Prolog-Programming-Artificial-Intelligence-Bratko/dp/0201403757)
* [Concepts, Techniques, and Models of Computer Programming](http://www.info.ucl.ac.be/~pvr/book.html)
