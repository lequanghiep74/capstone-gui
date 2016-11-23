(declare-datatypes () ((EvenOdd even odd)))
(declare-fun result ()EvenOdd)
(declare-const a Int)
(assert (> a 0))
(assert (= result
(if (= (mod a 2) 0)
even
odd
)))
(check-sat)
