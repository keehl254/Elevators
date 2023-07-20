package com.lkeehl.elevators.helpers;

import java.io.*;
import java.nio.file.Files;

public class ResourceHelper {

    @SuppressWarnings({"CatchMayIgnoreException", "ResultOfMethodCallIgnored"})
    public static void exportResource(Object clazz, String resourceName, String outputDirectory, String outputName, boolean overwrite) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        File returnFile;
        try {
            stream = clazz.getClass().getResourceAsStream(resourceName);
            if (stream == null)
                throw new Exception("Cannot get resource \"" + resourceName + "\"!");

            int readBytes;
            byte[] buffer = new byte['?'];
            jarFolder = outputDirectory.replace('\\', '/');
            returnFile = new File(jarFolder + File.separator + outputName);
            if (returnFile.exists() && !overwrite)
                throw new Exception();
            try {
                new File(jarFolder).mkdirs();
                returnFile.createNewFile();
            } catch (IOException ex) {
                throw new Exception("Error creating clone of resource!");
            }
            resStreamOut = Files.newOutputStream(returnFile.toPath());
            while ((readBytes = stream.read(buffer)) != -1)
                resStreamOut.write(buffer, 0, readBytes);
        } catch (Exception ex) {
            if (ex.getMessage() != null)
                ex.printStackTrace();
        } finally {
            try {
                if (stream != null)
                    stream.close();
                if (resStreamOut != null)
                    resStreamOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "CatchMayIgnoreException"})
    public static void exportResource(Object clazz, String resourceName, File outputFile, boolean overwrite) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = clazz.getClass().getResourceAsStream(resourceName);
            if (stream == null)
                throw new Exception("Cannot get resource \"" + resourceName + "\"!");

            int readBytes;
            byte[] buffer = new byte['?'];
            if (outputFile.exists() && !overwrite)
                throw new Exception();
            try {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
            } catch (IOException ex) {
                throw new Exception("Error creating clone of resource!");
            }
            resStreamOut = Files.newOutputStream(outputFile.toPath());
            while ((readBytes = stream.read(buffer)) != -1)
                resStreamOut.write(buffer, 0, readBytes);
        } catch (Exception ex) {
            if (ex.getMessage() != null)
                ex.printStackTrace();
        } finally {
            try {
                if (stream != null)
                    stream.close();
                if (resStreamOut != null)
                    resStreamOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
