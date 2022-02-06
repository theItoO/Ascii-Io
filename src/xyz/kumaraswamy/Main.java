package xyz.kumaraswamy;

import xyz.kumaraswamy.asciio.AsciiIo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        byte[] input = Files.readAllBytes(new File("C:\\Users\\kumar\\IdeaProjects\\Ascii Io\\files\\input.jpg").toPath());

        System.out.println(Arrays.toString(input));
        File image = new File("C:\\Users\\kumar\\IdeaProjects\\Ascii Io\\files\\image.png");

        byte[] encoded = new AsciiIo(input).encode();
        Files.write(image.toPath(), encoded);

        byte[] data = Files.readAllBytes(image.toPath());
        byte[] decoded = new AsciiIo(data).decode();

        Files.write(new File(
                "C:\\Users\\kumar\\IdeaProjects\\" +
                        "Ascii Io\\files\\decoded.jpg").toPath(), decoded);
    }
}
