package ru.gloomyfolken.tcn2obj.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import ru.gloomyfolken.tcn2obj.ModelFormatException;
import ru.gloomyfolken.tcn2obj.json.helpers.JsonFactory;

public class JsonModel
{
    public JsonJsonModel model;
    private String       filename;

    public JsonModel(File file) throws ModelFormatException
    {
        this.filename = file.getName();
        loadModel(file);
    }

    private void loadModel(File file) throws ModelFormatException
    {
        try
        {
            FileInputStream stream = new FileInputStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String str = "";
            String line;
            while ((line = reader.readLine()) != null)
            {
                str = str + line + "\n";
            }

            reader.close();

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(JsonJsonModel.class, new Deserializer());
            Gson gson = builder.create();
            model = gson.fromJson(str, JsonJsonModel.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ModelFormatException("Model " + filename + " has something wrong");
        }
    }

    public class Deserializer implements JsonDeserializer<JsonJsonModel>
    {

        @Override
        public JsonJsonModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonJsonModel model = JsonFactory.getGson().fromJson(json, JsonJsonModel.class);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
            {
                if (entry.getKey().equals("textures"))
                {
                    Map<String, String> map = context.deserialize(entry.getValue(), Map.class);
                    for (String o : map.keySet())
                    {
                        model.texMap.put(o, map.get(o));
                    }
                }
            }
            return model;
        }

    }

}
