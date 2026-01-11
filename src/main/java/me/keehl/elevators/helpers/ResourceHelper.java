package me.keehl.elevators.helpers;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

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
                ElevatorsAPI.log(Level.SEVERE, "Failed to export resource. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(ex));
        } finally {
            try {
                if (stream != null)
                    stream.close();
                if (resStreamOut != null)
                    resStreamOut.close();
            } catch (Exception e) {
                ElevatorsAPI.log(Level.SEVERE, "Failed to close resource stream. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
            }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "CatchMayIgnoreException"})
    public static void exportResource(Object clazz, String resourceName, File outputFile, boolean overwrite) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = clazz.getClass().getClassLoader().getResourceAsStream(resourceName);
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
                ElevatorsAPI.log(Level.SEVERE, "Failed to export resource. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(ex));
        } finally {
            try {
                if (stream != null)
                    stream.close();
                if (resStreamOut != null)
                    resStreamOut.close();
            } catch (Exception e) {
                ElevatorsAPI.log(Level.SEVERE, "Failed to close resource stream. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
            }
        }
    }

    public static String cleanTrace(Throwable ex) {
        List<String> traces = new ArrayList<>();
        boolean continuation = false;
        for(StackTraceElement stackTraceElement : ex.getStackTrace()) {
            if(!stackTraceElement.getClassName().startsWith("me.keehl.elevators") || stackTraceElement.getFileName() == null) {
                continuation = true;
                continue;
            }
            if (continuation) {
                traces.add("_");
                continuation = false;
            }

            traces.add(String.format("%s::%s:%d", stackTraceElement.getFileName().replace(".java",""), stackTraceElement.getMethodName().replace("lambda$", "").replace("$1",""), stackTraceElement.getLineNumber()));
        }

        Collections.reverse(traces);
        return "\t" + ex.getLocalizedMessage() + "\n\t" + String.join(" -> ", traces);
    }

}
