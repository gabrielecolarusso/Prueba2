/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UTILS;

/**
 *
 * @author yarge
 */
import com.google.gson.*;
import java.awt.*;
import java.lang.reflect.Type;

/**
 * 📌 Adaptador para convertir `Color` en JSON y viceversa.
 */
public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("r", color.getRed());
        json.addProperty("g", color.getGreen());
        json.addProperty("b", color.getBlue());
        return json;
    }

    @Override
    public Color deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int r = jsonObject.get("r").getAsInt();
        int g = jsonObject.get("g").getAsInt();
        int b = jsonObject.get("b").getAsInt();
        return new Color(r, g, b);
    }
}