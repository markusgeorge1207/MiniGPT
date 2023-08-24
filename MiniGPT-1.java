import java.io.*;
import java.util.*;

public class MiniGPT {
    private int order;
    private Map<String, ArrayList<Integer>> frequencies;


    public MiniGPT (String inputFileName, int chainOrder) throws IOException {
        order = chainOrder;
        frequencies = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
        String line = reader.readLine();
        while (line != null) {
            addLine(line);
            line = reader.readLine();
        }
        reader.close();
    }

    private void addLine(String line) {
        String text = line.toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length() - order; i++) {
            sb.setLength(0);
            for (int j = 0; j < order; j++) {
                sb.append(text.charAt(i + j));
            }
            String sequence = sb.toString();
            char nextChar = text.charAt(i + order);
            ArrayList<Integer> nextCharCounts = frequencies.get(sequence);
            if (nextCharCounts == null) {
                nextCharCounts = new ArrayList<>(Collections.nCopies(128, 0)); // Assuming ASCII characters
                frequencies.put(sequence, nextCharCounts);
            }
            int index = (int) nextChar;
            int count = nextCharCounts.get(index);
            nextCharCounts.set(index, count + 1);
        }
    }

    public void generateText(String outputFileName, int numChars) throws IOException {
    PrintWriter writer = new PrintWriter(new FileWriter(outputFileName));
    String sequence = getRandomSequence();
    int i = 0;
    while (i < numChars) {
        char nextChar = getRandomNextChar(sequence);  
        writer.print(nextChar);
        sequence = sequence.substring(1) + nextChar;
        i++;
        if (i >= order && frequencies.get(sequence) == null) {
            sequence = getRandomSequence();
        }
    }
    writer.close();
}


    private String getRandomSequence() {
        List<String> sequences = new ArrayList<>(frequencies.keySet());
        return sequences.get((int)(Math.random() * sequences.size()));
    }

    private char getRandomNextChar(String sequence) {
    ArrayList<Integer> nextCharCounts = frequencies.get(sequence);
    if (nextCharCounts == null) {
        return '/'; 
    }
    int totalCount = 0;
    for (int count : nextCharCounts) {
        totalCount += count;
    }
    int randomIndex = (int)(Math.random() * totalCount);
    int countSoFar = 0;
    for (int i = 0; i < nextCharCounts.size(); i++) {
        countSoFar += nextCharCounts.get(i);
        if (randomIndex < countSoFar) {
            return (char)i;
        }
    }
    return '/';
}


    public void seedString ()
{
    File file = new File("input.txt"); 
        int maxLength = 10; 
        
        Map<String, Integer> frequencyMap = new HashMap<>(); // Map to store frequency of each string
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    for (int j = i + 1; j <= Math.min(i + maxLength, line.length()); j++) {
                        String substring = line.substring(i, j);
                        frequencyMap.put(substring, frequencyMap.getOrDefault(substring, 0) + 1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        // Get the most frequent strings for each length
        Map<Integer, List<String>> mostFrequentStrings = new HashMap<>();
        for (String key : frequencyMap.keySet()) {
            int length = key.length();
            List<String> currentList = mostFrequentStrings.getOrDefault(length, new ArrayList<>());
            currentList.add(key);
            mostFrequentStrings.put(length, currentList);
        }
        for (int length = 1; length <= maxLength; length++) {
            List<String> stringsOfLength = mostFrequentStrings.get(length);
            if (stringsOfLength == null) {
                continue;
            }
            Collections.sort(stringsOfLength, (s1, s2) -> frequencyMap.get(s2) - frequencyMap.get(s1)); // Sort by frequency in descending order
            System.out.println("Most frequent strings of length " + length + ":");
            for (int i = 0; i < Math.min(10, stringsOfLength.size()); i++) { // Print top 10 most frequent strings
                System.out.println(stringsOfLength.get(i) + " (" + frequencyMap.get(stringsOfLength.get(i)) + " occurrences)");
            }
            System.out.println();
        }
    }
}
