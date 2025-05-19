



| Commande   | json                                  | Direction                             | description                              |
|------------|---------------------------------------|---------------------------------------|------------------------------------------|
| shutdown   | {"cmd":"shutdown"}                    | client -> seveur & serveur -> client  | eteind le serveur et ferme les connexion |
| start      | {"cmd":"start"}                       | client -> serveur & serveur -> client | lance la game                            |
| move       | {"cmd":"move","data":"..."}           | client -> serveur                     | deplacement de la raquette               |
| updategame | {"cmd":"udpategame","data":"..."      | serveur -> client                     | donne l'etat du jeux                     |
| endgame    | {"cmd":"endgame, "data":"gamestatus"} | serveur -> client                     | serveur valide la victoire               |
| quit       | {"cmd":"quit"}                        | client -> serveur                     | client se deconnect du server            |

