(declare-datatypes () ((Register SUCCESS FAIL INVALID)))
(declare-fun result ()Register)
(declare-const age Int)
(declare-const user Int)
(declare-const pass Int)
(declare-const email Int)
(declare-const firstName Int)
(declare-const lastName Int)
(define-fun ageCon ()Bool (>= age 18))
(define-fun userLength ()Bool (> user 10))
(define-fun passLength ()Bool (> pass 6))
(define-fun emailLength ()Bool (> email 10))
(define-fun firstNameLength ()Bool (> firstName 5))
(define-fun lastNameLength ()Bool (> lastName 5))
(define-fun conSuccess ()Bool (and (and (and (and (and ageCon userLength) passLength) emailLength) firstNameLength) lastNameLength))
(define-fun conFail ()Bool (or (or (or (or (or ageCon userLength) passLength) emailLength) firstNameLength) lastNameLength))
(define-fun ageB ()Bool (= age 50))
(assert (and (and (and (>= age 0) (<= age 120)) (> pass 0)) (< pass 30)))
(assert (and (and (and (and (and (and (and (> email 0) (< email 30)) (> firstName 0)) (< firstName 50)) (> lastName 0)) (< lastName 50)) (> user 0)) (< user 30)))
(assert (= result
(if conSuccess
SUCCESS
(if conFail
FAIL
INVALID
))))
(check-sat)
