;; regras.clp
;;
;; Regras para comportamento do fantasma azul do jogo pacman.
;;

(import nrc.fuzzy.*)
(import nrc.fuzzy.jess.*)
(load-package FuzzyFunctions)

(defglobal ?*distPacman* = (new nrc.fuzzy.FuzzyVariable "distancia" 0.0 475.0 "Pixels"))

(defglobal ?*rlf* = (new nrc.fuzzy.RightLinearFunction))
(defglobal ?*llf* = (new nrc.fuzzy.LeftLinearFunction))
(defglobal ?*tf* = (new nrc.fuzzy.RightLinearFunction))

(defglobal ?*regrasDisparadas* = "")
(defglobal ?*comportamento* = "")

(defrule init
   (declare (salience 100))
  =>
   (import nrc.fuzzy.*)
   (load-package nrc.fuzzy.jess.FuzzyFunctions)
   (?*distPacman* addTerm "near" (new RFuzzySet 0.0 158.0 ?*rlf*))
   (?*distPacman* addTerm "medium" (new TriangleFuzzySet  0.0 158.0 316.0))
   (?*distPacman* addTerm "far" (new LeftLinearFuzzySet 200.0 316.0))
   
   (store DISTPACFUZZY ?*distPacman*)
      
)

(defrule far
  (dist ?d&:(fuzzy-match ?d "far"))
  
 =>
  (assert (mude_comportamento (new FuzzyValue ?*distPacman* "far")))
  (bind ?*comportamento* STUPID)
  (bind ?*regrasDisparadas* (str-cat ?*regrasDisparadas* "!Rule: if Pacman far then change comportamento STUPID fires%"))
)

(defrule medium
  (dist ?d&:(fuzzy-match ?d "medium"))
  
 =>
  (assert (mude_comportamento (new FuzzyValue ?*distPacman* "medium")))
  (bind ?*comportamento* SHY)
  (bind ?*regrasDisparadas* (str-cat ?*regrasDisparadas* "!Rule: if Pacman far then change comportamento SHY fires%"))
)

(defrule near
  (dist ?d&:(fuzzy-match ?d "near"))
  
=>
  (assert (mude_comportamento (new FuzzyValue ?*distPacman* "near")))
  (bind ?*comportamento* HUNTER)
  (bind ?*regrasDisparadas* (str-cat ?*regrasDisparadas* "!Rule: if Pacman far then change comportamento HUNTER fires%"))
)

(defrule defuzzify "low salience to allow all rules to fire and do global contribution"
   (declare (salience -100))
   ?mc <- (mude_comportamento ?m)
   ?dist <- (dist ?)
 =>
  (bind ?cp-muda (?m momentDefuzzify))
  (store COMPORT ?*comportamento*)
  (store REGRASDISPARADAS ?*regrasDisparadas*)
  (bind ?*regrasDisparadas* "")
  (retract ?mc ?dist)
(reset)
)




