(declare-datatypes () ((Triangle EQUI ISO SCA NOT)))
(declare-const a Int)
(declare-const b Int)
(declare-const c Int)
(declare-fun result ()Triangle)
(assert (and (and (and (and (and (>= a 0) (<= a 100)) (>= b 0)) (<= b 100)) (>= c 0)) (<= c 100)))
(define-fun eq_ab ()Bool (= a b))
(define-fun eq_bc ()Bool (= b c))
(define-fun eq_ca ()Bool (= c a))
(define-fun tri1 ()Bool (> (+ a b) c))
(define-fun tri2 ()Bool (> (+ b c) a))
(define-fun tri3 ()Bool (> (+ a c) b))
(define-fun tri ()Bool (and (and tri1 tri2) tri3))
(define-fun iso1 ()Bool (and (and tri eq_ab) (not eq_bc)))
(define-fun iso2 ()Bool (and (and tri eq_bc) (not eq_ca)))
(define-fun iso3 ()Bool (and (and tri eq_ca) (not eq_ab)))
(assert (= result
(if (not tri)
NOT
(if (and (and tri eq_ab) eq_bc)
EQUI
(if (or (or iso1 iso2) iso3)
ISO
SCA
)))))
(check-sat)
