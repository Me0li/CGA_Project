# CGA Projekt SS21

# Projektname
Pokémon Dorado

# Teammitglieder
- Oliver Mertens ->  11119032
- Daniel Weyand  ->  11120186

# Featureliste
Die folgenden Features wurden allesamt in Zusammenarbeit erstellt:
- Das Spiel

- Interaktiver Wechsel zwischen verschiedenen Kameraperspektiven:
  - 3rd Person View um den aktuell gewählten Spielstein (mit Zoomfunktion)
  - Orbital Camera auf das Spielbrett
  - Freeflight Camera
  - Dice Camera
  
- Darstellung des Spielbretts in verschiedenen Grafikstilen möglich
  - Verwundung von selbst erstellten Texturen
- Skybox 
- Rendering verschiedener Modelle innerhalb einer zusammenhängenden Spielszene
- Wechsel zwischen verschiednenen Shadern möglich
  - dynamische Parameter werden eingesetzt 

# Spielregeln
Pokémon Dorado ist ein Spiel für 2 bis 4 Personen. Jeder Spieler wählt sich eine Farbe aus, woraufhin jewils 4 Spielsteine auf das Startfeld platziert werden.

Das Zugrecht geht im Uhrzeigersinn um. Der Spieler am Zug würfelt und muss dann einen seiner Spielsteine um genau diese Zahl auf dem Spielfeld vorrücken. Hat er mehrere Möglichkeiten, darf er frei die günstigste Wählen. Kann er keinen Stein ziehen, muss er aussetzen.

Landet ein Stein auf einem besetzen Feld, wird er einfach auf die dort bereits stehenden Steine oben auf platziert. Dadurch bildet sich ein Turm. Von jedem Turm darf nur der oberste Stein ziehen, die darunterliegenden sind solange blockiert, bis alle darüber liegenden weggezogen sind.

Landet ein Stein im Waser, so ist er verloren! Alle nachfolgenden Steine können nun das Wasser an dieser Stelle sicheren Fußes überqueren, genau wie auf jedem anderen Landfeld.

Das Ziel muss mit einem exaktem Wurf erreicht werden. Ein Spielstein, der über das Ziel hinausschießen würden, kann in diesem Zug nicht ziehen!

Der erste Stein, der das Ziel erreicht, bekommt 100 Punkte. Jeder später ankommende Stein erhält jeweils 10 Punkte mehr. Also der zweite Stein erhält 110 Punkte, der dritte 120 usw. Wenn kein Spieler mehr ziehen kann, gewinnt der Spieler mit den meisten Punkten.

# Steuerung
Allgemein:
| Eingabe | Funktion | 
|------|----------------|
| R | Wechsel verschiedener Shaderprogramme | 
| T | Wechsel zwischen verschiedenen Leveln des Cell-Shadings | 
| F | Wechsel zwischen verschiedenen Grafikmodellen | 
| 1 | 3rd Person Kamera | 
| 2 | Orbital Kamera | 
| 3 | Free Flight Kamera | 
| Pfeil links | Spielstein links auswählen | 
| Pfeil rechts | Spielstein rechts auswählen | 
| Enter | Auswahl bestätigen | 

Free Flight Kamera:
| Eingabe | Funktion | 
|------|----------------|
| WASD + Maus | Bewegung der Free Flight Kamera | 
| Leertaste | Bewegung der Free Flight Kamera nach oben | 
| STRG |  Bewegung der Free Flight Kamera nach unten | 

3rd Person Kamera:
| Eingabe | Funktion | 
|------|----------------|
| Maus | Bewegung der 3rd Person Kamera | 
| Mausrad | Kamerazoom | 

# Quellen
3D-Objekte:
- Hexagon       https://sketchfab.com/3d-models/hexagon-no-textures-75a0b9e8c51b4ad98d0a9a74735409b0
- Spielsteine   https://sketchfab.com/3d-models/blue-man-checker-game-581bb93174d04bd7a7a9f8d9f6ae5039  
- Skybox        https://gamebanana.com/mods/218297
