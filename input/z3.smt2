(declare-datatypes () ((CanMarry TRUE FALSE)))
(declare-const age Int)
(declare-const isMale Bool)
(declare-fun result ()CanMarry)
(assert (and (>= age 0) (<= age 150)))
(define-fun con1 ()Bool (>= age 18))
(define-fun con2 ()Bool (and (>= age 16) (= isMale false)))
(assert (= result
(if (or con1 con2)
TRUE
FALSE
)))
(check-sat)
