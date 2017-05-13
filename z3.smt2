(declare-datatypes () ((Triangle EQUI ISO SCA NOT INVALID)))
(declare-fun result ()Triangle)
(declare-const a Int)
(declare-const b Int)
(declare-const c Int)
(assert (and (and (and (and (and (>= a -4) (>= b -4)) (>= c -4)) (<= a 30)) (<= b 30)) (<= c 30)))
(define-fun eq_ab ()Bool (= a b))
(define-fun eq_bc ()Bool (= b c))
(define-fun eq_ca ()Bool (= c a))
(define-fun tri1 ()Bool (> (+ a b) c))
(define-fun tri2 ()Bool (> (+ b c) a))
(define-fun tri3 ()Bool (> (+ a c) b))
(define-fun equi1 ()Bool (and eq_ab eq_bc))
(define-fun equi2 ()Bool (and eq_bc eq_ca))
(define-fun equi3 ()Bool (and eq_ca eq_ab))
(define-fun tri ()Bool (and (and tri1 tri2) tri3))
(define-fun iso1 ()Bool (and (and tri eq_ab) (not eq_bc)))
(define-fun iso2 ()Bool (and (and tri eq_bc) (not eq_ca)))
(define-fun iso3 ()Bool (and (and tri eq_ca) (not eq_ab)))
(assert (= result
(if (or (or (<= a 0) (<= b 0)) (<= c 0))
INVALID
(if (not tri)
NOT
(if (and tri (or (or equi1 equi2) equi3))
EQUI
(if (or (or iso1 iso2) iso3)
ISO
SCA
))))))
(check-sat)
