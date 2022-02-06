package xyz.kumaraswamy.asciio;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class AsciiIo {

    private final byte[] bytes;

    public AsciiIo(String text) {
        bytes = text.getBytes();
    }

    public AsciiIo(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] encode() throws IOException {
        if (bytes.length == 0) {
            throw new IllegalArgumentException();
        }
        byte[] asciiEncoded = AsciiE.encode(bytes);
        System.out.println("encoded: " + Arrays.toString(asciiEncoded));

        int pixelCounts = asciiEncoded.length;
        while (pixelCounts % 3 != 0) {
            pixelCounts++;
        }
        int height = optimumHeight(pixelCounts);
        int width = (pixelCounts + height - 1) / height;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int index = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (index == pixelCounts || index == asciiEncoded.length) {
                    break;
                }
                byte red = asciiEncoded[index++];

                byte green = index == pixelCounts || index == asciiEncoded.length ? 0 : asciiEncoded[index++];
                byte blue = index == pixelCounts || index == asciiEncoded.length ? 0 : asciiEncoded[index++];
                image.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
        return new ByteArrayOutputStream() {{
            ImageIO.write(image, "PNG", this);
        }}.toByteArray();
    }

    static int optimumHeight(int areaSize) {
        for (int h = (int) Math.sqrt(areaSize); h > 1; h--)
            if (areaSize % h == 0) {
                return h;
            }
        int altSize = areaSize + 1;
        for (int h = (int) Math.sqrt(altSize); h > 2; h--)
            if (altSize % h == 0) {
                return h;
            }
        return 2;
    }

    public byte[] decode() throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed(), green = color.getGreen(), blue = color.getBlue();
                if (red != 0) {
                    stream.write(red);
                }
                if (green != 0) {
                    stream.write(green);
                }
                if (blue != 0) {
                    stream.write(blue);
                }
            }
        }
        return AsciiE.decode(stream.toByteArray());
    }
}
