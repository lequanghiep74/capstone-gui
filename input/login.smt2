(declare-const user String)
(declare-const pass String)
(declare-const result Bool)
(assert (= result
    (
        if (= user pass)
            true
        false
     )
))
(check-sat)
(get-model)