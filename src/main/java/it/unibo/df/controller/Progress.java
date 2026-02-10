package it.unibo.df.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityAreas;
import it.unibo.df.model.abilities.AbilityFn;
import it.unibo.df.model.abilities.AbilityType;

/**
 * Loads abilities from a YAML file.
 */
public final class Progress {
    private static final List<Integer> DEFAULT_UNLOCKED_IDS = List.of(1, 2, 3, 4, 5);
    private Map<Integer, Ability> unlockedAbilitiesById = new LinkedHashMap<>();
    private final Map<Integer, Ability> lockedAbilitiesById = new LinkedHashMap<>();
    /**
     * Creates a registry loading abilities.yml from resources.
     */
    public Progress() {
        loadCurrent();
    }

    public void update(long killedEnemy) {
        final Random random = new Random();
        for (int i= 0; i < killedEnemy; i++) {
            List<Integer> keys = List.copyOf(lockedAbilitiesById.keySet());
            int index = random.nextInt(0, keys.size()); // % keys.size();
            unlockedAbilitiesById.put(keys.get(index), lockedAbilitiesById.get(keys.get(index)));
            lockedAbilitiesById.remove(keys.get(index));
        }
    }

    public void reset() {
        unlockedAbilitiesById.entrySet().stream()
            .filter(e -> !DEFAULT_UNLOCKED_IDS.contains(e.getKey()))
            .forEach(a -> lockedAbilitiesById.put(a.getKey(), a.getValue()));
        unlockedAbilitiesById = unlockedAbilitiesById.entrySet().stream()
            .filter(e -> DEFAULT_UNLOCKED_IDS.contains(e.getKey()))
            .collect(Collectors.toMap(k -> k.getKey(),v -> v.getValue()));
    }

    public Map<Integer,Ability> unlockedAbilities() {
        return Collections.unmodifiableMap(unlockedAbilitiesById);
    }
    
    //queste 3 funxioni non servono piu 
     /**
      * Returns an ability by id.
      *
      * @param id ability id
      * @return ability or null
      */
    public Ability get(final int id) {
        return unlockedAbilitiesById.get(id);
    }

     /**
      * Returns all registered abilities.
      *
      * @return list of abilities
      */
    public List<Ability> getAll() {
        return List.copyOf(unlockedAbilitiesById.values());
    }

     /**
      * Returns how many abilities are registered.
      *
      * @return number of abilities
      */
    public int size() {
        return unlockedAbilitiesById.size();
    }



    private void loadCurrent() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("abilities.yml")) {
            if (stream == null) {
                throw new IllegalStateException("abilities.yml not found");
            }
            loadFromStream(stream);
        } catch (final IOException e) {
            throw new IllegalStateException("Cannot read abilities.yml", e);
        }
    }

    private void loadFromStream(final InputStream stream) {
        final Object loaded = new Yaml().load(stream);
        if (!(loaded instanceof Map<?, ?> root)) {
            throw new IllegalStateException("Invalid abilities.yml format");
        }

        final Object listObj = root.get("abilities");
        if (!(listObj instanceof List<?> list)) {
            throw new IllegalStateException("Missing abilities list");
        }
        
        
        for (final Object entry : list) {
            addAbility(entry);
        }
    }

    @SuppressWarnings("unchecked")
    public void write() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("abilities.yml");
                FileWriter output = new FileWriter("src/main/resources/abilities.yml")) {
            
            DumperOptions options = new DumperOptions(); 
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); 
            options.setPrettyFlow(true); 
            Yaml file = new Yaml(options);
            Map<String, Object> root = file.load(input);
            List<Map<String,Object>> abilities = (List<Map<String, Object>>) root.get("abilities");
            for (Map<String, Object> ability : abilities) {
                if (unlockedAbilitiesById.keySet().contains((int) ability.get("id"))) {
                    ability.put("unlocked", true);
                }
            }
            file.dump(root, output);
        }catch (IOException e) {
            throw new IllegalStateException("Missing abilities list");
        }
    }

    


    @SuppressWarnings("unchecked")
    private void addAbility(final Object entry) {
        if (!(entry instanceof Map<?, ?>)) {
            throw new IllegalStateException("Invalid ability entry: " + entry);
        }

        final Map<String, Object> abilityData = (Map<String, Object>) entry;

        final int id = (int) abilityData.get("id");
        final String name = String.valueOf(abilityData.get("name"));
        final boolean unlocked = (boolean) abilityData.get("unlocked");
        final int cooldown = (int) abilityData.get("cooldown");
        final AbilityType type = AbilityType.valueOf(
            String.valueOf(abilityData.get("type")).toUpperCase(Locale.ROOT)
        );
        final int casterHpDelta = (int) abilityData.get("casterHpDelta");
        final int targetHpDelta = (int) abilityData.get("targetHpDelta");

        final String area = String.valueOf(abilityData.getOrDefault("area", "NONE"))
            .toUpperCase(Locale.ROOT);

        final AbilityFn effect = AbilityAreas.fromString(area);
        ((boolean) abilityData.get("unlocked") ? unlockedAbilitiesById : lockedAbilitiesById)
            .put(id, new Ability(
                id,
                name,
                unlocked,
                cooldown,
                type,
                casterHpDelta,
                targetHpDelta,
                effect
            ));
    }
}
