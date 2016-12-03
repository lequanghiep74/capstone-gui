(declare-datatypes () ((bmi invalid underweight normalweight overweight obesity)))
(declare-fun result ()bmi)
(declare-const w Int)
(declare-const h Int)
(assert (and (and (and (>= w -1) (<= w 200)) (>= h 0)) (<= h 200)))
(define-fun con1 ()Bool (< (/ w (/ (* h h) 10000)) 18))
(define-fun con2 ()Bool (< (/ w (/ (* h h) 10000)) 25))
(define-fun con3 ()Bool (>= (/ w (/ (* h h) 10000)) 18))
(define-fun con4 ()Bool (>= (/ w (/ (* h h) 10000)) 25))
(define-fun con5 ()Bool (< (/ w (/ (* h h) 10000)) 30))
(define-fun con6 ()Bool (>= (/ w (/ (* h h) 10000)) 30))
(assert (= result
(if con1
underweight
(if (and con2 con3)
normalweight
(if (and con5 con4)
overweight
(if con6
obesity
invalid
))))))
(check-sat)
