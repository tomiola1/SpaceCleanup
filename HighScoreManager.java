package util;

import java.io.*;
import java.util.*;

public class HighScoreManager {
    private final File file = new File("highscores.txt");
    public void submit(String name, int score) throws IOException {
        List<String> lines = new ArrayList<>();
        if (file.exists()) lines = new ArrayList<>(java.nio.file.Files.readAllLines(file.toPath()));
        lines.add(name + "," + score);
        lines.sort((a,b) -> Integer.compare(Integer.parseInt(b.split(",")[1]), Integer.parseInt(a.split(",")[1])));
        if (lines.size() > 20) lines = lines.subList(0, 20);
        java.nio.file.Files.write(file.toPath(), lines);
    }
    public List<String> top() throws IOException {
        if (!file.exists()) return List.of();
        return java.nio.file.Files.readAllLines(file.toPath());
    }
}
