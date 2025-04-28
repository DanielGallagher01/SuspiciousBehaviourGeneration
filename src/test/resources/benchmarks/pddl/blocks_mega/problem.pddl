(define (problem BLOCKS-4-0)
(:domain BLOCKS)
(:objects A1 A2 A3 E P S1 S2 T1 T2 H Y R O  - block)
(:INIT   (ONTABLE A1)
         (ON A2 A1)
         (CLEAR A3) (ON A3 A2)
         (CLEAR E) (ONTABLE E)
         (CLEAR P) (ONTABLE P)
         (ONTABLE S1)
         (CLEAR S2) (ON S2 S1)
         (ONTABLE T1)
         (CLEAR T2) (ON T2 T1)
         (CLEAR H) (ONTABLE H)
         (CLEAR Y) (ONTABLE Y)
         (CLEAR R) (ONTABLE R)
         (CLEAR O) (ONTABLE O)
       (HANDEMPTY)
)
(:goal (AND 
        ; THAT
        (OR (ON T1 H) (ON T2 H))
        (OR (AND (ON H A1) (OR (ON A1 T1) (ON A1 T2)))
            (AND (ON H A2) (OR (ON A2 T1) (ON A2 T2)))
            (AND (ON H A3) (OR (ON A3 T1) (ON A2 T2)))
        )
        (OR (ONTABLE T1) (ONTABLE T2))


        ; RAYS
        (OR (AND (ON R A1) (ON A1 Y))
            (AND (ON R A2) (ON A2 Y))
            (AND (ON R A3) (ON A3 Y)))
        (OR (AND (ON Y S1) (ONTABLE S1))
            (AND (ON Y S2) (ONTABLE S2)))
        
      
        ; SOAP       
        (OR (ON S1 O) (ON S2 O)) 
        (OR (AND (ON O A1) (ON A1 P)) (AND (ON O A2) (ON A2 P)) (AND (ON O A2) (ON A2 P)))
        (ONTABLE P)
        
        )
)
)
           
