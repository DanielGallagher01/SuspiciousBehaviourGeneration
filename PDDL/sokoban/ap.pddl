(define (animation sokuban)
    (:cost_keyword total-cost)
  
    (:predicate IS-NONGOAL
        :parameters (?p)
        :priority 0
        :effect(
           (assign (?p x y) (function distribute_grid_around_point (objects ?p)))
           (equal (?p color) #873e23)
        )
    )

    (:predicate IS-GOAL
        :parameters (?p)
        :priority 5
        :effect(

            (assign (?p x y) (function distribute_grid_around_point (objects ?p)) )
            (equal (?p prefabImage) img-circle)
            (equal (?p color) green)

        )
    )

        (:predicate at
        :parameters (?t ?l)
        :priority 2
        :effect (
            (equal (?t y) (?l y))
            (equal (?t x) (?l x))
            (equal (?l color) white)

        )
    )
    


    (:predicate clear
        :parameters (?p)
        :priority 1
        :effect (
           (equal (?p color) white)
        )
    )

  
  (:visual pos
            :type default
            :properties(
              (prefabImage img-road)
              (showName FALSE)
              (x Null)
              (y Null)
              (color white)
              (width 80)
              (height 80)
              (depth 1)
          )
  )

    (:visual player
            :type predefine

            :objects %player
            :properties(
              (prefabImage img-robot)
              (showName FALSE)
              (x Null)
              (y Null)
              (color black)
              (width 80)
              (height 80)
              (depth 2)
          )
  )

  (:visual stone
            :type predefine
            :objects %stone
            :properties(
              (prefabImage img-box)
              (showName FALSE)
              (x Null)
              (y Null)
              (color yellow)
              (width 80)
              (height 80)
              (depth 2)
          )
)






  
    (:image
        (img-circle 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 )
        (img-road iVBORw0KGgoAAAANSUhEUgAAAH4AAAB+CAYAAADiI6WIAAABIGlDQ1BzUkdCAAAYlX2PsUrDYBSFv99oLaWioIiDQ5BMogi6dKx16CKKUaGpU5I2dbA1JJE+gXQUHZwEUQffQBcH8QmEgoMo+AxWXLRE/lRIQOxZ7sfhwLkHxBvAYB7qjcDTiwW1ZJRVEjJt36WvPp8Q8rbnN4JUun/2jzJeySgD38BELWIxJtnqsSa5GbgBiJxk2/UkrwIz9q5ZASH/m/O29BUQZ4BaS7CV4ErVt0HcAVq8c/8cch+gHMeedQq3LZh6iT3tAkYP4aYde531aLuYfKjvHdi/m6STrTa2N4EUMM0aCyyD7ywt9hLZPAy9hmFnFoZPoHsUhl+XYdi9AuUZ7luu6ZlRVgEGHAfer2HEgPFHyOz805eO+nx0ihR+AHaFTnJG+ySwAAAACXBIWXMAAA7EAAAOxAGVKw4bAAAC3ElEQVR4nO3cPU4bURSG4e+MLEGfKqTPBqBKEWEpWwhS2iyDBtyEVUTpk03YDSgFBWuIFIk2BSHVSZGBOBE/I/niO9ff+0gWxubnyC93GI3tCS3JzF1JryQ90/jMI2J+1x2Zebziz05J8cB9j7nve8fgVNJFRFwu33g7cGZ+lPS+ymjDTB8IPySOu8OIOLn5pNPfFTPm6Fjdh+UtY5eZW5Le1Z0Ja3KUmW/Vr/g3kl7Wnghr8zkzn3eSXtSeBGu320naqT0F1m6vqz0B6iC8KcKbIrwpwpsivCnCmyK8KcKbIrwpwpsivCnCmyK8qYmkeX9p2bT2AAAAAAAAAEBRo3x7b2ZGfzj55uP/15/qvntvi4hF7celpEn/DsppoQdyla9Zvm1sf5AzSYPCZ+ZM0uunH2kli0l/ZeyDtmRb0n7tIR6x4Nm58rZrDzAE4csjvCnCm9qqPcAQhC+PFW+K8KYIb4rwpghvir16U6x4U4Q3RXhThDfFzp2bzJyM8EUkdyJ8WU1s5kX44ghvqon/7yJ8cax4U4Q3xabeVDMrHgAAAAAAAAAA1BX9yY+Oag8ywDQi7jyvfmbm+sdp2oxn50wR3hThTRHeFOFNEd4U4U0R3hThTRHeFOFNEd4U4U0R3hThTRHeFOFNEd4U4U0R3hThTRHeFOFNEd7URNK8v7RsWnsAAAAAAAAAoKjIzP3aQ7TgvtOwLGvpsZxI2m/kHDg1zQYe1r6S9HUN86yKc+AUdlV7gKEIXxbhTf2sPcBQhC/ruvYAQxG+LFa8o4hgxRv7VXuAIQhfXhN79oQvj/CmCG+K8KYIb4rwppoJf1p7iA3TxEGcTtJF7SE2TAuHbc+6iLiUdFh7kg0y9hX/TdJZpz/HmE/6V5lgdWNf8UcR8eN25y4ijiUd1J1pI4x1xZ9JOoiIT+pfc3crIr5k5o6kXUl71Ub8V0qKR66P6fdcj2zr+V3SeUScL9/6Gz5SgcxNpxihAAAAAElFTkSuQmCC)
        (img-box iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAAAb9JREFUaEPtWUtOxDAM7SAqVCRYwEm4AntOy5JDzElgAdJUqAhQOsrIZJwmdmLXqehqJvPJe372i5PsDi+PP13Dz24zBIb7u6Z0GF/fZrwnBRyB5/2RxNPD8UNrD8S3TQLWIr6E50yBlsBDrNt1IS8RdCdszEWjdPx7mrrP9485sJfD0PXXwynIqf+OupAWAQw85jahSyZdSIPA1e0NGnk2AVgcGgT8fGHa+PHpMHZf4zi/DRdaVIE1COSAN0VgqWCxyPsxEwpQwcMaWZ0AB/xF3yct+o+NStVAzG3gfLBg3fcd+Jw1RoUAxW3CgiUtZFIKYCtsbsGuRoCT876doLQvIinEAR8rWHUFuOBjBatKoMRtTBAocRszBHJ7m6X+RrWIOTmPuc0qCnDA57QHKgpwwee0B+IEJNxGNYUk3EadQG23ESfAyXmK24gS4ICnuo0YgZKCTfUxsW1iVReiFCzcSVHtUkwBymYkJ6JUYikVk/sBLbepokDOkZ6bKNyA13CbHAKkGxooHfwhdnqQkrkktWI4WEeL3KOPWgSwQ4ftXHBAdi29jrqQdRLJCw6LBEgu1CQBi6BjmP7via2o9QvT9jvNG/Q+ogAAAABJRU5ErkJggg==)
        (img-robot iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAsdJREFUWEell4FRIzEMRa1KCJ1AJVwqOagEqASuEkIlun3C8miN7DVhZzKbbNbyl770JUtZuFT1bynlrn4WVpRLKeVFRJ6OXpajF1T1bbAxm/h1Ct957r8BcZ7tMQWgqhj6qAZu6/1huz9uoN5F5J5nqvpcSvlTvT536+5F5H0E4ggARjHePAnGLyJioDaKlPsW8mav0gbQs4i8XAsA3qGgeZttGGhq3oZnvwIABQDgjhfwS1T4TVhfAy2AteSr//Nei9JVEajeRhBHORv/BwzeD/k32lYsVt7x8Ka+D7dc8e7e8/xzxnvccwlAD1JVqYwTSReScpcnK461CFQjlNcOuariNc/7C365nG/nPwv3q9OQ7YMHnum+SSwvFNDDvOpU/16rgk7UvnIkPGQjEs6yt374zYf//v0QAZHLbBElPl9iFkVEVV14hl6sgghC1C8xraj7XgDgMho1HP0GpcuuhbG+C2Wzi2REjp0+PEUvvJz3vSIkhnMdOXMjDmDUmCIgk+1Miruo2HtWhqNS6o2EfLkVkdgNseHJPARQ93InzMa1AOiCOwC1ZbfGNWpGvRMOIOZBGyQmEZjlwIyCWPKPDCyjJPRM7XMAoHH4yICQhE8J+F5vTMgAEGW1ZW5mZLUEK9cZeMrcK4x9rQy93cIrKPEyjcAvATggtw0AEyLv26bjhM6HydWpJgOWUAB1d94la+WdjkYyR03TuUaKiagl2yh6KYBa0ysJt8rKcDj5BqDrjiyEGjxpo3aiAQ4knh36tSmIDIArVQtdJ9fDwSN21pBHAPee8m1GzAC0ETsMJDQTPLAzQhy/IwddZ/VhZrp2B6DvCUkmm2Zs3S3rBX6IsQitrh1GAC0IQ4m3UOhJI9CfhlbXZgC89Gzur3MA3lEVhHV43guzxfLaDMDROaDvgjENZn0iPaSMdMAz9ydH8pEmTI/q/wH0tR4ByGFKHAAAAABJRU5ErkJggg==)
        
    )
)