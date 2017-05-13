(declare-datatypes () ((Transfer SUCCESS FAIL INVALID)))
(declare-fun result ()Transfer)
(declare-const user Int)
(declare-const money Int)
(define-fun moneyCon ()Bool (= (mod money 50000) 0))
(define-fun userLength ()Bool (and (>= user 6) (<= user 50)))
(define-fun moneyLength ()Bool (<= money 100000000))
(define-fun conSuccess ()Bool (and (and moneyCon moneyLength) userLength))
(define-fun conFail ()Bool (or (or (not moneyCon) (not moneyLength)) (not userLength)))
(assert (and (and (and (> user 0) (<= user 50)) (> money 0)) (<= money 100000000)))
(assert (= result
(if conSuccess
SUCCESS
(if conFail
FAIL
INVALID
))))
(check-sat)
