package net.dorianpb.cem.internal.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.mixins.EntityModelLayersAccessor;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public enum OptifineFixes {
    ;
    private static final Map<String, BiMap<String, String>>       partnames      = new HashMap<>();
    private static final Map<String, Map<String, ModelTransform>> transformfixes = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void accept(Object key, LinkedTreeMap<String, Object> json) {
        String entity = key.toString();
        try {
            if(json.containsKey("partnames")) {
                partnames.put(entity, parsePartNames((LinkedTreeMap<String, String>) json.get("partnames")));
            }

            if(json.containsKey("modelfixes")) {
                transformfixes.put(entity, parseModelFixes((LinkedTreeMap<String, ArrayList<Double>>) json.get("modelfixes")));
            }

        } catch(RuntimeException e) {
            CemFairy.getLogger().error(e);
        }

    }

    private static BiMap<String, String> parsePartNames(LinkedTreeMap<String, String> json) {
        return HashBiMap.create(json);
    }

    private static Map<String, ModelTransform> parseModelFixes(LinkedTreeMap<String, ArrayList<Double>> json) {
        Map<String, ModelTransform> map = new HashMap<>();
        for(Map.Entry<String, ArrayList<Double>> entry : json.entrySet()) {
            map.put(entry.getKey(),
                    ModelTransform.pivot(entry.getValue().get(0).floatValue(),
                                         entry.getValue().get(1).floatValue(),
                                         entry.getValue().get(2).floatValue()));
        }
        return map;
    }

    public static BiMap<String, String> getPartNames(EntityModelLayer entity) {
        return partnames.getOrDefault(entity.toString(),
                                      partnames.get(new EntityModelLayer(entity.getId(), EntityModelLayersAccessor.getMAIN()).toString()));
    }

    public static BiMap<String, String> getPartNames(Object entity) {
        return partnames.get(entity.toString());
    }

    public static Map<String, ModelTransform> getModelFixes(EntityModelLayer entity) {
        return transformfixes.getOrDefault(entity.toString(),
                                           transformfixes.get(new EntityModelLayer(entity.getId(), EntityModelLayersAccessor.getMAIN()).toString()));
    }

    public static Map<String, ModelTransform> getModelFixes(Object entity) {
        return transformfixes.get(entity.toString());
    }

    public static boolean hasFixesFor(EntityModelLayer entity) {
        //noinspection ConstantValue
        return partnames.containsKey(entity.toString()) ||
               partnames.containsKey(new EntityModelLayer(entity.getId(), EntityModelLayersAccessor.getMAIN()).toString()) ||
               transformfixes.containsKey(entity.toString()) ||
               transformfixes.containsKey(new EntityModelLayer(entity.getId(), EntityModelLayersAccessor.getMAIN()).toString());
    }

    public static boolean hasFixesFor(Object entity) {
        String str = entity.toString();
        return partnames.containsKey(str) || transformfixes.containsKey(str);
    }
}