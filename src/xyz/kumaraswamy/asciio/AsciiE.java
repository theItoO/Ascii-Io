package xyz.kumaraswamy.asciio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;

class AsciiE {
    public static byte[] encode(byte[] bytes) throws IOException {
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();

        ArrayList<int[]> ints = new ArrayList<>();
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();

        boolean base64Last = false;

        int copy = 0;

        Integer lastL = null;
        for (byte ch : bytes) {
            if (ch > 0) {
                encoded.write(ch);
                base64Last = false;
            } else {
                byte[] base64 = encoder.encode(new byte[]{ch});
                encoded.write(base64);

                int len = base64.length;
                if (base64Last) {
                    ints.get(ints.size() - 1)[1] += len;
                } else {
                    int copyI = copy;
                    if (lastL != null) {
                        copyI = copyI - lastL;
                    }
                    ints.add(new int[]{copyI, len});
                    base64Last = true;
                    lastL = copy;
                }
                copy = copy + len;
                continue;
            }
            copy++;
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int[] intsI : ints) {
            result.write((String.valueOf(
                    intsI[0]) + '=' +
                    intsI[1] + '&').getBytes());
        }
        byte[] bytesE = result.toByteArray();

        return new ByteArrayOutputStream() {{
            if (bytesE.length > 0) {
                bytesE[bytesE.length - 1] = '/';
                write(bytesE);
            } else {
                write('/');
            }
            write(encoded.toByteArray());
        }}.toByteArray();
    }

    public static byte[] decode(byte[] bytes) throws IOException {
        int intL = -1, intR = -1;

        boolean sideL = true;
        ArrayList<int[]> ints = new ArrayList<>();

        int times = 0;

        Integer lastL = null;
        for (byte byt : bytes) {
            times++;
            if (byt == '/' || byt == '&') {
                int intLL = intL;
                if (lastL != null) {
                    intLL += lastL;
                }
                if (intL != -1 && intR != -1) {
                    ints.add(new int[]{intLL, intR});
                }
                if (byt == '/') {
                    break;
                }
                lastL = intLL;

                intL = -1;
                intR = -1;
                sideL = true;
                continue;
            }
            if (byt == '=') {
                sideL = !sideL;
                continue;
            }
            int digit = Integer.parseInt(
                    String.valueOf((char) byt));

            if (sideL) {
                if (intL == -1) {
                    intL = 0;
                }
                intL = intL * 10 + digit;
            } else {
                if (intR == -1) {
                    intR = 0;
                }
                intR = intR * 10 + digit;
            }
        }

        Iterator<int[]> iterator = ints.iterator();
        int[] intsIV = iterator.hasNext() ? iterator.next() : null;

        Base64.Decoder decoder = Base64.getDecoder();
        ByteArrayOutputStream decoded = new ByteArrayOutputStream();

        for (int i = times; i < bytes.length; i++) {
            int reIndex = i - times;
            if (intsIV != null && intsIV[0] == reIndex) {
                int till = intsIV[1];

                int j = 0, iNx = i + 1;
                while (j != till) {
                    if (j % 2 == 0) {
                        decoded.write(
                                decoder.decode(Character.toString(
                                        (char) bytes[iNx - 1]) + (char) bytes[iNx]));
                    }
                    j++;
                    iNx++;
                }
                i = i + till - 1;
                if (iterator.hasNext()) {
                    intsIV = iterator.next();
                }
            } else {
                decoded.write(bytes[i]);
            }
        }
        return decoded.toByteArray();
    }
}

