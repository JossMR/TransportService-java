package sokoban.models;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FileTextReader {
        private static final String levelsPath = "src\\main\\resources\\levels\\";
        private static final String levelSavedPath = "src\\main\\resources\\save\\levelSaved.txt";
        private static final String levelWinnedPath = "src\\main\\resources\\save\\winnedLevel.txt";
                
    public static List<char[]> getLevelsList() {
        List<char[]> charList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            try (FileReader read = new FileReader(levelsPath+"Level"+i+".txt")) {
                int character;
                int cont=0;
                char[] vec=new char[121];
                while ((character = read.read()) != -1) {
                    if(character=='*'||character=='#'||character=='.'||character==' '||character=='@'||character=='$'){
                        vec[cont]=(char)character;
                        cont++;
                    }
                }
                charList.add(vec);
            } catch (Exception ex) {
                System.out.println("Error al leer el archivo: " + ex.getMessage());
            }
        }return charList;
    }
    
    public static char[] getSavedLevel() {
            try (FileReader read = new FileReader(levelSavedPath)) {
                int character;
                int cont=0;
                char[] vec=new char[121];
                while ((character = read.read()) != -1) {
                    if(character=='*'||character=='#'||character=='.'||character==' '||character=='@'||character=='$'){
                        vec[cont]=(char)character;
                        cont++;
                    }
                }
                return vec;
            } catch (Exception ex) {
                System.out.println("Error al leer el archivo: " + ex.getMessage());
            }
        return null;
    }
    
    public static int getSavedLevelID(){
        try (FileReader read = new FileReader(levelSavedPath)) {
            int character;
            int cont=0;
            while ((character = read.read()) != -1) {
                if(character>48&&character<54){
                    return Character.getNumericValue(character);
                }
                cont++;
            }
        } catch (Exception ex) {
            System.out.println("Error al leer el archivo: " + ex.getMessage());
        }
        return 0;
    }
    
    public static ArrayList<Character> getSavedSteps(){
        try (FileReader read = new FileReader(levelSavedPath)) {
            int character;
            ArrayList<Character> steps=new ArrayList<>();
            boolean startReading=false;
            while ((character = read.read()) != -1) {
                if(character>47&&character<53){
                    startReading=true;
                }else if(startReading){
                    steps.add((char)character);
                }
            }
            return steps;
        } catch (Exception ex) {
            System.out.println("Error al leer el archivo: " + ex.getMessage());
        }
        return null;
    }
    
    public static boolean isLevelSaved(){
        try (FileReader read = new FileReader(levelSavedPath)) {
                int caracter;
                int cont=0;
                while ((caracter = read.read()) != -1) {
                    cont++;
                }
                if(cont==0){
                    return false;
                }
            } catch (Exception ex) {
                System.out.println("Error al leer el archivo: " + ex.getMessage());
            }
        return true;
    }
    
    public static void saveLevel(ListGrid levelToSave,int IDLevel, ArrayList<Character> steps){
        try (FileWriter write = new FileWriter(levelSavedPath)) {
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 11; j++) {
                    write.write(levelToSave.getElement(i, j).getCharacter());
                }write.write("\n");
            }
            write.write(String.valueOf(IDLevel));
            for (int i = 0; i < steps.size(); i++) {
                write.write(steps.get(i));
            }
        } catch (Exception ex) {
            System.out.println("Error al leer el archivo: " + ex.getMessage());
        }
    }
    
    public static void saveWinnedLevel(ListGrid levelToSave,int IDLevel, ArrayList<Character> steps){
        try (FileWriter write = new FileWriter(levelWinnedPath)) {
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 11; j++) {
                    write.write(levelToSave.getElement(i, j).getCharacter());
                }write.write("\n");
            }
            write.write(String.valueOf(IDLevel));
            for (int i = 0; i < steps.size(); i++) {
                write.write(steps.get(i));
            }
        } catch (Exception ex) {
            System.out.println("Error al leer el archivo: " + ex.getMessage());
        }
    }

    public static int getWinnedLevelID(){
        try (FileReader read = new FileReader(levelWinnedPath)) {
            int character;
            int cont=0;
            while ((character = read.read()) != -1) {
                if(character>48&&character<54){
                    return Character.getNumericValue(character);
                }
                cont++;
            }
        } catch (Exception ex) {
            System.out.println("Error al leer el archivo: " + ex.getMessage());
        }
        return 0;
    }

    public static ArrayList<Character> getWinnedSteps(){
        try (FileReader read = new FileReader(levelWinnedPath)) {
            int character;
            ArrayList<Character> steps=new ArrayList<>();
            boolean startReading=false;
            while ((character = read.read()) != -1) {
                if(character>47&&character<53){
                    startReading=true;
                }else if(startReading){
                    steps.add((char)character);
                }
            }
            return steps;
        } catch (Exception ex) {
            System.out.println("Error al leer el archivo: " + ex.getMessage());
        }
        return null;
    }

    public static void deleteWinnedLevel(){
        try (FileWriter write = new FileWriter(levelWinnedPath)) {
            write.write("");
        } catch (Exception ex) {
            System.out.println("Error al leer el archivo: " + ex.getMessage());
        }
    }
}