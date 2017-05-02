package ru.gloomyfolken.tcn2obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import ru.gloomyfolken.tcn2obj.json.JsonModel;
import ru.gloomyfolken.tcn2obj.obj.ObjModel;
import ru.gloomyfolken.tcn2obj.tbl.TabulaModel;
import ru.gloomyfolken.tcn2obj.tcn.TechneModel;

public class Main
{

    private static final String tcn        = ".tcn";
    private static final String tbl        = ".tbl";
    private static final String obj        = ".obj";
    private static final String json       = ".json";

    static boolean              tblMeta    = false;
    static boolean              jsonRename = false;
    static boolean              jsonTexture = false;

    public static void main(String[] args) throws Exception
    {
        File baseDir = new File(".");
        loadCfg();
        doTbl(baseDir);
        doTcn(baseDir);
        doJson(baseDir);
//        cleanDir("/converted/assets");
        System.out.println("Done!");
    }

    static void cleanDir(String toClean) throws IOException
    {
        File dir = new File("." + toClean);

        List<File> files = getFiles(dir, "");

        for (File file : files)
        {
            if (!file.getName().endsWith(obj))
            {
                file.delete();
            }
        }
        System.out.println(dir.exists() + " " + files.size());
    }

    private static void doJson(File baseDir) throws Exception
    {
        List<File> files = getFiles(baseDir, json);
        JsonConverter converter = new JsonConverter();

        for (File file : files)
        {
            System.out.println("Processing " + file.getAbsolutePath());

            String filename;

            if (jsonRename)
            {
                filename = getJsonName(file);
            }
            else
            {
                filename = file.getName().substring(0, file.getName().length() - json.length());
            }

            File objFile = new File(file.getParentFile(), filename + ".obj");

            JsonModel model = new JsonModel(file);
            ObjModel objModel = converter.tcn2obj(model, 0.0625f);
            if (tblMeta)
            {
                File xmlFile = new File(file.getParentFile(), filename + ".xml");
                TabulaMetadataExporter metaExp = new TabulaMetadataExporter(null);
                saveFile(xmlFile, metaExp.getXMLLines());
            }
            saveFile(objFile, objModel.toStringList());
        }
    }

    private static void doTcn(File baseDir) throws Exception
    {
        List<File> files = getFiles(baseDir, tcn);
        TcnConverter tcnConverter = new TcnConverter();

        for (File tcnFile : files)
        {
            System.out.println("Processing " + tcnFile.getAbsolutePath());

            try
            {
                String filename = tcnFile.getName().substring(0, tcnFile.getName().length() - obj.length());
                File objFile = new File(tcnFile.getParentFile(), filename + ".obj");

                TechneModel tcnModel = new TechneModel(tcnFile);
                ObjModel objModel = tcnConverter.tcn2obj(tcnModel, 0.0625f);
              saveFile(objFile, objModel.toStringList());
            }
            catch (Exception e)
            {
                System.err.println("Error with "+tcnFile);
                e.printStackTrace();
            }

        }
    }

    private static void doTbl(File baseDir) throws Exception
    {
        TblConverter tblConverter = new TblConverter();
        List<File> files = getFiles(baseDir, tbl);
        for (File tblFile : files)
        {
            System.out.println("Processing " + tblFile.getAbsolutePath());

            String filename = tblFile.getName().substring(0, tblFile.getName().length() - obj.length());
            File objFile = new File(tblFile.getParentFile(), filename + ".obj");

            TabulaModel tblModel = new TabulaModel(tblFile);
            ObjModel objModel = tblConverter.tcn2obj(tblModel, 0.0625f);
            saveFile(objFile, objModel.toStringList());
            if (tblMeta)
            {
                File xmlFile = new File(tblFile.getParentFile(), filename + ".xml");
                TabulaMetadataExporter metaExp = new TabulaMetadataExporter(tblConverter);
                saveFile(xmlFile, metaExp.getXMLLines());
            }
        }
    }

    private static void loadCfg()
    {
        File dir = new File(".");
        File config = new File(dir, "tbl2obj.cfg");
        if (config.exists())
        {
            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(config));
                String line = "";
                while ((line = reader.readLine()) != null)
                {
                    parseCfgLine(line);
                }
                reader.close();
                return;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            FileWriter writer;
            try
            {
                writer = new FileWriter(config);
                writer.write("outputXML=false\n");
                writer.write("renameJson=false\n");
                writer.write("tryTextureJson=true");
                writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return;
    }

    private static void parseCfgLine(String line)
    {
        String[] args = line.split("=");
        if (args[0].equals("outputXML"))
        {
            tblMeta = Boolean.parseBoolean(args[1].trim());
        }
        else if (args[0].equals("renameJson"))
        {
            jsonRename = Boolean.parseBoolean(args[1].trim());
        }
        else if (args[0].equals("tryTextureJson"))
        {
            jsonTexture = Boolean.parseBoolean(args[1].trim());
        }
    }

    private static String getJsonName(File file) throws FileNotFoundException
    {
        String name = file.getName().substring(0, file.getName().length() - json.length());
        ;
        JsonParser parser = new JsonParser();
        FileInputStream in = new FileInputStream(file);
        JsonElement element = parser.parse(new InputStreamReader(in));
        JsonElement element1 = element.getAsJsonObject().get("textures");

        if (element1 == null || !element1.isJsonObject() || element1.getAsJsonObject().entrySet() == null
                || element1.getAsJsonObject().entrySet().isEmpty())
            return name;

        Iterator<Entry<String, JsonElement>> iterate = element1.getAsJsonObject().entrySet().iterator();
        name = iterate.next().getValue().getAsString();
        name = name.substring(name.lastIndexOf("/") + 1, name.length());
        System.out.println(name);
        return name;
    }

    private static void saveFile(File file, List<String> lines) throws IOException
    {
        FileWriter writer = new FileWriter(file);
        for (String str : lines)
        {
            writer.write(str);
            writer.write("\n");
        }
        writer.close();
    }

    private static List<File> getFiles(File dir, String postfix) throws IOException
    {
        ArrayList<File> files = new ArrayList<File>();
        File[] filesArray = dir.listFiles();
        if (filesArray != null)
        {
            for (File file : filesArray)
            {
                if (file.isDirectory())
                {
                    files.addAll(getFiles(file, postfix));
                }
                else if (file.getName().endsWith(postfix))
                {
                    files.add(file);
                }
            }
        }
        return files;
    }

}
