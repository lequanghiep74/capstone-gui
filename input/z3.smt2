(declare-datatypes () ((Triangle EQUI ISO SCA NOT)))
(declare-const a Int)
(declare-const b Int)
(declare-const c Int)
(declare-fun x ()Triangle)
(assert (and (<= 0 a) (<= a 100)  (<= 0 b) (<= b 100) (<= 0 c) (<= c 100)))
(define-fun eq_ab ()Bool (= a b))
(define-fun eq_bc ()Bool (= b c))
(define-fun eq_ca ()Bool (= c a))
(define-fun tri1 ()Bool (> (+ a b) c ))
(define-fun tri2 ()Bool (> (+ b c) a ))
(define-fun tri3 ()Bool (> (+ a c) b ))
(define-fun tri ()Bool (and tri1 tri2 tri3))
(assert (= x
(if (not tri)
NOT
(if (and tri eq_ab eq_bc)
EQUI
(if (or (and tri eq_ab (not eq_bc)) (and tri eq_bc (not eq_ca)) (and tri eq_ca (not eq_ab)))
ISO
SCA
)))))
(check-sat)
