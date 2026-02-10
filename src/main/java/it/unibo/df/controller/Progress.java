package it.unibo.df.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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

    private final Map<Integer, Ability> unlockedAbilitiesById = new LinkedHashMap<>();
    private final Map<Integer, Ability> lockedAbilitiesById = new LinkedHashMap<>();
    /**
     * Creates a registry loading abilities.yml from resources.
     */
    public Progress() {
        loadDefault();
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



    private void loadDefault() {
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
    public void write() {
        List<Ability> abilities = new LinkedList<>();
        abilities.addAll(unlockedAbilitiesById.values());
        abilities.addAll(lockedAbilitiesById.values());
        Map<String,Object> root = new LinkedHashMap<>();
        root.put("abilities", abilities);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter("abilities.yml")) {
            yaml.dump(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
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
