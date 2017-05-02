package ru.gloomyfolken.tcn2obj.json.helpers;

import java.io.InputStream;
import java.io.InputStreamReader;

import ru.gloomyfolken.tcn2obj.json.JsonJsonModel;
import ru.gloomyfolken.tcn2obj.tbl.JsonTabulaModel;
import ru.gloomyfolken.tcn2obj.tcn.JsonTechneModel;

/** Class for parsing json files to containers.
 *
 * @author iLexiconn
 * @since 0.1.0 */
public class JsonHelper
{
    public static JsonTabulaModel parseTabulaModel(InputStream stream)
    {
        return JsonFactory.getGson().fromJson(new InputStreamReader(stream), JsonTabulaModel.class);
    }

    public static JsonTechneModel parseTechneModel(InputStream stream)
    {
        return JsonFactory.getGson().fromJson(new InputStreamReader(stream), JsonTechneModel.class);
    }

    public static JsonJsonModel parseJsonModel(InputStream stream)
    {
        return JsonFactory.getGson().fromJson(new InputStreamReader(stream), JsonJsonModel.class);
    }
}
