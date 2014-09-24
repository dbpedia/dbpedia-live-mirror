package org.dbpedia.extraction.live.mirror.helper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/24/14 1:16 PM
 */
public final class Utils {

    private Utils() {}

    public static List<String> getLinesFromFile(String filename) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {

            String line = null;
            while ((line = in.readLine()) != null) {

                lines.add(line.trim());

            }

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + filename + " not fount!", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UnsupportedEncodingException: ", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("IOException in file " + filename, e);
        }

        return lines;

    }

    public static String generateStringFromList(List<String> strList,String sep) {

        StringBuilder finalString = new StringBuilder();


        for (String str: strList) {
            finalString.append(str);
            finalString.append(sep);
        }

        return finalString.toString();
    }

}
