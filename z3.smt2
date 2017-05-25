(declare-datatypes () ((Login SUCCESS FAIL INVALID)))
(declare-fun result ()Login)
(declare-const user Int)
(declare-const PIN Int)
(define-fun userLength ()Bool (and (>= user 6) (<= user 50)))
(define-fun pinLength ()Bool (>= PIN 6))
(define-fun conSuccess ()Bool (and userLength pinLength))
(define-fun conFail ()Bool (or (not userLength) (not pinLength)))
(assert (and (> PIN 0) (<= PIN 20)))
(assert (and (> user 0) (<= user 50)))
(assert (= result
(if conSuccess
SUCCESS
(if conFail
FAIL
INVALID
))))
(check-sat)
