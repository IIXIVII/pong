//package Game;
//
//import org.ini4j.Ini;
//import java.io.FileReader;
//import java.io.IOException;
//
//
//public class Config {
//
//
//        public static void initConfig(String filePath) {
//
//
//            try {
//                Ini ini = new Ini();
//                ini.load(new FileReader(filePath));
//
//                // Accéder à une valeur dans une section spécifique
//                String dbHost = ini.get("Database", "host");
//                System.out.println("Database host: " + dbHost);
//
//                // Accéder à une valeur dans la section "UserSettings"
//                boolean notifications = ini.get("UserSettings", "notifications", boolean.class);
//                System.out.println("User notifications: " + notifications);
//
//                // Accéder à une valeur globale (sans section explicite au début du fichier)
//                // ini4j les met dans une section dont le nom est une chaîne vide "" ou null
//                // Souvent, il est plus simple de les définir sous une section [DEFAULT] ou [Global]
//                // Si votre fichier INI a `global_param=HelloWorld` au début, essayez :
//                String globalParam = ini.get("", "global_param");
//                // ou si vous avez défini une section implicite par défaut
//                // String globalParam = ini.get("DEFAULT", "global_param");
//                System.out.println("Global parameter: " + globalParam);
//
//
//                // Itérer sur les sections et les clés
//                for (String sectionName : ini.keySet()) {
//                    System.out.println("[" + sectionName + "]");
//                    Ini.Section section = ini.get(sectionName);
//                    for (String optionKey : section.keySet()) {
//                        System.out.println(optionKey + " = " + section.get(optionKey));
//                    }
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//}
